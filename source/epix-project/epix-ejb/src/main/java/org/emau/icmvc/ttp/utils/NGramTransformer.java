package org.emau.icmvc.ttp.utils;

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
import java.util.List;

/**
 * @author Christopher Hampf
 */
public class NGramTransformer
{
	private NGramTransformer()
	{
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Returns a list of n-grams of given string.
	 *
	 * @param value string whose n-grams are to be returned
	 * @param length number of characters in gram (n)
	 *
	 * @return list of n-grams of given string
	 */
	public static List<String> getNGrams(String value, int length)
	{
		StringBuilder headAndTail = new StringBuilder();
		for (int i = 0; i < length - 1; ++i)
			headAndTail.append(" ");

		StringBuilder sb = new StringBuilder();
		sb.append(headAndTail).append(value).append(headAndTail);

		List<String> result = new ArrayList<>(2 * (length - 1) + value.length());
		for (int i = 0, j = value.length() + 1; i < j; ++i)
			result.add(sb.substring(i, i + length));

		return result;
	}
}
