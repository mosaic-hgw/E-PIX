package org.emau.icmvc.ttp.test;

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

import java.util.HashMap;
import java.util.Map;

import org.emau.icmvc.ttp.deduplication.impl.bloomfilter.BloomFilter;
import org.emau.icmvc.ttp.deduplication.impl.bloomfilter.RandomHashingStrategy;
import org.emau.icmvc.ttp.deduplication.preprocessing.impl.ToUpperCaseTransformation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class RandomHashingTest
{
	private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

	private static final ToUpperCaseTransformation UTCT = new ToUpperCaseTransformation();

	@Test
	void testDifferentSeeds()
	{
		Map<String, Long> attr1 = new HashMap<>();
		attr1.put("firstname", 12345L);
		attr1.put("lastname", 67890L);

		Map<String, Long> attr2 = new HashMap<>();
		attr2.put("firstname", 67890L);
		attr2.put("lastname", 12345L);

		RandomHashingStrategy rhs1 = new RandomHashingStrategy(500, 15, 2, ALPHABET, attr1);
		RandomHashingStrategy rhs2 = new RandomHashingStrategy(500, 15, 2, ALPHABET, attr2);

		BloomFilter bf1 = new BloomFilter(500, rhs1);
		BloomFilter bf2 = new BloomFilter(500, rhs2);

		bf1.add(UTCT.transform("Janka"), "firstname");
		bf1.add(UTCT.transform("Devin"), "lastname");

		bf2.add(UTCT.transform("Janka"), "firstname");
		bf2.add(UTCT.transform("Devin"), "lastname");

		assertNotEquals(bf2, bf1);
	}

	@Test
	void testSameInputsWithSameSeeds()
	{
		Map<String, Long> attr1 = new HashMap<>();
		attr1.put("firstname", 12345L);
		attr1.put("lastname", 67890L);

		Map<String, Long> attr2 = new HashMap<>();
		attr2.put("firstname", 12345L);
		attr2.put("lastname", 67890L);

		RandomHashingStrategy rhs1 = new RandomHashingStrategy(500, 15, 2, ALPHABET, attr1);
		RandomHashingStrategy rhs2 = new RandomHashingStrategy(500, 15, 2, ALPHABET, attr2);

		BloomFilter bf1 = new BloomFilter(500, rhs1);
		BloomFilter bf2 = new BloomFilter(500, rhs2);

		bf1.add(UTCT.transform("Janka"), "firstname");
		bf1.add(UTCT.transform("Devin"), "lastname");

		bf2.add(UTCT.transform("Janka"), "firstname");
		bf2.add(UTCT.transform("Devin"), "lastname");

		assertEquals(bf2, bf1);
	}
}
