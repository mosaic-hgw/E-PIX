package org.emau.icmvc.ttp.epix.service;

/*-
 * ###license-information-start###
 * E-PIX - Enterprise Patient Identifier Cross-referencing
 * __
 * Copyright (C) 2009 - 2025 Trusted Third Party of the University Medicine Greifswald
 * 							kontakt-ths@uni-greifswald.de
 * 
 * 							concept and implementation
 * 							l.geidel,c.schack, d.langner, g.koetzschke
 * 
 * 							web client
 * 							a.blumentritt, f.m. moser
 * 
 * 							docker
 * 							r.schuldt, f.m. moser
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
import java.util.function.Supplier;

import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;
import org.apache.commons.lang3.StringUtils;
import org.emau.icmvc.ttp.deduplication.config.model.MatchingConfiguration;
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
import org.emau.icmvc.ttp.epix.persistence.PublicDAO;

/**
 * @author Christian Schack, geidell
 */
@WebService(name = "epixManagementService")
@SOAPBinding(style = SOAPBinding.Style.RPC)
@Stateless
@Remote(value = EPIXManagementService.class)
public class EPIXManagementServiceImpl extends EpixServiceBase implements EPIXManagementService
{

	private static final String FOUND_POSSIBLE_MATCH_HISTORY_ENTRIES = "found {} possible match history entries";
	private static final String FOUND_IDENTITY_HISTORY_ENTRIES = "found {} identity history entries";

	public EPIXManagementServiceImpl()
	{
		logger.debug("creating epix management service");
	}

	@Override
	public IdentifierDomainDTO addIdentifierDomain(IdentifierDomainDTO identifierDomain)
			throws DuplicateEntryException, InvalidParameterException, MPIException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("addIdentifierDomain {}", identifierDomain);
		}
		else
		{
			logger.info("addIdentifierDomain");
		}
		checkParameter(identifierDomain, "identifierDomain");
		checkParameter(identifierDomain.getName(), "identifierDomain.getName()");
		IdentifierDomainDTO result = dao.addIdentifierDomain(identifierDomain);
		logger.info("identifier domain added");
		return result;
	}

	@Override
	public IdentifierDomainDTO getIdentifierDomain(String identifierDomainName) throws UnknownObjectException, InvalidParameterException
	{
		logger.debug("getIdentifierDomain with name {}", identifierDomainName);
		checkParameter(identifierDomainName, "identifierDomainName");
		IdentifierDomainDTO result = dao.getIdentifierDomain(identifierDomainName);
		logger.info("returning found identifier domain");
		return result;
	}

	@Override
	public List<IdentifierDomainDTO> getIdentifierDomains()
	{
		logger.debug("getAllIdentifierDomains");
		List<IdentifierDomainDTO> result = dao.getIdentifierDomains();
		logger.debug("found {} identifier domains", result.size());
		return result;
	}

	@Override
	public IdentifierDomainDTO updateIdentifierDomain(IdentifierDomainDTO identifierDomain)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("updateIdentifierDomain with {}", identifierDomain);
		}
		else
		{
			logger.info("updateIdentifierDomain");
		}
		checkParameter(identifierDomain, "identifierDomain");
		checkParameter(identifierDomain.getName(), "identifierDomain.getName()");
		IdentifierDomainDTO result = dao.updateIdentifierDomain(identifierDomain);
		logger.info("identifier domain updated");
		return result;
	}

	@Override
	public void deleteIdentifierDomain(String identifierDomainName)
			throws InvalidParameterException, MPIException, ObjectInUseException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("deleteIdentifierDomain with name {}", identifierDomainName);
		}
		else
		{
			logger.info("deleteIdentifierDomain");
		}
		dao.deleteIdentifierDomain(identifierDomainName);
		logger.info("identifier domain deleted");
	}

	@Override
	public DomainDTO addDomain(DomainDTO domain) throws DuplicateEntryException, InvalidParameterException, MPIException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("addDomain {}", domain);
		}
		else
		{
			logger.info("addDomain");
		}
		checkParameter(domain.getName(), "domain.getName()");

		try
		{
			// the only place, where we do not want to throw an UnknownObjectException but an InvalidParameterException
			// when the caller is not allowed to create (or deal with in general) domains of the given name w.r.t. domain-based roles
			checkAllowedDomain(domain.getName());
		}
		catch (UnknownObjectException e)
		{
			throw new InvalidParameterException("domain.name", "Illegal domain name " + domain.getName()
					+ " (not matching any domain-based role: " + getAuthContext().getDomainBasedRoles() + ")");
		}

		checkParameter(domain.getMpiDomain(), "domain.getMpiDomain()");
		checkParameter(domain.getSafeSource(), "domain.getSafeSource()");
		DomainDTO result = dao.addDomain(domain);
		logger.info("domain added");
		return result;
	}

	@Override
	public DomainDTO getDomain(String domainName) throws UnknownObjectException, InvalidParameterException
	{
		logger.debug("getDomain with name {}", domainName);
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		DomainDTO result = dao.getDomain(domainName);
		logger.debug("returning found domain");
		return result;
	}

	@Override
	public List<DomainDTO> getDomains()
	{
		logger.debug("getDomains");
		List<DomainDTO> result = filterAllowedDomains(dao.getDomains());
		logger.debug("found {} domains", result.size());
		return result;
	}

	@Override
	public DomainDTO updateDomain(DomainDTO domain) throws InvalidParameterException, MPIException, ObjectInUseException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("updateDomain with {}", domain);
		}
		else
		{
			logger.info("updateDomain");
		}
		checkParameter(domain.getName(), "domain.getName()");
		checkAllowedDomain(domain.getName());
		checkParameter(domain.getMpiDomain(), "domain.getMpiDomain()");
		checkParameter(domain.getSafeSource(), "domain.getSafeSource()");
		DomainDTO result = dao.updateDomain(domain);
		logger.info("domain updated");
		return result;
	}

	@Override
	public DomainDTO updateDomainInUse(String domainName, String label, String description)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("updateDomainInUse with name {}, set label = {} and description = {}", domainName, label, description);
		}
		else
		{
			logger.info("updateDomainInUse");
		}
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		DomainDTO result = dao.updateDomainInUse(domainName, label, description);
		logger.info("domain updated");
		return result;
	}

	@Override
	public void deleteDomain(String domainName, boolean force) throws InvalidParameterException, MPIException, ObjectInUseException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("deleteDomain with name {}", domainName);
		}
		else
		{
			logger.info("deleteDomain");
		}
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		dao.deleteDomain(domainName, force, getAuthUser());
		logger.info("domain deleted");
	}

	@Override
	public SourceDTO addSource(SourceDTO source) throws DuplicateEntryException, InvalidParameterException, MPIException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("addSource {}", source);
		}
		else
		{
			logger.info("addSource");
		}
		checkParameter(source, "source");
		checkParameter(source.getName(), "source.getName()");
		SourceDTO result = dao.addSource(source);
		logger.info("source added");
		return result;
	}

	@Override
	public SourceDTO getSource(String sourceName) throws UnknownObjectException, InvalidParameterException
	{
		logger.debug("getSource with name {}", sourceName);
		checkParameter(sourceName, "sourceName");
		SourceDTO result = dao.getSource(sourceName);
		logger.debug("returning found source");
		return result;
	}

	@Override
	public List<SourceDTO> getSources()
	{
		logger.debug("getSources");
		List<SourceDTO> result = dao.getSources();
		logger.debug("found {} sources", result.size());
		return result;
	}

	@Override
	public SourceDTO updateSource(SourceDTO source) throws InvalidParameterException, MPIException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("updateSource with {}", source);
		}
		else
		{
			logger.info("updateSource");
		}
		checkParameter(source, "source");
		checkParameter(source.getName(), "source.getName()");
		SourceDTO result = dao.updateSource(source);
		logger.info("source updated");
		return result;
	}

	@Override
	public void deleteSource(String sourceName) throws InvalidParameterException, MPIException, ObjectInUseException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("deleteSource with name {}", sourceName);
		}
		else
		{
			logger.info("deleteSource");
		}
		checkParameter(sourceName, "sourceName");
		dao.deleteSource(sourceName);
		logger.info("source deleted");
	}

	@Override
	public PersonDTO getPersonById(long id) throws UnknownObjectException
	{
		logger.debug("getPersonById with id {}", id);
		PersonDTO person = dao.getPersonById(id);
		checkAllowedPerson(person);
		return person;
	}

	@Override
	public IdentityOutDTO getIdentityById(long id) throws UnknownObjectException
	{
		logger.debug("getIdentityById with id {}", id);
		IdentityOutDTO identity = dao.getIdentityById(id);
		checkAllowedEntity(
				getPersonIdDomainSupplier(identity.getPersonId()),
				() -> PublicDAO.createUnknownIdentityIdException(id));
		return identity;
	}

	@Override
	public List<PersonDTO> getDeactivatedPersonsForDomain(String domainName) throws InvalidParameterException, UnknownObjectException
	{
		logger.debug("getDeactivatedPersonsForDomain for domain {}", domainName);
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		List<PersonDTO> result = dao.getDeactivatedPersons(domainName);
		logger.debug("found {} persons", result.size());
		return result;
	}

	@Override
	public List<IdentityOutDTO> getDeacticatedIdentitiesForDomain(String domainName) throws InvalidParameterException, UnknownObjectException
	{
		logger.debug("getDeacticatedIdentitiesForDomain for domain {}", domainName);
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		List<IdentityOutDTO> result = dao.getDeactivatedIdentitiesByDomain(domainName);
		logger.debug("found {} identities", result.size());
		return result;
	}

	@Override
	public List<PersonHistoryDTO> getHistoryForPerson(String domainName, String mpiId) throws InvalidParameterException, UnknownObjectException
	{
		logger.debug("getHistoryForPerson for mpi id {} within domain {}", mpiId, domainName);
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		checkParameter(mpiId, "mpiId");
		List<PersonHistoryDTO> result = dao.getHistoryForPerson(domainName, mpiId);
		logger.debug("found {} person history entries", result.size());
		return result;
	}

	@Override
	public List<IdentityHistoryDTO> getIdentityHistoriesForDomain(String domainName) throws InvalidParameterException, UnknownObjectException
	{
		logger.debug("getIdentityHistoriesForDomain " + domainName);
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		List<IdentityHistoryDTO> result = dao.getIdentityHistoriesForDomain(domainName, null, false);
		logger.debug(FOUND_IDENTITY_HISTORY_ENTRIES, result.size());
		return result;
	}

	@Override
	public List<IdentityHistoryDTO> getIdentityHistoriesForDomainFiltered(String domainName, Map<IdentityField, String> filter,
			boolean filterIsCaseSensitive) throws InvalidParameterException, UnknownObjectException
	{
		logger.debug("getIdentityHistoriesForDomainFiltered, domain={} and the filter is case sensitive={}", domainName
						+ (filter != null && !filter.isEmpty() ? filter.size() + " filter values" : " no filter values")
				, filterIsCaseSensitive);
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		List<IdentityHistoryDTO> result = dao.getIdentityHistoriesForDomain(domainName, filter, filterIsCaseSensitive);
		logger.debug(FOUND_IDENTITY_HISTORY_ENTRIES, result.size());
		return result;
	}

	@Override
	public List<IdentityHistoryDTO> getIdentityHistoriesForDomainPaginated(String domainName, PaginationConfig paginationConfig)
			throws InvalidParameterException, UnknownObjectException
	{
		logger.debug("getIdentityHistoriesForDomainPaginated, domain={}, paginationConfig={}", domainName, paginationConfig);
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		List<IdentityHistoryDTO> result = dao.getIdentityHistoriesPaginated(domainName, paginationConfig);
		logger.debug(FOUND_IDENTITY_HISTORY_ENTRIES, result.size());
		return result;
	}

	@Override
	public long countIdentityHistoriesForDomain(String domainName, PaginationConfig paginationConfig)
			throws InvalidParameterException, UnknownObjectException
	{
		logger.debug("countIdentityHistoriesForDomain, domain={} paginationConfig={}", domainName, paginationConfig);
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		long count = dao.countIdentityHistories(domainName, paginationConfig);
		logger.debug("counted {} identity history entries", count);
		return count;
	}

	@Override
	public long countPossibleMatchHistoriesForDomain(String domainName, PaginationConfig pc) throws UnknownObjectException, InvalidParameterException
	{
		logger.debug("countPossibleMatchHistoriesForDomain, domain={} paginationConfig={}", domainName, pc);
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		long count = dao.countPossibleMatchHistories(domainName, pc);
		logger.debug("counted {} possible match history entries", count);
		return count;
	}

	@Override
	public List<IdentityHistoryDTO> getHistoryForIdentity(long identityId) throws UnknownObjectException
	{
		logger.debug("getHistoryForIdentity with id {}", identityId);
		List<IdentityHistoryDTO> result = dao.getHistoryForIdentity(identityId);
		if (!result.isEmpty())
		{
			// check allowed identity ID via the person ID of the result,
			// so we can skip an extra call to dao.getIdentityById(id) to get the person ID (to get the domain)
			checkAllowedEntity(
					getPersonIdDomainSupplier(result.get(0).getPersonId()),
					() -> PublicDAO.createUnknownIdentityIdException(identityId));
		}
		logger.debug(FOUND_IDENTITY_HISTORY_ENTRIES, result.size());
		return result;
	}

	@Override
	public List<ContactHistoryDTO> getHistoryForContact(long contactId) throws UnknownObjectException
	{
		logger.debug("getHistoryForContact with id {}", contactId);
		List<ContactHistoryDTO> result = dao.getHistoryForContact(contactId);
		if (!result.isEmpty())
		{
			// check allowed identityID via the person ID of the result,
			// so we can skip an extra call to dao.getIdentityById(id) to get the person ID (to get the domain)
			checkAllowedEntity(
					getIdentityIdDomainSupplier(result.get(0).getIdentityId()),
					() -> PublicDAO.createUnknownContactIdException(contactId));
		}
		logger.debug("found {} contact history entries", result.size());
		return result;
	}

	@Override
	public List<IdentifierHistoryDTO> getHistoryForIdentifier(String identifierDomainName, String value) throws UnknownObjectException
	{
		logger.debug("getHistoryForIdentifier with identifierDomainName '{}' and value '{}'", identifierDomainName, value);
		List<IdentifierHistoryDTO> result = dao.getHistoryForIdentifier(identifierDomainName, value);
		logger.debug("found {} identifier history entries", result.size());
		return result;
	}

	@Override
	public List<PossibleMatchHistoryDTO> getPossibleMatchHistoryForPerson(String domainName, String mpiId)
			throws InvalidParameterException, UnknownObjectException
	{
		logger.debug("getPossibleMatchHistoryForPerson with mpi id {} within domain {}", mpiId, domainName);
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		checkParameter(mpiId, "mpiId");
		List<PossibleMatchHistoryDTO> result = dao.getPossibleMatchHistoryForPerson(domainName, mpiId);
		logger.debug(FOUND_POSSIBLE_MATCH_HISTORY_ENTRIES, result.size());
		return result;
	}

	@Override
	public List<PossibleMatchHistoryDTO> getPossibleMatchHistoryForUpdatedIdentity(long updatedIdentityId) throws InvalidParameterException, UnknownObjectException
	{
		logger.debug("getPossibleMatchHistoryForUpdatedIdentity with updatedIdentityId {}", updatedIdentityId);
		List<PossibleMatchHistoryDTO> result = dao.getPossibleMatchHistoryForUpdatedIdentity(updatedIdentityId);
		checkAllowedPossibleMatchHistoryResult(updatedIdentityId, result);
		logger.debug(FOUND_POSSIBLE_MATCH_HISTORY_ENTRIES, result.size());
		return result;
	}

	@Override
	public List<PossibleMatchHistoryDTO> getPossibleMatchHistoryByIdentity(long identityId) throws UnknownObjectException
	{
		logger.debug("getPossibleMatchHistoryByIdentity for identityId {}", identityId);
		List<PossibleMatchHistoryDTO> result = dao.getPossibleMatchHistoryByIdentity(identityId);
		checkAllowedPossibleMatchHistoryResult(identityId, result);
		logger.debug(FOUND_POSSIBLE_MATCH_HISTORY_ENTRIES, result.size());
		return result;
	}

	@Override
	public ConfigurationContainer getConfigurationForDomain(String domainName) throws InvalidParameterException, UnknownObjectException
	{
		logger.debug("getConfigurationForDomain {}", domainName);
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		ConfigurationContainer result = dao.getConfigurationContainerForDomain(domainName);
		logger.debug("configuration found");
		return result;
	}

	@Override
	public List<IdentityHistoryDTO> getIdentityHistoryByPersonId(Long personId) throws UnknownObjectException
	{
		logger.debug("getReferenceIdentityAtTimestamp with personId {}", personId);
		checkAllowedPersonId(personId);
		List<IdentityHistoryDTO> result = dao.getIdentityHistoryByPersonId(personId);
		logger.debug("found {} identity history entries for personId", result.size());
		return result;
	}

	@Override
	public List<ReasonDTO> getDefinedDeduplicationReasons(String domainName) throws InvalidParameterException, UnknownObjectException
	{
		logger.debug("getDefinedDeduplicationReasons {}", domainName);
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		List<ReasonDTO> result = dao.getDefinedDeduplicationReasons(domainName);
		logger.debug("reasons found");
		return result;
	}

	@Override
	public ConfigurationContainer parseMatchingConfiguration(String xml) throws InvalidParameterException, MPIException
	{
		logger.debug("parseMatchingConfiguration from '{}'",
				xml == null ? "null" : StringUtils.truncate(xml, 1000) + "...");
		checkParameter(xml, "xml");
		return unwrapRuntimeException(() ->
				MatchingConfiguration.fromXml(xml, "unknown").toConfigurationContainer());
	}

	@Override
	public String encodeMatchingConfiguration(ConfigurationContainer config) throws InvalidParameterException, MPIException
	{
		logger.debug("encodeMatchingConfiguration from '{}'", config == null ? "null" : config);
		checkParameter(config, "config");
		return unwrapRuntimeException(() ->
				new MatchingConfiguration(config, "unknown").toXml("unknown"));
	}

	private <T> T unwrapRuntimeException(Supplier<T> supplier) throws InvalidParameterException, MPIException
	{
		try
		{
			return supplier.get();
		}
		catch (RuntimeException e)
		{
			if (e.getCause() instanceof MPIException mpie)
			{
				throw mpie;
			}
			else if (e.getCause() instanceof InvalidParameterException ipe)
			{
				throw ipe;
			}
			throw e;
		}
	}
}
