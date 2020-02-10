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


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.xml.datatype.DatatypeConfigurationException;

import org.emau.icmvc.ganimed.epix.common.MPIException;
import org.emau.icmvc.ganimed.epix.common.model.Contact;
import org.emau.icmvc.ganimed.epix.common.model.IdentifierDomain;
import org.emau.icmvc.ganimed.epix.common.model.MPIID;
import org.emau.icmvc.ganimed.epix.common.model.MPIResponse;
import org.emau.icmvc.ganimed.epix.common.model.Patient;
import org.emau.icmvc.ganimed.epix.common.model.ResponseEntry;
import org.emau.icmvc.ganimed.ttp.model.FormModel;
import org.emau.icmvc.ganimed.ttp.model.TableContact;
import org.emau.icmvc.ganimed.ttp.modelV2.PatientModel;

/**
 * Created 1st Oct. 2013
 * 
 * @author mathiasd
 * 
 *         edited by bialkem on 2014-02-04
 *          edited by kuehnea on 2015-03-06
 * 
 */
@ViewScoped
@ManagedBean(name = "updateBean")
public class UpdateMPIBean extends AbstractManagedBean {

	private FormModel formModel;

	private PatientModel patientModel;

	private TableContact selectedContact;

	private ResponseEntry entry;

	private MPIID mpi;

	/**
	 * Method called, when site is loaded.
	 */
	public void onStart() {
		formModel = new FormModel();
		patientModel = new PatientModel();
		selectedContact = new TableContact();

		formModel.prepareDropdownMenus();

		try {
			formModel.setLocalDomains(proxy.getAllIdentifierDomains());
		} catch (MPIException e) {
			logger.debug("Unable to retrieve local domain identifiers. " + e.getMessage());
		}

		entry = new ResponseEntry();
		// Reads the selected Person from the ExternalContext
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, Object> sessionMap = externalContext.getSessionMap();
		entry = (ResponseEntry) sessionMap.get("selectionKey");

		// Checks, if an entry is existing
		if (entry != null) {
			formModel.setShowEmptyView(false);
			formModel.setShowMainView(true);

			patientModel = new PatientModel(entry.getPerson());

			if (Patient.class.isInstance(entry.getPerson())) {
				patientModel.setPersonType("Patient");
				formModel.setPersontype(0);
			} else {
				patientModel.setPersonType("HealthcareProvider");
				formModel.setPersontype(1);
			}
			// updates the labels of the form according to the type of the
			// person
			formModel.updateOutputs(proxy);

		} else {
			formModel.setShowMainView(false);
			formModel.setShowEmptyView(true);
		}
	}

	/**
	 * Loads all contact information of the current selected Contact.
	 */
	public void editButtonListener(TableContact tableContact) {
		this.selectedContact = tableContact;
	}

	/**
	 * Adds a new contact to the list und resets the inputFields.
	 */
	public void cancelButtonListener() {
		selectedContact = new TableContact();
	}

	public void addContactButtonListener() {

		if (!contactIsEmpty(selectedContact.getContact())) {
			patientModel.addContact(selectedContact);
			String info;
			if (selectedContact.getId() == 0) {
				info = "Contact added.";

			} else {
				info = "Contact saved.";
			}
			patientModel.addContact(selectedContact);

			logger.debug(info);
			FacesContext.getCurrentInstance().addMessage("info", new FacesMessage(FacesMessage.SEVERITY_INFO, info, ""));
			// resets the input
			selectedContact = new TableContact();
		} else {
			String info = "Contact is empty. At least one property has to be set. Operation canceled.";
			logger.debug(info);
			FacesContext.getCurrentInstance().addMessage("info", new FacesMessage(FacesMessage.SEVERITY_INFO, "Operation canceled", info));
		}
	}

	/**
	 * Overrides the selectedContact with the informations from the inputfields.
	 */
	public void updateContact() {
		addContactButtonListener();
		// feedback
		FacesContext.getCurrentInstance().addMessage("feedback", new FacesMessage(FacesMessage.SEVERITY_INFO, "Edit", "Contact was edited"));
	}

	/**
	 * Action for the "Update Person"-Button. Checks if the birthdate is valid and either updates the Person or returns a error message to the view.
	 */
	public String updateButtonListener() {
		updatePerson();
		return "responseUpdate.xhtml?faces-redirect=true";
	}

	/**
	 * Proxy-method for updating the Person.
	 */
	public void updatePerson() {
		// XXX musste wieder die Rolle in der epix-service/epixServiceBean.java
		// ändern
		// getIdentifierDomain Object
		try {
			IdentifierDomain identifierDomain = null;
			for (IdentifierDomain idd : formModel.getLocalDomains()) {
				if (idd.getName().equals(patientModel.getIdentifierDomain())) {
					identifierDomain = idd;
				}
			}
			
			// setze die Werte der Values1 bis 10 auf null, wenn kein Wert eingetragen
			if(!patientModel.getValues().isEmpty()){
				List<String> temp = new ArrayList<String>();

				 Iterator<String> iter = patientModel.getValues().iterator();
			        while (iter.hasNext()) {
			            String s = iter.next();
			            
			            logger.info("Wert: " + s);
			            if(s.isEmpty()){
			            	logger.info("Wert auist leer!");
			            	temp.add(null);
			            }else{
			            	temp.add(s);
			            }

			        } 
			        patientModel.setValues(temp)   ; 			        
			}
			
			// proxy-method for updating person
			MPIResponse response = proxy.updatePerson(patientModel.toRequest(identifierDomain));

			// Response put in ExternalContet for responding View
			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
			Map<String, Object> sessionMap = externalContext.getSessionMap();
			sessionMap.put("responseUpdateKey", response);

			// logging
			logger.info("MPIRequest for Person update was send. MPI Value: {}, Response: {} -- {} ", response.getResponseEntries().get(0).getPerson()
					.getMpiid().getValue(), response.getResponseEntries().get(0).getErrorCode().getCode(), response.getResponseEntries().get(0)
					.getErrorCode().getMessage());

		} catch (MPIException e) {
			logger.error(e.getMessage(), e);
		} catch (DatatypeConfigurationException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Method for the Response View.
	 */
	public void response() {
		// loads response
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, Object> sessionMap = externalContext.getSessionMap();
		MPIResponse response = (MPIResponse) sessionMap.get("responseUpdateKey");
		formModel = new FormModel();
		patientModel = new PatientModel();

		StringBuilder sb = new StringBuilder();

		if (response != null) {
			formModel.setShowEmptyView(false);
			formModel.setShowMainView(true);
			ResponseEntry r = response.getResponseEntries().get(0);

			sb.append("[Response Code: ").append(r.getErrorCode().getCode())

			.append("] \t").append(r.getErrorCode().getMessage());

		}

		else {
			sb.append("Unable to get response from MPI Service!");
			formModel.setShowMainView(false);
			formModel.setShowEmptyView(true);
		}

		formModel.setResponseEntry(sb.toString());
		sessionMap.clear();
	}

	/**
	 * Get-Method for formModel.
	 * 
	 * @return formModel
	 */
	public FormModel getFormModel() {
		return formModel;
	}

	/**
	 * Set-Method for formModel.
	 * 
	 * @param formModel
	 */
	public void setFormModel(FormModel formModel) {
		this.formModel = formModel;
	}

	/**
	 * Get-Method for patientModel.
	 * 
	 * @return patientModel
	 */
	public PatientModel getPatientModel() {
		return patientModel;
	}

	/**
	 * Set-Method for patientModel.
	 * 
	 * @param patientModel
	 */
	public void setPatientModel(PatientModel patientModel) {
		this.patientModel = patientModel;
	}

	/**
	 * Get-Method for entry.
	 * 
	 * @return entry
	 */
	public ResponseEntry getEntry() {
		return entry;
	}

	/**
	 * Set-Method for entry.
	 * 
	 * @param entry
	 */
	public void setEntry(ResponseEntry entry) {
		this.entry = entry;
	}

	/**
	 * Get-Method for mpi.
	 * 
	 * @return mpi
	 */
	public MPIID getMpi() {
		return mpi;
	}

	/**
	 * Set-Method for mpi.
	 * 
	 * @param mpi
	 */
	public void setMpi(MPIID mpi) {
		this.mpi = mpi;
	}

	/**
	 * Get-Method for selectedContact.
	 * 
	 * @return selectedContact
	 */
	public TableContact getSelectedContact() {
		return selectedContact;
	}

	/**
	 * Set-Method for selectedContact.
	 * 
	 * @param selectedContact
	 */
	public void setSelectedContact(TableContact selectedContact) {
		this.selectedContact = selectedContact;
	}

	/**
	 * Get-Method for contactModel.
	 * 
	 * @return contactModel
	 */
	public void deleteButtonListener(TableContact contactEntry) {
		patientModel.deleteContact(contactEntry);

		// feedback
		FacesContext.getCurrentInstance().addMessage("feedback", new FacesMessage(FacesMessage.SEVERITY_INFO, "REMOVE", "Contact removed"));
	}

	/**
	 * check if given contact is null or empty
	 * 
	 * @param c
	 *            contact to be checked
	 * @return true if each field of c is null, false otherwise
	 */
	private boolean contactIsEmpty(Contact c) {
		if (c != null
				&& (c.getStreet() != null || c.getZipCode() != null || c.getCity() != null || c.getState() != null || c.getCountry() != null
						|| c.getPhone() != null || c.getEmail() != null || c.getDistrict() != null)) {
			return false;
		}
		return true;
	}
}
