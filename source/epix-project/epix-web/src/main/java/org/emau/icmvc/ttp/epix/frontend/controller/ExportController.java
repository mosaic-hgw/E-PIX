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


import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.emau.icmvc.ttp.epix.common.exception.InvalidParameterException;
import org.emau.icmvc.ttp.epix.common.exception.MPIException;
import org.emau.icmvc.ttp.epix.common.exception.UnknownObjectException;
import org.emau.icmvc.ttp.epix.common.model.IdentifierDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityOutDTO;
import org.emau.icmvc.ttp.epix.common.model.PersonDTO;
import org.emau.icmvc.ttp.epix.frontend.controller.common.AbstractBatchBean;
import org.emau.icmvc.ttp.epix.frontend.model.Column;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;

@ViewScoped
@ManagedBean(name = "exportController")
public class ExportController extends AbstractBatchBean
{
	private ExportController.Mode mode;
	private ExportController.IdentityType exportIdentityType;
	private boolean onlyMainIdentityIdentifiers = true;
	private boolean searched = false;

	@PostConstruct
	public void construct()
	{
		init();
		mode = Mode.start;
	}

	@Override
	public void init()
	{
		super.init();
		loadColumnsForExport();
		originalColumns.clear();
		originalColumns.addAll(columns);
		searched = false;
	}

	public void chooseExportAllPersons()
	{
		init();
		mode = Mode.exportAllPersons;
	}

	public void chooseExportByIdentifierDomain()
	{
		init();
		mode = Mode.exportByIdentifierDomain;
	}

	public void chooseExportByIdentifiers()
	{
		init();
		uploadFile = null;
		mode = Mode.exportByIdentifiers;
	}

	public void exportAllPersons()
	{
		init();
		searched = true;
		List<PersonDTO> persons = new ArrayList<>();
		try
		{
			persons = service.getPersonsForDomain(domainSelector.getSelectedDomainName());
		}
		catch (InvalidParameterException | UnknownObjectException e)
		{
			logMessage(e);
		}

		for (PersonDTO person : persons)
		{
			List<IdentifierDTO> identifiers = new ArrayList<>();
			identifiers.addAll(person.getReferenceIdentity().getIdentifiers());
			if (!onlyMainIdentityIdentifiers)
			{
				List<IdentityOutDTO> allIdentities = new ArrayList<>();
				allIdentities.add(person.getReferenceIdentity());
				allIdentities.addAll(person.getOtherIdentities());

				for (IdentityOutDTO identity : allIdentities)
				{
					identifiers.addAll(identity.getIdentifiers());
				}
			}

			exportPerson(person.getReferenceIdentity(), person.getMpiId().getValue(), identifiers, "", false);
		}
	}

	public void exportByIdentifierDomain()
	{
		init();
		searched = true;

		if (selectedIndentifierDomain == null || exportIdentityType == null)
		{
			logMessage(getBundle().getString("export.warn.selectIdentifierDomainAndIdentity"), Severity.WARN);
			return;
		}

		List<IdentityOutDTO> identities;
		try
		{
			identities = service.getIdentitiesForDomain(domainSelector.getSelectedDomainName());

			for (IdentityOutDTO identity : identities)
			{
				for (IdentifierDTO identifier : identity.getIdentifiers())
				{
					if (identifier.getIdentifierDomain().equals(selectedIndentifierDomain))
					{
						if (exportIdentityType.equals(IdentityType.original))
						{
							String mpi = service.getMPIForIdentifier(domainSelector.getSelectedDomainName(), identifier);
							exportPerson(identity, mpi, identity.getIdentifiers(), "", false);
						}
						else if (exportIdentityType.equals(IdentityType.reference))
						{
							PersonDTO person = service.getPersonByLocalIdentifier(domainSelector.getSelectedDomainName(), identifier);
							exportPerson(person.getReferenceIdentity(), person.getMpiId().getValue(), identity.getIdentifiers(), "", false);
						}
					}
				}
			}
		}
		catch (InvalidParameterException | UnknownObjectException e)
		{
			this.logMessage(e);
		}
	}

	/**
	 * Upload a list of local ids to be exported
	 *
	 * @param event
	 * @throws IOException
	 */
	public void handleUploadIdentifiers(FileUploadEvent event) throws IOException
	{
		init();
		uploadColumns = new ArrayList<>();
		uploadFile = event.getFile();

		try (BufferedReader rd = new BufferedReader(new StringReader(new String(uploadFile.getContent(), StandardCharsets.UTF_8))))
		{
			uploadFile(rd);

			if (data.isEmpty())
			{
				this.logMessage(getBundle().getString("export.warn.emptyFile"), Severity.WARN);
			}
		}
		catch (Exception e)
		{
			this.logMessage(e);
		}
	}

	/**
	 * Find the persons for the uploaded local ids
	 *
	 * @throws MPIException
	 * @throws InvalidParameterException
	 */
	public void exportByIdentitifers()
	{
		searched = true;

		// Iterate over all imported local ids
		for (List<String> record : data)
		{
			IdentifierDTO identifier = new IdentifierDTO();
			identifier.setIdentifierDomain(selectedIndentifierDomain);
			identifier.setValue(record.get(0));

			try
			{
				PersonDTO person = service.getPersonByLocalIdentifier(domainSelector.getSelectedDomainName(), identifier);
				List<IdentityOutDTO> identities = new ArrayList<>();
				identities.add(person.getReferenceIdentity());
				identities.addAll(person.getOtherIdentities());

				IdentityOutDTO identity = null;

				outerspace: for (IdentityOutDTO id : identities)
				{
					for (IdentifierDTO idf : id.getIdentifiers())
					{
						if (idf.getIdentifierDomain().equals(identifier.getIdentifierDomain()) && idf.getValue().equals(identifier.getValue()))
						{
							identity = id;
							break outerspace;
						}
					}
				}

				if (identity == null)
				{
					logger.error("Cannot find identity with identifierValue: " + identifier.getValue() + " in person with id: " + person.getPersonId());
				}
				else if (exportIdentityType.equals(IdentityType.original))
				{
					String mpi = service.getMPIForIdentifier(domainSelector.getSelectedDomainName(), identifier);
					keepSpecificIdentifierValue(identity, identifier);
					exportPerson(identity, mpi, identity.getIdentifiers(), "", false);
				}
				else if (exportIdentityType.equals(IdentityType.reference))
				{
					keepSpecificIdentifierValue(identity, identifier);
					exportPerson(person.getReferenceIdentity(), person.getMpiId().getValue(), identity.getIdentifiers(), "", false);
				}
			}
			catch (InvalidParameterException | UnknownObjectException e)
			{
				logMessage(e.getLocalizedMessage(), Severity.ERROR);
			}
		}

		if (exportData.isEmpty())
		{
			logMessage(getBundle().getString("export.warn.noPersons"), Severity.WARN);
		}
	}

	/**
	 * If the identity has multiple identifiers for the same domain, keep only the value of the given identifier for this domain
	 *
	 * @param identity
	 * @param identifier
	 */
	private void keepSpecificIdentifierValue(IdentityOutDTO identity, IdentifierDTO identifier)
	{
		// If the identity has multiple identifiers with the same domain, return the identifier with the uploaded value
		Iterator<IdentifierDTO> iterator = identity.getIdentifiers().iterator();
		while (iterator.hasNext())
		{
			IdentifierDTO idf = iterator.next();
			if (idf.getIdentifierDomain().equals(identifier.getIdentifierDomain()) && !idf.getValue().equals(identifier.getValue()))
			{
				iterator.remove();
			}
		}
	}

	/**
	 * @throws MPIException
	 */
	public void handleDownloadResult()
	{
		reorderColumns();
		createDownloadFile("export");
	}

	private void reorderColumns()
	{
		ArrayList<Column> columnOrderNew = new ArrayList<>();

		DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("main:persons_table");

		for (UIColumn uiColumn : dataTable.getColumns())
		{
			String key = uiColumn.getColumnKey();
			Integer index = Integer.parseInt(key.substring(key.lastIndexOf(":") + 1));

			Column column = originalColumns.get(index);
			if (column.getActive())
			{
				columnOrderNew.add(column);
			}
		}
		columns = columnOrderNew;
	}

	public List<List<String>> getExportData()
	{
		return exportData;
	}

	public UploadedFile getUploadFile()
	{
		return uploadFile;
	}

	public boolean isDownloadEnabled()
	{
		return downloadEnabled;
	}

	public void setDownloadEnabled(boolean downloadEnabled)
	{
		this.downloadEnabled = downloadEnabled;
	}

	public enum Mode
	{
		start, exportAllPersons, exportByIdentifierDomain, exportByIdentifiers
	}

	;

	public String getMode()
	{
		return mode.toString();
	}

	public enum IdentityType
	{
		original, reference
	}

	;

	public IdentityType[] getIdentityTypes()
	{
		return IdentityType.values();
	}

	public ExportController.IdentityType getExportIdentity()
	{
		return exportIdentityType;
	}

	public void setExportIdentity(ExportController.IdentityType exportIdentity)
	{
		this.exportIdentityType = exportIdentity;
	}

	public boolean isOnlyMainIdentityIdentifiers()
	{
		return onlyMainIdentityIdentifiers;
	}

	public void setOnlyMainIdentityIdentifiers(boolean onlyMainIdentityIdentifiers)
	{
		this.onlyMainIdentityIdentifiers = onlyMainIdentityIdentifiers;
	}

	public boolean isSearched()
	{
		return searched;
	}
}
