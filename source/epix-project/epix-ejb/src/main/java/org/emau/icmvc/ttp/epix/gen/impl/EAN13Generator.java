package org.emau.icmvc.ttp.epix.gen.impl;

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

import org.apache.commons.lang3.StringUtils;
import org.emau.icmvc.ttp.epix.gen.MPIGenerator;

/**
 * @author Christian Schack, geidell
 */
public class EAN13Generator extends MPIGenerator
{
	public static final boolean FIXED_4_DIGITS_PREFIX = true;
	private static final String ZERO = "0";
	private static final String MPIID_DESCRIPTION = "generated MPI id";

	@Override
	public String getMPIDescription()
	{
		return MPIID_DESCRIPTION;
	}

	@Override
	public String generate(long counter, int mpiPrefix)
	{
		logger.debug("generate mpi for id {} and mpi-prefix {}", counter, mpiPrefix);

		int mpiPrefixLength = Integer.toString(mpiPrefix).length();
		int maxCounterLength = FIXED_4_DIGITS_PREFIX ? 8 : 12 - mpiPrefixLength;
		if (counter <= 0 || Long.toString(counter).length() > maxCounterLength)
		{
			throw new IllegalArgumentException("counter for MPI id must have a max length of " +
					maxCounterLength + " and must be greater than 0 but is " + counter);
		}
		else if (mpiPrefix > 9999 || mpiPrefix < 0)
		{
			throw new IllegalArgumentException("mpi-prefix must be >=0 and <=9999 but is " + mpiPrefix);
		}

		// format: 1..4 digits prefix, 12 - (1..4) digits counter (with left padded zeroes if FIXED_4_DIGITS_PREFIX is true)
		long base = mpiPrefix * 100000000L;
		if (!FIXED_4_DIGITS_PREFIX)
		{
			base *= switch (mpiPrefixLength)
			{
				case 1 -> 1000;
				case 2 -> 100;
				case 3 -> 10;
				case 4 -> 1;
				default -> throw new IllegalArgumentException("mpi-prefix must be >=0 and <=9999 but is " + mpiPrefix); // will never happen
			};
		}
		String cwcs = String.valueOf(base + counter);
		while (cwcs.length() < 12)
		{
			cwcs = ZERO.concat(cwcs);
		}

		logger.debug("MPI id without checksum: {}", cwcs);

		return cwcs + calculateChecksum(cwcs);
	}

	@Override
	public String generatePrefix(int mpiPrefix)
	{
		return FIXED_4_DIGITS_PREFIX ? StringUtils.leftPad(Integer.toString(mpiPrefix), 4, '0') : Integer.toString(mpiPrefix);
	}

	int calculateChecksum(String cwcs)
	{
		logger.debug("calculate checksum for: {}", cwcs);

		// Filling the number into an array of int
		int[] code = new int[12];
		for (int i = 0; i < 12; i++)
		{
			code[i] = cwcs.charAt(i);
		}

		int checksum = 0;

		// Checksum calculation
		// alternate multiplication of each number with 1 (MOD 2 == 0) and 3
		// (MOD 2 != 0) and creating the sum
		// of the products.
		for (int i = 0; i < 12; i++)
		{
			if (i % 2 == 0)
			{
				checksum = checksum + code[i];
			}
			else
			{
				checksum = checksum + code[i] * 3;
			}
		}

		// Difference between current checksum and the next multiple of ten
		int diff = checksum % 10;
		if (diff != 0)
		{
			diff = 10 - diff;
		}

		logger.debug("checksum for {} is {}", cwcs, diff);

		return diff;
	}

	@Override
	public long toCounter(String mpiIdValue, int mpiPrefix)
	{
		int start = FIXED_4_DIGITS_PREFIX ? 4 : Integer.toString(mpiPrefix).length();
		return Long.parseLong(mpiIdValue.substring(start, 12));
	}

	@Override
	public boolean checkConsistence(String mpiIdValue)
	{
		if (mpiIdValue != null && mpiIdValue.length() == 13)
		{
			String cwcs = mpiIdValue.substring(0, 12);
			String compareString = cwcs + calculateChecksum(cwcs);
			return compareString.equalsIgnoreCase(mpiIdValue);
		}
		return false;
	}
}
