package org.emau.icmvc.ttp.utils;

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

import java.util.BitSet;

/**
 * @author Christopher Hampf
 */
public class BitSetConverter
{
	private BitSetConverter()
	{
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Convert the given string to a bitset. '0' in input string is interpreted as unset bit, all other character as set bit.
	 *
	 * @param str Input string, contains sequence of '0' and '1'
	 * @return Bitset, contains unset bits at positions with '0' in the input string and set bits at positions with characters other than '0'.
	 */
	public static BitSet getBitset(String str)
	{
		BitSet result = new BitSet(str.length());

		for (int i = 0, n = str.length(); i < n; ++i)
		{
			if (str.charAt(i) != '0')
				result.set(i);
		}

		return result;
	}
}
