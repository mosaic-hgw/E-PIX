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


import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.emau.icmvc.ttp.deduplication.impl.bloomfilter.BloomFilter;
import org.emau.icmvc.ttp.deduplication.impl.bloomfilter.DoubleHashingStrategy;
import org.emau.icmvc.ttp.deduplication.impl.validation.BalancedBloomFilterValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidatorBalancedBloomFilterTest
{
	private static final Logger logger = LogManager.getLogger(ValidatorBalancedBloomFilterTest.class);

	@Test
	void testValidBBF()
	{
		// 11110000000011111111000000001111
		assertTrue((new BalancedBloomFilterValidator(32)).validate("8A/wDw=="));

		// 11111111000000000000000011111111
		assertTrue((new BalancedBloomFilterValidator(32)).validate("/wAA/w=="));

		// 10101010101010
		assertTrue((new BalancedBloomFilterValidator(14)).validate("qqg="));

		// 1100110000110011
		assertTrue((new BalancedBloomFilterValidator(16)).validate("zDM="));
	}

	@Test
	void testMultipleValidBBF()
	{
		int LENGTH = 1000;

		String[][] values = {
				{ "HANS", "WURST" },
				{ "TORBEN", "SCHMIDT", "1999-07-04" },
				{ "FRANZ", "MUELLER" },
				{ "LOREEN", "MEIER", "1999-05-14", "F" },
				{ "ANNA-LENA", "SCHMITT", "1980-08-24", "F" } };

		for (String[] e : values)
		{
			DoubleHashingStrategy dhs = new DoubleHashingStrategy(LENGTH, 25, 2);
			BloomFilter bf = new BloomFilter(LENGTH, dhs);

			for (String val : e)
			{
				bf.add(val);
			}

			logger.info(Arrays.deepToString(e));
			assertTrue((new BalancedBloomFilterValidator(LENGTH * 2)).validate(bf.getBalancedBloomFilter(100).getAsEncodedString()));
		}
	}

	@Test
	void testInvalidBBF()
	{
		// 1111000000001111111100000000
		assertFalse((new BalancedBloomFilterValidator(28)).validate("8A/wAA=="));

		// 1111111100000000111111111111
		assertFalse((new BalancedBloomFilterValidator(28)).validate("/wD/8A=="));

		// 11111111000000001111
		assertFalse((new BalancedBloomFilterValidator(20)).validate("/wDw"));

		// 11111111000111111111111
		assertFalse((new BalancedBloomFilterValidator(23)).validate("/x/+"));
	}
}
