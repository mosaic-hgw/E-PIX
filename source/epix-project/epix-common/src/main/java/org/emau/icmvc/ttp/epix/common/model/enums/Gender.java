package org.emau.icmvc.ttp.epix.common.model.enums;

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


import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * 
 * @author geidell
 *
 */
@XmlJavaTypeAdapter(GenderAdapter.class)
public enum Gender
{
	M('m'), F('f'), X('x'), O('o'), U('u');

	private static final Gender VALUES[] = values();
	private final char value;

	private Gender(char v)
	{
		value = v;
	}

	public char getValue()
	{
		return value;
	}

	public static Gender fromValue(String v)
	{
		if (v == null || v.length() != 1)
		{
			throw new IllegalArgumentException("invalid value for gender: " + v);
		}
		char compare = Character.toLowerCase(v.charAt(0));
		for (Gender c : VALUES)
		{
			if (c.value == compare)
			{
				return c;
			}
		}
		throw new IllegalArgumentException("invalid char for gender: " + v);
	}

	public static Gender fromValue(Character v)
	{
		if (v == null)
		{
			throw new IllegalArgumentException("invalid value for gender: " + v);
		}
		char compare = Character.toLowerCase(v);
		for (Gender c : VALUES)
		{
			if (c.value == compare)
			{
				return c;
			}
		}
		throw new IllegalArgumentException("invalid char for gender: " + v);
	}

	public static Gender fromValue(char v)
	{
		char compare = Character.toLowerCase(v);
		for (Gender c : VALUES)
		{
			if (c.value == compare)
			{
				return c;
			}
		}
		throw new IllegalArgumentException("invalid char for gender: " + v);
	}
}
