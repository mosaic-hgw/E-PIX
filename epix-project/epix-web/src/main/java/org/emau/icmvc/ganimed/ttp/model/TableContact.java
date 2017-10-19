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


import org.emau.icmvc.ganimed.epix.common.model.Contact;

/**
 * Wrapper-Class for the DataTable containing Contacts. Created 18.Oct 2013
 * 
 * @author mathiasd
 * 
 */
public class TableContact {

	// ID for selecting the row of the dataTable.
	private long id;

	// Output-Label for the DataTable
	private String label;

	// Contact
	private Contact contact;

	public TableContact() {
		this.contact = new Contact();
	}

	public TableContact(int i, Contact contact) {
		this.id = (long) i;
		this.contact = contact;
	}

	/**
	 * Get-Method for id.
	 * 
	 * @return id
	 */
	public long getId() {
		return id;
	}

	/**
	 * Set-Method for id.
	 * 
	 * @param id
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Get-Method for label.
	 * 
	 * @return label
	 */
	public String getLabel() {
		StringBuilder sb = new StringBuilder();
		if (contact.getStreet() != null) {
			sb.append(contact.getStreet()).append(" - ");
		}
		if (contact.getZipCode() != null) {
			sb.append(contact.getZipCode()).append(" - ");
		}
		if (contact.getCity() != null) {
			sb.append(contact.getCity()).append(" - ");
		}
		if (contact.getState() != null) {
			sb.append(contact.getState()).append(" - ");
		}
		if (contact.getCountry() != null) {
			sb.append(contact.getCountry()).append(" - ");
		}
		if (contact.getPhone() != null) {
			sb.append(contact.getPhone()).append(" - ");
		}
		if (contact.getEmail() != null) {
			sb.append(contact.getEmail());
		}
		sb.append("-");
		label = sb.toString();

		return label;
	}

	/**
	 * Set-Method for label.
	 * 
	 * @param label
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Get-Method for contact.
	 * 
	 * @return contact
	 */
	public Contact getContact() {
		return contact;
	}

	/**
	 * Set-Method for contact.
	 * 
	 * @param contact
	 */
	public void setContact(Contact contact) {
		this.contact = contact;
	}
}
