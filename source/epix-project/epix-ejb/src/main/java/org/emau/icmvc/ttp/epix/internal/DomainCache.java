package org.emau.icmvc.ttp.epix.internal;

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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import it.unimi.dsi.fastutil.longs.LongList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.emau.icmvc.ttp.deduplication.PreprocessingStrategy;
import org.emau.icmvc.ttp.deduplication.PrivacyStrategy;
import org.emau.icmvc.ttp.deduplication.ValidateRequiredStrategy;
import org.emau.icmvc.ttp.deduplication.model.DeduplicationResult;
import org.emau.icmvc.ttp.deduplication.model.MatchResult;
import org.emau.icmvc.ttp.epix.common.exception.DuplicateEntryException;
import org.emau.icmvc.ttp.epix.common.exception.InvalidParameterException;
import org.emau.icmvc.ttp.epix.common.exception.MPIException;
import org.emau.icmvc.ttp.epix.common.exception.UnknownObjectException;
import org.emau.icmvc.ttp.epix.common.exception.UnknownObjectType;
import org.emau.icmvc.ttp.epix.pdqquery.model.SearchMask;
import org.emau.icmvc.ttp.epix.persistence.model.Domain;
import org.emau.icmvc.ttp.epix.persistence.model.Identity;
import org.emau.icmvc.ttp.epix.persistence.model.IdentityPreprocessed;

/**
 *
 * @author geidell
 *
 */
public class DomainCache
{
	private static final Logger logger = LogManager.getLogger(DomainCache.class);
	private boolean initialised = false;
	private static final Map<String, DomainCacheEntry> cache = new HashMap<>();
	private static final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	private static final DomainCache INSTANCE = new DomainCache();

	private DomainCache()
	{}

	public static DomainCache getInstance()
	{
		return INSTANCE;
	}

	/**
	 * initialize cache
	 *
	 * @param domains
	 * @throws InvalidParameterException
	 */
	public void init(List<Domain> domains, Map<String, Long> personCounterForDomains) throws InvalidParameterException
	{
		rwl.writeLock().lock();
		try
		{
			if (!initialised)
			{
				logger.debug("initialising domain cache");
				StringBuilder sb = new StringBuilder();
				for (Domain domain : domains)
				{
					if (logger.isDebugEnabled())
					{
						sb.setLength(0);
						sb.append("initialise domain cache for domain ");
						sb.append(domain.getName());
						logger.debug(sb.toString());
					}
					Long personCount = personCounterForDomains.get(domain.getName());
					if (personCount == null)
					{
						String message = "new person count given for domain " + domain.getName();
						logger.error(message);
						throw new InvalidParameterException(message);
					}
					cache.put(domain.getName(), new DomainCacheEntry(domain, personCount));
				}
				initialised = true;
				logger.debug("domain cache initialised");
			}
		}
		finally
		{
			rwl.writeLock().unlock();
		}
	}

	public void destroy()
	{
		rwl.writeLock().lock();
		try
		{
			for (DomainCacheEntry domainCache : cache.values())
			{
				domainCache.ipCache.destroy();
			}
		}
		finally
		{
			rwl.writeLock().unlock();
		}
	}

	public void addIPsToDomain(String domainName, List<IdentityPreprocessed> ips) throws MPIException, UnknownObjectException
	{
		logger.debug("add " + ips.size() + " entries to cache for domain " + domainName);
		rwl.writeLock().lock();
		try
		{
			getCacheEntry(domainName).addIPs(ips);
		}
		finally
		{
			rwl.writeLock().unlock();
		}
	}

	public void addDomain(Domain domain, long personCount) throws DuplicateEntryException, InvalidParameterException
	{
		rwl.writeLock().lock();
		try
		{
			if (cache.get(domain.getName()) != null)
			{
				throw new DuplicateEntryException("domain with name '" + domain.getName() + "' already exists.");
			}
			try
			{
				cache.put(domain.getName(), new DomainCacheEntry(domain, personCount));
			}
			catch (InvalidParameterException e)
			{
				String message = "exception while parsing matching configuration for domain: " + domain.getName() + " - " + e.getMessage();
				logger.error(message);
				throw new InvalidParameterException("domain.config", message);
			}
		}
		finally
		{
			rwl.writeLock().unlock();
		}
	}

	public void removeDomain(String domainName) throws UnknownObjectException
	{
		rwl.writeLock().lock();
		try
		{
			DomainCacheEntry dcEntry = getCacheEntry(domainName);
			dcEntry.getIpCache().destroy();
			cache.remove(domainName);
		}
		finally
		{
			rwl.writeLock().unlock();
		}
	}

	public Domain getDomain(String domainName) throws UnknownObjectException
	{
		rwl.readLock().lock();
		try
		{
			DomainCacheEntry dcEntry = getCacheEntry(domainName);
			return dcEntry.getDomain();
		}
		finally
		{
			rwl.readLock().unlock();
		}
	}

	public DeduplicationResult match(String domainName, PreprocessedCacheObject matchable) throws MPIException, UnknownObjectException
	{
		rwl.readLock().lock();
		try
		{
			DomainCacheEntry dcEntry = getCacheEntry(domainName);
			return dcEntry.getIpCache().getMatches(matchable);
		}
		finally
		{
			rwl.readLock().unlock();
		}
	}

	public DeduplicationResult match(String domainName, PreprocessedCacheObject matchable, Collection<PreprocessedCacheObject> candidates)
			throws MPIException, UnknownObjectException
	{
		rwl.readLock().lock();
		try
		{
			DomainCacheEntry dcEntry = getCacheEntry(domainName);
			return dcEntry.getIpCache().getMatches(matchable, candidates);
		}
		finally
		{
			rwl.readLock().unlock();
		}
	}

	public PreprocessedCacheObject perfectMatch(String domainName, PreprocessedCacheObject matchable) throws MPIException, UnknownObjectException
	{
		rwl.readLock().lock();
		try
		{
			DomainCacheEntry dcEntry = getCacheEntry(domainName);
			return dcEntry.getIpCache().getPerfectMatch(matchable);
		}
		finally
		{
			rwl.readLock().unlock();
		}
	}

	public PreprocessedCacheObject perfectMatch(String domainName, PreprocessedCacheObject matchable, Collection<PreprocessedCacheObject> candidates)
			throws UnknownObjectException
	{
		rwl.readLock().lock();
		try
		{
			DomainCacheEntry dcEntry = getCacheEntry(domainName);
			return dcEntry.getIpCache().getPerfectMatch(matchable, candidates);
		}
		finally
		{
			rwl.readLock().unlock();
		}
	}

	public MatchResult directMatch(String domainName, PreprocessedCacheObject matchable, PreprocessedCacheObject candidate)
			throws MPIException, UnknownObjectException
	{
		rwl.readLock().lock();
		try
		{
			DomainCacheEntry dcEntry = getCacheEntry(domainName);
			return dcEntry.getIpCache().getDirectMatchResult(matchable, candidate);
		}
		finally
		{
			rwl.readLock().unlock();
		}
	}

	public void checkValidation(String domainName, IdentityPreprocessed matchable)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		rwl.readLock().lock();
		try
		{
			DomainCacheEntry dcEntry = getCacheEntry(domainName);
			dcEntry.checkValidation(matchable);
		}
		finally
		{
			rwl.readLock().unlock();
		}
	}

	public void updateIdentity(String domainName, long identityId, IdentityPreprocessed newIdentityPP, PreprocessedCacheObject newIdentityPPCO)
			throws MPIException, UnknownObjectException
	{
		rwl.writeLock().lock();
		try
		{
			DomainCacheEntry dcEntry = getCacheEntry(domainName);
			dcEntry.getIpCache().updateIdentity(identityId, newIdentityPP, newIdentityPPCO);
		}
		finally
		{
			rwl.writeLock().unlock();
		}
	}

	public void updateIdentityWithDBIDs(String domainName, long identityId, long personId, Timestamp timestamp)
			throws MPIException, UnknownObjectException
	{
		rwl.writeLock().lock();
		try
		{
			DomainCacheEntry dcEntry = getCacheEntry(domainName);
			dcEntry.getIpCache().updateIdentityWithDBIds(identityId, personId, domainName, timestamp);
		}
		finally
		{
			rwl.writeLock().unlock();
		}
	}

	public void incPersonCount(String domainName) throws UnknownObjectException
	{
		rwl.writeLock().lock();
		try
		{
			DomainCacheEntry dcEntry = getCacheEntry(domainName);
			dcEntry.incPersonCount();
		}
		finally
		{
			rwl.writeLock().unlock();
		}
	}

	public void decPersonCount(String domainName) throws UnknownObjectException
	{
		rwl.writeLock().lock();
		try
		{
			DomainCacheEntry dcEntry = getCacheEntry(domainName);
			dcEntry.decPersonCount();
		}
		finally
		{
			rwl.writeLock().unlock();
		}
	}

	public void addIdentity(String domainName, IdentityPreprocessed identityPP, PreprocessedCacheObject identityPPCO)
			throws UnknownObjectException, MPIException
	{
		rwl.writeLock().lock();
		try
		{
			DomainCacheEntry dcEntry = getCacheEntry(domainName);
			dcEntry.getIpCache().addIdentity(identityPP, identityPPCO);
		}
		finally
		{
			rwl.writeLock().unlock();
		}
	}

	public void removeIdentity(String domainName, long identityId) throws UnknownObjectException
	{
		rwl.writeLock().lock();
		try
		{
			DomainCacheEntry dcEntry = getCacheEntry(domainName);
			dcEntry.getIpCache().removeIdentity(identityId);
		}
		finally
		{
			rwl.writeLock().unlock();
		}
	}

	public PreprocessedCacheObject getPreprocessedCacheObjectByIdentityId(String domainName, long id) throws MPIException, UnknownObjectException
	{
		rwl.readLock().lock();
		try
		{
			DomainCacheEntry dcEntry = getCacheEntry(domainName);
			return dcEntry.getIpCache().getPreprocessedCacheObjectByIdentityId(id);
		}
		finally
		{
			rwl.readLock().unlock();
		}
	}

	public PreprocessedCacheObject createPreprocessedCacheObjectFromIdentityPP(String domainName, IdentityPreprocessed identityPP)
			throws MPIException, UnknownObjectException
	{
		rwl.readLock().lock();
		try
		{
			DomainCacheEntry dcEntry = getCacheEntry(domainName);
			return dcEntry.getIpCache().createPreprocessedCacheObjectFromIdentityPP(identityPP);
		}
		finally
		{
			rwl.readLock().unlock();
		}
	}

	public LongList findPersonIdsByPDQ(SearchMask mask, LongList personIds) throws MPIException, UnknownObjectException
	{
		rwl.readLock().lock();
		try
		{
			DomainCacheEntry dcEntry = getCacheEntry(mask.getDomainName());
			IdentityPreprocessed maskIdentityPreprocessed;
			maskIdentityPreprocessed = dcEntry.preprocess(new Identity(mask.getIdentity(), new ArrayList<>(), new ArrayList<>(), null, null, false,
					new Timestamp(System.currentTimeMillis())));
			return dcEntry.getIpCache().findPersonIdsByPDQ(mask, maskIdentityPreprocessed, personIds);
		}
		finally
		{
			rwl.readLock().unlock();
		}
	}

	public IdentityPreprocessed preprocess(String domainName, Identity identity)
			throws InvalidParameterException, MPIException, UnknownObjectException
	{
		rwl.readLock().lock();
		try
		{
			DomainCacheEntry dcEntry = getCacheEntry(domainName);
			IdentityPreprocessed result = dcEntry.preprocess(identity);
			dcEntry.checkValidation(result);
			return result;
		}
		finally
		{
			rwl.readLock().unlock();
		}
	}

	public IdentityPreprocessed setPrivacyAttributes(String domainName, IdentityPreprocessed identity, boolean overwrite)
			throws UnknownObjectException
	{
		rwl.readLock().lock();
		try
		{
			DomainCacheEntry dcEntry = getCacheEntry(domainName);
			return dcEntry.setPrivacyAttributes(identity, overwrite);
		}
		finally
		{
			rwl.readLock().unlock();
		}
	}

	public void copyPrivacyAttributes(String domainName, IdentityPreprocessed from, Identity to)
			throws UnknownObjectException
	{
		rwl.readLock().lock();
		try
		{
			DomainCacheEntry dcEntry = getCacheEntry(domainName);
			dcEntry.copyPrivacyAttributes(from, to);
		}
		finally
		{
			rwl.readLock().unlock();
		}
	}

	public IdentityPreprocessed removePII(String domainName, IdentityPreprocessed identity)
			throws UnknownObjectException
	{
		rwl.readLock().lock();
		try
		{
			DomainCacheEntry dcEntry = getCacheEntry(domainName);
			return dcEntry.removePII(identity);
		}
		finally
		{
			rwl.readLock().unlock();
		}
	}

	public Identity removePII(String domainName, Identity identity)
			throws UnknownObjectException
	{
		rwl.readLock().lock();
		try
		{
			DomainCacheEntry dcEntry = getCacheEntry(domainName);
			return dcEntry.removePII(identity);
		}
		finally
		{
			rwl.readLock().unlock();
		}
	}

	private DomainCacheEntry getCacheEntry(String domainName) throws UnknownObjectException
	{
		DomainCacheEntry dcEntry;
		dcEntry = cache.get(domainName);
		if (dcEntry == null)
		{
			String message = "domain not found: " + domainName;
			throw new UnknownObjectException(message, UnknownObjectType.DOMAIN, domainName);
		}
		return dcEntry;
	}

	public List<Domain> getDomains()
	{
		List<Domain> result = new ArrayList<>();
		rwl.readLock().lock();
		try
		{
			for (DomainCacheEntry dcEntry : cache.values())
			{
				result.add(dcEntry.getDomain());
			}
			return result;
		}
		finally
		{
			rwl.readLock().unlock();
		}
	}

	private final class DomainCacheEntry
	{
		private final Domain domain;
		private final IdentityPreprocessedCache ipCache;
		private final PreprocessingStrategy preprocessor;
		private final PrivacyStrategy privacyStrategy;
		private final ValidateRequiredStrategy validateRequiredStrategy;

		public DomainCacheEntry(Domain domain, long personCount) throws InvalidParameterException
		{
			logger.info("initialising domain cache for " + domain.getName());
			this.domain = domain;
			preprocessor = new PreprocessingStrategy(domain.getMatchingConfiguration().getPreprocessingConfig());
			ipCache = new IdentityPreprocessedCache(domain.getMatchingConfiguration());
			domain.setPersonCount(personCount);
			validateRequiredStrategy = new ValidateRequiredStrategy(domain.getMatchingConfiguration().getRequiredFields());
			privacyStrategy = new PrivacyStrategy(domain.getMatchingConfiguration().getPrivacy());
			logger.info("cache for " + domain.getName() + " initialised");
		}

		public void addIPs(List<IdentityPreprocessed> ips) throws MPIException
		{
			ipCache.addIdentites(ips);
		}

		public void incPersonCount()
		{
			domain.setPersonCount(domain.getPersonCount() + 1);
		}

		public void decPersonCount()
		{
			domain.setPersonCount(domain.getPersonCount() - 1);
		}

		public IdentityPreprocessed preprocess(Identity identity) throws MPIException
		{
			IdentityPreprocessed identityPreprocessed = new IdentityPreprocessed(identity);
			identityPreprocessed = preprocessor.preprocess(identityPreprocessed);
			return identityPreprocessed;
		}

		public IdentityPreprocessed setPrivacyAttributes(IdentityPreprocessed identity, boolean overwrite)
		{
			return privacyStrategy.setPrivacyAttributes(identity, overwrite);
		}

		public void copyPrivacyAttributes(IdentityPreprocessed from, Identity to)
		{
			privacyStrategy.copyPrivacyAttributes(from, to);
		}

		public IdentityPreprocessed removePII(IdentityPreprocessed identity)
		{
			return privacyStrategy.removePII(identity);
		}

		public Identity removePII(Identity identity)
		{
			return privacyStrategy.removePII(identity);
		}

		public Domain getDomain()
		{
			return domain;
		}

		public void checkValidation(IdentityPreprocessed matchable) throws InvalidParameterException, MPIException
		{
			validateRequiredStrategy.checkValidation(matchable);
		}

		public IdentityPreprocessedCache getIpCache()
		{
			return ipCache;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + (domain == null ? 0 : domain.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
			{
				return true;
			}
			if (obj == null)
			{
				return false;
			}
			if (getClass() != obj.getClass())
			{
				return false;
			}
			DomainCacheEntry other = (DomainCacheEntry) obj;
			if (!getOuterType().equals(other.getOuterType()))
			{
				return false;
			}
			if (domain == null)
			{
				if (other.domain != null)
				{
					return false;
				}
			}
			else if (!domain.equals(other.domain))
			{
				return false;
			}
			return true;
		}

		private DomainCache getOuterType()
		{
			return DomainCache.this;
		}
	}
}
