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

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.emau.icmvc.ttp.epix.common.model.IdentifierDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityInDTO;

@ViewScoped
@ManagedBean(name = "identifierController")
public class IdentifierController extends AbstractEpixWebBean
{
	IdentityInDTO identity;
	List<IdentifierDTO> identifiers;

	public void init(IdentityInDTO identity)
	{
		if (this.identity == null)
		{
			this.identity = identity;
			this.identifiers = identity.getIdentifiers();
		}
	}

	public void addIdentifier()
	{
		this.identifiers.add(new IdentifierDTO());
	}
	
	public void removeIdentifier(IdentifierDTO identifier)
	{
		this.identifiers.remove(identifier);
	}

	public List<IdentifierDTO> getIdentifiers()
	{
		return identifiers;
	}

	public void setIdentifiers(List<IdentifierDTO> identifiers)
	{
		this.identifiers = identifiers;
	}
}
