package org.emau.icmvc.ttp.epix.frontend.model;

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

/**
 * Represents a column for importing data
 *
 * @author blumentritta
 */
public class Column
{
	private String name;
	private Boolean active;

	public Column(String name)
	{
		this.name = name;
		active = true;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Boolean getActive()
	{
		return active;
	}

	public void setActive(Boolean active)
	{
		this.active = active;
	}

	/**
	 * Get a list of all available column types for import
	 *
	 * @return list of column types
	 */
	public static List<String> getColumnTypesForImport()
	{
		List<String> columnTypes = new ArrayList<String>();
		for (Type type : Type.values())
		{
			if (type.equals(Type.unknown))
			{
				continue;
			}
			columnTypes.add(type.toString());
		}
		return columnTypes;
	}

	/**
	 * Get a list of all available column types for export
	 *
	 * @return list of column types
	 */
	public static List<String> getColumnTypesForExport()
	{
		List<String> columnTypes = new ArrayList<String>();
		for (Type type : Type.values())
		{
			if (type.equals(Type.MPI) || type.equals(Type.localId) || type.equals(Type.unknown))
			{
				continue;
			}
			columnTypes.add(type.toString());
		}
		return columnTypes;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((active == null) ? 0 : active.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Column other = (Column) obj;
		if (active == null)
		{
			if (other.active != null)
			{
				return false;
			}
		}
		else if (!active.equals(other.active))
		{
			return false;
		}
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
		return true;
	}

	/**
	 * Column names for epix data
	 */
	public enum Type
	{
		MPI, localId, degree, lastName, mothersMaidenName, middleName, firstName, birthDate, birthPlace, gender, nationality, motherTongue, civilStatus, race, religion, prefix, suffix, externalDate, value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, street, zipCode, city, state, country, countryCode, district, municipalityKey, phone, email, streetOnly, number, contactExternalDate, unknown
	};
}
