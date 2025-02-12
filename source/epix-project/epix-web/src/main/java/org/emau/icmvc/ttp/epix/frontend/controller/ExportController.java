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


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serial;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import org.apache.commons.lang3.StringUtils;
import org.emau.icmvc.ttp.epix.common.exception.InvalidParameterException;
import org.emau.icmvc.ttp.epix.common.exception.UnknownObjectException;
import org.emau.icmvc.ttp.epix.common.model.ContactInDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentifierDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentifierDomainDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityOutDTO;
import org.emau.icmvc.ttp.epix.common.model.PersonDTO;
import org.emau.icmvc.ttp.epix.frontend.controller.common.AbstractEpixWebBean;
import org.emau.icmvc.ttp.epix.frontend.controller.component.SearchForm;
import org.emau.icmvc.ttp.epix.frontend.model.Column;
import org.emau.icmvc.ttp.epix.frontend.model.EpixWebFile;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.model.DefaultStreamedContent;

@ViewScoped
@Named("exportController")
public class ExportController extends AbstractEpixWebBean implements Serializable
{
	@Serial private static final long serialVersionUID = 1381261265011992921L;

	private List<IdentifierDomainDTO> identifierDomains;

	private List<Column> columns = new ArrayList<>();
	private final List<Column> originalColumns = new ArrayList<>();

	private List<List<String>> exportData;
	private final BitSet exportUsedColumns = new BitSet();

	private DefaultStreamedContent downloadFile;

	private final Pattern splitStreetPattern = Pattern.compile("(.*?)\\s*((\\d+)(?!.*\\d)+\\s?\\w?)?$");

	protected int sum;
	protected int counter;

	private ExportController.Mode mode;
	private boolean searched = false;
	private boolean showSettings;

	// Export all
	private boolean onlyMainIdentityIdentifiers = true;

	// Export by idat
	private SearchForm searchForm;

	// Export by identifier domain
	private ExportController.IdentityType exportIdentityType;
	private IdentifierDomainDTO selectedIndentifierDomain;

	// Export by identifier list
	private EpixWebFile identifiersUpload;

	@PostConstruct
	public void construct()
	{
		init();
		mode = Mode.start;
		searchForm = new SearchForm(this);
		identifiersUpload = new EpixWebFile(languageBean, getDomainSelector().getSelectedDomainConfiguration(), epixHelper.getIdentifierDomainsFiltered());
	}

	public void init()
	{
		// Init data arrays
		exportData = new ArrayList<>();
		exportUsedColumns.clear();

		// Init columns
		columns.clear();
		originalColumns.clear();

		// Init progress bar
		sum = 0;
		counter = 0;

		// Load identifier domains
		identifierDomains = super.getIdentifierDomainsFiltered();

		loadColumnsForExport();
		originalColumns.clear();
		originalColumns.addAll(columns);
		searched = false;
		showSettings = true;
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
		identifiersUpload.onNewUpload();
		mode = Mode.exportByIdentifiers;
	}

	public void chooseExportByIDAT()
	{
		init();
		mode = Mode.exportByIDAT;
	}

	private void finishExport(boolean message)
	{
		if (!exportData.isEmpty())
		{
			showSettings = false;
		}
		if (message)
		{
			Object[] args = { exportData.size() };
			logMessage(new MessageFormat(getBundle().getString("page.export.message.info.found")).format(args), Severity.INFO, false);
		}
	}

	public void exportAllPersons()
	{
		init();
		searched = true;
		List<PersonDTO> persons = new ArrayList<>();
		try
		{
			persons = getService().getActivePersonsForDomain(getDomainSelector().getSelectedDomainName());
		}
		catch (InvalidParameterException | UnknownObjectException e)
		{
			logMessage(e.getLocalizedMessage(), Severity.ERROR, false);
		}

		for (PersonDTO person : persons)
		{
			exportPerson(person, onlyMainIdentityIdentifiers);
		}
		finishExport(true);
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
			identities = getService().getIdentitiesForDomain(getDomainSelector().getSelectedDomainName());

			for (IdentityOutDTO identity : identities)
			{
				for (IdentifierDTO identifier : identity.getIdentifiers())
				{
					if (identifier.getIdentifierDomain().equals(selectedIndentifierDomain))
					{
						if (exportIdentityType.equals(IdentityType.original))
						{
							String mpi = getService().getMPIForIdentifier(getDomainSelector().getSelectedDomainName(), identifier);
							exportPerson(identity, mpi, List.of(identifier));
						}
						else if (exportIdentityType.equals(IdentityType.reference))
						{
							PersonDTO person = getService().getActivePersonByLocalIdentifier(getDomainSelector().getSelectedDomainName(), identifier);
							exportPerson(person.getReferenceIdentity(), person.getMpiId().getValue(), List.of(identifier));
						}
					}
				}
			}
			finishExport(true);
		}
		catch (InvalidParameterException | UnknownObjectException e)
		{
			this.logMessage(e.getLocalizedMessage(), Severity.ERROR, false);
		}
	}

	public void exportByIDAT()
	{
		init();
		searched = true;
		searchForm.onSearchAllPersons(false);

		for (PersonDTO person : searchForm.getPersonDTOs())
		{
			exportPerson(person, onlyMainIdentityIdentifiers);
		}
		if (!exportData.isEmpty())
		{
			showSettings = false;
		}
		finishExport(false);
	}

	/**
	 * Find the persons for the uploaded local ids
	 */
	public void exportByIdentitifers()
	{
		init();
		searched = true;
		counter = 0;

		// Iterate over all imported local ids
		for (List<String> record : identifiersUpload.getElements())
		{
			IdentifierDTO identifier = new IdentifierDTO();
			identifier.setIdentifierDomain(selectedIndentifierDomain);
			identifier.setValue(record.get(identifiersUpload.getSelectedColumn()));

			try
			{
				PersonDTO person = getService().getActivePersonByLocalIdentifier(getDomainSelector().getSelectedDomainName(), identifier);
				List<IdentityOutDTO> identities = new ArrayList<>();
				identities.add(person.getReferenceIdentity());
				identities.addAll(person.getOtherIdentities());

				IdentityOutDTO identity = null;

				outerspace:
				for (IdentityOutDTO id : identities)
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
					String mpi = getService().getMPIForIdentifier(getDomainSelector().getSelectedDomainName(), identifier);
					keepSpecificIdentifierValue(identity, identifier);
					exportPerson(identity, mpi, identity.getIdentifiers());
				}
				else if (exportIdentityType.equals(IdentityType.reference))
				{
					keepSpecificIdentifierValue(identity, identifier);
					exportPerson(person.getReferenceIdentity(), person.getMpiId().getValue(), identity.getIdentifiers());
				}
			}
			catch (InvalidParameterException | UnknownObjectException e)
			{
				logMessage(e.getLocalizedMessage(), Severity.ERROR, false);
			}
			counter++;
		}

		finishExport(true);
	}

	/**
	 * If the identity has multiple identifiers for the same domain, keep only the value of the given identifier for this domain
	 */
	private void keepSpecificIdentifierValue(IdentityOutDTO identity, IdentifierDTO identifier)
	{
		// If the identity has multiple identifiers with the same domain, return the identifier with the uploaded value
		identity.getIdentifiers().removeIf(idf -> idf.getIdentifierDomain().equals(identifier.getIdentifierDomain()) && !idf.getValue().equals(identifier.getValue()));
	}

	public void handleDownloadResult()
	{
		reorderColumns();
		createDownloadFile();
	}

	public void toggle(Column toggleColumn)
	{
		for (Column column : columns)
		{
			if (column.getName().equals(toggleColumn.getName()))
			{
				column.setActive(!column.getActive());
			}
		}
	}

	public String getColumnText(String columnName)
	{
		if (getBundle().containsKey("common.person." + columnName))
		{
			return getBundle().getString("common.person." + columnName);
		}
		else
		{
			return columnName;
		}
	}

	public void onNewUpload()
	{
		identifiersUpload.onNewUpload();
		init();
	}

	protected void exportPerson(PersonDTO person, boolean onlyMainIdentityIdentifiers)
	{
		List<IdentifierDTO> identifiers = new ArrayList<>(person.getReferenceIdentity().getIdentifiers());
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

		exportPerson(person.getReferenceIdentity(), person.getMpiId().getValue(), identifiers);
	}

	/**
	 * Store the person objekt as a record in a multidimensional dataarray The
	 * first entry is MPI The rest must fit the inital column ordering (see:
	 * org.emau.icmvc.ttp.ttp.model.Column.Type)
	 * <br>
	 * If an identifier value is given, than that one will be used in the export
	 * and the identifier of the reference identity.
	 */
	protected void exportPerson(IdentityOutDTO identity, String mpi, List<IdentifierDTO> identifiers)
	{
		List<String> record = new ArrayList<>();

		if (mpi != null)
		{
			add(record, mpi);
		}
		else
		{
			add(record, "");
		}

		for (IdentifierDomainDTO identifierDomain : identifierDomains)
		{
			if (identifierDomain.equals(getDomainSelector().getSelectedDomain().getMpiDomain()))
			{
				continue;
			}

			boolean found = false;

			for (IdentifierDTO identifier : identifiers)
			{
				if (identifier.getIdentifierDomain().equals(identifierDomain))
				{
					add(record, identifier.getValue());
					found = true;
					break;
				}
			}
			if (!found)
			{
				add(record, null);
			}
		}

		add(record, identity.getDegree());
		add(record, identity.getLastName());
		add(record, identity.getMothersMaidenName());
		add(record, identity.getMiddleName());
		add(record, identity.getFirstName());
		add(record, parseDate(identity.getBirthDate()));
		add(record, identity.getBirthPlace());
		if (identity.getGender() != null)
		{
			add(record, identity.getGender().toString());
		}
		else
		{
			add(record, null);
		}
		add(record, identity.getNationality());
		add(record, identity.getMotherTongue());
		add(record, identity.getCivilStatus());
		add(record, identity.getRace());
		add(record, identity.getReligion());
		add(record, identity.getPrefix());
		add(record, identity.getSuffix());
		add(record, parseDate(identity.getExternalDate()));
		if (epixHelper.showValueField("value1"))
		{
			add(record, identity.getValue1());
		}
		if (epixHelper.showValueField("value2"))
		{
			add(record, identity.getValue2());
		}
		if (epixHelper.showValueField("value3"))
		{
			add(record, identity.getValue3());
		}
		if (epixHelper.showValueField("value4"))
		{
			add(record, identity.getValue4());
		}
		if (epixHelper.showValueField("value5"))
		{
			add(record, identity.getValue5());
		}
		if (epixHelper.showValueField("value6"))
		{
			add(record, identity.getValue6());
		}
		if (epixHelper.showValueField("value7"))
		{
			add(record, identity.getValue7());
		}
		if (epixHelper.showValueField("value8"))
		{
			add(record, identity.getValue8());
		}
		if (epixHelper.showValueField("value9"))
		{
			add(record, identity.getValue9());
		}
		if (epixHelper.showValueField("value10"))
		{
			add(record, identity.getValue10());
		}

		if (identity.getContacts() != null && !identity.getContacts().isEmpty())
		{
			ContactInDTO contact = identity.getContacts().get(0);
			add(record, contact.getStreet());
			add(record, contact.getZipCode());
			add(record, contact.getCity());
			add(record, contact.getState());
			add(record, contact.getCountry());
			add(record, contact.getCountryCode());
			add(record, contact.getDistrict());
			add(record, contact.getMunicipalityKey());
			add(record, contact.getPhone());
			add(record, contact.getEmail());

			if (contact.getStreet() != null)
			{
				// Split street and number
				String[] streetNumberArray = splitStreetAndNumber(contact.getStreet());
				if (streetNumberArray.length > 0)
				{
					add(record, streetNumberArray[0]);
					add(record, streetNumberArray[1]);
				}
				else
				{
					add(record, "");
					add(record, "");
				}
			}
			else
			{
				add(record, "");
				add(record, "");
			}
			add(record, parseDate(contact.getExternalDate()));
		}
		else
		{
			for (int i = 0; i < 13; i++)
			{
				add(record, null);
			}
		}

		if (identity.getVitalStatus() != null)
		{
			add(record, identity.getVitalStatus().name());
		}
		else
		{
			add(record, null);
		}
		add(record, parseDate(identity.getDateOfDeath()));
		add(record, "");

		exportData.add(record);
		for (int i = 0; i < record.size(); i++)
		{
			if (StringUtils.isNotEmpty(record.get(i)))
			{
				exportUsedColumns.set(i);
			}
		}
	}

	protected void add(List<String> record, String value)
	{
		record.add(value == null ? "" : value);
	}

	public List<Column> findEmptyColumns()
	{
		if (exportData == null || exportData.isEmpty())
		{
			return Collections.emptyList();
		}

		// now the states will be flipped to mark empty columns with the 'set' state
		BitSet emptyColumns = new BitSet();
		emptyColumns.or(exportUsedColumns); // copy
		emptyColumns.flip(0, originalColumns.size());

		// return a list with the empty columns
		return emptyColumns.stream().mapToObj(originalColumns::get).toList();
	}

	public boolean hasEmptyActiveColumns()
	{
		for (Column col : findEmptyColumns())
		{
			if (col.getActive())
			{
				return true;
			}
		}
		return false;
	}

	public boolean hasInactiveColumns()
	{
		for (Column column : columns)
		{
			if (!column.getActive())
			{
				return true;
			}
		}
		return false;
	}

	public void excludeEmptyColumns()
	{
		for (Column col : findEmptyColumns())
		{
			col.setActive(false);
		}
	}

	public void includeAllColumns()
	{
		for (Column column : columns)
		{
			column.setActive(true);
		}
	}

	private void reorderColumns()
	{
		ArrayList<Column> columnOrderNew = new ArrayList<>();

		DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("main:persons_table");

		for (UIColumn uiColumn : dataTable.getColumns())
		{
			String key = uiColumn.getColumnKey();
			int index = Integer.parseInt(key.substring(key.lastIndexOf(":") + 1));

			Column column = originalColumns.get(index);
			if (column.getActive())
			{
				columnOrderNew.add(column);
			}
		}
		columns = columnOrderNew;
	}

	protected String[] splitStreetAndNumber(String streetAndNumber)
	{
		Matcher matcher = splitStreetPattern.matcher(streetAndNumber);
		if (matcher.find() && matcher.groupCount() >= 2)
		{
			return new String[] { matcher.group(1), matcher.group(2) };
		}
		return new String[0];
	}

	private void createDownloadFile()
	{
		// Start an output string
		StringBuilder output = new StringBuilder();
		output.append(generateOutputHeader());

		// Iterate over all found persondata
		for (List<String> record : exportData)
		{
			output.append(generateOutput(record));
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String fileName = "epix_export_" + dateFormat.format(new Date()) + ".csv";
		InputStream stream = new ByteArrayInputStream(output.toString().getBytes(StandardCharsets.UTF_16LE));

		downloadFile = DefaultStreamedContent.builder()
				.stream(() -> stream)
				.contentType("text/csv")
				.name(fileName)
				.contentEncoding(StandardCharsets.UTF_16LE.name())
				.build();
	}

	private void loadColumnsForExport()
	{
		columns.clear();
		columns.add(new Column(getDomainSelector().getSelectedDomain().getMpiDomain().getLabel()));
		for (IdentifierDomainDTO identifierDomain : identifierDomains)
		{
			if (identifierDomain.equals(getDomainSelector().getSelectedDomain().getMpiDomain()))
			{
				continue;
			}
			columns.add(new Column(StringUtils.isEmpty(identifierDomain.getLabel()) ? identifierDomain.getName() : identifierDomain.getLabel()));
		}
		for (String type : Column.getColumnTypesForExport())
		{
			if (type.contains("value"))
			{
				if (epixHelper.showValueField(type))
				{
					columns.add(new Column(type));
				}
			}
			else
			{
				columns.add(new Column(type));
			}
		}
	}

	private String generateOutputHeader()
	{
		StringBuilder result = new StringBuilder();
		result.append("sep=;");
		result.append("\r\n");

		for (Column column : columns)
		{
			result.append(column.getName()).append(";");
		}
		// Remove last seperator in header
		result.delete(result.length() - 1, result.length());

		result.append("\r\n");

		return result.toString();
	}

	private String generateOutput(List<String> record)
	{
		StringBuilder result = new StringBuilder();

		for (Column column : columns)
		{
			int index = originalColumns.indexOf(column);
			if (index != -1)
			{
				try
				{
					result.append(record.get(index));
				}
				catch (IndexOutOfBoundsException ignored)
				{
				}
			}
			else
			{
				result.append("null");
			}
			result.append(";");
		}
		// Remove last separator in line
		result.delete(result.length() - 1, result.length());

		result.append("\r\n");
		return result.toString();
	}

	private String parseDate(Date date)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		if (date != null)
		{
			return sdf.format(date);
		}
		else
		{
			return null;
		}
	}

	/**
	 * @return Progress of Batch Processing in percentage
	 */
	public Integer getProgress()
	{
		if (sum == 0)
		{
			return 0;
		}
		else
		{
			int progress = counter * 100 / sum;
			return progress == 0 ? 1 : progress;
		}
	}

	public boolean isShowSettings()
	{
		return showSettings;
	}

	public void setShowSettings(boolean showSettings)
	{
		this.showSettings = showSettings;
	}

	public List<List<String>> getExportData()
	{
		return exportData;
	}

	public enum Mode
	{
		start, exportAllPersons, exportByIdentifierDomain, exportByIdentifiers, exportByIDAT
	}

	public String getMode()
	{
		return mode.toString();
	}

	public enum IdentityType
	{
		original, reference
	}

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

	public SearchForm getSearchForm()
	{
		return searchForm;
	}

	public EpixWebFile getIdentifiersUpload()
	{
		return identifiersUpload;
	}

	public void setIdentifiersUpload(EpixWebFile identifiersUpload)
	{
		this.identifiersUpload = identifiersUpload;
	}

	public List<Column> getColumns()
	{
		return columns;
	}

	public void setColumns(List<Column> columns)
	{
		this.columns = columns;
	}

	public DefaultStreamedContent getDownloadFile()
	{
		return downloadFile;
	}

	public int getSum()
	{
		return sum;
	}

	public IdentifierDomainDTO getSelectedIndentifierDomain()
	{
		return selectedIndentifierDomain;
	}

	public void setSelectedIndentifierDomain(IdentifierDomainDTO selectedIndentifierDomain)
	{
		this.selectedIndentifierDomain = selectedIndentifierDomain;
	}
}
