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

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;

import org.emau.icmvc.ttp.epix.common.exception.*;
import org.emau.icmvc.ttp.epix.common.model.ContactInDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentifierDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityInDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityOutDTO;
import org.emau.icmvc.ttp.epix.common.model.MPIRequestDTO;
import org.emau.icmvc.ttp.epix.common.model.MPIResponseDTO;
import org.emau.icmvc.ttp.epix.common.model.RequestConfig;
import org.emau.icmvc.ttp.epix.common.model.ResponseEntryDTO;

/**
 *
 * @author geidell
 *
 */
@WebService
public interface EPIXServiceWithNotification
{
	// ***********************************
	// persons
	// ***********************************

	ResponseEntryDTO requestMPI(
			@XmlElement(required = false) @WebParam(name = "notificationClientID") String notificationClientID,
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "identity") IdentityInDTO identity,
			@XmlElement(required = true) @WebParam(name = "sourceName") String sourceName,
			@XmlElement(required = false) @WebParam(name = "comment") String comment)
			throws InvalidParameterException, MPIException, UnknownObjectException;

	ResponseEntryDTO requestMPIWithConfig(
			@XmlElement(required = false) @WebParam(name = "notificationClientID") String notificationClientID,
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "identity") IdentityInDTO identity,
			@XmlElement(required = true) @WebParam(name = "sourceName") String sourceName,
			@XmlElement(required = false) @WebParam(name = "comment") String comment,
			@XmlElement(required = true) @WebParam(name = "requestConfig") RequestConfig requestConfig)
			throws InvalidParameterException, MPIException, UnknownObjectException;

	MPIResponseDTO requestMPIBatch(
			@XmlElement(required = false) @WebParam(name = "notificationClientID") String notificationClientID,
			@XmlElement(required = true) @WebParam(name = "mpiRequest") MPIRequestDTO mpiRequest)
			throws InvalidParameterException, MPIException, UnknownObjectException;

	ResponseEntryDTO updatePerson(
			@XmlElement(required = false) @WebParam(name = "notificationClientID") String notificationClientID,
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "mpiId") String mpiId,
			@XmlElement(required = true) @WebParam(name = "identity") IdentityInDTO identity,
			@XmlElement(required = true) @WebParam(name = "sourceName") String sourceName,
			@XmlElement(required = true) @WebParam(name = "force") boolean force,
			@XmlElement(required = false) @WebParam(name = "comment") String comment)
			throws InvalidParameterException, MPIException, UnknownObjectException;

	ResponseEntryDTO updatePersonWithConfig(
			@XmlElement(required = false) @WebParam(name = "notificationClientID") String notificationClientID,
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "mpiId") String mpiId,
			@XmlElement(required = true) @WebParam(name = "identity") IdentityInDTO identity,
			@XmlElement(required = true) @WebParam(name = "sourceName") String sourceName,
			@XmlElement(required = true) @WebParam(name = "force") boolean force,
			@XmlElement(required = false) @WebParam(name = "comment") String comment,
			@XmlElement(required = true) @WebParam(name = "requestConfig") RequestConfig requestConfig)
			throws InvalidParameterException, MPIException, UnknownObjectException;

	ResponseEntryDTO addPerson(
			@XmlElement(required = false) @WebParam(name = "notificationClientID") String notificationClientID,
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "identity") IdentityInDTO identity,
			@XmlElement(required = true) @WebParam(name = "sourceName") String sourceName,
			@XmlElement(required = false) @WebParam(name = "comment") String comment)
			throws InvalidParameterException, MPIException, UnknownObjectException;

	void deactivatePerson(
			@XmlElement(required = false) @WebParam(name = "notificationClientID") String notificationClientID,
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "mpiId") String mpiId)
			throws InvalidParameterException, MPIException, UnknownObjectException;

	void deletePerson(
			@XmlElement(required = false) @WebParam(name = "notificationClientID") String notificationClientID,
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "mpiId") String mpiId)
			throws IllegalOperationException, InvalidParameterException, MPIException, UnknownObjectException;

	// ***********************************
	// identities
	// ***********************************

	ResponseEntryDTO setReferenceIdentity(
			@XmlElement(required = false) @WebParam(name = "notificationClientID") String notificationClientID,
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "mpiId") String mpiId,
			@XmlElement(required = true) @WebParam(name = "identityId") long identityId,
			@XmlElement(required = false) @WebParam(name = "comment") String comment)
			throws InvalidParameterException, MPIException, UnknownObjectException;

	void deactivateIdentity(
			@XmlElement(required = false) @WebParam(name = "notificationClientID") String notificationClientID,
			@XmlElement(required = true) @WebParam(name = "identityId") long identityId)
			throws MPIException, UnknownObjectException;

	void deleteIdentity(
			@XmlElement(required = false) @WebParam(name = "notificationClientID") String notificationClientID,
			@XmlElement(required = true) @WebParam(name = "identityId") long identityId)
			throws IllegalOperationException, MPIException, UnknownObjectException;

	// ***********************************
	// contacts
	// ***********************************

	IdentityOutDTO addContact(
			@XmlElement(required = false) @WebParam(name = "notificationClientID") String notificationClientID,
			@XmlElement(required = true) @WebParam(name = "identityId") long identityId,
			@XmlElement(required = true) @WebParam(name = "contact") ContactInDTO contactDTO)
			throws DuplicateEntryException, InvalidParameterException, MPIException, UnknownObjectException;

	// ***********************************
	// identifier
	// ***********************************

	void addLocalIdentifierToMPI(
			@XmlElement(required = false) @WebParam(name = "notificationClientID") String notificationClientID,
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "mpiId") String mpiId,
			@XmlElement(required = true) @WebParam(name = "localIds") List<IdentifierDTO> localIds)
			throws InvalidParameterException, MPIException, UnknownObjectException;

	void addLocalIdentifierToIdentifier(
			@XmlElement(required = false) @WebParam(name = "notificationClientID") String notificationClientID,
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "identifier") IdentifierDTO identifier,
			@XmlElement(required = true) @WebParam(name = "localIds") List<IdentifierDTO> localIds)
			throws InvalidParameterException, MPIException, UnknownObjectException;

	// ***********************************
	// possible matches
	// ***********************************

	void assignIdentity(
			@XmlElement(required = false) @WebParam(name = "notificationClientID") String notificationClientID,
			@XmlElement(required = true) @WebParam(name = "possibleMatchId") long possibleMatchId,
			@XmlElement(required = true) @WebParam(name = "winningIdentityId") long winningIdentityId,
			@XmlElement(required = false) @WebParam(name = "comment") String comment)
			throws InvalidParameterException, MPIException;

	void moveIdentitiesForIdentifierToPerson(
			@XmlElement(required = false) @WebParam(name = "notificationClientID") String notificationClientID,
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "identifier") IdentifierDTO identifier,
			@XmlElement(required = true) @WebParam(name = "mpiId") String mpiId,
			@XmlElement(required = false) @WebParam(name = "comment") String comment)
			throws InvalidParameterException, MPIException, UnknownObjectException;
}
