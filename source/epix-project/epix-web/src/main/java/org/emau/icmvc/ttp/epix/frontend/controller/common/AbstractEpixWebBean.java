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

import java.util.List;

import javax.faces.bean.ManagedProperty;

import org.emau.icmvc.ttp.epix.common.model.DomainDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentifierDomainDTO;
import org.emau.icmvc.ttp.epix.common.model.SourceDTO;
import org.emau.icmvc.ttp.epix.frontend.controller.component.DomainSelector;
import org.emau.icmvc.ttp.epix.frontend.util.EpixHelper;

public abstract class AbstractEpixWebBean extends AbstractEpixBean
{
	@ManagedProperty(value = "#{epixHelper}")
	protected EpixHelper epixHelper;

	@Deprecated
	public boolean showValueField(String valueField, boolean showBloomfilterFields)
	{
		return epixHelper.showValueField(valueField, showBloomfilterFields);
	}

	@Deprecated
	public boolean showValueField(String valueField)
	{
		return epixHelper.showValueField(valueField, true);
	}

	@Deprecated
	public String getValueFieldLabel(String valueField)
	{
		return epixHelper.getValueFieldLabel(valueField);
	}

	public DomainSelector getDomainSelector()
	{
		return epixHelper.getDomainSelector();
	}

	public List<IdentifierDomainDTO> getIdentifierDomainsFiltered()
	{
		return epixHelper.getIdentifierDomainsFiltered();
	}

	public List<SourceDTO> getSources()
	{
		return managementService.getSources();
	}

	public DomainDTO getSelectedDomain()
	{
		return getDomainSelector().getSelectedDomain();
	}

	public boolean isUseNotifications()
	{
		return getDomainSelector().getSelectedDomainConfiguration().isUseNotifications();
	}

	public void logMessage(String message, Severity severity) {
		super.logMessage(message, severity);
	}

	public void logMessage(String message, Severity severity, boolean scrollToTop) {
		super.logMessage(message, severity, scrollToTop);
	}

	public void setEpixHelper(EpixHelper epixHelper)
	{
		this.epixHelper = epixHelper;
	}

	public String getDeduplicationReasonLabel(String reason)
	{
		return getBundle().containsKey("deduplication." + reason) ? getBundle().getString("deduplication." + reason) : reason;
	}

	public String getDeduplicationReasonDescription(String description)
	{
		return getBundle().containsKey("deduplication." + description) ? getBundle().getString("deduplication." + description) : description;
	}
}
