package org.emau.icmvc.ttp.epix.common.utils;

/*-
 * ###license-information-start###
 * gPAS - a Generic Pseudonym Administration Service
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

public class StatisticKeys
{
	public static final String CALCULATION_TIME = "calculation_time";
	public static final String DOMAINS = "domains";
	public static final String PERSONS = "persons";
	public static final String IDENTITIES = "identities";
	public static final String POSSIBLE_MATCHES_OPEN = "possible_matches";
	public static final String POSSIBLE_MATCHES_MERGED = "possible_matches_merged";
	public static final String POSSIBLE_MATCHES_SPLIT = "possible_matches_separated";
	public static final String IDENTITY_PERFECT_MATCH = "identity_perfect_match";
	public static final String IDENTITY_MATCH = "identity_match";
	public static final String IDENTITY_POSSIBLE_MATCH = "identity_possible_match";
	public static final String IDENTITY_NO_MATCH = "identity_no_match";
	public static final String PER_DOMAIN = ".per_domain.";
	
	private String key;

	public StatisticKeys(String key)
	{
		this.key = key;
	}

	public StatisticKeys perDomain(String domainName)
	{
		key += PER_DOMAIN + domainName;
		return this;
	}

	public String build()
	{
		return key;
	}
}
