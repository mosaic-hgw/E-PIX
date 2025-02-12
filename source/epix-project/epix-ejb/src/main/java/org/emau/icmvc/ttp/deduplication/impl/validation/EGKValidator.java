package org.emau.icmvc.ttp.deduplication.impl.validation;

/*-
 * ###license-information-start###
 * E-PIX - Enterprise Patient Identifier
 * 							Cross-referencing
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.emau.icmvc.ttp.epix.common.deduplication.IValidator;

/**
 * Validates the fix part of an EGK.
 * @link <a href="https://de.wikipedia.org/wiki/Krankenversichertennummer">Krankenversichertennummer</a>
 */
public class EGKValidator implements IValidator
{
	@Override
	public boolean validate(String value)
	{
		if (value == null || value.length() < 10)
			return false;

		boolean isValid = false;

		Pattern p = Pattern.compile("^([A-Z])(\\d{8})(\\d)$");
		Matcher m = p.matcher(value.length() == 10 ? value : value.substring(0, 10));

		if (m.matches())
		{
			String s = String.format("%02d", m.group(1).charAt(0) - 'A' + 1) + m.group(2);

			int sum = 0;
			for (int i = 0; i < 10; ++i)
			{
				int num = s.charAt(i) - '0';
				if (i % 2 == 1)
				{
					num *= 2;
				}

				if (num > 9)
				{
					num -= 9;
				}

				sum += num;
			}

			isValid = sum % 10 == (m.group(3).charAt(0) - '0');
		}

		return isValid;
	}

	@Override
	public String toString()
	{
		return "EGKValidator{}";
	}
}
