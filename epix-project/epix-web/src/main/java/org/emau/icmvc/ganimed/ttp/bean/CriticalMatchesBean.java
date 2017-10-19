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

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.validation.constraints.Size;

import org.emau.icmvc.ganimed.epix.common.MPIException;
import org.emau.icmvc.ganimed.epix.common.model.Contact;
import org.emau.icmvc.ganimed.epix.common.model.CriticalMatch;
import org.emau.icmvc.ganimed.epix.common.model.MPIResponse;
import org.emau.icmvc.ganimed.epix.pdqquery.model.SearchMask;
import org.emau.icmvc.ganimed.ttp.model.CriticalMatchesDataModel;
import org.emau.icmvc.ganimed.ttp.model.PersonDetails;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Enumeration;

/**
 * 
 * created Sept. 2013
 * 
 * @author mathiasd
 * 
 */
@SessionScoped
@ManagedBean(name = "criticalMatches")
public class CriticalMatchesBean extends AbstractManagedBean {

	private List<CriticalMatch> matchesList = new ArrayList<CriticalMatch>();
	private List<CriticalMatch> filteredMatchesList;

	private CriticalMatch selectedMatch;

	// DataModel which contains the data of the arraylist
	private CriticalMatchesDataModel matchesModel;

	private List<CriticalMatch> linksList = new ArrayList<CriticalMatch>();

	private CriticalMatch selectedLink;

	// DataModel which contains the data of the arraylist
	private CriticalMatchesDataModel linksModel;

	// Models for detailed Infos about Critical Matches
	private PersonDetails criticalMatchDetails;
	private PersonDetails linkedMatchDetails;

	private boolean removeLinksButtonDisable = true;

	private boolean assignButtonDisable = true;
	
	private String thsCode;

	// Annotation RunAs in the class MpiEjb can only contain one role.
	// So the application can only use one kind of methods at a time (user or
	// admin).
	// For testing you either have to create a user wit multiple roles or change
	// the accepted Roles of the methods in the EPIXServiceBean.

	
	
	/**
	 * The tables are resetted and the current CriticalMatches are requested and loaded into the CriticalMatches-table.
	 * 
	 * @author mathiasd
	 */
	public void receiveTableData() {
		// Reset linkedPersons table and details-Textareas
		resetTable();

		// Request CriticalMatches list
		try {
			matchesList = null;
			matchesList = proxy.requestCriticalMatches();
			
			
			// The same Person appear multiple times in the list, when the
			// person has multiple links to other person, he appears for each
			// link in the list.
			ArrayList<CriticalMatch> result = new ArrayList<CriticalMatch>();
			ArrayList<String> idList = new ArrayList<String>();
			// filtering duplicates
			for (CriticalMatch match : matchesList) {
				if (!idList.contains(match.getDatabaseId())) {
					
//					proxy.requestLinkedPersons(match.getDatabaseId());
					idList.add(match.getDatabaseId());
					result.add(match);
				}
			}
			matchesList = result;

			logger.info("Requested list of criticalmatches, Number total: {} ", matchesList.size());
		} catch (MPIException e) {
			logger.error(e.getMessage(), e);
		}
		// Loading matchesList into table
		matchesModel = new CriticalMatchesDataModel(matchesList);
		
		
		// Selecting first row of criticalMatches and linkedPersons
		// selectCriticalMatch();
		// selectLinkedPerson();
	}

	public String getThsCode() {
		return thsCode;
	}

	public void setThsCode(String thsCode) {
		this.thsCode = thsCode;
	}

	/**
	 * Requests linkedPersons for the selected CriticalMatch. The 'Remove Links'-Button becomes enabled.
	 * 
	 * @param SelectEvent
	 * @author mathiasd
	 */
	public void onRowSelectMatch(SelectEvent event) {
		// resetting linkedPerson table
		resetTable();
		selectedMatch = (CriticalMatch) event.getObject();
		try {
			linksList = proxy.requestLinkedPersons(selectedMatch.getDatabaseId());
		} catch (MPIException e) {
			logger.error(e.getMessage(), e);
		}

		// filtering of seletedMatch from the linklist.
		// returned list from proxy.requestLinkedPersons contains also the
		// selectedMatch
		ArrayList<CriticalMatch> tempList = new ArrayList<CriticalMatch>();
		for (CriticalMatch crit : linksList) {
			if (!crit.getDatabaseId().equalsIgnoreCase(selectedMatch.getDatabaseId())) {
				tempList.add(crit);
			}
		}
		linksList = tempList;
		
		
		// generating criticalMatchDetails
		setCriticalMatchDetails(PersonDetails(selectedMatch));
		// enabling removeLinks button
		removeLinksButtonDisable = false;
		// updating linkedPerson table for current Match
		linksModel = new CriticalMatchesDataModel(linksList);
		// selecting first row of linkedPerson table
		// selectLinkedPerson();
	}

	/**
	 * Updates the details for the selected Persons and enables the assign- and removeLink-buttons. create the THSCode Key
	 * 
	 * @param SelectEvent
	 * @author mathiasd, kuehnea
	 */
	public void onRowSelectLink(SelectEvent event) {
		// enabling assign buttons
		assignButtonDisable = false;
		selectedLink = (CriticalMatch) event.getObject();
		
		// THS Code ermitteln	
		String calculateTHSCode = setTHSCode();
		setThsCode(calculateTHSCode);
		
		// setze Begründung automatisch, wenn es Daten gibt
		if(!calculateTHSCode.equals(""))
			setExplanation(calculateTHSCode);
		
		// Adressen? 
		
		SearchMask tempSearchMAskSelectedLink = new SearchMask();

		tempSearchMAskSelectedLink.setMpiid(selectedLink.getMpiid());
		logger.info("selectedLink.getMpiid() " + selectedLink.getMpiid());
		
		// Anzeige aller Adressen für das Value6. Ist nur für die THS-ZKKR wichtig!
		try {
			MPIResponse tempPersonSelectedLink = proxy.queryPersonByDemographicData(tempSearchMAskSelectedLink);
			String tempContactData = "";
			
			if(!tempPersonSelectedLink.getResponseEntries().get(0).getPerson().getContacts().isEmpty()){
				Iterator<Contact> iter = tempPersonSelectedLink.getResponseEntries().get(0).getPerson().getContacts().iterator();
		        while (iter.hasNext()) {
		        	Contact s = iter.next();
		        	tempContactData = tempContactData + " " + s.getZipCode() + " " + s.getCity() + " " + s.getStreet() + " " + s.getDistrict();	            
		        } 
			}
			
			selectedLink.setValue6(tempContactData);
			
		} catch (MPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		SearchMask tempSearchMAskSelectedMatch = new SearchMask();
//		tempSearchMAsk.setFirstName(selectedLink.getFirstName());
//		tempSearchMAsk.setLastName(selectedLink.getName())
		tempSearchMAskSelectedMatch.setMpiid(selectedMatch.getMpiid());
		logger.info("selectedMatch.getMpiid() " + selectedMatch.getMpiid());
		logger.info("selectedMatch.getMpiid() " + selectedMatch.getFirstName());
		
		
		// Anzeige aller Adressen für das Value6. Ist nur für die THS-ZKKR wichtig!
		try {
			MPIResponse tempPersonSelectedMatch = proxy.queryPersonByDemographicData(tempSearchMAskSelectedMatch);
			
			String tempContactDataSelectedMatch = "";
			logger.info("selectedMatch.getMpiid() " + selectedMatch.getFirstName());
			if(!tempPersonSelectedMatch.getResponseEntries().get(0).getPerson().getContacts().isEmpty()){
				Iterator<Contact> iterSelectedMatch = tempPersonSelectedMatch.getResponseEntries().get(0).getPerson().getContacts().iterator();
		        while (iterSelectedMatch.hasNext()) {
		        	logger.info("selectedMatch.getMpiid() " + selectedMatch.getFirstName());
		        	Contact sSelectedMatch = iterSelectedMatch.next();
		        	tempContactDataSelectedMatch = tempContactDataSelectedMatch + " " + sSelectedMatch.getZipCode() + " " + sSelectedMatch.getCity() + " " + sSelectedMatch.getStreet() + " " + sSelectedMatch.getDistrict();	            
		        } 
			}
			
			selectedMatch.setValue6(tempContactDataSelectedMatch);
			setCriticalMatchDetails(PersonDetails(selectedMatch));
			
		} catch (MPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		// generating linkedMatchDetails
		setLinkedMatchDetails(PersonDetails(selectedLink));
	}

	/**
	 * Resets tables and details, when CriticalMatch is unselected.
	 * 
	 * @param UnselectEvent
	 * @author mathiasd
	 */
	public void onRowUnselectMatch(UnselectEvent event) {
		resetTable();
	}

	/**
	 * Resets details, when Linked Person is unselected.
	 * 
	 * @param UnselectEvent
	 * @author mathiasd
	 */
	public void onRowUnselectLink(UnselectEvent event) {
		// disabling assign buttons
		assignButtonDisable = true;
		// deselecting Person in linkedPerson table
		selectedLink = null;
		// resetting linedPersonDetail-textarea
	}

	/**
	 * Empties the LinkedPerson-table and the details and disables the assign- and 'Remove Links'-buttons.
	 * 
	 * @author mathiasd
	 */
	public void resetTable() {
		// emptying linkedPerson table
		linksList = new ArrayList<CriticalMatch>();
		linksModel = new CriticalMatchesDataModel(linksList);
		// disabling buttons for assigning and removing links
		assignButtonDisable = true;
		removeLinksButtonDisable = true;
		// unselecting linkedPerson-entry
		selectedLink = null;
		//selectedMatch = null;
		// resetting detail-textareas
		linkedMatchDetails = null;
		criticalMatchDetails = null;
//		filteredMatchesList = null;
	}

	/**
	 * Sets the Selection of the CriticalMatches-DataTable to the first Object
	 * 
	 * @author mathiasd
	 */
	// public void selectCriticalMatch()
	// {
	// if (!matchesList.isEmpty())
	// {
	// DataTable dt = (DataTable) FacesContext.getCurrentInstance()
	// .getViewRoot().findComponent("main:criticalMatches");
	// // creating selecting-event for CriticalMatches-Datatable
	// SelectEvent select = new SelectEvent(dt,
	// new org.primefaces.component.behavior.ajax.AjaxBehavior(),
	// matchesList.get(0));
	// onRowSelectMatch(select);
	// }
	// }

	/**
	 * Sets the Selection of the LinkedPerson-DataTable to the first Object
	 * 
	 * @author mathiasd
	 */
	// public void selectLinkedPerson()
	// {
	// if (!linksList.isEmpty())
	// {
	// DataTable dt = (DataTable) FacesContext.getCurrentInstance()
	// .getViewRoot().findComponent("main:linkedPersons");
	// // creating selecting-event for LinkedPersons-Datatable
	// SelectEvent select = new SelectEvent(dt,
	// new org.primefaces.component.behavior.ajax.AjaxBehavior(),
	// linksList.get(0));
	// onRowSelectLink(select);
	// }
	// }

	/**
	 * Removes all linked Persons for the selected Critical Match.
	 * 
	 * @author mathiasd
	 */
	public void removeLinks() {
		ArrayList<String> personIds = new ArrayList<String>();
		// creating ArrayList with personIDs of te current linkedPersons
		for (CriticalMatch cm : linksList) {
			personIds.add(cm.getDatabaseId());
		}
		try {
			// removing all links between current selected CriticalMatch and
			// linked Persons
			logger.info("removeall: " + explanation);
			proxy.removeLinks(selectedMatch.getDatabaseId(), personIds, explanation);
			logger.info("Removed all Links (Number: {}) from CriticalMatch {}", linksList.size(), selectedMatch.getMpiid());

			resetExplanation();

		} catch (MPIException e) {
			logger.error(e.getMessage(), e);
		}
		// updating Tables
		receiveTableData();
	}

	/**
	 * Removes the selected linked Person from the selected Critical Match.
	 * 
	 * @author mathiasd
	 */
	public void removeLink() {
		try {
			logger.info("remove: " + explanation);
			// remove link between current selected persons
			proxy.removeLink(selectedMatch.getDatabaseId(), selectedLink.getDatabaseId(), explanation);
			logger.info("Link between CriticalMatch {} and LinkedPerson {} removed", selectedMatch.getMpiid(), selectedLink.getMpiid());
			linksList.remove(selectedLink);

			resetExplanation();

		} catch (MPIException e) {
			logger.error(e.getMessage(), e);
		}
		// updating Tables
		receiveTableData();
	}

	/**
	 * Assigns the Linked Person to the Critical Match.
	 * 
	 * @author mathiasd
	 */
	public void assignToLeft() {
		try {
			logger.info("left: " + explanation);
			proxy.assignIdentity(selectedMatch.getDatabaseId(), selectedLink.getDatabaseId(), explanation);
			logger.info("Assigned LinkedPerson {} to CriticalMatch {}", selectedLink.getMpiid(), selectedMatch.getMpiid());
			linksList.remove(selectedLink);

			resetExplanation();

		} catch (MPIException e) {
			logger.error(e.getMessage(), e);
		}
		// updating Tables
		receiveTableData();
	}

	@Size(max = 255)
	private String explanation = "";

	/**
	 * Assigns the Critical Match to the Linked Person.
	 * 
	 * @author mathiasd
	 */
	public void assignToRight() {
		try {
			logger.info("right: " + explanation);
			proxy.assignIdentity(selectedLink.getDatabaseId(), selectedMatch.getDatabaseId(), explanation);
			logger.info("Assigned CriticalMatch {} to LinkedPerson {}", selectedMatch.getMpiid(), selectedLink.getMpiid());
			linksList.remove(selectedLink);
					

			resetExplanation();

		} catch (MPIException e) {
			logger.error(e.getMessage(), e);
		}
		// updating Tables
		receiveTableData();

	}

	private PersonDetails PersonDetails(CriticalMatch crit) {
		return new PersonDetails(crit);
	}

	/**
	 * Returns matchesList
	 * 
	 * @return matchesList
	 * @author mathiasd
	 */
	public List<CriticalMatch> getMatchesList() {
		return matchesList;
	}

	/**
	 * Setting matchesList
	 * 
	 * @param matchesList
	 * @author mathiasd
	 */
	public void setMatchesList(List<CriticalMatch> matchesList) {
		this.matchesList = matchesList;
	}

	/**
	 * Returns selectedMatch
	 * 
	 * @return selectedMatch
	 * @author mathiasd
	 */
	public CriticalMatch getSelectedMatch() {
		return selectedMatch;
	}

	/**
	 * Setting selectedMatch
	 * 
	 * @param selectedMatch
	 * @author mathiasd
	 */
	public void setSelectedMatch(CriticalMatch selectedMatch) {
		this.selectedMatch = selectedMatch;
	}

	/**
	 * Returns matchesModel
	 * 
	 * @return matchesModel
	 * @author mathiasd
	 */
	public CriticalMatchesDataModel getMatchesModel() {
		return matchesModel;
	}

	/**
	 * Setting matchesModel
	 * 
	 * @param matchesModel
	 * @author mathiasd
	 */
	public void setMatchesModel(CriticalMatchesDataModel matchesModel) {
		this.matchesModel = matchesModel;
	}

	/**
	 * Returns linksList
	 * 
	 * @return linksList
	 * @author mathiasd
	 */
	public List<CriticalMatch> getLinksList() {
		return linksList;
	}

	/**
	 * Setting linksList
	 * 
	 * @param linksList
	 * @author mathiasd
	 */
	public void setLinksList(List<CriticalMatch> linksList) {
		this.linksList = linksList;
	}

	/**
	 * Returns selectedLink
	 * 
	 * @return selectedLink
	 * @author mathiasd
	 */
	public CriticalMatch getSelectedLink() {
		return selectedLink;
	}

	/**
	 * Setting selectedLink
	 * 
	 * @param selectedLink
	 * @author mathiasd
	 */
	public void setSelectedLink(CriticalMatch selectedLink) {
		this.selectedLink = selectedLink;
	}

	/**
	 * Returns linksModel
	 * 
	 * @return linksModel
	 * @author mathiasd
	 */
	public CriticalMatchesDataModel getLinksModel() {
		return linksModel;
	}

	/**
	 * Setting linksModel
	 * 
	 * @param linksModel
	 * @author mathiasd
	 */
	public void setLinksModel(CriticalMatchesDataModel linksModel) {
		this.linksModel = linksModel;
	}

	/**
	 * Returns boolean removeLinksButtonDisable
	 * 
	 * @return removeLinksButtonDisable
	 * @author mathiasd
	 */
	public boolean isRemoveLinksButtonDisable() {
		return removeLinksButtonDisable;
	}

	/**
	 * Setting removeLinksButtonDisable
	 * 
	 * @param removeLinksButtonDisable
	 * @author mathiasd
	 */
	public void setRemoveLinksButtonDisable(boolean removeLinksButtonDisable) {
		this.removeLinksButtonDisable = removeLinksButtonDisable;
	}

	/**
	 * Returns boolean assignButtonDisable
	 * 
	 * @return assignButtonDisable
	 * @author mathiasd
	 */
	public boolean isAssignButtonDisable() {
		return assignButtonDisable;
	}

	/**
	 * Setting assignButtonDisable
	 * 
	 * @param assignButtonDisable
	 * @author mathiasd
	 */
	public void setAssignButtonDisable(boolean assignButtonDisable) {
		this.assignButtonDisable = assignButtonDisable;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		logger.info("Explanation set: " + explanation);
		this.explanation = explanation;
	}

	public void resetExplanation() {
		setExplanation("");
	}

	public List<CriticalMatch> getFilteredMatchesList() {
		return filteredMatchesList;
	}

	public void setFilteredMatchesList(List<CriticalMatch> filteredMatchesList) {
		this.filteredMatchesList = filteredMatchesList;
	}

	public PersonDetails getCriticalMatchDetails() {
		return criticalMatchDetails;
	}

	public void setCriticalMatchDetails(PersonDetails criticalMatchDetails) {
		this.criticalMatchDetails = criticalMatchDetails;
	}

	public PersonDetails getLinkedMatchDetails() {
		return linkedMatchDetails;
	}

	public void setLinkedMatchDetails(PersonDetails linkedMatchDetails) {
		this.linkedMatchDetails = linkedMatchDetails;
	}
	
	public int[] setTempData(String selectedLinkValue, String selectedMatchValue,int position, int[] caseResultDiffernce){

		logger.info("setTempData");
		if(selectedLinkValue.equals(selectedMatchValue)){
			caseResultDiffernce[position] = 1;
		}else{
			caseResultDiffernce[position] = 0;
		}
		return caseResultDiffernce;
	}
	
	public String setTHSCode(){
		String thsCode = "";
		logger.info("setTHSCode");
		
		Properties prop = new Properties();
    	InputStream input = null;
    	int maxi = 0;
    	
    	// initialisieren
		int[] caseResultDiffernce = new int[100];
		for(int i= 1;i<=95;i++){ // 5 Kominationsmöglichkeiten = 25 verschiedene Kombinationen
			caseResultDiffernce[i] = 0;
		}
		
		String resultDifference = "";
 
    	try {
    		String filename = "thsCode.properties";
    		input = getClass().getResourceAsStream("/thsCode.properties");
	
    		if(input==null){
    			logger.info("Sorry, unable to find " + filename);
    		}else{
    			//load a properties file from class path, inside static method
        		prop.load(input);
        		logger.info("properties");
        		
                    //get the property value and print it out
        	        
        	        Enumeration<?> e = prop.propertyNames();
        	        
        			while (e.hasMoreElements()) {
        				String key = (String) e.nextElement();
        				String tempValue = prop.getProperty(key);
        				logger.info("Key : " + key + ", Value : " + tempValue);
        				
        				int value = Integer.parseInt(tempValue);
        				
        				if(value>maxi)
        					maxi=value;
        				
        				if(key.equals("first_name") && value>0)
        					setTempData(selectedLink.getFirstName(), selectedMatch.getFirstName(), value, caseResultDiffernce);
        				
        				if(key.equals("last_name") && value>0)
        					setTempData(selectedLink.getName(), selectedMatch.getName(), value, caseResultDiffernce);
        				
        				if(key.equals("gender") && value>0)
        					setTempData(selectedLink.getSex(), selectedMatch.getSex(), value, caseResultDiffernce); 
        				
        				if(key.equals("zip") && value>0)
        					setTempData(selectedLink.getPostalCode(), selectedMatch.getPostalCode(), value, caseResultDiffernce);
        				
        				if(key.equals("date_of_birth") && value>0)
        					setTempData(selectedLink.getBirthdate(), selectedMatch.getBirthdate(), value, caseResultDiffernce);
        				
        				if(key.equals("birthPlace") && value>0)
        					setTempData(selectedLink.getBirthPlace(), selectedMatch.getBirthPlace(), value, caseResultDiffernce);
        				
        				if(key.equals("city") && value>0)
        					setTempData(selectedLink.getCity(), selectedMatch.getCity(), value, caseResultDiffernce);
        				
        				if(key.equals("degree") && value>0)
        					setTempData(selectedLink.getDegree(), selectedMatch.getDegree(), value, caseResultDiffernce);
        				
        				if(key.equals("middle_name") && value>0)
        					setTempData(selectedLink.getMiddleName(), selectedMatch.getMiddleName(), value, caseResultDiffernce);

        				if(key.equals("mothersMaidenName") && value>0)
        					setTempData(selectedLink.getMothersMaidenName(), selectedMatch.getMothersMaidenName(), value, caseResultDiffernce);

        				if(key.equals("nationality") && value>0){
        					if((selectedLink.getNationality()== null || selectedMatch.getNationality()==null) || (selectedLink.getNationality().isEmpty() && selectedMatch.getNationality().isEmpty()) ){
        						thsCode = "Daten nicht verwertbar (nationality)";      						
        						return thsCode;// Abbruch while Schleife
        					}else
        						setTempData(selectedLink.getNationality(), selectedMatch.getNationality(), value, caseResultDiffernce);
        				}
       				
        				if(key.equals("street") && value>0){
        					if((selectedLink.getStreet()== null || selectedMatch.getStreet()==null) || (selectedLink.getStreet().isEmpty() && selectedMatch.getStreet().isEmpty()) ){
        						thsCode = "Daten nicht verwertbar (street)";      						
        						return thsCode;// Abbruch while Schleife
        					}else
        						setTempData(selectedLink.getStreet(), selectedMatch.getStreet(), value, caseResultDiffernce);
        				}

        				if(key.equals("value1") && value>0)
        					setTempData(selectedLink.getValue1(), selectedMatch.getValue1(), value, caseResultDiffernce);
        				if(key.equals("value2") && value>0)
        					setTempData(selectedLink.getValue2(), selectedMatch.getValue2(), value, caseResultDiffernce);
        				if(key.equals("value3") && value>0)
        					setTempData(selectedLink.getValue3(), selectedMatch.getValue3(), value, caseResultDiffernce);
        				if(key.equals("value4") && value>0)
        					setTempData(selectedLink.getValue4(), selectedMatch.getValue4(), value, caseResultDiffernce);
        				if(key.equals("value5") && value>0)
        					setTempData(selectedLink.getValue5(), selectedMatch.getValue5(), value, caseResultDiffernce);
        				if(key.equals("value6") && value>0)
        					setTempData(selectedLink.getValue6(), selectedMatch.getValue6(), value, caseResultDiffernce);
        				if(key.equals("value7") && value>0)
        					setTempData(selectedLink.getValue7(), selectedMatch.getValue7(), value, caseResultDiffernce);
        				if(key.equals("value8") && value>0)
        					setTempData(selectedLink.getValue8(), selectedMatch.getValue8(), value, caseResultDiffernce);
        				if(key.equals("value9") && value>0)
        					setTempData(selectedLink.getValue9(), selectedMatch.getValue9(), value, caseResultDiffernce);
        				if(key.equals("value10") && value>0)
        					setTempData(selectedLink.getValue10(), selectedMatch.getValue10(), value, caseResultDiffernce);		
        			}		
    		}

    	} catch (IOException ex) {
    		ex.printStackTrace();
        } finally{
        	if(input!=null){
        		try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        	}
        }
		
		for(int y= 1;y<=maxi;y++){ 
			resultDifference = resultDifference + caseResultDiffernce[y];
		}

		logger.info(resultDifference);
		
		if(resultDifference.equals("")){
			thsCode="";
		}else{
			int dec = Integer.parseInt(resultDifference, 2);
			logger.info(Integer.toString(dec));
			thsCode = "THS Code: " + Integer.toString(dec);			
		}
	
		return thsCode;
	}
}
