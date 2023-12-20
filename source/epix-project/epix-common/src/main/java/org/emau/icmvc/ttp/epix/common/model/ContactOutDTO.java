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

import java.util.Date;

/**
 *
 * @author geidell
 *
 */
public class ContactOutDTO extends ContactInDTO
{
	private static final long serialVersionUID = -7249525362220014296L;
	private long contactId;
	private Integer contactVersion;
	private long identityId;
	private boolean deactivated;
	private Date contactCreated;
	private Date contactLastEdited;

	public ContactOutDTO()
	{}

	public ContactOutDTO(ContactInDTO superDTO, long contactId, Integer contactVersion, long identityId, boolean deactivated, Date contactCreated,
			Date contactLastEdited)
	{
		super(superDTO);
		this.contactId = contactId;
		this.contactVersion = contactVersion;
		this.identityId = identityId;
		this.deactivated = deactivated;
		setContactCreated(contactCreated);
		setContactLastEdited(contactLastEdited);
	}

	public ContactOutDTO(ContactOutDTO dto)
	{
		this(dto, dto.getContactId(), dto.getContactVersion(), dto.getIdentityId(), dto.isDeactivated(), dto.getContactCreated(),
				dto.getContactLastEdited());
	}

	public long getContactId()
	{
		return contactId;
	}

	public void setContactId(long contactId)
	{
		this.contactId = contactId;
	}

	public Integer getContactVersion()
	{
		return contactVersion;
	}

	public void setContactVersion(Integer contactVersion)
	{
		this.contactVersion = contactVersion;
	}

	public long getIdentityId()
	{
		return identityId;
	}

	public void setIdentityId(long identityId)
	{
		this.identityId = identityId;
	}

	public boolean isDeactivated()
	{
		return deactivated;
	}

	public void setDeactivated(boolean deactivated)
	{
		this.deactivated = deactivated;
	}

	public Date getContactCreated()
	{
		return contactCreated;
	}

	public void setContactCreated(Date contactCreated)
	{
		this.contactCreated = contactCreated != null ? new Date(contactCreated.getTime()) : null;
	}

	public Date getContactLastEdited()
	{
		return contactLastEdited;
	}

	public void setContactLastEdited(Date contactLastEdited)
	{
		this.contactLastEdited = contactLastEdited != null ? new Date(contactLastEdited.getTime()) : null;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (contactCreated == null ? 0 : contactCreated.hashCode());
		result = prime * result + (int) (contactId ^ contactId >>> 32);
		result = prime * result + (contactLastEdited == null ? 0 : contactLastEdited.hashCode());
		result = prime * result + (contactVersion == null ? 0 : contactVersion.hashCode());
		result = prime * result + (deactivated ? 1231 : 1237);
		result = prime * result + (int) (identityId ^ identityId >>> 32);
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
		ContactOutDTO other = (ContactOutDTO) obj;
		if (contactCreated == null)
		{
			if (other.contactCreated != null)
			{
				return false;
			}
		}
		else if (!contactCreated.equals(other.contactCreated))
		{
			return false;
		}
		if (contactId != other.contactId)
		{
			return false;
		}
		if (contactLastEdited == null)
		{
			if (other.contactLastEdited != null)
			{
				return false;
			}
		}
		else if (!contactLastEdited.equals(other.contactLastEdited))
		{
			return false;
		}
		if (contactVersion == null)
		{
			if (other.contactVersion != null)
			{
				return false;
			}
		}
		else if (!contactVersion.equals(other.contactVersion))
		{
			return false;
		}
		if (deactivated != other.deactivated)
		{
			return false;
		}
		if (identityId != other.identityId)
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		return "ContactOutDTO [contactId=" + contactId + ", contactVersion=" + contactVersion + ", identityId=" + identityId + ", deactivated="
				+ deactivated + ", contactCreated=" + contactCreated + ", contactLastEdited=" + contactLastEdited + ", including " + super.toString()
				+ "]";
	}
}
