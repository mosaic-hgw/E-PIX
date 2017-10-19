package org.emau.icmvc.ganimed.deduplication;

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



import java.util.HashMap;

import org.emau.icmvc.ganimed.deduplication.config.model.MatchingConfiguration;
import org.emau.icmvc.ganimed.deduplication.config.model.ObjectField;
import org.emau.icmvc.ganimed.deduplication.model.MatchResult;
import org.emau.icmvc.ganimed.deduplication.model.MatchResult.DECISION;
import org.emau.icmvc.ganimed.epix.common.MPIException;
import org.emau.icmvc.ganimed.exception.DeduplicationException;

/**
 * 
 * @author Christian Schack
 * @since 2011
 *
 */
public abstract class DeduplicationStrategy<T> extends AbstractMatching {
	
	protected MatchingConfiguration matchingConfiguration;
	
	protected HashMap<String, StringMatchingAlgorithm> stringMatchingAlgorithms = new HashMap<String, StringMatchingAlgorithm>();	

	public DeduplicationStrategy() {}

	/**
	 * FIXME change match comment
	 * Compares the to given to string using the Levensthein distance.
	 * 
	 * No match: 		0.0 >= s <= <code>MatchingConfiguration#getThreshold()</code>
	 * Critical match:  threshold >= s <= threshold + rejectRegion
	 * Match          	s > rejectRegion          
	 * 
	 * 
	 * @see MatchingConfiguration
	 * @param toMatch
	 * @param comparator
	 * @return
	 * @throws MPIException
	 */
	public abstract MatchResult<T> match(T toMatch, T candidate) throws DeduplicationException;

	/**
	 * 
	 * @param mr
	 * @param probability
	 * @return
	 * @throws Exception
	 */
	protected abstract MatchResult<T> decide(MatchResult<T> mr, T candidate) throws DeduplicationException;
	
	/**
	 * 
	 * @param mr
	 * @param probability
	 * @param toMatchValue
	 * @param candidateValue
	 * @param objectField
	 * @return
	 * @throws Exception
	 */
	protected abstract MatchResult<T> decide(MatchResult<T> mr, 
											double probability, 
											String toMatchValue, 
											String candidateValue, 
											ObjectField objectField) throws DeduplicationException;
	
	
	protected MatchResult<T> createMatchingResult(T comparativeValue, double ratio, DECISION decision, String strategy) {
		MatchResult<T> mr = new MatchResult<T>();
		mr.setMatchStrategy(strategy);
		mr.setDecision(decision);
		mr.setRatio(ratio);
		mr.setComparativeValue(comparativeValue);
		return mr;
	}
	
	/**
	 * 
	 * @return
	 */
	public MatchingConfiguration getMatchingConfiguration() {
		return matchingConfiguration;
	}

	/**
	 * 
	 * @param matchingConfiguration
	 */
	public void setMatchingConfiguration(MatchingConfiguration matchingConfiguration) {
		this.matchingConfiguration = matchingConfiguration;
	}
	
	/**
	 * 
	 * @param algorithmType
	 * @return
	 * @throws DeduplicationException
	 */
	protected StringMatchingAlgorithm getStringMatchingAlgorithm(String algorithmType) throws DeduplicationException {
		try {
			StringMatchingAlgorithm algorithm = stringMatchingAlgorithms.get(algorithmType);			
			if (algorithm==null) {		
				algorithm = (StringMatchingAlgorithm)Class.forName(algorithmType).newInstance();
				stringMatchingAlgorithms.put(algorithmType, algorithm);			
			}
			return algorithm;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			throw new DeduplicationException(e.getLocalizedMessage(), e);
		}
	}

}
