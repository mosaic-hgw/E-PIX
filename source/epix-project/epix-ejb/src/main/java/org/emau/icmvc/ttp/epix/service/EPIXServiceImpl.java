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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import org.emau.icmvc.ttp.epix.common.exception.DuplicateEntryException;
import org.emau.icmvc.ttp.epix.common.exception.IllegalOperationException;
import org.emau.icmvc.ttp.epix.common.exception.InvalidParameterException;
import org.emau.icmvc.ttp.epix.common.exception.MPIErrorCode;
import org.emau.icmvc.ttp.epix.common.exception.MPIException;
import org.emau.icmvc.ttp.epix.common.exception.UnknownObjectException;
import org.emau.icmvc.ttp.epix.common.exception.UnknownObjectType;
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
import org.jboss.ejb3.annotation.TransactionTimeout;

/**
 *
 * @author geidell
 *
 */
@WebService(name = "epixService")
@Remote({ EPIXService.class })
@SOAPBinding(style = SOAPBinding.Style.RPC)
@Stateless
public class EPIXServiceImpl extends EpixServiceBase implements EPIXService
{

	public EPIXServiceImpl()
	{
		logger.debug("creating epix service");
	}

	@Override
	public ResponseEntryDTO requestMPI(String domainName, IdentityInDTO identity, String sourceName, String comment)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		return requestMPIInternal(null, domainName, identity, sourceName, comment, null);
	}

	@Override
	public ResponseEntryDTO requestMPIWithConfig(String domainName, IdentityInDTO identity, String sourceName, String comment,
			RequestConfig requestConfig)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		return requestMPIInternal(null, domainName, identity, sourceName, comment, requestConfig);
	}

	@TransactionTimeout(3600)
	@Override
	public MPIResponseDTO requestMPIBatch(MPIRequestDTO mpiRequest)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		return requestMPIBatchInternal(null, mpiRequest);
	}

	@Override
	public List<PersonDTO> getPersonsForDomain(String domainName) throws InvalidParameterException, UnknownObjectException
	{
		return getActivePersonsForDomain(domainName);
	}

	@Override
	public List<PersonDTO> getActivePersonsForDomain(String domainName) throws InvalidParameterException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("getActivePersonsForDomain " + domainName);
		}
		else
		{
			logger.info("getActivePersonsForDomain");
		}
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		List<PersonDTO> result = dao.getActivePersons(domainName, null, false);
		logger.info("found " + result.size() + " persons");
		return result;
	}

	@Override
	public List<PersonDTO> getPersonsForDomainFiltered(String domainName, Map<PersonField, String> filter, boolean filterIsCaseSensitive)
			throws InvalidParameterException, UnknownObjectException
	{
		return getActivePersonsForDomainFiltered(domainName, filter, filterIsCaseSensitive);
	}

	@Override
	public List<PersonDTO> getActivePersonsForDomainFiltered(String domainName, Map<PersonField, String> filter, boolean filterIsCaseSensitive)
			throws InvalidParameterException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("getActivePersonsForDomainFiltered, domain=" + domainName
					+ (filter != null && !filter.isEmpty() ? filter.size() + " filter values" : " no filter values")
					+ " and the filter is case sensitive=" + filterIsCaseSensitive);
		}
		else
		{
			logger.info("getActivePersonsForDomainFiltered");
		}
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		List<PersonDTO> result = dao.getActivePersons(domainName, filter, filterIsCaseSensitive);
		logger.info("found " + result.size() + " persons");
		return result;
	}

	@Override
	public List<PersonDTO> getPersonsForDomainPaginated(String domainName, int firstEntry, int pageSize, PersonField sortField,
			boolean sortIsAscending, Map<PersonField, String> filter, boolean filterIsCaseSensitive)
			throws InvalidParameterException, UnknownObjectException
	{
		return getActivePersonsForDomainPaginated(domainName, firstEntry, pageSize, sortField, sortIsAscending, filter, filterIsCaseSensitive);
	}

	@Override
	public List<PersonDTO> getActivePersonsForDomainPaginated(String domainName, int firstEntry, int pageSize, PersonField sortField,
			boolean sortIsAscending, Map<PersonField, String> filter, boolean filterIsCaseSensitive)
			throws InvalidParameterException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("getActivePersonsForDomainPaginated, domain=" + domainName + ", firstEntry=" + firstEntry + ", pageSize=" + pageSize
					+ ", sortField=" + sortField + ", sortIsAscending=" + sortIsAscending
					+ (filter != null && !filter.isEmpty() ? filter.size() + " filter values" : " no filter values")
					+ " and the filter is case sensitive=" + filterIsCaseSensitive);
		}
		else
		{
			logger.info("getActivePersonsForDomainPaginated");
		}
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		List<PersonDTO> result = dao.getActivePersonsPaginated(domainName, firstEntry, pageSize, sortField, sortIsAscending, filter, filterIsCaseSensitive);
		logger.info("found " + result.size() + " persons");
		return result;
	}

	@Override
	public long countPersonsForDomainFiltered(String domainName, Map<PersonField, String> filter, boolean filterIsCaseSensitive)
			throws InvalidParameterException, UnknownObjectException
	{
		return countActivePersonsForDomainFiltered(domainName, filter, filterIsCaseSensitive);
	}

	@Override
	public long countActivePersonsForDomainFiltered(String domainName, Map<PersonField, String> filter, boolean filterIsCaseSensitive)
			throws InvalidParameterException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("countActivePersonsForDomainFiltered, domain=" + domainName + (filter != null && !filter.isEmpty() ? filter.size() + " filter values" : " no filter values")
					+ " and the filter is case sensitive=" + filterIsCaseSensitive);
		}
		else
		{
			logger.info("countActivePersonsForDomainFiltered");
		}
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		return dao.countActivePersonsFiltered(domainName, filter, filterIsCaseSensitive);
	}

	@Override
	public PersonDTO getPersonByFirstMPI(String domainName, String mpiId) throws InvalidParameterException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("getPersonByFirstMPI for domain " + domainName + " and mpiId " + mpiId);
		}
		else
		{
			logger.info("getPersonByFirstMPI");
		}
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		checkParameter(mpiId, "mpiId");
		PersonDTO result = dao.getPersonByFirstMPI(domainName, mpiId);
		logger.info("person found");
		return result;
	}

	@Override
	public List<PersonDTO> getPersonsByFirstMPIBatch(String domainName, List<String> mpiIds) throws InvalidParameterException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("getPersonsByFirstMPIBatch for domain " + domainName + " and " + mpiIds.size() + " mpiIds");
		}
		else
		{
			logger.info("getPersonsByFirstMPIBatch");
		}
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		checkParameter(mpiIds, "mpiIds");
		List<PersonDTO> result = dao.getPersonsByFirstMPIBatch(domainName, mpiIds);
		logger.info("found " + result.size() + " persons");
		return result;
	}

	@Override
	public PersonDTO getPersonByMPI(String domainName, String mpiId) throws InvalidParameterException, UnknownObjectException
	{
		return getActivePersonByMPI(domainName, mpiId);
	}

	@Override
	public PersonDTO getActivePersonByMPI(String domainName, String mpiId) throws InvalidParameterException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("getActivePersonByMpi for domain " + domainName + " and mpiId " + mpiId);
		}
		else
		{
			logger.info("getActivePersonByMpi");
		}
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		checkParameter(mpiId, "mpiId");
		PersonDTO result = dao.getActivePersonByMPI(domainName, mpiId);
		logger.info("person found");
		return result;
	}

	@Override
	public List<PersonDTO> getPersonsByMPIBatch(String domainName, List<String> mpiIds) throws InvalidParameterException, UnknownObjectException
	{
		return getActivePersonsByMPIBatch(domainName, mpiIds);
	}

	@Override
	public List<PersonDTO> getActivePersonsByMPIBatch(String domainName, List<String> mpiIds) throws InvalidParameterException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("getActivePersonByMPIBatch for domain " + domainName + " and " + mpiIds.size() + " mpiIds");
		}
		else
		{
			logger.info("getActivePersonByMPIBatch");
		}
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		checkParameter(mpiIds, "mpiIds");
		List<PersonDTO> result = dao.getActivePersonsByMPIBatch(domainName, mpiIds);
		logger.info("found " + result.size() + " persons");
		return result;
	}

	@Override
	public PersonDTO getPersonByLocalIdentifier(String domainName, IdentifierDTO identifier)
			throws InvalidParameterException, UnknownObjectException
	{
		return getActivePersonByLocalIdentifier(domainName, identifier);
	}

	@Override
	public PersonDTO getActivePersonByLocalIdentifier(String domainName, IdentifierDTO identifier)
			throws InvalidParameterException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("getActivePersonByLocalIdentifier for domain " + domainName + " and " + identifier);
		}
		else
		{
			logger.info("getActivePersonByLocalIdentifier");
		}
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		checkParameter(identifier, "identifier");
		checkParameter(identifier.getIdentifierDomain(), "identifier.getIdentifierDomain()");
		PersonDTO result = dao.getActivePersonByLocalIdentifier(domainName, identifier);
		logger.info("person found");
		return result;
	}

	@Override
	public PersonDTO getPersonByMultipleLocalIdentifier(String domainName, List<IdentifierDTO> identifier, boolean allIdentifierRequired)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		return getActivePersonByMultipleLocalIdentifier(domainName, identifier, allIdentifierRequired);
	}

	@Override
	public PersonDTO getActivePersonByMultipleLocalIdentifier(String domainName, List<IdentifierDTO> identifier, boolean allIdentifierRequired)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("getActivePersonByMultipleLocalIdentifier for domain " + domainName + " and " + identifier);
		}
		else
		{
			logger.info("getActivePersonByMultipleLocalIdentifier");
		}
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		checkParameter(identifier, "identifier");
		for (IdentifierDTO ident : identifier)
		{
			checkParameter(ident.getIdentifierDomain(), "identifier.getIdentifierDomain()");
		}
		PersonDTO result = dao.getActivePersonByMultipleLocalIdentifier(domainName, identifier, allIdentifierRequired);
		logger.info("person found");
		return result;
	}

	@Override
	public ResponseEntryDTO updatePerson(String domainName, String mpiId, IdentityInDTO identity, String sourceName, boolean force, String comment)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		return updatePersonInternal(null, domainName, mpiId, identity, sourceName, force, comment, null);
	}

	@Override
	public ResponseEntryDTO updatePersonWithConfig(String domainName, String mpiId, IdentityInDTO identity, String sourceName, boolean force,
			String comment, RequestConfig requestConfig)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		return updatePersonInternal(null, domainName, mpiId, identity, sourceName, force, comment, requestConfig);
	}

	@Override
	public ResponseEntryDTO addPerson(String domainName, IdentityInDTO identity, String sourceName, String comment)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		return addPersonInternal(null, domainName, identity, sourceName, comment);
	}

	@Override
	public List<IdentityOutDTO> getIdentitiesForDomain(String domainName) throws InvalidParameterException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("getIdentitiesForDomain " + domainName);
		}
		else
		{
			logger.info("getIdentitiesForDomain");
		}
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		List<IdentityOutDTO> result = dao.getIdentitiesByDomain(domainName, null, false);
		logger.info("found " + result.size() + " identities");
		return result;
	}

	@Override
	public List<IdentityOutDTO> getIdentitiesForDomainFiltered(String domainName, Map<IdentityField, String> filter, boolean filterIsCaseSensitive)
			throws InvalidParameterException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("getIdentitiesForDomainFiltered, domain=" + domainName
					+ (filter != null && !filter.isEmpty() ? filter.size() + " filter values" : " no filter values")
					+ " and the filter is case sensitive=" + filterIsCaseSensitive);
		}
		else
		{
			logger.info("getIdentitiesForDomainFiltered");
		}
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		List<IdentityOutDTO> result = dao.getIdentitiesByDomain(domainName, filter, filterIsCaseSensitive);
		logger.info("found " + result.size() + " persons");
		return result;
	}

	@Override
	public List<IdentityOutDTO> getIdentitiesForDomainPaginated(String domainName, int firstEntry, int pageSize, IdentityField sortField,
			boolean sortIsAscending, Map<IdentityField, String> filter, boolean filterIsCaseSensitive)
			throws InvalidParameterException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("getIdentitiesForDomainPaginated, domain=" + domainName + ", firstEntry=" + firstEntry + ", pageSize=" + pageSize
					+ ", sortField=" + sortField + ", sortIsAscending=" + sortIsAscending + " "
					+ (filter != null && !filter.isEmpty() ? filter.size() + " filter values" : "no filter values")
					+ " and the filter is case sensitive=" + filterIsCaseSensitive);
		}
		else
		{
			logger.info("getIdentitiesForDomainPaginated");
		}
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		List<IdentityOutDTO> result = dao.getIdentitiesByDomainPaginated(domainName, firstEntry, pageSize, sortField, sortIsAscending, filter,
				filterIsCaseSensitive);
		logger.info("found " + result.size() + " persons");
		return result;
	}

	@Override
	public long countIdentitiesForDomainFiltered(String domainName, Map<IdentityField, String> filter, boolean filterIsCaseSensitive)
			throws InvalidParameterException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("countIdentitiesForDomainFiltered, domain=" + domainName + (filter != null && !filter.isEmpty() ? filter.size() + " filter values" : "no filter values")
					+ " and the filter is case sensitive=" + filterIsCaseSensitive);
		}
		else
		{
			logger.info("countIdentitiesForDomainFiltered");
		}
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		return dao.countIdentitiesByDomainFiltered(domainName, filter, filterIsCaseSensitive);
	}

	/*
	 * @Override
	 * public Map<IdentifierDTO, IdentityOutDTO> getReferenceIdentitiesForDomain(String domainName)
	 * throws InvalidParameterException, UnknownObjectException
	 * {
	 * if (logger.isDebugEnabled())
	 * {
	 * logger.debug("getReferenceIdentitiesForDomain " + domainName);
	 * }
	 * else
	 * {
	 * logger.info("getReferenceIdentitiesForDomain");
	 * }
	 * checkAllowedDomain(domainName, true);
	 * checkParameter(domainName, "domainName");
	 * // TODO optimieren - nur ref-ids laden - kompliziertes sql ...
	 * List<PersonDTO> persons = getPersonsForDomain(domainName);
	 * Map<IdentifierDTO, IdentityOutDTO> result = new HashMap<>();
	 * for (PersonDTO person : persons)
	 * {
	 * result.put(person.getMpiId(), person.getReferenceIdentity());
	 * }
	 * logger.info("found " + persons.size() + " reference identities");
	 * return result;
	 * }
	 */

	@Override
	public ResponseEntryDTO setReferenceIdentity(String domainName, String mpiId, long identityId, String comment)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		return setReferenceIdentityInternal(null, domainName, mpiId, identityId, comment);
	}

	@Override
	public void deactivateIdentity(long identityId)
			throws MPIException, UnknownObjectException
	{
		deactivateIdentityInternal(null, identityId);
	}

	@Override
	public void deactivatePerson(String domainName, String mpiId)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		deactivatePersonInternal(null, domainName, mpiId);
	}

	@Override
	public void deleteIdentity(long identityId)
			throws IllegalOperationException, MPIException, UnknownObjectException
	{
		deleteIdentityInternal(null, identityId);
	}

	@Override
	public void deletePerson(String domainName, String mpiId)
			throws IllegalOperationException, InvalidParameterException, MPIException, UnknownObjectException
	{
		deletePersonInternal(null, domainName, mpiId);
	}

	@TransactionTimeout(value = Long.MAX_VALUE)
	@Override
	public void updatePrivacy(String domainName, List<String> mpiIds, boolean onlyReferenceIdentity)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		updatePrivacyInternal(domainName, mpiIds, onlyReferenceIdentity);
	}

	@Override
	public IdentityOutDTO addContact(long identityId, ContactInDTO contactDTO)
			throws DuplicateEntryException, InvalidParameterException, MPIException, UnknownObjectException
	{
		return addContactInternal(null, identityId, contactDTO);
	}

	@Override
	public void deactivateContact(long contactId)
			throws UnknownObjectException
	{
		deactivateContactInternal(contactId);
	}

	@Override
	public void deleteContact(long contactId) throws IllegalOperationException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("deleteContact with id " + contactId);
		}
		else
		{
			logger.info("deleteContact");
		}
		checkAllowedContactId(contactId);
		dao.deleteContact(contactId, null, getAuthUser());
		logger.info("contact deleted");
	}

	@Override
	public List<PersonDTO> searchPersonsByPDQ(SearchMask searchMask) throws InvalidParameterException, MPIException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("searchPersonByPDQCache with " + searchMask);
		}
		else
		{
			logger.info("searchPersonByPDQCache");
		}
		checkParameter(searchMask.getDomainName(), "searchMask.getDomainName()");
		checkAllowedDomain(searchMask.getDomainName());
		checkParameter(searchMask.getIdentity(), "searchMask.getIdentity()");
		LongList personIds = new LongArrayList();
		List<PersonDTO> result = new ArrayList<>();
		if (!searchMask.hasSearchValues())
		{
			logger.info("no values withhin searchMask - returning empty list");
			return result;
		}
		// lokale ids holen
		if (searchMask.getIdentity().getIdentifiers() != null)
		{
			for (IdentifierDTO identifier : searchMask.getIdentity().getIdentifiers())
			{
				try
				{
					PersonDTO personForLocalId = dao.getActivePersonByLocalIdentifier(searchMask.getDomainName(), identifier);
					if (!personIds.contains(personForLocalId.getPersonId()) && !personForLocalId.isDeactivated())
					{
						personIds.add(personForLocalId.getPersonId());
					}
				}
				catch (UnknownObjectException maybe)
				{
					if (UnknownObjectType.IDENTIFIER.equals(maybe.getObjectType()))
					{
						logger.debug("ignore expected UnknownObjectException: " + maybe.getMessage());
					}
					else
					{
						logger.error("unexpected exception", maybe);
						throw maybe;
					}
				}
			}
		}
		try
		{
			result = dao.findPersonsByPDQ(searchMask, personIds);
			logger.info("found " + result.size() + " persons");
			return result;
		}
		catch (MPIException e)
		{
			String message = "unexpected internal error while searching persons by PDQ: " + e.getMessage();
			logger.error(message, e);
			throw new MPIException(MPIErrorCode.INTERNAL_ERROR, message, e);
		}
	}

	@Override
	public List<String> getAllMPIFromPersonByMPI(String domainName, String mpiId)
			throws InvalidParameterException, UnknownObjectException
	{
		return getAllMPIFromActivePersonByMPI(domainName, mpiId);
	}

	@Override
	public List<String> getAllMPIFromActivePersonByMPI(String domainName, String mpiId)
			throws InvalidParameterException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("getAllMPIFromActivePersonByMPI for domain " + domainName + " and mpiId " + mpiId);
		}
		else
		{
			logger.info("getAllMPIFromActivePersonByMPI");
		}
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		checkParameter(mpiId, "mpiId");
		PersonDTO person = getActivePersonByMPI(domainName, mpiId);
		List<String> result = new ArrayList<>();
		result.add(person.getMpiId().getValue());
		for (IdentifierDTO identifier : person.getReferenceIdentity().getIdentifiers())
		{
			if (identifier.getIdentifierDomain().equals(person.getMpiId().getIdentifierDomain()))
			{
				result.add(identifier.getValue());
			}
		}
		for (IdentityOutDTO identity : person.getOtherIdentities())
		{
			for (IdentifierDTO identifier : identity.getIdentifiers())
			{
				if (identifier.getIdentifierDomain().equals(person.getMpiId().getIdentifierDomain()))
				{
					result.add(identifier.getValue());
				}
			}
		}
		logger.info("found " + result.size() + " MPIs");
		return result;
	}

	@Override
	public String getMPIForIdentifier(String domainName, IdentifierDTO identifier) throws InvalidParameterException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("getMPIForIdentifier for domain " + domainName + " and " + identifier);
		}
		else
		{
			logger.info("getMPIForIdentifier");
		}
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		checkParameter(identifier, "identifier");
		checkParameter(identifier.getIdentifierDomain(), "identifier.getIdentifierDomain()");
		PersonDTO person = dao.getActivePersonByLocalIdentifier(domainName, identifier);
		logger.info("mpi found");
		return person.getMpiId().getValue();
	}

	@Override
	public List<IdentifierDTO> getAllIdentifierForMPI(String domainName, String mpiId)
			throws InvalidParameterException, UnknownObjectException
	{
		return getAllIdentifierForAcivePersonWithMPI(domainName, mpiId);
	}

	@Override
	public List<IdentifierDTO> getAllIdentifierForAcivePersonWithMPI(String domainName, String mpiId)
			throws InvalidParameterException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("getAllIdentifierForAcivePersonWithMPI for domain " + domainName + " and mpiId " + mpiId);
		}
		else
		{
			logger.info("getAllIdentifierForAcivePersonWithMPI");
		}
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		checkParameter(mpiId, "mpiId");
		PersonDTO person = getActivePersonByMPI(domainName, mpiId);
		List<IdentifierDTO> result = getAllIdentifierForPerson(person);
		logger.info("found " + result.size() + " identifier");
		return result;
	}

	private List<IdentifierDTO> getAllIdentifierForPerson(PersonDTO person)
	{
		logger.debug("getAllIdentifierForPerson");
		List<IdentifierDTO> result = new ArrayList<>();
		result.add(person.getMpiId());
		result.addAll(person.getReferenceIdentity().getIdentifiers());
		for (IdentityOutDTO identity : person.getOtherIdentities())
		{
			result.addAll(identity.getIdentifiers());
		}
		return result;
	}

	@Override
	public List<IdentifierDTO> getAllIdentifierForIdentifier(String domainName, IdentifierDTO identifierDTO)
			throws InvalidParameterException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("getAllIdentifierForIdentifier for domain " + domainName + " and " + identifierDTO);
		}
		else
		{
			logger.info("getAllIdentifierForIdentifier");
		}
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		checkParameter(identifierDTO, "identifier");
		checkParameter(identifierDTO.getIdentifierDomain(), "identifier.getIdentifierDomain()");
		PersonDTO person = dao.getActivePersonByLocalIdentifier(domainName, identifierDTO);
		List<IdentifierDTO> result = new ArrayList<>(getAllIdentifierForPerson(person));
		logger.info("found " + result.size() + " identifier");
		return result;
	}

	@Override
	public void addLocalIdentifierToMPI(String domainName, String mpiId, List<IdentifierDTO> localIds)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		addLocalIdentifierToActivePersonWithMPIInternal(null, domainName, mpiId, localIds);
	}

	@Override
	public void addLocalIdentifierToActivePersonWithMPI(String domainName, String mpiId, List<IdentifierDTO> localIds)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		addLocalIdentifierToActivePersonWithMPIInternal(null, domainName, mpiId, localIds);
	}

	@Override
	public void addLocalIdentifierToIdentifier(String domainName, IdentifierDTO identifierDTO, List<IdentifierDTO> localIds)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		addLocalIdentifierToIdentifierInternal(null, domainName, identifierDTO, localIds);
	}

	@Override
	public Map<IdentifierDTO, IdentifierDeletionResult> removeLocalIdentifier(String domainName, List<IdentifierDTO> localIds)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		return removeLocalIdentifierInternal(null, domainName, localIds);
	}

	// ***********************************
	// possible matches
	// ***********************************

	/**
	 * Returns all possible matches for the given domain.
	 *
	 * @param domainName
	 *            the name of the domain
	 */
	@Override
	public List<PossibleMatchDTO> getPossibleMatchesForDomain(String domainName)
			throws InvalidParameterException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("getPossibleMatchesForDomain " + domainName);
		}
		else
		{
			logger.info("getPossibleMatchesForDomain");
		}
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		List<PossibleMatchDTO> result = dao.getPossibleMatchesForDomain(domainName);
		logger.info("found " + result.size() + " possible matches for domainName " + domainName);
		return result;
	}

	/**
	 * Counts matching {@link PossibleMatchDTO} entries.
	 * If {@link IdentityField#NONE} is the only key in the filter map, then search for the corresponding
	 * pattern in all required fields (as defined in the configuration container) linked by OR (disjunction),
	 * otherwise search in all given fields for the respective pattern linked by AND (conjunction).
	 *
	 * @param domainName
	 *            the name of the domain
	 * @return number of matching {@link PossibleMatchDTO} entries.
	 * @throws UnknownObjectException
	 *             for a wrong domainName name
	 */
	@Override
	public long countPossibleMatchesForDomain(String domainName)
			throws InvalidParameterException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("countPossibleMatchesForDomain " + domainName);
		}
		else
		{
			logger.info("countPossibleMatchesForDomain");
		}
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		return dao.countPossibleMatchesForDomain(domainName);
	}

	/**
	 * Returns matching {@link PossibleMatchDTO} entries.
	 * If {@link IdentityField#NONE} is the only key in the filter map, then search for the corresponding
	 * pattern in all required fields (as defined in the configuration container) linked by OR (disjunction),
	 * otherwise search in all given fields for the respective pattern linked by AND (conjunction).
	 *
	 * @param domainName
	 *            the name of the domain
	 * @param pc
	 *            the pagination configuration
	 * @return matching {@link PossibleMatchDTO} entries
	 * @throws UnknownObjectException
	 *             for a wrong domainName name
	 */
	@Override
	public List<PossibleMatchDTO> getPossibleMatchesForDomainFiltered(String domainName, PaginationConfig pc)
			throws InvalidParameterException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("getPossibleMatchesForDomainFiltered " + domainName + " with " + pc);
		}
		else
		{
			logger.info("getPossibleMatchesForDomainFiltered");
		}
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		List<PossibleMatchDTO> result = dao.getPossibleMatchesForDomainFiltered(domainName, pc);
		logger.info("found " + result.size() + " possible matches for domainName " + domainName);
		return result;
	}

	/**
	 * Counts matching {@link PossibleMatchDTO} entries.
	 * If {@link IdentityField#NONE} is the only key in the filter map, then search for the corresponding
	 * pattern in all required fields (as defined in the configuration container) linked by OR (disjunction),
	 * otherwise search in all given fields for the respective pattern linked by AND (conjunction).
	 *
	 * @param domainName
	 *            the name of the domain
	 * @param pc
	 *            the pagination configuration
	 * @return number of matching {@link PossibleMatchDTO} entries.
	 * @throws UnknownObjectException
	 *             for a wrong domainName name
	 */
	@Override
	public long countPossibleMatchesForDomainFiltered(String domainName, PaginationConfig pc)
			throws InvalidParameterException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("countPossibleMatchesForDomainFiltered for domain " + domainName + " with " + pc);
		}
		else
		{
			logger.info("countPossibleMatchesForDomainFiltered");
		}
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		return dao.countPossibleMatchesForDomainFiltered(domainName, pc);
	}

	@Override
	public List<PossibleMatchForMPIDTO> getPossibleMatchesForPerson(String domainName, String mpiId)
			throws InvalidParameterException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("getPossibleMatchesForPerson for domain " + domainName + " and mpiId " + mpiId);
		}
		else
		{
			logger.info("getPossibleMatchesForPerson");
		}
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		checkParameter(mpiId, "mpiId");
		List<PossibleMatchForMPIDTO> result = dao.getPossibleMatchesByPerson(domainName, mpiId);
		logger.info("found " + result.size() + " possible matches for mpi " + mpiId);
		return result;
	}

	@Override
	public void removePossibleMatches(List<Long> possibleMatchIds, String comment)
			throws InvalidParameterException, MPIException
	{
		removePossibleMatchesInternal(possibleMatchIds, comment);
	}

	@Override
	public void removePossibleMatch(long possibleMatchId, String comment)
			throws InvalidParameterException, MPIException
	{
		removePossibleMatchInternal(possibleMatchId, comment);
	}

	@Override
	public boolean prioritizePossibleMatch(long linkId, PossibleMatchPriority priority) throws InvalidParameterException, MPIException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("prioritizePossibleMatch with id " + linkId + " as " + priority);
		}
		else
		{
			logger.info("prioritizePossibleMatch");
		}
		checkAllowedPossibleMatchId(linkId);
		boolean result = dao.prioritizePossibleMatch(linkId, priority);
		logger.info("possible match prioritized");
		return result;
	}

	@Override
	public void assignIdentity(long possibleMatchId, long winningIdentityId, String comment)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		assignIdentityInternal(null, possibleMatchId, winningIdentityId, comment);
	}

	@Override
	public void moveIdentitiesForIdentifierToPerson(String domainName, IdentifierDTO identifier, String mpiId, String comment)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		moveIdentitiesForIdentifierToPersonInternal(null, domainName, identifier, mpiId, comment);
	}

	@Override
	public PossibleMatchDTO externalPossibleMatchForPerson(String domainName, String mpiId, String aliasMpiId)
			throws DuplicateEntryException, InvalidParameterException, MPIException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("externalPossibleMatchForPerson between persons with mpi " + mpiId + " and " + aliasMpiId);
		}
		else
		{
			logger.info("externalPossibleMatchForPerson");
		}
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		checkParameter(mpiId, "mpiId");
		checkParameter(aliasMpiId, "aliasMpiId");
		PossibleMatchDTO result = dao.externalPossibleMatchForPerson(domainName, mpiId, aliasMpiId);
		logger.info("possible match created or found");
		return result;
	}

	@Override
	public PossibleMatchDTO externalPossibleMatchForIdentity(String domainName, long identityId, long aliasIdentityId)
			throws DuplicateEntryException, InvalidParameterException, MPIException, UnknownObjectException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("externalPossibleMatchForIdentity between identities with id " + identityId + " and " + aliasIdentityId);
		}
		else
		{
			logger.info("externalPossibleMatchForIdentity");
		}
		checkParameter(domainName, "domainName");
		checkAllowedDomain(domainName);
		PossibleMatchDTO result = dao.externalPossibleMatchForIdentity(domainName, identityId, aliasIdentityId);
		logger.info("possible match created or found");
		return result;
	}

}
