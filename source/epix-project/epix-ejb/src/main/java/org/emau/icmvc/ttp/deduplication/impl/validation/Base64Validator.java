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

import org.emau.icmvc.ttp.epix.common.deduplication.IValidator;

public class Base64Validator implements IValidator
{
	@Override
	public boolean validate(String value)
	{
		return isBase64(value);
	}

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

	@Override
	public String toString()
	{
		return "Base64Validator{}";
	}
}
