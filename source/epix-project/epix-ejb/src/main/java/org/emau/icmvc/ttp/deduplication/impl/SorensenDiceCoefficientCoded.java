package org.emau.icmvc.ttp.deduplication.impl;

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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.emau.icmvc.ttp.deduplication.impl.bloomfilter.Base64Cache;
import org.emau.icmvc.ttp.deduplication.impl.bloomfilter.BloomFilter;
import org.emau.icmvc.ttp.epix.common.deduplication.IStringMatchingAlgorithm;

/**
 * @author Christopher Hampf
 */
public class SorensenDiceCoefficientCoded implements IStringMatchingAlgorithm
{
	private static final Logger logger = LogManager.getLogger(SorensenDiceCoefficientCoded.class);

	/**
	 * Returns the sorensen dice coefficient of two encoded bloom filters {@link BloomFilter#getAsEncodedString}.
	 *
	 * @param str1
	 *            First encoded bloom filter
	 * @param str2
	 *            Second encoded bloom filter
	 * @throws NumberFormatException
	 *             if length is not correct formatted
	 *
	 * @return Value between 0 and 1 ([0,1]), which represent the similarity of the input strings.
	 */
	@Override
	public double match(String str1, String str2)
	{
		if (!isBase64(str1) || !isBase64(str2))
		{
			throw new IllegalArgumentException("At least one given bloom filter is not in valid base64 format.");
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

		int card1 = 0;
		int card2 = 0;
		int and = 0;

		for (int i = 0; i < str1.length(); ++i)
		{
			char a = str1.charAt(i);
			char b = str2.charAt(i);

			card1 += Base64Cache.getCardinality(a);
			card2 += Base64Cache.getCardinality(b);

			and += Base64Cache.getIntersectionCardinality(a, b);
		}

		int sum = card1 + card2;

		return sum != 0 ? 2d * and / sum : 1.0;
	}

	// https://stackoverflow.com/a/23955827
	private boolean isBase64(String value)
	{
		if (value == null || value.length() == 0 || value.length() % 4 != 0
				|| value.indexOf(' ') >= 0 || value.indexOf('\r') >= 0 || value.indexOf('\n') >= 0 || value.indexOf('\t') >= 0)
		{
			return false;
		}

		int index = value.length() - 1;
		if (value.endsWith("="))
		{
			index--;
		}
		if (value.endsWith("=="))
		{
			index--;
		}
		for (int i = 0; i <= index; i++)
		{
			if (isInvalidBase64Char(value.charAt(i)))
			{
				return false;
			}
		}
		return true;
	}

	private boolean isInvalidBase64Char(int value)
	{
		if (value >= 48 && value <= 57)
		{
			return false;
		}
		if (value >= 65 && value <= 90)
		{
			return false;
		}
		if (value >= 97 && value <= 122)
		{
			return false;
		}
		return value != 43 && value != 47;
	}
}
