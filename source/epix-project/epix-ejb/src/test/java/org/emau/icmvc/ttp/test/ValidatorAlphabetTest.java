package org.emau.icmvc.ttp.test;

import org.emau.icmvc.ttp.deduplication.impl.validation.AlphabetValidator;
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

class ValidatorAlphabetTest
{
	@Test
	void testAllowedAlphabetSingleChar()
	{
		AlphabetValidator val1 = new AlphabetValidator("A");
		AlphabetValidator val2 = new AlphabetValidator("a");
		AlphabetValidator val3 = new AlphabetValidator("4");
		AlphabetValidator val4 = new AlphabetValidator("9");
		AlphabetValidator val5 = new AlphabetValidator("#");
		AlphabetValidator val6 = new AlphabetValidator("ä");

		assertTrue(val1.validate("A"));
		assertTrue(val2.validate("a"));
		assertTrue(val3.validate("4"));
		assertTrue(val4.validate("9"));
		assertTrue(val5.validate("#"));
		assertTrue(val6.validate("ä"));
	}

	@Test
	void testAllowedAlphabetMultipleChars()
	{
		AlphabetValidator val = new AlphabetValidator("ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890");

		for (String value : validValues)
		{
			assertTrue(val.validate(value));
		}
	}

	@Test
	void testNull()
	{
		AlphabetValidator val1 = new AlphabetValidator("A");
		AlphabetValidator val2 = new AlphabetValidator("");

		assertFalse(val1.validate(null));
		assertTrue(val2.validate(null));
	}

	@Test
	void testNotAllowedAlphabetSingleChar()
	{
		AlphabetValidator val1 = new AlphabetValidator("A");
		AlphabetValidator val2 = new AlphabetValidator("a");
		AlphabetValidator val3 = new AlphabetValidator("4");
		AlphabetValidator val4 = new AlphabetValidator("9");
		AlphabetValidator val5 = new AlphabetValidator("#");
		AlphabetValidator val6 = new AlphabetValidator("ä");

		assertFalse(val1.validate("B"));
		assertFalse(val2.validate("b"));
		assertFalse(val3.validate("5"));
		assertFalse(val4.validate("0"));
		assertFalse(val5.validate("_"));
		assertFalse(val6.validate("Ä"));
	}
	@Test
	void testNotAllowedAlphabetMultipleChars()
	{
		AlphabetValidator val = new AlphabetValidator("13579");

		for (String value : invalidValues)
		{
			assertFalse(val.validate(value));
		}
	}

	@Test
	void testEmptyString()
	{
		AlphabetValidator notEmpty = new AlphabetValidator("12345");
		AlphabetValidator empty = new AlphabetValidator("");

		assertFalse(notEmpty.validate(null));
		assertFalse(notEmpty.validate(""));

		assertTrue(empty.validate(null));
		assertTrue(empty.validate(""));


	}

	private static final String[] validValues = new String[]
	{
		"ABC", "123", "A1", "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "123ABC", "1A2B3C"
	};

	private static final String[] invalidValues = new String[]
	{
		"222", "ABC", "1352", "789", "ACD", "1A", "13579B", "abc"
	};
}
