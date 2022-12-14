package org.emau.icmvc.ttp.test;

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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.emau.icmvc.ttp.deduplication.impl.JaccardSimilarityAlgorithm;
import org.emau.icmvc.ttp.deduplication.impl.JaccardSimilarityAlgorithmCoded;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JaccardSimilarityAlgorithmTest
{
	private static final JaccardSimilarityAlgorithm jsa = new JaccardSimilarityAlgorithm();

	@Test
	public void testAllZerosEqualsWithSameLength()
	{
		String empty1 = "0000000000";
		String empty2 = "0000000000";

		assertEquals(1d, jsa.match(empty1, empty2), 0d, "testAllZerosEqualsWithSameLength - Strings are the same, but similarity is not 1");
	}

	@Test
	public void testEqualsWithSameLength()
	{
		String str1 = "0101110011";
		String str2 = "0101110011";

		assertEquals(1d, jsa.match(str1, str2), 0d, "testEqualsWithSameLength - Strings are the same, but similarity is not 1");
	}

	@Test
	public void testDifferentLength()
	{
		String str1 = "010111001101001";
		String str2 = "0101110011";

		assertEquals(0d, jsa.match(str1, str2), 0d, "testDifferentLength - Strings have different length, but similarity is not 0");
	}

	@Test
	public void testNullStrings()
	{
		String str1 = null;
		String str2 = null;

		assertEquals(0d, jsa.match(str1, str2), 0d, "testNullStrings - Strings are null, but similarity is not 0");
	}

	@Test
	public void testOtherSignsEqualWithSameLength()
	{
		String str1 = "A0B0C0D0E0";
		String str2 = "1010101010";

		assertEquals(1d, jsa.match(str1, str2), 0d, "testOtherSignsEqualWithSameLength - Strings are the same, but not 1");
	}

	@Test
	public void test50PercentSameLength()
	{
		String str1 = "1111110000";
		String str2 = "1110000000";

		assertEquals(0.5d, jsa.match(str1, str2), 0d, "test50PercentSameLength");
	}

	@Test
	public void testInvalidBase64()
	{
		String corr = "SGFsbG8=";
		String fail = ".SGFsbG8=";

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			JaccardSimilarityAlgorithmCoded jsac = new JaccardSimilarityAlgorithmCoded();
			jsac.match(corr, fail);
		});
	}
}
