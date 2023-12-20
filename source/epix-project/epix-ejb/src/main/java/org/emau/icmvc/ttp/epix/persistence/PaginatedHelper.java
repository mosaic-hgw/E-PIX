package org.emau.icmvc.ttp.epix.persistence;

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
import org.emau.icmvc.ttp.epix.common.model.enums.PossibleMatchPriority;
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
 * hilfsfunktionen fuer paginated-abfragen, nur wegen der uebersichtlichkeit aus publicDAO herausgezogen.
 *
 * Kleine Eselsbrücke:<br>
 * conjuction (and); alle müssen wahr sein: keiner da, alles wahr -> TRUE <br>
 * disjunction (OR): einer muss wahr sein: keiner da, nichts wahr -> FALSE <br>
 *
 * @author geidell
 */
class PaginatedHelper
{

	private static final Logger logger = LogManager.getLogger(PublicDAO.class);

	// ====== Person ==========================================================================================================================

	// TODO(FMM) rewrite using pagination config and add dateOfDeath
	static Predicate generateWherePredicateForPerson(CriteriaBuilder cb, Path<Person> path, Domain domain, Map<PersonField, String> filter,
			boolean filterIsCaseSensitive)
	{
		String dateFormat = createDefaultDateFormat(); // TODO(FMM): use date format from pagination config
		String timeFormat = createDefaultTimeFormat(); // TODO(FMM): use time format from pagination config
		boolean asConjunction = true;
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
					case PERSON_CREATED:
						predicate = link(cb, asConjunction, predicate,
								generateDateMatchPredicate(cb, path.get(Person_.createTimestamp), entry.getValue(), timeFormat));
						break;
					case PERSON_LAST_EDITED:
						predicate = link(cb, asConjunction, predicate,
								generateDateMatchPredicate(cb, path.get(Person_.timestamp), entry.getValue(), timeFormat));
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

	// TODO(FMM) rewrite using pagination config
	static Predicate generateWherePredicateForIdentity(CriteriaBuilder cb, Path<Identity> path, Domain domain, Map<IdentityField, String> filter,
			boolean filterIsCaseSensitive)
	{
		String dateFormat = createDefaultDateFormat(); // TODO(FMM): use date format from pagination config
		String timeFormat = createDefaultTimeFormat(); // TODO(FMM): use time format from pagination config
		boolean asConjunction = true;
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
								generateLikePredicate(cb, path.get(Identity_.birthPlace), entry.getValue(), filterIsCaseSensitive));
						break;
					case CIVIL_STATUS:
						predicate = cb.and(predicate,
								generateLikePredicate(cb, path.get(Identity_.civilStatus), entry.getValue(), filterIsCaseSensitive));
						break;
					case DEGREE:
						predicate = cb.and(predicate,
								generateLikePredicate(cb, path.get(Identity_.degree), entry.getValue(), filterIsCaseSensitive));
						break;
					case FIRST_NAME:
						predicate = cb.and(predicate,
								generateLikePredicate(cb, path.get(Identity_.firstName), entry.getValue(), filterIsCaseSensitive));
						break;
					case GENDER:
						predicate = link(cb, asConjunction, predicate,
								generateStatusMatchPredicate(cb, path.get(Identity_.gender), entry.getValue(), false));
						break;
					case IDENTITY_ID:
						predicate = cb.and(predicate,
								cb.like(path.get(Identity_.id).as(String.class), entry.getValue()));
						break;
					case LAST_NAME:
						predicate = cb.and(predicate,
								generateLikePredicate(cb, path.get(Identity_.lastName), entry.getValue(), filterIsCaseSensitive));
						break;
					case MIDDLE_NAME:
						predicate = cb.and(predicate,
								generateLikePredicate(cb, path.get(Identity_.middleName), entry.getValue(), filterIsCaseSensitive));
						break;
					case MOTHERS_MAIDEN_NAME:
						predicate = cb.and(predicate,
								generateLikePredicate(cb, path.get(Identity_.mothersMaidenName), entry.getValue(), filterIsCaseSensitive));
						break;
					case MOTHER_TONGUE:
						predicate = cb.and(predicate,
								generateLikePredicate(cb, path.get(Identity_.motherTongue), entry.getValue(), filterIsCaseSensitive));
						break;
					case NATIONALITY:
						predicate = cb.and(predicate,
								generateLikePredicate(cb, path.get(Identity_.nationality), entry.getValue(), filterIsCaseSensitive));
						break;
					case NONE:
						break;
					case PERSON_ID:
						predicate = cb.and(predicate,
								cb.like(path.get(Identity_.person).get(Person_.id).as(String.class), entry.getValue()));
						break;
					case PREFIX:
						predicate = cb.and(predicate,
								generateLikePredicate(cb, path.get(Identity_.prefix), entry.getValue(), filterIsCaseSensitive));
						break;
					case RACE:
						predicate = cb.and(predicate,
								generateLikePredicate(cb, path.get(Identity_.race), entry.getValue(), filterIsCaseSensitive));
						break;
					case RELIGION:
						predicate = cb.and(predicate,
								generateLikePredicate(cb, path.get(Identity_.religion), entry.getValue(), filterIsCaseSensitive));
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
								generateLikePredicate(cb, path.get(Identity_.suffix), entry.getValue(), filterIsCaseSensitive));
						break;
					case VALUE1:
						predicate = cb.and(predicate,
								generateLikePredicate(cb, path.get(Identity_.value1), entry.getValue(), filterIsCaseSensitive));
						break;
					case VALUE10:
						predicate = cb.and(predicate,
								generateLikePredicate(cb, path.get(Identity_.value10), entry.getValue(), filterIsCaseSensitive));
						break;
					case VALUE2:
						predicate = cb.and(predicate,
								generateLikePredicate(cb, path.get(Identity_.value2), entry.getValue(), filterIsCaseSensitive));
						break;
					case VALUE3:
						predicate = cb.and(predicate,
								generateLikePredicate(cb, path.get(Identity_.value3), entry.getValue(), filterIsCaseSensitive));
						break;
					case VALUE4:
						predicate = cb.and(predicate,
								generateLikePredicate(cb, path.get(Identity_.value4), entry.getValue(), filterIsCaseSensitive));
						break;
					case VALUE5:
						predicate = cb.and(predicate,
								generateLikePredicate(cb, path.get( Identity_.value5), entry.getValue(), filterIsCaseSensitive));
						break;
					case VALUE6:
						predicate = cb.and(predicate,
								generateLikePredicate(cb, path.get(Identity_.value6), entry.getValue(), filterIsCaseSensitive));
						break;
					case VALUE7:
						predicate = cb.and(predicate,
								generateLikePredicate(cb, path.get(Identity_.value7), entry.getValue(), filterIsCaseSensitive));
						break;
					case VALUE8:
						predicate = cb.and(predicate,
								generateLikePredicate(cb, path.get(Identity_.value8), entry.getValue(), filterIsCaseSensitive));
						break;
					case VALUE9:
						predicate = cb.and(predicate,
								generateLikePredicate(cb, path.get(Identity_.value9), entry.getValue(), filterIsCaseSensitive));
						break;
					case VITAL_STATUS:
						predicate = link(cb, asConjunction, predicate,
								generateStatusMatchPredicate(cb, path.get(Identity_.vitalStatus), entry.getValue(), filterIsCaseSensitive));
						break;
					case DATE_OF_DEATH:
						predicate = link(cb, asConjunction, predicate,
								generateDateMatchPredicate(cb, path.get(Identity_.dateOfDeath), entry.getValue(), dateFormat));
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
				case VITAL_STATUS:
					order = path.get(Identity_.vitalStatus);
					break;
				case DATE_OF_DEATH:
					order = path.get(Identity_.dateOfDeath);
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
		pc = normalized(pc);

		if (pc.isUsingSolutionFiltering())
		{
			logger.warn("Ignoring solution filter");
		}

		if (pc.isUsingPersonFiltering())
		{
			logger.warn("Ignoring person filter");
		}

		Set<IdentityHistoryEvent> eventFilter = pc.getEventFilter();
		Map<IdentityField, String> identityFilter = pc.getIdentityFilter();
		boolean filterIsCaseSensitive = pc.isIdentityFilterCaseSensitive();
		boolean asConjunction = pc.isIdentityFilterAsConjunction();
		String dateFormat = pc.getDateFormat() == null ? createDefaultDateFormat() : convertSimpleDateTimeFormat(pc.getDateFormat());
		String timeFormat = pc.getTimeFormat() == null ? createDefaultTimeFormat() : convertSimpleDateTimeFormat(pc.getTimeFormat());

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
			String globalFilterPattern = pc.getGlobalIdentityFilterPattern();

			if (globalFilterPattern != null && !globalFilterPattern.isEmpty()) // this implies: asConjunction == false
			{
				// when searching as disjunction (OR), then also search in history event'S MPI and creation timestamp
				logger.debug("generateWherePredicateForIdentityHistory: adding timestamp, MPI, and event type columns for global search");
				search = link(cb, false, search,
							generateDateMatchPredicate(cb, path.get(IdentityHistory_.historyTimestamp), globalFilterPattern, timeFormat));
				search = link(cb, false, search,
							cb.like(path.get(IdentityHistory_.person).get(Person_.firstMPI).get(Identifier_.value), globalFilterPattern));
			}

			for (Entry<IdentityField, String> entry : identityFilter.entrySet())
			{
				switch (entry.getKey())
				{
					case BIRTH_DATE:
						search = link(cb, asConjunction, search,
								generateDateMatchPredicate(cb, path.get(IdentityHistory_.birthDate), entry.getValue(), dateFormat));
						break;
					case BIRTHPLACE:
						search = link(cb, asConjunction, search,
								generateLikePredicate(cb, path.get(IdentityHistory_.birthPlace), entry.getValue(), filterIsCaseSensitive));
						break;
					case CIVIL_STATUS:
						search = link(cb, asConjunction, search,
								generateLikePredicate(cb, path.get(IdentityHistory_.civilStatus), entry.getValue(), filterIsCaseSensitive));
						break;
					case DEGREE:
						search = link(cb, asConjunction, search,
								generateLikePredicate(cb, path.get(IdentityHistory_.degree), entry.getValue(), filterIsCaseSensitive));
						break;
					case FIRST_NAME:
						search = link(cb, asConjunction, search,
								generateLikePredicate(cb, path.get(IdentityHistory_.firstName), entry.getValue(), filterIsCaseSensitive));
						break;
					case GENDER:
						search = link(cb, asConjunction, search,
								generateStatusMatchPredicate(cb, path.get(IdentityHistory_.gender), entry.getValue(), false));
						break;
					case IDENTITY_ID:
						search = link(cb, asConjunction, search,
								cb.like(path.get(IdentityHistory_.id).as(String.class), entry.getValue()));
						break;
					case LAST_NAME:
						search = link(cb, asConjunction, search,
								generateLikePredicate(cb, path.get(IdentityHistory_.lastName), entry.getValue(), filterIsCaseSensitive));
						break;
					case MIDDLE_NAME:
						search = link(cb, asConjunction, search,
								generateLikePredicate(cb, path.get(IdentityHistory_.middleName), entry.getValue(), filterIsCaseSensitive));
						break;
					case MOTHERS_MAIDEN_NAME:
						search = link(cb, asConjunction, search,
								generateLikePredicate(cb, path.get(IdentityHistory_.mothersMaidenName), entry.getValue(), filterIsCaseSensitive));
						break;
					case MOTHER_TONGUE:
						search = link(cb, asConjunction, search,
								generateLikePredicate(cb, path.get(IdentityHistory_.motherTongue), entry.getValue(), filterIsCaseSensitive));
						break;
					case NATIONALITY:
						search = link(cb, asConjunction, search,
								generateLikePredicate(cb, path.get(IdentityHistory_.nationality), entry.getValue(), filterIsCaseSensitive));
						break;
					case NONE:
						break;
					case PERSON_ID:
						search = link(cb, asConjunction, search,
								cb.like(path.get(IdentityHistory_.person).get(Person_.id).as(String.class), entry.getValue()));
						break;
					case PREFIX:
						search = link(cb, asConjunction, search,
								generateLikePredicate(cb, path.get(IdentityHistory_.prefix), entry.getValue(), filterIsCaseSensitive));
						break;
					case RACE:
						search = link(cb, asConjunction, search,
								generateLikePredicate(cb, path.get(IdentityHistory_.race), entry.getValue(), filterIsCaseSensitive));
						break;
					case RELIGION:
						search = link(cb, asConjunction, search,
								generateLikePredicate(cb, path.get(IdentityHistory_.religion), entry.getValue(), filterIsCaseSensitive));
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
								generateLikePredicate(cb, path.get(IdentityHistory_.suffix), entry.getValue(), filterIsCaseSensitive));
						break;
					case VALUE1:
						search = link(cb, asConjunction, search,
								generateLikePredicate(cb, path.get(IdentityHistory_.value1), entry.getValue(), filterIsCaseSensitive));
						break;
					case VALUE10:
						search = link(cb, asConjunction, search,
								generateLikePredicate(cb, path.get(IdentityHistory_.value10), entry.getValue(), filterIsCaseSensitive));
						break;
					case VALUE2:
						search = link(cb, asConjunction, search,
								generateLikePredicate(cb, path.get(IdentityHistory_.value2), entry.getValue(), filterIsCaseSensitive));
						break;
					case VALUE3:
						search = link(cb, asConjunction, search,
								generateLikePredicate(cb, path.get(IdentityHistory_.value3), entry.getValue(), filterIsCaseSensitive));
						break;
					case VALUE4:
						search = link(cb, asConjunction, search,
								generateLikePredicate(cb, path.get(IdentityHistory_.value4), entry.getValue(), filterIsCaseSensitive));
						break;
					case VALUE5:
						search = link(cb, asConjunction, search,
								generateLikePredicate(cb, path.get(IdentityHistory_.value5), entry.getValue(), filterIsCaseSensitive));
						break;
					case VALUE6:
						search = link(cb, asConjunction, search,
								generateLikePredicate(cb, path.get(IdentityHistory_.value6), entry.getValue(), filterIsCaseSensitive));
						break;
					case VALUE7:
						search = link(cb, asConjunction, search,
								generateLikePredicate(cb, path.get(IdentityHistory_.value7), entry.getValue(), filterIsCaseSensitive));
						break;
					case VALUE8:
						search = link(cb, asConjunction, search,
								generateLikePredicate(cb, path.get(IdentityHistory_.value8), entry.getValue(), filterIsCaseSensitive));
						break;
					case VALUE9:
						search = link(cb, asConjunction, search,
								generateLikePredicate(cb, path.get(IdentityHistory_.value9), entry.getValue(), filterIsCaseSensitive));
						break;
					case VITAL_STATUS:
						predicate = link(cb, asConjunction, predicate,
								generateStatusMatchPredicate(cb, path.get(IdentityHistory_.vitalStatus), entry.getValue(), filterIsCaseSensitive));
						break;
					case DATE_OF_DEATH:
						predicate = link(cb, asConjunction, predicate,
								generateDateMatchPredicate(cb, path.get(IdentityHistory_.dateOfDeath), entry.getValue(), dateFormat));
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
				case VITAL_STATUS:
					order = path.get(IdentityHistory_.vitalStatus);
					break;
				case DATE_OF_DEATH:
					order = path.get(IdentityHistory_.dateOfDeath);
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
	 * Returns a query which searches for filter patterns as substring in the corresponding fields of {@link Identity_}.
	 *
	 * @param cb a criteria builder
	 * @param domain the domain
	 * @param pc the pagination config
	 * @return the search query
	 */
	static CriteriaQuery<IdentityLink> generateWhereQueryForIdentityLink(CriteriaBuilder cb, Domain domain, PaginationConfig pc)
	{
		pc = normalized(pc);
		CriteriaQuery<IdentityLink> cq = cb.createQuery(IdentityLink.class);
		Root<IdentityLink> root = cq.from(IdentityLink.class);
		Predicate predicate = generateWherePredicateForIdentityLink(cb, root, domain, pc);
		cq.select(root).where(predicate);

		boolean asc = pc.isSortIsAscending();
		Expression<?> order = pc.isUsingSorting() ? generateSortExpressionForIdentityLink(pc.getSortField(), root) : null;

		if (order == null)
		{
			asc = false;
			order = generateSortExpressionForIdentityLink(IdentityField.NONE, root); // default sort order by creation time
		}

		cq.orderBy(asc ? cb.asc(order) : cb.desc(order));

		return cq;
	}

	/**
	 * Returns a predicate which describes a search for possible matches {@link IdentityLink_}.
	 * When the pagination config's filter only contains a pattern for {@link IdentityField#NONE}, then
	 * the default filter set will be used, which is to search for a pattern as substring in both identities' {@link Identity_#lastName},
	 * {@link Identity_#firstName}, {@link Identity_#birthDate}, {@link Identity_#id} and in {@link IdentityLink_#createTimestamp}.
	 *
	 * @param cb a criteria builder
	 * @param path the path entity for the from-query
	 * @param domain the domain
	 * @param pc the pagination configuration
	 * @return the search predicate
	 */
	static Predicate generateWherePredicateForIdentityLink(CriteriaBuilder cb, Path<IdentityLink> path, Domain domain, PaginationConfig pc)
	{
		pc = normalized(pc);

		if (pc.isUsingSolutionFiltering())
		{
			logger.warn("Ignoring solution filter when searching possible matches");
		}

		if (pc.isUsingEventFiltering())
		{
			logger.warn("Ignoring event filter when searching possible matches");
		}

		String dateFormat = pc.getDateFormat() == null ? createDefaultDateFormat() : convertSimpleDateTimeFormat(pc.getDateFormat());
		String timeFormat = pc.getTimeFormat() == null ? createDefaultTimeFormat() : convertSimpleDateTimeFormat(pc.getTimeFormat());
		Predicate predicate = pc.isIdentityAndPersonFilterCombinedAsConjunction() ? cb.conjunction() : cb.disjunction();
		boolean configured = false;

		if (pc.isUsingIdentityFiltering())
		{
			if (pc.detectAndConfigureGlobalIdentityFiltering(Set.of(IdentityField.LAST_NAME, IdentityField.FIRST_NAME, IdentityField.BIRTH_DATE)))
			{
				logger.debug("Configured default identity filter set for globally searching possible matches (" + pc.getIdentityFilter() + ")");
				configured = true;
			}

			predicate = link(cb, pc.isIdentityAndPersonFilterCombinedAsConjunction(), predicate,
					generateFilterPredicateForIdentityPair(cb,
							path.get(IdentityLink_.destIdentity),
							path.get(IdentityLink_.srcIdentity),
							pc.getIdentityFilter(), pc.isIdentityFilterCaseSensitive(), pc.isIdentityFilterAsConjunction(), dateFormat));
		}

		if (pc.isUsingPersonFiltering())
		{
			if (pc.detectAndConfigureGlobalPersonFiltering(Set.of(PersonField.MPI)))
			{
				logger.debug("Configured default person filter set for globally searching possible matches (" + pc.getPersonFilter() + ")");
				configured = true;
			}

			predicate = link(cb, pc.isIdentityAndPersonFilterCombinedAsConjunction(), predicate,
					generateFilterPredicateForPersonPair(cb,
							path.get(IdentityLink_.destIdentity).get(Identity_.person),
							path.get(IdentityLink_.srcIdentity).get(Identity_.person),
							pc.getPersonFilter(), pc.isPersonFilterCaseSensitive(), pc.isPersonFilterAsConjunction(), timeFormat));
		}

		if (configured)
		{
			logger.debug("Final pagination config is " + pc);
		}

		if (pc.isUsingCreateTimestampFiltering())
		{
			predicate = cb.or(predicate, generateDateMatchPredicate(cb,
					path.get(IdentityLink_.createTimestamp), pc.getCreateTimestampFilter(), timeFormat));
		}

		if (predicate.getExpressions().isEmpty())
		{
			// no filtering so far, we want all entries
			predicate = cb.conjunction(); // AND: empty conjunction is true
		}

		if (pc.isUsingPriorityFiltering())
		{
			Predicate priorityPredicate = cb.disjunction(); // OR: empty disjunction is false (but we will not be empty)
			for (PossibleMatchPriority s : pc.getPriorityFilter())
			{
				priorityPredicate = cb.or(priorityPredicate, cb.like(path.get(IdentityLink_.priority).as(String.class), s.name()));
			}
			predicate = cb.and(predicate, priorityPredicate);
		}

		// only search for entries in the given domain
		Predicate domainPredicate = cb.equal(path.get(IdentityLink_.srcIdentity).get(Identity_.person).get(Person_.domain), domain);

		return cb.and(domainPredicate, predicate);
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
		pc = normalized(pc);

		if (pc.isUsingEventFiltering())
		{
			logger.warn("Ignoring event filter");
		}

		if (pc.isUsingPersonFiltering())
		{
			logger.warn("Ignoring person filter");
		}

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
							generateDateMatchPredicate(cb, path1.get(Identity_.birthDate), path2.get(Identity_.birthDate), pattern, dateFormat, false));
					break;
				case BIRTHPLACE:
					predicate = link(cb, asConjunction, predicate,
							generateLikePredicate(cb, path1, path2, Identity_.birthPlace, pattern, caseSensitive, false));
					break;
				case CIVIL_STATUS:
					predicate = link(cb, asConjunction, predicate,
							generateLikePredicate(cb, path1, path2, Identity_.civilStatus, pattern, caseSensitive, false));
					break;
				case DEGREE:
					predicate = link(cb, asConjunction, predicate,
							generateLikePredicate(cb, path1, path2, Identity_.degree, pattern, caseSensitive, false));
					break;
				case FIRST_NAME:
					predicate = link(cb, asConjunction, predicate,
							generateLikePredicate(cb, path1, path2, Identity_.firstName, pattern, caseSensitive, false));
					break;
				case GENDER:
					predicate = link(cb, asConjunction, predicate,
							generateStatusMatchPredicate(cb, path1.get(Identity_.gender), path2.get(Identity_.gender), entry.getValue(), false, asConjunction));
					break;
				case IDENTITY_ID:
					predicate = link(cb, asConjunction, predicate, cb.or(
							cb.like(path1.get(Identity_.id).as(String.class), pattern),
							cb.like(path2.get(Identity_.id).as(String.class), pattern)));
					break;
				case LAST_NAME:
					predicate = link(cb, asConjunction, predicate,
							generateLikePredicate(cb, path1, path2, Identity_.lastName, pattern, caseSensitive, false));
					break;
				case MIDDLE_NAME:
					predicate = link(cb, asConjunction, predicate,
							generateLikePredicate(cb, path1, path2, Identity_.middleName, pattern, caseSensitive, false));
					break;
				case MOTHERS_MAIDEN_NAME:
					predicate = link(cb, asConjunction, predicate,
							generateLikePredicate(cb, path1, path2, Identity_.mothersMaidenName, pattern, caseSensitive, false));
					break;
				case MOTHER_TONGUE:
					predicate = link(cb, asConjunction, predicate,
							generateLikePredicate(cb, path1, path2, Identity_.motherTongue, pattern, caseSensitive, false));
					break;
				case NATIONALITY:
					predicate = link(cb, asConjunction, predicate,
							generateLikePredicate(cb, path1, path2, Identity_.nationality, pattern, caseSensitive, false));
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
							generateLikePredicate(cb, path1, path2, Identity_.prefix, pattern, caseSensitive, false));
					break;
				case RACE:
					predicate = link(cb, asConjunction, predicate,
							generateLikePredicate(cb, path1, path2, Identity_.race, pattern, caseSensitive, false));
					break;
				case RELIGION:
					predicate = link(cb, asConjunction, predicate,
							generateLikePredicate(cb, path1, path2, Identity_.religion, pattern, caseSensitive, false));
					break;
				case SOURCE:
					if (caseSensitive)
					{
						predicate = link(cb, asConjunction, predicate,
								generateLikePredicate(cb, path1.get(Identity_.source), path2.get(Identity_.source), Source_.name, pattern, caseSensitive, false));
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
							generateLikePredicate(cb, path1, path2, Identity_.suffix, pattern, caseSensitive, false));
					break;
				case VALUE1:
					predicate = link(cb, asConjunction, predicate,
							generateLikePredicate(cb, path1, path2, Identity_.value1, pattern, caseSensitive, false));
					break;
				case VALUE10:
					predicate = link(cb, asConjunction, predicate,
							generateLikePredicate(cb, path1, path2, Identity_.value10, pattern, caseSensitive, false));
					break;
				case VALUE2:
					predicate = link(cb, asConjunction, predicate,
							generateLikePredicate(cb, path1, path2, Identity_.value2, pattern, caseSensitive, false));
					break;
				case VALUE3:
					predicate = link(cb, asConjunction, predicate,
							generateLikePredicate(cb, path1, path2, Identity_.value3, pattern, caseSensitive, false));
					break;
				case VALUE4:
					predicate = link(cb, asConjunction, predicate,
							generateLikePredicate(cb, path1, path2, Identity_.value4, pattern, caseSensitive, false));
					break;
				case VALUE5:
					predicate = link(cb, asConjunction, predicate,
							generateLikePredicate(cb, path1, path2, Identity_.value5, pattern, caseSensitive, false));
					break;
				case VALUE6:
					predicate = link(cb, asConjunction, predicate,
							generateLikePredicate(cb, path1, path2, Identity_.value6, pattern, caseSensitive, false));
					break;
				case VALUE7:
					predicate = link(cb, asConjunction, predicate,
							generateLikePredicate(cb, path1, path2, Identity_.value7, pattern, caseSensitive, false));
					break;
				case VALUE8:
					predicate = link(cb, asConjunction, predicate,
							generateLikePredicate(cb, path1, path2, Identity_.value8, pattern, caseSensitive, false));
					break;
				case VALUE9:
					predicate = link(cb, asConjunction, predicate,
							generateLikePredicate(cb, path1, path2, Identity_.value9, pattern, caseSensitive, false));
					break;
				case VITAL_STATUS:
					predicate = link(cb, asConjunction, predicate,
							generateStatusMatchPredicate(cb, path1, path2, Identity_.vitalStatus, pattern, caseSensitive, false));
					break;
				case DATE_OF_DEATH:
					predicate = link(cb, asConjunction, predicate,
							generateDateMatchPredicate(cb, path1, path2, Identity_.dateOfDeath, pattern, dateFormat, false));
				default:
					logger.warn("unimplemented IdentityField '" + entry.getKey().name()
							+ "' for filter-clause within generatePredicateForIdentityPair");
					break;
			}
		}
		return predicate;
	}

	static Predicate generateFilterPredicateForPersonPair(CriteriaBuilder cb, Path<Person> path1, Path<Person> path2,
			Map<PersonField, String> filter, boolean caseSensitive, boolean asConjunction, String timeFormat)
	{
		if (path1 == null || path2 == null) {
			throw new IllegalArgumentException("Paths must not be null");
		}

		Predicate predicate = asConjunction ? cb.conjunction() : cb.disjunction();

		for (Entry<PersonField, String> entry : filter.entrySet())
		{
			String pattern = entry.getValue();
			switch (entry.getKey())
			{
				case MPI:
					predicate = link(cb, asConjunction, predicate,
							generateLikePredicate(cb, path1.get(Person_.firstMPI), path2.get(Person_.firstMPI), Identifier_.value, pattern, caseSensitive, false));
					break;
				case PERSON_ID:
					predicate = link(cb, asConjunction, predicate,
							generateNumberLikePredicate(cb, path1.get(Person_.id), path2.get(Person_.id), pattern, caseSensitive, false));
					break;
				case PERSON_CREATED:
					predicate = link(cb, asConjunction, predicate,
							generateDateMatchPredicate(cb, path1.get(Person_.createTimestamp), path2.get(Person_.createTimestamp), pattern, timeFormat, false));
					break;
				case PERSON_LAST_EDITED:
					predicate = link(cb, asConjunction, predicate,
							generateDateMatchPredicate(cb, path1.get(Person_.timestamp), path2.get(Person_.timestamp), pattern, timeFormat, false));
					break;
				case NONE:
					break;
				default:
					logger.warn("unimplemented PersonField '" + entry.getKey().name()
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
	 * @param p the p
	 * @param value the value
	 * @param caseSensitive true to respect case
	 * @return the like predicate
	 */
	static Predicate generateLikePredicate(CriteriaBuilder cb, Path<String> p, String value, boolean caseSensitive)
	{
		if (caseSensitive)
		{
			return cb.like(p, value);
		}
		else
		{
			return cb.like(cb.lower(p), cb.lower(cb.literal(value)));
		}
	}


	static Predicate generateLikePredicate(CriteriaBuilder cb, Path<String> p1, Path<String> p2,
			String value, boolean caseSensitive, boolean asConjunction)
	{
		if (p1 == null)
		{
			return generateLikePredicate(cb, p2, value, caseSensitive);
		}
		else if (p2 == null)
		{
			return generateLikePredicate(cb, p1, value, caseSensitive);
		}
		else
		{
			return link(cb, asConjunction,
					generateLikePredicate(cb, p1, value, caseSensitive),
					generateLikePredicate(cb, p2, value, caseSensitive));
		}
	}

	static <T> Predicate generateLikePredicate(CriteriaBuilder cb, Path<T> p1, Path<T> p2,
			SingularAttribute<T, String> attribute, String value, boolean caseSensitive, boolean asConjunction)
	{
		return generateLikePredicate(cb, p1.get(attribute), p2.get(attribute), value, caseSensitive, asConjunction);
	}

	/**
	 * Generalizes the creation of a like predicate for numbers.
	 * @param cb the criteria builder
	 * @param p the p
	 * @param value the value
	 * @param caseSensitive true to respect case
	 * @return the like predicate
	 */
	static Predicate generateNumberLikePredicate(CriteriaBuilder cb, Path<? extends Number> p,
			String value, boolean caseSensitive)
	{
		if (caseSensitive)
		{
			return cb.like(p.as(String.class), value);
		}
		else
		{
			return cb.like(cb.lower(p.as(String.class)), cb.lower(cb.literal(value)));
		}
	}

	static Predicate generateNumberLikePredicate(CriteriaBuilder cb, Path<? extends Number> p1,
			Path<? extends Number> p2, String value, boolean caseSensitive, boolean asConjunction)
	{
		if (p1 == null)
		{
			return generateNumberLikePredicate(cb, p2, value, caseSensitive);
		}
		else if (p2 == null)
		{
			return generateNumberLikePredicate(cb, p1, value, caseSensitive);
		}
		else
		{
			return link(cb, asConjunction,
					generateNumberLikePredicate(cb, p1, value, caseSensitive),
					generateNumberLikePredicate(cb, p2, value, caseSensitive));
		}
	}

	static <T> Predicate generateNumberLikePredicate(CriteriaBuilder cb, Path<T> path1,
			Path<T> path2, SingularAttribute<T, ? extends Number> attribute, String value,
			boolean caseSensitive, boolean asConjunction)
	{
		return generateNumberLikePredicate(cb, path1.get(attribute), path2.get(attribute), value, caseSensitive, asConjunction);
	}


	/**
	 * Generalizes the creation of a like predicate for statuses e.g. from enums.
	 * @param cb the criteria builder
	 * @param p the p
	 * @param value the value
	 * @return the like predicate
	 */
	static Predicate generateStatusMatchPredicate(CriteriaBuilder cb, Path<?> p, String value, boolean caseSensitive)
	{
		Predicate predicate = cb.disjunction();
		for (String e : value.split(","))
		{
			if (caseSensitive)
			{
				predicate = cb.or(predicate,
						cb.equal(p.as(String.class), cb.literal(e)));
			}
			else
			{
				predicate = cb.or(predicate,
						cb.equal(cb.lower(p.as(String.class)), cb.lower(cb.literal(e))));
			}
		}
		return predicate;
	}

	static Predicate generateStatusMatchPredicate(CriteriaBuilder cb, Path<?> p1, Path<?> p2,
			String pattern, boolean caseSensitive, boolean asConjunction)
	{
		if (p1 == null)
		{
			return generateStatusMatchPredicate(cb, p2, pattern, caseSensitive);
		}
		else if (p2 == null)
		{
			return generateStatusMatchPredicate(cb, p1, pattern, caseSensitive);
		}
		else
		{
			return link(cb, asConjunction,
					generateStatusMatchPredicate(cb, p1, pattern, caseSensitive),
					generateStatusMatchPredicate(cb, p2, pattern, caseSensitive));
		}
	}

	static <T> Predicate generateStatusMatchPredicate(CriteriaBuilder cb, Path<T> path1, Path<T> path2,
			SingularAttribute<T, ?> attribute, String pattern, boolean caseSensitive, boolean asConjunction)
	{
		return generateStatusMatchPredicate(cb, path1.get(attribute), path2.get(attribute), pattern, caseSensitive, asConjunction);
	}

	/**
	 * Creates a predicate which matches a pattern to the string representation of a date (or time) entity
	 * where the representation is defined by a date (or time) format pattern as described in SQL's DATE_FORMAT function.
	 *
	 * @param cb the criteria builder
	 * @param p the date entity
	 * @param value the filter pattern
	 * @param format the date format
	 * @return the date match predicate
	 */
	static Predicate generateDateMatchPredicate(CriteriaBuilder cb, Path<? extends Date> p, String value, String format)
	{
		if (!format.contains("'"))
		{
			format = "'" + format + "'";
		}
		Expression<String> dateExpression = cb.function("DATE_FORMAT", String.class, p, cb.literal(format));
		return cb.like(cb.lower(dateExpression), value.toLowerCase());
	}

	static Predicate generateDateMatchPredicate(CriteriaBuilder cb, Path<? extends Date> p1,
			Path<? extends Date> p2, String value, String format, boolean asConjunction)
	{
		if (p1 == null)
		{
			return generateDateMatchPredicate(cb, p2 , value, format);
		}
		else if (p2 == null)
		{
			return generateDateMatchPredicate(cb, p1, value, format);
		}
		else
		{
			return link(cb, asConjunction,
					generateDateMatchPredicate(cb, p1, value, format),
					generateDateMatchPredicate(cb, p2, value, format));
		}
	}

	static <T> Predicate generateDateMatchPredicate(CriteriaBuilder cb, Path<T> p1, Path<T> p2,
			SingularAttribute<T, ? extends Date> attribute, String value, String format, boolean asConjunction)
	{
		return generateDateMatchPredicate(cb, p1.get(attribute), p2.get(attribute), value, format, asConjunction);
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

	private static PaginationConfig normalized(PaginationConfig pc)
	{
		pc = pc == null ? new PaginationConfig() : pc;
		pc.normalize();
		return pc;
	}
}
