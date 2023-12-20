package org.emau.icmvc.ttp.epix.frontend.controller.management;

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

import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.emau.icmvc.ttp.epix.common.exception.DuplicateEntryException;
import org.emau.icmvc.ttp.epix.common.exception.InvalidParameterException;
import org.emau.icmvc.ttp.epix.common.exception.MPIException;
import org.emau.icmvc.ttp.epix.common.exception.ObjectInUseException;
import org.emau.icmvc.ttp.epix.common.exception.UnknownObjectException;
import org.emau.icmvc.ttp.epix.common.model.SourceDTO;
import org.emau.icmvc.ttp.epix.frontend.controller.common.AbstractEpixBean;
import org.emau.icmvc.ttp.epix.frontend.controller.common.ICRUDObject;
import org.emau.icmvc.ttp.epix.frontend.util.EpixHelper;
import org.emau.icmvc.ttp.epix.service.EPIXManagementService;
import org.icmvc.ttp.web.controller.Text;

public class SourceBean extends AbstractEpixBean implements ICRUDObject<SourceDTO>
{
	private SourceDTO selected;

	@Override
	public void init(EPIXManagementService managementService, EpixHelper epixHelper, Text text)
	{
		this.managementService = managementService;
		this.text = text;
		pageMode = PageMode.READ;
		reload();
	}

	@Override
	public void reload()
	{}

	@Override
	public void onShowDetails(SourceDTO object)
	{
		selected = object;
		pageMode = PageMode.READ;
	}

	@Override
	public void onNew()
	{
		selected = new SourceDTO();
		pageMode = PageMode.NEW;
	}

	@Override
	public void onEdit(SourceDTO object)
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
				managementService.updateSource(selected);
				logMessage(new MessageFormat(getBundle().getString("domain.message.source.edit.success")).format(args), Severity.INFO);
			}
			catch (InvalidParameterException e)
			{
				logMessage(getBundle().getString("domain.message.source.edit.invalidParameter"), Severity.WARN);
			}
			catch (UnknownObjectException e)
			{
				logMessage(getBundle().getString("domain.message.source.edit.unknownObject"), Severity.WARN);
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
				// operate on a copy for the case that we stay on the dialog after a warning
				SourceDTO tmp = new SourceDTO(selected);
				tmp.setName(StringUtils.isEmpty(tmp.getName()) ? tmp.getLabel().replace(" ", "_") : tmp.getName());
				managementService.addSource(tmp);
				logMessage(new MessageFormat(getBundle().getString("domain.message.source.add.success")).format(args), Severity.INFO);
				// on success write back changes on selected (currently unused)
				selected = new SourceDTO(tmp);
			}
			catch (DuplicateEntryException e)
			{
				logMessage(getBundle().getString("domain.message.source.add.duplicateEntry"), Severity.WARN);
			}
			catch (InvalidParameterException e)
			{
				logMessage(getBundle().getString("domain.message.source.add.invalidParameter"), Severity.WARN);
			}
			catch (MPIException e)
			{
				logMPIException(e);
			}
		}

		reload();
	}

	@Override
	public void onCancel()
	{
		selected = null;
	}

	@Override
	public boolean isEditable(SourceDTO object)
	{
		return true;
	}

	@Override
	public void onDeleteCurrent()
	{
		Object[] args = { selected.getLabel() };
		try
		{
			managementService.deleteSource(selected.getName());
			logMessage(new MessageFormat(getBundle().getString("domain.message.source.delete.success")).format(args), Severity.INFO);
			reload();
		}
		catch (UnknownObjectException e)
		{
			logMessage(getBundle().getString("domain.message.source.delete.unknownObject"), Severity.WARN);
		}
		catch (InvalidParameterException e)
		{
			logMessage(getBundle().getString("domain.message.source.delete.invalidParameter"), Severity.WARN);
		}
		catch (ObjectInUseException e)
		{
			logMessage(getBundle().getString("domain.message.source.delete.objectInUse"), Severity.WARN);
		}
		catch (MPIException e)
		{
			logMPIException(e);
		}
	}

	@Override
	public List<SourceDTO> getAll()
	{
		return managementService.getSources();
	}

	@Override
	public boolean isNew()
	{
		if (selected == null || selected.getName() == null)
		{
			return true;
		}

		if (pageMode == PageMode.NEW)
		{
			return true;
		}

		for (SourceDTO sourceDTO : getAll())
		{
			if (sourceDTO.getName().equals(selected.getName()))
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public SourceDTO getSelected()
	{
		return selected;
	}

	@Override
	public void setSelected(SourceDTO selected)
	{
		this.selected = selected != null ? selected : this.selected;
	}
}
