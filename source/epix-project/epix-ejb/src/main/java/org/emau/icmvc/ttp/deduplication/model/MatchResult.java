package org.emau.icmvc.ttp.deduplication.model;

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


import java.util.Arrays;

import org.emau.icmvc.ttp.epix.internal.PreprocessedCacheObject;

/**
 * 
 * @author schackc, geidell
 * @since 05.11.2010
 */
public class MatchResult
{
	public enum DECISION
	{
		MATCH, NO_MATCH, POSSIBLE_MATCH;
	}

	private final String matchStrategy;
	private final double[] matchProbabilities;
	private final double[] noMatchProbabilities;
	private double ratio;
	private DECISION decision = DECISION.NO_MATCH;
	private final PreprocessedCacheObject comparativeValue;

	public MatchResult(String matchStrategy, PreprocessedCacheObject comparativeValue, int size)
	{
		super();
		this.matchStrategy = matchStrategy;
		this.comparativeValue = comparativeValue;
		matchProbabilities = new double[size];
		noMatchProbabilities = new double[size];
	}

	public String getMatchStrategy()
	{
		return matchStrategy;
	}

	public double getRatio()
	{
		return ratio;
	}

	public void setRatio(double ratio)
	{
		this.ratio = ratio;
	}

	public DECISION getDecision()
	{
		return decision;
	}

	public void setDecision(DECISION decision)
	{
		this.decision = decision;
	}

	public PreprocessedCacheObject getComparativeValue()
	{
		return comparativeValue;
	}

	public double getMatchProbabilities(int index)
	{
		return matchProbabilities[index];
	}

	public void setMatchProbabilities(int index, double value)
	{
		matchProbabilities[index] = value;
	}

	public double getNoMatchProbabilities(int index)
	{
		return noMatchProbabilities[index];
	}

	public void setNoMatchProbabilities(int index, double value)
	{
		noMatchProbabilities[index] = value;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((comparativeValue == null) ? 0 : comparativeValue.hashCode());
		result = prime * result + ((decision == null) ? 0 : decision.hashCode());
		result = prime * result + Arrays.hashCode(matchProbabilities);
		result = prime * result + ((matchStrategy == null) ? 0 : matchStrategy.hashCode());
		result = prime * result + Arrays.hashCode(noMatchProbabilities);
		long temp;
		temp = Double.doubleToLongBits(ratio);
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
		MatchResult other = (MatchResult) obj;
		if (comparativeValue == null)
		{
			if (other.comparativeValue != null)
				return false;
		}
		else if (!comparativeValue.equals(other.comparativeValue))
			return false;
		if (decision != other.decision)
			return false;
		if (!Arrays.equals(matchProbabilities, other.matchProbabilities))
			return false;
		if (matchStrategy == null)
		{
			if (other.matchStrategy != null)
				return false;
		}
		else if (!matchStrategy.equals(other.matchStrategy))
			return false;
		if (!Arrays.equals(noMatchProbabilities, other.noMatchProbabilities))
			return false;
		if (Double.doubleToLongBits(ratio) != Double.doubleToLongBits(other.ratio))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "MatchResult [matchStrategy=" + matchStrategy + ", matchProbabilities=" + Arrays.toString(matchProbabilities)
				+ ", noMatchProbabilities=" + Arrays.toString(noMatchProbabilities) + ", ratio=" + ratio + ", decision=" + decision
				+ ", comparativeValue=" + comparativeValue + "]";
	}
}
