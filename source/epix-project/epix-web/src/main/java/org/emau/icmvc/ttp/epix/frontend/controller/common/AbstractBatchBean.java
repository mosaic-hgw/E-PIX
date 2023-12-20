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


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.emau.icmvc.ttp.epix.common.model.ContactInDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentifierDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentifierDomainDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityOutDTO;
import org.emau.icmvc.ttp.epix.common.model.PersonDTO;
import org.emau.icmvc.ttp.epix.frontend.model.Column;
import org.emau.icmvc.ttp.epix.frontend.model.Column2;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.file.UploadedFile;

/**
 * Superclass for batch processing, e.g. import and export
 *
 * @author blumentritta
 */
public class AbstractBatchBean extends AbstractEpixWebBean
{
	protected List<IdentifierDomainDTO> identifierDomains;
	protected IdentifierDomainDTO selectedIndentifierDomain;

	protected List<Column> columns = new ArrayList<>();
	protected List<Column> originalColumns = new ArrayList<>();

	// New
	protected List<Column2> uploadColumns;
	protected List<Column2> selectedUploadColumns;

	protected List<List<String>> data;
	protected List<List<String>> exportData;
	protected final BitSet exportUsedColumns = new BitSet();
	protected Map<String, Integer> columnIndexMap;

	protected DefaultStreamedContent downloadFile;
	protected UploadedFile uploadFile;
	protected boolean containsHeader = true;
	protected boolean downloadEnabled;

	private final Pattern splitStreetPattern = Pattern.compile("(.*?)\\s*((\\d+)(?!.*\\d)+\\s?\\w?)?$");

	// Encoding detection
	protected Charset uploadEncoding;
	protected List<Charset> charsets;

	protected int sum;
	protected int counter;

	protected void init()
	{
		columnIndexMap = new HashMap<>();

		// Init data arrays
		data = new ArrayList<>();
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
		columnIndexMap = new HashMap<>();
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
		uploadFile = null;
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

		exportPerson(person.getReferenceIdentity(), person.getMpiId().getValue(), identifiers, "", false);
	}

	/**
	 * Store the person objekt as a record in a multidimensional dataarray The
	 * first entry is MPI The rest must fit the inital column ordering (see:
	 * org.emau.icmvc.ttp.ttp.model.Column.Type)
	 * <br>
	 * If an identifier value is given, than that one will be used in the export
	 * and the identifier of the reference identity.
	 */
	protected void exportPerson(IdentityOutDTO identity, String mpi, List<IdentifierDTO> identifiers, String errorCode, boolean skipUnusedIdentifierDomains)
	{
		List<String> record = new ArrayList<>();

		// if (person.getFirstMPIID() != null)
		// {
		// record.add(person.getFirstMPIID());
		// }
		// else
		// {
		// record.add(null);
		// }

		if (mpi != null)
		{
			record.add(mpi);
		}
		else
		{
			record.add("");
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
					record.add(identifier.getValue());
					found = true;
					break;
				}
			}
			if (!found && !skipUnusedIdentifierDomains)
			{
				record.add(null);
			}
		}

		record.add(identity.getDegree());
		record.add(identity.getLastName());
		record.add(identity.getMothersMaidenName());
		record.add(identity.getMiddleName());
		record.add(identity.getFirstName());
		record.add(parseDate(identity.getBirthDate()));
		record.add(identity.getBirthPlace());
		if (identity.getGender() != null)
		{
			record.add(identity.getGender().toString());
		}
		else
		{
			record.add(null);
		}
		record.add(identity.getNationality());
		record.add(identity.getMotherTongue());
		record.add(identity.getCivilStatus());
		record.add(identity.getRace());
		record.add(identity.getReligion());
		record.add(identity.getPrefix());
		record.add(identity.getSuffix());
		record.add(parseDate(identity.getExternalDate()));
		if (showValueField("value1"))
		{
			record.add(identity.getValue1());
		}
		if (showValueField("value2"))
		{
			record.add(identity.getValue2());
		}
		if (showValueField("value3"))
		{
			record.add(identity.getValue3());
		}
		if (showValueField("value4"))
		{
			record.add(identity.getValue4());
		}
		if (showValueField("value5"))
		{
			record.add(identity.getValue5());
		}
		if (showValueField("value6"))
		{
			record.add(identity.getValue6());
		}
		if (showValueField("value7"))
		{
			record.add(identity.getValue7());
		}
		if (showValueField("value8"))
		{
			record.add(identity.getValue8());
		}
		if (showValueField("value9"))
		{
			record.add(identity.getValue9());
		}
		if (showValueField("value10"))
		{
			record.add(identity.getValue10());
		}

		if (identity.getContacts() != null && !identity.getContacts().isEmpty())
		{
			ContactInDTO contact = identity.getContacts().get(0);
			record.add(contact.getStreet());
			record.add(contact.getZipCode());
			record.add(contact.getCity());
			record.add(contact.getState());
			record.add(contact.getCountry());
			record.add(contact.getCountryCode());
			record.add(contact.getDistrict());
			record.add(contact.getMunicipalityKey());
			record.add(contact.getPhone());
			record.add(contact.getEmail());

			if (contact.getStreet() != null)
			{
				// Split street and number
				String[] streetNumberArray = splitStreetAndNumber(contact.getStreet());
				if (streetNumberArray.length > 0)
				{
					record.add(streetNumberArray[0]);
					record.add(streetNumberArray[1]);
				}
				else
				{
					record.add("");
					record.add("");
				}
			}
			else
			{
				record.add("");
				record.add("");
			}
			record.add(parseDate(contact.getExternalDate()));
		}
		else
		{
			for (int i = 0; i < 13; i++)
			{
				record.add(null);
			}
		}

		record.add(String.valueOf(errorCode));

		exportData.add(record);
		for (int i = 0; i < record.size(); i++)
		{
			if (StringUtils.isNotEmpty(record.get(i)))
			{
				exportUsedColumns.set(i);
			}
		}
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
		return emptyColumns.stream().mapToObj(i -> originalColumns.get(i)).toList();
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

		columnIndexMap = new HashMap<>();
	}

	public void includeAllColumns()
	{
		for (Column column : columns)
		{
			column.setActive(true);
		}
		columnIndexMap = new HashMap<>();
	}

	String[] splitStreetAndNumber(String streetAndNumber)
	{
		Matcher matcher = splitStreetPattern.matcher(streetAndNumber);
		if (matcher.find() && matcher.groupCount() >= 2)
		{
			return new String[] { matcher.group(1), matcher.group(2) };
		}
		return new String[0];
	}

	protected void createDownloadFile(String filename)
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
		String fileName = "epix_" + filename + "_" + dateFormat.format(new Date()) + ".csv";
		InputStream stream = new ByteArrayInputStream(output.toString().getBytes(StandardCharsets.UTF_16LE));

		downloadFile = DefaultStreamedContent.builder()
				.stream(() -> stream)
				.contentType("text/csv")
				.name(fileName)
				.contentEncoding(StandardCharsets.UTF_16LE.name())
				.build();
	}

	protected void uploadFile(BufferedReader rd) throws IOException
	{
		String sep;
		String line;
		boolean ready = false;

		int columnSize = 0;

		// read the file and convert data into a list
		Pattern pattern = null;
		Matcher matcher;
		while ((line = rd.readLine()) != null)
		{
			if (!ready)
			{
				// Get seperator
				if (line.contains("sep"))
				{
					sep = line.substring(4);
					line = rd.readLine();
				}
				else
				{
					sep = ";";
				}

				// Compile pattern
				pattern = Pattern.compile("((?:\"[^\"]*?\")*|[^\"][^" + sep + "]*?)([" + sep + "]|$)");

				// Ignore table header
				if (containsHeader)
				{
					uploadColumns = new ArrayList<>();

					matcher = pattern.matcher(line);
					while (matcher.find())
					{
						String columnName = matcher.group(1);
						if (columnName != null && !columnName.isEmpty())
						{
							Column2.Type type = Column2.getTypeByString(cleanupColumn(columnName));
							uploadColumns.add(new Column2(columnSize, type));
							columnSize++;
						}
					}

					line = rd.readLine();
				}

				// Ready for parsing CSV data
				ready = true;
			}

			if (line != null && !line.isEmpty())
			{
				List<String> row = new ArrayList<>();
				matcher = pattern.matcher(line);

				int i = 1;
				while (matcher.find())
				{
					String element = matcher.group(1);
					// if (element != null && !element.isEmpty())
					// {
					row.add(element);
					if (columnSize < i && element != null && !element.isEmpty())
					{
						columnSize++;
						uploadColumns.add(new Column2(columnSize));
					}
					i++;
					// }
				}

				// add data record
				data.add(row);
			}

		}
	}

	protected boolean compareIdentitiesEqual(IdentityOutDTO id1, IdentityOutDTO id2)
	{
		// TODO Kontakte vergleichen

		if (Objects.equals(id1.getDegree(), id2.getDegree())
				&& Objects.equals(id1.getLastName(), id2.getLastName())
				&& Objects.equals(id1.getMothersMaidenName(), id2.getMothersMaidenName())
				&& Objects.equals(id1.getMiddleName(), id2.getMiddleName())
				&& Objects.equals(id1.getFirstName(), id2.getFirstName())
				&& Objects.equals(id1.getBirthDate(), id2.getBirthDate())
				&& Objects.equals(id1.getBirthPlace(), id2.getBirthPlace())
				&& Objects.equals(id1.getGender(), id2.getGender())
				&& Objects.equals(id1.getNationality(), id2.getNationality())
				&& Objects.equals(id1.getMotherTongue(), id2.getMotherTongue())
				&& Objects.equals(id1.getCivilStatus(), id2.getCivilStatus())
				&& Objects.equals(id1.getRace(), id2.getRace())
				&& Objects.equals(id1.getReligion(), id2.getReligion())
				&& Objects.equals(id1.getPrefix(), id2.getPrefix())
				&& Objects.equals(id1.getSuffix(), id2.getSuffix())
				&& Objects.equals(id1.getExternalDate(), id2.getExternalDate())
				&& Objects.equals(id1.getValue1(), id2.getValue1())
				&& Objects.equals(id1.getValue2(), id2.getValue2())
				&& Objects.equals(id1.getValue3(), id2.getValue3())
				&& Objects.equals(id1.getValue4(), id2.getValue4())
				&& Objects.equals(id1.getValue5(), id2.getValue5())
				&& Objects.equals(id1.getValue6(), id2.getValue6())
				&& Objects.equals(id1.getValue7(), id2.getValue7())
				&& Objects.equals(id1.getValue8(), id2.getValue8())
				&& Objects.equals(id1.getValue9(), id2.getValue9())
				&& Objects.equals(id1.getValue10(), id2.getValue10()))
		{
			return true;
		}
		return false;
	}

	protected void loadColumnsForExport()
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
				if (showValueField(type))
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

	protected void loadColumnsForImport()
	{
		columns.clear();
		for (String type : Column.getColumnTypesForImport())
		{
			// only allow import of value fields that are configured in this domain
			if (type.contains("value"))
			{
				if (showValueField(type))
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
				catch (IndexOutOfBoundsException e)
				{
				}
			}
			else
			{
				result.append("null");
			}
			result.append(";");
		}
		// Remove last seperator in line
		result.delete(result.length() - 1, result.length());

		result.append("\r\n");
		return result.toString();
	}

	private String cleanupColumn(String column)
	{
		// TODO übersetzer einbauen
		return column.toLowerCase().replaceAll("[-_. ]", "");
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

	// Getters and Setters
	// -------------------

	public List<Column> getColumns()
	{
		return columns;
	}

	public void setColumns(List<Column> columns)
	{
		this.columns = columns;
	}

	public List<List<String>> getData()
	{
		return data;
	}

	public DefaultStreamedContent getDownloadFile()
	{
		return downloadFile;
	}

	public int getSum()
	{
		return sum;
	}

	public boolean isFirstLineIsHeader()
	{
		return containsHeader;
	}

	public void setFirstLineIsHeader(boolean firstLineIsHeader)
	{
		containsHeader = firstLineIsHeader;
	}

	@Override
	public List<IdentifierDomainDTO> getIdentifierDomainsFiltered()
	{
		return identifierDomains;
	}

	public IdentifierDomainDTO getSelectedIndentifierDomain()
	{
		return selectedIndentifierDomain;
	}

	public void setSelectedIndentifierDomain(IdentifierDomainDTO selectedIndentifierDomain)
	{
		this.selectedIndentifierDomain = selectedIndentifierDomain;
	}

	public Charset getUploadEncoding()
	{
		return uploadEncoding;
	}

	public void setUploadEncoding(Charset uploadEncoding)
	{
		this.uploadEncoding = uploadEncoding;
	}

	public List<Charset> getCharsets()
	{
		return charsets;
	}
}
