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
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.xml.datatype.DatatypeConfigurationException;

import org.emau.icmvc.ganimed.epix.common.MPIException;
import org.emau.icmvc.ganimed.epix.common.model.IdentifierDomain;
import org.emau.icmvc.ganimed.epix.common.model.MPIResponse;
import org.emau.icmvc.ganimed.epix.common.model.ResponseEntry;
import org.emau.icmvc.ganimed.epix.pdqquery.model.SearchMask;
import org.emau.icmvc.ganimed.ttp.exception.EmptyMaskException;
import org.emau.icmvc.ganimed.ttp.exception.InvalidIdentifierException;
import org.emau.icmvc.ganimed.ttp.model.FormModel;
import org.emau.icmvc.ganimed.ttp.modelV2.PatientModel;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

/**
 * Created 8th Oct. 2013
 * 
 * @author mathiasd
 * 
 */
@ViewScoped
@ManagedBean(name = "searchBean")
public class SearchMPIBean extends AbstractManagedBean {

	private FormModel formModel;

	private PatientModel patientModel;

	private List<ResponseEntry> responseEntries = new ArrayList<ResponseEntry>();

	private ResponseEntry selectedEntry;

	private ResponseEntryDataModel entryModel;

	private boolean editButtonDisable = true;

	private boolean searchmaskValid;

	/**
	 * Method called, when site is loaded.
	 */
	public void onStart() {
		formModel = new FormModel();
		patientModel = new PatientModel();
		try {
			formModel.setLocalDomains(proxy.getAllIdentifierDomains());
		} catch (MPIException e) {
			logger.debug("Unable to retrieve local domain identifiers. " + e.getMessage());
		}
	}

	/**
	 * Searches all persons accoring to the searchmask by PDQ.
	 * 
	 * @throws Exception
	 */
	public void searchPerson() // throws Exception
	{
		try {
			logger.info("Searching Person by PDQ");

			String identifierDomainOid = null;
			if (patientModel.getIdentifierDomain() != null) {
				for (IdentifierDomain idd : formModel.getLocalDomains()) {
					if (idd.getName().equals(patientModel.getIdentifierDomain())) {
						identifierDomainOid = idd.getOid();
					}
				}
			}
			// create the searchMask
			SearchMask searchMask = patientModel.toSearchMask(identifierDomainOid);

			//TODO klappt nun nicht mehr mit Auszügen eines Identifiers --> In PersonDAOImpl wurde createWhereForLocalId() geändert (vorher war dort ein Like nun ein = aus Performancegründen)
			// proxy-method for searching person
			MPIResponse response = proxy.queryPersonByDemographicData(searchMask);
			// reads the responded entries
			responseEntries = response.getResponseEntries();
			// fills dataModel for dataTable
			entryModel = new ResponseEntryDataModel(responseEntries);
			// logging the search
			logger.info("Searching Person by PDQ success:" + entryModel.getRowCount() + "Entries found");
			FacesContext.getCurrentInstance().addMessage("info",
					new FacesMessage(FacesMessage.SEVERITY_INFO, "", entryModel.getRowCount() + " " + messages.getString("search.info.success")));
		} catch (MPIException e) {
			logger.error(e.getMessage(), e);
		} catch (DatatypeConfigurationException e) {
			logger.error(e.getMessage());
		} catch (InvalidIdentifierException e) {
			FacesContext.getCurrentInstance().addMessage("info",
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Operation canceled", messages.getString("search.error.identifierInvalid")));
		} catch (EmptyMaskException e) {
			FacesContext.getCurrentInstance().addMessage("info",
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Operation canceled", messages.getString("search.error.emptyMask")));
		}
	}

	/**
	 * Puts the selected Person from the dataTable in the ExternalContext and redirects to the next View.
	 * 
	 * @return String
	 */
	public String edit() {
		// puts selected Person into the ExternalContext
		// so it can be read in the next view (update-view)
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, Object> sessionMap = externalContext.getSessionMap();
		sessionMap.put("selectionKey", selectedEntry);

		// redirects to the update-View
		return "update.xhml?faces-redirect=true";
	}

	/**
	 * Event-method for selecting an entry in the dataTable.
	 * 
	 * @param event
	 */
	public void onRowSelect(SelectEvent event) {
		editButtonDisable = false;
		selectedEntry = (ResponseEntry) event.getObject();
	}

	/**
	 * Event-method for canceling the selection of a person in the dataTable. Disables the Update-button.
	 * 
	 * @param SelectEvent
	 */
	public void onRowUnselect(UnselectEvent event) {
		editButtonDisable = true;
		selectedEntry = null;
	}

	public void onDomainChanged() {
		if (patientModel.getIdentifierDomain() == null) {
			patientModel.setLocalIdentifier(null);
		}
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
	 * Get-Method for responseEntries.
	 * 
	 * @return responseEntries
	 */
	public List<ResponseEntry> getResponseEntries() {
		return responseEntries;
	}

	/**
	 * Set-Method for responseEntries.
	 * 
	 * @param responseEntries
	 */
	public void setResponseEntries(List<ResponseEntry> responseEntries) {
		this.responseEntries = responseEntries;
	}

	/**
	 * Get-Method for selectedEntry.
	 * 
	 * @return selectedEntry
	 */
	public ResponseEntry getSelectedEntry() {
		return selectedEntry;
	}

	/**
	 * Set-Method for selectedEntry.
	 * 
	 * @param selectedEntry
	 */
	public void setSelectedEntry(ResponseEntry selectedEntry) {
		this.selectedEntry = selectedEntry;
	}

	/**
	 * Get-Method for entryModel.
	 * 
	 * @return entryModel
	 */
	public ResponseEntryDataModel getEntryModel() {
		return entryModel;
	}

	/**
	 * Set-Method for entryModel.
	 * 
	 * @param entryModel
	 */
	public void setEntryModel(ResponseEntryDataModel entryModel) {
		this.entryModel = entryModel;
	}

	/**
	 * Get-Method for editButtonDisable.
	 * 
	 * @return editButtonDisable
	 */
	public boolean isEditButtonDisable() {
		return editButtonDisable;
	}

	/**
	 * Set-Method for editButtonDisable.
	 * 
	 * @param editButtonDisable
	 */
	public void setEditButtonDisable(boolean editButtonDisable) {
		this.editButtonDisable = editButtonDisable;
	}

	/**
	 * Get-Method for searchmaskValid.
	 * 
	 * @return searchmaskValid
	 */
	public boolean isSearchmaskValid() {
		return searchmaskValid;
	}

	/**
	 * Set-Method for searchmaskValid.
	 * 
	 * @param searchmaskValid
	 */
	public void setSearchmaskValid(boolean searchmaskValid) {
		this.searchmaskValid = searchmaskValid;
	}

	/**
	 * Action for the "Update Person"-Button. Checks if the birthdate is valid and either updates the Person or returns a error message to the view.
	 */
	public String editButtonListener(ResponseEntry entry) {
		selectedEntry = entry;
		return edit();
	}
}
