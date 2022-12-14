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
public class IdentifierDTO implements Serializable
{
	private static final long serialVersionUID = 890293339837441647L;
	private String value;
	private String description;
	private Date entryDate;
	private IdentifierDomainDTO identifierDomain;

	public IdentifierDTO()
	{
		super();
	}

	public IdentifierDTO(String value, String description, Date entryDate, IdentifierDomainDTO identifierDomain)
	{
		super();
		this.value = value;
		this.description = description;
		this.entryDate = entryDate;
		this.identifierDomain = identifierDomain;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public Date getEntryDate()
	{
		return entryDate;
	}

	public void setEntryDate(Date entryDate)
	{
		this.entryDate = entryDate;
	}

	public IdentifierDomainDTO getIdentifierDomain()
	{
		return identifierDomain;
	}

	public void setIdentifierDomain(IdentifierDomainDTO identifierDomain)
	{
		this.identifierDomain = identifierDomain;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((entryDate == null) ? 0 : entryDate.hashCode());
		result = prime * result + ((identifierDomain == null) ? 0 : identifierDomain.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		IdentifierDTO other = (IdentifierDTO) obj;
		if (description == null)
		{
			if (other.description != null)
				return false;
		}
		else if (!description.equals(other.description))
			return false;
		if (entryDate == null)
		{
			if (other.entryDate != null)
				return false;
		}
		else if (!entryDate.equals(other.entryDate))
			return false;
		if (identifierDomain == null)
		{
			if (other.identifierDomain != null)
				return false;
		}
		else if (!identifierDomain.equals(other.identifierDomain))
			return false;
		if (value == null)
		{
			if (other.value != null)
				return false;
		}
		else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "IdentifierDTO [value=" + value + ", description=" + description + ", entryDate=" + entryDate + ", identifierDomain="
				+ identifierDomain + "]";
	}

	public String toShortString()
	{
		return "IdentifierDTO [value=" + value + ", identifierDomain=" + identifierDomain + "]";
	}
}
