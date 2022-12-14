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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Christopher Hampf
 */
public class Base64Cache
{
	/**
	 * Cache of base64 character and byte value (A is 0 etc.)
	 */
	private static final Map<Character, Byte> base64ToValue = getBase64ToValues();

	/**
	 * Cache for base64 character and cardinality (number of set bits) of byte value (A has a cardinality of 0)
	 */
	private static final Map<Character, Byte> cardinalities = getCardinalities();

	/**
	 * Cache of all combinations of AND-linked numeric values of base64 characters
	 */
	private static final Map<Character, Map<Character, Byte>> intersections = getIntersections();

	/**
	 * Cache of all combinations of OR-linked numeric values of base64 characters
	 */
	private static final Map<Character, Map<Character, Byte>> unions = getUnions();

	private Base64Cache()
	{
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Returns value of the given base64 character.
	 * <p>
	 * {@code 'A' returns 0; '/' returns 63; see <a href="https://en.wikipedia.org/wiki/Base64#Base64_table">Base64 table</a>}.
	 *
	 * @param base64 character whose value is to be returned
	 * @return Value of given base64 character
	 * @throws IllegalArgumentException if given character is not valid
	 */
	public static byte getValueOfBase64Value(char base64)
	{
		if (!base64ToValue.containsKey(base64))
			throw new IllegalArgumentException("Given char is invalid: '" + base64 +"'");

		return base64ToValue.get(base64);
	}

	/**
	 * Returns the cardinality (bit that set to one) of the given base64 character.
	 * <p>
	 * {@code 'A' returns 0; '/' returns 6; see <a href="https://en.wikipedia.org/wiki/Base64#Base64_table">Base64 table</a>}.
	 *
	 * @param base64 character whose value is to be returned
	 * @return Number of set ones of given base64 character
	 * @throws IllegalArgumentException if given character is not valid
	 */
	public static byte getCardinality(char base64)
	{
		if (!cardinalities.containsKey(base64))
			throw new IllegalArgumentException("Given char is invalid: '" + base64 +"'");

		return cardinalities.get(base64);
	}

	/**
	 * Returns the cardinality (bit that set to one) of AND-linked given base64 characters.
	 *
	 * @param base64Char1 First base64 character
	 * @param base64Char2 Second base64 character
	 * @return Number of set ones of AND-linked base64 characters
	 * @throws IllegalArgumentException if one given character is not valid
	 */
	public static byte getIntersectionCardinality(char base64Char1, char base64Char2)
	{
		if (!intersections.containsKey(base64Char1))
			throw new IllegalArgumentException("Given char1 is invalid");

		if (!intersections.get(base64Char1).containsKey(base64Char2))
			throw new IllegalArgumentException("Given char2 is invalid");

		return intersections.get(base64Char1).get(base64Char2);
	}

	/**
	 * Returns the cardinality (bit that set to one) of OR-linked given base64 characters.
	 *
	 * @param base64Char1 First base64 character
	 * @param base64Char2 Second base64 character
	 * @return Number of set ones of OR-linked base64 characters
	 * @throws IllegalArgumentException if one given character is not valid
	 */
	public static byte getUnionCardinality(char base64Char1, char base64Char2)
	{
		if (!unions.containsKey(base64Char1))
			throw new IllegalArgumentException("Given char1 is invalid");

		if (!unions.get(base64Char1).containsKey(base64Char2))
			throw new IllegalArgumentException("Given char2 is invalid");

		return unions.get(base64Char1).get(base64Char2);
	}

	/**
	 * Returns a map of all base64 characters (except '=') and the corresponding numeric value ('A' is 0 etc.).
	 * see <a href="https://en.wikipedia.org/wiki/Base64#Base64_table">Base64 table</a>}
	 *
	 * @return Map of all base64 characters with corresponding numeric value
	 */
	private static Map<Character, Byte> getBase64ToValues()
	{
		Map<Character, Byte> tmp = new HashMap<>();

		for (char i = 'A'; i <= 'Z'; ++i)
			tmp.put(i, (byte) (i - 'A'));

		for (char i = 'a'; i <= 'z'; ++i)
			tmp.put(i, (byte) (i - 'a' + 26));

		for (char i = '0'; i <= '9'; ++i)
			tmp.put(i, (byte) (i - '0' + 52));

		tmp.put('+', (byte) 62);
		tmp.put('/', (byte) 63);

		tmp.put('=', (byte) 0);

		return tmp;
	}

	/**
	 * Returns a map of all base64 characters (except '=') and the corresponding cardinality.
	 * see <a href="https://en.wikipedia.org/wiki/Base64#Base64_table">Base64 table</a>}
	 *
	 * @return Map of all base64 characters with corresponding cardinality
	 */
	private static Map<Character, Byte> getCardinalities()
	{
		Map<Character, Byte> tmp = new HashMap<>();

		for (Map.Entry<Character, Byte> e : base64ToValue.entrySet())
			tmp.put(e.getKey(), getCardinality(e.getValue()));

		return tmp;
	}

	/**
	 * Returns the cardinality (number of set ones) of the given number.
	 *
	 * @return Cardinality of given number
	 */
	private static byte getCardinality(byte val)
	{
		final byte mask1 = 0x55;
		final byte mask2 = 0x33;
		final byte mask3 = 0x0F;

		val = (byte) ((byte) (val & mask1) + (byte) ((val >> (byte) 1) & (mask1 & 0xff)));
		val = (byte) ((byte) (val & mask2) + (byte) ((val >> (byte) 2) & (mask2 & 0xff)));
		val = (byte) ((byte) (val & mask3) + (byte) ((val >> (byte) 4) & (mask3 & 0xff)));

		return val;
	}

	/**
	 * Returns a map of all combinations of AND-linked numeric values of base64 characters.
	 *
	 * @return Map of AND-linked numeric values of base64 characters
	 */
	private static Map<Character, Map<Character, Byte>> getIntersections()
	{
		Map<Character, Map<Character, Byte>> tmp = new HashMap<>();

		for (char a : base64ToValue.keySet())
		{
			Map<Character, Byte> inner = new HashMap<>();

			for (char b : base64ToValue.keySet())
			{
				byte intersec = getCardinality((byte) (getValueOfBase64Value(a) & getValueOfBase64Value(b)));

				inner.put(b, intersec);
			}

			tmp.put(a, inner);
		}

		return tmp;
	}

	/**
	 * Returns a map of all combinations of OR-linked numeric values of base64 characters.
	 *
	 * @return Map of OR-linked numeric values of base64 characters
	 */
	private static Map<Character, Map<Character, Byte>> getUnions()
	{
		Map<Character, Map<Character, Byte>> tmp = new HashMap<>();

		for (char a : base64ToValue.keySet())
		{
			Map<Character, Byte> inner = new HashMap<>();

			for (char b : base64ToValue.keySet())
			{
				byte union = getCardinality((byte) (getValueOfBase64Value(a) | getValueOfBase64Value(b)));

				inner.put(b, union);
			}

			tmp.put(a, inner);
		}

		return tmp;
	}
}
