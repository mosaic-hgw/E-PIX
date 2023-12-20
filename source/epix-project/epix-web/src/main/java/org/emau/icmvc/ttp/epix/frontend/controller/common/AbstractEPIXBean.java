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


import java.util.ResourceBundle;

import javax.faces.context.FacesContext;

import org.emau.icmvc.ttp.epix.common.exception.InvalidParameterException;
import org.emau.icmvc.ttp.epix.common.exception.MPIException;

public abstract class AbstractEpixBean extends AbstractEpixServiceBean
{

	@Override
	public ResourceBundle getBundle()
	{
		FacesContext facesContext = FacesContext.getCurrentInstance();
		return facesContext.getApplication().getResourceBundle(facesContext, "msg");
	}

	public void logMPIException(MPIException e)
	{
		String key = e.getErrorCode() != null ?
				"common.error.MPIErrorCode." + e.getErrorCode().toString() :
				"common.error." + (e.getMessage() != null ? e.getMessage().replace(" ", "_") : "MPIError");

		logBundleError(key, e.getLocalizedMessage());
	}

	public void logInvalidParameterException(InvalidParameterException e)
	{
		String key = e.getErrorCode() != null ?
				"common.error.InvalidParameterCode." + e.getErrorCode().toString() :
				"common.error." + (e.getMessage() != null ? e.getMessage().replace(" ", "_") : "InvalidParameter");

		logBundleError(key, e.getLocalizedMessage());
	}

	private void logBundleError(String key, String localizedMsg)
	{
		if (getBundle().containsKey(key))
		{
			// Log original message
			logger.error(localizedMsg);
			// Log and display translated message
			logBundleMessage(key, Severity.ERROR, true);
		}
		else
		{
			// Log and display original message
			logMessage(localizedMsg, Severity.ERROR);
		}
	}

	protected void logBundleMessage(String key, Severity severity, boolean scrollToTop)
	{
		String msg = getBundle().getString(key);
		logMessage(msg, severity, scrollToTop);
	}
}
