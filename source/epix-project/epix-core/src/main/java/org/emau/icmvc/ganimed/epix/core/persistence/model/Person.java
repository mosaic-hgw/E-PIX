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
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
import javax.persistence.Version;

@Entity
@Table(name = "person")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "person_type", discriminatorType = DiscriminatorType.STRING)
@NamedQueries({
		@NamedQuery(name = "Person.findByLastName", query = "SELECT p FROM Person p WHERE p.lastName = :name"),
		@NamedQuery(name = "Person.findByFirstNameAndLastNameAndDateOfBirth", query = "SELECT p FROM Person p WHERE p.lastName = :lastName AND p.firstName = :firstName AND p.birthDate = :birthDate"),
		@NamedQuery(name = "Person.findByLocalIdetifierAndDomain", query = "SELECT p FROM Person p join p.localIdentifiers i WHERE i.value = :ivalue AND i.domain.oid = :oid"),
		@NamedQuery(name = "Person.getAllPersons", query = "SELECT p FROM Person p"),
		@NamedQuery(name = "Person.findByPersonGroup", query = "SELECT p FROM Person p WHERE p.personGroup = :pg"),
		@NamedQuery(name = "Person.getPersonByID", query = "SELECT p FROM Person p WHERE p.id = :id"),
		@NamedQuery(name = "Person.getAllPersonMinusLinkedPersons", query = "SELECT p FROM Person p WHERE p.id NOT IN (SELECT pl.destPerson.id FROM PersonLink pl WHERE pl.destPerson.id = p.id)"),
		@NamedQuery(name = "Person.getPersonsByIds", query = "SELECT p FROM Person p WHERE p.id IN :ids") })
public class Person extends AbstractEntity implements Serializable, Cloneable {

	private static final long serialVersionUID = 8655678140056612704L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Version
	private Integer version;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "middle_name")
	private String middleName;

	@Column(name = "last_name")
	private String lastName;

	protected String prefix;

	protected String suffix;

	@Column(name = "date_of_birth")
	private Date birthDate;

	@Column(name = "gender")
	private String gender;

	@Column(name = "origin_date")
	private Timestamp originDate;

	@Column
	private Timestamp timestamp;

	@OneToMany(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "person_id", nullable = false)
	private List<Contact> contacts;

	// @OneToMany(cascade = CascadeType.ALL, targetEntity = Identifier.class,
	// mappedBy = "person")
	// private List<Identifier> localIdentifiers;

	@ManyToMany(fetch=FetchType.LAZY,cascade = CascadeType.ALL, targetEntity = Identifier.class)
	private List<Identifier> localIdentifiers;

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

	@ManyToOne(fetch=FetchType.LAZY, targetEntity = Source.class)
	private Source source;

	@ManyToOne(targetEntity = PersonGroup.class, optional = false)
	private PersonGroup personGroup;

	public Person() {
		this(null, null);
	}

	public Person(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.setTimestamp(new Timestamp(System.currentTimeMillis()));
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public List<Contact> getContacts() {
		if (contacts == null) {
			contacts = new ArrayList<Contact>();
		}
		return contacts;
	}

	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
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

	public List<Identifier> getLocalIdentifiers() {
		if (localIdentifiers == null) {
			localIdentifiers = new ArrayList<Identifier>();
		}
		return localIdentifiers;
	}

	public void setLocalIdentifiers(List<Identifier> localIdentifiers) {
		this.localIdentifiers = localIdentifiers;
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

	public PersonGroup getPersonGroup() {
		return personGroup;
	}

	public void setPersonGroup(PersonGroup personGroup) {
		this.personGroup = personGroup;
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
	public Object clone() throws CloneNotSupportedException {
		// MPIid, Contacts, Identifier
		Person clone = (Person) super.clone();
		if (contacts != null) {
			List<Contact> contactsClone = new ArrayList<Contact>();
			for (Contact contact : contacts) {
				contactsClone.add(contact.clone());
			}
			clone.setContacts(contactsClone);
		}
		if (localIdentifiers != null) {
			List<Identifier> localIdentifierClone = new ArrayList<Identifier>();
			for (Identifier identifier : localIdentifiers) {
				localIdentifierClone.add(identifier.clone());
			}
			clone.setLocalIdentifiers(localIdentifierClone);
		}
		return clone;
	}

	@Override
	public String toString() {
		return "Person [id=" + id + ", version=" + version + ", firstName=" + firstName + ", middleName=" + middleName
				+ ", lastName=" + lastName + ", prefix=" + prefix + ", suffix=" + suffix + ", birthDate=" + birthDate
				+ ", gender=" + gender + ", originDate=" + originDate + ", timestamp=" + timestamp + ", contacts="
				+ contacts + ", localIdentifiers=" + localIdentifiers + ", birthPlace=" + birthPlace + ", race=" + race
				+ ", religion=" + religion + ", mothersMaidenName=" + mothersMaidenName + ", degree=" + degree
				+ ", motherTongue=" + motherTongue + ", nationality=" + nationality + ", civilStatus=" + civilStatus
				+ ", value1=" + value1 + ", value2=" + value2 + ", value3=" + value3 + ", value4=" + value4
				+ ", value5=" + value5 + ", value6=" + value6 + ", value7=" + value7 + ", value8=" + value8
				+ ", value9=" + value9 + ", value10=" + value10 + ", source=" + source + ", personGroup=" + personGroup
				+ "]";
	}

}
