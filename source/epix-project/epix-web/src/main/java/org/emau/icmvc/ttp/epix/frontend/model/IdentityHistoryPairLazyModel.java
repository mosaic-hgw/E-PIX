package org.emau.icmvc.ttp.epix.frontend.model;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.emau.icmvc.ttp.epix.common.exception.InvalidParameterException;
import org.emau.icmvc.ttp.epix.common.exception.MPIException;
import org.emau.icmvc.ttp.epix.common.exception.UnknownObjectException;
import org.emau.icmvc.ttp.epix.common.model.IdentityHistoryDTO;
import org.emau.icmvc.ttp.epix.common.model.PossibleMatchHistoryDTO;
import org.emau.icmvc.ttp.epix.common.model.enums.FieldName;
import org.emau.icmvc.ttp.epix.common.model.enums.IdentityField;
import org.emau.icmvc.ttp.epix.common.model.enums.IdentityHistoryEvent;
import org.emau.icmvc.ttp.epix.common.utils.PaginationConfig;
import org.emau.icmvc.ttp.epix.frontend.controller.component.DomainSelector;
import org.emau.icmvc.ttp.epix.service.EPIXManagementService;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

public class IdentityHistoryPairLazyModel extends AbstractLazyDataModel<IdentityHistoryPair>
{
	private static final long serialVersionUID = 1104825405795685955L;

	public IdentityHistoryPairLazyModel(EPIXManagementService management, DomainSelector domainSelector, String birthDateFormat, String creationTimeFormat)
	{
		super(management, domainSelector, birthDateFormat, creationTimeFormat);
	}

	@Override
	protected Long toId(IdentityHistoryPair entity)
	{
		return entity.getNewIdentity().getHistoryId();
	}

	@Override
	public List<IdentityHistoryPair> load(int firstEntry, int pageSize, Map<String, SortMeta> sortFields, Map<String, FilterMeta> filters)
	{
		logger.debug("load: firstEntry={}, pageSize={}, sorter={}, filter={}", firstEntry, pageSize, sortFields, filters);
		List<IdentityHistoryPair> result = new ArrayList<>();
		boolean caseSensitive = false;

		// identity fields (by default searches with one pattern per explicitly named field linked by AND)
		Map<IdentityField, String> identityFilter = new HashMap<>();
		// events (restricts the search to explicitly named event types linked by OR)
		Set<IdentityHistoryEvent> eventFilter = new HashSet<>();

		for (Entry<String, FilterMeta> filter : filters.entrySet())
		{
			Object filterValue = filter.getValue().getFilterValue();
			if (filterValue == null || filterValue.toString().isEmpty())
			{
				continue;
			}

			String filterKey = filter.getKey();
			if ("event".equals(filterKey))
			{
				if (filterValue instanceof String[])
				{
					Arrays.stream((String[]) filterValue).map(IdentityHistoryEvent::valueOf).forEach(eventFilter::add);
				}
				logger.debug("load: search for given events only: events={}", eventFilter);
			}
			else if (FilterMeta.GLOBAL_FILTER_KEY.equals(filterKey))
			{
				// global filter (searches a global pattern in all required fields linked by OR)
				logger.debug("load: search with global filtering: pattern={}", filterValue);
				identityFilter.put(IdentityField.NONE, toSQLQueryPattern(filterValue));
			}
			else
			{
				IdentityField filterField = toIdentityField(filterKey);

				if (filterField != null && !filterField.equals(IdentityField.NONE))
				{
					logger.debug("load: search for custom field: field={} value={}", filterField, filterValue);
					identityFilter.put(filterField, toSQLQueryPattern(filterValue));
				}
			}
		}

		try
		{
			SortMeta sortMeta = sortFields.values().iterator().next(); // contains exactly one sortMeta for the date
			PaginationConfig pc = PaginationConfig.builder()
					.withPagination(firstEntry, pageSize)
					.withIdentityFilter(identityFilter, true, caseSensitive)
					.withEventFilter(eventFilter)
					.withSorting(toIdentityField(sortMeta.getField()), sortMeta.getOrder().equals(SortOrder.ASCENDING))
					.withDateTimeFormats(birthDateFormat, creationTimeFormat).build();

			// translates searching for a global pattern marked with key NONE to all required fields with the same pattern linked by OR
			detectAndConfigureGlobalFilteringWithRequiredFields(pc);
			detectAndConfigureGenderFilteringWithGenderSymbols(pc);

			boolean sameQueryRowCount = isSameQueryWhenPagingAndSortingIsIgnored(pc);
			if (registerQuery(pc))
			{
				return getLastResult();
			}

			for (IdentityHistoryDTO identityHistoryEntry : management.getIdentityHistoriesForDomainPaginated(getDomainName(), pc))
			{
				IdentityHistoryPair pair = toIdentityHistoryPair(identityHistoryEntry);
				if (pair != null)
				{
					if (pair.getNewMpi() == null)
					{
						pair.setNewMpi(management.getPersonById(identityHistoryEntry.getPersonId()).getMpiId().getValue());
					}
					result.add(pair);
				}
			}

			// do not recount when only the paging parameters have changed (firstEntry, pageSize)
			if (getRowCount() <= 0 || !sameQueryRowCount)
			{
				// query count of ALL filtered identity history entries wrt the filters (not only for the current page(s))
				setRowCount((int) management.countIdentityHistoriesForDomain(getDomainName(), pc));
			}

			updateResult(result);
		}
		catch (InvalidParameterException | UnknownObjectException e)
		{
			logger.error(e.getLocalizedMessage());
		}

		return result;
	}

	/**
	 * Find partner for history identity and return as pair
	 *
	 * @param entry the history entry to find the partner for
	 * @return the pair with the partner
	 */
	private IdentityHistoryPair toIdentityHistoryPair(IdentityHistoryDTO entry)
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
					return toMergePair(entry);
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
	 * @param entry the identity history entry
	 * @return the merge pair with the exact partner identity at the time of the merge
	 * @throws InvalidParameterException
	 * @throws UnknownObjectException
	 * @throws MPIException
	 */
	private IdentityHistoryPair toMergePair(IdentityHistoryDTO entry) throws InvalidParameterException, UnknownObjectException, MPIException
	{
		PossibleMatchHistoryDTO possibleMatchHistory = getPossibleMatchHistory(entry);

		if (possibleMatchHistory == null)
		{
			logger.debug("Could not find possibleMatchHistoryDTO for IdentityHistoryDTO {}", entry);
			return new IdentityHistoryPair(entry, null, entry.getMatchingScore(), entry.getComment());
		}

		// Get the partner id of the merge
		Long winningId = null;
		if (possibleMatchHistory.getIdentity1Id() == entry.getIdentityId())
		{
			winningId = possibleMatchHistory.getIdentity2Id();
		}
		else if (possibleMatchHistory.getIdentity2Id() == entry.getIdentityId())
		{
			winningId = possibleMatchHistory.getIdentity1Id();
		}

		if (winningId == null)
		{
			logger.warn("Could not find winningIdentityHistory for possibleMatchHistory {}", possibleMatchHistory);
			return null;
		}

		// Return the exact identity at the time of the merge
		IdentityHistoryDTO winningIdentity = getIdentityAtTimestamp(winningId, new Date(entry.getHistoryTimestamp().getTime() + 1000));
		return new IdentityHistoryPair(winningIdentity, entry, entry.getMatchingScore(), entry.getComment());
	}

	/**
	 * Get the possible match history entry with the highest threshold for the given identity and timestamp
	 *
	 * @param entry the identity history entry
	 * @return the possible match history entry with the highest threshold for the given identity and timestamp
	 * @throws UnknownObjectException
	 * @throws InvalidParameterException
	 */
	private PossibleMatchHistoryDTO getPossibleMatchHistory(IdentityHistoryDTO entry) throws InvalidParameterException, UnknownObjectException
	{
		for (PossibleMatchHistoryDTO possibleMatchHistory : management.getPossibleMatchHistoryForUpdatedIdentity(entry.getIdentityId()))
		{
			if (entry.getHistoryTimestamp().compareTo(possibleMatchHistory.getHistoryTimestamp()) == 0)
			{
				return possibleMatchHistory;
			}
		}
		return null;
	}

	/**
	 * Get the exact identity at the given timestamp.
	 *
	 * @param identityId the identity id
	 * @param timestamp the timestamp
	 * @return the exact identity at the given timestamp.
	 * @throws UnknownObjectException
	 */
	private IdentityHistoryDTO getIdentityAtTimestamp(Long identityId, Date timestamp) throws UnknownObjectException
	{
		IdentityHistoryDTO result = null;

		// search the complete history of the given identity id
		for (IdentityHistoryDTO identityHistoryEntry : management.getHistoryForIdentity(identityId))
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
	 * @param entry the identity history entry
	 * @return the match pair
	 */
	private IdentityHistoryPair getMatchPair(IdentityHistoryDTO entry) throws UnknownObjectException
	{
		// TODO: muss Quelle oder so noch berücksichtigt werden um Referenzidentität zu bestimmen?

		IdentityHistoryDTO oldIdentity = null;

		// durchsuche alle identityHistoryEinträge für die personId von entry
		for (IdentityHistoryDTO e : management.getIdentityHistoryByPersonId(entry.getPersonId()))
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

	protected void detectAndConfigureGlobalFilteringWithRequiredFields(PaginationConfig pg) throws InvalidParameterException, UnknownObjectException
	{
		List<IdentityField> fields = FieldName.toIdentityFields(management.getConfigurationForDomain(getDomainName()).getRequiredFields());
		pg.detectAndConfigureGlobalFiltering(new HashSet<>(fields));
	}

	protected void detectAndConfigureGenderFilteringWithGenderSymbols(PaginationConfig pg) throws InvalidParameterException, UnknownObjectException
	{
		pg.detectAndConfigureGenderFiltering(getGenderStrings());
	}
}
