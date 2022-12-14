package org.emau.icmvc.ttp.epix.frontend.converter;

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

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.emau.icmvc.ttp.epix.common.model.SourceDTO;
import org.emau.icmvc.ttp.epix.service.EPIXManagementService;

// Cannot use @FacesConverter with @EJB until JSF 2.3
@ManagedBean
@RequestScoped
public class SourceDTOConverter implements Converter
{
	@EJB(lookup = "java:global/epix/epix-ejb/EPIXManagementServiceImpl!org.emau.icmvc.ttp.epix.service.EPIXManagementService")
	protected EPIXManagementService managementService;

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object modelValue)
	{
		if (modelValue == null)
		{
			return "";
		}

		if (modelValue instanceof SourceDTO)
		{
			return ((SourceDTO) modelValue).getName();
		}
		else
		{
			throw new ConverterException(new FacesMessage(modelValue + " is not a valid SourceDTO"));
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
			List<SourceDTO> sources = managementService.getSources();

			for (SourceDTO source : sources)
			{
				if (source.getName().equals(submittedValue))
				{
					return source;
				}
			}
			throw new ConverterException(new FacesMessage("Cannot find SourceDTO with ID: " + submittedValue));
		}
		catch (NumberFormatException e)
		{
			throw new ConverterException(new FacesMessage(submittedValue + " is not a valid SourceDTO ID"), e);
		}
	}
}