package org.emau.icmvc.ttp.epix.common.utils;

/*-
 * ###license-information-start###
 * gPAS - a Generic Pseudonym Administration Service
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

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.emau.icmvc.ttp.epix.common.model.enums.Gender;
import org.emau.icmvc.ttp.epix.common.model.enums.IdentityField;
import org.emau.icmvc.ttp.epix.common.model.enums.IdentityHistoryEvent;
import org.emau.icmvc.ttp.epix.common.model.enums.PossibleMatchSolution;

/**
 * parameter for get...Paginated functions
 * <p>
 * <b>firstEntry</b><br>
 * default = 0
 * <p>
 * <b>pageSize</b><br>
 * default = 10
 * <p>
 * <b>filter</b><br>
 * default = empty map<br>
 * filter values; for possible keys see {@link IdentityField}
 * <p>
 * <b>startDate</b><br>
 * default = null<br>
 * consents with date >= startDate are returned
 * <p>
 * <b>endDate</b><br>
 * default = null<br>
 * consents with date <= endDate are returned
 * <p>
 * <b>templateType</b><br>
 * default = null<br>
 * <p>
 * <b>filterAreTreatedAsConjunction</b><br>
 * default = true<br>
 * has no effect on template type
 * <p>
 * <b>filterIsCaseSensitive</b><br>
 * default = false
 * <p>
 * <b>sortField</b><br>
 * see {@link IdentityField}<br>
 * default = null
 * <p>
 * <b>sortIsAscending</b><br>
 * default = true
 * <p>
 *
 * @author geidell, moser
 *
 */
public class PaginationConfig implements Serializable
{
	private static final long serialVersionUID = 5400581744252774912L;

	private int firstEntry = 0;
	private int pageSize = 0;
	private final Set<IdentityHistoryEvent> eventFilter = new HashSet<>();
	private final Set<PossibleMatchSolution> solutionFilter = new HashSet<>();
	private final Map<IdentityField, String> identityFilter = new HashMap<>();
	private boolean identityFilterAsConjunction = true;
	private boolean identityFilterCaseSensitive = false;
	private IdentityField sortField = null;
	private boolean sortIsAscending = true;
	private String dateFormat;
	private String dateTimeFormat;

	public PaginationConfig()
	{
	}

	public PaginationConfig(Map<IdentityField, String> identityFilter, boolean identityFilterAsConjunction, boolean identityFilterCaseSensitive)
	{
		setIdentityFilter(identityFilter);
		this.identityFilterAsConjunction = identityFilterAsConjunction;
		this.identityFilterCaseSensitive = identityFilterCaseSensitive;
	}

	public boolean isUsingPagination()
	{
		return pageSize > 0;
	}

	public int getFirstEntry()
	{
		return firstEntry;
	}

	public void setFirstEntry(int firstEntry)
	{
		this.firstEntry = firstEntry;
	}

	public int getPageSize()
	{
		return pageSize;
	}

	public void setPageSize(int pageSize)
	{
		this.pageSize = pageSize;
	}

	public boolean isUsingSorting()
	{
		return sortField != null;

	}
	public IdentityField getSortField()
	{
		return sortField;
	}

	public void setSortField(IdentityField sortField)
	{
		this.sortField = sortField;
	}

	public boolean isSortIsAscending()
	{
		return sortIsAscending;
	}

	public void setSortIsAscending(boolean sortIsAscending)
	{
		this.sortIsAscending = sortIsAscending;
	}

	public boolean isUsingEventFiltering()
	{
		return !eventFilter.isEmpty();
	}

	public Set<IdentityHistoryEvent> getEventFilter()
	{
		return Collections.unmodifiableSet(eventFilter);
	}

	public void setEventFilter(Set<IdentityHistoryEvent> eventFilter)
	{
		this.eventFilter.clear();
		if (eventFilter != null)
		{
			this.eventFilter.addAll(eventFilter.stream().filter(Objects::nonNull).collect(Collectors.toSet()));
		}
	}

	public boolean isUsingSolutionFiltering()
	{
		return !solutionFilter.isEmpty();
	}

	public Set<PossibleMatchSolution> getSolutionFilter()
	{
		return Collections.unmodifiableSet(solutionFilter);
	}

	public void setSolutionFilter(Set<PossibleMatchSolution> solutionFilter)
	{
		this.solutionFilter.clear();
		if (solutionFilter != null)
		{
			this.solutionFilter.addAll(solutionFilter.stream().filter(Objects::nonNull).collect(Collectors.toSet()));
		}
	}

	public boolean isUsingIdentityFiltering()
	{
		return !identityFilter.isEmpty();
	}

	public Map<IdentityField, String> getIdentityFilter()
	{
		return Collections.unmodifiableMap(identityFilter);
	}

	public void setIdentityFilter(Map<IdentityField, String> identityFilter)
	{
		this.identityFilter.clear();
		if (identityFilter != null)
		{
			identityFilter.entrySet().stream().filter(e -> e.getKey() != null && e.getValue() != null && !e.getValue().isEmpty())
					.forEach(e -> this.identityFilter.put(e.getKey(), e.getValue()));
		}
	}

	public boolean isIdentityFilterAsConjunction()
	{
		return identityFilterAsConjunction;
	}

	public void setIdentityFilterAsConjunction(boolean identityFilterAsConjunction)
	{
		this.identityFilterAsConjunction = identityFilterAsConjunction;
	}

	public boolean isIdentityFilterCaseSensitive()
	{
		return identityFilterCaseSensitive;
	}

	public void setIdentityFilterCaseSensitive(boolean identityFilterCaseSensitive)
	{
		this.identityFilterCaseSensitive = identityFilterCaseSensitive;
	}

	public String getDateFormat()
	{
		return dateFormat;
	}

	public void setDateFormat(String dateFormat)
	{
		this.dateFormat = dateFormat;
	}

	public String getDateTimeFormat()
	{
		return dateTimeFormat;
	}

	public void setDateTimeFormat(String dateTimeFormat)
	{
		this.dateTimeFormat = dateTimeFormat;
	}

	/**
	 * If {@link IdentityField#NONE} is the only key in the identity filter map with a non-empty pattern value,
	 * then configure the filter to search in all given global filter fields linked by OR (disjunction),
	 * otherwise leave the filter configuration untouched.
	 *
	 * @param globalFilterFields the fields to search in when using global identity field filtering
	 *
	 * @return true when global filtering was detected and configured
	 */
	public boolean detectAndConfigureGlobalFiltering(Set<IdentityField> globalFilterFields)
	{
		final String pattern = identityFilter.get(IdentityField.NONE);
		if (identityFilter.size() == 1 && pattern != null && !pattern.isEmpty())
		{
			// replace identity the filter map with the NONE key by a map with
			// all given fields as keys and the same global search pattern
			setIdentityFilter(globalFilterFields.stream().collect(Collectors.toMap(f -> f, f -> pattern, (a, b) -> b)));
			setIdentityFilterAsConjunction(false);
			return true;
		}
		return false;
	}

	/**
	 * If {@link IdentityField#GENDER} is a key in the identity fields filter map, then replace the (localized) gender strings
	 * by all gender symbol keys (as defined in {@link Gender}) for which the value's pattern matches the gender string value.
	 *
	 * @param genderStrings a map with genders as keys and the localized strings as values
	 * @return true when the identity gender pattern has changed
	 */
	public boolean detectAndConfigureGenderFiltering(Map<Gender, String> genderStrings)
	{
		String pattern = identityFilter.get(IdentityField.GENDER);
		if (pattern != null && !pattern.isEmpty())
		{
			// replace gender string by comma-separated gender symbols
			// all given fields as keys and the same global search pattern
			final String p = pattern.replace("*", "").replace("%", "");
			String genders;

			if (isIdentityFilterCaseSensitive())
			{
				genders = genderStrings.entrySet().stream().filter(e -> e.getValue().contains(p))
							.map(e -> e.getKey().name()).collect(Collectors.joining(","));
			}
			else
			{
				genders = genderStrings.entrySet().stream().filter(e -> e.getValue().toLowerCase().contains(p.toLowerCase()))
						.map(e -> e.getKey().name()).collect(Collectors.joining(","));
			}
			if (!genders.isEmpty())
			{
				identityFilter.put(IdentityField.GENDER, genders);
			}
			else
			{
				identityFilter.remove(IdentityField.GENDER);
			}
			return true;
		}
		return false;
	}

	public boolean isGlobalFiltering()
	{
		return getGlobalFilterPattern() != null;
	}

	public String getGlobalFilterPattern()
	{
		if (isIdentityFilterAsConjunction())
		{
			return null;
		}

		Set<String> patternsWithoutGenderPattern = identityFilter.entrySet().stream().filter(e -> !IdentityField.GENDER.equals(e.getKey())).map(Map.Entry::getValue).collect(Collectors.toSet());

		if (patternsWithoutGenderPattern.size() != 1) {
			return null;
		}

		return patternsWithoutGenderPattern.stream().findFirst().orElse(null);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + firstEntry;
		result = prime * result + pageSize;
		result = prime * result + eventFilter.hashCode();
		result = prime * result + identityFilter.hashCode();
		result = prime * result + solutionFilter.hashCode();
		result = prime * result + (identityFilterAsConjunction ? 1231 : 1237);
		result = prime * result + (identityFilterCaseSensitive ? 1231 : 1237);
		result = prime * result + (sortField == null ? 0 : sortField.hashCode());
		result = prime * result + (sortIsAscending ? 1231 : 1237);
		result = prime * result + (dateFormat == null ? 0 : dateFormat.hashCode());
		result = prime * result + (dateTimeFormat == null ? 0 : dateTimeFormat.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!equalsWhenPagingAndSortingIsIgnored(obj))
		{
			return false;
		}
		// now we know for sure that 'obj' is not null and an instance of PaginationConfig
		PaginationConfig other = (PaginationConfig) obj;
		if (firstEntry != other.firstEntry)
		{
			return false;
		}
		if (pageSize != other.pageSize)
		{
			return false;
		}
		if (sortField != other.sortField)
		{
			return false;
		}
		if (sortIsAscending != other.sortIsAscending)
		{
			return false;
		}
		return true;
	}

	public boolean equalsWhenPagingAndSortingIsIgnored(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		PaginationConfig other = (PaginationConfig) obj;
		if (!identityFilter.equals(other.identityFilter))
		{
			return false;
		}
		if (!eventFilter.equals(other.eventFilter))
		{
			return false;
		}
		if (!solutionFilter.equals(other.solutionFilter))
		{
			return false;
		}
		if (identityFilterAsConjunction != other.identityFilterAsConjunction)
		{
			return false;
		}
		if (identityFilterCaseSensitive != other.identityFilterCaseSensitive)
		{
			return false;
		}
		if (!Objects.equals(dateFormat, other.dateFormat))
		{
			return false;
		}
		if (!Objects.equals(dateTimeFormat, other.dateTimeFormat))
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		return "PaginationConfig [" + String.join(", ",
				isUsingPagination() ? "firstEntry=" + firstEntry + "pageSize=" + pageSize : "",
				isUsingEventFiltering() ? ", eventFilter=" + eventFilter : "",
				isUsingSolutionFiltering() ? ", solutionFilter=" + solutionFilter : "",
				isUsingIdentityFiltering() ? ", identityFilter=" + identityFilter + ", filterAsConjunction=" + identityFilterAsConjunction + ", filterCaseSensitive=" + identityFilterCaseSensitive : "",
				isUsingSorting() ? ", sortField=" + sortField + ", sortIsAscending=" + sortIsAscending : "",
				"dateFormat=" + dateFormat + ", dateTimeFormat=" + dateTimeFormat)
				+ "]";
	}

	/**
	 * Creates a builder for the pagination config (see Builder-Pattern).
	 *
	 * @return a builder for the pagination config.
	 */
	public static Builder builder()
	{
		return new Builder();
	}

	public static class Builder
	{
		PaginationConfig pg = new PaginationConfig();

		public PaginationConfig build()
		{
			return pg;
		}

		public Builder withEventFilter(Set<IdentityHistoryEvent> eventFilter)
		{
			pg.setEventFilter(eventFilter);
			return this;
		}

		public Builder withSolutionFilter(Set<PossibleMatchSolution> possibleMatchSolutions)
		{
			pg.setSolutionFilter(possibleMatchSolutions);
			return this;
		}

		public Builder withIdentityFilter(Map<IdentityField, String> identityFilters, boolean asConjunction, boolean caseSensitive)
		{
			pg.setIdentityFilter(identityFilters);
			pg.setIdentityFilterAsConjunction(asConjunction);
			pg.setIdentityFilterCaseSensitive(caseSensitive);
			return this;
		}

		public Builder withSorting(IdentityField sortField, boolean ascending)
		{
			pg.setSortField(sortField);
			pg.setSortIsAscending(ascending);
			return this;
		}

		public Builder withPagination(int firstEntry, int pageSize)
		{
			pg.setFirstEntry(firstEntry);
			pg.setPageSize(pageSize);
			return this;
		}

		public Builder withDateTimeFormats(String dateFormat, String dateTimeFormat)
		{
			pg.setDateFormat(dateFormat);
			pg.setDateTimeFormat(dateTimeFormat);
			return this;
		}
	}
}
