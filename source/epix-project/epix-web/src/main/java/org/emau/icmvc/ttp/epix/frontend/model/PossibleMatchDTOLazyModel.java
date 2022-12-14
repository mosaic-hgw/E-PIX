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
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.emau.icmvc.ttp.epix.common.exception.InvalidParameterException;
import org.emau.icmvc.ttp.epix.common.exception.UnknownObjectException;
import org.emau.icmvc.ttp.epix.common.model.PossibleMatchDTO;
import org.emau.icmvc.ttp.epix.common.model.enums.IdentityField;
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
		Map<IdentityField, String> filter = new EnumMap<>(IdentityField.class);

		try
		{
			FilterMeta globalFilter = filterMetaMap.get(FilterMeta.GLOBAL_FILTER_KEY);
			if (globalFilter != null)
			{
				// if the global filter is set, all other filters will be ignored
				Object filterValue = globalFilter.getFilterValue();
				String pattern = toSQLQueryPattern(filterValue);
				filter.put(IdentityField.NONE, pattern);

				PaginationConfig pc = PaginationConfig.builder()
						.withPagination(first, pageSize)
						.withIdentityFilter(filter, false, caseSensitive)
						.withDateTimeFormats(birthDateFormat, creationTimeFormat).build();

				boolean sameQueryRowCount = isSameQueryWhenPagingAndSortingIsIgnored(pc);
				if (registerQuery(pc))
				{
					return getLastResult();
				}

				logger.debug("load: with global filtering: {}", pc);
				// TODO(FMM) the service interface should be simplified to take
				//  a PaginationConfig directly instead to define a bunch of single parameters:
				//    service.getPossibleMatchesForDomainFilteredByDefaultAndPaginated(...)
				//    service.countPossibleMatchesForDomainFilteredByDefault(...)
				//    service.getPossibleMatchesForDomainFilteredAndPaginated(...)
				//    service.countPossibleMatchesForDomainFiltered(...)
				//  See org.emau.icmvc.ttp.epix.frontend.model.IdentityHistoryPairLazyModel
				possibleMatches.addAll(service.getPossibleMatchesForDomainFilteredByDefaultAndPaginated(
						getDomainName(), first, pageSize, pattern, caseSensitive, birthDateFormat, creationTimeFormat));

				// do not recount when only the paging parameters have changed (first, pageSize)
				if (getRowCount() <= 0 || !sameQueryRowCount)
				{
					// query count of ALL filtered possible matches wrt the pattern (not only for the current page(s))
					setRowCount((int) service.countPossibleMatchesForDomainFilteredByDefault(
							getDomainName(), pattern, caseSensitive, birthDateFormat, creationTimeFormat));
				}
			}
			else {
				for (FilterMeta meta : filterMetaMap.values()) {
					IdentityField field = toIdentityField(meta.getField());
					String pattern = toSQLQueryPattern(meta.getFilterValue());

					if (field != null && StringUtils.isNotEmpty(pattern))
					{
						filter.put(field, pattern);
					}
				}

				PaginationConfig pc = PaginationConfig.builder()
						.withPagination(first, pageSize)
						.withIdentityFilter(filter, false, caseSensitive)
						.withDateTimeFormats(birthDateFormat, creationTimeFormat).build();

				boolean sameQueryRowCount = isSameQueryWhenPagingAndSortingIsIgnored(pc);
				if (registerQuery(pc))
				{
					return getLastResult();
				}

				logger.debug("load: with field-specific filtering: {}", pc);
				possibleMatches.addAll(service.getPossibleMatchesForDomainFilteredAndPaginated(
						getDomainName(), first, pageSize, IdentityField.NONE, false, filter, caseSensitive));

				// do not recount when only the paging parameters have changed (first, pageSize)
				if (getRowCount() <= 0 || !sameQueryRowCount)
				{
					// query count of ALL filtered possible matches wrt the filters (not only for the current page(s))
					setRowCount((int) service.countPossibleMatchesForDomainFiltered(getDomainName(), filter, caseSensitive));
				}
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
}
