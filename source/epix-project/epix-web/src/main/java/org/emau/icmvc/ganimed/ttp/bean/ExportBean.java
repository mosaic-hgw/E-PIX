package org.emau.icmvc.ganimed.ttp.bean;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.emau.icmvc.ganimed.epix.common.MPIException;
import org.emau.icmvc.ganimed.epix.common.model.Identifier;
import org.emau.icmvc.ganimed.epix.common.model.IdentifierDomain;
import org.emau.icmvc.ganimed.epix.common.model.MPIResponse;
import org.emau.icmvc.ganimed.epix.common.model.Person;
import org.emau.icmvc.ganimed.epix.common.model.QueryResponseEntry;
import org.emau.icmvc.ganimed.ttp.model.Column;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

@ViewScoped
@ManagedBean(name = "exportBean")
public class ExportBean extends BatchProcessing {

	private StreamedContent exampleFile;

	private List<IdentifierDomain> identifierDomains;
	private String selectedIndentifierDomain;

	private String personToExport;

	private int sum;
	private int counter;
	private ExportBean.Type type;

	/**
	 * Counts the number of persons processed already for Progress
	 * feedback(progress bar)
	 */

	// ------------methods-------------------------------------
	@PostConstruct
	public void init() {
		this.type = Type.start;
		this.personToExport = "reference";

		try {
			this.identifierDomains = proxy.getAllIdentifierDomains();
		} catch (MPIException e) {
			e.printStackTrace();
		}
	}

	public void chooseExportAll() {
		this.type = Type.exportAll;

		super.init();
		this.columns.add(0, new Column("MPI"));
	}

	public void chooseExportDomain() {
		this.type = Type.exportDomain;

		super.init();
		this.columns.add(0, new Column("MPI"));
	}

	public void chooseExportIds() {
		this.type = Type.exportIds;

		super.init();
		this.columns.add(0, new Column("MPI"));
	}

	/**
	 * Export all persons in epix If onlyReferencePersons is true, than only the
	 * main identity per person will be exported If onlyReferencePersons is
	 * false, than all identities per person will be exported with their local
	 * identifiers but with IDAT from the main identity
	 * 
	 * @throws MPIException
	 */
	public void exportAll() throws MPIException {
		this.exportData = new ArrayList<List<String>>();
		List<Person> persons;

		persons = proxy.getAllReferencePersons();

		for (Person person : persons) {
			this.exportPerson(person, null, 0);
		}
	}

	public void exportDomain() throws MPIException {
		this.exportData = new ArrayList<List<String>>();
		List<Person> persons = proxy.getAllPersons();

		IdentifierDomain identifierDomain = getIdentifierDomain(this.selectedIndentifierDomain);

		for (Person person : persons) {
			for (Identifier identifier : person.getIdentifiers()) {
				if (identifier.getIdentifierDomain().equals(identifierDomain)) {
					if (this.personToExport.equals("original")) {
						this.exportPerson(person, identifier.getValue(), 0);
					} else if (this.personToExport.equals("reference")) {
						this.exportPerson(proxy.queryPersonByLocalIdentfier(identifier.getValue(), identifier.getIdentifierDomain())
								.getResponseEntries().get(0).getPerson(), identifier.getValue(), 0);
					}
				}
			}
		}
	}

	/**
	 * Upload a list of local ids to be exported
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void handleUpload(FileUploadEvent event) throws IOException {
		super.init();
		FacesContext context = FacesContext.getCurrentInstance();

		this.columns.add(0, new Column("MPI"));

		this.uploadFile = event.getFile();
		BufferedReader rd = new BufferedReader(new InputStreamReader(uploadFile.getInputstream()));

		try {

			this.uploadFile(rd);

			if (this.data.isEmpty()) {
				context.addMessage("feedback",
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Operation canceled", messages.getString("batch.error.emptyFile")));
			}

			this.sum = this.data.size();
			this.counter = 0;

		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
			if (logger.isErrorEnabled()) {
				logger.error("", e);
			}
		} finally {
			if (rd != null) {
				try {
					rd.close();
				} catch (IOException e) {
					if (logger.isErrorEnabled()) {
						logger.error("", e);
					}
				}
			}
		}
	}

	/**
	 * Find the persons for the uploaded local ids
	 * 
	 * @throws MPIException
	 */
	public void exportIds() throws MPIException {
		this.exportData = new ArrayList<List<String>>();

		// Get the identifier Domain from selectbox
		IdentifierDomain identifierDomain = getIdentifierDomain(this.selectedIndentifierDomain);

		// Iterate over all imported local ids
		for (List<String> record : this.data) {
			String local_id = record.get(0);
			this.counter++;

			// Find the person with the given local id, this should be unique
			QueryResponseEntry responseEntry = this.getResponseEntryByLocalIdentifier(local_id, identifierDomain);

			// Check if a person was found
			if (responseEntry != null) {

				if (this.personToExport.equals("original")) {
					// Iterate over the identities of this person
					for (Person identity : responseEntry.getPersonIdentities()) {

						// Find the identity whose local id matches the given
						// local id
						for (Identifier identifier : identity.getIdentifiers()) {
							if (identifier.getIdentifierDomain().equals(identifierDomain) && identifier.getValue().equals(local_id)) {

								// Output this identity
								this.exportPerson(identity, identifier.getValue(), responseEntry.getErrorCode().getCode());
								break;
							}
						}
					}
				} else if (this.personToExport.equals("reference")) {
					this.exportPerson(responseEntry.getPerson(), local_id, responseEntry.getErrorCode().getCode());
				}
			}
		}

		if (this.exportData.isEmpty()) {
			FacesContext.getCurrentInstance().addMessage("feedback",
					new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning", messages.getString("export.warn.noPersons")));
		}
	}

	/**
	 * @throws MPIException
	 */
	public void handleDownloadResult() throws MPIException {
		this.reorderColumns();
		this.createDownloadFile("export");
	}

	private QueryResponseEntry getResponseEntryByLocalIdentifier(String identifier, IdentifierDomain domain) {

		try {
			logger.info("Searching Person by PDQ");
			MPIResponse response = proxy.queryPersonByLocalIdentfier(identifier, domain);

			if (!response.getResponseEntries().isEmpty()) {
				return (QueryResponseEntry) response.getResponseEntries().get(0);
			}
		} catch (MPIException e) {
			logger.error(e.getMessage(), e);
		}

		return null;
	}

	private void reorderColumns() {
		this.columnOrderNew = new ArrayList<Column>();

		DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("main:persons_table");

		for (UIColumn uiColumn : dataTable.getColumns()) {
			String key = uiColumn.getColumnKey();
			Integer index = Integer.parseInt(key.substring(key.lastIndexOf(":") + 1));

			Column column = this.columns.get(index);
			if (column.getActive())
				this.columnOrderNew.add(column);
		}
	}

	/**
	 * 
	 * @return Progress of Batch Processing in percentage
	 */
	public Integer getProgress() {
		if (this.sum == 0) {
			return 0;
		} else {
			return this.counter * 100 / this.sum;
		}

	}

	private IdentifierDomain getIdentifierDomain(String name) {
		if (name != null) {
			for (IdentifierDomain idd : this.identifierDomains) {
				if (idd.getName().equals(name)) {
					return idd;
				}
			}
		}
		return null;
	}

	// ----------getters/setters---------------------------------------

	public List<List<String>> getExportData() {
		return exportData;
	}

	public void setExportData(List<List<String>> exportData) {
		this.exportData = exportData;
	}

	public UploadedFile getUploadFile() {
		return uploadFile;
	}

	public void setUploadFile(UploadedFile uploadFile) {
		this.uploadFile = uploadFile;
	}

	public List<IdentifierDomain> getIdentifierDomains() {
		return identifierDomains;
	}

	public void setIdentifierDomains(List<IdentifierDomain> identifierDomains) {
		this.identifierDomains = identifierDomains;
	}

	public String getSelectedIndentifierDomain() {
		return selectedIndentifierDomain;
	}

	public void setSelectedIndentifierDomain(String selectedIndentifierDomain) {
		this.selectedIndentifierDomain = selectedIndentifierDomain;
	}

	public boolean isIgnoreHeader() {
		return ignoreHeader;
	}

	public boolean isDownloadEnabled() {
		return downloadEnabled;
	}

	public void setIgnoreHeader(boolean ignoreHeader) {
		this.ignoreHeader = ignoreHeader;
	}

	public void setDownloadEnabled(boolean downloadEnabled) {
		this.downloadEnabled = downloadEnabled;
	}

	public StreamedContent getExampleFile() {
		return exampleFile;
	}

	public void setExampleFile(StreamedContent exampleFile) {
		this.exampleFile = exampleFile;
	}

	public int getSum() {
		return sum;
	}

	public enum Type {
		start, exportAll, exportDomain, exportIds
	};

	public String getType() {
		return this.type.toString();
	}

	public String getPersonToExport() {
		return personToExport;
	}

	public void setPersonToExport(String personToExport) {
		this.personToExport = personToExport;
	}
}
