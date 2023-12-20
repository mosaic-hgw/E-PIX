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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.SetValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.emau.icmvc.ttp.epix.common.model.enums.VitalStatus;
import org.emau.icmvc.ttp.epix.common.model.enums.VitalStatusType;

/**
 *
 * @author geidell
 *
 */
public class PersonDTO extends PersonBaseDTO
{
	private static final long serialVersionUID = -7994810876955918912L;
	private IdentityOutDTO referenceIdentity;
	private final List<IdentityOutDTO> otherIdentities = new ArrayList<>();

	public PersonDTO()
	{}

	public PersonDTO(PersonBaseDTO superDTO, IdentityOutDTO referenceIdentity, List<IdentityOutDTO> otherIdentities)
	{
		super(superDTO);
		setReferenceIdentity(referenceIdentity);
		setOtherIdentities(otherIdentities);
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
		this.referenceIdentity = referenceIdentity != null ? new IdentityOutDTO(referenceIdentity) : null;
	}

	public List<IdentityOutDTO> getOtherIdentities()
	{
		return otherIdentities;
	}

	public void setOtherIdentities(List<IdentityOutDTO> otherIdentities)
	{
		this.otherIdentities.clear();
		if (otherIdentities != null)
		{
			for (IdentityOutDTO iDTO : otherIdentities)
			{
				this.otherIdentities.add(new IdentityOutDTO(iDTO));
			}
		}
	}

	/**
	 * Returns the vital status type of the person which is derived from the reference identity and the other identities as follows:
	 * If the reference identity or any of the other identities is DEAD, then the person's vital status type is DEAD,
	 * otherwise if the reference identity or any of the other identities is ALIVE, then the person's vital status type is ALIVE,
	 * otherwise the vital status type of the person is UNKNOWN.
	 *
	 * @return the vital status type of the person
	 */
	public VitalStatusType getVitalStatusType()
	{
		VitalStatusType result = VitalStatusType.getType(referenceIdentity.getVitalStatus());
		if (result.isDead())
		{
			return result;
		}
		for (IdentityOutDTO i : otherIdentities)
		{
			if (VitalStatusType.getType(i.getVitalStatus()).isDead())
			{
				return VitalStatusType.DEAD;
			}
			else if (VitalStatusType.getType(i.getVitalStatus()).isAlive())
			{
				result = VitalStatusType.ALIVE;
			}
		}
		return result;
	}

	/**
	 * Returns a map with (distinct) conflicting vital status descriptors of other identities as keys and sets of corresponding identities as values.
	 * Conflicting vital statuses are those statuses of other identities having a different vital status than the reference identity (including date of death if dead).
	 *
	 * @param includeUnknown
	 *            true to include identities with unknown vital status type
	 * @return a map with conflicting vital status descriptors as keys and the corresponding identities as values.
	 */
	public SetValuedMap<VitalStatus.Descriptor, IdentityOutDTO> getIdentitiesWithConflictingVitalStatus(boolean includeUnknown)
	{
		if (otherIdentities == null || otherIdentities.isEmpty())
		{
			return new HashSetValuedHashMap<>();
		}

		VitalStatus.Descriptor d = referenceIdentity.getVitalStatusDescriptor();

		return otherIdentities.stream().filter(id -> d.conflictsWith(id.getVitalStatusDescriptor()) && (includeUnknown || !id.getVitalStatusDescriptor().getVitalStatus().isUnknown())).collect(
				HashSetValuedHashMap::new, (multimap, id) -> multimap.put(id.getVitalStatusDescriptor(), id), MultiValuedMap::putAll);
	}

	/**
	 * Returns a sorted list with distinct vital status descriptors of other identities than the reference identity having a different vital status than the reference identity.
	 * The order guarantees to list DEAD statuses with a non-null date of death first, then the DEAD status without a date of death, followed by ALIVE and then UNKNOWN (if present).
	 *
	 * @param includeUnknown
	 *            true to include identities with unknown vital status type
	 * @return a sorted list with distinct conflicting vital status descriptors of other identities
	 */
	public List<VitalStatus.Descriptor> getConflictingVitalStatuses(boolean includeUnknown)
	{
		return getIdentitiesWithConflictingVitalStatus(includeUnknown).keys().stream().sorted().collect(Collectors.toList());
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (referenceIdentity == null ? 0 : referenceIdentity.hashCode());
		result = prime * result + (otherIdentities == null ? 0 : otherIdentities.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (!super.equals(obj))
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		PersonDTO other = (PersonDTO) obj;
		if (referenceIdentity == null)
		{
			if (other.referenceIdentity != null)
			{
				return false;
			}
		}
		else if (!referenceIdentity.equals(other.referenceIdentity))
		{
			return false;
		}
		if (otherIdentities == null)
		{
			if (other.otherIdentities != null)
			{
				return false;
			}
		}
		else if (!otherIdentities.equals(other.otherIdentities))
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		return "PersonDTO [referenceIdentity=" + referenceIdentity + ", otherIdentities="
				+ otherIdentities.stream().map(Object::toString).collect(Collectors.joining(", ")) + ", including " + super.toString() + "]";
	}
}
