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

public class DeduplicationResult <T> {

	
	private List<MatchResult<T>> criticalMatches = new ArrayList<MatchResult<T>>();
	
	private List<MatchResult<T>> matches = new ArrayList<MatchResult<T>>();
	
	@Deprecated
	private T perfectMatch = null;
	
	private boolean noMatchable;
	
	private T matchable;

	public List<MatchResult<T>> getCriticalMatches() {
		return criticalMatches;
	}

	public void setCriticalMatches(List<MatchResult<T>> criticalMatches) {
		this.criticalMatches = criticalMatches;
	}

	public List<MatchResult<T>> getMatches() {
		return matches;
	}

	public void setMatches(List<MatchResult<T>> matches) {
		this.matches = matches;
	}
	
	public void addMatchResult(MatchResult<T> result, MatchResult.DECISION decision){		
		switch (decision) {
			case MATCH: matches.add(result);
						 break;
			case CRITICAL: criticalMatches.add(result);
							break;
			default : break; 
				
		}
		
	}
	
	public boolean hasNonMatches() {
		return criticalMatches.isEmpty() && matches.isEmpty() && perfectMatch == null;
	}
	
	/**
	 * Given matchable could be assigned to exactly one existing matchable within the store.
	 * @return
	 */
	public boolean hasUniqueMatch(){
		return criticalMatches.isEmpty() && matches.size()==1;
	}
	
	/**
	 * Given matchable could be assigned to more than one existing person or has more than one critical match
	 * @return
	 */
	public boolean hasMultibleMatches() {
		return !criticalMatches.isEmpty() || matches.size() > 1;
	}
	
	/**
	 * Given matchable could be assigned to an perfect match.
	 * @return
	 */
	public boolean hasPerfectMatch() {
		return perfectMatch != null;
	}
	
	public MatchResult<T> getUniqueMatchResult(){
		if(!hasUniqueMatch()){
			throw new IllegalArgumentException("Matchresult is not unique!");
		}
		return matches.get(0);
	}

	public T getUniqueMatch(){
		return getUniqueMatchResult().getComparativeValue();
	}
	
	public List<MatchResult<T>> getMatchResults(){
		if(!hasMultibleMatches()){
			throw new IllegalArgumentException("Matchresult is unique!");
		}
		List<MatchResult<T>> m = new ArrayList<MatchResult<T>>();
		m.addAll(matches);
		m.addAll(criticalMatches);
		return m;
	}
	
	public void setNoMatchable(boolean bool){
		this.noMatchable = bool;
	}
	
	public boolean isNoMatchable(){
		return noMatchable;
	}

	public T getMatchable() {
		return matchable;
	}

	public void setMatchable(T matchable) {
		this.matchable = matchable;
	}

	public T getPerfectMatch() {
		return perfectMatch;
	}

	public void setPerfectMatch(T perfectMatch) {
		this.perfectMatch = perfectMatch;
	}
	
	
	
	
	
}
