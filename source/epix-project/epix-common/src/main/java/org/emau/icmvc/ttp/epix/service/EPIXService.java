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
import org.emau.icmvc.ttp.epix.common.model.enums.IdentifierDeletionResult;
import org.emau.icmvc.ttp.epix.common.model.enums.IdentityField;
import org.emau.icmvc.ttp.epix.common.model.enums.PersonField;
import org.emau.icmvc.ttp.epix.common.model.enums.PossibleMatchPriority;
import org.emau.icmvc.ttp.epix.common.utils.PaginationConfig;
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

	/**
	 * @deprecated use getActivePersonsForDomain (name change for clarity reasons)
	 */
	@Deprecated
	List<PersonDTO> getPersonsForDomain(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName)
			throws InvalidParameterException, UnknownObjectException;

	List<PersonDTO> getActivePersonsForDomain(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName)
			throws InvalidParameterException, UnknownObjectException;

	/**
	 * @deprecated use getActivePersonsForDomainFiltered (name change for clarity reasons)
	 */
	@Deprecated
	List<PersonDTO> getPersonsForDomainFiltered(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "filter") Map<PersonField, String> filter,
			@XmlElement(required = true) @WebParam(name = "filterIsCaseSensitive") boolean filterIsCaseSensitive)
			throws InvalidParameterException, UnknownObjectException;

	List<PersonDTO> getActivePersonsForDomainFiltered(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "filter") Map<PersonField, String> filter,
			@XmlElement(required = true) @WebParam(name = "filterIsCaseSensitive") boolean filterIsCaseSensitive)
			throws InvalidParameterException, UnknownObjectException;

	/**
	 * @deprecated use getActivePersonsForDomainPaginated (name change for clarity reasons)
	 */
	@Deprecated
	List<PersonDTO> getPersonsForDomainPaginated(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "firstEntry") int firstEntry,
			@XmlElement(required = true) @WebParam(name = "pageSize") int pageSize,
			@XmlElement(required = true) @WebParam(name = "sortField") PersonField sortField,
			@XmlElement(required = true) @WebParam(name = "sortIsAscending") boolean sortIsAscending,
			@XmlElement(required = true) @WebParam(name = "filter") Map<PersonField, String> filter,
			@XmlElement(required = true) @WebParam(name = "filterIsCaseSensitive") boolean filterIsCaseSensitive)
			throws InvalidParameterException, UnknownObjectException;

	List<PersonDTO> getActivePersonsForDomainPaginated(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "firstEntry") int firstEntry,
			@XmlElement(required = true) @WebParam(name = "pageSize") int pageSize,
			@XmlElement(required = true) @WebParam(name = "sortField") PersonField sortField,
			@XmlElement(required = true) @WebParam(name = "sortIsAscending") boolean sortIsAscending,
			@XmlElement(required = true) @WebParam(name = "filter") Map<PersonField, String> filter,
			@XmlElement(required = true) @WebParam(name = "filterIsCaseSensitive") boolean filterIsCaseSensitive)
			throws InvalidParameterException, UnknownObjectException;

	/**
	 * @deprecated use countActivePersonsForDomainFiltered (name change for clarity reasons)
	 */
	@Deprecated
	long countPersonsForDomainFiltered(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "filter") Map<PersonField, String> filter,
			@XmlElement(required = true) @WebParam(name = "filterIsCaseSensitive") boolean filterIsCaseSensitive)
			throws InvalidParameterException, UnknownObjectException;

	long countActivePersonsForDomainFiltered(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "filter") Map<PersonField, String> filter,
			@XmlElement(required = true) @WebParam(name = "filterIsCaseSensitive") boolean filterIsCaseSensitive)
			throws InvalidParameterException, UnknownObjectException;

	/**
	 * returns the person (may be deactivated) with the given mpiId.
	 *
	 * @param domainName
	 * @param mpiId
	 * @return
	 * @throws InvalidParameterException
	 * @throws UnknownObjectException
	 */
	PersonDTO getPersonByFirstMPI(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "mpiId") String mpiId)
			throws InvalidParameterException, UnknownObjectException;

	/**
	 * returns the persons (may be deactivated) with the given mpiIds.
	 *
	 * @param domainName
	 * @param mpiIds
	 * @return
	 * @throws InvalidParameterException
	 * @throws UnknownObjectException
	 */
	List<PersonDTO> getPersonsByFirstMPIBatch(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "mpiIds") List<String> mpiIds)
			throws InvalidParameterException, UnknownObjectException;

	/**
	 * @deprecated use getActivePersonByMPI (name change for clarity reasons)
	 */
	@Deprecated
	PersonDTO getPersonByMPI(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "mpiId") String mpiId)
			throws InvalidParameterException, UnknownObjectException;

	/**
	 * returns the active person which is currently associated with the given mpiId. this could be a person with another mpiId as first mpi (-> merge).
	 *
	 * @param domainName
	 * @param mpiId
	 * @return
	 * @throws InvalidParameterException
	 * @throws UnknownObjectException
	 */
	PersonDTO getActivePersonByMPI(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "mpiId") String mpiId)
			throws InvalidParameterException, UnknownObjectException;

	/**
	 * @deprecated use getActivePersonsByMPIBatch (name change for clarity reasons)
	 */
	@Deprecated
	List<PersonDTO> getPersonsByMPIBatch(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "mpiIds") List<String> mpiIds)
			throws InvalidParameterException, UnknownObjectException;

	/**
	 * returns the active persons which are currently associated with the given mpiIds. this could be persons with another mpiId as first mpi (-> merge).
	 *
	 * @param domainName
	 * @param mpiIds
	 * @return
	 * @throws InvalidParameterException
	 * @throws UnknownObjectException
	 */
	List<PersonDTO> getActivePersonsByMPIBatch(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "mpiIds") List<String> mpiIds)
			throws InvalidParameterException, UnknownObjectException;

	/**
	 * @deprecated use getActivePersonByLocalIdentifier (name change for clarity reasons)
	 */
	@Deprecated
	PersonDTO getPersonByLocalIdentifier(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "identifier") IdentifierDTO identifier)
			throws InvalidParameterException, UnknownObjectException;

	PersonDTO getActivePersonByLocalIdentifier(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "identifier") IdentifierDTO identifier)
			throws InvalidParameterException, UnknownObjectException;

	/**
	 * @deprecated use getActivePersonByMultipleLocalIdentifier (name change for clarity reasons)
	 */
	@Deprecated
	PersonDTO getPersonByMultipleLocalIdentifier(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "identifier") List<IdentifierDTO> identifier,
			@XmlElement(required = true) @WebParam(name = "allIdentifierRequired") boolean allIdentifierRequired)
			throws InvalidParameterException, MPIException, UnknownObjectException;

	PersonDTO getActivePersonByMultipleLocalIdentifier(
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

	long countIdentitiesForDomainFiltered(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
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

	void deactivateContact(
			@XmlElement(required = true) @WebParam(name = "contactId") long contactId)
			throws UnknownObjectException;

	void deleteContact(
			@XmlElement(required = true) @WebParam(name = "contactId") long contactId)
			throws IllegalOperationException, UnknownObjectException;

	// ***********************************
	// search
	// ***********************************

	List<PersonDTO> searchPersonsByPDQ(
			@XmlElement(required = true) @WebParam(name = "searchMask") SearchMask searchMask)
			throws InvalidParameterException, MPIException, UnknownObjectException;

	// ***********************************
	// identifier
	// ***********************************

	/**
	 * @deprecated use getAllMPIFromActivePersonByMPI (name change for clarity reasons)
	 */
	@Deprecated
	List<String> getAllMPIFromPersonByMPI(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "mpiId") String mpiId)
			throws InvalidParameterException, MPIException, UnknownObjectException;

	List<String> getAllMPIFromActivePersonByMPI(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "mpiId") String mpiId)
			throws InvalidParameterException, MPIException, UnknownObjectException;

	String getMPIForIdentifier(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "identifier") IdentifierDTO identifier)
			throws InvalidParameterException, UnknownObjectException;

	/**
	 * @deprecated use getAllIdentifierForAcivePersonWithMPI (name change for clarity reasons)
	 */
	@Deprecated
	List<IdentifierDTO> getAllIdentifierForMPI(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "mpiId") String mpiId)
			throws InvalidParameterException, MPIException, UnknownObjectException;

	List<IdentifierDTO> getAllIdentifierForAcivePersonWithMPI(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "mpiId") String mpiId)
			throws InvalidParameterException, MPIException, UnknownObjectException;

	List<IdentifierDTO> getAllIdentifierForIdentifier(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "identifier") IdentifierDTO identifier)
			throws InvalidParameterException, UnknownObjectException;

	/**
	 * @deprecated use addLocalIdentifierToActivePersonWithMPI (name change for clarity reasons)
	 */
	@Deprecated
	void addLocalIdentifierToMPI(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "mpiId") String mpiId,
			@XmlElement(required = true) @WebParam(name = "localIds") List<IdentifierDTO> localIds)
			throws InvalidParameterException, MPIException, UnknownObjectException;

	void addLocalIdentifierToActivePersonWithMPI(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "mpiId") String mpiId,
			@XmlElement(required = true) @WebParam(name = "localIds") List<IdentifierDTO> localIds)
			throws InvalidParameterException, MPIException, UnknownObjectException;

	void addLocalIdentifierToIdentifier(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "identifier") IdentifierDTO identifier,
			@XmlElement(required = true) @WebParam(name = "localIds") List<IdentifierDTO> localIds)
			throws InvalidParameterException, MPIException, UnknownObjectException;

	/**
	 * Removes identifiers in the given domain from the associated persons' identities and from the DB
	 * if it is not used in other domains. An identifier cannot be deleted if its identifier domain is the
	 * MPI domain of the given domain.
	 *
	 * @param domainName
	 *            the name of the domain to delete the local identifier in
	 * @param localIds
	 *            the spec of the local identifiers to delete
	 * @return a map with the identifiers and their deletion result
	 * @throws UnknownObjectException
	 *             if domain does not exist
	 */
	Map<IdentifierDTO, IdentifierDeletionResult> removeLocalIdentifier(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "localIds") List<IdentifierDTO> localIds)
			throws InvalidParameterException, MPIException, UnknownObjectException;

	// ***********************************
	// possible matches
	// ***********************************

	/**
	 * Returns all possible matches for the given domain.
	 *
	 * @param domainName
	 *            the name of the domain
	 */
	List<PossibleMatchDTO> getPossibleMatchesForDomain(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName)
			throws InvalidParameterException, UnknownObjectException;

	/**
	 * Counts all possible matches for the given domain.
	 *
	 * @param domainName
	 *            the name of the domain
	 */
	long countPossibleMatchesForDomain(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName)
			throws InvalidParameterException, UnknownObjectException;

	/**
	 * Returns matching {@link PossibleMatchDTO} entries.
	 * If {@link IdentityField#NONE} is the only key in the filter map, then search for the corresponding
	 * pattern in all required fields (as defined in the configuration container) linked by OR (disjunction),
	 * otherwise search in all given fields for the respective pattern.
	 *
	 * @param domainName
	 *            the name of the domain
	 * @param paginationConfig
	 *            the pagination configuration
	 * @return matching {@link PossibleMatchDTO} entries.
	 * @throws UnknownObjectException
	 *             for a wrong domain name
	 */
	List<PossibleMatchDTO> getPossibleMatchesForDomainFiltered(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "paginationConfig") PaginationConfig paginationConfig)
			throws InvalidParameterException, UnknownObjectException;

	/**
	 * Counts matching {@link PossibleMatchDTO} entries.
	 * If {@link IdentityField#NONE} is the only key in the filter map, then search for the corresponding
	 * pattern in all required fields (as defined in the configuration container) linked by OR (disjunction),
	 * otherwise search in all given fields for the respective pattern linked by AND (conjunction).
	 *
	 * @param domainName
	 *            the name of the domain
	 * @param paginationConfig
	 *            the pagination configuration
	 * @return number of matching {@link PossibleMatchDTO} entries.
	 * @throws UnknownObjectException
	 *             for a wrong domain name
	 */
	long countPossibleMatchesForDomainFiltered(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "paginationConfig") PaginationConfig paginationConfig)
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

	/**
	 * Marks a possible match as postponed (or not).
	 *
	 * @param linkId
	 *            the link ID
	 * @return true if the postponed status actually has changed
	 * @throws InvalidParameterException
	 *             for invalid parameters
	 * @throws MPIException
	 *             for problems with MPI
	 */
	boolean prioritizePossibleMatch(
			@XmlElement(required = true) @WebParam(name = "linkId") long linkId,
			@XmlElement(required = true) @WebParam(name = "priority") PossibleMatchPriority priority)
			throws InvalidParameterException, MPIException;

	void assignIdentity(
			@XmlElement(required = true) @WebParam(name = "possibleMatchId") long possibleMatchId,
			@XmlElement(required = true) @WebParam(name = "winningIdentityId") long winningIdentityId,
			@XmlElement(required = false) @WebParam(name = "comment") String comment)
			throws InvalidParameterException, MPIException, UnknownObjectException;

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
