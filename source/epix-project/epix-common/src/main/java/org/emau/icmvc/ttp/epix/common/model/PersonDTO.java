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


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 
 * @author geidell
 *
 */
public class PersonDTO extends PersonBaseDTO
{
	private static final long serialVersionUID = 7976560337541641293L;
	private IdentityOutDTO referenceIdentity;
	private List<IdentityOutDTO> otherIdentities = new ArrayList<>();

	public PersonDTO()
	{}

	public PersonDTO(PersonBaseDTO superDTO, IdentityOutDTO referenceIdentity, List<IdentityOutDTO> otherIdentities)
	{
		super(superDTO);
		this.referenceIdentity = referenceIdentity;
		if (otherIdentities != null)
		{
			this.otherIdentities = otherIdentities;
		}
	}

	public PersonDTO(PersonDTO dto)
	{
		this(dto, dto.getReferenceIdentity(), dto.getOtherIdentities());
	}

	public IdentityOutDTO getReferenceIdentity()
	{
		return referenceIdentity;
	}

	public void setReferenceIdentity(IdentityOutDTO referenceIdentity)
	{
		this.referenceIdentity = referenceIdentity;
	}

	public List<IdentityOutDTO> getOtherIdentities()
	{
		return otherIdentities;
	}

	public void setOtherIdentities(List<IdentityOutDTO> otherIdentities)
	{
		if (otherIdentities != null)
		{
			this.otherIdentities = otherIdentities;
		}
		else
		{
			this.otherIdentities.clear();
		}
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((referenceIdentity == null) ? 0 : referenceIdentity.hashCode());
		result = prime * result + ((otherIdentities == null) ? 0 : otherIdentities.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PersonDTO other = (PersonDTO) obj;
		if (referenceIdentity == null)
		{
			if (other.referenceIdentity != null)
				return false;
		}
		else if (!referenceIdentity.equals(other.referenceIdentity))
			return false;
		if (otherIdentities == null)
		{
			if (other.otherIdentities != null)
				return false;
		}
		else if (!otherIdentities.equals(other.otherIdentities))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "PersonDTO [referenceIdentity=" + referenceIdentity + ", otherIdentities="
				+ otherIdentities.stream().map(Object::toString).collect(Collectors.joining(", ")) + ", including " + super.toString() + "]";
	}
}
