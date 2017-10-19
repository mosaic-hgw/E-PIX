package org.emau.icmvc.ganimed.deduplication.impl;

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
import java.util.List;

import org.emau.icmvc.ganimed.deduplication.DeduplicationStrategy;
import org.emau.icmvc.ganimed.deduplication.StringMatchingAlgorithm;
import org.emau.icmvc.ganimed.deduplication.config.model.ObjectField;
import org.emau.icmvc.ganimed.deduplication.config.model.ObjectOfSingleTruth;
import org.emau.icmvc.ganimed.deduplication.config.model.ObjectType;
import org.emau.icmvc.ganimed.deduplication.config.model.SimpleType;
import org.emau.icmvc.ganimed.deduplication.config.model.ThresholdConfiguration;
import org.emau.icmvc.ganimed.deduplication.model.MatchCriteriaResult;
import org.emau.icmvc.ganimed.deduplication.model.MatchResult;
import org.emau.icmvc.ganimed.deduplication.model.MatchResult.DECISION;
import org.emau.icmvc.ganimed.epix.common.utils.ReflectionUtil;
import org.emau.icmvc.ganimed.exception.DeduplicationException;

/**
 * 
 * @author Christian Schack
 * since 2011
 *
 */
public class FellegiSunterAlgorithm<T> extends DeduplicationStrategy<T> {


	@Override
	public MatchResult<T> match(T toMatch, T candidate) throws DeduplicationException {
		//There is always a decision if a candidate exists or an internal exception occurs		
		MatchResult<T> mr = createMatchingResult(candidate, 0.0, DECISION.NOMATCH, FellegiSunterAlgorithm.class.getName());
		
		ObjectOfSingleTruth oost = matchingConfiguration.getObjectOfSingleTruth();
		
		matchComplexType(mr, toMatch, candidate, oost.getObjectType());

		return decide(mr, candidate);		
	}	
	
	protected MatchResult<T> decide(MatchResult<T> mr, T candidate) {
				
		double threshold = matchingConfiguration.getObjectOfSingleTruth().getGlobalThreshold().getThreshold();
		double rejectThreshold = matchingConfiguration.getObjectOfSingleTruth().getGlobalThreshold().getRejectThreshold();
		
		double matchProbability = calculateProbability(mr.getMatches(), false);
		double noMatchProbalilty = calculateProbability(mr.getNoMatches(), true);
		double ratio = 1000.0; 
			
		// Denominator must be > 0
		if (noMatchProbalilty>0.0) {			
			ratio = matchProbability / noMatchProbalilty;
		}
		
		mr.setRatio(ratio);
		if (ratio <= threshold) {
			mr.setDecision(DECISION.NOMATCH);							
		} else if (	ratio > threshold && ratio <= rejectThreshold) {
			mr.setDecision(DECISION.CRITICAL);			
		} else {
			mr.setDecision(DECISION.MATCH);			
		}	
		return mr;
	}

	@Override
	protected MatchResult<T> decide(MatchResult<T> mr, double probability, String toMatchValue, String candidateValue, ObjectField objectField) throws DeduplicationException {
		
		SimpleType simpleType = objectField.getSimpleType();
		ThresholdConfiguration thresConfig = simpleType.getThresholdConfig();
		
		String algorithm = simpleType.getAlgorithm();
		
		MatchCriteriaResult mcr = new MatchCriteriaResult();
		mcr.setCriteria(objectField.getName());
		mcr.setProbability(probability);
		mcr.setAlgorithm(algorithm);
		mcr.setCandidateValue(candidateValue);
		mcr.setToMatchValue(toMatchValue);
		mcr.setWeight(thresConfig.getWeight());
			
		double threshold = simpleType.getThresholdConfig().getThreshold();
		if (probability>=threshold) {
			mr.getMatches().add(mcr);
		} else {
			mr.getNoMatches().add(mcr);
		}
		return mr;
	}
	
	/**
	 * 
	 * @param probalities
	 * @return
	 */
	private double calculateProbability(List<MatchCriteriaResult> probalities, boolean inverse) {		
		double probality = 0.0;
		for (MatchCriteriaResult mcr : probalities) {
			probality += mcr.getWeight() * (inverse ? 1-mcr.getProbability() : mcr.getProbability());
		}		
		return probality;				
	}	
	
	private <V, W> void matchComplexType(MatchResult<T> mr, V toMatch, V candidate, ObjectType type) throws DeduplicationException {
					
		List<ObjectField> fields = type.getObjectField();
		
		for (ObjectField objectField : fields) {
																
			if (objectField.getSimpleType() != null ) {					
				//Only consider comparable values
				matchSimpleType(mr, toMatch, candidate, objectField);				
			}			
			
			if (objectField.getComplexType() != null ) {
				//FIXME this is wrong toMatch must be the object belonging to fieldName of the given field 
				matchComplexType(mr, toMatch, candidate, objectField.getComplexType());
			}
			
			if (objectField.getListType() != null) {	
				matchListType(mr, toMatch, candidate, objectField.getListType(), objectField.getName());
			}
		}				
	}
	
	/**
	 * 
	 * @param <V>
	 * @param <W>
	 * @param toMatch
	 * @param candidate
	 * @param listType
	 * @param fieldName
	 * @return
	 */
	@SuppressWarnings("unchecked")	
	private <V, W> void matchListType(MatchResult<T> mr, V toMatch, V candidate, ObjectType listType, String fieldName) throws DeduplicationException  {
		List<MatchResult<T>> mrList = new ArrayList<MatchResult<T>>();
		try {
			List<W> toMatchList = (List<W>)ReflectionUtil.getProperty(toMatch, fieldName);
			List<W> candidateList = (List<W>)ReflectionUtil.getProperty(candidate, fieldName);		
			for (W toMatchItem : toMatchList) {			
				for (W candidateItem : candidateList) {
					MatchResult<T> temporalMr = new MatchResult<T>();
					matchComplexType(temporalMr, toMatchItem, candidateItem, listType);
					mrList.add(temporalMr);
				}
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			throw new DeduplicationException(e.getLocalizedMessage(), e);
		}
		
		MatchResult<T> bestMr = null;
		for(MatchResult<T> listMr : mrList) {
			if(bestMr == null) {
				decide(listMr, (T)candidate);
				bestMr = listMr;
			} else {
				decide(listMr, (T)candidate);
				if(bestMr.getRatio() < listMr.getRatio()) {
					bestMr = listMr;
				}
			}	
		}
		
		mr.getMatches().addAll(bestMr.getMatches());
		mr.getNoMatches().addAll(bestMr.getNoMatches());
		
	}

	private <V> void matchSimpleType(MatchResult<T> mr, V toMatch, V candidate, ObjectField field) throws DeduplicationException{
		SimpleType type = field.getSimpleType();
		StringMatchingAlgorithm sma = getStringMatchingAlgorithm(type.getAlgorithm());						
			
		String toMatchValue = getFieldValue(toMatch, type, field.getName());
		String candidateValue = getFieldValue(candidate, type, field.getName());
		
		double probability = 0.5;
		
		if (toMatchValue==null || toMatchValue.length()==0 && 
			candidateValue==null || ("").equals(candidateValue)) {
			return;
		} 
		
		if (toMatchValue!=null && toMatchValue.length()>0 && 
			candidateValue!=null && candidateValue.length()>0) {
			probability = sma.match(toMatchValue, candidateValue);
		} 
		
		decide(mr, probability, toMatchValue, candidateValue, field);			
							
	}
	
}
