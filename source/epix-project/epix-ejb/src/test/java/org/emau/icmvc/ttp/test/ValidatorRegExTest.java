package org.emau.icmvc.ttp.test;

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

import org.emau.icmvc.ttp.deduplication.impl.validation.RegExValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidatorRegExTest
{
	@Test
	void testRegEx()
	{
		RegExValidator rev1 = new RegExValidator("a*b+");
		RegExValidator rev2 = new RegExValidator("[a-z0-9][a-z0-9]*");

		assertTrue(rev1.validate("aaaab"));
		assertFalse(rev1.validate("aaa"));
		assertTrue(rev1.validate("ab"));
		assertFalse(rev2.validate(""));
		assertTrue(rev2.validate("1zs749"));
		assertTrue(rev2.validate("fju65"));
		assertTrue(rev2.validate("456789a"));
	}

	@Test
	void testNullRegEx()
	{
		RegExValidator rev1 = new RegExValidator("a*b+");
		assertFalse(rev1.validate(null));

		RegExValidator rev2 = new RegExValidator("a*");
		assertTrue(rev2.validate(null));
		assertTrue(rev2.validate("a"));

		RegExValidator rev3 = new RegExValidator("^$");
		assertTrue(rev3.validate(null));
		assertTrue(rev3.validate(""));
	}
}
