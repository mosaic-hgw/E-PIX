package org.emau.icmvc.ttp.epix.pdqquery.model;

/*-
 * ###license-information-start###
 * E-PIX - Enterprise Patient Identifier Cross-referencing
 * __
 * Copyright (C) 2009 - 2023 Trusted Third Party of the University Medicine Greifswald
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


import java.io.Serializable;

import org.emau.icmvc.ttp.epix.pdqquery.model.enums.MatchTypeForSearch;

public class FuzzySearchParams implements Serializable
{
	private static final long serialVersionUID = -230892869586756955L;
	private MatchTypeForSearch matchType = MatchTypeForSearch.MATCH;
	private double thresholdPossibleMatch = 0;
	private double thresholdAutomaticMatch = 1000;

	public FuzzySearchParams(MatchTypeForSearch matchType, double thresholdPossibleMatch, double thresholdAutomaticMatch)
	{
		super();
		this.matchType = matchType;
		this.thresholdPossibleMatch = thresholdPossibleMatch;
		this.thresholdAutomaticMatch = thresholdAutomaticMatch;
	}

	public MatchTypeForSearch getMatchType()
	{
		return matchType;
	}

	public void setMatchType(MatchTypeForSearch matchType)
	{
		this.matchType = matchType;
	}

	public double getThresholdPossibleMatch()
	{
		return thresholdPossibleMatch;
	}

	public void setThresholdPossibleMatch(double thresholdPossibleMatch)
	{
		this.thresholdPossibleMatch = thresholdPossibleMatch;
	}

	public double getThresholdAutomaticMatch()
	{
		return thresholdAutomaticMatch;
	}

	public void setThresholdAutomaticMatch(double thresholdAutomaticMatch)
	{
		this.thresholdAutomaticMatch = thresholdAutomaticMatch;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((matchType == null) ? 0 : matchType.hashCode());
		long temp;
		temp = Double.doubleToLongBits(thresholdAutomaticMatch);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(thresholdPossibleMatch);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FuzzySearchParams other = (FuzzySearchParams) obj;
		if (matchType != other.matchType)
			return false;
		if (Double.doubleToLongBits(thresholdAutomaticMatch) != Double.doubleToLongBits(other.thresholdAutomaticMatch))
			return false;
		if (Double.doubleToLongBits(thresholdPossibleMatch) != Double.doubleToLongBits(other.thresholdPossibleMatch))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "FuzzySearchParams [matchType=" + matchType + ", thresholdPossibleMatch=" + thresholdPossibleMatch + ", thresholdAutomaticMatch="
				+ thresholdAutomaticMatch + "]";
	}
}
