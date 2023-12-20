package org.emau.icmvc.ttp.epix.service;

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

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.emau.icmvc.ttp.auth.AbstractServiceBase;
import org.emau.icmvc.ttp.auth.TTPNames;
import org.emau.icmvc.ttp.epix.common.exception.DuplicateEntryException;
import org.emau.icmvc.ttp.epix.common.exception.IllegalOperationException;
import org.emau.icmvc.ttp.epix.common.exception.InvalidParameterException;
import org.emau.icmvc.ttp.epix.common.exception.MPIErrorCode;
import org.emau.icmvc.ttp.epix.common.exception.MPIException;
import org.emau.icmvc.ttp.epix.common.exception.UnknownObjectException;
import org.emau.icmvc.ttp.epix.common.exception.UnknownObjectType;
import org.emau.icmvc.ttp.epix.common.model.ContactInDTO;
import org.emau.icmvc.ttp.epix.common.model.DomainDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentifierDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityInBaseDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityInDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityOutDTO;
import org.emau.icmvc.ttp.epix.common.model.MPIIdentityDTO;
import org.emau.icmvc.ttp.epix.common.model.MPIRequestDTO;
import org.emau.icmvc.ttp.epix.common.model.MPIResponseDTO;
import org.emau.icmvc.ttp.epix.common.model.PersonDTO;
import org.emau.icmvc.ttp.epix.common.model.PossibleMatchHistoryDTO;
import org.emau.icmvc.ttp.epix.common.model.RequestConfig;
import org.emau.icmvc.ttp.epix.common.model.ResponseEntryDTO;
import org.emau.icmvc.ttp.epix.common.model.enums.IdentifierDeletionResult;
import org.emau.icmvc.ttp.epix.common.model.enums.MatchStatus;
import org.emau.icmvc.ttp.epix.common.model.enums.MatchingMode;
import org.emau.icmvc.ttp.epix.persistence.PublicDAO;

/**
 *
 * @author geidell
 */
public abstract class EpixServiceBase extends AbstractServiceBase
{
	protected final Logger logger = LogManager.getLogger(getClass());
	private static final String PARAMETER_MISSING_MESSAGE = "invalid parameter: ";
	protected static final ResponseEntryDTO MATCH_ERROR_ENTRY = new ResponseEntryDTO(null, MatchStatus.MATCH_ERROR);
	protected static final RequestConfig DEFAULT_REQUEST_CONFIG = new RequestConfig();

	@EJB
	protected PublicDAO dao;

	@Override
	public TTPNames.Tool getTool()
	{
		return TTPNames.Tool.epix;
	}

	protected List<DomainDTO> filterAllowedDomains(List<DomainDTO> domains)
	{
		return filterAllowedDomains(domains, DomainDTO::getName);
	}

	protected void checkAllowedEntity(DomainSupplier domainSupplier, UnknownObjectExceptionSupplier exceptionSupplier) throws UnknownObjectException
	{
		if (!isAllowedDomain(domainSupplier.get()))
		{
			throw exceptionSupplier.get();
		}
	}

	protected void checkAllowedPerson(PersonDTO person) throws UnknownObjectException
	{
		checkAllowedEntity(
				person::getDomainName,
				() -> PublicDAO.createUnknownPersonIdException(person.getPersonId()));
	}

	protected void checkAllowedPersonId(long personId) throws UnknownObjectException
	{
		checkAllowedEntity(
				getPersonIdDomainSupplier(personId),
				() -> PublicDAO.createUnknownPersonIdException(personId));
	}

	protected void checkAllowedIdentityId(long identityId) throws UnknownObjectException
	{
		checkAllowedEntity(
				getIdentityIdDomainSupplier(identityId),
				() -> PublicDAO.createUnknownIdentityIdException(identityId));
	}

	protected void checkAllowedContactId(long contactId) throws UnknownObjectException
	{
		checkAllowedEntity(
				getContactIdDomainSupplier(contactId),
				() -> PublicDAO.createUnknownContactIdException(contactId));
	}

	protected void checkAllowedDomain(String domain) throws UnknownObjectException
	{
		checkAllowedEntity(
				() -> domain,
				() -> new UnknownObjectException("domain not found: " + domain, UnknownObjectType.DOMAIN, domain));
	}

	protected void checkAllowedPossibleMatchId(long possibleMatchId) throws InvalidParameterException
	{
		try
		{
			checkAllowedEntity(
					getPossibleMatchIdDomainSupplier(possibleMatchId),
					() ->
					{
						String message = "possible match with id " + possibleMatchId + " not found";
						InvalidParameterException ipe = new InvalidParameterException("possibleMatchId", message);
						return new UnknownObjectException(ipe);
					});
		}
		catch (UnknownObjectException e)
		{
			if (e.getCause() instanceof InvalidParameterException ipe)
			{
				throw ipe;
			}
			throw new InvalidParameterException("possibleMatchId", e);
		}
	}

	protected void checkAllowedPossibleMatchHistoryResult(long identityId, List<PossibleMatchHistoryDTO> result) throws UnknownObjectException
	{
		if (!result.isEmpty())
		{
			// check allowed identityID via the person ID of the result,
			// so we can skip an extra call to dao.getIdentityById(id) to get the person ID (to get the domain)
			checkAllowedEntity(
					getPersonIdDomainSupplier(result.get(0).getPerson1Id()),
					() -> PublicDAO.createUnknownIdentityIdException(identityId));
		}
	}

	@FunctionalInterface
	protected interface DomainSupplier
	{
		String get() throws UnknownObjectException;
	}

	protected DomainSupplier getPersonIdDomainSupplier(long personId)
	{
		return () -> dao.getPersonById(personId).getDomainName();
	}

	protected DomainSupplier getIdentityIdDomainSupplier(long identityId)
	{
		return () -> dao.getPersonById(dao.getIdentityById(identityId).getPersonId()).getDomainName();
	}

	protected DomainSupplier getContactIdDomainSupplier(long contactId)
	{
		return () -> dao.getPersonById(dao.getIdentityById(dao.getContactById(contactId).getIdentityId()).getPersonId()).getDomainName();
	}

	protected DomainSupplier getPossibleMatchIdDomainSupplier(long possibleMatchId)
	{
		return () ->
		{
			try
			{
				MPIIdentityDTO matchingMPIIdentity = dao.getPossibleMatchById(possibleMatchId).getMatchingMPIIdentities().stream().findFirst().orElse(null);

				if (matchingMPIIdentity == null)
				{
					throw new UnknownObjectException("no matchingMPIIdentity for possibleMatchId " + possibleMatchId);
				}

				return getPersonIdDomainSupplier(matchingMPIIdentity.getIdentity().getPersonId()).get();
			}
			catch (InvalidParameterException e)
			{
				throw new UnknownObjectException(e);
			}
		};
	}

	@FunctionalInterface
	protected interface UnknownObjectExceptionSupplier
	{
		UnknownObjectException get();
	}

	protected void checkParameter(Object parameter, String paramName) throws InvalidParameterException
	{
		if (parameter == null)
		{
			throwParameterMissingIPE(paramName);
		}
		else if (parameter instanceof String s)
		{
			if (s.isEmpty())
			{
				throwParameterMissingIPE(paramName);
			}
		}
		else if (parameter instanceof List<?> l)
		{
			if (l.isEmpty())
			{
				throwParameterMissingIPE(paramName);
			}
		}
		else if (parameter instanceof IdentityInBaseDTO id)
		{
			validateIdentity(id, paramName);
		}
	}

	private void validateIdentity(IdentityInBaseDTO id, String paramName) throws InvalidParameterException
	{
		InvalidParameterException ipe = null;

		if (isFutureDay(id.getBirthDate()))
		{
			ipe = new InvalidParameterException(paramName, InvalidParameterException.ErrorCode.FUTURE_DATE_OF_BIRTH);
		}
		else if (isFutureDay(id.getDateOfDeath()))
		{
			ipe = new InvalidParameterException(paramName, InvalidParameterException.ErrorCode.FUTURE_DATE_OF_DEATH);
		}
		// to be continued

		if (ipe != null)
		{
			logger.warn(ipe);
			throw ipe;
		}
	}

	private boolean isFutureDay(Date date)
	{
		if (date != null)
		{
			LocalDate today = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate givenDay = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			return givenDay.isAfter(today);
		}
		return false;
	}

	protected void checkMatchingMode(MatchingMode requiredMM, MatchingMode domainMM, String domainName) throws MPIException
	{
		if (!requiredMM.equals(domainMM))
		{
			String message = "domain " + domainName + " has the wrong matching type for this function, it should be " + requiredMM;
			logger.error(message);
			throw new MPIException(MPIErrorCode.WRONG_MATCHING_TYPE, message);
		}
	}

	private void throwParameterMissingIPE(String paramName) throws InvalidParameterException
	{
		String message = PARAMETER_MISSING_MESSAGE + paramName;
		logger.warn(message);
		throw new InvalidParameterException(paramName, message);
	}

	protected ResponseEntryDTO requestMPIInternal(String notificationClientID, String domainName, IdentityInDTO identity,
			String sourceName, String comment, RequestConfig requestConfig)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		requestConfig = requestConfig == null ? DEFAULT_REQUEST_CONFIG : requestConfig;

		if (logger.isDebugEnabled())
		{
			logger.debug("requestMPI for domain {}, source {}, comment '{}' and requestConfig {} with {} (notificationClientID = {})",
					domainName, sourceName, comment, requestConfig, identity, notificationClientID);
		}
		else
		{
			logger.info("requestMPI");
		}
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		checkParameter(identity, "identity");
		checkParameter(sourceName, "sourceName");
		checkMatchingMode(MatchingMode.MATCHING_IDENTITIES, dao.getMatchingMode(domainName), domainName);
		ResponseEntryDTO result = dao.handleMPIRequest(notificationClientID, domainName, sourceName, identity, comment, requestConfig, getAuthUser());
		logger.info("requestMPI was successful");
		return result;
	}

	protected MPIResponseDTO requestMPIBatchInternal(String notificationClientID, MPIRequestDTO mpiRequest)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("requestMPIBatch with {} (notificationClientID = {})", mpiRequest, notificationClientID);
		}
		else
		{
			logger.info("requestMPIBatch (notificationClientID = {})", notificationClientID);
		}
		checkParameter(mpiRequest, "mpiRequest");
		checkParameter(mpiRequest.getDomainName(), "mpiRequest.getDomainName()");
		checkAllowedDomain(mpiRequest.getDomainName());
		checkParameter(mpiRequest.getSourceName(), "mpiRequest.getSourceName()");
		checkMatchingMode(MatchingMode.MATCHING_IDENTITIES, dao.getMatchingMode(mpiRequest.getDomainName()), mpiRequest.getDomainName());
		MPIResponseDTO result = new MPIResponseDTO();
		for (IdentityInBaseDTO identity : mpiRequest.getRequestEntries())
		{
			try
			{
				ResponseEntryDTO singleResult = dao.handleMPIRequest(notificationClientID, mpiRequest.getDomainName(), mpiRequest.getSourceName(),
						identity, mpiRequest.getComment(), mpiRequest.getRequestConfig(), getAuthUser());
				result.getResponseEntries().put(identity, singleResult);
			}
			catch (InvalidParameterException | MPIException e)
			{
				logger.warn("exception while requesting mpi", e);
				result.getResponseEntries().put(identity, MATCH_ERROR_ENTRY);
			}
		}
		logger.info("requestMPIBatch finished");
		return result;
	}

	protected ResponseEntryDTO updatePersonInternal(String notificationClientID, String domainName, String mpiId, IdentityInDTO identity,
			String sourceName, boolean force, String comment, RequestConfig requestConfig)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		requestConfig = requestConfig == null ? DEFAULT_REQUEST_CONFIG : requestConfig;

		if (logger.isDebugEnabled())
		{
			logger.debug("updatePerson for domain {}, mpiId {}, source {} and requestConfig {} with {} (notificationClientID = {})",
					domainName, mpiId, sourceName, requestConfig, identity, notificationClientID);
		}
		else
		{
			logger.info("updatePerson{} (notificationClientID = {})",
					requestConfig != DEFAULT_REQUEST_CONFIG ? "WithConfig" : "", notificationClientID);
		}
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		checkParameter(mpiId, "mpiId");
		checkParameter(identity, "identity");
		checkParameter(sourceName, "sourceName");
		ResponseEntryDTO result = dao.updatePerson(notificationClientID, domainName, mpiId, identity, sourceName, force, comment, requestConfig, getAuthUser());
		logger.info("updatePerson successfully executed");
		return result;
	}

	protected ResponseEntryDTO addPersonInternal(String notificationClientID, String domainName, IdentityInDTO identity, String sourceName, String comment)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("addPerson for domain {} and source {} with {} (notificationClientID = {})",
					domainName, sourceName, identity, notificationClientID);
		}
		else
		{
			logger.info("addPerson (notificationClientID = {})", notificationClientID);
		}
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		checkParameter(identity, "identity");
		checkParameter(sourceName, "sourceName");
		checkMatchingMode(MatchingMode.NO_DECISION, dao.getMatchingMode(domainName), domainName);
		ResponseEntryDTO result = dao.addPerson(notificationClientID, domainName, identity, sourceName, comment, getAuthUser());
		logger.info("person successfully added");
		return result;
	}

	protected ResponseEntryDTO setReferenceIdentityInternal(String notificationClientID, String domainName, String mpiId, long identityId, String comment)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("setReferenceIdentity for mpiId {} within domain {}, new reference should be identity with id {}. comment is '{}' (notificationClientID = {})",
					mpiId, domainName, identityId, comment, notificationClientID);
		}
		else
		{
			logger.info("setReferenceIdentity (notificationClientID = {})", notificationClientID);
		}
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		checkParameter(mpiId, "mpiId");
		ResponseEntryDTO result = dao.setReferenceIdentity(notificationClientID, domainName, mpiId, identityId, comment, getAuthUser());
		logger.info("reference identity set");
		return result;
	}

	protected void deactivateIdentityInternal(String notificationClientID, long identityId)
			throws MPIException, UnknownObjectException
	{
		logger.info("deactivateIdentity with id {} (notificationClientID = {})",
				identityId, notificationClientID);
		checkAllowedIdentityId(identityId);
		dao.deactivateIdentity(notificationClientID, identityId, getAuthUser());
		logger.info("Identity deactivated");
	}

	protected void deactivatePersonInternal(String notificationClientID, String domainName, String mpiId)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		logger.info("deactivatePerson with mpi id {} within domain {} (notificationClientID = {})",
				mpiId, domainName, notificationClientID);
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		checkParameter(mpiId, "mpiId");
		dao.deactivatePerson(notificationClientID, domainName, mpiId, getAuthUser());
		logger.info("Person deactivated");
	}

	protected void deleteIdentityInternal(String notificationClientID, long identityId)
			throws IllegalOperationException, MPIException, UnknownObjectException
	{
		logger.info("deleteIdentity with id {} (notificationClientID = {})", identityId, notificationClientID);
		checkAllowedIdentityId(identityId);
		dao.deleteIdentity(notificationClientID, identityId, getAuthUser());
		logger.info("Identity deleted");
	}

	protected void deletePersonInternal(String notificationClientID, String domainName, String mpiId)
			throws IllegalOperationException, InvalidParameterException, MPIException, UnknownObjectException
	{
		logger.info("deletePerson with mpi id {} within domain {} (notificationClientID = {})",
				mpiId, domainName, notificationClientID);
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		checkParameter(mpiId, "mpiId");
		dao.deletePerson(notificationClientID, domainName, mpiId, getAuthUser());
		logger.info("Person deleted");
	}

	protected void updatePrivacyInternal(String domainName, List<String> mpiIds, boolean onlyReferenceIdentity)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		logger.info("updatePrivacy for {} mpi ids ({} identities) within domain {}",
				mpiIds == null || mpiIds.isEmpty() ? "all" : mpiIds.size(), onlyReferenceIdentity ? "only primary" : "all", domainName);
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		dao.updatePrivacy(domainName, mpiIds, onlyReferenceIdentity, getAuthUser());
		logger.info("Privacy updated");
	}

	protected IdentityOutDTO addContactInternal(String notificationClientID, long identityId, ContactInDTO contactDTO)
			throws DuplicateEntryException, MPIException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("addContact with identity id {} and {} (notificationClientID = {})",
					identityId, contactDTO, notificationClientID);
		}
		else
		{
			logger.info("addContact (notificationClientID = {})", notificationClientID);
		}
		checkAllowedIdentityId(identityId);
		IdentityOutDTO result = dao.addContact(notificationClientID, identityId, contactDTO, getAuthUser());
		logger.info("contact added");
		return result;
	}

	protected void deactivateContactInternal(long contactId)
			throws UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("deactivateContact with id {}", contactId);
		}
		else
		{
			logger.info("deactivateContact");
		}
		checkAllowedContactId(contactId);
		dao.deactivateContact(contactId, getAuthUser());
		logger.info("contact deactivated");
	}

	protected void addLocalIdentifierToActivePersonWithMPIInternal(String notificationClientID, String domainName, String mpiId, List<IdentifierDTO> localIds)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			StringBuilder sb = new StringBuilder("addLocalIdentifierToActivePersonWithMPIInternal for domain ");
			sb.append(domainName);
			sb.append(" and mpiId ");
			sb.append(mpiId);
			sb.append(". add the following local ids: ");
			for (IdentifierDTO identifier : localIds)
			{
				sb.append(identifier);
				sb.append(' ');
			}
			sb.append(" (notificationClientID = ").append(notificationClientID).append(")");
			logger.debug(sb);
		}
		else
		{
			logger.info("addLocalIdentifierToMPI (notificationClientID = {})", notificationClientID);
		}
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		checkParameter(mpiId, "mpiId");
		checkParameter(localIds, "localIds");
		dao.addIdentifierToActivePerson(notificationClientID, domainName, mpiId, localIds, getAuthUser());
		logger.info("local identifier added to mpi");
	}

	protected void addLocalIdentifierToIdentifierInternal(String notificationClientID, String domainName, IdentifierDTO identifierDTO,
			List<IdentifierDTO> localIds) throws InvalidParameterException, MPIException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			StringBuilder sb = new StringBuilder("addLocalIdentifierToIdentifier for domain ");
			sb.append(domainName);
			sb.append(" and ");
			sb.append(identifierDTO);
			sb.append(". add the following local ids: ");
			for (IdentifierDTO ident : localIds)
			{
				sb.append(ident);
				sb.append(' ');
			}
			sb.append(" (notificationClientID = ").append(notificationClientID).append(")");
			logger.debug(sb);
		}
		else
		{
			logger.info("addLocalIdentifierToIdentifier (notificationClientID = " + notificationClientID + ")");
		}
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		checkParameter(identifierDTO, "identifier");
		checkParameter(localIds, "localIds");
		dao.addLocalIdentifierToIdentifier(notificationClientID, domainName, identifierDTO, localIds, getAuthUser());
		logger.info("local identifier added to identities with the given identifier");
	}

	protected Map<IdentifierDTO, IdentifierDeletionResult> removeLocalIdentifierInternal(String notificationClientID, String domainName, List<IdentifierDTO> localIds)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		StringBuilder sb = new StringBuilder("removeLocalIdentifier for domain ")
				.append(domainName).append(" (notificationClientID = ").append(notificationClientID).append(").");

		if (logger.isDebugEnabled())
		{
			sb.append("Remove the following local ids: ");
			for (IdentifierDTO identifier : localIds)
			{
				sb.append(identifier);
				sb.append(' ');
			}
			logger.debug(sb);
		}
		else
		{
			logger.info(sb);
		}
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		checkParameter(localIds, "localIds");
		return dao.removeIdentifier(notificationClientID, domainName, localIds, getAuthUser());
	}

	protected void removePossibleMatchesInternal(List<Long> possibleMatchIds, String comment)
			throws InvalidParameterException, MPIException
	{
		if (logger.isDebugEnabled())
		{
			StringBuilder sb = new StringBuilder("removePossibleMatches with id ");
			for (Long id : possibleMatchIds)
			{
				sb.append(id);
				sb.append(' ');
			}
			sb.append(" and comment ");
			sb.append(comment);
			logger.debug(sb.toString());
		}
		else
		{
			logger.info("removePossibleMatches");
		}
		checkParameter(possibleMatchIds, "possibleMatchIds");

		for (Long possibleMatchId : possibleMatchIds)
		{
			checkAllowedPossibleMatchId(possibleMatchId);
		}

		for (Long possibleMatchId : possibleMatchIds)
		{
			dao.removePossibleMatch(possibleMatchId, comment, getAuthUser());
		}
		logger.info("{} possible matches deleted", possibleMatchIds.size());
	}

	protected void removePossibleMatchInternal(long possibleMatchId, String comment)
			throws InvalidParameterException, MPIException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("removePossibleMatch with id {} with comment {}", possibleMatchId, comment);
		}
		else
		{
			logger.info("removePossibleMatch");
		}
		checkAllowedPossibleMatchId(possibleMatchId);
		dao.removePossibleMatch(possibleMatchId, comment, getAuthUser());
		logger.info("possible match deleted");
	}

	protected void assignIdentityInternal(String notificationClientID, long possibleMatchId, long winningIdentityId, String comment)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("assignIdentity for link id {} with winning id {} with comment {} (notificationClientID = {})",
					possibleMatchId, winningIdentityId, comment, notificationClientID);
		}
		else
		{
			logger.info("assignIdentity (notificationClientID = {})", notificationClientID);
		}
		checkAllowedIdentityId(winningIdentityId);
		dao.assignIdentity(notificationClientID, possibleMatchId, winningIdentityId, comment, getAuthUser());
		logger.info("identities assigned");
	}

	protected void moveIdentitiesForIdentifierToPersonInternal(String notificationClientID, String domainName, IdentifierDTO identifier,
			String mpiId, String comment) throws InvalidParameterException, MPIException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("moveIdentitiesForIdentifierToPerson for domain {} and {} to mpiId {} with comment {} (notificationClientID = {})",
					domainName, identifier, mpiId, comment, notificationClientID);
		}
		else
		{
			logger.info("moveIdentitiesForIdentifierToPerson (notificationClientID = {})", notificationClientID);
		}
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		checkParameter(identifier, "identifier");
		checkParameter(mpiId, "mpiId");
		checkMatchingMode(MatchingMode.NO_DECISION, dao.getMatchingMode(domainName), domainName);
		dao.moveIdentitiesForIdentifierToPersonNotification(notificationClientID, domainName, identifier, mpiId, comment, getAuthUser());
		logger.info("identities moved");
	}
}
