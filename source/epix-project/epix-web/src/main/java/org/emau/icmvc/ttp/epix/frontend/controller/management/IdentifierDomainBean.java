package org.emau.icmvc.ttp.epix.frontend.controller.management;

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


import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.emau.icmvc.ttp.epix.common.exception.DuplicateEntryException;
import org.emau.icmvc.ttp.epix.common.exception.InvalidParameterException;
import org.emau.icmvc.ttp.epix.common.exception.MPIException;
import org.emau.icmvc.ttp.epix.common.exception.ObjectInUseException;
import org.emau.icmvc.ttp.epix.common.exception.UnknownObjectException;
import org.emau.icmvc.ttp.epix.common.model.IdentifierDomainDTO;
import org.emau.icmvc.ttp.epix.frontend.controller.common.AbstractEpixBean;
import org.emau.icmvc.ttp.epix.frontend.controller.common.ICRUDObject;
import org.emau.icmvc.ttp.epix.service.EPIXManagementService;

public class IdentifierDomainBean extends AbstractEpixBean implements ICRUDObject<IdentifierDomainDTO>
{
	private IdentifierDomainDTO selected;

	@Override
	public void init(EPIXManagementService managementService)
	{
		this.managementService = managementService;
		reload();
	}

	@Override
	public void reload()
	{
	}

	@Override public void onShowDetails(IdentifierDomainDTO object)
	{
		selected = object;
		pageMode = PageMode.READ;
	}

	@Override
	public void onNew()
	{
		selected = new IdentifierDomainDTO();
		pageMode = PageMode.NEW;
	}

	@Override
	public void onEdit(IdentifierDomainDTO object)
	{
		onShowDetails(object);
		pageMode = PageMode.EDIT;
	}

	@Override
	public void onSaveCurrent()
	{
		Object[] args = { selected.getLabel() };

		// Update
		if (pageMode == PageMode.EDIT)
		{
			try
			{
				managementService.updateIdentifierDomain(selected);
				logMessage(new MessageFormat(getBundle().getString("domain.message.identifierDomain.edit.success")).format(args), Severity.INFO);
			}
			catch (InvalidParameterException e)
			{
				logMessage(getBundle().getString("domain.message.identifierDomain.edit.invalidParameter"), Severity.WARN);
			}
			catch (UnknownObjectException e)
			{
				logMessage(getBundle().getString("domain.message.identifierDomain.edit.unknownObject"), Severity.WARN);
			}
			catch (MPIException e)
			{
				logMPIException(e);
			}
		}
		// Save new
		else
		{
			try
			{
				selected.setName(StringUtils.isEmpty(selected.getName()) ? selected.getLabel().replace(" ", "_") : selected.getName());
				selected.setOid(StringUtils.isEmpty(selected.getOid()) ? UUID.randomUUID().toString() : selected.getOid());
				managementService.addIdentifierDomain(selected);
				logMessage(new MessageFormat(getBundle().getString("domain.message.identifierDomain.add.success")).format(args), Severity.INFO);
			}
			catch (DuplicateEntryException e)
			{
				logMessage(getBundle().getString("domain.message.identifierDomain.add.duplicateEntry"), Severity.WARN);
			}
			catch (InvalidParameterException e)
			{
				logMessage(getBundle().getString("domain.message.identifierDomain.add.invalidParameter"), Severity.WARN);
			}
			catch (MPIException e)
			{
				logMPIException(e);
			}
		}

		reload();
		selected = null;
	}

	@Override
	public void onCancel()
	{
		selected = null;
	}

	@Override public boolean isEditable(IdentifierDomainDTO object)
	{
		return true;
	}

	@Override
	public void onDeleteCurrent()
	{
		Object[] args = { selected.getLabel() };
		try
		{
			managementService.deleteIdentifierDomain(selected.getName());
			logMessage(new MessageFormat(getBundle().getString("domain.message.identifierDomain.delete.success")).format(args), Severity.INFO);
			reload();
		}
		catch (UnknownObjectException e)
		{
			logMessage(getBundle().getString("domain.message.identifierDomain.delete.unknownObject"), Severity.WARN);
		}
		catch (InvalidParameterException e)
		{
			logMessage(getBundle().getString("domain.message.identifierDomain.delete.invalidParameter"), Severity.WARN);
		}
		catch (ObjectInUseException e)
		{
			logMessage(getBundle().getString("domain.message.identifierDomain.delete.objectInUse"), Severity.WARN);
		}
		catch (MPIException e)
		{
			logMPIException(e);
		}
	}

	@Override
	public List<IdentifierDomainDTO> getAll()
	{
		return managementService.getIdentifierDomains();
	}

	public boolean isNew()
	{
		if (selected == null || selected.getName() == null)
		{
			return true;
		}

		for (IdentifierDomainDTO identifierDomainDTO : getAll())
		{
			if (identifierDomainDTO.getName().equals(selected.getName()))
			{
				return false;
			}
		}
		return true;
	}

	@Override public IdentifierDomainDTO getSelected()
	{
		return selected;
	}

	@Override public void setSelected(IdentifierDomainDTO selected)
	{
		this.selected = selected != null ? selected : this.selected;
	}
}
