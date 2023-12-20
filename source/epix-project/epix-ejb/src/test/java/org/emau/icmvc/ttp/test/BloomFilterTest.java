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

import java.util.BitSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.emau.icmvc.ttp.deduplication.impl.bloomfilter.BloomFilter;
import org.emau.icmvc.ttp.deduplication.impl.bloomfilter.DoubleHashingStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BloomFilterTest
{
	private static final Logger logger = LogManager.getLogger(BloomFilterTest.class);

	@Test
	public void testIsMemberTrueOk()
	{
		DoubleHashingStrategy dhs = new DoubleHashingStrategy(100, 15, 2);
		BloomFilter bf = new BloomFilter(500, dhs);

		final String testStr = "Hallo Welt";

		bf.add(testStr);
		assertTrue(bf.isMember("Hallo Welt"));
	}

	@Test
	public void testIsNotMemberOk()
	{
		DoubleHashingStrategy dhs = new DoubleHashingStrategy(100, 15, 2);
		BloomFilter bf = new BloomFilter(500, dhs);

		final String testStr = "Hallo Welt";

		bf.add(testStr);
		assertFalse(bf.isMember("Hello World"));
	}

	@Test
	public void testFoldOk1()
	{
		BitSet toFold = new BitSet(16);
		toFold.set(0);
		toFold.set(1);
		toFold.set(4);
		toFold.set(6);
		toFold.set(7);
		toFold.set(9);
		toFold.set(12);
		toFold.set(15);

		BitSet correctResult = new BitSet(4);
		correctResult.set(0);
		correctResult.set(2);

		BloomFilter in = new BloomFilter(toFold, 16, null);
		BloomFilter correct = new BloomFilter(correctResult, 4, null);

		assertEquals(correct, in.fold(3));
	}

	@Test
	public void testFoldOk2()
	{
		BitSet toFold = new BitSet(8);
		toFold.set(4);
		toFold.set(5);
		toFold.set(6);
		toFold.set(7);
		//01234567
		//00001111

		BitSet correctResult = new BitSet(4);
		correctResult.set(0);
		correctResult.set(1);
		correctResult.set(2);
		correctResult.set(3);

		BloomFilter in = new BloomFilter(toFold, 8, null);
		BloomFilter correct = new BloomFilter(correctResult, 4, null);

		assertEquals(correct, in.fold(1));
	}

	@Test
	public void testFoldOk3()
	{
		BitSet toFold = new BitSet(8);
		toFold.set(0);
		toFold.set(1);
		toFold.set(2);
		toFold.set(3);
		toFold.set(4);
		toFold.set(5);
		toFold.set(6);
		toFold.set(7);

		BitSet correctResult = new BitSet(4);

		BloomFilter in = new BloomFilter(toFold, 8, null);
		BloomFilter correct = new BloomFilter(correctResult, 4, null);

		assertEquals(correct, in.fold(1));
	}

	@Test
	public void testFoldOk4FoldOneAndTwiceAreEqual()
	{
		BitSet toFold = new BitSet(16);
		toFold.set(0);
		toFold.set(1);
		toFold.set(4);
		toFold.set(6);
		toFold.set(7);
		toFold.set(9);
		toFold.set(12);
		toFold.set(15);

		BitSet correctResult = new BitSet(4);
		correctResult.set(0);
		correctResult.set(2);

		BloomFilter foldOnce = new BloomFilter(toFold, 16, null);
		foldOnce = foldOnce.fold(3);

		BloomFilter foldTwice = new BloomFilter(toFold, 16, null);
		foldTwice = foldTwice.fold(1);
		foldTwice = foldTwice.fold(1);

		BloomFilter correct = new BloomFilter(correctResult, 4, null);

		assertEquals(correct, foldOnce);
		assertEquals(correct, foldTwice);
	}

	@Test
	public void testConvertFromStringToBitSetToStringOk()
	{
		String bfVector = "1111000011001110001010100101";
		BloomFilter bf = new BloomFilter(bfVector, null);

		assertEquals(bfVector, bf.toString());
	}

	@Test
	public void testMultipleValuesAreOKAllValuesAreAdded()
	{
		DoubleHashingStrategy sbfs = new DoubleHashingStrategy(100, 15, 2);
		BloomFilter bf = new BloomFilter(500, sbfs);

		final String testStr1 = "Hallo Welt";
		final String testStr2 = "Bye Bye";
		final String testStr3 = "123456789";
		final String testStr4 = "ABCDEFGXYZ";

		bf.add(testStr1);
		bf.add(testStr2);
		bf.add(testStr3);
		bf.add(testStr4);

		assertTrue(bf.isMember(testStr1));
		assertTrue(bf.isMember(testStr2));
		assertTrue(bf.isMember(testStr3));
		assertTrue(bf.isMember(testStr4));
	}

	@Test
	public void testMultipleValuesAreOKOneValueIsAdded()
	{
		DoubleHashingStrategy dhs = new DoubleHashingStrategy(100, 15, 2);
		BloomFilter bf = new BloomFilter(500, dhs);

		final String testStr1 = "Hallo Welt";
		final String testStr2 = "Bye Bye";
		final String testStr3 = "123456789";
		final String testStr4 = "ABCDEFGXYZ";

		bf.add(testStr1);

		assertTrue(bf.isMember(testStr1));
		assertFalse(bf.isMember(testStr2));
		assertFalse(bf.isMember(testStr3));
		assertFalse(bf.isMember(testStr4));
	}

	@Test
	public void testMultipleValuesAreOKNoneValueIsAdded()
	{
		DoubleHashingStrategy dhs = new DoubleHashingStrategy(100, 15, 2);
		BloomFilter bf = new BloomFilter(500, dhs);

		final String testStr1 = "Hallo Welt";
		final String testStr2 = "Bye Bye";
		final String testStr3 = "123456789";
		final String testStr4 = "ABCDEFGXYZ";

		assertFalse(bf.isMember(testStr1));
		assertFalse(bf.isMember(testStr2));
		assertFalse(bf.isMember(testStr3));
		assertFalse(bf.isMember(testStr4));
	}

	@Test
	public void testSpecificValuesFoldWithoutError()
	{
		DoubleHashingStrategy dhs = new DoubleHashingStrategy(500, 15, 2);
		BloomFilter bf = new BloomFilter(500, dhs);

		final String firstName = "Maximilian";
		final String lastName = "Mustermann";
		final String birthData = "1985-05-21";
		final String gender = "M";

		final String salt = "ah36K340F3#x3790";

		bf.add(firstName, salt);
		bf.add(lastName, salt);
		bf.add(birthData, salt);
		bf.add(gender, salt);

		assertNotEquals("", bf.toString());
	}

	@Test
	void testBalancedFilterCorrectNumberOfSets()
	{
		DoubleHashingStrategy dhs = new DoubleHashingStrategy(500, 15, 2);
		BloomFilter bf = new BloomFilter(500, dhs);

		final String firstName = "Maximilian";
		final String lastName = "Mustermann";
		final String birthData = "1985-05-21";
		final String gender = "M";

		final String salt = "ah36K340F3#x3790";

		bf.add(firstName, salt);
		bf.add(lastName, salt);
		bf.add(birthData, salt);
		bf.add(gender, salt);

		int num = 100000;
		logger.info("Generate " + num + " balanced bloom filters by various seeds and check set ones");
		for (int i = 0; i < num; ++i)
		{
			BloomFilter balanced = bf.getBalancedBloomFilter((long)(12345 * i * Math.PI));

			String tmp = balanced.toString();
			int diff = 0;
			for (char c : tmp.toCharArray())
				diff = (c == '1') ? diff + 1 : diff - 1;

			assertEquals(0, diff);
		}
	}
}
