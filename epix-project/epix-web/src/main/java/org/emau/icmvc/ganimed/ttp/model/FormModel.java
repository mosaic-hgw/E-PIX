package org.emau.icmvc.ganimed.ttp.model;

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


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.faces.context.FacesContext;

import org.emau.icmvc.ganimed.epix.common.MPIException;
import org.emau.icmvc.ganimed.epix.common.model.IdentifierDomain;
import org.emau.icmvc.ganimed.epix.service.EPIXService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FormModel {

	private Logger logger = LoggerFactory.getLogger(getClass());

	// Lists for Dropdown-menus
	private ArrayList<String> states = new ArrayList<String>();

	// private ArrayList<TableContact> tableContacts = new ArrayList<TableContact>();

	// private ArrayList<Contact> contacts = new ArrayList<Contact>();

	private ArrayList<String> requiredElements;

	private Map<String, String> valueMapping;

	private ArrayList<String> requirements;

	private List<IdentifierDomain> localDomains;

	public List<IdentifierDomain> getLocalDomains() {
		return localDomains;
	}

	public void setLocalDomains(List<IdentifierDomain> localDomains) {
		this.localDomains = localDomains;
	}

	private int persontype;

	// Strings for Response Output
	private String responseEntry;

	private String responseMPI;

	private boolean showMainView = true;

	private boolean showEmptyView = false;

	/**
	 * Method which fills all Lists for the Dropdown-menus
	 */
	public void prepareDropdownMenus() {
		states = new ArrayList<String>();
		states.add("Baden-Württemberg");
		states.add("Bayern");
		states.add("Berlin");
		states.add("Brandenburg");
		states.add("Bremen");
		states.add("Hamburg");
		states.add("Hessen");
		states.add("Mecklenburg-Vorpommern");
		states.add("Niedersachsen");
		states.add("Nordrhein-Westfalen");
		states.add("Rheinland-Pfalz");
		states.add("Saarland");
		states.add("Sachsen");
		states.add("Sachsen-Anhalt");
		states.add("Schleswig-Holstein");
		states.add("Thüringen");
	}

	/**
	 * Returns boolean whether the field is required or not.
	 * 
	 * @param String
	 * @return boolean
	 */
	public boolean returnValidation(String s) {
		if (requirements.contains(s)) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * rückgabe für strings bei internationalisierung
	 * 
	 * public static String getWording(String key, Object... args) { FacesContext ctx=FacesContext.getCurrentInstance(); ResourceBundle bundle=ctx.getApplication().getResourceBundle(ctx, "messages");
	 * String message=bundle.getString(key); return MessageFormat.format(message, args); }
	 */

	/**
	 * Marks the labels for the Textareas.
	 */
	public void updateOutputs(EPIXService proxy) {

		requirements = new ArrayList<String>();
		valueMapping = new HashMap<String, String>();
		try {
			// read required Fields
			List<String> elements;
			elements = proxy.getConfig().getRequiredFields();
			Properties properties = new Properties();
			InputStream input = FacesContext.getCurrentInstance().getExternalContext()
					.getResourceAsStream("/WEB-INF/classes/validator_configuration.properties");
			properties.load(input);
			input.close();
			for (String e : elements) {
				requirements.add(properties.getProperty(e));
			}
			// read valueMapping
			valueMapping = proxy.getConfig().getValueFieldMapping();
		} catch (IOException e1) {
			logger.error(e1.getMessage());
		} catch (MPIException e1) {
			logger.error(e1.getMessage());
		}
	}

	/**
	 * Reads die .xml config and filters every Element which is required in the form.
	 * 
	 * @return ArrayList
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	// private ArrayList<String> readConfig() throws SAXException, IOException,
	// ParserConfigurationException
	// {
	// // TODO korrekten Pfad
	// // matching-config-2.1.0.xml
	// // for current class, so epix-web.war-resources
	// // Webservice needed
	// File config = new File(
	// "C:/Users/mathiasd/Desktop/matching-config-2.1.0.xml");
	// requiredElements = new ArrayList<String>();
	// if (config.exists())
	// {
	// DocumentBuilderFactory dbFactory = DocumentBuilderFactory
	// .newInstance();
	// DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	// Document doc = dBuilder.parse(config);
	// doc.getDocumentElement().normalize();
	// // reads all sub-tags of required-type
	// NodeList nList = doc.getElementsByTagName("required-type");
	// // Node for every persontype
	// // 0 is for Patient, 1 is for HealthcareProvider
	// Node nNode = nList.item(persontype);
	//
	// if (nNode.getNodeType() == Node.ELEMENT_NODE)
	// {
	// Element eElement = (Element) nNode;
	// // reads the fields
	// NodeList list = eElement.getElementsByTagName("field-name");
	// for (int i = 0; i < list.getLength(); i++)
	// {
	// requiredElements.add(list.item(i).getTextContent());
	// }
	// }
	// } else
	// {
	// // default required fields
	// requiredElements.add("firstName");
	// requiredElements.add("lastName");
	// requiredElements.add("birthDate");
	// requiredElements.add("gender");
	// requiredElements.add("localIdentifier");
	// requiredElements.add("localDomain");
	// }
	// return requiredElements;
	// }

	/**
	 * Get-Method for states.
	 * 
	 * @return states
	 */
	public ArrayList<String> getStates() {
		return states;
	}

	/**
	 * Set-Method for states.
	 * 
	 * @param states
	 */
	public void setStates(ArrayList<String> states) {
		this.states = states;
	}

	// /**
	// * Get-Method for tableContacts.
	// *
	// * @return tableContacts
	// */
	// public ArrayList<TableContact> getTableContacts()
	// {
	// return tableContacts;
	// }
	//
	// /**
	// * Set-Method for tableContacts.
	// *
	// * @param tableContacts
	// */
	// public void setTableContacts(ArrayList<TableContact> tableContacts)
	// {
	// this.tableContacts = tableContacts;
	// }
	//
	// /**
	// * Get-Method for contacts.
	// *
	// * @return contacts
	// */
	// public ArrayList<Contact> getContacts()
	// {
	// return contacts;
	// }
	//
	// /**
	// * Set-Method for contacts.
	// *
	// * @param contacts
	// */
	// public void setContacts(ArrayList<Contact> contacts)
	// {
	// this.contacts = contacts;
	// }

	/**
	 * Get-Method for requiredElements.
	 * 
	 * @return requiredElements
	 */
	public ArrayList<String> getRequiredElements() {
		return requiredElements;
	}

	/**
	 * Set-Method for requiredElements.
	 * 
	 * @param requiredElements
	 */
	public void setRequiredElements(ArrayList<String> requiredElements) {
		this.requiredElements = requiredElements;
	}

	public ArrayList<String> getRequirements() {
		return requirements;
	}

	public void setRequirements(ArrayList<String> requirements) {
		this.requirements = requirements;
	}

	/**
	 * Get-Method for persontype.
	 * 
	 * @return persontype
	 */
	public int getPersontype() {
		return persontype;
	}

	/**
	 * Set-Method for persontype.
	 * 
	 * @param persontype
	 */
	public void setPersontype(int persontype) {
		this.persontype = persontype;
	}

	/**
	 * Get-Method for responseEntry.
	 * 
	 * @return responseEntry
	 */
	public String getResponseEntry() {
		return responseEntry;
	}

	/**
	 * Set-Method for responseEntry.
	 * 
	 * @param responseEntry
	 */
	public void setResponseEntry(String responseEntry) {
		this.responseEntry = responseEntry;
	}

	/**
	 * Get-Method for responseMPI.
	 * 
	 * @return responseMPI
	 */
	public String getResponseMPI() {
		return responseMPI;
	}

	/**
	 * Set-Method for responseMPI.
	 * 
	 * @param responseMPI
	 */
	public void setResponseMPI(String responseMPI) {
		this.responseMPI = responseMPI;
	}

	/**
	 * Get-Method for showMainView.
	 * 
	 * @return showMainView
	 */
	public boolean isShowMainView() {
		return showMainView;
	}

	/**
	 * Set-Method for showMainView.
	 * 
	 * @param showMainView
	 */
	public void setShowMainView(boolean showMainView) {
		this.showMainView = showMainView;
	}

	/**
	 * Get-Method for showEmptyView.
	 * 
	 * @return showEmptyView
	 */
	public boolean isShowEmptyView() {
		return showEmptyView;
	}

	/**
	 * Set-Method for showEmptyView.
	 * 
	 * @param showEmptyView
	 */
	public void setShowEmptyView(boolean showEmptyView) {
		this.showEmptyView = showEmptyView;
	}

	public Map<String, String> getValueMapping() {
		return valueMapping;
	}

	public void setValueMapping(Map<String, String> valueMapping) {
		this.valueMapping = valueMapping;
	}
}
