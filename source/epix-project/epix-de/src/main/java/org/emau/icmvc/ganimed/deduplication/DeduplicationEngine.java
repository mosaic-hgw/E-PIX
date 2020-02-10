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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.emau.icmvc.ganimed.deduplication.config.model.MatchingConfiguration;
import org.emau.icmvc.ganimed.deduplication.model.DeduplicationResult;
import org.emau.icmvc.ganimed.deduplication.model.MatchCriteriaResult;
import org.emau.icmvc.ganimed.deduplication.model.MatchResult;
import org.emau.icmvc.ganimed.epix.common.utils.ReflectionUtil;
import org.emau.icmvc.ganimed.exception.DeduplicationException;

/**
 * 
 * @author Christian Schack
 * @since 2011
 * 
 */
public class DeduplicationEngine<T> {

	private DeduplicationStrategy<T> deduplicationStrategy = null;
	
	private MatchCandidateLoader<T> matchCandidateLoader = null;
	
	private MatchingConfiguration matchingConfiguration;
	
	private PerfectMatchProcessor<T> perfectMatchProzessor = null;

	private ValidateRequiredStrategy<T> validateRequiredStrategy = null;

	private PerfectMatchStrategy<T> perfectMatchStrategy = null;

	private static final Logger logger = Logger.getLogger(DeduplicationEngine.class);

	public DeduplicationEngine() {
	}

	@SuppressWarnings("unchecked")
	public void init(MatchingConfiguration matchingConfiguration) throws Exception {

		if (matchingConfiguration == null) {
			throw new IllegalArgumentException("MatchingConfiguration must not be null");
		}
		this.matchingConfiguration=matchingConfiguration;
		logger.debug("!!!matchingConfiguration.getVersion(): " + matchingConfiguration.getVersion());

		String deduplicationAlgorithm = matchingConfiguration.getDeduplicationAlgorithm();

		deduplicationStrategy = (DeduplicationStrategy<T>) ReflectionUtil.newInstance(deduplicationAlgorithm);

		// Set configuration
		deduplicationStrategy.setMatchingConfiguration(matchingConfiguration);
		validateRequiredStrategy.setMatchingConfiguration(matchingConfiguration);
		perfectMatchStrategy.setMatchingConfiguration(matchingConfiguration);
		perfectMatchStrategy.setPerfectMatchProzessor(perfectMatchProzessor);
		
	}
	
	private List<T> preprocessList(List<T> matchables) throws DeduplicationException {
		List<T> preprocessedList = new ArrayList<T>();
		for (T t : matchables) {
			preprocessedList.add(matchCandidateLoader.preprocess(t));
		}
		return preprocessedList;
	}
 
	public DeduplicationResult<T> match(T m) throws DeduplicationException {
		if (m == null) {
			throw new DeduplicationException(new IllegalArgumentException("Matchable must not be null!"));
		}
		// Preprocessing
		T matchable = matchCandidateLoader.preprocess(m);
		BlockingStrategy<T> blockingStrategy = new BlockingStrategy<T>(matchable, matchingConfiguration);
		return matchCandidateLoader.getMatchesViaCache(matchable, blockingStrategy, deduplicationStrategy);
	}

	/**
	 * Returns a valid match() result without matching
	 * 
	 * @param m
	 * @return
	 * @throws DeduplicationException
	 */
	public DeduplicationResult<T> preprocessWithoutMatch(T m) throws DeduplicationException {
		DeduplicationResult<T> result = new DeduplicationResult<T>();
		if (m == null) {
			throw new DeduplicationException(new IllegalArgumentException("Matchable must not be null!"));
		}
		// Preprocessing
		T matchable = matchCandidateLoader.preprocess(m);
		result.setMatchable(matchable);
		return result;
	}
	
	public T perfectMatch(T m) throws DeduplicationException {
		if (m == null) {
			throw new DeduplicationException(new IllegalArgumentException("Matchable must not be null!"));
		}

		//Preprocessing
		T matchable = matchCandidateLoader.preprocess(m);
		
		//Check for perfect match
		if(perfectMatchStrategy.active) {
			T result = perfectMatchStrategy.perfectMatch(matchable);
			return result;
		}
		return null;
	}
	
	public T perfectMatch(T m, List<T> n) throws DeduplicationException {
		if (m == null) {
			throw new DeduplicationException(new IllegalArgumentException("Matchable must not be null!"));
		}
		if (n == null || n.size()<1) {
			throw new DeduplicationException(new IllegalArgumentException("Candidate must not be null!"));
		}
		T matchable = matchCandidateLoader.preprocess(m);
		List<T> candidates = preprocessList(n);
		if(perfectMatchStrategy.active) {
			return perfectMatchStrategy.perfectMatch(matchable, candidates);
		}
		return null;
	}

	public DeduplicationResult<T> matchSameLocalIdentifierCandidate(T m, T n) throws DeduplicationException {
		DeduplicationResult<T> result = new DeduplicationResult<T>();

		if (m == null || n == null) {
			throw new DeduplicationException(new IllegalArgumentException("Matchable must not be null!"));
		}

		// validate requires fields/attributes
		boolean validateM = isValidate(m);
		boolean validateN = isValidate(n);

		if (validateM && validateN) {

			//Preprocessing
			T matchable = matchCandidateLoader.preprocess(m);
			T tomatch = matchCandidateLoader.preprocess(n);

			result.setMatchable(matchable);
			BlockingStrategy<T> blockingStrategy = new BlockingStrategy<T>(matchable, matchingConfiguration);

			// Load candidates from database (max element number equals pageCount)
			List<T> candidateList = new ArrayList<T>();
			candidateList.add(tomatch);
			if(logger.isDebugEnabled()) {
				logger.debug("Blocking candidate list size: " + candidateList.size());
			}

			// Blocking
			// block the uncertain candidates and remove them from the list 
			List<T> toMatchList = block(blockingStrategy, candidateList);

			if(logger.isDebugEnabled()) {
				logger.debug("Match candidate list size: " + toMatchList.size());
			}

			for (T candidate : toMatchList) {
				MatchResult<T> mr = deduplicationStrategy.match(matchable, candidate);
				result.addMatchResult(mr, mr.getDecision());
			}

		}

		//		print(mr);
		return result;
	}

	public DeduplicationResult<T> matchSameLocalIdentifierCandidates(T m, List<T> n) throws DeduplicationException {

		DeduplicationResult<T> result = new DeduplicationResult<T>();

		if (m == null || n == null) {
			throw new DeduplicationException(new IllegalArgumentException("Matchable must not be null!"));
		}

		// validate requires fields/attributes
		boolean validateM = isValidate(m);
		boolean validateN = true;
		for(T t : n) {
			if(!isValidate(t)) {
				validateN = false;
			}
		}

		if (validateM && validateN) {

			//Preprocessing
			T matchable = matchCandidateLoader.preprocess(m);
			List<T> tomatch = preprocessList(n);

			result.setMatchable(matchable);
			BlockingStrategy<T> blockingStrategy = new BlockingStrategy<T>(matchable, matchingConfiguration);

			// Load candidates from database (max element number equals pageCount)
			List<T> candidateList = new ArrayList<T>();
			candidateList.addAll(tomatch);
			if(logger.isDebugEnabled()) {
				logger.debug("Blocking candidate list size: " + candidateList.size());
			}

			// Blocking
			// block the uncertain candidates and remove them from the list 
			List<T> toMatchList = block(blockingStrategy, candidateList);

			if(logger.isDebugEnabled()) {
				logger.debug("Match candidate list size: " + toMatchList.size());
			}

			for (T candidate : toMatchList) {
				MatchResult<T> mr = deduplicationStrategy.match(matchable, candidate);
				result.addMatchResult(mr, mr.getDecision());
			}

		}

		//		print(mr);
		return result;
	}
	
	public boolean isValidate(T matchable) throws DeduplicationException {
		return validateRequiredStrategy.isValidate(matchable);
	}

	private List<T> block(BlockingStrategy<T> blockingStrategie, List<T> candidateList) throws DeduplicationException {

		List<T> toMatchList = new ArrayList<T>();
		for (T candidate : candidateList) {
			if (!blockingStrategie.block(candidate)) {
				toMatchList.add(candidate);
				if(logger.isDebugEnabled()) {
					logger.debug("Found potential candidate for matching " + candidate);
				}
			}
		}

		return toMatchList;
	}

	@SuppressWarnings("unused")
	private void print(MatchResult<T> mr) {
		logger.info("Matches: ");
		for (MatchCriteriaResult mrc : mr.getMatches()) {
			logger.info("Kriterium: " + mrc.getCriteria() + " Eingabe: " + mrc.getCandidateValue() + " Kandidat: " + mrc.getToMatchValue() + " Wahrscheinlichkeit: "
					+ mrc.getProbability());
		}

		logger.info("No Match: ");
		for (MatchCriteriaResult mrc : mr.getNoMatches()) {
			logger.info("Kriterium: " + mrc.getCriteria() + " Eingabe: " + mrc.getCandidateValue() + " Kandidat: " + mrc.getToMatchValue() + " Wahrscheinlichkeit: "
					+ mrc.getProbability());
		}

		logger.info("Ratio: " + mr.getRatio() + " Entscheidung: " + mr.getDecision());
	}

	public MatchCandidateLoader<T> getMatchCandidateLoader() {
		return matchCandidateLoader;
	}

	public void setMatchCandidateLoader(MatchCandidateLoader<T> matchCandidateLoader) {
		this.matchCandidateLoader = matchCandidateLoader;
	}

	public ValidateRequiredStrategy<T> getValidateRequiredStrategy() {
		return validateRequiredStrategy;
	}

	public void setValidateRequiredStrategy(ValidateRequiredStrategy<T> validateRequiredStrategy) {
		this.validateRequiredStrategy = validateRequiredStrategy;
	}

	public PerfectMatchStrategy<T> getPerfectMatchStrategy() {
		return perfectMatchStrategy;
	}

	public void setPerfectMatchStrategy(PerfectMatchStrategy<T> perfectMatchStrategy) {
		this.perfectMatchStrategy = perfectMatchStrategy;
	}

	public PerfectMatchProcessor<T> getPerfectMatchProzessor() {
		return perfectMatchProzessor;
	}

	public void setPerfectMatchProzessor(
			PerfectMatchProcessor<T> perfectMatchProzessor) {
		this.perfectMatchProzessor = perfectMatchProzessor;
	}

}
