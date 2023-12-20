package org.emau.icmvc.ttp.epix.common.model;

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
import java.util.Date;
import java.util.Objects;
import java.util.StringJoiner;

import org.emau.icmvc.ttp.epix.common.model.enums.PossibleMatchPriority;

/**
 *
 * @author geidell
 *
 */
public class PossibleMatchForMPIDTO extends PossibleMatchBaseDTO implements Serializable
{
	private static final long serialVersionUID = -3516760475884765479L;
	private IdentifierDTO requestedMPI;
	private IdentityOutDTO assignedIdentity;
	private MPIIdentityDTO matchingMPIIdentity;

	public PossibleMatchForMPIDTO()
	{}

	public PossibleMatchForMPIDTO(IdentifierDTO requestedMPI, IdentityOutDTO assignedIdentity, MPIIdentityDTO matchingMPIIdentity, long linkId,
			Double probability, Date possibleMatchCreated, PossibleMatchPriority priority)
	{
		super(linkId, probability, possibleMatchCreated, priority);
		setRequestedMPI(requestedMPI);
		setAssignedIdentity(assignedIdentity);
		setMatchingMPIIdentity(matchingMPIIdentity);
	}

	public PossibleMatchForMPIDTO(PossibleMatchBaseDTO baseDTO, IdentifierDTO requestedMPI, IdentityOutDTO assignedIdentity, MPIIdentityDTO matchingMPIIdentity)
	{
		super(baseDTO);
		setRequestedMPI(requestedMPI);
		setAssignedIdentity(assignedIdentity);
		setMatchingMPIIdentity(matchingMPIIdentity);
	}

	public PossibleMatchForMPIDTO(PossibleMatchForMPIDTO dto)
	{
		this(dto, dto.getRequestedMPI(), dto.getAssignedIdentity(), dto.getMatchingMPIIdentity());
	}

	public IdentifierDTO getRequestedMPI()
	{
		return requestedMPI;
	}

	public void setRequestedMPI(IdentifierDTO requestedMPI)
	{
		this.requestedMPI = requestedMPI != null ? new IdentifierDTO(requestedMPI) : null;
	}

	public IdentityOutDTO getAssignedIdentity()
	{
		return assignedIdentity;
	}

	public void setAssignedIdentity(IdentityOutDTO assignedIdentity)
	{
		this.assignedIdentity = assignedIdentity != null ? new IdentityOutDTO(assignedIdentity) : null;
	}

	public MPIIdentityDTO getMatchingMPIIdentity()
	{
		return matchingMPIIdentity;
	}

	public void setMatchingMPIIdentity(MPIIdentityDTO matchingMPIIdentity)
	{
		this.matchingMPIIdentity = matchingMPIIdentity != null ? new MPIIdentityDTO(matchingMPIIdentity) : null;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof PossibleMatchForMPIDTO that))
		{
			return false;
		}
		if (!super.equals(o))
		{
			return false;
		}
		return Objects.equals(getRequestedMPI(), that.getRequestedMPI()) && Objects.equals(getAssignedIdentity(), that.getAssignedIdentity()) && Objects.equals(
				getMatchingMPIIdentity(), that.getMatchingMPIIdentity());
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(super.hashCode(), getRequestedMPI(), getAssignedIdentity(), getMatchingMPIIdentity());
	}

	@Override
	public String toString()
	{
		return new StringJoiner(", ", PossibleMatchForMPIDTO.class.getSimpleName() + "[", "]")
				.add("requestedMPI=" + requestedMPI)
				.add("assignedIdentity=" + assignedIdentity)
				.add("matchingMPIIdentity=" + matchingMPIIdentity)
				.add(toStringBase())
				.toString();
	}
}
