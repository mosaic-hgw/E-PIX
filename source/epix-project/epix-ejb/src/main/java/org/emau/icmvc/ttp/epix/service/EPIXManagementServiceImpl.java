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
import java.util.function.Supplier;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

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
 *
 * @author Christian Schack, geidell
 *
 */
@WebService(name = "epixManagementService")
@SOAPBinding(style = SOAPBinding.Style.RPC)
@Stateless
@Remote(value = EPIXManagementService.class)
public class EPIXManagementServiceImpl extends EpixServiceBase implements EPIXManagementService
{
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
			logger.debug("addIdentifierDomain " + identifierDomain);
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
		if (logger.isDebugEnabled())
		{
			logger.debug("getIdentifierDomain with name " + identifierDomainName);
		}
		else
		{
			logger.info("getIdentifierDomain");
		}
		checkParameter(identifierDomainName, "identifierDomainName");
		IdentifierDomainDTO result = dao.getIdentifierDomain(identifierDomainName);
		logger.info("returning found identifier domain");
		return result;
	}

	@Override
	public List<IdentifierDomainDTO> getIdentifierDomains()
	{
		logger.info("getAllIdentifierDomains");
		List<IdentifierDomainDTO> result = dao.getIdentifierDomains();
		logger.info("found " + result.size() + " identifier domains");
		return result;
	}

	@Override
	public IdentifierDomainDTO updateIdentifierDomain(IdentifierDomainDTO identifierDomain)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("updateIdentifierDomain with " + identifierDomain);
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
			logger.debug("deleteIdentifierDomain with name " + identifierDomainName);
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
			logger.debug("addDomain " + domain);
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
		if (logger.isDebugEnabled())
		{
			logger.debug("getDomain with name " + domainName);
		}
		else
		{
			logger.info("getDomain");
		}
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		DomainDTO result = dao.getDomain(domainName);
		logger.info("returning found domain");
		return result;
	}

	@Override
	public List<DomainDTO> getDomains()
	{
		logger.info("getDomains");
		List<DomainDTO> result = filterAllowedDomains(dao.getDomains());
		logger.info("found " + result.size() + " domains");
		return result;
	}

	@Override
	public DomainDTO updateDomain(DomainDTO domain) throws InvalidParameterException, MPIException, ObjectInUseException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("updateDomain with " + domain);
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
			logger.debug("updateDomainInUse with name " + domainName + ", set label = " + label + " and description = " + description);
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
			logger.debug("deleteDomain with name " + domainName);
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
			logger.debug("addSource " + source);
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
		if (logger.isDebugEnabled())
		{
			logger.debug("getSource with name " + sourceName);
		}
		else
		{
			logger.info("getSource");
		}
		checkParameter(sourceName, "sourceName");
		SourceDTO result = dao.getSource(sourceName);
		logger.info("returning found source");
		return result;
	}

	@Override
	public List<SourceDTO> getSources()
	{
		logger.info("getSources");
		List<SourceDTO> result = dao.getSources();
		logger.info("found " + result.size() + " sources");
		return result;
	}

	@Override
	public SourceDTO updateSource(SourceDTO source) throws InvalidParameterException, MPIException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("updateSource with " + source);
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
			logger.debug("deleteSource with name " + sourceName);
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
		if (logger.isDebugEnabled())
		{
			logger.debug("getPersonById with id " + id);
		}
		else
		{
			logger.info("getPersonById");
		}

		PersonDTO person = dao.getPersonById(id);
		checkAllowedPerson(person);

		return person;
	}

	@Override
	public IdentityOutDTO getIdentityById(long id) throws UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("getIdentityById with id " + id);
		}
		else
		{
			logger.info("getIdentityById");
		}

		IdentityOutDTO identity = dao.getIdentityById(id);

		checkAllowedEntity(
				getPersonIdDomainSupplier(identity.getPersonId()),
				() -> PublicDAO.createUnknownIdentityIdException(id));

		return identity;
	}

	@Override
	public List<PersonDTO> getDeactivatedPersonsForDomain(String domainName) throws InvalidParameterException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("getDeactivatedPersonsForDomain for domain " + domainName);
		}
		else
		{
			logger.info("getDeactivatedPersonsForDomain");
		}
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		List<PersonDTO> result = dao.getDeactivatedPersons(domainName);
		logger.info("found " + result.size() + " persons");
		return result;
	}

	@Override
	public List<IdentityOutDTO> getDeacticatedIdentitiesForDomain(String domainName) throws InvalidParameterException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("getDeacticatedIdentitiesForDomain for domain " + domainName);
		}
		else
		{
			logger.info("getDeacticatedIdentitiesForDomain");
		}
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		List<IdentityOutDTO> result = dao.getDeactivatedIdentitiesByDomain(domainName);
		logger.info("found " + result.size() + " identities");
		return result;
	}

	@Override
	public List<PersonHistoryDTO> getHistoryForPerson(String domainName, String mpiId) throws InvalidParameterException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("getHistoryForPerson for mpi id " + mpiId + " within domain " + domainName);
		}
		else
		{
			logger.info("getHistoryForPerson");
		}
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		checkParameter(mpiId, "mpiId");
		List<PersonHistoryDTO> result = dao.getHistoryForPerson(domainName, mpiId);
		logger.info("found " + result.size() + " person history entries");
		return result;
	}

	@Override
	public List<IdentityHistoryDTO> getIdentityHistoriesForDomain(String domainName) throws InvalidParameterException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("getIdentityHistoriesForDomain " + domainName);
		}
		else
		{
			logger.info("getIdentityHistoriesForDomain");
		}
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		List<IdentityHistoryDTO> result = dao.getIdentityHistoriesForDomain(domainName, null, false);
		logger.info("found " + result.size() + " identity history entries");
		return result;
	}

	@Override
	public List<IdentityHistoryDTO> getIdentityHistoriesForDomainFiltered(String domainName, Map<IdentityField, String> filter,
			boolean filterIsCaseSensitive) throws InvalidParameterException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("getIdentityHistoriesForDomainFiltered, domain=" + domainName
					+ (filter != null && !filter.isEmpty() ? filter.size() + " filter values" : " no filter values")
					+ " and the filter is case sensitive=" + filterIsCaseSensitive);
		}
		else
		{
			logger.info("getIdentityHistoriesForDomainFiltered");
		}
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		List<IdentityHistoryDTO> result = dao.getIdentityHistoriesForDomain(domainName, filter, filterIsCaseSensitive);
		logger.info("found " + result.size() + " identity history entries");
		return result;
	}

	@Override
	public List<IdentityHistoryDTO> getIdentityHistoriesForDomainPaginated(String domainName, PaginationConfig paginationConfig)
			throws InvalidParameterException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("getIdentityHistoriesForDomainPaginated, domain=" + domainName + ", paginationConfig=" + paginationConfig);
		}
		else
		{
			logger.info("getIdentityHistoriesForDomainPaginated");
		}
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		List<IdentityHistoryDTO> result = dao.getIdentityHistoriesPaginated(domainName, paginationConfig);
		logger.info("found " + result.size() + " identity history entries");
		return result;
	}

	@Override
	public long countIdentityHistoriesForDomain(String domainName, PaginationConfig paginationConfig)
			throws InvalidParameterException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("countIdentityHistoriesForDomain, domain=" + domainName + " paginationConfig=" + paginationConfig);
		}
		else
		{
			logger.info("countIdentityHistoriesForDomain");
		}
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		long count = dao.countIdentityHistories(domainName, paginationConfig);
		logger.info("counted " + count + " identity history entries");
		return count;
	}

	@Override
	public long countPossibleMatchHistoriesForDomain(String domainName, PaginationConfig pc) throws UnknownObjectException, InvalidParameterException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("countPossibleMatchHistoriesForDomain, domain=" + domainName + " paginationConfig=" + pc);
		}
		else
		{
			logger.info("countPossibleMatchHistoriesForDomain");
		}
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		long count = dao.countPossibleMatchHistories(domainName, pc);
		logger.info("counted " + count + " possible match history entries");
		return count;
	}

	@Override
	public List<IdentityHistoryDTO> getHistoryForIdentity(long identityId) throws UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("getHistoryForIdentity with id " + identityId);
		}
		else
		{
			logger.info("getHistoryForIdentity");
		}
		List<IdentityHistoryDTO> result = dao.getHistoryForIdentity(identityId);
		if (!result.isEmpty())
		{
			// check allowed identity ID via the person ID of the result,
			// so we can skip an extra call to dao.getIdentityById(id) to get the person ID (to get the domain)
			checkAllowedEntity(
					getPersonIdDomainSupplier(result.get(0).getPersonId()),
					() -> PublicDAO.createUnknownIdentityIdException(identityId));
		}
		logger.info("found " + result.size() + " identity history entries");
		return result;
	}

	@Override
	public List<ContactHistoryDTO> getHistoryForContact(long contactId) throws UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("getHistoryForContact with id " + contactId);
		}
		else
		{
			logger.info("getHistoryForContact");
		}
		List<ContactHistoryDTO> result = dao.getHistoryForContact(contactId);
		if (!result.isEmpty())
		{
			// check allowed identityID via the person ID of the result,
			// so we can skip an extra call to dao.getIdentityById(id) to get the person ID (to get the domain)
			checkAllowedEntity(
					getIdentityIdDomainSupplier(result.get(0).getIdentityId()),
					() -> PublicDAO.createUnknownContactIdException(contactId));
		}
		logger.info("found " + result.size() + " contact history entries");
		return result;
	}

	@Override
	public List<IdentifierHistoryDTO> getHistoryForIdentifier(String identifierDomainName, String value) throws UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("getHistoryForIdentifier with identifierDomainName '" + identifierDomainName + "' and value '" + value + "'");
		}
		else
		{
			logger.info("getHistoryForIdentifier");
		}
		List<IdentifierHistoryDTO> result = dao.getHistoryForIdentifier(identifierDomainName, value);
		logger.info("found " + result.size() + " identifier history entries");
		return result;
	}

	@Override
	public List<PossibleMatchHistoryDTO> getPossibleMatchHistoryForPerson(String domainName, String mpiId)
			throws InvalidParameterException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("getPossibleMatchHistoryForPerson with mpi id " + mpiId + " within domain " + domainName);
		}
		else
		{
			logger.info("getPossibleMatchHistoryForPerson");
		}
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		checkParameter(mpiId, "mpiId");
		List<PossibleMatchHistoryDTO> result = dao.getPossibleMatchHistoryForPerson(domainName, mpiId);
		logger.info("found " + result.size() + " possible match history entries");
		return result;
	}

	@Override
	public List<PossibleMatchHistoryDTO> getPossibleMatchHistoryForUpdatedIdentity(long updatedIdentityId) throws InvalidParameterException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("getPossibleMatchHistoryForUpdatedIdentity with updatedIdentityId " + updatedIdentityId);
		}
		else
		{
			logger.info("getPossibleMatchHistoryForUpdatedIdentity");
		}
		List<PossibleMatchHistoryDTO> result = dao.getPossibleMatchHistoryForUpdatedIdentity(updatedIdentityId);
		checkAllowedPossibleMatchHistoryResult(updatedIdentityId, result);
		logger.info("found " + result.size() + " possible match history entries");
		return result;
	}

	@Override
	public List<PossibleMatchHistoryDTO> getPossibleMatchHistoryByIdentity(long identityId) throws UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("getPossibleMatchHistoryByIdentity for identityId " + identityId);
		}
		else
		{
			logger.info("getPossibleMatchHistoryForIdentity");
		}
		List<PossibleMatchHistoryDTO> result = dao.getPossibleMatchHistoryByIdentity(identityId);
		checkAllowedPossibleMatchHistoryResult(identityId, result);
		logger.info("found " + result.size() + " possible match history entries");
		return result;
	}

	@Override
	public ConfigurationContainer getConfigurationForDomain(String domainName) throws InvalidParameterException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("getConfigurationForDomain " + domainName);
		}
		else
		{
			logger.info("getConfigurationForDomain");
		}
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		ConfigurationContainer result = dao.getConfigurationContainerForDomain(domainName);
		logger.info("configuration found");
		return result;
	}

	@Override
	public List<IdentityHistoryDTO> getIdentityHistoryByPersonId(Long personId) throws UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("getReferenceIdentityAtTimestamp with personId " + personId);
		}
		else
		{
			logger.info("getReferenceIdentityAtTimestamp");
		}
		checkAllowedPersonId(personId);
		List<IdentityHistoryDTO> result = dao.getIdentityHistoryByPersonId(personId);
		logger.info("found " + result.size() + " identity history entries for personId");
		return result;
	}

	@Override
	public List<ReasonDTO> getDefinedDeduplicationReasons(String domainName) throws InvalidParameterException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("getDefinedDeduplicationReasons " + domainName);
		}
		else
		{
			logger.info("getDefinedDeduplicationReasons");
		}
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		List<ReasonDTO> result = dao.getDefinedDeduplicationReasons(domainName);
		logger.info("reasons found");
		return result;
	}

	@Override
	public ConfigurationContainer parseMatchingConfiguration(String xml) throws InvalidParameterException, MPIException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("parseMatchingConfiguration from '{}'",
					xml == null ? "null" : StringUtils.truncate(xml, 1000) + "...");
		}
		else
		{
			logger.info("parseMatchingConfiguration");
		}

		checkParameter(xml, "xml");

		return unwrapRuntimeException(() ->
				MatchingConfiguration.fromXml(xml, "unknown").toConfigurationContainer());
	}

	@Override
	public String encodeMatchingConfiguration(ConfigurationContainer config) throws InvalidParameterException, MPIException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("encodeMatchingConfiguration from '{}'", config == null ? "null" : config);
		}
		else
		{
			logger.info("encodeMatchingConfiguration");
		}

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
