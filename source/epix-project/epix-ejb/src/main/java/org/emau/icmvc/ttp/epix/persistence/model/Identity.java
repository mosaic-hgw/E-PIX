package org.emau.icmvc.ttp.epix.persistence.model;

/*-
 * ###license-information-start###
 * E-PIX - Enterprise Patient Identifier Cross-referencing
 * __
 * Copyright (C) 2009 - 2022 Trusted Third Party of the University Medicine Greifswald
 * 							kontakt-ths@uni-greifswald.de
 * 
 * 							concept and implementation
 * 							l.geidel,c.schack, d.langner, g.koetzschke
 * 
 * 							web client
 * 							a.blumentritt, f.m. moser
 * 
 * 							docker
 * 							r.schuldt
 * 
 * 							privacy preserving record linkage (PPRL)
 * 							c.hampf
 * 
 * 							please cite our publications
 * 							http://dx.doi.org/10.3414/ME14-01-0133
 * 							http://dx.doi.org/10.1186/s12967-015-0545-6
 * 							https://translational-medicine.biomedcentral.com/articles/10.1186/s12967-020-02257-4
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
import java.util.stream.Collectors;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

import org.emau.icmvc.ttp.epix.common.model.ContactOutDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentifierDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityInBaseDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityOutBaseDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityOutDTO;
import org.emau.icmvc.ttp.epix.common.model.enums.Gender;

/**
 *
 * @author geidell
 *
 */
@Entity
@Table(name = "identity")
@TableGenerator(name = "identity_index", table = "sequence", initialValue = 0, allocationSize = 50)
@Cacheable(false)
@NamedQueries({
		@NamedQuery(name = "Identity.findByIdentifier", query = "SELECT i FROM Identity i JOIN i.person p JOIN i.identifiers li WHERE li.value = :value AND li.identifierDomain = :identifierDomain AND p.domain = :domain AND i.deactivated = false"),
		@NamedQuery(name = "Identity.findByPerson", query = "SELECT i FROM Identity i WHERE i.person = :person AND i.deactivated = false"),
		@NamedQuery(name = "Identity.findByIds", query = "SELECT i FROM Identity i WHERE i.id IN :ids AND i.deactivated = false"),
		@NamedQuery(name = "Identity.findDeactivatedByDomain", query = "SELECT i FROM Identity i JOIN i.person p WHERE i.deactivated = true AND p.domain = :domain") })
public class Identity implements Serializable
{
	private static final long serialVersionUID = 2307144354821091923L;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "identity_index")
	private long id;
	@Version
	private int version;
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
	private char gender;
	@Column(name = "external_timestamp")
	private Timestamp externalTimestamp;
	@Column(name = "create_timestamp", nullable = false)
	private Timestamp createTimestamp;
	@Column(nullable = false)
	private Timestamp timestamp;
	@Column(name = "birth_place")
	protected String birthPlace;
	protected String race;
	protected String religion;
	@Column(name = "mothers_maiden_name")
	protected String mothersMaidenName;
	private String degree;
	@Column(name = "mother_tongue")
	private String motherTongue;
	private String nationality;
	@Column(name = "civil_status")
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
	@Column(name = "forced_reference", columnDefinition = "BIT", length = 1)
	private boolean forcedReference;
	@Column(columnDefinition = "BIT", length = 1)
	private boolean deactivated;

	@OneToMany(mappedBy = "identity", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Contact> contacts = new ArrayList<>();
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinTable(name = "identity_identifier", joinColumns = { @JoinColumn(name = "identity_id", referencedColumnName = "id") }, inverseJoinColumns = {
			@JoinColumn(name = "identifiers_identifier_domain_name", referencedColumnName = "identifier_domain_name"),
			@JoinColumn(name = "identifiers_value", referencedColumnName = "value") })
	private List<Identifier> identifiers = new ArrayList<>();
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE, optional = false)
	@JoinColumn(name = "source_name", nullable = false)
	private Source source;
	@ManyToOne(cascade = CascadeType.MERGE, optional = false)
	private Person person;

	public Identity()
	{
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		this.createTimestamp = timestamp;
		this.timestamp = timestamp;
		this.version = 0;
	}

	public Identity(IdentityInBaseDTO dto, List<Identifier> identifiers, List<Contact> contacts, Source source, Person person,
			boolean forcedReference, Timestamp timestamp)
	{
		this.firstName = dto.getFirstName();
		this.middleName = dto.getMiddleName();
		this.lastName = dto.getLastName();
		this.prefix = dto.getPrefix();
		this.suffix = dto.getSuffix();
		this.birthDate = dto.getBirthDate();
		this.gender = dto.getGender() == null ? ' ' : dto.getGender().getValue();
		this.externalTimestamp = dto.getExternalDate() != null ? new Timestamp(dto.getExternalDate().getTime()) : null;
		this.createTimestamp = timestamp;
		this.timestamp = timestamp;
		this.birthPlace = dto.getBirthPlace();
		this.race = dto.getRace();
		this.religion = dto.getReligion();
		this.mothersMaidenName = dto.getMothersMaidenName();
		this.degree = dto.getDegree();
		this.motherTongue = dto.getMotherTongue();
		this.nationality = dto.getNationality();
		this.civilStatus = dto.getCivilStatus();
		this.value1 = dto.getValue1();
		this.value2 = dto.getValue2();
		this.value3 = dto.getValue3();
		this.value4 = dto.getValue4();
		this.value5 = dto.getValue5();
		this.value6 = dto.getValue6();
		this.value7 = dto.getValue7();
		this.value8 = dto.getValue8();
		this.value9 = dto.getValue9();
		this.value10 = dto.getValue10();
		this.identifiers.addAll(identifiers);
		this.source = source;
		this.person = person;
		this.forcedReference = forcedReference;
		addContacts(contacts);
		this.version = 0;
	}

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public int getVersion()
	{
		return version;
	}

	public void setVersion(int version)
	{
		this.version = version;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getMiddleName()
	{
		return middleName;
	}

	public void setMiddleName(String middleName)
	{
		this.middleName = middleName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public String getPrefix()
	{
		return prefix;
	}

	public void setPrefix(String prefix)
	{
		this.prefix = prefix;
	}

	public String getSuffix()
	{
		return suffix;
	}

	public void setSuffix(String suffix)
	{
		this.suffix = suffix;
	}

	public List<Contact> getContacts()
	{
		return contacts;
	}

	public void setContacts(List<Contact> contacts)
	{
		this.contacts = contacts;
	}

	public Date getBirthDate()
	{
		return birthDate;
	}

	public void setBirthDate(Date birthDate)
	{
		this.birthDate = birthDate;
	}

	public char getGender()
	{
		return gender;
	}

	public void setGender(char gender)
	{
		this.gender = gender;
	}

	public Timestamp getExternalTimestamp()
	{
		return externalTimestamp;
	}

	public void setExternalTimestamp(Timestamp externalTimestamp)
	{
		this.externalTimestamp = externalTimestamp;
	}

	public Timestamp getCreateTimestamp()
	{
		return createTimestamp;
	}

	public void setCreateTimestamp(Timestamp createTimestamp)
	{
		this.createTimestamp = createTimestamp;
	}

	public Timestamp getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp)
	{
		this.timestamp = timestamp;
	}

	public List<Identifier> getIdentifiers()
	{
		return identifiers;
	}

	public void setIdentifiers(List<Identifier> identifiers)
	{
		this.identifiers = identifiers;
	}

	public String getBirthPlace()
	{
		return birthPlace;
	}

	public void setBirthPlace(String birthPlace)
	{
		this.birthPlace = birthPlace;
	}

	public String getRace()
	{
		return race;
	}

	public void setRace(String race)
	{
		this.race = race;
	}

	public String getReligion()
	{
		return religion;
	}

	public void setReligion(String religion)
	{
		this.religion = religion;
	}

	public String getMothersMaidenName()
	{
		return mothersMaidenName;
	}

	public void setMothersMaidenName(String mothersMaidenName)
	{
		this.mothersMaidenName = mothersMaidenName;
	}

	public String getDegree()
	{
		return degree;
	}

	public void setDegree(String degree)
	{
		this.degree = degree;
	}

	public String getMotherTongue()
	{
		return motherTongue;
	}

	public void setMotherTongue(String motherTongue)
	{
		this.motherTongue = motherTongue;
	}

	public String getNationality()
	{
		return nationality;
	}

	public void setNationality(String nationality)
	{
		this.nationality = nationality;
	}

	public String getCivilStatus()
	{
		return civilStatus;
	}

	public void setCivilStatus(String civilStatus)
	{
		this.civilStatus = civilStatus;
	}

	public Person getPerson()
	{
		return person;
	}

	public void setPerson(Person person)
	{
		this.person = person;
	}

	public String getValue1()
	{
		return value1;
	}

	public void setValue1(String value1)
	{
		this.value1 = value1;
	}

	public String getValue2()
	{
		return value2;
	}

	public void setValue2(String value2)
	{
		this.value2 = value2;
	}

	public String getValue3()
	{
		return value3;
	}

	public void setValue3(String value3)
	{
		this.value3 = value3;
	}

	public String getValue4()
	{
		return value4;
	}

	public void setValue4(String value4)
	{
		this.value4 = value4;
	}

	public String getValue5()
	{
		return value5;
	}

	public void setValue5(String value5)
	{
		this.value5 = value5;
	}

	public String getValue6()
	{
		return value6;
	}

	public void setValue6(String value6)
	{
		this.value6 = value6;
	}

	public String getValue7()
	{
		return value7;
	}

	public void setValue7(String value7)
	{
		this.value7 = value7;
	}

	public String getValue8()
	{
		return value8;
	}

	public void setValue8(String value8)
	{
		this.value8 = value8;
	}

	public String getValue9()
	{
		return value9;
	}

	public void setValue9(String value9)
	{
		this.value9 = value9;
	}

	public String getValue10()
	{
		return value10;
	}

	public void setValue10(String value10)
	{
		this.value10 = value10;
	}

	public Source getSource()
	{
		return source;
	}

	public void setSource(Source source)
	{
		this.source = source;
	}

	public boolean isForcedReference()
	{
		return forcedReference;
	}

	public void setForcedReference(boolean forcedReference)
	{
		this.forcedReference = forcedReference;
	}

	public boolean isDeactivated()
	{
		return deactivated;
	}

	public void setDeactivated(boolean deactivated)
	{
		this.deactivated = deactivated;
	}

	public boolean isPossibleReference()
	{
		return forcedReference || person.getDomain().getSafeSource().equals(source);
	}

	public void update(Identity newIdentity, Timestamp timestamp)
	{
		this.firstName = newIdentity.getFirstName();
		this.middleName = newIdentity.getMiddleName();
		this.lastName = newIdentity.getLastName();
		this.prefix = newIdentity.getPrefix();
		this.suffix = newIdentity.getSuffix();
		this.birthDate = newIdentity.getBirthDate();
		this.gender = newIdentity.getGender();
		this.externalTimestamp = newIdentity.getExternalTimestamp();
		this.timestamp = timestamp;
		this.person.setTimestamp(timestamp);
		this.birthPlace = newIdentity.getBirthPlace();
		this.race = newIdentity.getRace();
		this.religion = newIdentity.getReligion();
		this.mothersMaidenName = newIdentity.getMothersMaidenName();
		this.degree = newIdentity.getDegree();
		this.motherTongue = newIdentity.getMotherTongue();
		this.nationality = newIdentity.getNationality();
		this.civilStatus = newIdentity.getCivilStatus();
		this.value1 = newIdentity.getValue1();
		this.value2 = newIdentity.getValue2();
		this.value3 = newIdentity.getValue3();
		this.value4 = newIdentity.getValue4();
		this.value5 = newIdentity.getValue5();
		this.value6 = newIdentity.getValue6();
		this.value7 = newIdentity.getValue7();
		this.value8 = newIdentity.getValue8();
		this.value9 = newIdentity.getValue9();
		this.value10 = newIdentity.getValue10();
		for (Identifier ident : newIdentity.getIdentifiers())
		{
			if (!identifiers.contains(ident))
			{
				identifiers.add(ident);
			}
		}
		this.source = newIdentity.getSource();
		this.forcedReference = newIdentity.isForcedReference();
		addContacts(newIdentity.getContacts());
	}

	public void addContacts(List<Contact> possibleNewContacts)
	{
		// nicht contains, da die equals-methode nur ueber die id geht!
		for (Contact c : possibleNewContacts)
		{
			boolean contactIsNew = true;
			for (Contact existingContact : contacts)
			{
				if (existingContact.equalContent(c))
				{
					contactIsNew = false;
					break;
				}
			}
			if (contactIsNew)
			{
				contacts.add(c);
				c.setIdentity(this);
			}
		}
	}

	public IdentityOutDTO toDTO()
	{
		List<ContactOutDTO> cList = new ArrayList<>();
		for (Contact c : contacts)
		{
			cList.add(c.toDTO());
		}
		List<IdentifierDTO> iList = new ArrayList<>();
		for (Identifier i : identifiers)
		{
			iList.add(i.toDTO());
		}
		IdentityInBaseDTO inBase = new IdentityInBaseDTO(firstName, middleName, lastName, prefix, suffix,
				gender == ' ' || gender == Character.MIN_VALUE ? null : Gender.fromValue(gender), birthDate, iList, birthPlace, race, religion, mothersMaidenName, degree,
				motherTongue, nationality, civilStatus, externalTimestamp == null ? null : new Date(externalTimestamp.getTime()), value1, value2,
				value3, value4, value5, value6, value7, value8, value9, value10);
		IdentityOutBaseDTO outBase = new IdentityOutBaseDTO(inBase, id, version, person.getId(), source.toDTO(), deactivated,
				createTimestamp == null ? null : new Date(createTimestamp.getTime()), timestamp == null ? null : new Date(timestamp.getTime()));
		return new IdentityOutDTO(outBase, cList);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ id >>> 32);
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		Identity other = (Identity) obj;
		if (id != other.id)
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		return "Identity [id=" + id + ", version=" + version + ", personId=" + (person == null ? "null" : person.getId()) + ", externalTimestamp="
				+ externalTimestamp + ", createTimestamp=" + createTimestamp + ", timestamp=" + timestamp + ", identifiers="
				+ (identifiers == null ? "null" : identifiers.stream().map(Object::toString).collect(Collectors.joining(", "))) + ", source=" + source
				+ "]";
	}

	public String toLongString()
	{
		return "Identity [id=" + id + ", version=" + version + ", firstName=" + firstName + ", middleName=" + middleName + ", lastName=" + lastName
				+ ", prefix=" + prefix + ", suffix=" + suffix + ", birthDate=" + birthDate + ", gender=" + gender + ", externalTimestamp="
				+ externalTimestamp + ", createTimestamp=" + createTimestamp + ", timestamp=" + timestamp + ", birthPlace=" + birthPlace + ", race="
				+ race + ", religion=" + religion + ", mothersMaidenName=" + mothersMaidenName + ", degree=" + degree + ", motherTongue="
				+ motherTongue + ", nationality=" + nationality + ", civilStatus=" + civilStatus + ", value1=" + value1 + ", value2=" + value2
				+ ", value3=" + value3 + ", value4=" + value4 + ", value5=" + value5 + ", value6=" + value6 + ", value7=" + value7 + ", value8="
				+ value8 + ", value9=" + value9 + ", value10=" + value10 + ", forcedReference=" + forcedReference + ", deactivated=" + deactivated
				+ ", contacts=" + (contacts == null ? "null" : contacts.stream().map(Contact::toLongString).collect(Collectors.joining(", ")))
				+ ", identifiers=" + (identifiers == null ? "null" : identifiers.stream().map(Object::toString).collect(Collectors.joining(", ")))
				+ ", source=" + source + ", personId=" + (person == null ? "null" : person.getId()) + "]";
	}
}
