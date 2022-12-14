package org.emau.icmvc.ttp.epix.persistence;

/*-
 * ###license-information-start###
 * E-PIX - Enterprise Patient Identifier Cross-referencing
 * __
 * Copyright (C) 2009 - 2022 Trusted Third Party of the University Medicine Greifswald
 * 							kontakt-ths@uni-greifswald.de
 *
 * 							concept and implementation
 * 							l.geidel,c.schack, d.langner, g.koetzschke
 *
 * 							web client
 * 							a.blumentritt, f.m. moser
 *
 * 							docker
 * 							r.schuldt
 *
 * 							privacy preserving record linkage (PPRL)
 * 							c.hampf
 *
 * 							please cite our publications
 * 							http://dx.doi.org/10.3414/ME14-01-0133
 * 							http://dx.doi.org/10.1186/s12967-015-0545-6
 * 							https://translational-medicine.biomedcentral.com/articles/10.1186/s12967-020-02257-4
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PreDestroy;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.emau.icmvc.ttp.deduplication.FellegiSunterAlgorithm;
import org.emau.icmvc.ttp.deduplication.model.DeduplicationResult;
import org.emau.icmvc.ttp.deduplication.model.MatchResult;
import org.emau.icmvc.ttp.epix.common.exception.DuplicateEntryException;
import org.emau.icmvc.ttp.epix.common.exception.IllegalOperationException;
import org.emau.icmvc.ttp.epix.common.exception.InvalidParameterException;
import org.emau.icmvc.ttp.epix.common.exception.MPIErrorCode;
import org.emau.icmvc.ttp.epix.common.exception.MPIException;
import org.emau.icmvc.ttp.epix.common.exception.ObjectInUseException;
import org.emau.icmvc.ttp.epix.common.exception.UnknownObjectException;
import org.emau.icmvc.ttp.epix.common.exception.UnknownObjectType;
import org.emau.icmvc.ttp.epix.common.model.ContactInDTO;
import org.emau.icmvc.ttp.epix.common.model.DomainDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentifierDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentifierDomainDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityInBaseDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityInDTO;
import org.emau.icmvc.ttp.epix.common.model.RequestConfig;
import org.emau.icmvc.ttp.epix.common.model.ResponseEntryDTO;
import org.emau.icmvc.ttp.epix.common.model.SourceDTO;
import org.emau.icmvc.ttp.epix.common.model.enums.IdentityHistoryEvent;
import org.emau.icmvc.ttp.epix.common.model.enums.MatchStatus;
import org.emau.icmvc.ttp.epix.common.model.enums.MatchingMode;
import org.emau.icmvc.ttp.epix.common.model.enums.PersistMode;
import org.emau.icmvc.ttp.epix.common.model.enums.PossibleMatchSolution;
import org.emau.icmvc.ttp.epix.common.model.enums.RequestSaveAction;
import org.emau.icmvc.ttp.epix.common.model.strings.IdentityHistoryStrings;
import org.emau.icmvc.ttp.epix.common.model.strings.PersonHistoryStrings;
import org.emau.icmvc.ttp.epix.common.model.strings.PossibleMatchHistoryStrings;
import org.emau.icmvc.ttp.epix.gen.MPIGenerator;
import org.emau.icmvc.ttp.epix.gen.impl.EAN13Generator;
import org.emau.icmvc.ttp.epix.internal.DomainCache;
import org.emau.icmvc.ttp.epix.internal.PreprocessedCacheObject;
import org.emau.icmvc.ttp.epix.persistence.model.Contact;
import org.emau.icmvc.ttp.epix.persistence.model.ContactHistory;
import org.emau.icmvc.ttp.epix.persistence.model.Domain;
import org.emau.icmvc.ttp.epix.persistence.model.Domain_;
import org.emau.icmvc.ttp.epix.persistence.model.Identifier;
import org.emau.icmvc.ttp.epix.persistence.model.IdentifierDomain;
import org.emau.icmvc.ttp.epix.persistence.model.IdentifierDomain_;
import org.emau.icmvc.ttp.epix.persistence.model.IdentifierId;
import org.emau.icmvc.ttp.epix.persistence.model.Identifier_;
import org.emau.icmvc.ttp.epix.persistence.model.Identity;
import org.emau.icmvc.ttp.epix.persistence.model.IdentityHistory;
import org.emau.icmvc.ttp.epix.persistence.model.IdentityLink;
import org.emau.icmvc.ttp.epix.persistence.model.IdentityLinkHistory;
import org.emau.icmvc.ttp.epix.persistence.model.IdentityPreprocessed;
import org.emau.icmvc.ttp.epix.persistence.model.IdentityPreprocessed_;
import org.emau.icmvc.ttp.epix.persistence.model.Identity_;
import org.emau.icmvc.ttp.epix.persistence.model.Person;
import org.emau.icmvc.ttp.epix.persistence.model.PersonHistory;
import org.emau.icmvc.ttp.epix.persistence.model.Person_;
import org.emau.icmvc.ttp.epix.persistence.model.Source;
import org.emau.icmvc.ttp.epix.persistence.model.Source_;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;

/**
 * central data access point (db and cache) - private parts
 * <p>
 * purpose of this class: make it possible to catch errors while updating db (see methods with TransactionAttributeType.REQUIRES_NEW)<br>
 * and separation of public and private methods of this anti-pattern god class
 *
 * @author geidell
 */
public abstract class DAO
{
	private static final Logger logger = LogManager.getLogger(DAO.class);
	private static final int IDENTITY_PREPROCESSED_PAGE_SIZE = 100000;
	protected static final DomainCache DOMAIN_CACHE = DomainCache.getInstance();
	// identifierDomain+prefix, counter
	private static final Object2LongMap<CounterMapKey> COUNTER_MAP = new Object2LongOpenHashMap<>();
	private static final Map<String, MPIGenerator> GENERATORS = new HashMap<>();

	@PersistenceContext(unitName = "epix")
	protected EntityManager em;

	public DAO()
	{}

	protected void initCache()
	{
		logger.info("initialising epix caches");
		GENERATORS.put(EAN13Generator.class.getName(), new EAN13Generator());
		try
		{
			List<Domain> domains = getDBDomains();
			for (Domain domain : domains)
			{
				// counter for mpi-ids
				Identifier identifier = getDBLastMPIIdentifierByDomain(domain);
				// TODO FIXME non-MPI-ids
				CounterMapKey key = new CounterMapKey(domain.getMpiDomain().getName(), domain.getMatchingConfiguration().getMpiPrefix());
				if (identifier != null && !identifier.getValue().isEmpty() && identifier.getValue().length() == 13)
				{
					long count = Long.parseLong(identifier.getValue().substring(4, 12));
					logger.debug("mpi counter for domain '" + domain.getName() + "' is " + count);
					COUNTER_MAP.put(key, count);
				}
				else
				{
					logger.debug("no mpi ids found for domain '" + domain.getName() + "' - so mpi counter is set to 0");
					COUNTER_MAP.put(key, 0l);
				}
			}
			Map<String, Long> personCounterForDomains = new HashMap<>();
			for (Domain domain : domains)
			{
				long personCount = getPersonCountForDomain(domain);
				personCounterForDomains.put(domain.getName(), personCount);
			}
			DOMAIN_CACHE.init(domains, personCounterForDomains);
			logger.info("added " + domains.size() + " domains to cache, loading identity preprocessed now");
			for (Domain domain : domains)
			{
				// load identity preprocessed chunked to save memory
				long ipCount = getIPCountForDomain(domain.getName());
				if (logger.isDebugEnabled())
				{
					logger.debug("found " + ipCount + " identity preprocessed for domain " + domain.getName());
				}
				for (int i = 0; i * IDENTITY_PREPROCESSED_PAGE_SIZE < ipCount; i++)
				{
					int nextPageSize = (int) ((i + 1) * IDENTITY_PREPROCESSED_PAGE_SIZE < ipCount ? IDENTITY_PREPROCESSED_PAGE_SIZE
							: ipCount - i * IDENTITY_PREPROCESSED_PAGE_SIZE);
					if (nextPageSize > 0)
					{
						List<IdentityPreprocessed> ips = getIPForDomainPaginated(domain.getName(), i * IDENTITY_PREPROCESSED_PAGE_SIZE, nextPageSize);
						DOMAIN_CACHE.addIPsToDomain(domain.getName(), ips);
						em.clear();
					}
				}
			}
			logger.info("epix caches initialised");
		}
		catch (Exception e)
		{
			String message = "exception while initialising epix caches";
			logger.fatal(message, e);
			throw new RuntimeException(message, e);
		}
	}

	// nur im init verwenden (wegen domain.inUse - transient field - wird beim holen aus dem cache gesetzt)
	private List<Domain> getDBDomains()
	{
		logger.debug("getDBDomains");
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<Domain> criteriaQuery = criteriaBuilder.createQuery(Domain.class);
		Root<Domain> root = criteriaQuery.from(Domain.class);
		criteriaQuery.select(root);
		List<Domain> result = em.createQuery(criteriaQuery).getResultList();
		logger.debug("found " + result.size() + " domains");
		return result;
	}

	private long getIPCountForDomain(String domainName)
	{
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<IdentityPreprocessed> root = criteriaQuery.from(IdentityPreprocessed.class);
		Predicate predicate = criteriaBuilder.equal(root.get(IdentityPreprocessed_.domainName), domainName);
		criteriaQuery.select(criteriaBuilder.count(root)).where(predicate);
		Query query = em.createQuery(criteriaQuery);
		return (long) query.getSingleResult();
	}

	private long getPersonCountForDomain(Domain domain)
	{
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<Person> root = criteriaQuery.from(Person.class);
		Predicate predicate = criteriaBuilder.equal(root.get(Person_.domain), domain);
		criteriaQuery.select(criteriaBuilder.count(root)).where(predicate);
		Query query = em.createQuery(criteriaQuery);
		return (long) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	private List<IdentityPreprocessed> getIPForDomainPaginated(String domainName, int startPosition, int maxResults)
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("getIPForDomainPaginated for domain " + domainName + " from " + (startPosition + 1) + " to " + (startPosition + maxResults));
		}
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<IdentityPreprocessed> criteriaQuery = criteriaBuilder.createQuery(IdentityPreprocessed.class);
		Root<IdentityPreprocessed> root = criteriaQuery.from(IdentityPreprocessed.class);
		Predicate predicate = criteriaBuilder.equal(root.get(IdentityPreprocessed_.domainName), domainName);
		criteriaQuery.select(root).where(predicate);
		Query query = em.createQuery(criteriaQuery);
		query.setFirstResult(startPosition);
		query.setMaxResults(maxResults);
		return query.getResultList();
	}

	@PreDestroy
	private void destroy()
	{
		DOMAIN_CACHE.destroy();
	}

	private Identifier getDBLastMPIIdentifierByDomain(Domain domain)
	{
		logger.debug("getDBLastMPIIdentifierByDomain within domain " + domain.getName());
		try
		{
			Identifier identifier = (Identifier) em.createNamedQuery("Identifier.getOrderedIdentifierByIdentifierDomain")
					.setParameter("identifierDomain", domain.getMpiDomain()).setParameter("prefix", domain.getMatchingConfiguration().getMpiPrefix())
					.setMaxResults(1).getSingleResult();
			logger.debug("found " + identifier);
			return identifier;
		}
		catch (NoResultException maybe)
		{
			return null;
		}
	}

	// ***********************************
	// requestMPI
	// ***********************************
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public ResponseEntryDTO saveMPIRequest(String domainName, String sourceName, IdentityInBaseDTO identityDTO, String comment,
			RequestConfig requestConfig, Timestamp timestamp) throws InvalidParameterException, MPIException, UnknownObjectException
	{
		ResponseEntryDTO result;
		Domain domain = getDBDomain(domainName);
		Source source = getDBSource(sourceName);

		// hier kann schon die MPIException von pfad 2 fliegen
		Identity identity = mapIdentity(identityDTO, source, domain, requestConfig.isForceReferenceUpdate(), timestamp);

		// hier fliegt die InvalidParameterException im falle von pfad 1
		IdentityPreprocessed identityPP = DOMAIN_CACHE.preprocess(domainName, identity);

		// Erzeuge Bloomfilter
		identityPP = DOMAIN_CACHE.setPrivacyAttributes(domainName, identityPP, false);

		// Entferne alle IDAT Felder (ausser Felder in denen Bloomfilter gespeichert sind)
		if (PersistMode.PRIVACY_PRESERVING.equals(DOMAIN_CACHE.getConfigurationContainerForDomain(domainName).getPersistMode()))
		{
			identityPP = DOMAIN_CACHE.removePII(domainName, identityPP);
			identity = DOMAIN_CACHE.removePII(domainName, identity);
		}

		// Kopiere Bloomfilter
		DOMAIN_CACHE.copyPrivacyAttributes(domainName, identityPP, identity);

		if (identity.getPerson() != null)
		{
			result = processRequestMPIWithIdentifier(domain, requestConfig.getSaveAction(), identity, identityPP, comment, timestamp, false);
		}
		else
		{
			result = processRequestMPIWithoutIdentifier(domain, requestConfig.getSaveAction(), identity, identityPP, comment, timestamp);
		}
		return result;
	}

	private ResponseEntryDTO processRequestMPIWithIdentifier(Domain domain, RequestSaveAction saveAction, Identity identity,
			IdentityPreprocessed identityPP, String comment, Timestamp timestamp, boolean fromUpdate) throws MPIException, UnknownObjectException
	{
		// diese methode gibt aber nie "null" zurueck - eclipse erkennt nicht, dass die throw... bzw. handleSaveServiceException auf jeden fall ne exception werfen
		ResponseEntryDTO result = null;
		logger.debug("found a person for the given local ids - check for perfect match within this person");
		PreprocessedCacheObject identityPPCO = DOMAIN_CACHE.createPreprocessedCacheObjectFromIdentityPP(domain.getName(), identityPP);
		List<PreprocessedCacheObject> existingIdentitiesPP = new ArrayList<>();
		for (Identity existingIdentity : identity.getPerson().getIdentities())
		{
			existingIdentitiesPP.add(DOMAIN_CACHE.getPreprocessedCacheObjectByIdentityId(domain.getName(), existingIdentity.getId()));
		}
		// perfectMatch erst mal nur auf den zu den local id passenden identitaeten
		PreprocessedCacheObject perfectMatchPPCO = DOMAIN_CACHE.perfectMatch(domain.getName(), identityPPCO, existingIdentitiesPP);
		if (perfectMatchPPCO != null)
		{
			logger.debug("perfect match found within the person for the given local ids");
			logger.debug("requestMPI path 3");
			Identity perfectMatch;
			if (RequestSaveAction.SAVE_ALL.equals(saveAction))
			{
				perfectMatch = updateIdentity(domain, perfectMatchPPCO.getIdentityId(), identity, identityPP, perfectMatchPPCO,
						fromUpdate ? IdentityHistoryEvent.UPDATE : IdentityHistoryEvent.PERFECT_MATCH, comment,
						FellegiSunterAlgorithm.MATCHING_SCORE_FOR_PERFECT_MATCH, timestamp);
			}
			else if (RequestSaveAction.DONT_SAVE_ON_PERFECT_MATCH_EXCEPT_CONTACTS.equals(saveAction))
			{
				perfectMatch = saveContacts(perfectMatchPPCO.getIdentityId(), identity.getContacts());
			}
			else
			{
				// rueckgabe ist ja der stand nach abfrage - bei save = false halt die matchende person
				perfectMatch = getDBIdentityById(perfectMatchPPCO.getIdentityId());
			}
			result = new ResponseEntryDTO(perfectMatch.getPerson().toDTO(), MatchStatus.PERFECT_MATCH);
		}
		else
		{
			logger.debug("no perfect match found within person, verifying that there is no perfect match with another person");
			perfectMatchPPCO = DOMAIN_CACHE.perfectMatch(domain.getName(), identityPPCO);
			if (perfectMatchPPCO != null)
			{
				logger.debug("requestMPI path 4");
				Person matchingPerson = getDBPersonById(perfectMatchPPCO.getPersonId());
				throwMultiplePersonsForIdentifierException(identity.getPerson().getFirstMPI().getValue(),
						Collections.singleton(matchingPerson.getFirstMPI().getValue()));
			}
			logger.debug("no perfect match found within db - search for other matches");
			DeduplicationResult deduplicationResultForDB = DOMAIN_CACHE.match(domain.getName(), identityPPCO);
			if (deduplicationResultForDB.hasNoResults())
			{
				logger.debug("requestMPI path 8 (shortcut)");
				throwIDatDontFitIdentifierException(identity.getPerson().getId());
			}
			// match mit genau einer person?
			boolean cemfim = domain.getMatchingConfiguration().getMatching().isUseCEMFIM();
			LongSet matchPersonIds = deduplicationResultForDB.getMatchPersonIDs();
			if (cemfim && matchPersonIds.size() > 1)
			{
				logger.debug("requestMPI path 5");
				throwMultiplePersonsForIdentifierException(identity.getPerson().getFirstMPI().getValue(), getMPIIdsForPersonIds(matchPersonIds));
			}
			else if (matchPersonIds.contains(identity.getPerson().getId()))
			{
				logger.debug("found match within person for given identifier - add identity to person");
				logger.debug("requestMPI path 6");
				Person matchingPerson = getDBPersonById(identity.getPerson().getId()); // TODO = identity.getPerson()?
				identity.setPerson(matchingPerson);
				if (!RequestSaveAction.DONT_SAVE.equals(saveAction))
				{
					double matchingScore = deduplicationResultForDB.getHighestMatchingScoreForPersonId(identity.getPerson().getId());
					matchingPerson = saveIdentityAndPossibleMatches(domain, identity, identityPP, identityPPCO,
							fromUpdate ? IdentityHistoryEvent.UPDATE : IdentityHistoryEvent.MATCH, deduplicationResultForDB.getMatches(), comment,
							matchingScore, timestamp);
				}
				result = new ResponseEntryDTO(matchingPerson.toDTO(), MatchStatus.MATCH);
			}
			else if (!matchPersonIds.isEmpty())
			{
				logger.debug("requestMPI path 7");
				throwMultiplePersonsForIdentifierException(identity.getPerson().getFirstMPI().getValue(), getMPIIdsForPersonIds(matchPersonIds));
			}
			else
			{
				LongSet possibleMatchPersonIds = deduplicationResultForDB.getPossibleMatchPersonIDs();
				if (cemfim && possibleMatchPersonIds.size() > 1)
				{
					logger.debug("requestMPI path 9");
					throwMultiplePersonsForIdentifierException(identity.getPerson().getFirstMPI().getValue(),
							getMPIIdsForPersonIds(possibleMatchPersonIds));
				}
				else if (possibleMatchPersonIds.contains(identity.getPerson().getId()))
				{
					logger.debug("found possible match within person for given identifier - add identity to person");
					logger.debug("requestMPI path 10");
					Person matchingPerson = getDBPersonById(identity.getPerson().getId());
					identity.setPerson(matchingPerson);
					if (!RequestSaveAction.DONT_SAVE.equals(saveAction))
					{
						double matchingScore = deduplicationResultForDB.getHighestMatchingScoreForPersonId(identity.getPerson().getId());
						matchingPerson = saveIdentityAndPossibleMatches(domain, identity, identityPP, identityPPCO,
								fromUpdate ? IdentityHistoryEvent.UPDATE : IdentityHistoryEvent.MATCH, deduplicationResultForDB.getPossibleMatches(),
								comment, matchingScore, timestamp);
					}
					result = new ResponseEntryDTO(matchingPerson.toDTO(), MatchStatus.MATCH);
				}
				else
				{
					logger.debug("requestMPI path 8");
					throwIDatDontFitIdentifierException(identity.getPerson().getId());
				}
			}
		}
		return result;
	}

	private Set<String> getMPIIdsForPersonIds(LongSet personIds)
	{
		Set<String> result = new HashSet<>();
		for (long personId : personIds)
		{
			try
			{
				result.add(getDBPersonById(personId).getFirstMPI().getValue());
			}
			catch (UnknownObjectException e)
			{
				logger.error("unexpected exception: can't find person for id " + personId, e);
			}
		}
		return result;
	}

	private ResponseEntryDTO processRequestMPIWithoutIdentifier(Domain domain, RequestSaveAction saveAction, Identity identity,
			IdentityPreprocessed identityPP, String comment, Timestamp timestamp) throws MPIException, UnknownObjectException
	{
		// diese methode gibt aber nie "null" zurueck - eclipse erkennt nicht, dass die throw... bzw. handleSaveServiceException auf jeden fall ne exception werfen
		ResponseEntryDTO result = null;
		logger.debug("didn't found a person for the given local ids - check for perfect match within db");
		PreprocessedCacheObject identityPPCO = DOMAIN_CACHE.createPreprocessedCacheObjectFromIdentityPP(domain.getName(), identityPP);
		PreprocessedCacheObject perfectMatchPPCO = DOMAIN_CACHE.perfectMatch(domain.getName(), identityPPCO);
		if (perfectMatchPPCO != null)
		{
			logger.debug("perfect match found within db - update identity");
			logger.debug("requestMPI path 11");
			Identity perfectMatch;
			if (RequestSaveAction.SAVE_ALL.equals(saveAction))
			{
				perfectMatch = updateIdentity(domain, perfectMatchPPCO.getIdentityId(), identity, identityPP, perfectMatchPPCO,
						IdentityHistoryEvent.PERFECT_MATCH, comment, FellegiSunterAlgorithm.MATCHING_SCORE_FOR_PERFECT_MATCH, timestamp);
			}
			else if (RequestSaveAction.DONT_SAVE_ON_PERFECT_MATCH_EXCEPT_CONTACTS.equals(saveAction))
			{
				perfectMatch = saveContacts(perfectMatchPPCO.getIdentityId(), identity.getContacts());
			}
			else
			{
				// rueckgabe ist ja der stand nach abfrage - bei save = false halt die matchende person
				perfectMatch = getDBIdentityById(perfectMatchPPCO.getIdentityId());
			}
			result = new ResponseEntryDTO(perfectMatch.getPerson().toDTO(), MatchStatus.PERFECT_MATCH);
		}
		else
		{
			logger.debug("no perfect match found within db - search for other matches");
			DeduplicationResult deduplicationResultForDB = DOMAIN_CACHE.match(domain.getName(), identityPPCO);
			if (deduplicationResultForDB.hasNoResults())
			{
				logger.debug("no match found within db - add new person");
				logger.debug("requestMPI path 15");
				Person newPerson;
				if (!RequestSaveAction.DONT_SAVE.equals(saveAction))
				{
					newPerson = saveIdentity(domain, identity, identityPP, identityPPCO, IdentityHistoryEvent.NEW, comment, 0.0, timestamp);
				}
				else
				{
					newPerson = createDummyPerson(domain, identity, timestamp);
				}
				result = new ResponseEntryDTO(newPerson.toDTO(), MatchStatus.NO_MATCH);
			}
			else
			{
				long matchingPersonId = deduplicationResultForDB.getUniqueMatchPersonId();
				if (matchingPersonId != -1l)
				{
					logger.debug("match found within db - add identity");
					logger.debug("requestMPI path 12");
					Person matchingPerson = getDBPersonById(matchingPersonId);
					identity.setPerson(matchingPerson);
					if (!RequestSaveAction.DONT_SAVE.equals(saveAction))
					{
						logger.debug("unique match found within db - add identity to the matching person");
						double matchingScore = deduplicationResultForDB.getHighestMatchingScoreForPersonId(identity.getPerson().getId());
						matchingPerson = saveIdentity(domain, identity, identityPP, identityPPCO, IdentityHistoryEvent.MATCH, comment, matchingScore,
								timestamp);
					}
					// kein else - rueckgabe ist ja der stand nach abfrage - bei save = false halt die matchende person
					result = new ResponseEntryDTO(matchingPerson.toDTO(), MatchStatus.MATCH);
				}
				else if (deduplicationResultForDB.hasMatches())
				{
					logger.debug("requestMPI path 13");
					Person newPerson;
					if (!RequestSaveAction.DONT_SAVE.equals(saveAction))
					{
						logger.debug("multiple matches found within db - store a new person for the request and possible matches for the matches");
						newPerson = saveIdentityAndPossibleMatches(domain, identity, identityPP, identityPPCO, IdentityHistoryEvent.NEW,
								deduplicationResultForDB.getMatches(), comment, 0.0, timestamp);
					}
					else
					{
						newPerson = createDummyPerson(domain, identity, timestamp);
					}
					result = new ResponseEntryDTO(newPerson.toDTO(), MatchStatus.MULTIPLE_MATCH);
				}
				else if (deduplicationResultForDB.hasPossibleMatches())
				{
					logger.debug("requestMPI path 14");
					Person newPerson;
					if (!RequestSaveAction.DONT_SAVE.equals(saveAction))
					{
						logger.debug("found possible matches within db - store a new person for the request and the found possible matches");
						newPerson = saveIdentityAndPossibleMatches(domain, identity, identityPP, identityPPCO, IdentityHistoryEvent.NEW,
								deduplicationResultForDB.getPossibleMatches(), comment, 0.0, timestamp);
					}
					else
					{
						newPerson = createDummyPerson(domain, identity, timestamp);
					}
					result = new ResponseEntryDTO(newPerson.toDTO(), MatchStatus.POSSIBLE_MATCH);
				}
				else
				{
					String message = "unexpected path while analysing match results";
					logger.error(message);
					throw new MPIException(MPIErrorCode.INTERNAL_ERROR, message);
				}
			}
		}
		return result;
	}

	private Identity mapIdentity(IdentityInBaseDTO identityDTO, Source source, Domain domain, boolean forcedReference, Timestamp timestamp)
			throws MPIException, UnknownObjectException
	{
		logger.debug("mapIdentity");
		Map<String, IdentifierDomain> idDomains = getIdentifierDomainsMapped();
		List<Identifier> localIdentifiers = new ArrayList<>();
		for (IdentifierDTO identifierDTO : identityDTO.getIdentifiers())
		{
			String idName = identifierDTO.getIdentifierDomain() != null ? identifierDTO.getIdentifierDomain().getName() : null;
			IdentifierDomain idDomain = idDomains.get(idName);
			if (idDomain == null)
			{
				String message = "identifier domain not found, name: " + idName;
				logger.error(message);
				throw new UnknownObjectException(message, UnknownObjectType.IDENTITFIER_DOMAIN, idName);
			}
			try
			{
				Identifier existingIdentifier = getDBIdentifierById(new IdentifierId(idName, identifierDTO.getValue()));
				localIdentifiers.add(existingIdentifier);
			}
			catch (UnknownObjectException maybe)
			{
				// neuer local identifier
				localIdentifiers.add(new Identifier(identifierDTO, idDomain, timestamp));
			}
		}
		Person existingPerson = null;
		try
		{
			existingPerson = getDBPersonByMultipleLocalIdentifier(domain, localIdentifiers, false, true);
		}
		catch (MPIException e)
		{
			logger.debug("requestMPI path 2 or updateMPI path 3");
			throw e;
		}
		catch (UnknownObjectException maybe)
		{
			// ist ok, keine person zu gegebenen identifiern bekannt
			logger.debug("ignoring expected exception: " + maybe.getMessage());
		}

		List<Contact> contacts = new ArrayList<>();
		if (identityDTO instanceof IdentityInDTO)
		{
			for (ContactInDTO contactDTO : ((IdentityInDTO) identityDTO).getContacts())
			{
				contacts.add(new Contact(contactDTO, null, timestamp));
			}
		}

		Identity result = new Identity(identityDTO, localIdentifiers, contacts, source, existingPerson, forcedReference, timestamp);
		for (Contact contact : result.getContacts())
		{
			contact.setIdentity(result);
		}
		logger.debug("identity mapped");
		return result;
	}

	private Person createDummyPerson(Domain domain, Identity identity, Timestamp timestamp)
	{
		logger.debug("creating dummy response");
		Identifier dummyMpi = new Identifier();
		dummyMpi.setIdentifierDomain(domain.getMpiDomain());
		dummyMpi.setValue("-1");
		List<Identity> identities = new ArrayList<>();
		identities.add(identity);
		Person result = new Person(false, dummyMpi, domain, identities, timestamp);
		identity.setPerson(result);
		return result;
	}

	private void throwIDatDontFitIdentifierException(long personId) throws MPIException
	{
		String message = "the given idat doesn't fit to the person (personId " + personId + ") belonging to the given identifier";
		logger.error(message);
		throw new MPIException(MPIErrorCode.IDAT_DONT_FIT_TO_IDENTIFIER, message);
	}

	private void throwMultiplePersonsForIdentifierException(String mpiId, Set<String> relatedMPIIds) throws MPIException
	{
		Set<String> allRelatedMPIIds = new HashSet<>(relatedMPIIds);
		allRelatedMPIIds.remove(mpiId); // kann sein, dass die dort auch enthalten ist
		StringBuilder sb = new StringBuilder("found one person with the given identifier or mpiid (MPI-Id: ");
		sb.append(mpiId);
		sb.append(") but some other which matches the idat or given identifier (MPI-Ids: ");
		for (String relatedMPIId : relatedMPIIds)
		{
			sb.append(relatedMPIId);
			sb.append(' ');
		}
		sb.append(") - these persons should be merged or this request is an error");
		String message = sb.toString();
		logger.error(message);
		allRelatedMPIIds.add(mpiId); // damit in der exception alle enthalten sind
		throw new MPIException(MPIErrorCode.MULTIPLE_PERSONS_FOR_IDENTIFIER, message, allRelatedMPIIds);
	}

	// ***********************************
	// cache (search)
	// ***********************************
	private IdentityPreprocessed getDBIdentityPreprocessedByIdentityId(long identityId) throws UnknownObjectException
	{
		logger.debug("getIdentityPreprocessedFromDBByIdentityId for " + identityId);
		IdentityPreprocessed result = em.find(IdentityPreprocessed.class, identityId);
		if (result == null)
		{
			String message = "identity preprocessed not found, identity id: " + identityId;
			logger.warn(message);
			throw new UnknownObjectException(message, UnknownObjectType.IDENTITY, Long.toString(identityId));
		}
		logger.debug("identity preprocessed found");
		return result;
	}

	// ***********************************
	// domains
	// ***********************************
	protected Domain getDBDomain(String domainName) throws UnknownObjectException
	{
		logger.debug("getDBDomain with name " + domainName);
		Domain result = DOMAIN_CACHE.getDomain(domainName);
		logger.debug("domain found");
		return result;
	}

	/**
	 * caller need to call {@link #addDomainToCache(Domain)} with the result of this method
	 *
	 * @param dto
	 * @param timestamp
	 * @return
	 * @throws InvalidParameterException
	 * @throws UnknownObjectException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Domain saveDomain(DomainDTO dto, Timestamp timestamp) throws InvalidParameterException, UnknownObjectException
	{
		logger.debug("saveDomain");
		IdentifierDomain mpiDomain = getDBIdentifierDomain(dto.getMpiDomain().getName());
		Source safeSource = getDBSource(dto.getSafeSource().getName());
		Domain domain;
		try
		{
			domain = new Domain(dto, mpiDomain, safeSource, timestamp);
			int mpiPrefix = Integer.valueOf(domain.getMatchingConfiguration().getMpiPrefix());
			if (mpiPrefix <= 0 || mpiPrefix > 9999)
			{
				String message = "invalid mpi prefix - have to be an integer >0 and <=9999 but is " + mpiPrefix;
				logger.error(message);
				throw new InvalidParameterException("domain.getConfig()", message);
			}
		}
		catch (Exception e)
		{
			String message = e.getMessage();
			logger.error(message, e);
			throw new InvalidParameterException("domain.getConfig()", message);
		}
		em.persist(domain);
		em.flush();
		return domain;
	}

	public void addDomainToCache(Domain domain) throws DuplicateEntryException, InvalidParameterException
	{
		DOMAIN_CACHE.addDomain(domain, getPersonCountForDomain(domain));
		synchronized (COUNTER_MAP)
		{
			CounterMapKey key = new CounterMapKey(domain.getMpiDomain().getName(), domain.getMatchingConfiguration().getMpiPrefix());
			if (!COUNTER_MAP.containsKey(key))
			{
				COUNTER_MAP.put(key, 0l);
			}
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Domain updateDomain(Domain domain, DomainDTO dto) throws UnknownObjectException
	{
		logger.debug("updateDomain");
		IdentifierDomain mpiDomain = getDBIdentifierDomain(dto.getMpiDomain().getName());
		Source safeSource = getDBSource(dto.getSafeSource().getName());
		domain.update(dto, mpiDomain, safeSource);
		domain = em.merge(domain); // nicht ganz klar, warum das gemacht werden muss; haengt wahrscheinlich mit dem cachen zusammen
		em.flush();
		return domain;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Domain updateDomainInUse(Domain domain, String label, String description)
	{
		logger.debug("updateDomainInUse");
		domain.updateInUse(label, description);
		domain = em.merge(domain); // nicht ganz klar, warum das gemacht werden muss; haengt wahrscheinlich mit dem cachen zusammen
		em.flush();
		return domain;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void deleteDBDomain(String domainName, boolean force) throws ObjectInUseException, UnknownObjectException
	{
		logger.debug("deleteDomain");
		Domain domain = getDBDomain(domainName);
		if (force)
		{
			for (Person person : getAllPersonsForDomain(domain))
			{
				deactivatePersonObject(person);
				try
				{
					deletePersonObject(person);
				}
				catch (IllegalOperationException | UnknownObjectException impossible)
				{
					logger.fatal("impossible exception while forcefully deleting domain " + domain.getName());
				}
			}
		}
		else if (domain.isInUse())
		{
			String message = "can't delete domain " + domainName + " because there are persons registered under this domain";
			logger.error(message);
			throw new ObjectInUseException(message);
		}
		domain = em.merge(domain); // nicht ganz klar, warum das gemacht werden muss; haengt wahrscheinlich mit dem cachen zusammen
		em.remove(domain);
		em.flush();
		DOMAIN_CACHE.removeDomain(domain.getName());
		synchronized (COUNTER_MAP)
		{
			CounterMapKey key = new CounterMapKey(domain.getMpiDomain().getName(), domain.getMatchingConfiguration().getMpiPrefix());
			if (COUNTER_MAP.containsKey(key))
			{
				COUNTER_MAP.removeLong(key);
			}
		}
	}

	private List<Person> getAllPersonsForDomain(Domain domain)
	{
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<Person> criteriaQuery = criteriaBuilder.createQuery(Person.class);
		Root<Person> root = criteriaQuery.from(Person.class);
		Predicate predicate = criteriaBuilder.equal(root.get(Person_.domain), domain);
		criteriaQuery.select(root).where(predicate);
		return em.createQuery(criteriaQuery).getResultList();
	}

	// ***********************************
	// identifier domains
	// ***********************************
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public IdentifierDomain saveIdentifierDomain(IdentifierDomainDTO identifierDomain, Timestamp timestamp)
	{
		IdentifierDomain result = new IdentifierDomain(identifierDomain, timestamp);
		em.persist(result);
		em.flush();
		return result;
	}

	protected IdentifierDomain getDBIdentifierDomain(String name) throws UnknownObjectException
	{
		logger.debug("getDBIdentifierDomain for " + name);
		IdentifierDomain result = em.find(IdentifierDomain.class, name);
		if (result == null)
		{
			String message = "identifier domain not found, name: " + name;
			logger.warn(message);
			throw new UnknownObjectException(message, UnknownObjectType.IDENTITFIER_DOMAIN, name);
		}
		logger.debug("identifier domain found");
		return result;
	}

	protected List<IdentifierDomain> getDBIdentifierDomains()
	{
		logger.debug("getDBIdentifierDomains");
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<IdentifierDomain> criteriaQuery = criteriaBuilder.createQuery(IdentifierDomain.class);
		Root<IdentifierDomain> root = criteriaQuery.from(IdentifierDomain.class);
		criteriaQuery.select(root);
		List<IdentifierDomain> result = em.createQuery(criteriaQuery).getResultList();
		logger.debug("found " + result.size() + " identifier domains");
		return result;
	}

	protected Map<String, IdentifierDomain> getIdentifierDomainsMapped()
	{
		logger.debug("getIdentifierDomainsMapped");
		// TODO caching
		Map<String, IdentifierDomain> result = new HashMap<>();
		for (IdentifierDomain idDomain : getDBIdentifierDomains())
		{
			result.put(idDomain.getName(), idDomain);
		}
		logger.debug("found " + result.size() + " identifier domains");
		return result;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public IdentifierDomain updateDBIdentifierDomain(IdentifierDomainDTO dto) throws UnknownObjectException
	{
		logger.debug("updateDBIdentifierDomain");
		IdentifierDomain identifierDomain = getDBIdentifierDomain(dto.getName());
		identifierDomain.update(dto);
		em.flush();
		return identifierDomain;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void deleteDBIdentifierDomain(String name) throws ObjectInUseException, UnknownObjectException
	{
		logger.debug("deleteDBIdentifierDomain");
		IdentifierDomain identifierDomain = getDBIdentifierDomain(name);
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<Identifier> root = criteriaQuery.from(Identifier.class);
		Predicate predicate = criteriaBuilder.equal(root.get(Identifier_.identifierDomain).get(IdentifierDomain_.name), name);
		criteriaQuery.select(criteriaBuilder.count(root)).where(predicate);
		long count = em.createQuery(criteriaQuery).getSingleResult();
		if (count > 0)
		{
			String message = "can't delete identifier domain " + name + " because it is used by " + count + " identifiers";
			logger.error(message);
			throw new ObjectInUseException(message);
		}
		Root<Domain> root2 = criteriaQuery.from(Domain.class);
		predicate = criteriaBuilder.equal(root2.get(Domain_.mpiDomain).get(IdentifierDomain_.name), name);
		criteriaQuery.select(criteriaBuilder.count(root2)).where(predicate);
		count = em.createQuery(criteriaQuery).getSingleResult();
		if (count > 0)
		{
			String message = "can't delete identifier domain " + name + " because it is configured as mpi-domain within " + count + " domains";
			logger.error(message);
			throw new ObjectInUseException(message);
		}
		em.remove(identifierDomain);
		em.flush();
	}

	// ***********************************
	// identifier
	// ***********************************
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void saveIdentifierToPerson(String domainName, String mpiId, List<IdentifierDTO> localIds, Timestamp timestamp)
			throws MPIException, UnknownObjectException
	{
		logger.debug("saveIdentifierToPerson");
		Domain domain = getDBDomain(domainName);
		Person person = getDBPersonByMPI(domain, mpiId);
		addIdsToIdentity(domain, person.getReferenceIdentity(), localIds, timestamp);
	}

	private void addIdsToIdentity(Domain domain, Identity identity, List<IdentifierDTO> localIds, Timestamp timestamp)
			throws UnknownObjectException, MPIException
	{
		logger.debug("addIdsToIdentity");
		for (IdentifierDTO identifierDTO : localIds)
		{
			IdentifierDomain identifierDomain = getDBIdentifierDomain(identifierDTO.getIdentifierDomain().getName());
			IdentifierId identifierId = new IdentifierId(identifierDTO);
			Identifier identifier;
			try
			{
				identifier = getDBIdentifierById(identifierId);
				try
				{
					Person existingPerson = getDBPersonByLocalIdentifier(domain, identifier);
					if (!identity.getPerson().equals(existingPerson))
					{
						Set<String> relatedMPIIds = new HashSet<>();
						relatedMPIIds.add(existingPerson.getFirstMPI().getValue());
						throwMultiplePersonsForIdentifierException(identity.getPerson().getFirstMPI().getValue(), relatedMPIIds);
					}
				}
				catch (UnknownObjectException maybe)
				{
					// keine person zu identifier gefunden - sollte zwar nicht vorkommen, waere aber ok
				}
			}
			catch (UnknownObjectException expected)
			{
				identifier = new Identifier(identifierDTO, identifierDomain, timestamp);
				em.persist(identifier);
				em.flush();
			}
			if (!identity.getIdentifiers().contains(identifier))
			{
				identity.getIdentifiers().add(identifier);
			}
			else
			{
				logger.info("identifier already belongs this identity");
			}
			logger.debug("identifier added to identity");
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void saveLocalIdentifierToIdentifier(String domainName, IdentifierDTO identifierDTO, List<IdentifierDTO> localIds, Timestamp timestamp)
			throws MPIException, UnknownObjectException
	{
		logger.debug("saveLocalIdentifierToIdentifier");
		Domain domain = getDBDomain(domainName);
		IdentifierDomain identifierDomain = getDBIdentifierDomain(identifierDTO.getIdentifierDomain().getName());
		List<Identity> identities = getDBIdentitiesByIdentifierForDomain(domain, identifierDTO.getValue(), identifierDomain);
		for (Identity identity : identities)
		{
			addIdsToIdentity(domain, identity, localIds, timestamp);
		}
		logger.debug("local identifier added");
	}

	protected Identifier getDBIdentifierById(IdentifierId id) throws UnknownObjectException
	{
		logger.debug("getDBIdentifierById for " + id);
		Identifier result = em.find(Identifier.class, id);
		if (result == null)
		{
			String message = "unknown identifier " + id.getValue() + " within " + id.getIdentifierDomain();
			logger.info(message);
			throw new UnknownObjectException(message, UnknownObjectType.IDENTIFIER, id.toString());
		}
		else
		{
			logger.debug("identifier found");
			return result;
		}
	}

	private List<Identifier> getDBIdentifierByPerson(Person person)
	{
		@SuppressWarnings("unchecked")
		List<Identifier> idList = em.createNamedQuery("Identifier.findByPerson").setParameter("person", person).getResultList();
		return idList;
	}

	// ***********************************
	// sources
	// ***********************************
	public Source getDBSource(String name) throws UnknownObjectException
	{
		Source result = em.find(Source.class, name);
		if (result == null)
		{
			String message = "source not found, name: " + name;
			logger.warn(message);
			throw new UnknownObjectException(message, UnknownObjectType.SOURCE, name);
		}
		else
		{
			logger.debug("source found");
			return result;
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Source saveSource(SourceDTO source, Timestamp timestamp)
	{
		Source result = new Source(source, timestamp);
		em.persist(result);
		em.flush();
		return result;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Source updateDBSource(SourceDTO dto) throws UnknownObjectException
	{
		logger.debug("updateDBSource");
		Source source = getDBSource(dto.getName());
		source.update(dto);
		em.flush();
		return source;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void deleteDBSource(String name) throws ObjectInUseException, UnknownObjectException
	{
		logger.debug("deleteDBSource");
		Source source = getDBSource(name);
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<Identity> root = criteriaQuery.from(Identity.class);
		Predicate predicate = criteriaBuilder.equal(root.get(Identity_.source).get(Source_.name), name);
		criteriaQuery.select(criteriaBuilder.count(root)).where(predicate);
		long count = em.createQuery(criteriaQuery).getSingleResult();
		if (count > 0)
		{
			String message = "can't delete source " + name + " because there are " + count + " identities from this source stored";
			logger.error(message);
			throw new ObjectInUseException(message);
		}
		Root<Domain> root2 = criteriaQuery.from(Domain.class);
		predicate = criteriaBuilder.equal(root2.get(Domain_.safeSource).get(Source_.name), name);
		criteriaQuery.select(criteriaBuilder.count(root2)).where(predicate);
		count = em.createQuery(criteriaQuery).getSingleResult();
		if (count > 0)
		{
			String message = "can't delete source" + name + " because it is configured as safe source within " + count + " domains";
			logger.error(message);
			throw new ObjectInUseException(message);
		}
		em.remove(source);
		em.flush();
	}

	// ***********************************
	// persons
	// ***********************************
	protected Person getDBPersonByLocalIdentifier(Domain domain, Identifier identifier) throws UnknownObjectException
	{
		logger.debug("getDBPersonByLocalIdentifier");
		try
		{
			Person result = (Person) em.createNamedQuery("Person.findByLocalIdentifierForDomain").setParameter("identifier", identifier)
					.setParameter("domain", domain).getSingleResult();
			return result;
		}
		catch (NoResultException maybe)
		{
			String message = "no person found for local identifier " + identifier + " within domain " + domain.getName();
			logger.info(message);
			throw new UnknownObjectException(message, UnknownObjectType.PERSON, identifier.toShortString());
		}
	}

	protected Person getDBPersonByMultipleLocalIdentifier(Domain domain, List<Identifier> identifierList, boolean allIdentifierRequired,
			boolean suppressLoggingForUnknownObjectException) throws MPIException, UnknownObjectException
	{
		logger.debug("getPersonByMultipleLocalIdentifier");
		Set<String> relatedMPIIds = new HashSet<>();
		Person result = null;
		for (Identifier identifier : identifierList)
		{
			try
			{
				Person possibleDuplicate = getDBPersonByLocalIdentifier(domain, identifier);
				if (result == null || result.equals(possibleDuplicate))
				{
					result = possibleDuplicate;
				}
				else
				{
					relatedMPIIds.add(possibleDuplicate.getFirstMPI().getValue());
				}
			}
			catch (UnknownObjectException maybe)
			{
				String message = "no person found for local identifier " + identifier + " within domain " + domain.getName()
						+ "; allIdentifierRequired is " + allIdentifierRequired;
				if (allIdentifierRequired)
				{
					if (!suppressLoggingForUnknownObjectException)
					{
						logger.error(message, maybe);
					}
					else
					{
						logger.debug(message);
					}
					throw new UnknownObjectException(message, UnknownObjectType.PERSON, identifier.toShortString());
				}
				else
				{
					logger.debug(message);
				}
			}
		}
		if (!relatedMPIIds.isEmpty())
		{
			throwMultiplePersonsForIdentifierException(result.getFirstMPI().getValue(), relatedMPIIds);
		}
		if (result == null)
		{
			StringBuffer sb = new StringBuffer("no person found for ");
			for (Identifier identifier : identifierList)
			{
				sb.append(identifier.toShortString());
				sb.append(' ');
			}
			sb.append("within domain ");
			sb.append(domain.getName());
			sb.append(" - allIdentifierRequired is ");
			sb.append(allIdentifierRequired);
			String message = sb.toString();
			if (!suppressLoggingForUnknownObjectException)
			{
				logger.error(message);
			}
			else
			{
				logger.debug(message);
			}
			throw new UnknownObjectException(message, UnknownObjectType.PERSON, "multiple identifier - see message");
		}
		return result;
	}

	protected Person getDBPersonById(long id) throws UnknownObjectException
	{
		logger.debug("getPersonById for id " + id);
		Person result = em.find(Person.class, id);
		if (result == null)
		{
			String message = "person not found, id: " + id;
			logger.debug(message);
			throw new UnknownObjectException(message, UnknownObjectType.PERSON, Long.toString(id));
		}
		else
		{
			logger.debug("person found");
			return result;
		}
	}

	/**
	 * Returns the active person with the given mpi as firstMPI.
	 *
	 * @param domain
	 *            the person's domain
	 * @param mpi
	 *            the identifier
	 * @return the active person with the given mpi as firstMPI.
	 * @throws UnknownObjectException
	 *             when no such person is found
	 */
	protected Person getDBPersonByFirstMPI(Domain domain, Identifier mpi) throws UnknownObjectException
	{
		return getDBPersonByFirstMPI(domain, mpi, false);
	}

	/**
	 * Returns the (active or deactivated) person with the given mpi as firstMPI.
	 *
	 * @param domain
	 *            the person's domain
	 * @param mpi
	 *            the identifier
	 * @param includeDeactivatedPersons
	 *            true to also find deactivated persons
	 * @return the (active or deactivated) person with the given mpi as firstMPI.
	 * @throws UnknownObjectException
	 *             when no such person is found
	 */
	protected Person getDBPersonByFirstMPI(Domain domain, Identifier mpi, boolean includeDeactivatedPersons) throws UnknownObjectException
	{
		logger.debug("getDBPersonByFirstMPI " + (includeDeactivatedPersons ? "(even if deactivated)" : ""));
		try
		{
			String query = includeDeactivatedPersons ? "Person.findByFirstMPIForDomainEvenIfDeactivated" : "Person.findByFirstMPIForDomain";
			Person result = (Person) em.createNamedQuery(query).setParameter("domain", domain).setParameter("mpi", mpi).getSingleResult();
			logger.debug("person found");
			return result;
		}
		catch (NoResultException maybe)
		{
			String message = "no person found for mpi " + mpi + " within domain " + domain.getName();
			logger.info(message);
			throw new UnknownObjectException(message, UnknownObjectType.PERSON, mpi.toShortString());
		}
	}

	/**
	 * Returns the associated active person for the given mpiId.
	 *
	 * @param domain
	 *            the person's domain
	 * @param mpiId
	 *            the identifier ID
	 * @return the associated active person for the given mpiId.
	 * @throws UnknownObjectException
	 *             when no such person is found
	 */
	protected Person getDBPersonByMPI(Domain domain, String mpiId) throws UnknownObjectException
	{
		IdentifierId identifierId = new IdentifierId(domain.getMpiDomain().getName(), mpiId);
		Identifier mpi = getDBIdentifierById(identifierId);
		return getDBPersonByMPI(domain, mpi, false);
	}

	/**
	 * Returns the associated (active or deactivated) person for the given mpiId. When including deactivated persons,
	 * then a deactivated person with the given mpi as firstMPI will be preferred over reassigned persons.
	 *
	 * @param domain
	 *            the person's domain
	 * @param mpiId
	 *            the identifier ID
	 * @param includeDeactivatedPersons
	 *            true to also find deactivated persons
	 * @return the associated (active or deactivated) person for the given mpiId.
	 * @throws UnknownObjectException
	 *             when no such person is found
	 */
	protected Person getDBPersonByMPI(Domain domain, Identifier mpi, boolean includeDeactivatedPersons) throws UnknownObjectException
	{
		logger.debug("getDBPersonByMPI for mpi " + mpi.getValue() + " within domain " + domain.getName());
		Person result;
		try
		{
			result = getDBPersonByFirstMPI(domain, mpi, includeDeactivatedPersons);
		}
		catch (UnknownObjectException e)
		{
			if (MatchingMode.MATCHING_IDENTITIES.equals(domain.getMatchingConfiguration().getMatchingMode()))
			{
				// mpi koennte durch eine zusammenfuehrung zum lokalen identifier geworden sein
				result = getDBPersonByLocalIdentifier(domain, mpi);
			}
			else
			{
				// im nd-modus kann eine mpi nicht zur lokalen id werden, da hier keine possible matches entstehen
				logger.info("no person found");
				throw e;
			}
		}
		return result;
	}

	/**
	 * Returns true if the given mpi is associated with a (active or deactivated) person.
	 *
	 * @param domain
	 *            the person's domain
	 * @param mpi
	 *            the person's identifier
	 * @param includeDeactivatedPersons
	 *            true to include deactivated persons
	 * @return true if the given mpi is associated with a (active or deactivated) person
	 */
	private boolean hasDBPersonForMPI(Domain domain, Identifier mpi, boolean includeDeactivatedPersons)
	{
		try
		{
			return getDBPersonByMPI(domain, mpi, includeDeactivatedPersons) != null;
		}
		catch (UnknownObjectException e)
		{
			return false;
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public ResponseEntryDTO updateDBPerson(String domainName, String mpiId, IdentityInDTO identityDTO, String sourceName, boolean force,
			String comment, RequestConfig requestConfig, Timestamp timestamp) throws InvalidParameterException, MPIException, UnknownObjectException
	{
		logger.debug("updateDBPerson");
		ResponseEntryDTO result = null;
		Domain domain = getDBDomain(domainName);
		Source source = getDBSource(sourceName);
		IdentifierId mpiIdentifierId = new IdentifierId(domain.getMpiDomain().getName(), mpiId);
		Identifier mpi = getDBIdentifierById(mpiIdentifierId);
		// hier fliegt die UnknownObjectException im falle von pfad 2
		Person person = getDBPersonByFirstMPI(domain, mpi);
		// hier kann schon die MPIException von pfad 3 fliegen
		Identity identity = mapIdentity(identityDTO, source, domain, requestConfig.isForceReferenceUpdate(), timestamp);
		if (identity.getPerson() != null && !person.equals(identity.getPerson()))
		{
			logger.debug("updatePerson path 3");
			throwMultiplePersonsForIdentifierException(mpiId, Collections.singleton(identity.getPerson().getFirstMPI().getValue()));
		}
		identity.setPerson(person);
		// hier fliegt die InvalidParameterException im falle von pfad 1
		IdentityPreprocessed identityPP = DOMAIN_CACHE.preprocess(domainName, identity);
		RequestSaveAction saveAction = requestConfig.getSaveAction();
		if (!force)
		{
			logger.debug("updatePerson path 4");
			result = processRequestMPIWithIdentifier(domain, saveAction, identity, identityPP, comment, timestamp, true);
		}
		else
		{
			PreprocessedCacheObject identityPPCO = DOMAIN_CACHE.createPreprocessedCacheObjectFromIdentityPP(domain.getName(), identityPP);
			List<PreprocessedCacheObject> existingIdentitiesPPCOs = new ArrayList<>();
			for (Identity existingIdentity : identity.getPerson().getIdentities())
			{
				existingIdentitiesPPCOs.add(DOMAIN_CACHE.getPreprocessedCacheObjectByIdentityId(domainName, existingIdentity.getId()));
			}
			// perfectMatch erst mal nur auf den zur mpi-id passenden identitaeten
			PreprocessedCacheObject perfectMatchPPCO = DOMAIN_CACHE.perfectMatch(domainName, identityPPCO, existingIdentitiesPPCOs);
			if (perfectMatchPPCO != null)
			{
				logger.debug("perfect match found within the person for the given mpi-id");
				logger.debug("updatePerson path 5");
				Identity perfectMatch;
				if (RequestSaveAction.SAVE_ALL.equals(saveAction))
				{
					perfectMatch = updateIdentity(domain, perfectMatchPPCO.getIdentityId(), identity, identityPP, perfectMatchPPCO,
							IdentityHistoryEvent.FORCED_UPDATE, comment, FellegiSunterAlgorithm.MATCHING_SCORE_FOR_PERFECT_MATCH, timestamp);
				}
				else if (RequestSaveAction.DONT_SAVE_ON_PERFECT_MATCH_EXCEPT_CONTACTS.equals(saveAction))
				{
					perfectMatch = saveContacts(perfectMatchPPCO.getIdentityId(), identity.getContacts());
				}
				else
				{
					// rueckgabe ist ja der stand nach abfrage - bei save = false halt die matchende person
					perfectMatch = getDBIdentityById(perfectMatchPPCO.getIdentityId());
				}
				result = new ResponseEntryDTO(perfectMatch.getPerson().toDTO(), MatchStatus.PERFECT_MATCH);
			}
			else
			{
				logger.debug("no perfect match found within person, verifying that there is no perfect match with another person");
				perfectMatchPPCO = DOMAIN_CACHE.perfectMatch(domainName, identityPPCO);
				if (perfectMatchPPCO != null)
				{
					logger.debug("updatePerson path 6");
					Person matchingPerson = getDBPersonById(perfectMatchPPCO.getPersonId());
					throwMultiplePersonsForIdentifierException(identity.getPerson().getFirstMPI().getValue(),
							Collections.singleton(matchingPerson.getFirstMPI().getValue()));
				}
				logger.debug("updatePerson path 7");
				if (!RequestSaveAction.DONT_SAVE.equals(saveAction))
				{
					logger.debug("check for new (possible) matches with updated identity");
					DeduplicationResult deduplicationResultForDB = DOMAIN_CACHE.match(domainName, identityPPCO);
					// kann 0.0 sein ...
					double matchingScore = deduplicationResultForDB.getHighestMatchingScoreForPersonId(identity.getPerson().getId());
					if (deduplicationResultForDB.hasMatches())
					{
						person = saveIdentityAndPossibleMatches(domain, identity, identityPP, identityPPCO, IdentityHistoryEvent.FORCED_UPDATE,
								deduplicationResultForDB.getMatches(), comment, matchingScore, timestamp);
					}
					else if (deduplicationResultForDB.hasPossibleMatches())
					{
						person = saveIdentityAndPossibleMatches(domain, identity, identityPP, identityPPCO, IdentityHistoryEvent.FORCED_UPDATE,
								deduplicationResultForDB.getPossibleMatches(), comment, matchingScore, timestamp);
					}
					else
					{
						person = saveIdentity(domain, identity, identityPP, identityPPCO, IdentityHistoryEvent.FORCED_UPDATE, comment, matchingScore,
								timestamp);
					}
				}
				result = new ResponseEntryDTO(person.toDTO(), MatchStatus.MATCH);
			}
		}
		logger.debug("updateDBPerson end");
		return result;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public ResponseEntryDTO addDBPerson(String domainName, IdentityInDTO identityDTO, String sourceName, String comment, Timestamp timestamp)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		logger.debug("addDBPerson");
		ResponseEntryDTO result = null;
		Domain domain = getDBDomain(domainName);
		Source source = getDBSource(sourceName);
		Identity identity = mapIdentity(identityDTO, source, domain, false, timestamp);
		logger.debug("check for existing persons with the given identifier");
		if (identity.getPerson() != null)
		{
			String message = "can't add identity as new person because at least one of the given identifiers already belongs to person with mpi "
					+ identity.getPerson().getFirstMPI().getValue();
			logger.error(message);
			throw new MPIException(MPIErrorCode.RESTRICTIONS_VIOLATED, message, Collections.singleton(identity.getPerson().getFirstMPI().getValue()));
		}
		IdentityPreprocessed identityPP = DOMAIN_CACHE.preprocess(domainName, identity);
		logger.debug("check for perfect match with existing identities");
		PreprocessedCacheObject identityPPCO = DOMAIN_CACHE.createPreprocessedCacheObjectFromIdentityPP(domain.getName(), identityPP);
		PreprocessedCacheObject perfectMatchPP = DOMAIN_CACHE.perfectMatch(domain.getName(), identityPPCO);
		if (perfectMatchPP != null)
		{
			Person existingPerson = getDBPersonById(perfectMatchPP.getPersonId());
			String message = "can't add identity as new person because the given identity already belongs to person with mpi "
					+ existingPerson.getFirstMPI().getValue();
			logger.error(message);
			throw new MPIException(MPIErrorCode.RESTRICTIONS_VIOLATED, message, Collections.singleton(existingPerson.getFirstMPI().getValue()));
		}
		DeduplicationResult deduplicationResultForDB = DOMAIN_CACHE.match(domain.getName(), identityPPCO);
		Person person;
		if (deduplicationResultForDB.hasMatches())
		{
			person = saveIdentityAndPossibleMatches(domain, identity, identityPP, identityPPCO, IdentityHistoryEvent.NEW,
					deduplicationResultForDB.getMatches(), comment, 0.0, timestamp);
		}
		else if (deduplicationResultForDB.hasPossibleMatches())
		{
			person = saveIdentityAndPossibleMatches(domain, identity, identityPP, identityPPCO, IdentityHistoryEvent.NEW,
					deduplicationResultForDB.getPossibleMatches(), comment, 0.0, timestamp);
		}
		else
		{
			person = saveIdentity(domain, identity, identityPP, identityPPCO, IdentityHistoryEvent.NEW, comment, 0.0, timestamp);
		}
		result = new ResponseEntryDTO(person.toDTO(), MatchStatus.NO_MATCH);
		DOMAIN_CACHE.incPersonCount(domainName);
		logger.debug("addDBPerson end");
		return result;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void deactivateDBPerson(String domainName, String mpiId) throws InvalidParameterException, UnknownObjectException
	{
		Domain domain = getDBDomain(domainName);
		IdentifierId mpiIdentifierId = new IdentifierId(domain.getMpiDomain().getName(), mpiId);
		Identifier mpiIdentifier = getDBIdentifierById(mpiIdentifierId);
		Person person = getDBPersonByFirstMPI(domain, mpiIdentifier, true);

		deactivatePersonObject(person);
		PersonHistory personHistory = new PersonHistory(person, "person deactivated", new Timestamp(System.currentTimeMillis()));
		em.persist(personHistory);
	}

	private void deactivatePersonObject(Person person) throws UnknownObjectException
	{
		person.setDeactivated(true);

		for (Identity identity : person.getIdentities())
		{
			deactivateDBIdentity(identity.getId());
		}
		em.flush();
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void deleteDBPerson(String domainName, String mpiId) throws IllegalOperationException, UnknownObjectException
	{
		Domain domain = getDBDomain(domainName);
		IdentifierId mpiIdentifierId = new IdentifierId(domain.getMpiDomain().getName(), mpiId);
		Identifier mpiIdentifier = getDBIdentifierById(mpiIdentifierId);
		deletePersonObject(getDBPersonByFirstMPI(domain, mpiIdentifier, true));
	}

	private void deletePersonObject(Person person) throws IllegalOperationException, UnknownObjectException
	{
		if (!person.isDeactivated())
		{
			throw new IllegalOperationException("person with first mpi id " + person.getFirstMPI().getId() + " is not deactivated and therefore can't be deleted");
		}

		// if a person is deactivated all its identities also have been deactivated,
		// so person.getIdentities() will return an empty list, right?
		// TODO: If so, then there is no need to loop over an empty list:
		for (Identity identity : new ArrayList<>(person.getIdentities()))
		{
			// TODO: this does never happens
			deleteDBIdentity(identity.getId());
		}

		getDBHistoryForPerson(person).stream().forEach(e -> em.remove(e));
		getDBHistoryForIdentityByPerson(person).stream().forEach(e -> em.remove(e));
		getDBPossibleMatchHistoryForPerson(person).stream().forEach(e -> em.remove(e));

		Identifier firstMPI = person.getFirstMPI();
		IdentifierDomain mpiDomain = person.getDomain().getMpiDomain();
		List<Identifier> idList = getDBIdentifierByPerson(person);
		for (Identifier id : idList)
		{
			if (id.getIdentifierDomain().equals(mpiDomain) && !id.getValue().equals(person.getFirstMPI().getValue()))
			{
				// mpi einer anderen person; durch zusammenlegung entstanden
				// die mpiId gehoert immer noch zu der "verliererperson" und kann hier daher nicht geloescht werden
				continue;
			}
			em.remove(id);
		}
		em.remove(person);
		em.flush();
		DOMAIN_CACHE.decPersonCount(person.getDomain().getName());

		// removing a person is not cascaded with deleting the person's first MPI anymore because the MPI might (and should)
		// be left in 'identity_identifier' (by reassigning) even after removing the person so check manually if it can be removed
		if (!hasDBPersonForMPI(person.getDomain(), firstMPI, true))
		{
			em.remove(firstMPI);
		}
	}

	public void updateDBPrivacy(String domainName, List<String> mpiIds, boolean onlyReferenceIdentity) throws InvalidParameterException, MPIException, UnknownObjectException
	{
		// No PII available --> bloomfilters cannot be generated
		if (PersistMode.PRIVACY_PRESERVING.equals(DOMAIN_CACHE.getConfigurationContainerForDomain(domainName).getPersistMode()))
		{
			return;
		}

		// No Privacy defined
		if (DOMAIN_CACHE.getConfigurationContainerForDomain(domainName).getPrivacy() == null)
		{
			return;
		}

		Domain domain = getDBDomain(domainName);
		List<Person> persons = new ArrayList<>();
		if (mpiIds == null || mpiIds.isEmpty())
		{
			persons.addAll(getAllPersonsForDomain(domain));
		}
		else
		{
			for (String mpi : mpiIds)
			{
				IdentifierId mpiIdentifierId = new IdentifierId(domain.getMpiDomain().getName(), mpi);
				Identifier mpiIdentifier = getDBIdentifierById(mpiIdentifierId);
				persons.add(getDBPersonByFirstMPI(domain, mpiIdentifier));
			}
		}

		for (Person p : persons)
		{
			List<Identity> identities = new ArrayList<>();

			if (onlyReferenceIdentity)
			{
				identities.add(p.getReferenceIdentity());
			}
			else
			{
				identities.addAll(p.getIdentities());
			}

			for (Identity ident : identities)
			{
				IdentityPreprocessed identityPP = DOMAIN_CACHE.preprocess(domainName, ident);
				identityPP = DOMAIN_CACHE.setPrivacyAttributes(domainName, identityPP, true);
				DOMAIN_CACHE.copyPrivacyAttributes(domainName, identityPP, ident);

				updateIdentity(domain, ident.getId(), ident, identityPP, DOMAIN_CACHE.createPreprocessedCacheObjectFromIdentityPP(domainName, identityPP),
						IdentityHistoryEvent.UPDATE, "Bloom filter added retroactively", FellegiSunterAlgorithm.MATCHING_SCORE_FOR_PERFECT_MATCH, new Timestamp(System.currentTimeMillis()));
			}
		}
	}

	// ***********************************
	// identities
	// ***********************************
	protected List<Identity> getDBIdentitiesByIdentifierForDomain(Domain domain, String value, IdentifierDomain identifierDomain)
	{
		logger.debug("getDBIdentitiesByIdentifier for identifier " + value + " within identifier domain " + identifierDomain + " for domain "
				+ domain.getName());
		@SuppressWarnings("unchecked")
		List<Identity> result = em.createNamedQuery("Identity.findByIdentifier").setParameter("value", value)
				.setParameter("identifierDomain", identifierDomain).setParameter("domain", domain).getResultList();
		logger.debug("found " + result.size() + " identities for identifier " + value + " within identifier domain " + identifierDomain);
		return result;
	}

	protected Identity getDBIdentityById(long id) throws UnknownObjectException
	{
		logger.debug("getDBIdentityById for " + id);
		Identity result = em.find(Identity.class, id);
		if (result == null)
		{
			String message = "identity not found, id: " + id;
			logger.debug(message);
			throw new UnknownObjectException(message, UnknownObjectType.IDENTITY, Long.toString(id));
		}
		else
		{
			logger.debug("identity found");
			return result;
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Person saveIdentity(Domain domain, Identity identity, IdentityPreprocessed identityPreprocessed, PreprocessedCacheObject identityPPCO,
			IdentityHistoryEvent historyEvent, String comment, double matchingScore, Timestamp timestamp)
			throws MPIException, UnknownObjectException
	{
		logger.debug("saveIdentity");
		Person person = identity.getPerson();
		boolean newPerson = person == null;
		if (newPerson)
		{
			logger.debug("no person set - create new");
			// TODO externe mpi
			Identifier mpiIdent = createMPIId(domain, timestamp);
			// mpiIdent.setIdentifierDomain(em.merge(mpiIdent.getIdentifierDomain()));
			person = new Person(false, mpiIdent, domain, new ArrayList<>(), timestamp);
			em.persist(person);
			identity.setPerson(person);
		}
		else
		{
			logger.debug("add identity to person with id: " + person.getId());
		}
		IdentityHistory identityHistory = new IdentityHistory(identity, historyEvent, comment, matchingScore, timestamp);
		PersonHistory personHistory = new PersonHistory(person, comment, timestamp);
		em.persist(identity);
		em.persist(identityHistory);
		em.persist(personHistory);
		person.getIdentities().add(identity);
		identityPreprocessed.setDBIds(identity.getId(), person.getId(), domain.getName(), timestamp);
		identityPPCO.setDBIds(person.getId(), identity.getId());
		em.persist(identityPreprocessed);
		em.flush();
		DOMAIN_CACHE.addIdentity(domain.getName(), identityPreprocessed, identityPPCO);
		if (newPerson)
		{
			DOMAIN_CACHE.incPersonCount(person.getDomain().getName());
		}
		logger.debug("identity saved");
		return person;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Identity updateIdentity(Domain domain, long identityId, Identity newIdentity, IdentityPreprocessed newIdentityPP,
			PreprocessedCacheObject newIdentityPPCO, IdentityHistoryEvent ihEvent, String comment, double matchingScore, Timestamp timestamp)
			throws MPIException, UnknownObjectException
	{
		logger.debug("updateDBIdentity for identity with id " + " because of " + ihEvent);
		Identity oldIdentity = getDBIdentityById(identityId);
		oldIdentity.update(newIdentity, timestamp);
		IdentityHistory identityHistory = new IdentityHistory(oldIdentity, ihEvent, comment, matchingScore, timestamp);
		em.persist(identityHistory);
		IdentityPreprocessed ipFromDB = getDBIdentityPreprocessedByIdentityId(oldIdentity.getId());
		ipFromDB.update(newIdentityPP);
		em.flush();
		DOMAIN_CACHE.updateIdentity(domain.getName(), oldIdentity.getId(), newIdentityPP, newIdentityPPCO);
		logger.debug("identity updated");
		return oldIdentity;
	}

	private Identifier createMPIId(Domain domain, Timestamp timestamp) throws UnknownObjectException
	{
		logger.debug("createMPIId for domain " + domain.getName());
		MPIGenerator gen = GENERATORS.get(domain.getMatchingConfiguration().getMpiGenerator());
		if (gen == null)
		{
			String message = "unknown generator " + domain.getMatchingConfiguration().getMpiGenerator();
			logger.error(message);
			throw new UnknownObjectException(message, UnknownObjectType.DOMAIN, domain.getName());
		}
		long counter;
		synchronized (COUNTER_MAP)
		{
			CounterMapKey key = new CounterMapKey(domain.getMpiDomain().getName(), domain.getMatchingConfiguration().getMpiPrefix());
			if (!COUNTER_MAP.containsKey(key))
			{
				String message = "no counter found for domain " + domain.getName();
				logger.error(message);
				throw new UnknownObjectException(message, UnknownObjectType.DOMAIN, domain.getName());
			}
			else
			{
				counter = COUNTER_MAP.getLong(key);
				counter++;
				COUNTER_MAP.put(key, counter);
			}
		}
		Identifier result = gen.generate(domain, counter, timestamp);
		logger.debug("mpi id generated: " + result.getValue());
		return result;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void saveReferenceIdentity(String domainName, String mpiId, long identityId, String comment, Timestamp timestamp)
			throws MPIException, UnknownObjectException
	{
		logger.debug("saveReferenceIdentity for domain " + domainName);
		Domain domain = getDBDomain(domainName);
		IdentifierId mpiIdentifierId = new IdentifierId(domain.getMpiDomain().getName(), mpiId);
		Identifier mpiIdentifier = getDBIdentifierById(mpiIdentifierId);
		Person person = getDBPersonByFirstMPI(domain, mpiIdentifier);
		Identity identity = getDBIdentityById(identityId);
		if (!identity.getPerson().equals(person))
		{
			String message = "can't set " + identity + " as reference identity for person with mpi " + mpiId
					+ " because it belongs to another person (mpi " + identity.getPerson().getFirstMPI().getValue() + ")";
			Set<String> relatedMpiIds = new HashSet<>();
			relatedMpiIds.add(mpiId);
			relatedMpiIds.add(identity.getPerson().getFirstMPI().getValue());
			logger.error(message);
			throw new MPIException(MPIErrorCode.RESTRICTIONS_VIOLATED, message, relatedMpiIds);
		}
		IdentityHistory identityHistory = new IdentityHistory(identity, IdentityHistoryEvent.SET_REFERENCE, "set as reference identity", 0.0,
				timestamp);
		em.persist(identityHistory);
		identity.setTimestamp(timestamp);
		identity.setForcedReference(true);
		person.setTimestamp(timestamp);
		PersonHistory personHistory = new PersonHistory(person, "set reference identity", timestamp);
		em.persist(personHistory);
		em.flush();
		logger.debug("reference identity set");
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void deactivateDBIdentity(long identityId) throws UnknownObjectException
	{
		Identity identity = getDBIdentityById(identityId);

		if (identity.isDeactivated())
		{
			return; // Nothing to do
		}

		// Remove possible matches
		List<IdentityLink> possibleMatches = getDBPossibleMatchesByIdentity(identity);
		try
		{
			for (IdentityLink il : possibleMatches)
			{
				saveRemovePossibleMatch(il.getId(), IdentityHistoryStrings.DEACTIVATED, new Timestamp(System.currentTimeMillis()));
			}
		}
		catch (InvalidParameterException impossible)
		{
			logger.fatal("impossible exception while deactivating identity with id " + identityId, impossible);
		}

		// Deactivate identity and remove from domain cache
		identity.setDeactivated(true);
		DOMAIN_CACHE.deleteIdentity(identity.getPerson().getDomain().getName(), identityId);

		// Remove pre-processed identity
		IdentityPreprocessed ip = em.find(IdentityPreprocessed.class, identity.getId());
		em.remove(ip);

		Timestamp refTimestamp = new Timestamp(System.currentTimeMillis());

		// Add history entry
		IdentityHistory identityHistory = new IdentityHistory(identity, IdentityHistoryEvent.DEACTIVATED, IdentityHistoryStrings.DEACTIVATED, 0.0, refTimestamp);
		em.persist(identityHistory);

		Person person = identity.getPerson();
		boolean allIdentitiesDeactivated = true;
		for (Identity i : person.getIdentities())
		{
			if (!i.isDeactivated())
			{
				allIdentitiesDeactivated = false;
				break;
			}
		}
		if (allIdentitiesDeactivated)
		{
			person.setDeactivated(true);
			PersonHistory personHistory = new PersonHistory(person, "person deactivated because all identities are deactivated", refTimestamp);
			em.persist(personHistory);
		}
		em.flush();
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void deleteDBIdentity(long identityId) throws IllegalOperationException, UnknownObjectException
	{
		Identity identity = getDBIdentityById(identityId);
		logger.debug("deleteDBIdentity: identity=" + identity);

		if (!identity.isDeactivated())
		{
			throw new IllegalOperationException("identity with id " + identityId + " is not deactivated and therefor can't be deleted");
		}

		for (Contact c : identity.getContacts())
		{
			getDBHistoryForContact(c).stream().forEach(e -> em.remove(e));
		}

		getDBPossibleMatchHistoryByIdentity(identity).stream().forEach(e -> em.remove(e));
		getDBPossibleMatchesByIdentity(identity).stream().forEach(e -> em.remove(e));
		getDBHistoryForIdentity(identity).stream().forEach(e -> em.remove(e));
		em.flush();

		IdentifierDomain mpiDomain = identity.getPerson().getDomain().getMpiDomain();
		for (Identifier identifier : new ArrayList<>(identity.getIdentifiers()))
		{
			if (identifier.getIdentifierDomain().equals(mpiDomain))
			{
				// mpi einer anderen person; durch zusammenlegung entstanden
				// die mpiId gehoert immer noch zu der "verliererperson" und kann hier daher nicht geloescht werden
				identity.getIdentifiers().remove(identifier);
				// removing a person is not cascaded with deleting the person's first MPI anymore because the MPI might (and should)
				// be left in 'identity_identifier' (by reassigning) even after removing the person so check manually if it can be removed
				if (!hasDBPersonForMPI(identity.getPerson().getDomain(), identifier, true))
				{
					em.remove(identifier);
				}
			}
		}
		em.remove(identity);
		Person person = identity.getPerson();
		person.getIdentities().remove(identity);

		if (person.getIdentities().isEmpty())
		{
			deactivatePersonObject(person);
			PersonHistory personHistory = new PersonHistory(person, "person deactivated because last identity was deleted", new Timestamp(System.currentTimeMillis()));
			em.persist(personHistory);
		}
		em.flush();
	}

	// ***********************************
	// contacts
	// ***********************************
	protected Contact getDBContactById(long id) throws UnknownObjectException
	{
		logger.debug("getDBContactById for " + id);
		Contact result = em.find(Contact.class, id);
		if (result == null)
		{
			String message = "unknown contact " + id;
			logger.info(message);
			throw new UnknownObjectException(message, UnknownObjectType.CONTACT, Long.toString(id));
		}
		else
		{
			logger.debug("contact found");
			return result;
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Identity saveContact(long identityId, ContactInDTO contactDTO, Timestamp timestamp) throws UnknownObjectException
	{
		Contact contact = new Contact(contactDTO, null, timestamp);
		Identity identity = getDBIdentityById(identityId);
		identity.addContacts(Collections.singletonList(contact));
		em.flush();
		return identity;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Identity saveContacts(long identityId, List<Contact> contacts) throws UnknownObjectException
	{
		Identity identity = getDBIdentityById(identityId);
		identity.addContacts(contacts);
		em.flush();
		return identity;
	}

	// ***********************************
	// possible matches
	// ***********************************
	protected List<IdentityLink> getDBPossibleMatchesByPerson(Person person)
	{
		logger.debug("getDBPossibleMatchesByPerson for person with id " + person.getId());
		@SuppressWarnings("unchecked")
		List<IdentityLink> result = em.createNamedQuery("IdentityLink.findByPerson").setParameter("person", person)
				.getResultList();
		logger.debug("found " + result.size() + " possible matches for person with id " + person.getId());
		return result;
	}

	protected List<IdentityLink> getDBPossibleMatchesByIdentites(Identity identity1, Identity identity2)
	{
		logger.debug("getDBPossibleMatchesByIdentites for identities with id " + identity1.getId() + " and " + identity2.getId());
		@SuppressWarnings("unchecked")
		List<IdentityLink> result = em.createNamedQuery("IdentityLink.findByIdentities").setParameter("identity1", identity1)
				.setParameter("identity2", identity2).getResultList();
		logger.debug("found " + result.size() + " possible matches for identities with id " + identity1.getId() + " and " + identity2.getId());
		return result;
	}

	protected List<IdentityLink> getDBPossibleMatchesByIdentity(Identity identity)
	{
		logger.debug("getDBPossibleMatchesByIdentity for identity with id " + identity.getId());
		@SuppressWarnings("unchecked")
		List<IdentityLink> result = em.createNamedQuery("IdentityLink.findByIdentity").setParameter("identity", identity).getResultList();
		logger.debug("found " + result.size() + " possible matches for identity with id " + identity.getId());
		return result;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Person saveIdentityAndPossibleMatches(Domain domain, Identity identity, IdentityPreprocessed identityPP,
			PreprocessedCacheObject identityPPCO, IdentityHistoryEvent historyEvent, List<MatchResult> possibleMatches, String comment,
			double matchingScore, Timestamp timestamp) throws MPIException, UnknownObjectException
	{
		logger.debug("savePossibleMatches");
		Person person = saveIdentity(domain, identity, identityPP, identityPPCO, historyEvent, comment, matchingScore, timestamp);
		for (MatchResult mr : possibleMatches)
		{
			Identity possibleMatchIdentity;
			try
			{
				possibleMatchIdentity = getDBIdentityById(mr.getComparativeValue().getIdentityId());
			}
			catch (UnknownObjectException e)
			{
				String message = "unexpected error: can't find identity with id " + mr.getComparativeValue().getIdentityId();
				logger.error(message, e);
				throw new MPIException(MPIErrorCode.INTERNAL_ERROR, message, e);
			}
			if (!person.equals(possibleMatchIdentity.getPerson()))
			{
				IdentityLink link = new IdentityLink(identity, possibleMatchIdentity, mr.getMatchStrategy(), mr.getRatio(), timestamp);
				em.persist(link);
			}
		}
		em.flush();
		logger.debug("possible matches added");
		return person;
	}

	protected IdentityLink getDBPossibleMatchById(long possibleMatchId) throws InvalidParameterException
	{
		logger.debug("getDBPossibleMatchById for id " + possibleMatchId);
		IdentityLink result = em.find(IdentityLink.class, possibleMatchId);
		if (result == null)
		{
			String message = "possible match with id " + possibleMatchId + " not found";
			logger.error(message);
			throw new InvalidParameterException("possibleMatchId", message);
		}
		logger.debug("possible match found");
		return result;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void saveRemovePossibleMatch(long possibleMatchId, String comment, Timestamp timestamp) throws InvalidParameterException
	{
		logger.debug("saveRemovePossibleMatch");
		IdentityLink possibleMatch = getDBPossibleMatchById(possibleMatchId);
		IdentityLinkHistory linkHistory = new IdentityLinkHistory(possibleMatch, PossibleMatchSolution.SPLIT, comment, timestamp);
		em.persist(linkHistory);
		em.remove(possibleMatch);
		em.flush();
		logger.debug("possible matches removed");
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void saveAssignIdentity(IdentityLink possibleMatch, Identity winningIdentity, Identity identityToMerge, String comment,
			Timestamp timestamp) throws InvalidParameterException, MPIException
	{
		logger.debug("saveAssignIdentity");
		Person personToMerge = identityToMerge.getPerson();
		Person winningPerson = winningIdentity.getPerson();
		List<IdentityLink> possibleMatchesForPerson = getDBPossibleMatchesByPerson(personToMerge);
		// alle links zwischen identitaeten der beiden personen loeschen
		for (IdentityLink link : possibleMatchesForPerson)
		{
			if (link.getSrcIdentity().getPerson().equals(winningPerson) || link.getDestIdentity().getPerson().equals(winningPerson))
			{
				// history vorm aendern der person bei identity erzeugen!
				if (link.getId() == possibleMatch.getId())
				{
					em.persist(new IdentityLinkHistory(link, PossibleMatchSolution.MERGE, comment, timestamp, identityToMerge));
				}
				else
				{
					em.persist(new IdentityLinkHistory(link, PossibleMatchSolution.INDIRECT_MERGE,
							PossibleMatchHistoryStrings.INDIRECT_MERGE + possibleMatch.getId(), timestamp, identityToMerge));
				}
				em.remove(link);
			}
		}
		personToMerge.getReferenceIdentity().getIdentifiers().add(personToMerge.getFirstMPI());
		String domainName = personToMerge.getDomain().getName();
		PersonHistory personHistory = new PersonHistory(winningPerson, PersonHistoryStrings.WINNING_PERSON_AT_MERGE, timestamp);
		em.persist(personHistory);
		// cache erst nach erfolgreichem db-update aendern!
		LongSet identityIdsForCacheUpdate = new LongOpenHashSet();
		if (!personToMerge.getIdentities().isEmpty())
		{
			// ip-eintraege loeschen, da der em keine relation zwischen identity und ip kennt und dadurch die fk-felder in ip beim update von identity probleme machen
			Set<IdentityPreprocessed> ipsToUpdate = new HashSet<>();
			for (Identity identity : personToMerge.getIdentities())
			{
				try
				{
					IdentityPreprocessed ip = getDBIdentityPreprocessedByIdentityId(identity.getId());
					em.remove(ip);
					ipsToUpdate.add(ip);
				}
				catch (UnknownObjectException impossible)
				{
					String message = "db inconsistence while getting identity preprocessed for identity with id " + identity.getId() + ": "
							+ impossible.getMessage();
					logger.error(message, impossible);
					throw new MPIException(MPIErrorCode.INTERNAL_ERROR, message);
				}
			}
			em.flush();

			// ref-identitaet aktualisieren, damit sie ref bleibt
			Timestamp refTimestamp = new Timestamp(timestamp.getTime() + 1);
			IdentityHistory identityHistory = new IdentityHistory(winningPerson.getReferenceIdentity(), IdentityHistoryEvent.MERGE,
					IdentityHistoryStrings.UPDATED_REF_IDENTITY_AT_MERGE, possibleMatch.getThreshold(), refTimestamp);
			em.persist(identityHistory);
			winningPerson.getReferenceIdentity().setTimestamp(refTimestamp);
			// forced reference setzen, fuer den fall, dass das bei der verliererperson true war, bei der gewinner aber nicht
			winningPerson.getReferenceIdentity().setForcedReference(true);
			winningPerson.setTimestamp(timestamp);
			for (Identity identity : personToMerge.getIdentities())
			{
				identityHistory = new IdentityHistory(identity, IdentityHistoryEvent.MERGE, comment, possibleMatch.getThreshold(), timestamp);
				em.persist(identityHistory);
				identity.setPerson(winningPerson);
				identity.setTimestamp(timestamp);
				winningPerson.getIdentities().add(identity);
				identityIdsForCacheUpdate.add(identity.getId());
			}
			em.flush();

			// zuvor geloeschte ip-eintraege aktualisiert wieder speichern
			for (IdentityPreprocessed ip : ipsToUpdate)
			{
				ip.setDBIds(ip.getIdentityId(), winningPerson.getId(), domainName, timestamp);
				em.persist(ip);
			}
		}
		personHistory = new PersonHistory(personToMerge, PersonHistoryStrings.LOOSING_PERSON_AT_MERGE, timestamp);
		em.persist(personHistory);
		personToMerge.getIdentities().clear();
		personToMerge.setDeactivated(true);
		personToMerge.setTimestamp(timestamp);
		em.flush();
		for (long id : identityIdsForCacheUpdate)
		{
			try
			{
				DOMAIN_CACHE.updateIdentityWithDBIDs(domainName, id, winningPerson.getId(), timestamp);
			}
			catch (MPIException | UnknownObjectException impossible)
			{
				String message = "db/cache inconsistence while updating identity preprocessed within cache for identity with id " + id + ": "
						+ impossible.getMessage();
				logger.error(message, impossible);
				throw new MPIException(MPIErrorCode.INTERNAL_ERROR, message);
			}
		}
		logger.debug("identities assigned");
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void saveMoveIdentitiesForIdentifierToPerson(String domainName, IdentifierDTO identifierDTO, String mpiId, String comment,
			Timestamp timestamp) throws MPIException, UnknownObjectException
	{
		logger.debug("saveMoveIdentitiesForIdentifierToPerson");
		Domain domain = getDBDomain(domainName);
		IdentifierId mpiIdentifierId = new IdentifierId(domain.getMpiDomain().getName(), mpiId);
		Identifier mpiIdentifier = getDBIdentifierById(mpiIdentifierId);
		Person person = getDBPersonByFirstMPI(domain, mpiIdentifier);
		IdentifierDomain identifierDomain = getDBIdentifierDomain(identifierDTO.getIdentifierDomain().getName());
		List<Identity> identitiesToMove = getDBIdentitiesByIdentifierForDomain(domain, identifierDTO.getValue(), identifierDomain);
		// cache erst nach erfolgreichem db-update aendern!
		LongSet identityIdsForCacheUpdate = new LongOpenHashSet();
		if (!identitiesToMove.isEmpty())
		{
			// koennen nur zu einer person gehoeren
			Person spendingPerson = identitiesToMove.get(0).getPerson();
			// test, ob durch das verschieben identifier zu 2 personen gehoeren wuerden
			Set<Identifier> identifierToMove = new HashSet<>();
			Set<Identifier> identifierToStay = new HashSet<>();
			for (Identity identity : identitiesToMove)
			{
				identifierToMove.addAll(identity.getIdentifiers());
			}
			for (Identity identity : spendingPerson.getIdentities())
			{
				if (!identitiesToMove.contains(identity))
				{
					identifierToStay.addAll(identity.getIdentifiers());
				}
			}
			// check for integrity
			for (Identifier identifier : identifierToMove)
			{
				if (identifierToStay.contains(identifier))
				{
					String message = "can't move identities for " + identifierDTO + " because otherwise a identifier (" + identifier
							+ ") will be assigned to two persons (mpiIds: " + person.getFirstMPI().getValue() + " and "
							+ spendingPerson.getFirstMPI().getValue();
					Set<String> relatedMpiIds = new HashSet<>();
					relatedMpiIds.add(person.getFirstMPI().getValue());
					relatedMpiIds.add(spendingPerson.getFirstMPI().getValue());
					logger.error(message);
					throw new MPIException(MPIErrorCode.RESTRICTIONS_VIOLATED, message, relatedMpiIds);
				}
			}
			List<IdentityLink> possibleMatchesForPerson = getDBPossibleMatchesByPerson(spendingPerson);
			// alle links mit den verschobenen identitaeten zwischen den beiden personen loeschen
			for (IdentityLink link : possibleMatchesForPerson)
			{
				if (link.getSrcIdentity().getPerson().equals(person) && identitiesToMove.contains(link.getDestIdentity())
						|| link.getDestIdentity().getPerson().equals(person) && identitiesToMove.contains(link.getSrcIdentity()))
				{
					em.persist(new IdentityLinkHistory(link, PossibleMatchSolution.MERGE_BY_MOVE, comment, timestamp, person.getReferenceIdentity()));
					em.remove(link);
				}
			}
			// ip-eintraege loeschen, da der em keine relation zwischen identity und ip kennt und dadurch die fk-felder in ip beim update von identity probleme machen
			Set<IdentityPreprocessed> ipsToUpdate = new HashSet<>();
			for (Identity identity : identitiesToMove)
			{
				try
				{
					IdentityPreprocessed ip = getDBIdentityPreprocessedByIdentityId(identity.getId());
					em.remove(ip);
					ipsToUpdate.add(ip);
				}
				catch (UnknownObjectException impossible)
				{
					String message = "db inconsistence while getting identity preprocessed for identity with id " + identity.getId() + ": "
							+ impossible.getMessage();
					logger.error(message, impossible);
					throw new MPIException(MPIErrorCode.INTERNAL_ERROR, message);
				}
			}
			em.flush();

			// ref-identitaet aktualisieren, damit sie ref bleibt
			Timestamp refTimestamp = new Timestamp(timestamp.getTime() + 1);
			IdentityHistory identityHistory = new IdentityHistory(person.getReferenceIdentity(), IdentityHistoryEvent.MERGE,
					IdentityHistoryStrings.UPDATED_REF_IDENTITY_AT_MOVE, 0.0, refTimestamp);
			em.persist(identityHistory);
			person.getReferenceIdentity().setTimestamp(refTimestamp);
			// forced reference setzen, fuer den fall, dass das bei der verliererperson true war, bei der gewinner aber nicht
			person.getReferenceIdentity().setForcedReference(true);
			for (Identity identity : identitiesToMove)
			{
				em.persist(new IdentityHistory(identity, IdentityHistoryEvent.MOVE, comment, 0.0, timestamp));
				identity.setPerson(person);
				identity.setTimestamp(timestamp);
				person.getIdentities().add(identity);
				spendingPerson.getIdentities().remove(identity);
				identityIdsForCacheUpdate.add(identity.getId());
			}
			em.flush();

			// zuvor geloeschte ip-eintraege aktualisiert wieder speichern
			for (IdentityPreprocessed ip : ipsToUpdate)
			{
				ip.setDBIds(ip.getIdentityId(), person.getId(), domainName, timestamp);
				em.persist(ip);
			}
			PersonHistory personHistory = new PersonHistory(spendingPerson, "spending person at move identities", timestamp);
			em.persist(personHistory);
			spendingPerson.setTimestamp(timestamp);
			if (spendingPerson.getIdentities().isEmpty())
			{
				spendingPerson.setDeactivated(true);
			}
			personHistory = new PersonHistory(person, "receiving person at move identities", timestamp);
			em.persist(personHistory);
			person.setTimestamp(timestamp);
			em.flush();
			for (long id : identityIdsForCacheUpdate)
			{
				try
				{
					DOMAIN_CACHE.updateIdentityWithDBIDs(domainName, id, person.getId(), timestamp);
				}
				catch (MPIException | UnknownObjectException impossible)
				{
					String message = "db/cache inconsistence while updating identity preprocessed within cache for identity with id " + id + ": "
							+ impossible.getMessage();
					logger.error(message, impossible);
					throw new MPIException(MPIErrorCode.INTERNAL_ERROR, message);
				}
			}
			logger.debug("identities moved");
		}
		else
		{
			logger.info("no identities found for " + mpiIdentifier);
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public IdentityLink saveExternalPossibleMatch(String domainName, Identity identity1, Identity identity2, Timestamp timestamp)
			throws DuplicateEntryException, InvalidParameterException, MPIException
	{
		IdentityLink link;
		if (identity1.getPerson().getId() == identity2.getPerson().getId())
		{
			String message = "both given identities belong to the same person";
			logger.error(message);
			throw new InvalidParameterException("ids", message);
		}
		List<IdentityLink> existingLinks = getDBPossibleMatchesByIdentites(identity1, identity2);
		if (!existingLinks.isEmpty())
		{
			String message = "possible match already exists with id: " + existingLinks.get(0).getId();
			logger.error(message);
			throw new DuplicateEntryException(message);
		}
		else
		{
			try
			{
				PreprocessedCacheObject idPP1 = DOMAIN_CACHE.getPreprocessedCacheObjectByIdentityId(domainName, identity1.getId());
				PreprocessedCacheObject idPP2 = DOMAIN_CACHE.getPreprocessedCacheObjectByIdentityId(domainName, identity2.getId());
				MatchResult mr = DOMAIN_CACHE.directMatch(domainName, idPP1, idPP2);
				link = new IdentityLink(identity1, identity2, mr.getMatchStrategy(), mr.getRatio(), timestamp);
			}
			catch (UnknownObjectException impossible)
			{
				String message = "db inconsistence while getting identity preprocessed for identity with id " + identity1.getId() + " or id "
						+ identity2.getId() + ": " + impossible.getMessage();
				logger.error(message, impossible);
				throw new MPIException(MPIErrorCode.INTERNAL_ERROR, message);
			}
			em.persist(link);
			em.flush();
			logger.debug("external possible match added");
		}
		return link;
	}

	// ***********************************
	// histories
	// ***********************************
	protected List<PersonHistory> getDBHistoryForPerson(Person person)
	{
		@SuppressWarnings("unchecked")
		List<PersonHistory> hpList = em.createNamedQuery("PersonHistory.findByPerson").setParameter("person", person).getResultList();
		return hpList;
	}

	protected List<IdentityHistory> getDBHistoryForIdentity(Identity identity)
	{
		@SuppressWarnings("unchecked")
		List<IdentityHistory> histories = em.createNamedQuery("IdentityHistory.findByIdentity")
				.setParameter("identity", identity).getResultList();
		logger.debug("found " + histories.size() + " history entries for identity " + identity);
		return histories;
	}

	protected List<IdentityHistory> getDBHistoryForIdentityByPerson(Person person)
	{
		@SuppressWarnings("unchecked")
		List<IdentityHistory> histories = em.createNamedQuery("IdentityHistory.findByPerson")
				.setParameter("person", person).getResultList();
		logger.debug("found " + histories.size() + " history entries for identity by person " + person);
		return histories;
	}

	protected List<ContactHistory> getDBHistoryForContact(Contact contact)
	{
		@SuppressWarnings("unchecked")
		List<ContactHistory> histories = em.createNamedQuery("ContactHistory.findByContact")
				.setParameter("contact", contact).getResultList();
		return histories;
	}

	protected List<IdentityLinkHistory> getDBPossibleMatchHistoryForPerson(Person person)
	{
		@SuppressWarnings("unchecked")
		List<IdentityLinkHistory> histories = em.createNamedQuery("IdentityLinkHistory.findByPerson")
				.setParameter("person", person).getResultList();
		return histories;
	}

	protected List<IdentityLinkHistory> getDBPossibleMatchHistoryForUpdatedIdentity(Identity identity)
	{
		@SuppressWarnings("unchecked")
		List<IdentityLinkHistory> histories = em.createNamedQuery("IdentityLinkHistory.findByUpdatedIdentity")
				.setParameter("updatedIdentity", identity).getResultList();
		logger.debug("found " + histories.size() + " possible match history entries for updatedIdentity " + identity);
		return histories;
	}

	protected List<IdentityLinkHistory> getDBPossibleMatchHistoryByIdentity(Identity identity)
	{
		@SuppressWarnings("unchecked")
		List<IdentityLinkHistory> result = em.createNamedQuery("IdentityLinkHistory.findByIdentity").setParameter("identity", identity).getResultList();
		logger.debug("found " + result.size() + " possible match history for identity with id " + identity.getId());
		return result;
	}

	// ***********************************
	// class CounterMapKey
	// ***********************************
	private final class CounterMapKey
	{
		private final String mpiDomain;
		private final String prefix;

		public CounterMapKey(String mpiDomain, String prefix)
		{
			super();
			this.mpiDomain = mpiDomain;
			this.prefix = prefix;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + (mpiDomain == null ? 0 : mpiDomain.hashCode());
			result = prime * result + (prefix == null ? 0 : prefix.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
			{
				return true;
			}
			if (obj == null)
			{
				return false;
			}
			if (getClass() != obj.getClass())
			{
				return false;
			}
			CounterMapKey other = (CounterMapKey) obj;
			if (!getOuterType().equals(other.getOuterType()))
			{
				return false;
			}
			if (mpiDomain == null)
			{
				if (other.mpiDomain != null)
				{
					return false;
				}
			}
			else if (!mpiDomain.equals(other.mpiDomain))
			{
				return false;
			}
			if (prefix == null)
			{
				if (other.prefix != null)
				{
					return false;
				}
			}
			else if (!prefix.equals(other.prefix))
			{
				return false;
			}
			return true;
		}

		@Override
		public String toString()
		{
			return "CounterMapKey [mpiDomain=" + mpiDomain + ", prefix=" + prefix + "]";
		}

		private DAO getOuterType()
		{
			return DAO.this;
		}
	}
}
