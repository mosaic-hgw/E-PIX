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

import com.google.gwt.dev.util.editdistance.GeneralEditDistance;
import com.google.gwt.dev.util.editdistance.GeneralEditDistances;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.emau.icmvc.ttp.epix.common.deduplication.IStringMatchingAlgorithm;

/**
 *
 * @author Christian Schack, geidell
 *
 */
public class LevenshteinAlgorithm implements IStringMatchingAlgorithm
{
	private static final Logger logger = LogManager.getLogger(LevenshteinAlgorithm.class);

	/**
	 * Returns 1 if str1 and str2 are equal, a value between 0 and 1 else
	 */
	@Override
	public double match(String str1, String str2)
	{
		return matchCached(str1, str2, GeneralEditDistances.getLevenshteinDistance(str1));
	}

	public double matchCached(String str1, String str2, GeneralEditDistance ed)
	{
		if (str1.equals(str2))
		{
			if (logger.isTraceEnabled())
			{
				logger.trace("levenshtein similarity for matching " + str1 + " and " + str2 + " = 1");
			}
			return 1;
		}
		// choose longest string
		int length = str1.length() > str2.length() ? str1.length() : str2.length();
		float dist = ed.getDistance(str2, length);
		if (dist > length)
		{
			dist = length;
		}
		float r = 1 - dist / length; // length kann wegen voherigem equals-vergleich nicht 0 sein
		if (logger.isTraceEnabled())
		{
			logger.trace("levenshtein similarity for matching " + str1 + " and " + str2 + " = " + r);
		}
		return r;
	}
}
