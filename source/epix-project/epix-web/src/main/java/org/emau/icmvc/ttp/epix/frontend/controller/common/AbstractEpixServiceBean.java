package org.emau.icmvc.ttp.epix.frontend.controller.common;

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

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.emau.icmvc.ttp.auth.TTPNames.Tool;
import org.emau.icmvc.ttp.epix.service.EPIXManagementService;
import org.emau.icmvc.ttp.epix.service.EPIXService;
import org.emau.icmvc.ttp.epix.service.EPIXServiceWithNotification;
import org.icmvc.ttp.web.controller.AbstractBean;

/**
 * An abstract bean  which encapsulates the E-PIX service interfaces
 * to ensure intercepting all calls to service methods
 * with updating the current auth context in the thread local context.
 *
 * @author moser
 */
public class AbstractEpixServiceBean extends AbstractBean
{
	protected static final String TOOL = "E-PIX";
	protected static final String NOTIFICATION_CLIENT_ID = "E-PIX_Web";
	@EJB(lookup = "java:global/epix/epix-ejb/EPIXServiceImpl!org.emau.icmvc.ttp.epix.service.EPIXService")
	private EPIXService serviceTarget;
	protected EPIXService service;
	@EJB(lookup = "java:global/epix/epix-ejb/EPIXServiceWithNotificationImpl!org.emau.icmvc.ttp.epix.service.EPIXServiceWithNotification")
	private EPIXServiceWithNotification serviceWithNotificationTarget;
	protected EPIXServiceWithNotification serviceWithNotification;
	@EJB(lookup = "java:global/epix/epix-ejb/EPIXManagementServiceImpl!org.emau.icmvc.ttp.epix.service.EPIXManagementService")
	private EPIXManagementService managementServiceTarget;
	protected EPIXManagementService managementService;

	@PostConstruct
	private void init()
	{
		if (getWebAuthContext().isUsingDomainBasedRolesDisabled(Tool.epix))
		{
			service = serviceTarget;
			managementService = managementServiceTarget;
			serviceWithNotification = serviceWithNotificationTarget;
		}
		else
		{
			service = getWebAuthContext().createUpdateAuthContextProxy(serviceTarget, EPIXService.class);
			managementService = getWebAuthContext().createUpdateAuthContextProxy(managementServiceTarget, EPIXManagementService.class);
			serviceWithNotification = getWebAuthContext().createUpdateAuthContextProxy(serviceWithNotificationTarget, EPIXServiceWithNotification.class);
		}
	}

	public EPIXService getService()
	{
		return service;
	}

	public EPIXServiceWithNotification getServiceWithNotification()
	{
		return serviceWithNotification;
	}

	public EPIXManagementService getManagementService()
	{
		return managementService;
	}

	public String getTool()
	{
		return TOOL;
	}
}
