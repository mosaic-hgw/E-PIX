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

/**
 * 
 * @author geidell
 *
 */
public class PossibleMatchForMPIDTO implements Serializable
{
	private static final long serialVersionUID = -4822913251520095663L;
	private IdentifierDTO requestedMPI;
	private IdentityOutDTO assignedIdentity;
	private MPIIdentityDTO matchingMPIIdentity;
	private long linkId;
	private Double probability;
	private Date possibleMatchCreated;

	public PossibleMatchForMPIDTO()
	{
		super();
	}

	public PossibleMatchForMPIDTO(IdentifierDTO requestedMPI, IdentityOutDTO assignedIdentity, MPIIdentityDTO matchingMPIIdentity, long linkId,
			Double probability, Date possibleMatchCreated)
	{
		super();
		this.requestedMPI = requestedMPI;
		this.assignedIdentity = assignedIdentity;
		this.matchingMPIIdentity = matchingMPIIdentity;
		this.linkId = linkId;
		this.probability = probability;
		this.possibleMatchCreated = possibleMatchCreated;
	}

	public IdentifierDTO getRequestedMPI()
	{
		return requestedMPI;
	}

	public void setRequestedMPI(IdentifierDTO requestedMPI)
	{
		this.requestedMPI = requestedMPI;
	}

	public IdentityOutDTO getAssignedIdentity()
	{
		return assignedIdentity;
	}

	public void setAssignedIdentity(IdentityOutDTO assignedIdentity)
	{
		this.assignedIdentity = assignedIdentity;
	}

	public MPIIdentityDTO getMatchingMPIIdentity()
	{
		return matchingMPIIdentity;
	}

	public void setMatchingMPIIdentity(MPIIdentityDTO matchingMPIIdentity)
	{
		this.matchingMPIIdentity = matchingMPIIdentity;
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
		result = prime * result + ((assignedIdentity == null) ? 0 : assignedIdentity.hashCode());
		result = prime * result + (int) (linkId ^ (linkId >>> 32));
		result = prime * result + ((matchingMPIIdentity == null) ? 0 : matchingMPIIdentity.hashCode());
		result = prime * result + ((possibleMatchCreated == null) ? 0 : possibleMatchCreated.hashCode());
		result = prime * result + ((probability == null) ? 0 : probability.hashCode());
		result = prime * result + ((requestedMPI == null) ? 0 : requestedMPI.hashCode());
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
		PossibleMatchForMPIDTO other = (PossibleMatchForMPIDTO) obj;
		if (assignedIdentity == null)
		{
			if (other.assignedIdentity != null)
				return false;
		}
		else if (!assignedIdentity.equals(other.assignedIdentity))
			return false;
		if (linkId != other.linkId)
			return false;
		if (matchingMPIIdentity == null)
		{
			if (other.matchingMPIIdentity != null)
				return false;
		}
		else if (!matchingMPIIdentity.equals(other.matchingMPIIdentity))
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
		if (requestedMPI == null)
		{
			if (other.requestedMPI != null)
				return false;
		}
		else if (!requestedMPI.equals(other.requestedMPI))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "PossibleMatchForMPIDTO [requestedMPI=" + requestedMPI + ", assignedIdentity=" + assignedIdentity + ", matchingMPIIdentity="
				+ matchingMPIIdentity + ", linkId=" + linkId + ", probability=" + probability + ", possibleMatchCreated=" + possibleMatchCreated
				+ "]";
	}
}
