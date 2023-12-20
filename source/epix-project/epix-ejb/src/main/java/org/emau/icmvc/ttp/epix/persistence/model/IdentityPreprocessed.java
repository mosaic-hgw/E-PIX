package org.emau.icmvc.ttp.epix.persistence.model;

/*-
 * ###license-information-start###
 * E-PIX - Enterprise Patient Identifier Cross-referencing
 * __
 * Copyright (C) 2009 - 2023 Trusted Third Party of the University Medicine Greifswald
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author geidell
 *
 */
@Entity
@Table(name = "identity_preprocessed")
@Cacheable(false)
public class IdentityPreprocessed implements Serializable
{
	private static final long serialVersionUID = 9183002566496129120L;
	public static final transient String DATE_FORMAT = "yyyyMMdd";
	private static final transient DateFormat df = new SimpleDateFormat(DATE_FORMAT);
	private static final transient String EMPTY_STRING = "";
	/**
	 * achtung! neue felder (sofern moegliche matchfelder) auch zu @link{FieldName} hinzufuegen<br>
	 * ebenso zu @link{PreprocessedCacheObject} (getFieldValue(...))
	 */
	@Id
	@Column(name = "identity_id")
	private long identityId;
	@Column(name = "person_id")
	private long personId;
	@Column(name = "domain_name")
	private String domainName;
	@Column(name = "first_name")
	private String firstName;
	@Column(name = "middle_name")
	private String middleName;
	@Column(name = "last_name")
	private String lastName;
	private String prefix;
	private String suffix;
	@Column(name = "date_of_birth", columnDefinition = "char(8)")
	private String birthDate;
	@Column(name = "gender")
	private char gender;
	@Column(name = "external_timestamp")
	private Timestamp externalTimestamp;
	@Column
	private Timestamp timestamp;
	@Column(name = "create_timestamp")
	private Timestamp createTimestamp;
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

	public IdentityPreprocessed()
	{}

	public IdentityPreprocessed(Identity identity)
	{
		this.firstName = getValidString(identity.getFirstName());
		this.middleName = getValidString(identity.getMiddleName());
		this.lastName = getValidString(identity.getLastName());
		this.prefix = getValidString(identity.getPrefix());
		this.suffix = getValidString(identity.getSuffix());
		this.gender = identity.getGender();
		synchronized (df)
		{
			this.birthDate = identity.getBirthDate() != null ? df.format(identity.getBirthDate()).intern() : EMPTY_STRING;
		}
		this.externalTimestamp = identity.getExternalTimestamp() == null ? null : new Timestamp(identity.getExternalTimestamp().getTime());
		this.birthPlace = getValidString(identity.getBirthPlace());
		this.race = getValidString(identity.getRace());
		this.religion = getValidString(identity.getReligion());
		this.mothersMaidenName = getValidString(identity.getMothersMaidenName());
		this.degree = getValidString(identity.getDegree());
		this.motherTongue = getValidString(identity.getMotherTongue());
		this.nationality = getValidString(identity.getNationality());
		this.civilStatus = getValidString(identity.getCivilStatus());
		this.timestamp = new Timestamp(identity.getTimestamp().getTime());
		this.createTimestamp = new Timestamp(identity.getCreateTimestamp().getTime());
		this.value1 = getValidString(identity.getValue1());
		this.value2 = getValidString(identity.getValue2());
		this.value3 = getValidString(identity.getValue3());
		this.value4 = getValidString(identity.getValue4());
		this.value5 = getValidString(identity.getValue5());
		this.value6 = getValidString(identity.getValue6());
		this.value7 = getValidString(identity.getValue7());
		this.value8 = getValidString(identity.getValue8());
		this.value9 = getValidString(identity.getValue9());
		this.value10 = getValidString(identity.getValue10());
		this.identityId = identity.getId();
		if (identity.getPerson() != null)
		{
			this.personId = identity.getPerson().getId();
			this.domainName = getValidString(identity.getPerson().getDomain().getName());
		}
		this.forcedReference = identity.isForcedReference();
		this.deactivated = identity.isDeactivated();
	}

	private String getValidString(String string)
	{
		return string != null ? string.intern() : EMPTY_STRING;
	}

	public void update(IdentityPreprocessed identity)
	{
		this.firstName = getValidString(identity.getFirstName());
		this.middleName = getValidString(identity.getMiddleName());
		this.lastName = getValidString(identity.getLastName());
		this.prefix = getValidString(identity.getPrefix());
		this.suffix = getValidString(identity.getSuffix());
		this.gender = identity.getGender();
		this.birthDate = getValidString(identity.getBirthDate());
		this.externalTimestamp = identity.getExternalTimestamp();
		this.birthPlace = getValidString(identity.getBirthPlace());
		this.race = getValidString(identity.getRace());
		this.religion = getValidString(identity.getReligion());
		this.mothersMaidenName = getValidString(identity.getMothersMaidenName());
		this.degree = getValidString(identity.getDegree());
		this.motherTongue = getValidString(identity.getMotherTongue());
		this.nationality = getValidString(identity.getNationality());
		this.civilStatus = getValidString(identity.getCivilStatus());
		this.timestamp = identity.getTimestamp();
		this.createTimestamp = identity.getCreateTimestamp();
		this.value1 = getValidString(identity.getValue1());
		this.value2 = getValidString(identity.getValue2());
		this.value3 = getValidString(identity.getValue3());
		this.value4 = getValidString(identity.getValue4());
		this.value5 = getValidString(identity.getValue5());
		this.value6 = getValidString(identity.getValue6());
		this.value7 = getValidString(identity.getValue7());
		this.value8 = getValidString(identity.getValue8());
		this.value9 = getValidString(identity.getValue9());
		this.value10 = getValidString(identity.getValue10());
	}

	public void setDBIds(long identityId, long personId, String domainName, Timestamp timestamp)
	{
		this.identityId = identityId;
		this.personId = personId;
		this.domainName = getValidString(domainName);
		this.timestamp = timestamp;
	}

	public long getIdentityId()
	{
		return identityId;
	}

	public long getPersonId()
	{
		return personId;
	}

	public String getDomainName()
	{
		return domainName;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public String getMiddleName()
	{
		return middleName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public String getPrefix()
	{
		return prefix;
	}

	public String getSuffix()
	{
		return suffix;
	}

	public String getBirthDate()
	{
		return birthDate;
	}

	public char getGender()
	{
		return gender;
	}

	public Timestamp getExternalTimestamp()
	{
		return externalTimestamp;
	}

	public Timestamp getTimestamp()
	{
		return timestamp;
	}

	public Timestamp getCreateTimestamp()
	{
		return createTimestamp;
	}

	public String getBirthPlace()
	{
		return birthPlace;
	}

	public String getRace()
	{
		return race;
	}

	public String getReligion()
	{
		return religion;
	}

	public String getMothersMaidenName()
	{
		return mothersMaidenName;
	}

	public String getDegree()
	{
		return degree;
	}

	public String getMotherTongue()
	{
		return motherTongue;
	}

	public String getNationality()
	{
		return nationality;
	}

	public String getCivilStatus()
	{
		return civilStatus;
	}

	public String getValue1()
	{
		return value1;
	}

	public String getValue2()
	{
		return value2;
	}

	public String getValue3()
	{
		return value3;
	}

	public String getValue4()
	{
		return value4;
	}

	public String getValue5()
	{
		return value5;
	}

	public String getValue6()
	{
		return value6;
	}

	public String getValue7()
	{
		return value7;
	}

	public String getValue8()
	{
		return value8;
	}

	public String getValue9()
	{
		return value9;
	}

	public String getValue10()
	{
		return value10;
	}

	public boolean isForcedReference()
	{
		return forcedReference;
	}

	public boolean isDeactivated()
	{
		return deactivated;
	}

	public void setIdentityId(long identityId)
	{
		this.identityId = identityId;
	}

	public void setPersonId(long personId)
	{
		this.personId = personId;
	}

	public void setDomainName(String domainName)
	{
		this.domainName = getValidString(domainName);
	}

	public void setFirstName(String firstName)
	{
		this.firstName = getValidString(firstName);
	}

	public void setMiddleName(String middleName)
	{
		this.middleName = getValidString(middleName);
	}

	public void setLastName(String lastName)
	{
		this.lastName = getValidString(lastName);
	}

	public void setPrefix(String prefix)
	{
		this.prefix = getValidString(prefix);
	}

	public void setSuffix(String suffix)
	{
		this.suffix = getValidString(suffix);
	}

	public void setBirthDate(String birthDate)
	{
		this.birthDate = getValidString(birthDate);
	}

	public void setGender(char gender)
	{
		this.gender = gender;
	}

	public void setExternalTimestamp(Timestamp externalTimestamp)
	{
		this.externalTimestamp = externalTimestamp;
	}

	public void setTimestamp(Timestamp timestamp)
	{
		this.timestamp = timestamp;
	}

	public void setCreateTimestamp(Timestamp createTimestamp)
	{
		this.createTimestamp = createTimestamp;
	}

	public void setBirthPlace(String birthPlace)
	{
		this.birthPlace = getValidString(birthPlace);
	}

	public void setRace(String race)
	{
		this.race = getValidString(race);
	}

	public void setReligion(String religion)
	{
		this.religion = getValidString(religion);
	}

	public void setMothersMaidenName(String mothersMaidenName)
	{
		this.mothersMaidenName = getValidString(mothersMaidenName);
	}

	public void setDegree(String degree)
	{
		this.degree = getValidString(degree);
	}

	public void setMotherTongue(String motherTongue)
	{
		this.motherTongue = getValidString(motherTongue);
	}

	public void setNationality(String nationality)
	{
		this.nationality = getValidString(nationality);
	}

	public void setCivilStatus(String civilStatus)
	{
		this.civilStatus = getValidString(civilStatus);
	}

	public void setValue1(String value1)
	{
		this.value1 = getValidString(value1);
	}

	public void setValue2(String value2)
	{
		this.value2 = getValidString(value2);
	}

	public void setValue3(String value3)
	{
		this.value3 = getValidString(value3);
	}

	public void setValue4(String value4)
	{
		this.value4 = getValidString(value4);
	}

	public void setValue5(String value5)
	{
		this.value5 = getValidString(value5);
	}

	public void setValue6(String value6)
	{
		this.value6 = getValidString(value6);
	}

	public void setValue7(String value7)
	{
		this.value7 = getValidString(value7);
	}

	public void setValue8(String value8)
	{
		this.value8 = getValidString(value8);
	}

	public void setValue9(String value9)
	{
		this.value9 = getValidString(value9);
	}

	public void setValue10(String value10)
	{
		this.value10 = getValidString(value10);
	}

	public void setForcedReference(boolean forcedReference)
	{
		this.forcedReference = forcedReference;
	}

	public void setDeactivated(boolean deactivated)
	{
		this.deactivated = deactivated;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (identityId ^ identityId >>> 32);
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
		IdentityPreprocessed other = (IdentityPreprocessed) obj;
		if (identityId != other.identityId)
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		return "IdentityPreprocessed [identityId=" + identityId + ", personId=" + personId + ", domainName=" + domainName + ", forcedReference="
				+ forcedReference + ", deactivated=" + deactivated + "]";
	}

	public String toLongString()
	{
		return "IdentityPreprocessed [identityId=" + identityId + ", personId=" + personId + ", domainName=" + domainName + ", firstName=" + firstName
				+ ", middleName=" + middleName + ", lastName=" + lastName + ", prefix=" + prefix + ", suffix=" + suffix + ", birthDate=" + birthDate
				+ ", gender=" + gender + ", externalTimestamp=" + externalTimestamp + ", timestamp=" + timestamp + ", createTimestamp="
				+ createTimestamp + ", birthPlace=" + birthPlace + ", race=" + race + ", religion=" + religion + ", mothersMaidenName="
				+ mothersMaidenName + ", degree=" + degree + ", motherTongue=" + motherTongue + ", nationality=" + nationality + ", civilStatus="
				+ civilStatus + ", value1=" + value1 + ", value2=" + value2 + ", value3=" + value3 + ", value4=" + value4 + ", value5=" + value5
				+ ", value6=" + value6 + ", value7=" + value7 + ", value8=" + value8 + ", value9=" + value9 + ", value10=" + value10
				+ ", forcedReference=" + forcedReference + ", deactivated=" + deactivated + "]";
	}
}
