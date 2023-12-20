package org.emau.icmvc.ttp.deduplication.config.model;

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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.emau.icmvc.ttp.epix.common.model.enums.FieldName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequiredFields", propOrder = { "names" })
public class RequiredFields
{
	@XmlElement(name = "name", required = true)
	private final List<FieldName> names = new ArrayList<>();

	public RequiredFields()
	{}

	public RequiredFields(List<FieldName> fieldNames)
	{
		setNames(fieldNames);
	}

	public List<FieldName> getNames()
	{
		return names;
	}

	public void setNames(List<FieldName> names)
	{
		this.names.clear();
		if (names != null)
		{
			this.names.addAll(names);
		}
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (names == null ? 0 : names.hashCode());
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
		RequiredFields other = (RequiredFields) obj;
		if (names == null)
		{
			if (other.names != null)
			{
				return false;
			}
		}
		else if (!names.equals(other.names))
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		return "RequiredConfig [names=" + names + "]";
	}
}
