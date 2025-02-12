package org.emau.icmvc.ttp.test;

import org.emau.icmvc.ttp.deduplication.impl.validation.PhoneNumberValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*-
 * ###license-information-start###
 * E-PIX - Enterprise Patient Identifier Cross-referencing
 * __
 * Copyright (C) 2009 - 2025 Trusted Third Party of the University Medicine Greifswald
 * 							kontakt-ths@uni-greifswald.de
 * 
 * 							concept and implementation
 * 							l.geidel,c.schack, d.langner, g.koetzschke
 * 
 * 							web client
 * 							a.blumentritt, f.m. moser
 * 
 * 							docker
 * 							r.schuldt, f.m. moser
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

class ValidatorPhoneNumberTest
{
	@Test
	void testValidPhoneNumber()
	{
		for (String number : validPhoneNumbers)
		{
			assertTrue((new PhoneNumberValidator()).validate(number));
		}
	}

	@Test
	void testInvalidPhoneNumber()
	{
		PhoneNumberValidator pnv = new PhoneNumberValidator();

		assertFalse(pnv.validate("0873 376461a"));
		assertFalse(pnv.validate("03748 37682358,"));
		assertFalse(pnv.validate("O-)05444 347687-350"));
	}

	private static final String[] validPhoneNumbers = new String[]
	{
		"0873 376461", "03748 37682358", "05444 347687-350", "0764 812632-41",
		"0180 2 123456", "0900 1 123456", "+49 30 3432622-113", "0179 1111111",
		"(06442) 3933023", "(02852) 5996-0", "(042) 1818 87 9919", "06442 / 3893023",
		"06442 / 38 93 02 3", "06442/3839023", "042/ 88 17 890 0", "+49 221 549144 – 79",
		"+49 221 - 542194 79", "+49 (221) - 542944 79", "0 52 22 - 9 50 93 10",
		"+49(0)121-79536 - 77", "+49(0)2221-39938-113", "+49 (0) 1739 906-44",
		"+49 (173) 1799 806-44", "0173173990644", "0214154914479", "02141 54 91 44 79",
		"01517953677", "+491517953677", "015777953677", "02162 - 54 91 44 79",
		"(02162) 54 91 44 79"
	};
}
