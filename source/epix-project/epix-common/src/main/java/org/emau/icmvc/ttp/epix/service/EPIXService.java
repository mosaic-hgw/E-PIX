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
import java.util.Map;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;

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
import org.emau.icmvc.ttp.epix.common.model.PersonDTO;
import org.emau.icmvc.ttp.epix.common.model.PossibleMatchDTO;
import org.emau.icmvc.ttp.epix.common.model.PossibleMatchForMPIDTO;
import org.emau.icmvc.ttp.epix.common.model.RequestConfig;
import org.emau.icmvc.ttp.epix.common.model.ResponseEntryDTO;
import org.emau.icmvc.ttp.epix.common.model.enums.IdentityField;
import org.emau.icmvc.ttp.epix.common.model.enums.PersonField;
import org.emau.icmvc.ttp.epix.pdqquery.model.SearchMask;

/**
 *
 * @author Christian Schack, geidell
 *
 */
@WebService
public interface EPIXService
{
	// ***********************************
	// persons
	// ***********************************

	ResponseEntryDTO requestMPI(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "identity") IdentityInDTO identity,
			@XmlElement(required = true) @WebParam(name = "sourceName") String sourceName,
			@XmlElement(required = false) @WebParam(name = "comment") String comment)
			throws InvalidParameterException, MPIException, UnknownObjectException;

	ResponseEntryDTO requestMPIWithConfig(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "identity") IdentityInDTO identity,
			@XmlElement(required = true) @WebParam(name = "sourceName") String sourceName,
			@XmlElement(required = false) @WebParam(name = "comment") String comment,
			@XmlElement(required = true) @WebParam(name = "requestConfig") RequestConfig requestConfig)
			throws InvalidParameterException, MPIException, UnknownObjectException;

	MPIResponseDTO requestMPIBatch(
			@XmlElement(required = true) @WebParam(name = "mpiRequest") MPIRequestDTO mpiRequest)
			throws InvalidParameterException, MPIException, UnknownObjectException;

	List<PersonDTO> getPersonsForDomain(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName)
			throws InvalidParameterException, UnknownObjectException;

	List<PersonDTO> getPersonsForDomainFiltered(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "filter") Map<PersonField, String> filter,
			@XmlElement(required = true) @WebParam(name = "filterIsCaseSensitive") boolean filterIsCaseSensitive)
			throws InvalidParameterException, UnknownObjectException;

	List<PersonDTO> getPersonsForDomainPaginated(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "firstEntry") int firstEntry,
			@XmlElement(required = true) @WebParam(name = "pageSize") int pageSize,
			@XmlElement(required = true) @WebParam(name = "sortField") PersonField sortField,
			@XmlElement(required = true) @WebParam(name = "sortIsAscending") boolean sortIsAscending,
			@XmlElement(required = true) @WebParam(name = "filter") Map<PersonField, String> filter,
			@XmlElement(required = true) @WebParam(name = "filterIsCaseSensitive") boolean filterIsCaseSensitive)
			throws InvalidParameterException, UnknownObjectException;

	PersonDTO getPersonByMPI(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "mpiId") String mpiId)
			throws InvalidParameterException, UnknownObjectException;

	PersonDTO getPersonByLocalIdentifier(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "identifier") IdentifierDTO identifier)
			throws InvalidParameterException, UnknownObjectException;

	PersonDTO getPersonByMultipleLocalIdentifier(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "identifier") List<IdentifierDTO> identifier,
			@XmlElement(required = true) @WebParam(name = "allIdentifierRequired") boolean allIdentifierRequired)
			throws InvalidParameterException, MPIException, UnknownObjectException;

	ResponseEntryDTO updatePerson(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "mpiId") String mpiId,
			@XmlElement(required = true) @WebParam(name = "identity") IdentityInDTO identity,
			@XmlElement(required = true) @WebParam(name = "sourceName") String sourceName,
			@XmlElement(required = true) @WebParam(name = "force") boolean force,
			@XmlElement(required = false) @WebParam(name = "comment") String comment)
			throws InvalidParameterException, MPIException, UnknownObjectException;

	ResponseEntryDTO updatePersonWithConfig(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "mpiId") String mpiId,
			@XmlElement(required = true) @WebParam(name = "identity") IdentityInDTO identity,
			@XmlElement(required = true) @WebParam(name = "sourceName") String sourceName,
			@XmlElement(required = true) @WebParam(name = "force") boolean force,
			@XmlElement(required = false) @WebParam(name = "comment") String comment,
			@XmlElement(required = true) @WebParam(name = "requestConfig") RequestConfig requestConfig)
			throws InvalidParameterException, MPIException, UnknownObjectException;

	ResponseEntryDTO addPerson(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "identity") IdentityInDTO identity,
			@XmlElement(required = true) @WebParam(name = "sourceName") String sourceName,
			@XmlElement(required = false) @WebParam(name = "comment") String comment)
			throws InvalidParameterException, MPIException, UnknownObjectException;

	void deactivatePerson(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "mpiId") String mpiId)
			throws InvalidParameterException, MPIException, UnknownObjectException;

	void deletePerson(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "mpiId") String mpiId)
			throws IllegalOperationException, InvalidParameterException, MPIException, UnknownObjectException;

	void updatePrivacy(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "mpiIds") List<String> mpiIds,
			@XmlElement(required = true) @WebParam(name = "onlyReferenceIdentity") boolean onlyReferenceIdentity)
			throws InvalidParameterException, MPIException, UnknownObjectException;

	// ***********************************
	// identities
	// ***********************************

	List<IdentityOutDTO> getIdentitiesForDomain(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName)
			throws InvalidParameterException, UnknownObjectException;

	List<IdentityOutDTO> getIdentitiesForDomainFiltered(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "filter") Map<IdentityField, String> filter,
			@XmlElement(required = true) @WebParam(name = "filterIsCaseSensitive") boolean filterIsCaseSensitive)
			throws InvalidParameterException, UnknownObjectException;

	List<IdentityOutDTO> getIdentitiesForDomainPaginated(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "firstEntry") int firstEntry,
			@XmlElement(required = true) @WebParam(name = "pageSize") int pageSize,
			@XmlElement(required = true) @WebParam(name = "sortField") IdentityField sortField,
			@XmlElement(required = true) @WebParam(name = "sortIsAscending") boolean sortIsAscending,
			@XmlElement(required = true) @WebParam(name = "filter") Map<IdentityField, String> filter,
			@XmlElement(required = true) @WebParam(name = "filterIsCaseSensitive") boolean filterIsCaseSensitive)
			throws InvalidParameterException, UnknownObjectException;

	/*
	 * public Map<IdentifierDTO, IdentityOutDTO> getReferenceIdentitiesForDomain(
	 *
	 * @XmlElement(required = true) @WebParam(name = "domainName") String domainName) throws InvalidParameterException, UnknownObjectException;
	 */

	ResponseEntryDTO setReferenceIdentity(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "mpiId") String mpiId,
			@XmlElement(required = true) @WebParam(name = "identityId") long identityId,
			@XmlElement(required = false) @WebParam(name = "comment") String comment)
			throws InvalidParameterException, MPIException, UnknownObjectException;

	void deactivateIdentity(
			@XmlElement(required = true) @WebParam(name = "identityId") long identityId)
			throws MPIException, UnknownObjectException;

	void deleteIdentity(
			@XmlElement(required = true) @WebParam(name = "identityId") long identityId)
			throws IllegalOperationException, MPIException, UnknownObjectException;

	// ***********************************
	// contacts
	// ***********************************

	IdentityOutDTO addContact(
			@XmlElement(required = true) @WebParam(name = "identityId") long identityId,
			@XmlElement(required = true) @WebParam(name = "contact") ContactInDTO contactDTO)
			throws DuplicateEntryException, InvalidParameterException, MPIException, UnknownObjectException;

	// ***********************************
	// search
	// ***********************************

	List<PersonDTO> searchPersonsByPDQ(
			@XmlElement(required = true) @WebParam(name = "searchMask") SearchMask searchMask)
			throws InvalidParameterException, MPIException, UnknownObjectException;

	// ***********************************
	// identifier
	// ***********************************

	List<String> getAllMPIFromPersonByMPI(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "mpiId") String mpiId)
			throws InvalidParameterException, MPIException, UnknownObjectException;

	String getMPIForIdentifier(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "identifier") IdentifierDTO identifier)
			throws InvalidParameterException, UnknownObjectException;

	List<IdentifierDTO> getAllIdentifierForMPI(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "mpiId") String mpiId)
			throws InvalidParameterException, MPIException, UnknownObjectException;

	List<IdentifierDTO> getAllIdentifierForIdentifier(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "identifier") IdentifierDTO identifier)
			throws InvalidParameterException, UnknownObjectException;

	void addLocalIdentifierToMPI(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "mpiId") String mpiId,
			@XmlElement(required = true) @WebParam(name = "localIds") List<IdentifierDTO> localIds)
			throws InvalidParameterException, MPIException, UnknownObjectException;

	void addLocalIdentifierToIdentifier(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "identifier") IdentifierDTO identifier,
			@XmlElement(required = true) @WebParam(name = "localIds") List<IdentifierDTO> localIds)
			throws InvalidParameterException, MPIException, UnknownObjectException;

	// ***********************************
	// possible matches
	// ***********************************

	List<PossibleMatchDTO> getPossibleMatchesForDomain(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName)
			throws InvalidParameterException, UnknownObjectException;

	long countPossibleMatchesForDomainFiltered(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "filter") Map<IdentityField, String> filter,
			@XmlElement(required = true) @WebParam(name = "filterIsCaseSensitive") boolean filterIsCaseSensitive)
			throws InvalidParameterException, UnknownObjectException;

	long countPossibleMatchesForDomainFilteredByDefault(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "filter") String filter,
			@XmlElement(required = true) @WebParam(name = "filterIsCaseSensitive") boolean filterIsCaseSensitive,
			@XmlElement(required = true) @WebParam(name = "birthDateFormat") String birthDateFormat,
			@XmlElement(required = true) @WebParam(name = "creationTimeFormat") String creationTimeFormat)
			throws InvalidParameterException, UnknownObjectException;

	List<PossibleMatchDTO> getPossibleMatchesForDomainFiltered(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "sortField") IdentityField sortField,
			@XmlElement(required = true) @WebParam(name = "sortIsAscending") boolean sortIsAscending,
			@XmlElement(required = true) @WebParam(name = "filter") Map<IdentityField, String> filter,
			@XmlElement(required = true) @WebParam(name = "filterIsCaseSensitive") boolean filterIsCaseSensitive)
			throws InvalidParameterException, UnknownObjectException;

	List<PossibleMatchDTO> getPossibleMatchesForDomainFilteredAndPaginated(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "firstEntry") int firstEntry,
			@XmlElement(required = true) @WebParam(name = "pageSize") int pageSize,
			@XmlElement(required = true) @WebParam(name = "sortField") IdentityField sortField,
			@XmlElement(required = true) @WebParam(name = "sortIsAscending") boolean sortIsAscending,
			@XmlElement(required = true) @WebParam(name = "filter") Map<IdentityField, String> filter,
			@XmlElement(required = true) @WebParam(name = "filterIsCaseSensitive") boolean filterIsCaseSensitive)
			throws InvalidParameterException, UnknownObjectException;

	List<PossibleMatchDTO> getPossibleMatchesForDomainFilteredByDefaultAndPaginated(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "firstEntry") int firstEntry,
			@XmlElement(required = true) @WebParam(name = "pageSize") int pageSize,
			@XmlElement(required = true) @WebParam(name = "filter") String filter,
			@XmlElement(required = true) @WebParam(name = "filterIsCaseSensitive") boolean filterIsCaseSensitive,
			@XmlElement(required = true) @WebParam(name = "birthDateFormat") String birthDateFormat,
			@XmlElement(required = true) @WebParam(name = "creationTimeFormat") String creationTimeFormat)
			throws InvalidParameterException, UnknownObjectException;

	List<PossibleMatchForMPIDTO> getPossibleMatchesForPerson(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "mpiId") String mpiId)
			throws InvalidParameterException, UnknownObjectException;

	void removePossibleMatches(
			@XmlElement(required = true) @WebParam(name = "possibleMatchIds") List<Long> possibleMatchIds,
			@XmlElement(required = true) @WebParam(name = "comment") String comment)
			throws InvalidParameterException, MPIException;

	void removePossibleMatch(
			@XmlElement(required = true) @WebParam(name = "possibleMatchId") long possibleMatchId,
			@XmlElement(required = true) @WebParam(name = "comment") String comment)
			throws InvalidParameterException, MPIException;

	void assignIdentity(
			@XmlElement(required = true) @WebParam(name = "possibleMatchId") long possibleMatchId,
			@XmlElement(required = true) @WebParam(name = "winningIdentityId") long winningIdentityId,
			@XmlElement(required = false) @WebParam(name = "comment") String comment)
			throws InvalidParameterException, MPIException;

	void moveIdentitiesForIdentifierToPerson(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "identifier") IdentifierDTO identifier,
			@XmlElement(required = true) @WebParam(name = "mpiId") String mpiId,
			@XmlElement(required = false) @WebParam(name = "comment") String comment)
			throws InvalidParameterException, MPIException, UnknownObjectException;

	PossibleMatchDTO externalPossibleMatchForPerson(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "mpiId") String mpiId,
			@XmlElement(required = true) @WebParam(name = "aliasMpiId") String aliasMpiId)
			throws DuplicateEntryException, InvalidParameterException, MPIException, UnknownObjectException;

	PossibleMatchDTO externalPossibleMatchForIdentity(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "identityId") long identityId,
			@XmlElement(required = true) @WebParam(name = "aliasIdentityId") long aliasIdentityId)
			throws DuplicateEntryException, InvalidParameterException, MPIException, UnknownObjectException;
}
