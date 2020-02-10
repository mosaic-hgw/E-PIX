package org.emau.icmvc.ganimed.ttp.bean;

import java.io.BufferedReader;

/*
 * ###license-information-start###
 * E-PIX - Enterprise Patient Identifier Cross-referencing
 * __
 * Copyright (C) 2009 - 2017 The MOSAIC Project - Institut fuer Community Medicine der
 * 							Universitaetsmedizin Greifswald - mosaic-projekt@uni-greifswald.de
 * 							concept and implementation
 * 							c. schack, d.langner, l. geidel
 * 							web client
 * 							a. blumentritt
 * 							g. weiher
 * 							please cite our publications
 * 							http://dx.doi.org/10.3414/ME14-01-0133
 * 							http://dx.doi.org/10.1186/s12967-015-0545-6
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.emau.icmvc.ganimed.epix.common.model.Contact;
import org.emau.icmvc.ganimed.epix.common.model.Person;
import org.emau.icmvc.ganimed.ttp.model.Column;
import org.emau.icmvc.ganimed.ttp.model.Column.Type;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.UploadedFile;

/**
 * Superclass for batch processing, e.g. import and export
 * 
 * @author blumentritta
 */
public class BatchProcessing extends AbstractManagedBean {
	
	protected List<Column> columns;
	protected List<Column> columnOrderNew;
	protected int maxColumns;

	protected List<List<String>> data;
	protected List<List<String>> exportData;
	private Map<String, Integer> indexList;
	
	protected DefaultStreamedContent downloadFile;
	protected UploadedFile uploadFile;
	protected boolean ignoreHeader;
	protected boolean downloadEnabled;

	protected void init() {
		
		// Load inital columns
		this.columns = new ArrayList<Column>();
		for (String type : Column.getColumnTypes()) {
			this.columns.add(new Column(type));
		}
		
		this.data = new ArrayList<List<String>>();
		this.indexList = new HashMap<String, Integer>();
	}
	
	/**
	 * Get a list of all available columns
	 * @return list of columns
	 */
	public List<String> getColumnsAvailable() {
		return Column.getColumnTypes();
	}
	
	/**
	 * Workaround to display a datatable with one row
	 * @return list with one integer
	 */
	public List<Integer> getSingleRow() {
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(new Integer(0));
		return list;
	}
	
	/**
	 * Check if the user defined active columns contain every active column at most one time
	 * @return true if columns are unique
	 */
	public boolean checkColumnsUnique() {
		List<String> columnsUnique = new ArrayList<String>();
		for (Column column : this.columns) {
			if (column.getActive())
			{
				if (!columnsUnique.contains(column.getName()))
				{
					columnsUnique.add(column.getName());
				}
				else
				{
					return false;
				}
			}
		}
		return true;
	}
	
	public void toggle(Column toggleColumn) {
		for (Column column : this.columns) {
			if (column.getName().equals(toggleColumn.getName()))
				column.setActive(!column.getActive());
		}
	}
	
	/**
	 * Store the person objekt as a record in a multidimensional dataarray
	 * The first entry is MPI
	 * The rest must fit the inital column ordering (see:
	 * org.emau.icmvc.ganimed.ttp.model.Column.Type)
	 * 
	 * @param person
	 */
	protected void exportPerson(Person person, String identifier, int errorCode) {
		List<String> record = new ArrayList<String>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

		if (person.getMpiid() != null)
			record.add(person.getMpiid().getValue());
		else
			record.add(null);
		if (identifier == null && person.getIdentifiers() != null && !person.getIdentifiers().isEmpty())
		{
			record.add(person.getIdentifiers().get(person.getIdentifiers().size()-1).getValue());
		}
			
		else
			record.add(identifier);
		record.add(person.getDegree());
		record.add(person.getLastName());
		record.add(person.getMothersMaidenName());
		record.add(person.getMiddleName());
		record.add(person.getFirstName());
		if (person.getBirthDate() != null)
			record.add(sdf.format(person.getBirthDate()));
		else
			record.add(null);
		record.add(person.getBirthPlace());
		if (person.getGender() != null)
			record.add(person.getGender().toString());
		else
			record.add(null);
		record.add(person.getNationality());
		record.add(person.getValue1());
		record.add(person.getValue2());
		record.add(person.getValue3());
		record.add(person.getValue4());
		record.add(person.getValue5());
		record.add(person.getValue6());
		record.add(person.getValue7());
		record.add(person.getValue8());
		record.add(person.getValue9());
		record.add(person.getValue10());

		if (person.getContacts() != null && !person.getContacts().isEmpty()) {
			Contact contact = person.getContacts().get(0);
			record.add(contact.getStreet());
			record.add(contact.getZipCode());
			record.add(contact.getCity());
			record.add(contact.getState());
			record.add(contact.getCountry());
			record.add(contact.getPhone());
			record.add(contact.getEmail());
			
			if (contact.getStreet() != null) {
				// Split street and number
				Pattern pattern = Pattern.compile("(.+)\\s(\\d+(\\s*[^\\d\\s]+)*)$");
			    Matcher matcher = pattern.matcher(contact.getStreet());
				
				if (matcher.find() && matcher.groupCount() >= 3) {
					record.add(matcher.group(1));
					record.add(matcher.group(2));
				} else {
					record.add("");
					record.add("");
				}
			} else {
				record.add("");
				record.add("");
			}
			
			
		} else {
			for (int i = 0; i<9; i++) {
				record.add(null);
			}
		}
		
		record.add(String.valueOf(errorCode));

		this.exportData.add(record);
	}
	
	/**
	 * Get the value of a record at the given column
	 * @param record with data
	 * @param column to get the value from
	 * @return
	 */
	protected String getValueInColumn(List<String> record, Type type) {
		if (this.indexList.isEmpty())
			this.generateIndexList();
		
		if (this.indexList.containsKey(type.toString())) {
			int index = this.indexList.get(type.toString());
			
			if (record.size() > index)
			{
				String result = record.get(index);
				return result.equals("null") ? null : result;
			}
		}

		return null;
	}
	
	protected void createDownloadFile(String filename) {
		// Start an output string
		StringBuilder output = new StringBuilder();
		output.append(this.generateOutputHeader());

		// Iterate over all found persondata
		for (List<String> record : this.exportData) {
			output.append(this.generateOutput(record));
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String fileName = "epix_" + filename + "_" + dateFormat.format(new Date()) + ".csv";
		InputStream stream = new ByteArrayInputStream(output.toString().getBytes());
		downloadFile = new DefaultStreamedContent(stream, "text/csv", fileName);
	}
	
	protected void uploadFile(BufferedReader rd) throws IOException {
		
		String sep = null;
		String tmp;
		Boolean ready = false;
		maxColumns = 0;
		
		// save the file id for display
		if (logger.isDebugEnabled()) {
			logger.debug("uploading file: " + uploadFile.getFileName());
		}
		
		// read the file and convert data into a list
		Pattern pattern = null;
		Matcher matcher;
		while ((tmp = rd.readLine()) != null) {
			if (!ready) {
				// Get seperator
				if (tmp.contains("sep")) {
					sep = tmp.substring(4);
					tmp = rd.readLine();
				} else {
					sep = ",";
				}
				
				// Compile pattern
				pattern = Pattern.compile("((?:\"[^\"]*?\")*|[^\"][^" + sep + "]*?)([" + sep + "]|$)");
				
				// Ignore table header
				if (ignoreHeader) {
					tmp = rd.readLine();
				}
				
				// Ready for parsing CSV data
				ready = true;
			}
			
			if (!tmp.isEmpty()) {
				List<String> record = new ArrayList<String>();
				matcher = pattern.matcher(tmp);
				while (matcher.find()) {
					record.add(matcher.group(1).replace("\"", ""));
				}						
				// remove empty match at the end of the record
				record.remove(record.size() - 1);
				
				// remember max column size
				if (maxColumns < record.size())
					maxColumns = record.size();
				
				// add data record
				this.data.add(record);
			}

		}
	}
	
	private String generateOutputHeader() {
		StringBuilder result = new StringBuilder();
		result.append("sep=;");
		result.append("\r\n");
		
		for (Column column : this.columnOrderNew) {
			result.append(column.getName()).append(";");
		}

		result.append("\r\n");

		return result.toString();
	}
	
	private String generateOutput(List<String> record) {
		StringBuilder result = new StringBuilder();
		
		for (Column column : this.columnOrderNew) {
			int index = this.columns.indexOf(column);
			if (index != -1)
				result.append(record.get(index));
			else
				result.append("null");
			result.append(";");
		}

		result.append("\r\n");
		return result.toString();
	}
	
	/**
	 * generate a map with the index number for each column
	 */
	private void generateIndexList() {
		int i = 0;
		for (Column column : this.columns) {
			if (column.getActive())
				this.indexList.put(column.getName(), new Integer(i));
			i++;
		}
	}
	
	// Getters and Setters
	// -------------------
	
	public List<Column> getColumns() {
		return this.columns;
	}
	
	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public List<List<String>> getData() {
		return data;
	}
	
	public DefaultStreamedContent getDownloadFile() {
		return downloadFile;
	}
}
