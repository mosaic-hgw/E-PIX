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
import org.emau.icmvc.ttp.epix.common.exception.InvalidParameterException;
import org.emau.icmvc.ttp.epix.common.exception.MPIException;
import org.emau.icmvc.ttp.epix.common.exception.ObjectInUseException;
import org.emau.icmvc.ttp.epix.common.exception.UnknownObjectException;
import org.emau.icmvc.ttp.epix.common.model.ContactHistoryDTO;
import org.emau.icmvc.ttp.epix.common.model.DomainDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentifierDomainDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentifierHistoryDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityHistoryDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityOutDTO;
import org.emau.icmvc.ttp.epix.common.model.PersonDTO;
import org.emau.icmvc.ttp.epix.common.model.PersonHistoryDTO;
import org.emau.icmvc.ttp.epix.common.model.PossibleMatchHistoryDTO;
import org.emau.icmvc.ttp.epix.common.model.SourceDTO;
import org.emau.icmvc.ttp.epix.common.model.config.ConfigurationContainer;
import org.emau.icmvc.ttp.epix.common.model.config.ReasonDTO;
import org.emau.icmvc.ttp.epix.common.model.enums.IdentityField;
import org.emau.icmvc.ttp.epix.common.utils.PaginationConfig;

/**
 *
 * @author geidell
 *
 */
@WebService
public interface EPIXManagementService
{
	// ***********************************
	// identifier domain
	// ***********************************
	IdentifierDomainDTO addIdentifierDomain(
			@XmlElement(required = true) @WebParam(name = "identifierDomain") IdentifierDomainDTO identifierDomain)
			throws DuplicateEntryException, InvalidParameterException, MPIException;

	IdentifierDomainDTO getIdentifierDomain(@XmlElement(required = true) @WebParam(name = "identifierDomainName") String identifierDomainName)
			throws UnknownObjectException, InvalidParameterException;

	List<IdentifierDomainDTO> getIdentifierDomains();

	IdentifierDomainDTO updateIdentifierDomain(
			@XmlElement(required = true) @WebParam(name = "identifierDomain") IdentifierDomainDTO identifierDomain)
			throws InvalidParameterException, MPIException, UnknownObjectException;

	void deleteIdentifierDomain(@XmlElement(required = true) @WebParam(name = "identifierDomainName") String identifierDomainName)
			throws InvalidParameterException, MPIException, ObjectInUseException, UnknownObjectException;

	// ***********************************
	// domain
	// ***********************************
	DomainDTO addDomain(@XmlElement(required = true) @WebParam(name = "domain") DomainDTO domain)
			throws DuplicateEntryException, InvalidParameterException, MPIException, UnknownObjectException;

	DomainDTO getDomain(@XmlElement(required = true) @WebParam(name = "domainName") String domainName)
			throws UnknownObjectException, InvalidParameterException;

	List<DomainDTO> getDomains();

	DomainDTO updateDomain(@XmlElement(required = true) @WebParam(name = "domain") DomainDTO domain)
			throws InvalidParameterException, MPIException, ObjectInUseException, UnknownObjectException;

	DomainDTO updateDomainInUse(@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "label") String label,
			@XmlElement(required = true) @WebParam(name = "description") String description)
			throws InvalidParameterException, MPIException, UnknownObjectException;

	void deleteDomain(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "force") boolean force)
			throws InvalidParameterException, MPIException, ObjectInUseException, UnknownObjectException;


	// ***********************************
	// source
	// ***********************************
	SourceDTO addSource(@XmlElement(required = true) @WebParam(name = "source") SourceDTO source)
			throws DuplicateEntryException, InvalidParameterException, MPIException;

	SourceDTO getSource(@XmlElement(required = true) @WebParam(name = "sourceName") String sourceName)
			throws UnknownObjectException, InvalidParameterException;

	List<SourceDTO> getSources();

	SourceDTO updateSource(@XmlElement(required = true) @WebParam(name = "source") SourceDTO source)
			throws InvalidParameterException, MPIException, UnknownObjectException;

	void deleteSource(@XmlElement(required = true) @WebParam(name = "sourceName") String sourceName)
			throws InvalidParameterException, MPIException, ObjectInUseException, UnknownObjectException;

	// ***********************************
	// getByDBId
	// ***********************************
	PersonDTO getPersonById(@XmlElement(required = true) @WebParam(name = "id") long id) throws UnknownObjectException;

	IdentityOutDTO getIdentityById(@XmlElement(required = true) @WebParam(name = "id") long id) throws UnknownObjectException;

	// ***********************************
	// deactivation
	// ***********************************
	List<PersonDTO> getDeactivatedPersonsForDomain(@XmlElement(required = true) @WebParam(name = "domainName") String domainName)
			throws InvalidParameterException, UnknownObjectException;

	List<IdentityOutDTO> getDeacticatedIdentitiesForDomain(@XmlElement(required = true) @WebParam(name = "domainName") String domainName)
			throws InvalidParameterException, UnknownObjectException;

	// ***********************************
	// history
	// ***********************************
	List<PersonHistoryDTO> getHistoryForPerson(@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "mpiId") String mpiId) throws InvalidParameterException, UnknownObjectException;

	List<IdentityHistoryDTO> getIdentityHistoriesForDomain(@XmlElement(required = true) @WebParam(name = "domainName") String domainName)
			throws InvalidParameterException, UnknownObjectException;

	List<IdentityHistoryDTO> getIdentityHistoriesForDomainFiltered(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "filter") Map<IdentityField, String> filter,
			@XmlElement(required = true) @WebParam(name = "filterIsCaseSensitive") boolean filterIsCaseSensitive)
			throws InvalidParameterException, UnknownObjectException;

	/**
	 * Returns matching {@link IdentityHistoryDTO} entries.
	 * If {@link IdentityField#NONE} is the only key in the filter map, then search for the corresponding
	 * pattern in all required fields (as defined in the configuration container) linked by OR (disjunction),
	 * otherwise search in all given fields for the respective pattern linked by AND (conjunction).
	 *
	 * @param domainName the name of the domain
	 * @param paginationConfig the pagination configuration
	 * @return matching {@link IdentityHistoryDTO} entries.
	 * @throws UnknownObjectException for a wrong domain name
	 */
	List<IdentityHistoryDTO> getIdentityHistoriesForDomainPaginated(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "paginationConfig") PaginationConfig paginationConfig)
			throws InvalidParameterException, UnknownObjectException;

	/**
	 * Counts matching {@link IdentityHistoryDTO} entries.
	 * If {@link IdentityField#NONE} is the only key in the filter map, then search for the corresponding
	 * pattern in all required fields (as defined in the configuration container) linked by OR (disjunction),
	 * otherwise search in all given fields for the respective pattern linked by AND (conjunction).
	 *
	 * @param domainName the name of the domain
	 * @param paginationConfig the pagination configuration
	 * @return number of matching {@link IdentityHistoryDTO} entries.
	 * @throws UnknownObjectException for a wrong domain name
	 */
	long countIdentityHistoriesForDomain(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "paginationConfig") PaginationConfig paginationConfig)
			throws InvalidParameterException, UnknownObjectException;

	/**
	 * Counts matching {@link PossibleMatchHistoryDTO} entries.
	 * w.r.t the given domain and pagination config.
	 * <p>
	 * Currently only the domain and the solution filter will be used for the search!
	 * A later implementation also should respect the identity filter map.
	 * <p>
	 *
	 * @param domainName the name of the domain
	 * @param paginationConfig the pagination configuration
	 * @return number of matching {@link PossibleMatchHistoryDTO} entries
	 * @throws UnknownObjectException for a wrong domain name
	 */
	long countPossibleMatchHistoriesForDomain(
			@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "paginationConfig") PaginationConfig paginationConfig)
			throws UnknownObjectException, InvalidParameterException;

	List<IdentityHistoryDTO> getHistoryForIdentity(@XmlElement(required = true) @WebParam(name = "identityId") long identityId) throws UnknownObjectException;

	List<ContactHistoryDTO> getHistoryForContact(@XmlElement(required = true) @WebParam(name = "contactId") long contactId) throws UnknownObjectException;

	List<IdentifierHistoryDTO> getHistoryForIdentifier(
			@XmlElement(required = true) @WebParam(name = "identifierDomainName") String identifierDomainName,
			@XmlElement(required = true) @WebParam(name = "identifierValue") String value) throws UnknownObjectException;

	List<PossibleMatchHistoryDTO> getPossibleMatchHistoryForPerson(@XmlElement(required = true) @WebParam(name = "domainName") String domainName,
			@XmlElement(required = true) @WebParam(name = "mpiId") String mpiId) throws InvalidParameterException, UnknownObjectException;

	List<PossibleMatchHistoryDTO> getPossibleMatchHistoryForUpdatedIdentity(@XmlElement(required = true) @WebParam(name = "updatedIdentityId") long updatedIdentityId)
			throws InvalidParameterException, UnknownObjectException;

	List<PossibleMatchHistoryDTO> getPossibleMatchHistoryByIdentity(@XmlElement(required = true) @WebParam(name = "identityId") long identityId)
			throws InvalidParameterException, UnknownObjectException;

	// ***********************************
	// config
	// ***********************************
	ConfigurationContainer getConfigurationForDomain(@XmlElement(required = true) @WebParam(name = "domainName") String domainName)
			throws InvalidParameterException, UnknownObjectException;

	List<IdentityHistoryDTO> getIdentityHistoryByPersonId(
			@XmlElement(required = true) @WebParam(name = "personId") Long personId)
			throws UnknownObjectException;

	List<ReasonDTO> getDefinedDeduplicationReasons(@XmlElement(required = true) @WebParam(name = "domainName") String domainName)
			throws InvalidParameterException, UnknownObjectException;

	/**
	 * Parses a matching configuration encoded as XML into a {@link ConfigurationContainer}.
	 * @param xml the configuration XML
	 * @return a {@link ConfigurationContainer} describing the configuration
	 * @throws InvalidParameterException if the XML is null or empty
	 * @throws MPIException if parsing failed
	 */
	ConfigurationContainer parseMatchingConfiguration(
			@XmlElement(required = true) @WebParam(name = "xml") String xml)
			throws InvalidParameterException, MPIException;

	/**
	 * Parses a matching configuration encoded as XML into a {@link ConfigurationContainer}.
	 * @param config a {@link ConfigurationContainer} describing the configuration
	 * @return the configuration as XML
	 * @throws InvalidParameterException if the config is null
	 * @throws MPIException if encoding failed
	 */
	String encodeMatchingConfiguration(
			@XmlElement(required = true) @WebParam(name = "config") ConfigurationContainer config)
			throws InvalidParameterException, MPIException;
}
