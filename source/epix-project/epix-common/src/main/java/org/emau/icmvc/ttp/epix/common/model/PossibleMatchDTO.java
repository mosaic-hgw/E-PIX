package org.emau.icmvc.ttp.epix.common.model;

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
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 
 * @author geidell
 *
 */
public class PossibleMatchDTO implements Serializable
{
	private static final long serialVersionUID = -558125912913762759L;
	private final Set<MPIIdentityDTO> matchingMPIIdentities = new HashSet<>();
	private long linkId;
	private Double probability;
	private Date possibleMatchCreated;

	public PossibleMatchDTO()
	{
		super();
	}

	public PossibleMatchDTO(MPIIdentityDTO mpiIdentity1, MPIIdentityDTO mpiIdentity2, long linkId, Double probability, Date possibleMatchCreated)
	{
		super();
		matchingMPIIdentities.add(mpiIdentity1);
		matchingMPIIdentities.add(mpiIdentity2);
		this.linkId = linkId;
		this.probability = probability;
		this.possibleMatchCreated = possibleMatchCreated;
	}

	public Set<MPIIdentityDTO> getMatchingMPIIdentities()
	{
		return matchingMPIIdentities;
	}

	public void setMatchingMPIIdentities(Set<MPIIdentityDTO> matchingMPIIdentities)
	{
		if (!this.matchingMPIIdentities.equals(matchingMPIIdentities))
		{
			this.matchingMPIIdentities.clear();
			this.matchingMPIIdentities.addAll(matchingMPIIdentities);
		}
	}

	public long getLinkId()
	{
		return linkId;
	}

	public void setLinkId(long linkId)
	{
		this.linkId = linkId;
	}

	public Double getProbability()
	{
		return probability;
	}

	public void setProbability(Double probability)
	{
		this.probability = probability;
	}

	public Date getPossibleMatchCreated()
	{
		return possibleMatchCreated;
	}

	public void setPossibleMatchCreated(Date possibleMatchCreated)
	{
		this.possibleMatchCreated = possibleMatchCreated;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (linkId ^ (linkId >>> 32));
		result = prime * result + ((matchingMPIIdentities == null) ? 0 : matchingMPIIdentities.hashCode());
		result = prime * result + ((possibleMatchCreated == null) ? 0 : possibleMatchCreated.hashCode());
		result = prime * result + ((probability == null) ? 0 : probability.hashCode());
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
		PossibleMatchDTO other = (PossibleMatchDTO) obj;
		if (linkId != other.linkId)
			return false;
		if (matchingMPIIdentities == null)
		{
			if (other.matchingMPIIdentities != null)
				return false;
		}
		else if (!matchingMPIIdentities.equals(other.matchingMPIIdentities))
			return false;
		if (possibleMatchCreated == null)
		{
			if (other.possibleMatchCreated != null)
				return false;
		}
		else if (!possibleMatchCreated.equals(other.possibleMatchCreated))
			return false;
		if (probability == null)
		{
			if (other.probability != null)
				return false;
		}
		else if (!probability.equals(other.probability))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "PossibleMatchDTO [matchingMPIIdentities=" + matchingMPIIdentities.stream().map(Object::toString).collect(Collectors.joining(", "))
				+ ", linkId=" + linkId + ", probability=" + probability + ", possibleMatchCreated=" + possibleMatchCreated + "]";
	}
}
