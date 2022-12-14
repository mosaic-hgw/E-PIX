package org.emau.icmvc.ttp.epix.persistence;

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

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.emau.icmvc.ttp.deduplication.model.MatchResult;
import org.emau.icmvc.ttp.deduplication.model.MatchResult.DECISION;
import org.emau.icmvc.ttp.epix.common.exception.DuplicateEntryException;
import org.emau.icmvc.ttp.epix.common.exception.IllegalOperationException;
import org.emau.icmvc.ttp.epix.common.exception.InvalidParameterException;
import org.emau.icmvc.ttp.epix.common.exception.MPIErrorCode;
import org.emau.icmvc.ttp.epix.common.exception.MPIException;
import org.emau.icmvc.ttp.epix.common.exception.ObjectInUseException;
import org.emau.icmvc.ttp.epix.common.exception.UnknownObjectException;
import org.emau.icmvc.ttp.epix.common.exception.UnknownObjectType;
import org.emau.icmvc.ttp.epix.common.model.ContactHistoryDTO;
import org.emau.icmvc.ttp.epix.common.model.ContactInDTO;
import org.emau.icmvc.ttp.epix.common.model.ContactOutDTO;
import org.emau.icmvc.ttp.epix.common.model.DomainDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentifierDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentifierDomainDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityHistoryDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityInBaseDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityInDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityOutDTO;
import org.emau.icmvc.ttp.epix.common.model.PersonDTO;
import org.emau.icmvc.ttp.epix.common.model.PersonHistoryDTO;
import org.emau.icmvc.ttp.epix.common.model.PossibleMatchDTO;
import org.emau.icmvc.ttp.epix.common.model.PossibleMatchForMPIDTO;
import org.emau.icmvc.ttp.epix.common.model.PossibleMatchHistoryDTO;
import org.emau.icmvc.ttp.epix.common.model.RequestConfig;
import org.emau.icmvc.ttp.epix.common.model.ResponseEntryDTO;
import org.emau.icmvc.ttp.epix.common.model.SourceDTO;
import org.emau.icmvc.ttp.epix.common.model.StatisticDTO;
import org.emau.icmvc.ttp.epix.common.model.config.ConfigurationContainer;
import org.emau.icmvc.ttp.epix.common.model.config.DeduplicationDTO;
import org.emau.icmvc.ttp.epix.common.model.config.ReasonDTO;
import org.emau.icmvc.ttp.epix.common.model.enums.IdentityField;
import org.emau.icmvc.ttp.epix.common.model.enums.IdentityHistoryEvent;
import org.emau.icmvc.ttp.epix.common.model.enums.MatchingMode;
import org.emau.icmvc.ttp.epix.common.model.enums.PersonField;
import org.emau.icmvc.ttp.epix.common.model.enums.PossibleMatchSolution;
import org.emau.icmvc.ttp.epix.common.utils.PaginationConfig;
import org.emau.icmvc.ttp.epix.common.utils.StatisticKeys;
import org.emau.icmvc.ttp.epix.internal.PreprocessedCacheObject;
import org.emau.icmvc.ttp.epix.pdqquery.model.SearchMask;
import org.emau.icmvc.ttp.epix.persistence.model.Contact;
import org.emau.icmvc.ttp.epix.persistence.model.ContactHistory;
import org.emau.icmvc.ttp.epix.persistence.model.Domain;
import org.emau.icmvc.ttp.epix.persistence.model.Identifier;
import org.emau.icmvc.ttp.epix.persistence.model.IdentifierDomain;
import org.emau.icmvc.ttp.epix.persistence.model.IdentifierId;
import org.emau.icmvc.ttp.epix.persistence.model.Identity;
import org.emau.icmvc.ttp.epix.persistence.model.IdentityHistory;
import org.emau.icmvc.ttp.epix.persistence.model.IdentityLink;
import org.emau.icmvc.ttp.epix.persistence.model.IdentityLinkHistory;
import org.emau.icmvc.ttp.epix.persistence.model.IdentityLink_;
import org.emau.icmvc.ttp.epix.persistence.model.IdentityPreprocessed;
import org.emau.icmvc.ttp.epix.persistence.model.Identity_;
import org.emau.icmvc.ttp.epix.persistence.model.Person;
import org.emau.icmvc.ttp.epix.persistence.model.PersonHistory;
import org.emau.icmvc.ttp.epix.persistence.model.Source;
import org.emau.icmvc.ttp.epix.persistence.model.Statistic;
import org.emau.icmvc.ttp.epix.persistence.model.Statistic_;
import org.emau.icmvc.ttp.epix.service.NotificationSender;

import it.unimi.dsi.fastutil.longs.LongList;

/**
 * central data access point (db and cache) - public parts
 * <p>
 * purpose of this class: conversion of dtos and read-write-lock for data access<br>
 * catching errors while updating db (see calls to PublicDAO self)<br>
 * setting timestamps<br>
 * sending notifications
 *
 * @author geidell
 */
@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class PublicDAO extends DAO
{
	private static final Logger logger = LogManager.getLogger(PublicDAO.class);
	private static final ReentrantReadWriteLock statisticRWL = new ReentrantReadWriteLock();
	protected static final ReentrantReadWriteLock emRWL = new ReentrantReadWriteLock();
	// extra lock fuer besseres multithreading bei requestMPI - immer nur eine matchende identitaet wird gleichzeitg prozessiert (aber durchaus mehrere nicht matchende)
	// Map<PreprocessedCacheObject, Set<PreprocessedCacheObject>> geht leider nicht, da eine identitaet mehrfach reinkommen kann
	// ausserdem kann sich das PreprocessedCacheObject waehrend des matchings aendern (identityId + personId)
	private static final Map<Thread, PreprocessedCacheObject> MULTITHREADING_CACHE_MAP = new HashMap<>();
	private static final Map<Thread, Set<Thread>> MULTITHREADING_BLOCK_MAP = new HashMap<>();

	@EJB
	private NotificationSender notificationSender;

	/**
	 * this is the only way to make it possible to catch a {@link PersistenceException}<br>
	 * (which is thrown after leaving the method where it occurs)
	 * <p>
	 * see https://www.javacodegeeks.com/2013/03/jpa-and-cmt-why-catching-persistence-exception-is-not-enough.html
	 */
	@EJB
	private PublicDAO self;

	@PostConstruct
	private void init()
	{
		emRWL.writeLock().lock();
		try
		{
			logger.debug("init start");
			initCache();
			logger.debug("init done");
		}
		finally
		{
			emRWL.writeLock().unlock();
		}
	}

	private void registerMatchBlock(String domainName, PreprocessedCacheObject ppco) throws MPIException, UnknownObjectException
	{
		Set<Thread> set = new HashSet<>();
		synchronized (MULTITHREADING_BLOCK_MAP)
		{
			if (logger.isDebugEnabled())
			{
				logger.debug("register match block for " + ppco);
			}
			for (Entry<Thread, PreprocessedCacheObject> entry : MULTITHREADING_CACHE_MAP.entrySet())
			{
				MatchResult matchResult = DOMAIN_CACHE.directMatch(domainName, ppco, entry.getValue());
				if (!matchResult.getDecision().equals(DECISION.NO_MATCH))
				{
					set.add(entry.getKey());
				}
			}
			MULTITHREADING_CACHE_MAP.put(Thread.currentThread(), ppco);
			MULTITHREADING_BLOCK_MAP.put(Thread.currentThread(), set);
		}
		if (logger.isDebugEnabled())
		{
			logger.debug(Thread.currentThread() + " is waiting for " + set.size() + " other identities");
		}
		// TODO: synchronization over a local variable makes no sense and has no effect - remove or replace by sync over an instance variable
		synchronized (set)
		{
			while (!set.isEmpty())
			{
				try
				{
					set.wait(); // das set wird als monitor-object genutzt
				}
				catch (InterruptedException e)
				{
					String message = "unexpected exception while waiting for matching";
					logger.error(message, e);
					Thread.currentThread().interrupt();
					throw new MPIException(MPIErrorCode.INTERNAL_ERROR, message, e);
				}
			}
		}
	}

	private void deregisterMatchBlock()
	{
		synchronized (MULTITHREADING_BLOCK_MAP)
		{
			if (logger.isDebugEnabled())
			{
				logger.debug("deregister match block for " + Thread.currentThread());
			}
			MULTITHREADING_CACHE_MAP.remove(Thread.currentThread());
			MULTITHREADING_BLOCK_MAP.remove(Thread.currentThread());
			for (Entry<Thread, Set<Thread>> entry : MULTITHREADING_BLOCK_MAP.entrySet())
			{
				synchronized (entry.getValue())
				{
					if (!entry.getValue().isEmpty())
					{
						entry.getValue().remove(Thread.currentThread());
						if (entry.getValue().isEmpty())
						{
							if (logger.isDebugEnabled())
							{
								logger.debug("all matching identites for " + entry.getKey() + " are processed, notifying the thread to continue");
							}
							entry.getValue().notifyAll(); // das set wird als monitor-object genutzt
						}
					}
				}
			}
		}
	}

	protected void handlePersistenceException(EJBException e) throws MPIException
	{
		String message = "exception while updating db: " + e.getMessage();
		logger.error(message, e);
		throw new MPIException(MPIErrorCode.INTERNAL_ERROR, message);
	}

	// ***********************************
	// requestMPI
	// ***********************************
	public ResponseEntryDTO handleMPIRequest(String notificationClientID, String domainName, String sourceName, IdentityInBaseDTO identityDTO,
			String comment, RequestConfig requestConfig) throws InvalidParameterException, MPIException, UnknownObjectException
	{
		logger.debug("handleMPIRequest start");
		// diese funktion gibt niemals "null" zurueck, eclipse erkennt nicht, dass die funktion vorher auf jeden fall eine exception wirft
		ResponseEntryDTO result = null;
		// ausnahme! hier wird das read-/writelock in DAO.java an entsprechenden stellen gesetzt
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());

		Identity identityForBlocking = new Identity(identityDTO, new ArrayList<>(), new ArrayList<>(), null, null, false, timestamp);
		IdentityPreprocessed identityPPForBlocking = DOMAIN_CACHE.preprocess(domainName, identityForBlocking);
		PreprocessedCacheObject ppcoForBlocking = DOMAIN_CACHE.createPreprocessedCacheObjectFromIdentityPP(domainName, identityPPForBlocking);
		emRWL.readLock().lock(); // hier reicht readlock wegen spezialabsicherung ueber MULTITHREADING_BLOCK_MAP
		try
		{
			registerMatchBlock(domainName, ppcoForBlocking);
			result = self.saveMPIRequest(domainName, sourceName, identityDTO, comment, requestConfig, timestamp);
		}
		catch (EJBException e)
		{
			handlePersistenceException(e);
		}
		finally
		{
			deregisterMatchBlock();
			emRWL.readLock().unlock();
		}

		if (notificationClientID != null && result != null)
		{
			notificationSender.sendHandleRequestNotification(notificationClientID, result.getPerson(), domainName, comment);
		}

		logger.debug("handleMPIRequest end");
		return result;
	}

	// ***********************************
	// search
	// ***********************************
	public List<PersonDTO> findPersonsByPDQ(SearchMask mask, LongList personIds) throws MPIException, UnknownObjectException
	{
		logger.debug("findPersonsByPDQ");
		emRWL.readLock().lock();
		try
		{
			LongList personIDs = DOMAIN_CACHE.findPersonIdsByPDQ(mask, personIds);
			List<PersonDTO> result = new ArrayList<>();
			for (long id : personIDs)
			{
				result.add(getDBPersonById(id).toDTO());
			}
			logger.debug("found " + result.size() + " persons");
			return result;
		}
		finally
		{
			emRWL.readLock().unlock();
		}
	}

	// ***********************************
	// domains
	// ***********************************
	public DomainDTO getDomain(String domainName) throws UnknownObjectException
	{
		logger.debug("getDomain for " + domainName);
		emRWL.readLock().lock();
		try
		{
			return getDBDomain(domainName).toDTO();
		}
		finally
		{
			emRWL.readLock().unlock();
		}
	}

	public List<DomainDTO> getDomains()
	{
		logger.debug("getDomains");
		emRWL.readLock().lock();
		try
		{
			List<Domain> temp = DOMAIN_CACHE.getDomains();
			List<DomainDTO> result = new ArrayList<>();
			for (Domain domain : temp)
			{
				result.add(domain.toDTO());
			}
			logger.debug("found " + result.size() + " domains");
			return result;
		}
		finally
		{
			emRWL.readLock().unlock();
		}
	}

	public DomainDTO addDomain(DomainDTO dto) throws DuplicateEntryException, InvalidParameterException, MPIException, UnknownObjectException
	{
		DomainDTO result = null;
		logger.debug("addDomain");
		emRWL.writeLock().lock();
		try
		{
			try
			{
				getDomain(dto.getName());
				String message = "domain already exists: " + dto.getName();
				logger.error(message);
				throw new DuplicateEntryException(message);
			}
			catch (UnknownObjectException expected)
			{
				try
				{
					Domain domain = self.saveDomain(dto, new Timestamp(System.currentTimeMillis()));
					self.addDomainToCache(domain);
					result = domain.toDTO();
					logger.debug("domain added");
				}
				catch (EJBException e)
				{
					handlePersistenceException(e);
				}
			}
		}
		finally
		{
			emRWL.writeLock().unlock();
		}
		return result;
	}

	public DomainDTO updateDomain(DomainDTO domainDTO) throws MPIException, ObjectInUseException, UnknownObjectException
	{
		DomainDTO result = null;
		logger.debug("updateDomain");
		emRWL.writeLock().lock();
		try
		{
			Domain domain = getDBDomain(domainDTO.getName());
			if (domain.isInUse())
			{
				String message = "can't update domain " + domainDTO.getName() + " because there are persons registered under this domain";
				logger.error(message);
				throw new ObjectInUseException(message);
			}
			result = self.updateDomain(domain, domainDTO).toDTO();
			logger.debug("domain updated");
		}
		catch (EJBException e)
		{
			handlePersistenceException(e);
		}
		finally
		{
			emRWL.writeLock().unlock();
		}
		return result;
	}

	public DomainDTO updateDomainInUse(String domainName, String label, String description) throws MPIException, UnknownObjectException
	{
		DomainDTO result = null;
		logger.debug("updateDomainInUse");
		emRWL.writeLock().lock();
		try
		{
			Domain domain = getDBDomain(domainName);
			result = self.updateDomainInUse(domain, label, description).toDTO();
			logger.debug("domain updated");
		}
		catch (EJBException e)
		{
			handlePersistenceException(e);
		}
		finally
		{
			emRWL.writeLock().unlock();
		}
		return result;
	}

	public void deleteDomain(String domainName, boolean force) throws MPIException, ObjectInUseException, UnknownObjectException
	{
		logger.debug("deleteDomain " + domainName + ", force=" + force);
		emRWL.writeLock().lock();
		try
		{
			self.deleteDBDomain(domainName, force);
			logger.debug("domain deleted");
		}
		catch (EJBException e)
		{
			handlePersistenceException(e);
		}
		finally
		{
			emRWL.writeLock().unlock();
		}
	}

	public MatchingMode getMatchingMode(String domainName) throws UnknownObjectException
	{
		logger.debug("getMatchingMode");
		emRWL.readLock().lock();
		try
		{
			Domain domain = getDBDomain(domainName);
			return domain.getMatchingConfiguration().getMatchingMode();
		}
		finally
		{
			emRWL.readLock().unlock();
		}
	}

	public ConfigurationContainer getConfigurationContainerForDomain(String domainName) throws UnknownObjectException
	{
		return DOMAIN_CACHE.getConfigurationContainerForDomain(domainName);
	}

	public List<ReasonDTO> getDefinedDeduplicationReasons(String domainName) throws UnknownObjectException
	{
		DeduplicationDTO tmp = DOMAIN_CACHE.getConfigurationContainerForDomain(domainName).getDeduplication();

		List<ReasonDTO> result = new ArrayList<>();

		if (tmp != null)
		{
			result = tmp.getReasons();
		}

		return result;
	}

	// ***********************************
	// identifier domains
	// ***********************************
	public IdentifierDomainDTO addIdentifierDomain(IdentifierDomainDTO identifierDomain) throws DuplicateEntryException, MPIException
	{
		IdentifierDomainDTO result = null;
		logger.debug("addIdentifierDomain");
		emRWL.writeLock().lock();
		try
		{
			getIdentifierDomain(identifierDomain.getName());
			String message = "identifier domain already exists: " + identifierDomain.getName();
			logger.error(message);
			throw new DuplicateEntryException(message);
		}
		catch (UnknownObjectException expected)
		{
			try
			{
				result = self.saveIdentifierDomain(identifierDomain, new Timestamp(System.currentTimeMillis())).toDTO();
			}
			catch (EJBException e)
			{
				handlePersistenceException(e);
			}
			logger.debug("identifier domain added");
		}
		finally
		{
			emRWL.writeLock().unlock();
		}
		return result;
	}

	public IdentifierDomainDTO getIdentifierDomainByOID(String oid) throws UnknownObjectException
	{
		logger.debug("getIdentifierDomainByOID for oid " + oid);
		emRWL.readLock().lock();
		try
		{
			IdentifierDomain id = (IdentifierDomain) em.createNamedQuery("IdentifierDomain.findByOID").setParameter("oid", oid).getSingleResult();
			logger.debug("identifier domain found");
			return id.toDTO();
		}
		catch (NoResultException e)
		{
			String message = "identifier domain not found, oid: " + oid;
			logger.warn(message);
			throw new UnknownObjectException(message, UnknownObjectType.IDENTITFIER_DOMAIN, oid);
		}
		finally
		{
			emRWL.readLock().unlock();
		}
	}

	public IdentifierDomainDTO getIdentifierDomain(String name) throws UnknownObjectException
	{
		logger.debug("getIdentifierDomain for " + name);
		emRWL.readLock().lock();
		try
		{
			return getDBIdentifierDomain(name).toDTO();
		}
		finally
		{
			emRWL.readLock().unlock();
		}
	}

	public List<IdentifierDomainDTO> getIdentifierDomains()
	{
		logger.debug("getIdentifierDomains");
		List<IdentifierDomainDTO> result = new ArrayList<>();
		emRWL.readLock().lock();
		try
		{
			List<IdentifierDomain> identifierDomains = getDBIdentifierDomains();
			for (IdentifierDomain identifierDomain : identifierDomains)
			{
				result.add(identifierDomain.toDTO());
			}
			logger.debug("found " + result.size() + " identifier domains");
			return result;
		}
		finally
		{
			emRWL.readLock().unlock();
		}
	}

	public IdentifierDomainDTO updateIdentifierDomain(IdentifierDomainDTO identifierDomainDTO) throws MPIException, UnknownObjectException
	{
		IdentifierDomainDTO result = null;
		logger.debug("updateIdentifierDomain");
		emRWL.writeLock().lock();
		try
		{
			IdentifierDomain identifierDomain = self.updateDBIdentifierDomain(identifierDomainDTO);
			for (Domain domain : DOMAIN_CACHE.getDomains())
			{
				if (domain.getMpiDomain().equals(identifierDomain))
				{
					domain.setMpiDomain(identifierDomain); // update nicht-key-felder
				}
			}
			result = identifierDomain.toDTO();
			logger.debug("identifier domain updated");
		}
		catch (EJBException e)
		{
			handlePersistenceException(e);
		}
		finally
		{
			emRWL.writeLock().unlock();
		}
		return result;
	}

	public void deleteIdentifierDomain(String name) throws MPIException, ObjectInUseException, UnknownObjectException
	{
		logger.debug("deleteIdentifierDomain");
		emRWL.writeLock().lock();
		try
		{
			self.deleteDBIdentifierDomain(name);
			logger.debug("identifier domain deleted");
		}
		catch (EJBException e)
		{
			handlePersistenceException(e);
		}
		finally
		{
			emRWL.writeLock().unlock();
		}
	}

	// ***********************************
	// identifier
	// ***********************************
	public void addIdentifierToPerson(String notificationClientID, String domainName, String mpiId, List<IdentifierDTO> localIds)
			throws MPIException, UnknownObjectException
	{
		logger.debug("addIdentifierToPerson");
		emRWL.writeLock().lock();
		try
		{
			self.saveIdentifierToPerson(domainName, mpiId, localIds, new Timestamp(System.currentTimeMillis()));
		}
		catch (EJBException e)
		{
			handlePersistenceException(e);
		}
		finally
		{
			emRWL.writeLock().unlock();
		}

		if (notificationClientID != null)
		{
			notificationSender.sendAddIdentifierToPersonNotification(notificationClientID, domainName, mpiId, localIds);
		}

		logger.debug("identifier added");
	}

	public void addLocalIdentifierToIdentifier(String notificationClientID, String domainName, IdentifierDTO identifierDTO,
			List<IdentifierDTO> localIds) throws MPIException, UnknownObjectException
	{
		logger.debug("addLocalIdentifierToIdentifier");
		emRWL.writeLock().lock();
		try
		{
			self.saveLocalIdentifierToIdentifier(domainName, identifierDTO, localIds, new Timestamp(System.currentTimeMillis()));
		}
		catch (EJBException e)
		{
			handlePersistenceException(e);
		}
		finally
		{
			emRWL.writeLock().unlock();
		}

		if (notificationClientID != null)
		{
			notificationSender.sendAddLocalIdentifierToIdentifierNotification(notificationClientID, domainName, identifierDTO, localIds);
		}

		logger.debug("local identifier added");
	}

	// TODO wird nirgends benutzt - weg?
	public IdentifierDTO getIdentifierById(IdentifierId id) throws UnknownObjectException
	{
		logger.debug("getIdentifierById for " + id);
		emRWL.readLock().lock();
		try
		{
			return getDBIdentifierById(id).toDTO();
		}
		finally
		{
			emRWL.readLock().unlock();
		}
	}

	// ***********************************
	// sources
	// ***********************************
	public List<SourceDTO> getSources()
	{
		logger.debug("getSources");
		emRWL.readLock().lock();
		try
		{
			CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
			CriteriaQuery<Source> criteriaQuery = criteriaBuilder.createQuery(Source.class);
			Root<Source> root = criteriaQuery.from(Source.class);
			criteriaQuery.select(root);
			List<Source> sources = em.createQuery(criteriaQuery).getResultList();
			List<SourceDTO> result = new ArrayList<>();
			for (Source source : sources)
			{
				result.add(source.toDTO());
			}
			logger.debug("found " + result.size() + " sources");
			return result;
		}
		finally
		{
			emRWL.readLock().unlock();
		}
	}

	public SourceDTO getSource(String name) throws UnknownObjectException
	{
		logger.debug("getSourceByName for " + name);
		emRWL.readLock().lock();
		try
		{
			return getDBSource(name).toDTO();
		}
		finally
		{
			emRWL.readLock().unlock();
		}
	}

	public SourceDTO addSource(SourceDTO source) throws DuplicateEntryException, MPIException
	{
		SourceDTO result = null;
		logger.debug("addSource");
		emRWL.writeLock().lock();
		try
		{
			getSource(source.getName());
			String message = "source already exists: " + source.getName();
			logger.error(message);
			throw new DuplicateEntryException(message);
		}
		catch (UnknownObjectException expected)
		{
			try
			{
				result = self.saveSource(source, new Timestamp(System.currentTimeMillis())).toDTO();
			}
			catch (EJBException e)
			{
				handlePersistenceException(e);
			}
		}
		finally
		{
			emRWL.writeLock().unlock();
		}
		return result;
	}

	public SourceDTO updateSource(SourceDTO sourceDTO) throws MPIException, UnknownObjectException
	{
		SourceDTO result = null;
		logger.debug("updateSource");
		emRWL.writeLock().lock();
		try
		{
			Source source = self.updateDBSource(sourceDTO);
			for (Domain domain : DOMAIN_CACHE.getDomains())
			{
				if (domain.getSafeSource().equals(source))
				{
					domain.setSafeSource(source); // update nicht-key-felder
				}
			}
			result = source.toDTO();
			logger.debug("source updated");
		}
		catch (EJBException e)
		{
			handlePersistenceException(e);
		}
		finally
		{
			emRWL.writeLock().unlock();
		}
		return result;
	}

	public void deleteSource(String sourceName) throws MPIException, ObjectInUseException, UnknownObjectException
	{
		logger.debug("deleteSource");
		emRWL.writeLock().lock();
		try
		{
			self.deleteDBSource(sourceName);
			logger.debug("source deleted");
		}
		catch (EJBException e)
		{
			handlePersistenceException(e);
		}
		finally
		{
			emRWL.writeLock().unlock();
		}
	}

	// ***********************************
	// persons
	// ***********************************
	public PersonDTO getPersonById(long id) throws UnknownObjectException
	{
		logger.debug("getPersonById");
		emRWL.readLock().lock();
		try
		{
			return getDBPersonById(id).toDTO();
		}
		finally
		{
			emRWL.readLock().unlock();
		}
	}

	public PersonDTO getPersonByLocalIdentifier(String domainName, IdentifierDTO identifierDTO) throws UnknownObjectException
	{
		logger.debug("getPersonByLocalIdentifier");
		emRWL.readLock().lock();
		try
		{
			Domain domain = getDBDomain(domainName);
			// zum test, ob diese exisitiert
			getIdentifierDomain(identifierDTO.getIdentifierDomain().getName());
			IdentifierId identifierId = new IdentifierId(identifierDTO);
			Identifier identifier = getDBIdentifierById(identifierId);
			return getDBPersonByLocalIdentifier(domain, identifier).toDTO();
		}
		finally
		{
			emRWL.readLock().unlock();
		}
	}

	public PersonDTO getPersonByMultipleLocalIdentifier(String domainName, List<IdentifierDTO> identifierDTOs, boolean allIdentifierRequired)
			throws MPIException, UnknownObjectException
	{
		logger.debug("getPersonByMultipleLocalIdentifier");
		emRWL.readLock().lock();
		try
		{
			Domain domain = getDBDomain(domainName);
			List<Identifier> identifier = new ArrayList<>();
			for (IdentifierDTO identDTO : identifierDTOs)
			{
				// zum test, ob diese exisitiert
				getIdentifierDomain(identDTO.getIdentifierDomain().getName());
				IdentifierId identifierId = new IdentifierId(identDTO);
				try
				{
					identifier.add(getDBIdentifierById(identifierId));
				}
				catch (UnknownObjectException maybe)
				{
					if (allIdentifierRequired)
					{
						logger.error(maybe.getMessage());
						throw maybe;
					}
					else
					{
						logger.warn(maybe.getMessage());
					}
				}
			}
			if (identifier.isEmpty())
			{
				StringBuffer sb = new StringBuffer("all given identifiers (");
				for (IdentifierDTO identifierDTO : identifierDTOs)
				{
					sb.append(identifierDTO.toShortString());
					sb.append(' ');
				}
				sb.append(") are unknown within domain ");
				sb.append(domain.getName());
				String message = sb.toString();
				logger.error(message);
				throw new UnknownObjectException(message, UnknownObjectType.PERSON, "multiple identifier - see message");
			}
			return getDBPersonByMultipleLocalIdentifier(domain, identifier, allIdentifierRequired, false).toDTO();
		}
		finally
		{
			emRWL.readLock().unlock();
		}
	}

	public List<PersonDTO> getPersons(String domainName, Map<PersonField, String> filter, boolean filterIsCaseSensitive)
			throws UnknownObjectException
	{
		logger.debug("getPersons for domain " + domainName);
		List<PersonDTO> result = new ArrayList<>();
		emRWL.readLock().lock();
		try
		{
			Domain domain = getDBDomain(domainName);
			CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
			CriteriaQuery<Person> criteriaQuery = criteriaBuilder.createQuery(Person.class);
			Root<Person> root = criteriaQuery.from(Person.class);
			Predicate predicate = PaginatedHelper.generateWherePredicateForPerson(criteriaBuilder, root, domain, filter, filterIsCaseSensitive);
			criteriaQuery.select(root).where(predicate);
			List<Person> persons = em.createQuery(criteriaQuery).getResultList();

			for (Person person : persons)
			{
				result.add(person.toDTO());
			}
			return result;
		}
		finally
		{
			emRWL.readLock().unlock();
		}
	}

	public List<PersonDTO> getPersonsPaginated(String domainName, int firstEntry, int pageSize, PersonField sortField, boolean sortIsAscending,
			Map<PersonField, String> filter, boolean filterIsCaseSensitive) throws UnknownObjectException
	{
		logger.debug("getPersonsPaginated for domain " + domainName);
		List<PersonDTO> result = new ArrayList<>();
		emRWL.readLock().lock();
		try
		{
			Domain domain = getDBDomain(domainName);
			CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
			CriteriaQuery<Person> criteriaQuery = criteriaBuilder.createQuery(Person.class);
			Root<Person> root = criteriaQuery.from(Person.class);
			Predicate predicate = PaginatedHelper.generateWherePredicateForPerson(criteriaBuilder, root, domain, filter, filterIsCaseSensitive);
			criteriaQuery.select(root).where(predicate);
			Expression<?> order = PaginatedHelper.generateSortExpressionForPerson(sortField, root);
			if (order != null)
			{
				if (sortIsAscending)
				{
					criteriaQuery.orderBy(criteriaBuilder.asc(order));
				}
				else
				{
					criteriaQuery.orderBy(criteriaBuilder.desc(order));
				}
			}
			List<Person> persons = em.createQuery(criteriaQuery).setFirstResult(firstEntry).setMaxResults(pageSize).getResultList();

			for (Person person : persons)
			{
				result.add(person.toDTO());
			}
			return result;
		}
		finally
		{
			emRWL.readLock().unlock();
		}
	}

	public List<PersonDTO> getDeactivatedPersons(String domainName) throws UnknownObjectException
	{
		logger.debug("getDeactivatedPersons for domain " + domainName);
		emRWL.readLock().lock();
		try
		{
			Domain domain = getDBDomain(domainName);
			@SuppressWarnings("unchecked")
			List<Person> temp = em.createNamedQuery("Person.findDeactivatedByDomain").setParameter("domain", domain).getResultList();
			logger.debug("found " + temp.size() + " persons");
			List<PersonDTO> result = new ArrayList<>();
			for (Person person : temp)
			{
				result.add(person.toDTO());
			}
			return result;
		}
		finally
		{
			emRWL.readLock().unlock();
		}
	}

	public PersonDTO getPersonByMPI(String domainName, String mpiId) throws UnknownObjectException
	{
		logger.debug("getPersonByMPI for mpi " + mpiId + " within domain " + domainName);
		emRWL.readLock().lock();
		try
		{
			Domain domain = getDBDomain(domainName);
			return getDBPersonByMPI(domain, mpiId).toDTO();
		}
		finally
		{
			emRWL.readLock().unlock();
		}
	}

	public ResponseEntryDTO updatePerson(String notificationClientID, String domainName, String mpiId, IdentityInDTO identityDTO, String sourceName,
			boolean force, String comment, RequestConfig requestConfig) throws InvalidParameterException, MPIException, UnknownObjectException
	{
		logger.debug("updatePersonWithConfig start");
		// diese funktion gibt niemals "null" zurueck, eclipse erkennt nicht, dass die funktion vorher auf jeden fall eine exception wirft
		ResponseEntryDTO result = null;
		emRWL.writeLock().lock();
		try
		{
			result = self.updateDBPerson(domainName, mpiId, identityDTO, sourceName, force, comment, requestConfig,
					new Timestamp(System.currentTimeMillis()));
			logger.debug("updatePersonWithConfig end");
		}
		catch (EJBException e)
		{
			handlePersistenceException(e);
		}
		finally
		{
			emRWL.writeLock().unlock();
		}

		if (notificationClientID != null && result != null)
		{
			notificationSender.sendUpdatePersonNotification(notificationClientID, result.getPerson(), domainName, comment);
		}

		return result;
	}

	public ResponseEntryDTO addPerson(String notificationClientID, String domainName, IdentityInDTO identityDTO, String sourceName, String comment)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		logger.debug("addPerson start");
		// diese funktion gibt niemals "null" zurueck, eclipse erkennt nicht, dass die funktion vorher auf jeden fall eine exception wirft
		ResponseEntryDTO result = null;
		emRWL.writeLock().lock();
		try
		{
			result = self.addDBPerson(domainName, identityDTO, sourceName, comment, new Timestamp(System.currentTimeMillis()));
			logger.debug("addPerson end");
		}
		catch (EJBException e)
		{
			handlePersistenceException(e);
		}
		finally
		{
			emRWL.writeLock().unlock();
		}

		if (notificationClientID != null && result != null)
		{
			notificationSender.sendAddPersonNotification(notificationClientID, result.getPerson(), domainName, comment);
		}

		return result;
	}

	public void deactivatePerson(String notificationClientID, String domainName, String mpiId) throws InvalidParameterException, MPIException, UnknownObjectException
	{
		logger.debug("deactivatePerson start");
		emRWL.writeLock().lock();
		try
		{
			self.deactivateDBPerson(domainName, mpiId);
			logger.debug("deactivatePerson end");
		}
		catch (EJBException e)
		{
			handlePersistenceException(e);
		}
		finally
		{
			emRWL.writeLock().unlock();
		}

		if (notificationClientID != null)
		{
			notificationSender.sendDeactivatePersonNotification(notificationClientID, mpiId, domainName);
		}
	}

	public void deletePerson(String notificationClientID, String domainName, String mpiId) throws IllegalOperationException, MPIException, UnknownObjectException
	{
		logger.debug("deletePerson start");
		emRWL.writeLock().lock();
		try
		{
			self.deleteDBPerson(domainName, mpiId);
			logger.debug("deletePerson end");
		}
		catch (EJBException e)
		{
			handlePersistenceException(e);
		}
		finally
		{
			emRWL.writeLock().unlock();
		}

		if (notificationClientID != null)
		{
			notificationSender.sendDeletePersonNotification(notificationClientID, mpiId, domainName);
		}
	}

	// ***********************************
	// identities
	// ***********************************
	public IdentityOutDTO getIdentityById(long id) throws UnknownObjectException
	{
		logger.debug("getIdentityById");
		emRWL.readLock().lock();
		try
		{
			return getDBIdentityById(id).toDTO();
		}
		finally
		{
			emRWL.readLock().unlock();
		}
	}

	public List<IdentityOutDTO> getIdentitiesByDomain(String domainName, Map<IdentityField, String> filter, boolean filterIsCaseSensitive)
			throws UnknownObjectException
	{
		logger.debug("getIdentitiesByDomain for domain " + domainName);
		List<IdentityOutDTO> result = new ArrayList<>();
		emRWL.readLock().lock();
		try
		{
			Domain domain = getDBDomain(domainName);
			CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
			CriteriaQuery<Identity> criteriaQuery = criteriaBuilder.createQuery(Identity.class);
			Root<Identity> root = criteriaQuery.from(Identity.class);
			Predicate predicate = PaginatedHelper.generateWherePredicateForIdentity(criteriaBuilder, root, domain, filter, filterIsCaseSensitive);
			criteriaQuery.select(root).where(predicate);
			List<Identity> identities = em.createQuery(criteriaQuery).getResultList();

			for (Identity identity : identities)
			{
				result.add(identity.toDTO());
			}
			return result;
		}
		finally
		{
			emRWL.readLock().unlock();
		}
	}

	public List<IdentityOutDTO> getIdentitiesByDomainPaginated(String domainName, int firstEntry, int pageSize, IdentityField sortField,
			boolean sortIsAscending, Map<IdentityField, String> filter, boolean filterIsCaseSensitive) throws UnknownObjectException
	{
		logger.debug("getIdentitiesByDomainPaginated for domain " + domainName);
		List<IdentityOutDTO> result = new ArrayList<>();
		emRWL.readLock().lock();
		try
		{
			Domain domain = getDBDomain(domainName);
			CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
			CriteriaQuery<Identity> criteriaQuery = criteriaBuilder.createQuery(Identity.class);
			Root<Identity> root = criteriaQuery.from(Identity.class);
			Predicate predicate = PaginatedHelper.generateWherePredicateForIdentity(criteriaBuilder, root, domain, filter, filterIsCaseSensitive);
			criteriaQuery.select(root).where(predicate);
			Expression<?> order = PaginatedHelper.generateSortExpressionForIdentity(sortField, root);
			if (order != null)
			{
				if (sortIsAscending)
				{
					criteriaQuery.orderBy(criteriaBuilder.asc(order));
				}
				else
				{
					criteriaQuery.orderBy(criteriaBuilder.desc(order));
				}
			}
			List<Identity> identities = em.createQuery(criteriaQuery).setFirstResult(firstEntry).setMaxResults(pageSize).getResultList();

			for (Identity identity : identities)
			{
				result.add(identity.toDTO());
			}
			return result;
		}
		finally
		{
			emRWL.readLock().unlock();
		}
	}

	public List<IdentityOutDTO> getDeactivatedIdentitiesByDomain(String domainName) throws UnknownObjectException
	{
		logger.debug("getDeactivatedIdentitiesByDomain for domain " + domainName);
		emRWL.readLock().lock();
		try
		{
			Domain domain = getDBDomain(domainName);
			@SuppressWarnings("unchecked")
			List<Identity> temp = em.createNamedQuery("Identity.findDeactivatedByDomain").setParameter("domain", domain)
					.getResultList();
			logger.debug("found " + temp.size() + " identities for domain " + domainName);
			List<IdentityOutDTO> result = new ArrayList<>();
			for (Identity identity : temp)
			{
				result.add(identity.toDTO());
			}
			return result;
		}
		finally
		{
			emRWL.readLock().unlock();
		}
	}

	public void setReferenceIdentity(String notificationClientID, String domainName, String mpiId, long identityId, String comment)
			throws MPIException, UnknownObjectException
	{
		logger.debug("setReferenceIdentity");
		emRWL.writeLock().lock();
		try
		{
			self.saveReferenceIdentity(domainName, mpiId, identityId, comment, new Timestamp(System.currentTimeMillis()));
		}
		catch (EJBException e)
		{
			handlePersistenceException(e);
		}
		finally
		{
			emRWL.writeLock().unlock();
		}

		if (notificationClientID != null)
		{
			notificationSender.sendSetReferenceIdentityNotification(notificationClientID, mpiId, identityId, domainName, comment);
		}

		logger.debug("reference identity set");
	}

	public void deactivateIdentity(String notificationClientID, long identityId) throws MPIException, UnknownObjectException
	{

		logger.debug("deactivatePerson start");

		// Nur benoetigt fuer notification
		String domainName = null;

		emRWL.writeLock().lock();
		try
		{
			if (notificationClientID != null)
			{
				domainName = getDBIdentityById(identityId).getPerson().getDomain().getName();
			}

			self.deactivateDBIdentity(identityId);
			logger.debug("deactivatePerson end");
		}
		catch (EJBException e)
		{
			handlePersistenceException(e);
		}
		finally
		{
			emRWL.writeLock().unlock();
		}

		if (notificationClientID != null)
		{
			notificationSender.sendDeactivateIdentityNotification(notificationClientID, identityId, domainName);
		}
	}

	public void deleteIdentity(String notificationClientID, long identityId) throws IllegalOperationException, MPIException, UnknownObjectException
	{
		logger.debug("deletePerson start");

		// Nur benoetigt fuer notification
		String domainName = null;

		emRWL.writeLock().lock();
		try
		{
			if (notificationClientID != null)
			{
				domainName = getDBIdentityById(identityId).getPerson().getDomain().getName();
			}

			self.deleteDBIdentity(identityId);
			logger.debug("deletePerson end");
		}
		catch (EJBException e)
		{
			handlePersistenceException(e);
		}
		finally
		{
			emRWL.writeLock().unlock();
		}
		if (notificationClientID != null)
		{
			notificationSender.sendDeleteIdentityNotification(notificationClientID, identityId, domainName);
		}
	}

	public void updatePrivacy(String domainName, List<String> mpiIds, boolean onlyReferenceIdentity) throws InvalidParameterException, MPIException, UnknownObjectException
	{
		logger.debug("updatePrivacy start");
		emRWL.writeLock().lock();
		try
		{
			self.updateDBPrivacy(domainName, mpiIds, onlyReferenceIdentity);
			logger.debug("updatePrivacy end");
		}
		catch (EJBException e)
		{
			handlePersistenceException(e);
		}
		finally
		{
			emRWL.writeLock().unlock();
		}
	}

	// ***********************************
	// contacts
	// ***********************************
	public ContactOutDTO getContactById(long id) throws UnknownObjectException
	{
		logger.debug("getContactById for " + id);
		emRWL.readLock().lock();
		try
		{
			Contact result = getDBContactById(id);
			return result.toDTO();
		}
		finally
		{
			emRWL.readLock().unlock();
		}
	}

	public IdentityOutDTO addContact(String notificationClientID, long identityId, ContactInDTO contactDTO)
			throws DuplicateEntryException, MPIException, UnknownObjectException
	{
		logger.debug("addContact");
		emRWL.writeLock().lock();
		// diese funktion gibt niemals "null" zurueck, eclipse erkennt nicht, dass die funktion vorher auf jeden fall eine exception wirft
		IdentityOutDTO result = null;

		// Nur benoetigt fuer notification
		String domainName = null;
		try
		{
			Identity identity = getDBIdentityById(identityId);

			if (notificationClientID != null)
			{
				domainName = identity.getPerson().getDomain().getName();
			}

			Contact newContact = new Contact(contactDTO, null, new Timestamp(System.currentTimeMillis()));
			for (Contact contact : identity.getContacts())
			{
				if (contact.equalContent(newContact))
				{
					String message = "contact already exists within identity with id " + identityId;
					logger.error(message);
					throw new DuplicateEntryException(message);
				}
			}
			result = self.saveContact(identityId, contactDTO, new Timestamp(System.currentTimeMillis())).toDTO();
			logger.debug("addContact end");
		}
		catch (EJBException e)
		{
			handlePersistenceException(e);
		}
		finally
		{
			emRWL.writeLock().unlock();
		}

		if (notificationClientID != null)
		{
			notificationSender.sendAddContactNotification(notificationClientID, identityId, domainName);
		}

		return result;
	}

	// ***********************************
	// possible matches
	// ***********************************

	public List<PossibleMatchDTO> getPossibleMatchesByDomain(String domainName) throws UnknownObjectException
	{
		return getPossibleMatchesByDomainFiltered(domainName, null, false, null, false);
	}

	/**
	 * Returns a list with possible matches for a given filter configuration.
	 *
	 * @param domainName
	 *            the name of the domain
	 * @param sortField
	 *            the field to sort for
	 * @param sortIsAscending
	 *            true to sort ascending
	 * @param filter
	 *            the patterns for the fields to filter (AND)
	 * @param filterIsCaseSensitive
	 *            true to filter case sensitively
	 * @return a list with the filtered possible links
	 * @throws UnknownObjectException
	 */
	public List<PossibleMatchDTO> getPossibleMatchesByDomainFiltered(String domainName,
			IdentityField sortField, boolean sortIsAscending, Map<IdentityField, String> filter, boolean filterIsCaseSensitive)
			throws UnknownObjectException
	{
		logger.debug("getPossibleMatchesByDomainFiltered for domain " + domainName);
		emRWL.readLock().lock();
		try
		{
			CriteriaQuery<IdentityLink> criteriaQuery = PaginatedHelper.generateWhereQueryForIdentityLink(
					em.getCriteriaBuilder(), DOMAIN_CACHE.getDomain(domainName), sortField, sortIsAscending, filter, filterIsCaseSensitive);
			List<IdentityLink> links = em.createQuery(criteriaQuery).getResultList();
			return links.stream().map(IdentityLink::toDTO).collect(Collectors.toList());
		}
		finally
		{
			emRWL.readLock().unlock();
		}
	}

	/**
	 * Returns the number of possible matches for a given filter configuration.
	 *
	 * @param domainName
	 *            the name of the domain
	 * @param filter
	 *            the patterns for the fields to filter (AND)
	 * @param filterIsCaseSensitive
	 *            true to filter case sensitively
	 * @return a list with the filtered possible links
	 * @throws UnknownObjectException
	 */
	public long countPossibleMatchesForDomainFiltered(String domainName, Map<IdentityField, String> filter, boolean filterIsCaseSensitive)
			throws UnknownObjectException
	{
		logger.debug("countPossibleMatchesForDomainFiltered for domain " + domainName);
		emRWL.readLock().lock();
		try
		{
			CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
			CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
			Root<IdentityLink> root = criteriaQuery.from(IdentityLink.class);
			Predicate predicate = PaginatedHelper.generateWherePredicateForIdentityLink(criteriaBuilder, root,
					DOMAIN_CACHE.getDomain(domainName), filter, filterIsCaseSensitive);
			criteriaQuery.select(criteriaBuilder.count(root)).where(predicate);
			return em.createQuery(criteriaQuery).getSingleResult();
		}
		finally
		{
			emRWL.readLock().unlock();
		}
	}

	/**
	 * Returns a list with a page of possible matches for a given filter configuration.
	 *
	 * @param domainName
	 *            the name of the domain
	 * @param firstEntry
	 *            the first entry
	 * @param pageSize
	 *            the page size
	 * @param sortField
	 *            the field to sort for
	 * @param sortIsAscending
	 *            true to sort ascending
	 * @param filter
	 *            the patterns for the fields to filter (AND)
	 * @param filterIsCaseSensitive
	 *            true to filter case sensitively
	 * @return a list with the filtered possible links for the given page
	 * @throws UnknownObjectException
	 */
	public List<PossibleMatchDTO> getPossibleMatchesByDomainFilteredAndPaginated(String domainName, int firstEntry, int pageSize,
			IdentityField sortField, boolean sortIsAscending, Map<IdentityField, String> filter, boolean filterIsCaseSensitive)
			throws UnknownObjectException
	{
		logger.debug("getPossibleMatchesByDomainFilteredAndPaginated for domain " + domainName);
		emRWL.readLock().lock();
		try
		{
			CriteriaQuery<IdentityLink> criteriaQuery = PaginatedHelper.generateWhereQueryForIdentityLink(
					em.getCriteriaBuilder(), DOMAIN_CACHE.getDomain(domainName), sortField, sortIsAscending, filter, filterIsCaseSensitive);
			List<IdentityLink> links = em.createQuery(criteriaQuery).setFirstResult(firstEntry).setMaxResults(pageSize).getResultList();
			return links.stream().map(IdentityLink::toDTO).collect(Collectors.toList());
		}
		finally
		{
			emRWL.readLock().unlock();
		}
	}

	/**
	 * Returns the number of possible matches for a given default filter configuration.
	 *
	 * @param domainName
	 *            the name of the domain
	 * @param filter
	 *            the pattern to filter by default searching in any of both identities' {@link Identity_#lastName},
	 *            {@link Identity_#firstName}, {@link Identity_#birthDate}, and in {@link IdentityLink_#createTimestamp} (OR)
	 * @param filterIsCaseSensitive
	 *            true to filter case sensitively
	 * @param birthDateFormat
	 *            the date format pattern for matching the birth date
	 * @param creationTimeFormat
	 *            the time format pattern for matching the creation time
	 * @return a list with the filtered possible links
	 * @throws UnknownObjectException
	 */
	public long countPossibleMatchesForDomainFilteredByDefault(String domainName, String filter, boolean filterIsCaseSensitive,
			String birthDateFormat, String creationTimeFormat) throws UnknownObjectException
	{
		logger.debug("countPossibleMatchesForDomainFilteredByDefault for domain " + domainName + " with filter=" + filter +
				", birthDateFormat=" + birthDateFormat + ", creationTimeFormat=" + creationTimeFormat);
		emRWL.readLock().lock();
		try
		{
			CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
			CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
			Root<IdentityLink> root = criteriaQuery.from(IdentityLink.class);
			Predicate predicate = PaginatedHelper.generateWherePredicateForIdentityLink(criteriaBuilder, root, DOMAIN_CACHE.getDomain(domainName),
					filter, filterIsCaseSensitive, birthDateFormat, creationTimeFormat);
			criteriaQuery.select(criteriaBuilder.count(root)).where(predicate);
			return em.createQuery(criteriaQuery).getSingleResult();
		}
		finally
		{
			emRWL.readLock().unlock();
		}
	}

	/**
	 * Returns a list with a page of possible matches for a given default filter configuration.
	 *
	 * @param domainName
	 *            the name of the domain
	 * @param firstEntry
	 *            the first entry
	 * @param pageSize
	 *            the page size
	 * @param filter
	 *            the pattern to filter by default searching in any of both identities' {@link Identity_#lastName},
	 *            {@link Identity_#firstName}, {@link Identity_#birthDate}, and in {@link IdentityLink_#createTimestamp} (OR)
	 * @param filterIsCaseSensitive
	 *            true to filter case sensitively
	 * @param birthDateFormat
	 *            the date format pattern for matching the birth date
	 * @param creationTimeFormat
	 *            the time format pattern for matching the creation time
	 * @return a list with the filtered possible links for the given page
	 * @throws UnknownObjectException
	 */
	public List<PossibleMatchDTO> getPossibleMatchesByDomainFilteredByDefaultAndPaginated(String domainName, int firstEntry, int pageSize,
			String filter, boolean filterIsCaseSensitive, String birthDateFormat, String creationTimeFormat) throws UnknownObjectException
	{
		logger.debug("getPossibleMatchesByDomainFilteredAndPaginated for domain " + domainName + " with filter=" + filter + ", firstEntry=" + firstEntry
				+ ", pageSize=" + pageSize + ", birthDateFormat=" + birthDateFormat + ", creationTimeFormat=" + creationTimeFormat);
		emRWL.readLock().lock();
		try
		{
			CriteriaQuery<IdentityLink> criteriaQuery = PaginatedHelper.generateWhereQueryForIdentityLink(
					em.getCriteriaBuilder(), DOMAIN_CACHE.getDomain(domainName), filter, filterIsCaseSensitive, birthDateFormat, creationTimeFormat);
			List<IdentityLink> links = em.createQuery(criteriaQuery).setFirstResult(firstEntry).setMaxResults(pageSize).getResultList();
			return links.stream().map(IdentityLink::toDTO).collect(Collectors.toList());
		}
		finally
		{
			emRWL.readLock().unlock();
		}
	}

	public List<PossibleMatchForMPIDTO> getPossibleMatchesByPerson(String domainName, String mpiId) throws UnknownObjectException
	{
		logger.debug("getPossibleMatchesByPerson for mpi " + mpiId + " within domain " + domainName);
		emRWL.readLock().lock();
		try
		{
			Domain domain = getDBDomain(domainName);
			Person person = getDBPersonByMPI(domain, mpiId);
			List<IdentityLink> possibleMatches = getDBPossibleMatchesByPerson(person);
			List<PossibleMatchForMPIDTO> result = new ArrayList<>();
			for (IdentityLink identityLink : possibleMatches)
			{
				result.add(identityLink.toDTOForMPI(mpiId));
			}
			return result;
		}
		finally
		{
			emRWL.readLock().unlock();
		}
	}

	public void removePossibleMatch(long possibleMatchId, String comment) throws InvalidParameterException, MPIException
	{
		logger.debug("removePossibleMatch");
		emRWL.writeLock().lock();
		try
		{
			self.saveRemovePossibleMatch(possibleMatchId, comment, new Timestamp(System.currentTimeMillis()));
		}
		catch (EJBException e)
		{
			handlePersistenceException(e);
		}
		finally
		{
			emRWL.writeLock().unlock();
		}
		logger.debug("possible match removed");
	}

	public void assignIdentity(String notificationClientID, long possibleMatchId, long winningIdentityId, String comment)
			throws InvalidParameterException, MPIException
	{
		logger.debug("assignIdentity");
		emRWL.writeLock().lock();
		try
		{
			IdentityLink possibleMatch = getDBPossibleMatchById(possibleMatchId);
			logger.debug("collecting values to send");
			Identity winningIdentity;
			Identity identityToMerge;
			String mpiToMerge;

			if (possibleMatch.getDestIdentity().getId() == winningIdentityId)
			{
				winningIdentity = possibleMatch.getDestIdentity();
				identityToMerge = possibleMatch.getSrcIdentity();
			}
			else if (possibleMatch.getSrcIdentity().getId() == winningIdentityId)
			{
				identityToMerge = possibleMatch.getDestIdentity();
				winningIdentity = possibleMatch.getSrcIdentity();
			}
			else
			{
				String message = "identity with id " + winningIdentityId + " is not part of possible match with id " + possibleMatch.getId();
				logger.error(message);
				throw new InvalidParameterException("possibleMatchId|winningIdentityId", message);
			}
			mpiToMerge = identityToMerge.getPerson().getFirstMPI().getValue();
			self.saveAssignIdentity(possibleMatch, winningIdentity, identityToMerge, comment, new Timestamp(System.currentTimeMillis()));
			if (notificationClientID != null)
			{
				notificationSender.sendAssignNotification(notificationClientID, winningIdentity, identityToMerge, mpiToMerge, comment);
			}
		}
		catch (EJBException e)
		{
			handlePersistenceException(e);
		}
		finally
		{
			emRWL.writeLock().unlock();
		}
		logger.debug("identity assigned");
	}

	public void moveIdentitiesForIdentifierToPersonNotification(String notificationClientID, String domainName, IdentifierDTO identifier,
			String mpiId, String comment) throws MPIException, UnknownObjectException
	{
		logger.debug("assignIdentity");
		emRWL.writeLock().lock();
		try
		{
			self.saveMoveIdentitiesForIdentifierToPerson(domainName, identifier, mpiId, comment, new Timestamp(System.currentTimeMillis()));
		}
		catch (EJBException e)
		{
			handlePersistenceException(e);
		}
		finally
		{
			emRWL.writeLock().unlock();
		}

		if (notificationClientID != null)
		{
			notificationSender.sendMoveIdentitiesForIdentifierToPersonNotification(notificationClientID, domainName, identifier, mpiId, comment);
		}

		// TODO sendNoti
		logger.debug("identity assigned");
	}

	public PossibleMatchDTO externalPossibleMatchForPerson(String domainName, String mpiId, String aliasMpiId)
			throws DuplicateEntryException, InvalidParameterException, MPIException, UnknownObjectException
	{
		logger.debug("externalPossibleMatchForPerson");
		IdentityLink result = null;
		emRWL.writeLock().lock();
		try
		{
			Domain domain = getDBDomain(domainName);
			Person person1 = getDBPersonByMPI(domain, mpiId);
			Person person2 = getDBPersonByMPI(domain, aliasMpiId);
			result = self.saveExternalPossibleMatch(domainName, person1.getReferenceIdentity(), person2.getReferenceIdentity(),
					new Timestamp(System.currentTimeMillis()));
			logger.debug("possible match created or found");
		}
		catch (EJBException e)
		{
			handlePersistenceException(e);
		}
		finally
		{
			emRWL.writeLock().unlock();
		}
		return result.toDTO();
	}

	public PossibleMatchDTO externalPossibleMatchForIdentity(String domainName, long identityId, long aliasIdentityId)
			throws DuplicateEntryException, InvalidParameterException, MPIException, UnknownObjectException
	{
		logger.debug("externalPossibleMatchForIdentity");
		IdentityLink result = null;
		emRWL.writeLock().lock();
		try
		{
			Identity identity1 = getDBIdentityById(identityId);
			Identity identity2 = getDBIdentityById(aliasIdentityId);
			if (!domainName.equals(identity1.getPerson().getDomain().getName()) || !domainName.equals(identity2.getPerson().getDomain().getName()))
			{
				String message = "at least one of the given identities doesn't belong to the given domain";
				logger.error(message);
				throw new MPIException(MPIErrorCode.PERSONS_IN_DIFFERENT_DOMAINS, message);
			}
			result = self.saveExternalPossibleMatch(domainName, identity1, identity2, new Timestamp(System.currentTimeMillis()));
			logger.debug("possible match created or found");
		}
		catch (EJBException e)
		{
			handlePersistenceException(e);
		}
		finally
		{
			emRWL.writeLock().unlock();
		}
		return result.toDTO();
	}

	// ***********************************
	// histories
	// ***********************************
	public List<PersonHistoryDTO> getHistoryForPerson(String domainName, String mpiId) throws UnknownObjectException
	{
		logger.debug("getHistoryForPerson for mpi id " + mpiId + " within domain " + domainName);
		emRWL.readLock().lock();
		try
		{
			Domain domain = getDBDomain(domainName);
			Person person = getDBPersonByMPI(domain, mpiId);
			List<PersonHistory> histories = getDBHistoryForPerson(person);
			logger.debug("found " + histories.size() + " history entries for person " + person);
			List<PersonHistoryDTO> result = new ArrayList<>();
			for (PersonHistory history : histories)
			{
				result.add(history.toDTO());
			}
			return result;
		}
		finally
		{
			emRWL.readLock().unlock();
		}
	}

	/**
	 * Returns matching {@link IdentityHistoryDTO} entries.
	 * <p>
	 * If {@link IdentityField#NONE} is the only key in the filter map, then search for the corresponding
	 * pattern in all required fields (as defined in the configuration container) linked by OR (disjunction),
	 * otherwise search in all given fields for the respective pattern linked by AND (conjunction).
	 *
	 * @param domainName
	 *            the name of the domain
	 * @param filter
	 *            the filter map
	 * @param filterIsCaseSensitive
	 *            true to filter case-sensitively
	 * @return matching {@link IdentityHistoryDTO} entries.
	 * @throws UnknownObjectException
	 *             for a wrong domain name
	 */
	public List<IdentityHistoryDTO> getIdentityHistoriesForDomain(String domainName, Map<IdentityField, String> filter, boolean filterIsCaseSensitive)
			throws UnknownObjectException
	{
		logger.debug("getIdentityHistoriesForDomain " + domainName);
		List<IdentityHistoryDTO> result = new ArrayList<>();
		emRWL.readLock().lock();
		try
		{
			Domain domain = getDBDomain(domainName);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<IdentityHistory> cq = cb.createQuery(IdentityHistory.class);
			Root<IdentityHistory> root = cq.from(IdentityHistory.class);
			Predicate predicate = PaginatedHelper.generateWherePredicateForIdentityHistory(
					cb, root, domain, new PaginationConfig(filter, true, filterIsCaseSensitive));
			cq.select(root).where(predicate);
			List<IdentityHistory> identityHistories = em.createQuery(cq).getResultList();
			for (IdentityHistory identityHistory : identityHistories)
			{
				result.add(identityHistory.toDTO());
			}
			return result;
		}
		finally
		{
			emRWL.readLock().unlock();
		}
	}

	/**
	 * Returns matching {@link IdentityHistoryDTO} entries.
	 * <p>
	 * If {@link IdentityField#NONE} is the only key in the identityFilter map, then search for the corresponding
	 * pattern in all required fields (as defined in the configuration container) linked by OR (disjunction),
	 * otherwise search in all given fields for the respective pattern linked by AND (conjunction).
	 *
	 * @param domainName
	 *            the name of the domain
	 * @param pc
	 *            the pagination configuration
	 * @return matching {@link IdentityHistoryDTO} entries
	 * @throws UnknownObjectException
	 *             for a wrong domain name
	 */
	public List<IdentityHistoryDTO> getIdentityHistoriesPaginated(String domainName, PaginationConfig pc) throws UnknownObjectException
	{
		logger.debug("getIdentityHistoriesPaginated for domain " + domainName);
		List<IdentityHistoryDTO> result = new ArrayList<>();
		emRWL.readLock().lock();
		try
		{
			Domain domain = getDBDomain(domainName);
			CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
			CriteriaQuery<IdentityHistory> criteriaQuery = criteriaBuilder.createQuery(IdentityHistory.class);
			Root<IdentityHistory> root = criteriaQuery.from(IdentityHistory.class);
			Predicate predicate = PaginatedHelper.generateWherePredicateForIdentityHistory(criteriaBuilder, root, domain, pc);
			criteriaQuery.select(root).where(predicate);

			if (pc.isUsingSorting())
			{
				Expression<?> order = PaginatedHelper.generateSortExpressionForIdentityHistory(pc.getSortField(), root);
				if (order != null)
				{
					if (pc.isSortIsAscending())
					{
						criteriaQuery.orderBy(criteriaBuilder.asc(order));
					}
					else
					{
						criteriaQuery.orderBy(criteriaBuilder.desc(order));
					}
				}
			}

			List<IdentityHistory> identityHistories;

			if (pc.isUsingPagination())
			{
				identityHistories = em.createQuery(criteriaQuery).setFirstResult(pc.getFirstEntry()).setMaxResults(pc.getPageSize()).getResultList();
			}
			else
			{
				identityHistories = em.createQuery(criteriaQuery).getResultList();
			}

			for (IdentityHistory identityHistory : identityHistories)
			{
				result.add(identityHistory.toDTO());
			}
			return result;
		}
		finally
		{
			emRWL.readLock().unlock();
		}
	}

	/**
	 * Counts matching {@link IdentityHistoryDTO} entries.
	 * <p>
	 * If {@link IdentityField#NONE} is the only key in the identityFilter map, then search for the corresponding
	 * pattern in all required fields (as defined in the configuration container) linked by OR (disjunction),
	 * otherwise search in all given fields for the respective pattern linked by AND (conjunction).
	 *
	 * @param domainName
	 *            the name of the domain
	 * @param pc
	 *            the pagination configuration
	 * @return number of matching {@link IdentityHistoryDTO} entries.
	 * @throws UnknownObjectException
	 *             for a wrong domain name
	 */
	public long countIdentityHistories(String domainName, PaginationConfig pc) throws UnknownObjectException
	{
		logger.debug("countIdentityHistories for domain " + domainName);
		emRWL.readLock().lock();
		try
		{
			Domain domain = getDBDomain(domainName);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			Root<IdentityHistory> root = cq.from(IdentityHistory.class);
			Predicate predicate = PaginatedHelper.generateWherePredicateForIdentityHistory(cb, root, domain, pc);
			cq.select(cb.count(root)).where(predicate);
			return em.createQuery(cq).getSingleResult();
		}
		finally
		{
			emRWL.readLock().unlock();
		}
	}

	/**
	 * Counts matching {@link PossibleMatchHistoryDTO} (respectively {@link IdentityLinkHistory}) entries (in the DB).
	 * w.r.t the given domain and pagination config.
	 *
	 * @param domainName
	 *            the name of the domain
	 * @param pc
	 *            the pagination configuration
	 * @return number of matching {@link PossibleMatchHistoryDTO} (respectively {@link IdentityLinkHistory}) entries (in the DB)
	 * @throws UnknownObjectException
	 *             for a wrong domain name
	 */
	public long countPossibleMatchHistories(String domainName, PaginationConfig pc) throws UnknownObjectException
	{
		logger.debug("countIdentityHistories for domain " + domainName);
		emRWL.readLock().lock();
		try
		{
			Domain domain = getDBDomain(domainName);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			Root<IdentityLinkHistory> root = cq.from(IdentityLinkHistory.class);
			Predicate predicate = PaginatedHelper.generateWherePredicateForIdentityLinkHistory(cb, root, domain, pc);
			cq.select(cb.count(root)).where(predicate);
			return em.createQuery(cq).getSingleResult();
		}
		finally
		{
			emRWL.readLock().unlock();
		}
	}

	public List<IdentityHistoryDTO> getHistoryForIdentity(long identityId) throws UnknownObjectException
	{
		logger.debug("getHistoryForIdentity with id " + identityId);
		emRWL.readLock().lock();
		try
		{
			Identity identity = getDBIdentityById(identityId);
			List<IdentityHistory> histories = getDBHistoryForIdentity(identity);
			List<IdentityHistoryDTO> result = new ArrayList<>();
			for (IdentityHistory history : histories)
			{
				result.add(history.toDTO());
			}
			return result;
		}
		finally
		{
			emRWL.readLock().unlock();
		}
	}

	public List<ContactHistoryDTO> getHistoryForContact(long contactId) throws UnknownObjectException
	{
		logger.debug("getHistoryForContact with id " + contactId);
		emRWL.readLock().lock();
		try
		{
			Contact contact = getDBContactById(contactId);
			List<ContactHistory> histories = getDBHistoryForContact(contact);
			logger.debug("found " + histories.size() + " history entries for contact " + contact);
			List<ContactHistoryDTO> result = new ArrayList<>();
			for (ContactHistory history : histories)
			{
				result.add(history.toDTO());
			}
			return result;
		}
		finally
		{
			emRWL.readLock().unlock();
		}
	}

	public List<PossibleMatchHistoryDTO> getPossibleMatchHistoryForPerson(String domainName, String mpiId) throws UnknownObjectException
	{
		logger.debug("getPossibleMatchHistoryForPerson with mpi id " + mpiId + " within domain " + domainName);
		emRWL.readLock().lock();
		try
		{
			Domain domain = getDBDomain(domainName);
			Person person = getDBPersonByMPI(domain, mpiId);
			List<IdentityLinkHistory> histories = getDBPossibleMatchHistoryForPerson(person);
			logger.debug("found " + histories.size() + " possible match history entries for person " + person);
			List<PossibleMatchHistoryDTO> result = new ArrayList<>();
			for (IdentityLinkHistory history : histories)
			{
				result.add(history.toDTO());
			}
			return result;
		}
		finally
		{
			emRWL.readLock().unlock();
		}
	}

	public List<PossibleMatchHistoryDTO> getPossibleMatchHistoryForUpdatedIdentity(long updatedIdentityId) throws UnknownObjectException
	{
		logger.debug("getPossibleMatchHistoryForUpdatedIdentity with updatedIdentityId " + updatedIdentityId);
		emRWL.readLock().lock();
		try
		{
			Identity identity = getDBIdentityById(updatedIdentityId);
			List<IdentityLinkHistory> histories = getDBPossibleMatchHistoryForUpdatedIdentity(identity);
			List<PossibleMatchHistoryDTO> result = new ArrayList<>();
			for (IdentityLinkHistory history : histories)
			{
				result.add(history.toDTO());
			}
			return result;
		}
		finally
		{
			emRWL.readLock().unlock();
		}
	}

	public List<IdentityHistoryDTO> getIdentityHistoryByPersonId(Long personId) throws UnknownObjectException
	{
		logger.debug("getReferenceIdentityAtTimestamp with personId " + personId);
		emRWL.readLock().lock();
		try
		{
			return getDBHistoryForIdentityByPerson(getDBPersonById(personId)).stream().map(IdentityHistory::toDTO).collect(Collectors.toList());
		}
		finally
		{
			emRWL.readLock().unlock();
		}
	}

	// *********
	// statistic
	// *********
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public StatisticDTO getLatestStats()
	{
		statisticRWL.readLock().lock();
		StatisticDTO result;
		try
		{
			CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
			CriteriaQuery<Statistic> criteriaQuery = criteriaBuilder.createQuery(Statistic.class);
			Root<Statistic> root = criteriaQuery.from(Statistic.class);
			criteriaQuery.select(root).orderBy(criteriaBuilder.desc(root.get(Statistic_.stat_entry_id)));
			result = em.createQuery(criteriaQuery).setMaxResults(1).getSingleResult().toDTO();
		}
		catch (NoResultException | EJBException e)
		{
			// see #132
			// since (at least) wildfly 24 the expected NoResultException is wrapped into an EJBException
			if (e instanceof EJBException && !(e.getCause() instanceof NoResultException))
			{
				// only catch the case of a wrapped NoResultException, otherwise rethrow
				throw e;
			}
			result = new StatisticDTO();
		}
		finally
		{
			statisticRWL.readLock().unlock();
		}
		return result;
	}

	public List<StatisticDTO> getAllStats()
	{
		statisticRWL.readLock().lock();
		try
		{
			List<StatisticDTO> result = new ArrayList<>();
			CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
			CriteriaQuery<Statistic> criteriaQuery = criteriaBuilder.createQuery(Statistic.class);
			Root<Statistic> root = criteriaQuery.from(Statistic.class);
			criteriaQuery.select(root);
			List<Statistic> queryResult = em.createQuery(criteriaQuery).getResultList();

			for (Statistic stats : queryResult)
			{
				result.add(stats.toDTO());
			}
			return result;
		}
		finally
		{
			statisticRWL.readLock().unlock();
		}
	}

	public StatisticDTO updateStats()
	{
		Instant start = Instant.now();
		StatisticDTO result = new StatisticDTO();
		Map<String, Long> stat = result.getMappedStatValue();

		statisticRWL.readLock().lock();

		try
		{
			long allPersons = 0L;
			long allIdentities = 0L;
			long allPossibleMatchesOpen = 0L;
			long allPossibleMatchesMerged = 0L;
			long allPossibleMatchesSplit = 0L;
			long allIdentitiesNoMatch = 0L;
			long allIdentityPossibleMatch = 0L;
			long allIdentitiesMatch = 0L;
			long allIdentitiesPerfectMatch = 0L;

			for (Domain domain : DOMAIN_CACHE.getDomains())
			{
				long domainPersons = domain.getPersonCount();
				stat.put(new StatisticKeys(StatisticKeys.PERSONS).perDomain(domain.getName()).build(), domainPersons);
				allPersons += domainPersons;

				CriteriaBuilder criteriaBuilder2 = em.getCriteriaBuilder();
				CriteriaQuery<Long> criteriaQuery2 = criteriaBuilder2.createQuery(Long.class);
				Root<Identity> root2 = criteriaQuery2.from(Identity.class);
				Predicate predicate2 = PaginatedHelper.generateWherePredicateForIdentity(criteriaBuilder2, root2, domain, null, false);
				criteriaQuery2.select(criteriaBuilder2.count(root2)).where(predicate2);
				long domainIdentities = em.createQuery(criteriaQuery2).getSingleResult();
				stat.put(new StatisticKeys(StatisticKeys.IDENTITIES).perDomain(domain.getName()).build(), domainIdentities);
				allIdentities += domainIdentities;

				long possibleMatchesOpen = countPossibleMatchesForDomainFiltered(domain.getName(), null, false);
				stat.put(new StatisticKeys(StatisticKeys.POSSIBLE_MATCHES_OPEN).perDomain(domain.getName()).build(), possibleMatchesOpen);
				allPossibleMatchesOpen += possibleMatchesOpen;

				Set<IdentityHistoryEvent> merge = new HashSet<>(List.of(IdentityHistoryEvent.MERGE));
				PaginationConfig pc = PaginationConfig.builder().withEventFilter(merge).build();
				long possibleMatchesMerged = countIdentityHistories(domain.getName(), pc);
				stat.put(new StatisticKeys(StatisticKeys.POSSIBLE_MATCHES_MERGED).perDomain(domain.getName()).build(), possibleMatchesMerged);
				allPossibleMatchesMerged += possibleMatchesMerged;

				Set<PossibleMatchSolution> splitSolution = new HashSet<>(List.of(PossibleMatchSolution.SPLIT));
				pc = PaginationConfig.builder().withSolutionFilter(splitSolution).build();
				long possibleMatchesSplit = countPossibleMatchHistories(domain.getName(), pc);
				stat.put(new StatisticKeys(StatisticKeys.POSSIBLE_MATCHES_SPLIT).perDomain(domain.getName()).build(), possibleMatchesSplit);
				allPossibleMatchesSplit += possibleMatchesSplit;

				Set<IdentityHistoryEvent> noMatch = new HashSet<>(List.of(IdentityHistoryEvent.NEW));
				pc = PaginationConfig.builder().withEventFilter(noMatch).build();
				long identitiesNoMatch = countIdentityHistories(domain.getName(), pc);
				stat.put(new StatisticKeys(StatisticKeys.IDENTITY_NO_MATCH).perDomain(domain.getName()).build(), identitiesNoMatch);
				allIdentitiesNoMatch += identitiesNoMatch;

				Set<PossibleMatchSolution> manualSolution = new HashSet<>(Arrays.asList(PossibleMatchSolution.SPLIT, PossibleMatchSolution.MERGE));
				pc = PaginationConfig.builder().withSolutionFilter(manualSolution).build();
				long possibleMatchesHistoryManual = countPossibleMatchHistories(domain.getName(), pc);
				long identityPossibleMatch = possibleMatchesHistoryManual + possibleMatchesOpen;
				stat.put(new StatisticKeys(StatisticKeys.IDENTITY_POSSIBLE_MATCH).perDomain(domain.getName()).build(), identityPossibleMatch);
				allIdentityPossibleMatch += identityPossibleMatch;

				Set<IdentityHistoryEvent> match = new HashSet<>(List.of(IdentityHistoryEvent.MATCH));
				pc = PaginationConfig.builder().withEventFilter(match).build();
				long identitiesMatch = countIdentityHistories(domain.getName(), pc);
				stat.put(new StatisticKeys(StatisticKeys.IDENTITY_MATCH).perDomain(domain.getName()).build(), identitiesMatch);
				allIdentitiesMatch += identitiesMatch;

				Set<IdentityHistoryEvent> perfectMatch = new HashSet<>(List.of(IdentityHistoryEvent.PERFECT_MATCH));
				pc = PaginationConfig.builder().withEventFilter(perfectMatch).build();
				long identitiesPerfectMatch = countIdentityHistories(domain.getName(), pc);
				stat.put(new StatisticKeys(StatisticKeys.IDENTITY_PERFECT_MATCH).perDomain(domain.getName()).build(), identitiesPerfectMatch);
				allIdentitiesPerfectMatch += identitiesPerfectMatch;
			}

			stat.put(StatisticKeys.DOMAINS, (long) DOMAIN_CACHE.getDomains().size());
			stat.put(StatisticKeys.PERSONS, allPersons);
			stat.put(StatisticKeys.IDENTITIES, allIdentities);
			stat.put(StatisticKeys.POSSIBLE_MATCHES_OPEN, allPossibleMatchesOpen);
			stat.put(StatisticKeys.POSSIBLE_MATCHES_MERGED, allPossibleMatchesMerged);
			stat.put(StatisticKeys.POSSIBLE_MATCHES_SPLIT, allPossibleMatchesSplit);
			stat.put(StatisticKeys.IDENTITY_NO_MATCH, allIdentitiesNoMatch);
			stat.put(StatisticKeys.IDENTITY_MATCH, allIdentitiesMatch);
			stat.put(StatisticKeys.IDENTITY_PERFECT_MATCH, allIdentitiesPerfectMatch);
		}
		catch (UnknownObjectException e)
		{
			logger.error(e.getLocalizedMessage());
		}
		finally
		{
			statisticRWL.readLock().unlock();
		}

		Instant finish = Instant.now();
		result.getMappedStatValue().put(StatisticKeys.CALCULATION_TIME, Duration.between(start, finish).toMillis());

		addStat(result);
		return result;
	}

	public void addStat(StatisticDTO statisticDTO)
	{
		statisticRWL.writeLock().lock();
		try
		{
			Statistic stat = new Statistic(statisticDTO);
			em.persist(stat);
		}
		finally
		{
			statisticRWL.writeLock().unlock();
		}
	}
}
