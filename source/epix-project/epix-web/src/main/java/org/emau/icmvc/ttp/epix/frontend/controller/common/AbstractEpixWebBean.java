package org.emau.icmvc.ttp.epix.frontend.controller.common;

/*-
 * ###license-information-start###
 * E-PIX - Enterprise Patient Identifier Cross-referencing
 * __
 * Copyright (C) 2009 - 2022 Trusted Third Party of the University Medicine Greifswald
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
import java.util.stream.Collectors;

import javax.faces.bean.ManagedProperty;

import org.emau.icmvc.ttp.epix.common.model.DomainDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentifierDomainDTO;
import org.emau.icmvc.ttp.epix.common.model.SourceDTO;
import org.emau.icmvc.ttp.epix.common.model.enums.FieldName;
import org.emau.icmvc.ttp.epix.frontend.controller.component.DomainSelector;

public abstract class AbstractEpixWebBean extends AbstractEpixBean
{
	private List<IdentifierDomainDTO> identifierDomainsFiltered;

	@ManagedProperty(value = "#{domainSelector}")
	protected DomainSelector domainSelector;

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

	public DomainSelector getDomainSelector()
	{
		return domainSelector;
	}

	public void setDomainSelector(DomainSelector domainSelector)
	{
		this.domainSelector = domainSelector;
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

	public List<IdentifierDomainDTO> getIdentifierDomainsFiltered()
	{
		if (identifierDomainsFiltered == null)
		{
			identifierDomainsFiltered = managementService.getIdentifierDomains().stream()
					.filter(iddomain -> !iddomain.getName().equals(domainSelector.getSelectedDomain().getMpiDomain().getName()))
					.collect(Collectors.toList());
		}
		return identifierDomainsFiltered;
	}

	public List<SourceDTO> getSources()
	{
		return managementService.getSources();
	}

	public DomainDTO getSelectedDomain()
	{
		return domainSelector.getSelectedDomain();
	}
}
