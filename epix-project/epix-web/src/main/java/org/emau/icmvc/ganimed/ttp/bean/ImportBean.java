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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.xml.datatype.DatatypeConfigurationException;

import org.emau.icmvc.ganimed.epix.common.MPIException;
import org.emau.icmvc.ganimed.epix.common.model.Contact;
import org.emau.icmvc.ganimed.epix.common.model.Gender;
import org.emau.icmvc.ganimed.epix.common.model.Identifier;
import org.emau.icmvc.ganimed.epix.common.model.IdentifierDomain;
import org.emau.icmvc.ganimed.epix.common.model.MPIResponse;
import org.emau.icmvc.ganimed.epix.common.model.ResponseEntry;
import org.emau.icmvc.ganimed.ttp.exception.PatientParseException;
import org.emau.icmvc.ganimed.ttp.model.Column.Type;
import org.emau.icmvc.ganimed.ttp.model.Column;
import org.emau.icmvc.ganimed.ttp.model.TableContact;
import org.emau.icmvc.ganimed.ttp.modelV2.PatientModel;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

@ViewScoped
@ManagedBean(name = "importBean")
public class ImportBean extends BatchProcessing {

	private StreamedContent exampleFile;

	private List<IdentifierDomain> identifierDomains;
	private String selectedIndentifierDomain;

	private List<PatientModel> persons;
	private List<MPIResponse> responses;

	// ------------methods-------------------------------------
	@PostConstruct
	public void init() {
		super.init();
		// ColumnChooser choose;
		try {
			identifierDomains = proxy.getAllIdentifierDomains();
		} catch (MPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * Upload a list of persons
	 * 
	 * @param event
	 * @throws IOException 
	 */
	public void handleUpload(FileUploadEvent event) throws IOException {
		super.init();
		FacesContext context = FacesContext.getCurrentInstance();
		
		this.persons = new ArrayList<PatientModel>();
		this.responses = new ArrayList<MPIResponse>();		
		
		this.uploadFile = event.getFile();
		BufferedReader rd = new BufferedReader(new InputStreamReader(uploadFile.getInputstream()));

		try {

			this.uploadFile(rd);
			
			// cut number of columns to max column size of data records
			this.columns = this.columns.subList(0, maxColumns);
			
			if (this.data.isEmpty()) {
				// throw new InvalidUploadException("No entries in the file");
				context.addMessage("Error",
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", messages.getString("batch.error.emptyFile")));
			} else {
				context.addMessage("Info", new FacesMessage(FacesMessage.SEVERITY_INFO, "Info:", "Uploaded " + this.data.size() + " records"));
			}
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", e.getMessage()));
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
	 * Download only the imported persons with error in response code
	 */
	public void handleDownloadResultError() {
		this.prepareDownloadFile(true);
	}

	/**
	 * Download all imported persons including those with error in response code
	 */
	public void handleDownloadResultAll() {
		this.prepareDownloadFile(false);
	}

	/**
	 * Listener for "Start Batch Processing" button. Requests MPI for all persons and saves the responses
	 * @throws PatientParseException 
	 */
	public void handleImport() throws PatientParseException {
		FacesContext context = FacesContext.getCurrentInstance();
		
		if (!this.checkColumnsUnique()) {
			context.addMessage("Info",
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Import canceled:", messages.getString("batch.error.columnOrder")));
			return;
		}
	
		for (List<String> record : this.data) {
			try {
				this.persons.add(this.parseRecord(record));
			} catch (PatientParseException e) {
				logger.error(e.getMessage());
				context.addMessage(
						"Error",
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Operation canceled:", e.getMessage()
								+ messages.getString("batch.error.parsePatient")));
				break;
			}
		}
		
		responses = new ArrayList<MPIResponse>();
		IdentifierDomain domain = null;
		for (IdentifierDomain idd : identifierDomains) {
			if (idd.getName().equals(selectedIndentifierDomain)) {
				domain = idd;
				break;
			}
		}
		try {
			boolean foundErrorCode = false;
			for (PatientModel patient : persons) {
				MPIResponse response = proxy.requestMPI(patient.toRequest(domain));
				
				// Get identifier from raw import data in case of none identifier in epix found (cause of failed import)
				if (response.getResponseEntries().get(0).getPerson().getIdentifiers().isEmpty()) {
					Identifier identifier = new Identifier();
					identifier.setValue(patient.getLocalIdentifier());
					response.getResponseEntries().get(0).getPerson().getIdentifiers().add(identifier);
				}
				
				responses.add(response);
				if (!foundErrorCode && response.getResponseEntries().get(0).getErrorCode().getCode() != 0) {
					foundErrorCode = true;
				}
			}
			
			context.addMessage("Info", new FacesMessage(FacesMessage.SEVERITY_INFO, "Info:", "Imported " + this.responses.size() + " persons"));
			if (foundErrorCode)
				context.addMessage("Warning", new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning:", "Got an error for some persons. Please check response messages."));

			persons.clear();
		} catch (MPIException e) {
			context.addMessage("Info", new FacesMessage(FacesMessage.SEVERITY_INFO, "Operation canceled", e.getMessage()));
		} catch (DatatypeConfigurationException e) {
			context.addMessage("Info", new FacesMessage(FacesMessage.SEVERITY_INFO, "Operation canceled", e.getMessage()));
		}
	}
	
	/**
	 * Parses a record (List of String entries) to PatientModel. The record should be im the Form:
	 * {LocalID,Degree,LastName,MotherMaidensname,MiddleName,FirstName,BirthDate,BirthPlace,Gender,Nationality, value1,value2,value3,value4,valu5,value6,value7,value8,value9,value10,
	 * (Street,PostCode,City,State,Country,Phone,E-Mail)*}
	 * 
	 * @param record
	 *            List of String entries of a patient record
	 * @return PatientModel with Data from record
	 * @throws PatientParseException
	 *             should be thrown, when the record isn't formatted correctly
	 */
	private PatientModel parseRecord(List<String> record) throws PatientParseException {
		PatientModel person = new PatientModel();
		person.setTableContacts(new ArrayList<TableContact>());
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

		person.setLocalIdentifier(this.getValueInColumn(record, Type.Local_ID));				
		person.setDegree(this.getValueInColumn(record, Type.Degree));
		person.setLastName(this.getValueInColumn(record, Type.Last_Name));
		person.setMothersmaidenname(this.getValueInColumn(record, Type.Maiden_Name));
		person.setSecondName(this.getValueInColumn(record, Type.Second_Name));
		person.setFirstName(this.getValueInColumn(record, Type.First_Name));
		person.setBirthplace(this.getValueInColumn(record, Type.Birthplace));
		person.setNationality(this.getValueInColumn(record, Type.Nationality));
		
		String birthdate = (this.getValueInColumn(record, Type.Birthdate));
		if (birthdate == null || birthdate.isEmpty())
		{
				person.setBirthDate(null);
		}
		else
		{
			try {
				person.setBirthDate(sdf.parse(birthdate));
			} catch (ParseException e) {
				person.setBirthDate(null);
				FacesContext.getCurrentInstance().addMessage("Warning", new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning:", "Could not parse birthdate \"" + birthdate + "\" from person " + person.getLocalIdentifier()));
//				throw new PatientParseException(messages.getString("batch.error.parseBirthdate"), e);
			}
		}
		
		String gender = this.getValueInColumn(record, Type.Gender);
		if (gender == null || gender.isEmpty())
		{
				person.setSex(null);
		}
		else
		{
			if (gender.equals("M")) {
				person.setSex(Gender.M);
			} else if (gender.equals("F") || gender.equals("W")) {
				person.setSex(Gender.F);
			} else if (gender.equals("O")) {
				person.setSex(Gender.O);
			} else if (gender.equals("U")) {
				person.setSex(Gender.U);
			} else {
				FacesContext.getCurrentInstance().addMessage("Warning", new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning:", "Could not parse gender \"" + gender + "\" from person " + person.getLocalIdentifier()));
//				throw new PatientParseException("Unkown gender '" + gender + "'.");
			}
		}
		
		

		ArrayList<String> values = new ArrayList<String>();
		values.add(this.getValueInColumn(record, Type.Value1));
		values.add(this.getValueInColumn(record, Type.Value2));
		values.add(this.getValueInColumn(record, Type.Value3));
		values.add(this.getValueInColumn(record, Type.Value4));
		values.add(this.getValueInColumn(record, Type.Value5));
		values.add(this.getValueInColumn(record, Type.Value6));
		values.add(this.getValueInColumn(record, Type.Value7));
		values.add(this.getValueInColumn(record, Type.Value8));
		values.add(this.getValueInColumn(record, Type.Value9));
		values.add(this.getValueInColumn(record, Type.Value10));
		person.setValues(values);
	
		Contact contact = new Contact();
		// Build street and number from individual columns if not set in combined column
		String streetAndNumber = this.getValueInColumn(record, Type.Street_Number);
		String street = this.getValueInColumn(record, Type.Street);
		String number = this.getValueInColumn(record, Type.Number);
		if (streetAndNumber == null) {
			if (street != null)
				streetAndNumber = street;
			if (streetAndNumber != null && number != null)
				streetAndNumber += " " + number;
		}
		contact.setStreet(streetAndNumber);
		contact.setZipCode(this.getValueInColumn(record, Type.ZIP));
		contact.setCity(this.getValueInColumn(record, Type.City));
		contact.setState(this.getValueInColumn(record, Type.State));
		contact.setCountry(this.getValueInColumn(record, Type.Country));
		contact.setPhone(this.getValueInColumn(record, Type.Phone));
		contact.setEmail(this.getValueInColumn(record, Type.E_Mail));
		TableContact tableContact = new TableContact();
		tableContact.setContact(contact);
		person.addContact(tableContact);
		
		return person;
	}
	
	/**
	 * Prepare a download file
	 * 
	 * @param errorOnly choose if all or only persons with error in response code should be in the downloadfile
	 */
	private void prepareDownloadFile(Boolean errorOnly) {
		this.exportData = new ArrayList<List<String>>();
		
		// Set Column order (MPI + all possible columns)
		this.columnOrderNew = new ArrayList<Column>();
		this.columnOrderNew.add(new Column("MPI"));
		for (String type : Column.getColumnTypes()) {
			this.columnOrderNew.add(new Column(type));
		}
		this.columnOrderNew.add(new Column("Error_Code"));
		this.columns = this.columnOrderNew;

		// Create CSV
		for (MPIResponse response : responses) {
			ResponseEntry entry = response.getResponseEntries().get(0);
			
			if (!errorOnly || entry.getErrorCode().getCode() != 0) {
				this.exportPerson(entry.getPerson(), null, entry.getErrorCode().getCode());
			}
		}
		
		String filename = errorOnly ? "import_error" : "import_result";
		this.createDownloadFile(filename);
	}

	/**
	 * 
	 * @return Progress of Batch Processing in percentage
	 */
	public Integer getProgress() {
		if (this.data == null || this.data.size() == 0) {
			return 0;
		} else {
			return this.responses.size() * 100 / this.data.size();
		}

	}
	
	public String getLocalIdentifier(List<Identifier> identifiers) {
		if (!identifiers.isEmpty())
			return identifiers.get(0).getValue();
		else
			return "";
	}

	// ----------getters/setters---------------------------------------
	public List<PatientModel> getPersons() {
		return persons;
	}

	public void setPersons(List<PatientModel> persons) {
		this.persons = persons;
	}

	public UploadedFile getUploadFile() {
		return uploadFile;
	}

	public void setUploadFile(UploadedFile uploadFile) {
		this.uploadFile = uploadFile;
	}

	public List<MPIResponse> getResponses() {
		return responses;
	}

	public void setResponses(List<MPIResponse> responses) {
		this.responses = responses;
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

	public DefaultStreamedContent getDownloadFile() {
		return downloadFile;
	}

	public void setDownloadFile(DefaultStreamedContent downloadFile) {
		this.downloadFile = downloadFile;
	}

	public StreamedContent getExampleFile() {
		return exampleFile;
	}

	public void setExampleFile(StreamedContent exampleFile) {
		this.exampleFile = exampleFile;
	}
}
