package org.emau.icmvc.ganimed.epix.core.persistence.model;

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


import java.io.Serializable;


import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 
 * @author Christian Schack
 * @since 08.11.2010
 */
//@Entity
//@Table(name="identifier_history")
public class IdentifierHistory extends AbstractEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8517530769940089554L;
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id; 
	
	@Column(name="local_identifier", nullable=false)
	private String value;
	
	@Column(name="description")
	private String description;
	
	@Column(name="sending_facility")
	private String sendingFacility;
	
	@Column(name="sending_application")
	private String sendingApplication;
			
//	private PersonHistory personHistory;
	
	@Column(name="entry_date", nullable=false)
	private Timestamp entryDate;
	
	public IdentifierHistory() {	
	}
	
	public IdentifierHistory(Identifier identifier) {
		value = getValidString(identifier.getValue());
		description = getValidString(identifier.getDescription());
		sendingFacility = getValidString(identifier.getSendingFacility());
		sendingApplication = getValidString(identifier.getSendingApplication());			
//		domain = identifier.getDomain();
		entryDate = identifier.getEntryDate();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

//	public IdentifierDomain getDomain() {
//		return domain;
//	}
//
//	public void setDomain(IdentifierDomain domain) {
//		this.domain = domain;
//	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getSendingFacility() {
		return sendingFacility;
	}

	public void setSendingFacility(String sendingFacility) {
		this.sendingFacility = sendingFacility;
	}

	public String getSendingApplication() {
		return sendingApplication;
	}

	public void setSendingApplication(String sendingApplication) {
		this.sendingApplication = sendingApplication;
	}

//	@ManyToOne(optional=false, cascade=CascadeType.ALL, targetEntity=PersonHistory.class)
//	public PersonHistory getPersonHistory() {
//		return personHistory;
//	}
//
//	public void setPersonHistory(PersonHistory personHistory) {
//		this.personHistory = personHistory;
//	}

	public Timestamp getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(Timestamp entryDate) {
		this.entryDate = entryDate;
	} 

}
