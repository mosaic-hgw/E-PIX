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
import org.emau.icmvc.ganimed.epix.common.model.ErrorCode;
import org.emau.icmvc.ganimed.epix.common.model.IdentifierDomain;
import org.emau.icmvc.ganimed.epix.common.model.MPIResponse;
import org.emau.icmvc.ganimed.epix.common.model.ResponseEntry;
import org.emau.icmvc.ganimed.ttp.model.FormModel;
import org.emau.icmvc.ganimed.ttp.model.TableContact;
import org.emau.icmvc.ganimed.ttp.modelV2.PatientModel;

/**
 * Created 1st Oct. 2013
 * 
 * @author mathiasd
 * 
 */
@ViewScoped
@ManagedBean(name = "generateBean")
public class GenerateMPIBean extends AbstractManagedBean {

	private FormModel formModel;

	private PatientModel patientModel;

	private TableContact contact;

	// XXX Epix benötigt Funktion, um matches-config Konfiguration aus
	// epix-core/epix-project-ear auszulesen

	/**
	 * Method called when loading the page.
	 */
	public void onStart() {
		formModel = new FormModel();
		patientModel = new PatientModel();
		contact = new TableContact();
		formModel.setPersontype(0);
		resetInputs();

		try {
			formModel.setLocalDomains(proxy.getAllIdentifierDomains());
		} catch (MPIException e) {
			logger.debug("Unable to retrieve local domain identifiers. " + e.getMessage());
		}

		formModel.updateOutputs(proxy);
		formModel.prepareDropdownMenus();
	}

	/**
	 * Action for the "Generate MPI"-Button. Checks if the birthdate is valid and either generates the MPI or returns a error message to the view.
	 */
	public String generateButtonListener() {
		requestMPI();
		return "responseGenerate.xhtml?faces-redirect=true";
	}

	/**
	 * Adds a contact to the list for this Person.
	 */
	public void addContact() {

		if (!contactIsEmpty(contact.getContact())) {
			String info;
			if (contact.getId() == 0) {
				info = "Contact added.";

			} else {
				info = "Contact saved.";
			}
			patientModel.addContact(contact);

			logger.debug(info);
			FacesContext.getCurrentInstance().addMessage("info", new FacesMessage(FacesMessage.SEVERITY_INFO, info, ""));
			// resets the input
			contact = new TableContact();

		} else {
			String info = "Contact is empty. At least one property has to be set. Operation canceled.";
			logger.debug(info);
			FacesContext.getCurrentInstance().addMessage("info", new FacesMessage(FacesMessage.SEVERITY_INFO, "Operation canceled", info));
		}

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

	/**
	 * Requests the MPI with the Proxy-method.
	 */
	public void requestMPI() {
		try {

			// getIdentifierDomain Object
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
			
			// proxy-method for requesting a MPI
			if (!contactIsEmpty(contact.getContact()) && contact.getId() == 0) {
				patientModel.addContact(contact);
			}
			// setze source
//			patientModel.s
//			request.setSource(identifierDomain.getName());
//			patientModel.
			
			
			MPIResponse response = proxy.requestMPI(patientModel.toRequest(identifierDomain));
			// putting the response in the ExternalContext-sessionmap with a key
			// so another view (responseGenerate) can access these informations,
			// even when this bean is terminated
			final ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
			final Map<String, Object> sessionMap = externalContext.getSessionMap();
			sessionMap.put("responseKey", response);
			// resets all Inputs in the view
			resetInputs();
			// logging the request
			logger.info("MPIRequest for generating MPI was send. MPI Value: {}, Response: {} -- {} ", response.getResponseEntries().get(0)
					.getPerson().getMpiid().getValue(), response.getResponseEntries().get(0).getErrorCode().getCode(), response.getResponseEntries()
					.get(0).getErrorCode().getMessage());

			// XXX Error Nr. 43: "Error during perfectMatch" beim Generieren
			// einer MPI für eine Person, die ein perfect match für eine
			// geupdatete Person ist

		} catch (final MPIException e) {
			logger.error(e.getMessage(), e);
		} catch (DatatypeConfigurationException e) {
			logger.error(e.getMessage());
		}
	}

	/**
	 * Reads the MPIResponse of the Generated MPI and creates Outout-strings for the Response-View.
	 */
	public void response() {
		// reads response from the ExternalContet-sessionmap
		final ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		final Map<String, Object> sessionMap = externalContext.getSessionMap();
		final MPIResponse response = (MPIResponse) sessionMap.get("responseKey");

		formModel = new FormModel();
		patientModel = new PatientModel();

		final StringBuilder sb = new StringBuilder();

		if (response != null) {
			// if a response exists
			// setting booleans for the rendering of the views
			formModel.setShowEmptyView(false);
			formModel.setShowMainView(true);
			final ResponseEntry r = response.getResponseEntries().get(0);

			if (r.getErrorCode().getCode() == ErrorCode.OK) {
				sb.append("The generated MPI ID for the Person is: ");

				formModel.setResponseMPI(r.getPerson().getMpiid().getValue());
			} else {
				if (r.getErrorCode().getCode() == ErrorCode.CRITICAL_MATCH_FOR_PERSON) {
					sb.append("[Response Code: ").append(r.getErrorCode().getCode()).append("]\t ")
							.append("Matching algorithm found ambigious match for given person!");

					formModel.setResponseMPI(new String());

				} else {
					sb.append("[Response Code: ").append(r.getErrorCode().getCode()).append("]\t ").append(r.getErrorCode().getMessage());

					formModel.setResponseMPI(new String());
				}

			}
		}

		// no response available
		else {
			sb.append("Unable to get response from MPI Service!");
			// when no response exists
			formModel.setShowMainView(false);
			formModel.setShowEmptyView(true);
		}

		formModel.setResponseEntry(sb.toString());
		sessionMap.clear();
	}

	/**
	 * Handles the change of the person type. En- or disables the Button.
	 */
	public void personTypeChangeListener() {
		if (patientModel.getPersonType().equals("Patient")) {
			formModel.setPersontype(0);
		} else if (patientModel.getPersonType().equals("HealthcareProvider")) {
			formModel.setPersontype(1);
		}

		formModel.updateOutputs(proxy);
	}

	/**
	 * Sets Input-areas to default.
	 */
	private void resetInputs() {
		patientModel = new PatientModel();
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
	public void setFormModel(final FormModel formModel) {
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
	public void setPatientModel(final PatientModel patientModel) {
		this.patientModel = patientModel;
	}

	public void deleteButtonListener(TableContact contactEntry) {
		patientModel.deleteContact(contactEntry);

		// feedback

		FacesContext.getCurrentInstance().addMessage("feedback", new FacesMessage(FacesMessage.SEVERITY_INFO, "REMOVE", "Contact removed"));
	}

	public void cancelButtonListener() {
		contact = new TableContact();
	}

	public void editButtonListener(TableContact contactEntry) {
		this.contact = contactEntry;
	}

	public TableContact getContact() {
		return contact;
	}

	public void setContact(TableContact contact) {
		this.contact = contact;
	}
}
