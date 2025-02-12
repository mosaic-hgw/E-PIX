package org.emau.icmvc.ttp.epix.frontend.controller.common;

/*-
 * ###license-information-start###
 * gICS - a Generic Informed Consent Service
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

import java.io.Serial;
import java.io.Serializable;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.emau.icmvc.ttp.auth.TTPNames;
import org.emau.icmvc.ttp.epix.service.EPIXManagementService;
import org.emau.icmvc.ttp.epix.service.EPIXService;
import org.emau.icmvc.ttp.epix.service.EPIXServiceWithNotification;
import org.emau.icmvc.ttp.epix.service.StatisticManager;
import org.emau.icmvc.ttp.util.ProxyBuilder;
import org.icmvc.ttp.web.util.WebAuthContext;

/**
 * Common helper for using the (proxied) services.
 *
 * @author moser
 */
@Named
@SessionScoped
public class ServiceHelper implements Serializable
{
	@Serial
	private static final long serialVersionUID = 2240183450244716423L;

	public static final String TOOL = "E-PIX";
	public static final String NOTIFICATION_CLIENT_ID = TOOL + "_Web";

	@Inject
	private WebAuthContext webAuthContext;

	@EJB(lookup = "java:global/epix/epix-ejb/EPIXServiceImpl!org.emau.icmvc.ttp.epix.service.EPIXService")
	private transient EPIXService serviceTarget;
	protected transient EPIXService service;
	protected transient EPIXService serviceWithAutomaticNotification;

	@EJB(lookup = "java:global/epix/epix-ejb/EPIXManagementServiceImpl!org.emau.icmvc.ttp.epix.service.EPIXManagementService")
	private transient EPIXManagementService managerTarget;
	protected transient EPIXManagementService manager;

	@EJB(lookup = "java:global/epix/epix-ejb/EPIXServiceWithNotificationImpl!org.emau.icmvc.ttp.epix.service.EPIXServiceWithNotification")
	private transient EPIXServiceWithNotification serviceWithNotificationTarget;
	protected transient EPIXServiceWithNotification serviceWithNotification;

	@EJB(lookup = "java:global/epix/epix-ejb/StatisticManagerBean!org.emau.icmvc.ttp.epix.service.StatisticManager")
	private transient StatisticManager statisticServiceTarget;
	private transient StatisticManager statisticService;

	@PostConstruct
	private void init() {
		if (getWebAuthContext().isUsingDomainBasedRolesDisabled(TTPNames.Tool.epix))
		{
			service = serviceTarget;
			manager = managerTarget;
			serviceWithNotification = serviceWithNotificationTarget;
			statisticService = statisticServiceTarget;
		}
		else
		{
			service = getWebAuthContext().createUpdateAuthContextProxy(serviceTarget, EPIXService.class);
			manager = getWebAuthContext().createUpdateAuthContextProxy(managerTarget, EPIXManagementService.class);
			serviceWithNotification = getWebAuthContext().createUpdateAuthContextProxy(serviceWithNotificationTarget, EPIXServiceWithNotification.class);
			statisticService = getWebAuthContext().createUpdateAuthContextProxy(statisticServiceTarget, StatisticManager.class);
		}
	}

	public EPIXService getService()
	{
		if (service == null)
		{
			init();
		}
		return service;
	}

	public EPIXManagementService getManager()
	{
		if (manager == null)
		{
			init();
		}
		return manager;
	}

	public EPIXServiceWithNotification getServiceWithNotification()
	{
		if (serviceWithNotification == null)
		{
			init();
		}
		return serviceWithNotification;
	}

	public EPIXService getServiceWithAutomaticNotification(boolean notify)
	{
		if (notify && serviceWithAutomaticNotification == null)
		{
			serviceWithAutomaticNotification = ProxyBuilder.wrap(getService(), EPIXService.class).withMatchingMethodsDelegatingInvocationHandler(
					getServiceWithNotification(), EPIXServiceWithNotification.class, new ProxyBuilder.ArgumentsPrepender(NOTIFICATION_CLIENT_ID)).build();
		}
		return notify ? serviceWithAutomaticNotification : getService();
	}

	public StatisticManager getStatisticService()
	{
		if (statisticService == null)
		{
			init();
		}
		return statisticService;
	}

	public WebAuthContext getWebAuthContext()
	{
		return webAuthContext;
	}
}
