package org.emau.icmvc.ganimed.ttp.model;

/*
 * ###license-information-start###
 * E-PIX - Enterprise Patient Identifier Cross-referencing
 * __
 * Copyright (C) 2009 - 2017 The MOSAIC Project - Institut fuer Community Medicine der
 * 							Universitaetsmedizin Greifswald - mosaic-projekt@uni-greifswald.de
 * 							concept and implementation
 * 							c. schack, d.langner, l. geidel
 * 							web client
 * 							a. blumentritt
 * 							g. weiher
 * 							please cite our publications
 * 							http://dx.doi.org/10.3414/ME14-01-0133
 * 							http://dx.doi.org/10.1186/s12967-015-0545-6
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
public class Column {

	private String name;
	private Boolean active;
	
	public Column(String name) {
		this.name = name;
		this.active = true;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
	
	/**
	 * Get a list of all available column types
	 * @return list of column types
	 */
	public static List<String> getColumnTypes() {
		List<String> columnTypes = new ArrayList<String>();
		for (Type type : Type.values()) {
			columnTypes.add(type.toString());
		}
		return columnTypes;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((active == null) ? 0 : active.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Column other = (Column) obj;
		if (active == null) {
			if (other.active != null)
				return false;
		} else if (!active.equals(other.active))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	/**
	 * Column names for epix data
	 */
	public enum Type {
		Local_ID,
		Degree,
		Last_Name,
		Maiden_Name,
		Second_Name,
		First_Name,
		Birthdate,
		Birthplace,
		Gender,
		Nationality,
		Value1,
		Value2,
		Value3,
		Value4,
		Value5,
		Value6,
		Value7,
		Value8,
		Value9,
		Value10,
		Street_Number,
		ZIP,
		City,
		State,
		Country,
		Phone,
		E_Mail,
		Street,
		Number,
	};
}
