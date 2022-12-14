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
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.emau.icmvc.ttp.deduplication.impl.JaccardSimilarityAlgorithm;
import org.emau.icmvc.ttp.deduplication.impl.JaccardSimilarityAlgorithmCoded;
import org.emau.icmvc.ttp.deduplication.impl.SorensenDiceCoefficient;
import org.emau.icmvc.ttp.deduplication.impl.SorensenDiceCoefficientCoded;
import org.emau.icmvc.ttp.deduplication.impl.bloomfilter.BloomFilter;
import org.emau.icmvc.ttp.deduplication.impl.bloomfilter.DoubleHashingStrategy;
import org.junit.jupiter.api.Test;

public class SimilarityTest
{
	private static final Logger logger = LogManager.getLogger(SimilarityTest.class);

	private static final SorensenDiceCoefficient sdc = new SorensenDiceCoefficient();
	private static final JaccardSimilarityAlgorithm jsa = new JaccardSimilarityAlgorithm();

	private static final SorensenDiceCoefficientCoded sdcc = new SorensenDiceCoefficientCoded();
	private static final JaccardSimilarityAlgorithmCoded jsac = new JaccardSimilarityAlgorithmCoded();

	@Test
	public void highSimilarity1()
	{
		String salt = "nO2202c0mI";

		DoubleHashingStrategy sbfs = new DoubleHashingStrategy(500, 15, 2);
		BloomFilter bf1 = new BloomFilter(500, sbfs);
		BloomFilter bf2 = new BloomFilter(500, sbfs);

		bf1.add("Hallo", salt);
		bf2.add("Hello", salt);

		logger.info("highSimilarity1 - SorensenDiceCoefficient: " + sdc.match(bf1.toString(), bf2.toString()));
		logger.info("highSimilarity1 - JaccardSimilarityAlgorithm: " + jsa.match(bf1.toString(), bf2.toString()));

		assertTrue(sdc.match(bf1.toString(), bf2.toString()) > 0.5, "SorensenDiceCoefficient: similarity under 0.5");
		assertTrue(jsa.match(bf1.toString(), bf2.toString()) > 0.5, "JaccardSimilarityAlgorithm: similarity under 0.5");
	}

	@Test
	public void highSimilarity2()
	{
		String salt = "sA0m8AXALs";

		DoubleHashingStrategy sbfs = new DoubleHashingStrategy(500, 15, 2);
		BloomFilter bf1 = new BloomFilter(500, sbfs);
		BloomFilter bf2 = new BloomFilter(500, sbfs);

		bf1.add("Janka", salt);
		bf1.add("Devinn", salt);
		bf1.add("25.05.1961", salt);
		bf1.add("W", salt);

		bf2.add("Janka", salt);
		bf2.add("Devin", salt);
		bf2.add("25.05.1961", salt);
		bf2.add("W", salt);

		logger.info("highSimilarity2 - SorensenDiceCoefficient: " + sdc.match(bf1.toString(), bf2.toString()));
		logger.info("highSimilarity2 - JaccardSimilarityAlgorithm: " + jsa.match(bf1.toString(), bf2.toString()));

		assertTrue(sdc.match(bf1.toString(), bf2.toString()) > 0.5, "SorensenDiceCoefficient: similarity under 0.5");
		assertTrue(jsa.match(bf1.toString(), bf2.toString()) > 0.5, "JaccardSimilarityAlgorithm: similarity under 0.5");
	}

	@Test
	public void OneCharDifferent()
	{
		String bloom1 = "0001001110110110100101000100100111010001000101010001000001110110010110001111001100110011001110101111010011100011001100110111101111000010010001010010000011000010110011010111010010011001001100101101011110101101101110110001100110110000101001100101010010";
		String bloom2 = "0001011010110110100101000101101111010001100101000001100000111110010110001101001100110010001110111111010011100111001100110111111011010010010001010010000011000110110011100111010010010001001010101101001110101101101110110001100011100000101100100101010010";

		logger.info("highSimilarity2 - SorensenDiceCoefficient: " + sdc.match(bloom1, bloom2));
		logger.info("highSimilarity2 - JaccardSimilarityAlgorithm: " + jsa.match(bloom1, bloom2));

		assertTrue(sdc.match(bloom1, bloom2) > 0.5, "SorensenDiceCoefficient: similarity under 0.5");
		assertTrue(jsa.match(bloom1, bloom2) > 0.5, "JaccardSimilarityAlgorithm: similarity under 0.5");
	}

	@Test
	void testSDCCodedEqualsOk()
	{
		String salt = "sA0m8AXALs";

		DoubleHashingStrategy sbfs = new DoubleHashingStrategy(500, 15, 2);
		BloomFilter bf1 = new BloomFilter(500, sbfs);
		BloomFilter bf2 = new BloomFilter(500, sbfs);

		bf1.add("Janka", salt);
		bf1.add("Devinn", salt);
		bf1.add("25.05.1961", salt);
		bf1.add("W", salt);

		bf2.add("Janka", salt);
		bf2.add("Devin", salt);
		bf2.add("25.05.1961", salt);
		bf2.add("W", salt);

		logger.info("testSDCCodedEqualsOk - SorensenDiceCoefficient: " + sdc.match(bf1.toString(), bf2.toString()));
		logger.info("testSDCCodedEqualsOk - SorensenDiceCoefficientCoded: " + sdcc.match(bf1.getAsEncodedString(), bf2.getAsEncodedString()));

		assertEquals(sdc.match(bf1.toString(), bf2.toString()), sdcc.match(bf1.getAsEncodedString(), bf2.getAsEncodedString()));
	}

	@Test
	void testJSACodedEqualsOk()
	{
		String salt = "sA0m8AXALs";

		DoubleHashingStrategy sbfs = new DoubleHashingStrategy(500, 15, 2);
		BloomFilter bf1 = new BloomFilter(500, sbfs);
		BloomFilter bf2 = new BloomFilter(500, sbfs);

		bf1.add("Janka", salt);
		bf1.add("Devinn", salt);
		bf1.add("25.05.1961", salt);
		bf1.add("W", salt);

		bf2.add("Janka", salt);
		bf2.add("Devin", salt);
		bf2.add("25.05.1961", salt);
		bf2.add("W", salt);

		logger.info("testJSACodedEqualsOk - JaccardSimilarityAlgorithm: " + jsa.match(bf1.toString(), bf2.toString()));
		logger.info("testJSACodedEqualsOk - JaccardSimilarityAlgorithmCoded: " + jsac.match(bf1.getAsEncodedString(), bf2.getAsEncodedString()));

		assertEquals(jsa.match(bf1.toString(), bf2.toString()), jsac.match(bf1.getAsEncodedString(), bf2.getAsEncodedString()));
	}

	@Test
	void testBalancedBloomFilterEquality()
	{
		String salt = "sA0m8AXALs";

		DoubleHashingStrategy sbfs = new DoubleHashingStrategy(500, 15, 2);
		BloomFilter bf1 = new BloomFilter(500, sbfs);
		BloomFilter bf2 = new BloomFilter(500, sbfs);

		bf1.add("Janka", salt);
		bf1.add("Devin", salt);
		bf1.add("25.05.1961", salt);
		bf1.add("W", salt);

		bf2.add("Janka", salt);
		bf2.add("Devin", salt);
		bf2.add("25.05.1961", salt);
		bf2.add("W", salt);


		assertEquals(1, sdc.match(bf1.getBalancedBloomFilter(12345).getAsEncodedString(), bf2.getBalancedBloomFilter(12345).getAsEncodedString()));
		assertEquals(1, sdcc.match(bf1.getBalancedBloomFilter(12345).getAsEncodedString(), bf2.getBalancedBloomFilter(12345).getAsEncodedString()));

		assertEquals(1, jsa.match(bf1.getBalancedBloomFilter(12345).getAsEncodedString(), bf2.getBalancedBloomFilter(12345).getAsEncodedString()));
		assertEquals(1, jsac.match(bf1.getBalancedBloomFilter(12345).getAsEncodedString(), bf2.getBalancedBloomFilter(12345).getAsEncodedString()));
	}

	@Test
	void testBalancedBloomFilterInequality()
	{
		String salt = "sA0m8AXALs";

		DoubleHashingStrategy sbfs = new DoubleHashingStrategy(500, 15, 2);
		BloomFilter bf1 = new BloomFilter(500, sbfs);
		BloomFilter bf2 = new BloomFilter(500, sbfs);

		bf1.add("Janka", salt);
		bf1.add("Devin", salt);
		bf1.add("25.05.1961", salt);
		bf1.add("W", salt);

		bf2.add("Jankaa", salt);
		bf2.add("Devinn", salt);
		bf2.add("25.05.1961", salt);
		bf2.add("W", salt);

		assertTrue(1 > sdc.match(bf1.getBalancedBloomFilter(12345).getAsEncodedString(), bf2.getBalancedBloomFilter(12345).getAsEncodedString()));
		assertTrue(1 > sdcc.match(bf1.getBalancedBloomFilter(12345).getAsEncodedString(), bf2.getBalancedBloomFilter(12345).getAsEncodedString()));

		assertTrue(1 > jsa.match(bf1.getBalancedBloomFilter(12345).getAsEncodedString(), bf2.getBalancedBloomFilter(12345).getAsEncodedString()));
		assertTrue(1 > jsac.match(bf1.getBalancedBloomFilter(12345).getAsEncodedString(), bf2.getBalancedBloomFilter(12345).getAsEncodedString()));
	}
}
