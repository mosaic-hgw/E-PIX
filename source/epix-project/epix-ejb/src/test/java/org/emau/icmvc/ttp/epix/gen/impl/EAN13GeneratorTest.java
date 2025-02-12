package org.emau.icmvc.ttp.epix.gen.impl;

/*-
 * ###license-information-start###
 * E-PIX - Enterprise Identifier Cross-Referencing
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
import org.junit.jupiter.api.Test;

import static org.emau.icmvc.ttp.epix.gen.impl.EAN13Generator.FIXED_4_DIGITS_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EAN13GeneratorTest
{
	EAN13Generator ean = new EAN13Generator();

	/**
	 * Create manually the expected MPI depending on the strategy given in 'fixed4DigitsPrefix'.
	 *
	 * @param prefix
	 * 		the prefix
	 * @param counter
	 * 		the counter
	 * @param fixed4DigitsPrefix
	 *      true to use the fixed 4 digits as prefix
	 * @return an MPI of length 13
	 */
	String mpi(int prefix, long counter, boolean fixed4DigitsPrefix)
	{
		String cwcs = fixed4DigitsPrefix ?
			StringUtils.leftPad(Integer.toString(prefix), 4, "0") + StringUtils.leftPad(Long.toString(counter), 8, "0") :
			prefix + StringUtils.leftPad(Long.toString(counter), 12 - Integer.toString(prefix).length(), "0");
		return cwcs + ean.calculateChecksum(cwcs);
	}

	/**
	 * Assert correct creation of MPI and counter extraction for given parts
	 *
	 * @param prefix
	 * 		the prefix
	 * @param counter
	 * 		the counter
	 */
	protected void assertMPI(int prefix, long counter)
	{
		String expectedMPI = mpi(prefix, counter, FIXED_4_DIGITS_PREFIX);
		System.out.println(expectedMPI);
		String actualMPI = ean.generate(counter, prefix);
		assertEquals(expectedMPI, actualMPI);
		assertTrue(ean.checkConsistence(expectedMPI));
		assertEquals(counter, ean.toCounter(expectedMPI, prefix));
	}

	/**
	 * Assert incorrect creation of MPI or counter extraction for given parts
	 *
	 * @param prefix
	 * 		the prefix
	 * @param counter
	 * 		the counter
	 */
	protected void assertMPIException(String msg, Class<? extends Throwable> type, int prefix, long counter)
	{
		assertEquals(msg, assertThrows(type, () -> assertMPI(prefix, counter)).getMessage());
	}

	@Test
	void testPrefixNegativ()
	{
		assertMPIException("mpi-prefix must be >=0 and <=9999 but is -1", IllegalArgumentException.class, -1, 1);
	}

	@Test
	void testPrefix0()
	{
		assertMPI(0, 1);
		assertMPI(0, 12345678);
		if (!FIXED_4_DIGITS_PREFIX)
		{
			assertMPI(0, 12345678901L);
		}
		assertMPIException("counter for MPI id must have a max length of " + (FIXED_4_DIGITS_PREFIX ? 8 : 11) + " and must be greater than 0 but is 123456789012",
				IllegalArgumentException.class, 0, 123456789012L);
	}

	@Test
	void testPrefix1()
	{
		int prefix = 1;
		assertMPI(prefix, 1);
		assertMPI(prefix, 12345678);
		if (!FIXED_4_DIGITS_PREFIX)
		{
			assertMPI(prefix, 12345678901L);
		}
		assertMPIException("counter for MPI id must have a max length of " + (FIXED_4_DIGITS_PREFIX ? 8 : 11) + " and must be greater than 0 but is 123456789012",
				IllegalArgumentException.class, prefix, 123456789012L);
	}

	@Test
	void testPrefix1001()
	{
		int prefix = 1001;
		assertMPI(prefix, 1);
		assertMPI(prefix, 21);
		assertMPI(prefix, 321);
		assertMPI(prefix, 12345678);
		assertMPIException("counter for MPI id must have a max length of 8 and must be greater than 0 but is 0",
				IllegalArgumentException.class, prefix, 0);
		assertMPIException("counter for MPI id must have a max length of 8 and must be greater than 0 but is 123456789",
				IllegalArgumentException.class, prefix, 123456789);
	}

	@Test
	void testPrefix4444()
	{
		int prefix = 4444;
		assertMPI(prefix, 1);
		assertMPI(prefix, 21);
		assertMPI(prefix, 321);
		assertMPI(prefix, 12345678);
		assertMPIException("counter for MPI id must have a max length of 8 and must be greater than 0 but is 0",
				IllegalArgumentException.class, prefix, 0);
		assertMPIException("counter for MPI id must have a max length of 8 and must be greater than 0 but is 123456789",
				IllegalArgumentException.class, prefix, 123456789);
	}

	@Test
	void testPrefix333()
	{
		int prefix = 333;
		assertMPI(prefix, 1);
		assertMPI(prefix, 21);
		assertMPI(prefix, 321);
		assertMPI(prefix, 12345678);
		if (!FIXED_4_DIGITS_PREFIX)
		{
			assertMPI(prefix, 123456789);
		}
		assertMPIException("counter for MPI id must have a max length of " + (FIXED_4_DIGITS_PREFIX ? 8 : 9) + " and must be greater than 0 but is 0",
				IllegalArgumentException.class, prefix, 0);
		assertMPIException("counter for MPI id must have a max length of " + (FIXED_4_DIGITS_PREFIX ? 8 : 9) + " and must be greater than 0 but is 1234567890",
				IllegalArgumentException.class, prefix, 1234567890);
	}
}
