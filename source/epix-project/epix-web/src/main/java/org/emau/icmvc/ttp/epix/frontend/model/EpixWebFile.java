package org.emau.icmvc.ttp.epix.frontend.model;

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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.emau.icmvc.ttp.epix.common.model.config.ConfigurationContainer;
import org.icmvc.ttp.web.controller.LanguageBean;
import org.icmvc.ttp.web.model.WebFile;
import org.primefaces.event.FileUploadEvent;

public class EpixWebFile extends WebFile
{
	private static final long serialVersionUID = 2350821573867160868L;

	private final ConfigurationContainer domainConfiguration;
	private static final String NORMALIZE_PATTERN = "\\W|_";

	// Stores the names of the columns the user has selected for import
	private List<String> selectedColumns = new ArrayList<>();

	// Stores the type (or local identfier domain) for each user uploaded column
	private Map<String, String> columnTypeMapping = new HashMap<>();

	// Stores the position of each column type (or local identifier domain) in the uploaded file (for performance improvements)
	private Map<String, Integer> columnTypeIndex = new HashMap<>();

	public EpixWebFile(LanguageBean languageBean, ConfigurationContainer domainConfiguration)
	{
		super("E-PIX");
		this.languageBean = languageBean;
		this.domainConfiguration = domainConfiguration;
	}

	@Override
	public void onUpload(FileUploadEvent event)
	{
		super.onUpload(event);
		generateColumnTypeMapping();

		selectedColumns.clear();
		// Preselect all successful detected columns
		for (String column : getColumns())
		{
			if (!columnTypeMapping.get(column).equals(WebPersonField.unkown.name()))
			{
				selectedColumns.add(column);
			}
		}
	}

	/**
	 * Check if the user defined active columns contain every active column at
	 * most one time
	 *
	 * @return true if columns are unique
	 */
	public boolean checkColumnTypesUnique()
	{
		List<String> uniqueTypes = new ArrayList<>();
		for (String column : selectedColumns)
		{
			if (!uniqueTypes.contains(columnTypeMapping.get(column)))
			{
				uniqueTypes.add(columnTypeMapping.get(column));
			}
			else
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Check if the user defined active columns contain every active column at
	 * most one time
	 *
	 * @return true if columns are unique
	 */
	public boolean checkRequiredTypesPresent(List<WebPersonField> requiredTypes)
	{
		requiredTypes: for (WebPersonField requiredType : requiredTypes)
		{
			for (String column : selectedColumns)
			{
				if (requiredType.name().equals(columnTypeMapping.get(column)))
				{
					continue requiredTypes;
				}
			}
			return false;
		}
		return true;
	}

	/**
	 * Check if no active column has the type unkown
	 *
	 * @return true if columns are unique
	 */
	public boolean checkActiveColumnsNotTypeUnkown()
	{
		for (String column : selectedColumns)
		{
			if (WebPersonField.unkown.name().equals(columnTypeMapping.get(column)))
			{
				return false;
			}
		}
		return true;
	}

	// Try to detect the type for each column in the uploaded file. Set to unkown if not recognized.
	private void generateColumnTypeMapping()
	{
		columnTypeMapping.clear();
		for (String column : getColumns())
		{
			// Try to detect by matching the enum name
			// Normalize the given column text
			WebPersonField type = Arrays.stream(WebPersonField.values())
					.filter(t -> t.name().equalsIgnoreCase(column.replaceAll(NORMALIZE_PATTERN, ""))).findAny().orElse(WebPersonField.unkown);

			// Try to detect by matching the custom field value
			// Normalize the given column text and the configured field label
			if (WebPersonField.unkown.equals(type))
			{
				Map.Entry<String, String> field = domainConfiguration.getValueFieldMapping().entrySet().stream()
						.filter(e -> e.getValue().replaceAll(NORMALIZE_PATTERN, "").equalsIgnoreCase(column.replaceAll(NORMALIZE_PATTERN, ""))).findAny().orElse(null);
				if (field != null)
				{
					type = WebPersonField.valueOf(field.getKey());
				}
			}

			// Try to detect by matching every translation of the enum name
			// Normalize the given column text and the translated column type
			for (String language : languageBean.getSupportedLanguages())
			{
				if (WebPersonField.unkown.equals(type))
				{
					type = Arrays.stream(WebPersonField.values())
							.filter(t -> getBundle(language).getString("common.person." + t.name()).replaceAll(NORMALIZE_PATTERN, "").equalsIgnoreCase(column.replaceAll(NORMALIZE_PATTERN, "")))
							.findAny()
							.orElse(WebPersonField.unkown);
				}
			}

			columnTypeMapping.put(column, type.name());
		}
	}

	public void generateColumnTypeIndex()
	{
		columnTypeIndex.clear();

		int i = 0;
		for (String column : getColumns())
		{
			if (selectedColumns.contains(column))
			{
				columnTypeIndex.put(columnTypeMapping.get(column), i);
			}
			i++;
		}
	}

	/**
	 * Get bundle for a specific language
	 *
	 * @param language
	 *            2 char lower-case code for language, e.g. "en"
	 * @return bundle in given language
	 */
	private ResourceBundle getBundle(String language)
	{
		return ResourceBundle.getBundle("messages", new Locale(language));
	}

	public List<String> getSelectedColumns()
	{
		return selectedColumns;
	}

	public void setSelectedColumns(List<String> selectedColumns)
	{
		this.selectedColumns = selectedColumns;
	}

	public Map<String, String> getColumnTypeMapping()
	{
		return columnTypeMapping;
	}

	public void setColumnTypeMapping(Map<String, String> columnTypeMapping)
	{
		this.columnTypeMapping = columnTypeMapping;
	}

	public Map<String, Integer> getColumnTypeIndex()
	{
		return columnTypeIndex;
	}

	// Dont return value fields that are not enabled in DomainConfiguration
	public List<String> getTypes()
	{
		return Arrays.stream(WebPersonField.values()).map(Enum::name).filter(t -> !t.contains("value") || domainConfiguration.getValueFieldMapping().containsKey(t)).collect(Collectors.toList());
	}

	public boolean hasMpi()
	{
		for (Map.Entry<String, String> entry : columnTypeMapping.entrySet())
		{
			if (WebPersonField.MPI.name().equals(entry.getValue()) && selectedColumns.contains(entry.getKey()))
			{
				return true;
			}
		}
		return false;
	}

	public boolean isLocalIdentifierColumn(String column)
	{
		return columnTypeMapping.get(column).startsWith("localId");
	}
}
