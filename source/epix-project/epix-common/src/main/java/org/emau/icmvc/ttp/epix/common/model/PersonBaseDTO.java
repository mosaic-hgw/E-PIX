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

/**
 *
 * @author geidell
 *
 */
public class PersonBaseDTO implements Serializable
{
	private static final long serialVersionUID = -5583294444292811905L;
	private long personId;
	private boolean deactivated;
	private Date personCreated;
	private Date personLastEdited;
	private IdentifierDTO mpiId;
	private String domainName;

	public PersonBaseDTO()
	{}

	public PersonBaseDTO(long personId, boolean deactivated, Date personCreated, Date personLastEdited, IdentifierDTO mpiId, String domainName)
	{
		this.personId = personId;
		this.deactivated = deactivated;
		setPersonCreated(personCreated);
		setPersonLastEdited(personLastEdited);
		setMpiId(mpiId);
		this.domainName = domainName;
	}

	public PersonBaseDTO(PersonBaseDTO dto)
	{
		this(dto.getPersonId(), dto.isDeactivated(), dto.getPersonCreated(), dto.getPersonLastEdited(), dto.getMpiId(), dto.getDomainName());
	}

	public long getPersonId()
	{
		return personId;
	}

	public void setPersonId(long personId)
	{
		this.personId = personId;
	}

	public boolean isDeactivated()
	{
		return deactivated;
	}

	public void setDeactivated(boolean deactivated)
	{
		this.deactivated = deactivated;
	}

	public Date getPersonCreated()
	{
		return personCreated;
	}

	public void setPersonCreated(Date personCreated)
	{
		this.personCreated = personCreated != null ? new Date(personCreated.getTime()) : null;
	}

	public Date getPersonLastEdited()
	{
		return personLastEdited;
	}

	public void setPersonLastEdited(Date personLastEdited)
	{
		this.personLastEdited = personLastEdited != null ? new Date(personLastEdited.getTime()) : null;
	}

	public IdentifierDTO getMpiId()
	{
		return mpiId;
	}

	public void setMpiId(IdentifierDTO mpiId)
	{
		this.mpiId = mpiId != null ? new IdentifierDTO(mpiId) : null;
	}

	public String getDomainName()
	{
		return domainName;
	}

	public void setDomainName(String domainName)
	{
		this.domainName = domainName;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (deactivated ? 1231 : 1237);
		result = prime * result + (domainName == null ? 0 : domainName.hashCode());
		result = prime * result + (mpiId == null ? 0 : mpiId.hashCode());
		result = prime * result + (personCreated == null ? 0 : personCreated.hashCode());
		result = prime * result + (int) (personId ^ personId >>> 32);
		result = prime * result + (personLastEdited == null ? 0 : personLastEdited.hashCode());
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
		PersonBaseDTO other = (PersonBaseDTO) obj;
		if (deactivated != other.deactivated)
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
		if (mpiId == null)
		{
			if (other.mpiId != null)
			{
				return false;
			}
		}
		else if (!mpiId.equals(other.mpiId))
		{
			return false;
		}
		if (personCreated == null)
		{
			if (other.personCreated != null)
			{
				return false;
			}
		}
		else if (!personCreated.equals(other.personCreated))
		{
			return false;
		}
		if (personId != other.personId)
		{
			return false;
		}
		if (personLastEdited == null)
		{
			if (other.personLastEdited != null)
			{
				return false;
			}
		}
		else if (!personLastEdited.equals(other.personLastEdited))
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		return "PersonBaseDTO [" + (domainName != null ? "domainName=" + domainName + ", " : "") + "personId=" + personId + ", deactivated=" + deactivated + ", personCreated=" + personCreated
				+ ", personLastEdited=" + personLastEdited + ", mpiId=" + mpiId + "]";
	}
}
