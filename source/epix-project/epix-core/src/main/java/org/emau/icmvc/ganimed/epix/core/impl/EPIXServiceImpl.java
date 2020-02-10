package org.emau.icmvc.ganimed.epix.core.impl;

/*
 * ###license-information-start###
 * E-PIX - Enterprise Patient Identifier Cross-referencing
 * __
 * Copyright (C) 2009 - 2017 The MOSAIC Project - Institut fuer Community Medicine der
 * 							Universitaetsmedizin Greifswald - mosaic-projekt@uni-greifswald.de
 * 							concept and implementation
 * 							c. schack, d.langner, l. geidel
 * 							web client
 * 							a. blumentritt
 * 							g. weiher
 * 							please cite our publications
 * 							http://dx.doi.org/10.3414/ME14-01-0133
 * 							http://dx.doi.org/10.1186/s12967-015-0545-6
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.emau.icmvc.ganimed.deduplication.config.model.MatchingConfiguration;
import org.emau.icmvc.ganimed.deduplication.config.model.RequiredConfig;
import org.emau.icmvc.ganimed.deduplication.config.model.RequiredField;
import org.emau.icmvc.ganimed.deduplication.config.model.RequiredType;
import org.emau.icmvc.ganimed.deduplication.model.DeduplicationResult;
import org.emau.icmvc.ganimed.deduplication.model.MatchResult;
import org.emau.icmvc.ganimed.epix.common.CriticalMatchSolution;
import org.emau.icmvc.ganimed.epix.common.MPIException;
import org.emau.icmvc.ganimed.epix.common.model.ConfigurationContainer;
import org.emau.icmvc.ganimed.epix.common.model.CriticalMatch;
import org.emau.icmvc.ganimed.epix.common.model.ErrorCode;
import org.emau.icmvc.ganimed.epix.common.model.Gender;
import org.emau.icmvc.ganimed.epix.common.model.LinkedPerson;
import org.emau.icmvc.ganimed.epix.common.model.MPIID;
import org.emau.icmvc.ganimed.epix.common.model.MPIRequest;
import org.emau.icmvc.ganimed.epix.common.model.MPIResponse;
import org.emau.icmvc.ganimed.epix.common.model.QueryResponseEntry;
import org.emau.icmvc.ganimed.epix.common.model.RequestEntry;
import org.emau.icmvc.ganimed.epix.common.model.ResponseEntry;
import org.emau.icmvc.ganimed.epix.common.notifier.NotifactionException;
import org.emau.icmvc.ganimed.epix.common.notifier.Notification;
import org.emau.icmvc.ganimed.epix.common.utils.ReflectionUtil;
import org.emau.icmvc.ganimed.epix.core.EPIX;
import org.emau.icmvc.ganimed.epix.core.EPIXContext;
import org.emau.icmvc.ganimed.epix.core.gen.MPIGenerator;
import org.emau.icmvc.ganimed.epix.core.internal.PersonPreprocessedCache;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.IdentifierDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.IdentifierDomainDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.PersonDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.PersonGroupDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.PersonGroupHistoryDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.PersonHistoryDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.PersonLinkDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.PersonLinkHistoryDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.PersonPreprocessedDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.SourceDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Contact;
import org.emau.icmvc.ganimed.epix.core.persistence.model.HealthcareProvider;
import org.emau.icmvc.ganimed.epix.core.persistence.model.HealthcareProviderHistory;
import org.emau.icmvc.ganimed.epix.core.persistence.model.HealthcareProviderPreprocessed;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Identifier;
import org.emau.icmvc.ganimed.epix.core.persistence.model.IdentifierDomain;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Pair;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Patient;
import org.emau.icmvc.ganimed.epix.core.persistence.model.PatientHistory;
import org.emau.icmvc.ganimed.epix.core.persistence.model.PatientPreprocessed;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Person;
import org.emau.icmvc.ganimed.epix.core.persistence.model.PersonGroup;
import org.emau.icmvc.ganimed.epix.core.persistence.model.PersonGroupHistory;
import org.emau.icmvc.ganimed.epix.core.persistence.model.PersonHistory;
import org.emau.icmvc.ganimed.epix.core.persistence.model.PersonHistory.EVENT;
import org.emau.icmvc.ganimed.epix.core.persistence.model.PersonLink;
import org.emau.icmvc.ganimed.epix.core.persistence.model.PersonLinkHistory;
import org.emau.icmvc.ganimed.epix.core.persistence.model.PersonPreprocessed;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Project;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Source;
import org.emau.icmvc.ganimed.epix.core.persistence.model.enums.IdentifierTypeEnum;
import org.emau.icmvc.ganimed.epix.core.persistence.model.enums.PersonLinkHistoryEnum;
import org.emau.icmvc.ganimed.epix.pdqquery.model.SearchMask;
import org.emau.icmvc.ganimed.epix.service.EPIXService;
import org.emau.icmvc.ganimed.exception.DeduplicationException;

/**
 *
 * @author schackc
 * @since 05.11.2010
 */
public class EPIXServiceImpl extends EPIX implements EPIXService
{

	private final PersonPreprocessedCache<PersonPreprocessed> personPreprocessedCache;
	private static final Object emSynchronizerDummy = new Object();
	private final MatchingConfiguration matchingConfiguration;

	public EPIXServiceImpl(EPIXContext context, PersonPreprocessedCache<PersonPreprocessed> personPreprocessedCache, MatchingConfiguration matchingConfiguration)
	{
		super(context);
		this.matchingConfiguration = matchingConfiguration;
		this.personPreprocessedCache = personPreprocessedCache;
	}

	/**
	 * Adds the persons from the request object to the database. Checks if any
	 * person from the database fits to the person to add.
	 *
	 * @param request
	 *            contains the persons
	 * @return matching decision and MPI-ID for every person
	 * @throws MPIException
	 */
	@Override
	public MPIResponse requestMPI(MPIRequest request) throws MPIException
	{
		if (request == null)
		{
			throw new MPIException(ErrorCode.INVALID_OPERATION_PARAMETER);
		}

		if (request.getRequestEntries() == null)
		{
			throw new MPIException(ErrorCode.INVALID_OPERATION_PARAMETER);
		}

		MPIResponse res = new MPIResponse();

		// Initialize account with associated Project and Roles
		// Not needed for whole request, will be checked for every identifier in
		// a person
		// IdentifierDomain domain = initDomain(request);

		List<RequestEntry> entries = request.getRequestEntries();
		if (logger.isDebugEnabled())
		{
			logger.debug("Handle MPIRequest for " + entries.size() + " entries.");
		}

		// Validate the source and get the source object from the database.
		Source source = validateSource(request.getSource());
		// TODO zu Testzwecken(epix-web übergibt noch keine Source?)
		// Source source = validateSource("ANDERE_QUELLE");

		for (RequestEntry entry : entries)
		{
			try
			{
				res.getResponseEntries().add(detectMPI(entry, source));
				// Flush the entity manager to find this entry in the
				// next entry of this request.
				context.flushEM();
				if (logger.isDebugEnabled())
				{
					logger.debug("Handle MPIRequest for " + entry);
				}
			} catch (MPIException e)
			{
				logger.error(e.getLocalizedMessage(), e);
				throw e;
			}
		}

		if (reportBuilder.isReportActivated())
		{
			reportBuilder.finalizeReport();
			reportBuilder.storeReport();
		}

		return res;
	}

	/**
	 * Checks if any person from the database fits to the person to add. Does
	 * not change anything in the database. It is designed to perform a
	 * pre-check.
	 *
	 * @param request
	 *            contains the persons
	 * @return matching decision for every person
	 * @throws MPIException
	 */
	@Override
	public MPIResponse requestMPINoPersist(MPIRequest request) throws MPIException
	{

		if (request == null)
		{
			throw new MPIException(ErrorCode.INVALID_OPERATION_PARAMETER);
		}

		if (request.getRequestEntries() == null)
		{
			throw new MPIException(ErrorCode.INVALID_OPERATION_PARAMETER);
		}

		// Validate the source and get the source object from the database.
		validateSource(request.getSource());

		MPIResponse res = new MPIResponse();

		List<RequestEntry> entries = request.getRequestEntries();
		for (RequestEntry entry : entries)
		{
			try
			{
				res.getResponseEntries().add(detectMPINoPersist(entry));
			} catch (MPIException e)
			{
				logger.error(e.getLocalizedMessage(), e);
				throw e;
			}
		}
		return res;
	}

	@Override
	public MPIResponse getLinkedPersons(org.emau.icmvc.ganimed.epix.common.model.Person person,
			org.emau.icmvc.ganimed.epix.common.model.IdentifierDomain domain) throws MPIException
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns all critical matches.
	 *
	 * @return all critical matches (also called possible matches) from the
	 *         personlink table
	 * @throws MPIException
	 */
	@Override
	public List<CriticalMatch> requestCriticalMatches() throws MPIException
	{
		List<CriticalMatch> criticalMatches = getCriticalMatches();
		return criticalMatches;
	}

	/**
	 * Returns all critical matches to a specific person.
	 *
	 * @param personId
	 *            database id from the person
	 * @return all critical matches for the requested personId
	 * @throws MPIException
	 */
	@Override
	public List<CriticalMatch> requestLinkedPersons(String personId) throws MPIException
	{
		if (personId == null || personId.isEmpty())
		{
			throw new MPIException(ErrorCode.NO_ATTRIBUTES_DEFINED);
		}
		List<CriticalMatch> criticalMatches = getCriticalMatches(personId);
		return criticalMatches;
	}

	/**
	 * Removes the links between the person from the personId and the persons
	 * from the linkedPersonIds. It means that they are individual persons.
	 *
	 * @param personId
	 *            id from the requested person
	 * @param linkedPersonIds
	 *            ids from the linked persons
	 * @param explanation
	 *            reason for the removing of the links
	 * @throws MPIException
	 */
	@Override
	public void removeLinks(String personId, List<String> linkedPersonIds, String explanation) throws MPIException
	{
		if (personId == null || personId.isEmpty() || linkedPersonIds == null || linkedPersonIds.isEmpty())
		{
			throw new MPIException(ErrorCode.NO_ATTRIBUTES_DEFINED);
		}
		for (String linkedPersonId : linkedPersonIds)
		{
			removeLink(personId, linkedPersonId, explanation);
		}
	}

	/**
	 * Removes the link between the person from the personId and the person from
	 * the linkedPerson. It means that they are individual persons.
	 *
	 * @param personId
	 *            id from the requested person
	 * @param linkedPerson
	 *            id from the linked person
	 * @param explanation
	 *            reason for the removing of the links
	 * @throws MPIException
	 */
	@Override
	public void removeLink(String personId, String linkedPerson, String explanation) throws MPIException
	{
		if (personId == null || personId.isEmpty() || linkedPerson == null || linkedPerson.isEmpty())
		{
			throw new MPIException(ErrorCode.NO_ATTRIBUTES_DEFINED);
		} else if (personId.equals(linkedPerson))
		{
			throw new MPIException(ErrorCode.SAME_PERSON_FOR_MERGE);
		}
		solveCriticalMatch(personId, linkedPerson, CriticalMatchSolution.DIFFERENT_PERSON, explanation);
	}

	/**
	 * Merges the persons of the person link. The first person (personId) is the
	 * reference person. Which means that it is the correct identity. The second
	 * person will be added as an additional identity.
	 *
	 * @param personId
	 *            id of the reference person
	 * @param aliasId
	 *            id of the additional identity
	 * @param explanation
	 *            reason for the merge
	 * @throws MPIException
	 */
	@Override
	public void assignIdentity(String personId, String aliasId, String explanation) throws MPIException
	{
		if (personId == null || personId.isEmpty() || aliasId == null || aliasId.length() == 0)
		{
			throw new MPIException(ErrorCode.NO_ATTRIBUTES_DEFINED);
		}
		solveCriticalMatch(personId, aliasId, CriticalMatchSolution.IDENTICAL_PERSON, explanation);
	}

	/**
	 * Returns all MPI-IDs of one person.
	 *
	 * @param mpiId
	 *            known MPI-ID
	 * @return all known MPI-IDs of the person
	 * @throws MPIException
	 */
	@Override
	public List<String> getAllMpiFromPersonByMpi(String mpiId) throws MPIException
	{
		List<String> mpiList = getAllMpiByMpi(mpiId);
		return mpiList;

	}

	public MPIResponse queryPersonByDemographicData(org.emau.icmvc.ganimed.epix.common.model.Person demographics)
			throws MPIException
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the person, which belongs to the identifier and domain.
	 *
	 * @param identifier
	 * @param domain
	 * @return MPIResponse contains the person which belongs to the identifier
	 *         and domain
	 * @throws MPIException
	 */
	@Override
	public MPIResponse queryPersonByLocalIdentfier(String identifier,
			org.emau.icmvc.ganimed.epix.common.model.IdentifierDomain domain) throws MPIException
	{
		if (identifier != null && domain != null)
		{
			MPIResponse res = new MPIResponse();
			PersonDAO dao = context.getPersonDAO();
			List<Person> persons = dao.getPersonByIdentifierAndDomain(identifier,
					modelMapper.mapIdentifierDomain(domain));
			if (persons != null && !persons.isEmpty())
			{
				if (checkSameGroup(persons))
				{
					res.getResponseEntries()
							.add(createQueryResponseEntry(persons.get(0).getPersonGroup().getReferencePerson(),
									ErrorCode.OK));
				} else
				{
					throw new MPIException(ErrorCode.DIFFERENT_PERSON_GROUP_FOR_SAME_LOCAL_IDENTIFIER);
				}
			}

			return res;
		}
		throw new MPIException(ErrorCode.INVALID_OPERATION_PARAMETER);
	}

	/**
	 * Changes the attributes of the person. Person will be loaded by the given
	 * MPI-ID. Before the update will be performed the new person data will be
	 * matched again the database.
	 *
	 * @param request
	 *            contains the persons to update (MPI-ID is required)
	 * @return the updated person
	 * @throws MPIException
	 */
	@Override
	public MPIResponse updatePerson(MPIRequest request) throws MPIException
	{
		if (request == null)
		{
			throw new MPIException(ErrorCode.INVALID_OPERATION_PARAMETER);
		}

		if (request.getRequestEntries() == null)
		{
			throw new MPIException(ErrorCode.INVALID_OPERATION_PARAMETER);
		}

		MPIResponse res = new MPIResponse();

		// Validate the source and get the source object from the database.
		Source source = validateSource(request.getSource());

		List<RequestEntry> entries = request.getRequestEntries();
		if (logger.isDebugEnabled())
		{
			logger.debug("Handle MPIRequest for " + entries.size() + " entries.");
		}

		for (RequestEntry entry : entries)
		{
			try
			{
				// res.getResponseEntries().add(handleRequestMPIrequestUpdate(entry));
				res.getResponseEntries().add(performMPIrequestUpdate(entry, source, request.isForceReferenceUpdate()));
				context.flushEM();
				if (logger.isDebugEnabled())
				{
					logger.debug("Handle MPIRequest for " + entry);
				}
			} catch (MPIException e)
			{
				logger.error(e.getLocalizedMessage(), e);
				throw e;
			}
		}

		if (reportBuilder.isReportActivated())
		{
			reportBuilder.finalizeReport();
			reportBuilder.storeReport();
		}

		return res;
	}

	/**
	 * Returns all IdentifierDomains. Needed in the web-client.
	 *
	 * @return a list with all IdentifierDomains
	 * @throws MPIException
	 */
	@Override
	public List<org.emau.icmvc.ganimed.epix.common.model.IdentifierDomain> getAllIdentifierDomains()
			throws MPIException
	{
		List<org.emau.icmvc.ganimed.epix.common.model.IdentifierDomain> identifierDomains = getIdentifierDomains();
		return identifierDomains;
	}

	/**
	 * Checks if the given source exists in the database. If not an exception
	 * will be thrown. If no safe source is set the check is deactivated.
	 *
	 * @param source
	 *            given source from the request
	 * @return null if no safe source is set or persistent source from the
	 *         database if a safe source exists and the given source is valid
	 * @throws MPIException
	 */
	private Source validateSource(String source) throws MPIException
	{
		if (context.getSafeSource() == null || context.getSafeSource().getValue().equals(""))
		{
			return null;
		} else if (source == null || source.isEmpty())
		{
			throw new MPIException(ErrorCode.SOURCE_NOT_EXISTS);
		} else
		{
			SourceDAO sourceDAO = context.getSourceDAO();
			return sourceDAO.getSourceByValue(source);
		}
	}

	protected ResponseEntry detectMPI(RequestEntry entry, Source source) throws MPIException
	{
		int error = ErrorCode.UNDEFINED;

		org.emau.icmvc.ganimed.epix.common.model.Person inputP = entry.getPerson();
		// Create a person entity object from JAXB Person
		Person p = modelMapper.mapPerson(inputP);

		// Create the contact entities from JAXB contact list
		List<Contact> contacts = modelMapper.mapContacts(inputP.getContacts(), p);
		p.setContacts(contacts);

		// Create an originDate if not existing; Will be needed for safe update
		if (p.getOriginDate() == null)
		{
			p.setOriginDate(new Timestamp(System.currentTimeMillis()));
		}

		p.setSource(source);

		// Create personPreprocessed from Person for matching
		PersonPreprocessed pp = createPreprocessedEntry(p);
		// Check if requested person is valid
		try
		{
			if (!deduplicationEngine.isValidate(pp))
			{
				return createQueryResponseEntry(p, ErrorCode.NO_MATCHABLE_ERROR);
			}
		} catch (DeduplicationException e)
		{
			logger.error("Error while performing validation: " + e.getMessage());
			return createQueryResponseEntry(p, ErrorCode.MATCH_ERROR);
		}
		// Map given identifiers
		List<Identifier> identifiers = null;
		try
		{
			if ((identifiers = modelMapper.mapIdentifiers(entry.getPerson().getIdentifiers())) != null)
			{
				List<Identifier> idents = addIdentifiers(identifiers, p);
				p.setLocalIdentifiers(idents);
			}
		} catch (MPIException e)
		{
			return createQueryResponseEntry(p, e.getErrorCode());
		}

		if (entry.getPerson().getMpiid() != null && entry.getPerson().getMpiid().getValue() != null
				&& !entry.getPerson().getMpiid().getValue().isEmpty())
		{
			IdentifierDAO identifierDAO = context.getIdentifierDAO();
			IdentifierDomainDAO identifierDomainDAO = context.getIdentifierDomainDAO();
			IdentifierDomain mpiDomain = identifierDomainDAO.findMPIDomain(context.getProject());
			Identifier mpiIdent = new Identifier();
			mpiIdent.setValue(entry.getPerson().getMpiid().getValue());
			Identifier ident = identifierDAO.getIdentifierByIdentifierAndDomain(mpiIdent, mpiDomain);
			if (ident == null)
			{
				return createQueryResponseEntry(p, ErrorCode.MPIID_NOT_EXISTS);
			}
			identifiers.add(ident);
		}

		// Check if identifier is already existing in database
		List<Person> existIdentPersons = null;
		try
		{
			existIdentPersons = personsExists(p, identifiers);
		} catch (MPIException e)
		{
			return createQueryResponseEntry(p, e.getErrorCode());
		}
		DeduplicationResult<PersonPreprocessed> dr = null;
		if (existIdentPersons != null && !existIdentPersons.isEmpty())
		{
			// Check if group of existingPersons is the same
			// If not throw error
			if (!checkSameGroup(existIdentPersons))
			{
				return createQueryResponseEntry(p, ErrorCode.DIFFERENT_PERSON_GROUP_FOR_SAME_LOCAL_IDENTIFIER);
			} else
			{
				PersonPreprocessed perfectMatchPP = null;
				PersonDAO personDAO = context.getPersonDAO();
				List<Person> groupList = personDAO.findPersonByPersonGroup(existIdentPersons.get(0).getPersonGroup());
				List<PersonPreprocessed> existIdentPersonsPP = createPreprocessedEnties(groupList);
				try
				{
					// TODO
					perfectMatchPP = deduplicationEngine.perfectMatch(pp, existIdentPersonsPP);
				} catch (DeduplicationException e)
				{
					throw new MPIException(ErrorCode.MATCH_ERROR, e);
				}
				// If perfectMatch is existing add new identifiers to
				// existing person and safe personHistory
				if (perfectMatchPP != null)
				{
					// PersonPreprocessedDAO personPreprocessedDAO =
					// context.getPersonPreprocessedDAO();
					// PersonPreprocessed findPP =
					// personPreprocessedDAO.findPersonPreprocessedByPersonID(perfectMatchPP.getPerson());
					//
					// PersonPreprocessed findPP =
					// personPreprocessedDAO.findPersonPreprocessedByPersonID(personDAO.getPersonByPersonPreprocessed(perfectMatchPP));
					// TODO by person id finden

					return savePerfectMatch(p, identifiers, perfectMatchPP, PersonHistory.EVENT.PERFECTMATCH, false);
				} else
				{
					// If there is no perfectMatch in the group check for
					// perfectMatch in the whole database.
					// If existing throw error.
					try
					{
						perfectMatchPP = deduplicationEngine.perfectMatch(pp);
					} catch (DeduplicationException e)
					{
						throw new MPIException(ErrorCode.MATCH_ERROR, e);
					}
					if (perfectMatchPP != null)
					{
						return createQueryResponseEntry(p, ErrorCode.DIFFERENT_PERSON_GROUP_FOR_SAME_LOCAL_IDENTIFIER);
					} else
					{
						// No perfectMatch found in database so match
						// entry to all persons of the personGroup
						List<PersonPreprocessed> ppGroupList = personPreprocessedCache.findPersonPreprocessedByPersonGroup(existIdentPersons.get(0).getPersonGroup().getId());

						try
						{
							dr = deduplicationEngine.matchSameLocalIdentifierCandidates(pp, ppGroupList);
						} catch (DeduplicationException e)
						{
							throw new MPIException(ErrorCode.MATCH_ERROR, e);
						}
						// If found uniqueMatch in personGroup add a new
						// personIdentitiy
						if (dr.hasUniqueMatch())
						{
							return saveUniqueMatch(p, identifiers, dr, false);
						} else if (dr.hasNonMatches())
						{
							DeduplicationResult<PersonPreprocessed> drAll = null;
							try
							{
								// TODO what
								// TODO sollte passen
								drAll = deduplicationEngine.match(pp);
							} catch (DeduplicationException e)
							{
								throw new MPIException(ErrorCode.MATCH_ERROR, e);
							}
							if (drAll.hasNonMatches())
							{
								return createQueryResponseEntry(p, ErrorCode.NO_MATCH_FOR_EXISTING_IDENTIFIER);
							} else
							{
								return createQueryResponseEntry(p,
										ErrorCode.DIFFERENT_PERSONS_FOR_SAME_LOCAL_IDENTIFIER);
							}
						} else if (dr.hasMultibleMatches())
						{
							DeduplicationResult<PersonPreprocessed> drAll;
							try
							{
								drAll = deduplicationEngine.match(pp);
							} catch (DeduplicationException e)
							{
								throw new MPIException(ErrorCode.MATCH_ERROR, e);
							}
							if (drAll.hasNonMatches() || drAll.hasUniqueMatch())
							{
								return createQueryResponseEntry(p, ErrorCode.MATCH_ERROR);
							} else
							{
								Long groupId = existIdentPersons.get(0).getPersonGroup().getId();
								for (MatchResult<PersonPreprocessed> mr : drAll.getMatchResults())
								{
									// TODO what?
									// TODO sollte passen
									Person mrPerson = personDAO.getPersonByPersonPreprocessed(mr.getComparativeValue());
									if (!groupId.equals(mrPerson.getPersonGroup().getId()))
									{
										return createQueryResponseEntry(p,
												ErrorCode.DIFFERENT_PERSONS_FOR_SAME_LOCAL_IDENTIFIER);
									}
								}
								return saveCriticalMatchWithExistingIdentifier(p, identifiers, dr, false);
							}
						}
					}
				}
			}
		} else
		{
			// In this case no existing identifier was found
			// First check for perfectMatch in the whole database
			PersonPreprocessed perfectMatchPP;
			try
			{
				perfectMatchPP = deduplicationEngine.perfectMatch(pp);
			} catch (DeduplicationException e)
			{
				throw new MPIException(ErrorCode.PERFECT_MATCH_ERROR, e);
			}
			if (perfectMatchPP != null)
			{
				// TODO WHAT?
				// TODO done
				// PersonPreprocessedDAO personPreprocessedDAO =
				// context.getPersonPreprocessedDAO();
				// PersonDAO personDAO = context.getPersonDAO();
				// PersonPreprocessed findPP =
				// personPreprocessedDAO.findPersonPreprocessedByPersonID(personDAO.getPersonByPersonPreprocessed(perfectMatchPP));
				return savePerfectMatch(p, identifiers, perfectMatchPP, PersonHistory.EVENT.PERFECTMATCH, false);
			} else
			{
				// No perfectMatch was found so perform a full match
				try
				{
					dr = deduplicationEngine.match(pp);
				} catch (DeduplicationException e)
				{
					throw new MPIException(ErrorCode.MATCH_ERROR, e);
				}
				// If no matches were found safe a new person
				if (dr.hasNonMatches())
				{
					// TODO what
					// TODO sollte passen
					return saveNewPerson(p, dr, false);
				} else if (dr.hasUniqueMatch())
				{
					// TODO what
					// TODO sollte passen
					return saveUniqueMatch(p, identifiers, dr, false);
				} else if (dr.hasMultibleMatches())
				{
					// TODO what
					// TODO sollte passen
					return saveCriticalMatch(p, dr, false);
				} else
				{
					return createQueryResponseEntry(p, ErrorCode.MATCH_ERROR);
				}
			}
		}

		//

		if (reportBuilder.isReportActivated() && dr.hasMultibleMatches())
		{
			for (MatchResult<PersonPreprocessed> mr : dr.getMatchResults())
			{
				reportBuilder.append(p, mr);
			}
		}

		try
		{
			context.getNotifier().notifyAll(
					new Notification("Create entry for person " + p + " with ErrorCode " + ErrorCode.getMessage(error),
							Notification.PRIORITY.INFO));
		} catch (NotifactionException e)
		{
			logger.error(e.getLocalizedMessage(), e);
		}

		return createQueryResponseEntry(p, error);
	}

	/**
	 * @param p
	 * @param dr
	 * @return
	 * @throws MPIException
	 */
	private ResponseEntry saveCriticalMatch(Person p, DeduplicationResult<PersonPreprocessed> dr,
			boolean forceReferenceUpdate) throws MPIException
	{
		if (addNewPerson(p, null, forceReferenceUpdate))
		{
			addNewPersonPreprocessed(dr.getMatchable(), p);
			for (MatchResult<PersonPreprocessed> mr : dr.getMatchResults())
			{
				updateLinkedPersons(mr, p);
			}
			PersonHistory personHistory = createHistoryEntry(p);
			addNewHistoryEntry(personHistory, PersonHistory.EVENT.NEW);
			return createQueryResponseEntry(p, ErrorCode.CRITICAL_MATCH_FOR_PERSON);
		} else
		{
			return createQueryResponseEntry(p, ErrorCode.ADD_PERSON_ERROR);
		}
	}

	/**
	 * @param p
	 * @param dr
	 * @return
	 * @throws MPIException
	 */
	private ResponseEntry saveNewPerson(Person p, DeduplicationResult<PersonPreprocessed> dr,
			boolean forceReferenceUpdate) throws MPIException
	{
		if (addNewPerson(p, null, forceReferenceUpdate))
		{
			addNewPersonPreprocessed(dr.getMatchable(), p);
			PersonHistory personHistory = createHistoryEntry(p);
			addNewHistoryEntry(personHistory, PersonHistory.EVENT.NEW);
			return createQueryResponseEntry(p, ErrorCode.OK);
		} else
		{
			return createQueryResponseEntry(p, ErrorCode.ADD_PERSON_ERROR);
		}
	}

	/**
	 * @param p
	 * @param identifiers
	 * @param dr
	 * @return
	 * @throws MPIException
	 */
	private ResponseEntry saveCriticalMatchWithExistingIdentifier(Person p, List<Identifier> identifiers,
			DeduplicationResult<PersonPreprocessed> dr, boolean forceReferenceUpdate) throws MPIException
	{
		// TODO
		// PersonDAO personDAO = context.getPersonDAO();
		// //Get Person from the database, because it is not available in the
		// persistence context
		// PersonPreprocessed pp =
		// dr.getMatchResults().get(0).getComparativeValue();
		// Person comparativePerson =
		// personDAO.getPersonByPersonPreprocessed(pp);

		// if (identifiers != null) {
		// List<Identifier> idents = p.getLocalIdentifiers();
		// p.getLocalIdentifiers().clear();
		// IdentifierDAO identifierDAO = context.getIdentifierDAO();
		// List<Identifier> pgidents =
		// identifierDAO.getIdentifierByPersonGroup(dr.getMatchResults().get(0)
		// .getComparativeValue().getPerson().getPersonGroup());
		// for (Identifier ident : idents) {
		// boolean identexists = false;
		// for (Identifier pgident : pgidents) {
		// if (ident.equals(pgident)) {
		// identexists = true;
		// p.getLocalIdentifiers().add(pgident);
		// }
		// }
		// if (identexists == false) {
		// p.getLocalIdentifiers().add(ident);
		// }
		// }
		// }
		PersonDAO personDAO = context.getPersonDAO();
		if (addNewPerson(p, personDAO.getPersonByPersonPreprocessed(dr.getMatchResults().get(0).getComparativeValue()).getPersonGroup(), forceReferenceUpdate))
		{
			addNewPersonPreprocessed(dr.getMatchable(), p);
			PersonHistory personHistory = createHistoryEntry(p);
			addNewHistoryEntry(personHistory, PersonHistory.EVENT.MATCH);
			return createQueryResponseEntry(p, ErrorCode.MULTIPLE_MATCH_FOR_SAME_LOCAL_IDENTIFIER_IN_SAME_GROUP);
		} else
		{
			return createQueryResponseEntry(p, ErrorCode.ADD_PERSON_ERROR);
		}
	}

	/**
	 * @param p
	 * @param identifiers
	 * @param dr
	 * @return
	 * @throws MPIException
	 */
	private ResponseEntry saveUniqueMatch(Person p, List<Identifier> identifiers,
			DeduplicationResult<PersonPreprocessed> dr, boolean forceReferenceUpdate) throws MPIException
	{
		// If the new identity has identifiers check if they are already
		// existing in database. If they are existing link them to the new
		// identity
		// else add them to the database.
		// TODO
		PersonDAO personDAO = context.getPersonDAO();
		// Get Person from the database, because it is not available in the
		// persistence context
		PersonPreprocessed pp = dr.getUniqueMatch();
		Person comparativePerson = personDAO.getPersonByPersonPreprocessed(pp);

		// if (identifiers != null) {
		// List<Identifier> idents = addIdentifiers(identifiers, p);
		// p.getLocalIdentifiers().clear();
		// IdentifierDAO identifierDAO = context.getIdentifierDAO();
		// // List<Identifier> pgidents =
		// //
		// identifierDAO.getIdentifierByPersonGroup(dr.getUniqueMatch().getPerson().getPersonGroup());
		// List<Identifier> pgidents =
		// identifierDAO.getIdentifierByPersonGroup(comparativePerson.getPersonGroup());
		//
		// for (Identifier ident : idents) {
		// boolean identexists = false;
		// for (Identifier pgident : pgidents) {
		// if (ident.equals(pgident)) {
		// identexists = true;
		// p.getLocalIdentifiers().add(pgident);
		// }
		// }
		// if (identexists == false) {
		// p.getLocalIdentifiers().add(ident);
		// }
		// }
		//
		// }
		if (addNewPerson(p, comparativePerson.getPersonGroup(), forceReferenceUpdate))
		{
			addNewPersonPreprocessed(dr.getMatchable(), p);
			PersonHistory personHistory = createHistoryEntry(p);
			addNewHistoryEntry(personHistory, PersonHistory.EVENT.MATCH);
			return createQueryResponseEntry(p, ErrorCode.IDENTICAL_MATCH_FOR_PERSON);
		} else
		{
			return createQueryResponseEntry(p, ErrorCode.ADD_PERSON_ERROR);
		}
	}

	/**
	 * @param p
	 * @param identifiers
	 * @param perfectMatchPP
	 * @return
	 * @throws MPIException
	 */
	private ResponseEntry savePerfectMatch(Person p, List<Identifier> identifiers, PersonPreprocessed perfectMatchPP,
			PersonHistory.EVENT event, boolean forceReferenceUpdate) throws MPIException
	{
		List<Identifier> idents = p.getLocalIdentifiers();
		PersonDAO personDAO = context.getPersonDAO();
		Person person = personDAO.getPersonByPersonPreprocessed(perfectMatchPP);
		List<Identifier> newIdents = new ArrayList<Identifier>();
		for (Identifier ident : idents)
		{
			boolean existing = false;
			for (Identifier oldIdent : person.getLocalIdentifiers())
			{
				if (ident.equals(oldIdent))
				{
					existing = true;
					break;
				}
			}
			if (!existing)
			{
				newIdents.add(ident);
			}
		}
		person.getLocalIdentifiers().addAll(newIdents);
		p.setPersonGroup(person.getPersonGroup());
		PersonHistory personHistory = createHistoryEntry(p);
		personHistory.setPerson(person);
		addNewHistoryEntry(personHistory, event);
		if (p.getOriginDate().after(person.getPersonGroup().getReferencePerson().getOriginDate()))
		{
			if (context.getSafeSource() == null || p.getSource().equals(context.getSafeSource())
					|| !person.getPersonGroup().getReferencePerson().getSource().equals(context.getSafeSource())
					|| forceReferenceUpdate)
			{
				updatePersonOnPerfectMatch(p, perfectMatchPP, person);
			}
		}
		Timestamp oldOriginDate = (Timestamp) person.getOriginDate().clone();
		Source oldSource = person.getSource();
		person.setSource(p.getSource());
		person.setOriginDate(p.getOriginDate());
		updateReferencePerson(person.getPersonGroup(), person, forceReferenceUpdate);
		person.setSource(oldSource);
		person.setOriginDate(oldOriginDate);
		p.setId(person.getId());
		return createQueryResponseEntry(person, ErrorCode.PERFECT_MATCH_FOR_PERSON);
	}

	/**
	 * Update person, personPreprocessed and their contacts to to keep
	 * attributes up to date
	 *
	 * @param requestPerson
	 *            person from the request
	 * @param perfectMatchPersonPreprocessed
	 *            found perfectMatch in the database (will also be updated)
	 * @param dbPerson
	 *            person to update
	 * @throws MPIException
	 */
	private void updatePersonOnPerfectMatch(Person requestPerson, PersonPreprocessed perfectMatchPersonPreprocessed,
			Person dbPerson) throws MPIException
	{
		PersonDAO personDAO = context.getPersonDAO();
		// PersonPreprocessedDAO personPreprocessedDAO =
		// context.getPersonPreprocessedDAO();
		personDAO.updatePerson(dbPerson, requestPerson);
		if (dbPerson instanceof HealthcareProvider && requestPerson instanceof HealthcareProvider)
		{
			HealthcareProvider findHpc = (HealthcareProvider) dbPerson;
			HealthcareProvider hpc = (HealthcareProvider) requestPerson;
			personDAO.updateHealthcareProvider(findHpc, hpc);
		} else if (dbPerson instanceof Patient && requestPerson instanceof Patient)
		{
			Patient findPatient = (Patient) dbPerson;
			Patient patient = (Patient) requestPerson;
			personDAO.updatePatient(findPatient, patient);
		}
		// Synchronize contacts
		for (Contact newContact : requestPerson.getContacts())
		{
			boolean exising = false;
			for (Contact existingContact : dbPerson.getContacts())
			{
				if (newContact.equals(existingContact))
				{
					exising = true;
				}
			}
			if (!exising)
			{
				dbPerson.getContacts().add(newContact);
			}
		}
		personDAO.addPerson(dbPerson, null);
		requestPerson.setContacts(dbPerson.getContacts());

		// ContactDAO contactDAO = context.getContactDAO();
		// contactDAO.updateContacts(dbPerson, requestPerson);
		// Update PersonPreprozessed
		PersonPreprocessed pp = null;
		try
		{
			if (requestPerson instanceof Patient)
			{
				pp = personPreprocessedCache.preprocess(new PatientPreprocessed((Patient) dbPerson));
			} else if (requestPerson instanceof HealthcareProvider)
			{
				pp = personPreprocessedCache.preprocess(new HealthcareProviderPreprocessed(
						(HealthcareProvider) dbPerson));
			} else
			{
				pp = personPreprocessedCache.preprocess(new PersonPreprocessed(dbPerson));
			}
		} catch (DeduplicationException e)
		{
			throw new MPIException(ErrorCode.DEDUPLICATION_ERROR, e);
		}
		PersonPreprocessedDAO personPreprocessedDAO = context.getPersonPreprocessedDAO();
		personPreprocessedDAO.updatePersonPreprocessed(pp);
		perfectMatchPersonPreprocessed.updatePersonPreprocessed(pp);

		// TODO Was soll damit passieren?
		// if (perfectMatchPersonPreprocessed instanceof
		// HealthcareProviderPreprocessed
		// && pp instanceof HealthcareProviderPreprocessed) {
		// HealthcareProviderPreprocessed findHcpp =
		// (HealthcareProviderPreprocessed) perfectMatchPersonPreprocessed;
		// HealthcareProviderPreprocessed hcpp =
		// (HealthcareProviderPreprocessed) pp;
		// personPreprocessedDAO.updateHealthcareProviderPreprocessed(findHcpp,
		// hcpp);
		// } else if (perfectMatchPersonPreprocessed instanceof
		// PatientPreprocessed && pp instanceof PatientPreprocessed) {
		// PatientPreprocessed findPatientP = (PatientPreprocessed)
		// perfectMatchPersonPreprocessed;
		// PatientPreprocessed patientP = (PatientPreprocessed) pp;
		// personPreprocessedDAO.updatePatientPreprocessed(findPatientP,
		// patientP);
		// }
	}

	/**
	 * Check a list of persons if all having the same group.
	 *
	 * @param persons
	 * @return <code>true</code> if all having the same group and
	 *         <code>false</code> if not
	 */
	private boolean checkSameGroup(List<Person> persons)
	{
		return checkSameGroup(persons.get(0), persons);
	}

	/**
	 * Check if a person has the same group like a list of persons.
	 *
	 * @param person
	 * @param personList
	 * @return <code>true</code> if all having the same group and
	 *         <code>false</code> if not
	 */
	private boolean checkSameGroup(Person person, List<Person> personList)
	{
		for (Person existingPerson : personList)
		{
			if (!existingPerson.getPersonGroup().getId().equals(person.getPersonGroup().getId()))
			{
				return false;
			}
		}
		return true;
	}

	protected ResponseEntry detectMPINoPersist(RequestEntry entry) throws MPIException
	{
		int error = ErrorCode.UNDEFINED;
		org.emau.icmvc.ganimed.epix.common.model.Person inputP = entry.getPerson();
		// Create a person entity object from JAXB Person
		Person p = modelMapper.mapPerson(inputP);

		// Create the contact entities from JAXB contact list
		List<Contact> contacts = modelMapper.mapContacts(inputP.getContacts(), p);
		p.setContacts(contacts);

		// Create an originDate if not existing; Will be needed for safe update
		if (p.getOriginDate() == null)
		{
			p.setOriginDate(new Timestamp(System.currentTimeMillis()));
		}

		// Create personPreprocessed from Person for matching
		PersonPreprocessed pp = createPreprocessedEntry(p);
		// Check if requested person is valid
		try
		{
			if (!deduplicationEngine.isValidate(pp))
			{
				return createQueryResponseEntry(p, ErrorCode.NO_MATCHABLE_ERROR);
			}
		} catch (DeduplicationException e)
		{
			logger.error("Error while performing validation: " + e.getMessage());
			return createQueryResponseEntry(p, ErrorCode.MATCH_ERROR);
		}
		// Map given identifiers
		List<Identifier> identifiers = null;
		try
		{
			if ((identifiers = modelMapper.mapIdentifiers(entry.getPerson().getIdentifiers())) != null)
			{
				List<Identifier> idents = addIdentifiers(identifiers, p);
				p.getLocalIdentifiers().addAll(idents);
			}
		} catch (MPIException e)
		{
			return createQueryResponseEntry(p, e.getErrorCode());
		}

		if (entry.getPerson().getMpiid() != null)
		{
			IdentifierDAO identifierDAO = context.getIdentifierDAO();
			IdentifierDomainDAO identifierDomainDAO = context.getIdentifierDomainDAO();
			IdentifierDomain mpiDomain = identifierDomainDAO.findMPIDomain(context.getProject());
			Identifier mpiIdent = new Identifier();
			mpiIdent.setValue(entry.getPerson().getMpiid().getValue());
			Identifier ident = identifierDAO.getIdentifierByIdentifierAndDomain(mpiIdent, mpiDomain);
			if (ident == null)
			{
				return createQueryResponseEntry(p, ErrorCode.MPIID_NOT_EXISTS);
			}
			identifiers.add(ident);
		}

		// Check if identifier is already existing in database
		List<Person> existIdentPersons = null;
		try
		{
			existIdentPersons = personsExists(p, identifiers);
		} catch (MPIException e)
		{
			return createQueryResponseEntry(p, e.getErrorCode());
		}
		DeduplicationResult<PersonPreprocessed> dr = null;
		if (existIdentPersons != null && !existIdentPersons.isEmpty())
		{
			// Check if group of existingPersons is the same
			// If not throw error
			if (!checkSameGroup(existIdentPersons))
			{
				return createQueryResponseEntry(p, ErrorCode.DIFFERENT_PERSON_GROUP_FOR_SAME_LOCAL_IDENTIFIER);
			} else
			{
				PersonPreprocessed perfectMatchPP = null;
				PersonDAO personDAO = context.getPersonDAO();
				List<Person> groupList = personDAO.findPersonByPersonGroup(existIdentPersons.get(0).getPersonGroup());
				List<PersonPreprocessed> existIdentPersonsPP = createPreprocessedEnties(groupList);
				try
				{
					perfectMatchPP = deduplicationEngine.perfectMatch(pp, existIdentPersonsPP);
				} catch (DeduplicationException e)
				{
					throw new MPIException(ErrorCode.MATCH_ERROR, e);
				}

				// If perfectMatch is existing add new identifiers to
				// existing person and safe personHistory
				if (perfectMatchPP != null)
				{

					Person dbPerson = personDAO.getPersonByPersonPreprocessed(perfectMatchPP);
					p.setPersonGroup(dbPerson.getPersonGroup());
					p.setId(dbPerson.getId());
					return createQueryResponseEntry(p, ErrorCode.PERFECT_MATCH_FOR_PERSON);
				} else
				{
					// If there is no perfectMatch in the group check for
					// perfectMatch in the whole database.
					// If existing throw error.
					try
					{
						perfectMatchPP = deduplicationEngine.perfectMatch(pp);
					} catch (DeduplicationException e)
					{
						throw new MPIException(ErrorCode.MATCH_ERROR, e);
					}
					if (perfectMatchPP != null)
					{
						return createQueryResponseEntry(p, ErrorCode.DIFFERENT_PERSON_GROUP_FOR_SAME_LOCAL_IDENTIFIER);
					} else
					{
						// No perfectMatch found in database so match
						// entry to all persons of the personGroup

						List<PersonPreprocessed> ppGroupList = personPreprocessedCache.findPersonPreprocessedByPersonGroup(existIdentPersons.get(0).getPersonGroup().getId());
						try
						{
							dr = deduplicationEngine.matchSameLocalIdentifierCandidates(pp, ppGroupList);
						} catch (DeduplicationException e)
						{
							throw new MPIException(ErrorCode.MATCH_ERROR, e);
						}
						// If found uniqueMatch in personGroup add a new
						// personIdentitiy
						if (dr.hasUniqueMatch())
						{
							p.setPersonGroup(personDAO.getPersonByPersonPreprocessed(dr.getUniqueMatch()).getPersonGroup());
							return createQueryResponseEntry(p, ErrorCode.IDENTICAL_MATCH_FOR_PERSON);
						} else if (dr.hasNonMatches())
						{
							DeduplicationResult<PersonPreprocessed> drAll = null;
							try
							{
								drAll = deduplicationEngine.match(pp);
							} catch (DeduplicationException e)
							{
								throw new MPIException(ErrorCode.MATCH_ERROR, e);
							}
							if (drAll.hasNonMatches())
							{
								return createQueryResponseEntry(p, ErrorCode.NO_MATCH_FOR_EXISTING_IDENTIFIER);
							} else
							{
								return createQueryResponseEntry(p,
										ErrorCode.DIFFERENT_PERSONS_FOR_SAME_LOCAL_IDENTIFIER);
							}
						} else if (dr.hasMultibleMatches())
						{
							DeduplicationResult<PersonPreprocessed> drAll;
							try
							{
								drAll = deduplicationEngine.match(pp);
							} catch (DeduplicationException e)
							{
								throw new MPIException(ErrorCode.MATCH_ERROR, e);
							}
							if (drAll.hasNonMatches() || drAll.hasUniqueMatch())
							{
								return createQueryResponseEntry(p, ErrorCode.MATCH_ERROR);
							} else
							{
								Long groupId = existIdentPersons.get(0).getPersonGroup().getId();
								for (MatchResult<PersonPreprocessed> mr : drAll.getMatchResults())
								{
									// TODO what?
									// TODO sollte passen
									// if
									// (!groupId.equals(mr.getComparativeValue().getPerson().getPersonGroup().getId()))
									// {
									// return createQueryResponseEntry(p,
									// ErrorCode.DIFFERENT_PERSONS_FOR_SAME_LOCAL_IDENTIFIER);
									// }

									if (!groupId.equals(personDAO.getPersonByPersonPreprocessed((mr.getComparativeValue())).getPersonGroup().getId()))
									{
										return createQueryResponseEntry(p,
												ErrorCode.DIFFERENT_PERSONS_FOR_SAME_LOCAL_IDENTIFIER);
									}

								}
								return createQueryResponseEntry(p, ErrorCode.CRITICAL_MATCH_FOR_PERSON);
							}
						}
					}
				}
			}
		} else
		{
			// In this case no existing identifier was found
			// First check for perfectMatch in the whole database
			PersonPreprocessed perfectMatchPP;
			try
			{
				perfectMatchPP = deduplicationEngine.perfectMatch(pp);
			} catch (DeduplicationException e)
			{
				throw new MPIException(ErrorCode.PERFECT_MATCH_ERROR, e);
			}
			if (perfectMatchPP != null)
			{

				// TOTO what?
				// TODO done
				PersonDAO personDAO = context.getPersonDAO();
				Person person = personDAO.getPersonByPersonPreprocessed(perfectMatchPP);
				p.setPersonGroup(person.getPersonGroup());

				// p.setPersonGroup(perfectMatchPP.getPerson().getPersonGroup());
				return createQueryResponseEntry(p, ErrorCode.PERFECT_MATCH_FOR_PERSON);
			} else
			{
				// No perfectMatch was found so perform a full match
				try
				{
					dr = deduplicationEngine.match(pp);
				} catch (DeduplicationException e)
				{
					throw new MPIException(ErrorCode.MATCH_ERROR, e);
				}
				// If no matches were found safe a new person
				if (dr.hasNonMatches())
				{
					return createQueryResponseEntry(p, ErrorCode.OK);
				} else if (dr.hasUniqueMatch())
				{
					PersonDAO personDAO = context.getPersonDAO();
					Person outPerson = personDAO.getPersonByPersonPreprocessed(dr.getUniqueMatch());
					p.setPersonGroup(outPerson.getPersonGroup());
					return createQueryResponseEntry(p, ErrorCode.IDENTICAL_MATCH_FOR_PERSON);
				} else if (dr.hasMultibleMatches())
				{
					return createQueryResponseEntry(p, ErrorCode.CRITICAL_MATCH_FOR_PERSON);
				} else
				{
					return createQueryResponseEntry(p, ErrorCode.MATCH_ERROR);
				}
			}
		}
		return createQueryResponseEntry(p, error);

	}

	/**
	 * Creates a new PersonGroup with given Person as ReferencePerson
	 *
	 * @param referencePerson
	 * @return The created PersonGroup
	 * @throws MPIException
	 */
	private PersonGroup addPersonGroup(PersonGroup personGroup) throws MPIException
	{
		PersonGroupDAO personGroupDAO = context.getPersonGroupDAO();
		return personGroupDAO.addPersonGroup(personGroup);
	}

	private PersonGroupHistory addPersonGroupHistory(PersonGroup personGroup) throws MPIException
	{
		PersonGroupHistoryDAO personGroupHistoryDAO = context.getPersonGroupHistoryDAO();
		PersonGroupHistory personGroupHistory = new PersonGroupHistory(personGroup);
		return personGroupHistoryDAO.addPersonGroupHistory(personGroupHistory);
	}

	/**
	 * Updates the ReferencePerson of the given PersonGroup if the person comes
	 * from the SafeSystem and the OriginDate of the given person lies before
	 * the OriginDate of the actual ReverencePerson
	 *
	 * @param personGroup
	 * @param person
	 * @throws MPIException
	 */
	private void updateReferencePerson(PersonGroup personGroup, Person person, boolean forceReferenceUpdate)
			throws MPIException
	{
		if (personGroup == null || person == null)
		{
			throw new MPIException(ErrorCode.CHECK_REFERENCE_PERSON_ERROR);
		}
		if (person.getOriginDate().after(personGroup.getReferencePerson().getOriginDate()))
		{
			if (context.getSafeSource() == null || person.getSource().equals(context.getSafeSource())
					|| !personGroup.getReferencePerson().getSource().equals(context.getSafeSource())
					|| forceReferenceUpdate)
			{
				personGroup.setReferencePerson(person);
				addPersonGroupHistory(personGroup);
			}
		}
	}

	/**
	 * Adds a new Person with the given PersonGroup </br> <b>If</b> personGroup
	 * is null </br> <b>Then</b> create a new one </br> <b> else</b> check if
	 * new person should be </br> the reference person
	 *
	 * @param person
	 * @param personGroup
	 * @return
	 * @throws MPIException
	 */
	private boolean addNewPerson(Person p, PersonGroup personGroup, boolean forceReferenceUpdate) throws MPIException
	{
		PersonDAO personDAO = context.getPersonDAO();
		IdentifierDAO identifierDAO = context.getIdentifierDAO();

		if (personGroup == null)
		{
			personGroup = new PersonGroup();
			p.setPersonGroup(personGroup);
			addPersonGroup(personGroup);
			personDAO.addPerson(p, p.getClass());
			personGroup.setReferencePerson(p);
			Identifier mpiIdent = createMPIId(p, context.getProject());
			if (mpiIdent == null)
			{
				return false;
			}
			identifierDAO.addIdentifier(mpiIdent);
			personGroup.setFirstMpi(mpiIdent);
			addPersonGroupHistory(personGroup);
		} else
		{
			p.setPersonGroup(personGroup);
			personDAO.addPerson(p, p.getClass());
			updateReferencePerson(personGroup, p, forceReferenceUpdate);
		}
		return true;
	}

	private void addNewHistoryEntry(PersonHistory personHistory, EVENT event) throws MPIException
	{
		PersonHistoryDAO personHistoryDAO = context.getPersonHistoryDAO();
		personHistory.setOriginalEvent(event.name());
		personHistoryDAO.addPersonHistory(personHistory, PersonHistory.class);
	}

	private PersonHistory createHistoryEntry(Person p)
	{
		PersonHistory personHistory = null;

		if (p instanceof Patient)
		{
			personHistory = new PatientHistory((Patient) p);
		} else if (p instanceof HealthcareProvider)
		{
			personHistory = new HealthcareProviderHistory((HealthcareProvider) p);
		} else
		{
			personHistory = new PersonHistory(p);
		}

		return personHistory;
	}

	private List<Identifier> addIdentifiers(List<Identifier> identifiers, Person person) throws MPIException
	{
		List<Identifier> list = new ArrayList<Identifier>();
		for (Identifier ident : identifiers)
		{
			ident.getPerson().add(person);
			ident.setIdentifierType(IdentifierTypeEnum.STANDARD);
			ident.setEntryDate(new Timestamp(System.currentTimeMillis()));
			IdentifierDomain domain = getIdentifierDomain(ident.getDomain());
			ident.setDomain(domain);
			// identifierDAO.addIdentifier(ident, domain);
			list.add(ident);
			// person.getLocalIdentifiers().add(ident);
			// personDAO.updatePerson(person, person.getClass());
		}
		return list;
	}

	private Identifier createMPIId(Person person, Project project) throws MPIException
	{
		MPIGenerator gen = getGenerator();
		Identifier identifier = gen.generate();
		if (identifier == null || identifier.getValue().isEmpty())
		{
			return null;
		}
		// person.setMpiid(mpiid);
		return identifier;
	}

	private org.emau.icmvc.ganimed.epix.common.model.Person mapOutputPerson(Person p) throws MPIException
	{
		org.emau.icmvc.ganimed.epix.common.model.Person outputPerson = modelMapper.mapOutputPerson(p);

		if (p.getPersonGroup() != null && p.getPersonGroup().getFirstMpi() != null)
		{
			// MATCH or NO MATCH
			org.emau.icmvc.ganimed.epix.common.model.MPIID mpiid = new org.emau.icmvc.ganimed.epix.common.model.MPIID();
			mpiid.setValue(p.getPersonGroup().getFirstMpi().getValue());
			outputPerson.setMpiid(mpiid);
		} else
		{
			// CRITICAL MATCH
			outputPerson.setMpiid(new MPIID("-1", null));
		}

		for (Identifier ident : p.getLocalIdentifiers())
		{
			outputPerson.getIdentifiers().add(modelMapper.mapOutputIdentifier(ident));
		}
		return outputPerson;
	}
	
	private org.emau.icmvc.ganimed.epix.common.model.Person mapOutputReferencePerson(Person p) throws MPIException
	{
		org.emau.icmvc.ganimed.epix.common.model.Person outputPerson = modelMapper.mapOutputPerson(p.getPersonGroup().getReferencePerson());

		if (p.getPersonGroup() != null && p.getPersonGroup().getFirstMpi() != null)
		{
			// MATCH or NO MATCH
			org.emau.icmvc.ganimed.epix.common.model.MPIID mpiid = new org.emau.icmvc.ganimed.epix.common.model.MPIID();
			mpiid.setValue(p.getPersonGroup().getFirstMpi().getValue());
			outputPerson.setMpiid(mpiid);
		} else
		{
			// CRITICAL MATCH
			outputPerson.setMpiid(new MPIID("-1", null));
		}

		for (Identifier ident : p.getLocalIdentifiers())
		{
			outputPerson.getIdentifiers().add(modelMapper.mapOutputIdentifier(ident));
		}
		return outputPerson;
	}

	private QueryResponseEntry createQueryResponseEntry(Person p, int code) throws MPIException
	{
		QueryResponseEntry re = new QueryResponseEntry();
		org.emau.icmvc.ganimed.epix.common.model.Person outputPerson = mapOutputPerson(p);
		PersonDAO personDAO = context.getPersonDAO();
		List<Person> personIdentities = personDAO.findPersonByPersonGroup(p.getPersonGroup());
		for (Person personIdentity : personIdentities)
		{
			re.getPersonIdentities().add(mapOutputPerson(personIdentity));
		}
		if (p.getId() != null) {
			PersonLinkDAO personLinkDAO = context.getPersonLinkDAO();
			for (PersonLink link : personLinkDAO.getPersonLinkForPersonGroup(p)) {
				org.emau.icmvc.ganimed.epix.common.model.Person linkPerson;
				if (p.getId().equals(link.getSrcPerson().getId())) {
					linkPerson = mapOutputPerson(link.getDestPerson());
				} else {
					linkPerson = mapOutputPerson(link.getSrcPerson());
				}
				re.getLinkedPersons().add(new LinkedPerson(linkPerson, link.getThreshold()));
			}
		}
		re.setPerson(outputPerson);
		List<Identifier> identifiers = null;
		if (p.getPersonGroup() != null)
		{
			re.setFirstMpi(modelMapper.mapOutputIdentifier(p.getPersonGroup().getFirstMpi()));
			IdentifierDAO identifierDAO = context.getIdentifierDAO();
			identifiers = identifierDAO.getIdentifierByPersonGroup(p.getPersonGroup());
		}
		List<org.emau.icmvc.ganimed.epix.common.model.Identifier> outIdentifiers = modelMapper
				.mapOutputIdentifiers(identifiers);
		re.setLocalIdentifiers(outIdentifiers);

		re.setErrorCode(new ErrorCode(code));
		return re;
	}

	/**
	 * Checks if persons existing for the given localIdentifiers and replace
	 * existing identifiers from the person with the persistent identifiers from
	 * the database
	 *
	 * @param person
	 * @param ident
	 * @return List of persons which are mapped to the existing identifiers
	 * @throws MPIException
	 */
	private List<Person> personsExists(Person person, List<Identifier> identifiers) throws MPIException
	{
		if (identifiers == null)
		{
			return null;
		} else
		{
			IdentifierDAO identifierDAO = context.getIdentifierDAO();
			PersonDAO personDAO = context.getPersonDAO();
			List<Person> personList = new ArrayList<Person>();
			person.getLocalIdentifiers().clear();
			for (Identifier currentIdent : identifiers)
			{
				Identifier match = identifierDAO.getIdentifierByIdentifierAndDomain(currentIdent,
						currentIdent.getDomain());
				if (match != null)
				{
					if (logger.isDebugEnabled())
					{
						logger.debug("Found local identifier for domain " + match.getDomain().getName() + ": " + match);
					}
					person.getLocalIdentifiers().add(match);
					List<Person> pList = personDAO.getPersonsByIdentifierAndDomain(match, match.getDomain());
					// Person already exists
					if (pList == null || pList.isEmpty())
					{
						throw new MPIException(ErrorCode.IDENTIFIER_ALREADY_EXISTS);
					}
					personList.addAll(pList);
				} else
				{
					person.getLocalIdentifiers().add(currentIdent);
				}
			}
			return personList;
		}
	}

	private void updateLinkedPersons(MatchResult<PersonPreprocessed> matchResult, Person person) throws MPIException
	{
		PersonDAO personDAO = context.getPersonDAO();
		// Get Person from the database, because it is not available in the
		// persistence context
		PersonPreprocessed pp = matchResult.getComparativeValue();
		Person comparativePerson = personDAO.getPersonByPersonPreprocessed(pp);
		// Person comparativePerson = matchResult.getComparativeValue()
		// .getPerson();

		personDAO.addLinkedPerson(comparativePerson, person, matchResult.getRatio(), matchResult.getMatchStrategy());
		personDAO.addLinkedPerson(person, comparativePerson, matchResult.getRatio(), matchResult.getMatchStrategy());

	}

	private IdentifierDomain getIdentifierDomain(IdentifierDomain requestDomain) throws MPIException
	{
		if (requestDomain == null)
		{
			throw new MPIException(ErrorCode.IDENTIFIERDOMAIN_NULL_ERROR);
		}

		IdentifierDomainDAO domainDAO = context.getIdentifierDomainDAO();
		IdentifierDomain domain = domainDAO.getIdentifierDomainByOID(requestDomain.getOid());

		if (domain == null)
		{
			logger.error("No valid domain registered for given domain " + requestDomain);
			throw new MPIException(ErrorCode.IDENTIFIERDOMAIN_NOT_EXISTS);
		}
		return domain;
	}

	private ResponseEntry performMPIrequestUpdate(RequestEntry entry, Source source, boolean forceReferenceUpdate)
			throws MPIException
	{
		int error = ErrorCode.UNDEFINED;
		org.emau.icmvc.ganimed.epix.common.model.Person inputP = entry.getPerson();

		// Create a person entity object from JAXB Person
		Person p = modelMapper.mapPerson(inputP);

		// Create the contact entities from JAXB contact list

		p.setContacts(modelMapper.mapContacts(inputP.getContacts(), p));

		List<Identifier> identifiers = null;
		try
		{
			identifiers = modelMapper.mapIdentifiers(entry.getPerson().getIdentifiers());
		} catch (MPIException e)
		{
			return createQueryResponseEntry(p, e.getErrorCode());
		}
		List<Identifier> idents = addIdentifiers(identifiers, p);
		p.getLocalIdentifiers().addAll(idents);

		p.setSource(source);

		if ((entry.getPerson().getMpiid() == null) || (entry.getPerson().getMpiid().getValue() == null)
				|| entry.getPerson().getMpiid().getValue().isEmpty())
		{
			error = ErrorCode.NO_MPIID_FOR_UPDATE;
			return createQueryResponseEntry(p, error);
		}
		PersonPreprocessed pp = createPreprocessedEntry(p);
		try
		{
			if (!deduplicationEngine.isValidate(pp))
			{
				return createQueryResponseEntry(p, ErrorCode.NO_MATCHABLE_ERROR);
			}
		} catch (DeduplicationException e)
		{
			logger.error("Error while performing validation: " + e.getMessage());
			return createQueryResponseEntry(p, ErrorCode.MATCH_ERROR);
		}

		PersonGroup findGroup = findPersonGroupByMPIIdentifier(entry.getPerson().getMpiid().getValue());
		Person findPerson = null;
		if (findGroup == null)
		{
			Person tempPerson = findPersonByMPIIdentifier(entry.getPerson().getMpiid().getValue());
			findGroup = tempPerson.getPersonGroup();
		}
		if (findGroup == null)
		{
			return createQueryResponseEntry(p, ErrorCode.NO_PERSON_FOUND);
		} else if (findGroup.isLocked())
		{
			Person person = findPersonByMPIIdentifier(entry.getPerson().getMpiid().getValue());
			findPerson = person.getPersonGroup().getReferencePerson();
		} else
		{
			findPerson = findGroup.getReferencePerson();
		}

		List<Person> existIdentPersons = null;
		try
		{
			existIdentPersons = personsExists(p, identifiers);
		} catch (MPIException e)
		{
			logger.error("Error during matchSameLocalIdentifier: " + e.getMessage());
			return createQueryResponseEntry(p, ErrorCode.MATCH_SAME_LOCAL_IDENTIFIER_ERROR);
		}

		// Test if changed person matches or has a perfectMatch
		PersonPreprocessed perfectMatch = null;
		DeduplicationResult<PersonPreprocessed> dr = null;
		try
		{
			perfectMatch = deduplicationEngine.perfectMatch(pp);
		} catch (DeduplicationException e)
		{
			logger.error("Error during perfectMatch: " + e.getMessage());
			return createQueryResponseEntry(p, ErrorCode.PERFECT_MATCH_ERROR);
		}
		try
		{
			// if (matchWhileUpdate(findPerson, entry.getPerson(),
			// matchingConfiguration.getRequiredConfig()))
			dr = deduplicationEngine.match(pp);
			// else
			// dr = deduplicationEngine.preprocessWithoutMatch(pp);
		} catch (DeduplicationException e)
		{
			logger.error("Error while performing matching algorithm: " + e.getMessage());
			return createQueryResponseEntry(p, ErrorCode.MATCH_ERROR);
		}

		// Check if matches have the same group as the findPerson
		boolean sameGroup = true;
		if (existIdentPersons != null && !existIdentPersons.isEmpty())
		{
			for (Person existIdentPerson : existIdentPersons)
			{
				if (!existIdentPerson.getPersonGroup().getId().equals(findPerson.getPersonGroup().getId()))
				{
					sameGroup = false;
				}
			}
		}
		// if (dr.hasMultibleMatches()) {
		// for (MatchResult<PersonPreprocessed> mr : dr.getMatchResults()) {
		// if (!mr.getComparativeValue().getPerson().getPersonGroup()
		// .getId().equals(findPerson.getPersonGroup().getId())) {
		//
		//
		// sameGroup = false;
		// }
		// }
		// } else
		if (dr.hasUniqueMatch())
		{
			// TODO what?
			// TODO sollte passen
			// if
			// (!dr.getUniqueMatch().getPerson().getPersonGroup().getId().equals(findPerson.getPersonGroup().getId()))
			// {
			// sameGroup = false;
			// }
			PersonDAO personDAO = context.getPersonDAO();
			if (!personDAO.getPersonByPersonPreprocessed(dr.getUniqueMatch()).getPersonGroup().getId().equals(findPerson.getPersonGroup().getId()))
			{
				sameGroup = false;
			}

		}
		if (perfectMatch != null)
		{
			PersonDAO personDAO = context.getPersonDAO();
			if (!personDAO.getPersonByPersonPreprocessed(perfectMatch).getPersonGroup().getId().equals(findPerson.getPersonGroup().getId()))
			{
				sameGroup = false;
			}
		}
		if (!sameGroup)
		{
			return createQueryResponseEntry(p, ErrorCode.DIFFERENT_PERSON_GROUP_FOR_UPDATE);
		}

		// Person findPerson = findPersonByIdentifierAndDomain(identifiers,
		// domain);
		// PersonPreprocessed findPersonPreprocessed =
		// findPreprocessedPersonByPersonID(findPerson); Not needed? --> No
		// Update on it.

		// if (existIdentPersons != null && !existIdentPersons.isEmpty()) {
		// p.getLocalIdentifiers().clear();
		// if ((identifiers =
		// modelMapper.mapIdentifiers(entry.getPerson().getIdentifiers())) !=
		// null) {
		// Person person =
		// existIdentPersons.get(0).getPersonGroup().getReferencePerson();
		// List<Identifier> newIdents = addIdentifiers(identifiers, person);
		// IdentifierDAO identifierDAO = context.getIdentifierDAO();
		// List<Identifier> pgidents =
		// identifierDAO.getIdentifierByPersonGroup(person.getPersonGroup());
		//
		// for (Identifier ident : newIdents) {
		// boolean identexists = false;
		// for (Identifier pgident : pgidents) {
		// if (ident.equals(pgident)) {
		// identexists = true;
		// p.getLocalIdentifiers().add(pgident);
		// }
		// }
		// if (identexists == false) {
		// p.getLocalIdentifiers().add(ident);
		// }
		// }
		// }
		// }

		if (p.getOriginDate() == null)
		{
			p.setOriginDate(new Timestamp(System.currentTimeMillis()));
		} else if (p.getOriginDate().compareTo(findPerson.getOriginDate()) <= 0)
		{
			error = ErrorCode.OLD_UPDATE_ERROR;
			return createQueryResponseEntry(p, error);
		}
		if (perfectMatch != null)
		{
			savePerfectMatch(p, identifiers, perfectMatch, PersonHistory.EVENT.UPDATE, forceReferenceUpdate);
			p.setPersonGroup(findPerson.getPersonGroup());
			error = ErrorCode.PERFECT_MATCH_FOR_PERSON;
		} else if (dr.hasMultibleMatches())
		{
			if (addNewPerson(p, findPerson.getPersonGroup(), forceReferenceUpdate))
			{
				addNewPersonPreprocessed(dr.getMatchable(), p);
				PersonHistory personHistory = createHistoryEntry(p);
				addNewHistoryEntry(personHistory, PersonHistory.EVENT.UPDATE);
			}
			error = ErrorCode.OK;
			for (MatchResult<PersonPreprocessed> mr : dr.getMatchResults())
			{
				// TODO what?
				// TODO sollte passen
				// if
				// (!mr.getComparativeValue().getPerson().getPersonGroup().getId()
				// .equals(findPerson.getPersonGroup().getId())) {
				// updateLinkedPersons(mr, p);
				// error = ErrorCode.CRITICAL_MATCH_FOR_PERSON;
				// }
				if (!mr.getComparativeValue().getPersonGroupID().equals(findPerson.getPersonGroup().getId()))
				{
					updateLinkedPersons(mr, p);
					error = ErrorCode.CRITICAL_MATCH_FOR_PERSON;
				}
			}
		} else if (addNewPerson(p, findPerson.getPersonGroup(), forceReferenceUpdate))
		{
			addNewPersonPreprocessed(dr.getMatchable(), p);
			PersonHistory personHistory = createHistoryEntry(p);
			addNewHistoryEntry(personHistory, PersonHistory.EVENT.UPDATE);
			error = ErrorCode.OK;
		} else
		{
			error = ErrorCode.ADD_PERSON_ERROR;
		}

		try
		{
			context.getNotifier().notifyAll(
					new Notification("Create entry for person " + p + " with ErrorCode " + ErrorCode.getMessage(error),
							Notification.PRIORITY.INFO));
		} catch (NotifactionException e)
		{
			logger.error(e.getLocalizedMessage(), e);
		}

		return createQueryResponseEntry(p, error);

	}

	/**
	 * Iterate through all required fields of two person objects. If a required
	 * field differs returns true. Any error returns true!
	 *
	 * @param findPerson
	 *            : Current person object
	 * @param person
	 *            : Updated person object
	 * @param requiredConfig
	 * @return true if a required field differs in current and updated person
	 */
	private boolean matchWhileUpdate(Person currentPerson, org.emau.icmvc.ganimed.epix.common.model.Person updatedPerson,
			RequiredConfig requiredConfig)
	{

		for (RequiredField requiredField : requiredConfig.getRequiredTypes().get(0).getRequiredFields())
		{
			Object currentPersonObject = null;
			Object updatedPersonObject = null;
			try
			{
				currentPersonObject = ReflectionUtil.getProperty(currentPerson, requiredField.getFieldName());
				updatedPersonObject = ReflectionUtil.getProperty(updatedPerson, requiredField.getFieldName());
			} catch (Exception e)
			{
				logger.error("Error while trying to retrieve requiredField. Matching is required!");
				return true;
			}
			if (currentPersonObject != null && updatedPersonObject != null && !currentPersonObject.toString().equals(updatedPersonObject.toString()))
			{
				return true;
			}
		}
		return false;
	}

	private List<PersonPreprocessed> createPreprocessedEnties(List<Person> pl)
	{
		List<PersonPreprocessed> ppl = new ArrayList<PersonPreprocessed>();
		for (Person p : pl)
		{
			PersonPreprocessed pp = createPreprocessedEntry(p);
			ppl.add(pp);
		}
		return ppl;
	}

	private PersonPreprocessed createPreprocessedEntry(Person p)
	{
		PersonPreprocessed personPreprocessed = null;
		// List<ContactPreprocessed> contactPreprocessed = new
		// ArrayList<ContactPreprocessed>();

		if (p instanceof Patient)
		{
			personPreprocessed = new PatientPreprocessed((Patient) p);
		} else if (p instanceof HealthcareProvider)
		{
			personPreprocessed = new HealthcareProviderPreprocessed((HealthcareProvider) p);
		} else
		{
			personPreprocessed = new PersonPreprocessed(p);
		}

		// for (Contact c : p.getContacts()) {
		// ContactPreprocessed contactPreprocessedEntry = new
		// ContactPreprocessed(c);
		// //
		// contactPreprocessedEntry.setPersonPreprocessed(personPreprocessed);
		// contactPreprocessed.add(contactPreprocessedEntry);
		// personPreprocessed.setContactPreprocessed(contactPreprocessed);
		// }

		return personPreprocessed;
	}

	private void addNewPersonPreprocessed(PersonPreprocessed personPreprocessed, Person person) throws MPIException
	{
		synchronized (emSynchronizerDummy)
		{
			logger.info("Persongroupid is: " + person.getPersonGroup().getId());
			logger.info("Personid is: " + person.getId());
			personPreprocessed.setPersonId(person.getId());
			personPreprocessed.setPersonGroupID(person.getPersonGroup().getId());
			PersonPreprocessedDAO personPreprocessedDAO = context.getPersonPreprocessedDAO();
			personPreprocessedDAO.addPersonPreprocessed(personPreprocessed);
			personPreprocessedCache.addPersonPreprocessedEntry(personPreprocessed);
		}
	}

	/**
	 * This function searches for persons in the database.
	 *
	 * @param searchMask
	 *            contains the search attributes
	 * @return MPIResponse holds all persons which match the search attributes
	 * @throws MPIException
	 */
	@Override
	public MPIResponse queryPersonByDemographicData(SearchMask searchMask) throws MPIException
	{
		int error = ErrorCode.UNDEFINED;
		if (searchMask == null)
		{
			throw new MPIException(ErrorCode.INVALID_OPERATION_PARAMETER);
		}

		MPIResponse res = new MPIResponse();

		// Initialize account with associated Project and Roles
		// IdentifierDomain domain = initDomain(mpiRequest);
		//
		// List<RequestEntry> entries = mpiRequest.getRequestEntries();
		if (logger.isDebugEnabled())
		{
			logger.debug("Handle EpixSearch.");
		}
		try
		{
			String firstName = searchMask.getFirstName();
			String lastName = searchMask.getLastName();
			String mpiId = searchMask.getMpiid();
			String identifier = searchMask.getIdentifier();
			String identifierDomain = searchMask.getIdentifierDomain();
			String year = searchMask.getBirthYear();
			String month = searchMask.getBirthMonth();
			String day = searchMask.getBirthDay();
			List<Person> resultList = findPersonByPDQ(firstName, lastName, year, month, day, mpiId, identifier,
					identifierDomain);
			error = ErrorCode.OK;
			List<Long> checkList = new ArrayList<Long>();
			for (Person person : resultList)
			{
				if (!checkList.contains(person.getPersonGroup().getId()))
				{
					QueryResponseEntry entry = createQueryResponseEntry(person.getPersonGroup().getReferencePerson(),
							error);
					res.getResponseEntries().add(entry);
					checkList.add(person.getPersonGroup().getId());
				}
			}
			if (logger.isDebugEnabled())
			{
				logger.debug("Handle MPIRequest for " + searchMask);
			}
		} catch (MPIException e)
		{
			logger.error(e.getLocalizedMessage(), e);
			throw e;
		}

		if (reportBuilder.isReportActivated())
		{
			reportBuilder.finalizeReport();
			reportBuilder.storeReport();
		}

		return res;
	}

	/**
	 *
	 * @param firstName
	 * @param lastName
	 * @param year
	 * @param month
	 * @param day
	 * @param mpiId
	 * @param identifier
	 * @param identifierDomain
	 * @return
	 * @throws MPIException
	 */
	private List<Person> findPersonByPDQ(String firstName, String lastName, String year, String month, String day,
			String mpiId, String identifier, String identifierDomain) throws MPIException
	{
		if ((firstName == null || "".equals(firstName)) && (lastName == null || "".equals(lastName))
				&& (mpiId == null || "".equals(mpiId)) && (year == null || "".equals(year))
				&& (month == null || "".equals(month)) && (day == null || "".equals(day))
				&& (identifier == null || "".equals(identifier))
				&& (identifierDomain == null || "".equals(identifierDomain)))
		{
			throw new MPIException(ErrorCode.NO_ATTRIBUTES_DEFINED);
		} else if (((identifier != null && !"".equals(identifier)) && (identifierDomain == null || ""
				.equals(identifierDomain)))
				|| ((identifier == null || "".equals(identifier)) && (identifierDomain != null && !""
						.equals(identifierDomain))))
		{
			throw new MPIException(ErrorCode.IDENTIFIER_OR_DOMAIN_NULL);
		} else
		{
			PersonDAO personDAO = context.getPersonDAO();
			List<Person> persons = new ArrayList<Person>();
			persons = personDAO.findPersonsByPDQ(firstName, lastName, year, month, day, mpiId, identifier,
					identifierDomain);

			// TODO: HIER CACHE IMPLEMENTATION

			// Workaround for mpiIds which belongs to a person and not to a
			// personGroup because of solving possible matches
			if (mpiId != null && !mpiId.isEmpty())
			{

				IdentifierDomainDAO identifierDomainDAO = context.getIdentifierDomainDAO();
				IdentifierDomain mpiDomain = identifierDomainDAO.findMPIDomain(context.getProject());
				List<Person> mpiPersons = personDAO.findPersonsByPDQ(null, null, null, null, null, null, mpiId, mpiDomain.getOid());
				for (Person mpiPerson : mpiPersons)
				{
					boolean existing = false;
					for (Person findPerson : persons)
					{

						if (mpiPerson.getPersonGroup().getId().equals(findPerson.getPersonGroup().getId()))
						{
							existing = true;
						}
					}
					if (!existing)
					{
						persons.add(mpiPerson);
					}
				}
			}
			// Workaround end
			return persons;
		}
	}

	private List<Person> findPersonsByMPIIdentifier(String mpiid) throws MPIException
	{
		if ("".equals(mpiid))
		{
			throw new MPIException(ErrorCode.NO_ATTRIBUTES_DEFINED);
		} else
		{
			PersonDAO personDAO = context.getPersonDAO();
			IdentifierDomainDAO identifierDomainDAO = context.getIdentifierDomainDAO();
			IdentifierDomain identifierDomain = identifierDomainDAO.findMPIDomain(context.getProject());
			List<Person> persons = personDAO.getPersonsByIdentifierAndDomain(mpiid, identifierDomain);
			return persons;
		}
	}

	private Person findPersonByMPIIdentifier(String mpiid) throws MPIException
	{
		if ("".equals(mpiid))
		{
			throw new MPIException(ErrorCode.NO_ATTRIBUTES_DEFINED);
		} else
		{
			PersonDAO personDAO = context.getPersonDAO();
			IdentifierDomainDAO identifierDomainDAO = context.getIdentifierDomainDAO();
			IdentifierDomain identifierDomain = identifierDomainDAO.findMPIDomain(context.getProject());
			Identifier identifier = new Identifier();
			identifier.setValue(mpiid);
			Person person = personDAO.getPersonByIdentifierAndDomain(identifier, identifierDomain);
			return person;
		}
	}

	private PersonGroup findPersonGroupByMPIIdentifier(String mpiid) throws MPIException
	{
		if ("".equals(mpiid))
		{
			throw new MPIException(ErrorCode.NO_ATTRIBUTES_DEFINED);
		} else
		{
			PersonGroupDAO personGroupDAO = context.getPersonGroupDAO();
			IdentifierDomainDAO identifierDomainDAO = context.getIdentifierDomainDAO();
			IdentifierDomain mpiDomain = identifierDomainDAO.findMPIDomain(context.getProject());
			PersonGroup personGroup = personGroupDAO.getPersonGroupByFirstMPI(mpiid, mpiDomain);
			return personGroup;
		}
	}

	private List<CriticalMatch> getCriticalMatches() throws MPIException
	{
		List<PersonLink> personLinks = getPersonLinksUnique();
		List<CriticalMatch> criticalMatches = getCriticalMatchesForPersonLinksFast(personLinks);
		return criticalMatches;
	}
	
	/**
	 * Gets only unique links (e.g. just one entry for link between person1 <-> person2 instead of a second entry for person2 <-> person1
	 * @return
	 * @throws MPIException
	 */
	private List<PersonLink> getPersonLinksUnique() throws MPIException
	{
		PersonLinkDAO personLinkDAO = context.getPersonLinkDAO();
		List<PersonLink> persons = new ArrayList<PersonLink>();
		List<Pair<Long, Long>> uniqueIdPairs = new ArrayList<Pair<Long, Long>>();
		
		List<PersonLink> allPersons = personLinkDAO.getAllLinkedPersons();
		
		for (PersonLink personLink : allPersons)
		{
			// assign src and dest ids by their size to left and right (this requires just one storage and one check for each pair, cause the direction is irrelevant)
			Long left = personLink.getSrcPerson().getId();
			Long right = personLink.getDestPerson().getId();
			
			if (left > right)
			{
				Long temp = left;
				left = right;
				right = temp;
			}
			
			if (!uniqueIdPairs.contains(new Pair<Long, Long>(left, right)))
			{
				persons.add(personLink);
				uniqueIdPairs.add(new Pair<Long, Long>(left, right));
			}
		}

		return persons;
	}

	private List<CriticalMatch> getCriticalMatchesForPersonLinks(List<PersonLink> personLinks) throws MPIException
	{
		PersonDAO personDAO = context.getPersonDAO();
		List<CriticalMatch> criticalMatches = new ArrayList<CriticalMatch>();
		for (PersonLink personLink : personLinks)
		{
			Person pers = personLink.getSrcPerson().getPersonGroup().getReferencePerson();
			CriticalMatch criticalMatch = modelMapper.mapCriticalMatch(pers);
			criticalMatch.setProbability(personLink.getThreshold());
			List<Person> personIdentities = personDAO.findPersonByPersonGroup(pers.getPersonGroup());
			for (Person personIdentity : personIdentities)
			{
				CriticalMatch alias = modelMapper.mapCriticalMatch(personIdentity);
				criticalMatch.getAliasList().add(alias.toString());
			}
			criticalMatches.add(criticalMatch);
		}
		return criticalMatches;
	}

	private List<CriticalMatch> getCriticalMatchesForPersonLinksFast(List<PersonLink> personLinks) throws MPIException
	{
		PersonDAO personDAO = context.getPersonDAO();
		List<CriticalMatch> criticalMatches = new ArrayList<CriticalMatch>();
		List<Long> personIds = new ArrayList<Long>();
		for (PersonLink personLink : personLinks)
		{
			personIds.add(personLink.getSrcPerson().getPersonGroup().getReferencePerson().getId());
		}
		if (personIds.isEmpty())
		{
			return criticalMatches;
		}
		List<Person> linkedPersons = personDAO.getPersonsByIds(personIds);
		for (Person person : linkedPersons)
		{
			CriticalMatch criticalMatch = modelMapper.mapCriticalMatch(person);
			criticalMatches.add(criticalMatch);
		}
		return criticalMatches;
	}

	private List<CriticalMatch> getCriticalMatches(String personId) throws MPIException
	{
		PersonDAO personDAO = context.getPersonDAO();
		Person person = personDAO.getPersonById(Long.valueOf(personId));
		List<PersonLink> personLinks = getCriticalMatchesForPersonGroup(person);
		List<CriticalMatch> criticalMatches = getCriticalMatchesForPersonLinks(personLinks);
		return criticalMatches;
	}

	private List<PersonLink> getPersonLinks() throws MPIException
	{
		PersonLinkDAO personLinkDAO = context.getPersonLinkDAO();
		List<PersonLink> persons = new ArrayList<PersonLink>();
		persons = personLinkDAO.getAllLinkedPersons();
		return persons;
	}

	/**
	 *
	 * @param personLink
	 * @param decision
	 * @return
	 * @throws MPIException
	 */
	private void solveCriticalMatch(String firstPersonID, String secondPersonId, CriticalMatchSolution decision,
			String explanation) throws MPIException
	{
		PersonDAO personDAO = context.getPersonDAO();
		Person person1 = personDAO.getPersonById(Long.valueOf(firstPersonID));
		Person person2 = personDAO.getPersonById(Long.valueOf(secondPersonId));
		if (person1 == null)
		{
			throw new MPIException(ErrorCode.UNKNOWN_PERSON_EXEPTION);
		}
		if (person2 == null)
		{
			throw new MPIException(ErrorCode.UNKNOWN_ALIAS_EXEPTION);
		}
		switch (decision)
		{
		case IDENTICAL_PERSON:
			mergeCriticalMatch(person1, person2, explanation);
			break;
		case DIFFERENT_PERSON:
			deleteCriticalMatch(person1, person2, true, explanation);
			break;
		default:
			throw new MPIException(ErrorCode.MERGE_PERSON_ERROR);
		}
	}

	private ErrorCode deleteCriticalMatch(Person person1, Person person2, boolean createHistory, String explanation)
			throws MPIException
	{
		if (createHistory)
		{
			createPersonLinkHistory(person1, person2, CriticalMatchSolution.DIFFERENT_PERSON, explanation);
		}
		PersonLinkDAO personLinkDAO = context.getPersonLinkDAO();
		personLinkDAO.deletePersonLinkByPersonGroups(person1, person2);
		return new ErrorCode(ErrorCode.OK);
	}

	/**
	 * Merges two persons into one. The first person is the target person which
	 * will get all person identities from the second person
	 *
	 * @param person1
	 * @param person2
	 * @return <code>true</code> when successful
	 * @throws MPIException
	 */
	private ErrorCode mergeCriticalMatch(Person person1, Person person2, String explanation) throws MPIException
	{
		PersonDAO personDAO = context.getPersonDAO();
		createPersonLinkHistory(person1, person2, CriticalMatchSolution.IDENTICAL_PERSON, explanation);
		PersonGroup targetPersonGroup = person1.getPersonGroup();
		PersonGroup oldPersonGroup = person2.getPersonGroup();
		// get the firstMPI from the old group and save it as an identifier from
		// the old reference person
		Identifier oldFirstMPI = oldPersonGroup.getFirstMpi();
		oldPersonGroup.getReferencePerson().getLocalIdentifiers().add(oldFirstMPI);
		deleteCriticalMatch(person1, person2, false, explanation);
		// Put all persons from the old personGroup into the target personGroup
		List<Person> toUpdatePersons = personDAO.findPersonByPersonGroup(oldPersonGroup);
		for (Person toUpdatePerson : toUpdatePersons)
		{
			toUpdatePerson.setPersonGroup(targetPersonGroup);
			PersonPreprocessed pp = personPreprocessedCache.getPersonPreprocessedByPersonID(toUpdatePerson.getId());
			synchronized (emSynchronizerDummy)
			{
				pp.setPersonGroupID(targetPersonGroup.getId());
			}
			PersonHistory personHistory = createHistoryEntry(toUpdatePerson);
			addNewHistoryEntry(personHistory, PersonHistory.EVENT.MERGE);
		}
		oldPersonGroup.setLocked(true);
		addPersonGroupHistory(oldPersonGroup);
		return new ErrorCode(ErrorCode.OK);
	}

	private List<PersonLink> getCriticalMatchesForPersonGroup(Person person) throws MPIException
	{
		if (person == null)
		{
			throw new MPIException(ErrorCode.NO_ATTRIBUTES_DEFINED);
		}
		PersonLinkDAO personLinkDAO = context.getPersonLinkDAO();
		List<PersonLink> personLinks = new ArrayList<PersonLink>();
		personLinks = personLinkDAO.getPersonLinkForPersonGroup(person);
		return personLinks;
	}

	private void createPersonLinkHistory(Person person1, Person person2, CriticalMatchSolution decision,
			String explanation) throws MPIException
	{
		PersonLinkDAO personLinkDAO = context.getPersonLinkDAO();
		List<PersonLink> personLinks = personLinkDAO.getPersonLinkByPersonGroups(person1, person2);
		for (PersonLink personlink : personLinks)
		{
			PersonLinkHistory personLinkHistory = new PersonLinkHistory(personlink);
			if (decision.equals(CriticalMatchSolution.DIFFERENT_PERSON))
			{
				personLinkHistory.setEvent(PersonLinkHistoryEnum.SPLIT.name());
				personLinkHistory.setUpdatedPerson(personlink.getSrcPerson());
				personLinkHistory.setFromGroup(personlink.getSrcPerson().getPersonGroup());
				personLinkHistory.setToGroup(personlink.getSrcPerson().getPersonGroup());
			} else if (decision.equals(CriticalMatchSolution.IDENTICAL_PERSON))
			{
				personLinkHistory.setEvent(PersonLinkHistoryEnum.BIND.name());
				personLinkHistory.setUpdatedPerson(personlink.getSrcPerson());
				if (personlink.getSrcPerson().getPersonGroup().getId().equals(person1.getPersonGroup().getId()))
				{
					personLinkHistory.setFromGroup(person1.getPersonGroup());
					personLinkHistory.setToGroup(person1.getPersonGroup());
				} else if (personlink.getSrcPerson().getPersonGroup().getId().equals(person2.getPersonGroup().getId()))
				{
					personLinkHistory.setFromGroup(person2.getPersonGroup());
					personLinkHistory.setToGroup(person1.getPersonGroup());
				}
			}
			personLinkHistory.setExplanation(explanation);
			PersonLinkHistoryDAO personLinkHistoryDAO = context.getPersonLinkHistoryDAO();
			personLinkHistoryDAO.addPersonLinkHistory(personLinkHistory);
		}
	}

	private List<org.emau.icmvc.ganimed.epix.common.model.IdentifierDomain> getIdentifierDomains() throws MPIException
	{
		IdentifierDomainDAO identifierDomainDAO = context.getIdentifierDomainDAO();
		List<IdentifierDomain> identifierDomainList = null;
		try
		{
			identifierDomainList = identifierDomainDAO.getAllIdentifierDomains();
		} catch (MPIException e)
		{
			throw new MPIException(ErrorCode.INTERNAL_ERROR, e);
		}
		List<org.emau.icmvc.ganimed.epix.common.model.IdentifierDomain> identifierDomains = new ArrayList<org.emau.icmvc.ganimed.epix.common.model.IdentifierDomain>();
		for (IdentifierDomain id : identifierDomainList)
		{
			org.emau.icmvc.ganimed.epix.common.model.IdentifierDomain ident = new org.emau.icmvc.ganimed.epix.common.model.IdentifierDomain(
					id.getName(), id.getOid());
			identifierDomains.add(ident);
		}
		return identifierDomains;
	}

	private List<String> getAllMpiByMpi(String mpiId) throws MPIException
	{
		List<Person> personList = findPersonsByMPIIdentifier(mpiId);
		if (personList != null && !personList.isEmpty())
		{
			if (!checkSameGroup(personList))
			{
				throw new MPIException(ErrorCode.DIFFERENT_PERSONS_FOR_SAME_LOCAL_IDENTIFIER);
			}
		} else
		{
			return null;
		}
		IdentifierDAO identifierDAO = context.getIdentifierDAO();
		IdentifierDomainDAO identifierDomainDAO = context.getIdentifierDomainDAO();
		IdentifierDomain mpiDomain = identifierDomainDAO.findMPIDomain(context.getProject());
		List<Identifier> identifierList = identifierDAO.getIdentifierByPersonGroupAndDomain(personList.get(0)
				.getPersonGroup(), mpiDomain);
		List<String> mpiList = new ArrayList<String>();
		for (Identifier ident : identifierList)
		{
			mpiList.add(ident.getValue());
		}
		return mpiList;
	}

	@Override
	public ConfigurationContainer getConfig() throws MPIException
	{
		ConfigurationContainer configuration = new ConfigurationContainer();
		// Required Fields abrufen
		List<RequiredType> requiredTypes = matchingConfiguration.getRequiredConfig()
				.getRequiredTypes();
		List<String> requiredFieldNames = new ArrayList<String>();
		for (RequiredType type : requiredTypes)
		{
			if (type.getQualifiedClassName().equals(PatientPreprocessed.class.getName()))
			{
				for (RequiredField field : type.getRequiredFields())
				{
					requiredFieldNames.add(field.getFieldName());
				}
			}
		}
		configuration.setRequiredFields(requiredFieldNames);
		// Value Mapping abrufen
		Properties valueMappingProperties = new Properties();
		InputStream stream = getClass().getResourceAsStream("/value-mapping.properties");
		try
		{
			valueMappingProperties.load(stream);
			stream.close();

			Map<String, String> valueMapping = new HashMap<String, String>();
			for (String key : valueMappingProperties.stringPropertyNames())
			{
				valueMapping.put(key, valueMappingProperties.getProperty(key));
			}
			configuration.setValueFieldMapping(valueMapping);
		} catch (FileNotFoundException e)
		{
			logger.error("Configuration file value-mapping.properties could not be found: " + e.getMessage());
			throw new MPIException(ErrorCode.NO_CONFIGURATION_FOR_EPIX);
		} catch (IOException e)
		{
			logger.error("Could not load Configuration file value-mapping.properties: " + e.getMessage());
			throw new MPIException(ErrorCode.NO_CONFIGURATION_FOR_EPIX);
		}
		return configuration;
	}

	@Override
	public void addLocalIdsToMpi(String mpiId, List<org.emau.icmvc.ganimed.epix.common.model.Identifier> localIds) throws MPIException {
		if (mpiId == null) {
			logger.warn("invalid argument: mpiId is null");
			return;
		} else if (localIds == null || localIds.isEmpty()) {
			logger.warn("invalid argument: localIds is null or empty");
			return;
		}
		// duplikate entfernen
		List<org.emau.icmvc.ganimed.epix.common.model.Identifier> uniqueLocalIds = new ArrayList<org.emau.icmvc.ganimed.epix.common.model.Identifier>();
		for (org.emau.icmvc.ganimed.epix.common.model.Identifier localId : localIds) {
			if (!uniqueLocalIds.contains(localId)) {
				uniqueLocalIds.add(localId);
			}
		}
		if (logger.isInfoEnabled()) {
			StringBuilder sb = new StringBuilder("adding ");
			sb.append(uniqueLocalIds.size());
			sb.append(" local ids to mpi-id '");
			sb.append(mpiId);
			sb.append("'");
			logger.info(sb.toString());
		}

		List<Identifier> newIdentifiers = null;
		newIdentifiers = modelMapper.mapIdentifiers(uniqueLocalIds);
		Timestamp entryDate = new Timestamp(System.currentTimeMillis());
		for (Identifier identifier : newIdentifiers) {
			identifier.setEntryDate(entryDate);
			identifier.setDomain(getIdentifierDomain(identifier.getDomain())); // nein, ich kommentier das jetzt nicht ...
			identifier.setIdentifierType(IdentifierTypeEnum.STANDARD);
		}

		// person anhand mpiId suchen
		PersonGroup findGroup = findPersonGroupByMPIIdentifier(mpiId);
		Person person = null;
		if (findGroup == null) {
			Person tempPerson = findPersonByMPIIdentifier(mpiId);
			if(tempPerson == null) {
				throw new MPIException(ErrorCode.MPIID_NOT_EXISTS);
			}
			findGroup = tempPerson.getPersonGroup();
		}
		if (findGroup == null) {
			logger.warn("exception while adding local ids to mpiId '" + mpiId + "' - no person found with that mpiId");
			throw new MPIException(ErrorCode.NO_PERSON_FOUND);
		} else if (findGroup.isLocked()) {
			Person tempPerson = findPersonByMPIIdentifier(mpiId);
			person = tempPerson.getPersonGroup().getReferencePerson();
		} else {
			person = findGroup.getReferencePerson();
		}

		// eventuelle weitere personen anhand localIds suchen
		List<Person> existIdentPersons = null;
		List<Identifier> dbLocalIds = new ArrayList<Identifier>();
		try {
			// die werden sonst in "personExists" geloescht - nicht fragen ...
			dbLocalIds.addAll(person.getLocalIdentifiers());
			// und noch mal duplikate raus
			for (Identifier newId : newIdentifiers) {
				if (!dbLocalIds.contains(newId)) {
					dbLocalIds.add(newId);
				} else if (logger.isInfoEnabled()) {
					logger.info("localId already exists: " + newId);
				}
			}
			existIdentPersons = personsExists(person, dbLocalIds);
		} catch (MPIException e) {
			logger.error("error while searching persons by local id: " + e.getMessage());
			throw new MPIException(ErrorCode.MATCH_SAME_LOCAL_IDENTIFIER_ERROR);
		}
		if (existIdentPersons != null && !existIdentPersons.isEmpty()) {
			for (Person existIdentPerson : existIdentPersons) {
				if (!existIdentPerson.getPersonGroup().getId().equals(person.getPersonGroup().getId())) {
					throw new MPIException(ErrorCode.DIFFERENT_PERSON_GROUP_FOR_UPDATE);
				}
			}
		}

		// save
		PersonPreprocessed pp = createPreprocessedEntry(person);
		savePerfectMatch(person, dbLocalIds, pp, PersonHistory.EVENT.UPDATE, true);
		context.flushEM();

		if (logger.isInfoEnabled()) {
			StringBuilder sb = new StringBuilder("added ");
			sb.append(uniqueLocalIds.size());
			sb.append(" local ids to mpi-id '");
			sb.append(mpiId);
			sb.append("'");
			logger.info(sb.toString());
		}
	}
	@Override
	public List<org.emau.icmvc.ganimed.epix.common.model.Person> searchPersonByPDQCache(SearchMask searchmask) throws MPIException
	{
		List<PersonPreprocessed> preprocessedPersons = new ArrayList<PersonPreprocessed>();
		List<org.emau.icmvc.ganimed.epix.common.model.Person> persons = new ArrayList<org.emau.icmvc.ganimed.epix.common.model.Person>();

		preprocessedPersons = personPreprocessedCache.getPersonsPreprocessedByPDQ(searchmask);

		for (PersonPreprocessed pc : preprocessedPersons)
		{
			org.emau.icmvc.ganimed.epix.common.model.Person person = new org.emau.icmvc.ganimed.epix.common.model.Person();
			person.setId(pc.getPersonId());
			person.setFirstName(pc.getFirstName());
			person.setLastName(pc.getLastName());
			person.setGender(Gender.valueOf(pc.getGender()));
			person.setBirthDate(pc.getBirthDate());
			person.setBirthPlace(pc.getBirthPlace());
			person.setDegree(pc.getDegree());
			person.setCivilStatus(pc.getCivilStatus());
			person.setNationality(pc.getNationality());
			person.setReligion(pc.getReligion());
			MPIID mpiid = new MPIID();
			PersonGroup pg = context.getPersonGroupDAO().getPersonGroupById(pc.getPersonGroupID());
			if (pg != null)
			{
				mpiid.setIdType(pg.getFirstMpi().getDomain().getName());
				mpiid.setValue(pg.getFirstMpi().getValue());
				person.setMpiid(mpiid);
				persons.add(person);
			}
			else
			{
				logger.error("could not find person group for id " + pc.getPersonGroupID().toString());
			}
		}

		return persons;
	}
	
	/**
	 * Get all Persons in Database
	 * 
	 * @return list with all persons
	 * @throws MPIException
	 */
	public List<org.emau.icmvc.ganimed.epix.common.model.Person> getAllPersons() throws MPIException {
		List<org.emau.icmvc.ganimed.epix.common.model.Person> result = new ArrayList<org.emau.icmvc.ganimed.epix.common.model.Person>();

		for (Person p : context.getPersonDAO().getAllPersons()) {
			result.add(this.mapOutputPerson(p));
		}

		return result;
	}
	
	/**
	 * Get all Reference Persons in Database
	 * 
	 * @return list with all persons
	 * @throws MPIException
	 */
	public List<org.emau.icmvc.ganimed.epix.common.model.Person> getAllReferencePersons() throws MPIException {
		List<org.emau.icmvc.ganimed.epix.common.model.Person> result = new ArrayList<org.emau.icmvc.ganimed.epix.common.model.Person>();
		Map<Long, org.emau.icmvc.ganimed.epix.common.model.Person> uniqueReferencePersons = new HashMap<Long, org.emau.icmvc.ganimed.epix.common.model.Person>();
		
		for (Person p : context.getPersonDAO().getAllPersons()) {
			org.emau.icmvc.ganimed.epix.common.model.Person referencePerson = this.mapOutputReferencePerson(p);
			uniqueReferencePersons.put(p.getPersonGroup().getId(), referencePerson);
		}
		
		for (org.emau.icmvc.ganimed.epix.common.model.Person p : uniqueReferencePersons.values())
		{
			result.add(p);
		}

		return result;
	}
}
