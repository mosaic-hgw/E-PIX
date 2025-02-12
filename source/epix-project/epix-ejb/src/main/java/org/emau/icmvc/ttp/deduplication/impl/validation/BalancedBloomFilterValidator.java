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

import org.emau.icmvc.ttp.deduplication.impl.bloomfilter.Base64Cache;
import org.emau.icmvc.ttp.epix.common.deduplication.IValidator;

public class BalancedBloomFilterValidator implements IValidator
{
	/** Number of bits per bloom filter */
	private final int length;

	public BalancedBloomFilterValidator(int length)
	{
		if (length <= 0)
			throw new IllegalArgumentException("balanced bloom filter validator needs the length of accepted bloom filters. Length of 0 or smaller is not allowed.");

		this.length = length;
	}

	/**
	 * Checks whether the number of set and unset positions is the same.
	 *
	 * @param value encoded balanced bloom filter
	 * @return true, if set and unset positions is the same or value is null or empty, otherwise false
	 */
	@Override
	public boolean validate(String value)
	{
		if (value == null || value.isEmpty())
			return true;

		int ones = 0;
		for (char base64 : value.toCharArray())
		{
			ones += Base64Cache.getCardinality(base64);
		}

		return ones == (length / 2);
	}

	@Override
	public String toString()
	{
		return "BalancedBloomFilterValidator{}";
	}
}
