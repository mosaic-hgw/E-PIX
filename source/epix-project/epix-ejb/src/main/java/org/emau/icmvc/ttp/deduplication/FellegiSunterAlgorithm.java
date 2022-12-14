package org.emau.icmvc.ttp.deduplication;

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

import java.util.HashMap;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.emau.icmvc.ttp.deduplication.config.model.Field;
import org.emau.icmvc.ttp.deduplication.impl.LevenshteinAlgorithm;
import org.emau.icmvc.ttp.deduplication.model.MatchResult;
import org.emau.icmvc.ttp.deduplication.model.MatchResult.DECISION;
import org.emau.icmvc.ttp.epix.common.deduplication.IStringMatchingAlgorithm;
import org.emau.icmvc.ttp.epix.common.exception.MPIErrorCode;
import org.emau.icmvc.ttp.epix.common.exception.MPIException;
import org.emau.icmvc.ttp.epix.internal.LevenshteinCache;
import org.emau.icmvc.ttp.epix.internal.PreprocessedCacheObject;
import org.emau.icmvc.ttp.utils.ReflectionUtil;

import com.google.gwt.dev.util.editdistance.GeneralEditDistance;

/**
 *
 * @author Christian Schack, geidell since 2011
 *
 */
public class FellegiSunterAlgorithm
{
	public static final double MATCHING_SCORE_FOR_PERFECT_MATCH = 1000.0;
	private static final Logger logger = LogManager.getLogger(FellegiSunterAlgorithm.class);
	private final HashMap<String, IStringMatchingAlgorithm> stringMatchingAlgorithms = new HashMap<>();
	private final Field[] fieldsForMaching;
	private final double thresholdPossibleMatch;
	private final double thresholdAutomaticMatch;

	public FellegiSunterAlgorithm(Field[] fieldsForMaching, double thresholdPossibleMatch, double thresholdAutomaticMatch)
	{
		super();
		this.fieldsForMaching = fieldsForMaching;
		this.thresholdPossibleMatch = thresholdPossibleMatch;
		this.thresholdAutomaticMatch = thresholdAutomaticMatch;
	}

	public MatchResult match(PreprocessedCacheObject entity, PreprocessedCacheObject candidate, LevenshteinCache lsCache) throws MPIException
	{
		// There is always a decision if a candidate exists or an internal exception occurs
		MatchResult mr = new MatchResult(FellegiSunterAlgorithm.class.getName(), candidate, fieldsForMaching.length);

		if (!entity.hasMultiValuesForMatching() && !candidate.hasMultiValuesForMatching())
		{
			for (int fieldIndex = 0; fieldIndex < fieldsForMaching.length; fieldIndex++)
			{
				double probability = matchSimpleType(mr, entity.getMatchFieldValue(fieldIndex), candidate.getMatchFieldValue(fieldIndex), fieldIndex,
						lsCache.getCache()[fieldIndex]);
				decideField(mr, probability, fieldIndex);
			}
		}
		else if (entity.hasMultiValuesForMatching() && candidate.hasMultiValuesForMatching())
		{
			int currentEntityIndex = 0;
			int currentCandidateIndex = 0;
			for (int fieldIndex = 0; fieldIndex < fieldsForMaching.length; fieldIndex++)
			{
				int startCandidateIndex = currentCandidateIndex;
				double probability = 0;
				int entityCount = entity.getCountForMultiValueForMatching(fieldIndex);
				int candidateCount = candidate.getCountForMultiValueForMatching(fieldIndex);
				for (; currentEntityIndex <= entityCount; currentEntityIndex++)
				{
					currentCandidateIndex = startCandidateIndex;
					for (; currentCandidateIndex <= candidateCount; currentCandidateIndex++)
					{
						double temp = matchSimpleType(mr, entity.getMatchFieldValue(currentEntityIndex),
								candidate.getMatchFieldValue(currentCandidateIndex), fieldIndex, lsCache.getCache()[currentEntityIndex]);
						if (fieldsForMaching[fieldIndex].getMultipleValues() != null)
						{
							if (currentEntityIndex < entity.getIndexForIncompleteMultiValueForMatching(fieldIndex)
									&& currentCandidateIndex < candidate.getIndexForIncompleteMultiValueForMatching(fieldIndex))
							{
								temp -= fieldsForMaching[fieldIndex].getMultipleValues().getPenaltyBothShort();
							}
							else if (currentEntityIndex < entity.getIndexForIncompleteMultiValueForMatching(fieldIndex)
									|| currentCandidateIndex < candidate.getIndexForIncompleteMultiValueForMatching(fieldIndex))
							{
								temp -= fieldsForMaching[fieldIndex].getMultipleValues().getPenaltyOneShort();
							}
							else if (currentEntityIndex < entityCount || currentCandidateIndex < candidateCount)
							{
								// in der letzten zeile stehen alle values in der eingegebenen reihenfolge, bei allen anderen zeilen penalty abziehen
								temp -= fieldsForMaching[fieldIndex].getMultipleValues().getPenaltyNotAPerfectMatch();
							}
						}
						if (temp > probability)
						{
							probability = temp;
						}
					}
				}
				decideField(mr, probability, fieldIndex);
			}
		}
		else if (entity.hasMultiValuesForMatching())
		{
			int currentEntityIndex = 0;
			for (int fieldIndex = 0; fieldIndex < fieldsForMaching.length; fieldIndex++)
			{
				double probability = 0;
				int entityCount = entity.getCountForMultiValueForMatching(fieldIndex);
				for (; currentEntityIndex <= entityCount; currentEntityIndex++)
				{
					double temp = matchSimpleType(mr, entity.getMatchFieldValue(currentEntityIndex), candidate.getMatchFieldValue(fieldIndex),
							fieldIndex, lsCache.getCache()[currentEntityIndex]);
					if (fieldsForMaching[fieldIndex].getMultipleValues() != null)
					{
						if (currentEntityIndex < entity.getIndexForIncompleteMultiValueForMatching(fieldIndex))
						{
							temp -= fieldsForMaching[fieldIndex].getMultipleValues().getPenaltyOneShort();
						}
						else if (currentEntityIndex < entityCount)
						{
							// in der letzten zeile stehen alle values in der eingegebenen reihenfolge, bei allen anderen zeilen penalty abziehen
							temp -= fieldsForMaching[fieldIndex].getMultipleValues().getPenaltyNotAPerfectMatch();
						}
					}
					if (temp > probability)
					{
						probability = temp;
					}
				}
				decideField(mr, probability, fieldIndex);
			}
		}
		else
		{
			int currentCandidateIndex = 0;
			for (int fieldIndex = 0; fieldIndex < fieldsForMaching.length; fieldIndex++)
			{
				double probability = 0;
				int candidateCount = candidate.getCountForMultiValueForMatching(fieldIndex);
				for (; currentCandidateIndex <= candidateCount; currentCandidateIndex++)
				{
					double temp = matchSimpleType(mr, entity.getMatchFieldValue(fieldIndex), candidate.getMatchFieldValue(currentCandidateIndex),
							fieldIndex, lsCache.getCache()[fieldIndex]);
					if (fieldsForMaching[fieldIndex].getMultipleValues() != null)
					{
						if (currentCandidateIndex < candidate.getIndexForIncompleteMultiValueForMatching(fieldIndex))
						{
							temp -= fieldsForMaching[fieldIndex].getMultipleValues().getPenaltyOneShort();
						}
						else if (currentCandidateIndex < candidateCount)
						{
							// in der letzten zeile stehen alle values in der eingegebenen reihenfolge, bei allen anderen zeilen penalty abziehen
							temp -= fieldsForMaching[fieldIndex].getMultipleValues().getPenaltyNotAPerfectMatch();
						}
					}
					if (temp > probability)
					{
						probability = temp;
					}
				}
				decideField(mr, probability, fieldIndex);
			}
		}
		return

		decide(mr);
	}

	private double matchSimpleType(MatchResult mr, String entityValue, String candidateValue, int fieldIndex, GeneralEditDistance ged)
			throws MPIException
	{
		IStringMatchingAlgorithm sma = getStringMatchingAlgorithm(fieldsForMaching[fieldIndex].getAlgorithm());
		if (sma instanceof LevenshteinAlgorithm)
		{
			return ((LevenshteinAlgorithm) sma).matchCached(entityValue, candidateValue, ged);
		}
		else
		{
			return sma.match(entityValue, candidateValue);
		}
	}

	private IStringMatchingAlgorithm getStringMatchingAlgorithm(String algorithmType) throws MPIException
	{
		try
		{
			IStringMatchingAlgorithm algorithm = stringMatchingAlgorithms.get(algorithmType);
			if (algorithm == null)
			{
				algorithm = (IStringMatchingAlgorithm) ReflectionUtil.newInstance(algorithmType);
				stringMatchingAlgorithms.put(algorithmType, algorithm);
			}
			return algorithm;
		}
		catch (Exception e)
		{
			logger.error(e.getLocalizedMessage(), e);
			throw new MPIException(MPIErrorCode.INTERNAL_ERROR, e.getLocalizedMessage(), e);
		}
	}

	private void decideField(MatchResult mr, double probability, int fieldIndex)
	{
		if (probability >= fieldsForMaching[fieldIndex].getMatchingThreshold())
		{
			mr.setMatchProbabilities(fieldIndex, probability);
		}
		else
		{
			mr.setNoMatchProbabilities(fieldIndex, probability);
		}
	}

	private MatchResult decide(MatchResult mr)
	{
		double matchProbability = 0;
		double noMatchProbability = 0;
		if (fieldsForMaching.length == 1)
		{
			matchProbability = fieldsForMaching[0].getWeight() * mr.getMatchProbabilities(0);
			noMatchProbability = 1;
		}
		else
		{
			for (int i = 0; i < fieldsForMaching.length; i++)
			{
				double weight = fieldsForMaching[i].getWeight();
				if (mr.getMatchProbabilities(i) > 0)
				{
					matchProbability += weight * mr.getMatchProbabilities(i);
				}
				else
				{
					noMatchProbability += weight * (1 - mr.getNoMatchProbabilities(i));
				}
			}
		}

		double ratio = MATCHING_SCORE_FOR_PERFECT_MATCH;
		if (noMatchProbability > 0.0)
		{
			ratio = matchProbability / noMatchProbability;
		}
		if (ratio > MATCHING_SCORE_FOR_PERFECT_MATCH - 1 && noMatchProbability > 0)
		{
			ratio = MATCHING_SCORE_FOR_PERFECT_MATCH - 1;
		}
		mr.setRatio(ratio);
		if (ratio < thresholdPossibleMatch)
		{
			mr.setDecision(DECISION.NO_MATCH);
		}
		else if (ratio < thresholdAutomaticMatch)
		{
			mr.setDecision(DECISION.POSSIBLE_MATCH);
		}
		else
		{
			mr.setDecision(DECISION.MATCH);
		}
		return mr;
	}
}
