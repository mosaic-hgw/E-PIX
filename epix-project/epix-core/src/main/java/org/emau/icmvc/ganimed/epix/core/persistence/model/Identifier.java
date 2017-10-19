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
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.emau.icmvc.ganimed.epix.core.persistence.model.enums.IdentifierTypeEnum;

/**
 * 
 * @author Christian Schack
 * @since 08.11.2010
 */
@Entity
@IdClass(IdentifierId.class)
@Table(name = "identifier")
@NamedQueries({
		@NamedQuery(name = "Identifier.findByDomainId", query = "SELECT i FROM Identifier i join i.domain d WHERE d.oid = :oid"),
		@NamedQuery(name = "Identifier.findByPersonGroup", query = "SELECT i FROM Person p join p.localIdentifiers i WHERE p.personGroup = :pg GROUP BY i.value, i.domain"),
		@NamedQuery(name = "Identifier.findByPersonGroupAndDomain", query = "SELECT i FROM Person p join p.localIdentifiers i WHERE p.personGroup = :pg AND i.domain = :domain"),
		@NamedQuery(name = "Identifier.getLastMpiIdentifierByProject", query = "SELECT i FROM Identifier i, Project p join p.mpiDomain id WHERE p = :project AND i.domain = id ORDER BY i.value desc")
})
public class Identifier implements Serializable, Cloneable {

	private static final long serialVersionUID = 6278925726114024217L;

	//	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	//	private Long id; 

	@Id
	@ManyToOne(optional = false)
	private IdentifierDomain domain;

	@Id
	@Column(name = "local_identifier", nullable = false)
	private String value;

	@Column(name = "description")
	private String description;

	@Column(name = "sending_facility")
	private String sendingFacility;

	@Column(name = "sending_application")
	private String sendingApplication;

	@Column(columnDefinition = "BIT", length = 1)
	private boolean active = true;

//	@ManyToOne
//	@JoinColumn(name = "person_id", nullable = false)
//	private Person person;
	
	@ManyToMany(mappedBy = "localIdentifiers")
	private List<Person> persons;

	@Column(name = "entry_date", nullable = false)
	private Timestamp entryDate;

	@Column(name = "identifier_type")
	@Enumerated(EnumType.STRING)
	private IdentifierTypeEnum identifierType;

	//	public Long getId() {
	//		return id;
	//	}
	//
	//	public void setId(Long id) {
	//		this.id = id;
	//	}

	public IdentifierDomain getDomain() {
		return domain;
	}

	public void setDomain(IdentifierDomain domain) {
		this.domain = domain;
	}

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
	
	public List<Person> getPerson() {
		if(persons == null) {
			persons = new ArrayList<Person>();
		}
		return persons;
	}

	public void setPerson(List<Person> person) {
		this.persons = person;
	}

	public Timestamp getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(Timestamp entryDate) {
		this.entryDate = entryDate;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @return the identifierType
	 */
	public IdentifierTypeEnum getIdentifierType() {
		return identifierType;
	}

	/**
	 * @param identifierType
	 *            the identifierType to set
	 */
	public void setIdentifierType(IdentifierTypeEnum identifierType) {
		this.identifierType = identifierType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((domain == null) ? 0 : domain.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Identifier))
			return false;
		Identifier other = (Identifier) obj;
		if (domain == null) {
			if (other.domain != null)
				return false;
		} else if (!domain.equals(other.domain))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Identifier [domain=" + domain + ", value=" + value + ", description=" + description + ", sendingFacility=" + sendingFacility + ", sendingApplication="
				+ sendingApplication + ", active=" + active + ", entryDate=" + entryDate + ", identifierType=" + identifierType + "]";
	}

	@Override
	public Identifier clone() throws CloneNotSupportedException {
		return (Identifier) super.clone();
	}
}
