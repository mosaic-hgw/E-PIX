package org.emau.icmvc.ttp.deduplication.impl;

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

import java.util.BitSet;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.emau.icmvc.ttp.epix.common.deduplication.IStringMatchingAlgorithm;
import org.emau.icmvc.ttp.utils.BitSetConverter;

/**
 * @author Christopher Hampf
 */
public class JaccardSimilarityAlgorithm implements IStringMatchingAlgorithm
{
	private static final Logger logger = LogManager.getLogger(JaccardSimilarityAlgorithm.class);

	/**
	 * Returns the jaccard similarity of two bloom filters. The input strings should only contain sequences of 0 and 1.
	 * Zero is interpreted as 0, all others as 1.
	 *
	 * @returns Value between 0 and 1 ([0,1]), which represent the similarity of the input strings.
	 */
	@Override
	public double match(String str1, String str2)
	{
		if (str1 == null || str2 == null)
		{
			logger.warn("At least one given string is null. Result is 0.");
			return 0d;
		}

		if (str1.equals(str2))
		{
			return 1d;
		}

		if (str1.length() != str2.length())
		{
			logger.warn("The given strings have different lengths. Result is 0.");
			return 0d;
		}

		BitSet intersection = BitSetConverter.getBitset(str1);
		intersection.and(BitSetConverter.getBitset(str2));

		BitSet union = BitSetConverter.getBitset(str1);
		union.or(BitSetConverter.getBitset(str2));

		return (double) intersection.cardinality() / (double) union.cardinality();

	}
}
