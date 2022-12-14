package org.emau.icmvc.ttp.deduplication.impl.bloomfilter;

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

import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.emau.icmvc.ttp.epix.common.deduplication.IBloomFilterStrategy;
import org.emau.icmvc.ttp.epix.common.exception.NotImplementedException;
import org.emau.icmvc.ttp.utils.NGramTransformer;

/**
 * Implementing a strategy for random hashing (see: 10.1109/ICDMW.2016.0038)
 *
 * @author Christopher Hampf
 */
public class RandomHashingStrategy implements IBloomFilterStrategy
{
	/**
	 * Length of bloom filter
	 */
	private final int length;

	/**
	 * Number of positions per n-gram
	 */
	private final int numBitPositions;

	/**
	 * Length of n-grams
	 */
	private final int nGramLength;

	/**
	 * Look-up Table (contains attributes and specific n-gram positions)
	 */
	Map<String, Map<String, List<Integer>>> lookUpTable;

	/**
	 * Generate a random hashing strategy that encodes values depends on attribute specific random positions for each possible n-gram.
	 *
	 * @param length
	 *            Length of the resulting bloom filter
	 * @param numBitPositions
	 *            Number of positions per n-gram
	 * @param nGramLength
	 *            Length of n-grams
	 * @param alphabet
	 *            All characters from which all permutations are generated (all possible n-grams) (contains all characters that are used in all given attributes)
	 * @param attributesAndSeeds
	 *            Map of attributes with a specific seed for random generator
	 * @throws IllegalArgumentException
	 *             if length, iterations or nGramLength is lower than 1 or if alphabet or attributes are empty or null
	 */
	public RandomHashingStrategy(int length, int numBitPositions, int nGramLength, String alphabet, Map<String, Long> attributesAndSeeds)
	{
		if (length < 1)
		{
			throw new IllegalArgumentException("Given length is not valid. Expected length is greater than 0.");
		}

		if (numBitPositions < 1)
		{
			throw new IllegalArgumentException("Given number of iterations is not valid. Expected number of iterations is greater than 0.");
		}

		if (nGramLength < 1)
		{
			throw new IllegalArgumentException("Given nGramLength is not valid. Expected nGramLength is greater than 0.");
		}

		this.length = length;
		this.numBitPositions = numBitPositions;
		this.nGramLength = nGramLength;

		lookUpTable = generatePossibleNGrams(attributesAndSeeds, alphabet);
	}

	@Override
	public BitSet getBitVector(String value)
	{
		throw new NotImplementedException();
	}

	/**
	 * Returns a BitVector that has the given value encoded by random hashing.
	 *
	 * @param value
	 *            Value that is encoded (if null, than empty string is used)
	 * @param attribute
	 *            Name of the attribute of the given value
	 * @return BitSet with the encoded value
	 * @throws IllegalArgumentException
	 *             if attribute is not contained in look-up table or attribute is null
	 */
	@Override
	public BitSet getBitVector(String value, String attribute)
	{
		if (attribute == null || !lookUpTable.containsKey(attribute))
		{
			throw new IllegalArgumentException("attribute is null or unknown in look-up-table");
		}

		String tmpVal = value == null ? "" : value;

		BitSet vector = new BitSet(length);

		Map<String, List<Integer>> attrMap = lookUpTable.get(attribute);

		List<String> ngrams = NGramTransformer.getNGrams(tmpVal, nGramLength);
		for (String ngram : ngrams)
		{
			if (!attrMap.containsKey(ngram))
			{
				continue; // N-Gram is unknown.
			}

			List<Integer> positions = attrMap.get(ngram);
			for (int e : positions)
			{
				vector.set(e);
			}
		}

		return vector;
	}

	/**
	 * Returns the length of the resulting bloom filter.
	 *
	 * @return length of bloom filter
	 */
	@Override
	public int getLength()
	{
		return length;
	}

	/**
	 * Returns a map containing all specified attributes and a value as map containing a list of positions for each n-gram.
	 *
	 * @param attributes
	 *            Attributes for that positions for all n-grams are generated
	 * @param alphabet
	 *            All characters from which all permutations are generated (all possible n-grams)
	 * @return Map containing all attributes and n-gram positions
	 * @throws IllegalArgumentException
	 *             if alphabet or attributes are empty or null
	 */
	private Map<String, Map<String, List<Integer>>> generatePossibleNGrams(Map<String, Long> attributes, String alphabet)
	{
		if (alphabet == null || alphabet.length() == 0)
		{
			throw new IllegalArgumentException("alphabet for random hashing is null or empty");
		}

		if (attributes == null || attributes.size() == 0)
		{
			throw new IllegalArgumentException("no given attributes for random hashing");
		}

		Map<String, Map<String, List<Integer>>> result = new LinkedHashMap<>();

		for (Map.Entry<String, Long> attrAndSeed : attributes.entrySet())
		{
			Map<String, List<Integer>> tmp = new LinkedHashMap<>();

			permute(nGramLength, "", alphabet, tmp);

			Random rnd = new Random(attrAndSeed.getValue());
			for (List<Integer> positions : tmp.values())
			{
				for (int j = 0; j < numBitPositions; ++j)
				{
					positions.add(rnd.nextInt(length));
				}
			}

			result.put(attrAndSeed.getKey(), tmp);
		}

		return result;
	}

	/**
	 * Generate all permutations of the given alphabet and store it in the given map.
	 * Example:
	 * Alphabet=ABC
	 * Level: 3
	 * Permutations: AAA,AAB,AAC,ABA,ABB,ABC,ACA,ACB,ACC,BAA,BAB,BAC,BBA,BBB,BBC,BCA,BCB,BCC,CAA,CAB,CAC,CBA,CBB,CBC,CCA,CCB,CCC
	 *
	 * @param level
	 *            Number of positions
	 * @param prefix
	 *            Prefix (is also counted as positions)
	 * @param alphabet
	 *            All characters from which all permutations are generated
	 * @param result
	 *            Map in which all permutations are stored (an empty list will be generated for each key)
	 * @throws NullPointerException
	 *             if map is null
	 */
	private void permute(int level, String prefix, String alphabet, Map<String, List<Integer>> result)
	{
		if (level <= 0)
		{
			result.put(prefix, new ArrayList<>());
			return;
		}

		for (int i = 0; i < alphabet.length(); ++i)
		{
			permute(level - 1, prefix + alphabet.charAt(i), alphabet, result);
		}
	}
}
