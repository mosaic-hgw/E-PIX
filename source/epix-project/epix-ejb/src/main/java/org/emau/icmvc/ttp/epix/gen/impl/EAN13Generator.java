package org.emau.icmvc.ttp.epix.gen.impl;

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

import java.sql.Timestamp;

import org.emau.icmvc.ttp.epix.gen.MPIGenerator;
import org.emau.icmvc.ttp.epix.persistence.model.Domain;
import org.emau.icmvc.ttp.epix.persistence.model.Identifier;

/**
 *
 * @author Christian Schack, geidell
 *
 */
public class EAN13Generator extends MPIGenerator
{
	private static final String ZERO = "0";
	private static final String MPIID_DESCRIPTION = "generated MPI id";

	@Override
	public Identifier generate(Domain domain, long counter, Timestamp timestamp)
	{
		int mpiPrefix = Integer.valueOf(domain.getMatchingConfiguration().getMpiPrefix());
		String mpiidValue = generate(counter, mpiPrefix);
		return new Identifier(domain.getMpiDomain(), mpiidValue, MPIID_DESCRIPTION, timestamp);
	}

	private String generate(long counter, int mpiPrefix)
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("generate mpi for id: " + counter + " and mpi-prefix:" + mpiPrefix);
		}
		if (counter <= 0 || counter > 99999999l)
		{
			throw new IllegalArgumentException("counter for MPI id must have a max length of 8 and must be greater than 0 but is:" + counter);
		}
		else if (mpiPrefix > 9999 || mpiPrefix < 0)
		{
			throw new IllegalArgumentException("mpi-prefix must be >0 and <10000 but is:" + mpiPrefix);
		}

		// format: 4 ziffern domain-id, 8 ziffern uebergebener counter
		String cwcs = String.valueOf(mpiPrefix * 100000000l + counter);
		while (cwcs.length() < 12)
		{
			cwcs = ZERO.concat(cwcs);
		}
		if (logger.isDebugEnabled())
		{
			logger.debug("MPI id without checksum: " + cwcs);
		}
		return cwcs + calculateChecksum(cwcs);
	}

	private int calculateChecksum(String cwcs)
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("calculate checksum for: " + cwcs);
		}
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
		if (logger.isDebugEnabled())
		{
			logger.debug("checksum for: " + cwcs + " is: " + diff);
		}
		return diff;
	}

	@Override
	public boolean checkConsistence(String mpiidValue)
	{
		if (mpiidValue != null && mpiidValue.length() == 13)
		{
			String cwcs = mpiidValue.substring(0, 12);
			String compareString = cwcs + calculateChecksum(cwcs);
			if (compareString.equalsIgnoreCase(mpiidValue))
			{
				return true;
			}
		}
		return false;
	}
}
