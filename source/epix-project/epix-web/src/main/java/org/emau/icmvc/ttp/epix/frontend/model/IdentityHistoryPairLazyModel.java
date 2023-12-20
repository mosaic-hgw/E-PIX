package org.emau.icmvc.ttp.epix.frontend.model;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.emau.icmvc.ttp.epix.common.exception.InvalidParameterException;
import org.emau.icmvc.ttp.epix.common.exception.UnknownObjectException;
import org.emau.icmvc.ttp.epix.common.model.IdentityHistoryDTO;
import org.emau.icmvc.ttp.epix.common.model.enums.IdentityField;
import org.emau.icmvc.ttp.epix.common.model.enums.IdentityHistoryEvent;
import org.emau.icmvc.ttp.epix.common.utils.PaginationConfig;
import org.emau.icmvc.ttp.epix.frontend.controller.component.DomainSelector;
import org.emau.icmvc.ttp.epix.frontend.util.HistoryHelper;
import org.emau.icmvc.ttp.epix.service.EPIXManagementService;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

public class IdentityHistoryPairLazyModel extends AbstractLazyDataModel<IdentityHistoryPair>
{
	@Serial
	private static final long serialVersionUID = 1104825405795685955L;

	public IdentityHistoryPairLazyModel(EPIXManagementService management, HistoryHelper historyHelper, DomainSelector domainSelector, String birthDateFormat, String creationTimeFormat)
	{
		super(management, historyHelper, domainSelector, birthDateFormat, creationTimeFormat);
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
					.withDateTimeFormats(birthDateFormat, creationTimeFormat)
					.withIdentityGenderStrings(getGenderStrings()).build();

			boolean sameQueryRowCount = isSameQueryWhenPagingAndSortingIsIgnored(pc);
			if (registerQuery(pc))
			{
				setRowCount(getLastRowCount()); // see comment in #count()
				return getLastResult();
			}

			for (IdentityHistoryDTO identityHistoryEntry : management.getIdentityHistoriesForDomainPaginated(getDomainName(), pc))
			{
				IdentityHistoryPair pair = historyHelper.toIdentityHistoryPair(identityHistoryEntry);
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
}
