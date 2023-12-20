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

import java.util.Objects;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.emau.icmvc.ttp.epix.common.model.config.SourceFieldDTO;
import org.emau.icmvc.ttp.epix.common.model.enums.FieldName;

/**
 * @author Christopher Hampf
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SourceField", propOrder = { "name", "saltValue", "saltField", "seed" })
public class SourceField
{
	@Enumerated(EnumType.STRING)
	@XmlElement(name = "name", required = false, defaultValue = "")
	private FieldName name;

	@XmlElement(name = "salt-value", required = false, defaultValue = "")
	private String saltValue = "";

	@Enumerated(EnumType.STRING)
	@XmlElement(name = "salt-field", required = false, defaultValue = "")
	private FieldName saltField;

	@XmlElement(name = "seed", required = false, defaultValue = "0")
	private long seed = 0L;

	public FieldName getName()
	{
		return name;
	}

	public void setName(FieldName name)
	{
		this.name = name;
	}

	public String getSaltValue()
	{
		return saltValue;
	}

	public void setSaltValue(String saltValue)
	{
		this.saltValue = saltValue;
	}

	public FieldName getSaltField()
	{
		return saltField;
	}

	public void setSaltField(FieldName saltField)
	{
		this.saltField = saltField;
	}

	public SourceFieldDTO toDTO()
	{
		return new SourceFieldDTO(name, saltValue, saltField, seed);
	}

	public long getSeed()
	{
		return seed;
	}

	public void setSeed(long seed)
	{
		this.seed = seed;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (name == null ? 0 : name.hashCode());
		result = prime * result + (saltValue == null ? 0 : saltValue.hashCode());
		result = prime * result + (saltField == null ? 0 : saltField.hashCode());
		result = prime * result + Objects.hash(seed);
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
		SourceField other = (SourceField) obj;
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
		if (saltValue == null)
		{
			if (other.saltValue != null)
			{
				return false;
			}
		}
		else if (!saltValue.equals(other.saltValue))
		{
			return false;
		}
		if (saltField == null)
		{
			if (other.saltField != null)
			{
				return false;
			}
		}
		else if (!saltField.equals(other.saltField))
		{
			return false;
		}
		return seed == other.seed;
	}

	@Override
	public String toString()
	{
		return "SourceField [name=" + name + ", saltValue=" + saltValue + ", saltField=" + saltField + "]";
	}
}
