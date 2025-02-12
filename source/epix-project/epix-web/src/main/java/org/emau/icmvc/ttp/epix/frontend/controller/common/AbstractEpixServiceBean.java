package org.emau.icmvc.ttp.epix.frontend.controller.common;

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

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;
import org.emau.icmvc.ttp.epix.service.EPIXManagementService;
import org.emau.icmvc.ttp.epix.service.EPIXService;
import org.emau.icmvc.ttp.epix.service.StatisticManager;
import org.icmvc.ttp.web.controller.AbstractBean;
import org.icmvc.ttp.web.util.WebAuthContext;

/**
 * An abstract bean  which encapsulates the E-PIX service interfaces
 * to ensure intercepting all calls to service methods
 * with updating the current auth context in the thread local context.
 *
 * @author moser
 */
public class AbstractEpixServiceBean extends AbstractBean
{
	protected static final String TOOL = ServiceHelper.TOOL;

	@Inject
	private ServiceHelper serviceHelper;

	@PostConstruct
	private void init()
	{
	}

	public ServiceHelper getServiceHelper()
	{
		if (serviceHelper == null)
		{
			// https://github.com/eclipse-ee4j/mojarra/issues/4308
			// If the above does not work here, too: "@Inject private ServiceHelper serviceHelper;"
			// but "CDI.current().select(ServiceHelper.class).get();" helps
			serviceHelper = CDI.current().select(ServiceHelper.class).get();
		}

		return serviceHelper;
	}

	public EPIXService getService()
	{
		return getServiceHelper().getService();
	}

	public EPIXService getServiceWithAutomaticNotification(boolean notify)
	{
		return getServiceHelper().getServiceWithAutomaticNotification(notify);
	}

	public EPIXManagementService getManager()
	{
		return getServiceHelper().getManager();
	}

	public StatisticManager getStatisticService()
	{
		return getServiceHelper().getStatisticService();
	}

	@Override
	public WebAuthContext getWebAuthContext()
	{
		return getServiceHelper().getWebAuthContext();
	}

	public String getTool()
	{
		return TOOL;
	}
}
