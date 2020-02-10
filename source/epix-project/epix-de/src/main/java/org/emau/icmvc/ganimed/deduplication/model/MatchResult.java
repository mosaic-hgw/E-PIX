package org.emau.icmvc.ganimed.deduplication.model;

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



/**
 * 
 * @author schackc
 * @since 05.11.2010
 */
public class MatchResult<T> {
	
	public enum DECISION {MATCH("Match"), NOMATCH("Nomatch"), CRITICAL("Critical");
						  
						  @SuppressWarnings("unused")
						  private String value;
						  
						  private DECISION(String value) {
							  this.value = value;
						  }				
	}

	private String matchStrategy = "";
	
	private List<MatchCriteriaResult> matches = new ArrayList<MatchCriteriaResult>();
	
	private List<MatchCriteriaResult> noMatches = new ArrayList<MatchCriteriaResult>();
	
	private double ratio = 0.0;
	
	private DECISION decision = DECISION.NOMATCH;

	private T comparativeValue;

	public MatchResult() {
	}
	
	public String getMatchStrategy() {
		return matchStrategy;
	}

	public void setMatchStrategy(String matchStrategy) {
		this.matchStrategy = matchStrategy;
	}

	public double getRatio() {
		return ratio;
	}

	public void setRatio(double ratio) {
		this.ratio = ratio;
	}

	public DECISION getDecision() {
		return decision;
	}

	public void setDecision(DECISION decision) {
		this.decision = decision;
	}
	
	public void setComparativeValue(T comparativeValue) {
		this.comparativeValue = comparativeValue;
		
	}	

	public T getComparativeValue() {
		return comparativeValue;
	}
	
	public List<MatchCriteriaResult> getMatches() {
		return matches;
	}

	public void setMatches(List<MatchCriteriaResult> matches) {
		this.matches = matches;
	}

	public List<MatchCriteriaResult> getNoMatches() {
		return noMatches;
	}

	public void setNoMatches(List<MatchCriteriaResult> noMatches) {
		this.noMatches = noMatches;
	}
	
	public int getNoMatchCount() {
		return noMatches.size();
	}
	
	public int getMatchCount() {
		return matches.size();
	}

	@Override
	public String toString() {
		return "MatchResult [matchStrategy=" + matchStrategy + ", matches="
				+ matches + ", noMatches=" + noMatches + ", ratio="
				+ ratio + ", decision=" + decision + ", comparativeValue="
				+ comparativeValue + "]";
	}

}
