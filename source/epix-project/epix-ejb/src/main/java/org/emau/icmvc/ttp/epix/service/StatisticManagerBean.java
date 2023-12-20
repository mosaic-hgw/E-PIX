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

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.emau.icmvc.ttp.epix.common.model.StatisticDTO;
import org.emau.icmvc.ttp.epix.persistence.PublicDAO;

@WebService(name = "statisticService")
@SOAPBinding(style = SOAPBinding.Style.RPC)
@Stateless
@Remote(StatisticManager.class)
public class StatisticManagerBean implements StatisticManager
{
	private static final Logger logger = LogManager.getLogger(StatisticManagerBean.class);
	@EJB
	protected PublicDAO dao;
	private boolean enableAutoUpdate = true;

	@Override
	public StatisticDTO getLatestStats()
	{
		logger.debug("call to getLatestStats");
		StatisticDTO result = dao.getLatestStats();
		if (logger.isDebugEnabled())
		{
			logger.debug("result of getLatestStats: " + result);
		}
		return result;
	}

	@Override
	public List<StatisticDTO> getAllStats()
	{
		logger.debug("call to getAllStats");
		List<StatisticDTO> result = dao.getAllStats();
		if (logger.isDebugEnabled())
		{
			logger.debug("number of results: " + result.size());
		}
		return result;
	}

	@Override
	public StatisticDTO updateStats()
	{
		logger.debug("call to getLatestStats");
		StatisticDTO result = dao.updateStats();
		if (logger.isDebugEnabled())
		{
			logger.debug("result of getLatestStats: " + result);
		}
		return result;
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
			logger.info("stat for " + statisticDTO + " added");
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
		logger.debug("Scheduling Mode enabled: " + enableAutoUpdate);
	}
}
