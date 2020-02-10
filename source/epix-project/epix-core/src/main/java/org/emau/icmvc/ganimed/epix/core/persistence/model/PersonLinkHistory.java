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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "personlink_history")
public class PersonLinkHistory implements Serializable {

	private static final long serialVersionUID = -8404003174989615687L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column
	private Long personLinkId;

	@OneToOne
	private Person srcPerson;

	@OneToOne
	private Person destPerson;

	@Column
	private Double threshold = 0.0;

	@Column
	private String algorithm;
	
	@Column
	private Timestamp timestamp;
	
	@OneToOne
	@JoinColumn(name = "updated_person")
	private Person updatedPerson;
	
	@OneToOne
	@JoinColumn(name = "from_group")
	private PersonGroup fromGroup;
	
	@OneToOne
	@JoinColumn(name = "to_group")
	private PersonGroup toGroup;
	
	@Column
	private String event;
	
	@Column
	private String explanation;

	public PersonLinkHistory() {

	}

	public PersonLinkHistory(Person srcPerson, Person destPerson) {
		super();
		this.srcPerson = srcPerson;
		this.destPerson = destPerson;
	}
	
	public PersonLinkHistory(PersonLink personlink) {
		this.personLinkId = personlink.getId();
		this.srcPerson = personlink.getSrcPerson();
		this.destPerson = personlink.getDestPerson();
		this.threshold = personlink.getThreshold();
		this.algorithm = personlink.getAlgorithm();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Person getSrcPerson() {
		return srcPerson;
	}

	public void setSrcPerson(Person srcPerson) {
		this.srcPerson = srcPerson;
	}

	public Person getDestPerson() {
		return destPerson;
	}

	public void setDestPerson(Person destPerson) {
		this.destPerson = destPerson;
	}

	public Double getThreshold() {
		return threshold;
	}

	public void setThreshold(Double threshold) {
		this.threshold = threshold;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PersonLinkHistory other = (PersonLinkHistory) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public Person getUpdatedPerson() {
		return updatedPerson;
	}

	public void setUpdatedPerson(Person updatedPerson) {
		this.updatedPerson = updatedPerson;
	}

	public PersonGroup getFromGroup() {
		return fromGroup;
	}

	public void setFromGroup(PersonGroup fromGroup) {
		this.fromGroup = fromGroup;
	}

	public PersonGroup getToGroup() {
		return toGroup;
	}

	public void setToGroup(PersonGroup toGroup) {
		this.toGroup = toGroup;
	}


	public Long getPersonLinkId() {
		return personLinkId;
	}

	public void setPersonLinkId(Long personLinkId) {
		this.personLinkId = personLinkId;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}
	
	@Override
	public String toString() {
		return "PersonLinkHistory [id=" + id + ", personLinkId=" + personLinkId
				+ ", srcPerson=" + srcPerson + ", destPerson=" + destPerson
				+ ", threshold=" + threshold + ", algorithm=" + algorithm
				+ ", timestamp=" + timestamp + ", updatedPerson="
				+ updatedPerson + ", fromGroup=" + fromGroup + ", toGroup="
				+ toGroup + ", event=" + event + ", explanation=" + explanation
				+ "]";
	}
}
