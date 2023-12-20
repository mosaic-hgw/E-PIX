package org.emau.icmvc.ttp.epix.frontend.util;

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

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.emau.icmvc.ttp.epix.common.exception.InvalidParameterException;
import org.emau.icmvc.ttp.epix.common.exception.MPIException;
import org.emau.icmvc.ttp.epix.common.exception.UnknownObjectException;
import org.emau.icmvc.ttp.epix.common.model.IdentityHistoryDTO;
import org.emau.icmvc.ttp.epix.common.model.PossibleMatchHistoryDTO;
import org.emau.icmvc.ttp.epix.common.model.enums.IdentityHistoryEvent;
import org.emau.icmvc.ttp.epix.frontend.controller.common.AbstractEpixServiceBean;
import org.emau.icmvc.ttp.epix.frontend.model.IdentityHistoryPair;

@ManagedBean(name = "historyHelper")
@SessionScoped
public class HistoryHelper extends AbstractEpixServiceBean implements Serializable
{
	@Serial private static final long serialVersionUID = 6840626136053940980L;

	/**
	 * Find partner for history identity and return as pair
	 *
	 * @param entry
	 * 		the history entry to find the partner for
	 * @return the pair with the partner
	 */
	public IdentityHistoryPair toIdentityHistoryPair(IdentityHistoryDTO entry)
	{
		try
		{
			switch (entry.getEvent())
			{
				case MATCH:
				case FORCED_MATCH:
					entry.setEvent(IdentityHistoryEvent.MATCH);
					return getMatchPair(entry);
				case PERFECT_MATCH:
					return getMatchPair(entry);
				case MERGE:
					return getMergePairForIdentity(entry);
				default:
					return new IdentityHistoryPair(entry);
			}
		}
		catch (InvalidParameterException | UnknownObjectException | MPIException e)
		{
			return new IdentityHistoryPair(entry);
		}
	}

	/**
	 * Get the exact partner identity at the time of the merge
	 *
	 * @param entry
	 * 		the identity history entry
	 * @return the merge pair with the exact partner identity at the time of the merge
	 * @throws InvalidParameterException
	 * @throws UnknownObjectException
	 * @throws MPIException
	 */
	private IdentityHistoryPair getMergePairForIdentity(IdentityHistoryDTO entry) throws InvalidParameterException, UnknownObjectException, MPIException
	{
		PossibleMatchHistoryDTO possibleMatchHistory = getPossibleMatchHistory(entry);
		if (possibleMatchHistory == null)
		{
			logger.debug("Could not find possibleMatchHistoryDTO for IdentityHistoryDTO {}", entry);
			return new IdentityHistoryPair(entry, null, entry.getMatchingScore(), entry.getComment());
		}
		
		return toMergePair(possibleMatchHistory);		
	}

	public IdentityHistoryPair toMergePair(PossibleMatchHistoryDTO possibleMatchHistory) throws UnknownObjectException
	{
		// Get the partner id of the merge
		Long winningId = null;
		if (possibleMatchHistory.getIdentity1Id() == possibleMatchHistory.getUpdatedIdentityId())
		{
			winningId = possibleMatchHistory.getIdentity2Id();
		}
		else {
			winningId = possibleMatchHistory.getIdentity1Id();
		}

		// Return the exact identity at the time of the merge
		IdentityHistoryDTO winningIdentity = getIdentityAtTimestamp(winningId, new Date(possibleMatchHistory.getHistoryTimestamp().getTime() + 1000));
		IdentityHistoryDTO loosingIdentity = getIdentityAtTimestamp(possibleMatchHistory.getUpdatedIdentityId(), new Date(possibleMatchHistory.getHistoryTimestamp().getTime() + 1000));
		return new IdentityHistoryPair(winningIdentity, loosingIdentity, possibleMatchHistory.getThreshold(), possibleMatchHistory.getExplanation());
	}

	public IdentityHistoryPair toSplitPair(PossibleMatchHistoryDTO possibleMatchHistory) throws UnknownObjectException
	{
		// Return the exact identity at the time of the merge
		IdentityHistoryDTO winningIdentity = getIdentityAtTimestamp(possibleMatchHistory.getIdentity1Id(), new Date(possibleMatchHistory.getHistoryTimestamp().getTime() + 1000));
		IdentityHistoryDTO loosingIdentity = getIdentityAtTimestamp(possibleMatchHistory.getIdentity2Id(), new Date(possibleMatchHistory.getHistoryTimestamp().getTime() + 1000));
		return new IdentityHistoryPair(winningIdentity, loosingIdentity, possibleMatchHistory.getThreshold(), possibleMatchHistory.getExplanation());
	}

	/**
	 * Get the possible match history entry with the highest threshold for the given identity and timestamp
	 *
	 * @param entry
	 * 		the identity history entry
	 * @return the possible match history entry with the highest threshold for the given identity and timestamp
	 * @throws UnknownObjectException
	 * @throws InvalidParameterException
	 */
	private PossibleMatchHistoryDTO getPossibleMatchHistory(IdentityHistoryDTO entry) throws InvalidParameterException, UnknownObjectException
	{
		for (PossibleMatchHistoryDTO possibleMatchHistory : managementService.getPossibleMatchHistoryForUpdatedIdentity(entry.getIdentityId()))
		{
			if (entry.getHistoryTimestamp().compareTo(possibleMatchHistory.getHistoryTimestamp()) == 0)
			{
				return possibleMatchHistory;
			}
		}
//		// look also for winners if not in loosers
//		for (PossibleMatchHistoryDTO possibleMatchHistory : management.getPossibleMatchHistoryByIdentity(entry.getIdentityId()))
//		{
//			if (entry.getHistoryTimestamp().compareTo(possibleMatchHistory.getHistoryTimestamp()) == 0)
//			{
//				return possibleMatchHistory;
//			}
//		}
		return null;
	}

	/**
	 * Get the exact identity at the given timestamp.
	 *
	 * @param identityId
	 * 		the identity id
	 * @param timestamp
	 * 		the timestamp
	 * @return the exact identity at the given timestamp.
	 * @throws UnknownObjectException
	 */
	private IdentityHistoryDTO getIdentityAtTimestamp(Long identityId, Date timestamp) throws UnknownObjectException
	{
		IdentityHistoryDTO result = null;

		// search the complete history of the given identity id
		for (IdentityHistoryDTO identityHistoryEntry : managementService.getHistoryForIdentity(identityId))
		{
			// if identityHistoryTimestamp before or equals timestamp and (no result yet or
			// identityHistoryTimestamp after current result timestamp)
			if ((identityHistoryEntry.getHistoryTimestamp().before(timestamp) || identityHistoryEntry.getHistoryTimestamp().equals(timestamp))
					&& (result == null || identityHistoryEntry.getHistoryTimestamp().after(result.getHistoryTimestamp())))
			{
				// result so far
				result = identityHistoryEntry;
			}
		}

		return result;
	}

	/**
	 * Get the reference identity of a person at the given timestamp. This method searches
	 * for the most recent identity for the given person before the given timestamp.
	 *
	 * @param entry
	 * 		the identity history entry
	 * @return the match pair
	 */
	private IdentityHistoryPair getMatchPair(IdentityHistoryDTO entry) throws UnknownObjectException
	{
		// TODO: muss Quelle oder so noch berücksichtigt werden um Referenzidentität zu bestimmen?

		IdentityHistoryDTO oldIdentity = null;

		// durchsuche alle identityHistoryEinträge für die personId von entry
		for (IdentityHistoryDTO e : managementService.getIdentityHistoryByPersonId(entry.getPersonId()))
		{
			// wenn timestamp vor übergebenem timestamp liegt
			if (e.getHistoryTimestamp().before(entry.getHistoryTimestamp()))
			{
				// wenn noch kein ergebnis oder timestamp nach bisherigem ergebnis liegt
				if (oldIdentity == null || e.getHistoryTimestamp().after(oldIdentity.getHistoryTimestamp()))
				{
					// vorläufiges ergebnis
					oldIdentity = e;
				}
			}
		}

		return new IdentityHistoryPair(entry, oldIdentity, entry.getMatchingScore(), entry.getComment());
	}
}
