package org.emau.icmvc.ttp.epix.common.model.enums;

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


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * 
 * @author geidell
 *
 */
@XmlType(name = "fieldName")
@XmlEnum
@XmlJavaTypeAdapter(FieldNameAdapter.class)
public enum FieldName
{
	firstName, middleName, lastName, prefix, suffix, birthDate, gender, birthPlace, race, religion, mothersMaidenName, degree, motherTongue, nationality, civilStatus, externalDate, value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, vitalStatus, dateOfDeath;

	public String value()
	{
		return name();
	}

	public static FieldName fromValue(String v)
	{
		return valueOf(v);
	}

	public IdentityField toIdentityField()
	{
		String name = String.join("_", name().split("(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])")).toUpperCase();

		if (name.equals("BIRTH_PLACE"))
		{
				name = "BIRTHPLACE";
		}
		return IdentityField.valueOf(name);
	}

	public static List<IdentityField> toIdentityFields(List<FieldName> fieldNames)
	{
		return fieldNames.stream().map(f -> f != null ? f.toIdentityField() : null).collect(Collectors.toList());
	}

	public static IdentityField[] toIdentityFields(FieldName[] fieldNames)
	{
		return toIdentityFields(Arrays.asList(fieldNames)).toArray(IdentityField[]::new);
	}
}
