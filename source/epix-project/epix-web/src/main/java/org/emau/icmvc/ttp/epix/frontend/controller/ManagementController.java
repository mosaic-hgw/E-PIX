package org.emau.icmvc.ttp.epix.frontend.controller;

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


import java.io.Serial;
import java.io.Serializable;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import org.emau.icmvc.ttp.epix.frontend.controller.common.AbstractEpixWebBean;
import org.emau.icmvc.ttp.epix.frontend.controller.management.DomainBean;
import org.emau.icmvc.ttp.epix.frontend.controller.management.IdentifierDomainBean;
import org.emau.icmvc.ttp.epix.frontend.controller.management.SourceBean;

@ViewScoped
@Named( "managementController")
public class ManagementController extends AbstractEpixWebBean implements Serializable
{
	@Serial
	private static final long serialVersionUID = 7536563585698378503L;
	private final DomainBean domain = new DomainBean();
	private final SourceBean source = new SourceBean();
	private final IdentifierDomainBean identifierDomain = new IdentifierDomainBean();

	@PostConstruct
	private void init()
	{
		domain.setDomainSelector(getDomainSelector());
		domain.init(epixHelper, text);
		source.init(epixHelper, text);
		identifierDomain.init(epixHelper, text);
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
