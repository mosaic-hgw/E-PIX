package org.emau.icmvc.ttp.deduplication.impl.bloomfilter;

/*-
 * ###license-information-start###
 * E-PIX - Enterprise Patient Identifier
 * 							Cross-referencing
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

import java.util.Base64;
import java.util.BitSet;
import java.util.Random;

import org.emau.icmvc.ttp.epix.common.deduplication.IBloomFilterStrategy;

/**
 * @author Christopher Hampf
 */
public class BloomFilter
{
	/**
	 * Bit Vector
	 */
	private BitSet vector;

	/**
	 * Length of Vector (length() of BitSet returns only the number of last true bit)
	 */
	private final int filterLength;

	/**
	 * Strategy to calculate bit positions
	 */
	private final IBloomFilterStrategy strategy;

	/**
	 * Creates an empty bloom filter.
	 *
	 * @param filterLength Length of bloom filter
	 * @param strategy     Method to calculate bit positions in bloom filter
	 */
	public BloomFilter(int filterLength, IBloomFilterStrategy strategy)
	{
		if (filterLength < 1)
			throw new IllegalArgumentException("filterLength smaller than 1 is not valid");

		this.filterLength = filterLength;
		vector = new BitSet(filterLength);
		this.strategy = strategy;
	}

	/**
	 * Creates a bloom filter with a given bit vector.
	 *
	 * @param vector       Vector of set bits
	 * @param filterLength Length of bloom filter
	 * @param strategy     Method to calculate bit positions in bloom filter
	 */
	public BloomFilter(BitSet vector, int filterLength, IBloomFilterStrategy strategy)
	{
		this(filterLength, strategy);

		this.vector = vector;
	}

	/**
	 * Creates a bloom filter with a given string representing the bit vector.
	 *
	 * @param vector   String with '1' and '0' as bit vector
	 * @param strategy Method to calculate bit positions in bloom filter
	 */
	public BloomFilter(String vector, IBloomFilterStrategy strategy)
	{
		this(vector.length(), strategy);

		this.vector = stringToBitSet(vector);
	}

	/**
	 * Adds the calculated bit positions of the given value to the bit vector. If no strategy is initiated,
	 * then nothing happens.
	 *
	 * @param value Value to be added
	 */
	public void add(String value)
	{
		add(value, "");
	}

	/**
	 * Adds the calculated bit positions of the given value with salt to the bit vector. If no strategy is initiated,
	 * then nothing happens.
	 *
	 * @param value Value to be added
	 * @param salt  Salt which is added to {@value}
	 */
	public void add(String value, String salt)
	{
		if (strategy != null)
			vector.or(strategy.getBitVector(value, salt));
	}

	/**
	 * Checks whether the given value is likely to be contained in the bloom filter. If no strategy is initiated,
	 * then the result is always false.
	 *
	 * @param value Checked value
	 * @return true, if the given value is likely to be contained in the bloom filter or false, if it is definitely not.
	 */
	public boolean isMember(String value)
	{
		return isMember(value, "");
	}

	/**
	 * Checks whether the given value with salt is likely to be contained in the bloom filter. If no strategy is initiated,
	 * then the result is always false.
	 *
	 * @param value Checked value
	 * @param salt  Salt which is added to {@value}
	 * @return true, if the given value is likely to be contained in the bloom filter or false, if it is definitely not.
	 */
	public boolean isMember(String value, String salt)
	{
		if (strategy == null)
			return false;
		else
		{
			BitSet searched = strategy.getBitVector(value, salt);

			boolean result = true;
			for (int i = 0; i < filterLength; ++i)
			{
				if (searched.get(i) && !vector.get(i))
				{
					result = false;
					break;
				}
			}

			return result;
		}
	}

	/**
	 * Returns a folded bloom filter.
	 * See "XOR-Folding for hardening Bloom Filter-based Encryptions for Privacy-preserving Record Linkage"
	 * of R. Schnell and C. Borgs for details.
	 *
	 * @param numberOfFolds Number of bloom filter folds
	 * @return Folded bloom filter.
	 * @throws IllegalArgumentException If the given length cannot be divided by the filter length without remainder.
	 */
	public BloomFilter fold(int numberOfFolds)
	{
		++numberOfFolds; // 1 fold results to 2 parts of filter; 3 folds results in 4 quarter of filter and so on... (we want the number of resulted parts => +1)
		if (filterLength % numberOfFolds != 0)
			throw new IllegalArgumentException("length for folding not valid. length divided filter length not without remainder.");

		final int length = filterLength / numberOfFolds;
		final int numberOfVectors = filterLength / length;

		BitSet[] vectors = new BitSet[numberOfVectors];
		int i = 0;
		int ind = 0;
		while (i < filterLength)
		{
			BitSet tmp = new BitSet(length);
			vectors[ind++] = tmp;

			for (int j = 0; j < length; ++j, ++i)
			{
				if (vector.get(i))
					tmp.set(j);
			}
		}

		for (int k = 1; k < numberOfVectors; ++k)
			vectors[0].xor(vectors[k]);

		return new BloomFilter(vectors[0], length, null);
	}

	/**
	 * Returns a new balanced bloom filter of this bloom filter. See {@link BloomFilter#getBalancedBloomFilter(long)}
	 * Note that the Balanced Bloomfilter is twice as long as the original Bloomfilter!
	 *
	 * @param seed Seed for random generator to permutate bits
	 * @return Balanced bloom filter
	 */
	public BloomFilter getBalancedBloomFilter(long seed)
	{
		BitSet balanced = getBalancedVector(seed);
		return new BloomFilter(balanced, filterLength * 2, null);
	}

	/**
	 * Returns the length of bloom filter.
	 *
	 * @return Length of bloom filter.
	 */
	public int length()
	{
		return filterLength;
	}

	/**
	 * Returns the bloom filter in base64 format.
	 * Example:
	 * Bloom filter with
	 * Data: "00111001000101010101"
	 * Encoded: "nKgK"
	 * (all without ")
	 *
	 * @return Encoded bloom filter
	 */
	public String getAsEncodedString()
	{
		Base64.Encoder e = Base64.getEncoder();
		int byteLength = (int)Math.ceil(filterLength / 8.0);

		byte[] vec = vector.toByteArray();
		byte[] completeVec = new byte[byteLength];
		System.arraycopy(vec, 0, completeVec, 0, vec.length);

		for (int i = 0; vec.length + i < byteLength; ++i)
			completeVec[vec.length + i] = 0;

		return e.encodeToString(completeVec);
	}

	/**
	 * Compares the bloom filter with given object.
	 *
	 * @param obj object to compare
	 * @return true, if given object is an instance of BloomFilter and contains the same bit vector, otherwise false.
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (!(obj instanceof BloomFilter))
			return false;

		if (filterLength != ((BloomFilter) obj).filterLength)
			return false;

		return vector.equals(((BloomFilter) obj).vector);
	}

	/**
	 * Returns hash code of bloom filter.
	 *
	 * @return Hash code.
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		long temp = Double.doubleToLongBits(filterLength);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((vector == null) ? 0 : vector.hashCode());
		result = prime * result + ((strategy == null) ? 0 : strategy.hashCode());

		return result;
	}

	/**
	 * Returns the bloom filter as string. The string contains "1" on positions the bit vector os true and
	 * "0" on positions the bit vector is false.
	 *
	 * @return string contains "1" and "0" for bit vector representation.
	 */
	@Override
	public String toString()
	{
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < filterLength; ++i)
			s.append(vector.get(i) ? 1 : 0);

		return s.toString();
	}

	/**
	 * Converts the given string into a bitset.
	 * All '1' are converted to true bits in bitset.
	 * All '0' are converted to false bits in bitset.
	 *
	 * @param str String to convert
	 * @return Bitset of converted string.
	 * @throws IllegalArgumentException If given string contains other character than '1' and '0'.
	 */
	private BitSet stringToBitSet(String str)
	{
		if (str == null)
			throw new IllegalArgumentException("Null can not converted to BitSet.");

		BitSet tmp = new BitSet(str.length());

		for (int i = 0, len = str.length(); i < len; ++i)
		{
			if (str.charAt(i) == '1')
				tmp.set(i);
			else if (str.charAt(i) != '0')
				throw new IllegalArgumentException("String contains invalid character.");
		}

		return tmp;
	}

	/**
	 * Returns a balanced bloom filter of this bloom filter.
	 * Note that the Balanced bloom filter is twice as long as the original bloom filter!
	 * See "Randomized Response and Balanced Bloom Filters for Privacy Preserving Record Linkage" (10.1109/ICDMW.2016.0038)
	 * of R. Schnell and C. Borgs for details.
	 *
	 * @param seed Seed for random generator to permute bits
	 * @return Balanced bloom filter
	 */
	private BitSet getBalancedVector(long seed)
	{
		int length = filterLength * 2;
		BitSet tmpVector = new BitSet(length);
		for (int i = 0; i < filterLength; ++i)
			tmpVector.set(i, vector.get(i % filterLength));

		for (int i = 0; i < filterLength; ++i)
			tmpVector.set(i + filterLength, !vector.get(i));

		Random rnd = new Random(seed);
		for (int i = 0; i < length; ++i)
		{
			int idx = rnd.nextInt(length);

			boolean tmp = tmpVector.get(idx);
			tmpVector.set(idx, tmpVector.get(i));
			tmpVector.set(i, tmp);
		}

		return tmpVector;
	}
}
