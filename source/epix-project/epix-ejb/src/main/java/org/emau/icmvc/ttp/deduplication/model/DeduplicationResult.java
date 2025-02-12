package org.emau.icmvc.ttp.deduplication.model;

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


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import org.emau.icmvc.ttp.epix.internal.PreprocessedCacheObject;

public class DeduplicationResult
{
	private final List<MatchResult> possibleMatches = new ArrayList<>();
	private final List<MatchResult> matches = new ArrayList<>();
	private final PreprocessedCacheObject matchable;
	private final LongSet matchPersonIDs = new LongOpenHashSet();
	private final LongSet possibleMatchPersonIDs = new LongOpenHashSet();

	public DeduplicationResult(PreprocessedCacheObject matchable)
	{
		super();
		this.matchable = matchable;
	}

	public List<MatchResult> getPossibleMatches()
	{
		return possibleMatches;
	}

	public List<MatchResult> getMatches()
	{
		return matches;
	}

	public void addMatchResult(MatchResult matchResult)
	{
		switch (matchResult.getDecision())
		{
			case MATCH:
				matches.add(matchResult);
				matchPersonIDs.add(matchResult.getComparativeValue().getPersonId());
				break;
			case POSSIBLE_MATCH:
				possibleMatches.add(matchResult);
				possibleMatchPersonIDs.add(matchResult.getComparativeValue().getPersonId());
				break;
			default:
		}
	}

	public boolean hasNoResults()
	{
		return possibleMatches.isEmpty() && matches.isEmpty();
	}

	public boolean hasMatches()
	{
		return !matches.isEmpty();
	}

	public boolean hasPossibleMatches()
	{
		return !possibleMatches.isEmpty();
	}

	public boolean hasUniqueMatch()
	{
		return matchPersonIDs.size() == 1;
	}

	public long getUniqueMatchPersonId()
	{
		long result = -1L;
		if (hasUniqueMatch())
		{
			result = matches.get(0).getComparativeValue().getPersonId();
		}
		return result;
	}

	public boolean hasUniquePossibleMatch()
	{
		return possibleMatchPersonIDs.size() == 1;
	}

	public long getUniquePossibleMatchPersonId()
	{
		long result = -1L;
		if (hasUniquePossibleMatch())
		{
			result = possibleMatches.get(0).getComparativeValue().getPersonId();
		}
		return result;
	}

	public List<MatchResult> getMatchResults()
	{
		List<MatchResult> m = new ArrayList<>();
		m.addAll(matches);
		m.addAll(possibleMatches);
		return m;
	}

	public LongSet getMatchPersonIDs()
	{
		return new LongOpenHashSet(matchPersonIDs);
	}

	public LongSet getPossibleMatchPersonIDs()
	{
		return new LongOpenHashSet(possibleMatchPersonIDs);
	}

	public PreprocessedCacheObject getMatchable()
	{
		return matchable;
	}

	public double getHighestMatchingScore()
	{
		double result = 0.0;
		for (MatchResult matchResult : matches)
		{
			if (matchResult.getRatio() > result)
			{
				result = matchResult.getRatio();
			}
		}
		if (result == 0.0)
		{
			for (MatchResult matchResult : possibleMatches)
			{
				if (matchResult.getRatio() > result)
				{
					result = matchResult.getRatio();
				}
			}
		}
		return result;
	}

	public double getHighestMatchingScoreForPersonId(long personId)
	{
		double result = 0.0;
		for (MatchResult matchResult : matches)
		{
			if ((matchResult.getComparativeValue().getPersonId() == personId) && matchResult.getRatio() > result)
			{
				result = matchResult.getRatio();
			}
		}
		if (result == 0.0)
		{
			for (MatchResult matchResult : possibleMatches)
			{
				if ((matchResult.getComparativeValue().getPersonId() == personId) && matchResult.getRatio() > result)
				{
					result = matchResult.getRatio();
				}
			}
		}
		return result;
	}

	/**
	 * {@return the match result with the highest matching score or null if there is no such result}
	 * @param decision one of {@link MatchResult.DECISION#MATCH} or {@link MatchResult.DECISION#POSSIBLE_MATCH}
	 * @param excludeIdentityId an id of an identity to exclude match results for
	 */
	public MatchResult getMatchResultWithHighestMatchingScore(MatchResult.DECISION decision, long excludeIdentityId)
	{
		List<MatchResult> results = switch (decision)
		{
			case MATCH -> matches;
			case POSSIBLE_MATCH -> possibleMatches;
			default -> new ArrayList<>();
		};

		return results.stream()
				.filter(r -> r.getDecision() == decision)
				.filter(r -> r.getComparativeValue().getIdentityId() != excludeIdentityId)
				.max(Comparator.comparingDouble(MatchResult::getRatio)).orElse(null);
	}
}
