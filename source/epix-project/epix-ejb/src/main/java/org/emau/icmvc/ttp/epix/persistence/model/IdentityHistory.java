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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.emau.icmvc.ttp.epix.common.model.IdentifierDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityHistoryDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityInBaseDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityOutBaseDTO;
import org.emau.icmvc.ttp.epix.common.model.enums.Gender;
import org.emau.icmvc.ttp.epix.common.model.enums.IdentityHistoryEvent;

/**
 *
 * @author geidell
 *
 */
@Entity
@Table(name = "identity_history")
@TableGenerator(name = "identity_history_index", table = "sequence", initialValue = 0, allocationSize = 50)
@Cacheable(false)
@NamedQueries({
		@NamedQuery(name = "IdentityHistory.findByIdentity", query = "SELECT ih FROM IdentityHistory ih WHERE ih.identity = :identity"),
		@NamedQuery(name = "IdentityHistory.findByPerson", query = "SELECT ih FROM IdentityHistory ih WHERE ih.person = :person") })
public class IdentityHistory implements Serializable
{
	private static final long serialVersionUID = -6439011370851530330L;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "identity_history_index")
	private long id;
	@Column(name = "identity_version")
	private int identityVersion;
	@Column(name = "first_name")
	private String firstName;
	@Column(columnDefinition = "char(13)")
	@Enumerated(EnumType.STRING)
	private IdentityHistoryEvent event;
	@Column(name = "middle_name")
	private String middleName;
	@Column(name = "last_name")
	private String lastName;
	private String prefix;
	private String suffix;
	@Column(name = "date_of_birth")
	// @Temporal(TemporalType.DATE)
	private Date birthDate;
	@Column(name = "gender")
	private char gender;
	@Column(name = "external_timestamp")
	private Timestamp externalTimestamp;
	@Column(name = "history_timestamp", nullable = false)
	private Timestamp historyTimestamp;
	@Column(name = "birth_place")
	private String birthPlace;
	private String race;
	private String religion;
	@Column(name = "mothers_maiden_name")
	private String mothersMaidenName;
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
	private String comment;
	private double matchingScore;
	@ManyToMany
	@JoinTable(name = "identity_history_identifier", joinColumns = {
			@JoinColumn(name = "identity_history_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "identifiers_identifier_domain_name", referencedColumnName = "identifier_domain_name"),
					@JoinColumn(name = "identifiers_value", referencedColumnName = "value") })
	private List<Identifier> identifiers = new ArrayList<>();
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "identity_id", nullable = false)
	private Identity identity;
	@ManyToOne(cascade = CascadeType.MERGE, optional = false)
	@JoinColumn(name = "source_name", nullable = false)
	private Source source;
	@ManyToOne(cascade = CascadeType.MERGE, optional = false)
	private Person person;

	public IdentityHistory()
	{
		historyTimestamp = new Timestamp(System.currentTimeMillis());
	}

	public IdentityHistory(Identity identity, IdentityHistoryEvent event, String comment, double matchingScore, Timestamp timestamp)
	{
		identityVersion = identity.getVersion();
		firstName = getValidString(identity.getFirstName());
		middleName = getValidString(identity.getMiddleName());
		lastName = getValidString(identity.getLastName());
		prefix = getValidString(identity.getPrefix());
		suffix = getValidString(identity.getSuffix());
		gender = identity.getGender();
		birthDate = identity.getBirthDate() != null ? new Date(identity.getBirthDate().getTime()) : null;
		externalTimestamp = identity.getExternalTimestamp() == null ? null : new Timestamp(identity.getExternalTimestamp().getTime());
		birthPlace = identity.getBirthPlace();
		race = identity.getRace();
		religion = identity.getReligion();
		mothersMaidenName = identity.getMothersMaidenName();
		degree = identity.getDegree();
		motherTongue = identity.getMotherTongue();
		nationality = identity.getNationality();
		civilStatus = identity.getCivilStatus();
		value1 = identity.getValue1();
		value2 = identity.getValue2();
		value3 = identity.getValue3();
		value4 = identity.getValue4();
		value5 = identity.getValue5();
		value6 = identity.getValue6();
		value7 = identity.getValue7();
		value8 = identity.getValue8();
		value9 = identity.getValue9();
		value10 = identity.getValue10();
		this.identity = identity;
		source = identity.getSource();
		person = identity.getPerson();
		forcedReference = identity.isForcedReference();
		deactivated = identity.isDeactivated();

		identifiers.addAll(identity.getIdentifiers());
		historyTimestamp = timestamp;
		this.event = event;
		this.comment = comment;
		this.matchingScore = matchingScore;
	}

	private String getValidString(String obj)
	{
		return obj != null ? obj : "";
	}

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public int getIdentityVersion()
	{
		return identityVersion;
	}

	public void setIdentityVersion(int identityVersion)
	{
		this.identityVersion = identityVersion;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public IdentityHistoryEvent getEvent()
	{
		return event;
	}

	public void setEvent(IdentityHistoryEvent event)
	{
		this.event = event;
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

	public Timestamp getHistoryTimestamp()
	{
		return historyTimestamp;
	}

	public void setHistoryTimestamp(Timestamp historyTimestamp)
	{
		this.historyTimestamp = historyTimestamp;
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

	public String getComment()
	{
		return comment;
	}

	public void setComment(String comment)
	{
		this.comment = comment;
	}

	public double getMatchingScore()
	{
		return matchingScore;
	}

	public void setMatchingScore(double matchingScore)
	{
		this.matchingScore = matchingScore;
	}

	public List<Identifier> getIdentifiers()
	{
		return identifiers;
	}

	public void setIdentifiers(List<Identifier> identifiers)
	{
		this.identifiers = identifiers;
	}

	public Identity getIdentity()
	{
		return identity;
	}

	public void setIdentity(Identity identity)
	{
		this.identity = identity;
	}

	public Source getSource()
	{
		return source;
	}

	public void setSource(Source source)
	{
		this.source = source;
	}

	public Person getPerson()
	{
		return person;
	}

	public void setPerson(Person person)
	{
		this.person = person;
	}

	public IdentityHistoryDTO toDTO()
	{
		List<IdentifierDTO> identifierDTOs = new ArrayList<>();
		for (Identifier identifier : identifiers)
		{
			identifierDTOs.add(identifier.toDTO());
		}
		IdentityInBaseDTO inBase = new IdentityInBaseDTO(firstName, middleName, lastName, prefix, suffix,
				gender == ' ' || gender == Character.MIN_VALUE ? null : Gender.fromValue(gender), birthDate, identifierDTOs, birthPlace, race, religion, mothersMaidenName, degree,
				motherTongue, nationality, civilStatus, externalTimestamp == null ? null : new Date(externalTimestamp.getTime()), value1, value2,
				value3, value4, value5, value6, value7, value8, value9, value10);
		IdentityOutBaseDTO outBase = new IdentityOutBaseDTO(inBase, identity.getId(), identityVersion, person.getId(), source.toDTO(), deactivated,
				new Date(identity.getCreateTimestamp().getTime()), new Date(identity.getTimestamp().getTime()));
		return new IdentityHistoryDTO(outBase, id, new Date(historyTimestamp.getTime()), event, matchingScore, comment);
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
		IdentityHistory other = (IdentityHistory) obj;
		if (id != other.id)
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		return "IdentityHistory [id=" + id + ", identityVersion=" + identityVersion + ", forcedReference=" + forcedReference + ", deactivated="
				+ deactivated + ", comment=" + comment + ", matchingScore=" + matchingScore + ", identifiers="
				+ (identifiers == null ? "null" : identifiers.stream().map(Object::toString).collect(Collectors.joining(", "))) + ", identityId="
				+ (identity == null ? "null" : identity.getId()) + ", source=" + source + ", person=" + person + "]";
	}

	public String toLongString()
	{
		return "IdentityHistory [id=" + id + ", identityVersion=" + identityVersion + ", firstName=" + firstName + ", event=" + event
				+ ", middleName=" + middleName + ", lastName=" + lastName + ", prefix=" + prefix + ", suffix=" + suffix + ", birthDate=" + birthDate
				+ ", gender=" + gender + ", externalTimestamp=" + externalTimestamp + ", historyTimestamp=" + historyTimestamp + ", birthPlace="
				+ birthPlace + ", race=" + race + ", religion=" + religion + ", mothersMaidenName=" + mothersMaidenName + ", degree=" + degree
				+ ", motherTongue=" + motherTongue + ", nationality=" + nationality + ", civilStatus=" + civilStatus + ", value1=" + value1
				+ ", value2=" + value2 + ", value3=" + value3 + ", value4=" + value4 + ", value5=" + value5 + ", value6=" + value6 + ", value7="
				+ value7 + ", value8=" + value8 + ", value9=" + value9 + ", value10=" + value10 + ", forcedReference=" + forcedReference
				+ ", deactivated=" + deactivated + ", comment=" + comment + ", matchingScore=" + matchingScore + ", identifiers="
				+ (identifiers == null ? "null" : identifiers.stream().map(Object::toString).collect(Collectors.joining(", "))) + ", identityId="
				+ (identity == null ? "null" : identity.getId()) + ", source=" + source + ", personId=" + (person == null ? "null" : person.getId())
				+ "]";
	}
}
