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

import org.apache.commons.lang3.StringUtils;
import org.emau.icmvc.ttp.epix.common.exception.DuplicateEntryException;
import org.emau.icmvc.ttp.epix.common.exception.InvalidParameterException;
import org.emau.icmvc.ttp.epix.common.exception.MPIException;
import org.emau.icmvc.ttp.epix.common.exception.ObjectInUseException;
import org.emau.icmvc.ttp.epix.common.exception.UnknownObjectException;
import org.emau.icmvc.ttp.epix.common.exception.UnknownObjectType;
import org.emau.icmvc.ttp.epix.common.model.DomainDTO;
import org.emau.icmvc.ttp.epix.frontend.controller.common.AbstractEpixBean;
import org.emau.icmvc.ttp.epix.frontend.controller.common.ICRUDObject;
import org.emau.icmvc.ttp.epix.frontend.controller.component.DomainSelector;
import org.emau.icmvc.ttp.epix.service.EPIXManagementService;

public class DomainBean extends AbstractEpixBean implements ICRUDObject<DomainDTO>
{
	private DomainDTO selected;
	private DomainSelector domainSelector;

	@Override
	public void init(EPIXManagementService managementService)
	{
		this.managementService = managementService;
		reload();
	}

	@Override
	public void reload()
	{
		domainSelector.loadDomains();
	}

	@Override
	public void onShowDetails(DomainDTO domain)
	{
		selected = domain;
		pageMode = PageMode.READ;
	}

	@Override
	public void onNew()
	{
		selected = new DomainDTO();
		pageMode = PageMode.NEW;
	}

	@Override
	public void onEdit(DomainDTO object)
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
				// Small edit
				if (selected.isInUse())
				{
					managementService.updateDomainInUse(selected.getName(), selected.getLabel(), selected.getDescription());
				}
				// Large edit
				else
				{
					managementService.updateDomain(selected);
				}
				logMessage(new MessageFormat(getBundle().getString("domain.message.edit.success")).format(args), Severity.INFO);
			}
			catch (UnknownObjectException e)
			{
				logMessage(getBundle().getString("domain.message.edit.unknownDomain"), Severity.WARN);
			}
			catch (InvalidParameterException e)
			{
				logMessage(getBundle().getString("domain.message.edit.invalidParameter"), Severity.WARN);
			}
			catch (ObjectInUseException e)
			{
				logMessage(getBundle().getString("domain.message.edit.objectInUse"), Severity.WARN);
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
				managementService.addDomain(selected);
				logMessage(new MessageFormat(getBundle().getString("domain.message.add.success")).format(args), Severity.INFO);
			}
			catch (DuplicateEntryException e)
			{
				logMessage(getBundle().getString("domain.message.add.duplicateEntry"), Severity.WARN);
			}
			catch (InvalidParameterException e)
			{
				if (e.getParameterName().equals("domain.config"))
				{
					logMessage(getBundle().getString("domain.message.add.invalidConfig"), Severity.WARN);
				}
				else
				{
					logMessage(getBundle().getString("domain.message.add.invalidParameter"), Severity.WARN);
				}
			}
			catch (UnknownObjectException e)
			{
				if (e.getObjectType().equals(UnknownObjectType.SOURCE))
				{
					logMessage(getBundle().getString("domain.message.add.unknownSourceException"), Severity.WARN);
				}
				else if (e.getObjectType().equals(UnknownObjectType.IDENTITFIER_DOMAIN))
				{
					logMessage(getBundle().getString("domain.message.add.unknownIdentifierDomain"), Severity.WARN);
				}
			}
			catch (MPIException e)
			{
				logMPIException(e);
			}
		}

		reload();
		if (domainSelector.getSelectedDomainName() == null || selected.getName().equals(domainSelector.getSelectedDomainName()))
		{
			domainSelector.setSelectedDomain(selected.getName());
		}
		selected = null;
	}

	@Override
	public void onCancel()
	{
		selected = null;
	}

	@Override
	public boolean isEditable(DomainDTO object)
	{
		return !object.isInUse();
	}

	@Override
	public void onDeleteCurrent()
	{
		Object[] args = { selected.getLabel() };
		try
		{
			managementService.deleteDomain(selected.getName(), false);
			logMessage(new MessageFormat(getBundle().getString("domain.message.delete.success")).format(args), Severity.INFO);
			reload();
			if (domainSelector.getSelectedDomainName() == null || selected.getName().equals(domainSelector.getSelectedDomainName()))
			{
				domainSelector.setSelectedDomain(selected.getName());
			}
			selected = null;
		}
		catch (UnknownObjectException e)
		{
			logMessage(getBundle().getString("domain.message.delete.unknownObject"), Severity.WARN);
		}
		catch (InvalidParameterException e)
		{
			logMessage(getBundle().getString("domain.message.delete.invalidParameter"), Severity.WARN);
		}
		catch (ObjectInUseException e)
		{
			logMessage(getBundle().getString("domain.message.delete.objectInUse"), Severity.WARN);
		}
		catch (MPIException e)
		{
			logMPIException(e);
		}
	}

	@Override
	public List<DomainDTO> getAll()
	{
		return domainSelector.getDomains();
	}

	@Override
	public boolean isNew()
	{
		if (selected == null || selected.getName() == null)
		{
			return true;
		}

		for (DomainDTO domainDTO : getAll())
		{
			if (domainDTO.getName().equals(selected.getName()))
			{
				return false;
			}
		}
		return true;
	}

	public void setDomainSelector(DomainSelector domainSelector)
	{
		this.domainSelector = domainSelector;
	}

	@Override
	public DomainDTO getSelected()
	{
		return selected;
	}

	@Override
	public void setSelected(DomainDTO selected)
	{
		this.selected = selected != null ? selected : this.selected;
	}
}
