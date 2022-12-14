package org.emau.icmvc.ttp.epix.service;

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


import java.util.List;

import javax.ejb.EJB;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.emau.icmvc.ttp.epix.common.exception.DuplicateEntryException;
import org.emau.icmvc.ttp.epix.common.exception.IllegalOperationException;
import org.emau.icmvc.ttp.epix.common.exception.InvalidParameterException;
import org.emau.icmvc.ttp.epix.common.exception.MPIErrorCode;
import org.emau.icmvc.ttp.epix.common.exception.MPIException;
import org.emau.icmvc.ttp.epix.common.exception.UnknownObjectException;
import org.emau.icmvc.ttp.epix.common.model.ContactInDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentifierDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityInBaseDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityInDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityOutDTO;
import org.emau.icmvc.ttp.epix.common.model.MPIRequestDTO;
import org.emau.icmvc.ttp.epix.common.model.MPIResponseDTO;
import org.emau.icmvc.ttp.epix.common.model.RequestConfig;
import org.emau.icmvc.ttp.epix.common.model.ResponseEntryDTO;
import org.emau.icmvc.ttp.epix.common.model.enums.MatchStatus;
import org.emau.icmvc.ttp.epix.common.model.enums.MatchingMode;
import org.emau.icmvc.ttp.epix.persistence.PublicDAO;

/**
 * 
 * @author geidell
 *
 */
public abstract class EpixServiceBase
{
	protected final Logger logger = LogManager.getLogger(getClass());
	@EJB
	protected PublicDAO dao;
	private static final String PARAMETER_MISSING_MESSAGE = "invalid parameter: ";
	protected static final ResponseEntryDTO MATCH_ERROR_ENTRY = new ResponseEntryDTO(null, MatchStatus.MATCH_ERROR);
	protected static final RequestConfig DEFAULT_REQUEST_CONFIG = new RequestConfig();

	protected void checkParameter(Object parameter, String paramName) throws InvalidParameterException
	{
		if (parameter == null)
		{
			throwIPE(paramName);
		}
		else if (parameter instanceof String)
		{
			if (((String) parameter).isEmpty())
			{
				throwIPE(paramName);
			}
		}
		else if (parameter instanceof List)
		{
			if (((List<?>) parameter).isEmpty())
			{
				throwIPE(paramName);
			}
		}
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

	private void throwIPE(String paramName) throws InvalidParameterException
	{
		String message = PARAMETER_MISSING_MESSAGE + paramName;
		logger.warn(message);
		throw new InvalidParameterException(paramName, message);
	}

	protected ResponseEntryDTO requestMPIInternal(String notificationClientID, String domainName, IdentityInDTO identity,
			String sourceName, String comment, RequestConfig requestConfig)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("requestMPI for domain " + domainName + ", source " + sourceName + ", comment '" + comment
					+ "' and requestConfig " + requestConfig + " with " + identity + " (notificationClientID = " + notificationClientID + ")");
		}
		else
		{
			logger.info("requestMPI");
		}
		checkParameter(domainName, "domainName");
		checkParameter(identity, "identity");
		checkParameter(sourceName, "sourceName");
		checkMatchingMode(MatchingMode.MATCHING_IDENTITIES, dao.getMatchingMode(domainName), domainName);
		ResponseEntryDTO result = dao.handleMPIRequest(notificationClientID, domainName, sourceName, identity, comment, requestConfig);
		logger.info("requestMPI was successful");
		return result;
	}

	protected MPIResponseDTO requestMPIBatchInternal(String notificationClientID, MPIRequestDTO mpiRequest)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("requestMPIBatch with " + mpiRequest + " (notificationClientID = " + notificationClientID + ")");
		}
		else
		{
			logger.info("requestMPIBatch (notificationClientID = " + notificationClientID + ")");
		}
		checkParameter(mpiRequest, "mpiRequest");
		checkParameter(mpiRequest.getDomainName(), "mpiRequest.getDomainName()");
		checkParameter(mpiRequest.getSourceName(), "mpiRequest.getSourceName()");
		checkMatchingMode(MatchingMode.MATCHING_IDENTITIES, dao.getMatchingMode(mpiRequest.getDomainName()), mpiRequest.getDomainName());
		MPIResponseDTO result = new MPIResponseDTO();
		for (IdentityInBaseDTO identity : mpiRequest.getRequestEntries())
		{
			try
			{
				ResponseEntryDTO singleResult = dao.handleMPIRequest(notificationClientID, mpiRequest.getDomainName(), mpiRequest.getSourceName(),
						identity, mpiRequest.getComment(), mpiRequest.getRequestConfig());
				result.getResponseEntries().put(identity, singleResult);
			}
			catch (InvalidParameterException | MPIException e)
			{
				result.getResponseEntries().put(identity, MATCH_ERROR_ENTRY);
			}
		}
		logger.info("requestMPIBatch was successful");
		return result;
	}

	protected ResponseEntryDTO updatePersonInternal(String notificationClientID, String domainName, String mpiId, IdentityInDTO identity,
			String sourceName, boolean force, String comment, RequestConfig requestConfig)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("updatePerson for domain " + domainName + ", mpiId " + mpiId + ", source " + sourceName
					+ " and requestConfig " + requestConfig + " with " + identity + " (notificationClientID = " + notificationClientID + ")");
		}
		else
		{
			logger.info("updatePerson (notificationClientID = " + notificationClientID + ")");
		}
		checkParameter(domainName, "domainName");
		checkParameter(mpiId, "mpiId");
		checkParameter(identity, "identity");
		checkParameter(sourceName, "sourceName");
		ResponseEntryDTO result = dao.updatePerson(notificationClientID, domainName, mpiId, identity, sourceName, force, comment, requestConfig);
		logger.info("updatePerson successfully executed");
		return result;
	}

	protected ResponseEntryDTO addPersonInternal(String notificationClientID, String domainName, IdentityInDTO identity, String sourceName, String comment)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("addPerson for domain " + domainName + " and source " + sourceName + " with " + identity
					+ " (notificationClientID = " + notificationClientID + ")");
		}
		else
		{
			logger.info("addPerson (notificationClientID = " + notificationClientID + ")");
		}
		checkParameter(domainName, "domainName");
		checkParameter(identity, "identity");
		checkParameter(sourceName, "sourceName");
		checkMatchingMode(MatchingMode.NO_DECISION, dao.getMatchingMode(domainName), domainName);
		ResponseEntryDTO result = dao.addPerson(notificationClientID, domainName, identity, sourceName, comment);
		logger.info("person successfully added");
		return result;
	}

	protected ResponseEntryDTO setReferenceIdentityInternal(String notificationClientID, String domainName, String mpiId, long identityId, String comment)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("setReferenceIdentity for mpiId " + mpiId + "within domain " + domainName
					+ ", new reference shhould be identity with id " + identityId + ". comment is '" + comment + "' (notificationClientID = " + notificationClientID + ")");
		}
		else
		{
			logger.info("setReferenceIdentity (notificationClientID = " + notificationClientID + ")");
		}
		checkParameter(domainName, "domainName");
		checkParameter(mpiId, "mpiId");
		dao.setReferenceIdentity(notificationClientID, domainName, mpiId, identityId, comment);
		logger.info("reference identity set");
		return null;
	}

	protected void deactivateIdentityInternal(String notificationClientID, long identityId)
			throws MPIException, UnknownObjectException
	{
		logger.info("deactivateIdentity with id " + identityId + " (notificationClientID = " + notificationClientID + ")");
		dao.deactivateIdentity(notificationClientID, identityId);
		logger.info("Identity deactivated");
	}

	protected void deactivatePersonInternal(String notificationClientID, String domainName, String mpiId)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		logger.info("deactivatePerson with mpi id " + mpiId + " within domain " + domainName + " (notificationClientID = " + notificationClientID + ")");
		checkParameter(domainName, "domainName");
		checkParameter(mpiId, "mpiId");
		dao.deactivatePerson(notificationClientID, domainName, mpiId);
		logger.info("Person deactivated");
	}

	protected void deleteIdentityInternal(String notificationClientID, long identityId) throws IllegalOperationException, MPIException, UnknownObjectException
	{
		logger.info("deleteIdentity with id " + identityId + " (notificationClientID = " + notificationClientID + ")");
		dao.deleteIdentity(notificationClientID, identityId);
		logger.info("Identity deleted");
	}

	protected void deletePersonInternal(String notificationClientID, String domainName, String mpiId)
			throws IllegalOperationException, InvalidParameterException, MPIException, UnknownObjectException
	{
		logger.info("deletePerson with mpi id " + mpiId + " within domain " + domainName + " (notificationClientID = " + notificationClientID + ")");
		checkParameter(domainName, "domainName");
		checkParameter(mpiId, "mpiId");
		dao.deletePerson(notificationClientID, domainName, mpiId);
		logger.info("Person deleted");
	}

	protected IdentityOutDTO addContactInternal(String notificationClientID, long identityId, ContactInDTO contactDTO)
			throws DuplicateEntryException, InvalidParameterException, MPIException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("addContact with identity id " + identityId + " and " + contactDTO + " (notificationClientID = " + notificationClientID + ")");
		}
		else
		{
			logger.info("addContact (notificationClientID = " + notificationClientID + ")");
		}
		IdentityOutDTO result = dao.addContact(notificationClientID, identityId, contactDTO);
		logger.info("contact added");
		return result;
	}

	protected void addLocalIdentifierToMPIInternal(String notificationClientID, String domainName, String mpiId, List<IdentifierDTO> localIds)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			StringBuilder sb = new StringBuilder("addLocalIdentifierToMPI for domain ");
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
			logger.info("addLocalIdentifierToMPI (notificationClientID = " + notificationClientID + ")");
		}
		checkParameter(domainName, "domainName");
		checkParameter(mpiId, "mpiId");
		checkParameter(localIds, "localIds");
		dao.addIdentifierToPerson(notificationClientID, domainName, mpiId, localIds);
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
		checkParameter(identifierDTO, "identifier");
		checkParameter(localIds, "localIds");
		dao.addLocalIdentifierToIdentifier(notificationClientID, domainName, identifierDTO, localIds);
		logger.info("local identifier added to identities with the given identifier");
	}

	protected void assignIdentityInternal(String notificationClientID, long possibleMatchId, long winningIdentityId, String comment)
			throws InvalidParameterException, MPIException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("assignIdentity for link id " + possibleMatchId + " with winning id " + winningIdentityId + " with comment "
					+ comment + " (notificationClientID = " + notificationClientID + ")");
		}
		else
		{
			logger.info("assignIdentity (notificationClientID = " + notificationClientID + ")");
		}
		dao.assignIdentity(notificationClientID, possibleMatchId, winningIdentityId, comment);
		logger.info("identities assigned");
	}

	protected void moveIdentitiesForIdentifierToPersonInternal(String notificationClientID, String domainName, IdentifierDTO identifier,
			String mpiId, String comment) throws InvalidParameterException, MPIException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("moveIdentitiesForIdentifierToPerson for domain " + domainName + " and " + identifier + " to mpiId " + mpiId
					+ " with comment " + comment + " (notificationClientID = " + notificationClientID + ")");
		}
		else
		{
			logger.info("moveIdentitiesForIdentifierToPerson (notificationClientID = " + notificationClientID + ")");
		}
		checkParameter(domainName, "domainName");
		checkParameter(identifier, "identifier");
		checkParameter(mpiId, "mpiId");
		checkMatchingMode(MatchingMode.NO_DECISION, dao.getMatchingMode(domainName), domainName);
		dao.moveIdentitiesForIdentifierToPersonNotification(notificationClientID, domainName, identifier, mpiId, comment);
		logger.info("identities moved");
	}
}
