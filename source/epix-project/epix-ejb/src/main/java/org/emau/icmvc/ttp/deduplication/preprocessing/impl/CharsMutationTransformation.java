package org.emau.icmvc.ttp.deduplication.preprocessing.impl;

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


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.emau.icmvc.ttp.epix.common.deduplication.IComplexTransformation;

/**
 * 
 * @author Christian Schack, geidell, Dirk Langner
 * @since 2012
 * 
 */
public class CharsMutationTransformation implements IComplexTransformation
{
	private static final Pattern SMALL_AE_PATTERN = Pattern.compile("ä", Pattern.LITERAL);
	private static final Pattern SMALL_OE_PATTERN = Pattern.compile("ö", Pattern.LITERAL);
	private static final Pattern SMALL_UE_PATTERN = Pattern.compile("ü", Pattern.LITERAL);
	private static final Pattern LARGE_AE_PATTERN = Pattern.compile("Ä", Pattern.LITERAL);
	private static final Pattern LARGE_OE_PATTERN = Pattern.compile("Ö", Pattern.LITERAL);
	private static final Pattern LARGE_UE_PATTERN = Pattern.compile("Ü", Pattern.LITERAL);
	private static final Pattern LARGE_SS_PATTERN = Pattern.compile("ẞ", Pattern.LITERAL);
	// Großes ß, kleines ß wird bei toUpperCase schon zu SS konvertiert
	// (ToUpperCaseTransformation und CharsMutationTransformation treten wahrscheinlich immer zusammen auf)

	@Override
	public String transform(String value)
	{
		// ist die String.replace-funktion, nur ohne staendiges kompilieren der pattern - ist threadsafe
		String newValue = SMALL_AE_PATTERN.matcher(value).replaceAll(Matcher.quoteReplacement("ae"));
		newValue = SMALL_OE_PATTERN.matcher(newValue).replaceAll(Matcher.quoteReplacement("oe"));
		newValue = SMALL_UE_PATTERN.matcher(newValue).replaceAll(Matcher.quoteReplacement("ue"));
		newValue = LARGE_AE_PATTERN.matcher(newValue).replaceAll(Matcher.quoteReplacement("AE"));
		newValue = LARGE_OE_PATTERN.matcher(newValue).replaceAll(Matcher.quoteReplacement("OE"));
		newValue = LARGE_UE_PATTERN.matcher(newValue).replaceAll(Matcher.quoteReplacement("UE"));
		newValue = LARGE_SS_PATTERN.matcher(newValue).replaceAll(Matcher.quoteReplacement("SS"));

		return newValue;
	}
}
