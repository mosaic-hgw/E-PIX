package org.emau.icmvc.ttp.deduplication.config.model;

/*
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

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.emau.icmvc.ttp.epix.common.model.config.ReasonDTO;

/**
 * @author Christopher Hampf
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Reason", propOrder = { "name", "description" })
public class Reason
{
	@Enumerated(EnumType.STRING)
	@XmlElement(name = "name", required = true, defaultValue = "")
	private String name;

	@Enumerated(EnumType.STRING)
	@XmlElement(name = "description", defaultValue = "")
	private String description;

	public Reason()
	{
		super();
	}

	public Reason(ReasonDTO dto)
	{
		super();

		this.name = dto.getName();
		this.description = dto.getDescription();
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public ReasonDTO toDTO()
	{
		return new ReasonDTO(name, description);
	}

	@Override public boolean equals(Object obj)
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
		Reason other = (Reason) obj;
		if (name == null)
		{
			if (other.name != null)
			{
				return false;
			}
		}
		else if (!name.equals(other.name))
		{
			return false;
		}
		if (description == null)
		{
			return other.description == null;
		}
		else
			return description.equals(other.description);
	}

	@Override public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (name == null ? 0 : name.hashCode());
		result = prime * result + (description == null ? 0 : description.hashCode());
		return result;
	}

	@Override public String toString()
	{
		return "Reason [" +
				"name='" + name + '\'' +
				", description='" + description + '\'' +
				']';
	}
}
