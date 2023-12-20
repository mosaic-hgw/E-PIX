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
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.emau.icmvc.ttp.epix.common.model.enums.PossibleMatchPriority;

/**
 *
 * @author geidell
 *
 */
public class PossibleMatchDTO extends PossibleMatchBaseDTO implements Serializable
{
	private static final long serialVersionUID = 6193372669052258377L;
	private final Set<MPIIdentityDTO> matchingMPIIdentities = new HashSet<>();

	public PossibleMatchDTO()
	{}

	public PossibleMatchDTO(MPIIdentityDTO mpiIdentity1, MPIIdentityDTO mpiIdentity2, long linkId, double probability, Date possibleMatchCreated, PossibleMatchPriority priority)
	{
		super(linkId, probability, possibleMatchCreated, priority);
		matchingMPIIdentities.add(new MPIIdentityDTO(mpiIdentity1));
		matchingMPIIdentities.add(new MPIIdentityDTO(mpiIdentity2));
	}

	public PossibleMatchDTO(PossibleMatchBaseDTO baseDTO, Set<MPIIdentityDTO> matchingMPIIdentities)
	{
		super(baseDTO);
		setMatchingMPIIdentities(matchingMPIIdentities);
	}

	public PossibleMatchDTO(PossibleMatchDTO dto)
	{
		this(dto, dto.getMatchingMPIIdentities());
	}

	public Set<MPIIdentityDTO> getMatchingMPIIdentities()
	{
		return matchingMPIIdentities;
	}

	public void setMatchingMPIIdentities(Set<MPIIdentityDTO> matchingMPIIdentities)
	{
		this.matchingMPIIdentities.clear();
		if (matchingMPIIdentities != null)
		{
			for (MPIIdentityDTO mpiDTO : matchingMPIIdentities)
			{
				this.matchingMPIIdentities.add(new MPIIdentityDTO(mpiDTO));
			}
		}
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof PossibleMatchDTO that))
		{
			return false;
		}
		if (!super.equals(o))
		{
			return false;
		}
		return getMatchingMPIIdentities().equals(that.getMatchingMPIIdentities());
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(super.hashCode(), getMatchingMPIIdentities());
	}

	@Override
	public String toString()
	{
		return new StringJoiner(", ", PossibleMatchDTO.class.getSimpleName() + "[", "]")
				.add("matchingMPIIdentities=" + matchingMPIIdentities.stream().map(Object::toString).collect(Collectors.joining(", ")))
				.add(toStringBase())
				.toString();
	}
}
