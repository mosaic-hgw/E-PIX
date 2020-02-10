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
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "person_history")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "person_type", discriminatorType = DiscriminatorType.STRING)
@NamedQueries(@NamedQuery(name = "PersonHistory.getByPerson", query = "SELECT ph from PersonHistory ph where ph.person = :person"))
public class PersonHistory extends AbstractEntity implements Serializable {

	private static final long serialVersionUID = -1514078993954051069L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "first_name")
	private String firstName;

	private String originalEvent;

	@Column(name = "middle_name")
	private String middleName;

	@Column(name = "last_name")
	private String lastName;

	protected String prefix;

	protected String suffix;

	@Column(name = "date_of_birth")
	// @Temporal(TemporalType.DATE)
	private Date birthDate;

	@Column(name = "gender")
	private String gender;

	@Column(name = "origin_date")
	private Timestamp originDate;

	@Column
	private Timestamp timestamp;

	@ManyToMany
	private List<Identifier> identifier;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "person_history_id", nullable = false)
	private List<ContactHistory> contactHistory;

	protected String birthPlace;

	protected String race;

	protected String religion;

	protected String mothersMaidenName;

	private String degree;

	private String motherTongue;

	private String nationality;

	private String civilStatus;

	private String value1;
	
	private String value2;
	
	private String value3;
	
	private String value4;
	
	private String value5;
	
	private String value6;
	
	private String value7;
	
	private String value8;
	
	private String value9;
	
	private String value10;

	@ManyToOne(targetEntity = Person.class)
	private Person person;
	
	@ManyToOne(targetEntity = Source.class)
	private Source source;

	@Column(name = "person_group_id", nullable = false)
	private Long personGroupId;

	public enum EVENT {
		NEW("new"), UPDATE("update"), MERGE("merge"), MATCH("match"), PERFECTMATCH("perfectMatch");

		@SuppressWarnings("unused")
		private String value;

		private EVENT(String value) {
			this.value = value;
		}
	}

	public PersonHistory() {
	}

	public PersonHistory(Person person) {
		firstName = getValidString(person.getFirstName());
		middleName = getValidString(person.getMiddleName());
		lastName = getValidString(person.getLastName());
		prefix = getValidString(person.getPrefix());
		suffix = getValidString(person.getSuffix());
		gender = getValidString(person.getGender());
		birthDate = (person.getBirthDate() != null ? new Date(person.getBirthDate().getTime()) : null);
		originDate = person.getOriginDate();
		timestamp = person.getTimestamp();
		birthPlace = person.getBirthPlace();
		race = person.getRace();
		religion = person.getReligion();
		mothersMaidenName = person.getMothersMaidenName();
		degree = person.getDegree();
		motherTongue = person.getMotherTongue();
		nationality = person.getNationality();
		civilStatus = person.getCivilStatus();
		value1 = person.getValue1();
		value2 = person.getValue2();
		value3 = person.getValue3();
		value4 = person.getValue4();
		value5 = person.getValue5();
		value6 = person.getValue6();
		value7 = person.getValue7();
		value8 = person.getValue8();
		value9 = person.getValue9();
		value10 = person.getValue10();
		this.person = person;
		source = person.getSource();
		personGroupId = person.getPersonGroup().getId();

		if (!person.getLocalIdentifiers().isEmpty() && person.getLocalIdentifiers() != null) {
			List<Identifier> idents = person.getLocalIdentifiers();
			if (identifier == null) {
				identifier = new ArrayList<Identifier>();
			}
			for (Identifier ident : idents) {
				identifier.add(ident);
			}
		}
		if (!person.getContacts().isEmpty() && person.getContacts() != null) {
			List<Contact> contacts = person.getContacts();
			if (contactHistory == null) {
				contactHistory = new ArrayList<ContactHistory>();
			}
			for (Contact contact : contacts) {
				contactHistory.add(new ContactHistory(contact));
			}
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOriginalEvent() {
		return originalEvent;
	}

	public void setOriginalEvent(String originalEvent) {
		this.originalEvent = originalEvent;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public Timestamp getOriginDate() {
		return originDate;
	}

	public void setOriginDate(Timestamp originDate) {
		this.originDate = originDate;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public List<Identifier> getIdentifier() {
		if (identifier == null) {
			identifier = new ArrayList<Identifier>();
		}
		return identifier;
	}

	public void setIdentifier(List<Identifier> identifier) {
		this.identifier = identifier;
	}

	public String getBirthPlace() {
		return birthPlace;
	}

	public void setBirthPlace(String birthPlace) {
		this.birthPlace = birthPlace;
	}

	public String getRace() {
		return race;
	}

	public void setRace(String race) {
		this.race = race;
	}

	public String getReligion() {
		return religion;
	}

	public void setReligion(String religion) {
		this.religion = religion;
	}

	public String getMothersMaidenName() {
		return mothersMaidenName;
	}

	public void setMothersMaidenName(String mothersMaidenName) {
		this.mothersMaidenName = mothersMaidenName;
	}

	public String getDegree() {
		return degree;
	}

	public void setDegree(String degree) {
		this.degree = degree;
	}

	public String getMotherTongue() {
		return motherTongue;
	}

	public void setMotherTongue(String motherTongue) {
		this.motherTongue = motherTongue;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getCivilStatus() {
		return civilStatus;
	}

	public void setCivilStatus(String civilStatus) {
		this.civilStatus = civilStatus;
	}

	public List<ContactHistory> getContactHistory() {
		return contactHistory;
	}

	public void setContactHistory(List<ContactHistory> contactHistory) {
		this.contactHistory = contactHistory;
	}

	public Long getPersonGroupId() {
		return personGroupId;
	}

	public void setPersonGroupId(Long personGroupId) {
		this.personGroupId = personGroupId;
	}

	public String getValue1() {
		return value1;
	}

	public void setValue1(String value1) {
		this.value1 = value1;
	}

	public String getValue2() {
		return value2;
	}

	public void setValue2(String value2) {
		this.value2 = value2;
	}

	public String getValue3() {
		return value3;
	}

	public void setValue3(String value3) {
		this.value3 = value3;
	}

	public String getValue4() {
		return value4;
	}

	public void setValue4(String value4) {
		this.value4 = value4;
	}

	public String getValue5() {
		return value5;
	}

	public void setValue5(String value5) {
		this.value5 = value5;
	}

	public String getValue6() {
		return value6;
	}

	public void setValue6(String value6) {
		this.value6 = value6;
	}

	public String getValue7() {
		return value7;
	}

	public void setValue7(String value7) {
		this.value7 = value7;
	}

	public String getValue8() {
		return value8;
	}

	public void setValue8(String value8) {
		this.value8 = value8;
	}

	public String getValue9() {
		return value9;
	}

	public void setValue9(String value9) {
		this.value9 = value9;
	}

	public String getValue10() {
		return value10;
	}

	public void setValue10(String value10) {
		this.value10 = value10;
	}
	
	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}
	
	@Override
	public String toString() {
		return "PersonHistory [id=" + id + ", firstName=" + firstName + ", originalEvent=" + originalEvent
				+ ", middleName=" + middleName + ", lastName=" + lastName + ", prefix=" + prefix + ", suffix=" + suffix
				+ ", birthDate=" + birthDate + ", gender=" + gender + ", originDate=" + originDate + ", timestamp="
				+ timestamp + ", identifier=" + identifier + ", contactHistory=" + contactHistory + ", birthPlace="
				+ birthPlace + ", race=" + race + ", religion=" + religion + ", mothersMaidenName=" + mothersMaidenName
				+ ", degree=" + degree + ", motherTongue=" + motherTongue + ", nationality=" + nationality
				+ ", civilStatus=" + civilStatus + ", value1=" + value1 + ", value2=" + value2 + ", value3=" + value3
				+ ", value4=" + value4 + ", value5=" + value5 + ", value6=" + value6 + ", value7=" + value7
				+ ", value8=" + value8 + ", value9=" + value9 + ", value10=" + value10 + ", person=" + person
				+ ", source=" + source + ", personGroupId=" + personGroupId + "]";
	}

}
