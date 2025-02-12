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

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.ejb.Remote;
import jakarta.ejb.Schedule;
import jakarta.ejb.Stateless;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;
import org.emau.icmvc.ttp.epix.common.model.DomainDTO;
import org.emau.icmvc.ttp.epix.common.model.StatisticDTO;
import org.emau.icmvc.ttp.epix.common.utils.StatisticKeys;

@WebService(name = "statisticService")
@SOAPBinding(style = SOAPBinding.Style.RPC)
@Stateless
@Remote(StatisticManager.class)
public class StatisticManagerBean extends AbstractEpixServiceBase implements StatisticManager
{
	private boolean enableAutoUpdate = true;

	@Override
	public StatisticDTO getFirstStats()
	{
		logger.debug("call to getFirstStats");
		StatisticDTO result = dao.getFirstStats();
		if (logger.isDebugEnabled())
		{
			logger.debug("result of getFirstStats: {}", result);
		}
		return filterAllowedStatisticDomains(result);
	}
	
	@Override
	public StatisticDTO getLatestStats()
	{
		logger.debug("call to getLatestStats");
		StatisticDTO result = dao.getLatestStats();
		if (logger.isDebugEnabled())
		{
			logger.debug("result of getLatestStats: {}", result);
		}
		return filterAllowedStatisticDomains(result);
	}

	@Override
	public List<StatisticDTO> getAllStats()
	{
		logger.debug("call to getAllStats");
		List<StatisticDTO> result = dao.getAllStats();
		if (logger.isDebugEnabled())
		{
			logger.debug("number of results: {}", result.size());
		}
		return filterAllowedStatisticDomains(result);
	}

	@Override
	public List<StatisticDTO> getStatsFromTo(Date from, Date to)
	{
		logger.debug("call to getStatsFromTo");
		List<StatisticDTO> result = dao.getStatsFromTo(from, to);
		if (logger.isDebugEnabled())
		{
			logger.debug("number of results: {}", result.size());
		}
		return filterAllowedStatisticDomains(result);
	}

	@Override
	public StatisticDTO updateStats()
	{
		logger.debug("call to getLatestStats");
		StatisticDTO result = dao.updateStats();
		if (logger.isDebugEnabled())
		{
			logger.debug("result of getLatestStats: {}", result);
		}
		return filterAllowedStatisticDomains(result);
	}

	@Override
	public void addStat(StatisticDTO statisticDTO)
	{
		if (logger.isDebugEnabled())
		{
			logger.info("call to addStat with " + statisticDTO);
		}
		dao.addStat(statisticDTO);
		if (logger.isDebugEnabled())
		{
			logger.info("stat for {} added", statisticDTO);
		}
	}

	@Schedule(second = "0", minute = "10", hour = "4")
	public void autoUpdate()
	{
		if (enableAutoUpdate)
		{
			logger.debug("Scheduled execution of updateStats.");
			updateStats();
		}
		else
		{
			logger.debug("Scheduling execution of updateStats skipped because autoUpdate is disabled.");
		}
	}

	@Override
	public void enableScheduling(boolean status)
	{
		this.enableAutoUpdate = status;
		logger.debug("Scheduling Mode enabled: {}", enableAutoUpdate);
	}

	/**
	 * {@return A list of statistic DTOs with filtered map entries}.
	 * See {@link #filterAllowedStatisticDomains(StatisticDTO)} for more detail on filtering.
	 * @param statisticDTOs the statistic DTOs to filter
	 */
	protected List<StatisticDTO> filterAllowedStatisticDomains(List<StatisticDTO> statisticDTOs)
	{
		FilterInfo info = new FilterInfo();
		return statisticDTOs.stream().map(s -> filterAllowedStatisticDomains(s, info)).collect(Collectors.toList());
	}

	/**
	 * If the current auth context indicates, that authorization with domain-based roles is activated,
	 * and if the currently permitted roles in this context effectively deny access to at least one of
	 * the domains of this E-Pix instance, then this method will return a new statistics DTO which only
	 * contains the statistic keys referring to allowed domains. Otherwise, the provided statistics DTO
	 * will be returned directly.
	 * @param statisticDTO the statistic DTO to filter
	 * @return if necessary the filtered statistics or the provided statistics otherwise
	 */
	protected StatisticDTO filterAllowedStatisticDomains(StatisticDTO statisticDTO)
	{
		return filterAllowedStatisticDomains(statisticDTO, new FilterInfo());
	}

	private StatisticDTO filterAllowedStatisticDomains(StatisticDTO statisticDTO, FilterInfo info)
	{
		FilterInfo finalInfo = info != null ? info : new FilterInfo();

		if (finalInfo.isDenyingDomains())
		{
			logger.trace("applying allowed domain filter {} to {}", info, statisticDTO);
			Map<String, Long> stats = statisticDTO.getMappedStatValue();
			Set<String> visitedDomains = new HashSet<>();
			Map<String, Long> allowedStats = stats.entrySet().stream()
					.filter(e -> isAllowedKey(e.getKey(), finalInfo.getAllowedDomains(), visitedDomains))
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (k1, k2) -> k2, HashMap::new));
			allowedStats.put(StatisticKeys.CALCULATION_TIME, stats.get(StatisticKeys.CALCULATION_TIME));
			allowedStats.put(StatisticKeys.DOMAINS, (long) visitedDomains.size());
			statisticDTO = new StatisticDTO(statisticDTO.getId(), statisticDTO.getEntrydate(), allowedStats);
			logger.trace("filtered statistics {}", statisticDTO);
		}

		return statisticDTO;
	}

	private boolean isAllowedKey(String statKey, Set<String> allowedDomains, Set<String> visitedDomains)
	{
		if (statKey.contains(StatisticKeys.PER_DOMAIN))
		{
			// we assume, that per-domain-keys always END with the domain name (for performance reasons)
			// when this one day changes, then we can use the logic from gICS:
			// https://git.icm.med.uni-greifswald.de/ths/gics-project/-/blob/6c755fcbf40722ef2a9948ab81186d1ac804b92f/gics-ejb/src/main/java/org/emau/icmvc/ganimed/ttp/cm2/StatisticManagerBean.java#L195
			String domain = statKey.substring(statKey.lastIndexOf('.') + 1);
			if (allowedDomains.contains(domain))
			{
				visitedDomains.add(domain);
				return true;
			}
		}
		return false;
	}

	private class FilterInfo extends AllowedDomainsFilterInfo
	{
		private static final boolean TESTING = false;
		public FilterInfo()
		{
			super(() -> dao.getDomains().stream().map(DomainDTO::getName).toList());
		}
		@Override
		public Set<String> getAllDomains()
		{
			return TESTING ? Set.of("Demo", "MII") : super.getAllDomains();
		}
		@Override
		public Set<String> getAllowedDomains()
		{
			return TESTING ? Set.of("Demo") : super.getAllowedDomains();
		}
		@Override
		public boolean isUsingDomainBasedRoles()
		{
			return TESTING || super.isUsingDomainBasedRoles();
		}
		@Override
		public boolean isDenyingDomains()
		{
			return TESTING || super.isDenyingDomains();
		}
	}
}
