package org.emau.icmvc.ttp.epix.frontend.util;

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

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.emau.icmvc.ttp.epix.common.model.IdentifierDomainDTO;
import org.emau.icmvc.ttp.epix.common.model.enums.FieldName;
import org.emau.icmvc.ttp.epix.common.model.enums.Gender;
import org.emau.icmvc.ttp.epix.common.model.enums.VitalStatus;
import org.emau.icmvc.ttp.epix.frontend.controller.common.AbstractEpixBean;
import org.emau.icmvc.ttp.epix.frontend.controller.component.DomainSelector;

@SessionScoped
@ManagedBean(name = "epixHelper")
public class EpixHelper extends AbstractEpixBean
{
	List<IdentifierDomainDTO> cachedFilteredIdentifierDomains;

	@ManagedProperty(value = "#{domainSelector}")
	protected DomainSelector domainSelector;

	public ResourceBundle getBundle()
	{
		FacesContext facesContext = FacesContext.getCurrentInstance();
		return facesContext.getApplication().getResourceBundle(facesContext, "msg");
	}

	public List<Gender> getGenders()
	{
		return Arrays.asList(Gender.values());
	}

	public List<VitalStatus> getVitalStatuses()
	{
		return Arrays.asList(VitalStatus.values());
	}

	public boolean required(String fieldName)
	{
		try
		{
			return domainSelector.getSelectedDomainConfiguration().getRequiredFields().contains(FieldName.fromValue(fieldName));
		}
		catch (IllegalArgumentException e)
		{
			return false;
		}
	}

	public boolean showValueField(String valueField, boolean showBloomfilterFields)
	{
		return domainSelector.getSelectedDomainConfiguration().getValueFieldMapping().containsKey(valueField)
				&& (showBloomfilterFields || domainSelector.getSelectedDomainConfiguration().getPrivacy() == null || (domainSelector.getSelectedDomainConfiguration().getPrivacy() != null
				&& domainSelector.getSelectedDomainConfiguration().getPrivacy().getBloomFilterConfigs().stream()
				.noneMatch((bfc -> valueField.equals(bfc.getField().value())))));
	}

	public boolean showValueField(String valueField)
	{
		return showValueField(valueField, true);
	}

	public String getValueFieldLabel(String valueField)
	{
		if (showValueField(valueField))
		{
			String key = "value.label." + domainSelector.getSelectedDomainConfiguration().getValueFieldMapping().get(valueField);
			if (getBundle().containsKey(key))
			{
				return getBundle().getString(key);
			}
			else
			{
				return domainSelector.getSelectedDomainConfiguration().getValueFieldMapping().get(valueField);
			}
		}
		return getBundle().getString("common.person." + valueField);
	}

	public List<IdentifierDomainDTO> getIdentifierDomainsFiltered()
	{
		if (cachedFilteredIdentifierDomains == null)
		{
			cachedFilteredIdentifierDomains = managementService.getIdentifierDomains().stream()
					.filter(identifierDomain -> !identifierDomain.getName().equals(getDomainSelector().getSelectedDomain().getMpiDomain().getName()))
					.collect(Collectors.toList());
		}
		return cachedFilteredIdentifierDomains;
	}

	public void resetCachedFilteredIdentifierDomains()
	{
		cachedFilteredIdentifierDomains = null;
	}

	public DomainSelector getDomainSelector()
	{
		return domainSelector;
	}

	public void setDomainSelector(DomainSelector domainSelector)
	{
		this.domainSelector = domainSelector;
	}
}
