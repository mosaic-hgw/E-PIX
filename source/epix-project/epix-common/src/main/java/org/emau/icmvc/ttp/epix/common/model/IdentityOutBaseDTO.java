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

import java.util.Date;

/**
 *
 * @author geidell
 *
 */
public class IdentityOutBaseDTO extends IdentityInBaseDTO
{
	private static final long serialVersionUID = 5379737596793202278L;
	private long identityId;
	private Integer identityVersion;
	private long personId;
	private SourceDTO source;
	private boolean deactivated;
	private Date identityCreated;
	private Date identityLastEdited;

	public IdentityOutBaseDTO()
	{
		super();
	}

	public IdentityOutBaseDTO(IdentityInBaseDTO superDTO, long identityId, Integer identityVersion, long personId, SourceDTO source,
			boolean deactivated, Date identityCreated, Date identityLastEdited)
	{
		super(superDTO);
		this.identityId = identityId;
		this.identityVersion = identityVersion;
		this.personId = personId;
		this.source = source;
		this.deactivated = deactivated;
		this.identityCreated = identityCreated;
		this.identityLastEdited = identityLastEdited;
	}

	public IdentityOutBaseDTO(IdentityOutBaseDTO dto)
	{
		this(dto, dto.getIdentityId(), dto.getIdentityVersion(), dto.getPersonId(), dto.getSource(), dto.isDeactivated(), dto.getIdentityCreated(),
				dto.getIdentityLastEdited());
	}

	public long getIdentityId()
	{
		return identityId;
	}

	public void setIdentityId(long identityId)
	{
		this.identityId = identityId;
	}

	public Integer getIdentityVersion()
	{
		return identityVersion;
	}

	public void setIdentityVersion(Integer identityVersion)
	{
		this.identityVersion = identityVersion;
	}

	public long getPersonId()
	{
		return personId;
	}

	public void setPersonId(long personId)
	{
		this.personId = personId;
	}

	public SourceDTO getSource()
	{
		return source;
	}

	public void setSource(SourceDTO source)
	{
		this.source = source;
	}

	public boolean isDeactivated()
	{
		return deactivated;
	}

	public void setDeactivated(boolean deactivated)
	{
		this.deactivated = deactivated;
	}

	public Date getIdentityCreated()
	{
		return identityCreated;
	}

	public void setIdentityCreated(Date identityCreated)
	{
		this.identityCreated = identityCreated;
	}

	public Date getIdentityLastEdited()
	{
		return identityLastEdited;
	}

	public void setIdentityLastEdited(Date identityLastEdited)
	{
		this.identityLastEdited = identityLastEdited;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (int) (identityId ^ identityId >>> 32);
		result = prime * result + (identityVersion == null ? 0 : identityVersion.hashCode());
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
		IdentityOutBaseDTO other = (IdentityOutBaseDTO) obj;
		if (identityId != other.identityId)
		{
			return false;
		}
		if (identityVersion == null)
		{
			if (other.identityVersion != null)
			{
				return false;
			}
		}
		else if (!identityVersion.equals(other.identityVersion))
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		return "IdentityOutBaseDTO [identityId=" + identityId + ", identityVersion=" + identityVersion + ", personId=" + personId + ", source="
				+ source + ", deactivated=" + deactivated + ", identityCreated=" + identityCreated + ", identityLastEdited=" + identityLastEdited
				+ ", including " + super.toString() + "]]";
	}
}
