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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.util.Strings;
import org.emau.icmvc.ttp.epix.common.model.enums.Gender;
import org.emau.icmvc.ttp.epix.common.model.enums.IdentityField;
import org.emau.icmvc.ttp.epix.common.model.enums.IdentityHistoryEvent;
import org.emau.icmvc.ttp.epix.common.model.enums.PersonField;
import org.emau.icmvc.ttp.epix.common.model.enums.PossibleMatchPriority;
import org.emau.icmvc.ttp.epix.common.model.enums.PossibleMatchSolution;
import org.emau.icmvc.ttp.epix.common.model.enums.VitalStatus;

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
	private static final long serialVersionUID = 6071259667642410949L;

	// pagination
	private int firstEntry = 0;
	private int pageSize = 0;

	// filtering modes
	private String globalFieldFilter;
	private boolean globalFieldFilterCaseSensitive = false;
	private final Set<IdentityHistoryEvent> eventFilter = new HashSet<>();
	private final Set<PossibleMatchSolution> solutionFilter = new HashSet<>();
	private final Set<PossibleMatchPriority> priorityFilter = new HashSet<>();

	// identity filtering
	private final Map<IdentityField, String> identityFilter = new HashMap<>();
	private boolean identityFilterAsConjunction = true;
	private boolean identityFilterCaseSensitive = false;
	private final Map<Gender, String> identityGenderStrings = new HashMap<>();
	private final Map<VitalStatus, String> identityVitalStatusStrings = new HashMap<>();
	private IdentityField sortField = null;
	private boolean sortIsAscending = true;

	// person filtering
	private final Map<PersonField, String> personFilter = new HashMap<>();
	private boolean personFilterAsConjunction = true;
	private boolean personFilterCaseSensitive = false;

	// identity and person
	private boolean identityAndPersonFilterCombinedAsConjunction = false;

	// misc
	private String createTimestampFilter = null;
	private String dateFormat;
	private String timeFormat;

	public PaginationConfig()
	{}

	public PaginationConfig(String globalFieldFilter, boolean caseSensitive)
	{
		setGlobalFieldFilter(globalFieldFilter);
		setGlobalFieldFilterCaseSensitive(caseSensitive);
	}

	public PaginationConfig(Map<IdentityField, String> identityFilter, Map<PersonField, String> personFilter, boolean asConjunction, boolean caseSensitive)
	{
		setIdentityFilter(identityFilter);
		this.identityFilterAsConjunction = asConjunction;
		this.identityFilterCaseSensitive = caseSensitive;

		setPersonFilter(personFilter);
		this.personFilterAsConjunction = asConjunction;
		this.personFilterCaseSensitive = caseSensitive;

		setIdentityAndPersonFilterCombinedAsConjunction(asConjunction);
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

	// ### filtering modes ##############################################################################################################

	public String getGlobalFieldFilter()
	{
		return globalFieldFilter;
	}

	public void setGlobalFieldFilter(String globalFieldFilter)
	{
		this.globalFieldFilter = globalFieldFilter;
		if (isUsingGlobalFieldFiltering())
		{
			identityFilter.clear();
			personFilter.clear();
			identityAndPersonFilterCombinedAsConjunction = false;
		}
	}

	public boolean isUsingGlobalFieldFiltering()
	{
		return globalFieldFilter != null && !globalFieldFilter.isBlank();
	}

	public boolean isGlobalFieldFilterCaseSensitive()
	{
		return globalFieldFilterCaseSensitive;
	}

	public void setGlobalFieldFilterCaseSensitive(boolean globalFieldFilterCaseSensitive)
	{
		this.globalFieldFilterCaseSensitive = globalFieldFilterCaseSensitive;
	}

	public boolean isUsingEventFiltering()
	{
		return !eventFilter.isEmpty();
	}

	public Set<IdentityHistoryEvent> getEventFilter()
	{
		return new HashSet<>(eventFilter);
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
		return new HashSet<>(solutionFilter);
	}

	public void setSolutionFilter(Set<PossibleMatchSolution> solutionFilter)
	{
		this.solutionFilter.clear();
		if (solutionFilter != null)
		{
			this.solutionFilter.addAll(solutionFilter.stream().filter(Objects::nonNull).collect(Collectors.toSet()));
		}
	}

	public boolean isUsingPriorityFiltering()
	{
		return !priorityFilter.isEmpty();
	}

	public Set<PossibleMatchPriority> getPriorityFilter()
	{
		return new HashSet<>(priorityFilter);
	}

	public void setPriorityFilter(Set<PossibleMatchPriority> priorityFilter)
	{
		this.priorityFilter.clear();
		if (priorityFilter != null)
		{
			this.priorityFilter.addAll(priorityFilter.stream().filter(Objects::nonNull).collect(Collectors.toSet()));
		}
	}

	// ### identity filtering ##############################################################################################################

	public boolean isUsingIdentityFiltering()
	{
		return !identityFilter.isEmpty();
	}

	public Map<IdentityField, String> getIdentityFilter()
	{
		return new HashMap<>(identityFilter);
	}

	public void setIdentityFilter(Map<IdentityField, String> identityFilter)
	{
		this.identityFilter.clear();
		if (identityFilter != null)
		{
			identityFilter.entrySet().stream().filter(e -> e.getKey() != null && e.getValue() != null && !e.getValue().isEmpty())
					.forEach(e -> this.identityFilter.put(e.getKey(), e.getValue()));
			globalFieldFilter = null;
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

	public boolean isGlobalIdentityFiltering()
	{
		return getGlobalIdentityFilterPattern() != null;
	}

	public String getGlobalIdentityFilterPattern()
	{
		if (isIdentityFilterAsConjunction())
		{
			return null;
		}

		Set<String> patternsWithoutGenderPattern = identityFilter.entrySet().stream().filter(e -> !IdentityField.GENDER.equals(e.getKey())).map(Map.Entry::getValue).collect(Collectors.toSet());

		if (patternsWithoutGenderPattern.size() != 1)
		{
			return null;
		}

		return patternsWithoutGenderPattern.stream().findFirst().orElse(null);
	}

	/**
	 * If {@link IdentityField#NONE} is the only key in the identity filter map with a non-empty pattern value,
	 * then configure the filter to search in all given global filter fields linked by OR (disjunction),
	 * otherwise leave the filter configuration untouched.
	 *
	 * @param globalFilterFields
	 *            the identity fields to search in when using global identity field filtering
	 *
	 * @return true when global filtering was detected and configured
	 */
	public boolean detectAndConfigureGlobalIdentityFiltering(Set<IdentityField> globalFilterFields)
	{
		final String pattern = identityFilter.get(IdentityField.NONE);

		if (identityFilter.size() == 1 && Strings.isNotBlank(pattern))
		{
			// replace in the identity filter map with the NONE key by a map with
			// all given fields as keys and the same global search pattern
			setIdentityFilter(globalFilterFields.stream().collect(Collectors.toMap(f -> f, f -> pattern, (a, b) -> b)));
			setIdentityFilterAsConjunction(false);
			return true;
		}
		return false;
	}

	public Map<Gender, String> getIdentityGenderStrings()
	{
		return Collections.unmodifiableMap(identityGenderStrings);
	}

	public void setIdentityGenderStrings(Map<Gender, String> identityGenderStrings)
	{
		this.identityGenderStrings.clear();
		if (identityGenderStrings != null)
		{
			this.identityGenderStrings.putAll(identityGenderStrings);
		}
	}

	public boolean detectAndConfigureIdentityGenderFiltering()
	{
		return detectAndConfigureIdentityGenderFiltering(getIdentityGenderStrings());
	}

	/**
	 * If {@link IdentityField#GENDER} is a key in the identity fields filter map, then replace the (localized) gender strings
	 * by all gender symbol keys (as defined in {@link Gender}) for which the value's pattern matches the gender string value.
	 *
	 * @param genderStrings
	 *            a map with genders as keys and the localized strings as values
	 * @return true when the identity gender pattern has changed
	 */
	public boolean detectAndConfigureIdentityGenderFiltering(Map<Gender, String> genderStrings)
	{
		return detectAndConfigureStatusFiltering(genderStrings, identityFilter, IdentityField.GENDER, isIdentityFilterCaseSensitive());
	}

	public Map<VitalStatus, String> getIdentityVitalStatusStrings()
	{
		return Collections.unmodifiableMap(identityVitalStatusStrings);
	}

	public void setIdentityVitalStatusStrings(Map<VitalStatus, String> identityVitalStatusStrings)
	{
		this.identityVitalStatusStrings.clear();
		if (identityVitalStatusStrings != null)
		{
			this.identityVitalStatusStrings.putAll(identityVitalStatusStrings);
		}
	}

	public boolean detectAndConfigureIdentityVitalStatusFiltering()
	{
		return detectAndConfigureIdentityVitalStatusFiltering(getIdentityVitalStatusStrings());
	}

	/**
	 * If {@link IdentityField#VITAL_STATUS} is a key in the person fields filter map, then replace the (localized) vitalStatus strings
	 * by all vital status symbol keys (as defined in {@link VitalStatus}) for which the value's pattern matches the vital status string value.
	 *
	 * @param vitalStatusStrings
	 *            a map with vital status as keys and the localized strings as values
	 * @return true when the person vital status pattern has changed
	 */
	public boolean detectAndConfigureIdentityVitalStatusFiltering(Map<VitalStatus, String> vitalStatusStrings)
	{
		return detectAndConfigureStatusFiltering(vitalStatusStrings, identityFilter, IdentityField.VITAL_STATUS, isIdentityFilterCaseSensitive());
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

	// ### person filtering ##############################################################################################################

	public boolean isUsingPersonFiltering()
	{
		return !personFilter.isEmpty();
	}

	public Map<PersonField, String> getPersonFilter()
	{
		return Collections.unmodifiableMap(personFilter);
	}

	public void setPersonFilter(Map<PersonField, String> personFilter)
	{
		this.personFilter.clear();
		if (personFilter != null)
		{
			personFilter.entrySet().stream().filter(e -> e.getKey() != null && e.getValue() != null && !e.getValue().isEmpty())
					.forEach(e -> this.personFilter.put(e.getKey(), e.getValue()));
			globalFieldFilter = null;
		}
	}

	public boolean isPersonFilterAsConjunction()
	{
		return personFilterAsConjunction;
	}

	public void setPersonFilterAsConjunction(boolean personFilterAsConjunction)
	{
		this.personFilterAsConjunction = personFilterAsConjunction;
	}

	public boolean isPersonFilterCaseSensitive()
	{
		return personFilterCaseSensitive;
	}

	public void setPersonFilterCaseSensitive(boolean personFilterCaseSensitive)
	{
		this.personFilterCaseSensitive = personFilterCaseSensitive;
	}

	public boolean isGlobalPersonFiltering()
	{
		return getGlobalPersonFilterPattern() != null;
	}

	public String getGlobalPersonFilterPattern()
	{
		if (isPersonFilterAsConjunction())
		{
			return null;
		}

		Set<String> patterns = personFilter.values().stream().collect(Collectors.toSet());

		if (patterns.size() != 1)
		{
			return null;
		}

		return patterns.stream().findFirst().orElse(null);
	}

	/**
	 * If {@link PersonField#NONE} is the only key in the identity filter map with a non-empty pattern value,
	 * then configure the filter to search in all given global filter fields linked by OR (disjunction),
	 * otherwise leave the filter configuration untouched.
	 *
	 * @param globalFilterFields
	 *            the person fields to search in when using global person field filtering
	 *
	 * @return true when global filtering was detected and configured
	 */
	public boolean detectAndConfigureGlobalPersonFiltering(Set<PersonField> globalFilterFields)
	{
		final String pattern = personFilter.get(PersonField.NONE);

		if (personFilter.size() == 1 && Strings.isNotBlank(pattern))
		{
			// replace in the person filter map with the NONE key by a map with
			// all given fields as keys and the same global search pattern
			setPersonFilter(globalFilterFields.stream().collect(Collectors.toMap(f -> f, f -> pattern, (a, b) -> b)));
			setPersonFilterAsConjunction(false);
			return true;
		}
		return false;
	}

	// ### general stuff ##############################################################################################################

	public boolean isIdentityAndPersonFilterCombinedAsConjunction()
	{
		return identityAndPersonFilterCombinedAsConjunction;
	}

	public void setIdentityAndPersonFilterCombinedAsConjunction(boolean identityAndPersonFilterCombinedAsConjunction)
	{
		this.identityAndPersonFilterCombinedAsConjunction = identityAndPersonFilterCombinedAsConjunction;
	}

	public boolean isUsingCreateTimestampFiltering()
	{
		return createTimestampFilter != null && !createTimestampFilter.isBlank();
	}

	public String getCreateTimestampFilter()
	{
		return createTimestampFilter;
	}

	public void setCreateTimestampFilter(String createTimestampFilter)
	{
		this.createTimestampFilter = createTimestampFilter;
	}

	// ### other stuff ###

	public String getDateFormat()
	{
		return dateFormat;
	}

	public void setDateFormat(String dateFormat)
	{
		this.dateFormat = dateFormat;
	}

	public String getTimeFormat()
	{
		return timeFormat;
	}

	public void setTimeFormat(String timeFormat)
	{
		this.timeFormat = timeFormat;
	}

	public boolean normalize()
	{
		if (isUsingGlobalFieldFiltering())
		{
			String filter = getGlobalFieldFilter();
			setIdentityFilter(Map.of(IdentityField.NONE, filter));
			setPersonFilter(Map.of(PersonField.NONE, filter));
			setIdentityAndPersonFilterCombinedAsConjunction(false);
			return true;
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + firstEntry;
		result = prime * result + pageSize;

		result = prime * result + (globalFieldFilter == null ? 0 : globalFieldFilter.hashCode());
		result = prime * result + (globalFieldFilterCaseSensitive ? 1231 : 1237);
		result = prime * result + eventFilter.hashCode();
		result = prime * result + solutionFilter.hashCode();
		result = prime * result + priorityFilter.hashCode();

		result = prime * result + identityFilter.hashCode();
		result = prime * result + (identityFilterAsConjunction ? 1231 : 1237);
		result = prime * result + (identityFilterCaseSensitive ? 1231 : 1237);
		result = prime * result + identityGenderStrings.hashCode();
		result = prime * result + identityVitalStatusStrings.hashCode();
		result = prime * result + (sortField == null ? 0 : sortField.hashCode());
		result = prime * result + (sortIsAscending ? 1231 : 1237);

		result = prime * result + personFilter.hashCode();
		result = prime * result + (personFilterAsConjunction ? 1231 : 1237);
		result = prime * result + (personFilterCaseSensitive ? 1231 : 1237);

		result = prime * result + (identityAndPersonFilterCombinedAsConjunction ? 1231 : 1237);
		result = prime * result + (createTimestampFilter == null ? 0 : createTimestampFilter.hashCode());
		result = prime * result + (dateFormat == null ? 0 : dateFormat.hashCode());
		result = prime * result + (timeFormat == null ? 0 : timeFormat.hashCode());
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
		if (!Objects.equals(eventFilter, other.eventFilter))
		{
			return false;
		}
		if (!Objects.equals(solutionFilter, other.solutionFilter))
		{
			return false;
		}
		if (!Objects.equals(priorityFilter, other.priorityFilter))
		{
			return false;
		}
		if (!Objects.equals(globalFieldFilter, other.globalFieldFilter))
		{
			return false;
		}
		if (globalFieldFilterCaseSensitive != other.globalFieldFilterCaseSensitive)
		{
			return false;
		}
		if (!Objects.equals(identityFilter, other.identityFilter))
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
		if (!Objects.equals(personFilter, other.personFilter))
		{
			return false;
		}
		if (personFilterAsConjunction != other.personFilterAsConjunction)
		{
			return false;
		}
		if (personFilterCaseSensitive != other.personFilterCaseSensitive)
		{
			return false;
		}
		if (identityAndPersonFilterCombinedAsConjunction != other.identityAndPersonFilterCombinedAsConjunction)
		{
			return false;
		}
		if (!Objects.equals(createTimestampFilter, other.createTimestampFilter))
		{
			return false;
		}
		if (!Objects.equals(dateFormat, other.dateFormat))
		{
			return false;
		}
		if (!Objects.equals(timeFormat, other.timeFormat))
		{
			return false;
		}
		if (!Objects.equals(identityGenderStrings, other.identityGenderStrings))
		{
			return false;
		}
		if (!Objects.equals(identityVitalStatusStrings, other.identityVitalStatusStrings))
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		List<String> props = new ArrayList<>();
		if (isUsingPagination())
		{
			props.add("firstEntry=" + firstEntry + ", pageSize=" + pageSize);
		}
		if (isUsingGlobalFieldFiltering())
		{
			props.add("globalFieldFilter=" + globalFieldFilter + ", globalFieldFilterCaseSensitive=" + globalFieldFilterCaseSensitive);
		}
		if (isUsingIdentityFiltering())
		{
			props.add("identityFilter=" + identityFilter + ", identityFilterAsConjunction=" + identityFilterAsConjunction + ", identityFilterCaseSensitive=" + identityFilterCaseSensitive);
		}
		if (isUsingPersonFiltering())
		{
			props.add("personFilter=" + personFilter + ", personFilterAsConjunction=" + personFilterAsConjunction + ", personFilterCaseSensitive=" + personFilterCaseSensitive);
		}
		if (isUsingPersonFiltering() || isUsingIdentityFiltering())
		{
			props.add("identityAndPersonFilterCombinedAsConjunction=" + identityAndPersonFilterCombinedAsConjunction);
		}
		if (isUsingSolutionFiltering())
		{
			props.add("solutionFilter=" + solutionFilter);
		}
		if (isUsingPriorityFiltering())
		{
			props.add("priorityFilter=" + priorityFilter);
		}
		if (isUsingEventFiltering())
		{
			props.add("eventFilter=" + eventFilter);
		}
		if (isUsingCreateTimestampFiltering())
		{
			props.add("createTimestampFilter=" + createTimestampFilter);
		}
		if (isUsingSorting())
		{
			props.add("sortField=" + sortField + ", sortIsAscending=" + sortIsAscending);
		}
		if (dateFormat != null)
		{
			props.add("dateFormat=" + dateFormat);
		}
		if (timeFormat != null)
		{
			props.add("dateTimeFormat=" + timeFormat);
		}
		if (!identityGenderStrings.isEmpty())
		{
			props.add("genderStrings=" + identityGenderStrings);
		}
		if (!identityVitalStatusStrings.isEmpty())
		{
			props.add("vitalStatusStrings=" + identityVitalStatusStrings);
		}

		return "PaginationConfig [" + String.join(", ", props) + "]";
	}

	private static <T extends Enum<?>> boolean detectAndConfigureStatusFiltering(Map<? extends Enum<?>, String> statusStrings,
			Map<T, String> filterMap, T filterKey, boolean caseSensitive)
	{
		String pattern = filterMap.get(filterKey);
		if (pattern != null && !pattern.isEmpty() && !statusStrings.isEmpty())
		{
			// replace (localized) status (or gender) string by comma-separated status symbols
			// all given fields as keys and the same global search pattern
			final String p = pattern.replace("*", "").replace("%", "");
			String statuses;

			if (caseSensitive)
			{
				statuses = statusStrings.entrySet().stream().filter(e -> e.getValue().contains(p))
						.map(e -> e.getKey().name()).collect(Collectors.joining(","));
			}
			else
			{
				statuses = statusStrings.entrySet().stream().filter(e -> e.getValue().toLowerCase().contains(p.toLowerCase()))
						.map(e -> "" + e.getKey().name()).collect(Collectors.joining(","));
			}
			if (!statuses.isEmpty())
			{
				filterMap.put(filterKey, statuses);
			}
			else
			{
				filterMap.remove(filterKey);
			}
			return true;
		}
		return false;
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
			pg.normalize();
			return pg;
		}

		public Builder withGlobalFieldFilter(String globalFieldFilter, boolean caseSensitive)
		{
			pg.setGlobalFieldFilter(globalFieldFilter);
			pg.setGlobalFieldFilterCaseSensitive(caseSensitive);
			return this;
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

		public Builder withPriorityFilter(Set<PossibleMatchPriority> possibleMatchPriorities)
		{
			pg.setPriorityFilter(possibleMatchPriorities);
			return this;
		}

		public Builder withPriorityFilter(PossibleMatchPriority possibleMatchPriority)
		{
			return withPriorityFilter(Collections.singleton(possibleMatchPriority));
		}

		public Builder withIdentityFilter(Map<IdentityField, String> identityFilters, boolean asConjunction, boolean caseSensitive)
		{
			pg.setIdentityFilter(identityFilters);
			pg.setIdentityFilterAsConjunction(asConjunction);
			pg.setIdentityFilterCaseSensitive(caseSensitive);
			return this;
		}

		public Builder withIdentityFilter(String globalIdentityFilter, boolean caseSensitive)
		{
			if (globalIdentityFilter != null && !globalIdentityFilter.isBlank())
			{
				pg.setIdentityFilter(Map.of(IdentityField.NONE, globalIdentityFilter));
			}
			else
			{
				pg.setIdentityFilter(null);
			}
			pg.setIdentityFilterAsConjunction(false);
			pg.setIdentityFilterCaseSensitive(caseSensitive);
			return this;
		}

		public Builder withPersonFilter(Map<PersonField, String> personFilters, boolean asConjunction, boolean caseSensitive)
		{
			pg.setPersonFilter(personFilters);
			pg.setPersonFilterAsConjunction(asConjunction);
			pg.setPersonFilterCaseSensitive(caseSensitive);
			return this;
		}

		public Builder withPersonFilter(String globalPersonFilter, boolean caseSensitive)
		{
			if (globalPersonFilter != null && !globalPersonFilter.isBlank())
			{
				pg.setPersonFilter(Map.of(PersonField.NONE, globalPersonFilter));
			}
			else
			{
				pg.setPersonFilter(null);
			}
			pg.setPersonFilterAsConjunction(false);
			pg.setPersonFilterCaseSensitive(caseSensitive);
			return this;
		}

		public Builder withCreateTimestampFilter(String createTimestampFilter)
		{
			pg.setCreateTimestampFilter(createTimestampFilter);
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
			pg.setTimeFormat(dateTimeFormat);
			return this;
		}

		public Builder withIdentityGenderStrings(Map<Gender, String> genderStrings)
		{
			pg.setIdentityGenderStrings(genderStrings);
			return this;
		}

		public Builder withIdentityVitalStatusStrings(Map<VitalStatus, String> vitalStatusStrings)
		{
			pg.setIdentityVitalStatusStrings(vitalStatusStrings);
			return this;
		}
	}
}
