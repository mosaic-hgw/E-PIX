package org.emau.icmvc.ttp.epix.pdqquery.model;

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


import java.io.Serializable;

import org.emau.icmvc.ttp.epix.common.model.IdentityInDTO;

public class SearchMask implements Serializable
{
	private static final long serialVersionUID = 3954145527489623040L;
	private String domainName;
	private IdentityInDTO identity;
	/**
	 * if true, then all given fields must match, else at least one of them ("or")
	 */
	private boolean and = true;
	private int yearOfBirth = 0;
	private int monthOfBirth = 0;
	private int dayOfBirth = 0;
	private int maxResults = 10;
	private FuzzySearchParams fuzzySearchParams = null;

	public SearchMask()
	{
		super();
	}

	public SearchMask(String domainName, IdentityInDTO identity, boolean and, int yearOfBirth, int monthOfBirth, int dayOfBirth, int maxResults)
	{
		this(domainName, identity, and, yearOfBirth, monthOfBirth, dayOfBirth, maxResults, null);
	}

	public SearchMask(String domainName, IdentityInDTO identity, boolean and, int yearOfBirth, int monthOfBirth, int dayOfBirth, int maxResults,
			FuzzySearchParams fuzzySearchParams)
	{
		super();
		this.domainName = domainName;
		this.identity = identity;
		this.and = and;
		this.yearOfBirth = yearOfBirth;
		this.monthOfBirth = monthOfBirth;
		this.dayOfBirth = dayOfBirth;
		this.maxResults = maxResults;
		this.fuzzySearchParams = fuzzySearchParams;
	}

	public String getDomainName()
	{
		return domainName;
	}

	public void setDomainName(String domainName)
	{
		this.domainName = domainName;
	}

	public IdentityInDTO getIdentity()
	{
		return identity;
	}

	public void setIdentity(IdentityInDTO identity)
	{
		this.identity = identity;
	}

	public boolean isAnd()
	{
		return and;
	}

	public void setAnd(boolean and)
	{
		this.and = and;
	}

	public int getYearOfBirth()
	{
		return yearOfBirth;
	}

	public void setYearOfBirth(int yearOfBirth)
	{
		this.yearOfBirth = yearOfBirth;
	}

	public int getMonthOfBirth()
	{
		return monthOfBirth;
	}

	public void setMonthOfBirth(int monthOfBirth)
	{
		this.monthOfBirth = monthOfBirth;
	}

	public int getDayOfBirth()
	{
		return dayOfBirth;
	}

	public void setDayOfBirth(int dayOfBirth)
	{
		this.dayOfBirth = dayOfBirth;
	}

	public int getMaxResults()
	{
		return maxResults;
	}

	public void setMaxResults(int maxResults)
	{
		this.maxResults = maxResults;
	}

	public FuzzySearchParams getFuzzySearchParams()
	{
		return fuzzySearchParams;
	}

	public void setFuzzySearchParams(FuzzySearchParams fuzzySearchParams)
	{
		this.fuzzySearchParams = fuzzySearchParams;
	}

	public boolean hasSearchValues()
	{
		// same fields as in IdentityPreprocessedCache.findPersonIdsByPDQ
		return yearOfBirth > 0 || monthOfBirth > 0 || dayOfBirth > 0
				|| identity != null && (identity.getFirstName() != null && !identity.getFirstName().isEmpty()
						|| identity.getMiddleName() != null && !identity.getMiddleName().isEmpty()
						|| identity.getLastName() != null && !identity.getLastName().isEmpty()
						|| identity.getPrefix() != null && !identity.getPrefix().isEmpty()
						|| identity.getSuffix() != null && !identity.getSuffix().isEmpty() || identity.getGender() != null
						|| identity.getBirthDate() != null || identity.getBirthPlace() != null && !identity.getBirthPlace().isEmpty()
						|| identity.getRace() != null && !identity.getRace().isEmpty()
						|| identity.getReligion() != null && !identity.getReligion().isEmpty()
						|| identity.getMothersMaidenName() != null && !identity.getMothersMaidenName().isEmpty()
						|| identity.getDegree() != null && !identity.getDegree().isEmpty()
						|| identity.getMotherTongue() != null && !identity.getMotherTongue().isEmpty()
						|| identity.getNationality() != null && !identity.getNationality().isEmpty()
						|| identity.getCivilStatus() != null && !identity.getCivilStatus().isEmpty()
						|| identity.getValue1() != null && !identity.getValue1().isEmpty()
						|| identity.getValue2() != null && !identity.getValue2().isEmpty()
						|| identity.getValue3() != null && !identity.getValue3().isEmpty()
						|| identity.getValue4() != null && !identity.getValue4().isEmpty()
						|| identity.getValue5() != null && !identity.getValue5().isEmpty()
						|| identity.getValue6() != null && !identity.getValue6().isEmpty()
						|| identity.getValue7() != null && !identity.getValue7().isEmpty()
						|| identity.getValue8() != null && !identity.getValue8().isEmpty()
						|| identity.getValue9() != null && !identity.getValue9().isEmpty()
						|| identity.getValue10() != null && !identity.getValue10().isEmpty()
						|| identity.getIdentifiers() != null && !identity.getIdentifiers().isEmpty());
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (and ? 1231 : 1237);
		result = prime * result + dayOfBirth;
		result = prime * result + (domainName == null ? 0 : domainName.hashCode());
		result = prime * result + (fuzzySearchParams == null ? 0 : fuzzySearchParams.hashCode());
		result = prime * result + (identity == null ? 0 : identity.hashCode());
		result = prime * result + maxResults;
		result = prime * result + monthOfBirth;
		result = prime * result + yearOfBirth;
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
		SearchMask other = (SearchMask) obj;
		if (and != other.and)
		{
			return false;
		}
		if (dayOfBirth != other.dayOfBirth)
		{
			return false;
		}
		if (domainName == null)
		{
			if (other.domainName != null)
			{
				return false;
			}
		}
		else if (!domainName.equals(other.domainName))
		{
			return false;
		}
		if (fuzzySearchParams == null)
		{
			if (other.fuzzySearchParams != null)
			{
				return false;
			}
		}
		else if (!fuzzySearchParams.equals(other.fuzzySearchParams))
		{
			return false;
		}
		if (identity == null)
		{
			if (other.identity != null)
			{
				return false;
			}
		}
		else if (!identity.equals(other.identity))
		{
			return false;
		}
		if (maxResults != other.maxResults)
		{
			return false;
		}
		if (monthOfBirth != other.monthOfBirth)
		{
			return false;
		}
		if (yearOfBirth != other.yearOfBirth)
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		return "SearchMask [domainName=" + domainName + ", identity=" + identity + ", and=" + and + ", yearOfBirth=" + yearOfBirth + ", monthOfBirth="
				+ monthOfBirth + ", dayOfBirth=" + dayOfBirth + ", maxResults=" + maxResults + ", fuzzySearchParams=" + fuzzySearchParams + "]";
	}
}
