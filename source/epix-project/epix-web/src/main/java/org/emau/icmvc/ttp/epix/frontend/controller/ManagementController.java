package org.emau.icmvc.ttp.epix.frontend.controller;

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


import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.emau.icmvc.ttp.epix.frontend.controller.common.AbstractEpixWebBean;
import org.emau.icmvc.ttp.epix.frontend.controller.management.DomainBean;
import org.emau.icmvc.ttp.epix.frontend.controller.management.IdentifierDomainBean;
import org.emau.icmvc.ttp.epix.frontend.controller.management.SourceBean;

@ViewScoped
@ManagedBean(name = "managementController")
public class ManagementController extends AbstractEpixWebBean
{
	private final DomainBean domain = new DomainBean();
	private final SourceBean source = new SourceBean();
	private final IdentifierDomainBean identifierDomain = new IdentifierDomainBean();

	@PostConstruct
	private void init()
	{
		domain.setDomainSelector(domainSelector);
		domain.init(managementService);
		source.init(managementService);
		identifierDomain.init(managementService);
	}

	public DomainBean getDomain()
	{
		return domain;
	}

	public SourceBean getSource()
	{
		return source;
	}

	public IdentifierDomainBean getIdentifierDomain()
	{
		return identifierDomain;
	}
}
