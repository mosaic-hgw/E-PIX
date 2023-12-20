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

import java.util.List;
import java.util.Map;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.emau.icmvc.ttp.epix.common.exception.DuplicateEntryException;
import org.emau.icmvc.ttp.epix.common.exception.IllegalOperationException;
import org.emau.icmvc.ttp.epix.common.exception.InvalidParameterException;
import org.emau.icmvc.ttp.epix.common.exception.MPIException;
import org.emau.icmvc.ttp.epix.common.exception.UnknownObjectException;
import org.emau.icmvc.ttp.epix.common.model.ContactInDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentifierDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityInDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityOutDTO;
import org.emau.icmvc.ttp.epix.common.model.MPIRequestDTO;
import org.emau.icmvc.ttp.epix.common.model.MPIResponseDTO;
import org.emau.icmvc.ttp.epix.common.model.RequestConfig;
import org.emau.icmvc.ttp.epix.common.model.ResponseEntryDTO;
import org.emau.icmvc.ttp.epix.common.model.enums.IdentifierDeletionResult;
import org.jboss.ejb3.annotation.TransactionTimeout;

/**
 * @author moser
 */
@WebService(name = "epixServiceWithNotification")
@Remote({ EPIXServiceWithNotification.class })
@SOAPBinding(style = SOAPBinding.Style.RPC)
@Stateless
public class EPIXServiceWithNotificationImpl extends EpixServiceBase implements EPIXServiceWithNotification
{
	@Override
	public ResponseEntryDTO requestMPI(String notificationClientID, String domainName, IdentityInDTO identity, String sourceName, String comment)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		return requestMPIInternal(notificationClientID, domainName, identity, sourceName, comment, null);
	}

	@Override
	public ResponseEntryDTO requestMPIWithConfig(String notificationClientID, String domainName, IdentityInDTO identity,
			String sourceName, String comment, RequestConfig requestConfig)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		return requestMPIInternal(notificationClientID, domainName, identity, sourceName, comment, requestConfig);
	}

	@TransactionTimeout(3600)
	@Override
	public MPIResponseDTO requestMPIBatch(String notificationClientID, MPIRequestDTO mpiRequest)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		return requestMPIBatchInternal(notificationClientID, mpiRequest);
	}

	@Override
	public ResponseEntryDTO updatePerson(String notificationClientID, String domainName, String mpiId, IdentityInDTO identity,
			String sourceName, boolean force, String comment)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		return updatePersonInternal(notificationClientID, domainName, mpiId, identity, sourceName, force, comment, null);
	}

	@Override
	public ResponseEntryDTO updatePersonWithConfig(String notificationClientID, String domainName, String mpiId, IdentityInDTO identity,
			String sourceName, boolean force, String comment, RequestConfig requestConfig)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		return updatePersonInternal(notificationClientID, domainName, mpiId, identity, sourceName, force, comment, requestConfig);
	}

	@Override
	public ResponseEntryDTO addPerson(String notificationClientID, String domainName, IdentityInDTO identity, String sourceName, String comment)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		return addPersonInternal(notificationClientID, domainName, identity, sourceName, comment);
	}

	@Override
	public ResponseEntryDTO setReferenceIdentity(String notificationClientID, String domainName, String mpiId, long identityId, String comment)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		return setReferenceIdentityInternal(notificationClientID, domainName, mpiId, identityId, comment);
	}

	@Override
	public void deactivateIdentity(String notificationClientID, long identityId)
			throws MPIException, UnknownObjectException
	{
		deactivateIdentityInternal(notificationClientID, identityId);
	}

	@Override
	public void deactivatePerson(String notificationClientID, String domainName, String mpiId)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		deactivatePersonInternal(notificationClientID, domainName, mpiId);
	}

	@Override
	public void deleteIdentity(String notificationClientID, long identityId)
			throws IllegalOperationException, MPIException, UnknownObjectException
	{
		deleteIdentityInternal(notificationClientID, identityId);
	}

	@Override
	public void deletePerson(String notificationClientID, String domainName, String mpiId)
			throws IllegalOperationException, InvalidParameterException, MPIException, UnknownObjectException
	{
		deletePersonInternal(notificationClientID, domainName, mpiId);
	}

	@Override
	public IdentityOutDTO addContact(String notificationClientID, long identityId, ContactInDTO contactDTO)
			throws DuplicateEntryException, InvalidParameterException, MPIException, UnknownObjectException
	{
		return addContactInternal(notificationClientID, identityId, contactDTO);
	}

	@Override
	public void addLocalIdentifierToMPI(String notificationClientID, String domainName, String mpiId, List<IdentifierDTO> localIds)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		addLocalIdentifierToActivePersonWithMPIInternal(notificationClientID, domainName, mpiId, localIds);
	}

	@Override
	public void addLocalIdentifierToIdentifier(String notificationClientID, String domainName, IdentifierDTO identifierDTO, List<IdentifierDTO> localIds)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		addLocalIdentifierToIdentifierInternal(notificationClientID, domainName, identifierDTO, localIds);
	}

	@Override
	public Map<IdentifierDTO, IdentifierDeletionResult> removeLocalIdentifier(String notificationClientID, String domainName, List<IdentifierDTO> localIds)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		return removeLocalIdentifierInternal(notificationClientID, domainName, localIds);
	}

	@Override
	public void assignIdentity(String notificationClientID, long possibleMatchId, long winningIdentityId, String comment)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		assignIdentityInternal(notificationClientID, possibleMatchId, winningIdentityId, comment);
	}

	@Override
	public void moveIdentitiesForIdentifierToPerson(String notificationClientID, String domainName, IdentifierDTO identifier, String mpiId, String comment)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		moveIdentitiesForIdentifierToPersonInternal(notificationClientID, domainName, identifier, mpiId, comment);
	}
}
