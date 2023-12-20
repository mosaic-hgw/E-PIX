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

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * 
 * @author geidell
 *
 */
@XmlJavaTypeAdapter(IdentityFieldAdapter.class)
public enum IdentityField
{
	// bei hinzufuegen org.emau.icmvc.ttp.epix.persistence.PaginatedHelper beachten
	NONE, IDENTITY_ID, PERSON_ID, FIRST_NAME, LAST_NAME, MIDDLE_NAME, PREFIX, SUFFIX, GENDER, BIRTH_DATE, BIRTHPLACE, RACE, RELIGION, MOTHERS_MAIDEN_NAME, DEGREE, MOTHER_TONGUE, NATIONALITY, CIVIL_STATUS, EXTERNAL_DATE, VALUE1, VALUE2, VALUE3, VALUE4, VALUE5, VALUE6, VALUE7, VALUE8, VALUE9, VALUE10, IDENTITY_CREATED, IDENTITY_LAST_EDITED, SOURCE, VITAL_STATUS, DATE_OF_DEATH;

	public FieldName toFieldName()
	{
		String name = Arrays.stream(name().split("_")).map((word) ->
				word.substring(0,1).toUpperCase() + word.substring(1).toLowerCase()).collect(Collectors.joining(""));

		name = name.substring(0,1).toLowerCase() + name.substring(1);

		if (name.equals("birthplace"))
		{
				name = "birthPlace";
		}

		try
		{
			return FieldName.valueOf(name);
		}
		catch (IllegalArgumentException e)
		{
			return null;
		}
	}

	public static List<FieldName> toFieldNames(List<IdentityField> fieldNames)
	{
		return fieldNames.stream().map(f -> f != null ? f.toFieldName() : null).collect(Collectors.toList());
	}

	public static FieldName[] toFieldNames(IdentityField[] fieldNames)
	{
		return toFieldNames(Arrays.asList(fieldNames)).toArray(FieldName[]::new);
	}
}
