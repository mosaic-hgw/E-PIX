package org.emau.icmvc.ttp.deduplication.impl.bloomfilter;

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

import java.math.BigInteger;
import java.util.BitSet;
import java.util.List;

import org.emau.icmvc.ttp.deduplication.impl.hashing.HashFunctionMD5;
import org.emau.icmvc.ttp.deduplication.impl.hashing.HashFunctionSHA1;
import org.emau.icmvc.ttp.epix.common.deduplication.IBloomFilterStrategy;
import org.emau.icmvc.ttp.utils.NGramTransformer;

/**
 * Implementing a strategy for double hashing (see: 10.1186/1472-6947-9-41)
 *
 * @author Christopher Hampf
 */
public class DoubleHashingStrategy implements IBloomFilterStrategy
{
	/**
	 * Length of bloom filter
	 */
	private final int length;

	/**
	 * Number of positions per n-gram
	 */
	private final int iterations;

	/**
	 * Length of n-grams
	 */
	private final int nGramLength;

	/**
	 * Generate a double hashing strategy that encoded values by
	 *
	 * @param length      Length of the resulting bloom filter
	 * @param iterations  Number of positions per n-gram
	 * @param nGramLength Length of n-grams
	 * @throws IllegalArgumentException if length, iterations or nGramLength is lower than 1
	 */
	public DoubleHashingStrategy(int length, int iterations, int nGramLength)
	{
		if (length < 1)
			throw new IllegalArgumentException("Given length is not valid. Expected length is greater than 0.");

		if (iterations < 1)
			throw new IllegalArgumentException("Given number of iterations is not valid. Expected number of iterations is greater than 0.");

		if (nGramLength < 1)
			throw new IllegalArgumentException("Given nGramLength is not valid. Expected nGramLength is greater than 0.");

		this.length = length;
		this.iterations = iterations;
		this.nGramLength = nGramLength;
	}

	/**
	 * Returns a BitVector that has the given value encoded by double hashing.
	 *
	 * @param value Value that is encoded (if null, than empty string is used)
	 * @return BitSet with the encoded value
	 */
	@Override
	public BitSet getBitVector(String value)
	{
		return getBitVector(value, "");
	}

	/**
	 * Returns a BitVector that has the given value and salt encoded by double hashing.
	 *
	 * @param value Value that is encoded (if null, than empty string is used)
	 * @param salt  Salt that is added to each n-gram (if null, than no salt is used)
	 * @return BitSet with the encoded value
	 */
	@Override
	public BitSet getBitVector(String value, String salt)
	{
		String tmpVal = (value == null) ? "" : value;
		String tmpSalt = (salt == null) ? "" : salt;

		BitSet vector = new BitSet(length);

		List<String> ngrams = NGramTransformer.getNGrams(tmpVal, nGramLength);
		for (String ngram : ngrams)
		{
			for (int i = 0; i < iterations; ++i)
				vector.set(getHashedPosition(ngram, tmpSalt, i));
		}

		return vector;
	}

	/**
	 * Returns the length of the resulting bloom filter.
	 *
	 * @return length of bloom filter
	 */
	@Override public int getLength()
	{
		return length;
	}

	/**
	 * Calculate the position of a given value in bloom filter.
	 *
	 * @param value Value that is encoded to a specific position in bloom filter (example: value contains a n-gram)
	 * @param salt	Salt that is added to the value before it is encoded
	 * @param index Index to generate different positions, even the given value is equals to a previous iteration
	 * @return Position in bloom filter (position is set to one)
	 */
	protected int getHashedPosition(String value, String salt, int index)
	{
		byte[] hash1 = new HashFunctionMD5().hash(salt + value);
		byte[] hash2 = new HashFunctionSHA1().hash(salt + value);

		BigInteger h1 = new BigInteger(1, hash1);
		BigInteger h2 = new BigInteger(1, hash2);

		BigInteger bIndex = new BigInteger(Integer.toString(index));
		BigInteger bLength = new BigInteger(Integer.toString(length));

		// gi(x) = (h1(x) + i * h2(x)) mod l
		return h1.add(bIndex.multiply(h2)).mod(bLength).intValue();
	}
}
