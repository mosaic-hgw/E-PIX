package org.emau.icmvc.ttp.deduplication.impl;

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

import org.apache.commons.codec.language.ColognePhonetic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.emau.icmvc.ttp.epix.common.deduplication.IStringMatchingAlgorithm;

/**
 *
 * @author Christian Schack, geidell
 * @since 2011
 *
 */
public class ColognePhoneticAlgorithm implements IStringMatchingAlgorithm
{
	private static final Logger logger = LogManager.getLogger(ColognePhoneticAlgorithm.class);
	private static final ColognePhonetic colognePhonetic = new ColognePhonetic();

	@Override
	public double match(String toBlock, String candidate)
	{
		boolean bool = colognePhonetic.isEncodeEqual(toBlock, candidate);
		if (logger.isTraceEnabled())
		{
			logger.trace("matching: " + toBlock + " and " + candidate + " gives " + bool);
		}
		return bool ? 1. : 0.;
	}
}
