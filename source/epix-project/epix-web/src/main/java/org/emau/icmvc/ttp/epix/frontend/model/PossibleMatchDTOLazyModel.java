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

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.emau.icmvc.ttp.epix.common.exception.InvalidParameterException;
import org.emau.icmvc.ttp.epix.common.exception.UnknownObjectException;
import org.emau.icmvc.ttp.epix.common.model.PossibleMatchDTO;
import org.emau.icmvc.ttp.epix.common.model.enums.IdentityField;
import org.emau.icmvc.ttp.epix.common.model.enums.PossibleMatchPriority;
import org.emau.icmvc.ttp.epix.common.utils.PaginationConfig;
import org.emau.icmvc.ttp.epix.frontend.controller.component.DomainSelector;
import org.emau.icmvc.ttp.epix.service.EPIXService;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;

/**
 * A lazy data model for possible matches. Some inspiration may be found in
 * 'org.emau.icmvc.ganimed.ttp.cm2.frontend.model.WebConsentLazyModel' in gICS
 */
public class PossibleMatchDTOLazyModel extends AbstractLazyDataModel<PossibleMatchDTO>
{
	private final Set<PossibleMatchPriority> priorities = new HashSet<>();

	public PossibleMatchDTOLazyModel(EPIXService service, DomainSelector domainSelector, String birthDateFormat, String creationTimeFormat)
	{
		super(service, domainSelector, birthDateFormat, creationTimeFormat);
	}

	@Override
	protected Long toId(PossibleMatchDTO entity)
	{
		return entity.getLinkId();
	}

	@Override
	public List<PossibleMatchDTO> load(int first, int pageSize, Map<String, SortMeta> sortMetaMap, Map<String, FilterMeta> filterMetaMap)
	{
		logger.debug("load: first={}, pageSize={}, sorter={}, filter={}", first, pageSize, sortMetaMap, filterMetaMap);
		List<PossibleMatchDTO> possibleMatches = new ArrayList<>();
		boolean caseSensitive = false;

		try
		{
			PaginationConfig pc = PaginationConfig.builder()
					.withPagination(first, pageSize)
					.withDateTimeFormats(birthDateFormat, creationTimeFormat)
					.withPriorityFilter(getPriorities())
					.build();

			FilterMeta globalFilter = filterMetaMap.get(FilterMeta.GLOBAL_FILTER_KEY);

			if (globalFilter != null || filterMetaMap.isEmpty())
			{
				// if the global filter is set, all other filters will be ignored
				String pattern = globalFilter != null ? toSQLQueryPattern(globalFilter.getFilterValue()) : null;
				pc.setGlobalFieldFilter(pattern);
				pc.setGlobalFieldFilterCaseSensitive(caseSensitive);
				pc.setCreateTimestampFilter(pattern);
			}
			else
			{
				Map<IdentityField, String> filter = new EnumMap<>(IdentityField.class);

				for (FilterMeta meta : filterMetaMap.values())
				{
					IdentityField field = toIdentityField(meta.getField());
					String pattern = toSQLQueryPattern(meta.getFilterValue());

					if (field != null && StringUtils.isNotEmpty(pattern))
					{
						filter.put(field, pattern);
					}
				}
				pc.setIdentityFilter(filter);
			}

			boolean sameQueryRowCount = isSameQueryWhenPagingAndSortingIsIgnored(pc);

			if (registerQuery(pc))
			{
				setRowCount(getLastRowCount()); // see comment in #count()
				return getLastResult();
			}

			logger.debug("load: query possible matches: {}", pc);
			possibleMatches.addAll(service.getPossibleMatchesForDomainFiltered(getDomainName(), pc));

			// do not recount when only the paging parameters have changed (first, pageSize)
			if (getRowCount() <= 0 || !sameQueryRowCount)
			{
				// query count of ALL filtered possible matches wrt the pattern (not only for the current page(s))
				logger.debug("load: count possible matches: {}", pc);
				setRowCount((int) service.countPossibleMatchesForDomainFiltered(getDomainName(), pc));
			}

			updateResult(possibleMatches);
		}
		catch (InvalidParameterException e)
		{
			logger.error("Invalid criteria for loading possible matches", e);
		}
		catch (UnknownObjectException e)
		{
			logger.debug("No possible matches found for the given filter criteria", e);
		}

		return possibleMatches;
	}

	public Set<PossibleMatchPriority> getPriorities()
	{
		return new HashSet<>(priorities);
	}

	public void setPriorities(Set<PossibleMatchPriority> priorities)
	{
		this.priorities.clear();
		if (priorities != null)
		{
			this.priorities.addAll(priorities.stream().filter(Objects::nonNull).collect(Collectors.toSet()));
		}
		triggerDataChanged();
	}

	public void setPriority(PossibleMatchPriority priority)
	{
		setPriorities(Collections.singleton(priority));
	}
}
