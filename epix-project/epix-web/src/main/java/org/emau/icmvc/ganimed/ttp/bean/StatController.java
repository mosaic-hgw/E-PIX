package org.emau.icmvc.ganimed.ttp.bean;

/*
 * ###license-information-start###
 * E-PIX - Enterprise Patient Identifier Cross-referencing
 * __
 * Copyright (C) 2009 - 2017 The MOSAIC Project - Institut fuer Community Medicine der
 * 							Universitaetsmedizin Greifswald - mosaic-projekt@uni-greifswald.de
 * 							concept and implementation
 * 							c. schack, d.langner, l. geidel
 * 							web client
 * 							a. blumentritt
 * 							g. weiher
 * 							please cite our publications
 * 							http://dx.doi.org/10.3414/ME14-01-0133
 * 							http://dx.doi.org/10.1186/s12967-015-0545-6
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


import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.emau.icmvc.ganimed.epix.service.EPIXService;
import org.emau.icmvc.ganimed.ttp.gstats.CommonStatisticBean;
import org.emau.icmvc.ganimed.ttp.gstats.StatisticManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedBean(name = "StatController")
@ViewScoped
/**
 * Controller Class to use statistic manager bean
 * 
 * @author bialkem
 *
 */
public class StatController {
	
	@EJB(beanName = "StatisticEjbBean")
	protected StatisticManager statProxy;

	private final Logger logger = LoggerFactory.getLogger(StatController.class);

	//@EJB(lookup = "java:global/epix/epix-ejb/StatisticManagerBean!org.emau.icmvc.ganimed.ttp.gstats.StatisticManager")
	//private StatisticManager statManager;

	private CommonStatisticBean lastStat;

	public CommonStatisticBean getLastStat() {
		if (logger.isDebugEnabled()) {
			logger.debug("getLastStat()");
		}
		return lastStat;
	}

	@PostConstruct
	public void init() {
		if (logger.isDebugEnabled()) {
			logger.debug("initializing");
		}
		update();
	}

	public void update() {
		// invoke update
		//statManager.updateStats();
		statProxy.updateStats();
		// getdata

		//lastStat = statManager.getLatestStats();
		lastStat = statProxy.getLatestStats();
	}
}
