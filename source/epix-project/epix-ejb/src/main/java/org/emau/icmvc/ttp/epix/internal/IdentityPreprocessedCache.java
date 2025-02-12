package org.emau.icmvc.ttp.epix.internal;

/*-
 * ###license-information-start###
 * E-PIX - Enterprise Patient Identifier Cross-referencing
 * __
 * Copyright (C) 2009 - 2025 Trusted Third Party of the University Medicine Greifswald
 * 							kontakt-ths@uni-greifswald.de
 * 
 * 							concept and implementation
 * 							l.geidel,c.schack, d.langner, g.koetzschke
 * 
 * 							web client
 * 							a.blumentritt, f.m. moser
 * 
 * 							docker
 * 							r.schuldt, f.m. moser
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
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.emau.icmvc.ttp.deduplication.FellegiSunterAlgorithm;
import org.emau.icmvc.ttp.deduplication.config.model.Field;
import org.emau.icmvc.ttp.deduplication.config.model.MatchingConfiguration;
import org.emau.icmvc.ttp.deduplication.model.DeduplicationResult;
import org.emau.icmvc.ttp.deduplication.model.MatchResult;
import org.emau.icmvc.ttp.epix.common.exception.InvalidParameterException;
import org.emau.icmvc.ttp.epix.common.exception.MPIErrorCode;
import org.emau.icmvc.ttp.epix.common.exception.MPIException;
import org.emau.icmvc.ttp.epix.common.model.IdentityInDTO;
import org.emau.icmvc.ttp.epix.common.model.enums.FieldName;
import org.emau.icmvc.ttp.epix.pdqquery.model.SearchMask;
import org.emau.icmvc.ttp.epix.persistence.model.IdentityPreprocessed;

/**
 *
 * @author Robert Wolff IdentityPreprocessed table cache object
 * @author geidell
 */
public class IdentityPreprocessedCache
{
	private static final Logger logger = LogManager.getLogger(IdentityPreprocessedCache.class);
	private static final String EMPTY_STRING = "";
	private static final String SPACE_STRING = " ";
	private static final String ZERO2_FORMAT_STRING = "%02d";
	private static final String ZERO2_STRING = "00";
	private static final String ZERO4_FORMAT_STRING = "%04d";
	private static final String ZERO4_STRING = "0000";
	private final FellegiSunterAlgorithm deduplication;
	private final Long2ObjectMap<PreprocessedCacheObject> matchingCache = new Long2ObjectOpenHashMap<>(10000);
	private final Long2ObjectMap<IdentityPreprocessed> ipCache = new Long2ObjectOpenHashMap<>(10000);
	private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	private final Field[] fieldsForBlocking;
	private final Field[] fieldsForMatching;
	private final boolean multiValueFields;
	private final int parallelMatchAfter;
	private final int numberOfThreads;
	private final ExecutorService threadPool;
	private final boolean lowMemory;

	public IdentityPreprocessedCache(MatchingConfiguration config) throws InvalidParameterException
	{
		parallelMatchAfter = config.getMatching().getParallelMatchingAfter();
		numberOfThreads = config.getMatching().getNumberOfThreads();
		threadPool = Executors.newFixedThreadPool(numberOfThreads);
		logger.info("parallelization: {} threads after {} entries", numberOfThreads, parallelMatchAfter);
		lowMemory = config.isLowMemory();
		if (lowMemory)
		{
			logger.info("option for low memory profile is set - search is restricted to matching fields");
		}
		int blockingCount = 0;
		int matchingCount = 0;
		boolean multiValueTemp = false;
		for (Field field : config.getMatching().getFields())
		{
			if (field.getBlockingThreshold() > 0.)
			{
				blockingCount++;
			}
			if (field.getMatchingThreshold() > 0.)
			{
				matchingCount++;
			}
			else
			{
				String message = "exception while parsing matching configuration - matching threshold needs to be > 0";
				logger.error(message);
				throw new InvalidParameterException("domain.config", message);
			}
		}
		fieldsForBlocking = new Field[blockingCount];
		fieldsForMatching = new Field[matchingCount];
		int blockingIndex = 0;
		int matchingIndex = 0;
		for (Field field : config.getMatching().getFields())
		{
			if (field.isBlockingField())
			{
				// konkrete reihenfolge ist egal. hauptsache, sie bleibt innerhalb einer domain gleich
				fieldsForBlocking[blockingIndex++] = field;
			}
			// konkrete reihenfolge ist egal. hauptsache, sie bleibt innerhalb einer domain gleich
			fieldsForMatching[matchingIndex++] = field;
			if (field.getMultipleValues() != null)
			{
				multiValueTemp = true;
			}
		}
		deduplication = new FellegiSunterAlgorithm(fieldsForMatching, config.getMatching().getThresholdPossibleMatch(),
				config.getMatching().getThresholdAutomaticMatch());
		multiValueFields = multiValueTemp;
		if (multiValueFields)
		{
			logger.info("multi value fields configured");
		}
		logger.info("configured {} blocking and {} matching fields", fieldsForBlocking.length, fieldsForMatching.length);
	}

	public void destroy()
	{
		threadPool.shutdownNow();
	}

	public void addIdentites(List<IdentityPreprocessed> identityPreprocessedList) throws MPIException
	{
		rwl.writeLock().lock();
		try
		{
			for (IdentityPreprocessed identityPreprocessed : identityPreprocessedList)
			{
				matchingCache.put(identityPreprocessed.getIdentityId(),
						new PreprocessedCacheObject(identityPreprocessed, fieldsForBlocking, fieldsForMatching, multiValueFields));
			}
			logger.debug("added {} entries to matching cache", matchingCache.size());
			if (!lowMemory)
			{
				for (IdentityPreprocessed identityPreprocessed : identityPreprocessedList)
				{
					ipCache.put(identityPreprocessed.getIdentityId(), identityPreprocessed);
				}
				logger.debug("added {} entries to IdentityPreprocessed cache", ipCache.size());
			}
		}
		finally
		{
			rwl.writeLock().unlock();
		}
	}

	/**
	 * add identity to cache
	 *
	 * @param identityPreprocessed
	 * @throws MPIException
	 */
	public void addIdentity(IdentityPreprocessed identityPP, PreprocessedCacheObject identityPPCO) throws MPIException
	{
		rwl.writeLock().lock();
		try
		{
			matchingCache.put(identityPP.getIdentityId(), identityPPCO);
			if (!lowMemory)
			{
				ipCache.put(identityPP.getIdentityId(), identityPP);
			}
		}
		finally
		{
			rwl.writeLock().unlock();
		}
	}

	/**
	 * update identity within cache
	 *
	 * @param identityPreprocessed
	 * @throws InternalException
	 */
	public void updateIdentity(long identityId, IdentityPreprocessed newIdentityPP, PreprocessedCacheObject newIdentityPPCO) throws MPIException
	{
		if (!lowMemory)
		{
			rwl.writeLock().lock();
			try
			{
				IdentityPreprocessed ipFromCache = getIdentityPreprocessedByIdentityId(identityId);
				ipFromCache.update(newIdentityPP);
			}
			finally
			{
				rwl.writeLock().unlock();
			}
		}
	}

	/**
	 * Removes the identity with given id from the cache. This is not reversible.
	 *
	 * @param identityId
	 */
	public void removeIdentity(long identityId)
	{
		rwl.writeLock().lock();
		try
		{
			matchingCache.remove(identityId);
			if (!lowMemory)
			{
				ipCache.remove(identityId);
			}
		}
		finally
		{
			rwl.writeLock().unlock();
		}
	}

	/**
	 * update identity (db id fields) within cache
	 *
	 * @param identityPreprocessed
	 * @throws InternalException
	 */
	public void updateIdentityWithDBIds(long identityId, long personId, String domainName, Timestamp timestamp) throws MPIException
	{
		rwl.writeLock().lock();
		try
		{
			PreprocessedCacheObject identityPPCO = matchingCache.get(identityId);
			identityPPCO.setDBIds(personId, identityId);
			if (!lowMemory)
			{
				IdentityPreprocessed ipFromCache = getIdentityPreprocessedByIdentityId(identityId);
				ipFromCache.setDBIds(identityId, personId, domainName, timestamp);
			}
		}
		finally
		{
			rwl.writeLock().unlock();
		}
	}

	/**
	 * match identity via cache
	 *
	 */
	public DeduplicationResult getMatches(PreprocessedCacheObject entity) throws MPIException
	{
		rwl.readLock().lock();
		try
		{
			return getMatches(entity, matchingCache.values());
		}
		finally
		{
			rwl.readLock().unlock();
		}
	}

	public DeduplicationResult getMatches(PreprocessedCacheObject entity, Collection<PreprocessedCacheObject> candidates) throws MPIException
	{
		DeduplicationResult result = new DeduplicationResult(entity);
		if (candidates.size() < parallelMatchAfter || numberOfThreads == 1)
		{
			LevenshteinCache lsCache = new LevenshteinCache(entity.getMatchFieldValues());
			for (PreprocessedCacheObject candidate : candidates)
			{
				if (!block(entity, candidate))
				{
					MatchResult mr = deduplication.match(entity, candidate, lsCache);
					result.addMatchResult(mr);
				}
			}
		}
		else
		{
			Collection<Callable<List<MatchResult>>> tasks = new ArrayList<>();
			for (int i = 0; i < numberOfThreads; i++)
			{
				tasks.add(new MatchTask(i, entity, candidates, new LevenshteinCache(entity.getMatchFieldValues())));
			}
			try
			{
				List<Future<List<MatchResult>>> allMatchResults = threadPool.invokeAll(tasks);
				for (Future<List<MatchResult>> singleThreadMatchResult : allMatchResults)
				{
					List<MatchResult> matchResultList = singleThreadMatchResult.get();
					for (MatchResult matchResult : matchResultList)
					{
						result.addMatchResult(matchResult);
					}
				}
			}
			catch (InterruptedException | ExecutionException e)
			{
				logger.error("error while trying to check for match", e);
				Thread.currentThread().interrupt();
				throw new MPIException(MPIErrorCode.INTERNAL_ERROR, e.getMessage(), e);
			}
		}
		return result;
	}

	private boolean block(PreprocessedCacheObject entity, PreprocessedCacheObject candidate)
	{
		if (!entity.hasMultiValuesForBlocking() && !candidate.hasMultiValuesForBlocking())
		{
			for (int i = 0; i < fieldsForBlocking.length; i++)
			{
				int index = i * 2;
				if (blockSingleValue(entity, candidate, index, index, i, fieldsForBlocking[i].getBlockingThreshold()))
				{
					return true;
				}
			}
		}
		else if (entity.hasMultiValuesForBlocking() && candidate.hasMultiValuesForBlocking())
		{
			int currentEntityIndex = 0;
			int currentCandidateIndex = 0;
			for (int i = 0; i < fieldsForBlocking.length; i++)
			{
				boolean fieldBlock = true;
				double blockingThreshold = fieldsForBlocking[i].getBlockingThreshold();
				int startCandidateIndex = currentCandidateIndex;
				int entityCount = entity.getCountForMultiValueForBlocking(i);
				int candidateCount = candidate.getCountForMultiValueForBlocking(i);
				for (; currentEntityIndex <= entityCount; currentEntityIndex++)
				{
					int entityIndex = currentEntityIndex * 2;
					currentCandidateIndex = startCandidateIndex;
					for (; currentCandidateIndex <= candidateCount; currentCandidateIndex++)
					{
						if (!blockSingleValue(entity, candidate, entityIndex, currentCandidateIndex * 2, i, blockingThreshold))
						{
							fieldBlock = false;
							currentCandidateIndex = candidateCount + 1;
							break;
						}
					}
					if (!fieldBlock)
					{
						currentEntityIndex = entityCount + 1;
						break;
					}
				}
				if (fieldBlock)
				{
					return true;
				}
			}
		}
		else if (entity.hasMultiValuesForBlocking())
		{
			int currentEntityIndex = 0;
			for (int i = 0; i < fieldsForBlocking.length; i++)
			{
				boolean fieldBlock = true;
				double blockingThreshold = fieldsForBlocking[i].getBlockingThreshold();
				int candidateIndex = i * 2;
				int entityCount = entity.getCountForMultiValueForBlocking(i);
				for (; currentEntityIndex <= entityCount; currentEntityIndex++)
				{
					if (!blockSingleValue(entity, candidate, currentEntityIndex * 2, candidateIndex, i, blockingThreshold))
					{
						fieldBlock = false;
						currentEntityIndex = entityCount + 1;
						break;
					}
				}
				if (fieldBlock)
				{
					return true;
				}
			}
		}
		else
		{
			int currentCandidateIndex = 0;
			for (int i = 0; i < fieldsForBlocking.length; i++)
			{
				boolean fieldBlock = true;
				double blockingThreshold = fieldsForBlocking[i].getBlockingThreshold();
				int entityIndex = i * 2;
				int candidateCount = candidate.getCountForMultiValueForBlocking(i);
				for (; currentCandidateIndex <= candidateCount; currentCandidateIndex++)
				{
					if (!blockSingleValue(entity, candidate, entityIndex, currentCandidateIndex * 2, i, blockingThreshold))
					{
						fieldBlock = false;
						currentCandidateIndex = candidateCount + 1;
						break;
					}
				}
				if (fieldBlock)
				{
					return true;
				}
			}
		}
		return false;
	}

	private boolean blockSingleValue(PreprocessedCacheObject entity, PreprocessedCacheObject candidate, int entityIndex, int candidateIndex,
			int blockingFieldNumber, double threshold)
	{
		if (threshold == 1.)
		{
			// direkter stringvergleich ueber die zugehoerigen matchingfelder
			int entityMatchingIndex = entityIndex / 2 + entity.getIndexAddForBlockingToMatching(blockingFieldNumber);
			int candidateMatchingIndex = candidateIndex / 2 + candidate.getIndexAddForBlockingToMatching(blockingFieldNumber);
			// stringvergleich mit "!=" zulaessig und sinnvoll, da die felder mit String.intern() gesetzt wurden
			return entity.getMatchFieldValue(entityMatchingIndex) != candidate.getMatchFieldValue(candidateMatchingIndex);
		}
		else
		{
			int ei2 = entityIndex + 1;
			int ci2 = candidateIndex + 1;
			int length1 = (int) (entity.getBlockFieldValue(ei2) >> 48);
			int length2 = (int) (candidate.getBlockFieldValue(ci2) >> 48);
			// anzahl unterschiedliche zeichen = count_bits(zeichen_kuerzerer_string xor (zeichen_kuerzerer_string and zeichen_anderer_string)) + |length1 - length2|, siehe def von blockFieldValues
			// bsp: 0101100 und 0101011 -> anzahl = count_bits(0101100 xor 0101000) + |3 - 4| = 1 + 1
			// das unterschaetzt moeglicherweise die wirkliche anzahl unterschiedlicher zeichen (und ziemlich wahrscheinlich die anzahl noetiger levenshtein-operationen)
			// wegen a) begrenzung auf max. 4 gleiche zeichen und b) das "sammelzeichen" im 2. long
			// dadurch werden aber nur nicht passende kandidaten durchgelassen - es werden nicht faelschlicherweise passende geblockt
			double count = 0; // muss wegen der div am ende double sein
			if (length1 > length2)
			{
				if (length2 < threshold * length1)
				{
					// der laengenunterschied ist schon zu groß
					return true;
				}
				long bitVector = candidate.getBlockFieldValue(candidateIndex) ^ entity.getBlockFieldValue(entityIndex) & candidate.getBlockFieldValue(candidateIndex);
				// anzahl "1" zaehlen
				while (bitVector != 0)
				{
					count += bitVector & 1;
					bitVector >>>= 1;
				}
				// zweiter long - enthaelt nur 10 4-stellige zeichenbits + 1 8-stellig (alle anderen)
				// dafuer aber noch die laenge; deswegen nur die letzten 48 bits nehmen
				bitVector = (candidate.getBlockFieldValue(ci2) ^ entity.getBlockFieldValue(ei2) & candidate.getBlockFieldValue(ci2)) & 281474976710655l; // die letzten 48 bits
				while (bitVector != 0)
				{
					count += bitVector & 1;
					bitVector >>>= 1;
				}
				// 1 - (count + length1 - length2) / length1 < threshold
				if (length2 - count < threshold * length1)
				{
					return true;
				}
			}
			else
			{
				if (length1 < threshold * length2)
				{
					// der laengenunterschied ist schon zu groß
					return true;
				}
				long bitVector = entity.getBlockFieldValue(entityIndex) ^ entity.getBlockFieldValue(entityIndex) & candidate.getBlockFieldValue(candidateIndex);
				// anzahl "1" zaehlen
				while (bitVector != 0)
				{
					count += bitVector & 1;
					bitVector >>>= 1;
				}
				// zweiter long - enthaelt nur 10 4-stellige zeichenbits + 1 8-stellig (alle anderen)
				// dafuer aber noch die laenge; deswegen nur die letzten 48 bits nehmen
				bitVector = (entity.getBlockFieldValue(ei2) ^ entity.getBlockFieldValue(ei2) & candidate.getBlockFieldValue(ci2)) & 281474976710655l; // die letzten 48 bits
				while (bitVector != 0)
				{
					count += bitVector & 1;
					bitVector >>>= 1;
				}
				// 1 - (count + length2 - length1) / length2 < threshold
				if (length1 - count < threshold * length2)
				{
					return true;
				}
			}
		}
		return false;
	}

	public PreprocessedCacheObject getPerfectMatch(PreprocessedCacheObject matchable) throws MPIException
	{
		rwl.readLock().lock();
		try
		{
			return getPerfectMatch(matchable, matchingCache.values());
		}
		finally
		{
			rwl.readLock().unlock();
		}
	}

	public PreprocessedCacheObject getPerfectMatch(PreprocessedCacheObject matchable, Collection<PreprocessedCacheObject> candidates)
	{
		PreprocessedCacheObject result = null;
		for (PreprocessedCacheObject candidate : candidates)
		{
			if (matchable.getMatchHashCode() == candidate.getMatchHashCode())
			{
				boolean found = true;
				for (int i = 0; i < fieldsForMatching.length; i++)
				{
					// wegen multivalue hier den jeweils letzten wert zum feld nehmen (enthaelt alle werte)
					// stringvergleich mit "!=" zulaessig und sinnvoll, da die felder mit String.intern() gesetzt wurden
					if (candidate.getMatchFieldValue(candidate.getCountForMultiValueForMatching(i)) != matchable.getMatchFieldValue(matchable.getCountForMultiValueForMatching(i)))
					{
						found = false;
						break;
					}
				}
				if (found)
				{
					result = candidate;
					break;
				}
			}
		}
		return result;
	}

	public MatchResult getDirectMatchResult(PreprocessedCacheObject matchable, PreprocessedCacheObject candidate) throws MPIException
	{
		LevenshteinCache lsCache = new LevenshteinCache(matchable.getMatchFieldValues());
		return deduplication.match(matchable, candidate, lsCache);
	}

	public PreprocessedCacheObject getPreprocessedCacheObjectByIdentityId(long id) throws MPIException
	{
		PreprocessedCacheObject result;
		rwl.readLock().lock();
		try
		{
			result = matchingCache.get(id);
		}
		finally
		{
			rwl.readLock().unlock();
		}
		if (result == null)
		{
			String message = "didn't found identity within matching cache, id: " + id;
			logger.error(message);
			throw new MPIException(MPIErrorCode.INTERNAL_ERROR, message);
		}
		return result;
	}

	private IdentityPreprocessed getIdentityPreprocessedByIdentityId(long id) throws MPIException
	{
		IdentityPreprocessed result;
		rwl.readLock().lock();
		try
		{
			result = ipCache.get(id);
		}
		finally
		{
			rwl.readLock().unlock();
		}
		if (result == null)
		{
			String message = "didn't found identity within cache, id: " + id;
			logger.error(message);
			throw new MPIException(MPIErrorCode.INTERNAL_ERROR, message);
		}
		return result;
	}

	public PreprocessedCacheObject createPreprocessedCacheObjectFromIdentityPP(IdentityPreprocessed identityPP) throws MPIException
	{
		return new PreprocessedCacheObject(identityPP, fieldsForBlocking, fieldsForMatching, multiValueFields);
	}

	public LongList findPersonIdsByPDQ(SearchMask mask, IdentityPreprocessed maskIP, LongList personIds, boolean limitSearch)
	{
		if (logger.isDebugEnabled())
		{
			StringBuilder sb = new StringBuilder("findPersonIdsByPDQ with ");
			sb.append(maskIP);
			if (!personIds.isEmpty())
			{
				sb.append(" and person ids ");
				for (long personId : personIds)
				{
					sb.append(personId);
					sb.append(SPACE_STRING);
				}
			}
			else
			{
				sb.append(" and no person ids");
			}
			logger.debug(sb.toString());
		}
		LongList result;
		if (mask.getMaxResults() < 1)
		{
			result = new LongArrayList();
		}
		else if (mask.isAnd())
		{
			result = findPersonIdsByPDQWithAnd(mask, maskIP, personIds, limitSearch);
		}
		else
		{
			result = findPersonIdsByPDQWithOr(mask, maskIP, personIds, limitSearch);
		}
		logger.debug("found {} persons", result.size());
		return result;
	}

	// unschoen, aber wird bei umstellung auf eav eh anders
	private LongList findPersonIdsByPDQWithAnd(SearchMask mask, IdentityPreprocessed maskIP, LongList personIds, boolean limitSearch)
	{
		LongList result = new LongArrayList();
		// hack, damit der not-empty-code nicht auch noch in IdentityPreprocessed dupliziert werden muss
		if (personIds.isEmpty())
		{
			// wenn personIds leer und suchmaske hat ids, dann abbruch - die funktion wuerde sonst wie suche ohne ids funktionieren
			if (!mask.getIdentity().getIdentifiers().isEmpty())
			{
				return result;
			}
			// wenn personIds leer und suchmaske ohne ids leer, dann abbruch - die funktion wuerde sonst alle personen zurueckliefern
			IdentityInDTO dummyIdentity = mask.getIdentity();
			dummyIdentity.setIdentifiers(new ArrayList<>());
			SearchMask dummyMask = new SearchMask(mask.getDomainName(), dummyIdentity, mask.isAnd(), mask.getYearOfBirth(), mask.getMonthOfBirth(),
					mask.getDayOfBirth(), mask.getMaxResults());
			if (!dummyMask.hasSearchValues())
			{
				return result;
			}
			if (limitSearch)
			{
				// ueberpruefen, ob wenigstens ein matchfeld eine sucheingabe enthaelt; gender nicht generell mitnehmen, da == ' '
				boolean searchfieldValuesForLimitedSearch = Character.MIN_VALUE != maskIP.getGender() && ' ' != maskIP.getGender();
				for (Field element : fieldsForMatching)
				{
					FieldName fieldName = element.getName();
					try
					{
						if (!fieldName.equals(FieldName.gender))
						{
							searchfieldValuesForLimitedSearch |= !maskIP.getFieldValue(fieldName).isEmpty();
						}
					}
					catch (MPIException e)
					{
						logger.error("{} for domain {}", e.getLocalizedMessage(), maskIP.getDomainName(), e);
					}
				}
				if (!searchfieldValuesForLimitedSearch)
				{
					return result;
				}
			}
		}
		String maskDay = String.format(ZERO2_FORMAT_STRING, mask.getDayOfBirth());
		String maskMonth = String.format(ZERO2_FORMAT_STRING, mask.getMonthOfBirth());
		String maskYear = String.format(ZERO4_FORMAT_STRING, mask.getYearOfBirth());
		boolean useDay = false;
		boolean useMonth = false;
		boolean useYear = false;
		if (!ZERO2_STRING.equals(maskDay))
		{
			useDay = true;
		}
		if (!ZERO2_STRING.equals(maskMonth))
		{
			useMonth = true;
		}
		if (!ZERO4_STRING.equals(maskYear))
		{
			useYear = true;
		}
		Object[] cacheArray;
		rwl.readLock().lock();
		try
		{
			if (limitSearch)
			{
				cacheArray = matchingCache.values().toArray();
			}
			else
			{
				cacheArray = ipCache.values().toArray();
			}
		}
		finally
		{
			rwl.readLock().unlock();
		}
		for (Object element : cacheArray)
		{
			String birthDate = EMPTY_STRING;
			if (limitSearch)
			{
				PreprocessedCacheObject candidatePPCO = (PreprocessedCacheObject) element;
				if (!personIds.isEmpty() && !personIds.contains(candidatePPCO.getPersonId()))
				{
					continue;
				}
				else if (result.contains(candidatePPCO.getPersonId()))
				{
					continue;
				}
				if (!checkMatchWithPPCOAnd(candidatePPCO, maskIP))
				{
					continue;
				}
				for (int i = 0; i < fieldsForMatching.length; i++)
				{
					if (fieldsForMatching[i].getName() == FieldName.birthDate)
					{
						birthDate = candidatePPCO.getMatchFieldValue(candidatePPCO.getCountForMultiValueForMatching(i));
						break;
					}
				}
			}
			else
			{
				IdentityPreprocessed cachedIPP = (IdentityPreprocessed) element;
				if (!personIds.isEmpty() && !personIds.contains(cachedIPP.getPersonId()))
				{
					continue;
				}
				else if (result.contains(cachedIPP.getPersonId()))
				{
					continue;
				}

				if (!checkMatchWithIPPAnd(cachedIPP, maskIP))
				{
					continue;
				}
				birthDate = cachedIPP.getBirthDate();
			}
			if (birthDate.length() == 8)
			{
				if (useDay)
				{
					String day = birthDate.substring(6, 8);
					if (!day.equals(maskDay))
					{
						continue;
					}
				}
				if (useMonth)
				{
					String month = birthDate.substring(4, 6);
					if (!month.equals(maskMonth))
					{
						continue;
					}
				}
				if (useYear)
				{
					String year = birthDate.substring(0, 4);
					if (!year.equals(maskYear))
					{
						continue;
					}
				}
			}
			else
			{
				continue;
			}
			if (limitSearch)
			{
				result.add(((PreprocessedCacheObject) element).getPersonId());
			}
			else
			{
				result.add(((IdentityPreprocessed) element).getPersonId());
			}
			if (result.size() == mask.getMaxResults())
			{
				break;
			}
		}
		return result;

	}

	// unschoen, aber wird bei umstellung auf eav eh anders
	private LongList findPersonIdsByPDQWithOr(SearchMask mask, IdentityPreprocessed maskIP, LongList personIds, boolean limitSearch)
	{
		LongList result = new LongArrayList();
		String maskDay = String.format(ZERO2_FORMAT_STRING, mask.getDayOfBirth());
		String maskMonth = String.format(ZERO2_FORMAT_STRING, mask.getMonthOfBirth());
		String maskYear = String.format(ZERO4_FORMAT_STRING, mask.getYearOfBirth());
		boolean useDay = false;
		boolean useMonth = false;
		boolean useYear = false;
		if (!ZERO2_STRING.equals(maskDay))
		{
			useDay = true;
		}
		if (!ZERO2_STRING.equals(maskMonth))
		{
			useMonth = true;
		}
		if (!ZERO4_STRING.equals(maskYear))
		{
			useYear = true;
		}
		Object[] cacheArray;
		rwl.readLock().lock();
		try
		{
			if (limitSearch)
			{
				cacheArray = matchingCache.values().toArray();
			}
			else
			{
				cacheArray = ipCache.values().toArray();
			}
		}
		finally
		{
			rwl.readLock().unlock();
		}
		for (Object element : cacheArray)
		{
			if (result.size() == mask.getMaxResults())
			{
				break;
			}
			String birthDate = EMPTY_STRING;
			if (limitSearch)
			{
				PreprocessedCacheObject candidatePPCO = (PreprocessedCacheObject) element;
				if (personIds.contains(candidatePPCO.getPersonId()))
				{
					if (!result.contains(candidatePPCO.getPersonId()))
					{
						result.add(candidatePPCO.getPersonId());
					}
					continue;
				}
				else if (result.contains(candidatePPCO.getPersonId()))
				{
					continue;
				}
				if (checkMatchWithPPCOOr(candidatePPCO, maskIP))
				{
					result.add(candidatePPCO.getPersonId());
					continue;
				}
				for (int i = 0; i < fieldsForMatching.length; i++)
				{
					if (fieldsForMatching[i].getName() == FieldName.birthDate)
					{
						birthDate = candidatePPCO.getMatchFieldValue(candidatePPCO.getCountForMultiValueForMatching(i));
						break;
					}
				}
			}
			else
			{
				IdentityPreprocessed cachedIPP = (IdentityPreprocessed) element;
				if (personIds.contains(cachedIPP.getPersonId()))
				{
					if (!result.contains(cachedIPP.getPersonId()))
					{
						result.add(cachedIPP.getPersonId());
					}
					continue;
				}
				else if (result.contains(cachedIPP.getPersonId()))
				{
					continue;
				}
				if (checkMatchWithIPPOr(cachedIPP, maskIP))
				{
					result.add(cachedIPP.getPersonId());
					continue;
				}
				birthDate = cachedIPP.getBirthDate();
			}

			if (birthDate.length() == 8)
			{
				boolean found = false;
				if (useDay)
				{
					String day = birthDate.substring(6, 8);
					if (day.equals(maskDay))
					{
						found = true;
					}
				}
				else if (useMonth)
				{
					String month = birthDate.substring(4, 6);
					if (month.equals(maskMonth))
					{
						found = true;
					}
				}
				else if (useYear)
				{
					String year = birthDate.substring(0, 4);
					if (year.equals(maskYear))
					{
						found = true;
					}
				}
				if (found)
				{
					if (limitSearch)
					{
						result.add(((PreprocessedCacheObject) element).getPersonId());
					}
					else
					{
						result.add(((IdentityPreprocessed) element).getPersonId());
					}
				}
			}
		}
		return result;
	}

	private boolean checkMatchWithPPCOAnd(PreprocessedCacheObject candidatePPCO, IdentityPreprocessed maskIP)
	{
		for (int i = 0; i < fieldsForMatching.length; i++)
		{
			try
			{
				FieldName fieldName = fieldsForMatching[i].getName();
				if (FieldName.gender.equals(fieldName))
				{
					if (Character.MIN_VALUE != maskIP.getGender() && ' ' != maskIP.getGender()
							&& !candidatePPCO.getMatchFieldValue(candidatePPCO.getCountForMultiValueForMatching(i)).equals(String.valueOf(maskIP.getGender())))
					{
						return false;
					}
				}
				else
				{
					if (!candidatePPCO.getMatchFieldValue(candidatePPCO.getCountForMultiValueForMatching(i)).contains(maskIP.getFieldValue(fieldName)))
					{
						return false;
					}
				}
			}
			catch (MPIException e)
			{
				logger.error(e.getLocalizedMessage() + " for domain " + maskIP.getDomainName(), e);
			}
		}
		return true;
	}

	private boolean checkMatchWithIPPAnd(IdentityPreprocessed cachedIPP, IdentityPreprocessed maskIP)
	{
		// contains fuer empty string liefert true, daher kein test auf empty
		if (!cachedIPP.getFirstName().contains(maskIP.getFirstName()))
		{
			return false;
		}
		else if (!cachedIPP.getMiddleName().contains(maskIP.getMiddleName()))
		{
			return false;
		}
		else if (!cachedIPP.getLastName().contains(maskIP.getLastName()))
		{
			return false;
		}
		else if (!cachedIPP.getPrefix().contains(maskIP.getPrefix()))
		{
			return false;
		}
		else if (!cachedIPP.getSuffix().contains(maskIP.getSuffix()))
		{
			return false;
		}
		else if (Character.MIN_VALUE != maskIP.getGender() && ' ' != maskIP.getGender()
				&& cachedIPP.getGender() != maskIP.getGender())
		{
			return false;
		}
		else if (!cachedIPP.getBirthPlace().contains(maskIP.getBirthPlace()))
		{
			return false;
		}
		else if (!cachedIPP.getRace().contains(maskIP.getRace()))
		{
			return false;
		}
		else if (!cachedIPP.getReligion().contains(maskIP.getReligion()))
		{
			return false;
		}
		else if (!cachedIPP.getMothersMaidenName().contains(maskIP.getMothersMaidenName()))
		{
			return false;
		}
		else if (!cachedIPP.getDegree().contains(maskIP.getDegree()))
		{
			return false;
		}
		else if (!cachedIPP.getMotherTongue().contains(maskIP.getMotherTongue()))
		{
			return false;
		}
		else if (!cachedIPP.getNationality().contains(maskIP.getNationality()))
		{
			return false;
		}
		else if (!cachedIPP.getCivilStatus().contains(maskIP.getCivilStatus()))
		{
			return false;
		}
		else if (!cachedIPP.getValue1().contains(maskIP.getValue1()))
		{
			return false;
		}
		else if (!cachedIPP.getValue2().contains(maskIP.getValue2()))
		{
			return false;
		}
		else if (!cachedIPP.getValue3().contains(maskIP.getValue3()))
		{
			return false;
		}
		else if (!cachedIPP.getValue4().contains(maskIP.getValue4()))
		{
			return false;
		}
		else if (!cachedIPP.getValue5().contains(maskIP.getValue5()))
		{
			return false;
		}
		else if (!cachedIPP.getValue6().contains(maskIP.getValue6()))
		{
			return false;
		}
		else if (!cachedIPP.getValue7().contains(maskIP.getValue7()))
		{
			return false;
		}
		else if (!cachedIPP.getValue8().contains(maskIP.getValue8()))
		{
			return false;
		}
		else if (!cachedIPP.getValue9().contains(maskIP.getValue9()))
		{
			return false;
		}
		else if (!cachedIPP.getValue10().contains(maskIP.getValue10()))
		{
			return false;
		}
		else if (!cachedIPP.getBirthDate().contains(maskIP.getBirthDate()))
		{
			return false;
		}
		return true;
	}

	private boolean checkMatchWithPPCOOr(PreprocessedCacheObject candidatePPCO, IdentityPreprocessed maskIP)
	{
		for (int i = 0; i < fieldsForMatching.length; i++)
		{
			try
			{
				FieldName fieldName = fieldsForMatching[i].getName();
				if (FieldName.gender.equals(fieldName))
				{
					if (Character.MIN_VALUE != maskIP.getGender() && ' ' != maskIP.getGender()
							&& candidatePPCO.getMatchFieldValue(candidatePPCO.getCountForMultiValueForMatching(i)).equals(String.valueOf(maskIP.getGender())))
					{
						return true;
					}
				}
				else
				{
					String value = maskIP.getFieldValue(fieldName);
					if (!value.isEmpty() && candidatePPCO.getMatchFieldValue(candidatePPCO.getCountForMultiValueForMatching(i)).contains(value))
					{
						return true;
					}
				}
			}
			catch (MPIException e)
			{
				logger.error( "{} for domain {}", e.getLocalizedMessage(), maskIP.getDomainName(), e);
			}
		}
		return false;

	}

	private boolean checkMatchWithIPPOr(IdentityPreprocessed cachedIPP, IdentityPreprocessed maskIP)
	{
		if (!maskIP.getFirstName().isEmpty()
				&& cachedIPP.getFirstName().contains(maskIP.getFirstName()))
		{
			return true;
		}
		else if (!maskIP.getMiddleName().isEmpty()
				&& cachedIPP.getMiddleName().contains(maskIP.getMiddleName()))
		{
			return true;
		}
		else if (!maskIP.getLastName().isEmpty()
				&& cachedIPP.getLastName().contains(maskIP.getLastName()))
		{
			return true;
		}
		else if (!maskIP.getPrefix().isEmpty()
				&& cachedIPP.getPrefix().contains(maskIP.getPrefix()))
		{
			return true;
		}
		else if (!maskIP.getSuffix().isEmpty()
				&& cachedIPP.getSuffix().contains(maskIP.getSuffix()))
		{
			return true;
		}
		else if (Character.MIN_VALUE != maskIP.getGender()
				&& cachedIPP.getGender() == maskIP.getGender())
		{
			return true;
		}
		else if (!maskIP.getBirthPlace().isEmpty()
				&& cachedIPP.getBirthPlace().contains(maskIP.getBirthPlace()))
		{
			return true;
		}
		else if (!maskIP.getRace().isEmpty()
				&& cachedIPP.getRace().contains(maskIP.getRace()))
		{
			return true;
		}
		else if (!maskIP.getReligion().isEmpty()
				&& cachedIPP.getReligion().contains(maskIP.getReligion()))
		{
			return true;
		}
		else if (!maskIP.getMothersMaidenName().isEmpty()
				&& cachedIPP.getMothersMaidenName().contains(maskIP.getMothersMaidenName()))
		{
			return true;
		}
		else if (!maskIP.getDegree().isEmpty()
				&& cachedIPP.getDegree().contains(maskIP.getDegree()))
		{
			return true;
		}
		else if (!maskIP.getMotherTongue().isEmpty()
				&& cachedIPP.getMotherTongue().contains(maskIP.getMotherTongue()))
		{
			return true;
		}
		else if (!maskIP.getNationality().isEmpty()
				&& cachedIPP.getNationality().contains(maskIP.getNationality()))
		{
			return true;
		}
		else if (!maskIP.getCivilStatus().isEmpty()
				&& cachedIPP.getCivilStatus().contains(maskIP.getCivilStatus()))
		{
			return true;
		}
		else if (!maskIP.getValue1().isEmpty()
				&& cachedIPP.getValue1().contains(maskIP.getValue1()))
		{
			return true;
		}
		else if (!maskIP.getValue2().isEmpty()
				&& cachedIPP.getValue2().contains(maskIP.getValue2()))
		{
			return true;
		}
		else if (!maskIP.getValue3().isEmpty()
				&& cachedIPP.getValue3().contains(maskIP.getValue3()))
		{
			return true;
		}
		else if (!maskIP.getValue4().isEmpty()
				&& cachedIPP.getValue4().contains(maskIP.getValue4()))
		{
			return true;
		}
		else if (!maskIP.getValue5().isEmpty()
				&& cachedIPP.getValue5().contains(maskIP.getValue5()))
		{
			return true;
		}
		else if (!maskIP.getValue6().isEmpty()
				&& cachedIPP.getValue6().contains(maskIP.getValue6()))
		{
			return true;
		}
		else if (!maskIP.getValue7().isEmpty()
				&& cachedIPP.getValue7().contains(maskIP.getValue7()))
		{
			return true;
		}
		else if (!maskIP.getValue8().isEmpty()
				&& cachedIPP.getValue8().contains(maskIP.getValue8()))
		{
			return true;
		}
		else if (!maskIP.getValue9().isEmpty()
				&& cachedIPP.getValue9().contains(maskIP.getValue9()))
		{
			return true;
		}
		else if (!maskIP.getValue10().isEmpty()
				&& cachedIPP.getValue10().contains(maskIP.getValue10()))
		{
			return true;
		}
		else if (!maskIP.getBirthDate().isEmpty()
				&& cachedIPP.getBirthDate().contains(maskIP.getBirthDate()))
		{
			return true;
		}
		return false;
	}

	// fuer die parallelisierung
	private final class MatchTask implements Callable<List<MatchResult>>
	{
		private final int start;
		private final PreprocessedCacheObject entity;
		private final Collection<PreprocessedCacheObject> matchingCache;
		private final LevenshteinCache lsCache;

		public MatchTask(int start, PreprocessedCacheObject entity, Collection<PreprocessedCacheObject> matchingCache, LevenshteinCache lsCache)
		{
			super();
			this.start = start;
			this.entity = entity;
			this.matchingCache = matchingCache;
			this.lsCache = lsCache;
		}

		@Override
		public List<MatchResult> call() throws Exception
		{
			List<MatchResult> result = new ArrayList<>();
			int i = start;
			for (PreprocessedCacheObject candidate : matchingCache)
			{
				if (++i < numberOfThreads)
				{
					continue;
				}
				else
				{
					i = 0;
					if (!block(entity, candidate))
					{
						MatchResult mr = deduplication.match(entity, candidate, lsCache);
						result.add(mr);
					}
				}
			}
			return result;
		}
	}
}
