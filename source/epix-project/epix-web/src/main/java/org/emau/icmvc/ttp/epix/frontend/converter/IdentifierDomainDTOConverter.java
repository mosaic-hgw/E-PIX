package org.emau.icmvc.ttp.epix.frontend.converter;

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

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.emau.icmvc.ttp.epix.common.model.IdentifierDomainDTO;
import org.emau.icmvc.ttp.epix.frontend.controller.common.AbstractEpixServiceBean;

// Cannot use @FacesConverter with @EJB until JSF 2.3
@ManagedBean
@RequestScoped
public class IdentifierDomainDTOConverter extends AbstractEpixServiceBean implements Converter
{
	@Override
	public String getAsString(FacesContext context, UIComponent component, Object modelValue)
	{
		if (modelValue == null)
		{
			return "";
		}

		if (modelValue instanceof IdentifierDomainDTO)
		{
			return String.valueOf(((IdentifierDomainDTO) modelValue).getName());
		}
		else
		{
			throw new ConverterException(new FacesMessage(text.sanitize(modelValue + " is not a valid IdentifierDomainDTO")));
		}
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String submittedValue)
	{
		if (submittedValue == null || submittedValue.isEmpty())
		{
			return null;
		}

		try
		{
			List<IdentifierDomainDTO> identifierDomains = managementService.getIdentifierDomains();

			for (IdentifierDomainDTO identifierDomain : identifierDomains)
			{
				if (String.valueOf(identifierDomain.getName()).equals(submittedValue))
				{
					return identifierDomain;
				}
			}
			throw new ConverterException(new FacesMessage("Cannot find IdentifierDomainDTO with ID: " + text.sanitize(submittedValue)));
		}
		catch (NumberFormatException e)
		{
			throw new ConverterException(new FacesMessage(text.sanitize(submittedValue) + " is not a valid IdentifierDomainDTO ID"), e);
		}
	}
}
