package org.emau.icmvc.ganimed.epix.core.internal;

/*
 * ###license-information-start###
 * E-PIX - Enterprise Patient Identifier Cross-referencing
 * __
 * Copyright (C) 2009 - 2017 The MOSAIC Project - Institut fuer Community Medicine der
 * 							Universitaetsmedizin Greifswald - mosaic-projekt@uni-greifswald.de
 * 							concept and implementation
 * 							c. schack, d.langner, l. geidel
 * 							web client
 * 							a. blumentritt
 * 							g. weiher
 * 							please cite our publications
 * 							http://dx.doi.org/10.3414/ME14-01-0133
 * 							http://dx.doi.org/10.1186/s12967-015-0545-6
 * __
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * ###license-information-end###
 */

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.emau.icmvc.ganimed.deduplication.BlockingStrategy;
import org.emau.icmvc.ganimed.deduplication.DeduplicationStrategy;
import org.emau.icmvc.ganimed.deduplication.config.model.MatchingConfiguration;
import org.emau.icmvc.ganimed.deduplication.impl.CommonPreprocessor;
import org.emau.icmvc.ganimed.deduplication.model.DeduplicationResult;
import org.emau.icmvc.ganimed.deduplication.model.MatchResult;
import org.emau.icmvc.ganimed.deduplication.model.PerfectMatchTransferObject;
import org.emau.icmvc.ganimed.epix.common.MPIException;
import org.emau.icmvc.ganimed.epix.common.model.ErrorCode;
import org.emau.icmvc.ganimed.epix.common.utils.ReflectionUtil;
import org.emau.icmvc.ganimed.epix.core.persistence.model.PersonPreprocessed;
import org.emau.icmvc.ganimed.epix.pdqquery.model.SearchMask;
import org.emau.icmvc.ganimed.exception.DeduplicationException;

/**
 *
 * @author Robert Wolff Personpreprocessed table cache object
 * @author geidell
 */
public class PersonPreprocessedCache<T>
{

	private static final Logger logger = Logger.getLogger(PersonPreprocessedCache.class);
	private boolean initialised = false;
	private static final Map<Long, PersonPreprocessed> ppCache = new HashMap<Long, PersonPreprocessed>();
	private static final Object emSynchronizerDummy = new Object();
	private CommonPreprocessor<T> personPreprocessedPreprocessor;

	public boolean isInitialised()
	{
		return initialised;
	}

	/**
	 * Initialize cache
	 *
	 * @param em
	 * @param matchingConfiguration
	 */
	public void init(EntityManager em, MatchingConfiguration matchingConfiguration)
	{
		synchronized (emSynchronizerDummy)
		{
			personPreprocessedPreprocessor = new CommonPreprocessor<T>();
			personPreprocessedPreprocessor.setMatchingConfiguration(matchingConfiguration);

			List<PersonPreprocessed> personPreprocessedList = getAllPersonPreprocessed(em);
			for (PersonPreprocessed personPreprocessed : personPreprocessedList)
			{
				ppCache.put(personPreprocessed.getPersonId(), personPreprocessed);
			}
			logger.info("ppCache initialized with: " + ppCache.size() + " entries");
			em.clear();
			initialised = true;
		}
	}

	public T preprocess(T personPreprocessed) throws DeduplicationException
	{
		// Preprocessing
		return personPreprocessedPreprocessor.preprocess(personPreprocessed);
	}

	/**
	 * Load all personpreprocessed entities from database
	 *
	 * @param em
	 * @return
	 */
	private List<PersonPreprocessed> getAllPersonPreprocessed(EntityManager em)
	{
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<PersonPreprocessed> criteriaQuery = criteriaBuilder.createQuery(PersonPreprocessed.class);
		Root<PersonPreprocessed> root = criteriaQuery.from(PersonPreprocessed.class);
		criteriaQuery.select(root);
		List<PersonPreprocessed> personPreprocessedList = em.createQuery(criteriaQuery).getResultList();
		return personPreprocessedList;
	}

	/**
	 * Add person to cache. Should be called after database insert to guarantee
	 * consistency
	 *
	 * @param personPreprocessed
	 */
	public void addPersonPreprocessedEntry(PersonPreprocessed personPreprocessed)
	{
		synchronized (emSynchronizerDummy)
		{
			ppCache.put(personPreprocessed.getPersonId(), personPreprocessed);
		}
	}

	/**
	 * Match person by using personpreprocessed cache object
	 *
	 * @param matchable
	 * @param blockingStrategy
	 * @param deduplicationStrategy
	 * @return
	 * @throws DeduplicationException
	 */
	public DeduplicationResult<PersonPreprocessed> getMatchesViaCache(PersonPreprocessed matchable,
			BlockingStrategy<PersonPreprocessed> blockingStrategy, DeduplicationStrategy<PersonPreprocessed> deduplicationStrategy)
			throws DeduplicationException
	{
		synchronized (emSynchronizerDummy)
		{
			DeduplicationResult<PersonPreprocessed> result = new DeduplicationResult<PersonPreprocessed>();
			result.setMatchable(matchable);

			for (PersonPreprocessed candidate : ppCache.values())
			{
				if (!blockingStrategy.block(candidate))
				{
					MatchResult<PersonPreprocessed> mr = deduplicationStrategy.match(matchable, candidate);
					result.addMatchResult(mr, mr.getDecision());
				}
			}
			return result;
		}
	}

	public PersonPreprocessed getPerfectMatch(PerfectMatchTransferObject transferObject) throws MPIException
	{
		PersonPreprocessed result = null;
		for (PersonPreprocessed candidate : ppCache.values())
		{
			boolean add = true;
			for (Entry<String, Object> key : transferObject.getValueMap().entrySet())
			{
				try
				{
					if (!ReflectionUtil.getProperty(candidate, key.getKey()).toString().equals(key.getValue().toString()))
					{
						add = false;
						break;
					}
				} catch (Exception e)
				{
					logger.error("Error while trying to check for perfectMatch", e);
				}
			}
			if (add)
			{
				if (result == null)
				{
					result = candidate;
				} else
				{
					throw new MPIException(ErrorCode.PERFECT_MATCH_FOR_PERSON);
				}
			}
		}
		return result;
	}

	public List<PersonPreprocessed> findPersonPreprocessedByPersonGroup(Long personGroupID)
	{
		synchronized (emSynchronizerDummy)
		{
			List<PersonPreprocessed> ppList = new ArrayList<PersonPreprocessed>();
			for (PersonPreprocessed pp : ppCache.values())
			{
				if (pp.getPersonGroupID().equals(personGroupID))
				{
					ppList.add(pp);
				}
			}
			return ppList;
		}
	}

	public PersonPreprocessed getPersonPreprocessedByPersonID(Long id) throws MPIException
	{
		PersonPreprocessed pp = ppCache.get(id);
		if (pp == null)
		{
			throw new MPIException(ErrorCode.NO_PERSON_FOUND);
		}
		return ppCache.get(id);
	}

	public List<PersonPreprocessed> getPersonsPreprocessedByPDQ(SearchMask mask) throws MPIException
	{
		List<PersonPreprocessed> persons = new ArrayList<PersonPreprocessed>();
		int maxResults = 1000;
		
		if(mask.isAnd())
		{
			getPersonsPreprocessedByPdqWithAnd(persons, mask, maxResults);
		}
		else
		{
			getPersonByPdqWithOr(persons, mask, maxResults);
		}

		return persons;
	}

	private List<PersonPreprocessed> getPersonsPreprocessedByPdqWithAnd(List<PersonPreprocessed> persons, SearchMask mask, int maxResults)
	{
		for (PersonPreprocessed person : ppCache.values())
		{
			Calendar.getInstance().setTime(person.getBirthDate());
			boolean result = true;
			boolean checkIf = false;
			
			if(persons.size() < maxResults)
			{
				if (StringUtils.isNotEmpty(mask.getFirstName()))
				{
					result = result && StringUtils.contains(person.getFirstName().toLowerCase(), mask.getFirstName().toLowerCase());
					checkIf = true;
				}
	
				if (StringUtils.isNotEmpty(mask.getLastName()))
				{
					result = result && StringUtils.contains(person.getLastName().toLowerCase(), mask.getLastName().toLowerCase());
					checkIf = true;
				}
	
				if (StringUtils.isNotEmpty(mask.getBirthDay()))
				{
					result = result && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == Integer.parseInt(mask.getBirthDay());
					checkIf = true;
				}
	
				if (StringUtils.isNotEmpty(mask.getBirthMonth()))
				{
					result = result && Calendar.getInstance().get(Calendar.MONTH) == Integer.parseInt(mask.getBirthMonth());
					checkIf = true;
				}			
				
	
				if (StringUtils.isNotEmpty(mask.getBirthYear()))
				{
					result = result && Calendar.getInstance().get(Calendar.YEAR) == Integer.parseInt(mask.getBirthYear());
					
					checkIf = true;
				}
	
				if(result & checkIf)
				{
						persons.add(person);	
				}
			}
		}
		return persons;
	}

	private List<PersonPreprocessed> getPersonByPdqWithOr(List<PersonPreprocessed> persons, SearchMask mask, int maxResults)
	{
		for(PersonPreprocessed person : ppCache.values())
		{
			if(persons.size() < maxResults)
			{
				Calendar.getInstance().setTime(person.getBirthDate());
	
				if (StringUtils.isNotEmpty(mask.getFirstName()) && person.getFirstName().toLowerCase().contains(mask.getFirstName().toLowerCase()))
				{
					persons.add(person);
					continue;
				}
	
				if (StringUtils.isNotEmpty(mask.getLastName()) && person.getLastName().toLowerCase().contains(mask.getLastName().toLowerCase()))
				{
					persons.add(person);
					continue;
				}
	
				if (StringUtils.isNotEmpty(mask.getBirthDay()) && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == Integer.parseInt(mask.getBirthDay()))
				{
					persons.add(person);
					continue;
				}
	
				if (StringUtils.isNotEmpty(mask.getBirthMonth()) && Calendar.getInstance().get(Calendar.MONTH) == Integer.parseInt(mask.getBirthMonth()))
				{
					persons.add(person);
					continue;
				}
	
				if (StringUtils.isNotEmpty(mask.getBirthYear()) && Calendar.getInstance().get(Calendar.YEAR) == Integer.parseInt(mask.getBirthYear()))
				{
					persons.add(person);
					continue;
				}
			}
		}
		return persons;
	}
}
