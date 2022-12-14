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

import java.util.Arrays;
import java.util.List;

import org.emau.icmvc.ttp.utils.NGramTransformer;
import org.junit.jupiter.api.Test;

public class NGramTransformerTest
{
	private static final String TESTSTRING = "ANNA";

	@Test
	public void testCorrectNumberOfBigrams()
	{
		int correctSize = TESTSTRING.length() + 1;

		assertEquals(correctSize, NGramTransformer.getNGrams(TESTSTRING, 2).size(), "testCorrectNumberOfBigrams");
	}

	@Test
	public void testCorrectNumberOfTrigrams()
	{
		int correctSize = TESTSTRING.length() + 1;

		assertEquals(correctSize, NGramTransformer.getNGrams(TESTSTRING, 3).size(), "testCorrectNumberOfTrigrams");
	}

	@Test
	public void testCorrectBigrams()
	{
		List<String> bigrams = Arrays.asList(" A", "AN", "NN", "NA", "A ");

		assertEquals(bigrams, NGramTransformer.getNGrams(TESTSTRING, 2), "testCorrectNumberOfTrigrams");
	}

	@Test
	public void testCorrectTrigrams()
	{
		List<String> trigrams = Arrays.asList("  A", " AN", "ANN", "NNA", "NA ");

		assertEquals(trigrams, NGramTransformer.getNGrams(TESTSTRING, 3), "testCorrectNumberOfTrigrams");
	}
}
