package org.emau.icmvc.ttp.epix.persistence;

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


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.emau.icmvc.ttp.epix.common.model.enums.IdentityField;
import org.emau.icmvc.ttp.epix.common.model.enums.IdentityHistoryEvent;
import org.emau.icmvc.ttp.epix.common.model.enums.PersonField;
import org.emau.icmvc.ttp.epix.common.model.enums.PossibleMatchSolution;
import org.emau.icmvc.ttp.epix.common.model.strings.IdentityHistoryStrings;
import org.emau.icmvc.ttp.epix.common.utils.PaginationConfig;
import org.emau.icmvc.ttp.epix.persistence.model.Domain;
import org.emau.icmvc.ttp.epix.persistence.model.Identifier_;
import org.emau.icmvc.ttp.epix.persistence.model.Identity;
import org.emau.icmvc.ttp.epix.persistence.model.IdentityHistory;
import org.emau.icmvc.ttp.epix.persistence.model.IdentityHistory_;
import org.emau.icmvc.ttp.epix.persistence.model.IdentityLink;
import org.emau.icmvc.ttp.epix.persistence.model.IdentityLinkHistory;
import org.emau.icmvc.ttp.epix.persistence.model.IdentityLinkHistory_;
import org.emau.icmvc.ttp.epix.persistence.model.IdentityLink_;
import org.emau.icmvc.ttp.epix.persistence.model.Identity_;
import org.emau.icmvc.ttp.epix.persistence.model.Person;
import org.emau.icmvc.ttp.epix.persistence.model.Person_;
import org.emau.icmvc.ttp.epix.persistence.model.Source_;

/**
 * hilfsfunktionen fuer paginated-abfragen, nur wegen der uebersichtlichkeit aus publicDAO herausgezogen
 * 
 * @author geidell
 */
class PaginatedHelper
{
	// TODO(FMM): let all generateWherePredicateFor...(...) methods consequently use PaginationConfig as parameter

	private static final Logger logger = LogManager.getLogger(PublicDAO.class);

	// ====== Person ==========================================================================================================================

	static Predicate generateWherePredicateForPerson(CriteriaBuilder cb, Path<Person> path, Domain domain, Map<PersonField, String> filter,
			boolean filterIsCaseSensitive)
	{
		Predicate predicate = cb.and(cb.equal(path.get(Person_.domain), domain),
				cb.isFalse(path.get(Person_.deactivated)));
		if (filter != null)
		{
			for (Entry<PersonField, String> entry : filter.entrySet())
			{
				switch (entry.getKey())
				{
					case MPI:
						if (filterIsCaseSensitive)
						{
							predicate = cb.and(predicate,
									cb.like(path.get(Person_.firstMPI).get(Identifier_.value), entry.getValue()));
						}
						else
						{
							predicate = cb.and(predicate,
									cb.like(cb.lower(path.get(Person_.firstMPI).get(Identifier_.value)),
											cb.lower(cb.literal(entry.getValue()))));
						}
						break;
					case PERSON_ID:
						predicate = cb.and(predicate, cb.like(path.get(Person_.id).as(String.class), entry.getValue()));
						break;
					case NONE:
						break;
					default:
						logger.warn("unimplemented PersonField '" + entry.getKey().name() + "' for filter-clause within generateWhereForPerson()");
						break;
				}
			}
		}
		return predicate;
	}

	static Expression<?> generateSortExpressionForPerson(PersonField sortField, Path<Person> path)
	{
		Expression<?> order = null;
		if (sortField != null)
		{
			switch (sortField)
			{
				case MPI:
					order = path.get(Person_.firstMPI).get(Identifier_.value);
					break;
				case PERSON_ID:
					order = path.get(Person_.id);
					break;
				case NONE: //default sort order for persons
				case PERSON_CREATED:
					order = path.get(Person_.createTimestamp);
					break;
				case PERSON_LAST_EDITED:
					order = path.get(Person_.timestamp);
					break;
				default:
					logger.warn("unimplemented PersonField '" + sortField.name() + "' for order-by-clause within generateSortExpressionForPerson()");
					break;
			}
		}
		return order;
	}

	// ====== Identity ==========================================================================================================================

	static Predicate generateWherePredicateForIdentity(CriteriaBuilder cb, Path<Identity> path, Domain domain, Map<IdentityField, String> filter,
			boolean filterIsCaseSensitive)
	{
		Predicate predicate = cb.and(cb.equal(path.get(Identity_.person).get(Person_.domain), domain),
				cb.isFalse(path.get(Identity_.deactivated)));

		if (filter != null)
		{
			for (Entry<IdentityField, String> entry : filter.entrySet())
			{
				switch (entry.getKey())
				{
					case BIRTH_DATE:
						predicate = cb.and(predicate,
								generateDateMatchPredicate(cb, path.get(Identity_.birthDate), entry.getValue(), createDefaultDateFormat()));
						break;
					case BIRTHPLACE:
						predicate = cb.and(predicate,
								generateLikePredicate(cb, path, Identity_.birthPlace, entry.getValue(), filterIsCaseSensitive));
						break;
					case CIVIL_STATUS:
						predicate = cb.and(predicate,
								generateLikePredicate(cb, path, Identity_.civilStatus, entry.getValue(), filterIsCaseSensitive));
						break;
					case DEGREE:
						predicate = cb.and(predicate,
								generateLikePredicate(cb, path, Identity_.degree, entry.getValue(), filterIsCaseSensitive));
						break;
					case FIRST_NAME:
						predicate = cb.and(predicate,
								generateLikePredicate(cb, path, Identity_.firstName, entry.getValue(), filterIsCaseSensitive));
						break;
					case GENDER:
						if (filterIsCaseSensitive)
						{
							Predicate p = cb.equal(path.get(Identity_.gender).as(String.class), entry.getValue());
							predicate = cb.and(predicate, p);
						}
						else
						{
							Predicate p = cb.equal(cb.lower(path.get(Identity_.gender).as(String.class)),
									cb.lower(cb.literal(entry.getValue())));
							predicate = cb.and(predicate, p);
						}
						break;
					case IDENTITY_ID:
						predicate = cb.and(predicate,
								cb.like(path.get(Identity_.id).as(String.class), entry.getValue()));
						break;
					case LAST_NAME:
						predicate = cb.and(predicate,
								generateLikePredicate(cb, path, Identity_.lastName, entry.getValue(), filterIsCaseSensitive));
						break;
					case MIDDLE_NAME:
						predicate = cb.and(predicate,
								generateLikePredicate(cb, path, Identity_.middleName, entry.getValue(), filterIsCaseSensitive));
						break;
					case MOTHERS_MAIDEN_NAME:
						predicate = cb.and(predicate,
								generateLikePredicate(cb, path, Identity_.mothersMaidenName, entry.getValue(), filterIsCaseSensitive));
						break;
					case MOTHER_TONGUE:
						predicate = cb.and(predicate,
								generateLikePredicate(cb, path, Identity_.motherTongue, entry.getValue(), filterIsCaseSensitive));
						break;
					case NATIONALITY:
						predicate = cb.and(predicate,
								generateLikePredicate(cb, path, Identity_.nationality, entry.getValue(), filterIsCaseSensitive));
						break;
					case NONE:
						break;
					case PERSON_ID:
						predicate = cb.and(predicate,
								cb.like(path.get(Identity_.person).get(Person_.id).as(String.class), entry.getValue()));
						break;
					case PREFIX:
						predicate = cb.and(predicate,
								generateLikePredicate(cb, path, Identity_.prefix, entry.getValue(), filterIsCaseSensitive));
						break;
					case RACE:
						predicate = cb.and(predicate,
								generateLikePredicate(cb, path, Identity_.race, entry.getValue(), filterIsCaseSensitive));
						break;
					case RELIGION:
						predicate = cb.and(predicate,
								generateLikePredicate(cb, path, Identity_.religion, entry.getValue(), filterIsCaseSensitive));
						break;
					case SOURCE:
						if (filterIsCaseSensitive)
						{
							Predicate p = cb.like(path.get(Identity_.source).get(Source_.name), entry.getValue());
							predicate = cb.and(predicate, p);
						}
						else
						{
							Predicate p = cb.like(cb.lower(path.get(Identity_.source).get(Source_.name).as(String.class)),
									cb.lower(cb.literal(entry.getValue())));
							predicate = cb.and(predicate, p);
						}
						break;
					case SUFFIX:
						predicate = cb.and(predicate,
								generateLikePredicate(cb, path, Identity_.suffix, entry.getValue(), filterIsCaseSensitive));
						break;
					case VALUE1:
						predicate = cb.and(predicate,
								generateLikePredicate(cb, path, Identity_.value1, entry.getValue(), filterIsCaseSensitive));
						break;
					case VALUE10:
						predicate = cb.and(predicate,
								generateLikePredicate(cb, path, Identity_.value10, entry.getValue(), filterIsCaseSensitive));
						break;
					case VALUE2:
						predicate = cb.and(predicate,
								generateLikePredicate(cb, path, Identity_.value2, entry.getValue(), filterIsCaseSensitive));
						break;
					case VALUE3:
						predicate = cb.and(predicate,
								generateLikePredicate(cb, path, Identity_.value3, entry.getValue(), filterIsCaseSensitive));
						break;
					case VALUE4:
						predicate = cb.and(predicate,
								generateLikePredicate(cb, path, Identity_.value4, entry.getValue(), filterIsCaseSensitive));
						break;
					case VALUE5:
						predicate = cb.and(predicate,
								generateLikePredicate(cb, path, Identity_.value5, entry.getValue(), filterIsCaseSensitive));
						break;
					case VALUE6:
						predicate = cb.and(predicate,
								generateLikePredicate(cb, path, Identity_.value6, entry.getValue(), filterIsCaseSensitive));
						break;
					case VALUE7:
						predicate = cb.and(predicate,
								generateLikePredicate(cb, path, Identity_.value7, entry.getValue(), filterIsCaseSensitive));
						break;
					case VALUE8:
						predicate = cb.and(predicate,
								generateLikePredicate(cb, path, Identity_.value8, entry.getValue(), filterIsCaseSensitive));
						break;
					case VALUE9:
						predicate = cb.and(predicate,
								generateLikePredicate(cb, path, Identity_.value9, entry.getValue(), filterIsCaseSensitive));
						break;
					default:
						logger.warn(
								"unimplemented IdentityField '" + entry.getKey().name() + "' for filter-clause within generateWhereForIdentity()");
						break;
				}
			}
		}
		return predicate;
	}

	static Expression<?> generateSortExpressionForIdentity(IdentityField sortField, Path<Identity> path)
	{
		Expression<?> order = null;
		if (sortField != null)
		{
			switch (sortField)
			{
				case BIRTH_DATE:
					order = path.get(Identity_.birthDate);
					break;
				case BIRTHPLACE:
					order = path.get(Identity_.birthPlace);
					break;
				case CIVIL_STATUS:
					order = path.get(Identity_.civilStatus);
					break;
				case DEGREE:
					order = path.get(Identity_.degree);
					break;
				case EXTERNAL_DATE:
					order = path.get(Identity_.externalTimestamp);
					break;
				case FIRST_NAME:
					order = path.get(Identity_.firstName);
					break;
				case GENDER:
					order = path.get(Identity_.gender);
					break;
				case NONE: //default sort order for identities
				case IDENTITY_CREATED:
					order = path.get(Identity_.createTimestamp);
					break;
				case IDENTITY_ID:
					order = path.get(Identity_.id);
					break;
				case IDENTITY_LAST_EDITED:
					order = path.get(Identity_.timestamp);
					break;
				case LAST_NAME:
					order = path.get(Identity_.lastName);
					break;
				case MOTHERS_MAIDEN_NAME:
					order = path.get(Identity_.mothersMaidenName);
					break;
				case MOTHER_TONGUE:
					order = path.get(Identity_.motherTongue);
					break;
				case NATIONALITY:
					order = path.get(Identity_.nationality);
					break;
				case PERSON_ID:
					order = path.get(Identity_.person).get(Person_.id);
					break;
				case PREFIX:
					order = path.get(Identity_.prefix);
					break;
				case RACE:
					order = path.get(Identity_.race);
					break;
				case RELIGION:
					order = path.get(Identity_.religion);
					break;
				case SOURCE:
					order = path.get(Identity_.source);
					break;
				case SUFFIX:
					order = path.get(Identity_.suffix);
					break;
				case VALUE1:
					order = path.get(Identity_.value1);
					break;
				case VALUE10:
					order = path.get(Identity_.value10);
					break;
				case VALUE2:
					order = path.get(Identity_.value2);
					break;
				case VALUE3:
					order = path.get(Identity_.value3);
					break;
				case VALUE4:
					order = path.get(Identity_.value4);
					break;
				case VALUE5:
					order = path.get(Identity_.value5);
					break;
				case VALUE6:
					order = path.get(Identity_.value6);
					break;
				case VALUE7:
					order = path.get(Identity_.value7);
					break;
				case VALUE8:
					order = path.get(Identity_.value8);
					break;
				case VALUE9:
					order = path.get(Identity_.value9);
					break;
				default:
					logger.warn(
							"unimplemented IdentityField '" + sortField.name() + "' for order-by-clause within generateSortExpressionForIdentity()");
					break;
			}
		}
		return order;
	}

	// ====== IdentityHistory ========================================================================================================================

	/**
	 * Returns a predicate which describes a search for identity filter pattern as substring in history entries {@link IdentityHistory_}.
	 * The column keys and patterns will be taken from the identityFilter map. If the search is not defined as conjunction (linked by AND)
	 * but as disjunction (linked by OR) and if the identityFilter pattern is the same for all columns, then automatically
	 * the history entry's MPI, its creation timestamp will be added as search columns.
	 *
	 * @param cb a criteria builder
	 * @param path the path entity for the from-query
	 * @param domain the domain
	 * @param pc the pagination configuration
	 * @return the search predicate
	 */
	static Predicate generateWherePredicateForIdentityHistory(CriteriaBuilder cb, Path<IdentityHistory> path, Domain domain, PaginationConfig pc)
	{
		Set<IdentityHistoryEvent> eventFilter = pc.getEventFilter();
		Map<IdentityField, String> identityFilter = pc.getIdentityFilter();
		boolean filterIsCaseSensitive = pc.isIdentityFilterCaseSensitive();
		boolean asConjunction = pc.isIdentityFilterAsConjunction();
		String birthDateFormat = pc.getDateFormat() == null ? createDefaultDateFormat() : convertSimpleDateTimeFormat(pc.getDateFormat());
		String creationTimeFormat = pc.getDateTimeFormat() == null ? createDefaultTimeFormat() : convertSimpleDateTimeFormat(pc.getDateTimeFormat());

		// only search for active entries in the given domain
		Predicate predicate = cb.and(cb.equal(path.get(IdentityHistory_.person).get(Person_.domain), domain),
				cb.isFalse(path.get(IdentityHistory_.deactivated)));

		// skip all, where IdentityHistoryStrings.UPDATED_REF_IDENTITY_AT_MERGE.equals(identityHistoryEntry.getComment())
		predicate = cb.and(predicate, cb.or(
				cb.isNull(path.get(IdentityHistory_.comment)),
				cb.notEqual(path.get(IdentityHistory_.comment), cb.literal(IdentityHistoryStrings.UPDATED_REF_IDENTITY_AT_MERGE))));

		if (eventFilter != null && !eventFilter.isEmpty())
		{
			// in any case (if there are any event types at all) restrict the search to the given types
			Predicate eventPredicate = cb.disjunction();
			for (IdentityHistoryEvent e : eventFilter)
			{
				eventPredicate = link(cb, false, eventPredicate,
									filterIsCaseSensitive ?
									cb.like(path.get(IdentityHistory_.event).as(String.class), e.name()) :
									cb.like(cb.lower(path.get(IdentityHistory_.event).as(String.class)), e.name().toLowerCase(Locale.ROOT)));
			}
			predicate = cb.and(predicate, eventPredicate);
		}

		if (identityFilter != null && !identityFilter.isEmpty())
		{
			Predicate search = asConjunction ? cb.conjunction() : cb.disjunction();
			String globalFilterPattern = pc.getGlobalFilterPattern();

			if (globalFilterPattern != null && !globalFilterPattern.isEmpty()) // this implies: asConjunction == false
			{
				// when searching as disjunction (OR), then also search in history event'S MPI and creation timestamp
				logger.debug("generateWherePredicateForIdentityHistory: adding timestamp, MPI, and event type columns for global search");
				search = link(cb, false, search,
							generateDateMatchPredicate(cb, path.get(IdentityHistory_.historyTimestamp), globalFilterPattern, creationTimeFormat));
				search = link(cb, false, search,
							cb.like(path.get(IdentityHistory_.person).get(Person_.firstMPI).get(Identifier_.value), globalFilterPattern));
			}

			for (Entry<IdentityField, String> entry : identityFilter.entrySet())
			{
				switch (entry.getKey())
				{
					case BIRTH_DATE:
						search = link(cb, asConjunction, search,
								generateDateMatchPredicate(cb, path.get(IdentityHistory_.birthDate), entry.getValue(), birthDateFormat));
						break;
					case BIRTHPLACE:
						search = link(cb, asConjunction, search,
								generateLikePredicate(cb, path, IdentityHistory_.birthPlace, entry.getValue(), filterIsCaseSensitive));
						break;
					case CIVIL_STATUS:
						search = link(cb, asConjunction, search,
								generateLikePredicate(cb, path, IdentityHistory_.civilStatus, entry.getValue(), filterIsCaseSensitive));
						break;
					case DEGREE:
						search = link(cb, asConjunction, search,
								generateLikePredicate(cb, path, IdentityHistory_.degree, entry.getValue(), filterIsCaseSensitive));
						break;
					case FIRST_NAME:
						search = link(cb, asConjunction, search,
								generateLikePredicate(cb, path, IdentityHistory_.firstName, entry.getValue(), filterIsCaseSensitive));
						break;
					case GENDER:
						{
							Predicate p = cb.disjunction();
							for (String e : entry.getValue().split(","))
							{
								p = cb.or(p, cb.equal(cb.lower(path.get(IdentityHistory_.gender).as(String.class)), cb.lower(cb.literal(e))));
							}
							search = link(cb, asConjunction, search, p);
						}
						break;
					case IDENTITY_ID:
						search = link(cb, asConjunction, search,
								cb.like(path.get(IdentityHistory_.id).as(String.class), entry.getValue()));
						break;
					case LAST_NAME:
						search = link(cb, asConjunction, search,
								generateLikePredicate(cb, path, IdentityHistory_.lastName, entry.getValue(), filterIsCaseSensitive));
						break;
					case MIDDLE_NAME:
						search = link(cb, asConjunction, search,
								generateLikePredicate(cb, path, IdentityHistory_.middleName, entry.getValue(), filterIsCaseSensitive));
						break;
					case MOTHERS_MAIDEN_NAME:
						search = link(cb, asConjunction, search,
								generateLikePredicate(cb, path, IdentityHistory_.mothersMaidenName, entry.getValue(), filterIsCaseSensitive));
						break;
					case MOTHER_TONGUE:
						search = link(cb, asConjunction, search,
								generateLikePredicate(cb, path, IdentityHistory_.motherTongue, entry.getValue(), filterIsCaseSensitive));
						break;
					case NATIONALITY:
						search = link(cb, asConjunction, search,
								generateLikePredicate(cb, path, IdentityHistory_.nationality, entry.getValue(), filterIsCaseSensitive));
						break;
					case NONE:
						break;
					case PERSON_ID:
						search = link(cb, asConjunction, search,
								cb.like(path.get(IdentityHistory_.person).get(Person_.id).as(String.class), entry.getValue()));
						break;
					case PREFIX:
						search = link(cb, asConjunction, search,
								generateLikePredicate(cb, path, IdentityHistory_.prefix, entry.getValue(), filterIsCaseSensitive));
						break;
					case RACE:
						search = link(cb, asConjunction, search,
								generateLikePredicate(cb, path, IdentityHistory_.race, entry.getValue(), filterIsCaseSensitive));
						break;
					case RELIGION:
						search = link(cb, asConjunction, search,
								generateLikePredicate(cb, path, IdentityHistory_.religion, entry.getValue(), filterIsCaseSensitive));
						break;
					case SOURCE:
						if (filterIsCaseSensitive)
						{
							search = link(cb, asConjunction, search,
									cb.like(path.get(IdentityHistory_.source).get(Source_.name), entry.getValue()));
						}
						else
						{
							search = link(cb, asConjunction, search,
									cb.like(cb.lower(path.get(IdentityHistory_.source).get(Source_.name).as(String.class)),
									cb.lower(cb.literal(entry.getValue()))));
						}
						break;
					case SUFFIX:
						search = link(cb, asConjunction, search,
								generateLikePredicate(cb, path, IdentityHistory_.suffix, entry.getValue(), filterIsCaseSensitive));
						break;
					case VALUE1:
						search = link(cb, asConjunction, search,
								generateLikePredicate(cb, path, IdentityHistory_.value1, entry.getValue(), filterIsCaseSensitive));
						break;
					case VALUE10:
						search = link(cb, asConjunction, search,
								generateLikePredicate(cb, path, IdentityHistory_.value10, entry.getValue(), filterIsCaseSensitive));
						break;
					case VALUE2:
						search = link(cb, asConjunction, search,
								generateLikePredicate(cb, path, IdentityHistory_.value2, entry.getValue(), filterIsCaseSensitive));
						break;
					case VALUE3:
						search = link(cb, asConjunction, search,
								generateLikePredicate(cb, path, IdentityHistory_.value3, entry.getValue(), filterIsCaseSensitive));
						break;
					case VALUE4:
						search = link(cb, asConjunction, search,
								generateLikePredicate(cb, path, IdentityHistory_.value4, entry.getValue(), filterIsCaseSensitive));
						break;
					case VALUE5:
						search = link(cb, asConjunction, search,
								generateLikePredicate(cb, path, IdentityHistory_.value5, entry.getValue(), filterIsCaseSensitive));
						break;
					case VALUE6:
						search = link(cb, asConjunction, search,
								generateLikePredicate(cb, path, IdentityHistory_.value6, entry.getValue(), filterIsCaseSensitive));
						break;
					case VALUE7:
						search = link(cb, asConjunction, search,
								generateLikePredicate(cb, path, IdentityHistory_.value7, entry.getValue(), filterIsCaseSensitive));
						break;
					case VALUE8:
						search = link(cb, asConjunction, search,
								generateLikePredicate(cb, path, IdentityHistory_.value8, entry.getValue(), filterIsCaseSensitive));
						break;
					case VALUE9:
						search = link(cb, asConjunction, search,
								generateLikePredicate(cb, path, IdentityHistory_.value9, entry.getValue(), filterIsCaseSensitive));
						break;
					default:
						logger.warn("unimplemented IdentityField '" + entry.getKey().name()
								+ "' for identityFilter-clause within generateWhereForIdentityHistory()");
						break;
				}
			}
			predicate = cb.and(predicate, search);
		}
		return predicate;
	}

	static Expression<?> generateSortExpressionForIdentityHistory(IdentityField sortField, Path<IdentityHistory> path)
	{
		Expression<?> order = null;
		if (sortField != null)
		{
			switch (sortField)
			{
				case BIRTH_DATE:
					order = path.get(IdentityHistory_.birthDate);
					break;
				case BIRTHPLACE:
					order = path.get(IdentityHistory_.birthPlace);
					break;
				case CIVIL_STATUS:
					order = path.get(IdentityHistory_.civilStatus);
					break;
				case DEGREE:
					order = path.get(IdentityHistory_.degree);
					break;
				case EXTERNAL_DATE:
					order = path.get(IdentityHistory_.externalTimestamp);
					break;
				case FIRST_NAME:
					order = path.get(IdentityHistory_.firstName);
					break;
				case GENDER:
					order = path.get(IdentityHistory_.gender);
					break;
				case NONE: //default sort order identity history entries
				case IDENTITY_CREATED:
					order = path.get(IdentityHistory_.historyTimestamp);
					break;
				case IDENTITY_ID:
					order = path.get(IdentityHistory_.id);
					break;
				case IDENTITY_LAST_EDITED:
					order = path.get(IdentityHistory_.historyTimestamp);
					break;
				case LAST_NAME:
					order = path.get(IdentityHistory_.lastName);
					break;
				case MOTHERS_MAIDEN_NAME:
					order = path.get(IdentityHistory_.mothersMaidenName);
					break;
				case MOTHER_TONGUE:
					order = path.get(IdentityHistory_.motherTongue);
					break;
				case NATIONALITY:
					order = path.get(IdentityHistory_.nationality);
					break;
				case PERSON_ID:
					order = path.get(IdentityHistory_.person).get(Person_.id);
					break;
				case PREFIX:
					order = path.get(IdentityHistory_.prefix);
					break;
				case RACE:
					order = path.get(IdentityHistory_.race);
					break;
				case RELIGION:
					order = path.get(IdentityHistory_.religion);
					break;
				case SOURCE:
					order = path.get(IdentityHistory_.source);
					break;
				case SUFFIX:
					order = path.get(IdentityHistory_.suffix);
					break;
				case VALUE1:
					order = path.get(IdentityHistory_.value1);
					break;
				case VALUE10:
					order = path.get(IdentityHistory_.value10);
					break;
				case VALUE2:
					order = path.get(IdentityHistory_.value2);
					break;
				case VALUE3:
					order = path.get(IdentityHistory_.value3);
					break;
				case VALUE4:
					order = path.get(IdentityHistory_.value4);
					break;
				case VALUE5:
					order = path.get(IdentityHistory_.value5);
					break;
				case VALUE6:
					order = path.get(IdentityHistory_.value6);
					break;
				case VALUE7:
					order = path.get(IdentityHistory_.value7);
					break;
				case VALUE8:
					order = path.get(IdentityHistory_.value8);
					break;
				case VALUE9:
					order = path.get(IdentityHistory_.value9);
					break;
				default:
					logger.warn("unimplemented IdentityField '" + sortField.name()
							+ "' for order-by-clause within generateSortExpressionForIdentityHistory()");
					break;
			}
		}
		return order;
	}

	// ====== IdentityLink ==============================================================================================

	/**
	 * Returns a query which searches for a filter pattern as substring in both identities' {@link Identity_#lastName},
	 * {@link Identity_#firstName}, {@link Identity_#birthDate}, and in {@link IdentityLink_#createTimestamp}.
	 * The pattern only must match for ANY single of the fields from above (OR).
	 *
	 * @param cb a criteria builder
	 * @param domain the domain
	 * @param filter the filter pattern
	 * @param filterIsCaseSensitive true to filter case sensitive
	 * @param birthDateFormat  the format for the string representation of the identities' birth date to search in
	 * @param creationTimeFormat the format for the string representation of the link's creation timestamp to search in
	 * @return the search query
	 */
	static CriteriaQuery<IdentityLink> generateWhereQueryForIdentityLink(CriteriaBuilder cb, Domain domain,
			String filter, boolean filterIsCaseSensitive, String birthDateFormat, String creationTimeFormat)
	{
		CriteriaQuery<IdentityLink> cq = cb.createQuery(IdentityLink.class);
		Root<IdentityLink> root = cq.from(IdentityLink.class);
		Predicate predicate = generateWherePredicateForIdentityLink(cb, root, domain,
				filter, filterIsCaseSensitive, birthDateFormat, creationTimeFormat);
		cq.select(root).where(predicate);
		cq.orderBy(cb.desc(generateSortExpressionForIdentityLink(IdentityField.NONE, root))); // default sort order by creation time
		return cq;
	}

	/**
	 * Returns a predicate which describes a search for a filter pattern as substring in both identities' {@link Identity_#lastName},
	 * {@link Identity_#firstName}, {@link Identity_#birthDate}, and in {@link IdentityLink_#createTimestamp}.
	 * The pattern only must match for ANY single of the fields from above (OR).
	 *
	 * @param cb a criteria builder
	 * @param path the path entity for the from-query
	 * @param domain the domain
	 * @param filter the filter pattern
	 * @param filterIsCaseSensitive true to filter case sensitive
	 * @param birthDateFormat  the format for the string representation of the identities' birth date to search in
	 * @param creationTimeFormat the format for the string representation of the link's creation timestamp to search in
	 * @return the search predicate
	 */
	static Predicate generateWherePredicateForIdentityLink(CriteriaBuilder cb, Path<IdentityLink> path, Domain domain,
			String filter, boolean filterIsCaseSensitive, String birthDateFormat, String creationTimeFormat)
	{
		Predicate predicate = generateWherePredicateForIdentityLink(cb, path, domain, null, filterIsCaseSensitive);

		if (filter != null && !filter.isEmpty()) {
			birthDateFormat = convertSimpleDateTimeFormat(birthDateFormat);

			Predicate like = generateLikePredicateLinkedByOr(cb, path.get(IdentityLink_.destIdentity), path.get(IdentityLink_.srcIdentity),
					Identity_.lastName, filter, filterIsCaseSensitive);
			like = cb.or(like, generateLikePredicateLinkedByOr(cb, path.get(IdentityLink_.destIdentity), path.get(IdentityLink_.srcIdentity),
					Identity_.firstName, filter, filterIsCaseSensitive));
			like = cb.or(like, generateDateMatchPredicate(cb,
					path.get(IdentityLink_.destIdentity).get(Identity_.birthDate), filter, birthDateFormat));
			like = cb.or(like, generateDateMatchPredicate(cb,
					path.get(IdentityLink_.srcIdentity).get(Identity_.birthDate), filter, birthDateFormat));
			like = cb.or(like, generateDateMatchPredicate(cb,
					path.get(IdentityLink_.createTimestamp), filter, convertSimpleDateTimeFormat(creationTimeFormat)));

			predicate = cb.and(predicate, like);
		}
		return predicate;
	}

	/**
	 * Returns a query which searches for filter patterns as substring in the corresponding fields of {@link Identity_}.
	 * The patterns must match for ALL specified fields (AND).
	 *
	 * @param cb a criteria builder
	 * @param domain the domain
	 * @param filter the filter pattern
	 * @param filterIsCaseSensitive true to filter case sensitive
	 * @return the search query
	 */
	static CriteriaQuery<IdentityLink> generateWhereQueryForIdentityLink(CriteriaBuilder cb, Domain domain,
			IdentityField sortField, boolean sortIsAscending, Map<IdentityField, String> filter, boolean filterIsCaseSensitive)
	{
		CriteriaQuery<IdentityLink> cq = cb.createQuery(IdentityLink.class);
		Root<IdentityLink> root = cq.from(IdentityLink.class);
		Predicate predicate = generateWherePredicateForIdentityLink(cb, root, domain, filter, filterIsCaseSensitive);
		cq.select(root).where(predicate);

		if (sortField != null)
		{
			Expression<?> order = generateSortExpressionForIdentityLink(sortField, root);
			if (order != null)
			{
				if (sortIsAscending)
				{
					cq.orderBy(cb.asc(order));
				}
				else
				{
					cq.orderBy(cb.desc(order));
				}
			}
		}

		return cq;
	}

	/**
	 * Returns a predicate which describes a search for filter patterns as substring in the corresponding fields of {@link Identity_}.
	 * The patterns must match for ALL specified fields (AND).
	 *
	 * @param cb a criteria builder
	 * @param path the path entity for the from-query
	 * @param domain the domain
	 * @param filter the filter pattern
	 * @param caseSensitive true to filter case sensitive
	 * @return the search query
	 */
	static Predicate generateWherePredicateForIdentityLink(CriteriaBuilder cb, Path<IdentityLink> path, Domain domain,
			Map<IdentityField, String> filter, boolean caseSensitive)
	{
		String dateFormat = createDefaultDateFormat();
		Predicate predicate = cb.equal(path.get(IdentityLink_.srcIdentity).get(Identity_.person).get(Person_.domain), domain);

		if (filter != null)
		{
			boolean asConjunction = true;
			predicate = cb.and(predicate,
					generateFilterPredicateForIdentityPair(cb,
							path.get(IdentityLink_.destIdentity),
							path.get(IdentityLink_.srcIdentity),
							filter, caseSensitive, asConjunction, dateFormat));
		}
		return predicate;
	}

	static Expression<?> generateSortExpressionForIdentityLink(IdentityField sortField, Path<IdentityLink> path)
	{
		return sortField != null && sortField != IdentityField.NONE ?
				generateSortExpressionForIdentity(sortField, path.get(IdentityLink_.srcIdentity)) :
				path.get(IdentityLink_.createTimestamp); // default sort order for identity link entries
	}

	// ===== IdentityLinkHistory =========================================================================================================================

	/**
	 * Returns a predicate which describes a search in possible match history entries {@link IdentityLinkHistory_}.
	 *
	 * @param cb a criteria builder
	 * @param path the path entity for the from-query
	 * @param domain the domain
	 * @param pc the pagination configuration
	 * @return the search predicate
	 */
	static Predicate generateWherePredicateForIdentityLinkHistory(CriteriaBuilder cb, Path<IdentityLinkHistory> path, Domain domain, PaginationConfig pc)
	{
		// only search for entries in the given domain
		Predicate predicate = cb.equal(path.get(IdentityLinkHistory_.srcPerson).get(Person_.domain), domain);

		if (pc.isUsingSolutionFiltering())
		{
			Predicate solutionPredicate = cb.disjunction();
			for (PossibleMatchSolution s : pc.getSolutionFilter())
			{
				solutionPredicate = cb.or(solutionPredicate, cb.like(path.get(IdentityLinkHistory_.event).as(String.class), s.name()));
			}
			predicate = cb.and(predicate, solutionPredicate);
		}

		if (pc.isUsingIdentityFiltering())
		{
			predicate = cb.and(predicate,
					generateFilterPredicateForIdentityPair(cb,
							path.get(IdentityLinkHistory_.destIdentity),
							path.get(IdentityLinkHistory_.srcIdentity),
							pc.getIdentityFilter(), pc.isIdentityFilterCaseSensitive(), pc.isIdentityFilterAsConjunction(), pc.getDateFormat()));
		}

		return predicate;
	}

	static Expression<?> generateSortExpressionForIdentityLinkHistory(IdentityField sortField, Path<IdentityLinkHistory> path)
	{
		return sortField != null && sortField != IdentityField.NONE ?
				generateSortExpressionForIdentity(sortField, path.get(IdentityLinkHistory_.updatedIdentity)) :
				path.get(IdentityLinkHistory_.historyTimestamp); // default sort order for identity link history entries
	}

	// ====== now a section for useful helpers ================================================================================================

	static Predicate generateFilterPredicateForIdentityPair(CriteriaBuilder cb, Path<Identity> path1, Path<Identity> path2,
			Map<IdentityField, String> filter, boolean caseSensitive, boolean asConjunction, String dateFormat)
	{
		if (path1 == null || path2 == null) {
			throw new IllegalArgumentException("Paths must not be null");
		}

		Predicate predicate = asConjunction ? cb.conjunction() : cb.disjunction();

		for (Entry<IdentityField, String> entry : filter.entrySet())
		{
			String pattern = entry.getValue();
			switch (entry.getKey())
			{
				case BIRTH_DATE:
					predicate = link(cb, asConjunction, predicate,
							generateBirthDateMatchPredicateLinkedByOr(cb, path1, path2, pattern, dateFormat));
					break;
				case BIRTHPLACE:
					predicate = link(cb, asConjunction, predicate,
							generateLikePredicateLinkedByOr(cb, path1, path2, Identity_.birthPlace, pattern, caseSensitive));
					break;
				case CIVIL_STATUS:
					predicate = link(cb, asConjunction, predicate,
							generateLikePredicateLinkedByOr(cb, path1, path2, Identity_.civilStatus, pattern, caseSensitive));
					break;
				case DEGREE:
					predicate = link(cb, asConjunction, predicate,
							generateLikePredicateLinkedByOr(cb, path1, path2, Identity_.degree, pattern, caseSensitive));
					break;
				case FIRST_NAME:
					predicate = link(cb, asConjunction, predicate,
							generateLikePredicateLinkedByOr(cb, path1, path2, Identity_.firstName, pattern, caseSensitive));
					break;
				case GENDER:
					if (caseSensitive)
					{
						predicate = link(cb, asConjunction, predicate, cb.or(
								cb.equal(path1.get(Identity_.gender).as(String.class), pattern),
								cb.equal(path2.get(Identity_.gender).as(String.class), pattern)));
					}
					else
					{
						predicate = link(cb, asConjunction, predicate, cb.or(
								cb.equal(
										cb.lower(path1.get(Identity_.gender).as(String.class)),
										cb.lower(cb.literal(pattern))),
								cb.equal(
										cb.lower(path2.get(Identity_.gender).as(String.class)),
										cb.lower(cb.literal(pattern)))));
					}
					break;
				case IDENTITY_ID:
					predicate = link(cb, asConjunction, predicate, cb.or(
							cb.like(path1.get(Identity_.id).as(String.class), pattern),
							cb.like(path2.get(Identity_.id).as(String.class), pattern)));
					break;
				case LAST_NAME:
					predicate = link(cb, asConjunction, predicate,
							generateLikePredicateLinkedByOr(cb, path1, path2, Identity_.lastName, pattern, caseSensitive));
					break;
				case MIDDLE_NAME:
					predicate = link(cb, asConjunction, predicate,
							generateLikePredicateLinkedByOr(cb, path1, path2, Identity_.middleName, pattern, caseSensitive));
					break;
				case MOTHERS_MAIDEN_NAME:
					predicate = link(cb, asConjunction, predicate,
							generateLikePredicateLinkedByOr(cb, path1, path2, Identity_.mothersMaidenName, pattern, caseSensitive));
					break;
				case MOTHER_TONGUE:
					predicate = link(cb, asConjunction, predicate,
							generateLikePredicateLinkedByOr(cb, path1, path2, Identity_.motherTongue, pattern, caseSensitive));
					break;
				case NATIONALITY:
					predicate = link(cb, asConjunction, predicate,
							generateLikePredicateLinkedByOr(cb, path1, path2, Identity_.nationality, pattern, caseSensitive));
					break;
				case NONE:
					break;
				case PERSON_ID:
					predicate = link(cb, asConjunction, predicate, cb.or(
							cb.like(path1.get(Identity_.person).get(Person_.id).as(String.class), pattern),
							cb.like(path2.get(Identity_.person).get(Person_.id).as(String.class), pattern)));
					break;
				case PREFIX:
					predicate = link(cb, asConjunction, predicate,
							generateLikePredicateLinkedByOr(cb, path1, path2, Identity_.prefix, pattern, caseSensitive));
					break;
				case RACE:
					predicate = link(cb, asConjunction, predicate,
							generateLikePredicateLinkedByOr(cb, path1, path2, Identity_.race, pattern, caseSensitive));
					break;
				case RELIGION:
					predicate = link(cb, asConjunction, predicate,
							generateLikePredicateLinkedByOr(cb, path1, path2, Identity_.religion, pattern, caseSensitive));
					break;
				case SOURCE:
					if (caseSensitive)
					{
						predicate = link(cb, asConjunction, predicate,
								generateLikePredicateLinkedByOr(cb, path1.get(Identity_.source), path2.get(Identity_.source), Source_.name, pattern, caseSensitive));
					}
					else
					{
						predicate = link(cb, asConjunction, predicate, cb.or(
								cb.like(
										cb.lower(path1.get(Identity_.source).get(Source_.name).as(String.class)),
										cb.lower(cb.literal(pattern))),
								cb.like(
										cb.lower(path2.get(Identity_.source).get(Source_.name).as(String.class)),
										cb.lower(cb.literal(pattern)))));
					}
					break;
				case SUFFIX:
					predicate = link(cb, asConjunction, predicate,
							generateLikePredicateLinkedByOr(cb, path1, path2, Identity_.suffix, pattern, caseSensitive));
					break;
				case VALUE1:
					predicate = link(cb, asConjunction, predicate,
							generateLikePredicateLinkedByOr(cb, path1, path2, Identity_.value1, pattern, caseSensitive));
					break;
				case VALUE10:
					predicate = link(cb, asConjunction, predicate,
							generateLikePredicateLinkedByOr(cb, path1, path2, Identity_.value10, pattern, caseSensitive));
					break;
				case VALUE2:
					predicate = link(cb, asConjunction, predicate,
							generateLikePredicateLinkedByOr(cb, path1, path2, Identity_.value2, pattern, caseSensitive));
					break;
				case VALUE3:
					predicate = link(cb, asConjunction, predicate,
							generateLikePredicateLinkedByOr(cb, path1, path2, Identity_.value3, pattern, caseSensitive));
					break;
				case VALUE4:
					predicate = link(cb, asConjunction, predicate,
							generateLikePredicateLinkedByOr(cb, path1, path2, Identity_.value4, pattern, caseSensitive));
					break;
				case VALUE5:
					predicate = link(cb, asConjunction, predicate,
							generateLikePredicateLinkedByOr(cb, path1, path2, Identity_.value5, pattern, caseSensitive));
					break;
				case VALUE6:
					predicate = link(cb, asConjunction, predicate,
							generateLikePredicateLinkedByOr(cb, path1, path2, Identity_.value6, pattern, caseSensitive));
					break;
				case VALUE7:
					predicate = link(cb, asConjunction, predicate,
							generateLikePredicateLinkedByOr(cb, path1, path2, Identity_.value7, pattern, caseSensitive));
					break;
				case VALUE8:
					predicate = link(cb, asConjunction, predicate,
							generateLikePredicateLinkedByOr(cb, path1, path2, Identity_.value8, pattern, caseSensitive));
					break;
				case VALUE9:
					predicate = link(cb, asConjunction, predicate,
							generateLikePredicateLinkedByOr(cb, path1, path2, Identity_.value9, pattern, caseSensitive));
					break;
				default:
					logger.warn("unimplemented IdentityField '" + entry.getKey().name()
							+ "' for filter-clause within generatePredicateForIdentityPair");
					break;
			}
		}
		return predicate;
	}

	static Predicate link(CriteriaBuilder cb, boolean asConjunction, Predicate p1,  Predicate p2)
	{
		return asConjunction ? cb.and(p1, p2) : cb.or(p1, p2);
	}

	/**
	 * Generalizes the creation of a like predicate for case-sensitive and case-insensitive situations.
	 * @param cb the criteria builder
	 * @param path the path
	 * @param attribute the attribute
	 * @param value the value
	 * @param caseSensitive true to respect case
	 * @param <T> the type of the path
	 * @return the like predicate
	 */
	static <T> Predicate generateLikePredicate(CriteriaBuilder cb, Path<T> path,
			SingularAttribute<T, String> attribute, String value, boolean caseSensitive)
	{
		if (caseSensitive)
		{
			return cb.like(path.get(attribute), value);
		}
		else
		{
			return cb.like(cb.lower(path.get(attribute)), cb.lower(cb.literal(value)));
		}
	}

	static <T> Predicate generateLikePredicateLinkedByOr(CriteriaBuilder cb, Path<T> path1, Path<T> path2,
			SingularAttribute<T, String> attribute, String value, boolean caseSensitive)
	{
		if (path1 == null)
		{
			return generateLikePredicate(cb, path2, attribute, value, caseSensitive);
		}
		else if (path2 == null)
		{
			return generateLikePredicate(cb, path1, attribute, value, caseSensitive);
		}
		else
		{
			return cb.or(
					generateLikePredicate(cb, path1, attribute, value, caseSensitive),
					generateLikePredicate(cb, path2, attribute, value, caseSensitive));
		}
	}

	/**
	 * Creates a predicate which matches a pattern to the string representation of a date entity
	 * where the representation is defined by a date format pattern as described in SQL's DATE_FORMAT function.
	 * @param cb the criteria builder
	 * @param dateEntity the date entity
	 * @param pattern the filter pattern
	 * @param format the date format
	 * @return the date match predicate
	 */
	static Predicate generateDateMatchPredicate(CriteriaBuilder cb, Path<? extends Date> dateEntity, String pattern, String format)
	{
		if (!format.contains("'"))
		{
			format = "'" + format + "'";
		}
		Expression<String> dateExpression = cb.function("DATE_FORMAT", String.class, dateEntity, cb.literal(format));
		return cb.like(cb.lower(dateExpression), pattern.toLowerCase());
	}

	static Predicate generateBirthDateMatchPredicate(CriteriaBuilder cb, Path<Identity> dateEntity, String pattern, String format)
	{
		return generateDateMatchPredicate(cb, dateEntity.get(Identity_.birthDate) , pattern, format);
	}

	static Predicate generateBirthDateMatchPredicateLinkedByOr(CriteriaBuilder cb, Path<Identity> dateEntity1,  Path<Identity> dateEntity2, String pattern, String format)
	{
		if (dateEntity1 == null)
		{
			return generateBirthDateMatchPredicate(cb, dateEntity2 , pattern, format);
		}
		else if (dateEntity2 == null)
		{
			return generateBirthDateMatchPredicate(cb, dateEntity1, pattern, format);
		}
		else
		{
			return cb.or(
					generateBirthDateMatchPredicate(cb, dateEntity1, pattern, format),
					generateBirthDateMatchPredicate(cb, dateEntity2, pattern, format));
		}
	}

	/**
	 * Converts a Java date-/time-format pattern (as defined in {@link SimpleDateFormat}) into a form
	 * which is compatible with the DATE_FORMAT function in SQL.
	 *
	 * @param format the Java date format
	 * @return the SQL date format
	 */
	static String convertSimpleDateTimeFormat(String format)
	{
		if (format == null || format.isEmpty()) {
			format = new SimpleDateFormat().toLocalizedPattern().replaceAll(" HH:.*", "");
		}
		// dd.MM.yyyy HH:mm:ss
		// yyyy-MM-dd HH:mm:ss
		// yyyy/MM/dd HH:mm:ss
		// TODO: write a more intelligent parser/converter which does not assume one of the format strings from above as input

		format = format.replace("yyyy", "%Y");
		format = format.replace("yy", "%y");
		format = format.replace("MM", "%m");
		format = format.replace("dd", "%d");
		format = format.replace("HH", "%H");
		format = format.replace("mm", "%i");
		format = format.replace("ss", "%s");
		format = format.replace("a", "%p");

		return format;
	}

	/**
	 * Returns the localized default date format pattern without the time part
	 * but converted into a format as described in SQL's DATE_FORMAT function.
	 *
	 * @return the localized default date format pattern without the time part (as described in SQL's DATE_FORMAT function)
	 * @see SimpleDateFormat#SimpleDateFormat()
	 * @see SimpleDateFormat#toLocalizedPattern()
	 */
	static String createDefaultDateFormat()
	{
		return convertSimpleDateTimeFormat(null);
	}

	/**
	 * Returns the localized default time format pattern converted into a format
	 * as described in SQL's DATE_FORMAT function.
	 *
	 * @return the localized default time format pattern (as described in SQL's DATE_FORMAT function)
	 * @see SimpleDateFormat#SimpleDateFormat()
	 * @see SimpleDateFormat#toLocalizedPattern()
	 */
	static String createDefaultTimeFormat()
	{
		return convertSimpleDateTimeFormat(new SimpleDateFormat().toLocalizedPattern());
	}
}
