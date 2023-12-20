package org.emau.icmvc.ttp.epix.frontend.controller.component;

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


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.emau.icmvc.ttp.epix.common.exception.InvalidParameterException;
import org.emau.icmvc.ttp.epix.common.exception.UnknownObjectException;
import org.emau.icmvc.ttp.epix.common.model.DomainDTO;
import org.emau.icmvc.ttp.epix.common.model.config.ConfigurationContainer;
import org.emau.icmvc.ttp.epix.common.model.enums.MatchingMode;
import org.emau.icmvc.ttp.epix.frontend.controller.common.AbstractEpixBean;

@SessionScoped
@ManagedBean(name = "domainSelector")
public class DomainSelector extends AbstractEpixBean
{
	private List<DomainDTO> domains = new ArrayList<>();
	private DomainDTO selectedDomain;
	private ConfigurationContainer configurationContainer;

	/**
	 * Init domains.
	 */
	@PostConstruct
	private void initialize()
	{
		loadDomains();
		if (domains.size() > 0)
		{
			setSelectedDomain(domains.get(0));
		}
	}

	public void loadDomains()
	{
		domains = managementService.getDomains();
		domains.sort((d1, d2) -> {
			String l1 = StringUtils.isNotEmpty(d1.getLabel()) ? d1.getLabel() : d1.getName();
			String l2 = StringUtils.isNotEmpty(d2.getLabel()) ? d2.getLabel() : d2.getName();
			return l1.toLowerCase().compareTo(l2.toLowerCase());
		});
	}

	public List<DomainDTO> getDomains()
	{
		// We are @SessionScoped but login via OIDC does not invalidate the session.
		// So we need to manually check if the auth type has changed and then reload domains.
		if (hasAuthTypeChanged()) {
			loadDomains();
		}
		return domains;
	}

	public DomainDTO getSelectedDomain()
	{
		List<DomainDTO> domains = getDomains();
		DomainDTO temp = domains.stream().filter(d -> d.getName().equals(getSelectedDomainName())).findFirst().orElse(null);

		// Domain does not exist
		if (temp == null)
		{
			selectedDomain = domains.size() > 0 ? domains.get(0) : null;
			// selectedDomain can be null
			if (selectedDomain != null)
			{
				loadDomainData();
			}
		}
		// Domain does exist
		else
		{
			// Reload domains list if the selected domain was altered
			if (!temp.equals(selectedDomain))
			{
				loadDomains();
				selectedDomain = temp;
				// selectedDomain can not be null
				loadDomainData();
			}
		}
		return selectedDomain;
	}

	public void setSelectedDomain(DomainDTO selectedDomain)
	{
		this.selectedDomain = selectedDomain;
		loadDomainData();

		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		try
		{
			ec.redirect(((HttpServletRequest) ec.getRequest()).getRequestURI());
		}
		catch (IOException e)
		{
			logMessage(e);
		}
	}

	public void setSelectedDomain(String name)
	{
		for (DomainDTO domain : getDomains())
		{
			if (domain.getName().equals(name))
			{
				selectedDomain = domain;
				loadDomainData();
			}
		}
	}

	public boolean isMindConcept()
	{
		for (DomainDTO domain : getDomains())
		{
			if (domain.getMatchingMode().equals(MatchingMode.NO_DECISION))
			{
				return true;
			}
		}
		return false;
	}

	private void loadDomainData()
	{
		try
		{
			if (configurationContainer == null)
			{
				if (selectedDomain != null)
				{
					loadDomains();
				}
				else {
					getSelectedDomain(); // using side-effects
				}
			}
			configurationContainer = managementService.getConfigurationForDomain(selectedDomain.getName());
		}
		catch (InvalidParameterException | UnknownObjectException e)
		{
			logMessage(e);
		}
	}

	private boolean containsDomain(DomainDTO domain)
	{
		for (DomainDTO d : getDomains())
		{
			if (d.getName().equals(domain.getName()))
			{
				return true;
			}
		}
		return false;
	}

	public String getLabel(DomainDTO domain)
	{
		return StringUtils.isEmpty(domain.getLabel()) ? domain.getName() : domain.getLabel();
	}

	public String getSelectedDomainName()
	{
		return selectedDomain != null ? selectedDomain.getName() : null;
	}

	public ConfigurationContainer getSelectedDomainConfiguration()
	{
		return configurationContainer;
	}
}
