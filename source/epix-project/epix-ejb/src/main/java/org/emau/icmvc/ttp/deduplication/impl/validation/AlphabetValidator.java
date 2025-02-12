package org.emau.icmvc.ttp.deduplication.impl.validation;

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

import java.util.Objects;

import org.emau.icmvc.ttp.epix.common.deduplication.IValidator;

public class AlphabetValidator implements IValidator
{
	private final String alphabet;
	public AlphabetValidator(String alphabet)
	{
		this.alphabet = (alphabet == null) ? "" : alphabet;
	}

	/**
	 * Check a given string by looking up all characters in the defined alphabet.
	 *
	 * @param value checked string
	 *
	 * @return
	 * true:
	 * 	- if all characters of value are in the defined alphabet
	 * 	- if value and alphabet are both empty
	 *  - if value is null and the defined alphabet is empty
	 * false:
	 *  - value contains characters that are not in the defined alphabet
	 *  - value is null or empty and the alphabet is not empty
	 */
	@Override
	public boolean validate(String value)
	{
		if (value == null && alphabet.isEmpty() || (value != null && value.isEmpty() && alphabet.isEmpty()))
		{
			return true;
		}
		else if (value == null || value.isEmpty())
		{
			return false;
		}
		else
		{
			boolean result = true;

			for (int i = 0; i < value.length(); ++i)
			{
				if (alphabet.indexOf(value.charAt(i)) == -1)
				{
					result = false;
					break;
				}
			}

			return result;
		}
	}

	public String getAlphabet()
	{
		return alphabet;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}
		AlphabetValidator that = (AlphabetValidator) o;
		return Objects.equals(alphabet, that.alphabet);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(alphabet);
	}

	@Override
	public String toString()
	{
		return "AlphabetValidator{" +
				"alphabet='" + alphabet + '\'' +
				'}';
	}
}
