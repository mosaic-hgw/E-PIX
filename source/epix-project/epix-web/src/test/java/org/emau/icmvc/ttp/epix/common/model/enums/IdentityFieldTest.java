package org.emau.icmvc.ttp.epix.common.model.enums;

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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IdentityFieldTest
{

	@BeforeEach
	void setUp()
	{
	}

	@AfterEach
	void tearDown()
	{
	}

	@Test
	void toFieldName()
	{
		Set<FieldName> fields = new HashSet<>();

		Arrays.stream(IdentityField.values()).forEach(f ->
				{
					FieldName fieldName = f.toFieldName();
					if (fieldName != null)
					{
						assertSame(f, fieldName.toIdentityField());
					}
					fields.add(fieldName);
				}
		);

		assertEquals(FieldName.values().length + 1, fields.size());
	}

	@Test
	void toFieldNames()
	{
		IdentityField[] identityFields = IdentityField.values();
		FieldName[] fieldNames = IdentityField.toFieldNames(identityFields);

		assertEquals(FieldName.values().length + 6, fieldNames.length);

		for (int i = 0; i < identityFields.length; i++)
		{
			switch (identityFields[i])
			{
				case NONE:
				case PERSON_ID:
				case IDENTITY_ID:
				case IDENTITY_CREATED:
				case IDENTITY_LAST_EDITED:
				case SOURCE:
					identityFields[i] = null;
			}
		}

		assertArrayEquals(identityFields, FieldName.toIdentityFields(fieldNames));
	}
}