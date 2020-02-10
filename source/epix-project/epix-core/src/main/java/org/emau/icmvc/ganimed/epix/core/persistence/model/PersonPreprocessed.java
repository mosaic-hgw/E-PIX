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

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.emau.icmvc.ganimed.epix.common.MPIException;
import org.emau.icmvc.ganimed.epix.common.model.ErrorCode;

@Entity
@Table(name = "person_preprocessed")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "person_type", discriminatorType = DiscriminatorType.STRING)
public class PersonPreprocessed extends AbstractEntity implements Serializable {

	private static final long serialVersionUID = -5649156155226505990L;

	@Id
	@Column(name = "person_id")
	private Long personId;

	@Column(name = "person_group_id")
	private Long personGroupID;

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

	public PersonPreprocessed() {
	}

	public PersonPreprocessed(Person person) {
		this.firstName = getValidString(person.getFirstName());
		this.middleName = getValidString(person.getMiddleName());
		this.lastName = getValidString(person.getLastName());
		this.prefix = getValidString(person.getPrefix());
		this.suffix = getValidString(person.getSuffix());
		this.gender = getValidString(person.getGender());
		this.birthDate = (person.getBirthDate() != null ? new Date(person.getBirthDate().getTime()) : null);
		this.originDate = (person.getOriginDate());
		this.birthPlace = getValidString(person.getBirthPlace());
		this.race = getValidString(person.getRace());
		this.religion = getValidString(person.getReligion());
		this.mothersMaidenName = getValidString(person.getMothersMaidenName());
		this.degree = getValidString(person.getDegree());
		this.motherTongue = getValidString(person.getMotherTongue());
		this.nationality = getValidString(person.getNationality());
		this.civilStatus = getValidString(person.getCivilStatus());
		this.value1 = person.getValue1();
		this.value2 = person.getValue2();
		this.value3 = person.getValue3();
		this.value4 = person.getValue4();
		this.value5 = person.getValue5();
		this.value6 = person.getValue6();
		this.value7 = person.getValue7();
		this.value8 = person.getValue8();
		this.value9 = person.getValue9();
		this.value10 = person.getValue10();
		this.personId = person.getId();
		if (person.getPersonGroup() != null) {
			this.personGroupID = person.getPersonGroup().getId();
		}
	}

	public PersonPreprocessed(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public Long getPersonId() {
		return personId;
	}

	public void setPersonId(Long personId) {
		this.personId = personId;
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

	public Long getPersonGroupID() {
		return personGroupID;
	}

	public void setPersonGroupID(Long personGroupID) {
		this.personGroupID = personGroupID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((birthDate == null) ? 0 : birthDate.hashCode());
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + ((gender == null) ? 0 : gender.hashCode());
		result = prime * result + ((personId == null) ? 0 : personId.hashCode());
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + ((middleName == null) ? 0 : middleName.hashCode());
		result = prime * result + ((originDate == null) ? 0 : originDate.hashCode());
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		result = prime * result + ((suffix == null) ? 0 : suffix.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof PersonPreprocessed))
			return false;
		PersonPreprocessed other = (PersonPreprocessed) obj;
		if (birthDate == null) {
			if (other.birthDate != null)
				return false;
		} else if (!birthDate.equals(other.birthDate))
			return false;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (gender == null) {
			if (other.gender != null)
				return false;
		} else if (!gender.equals(other.gender))
			return false;
		if (personId == null) {
			if (other.personId != null)
				return false;
		} else if (!personId.equals(other.personId))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (middleName == null) {
			if (other.middleName != null)
				return false;
		} else if (!middleName.equals(other.middleName))
			return false;
		if (originDate == null) {
			if (other.originDate != null)
				return false;
		} else if (!originDate.equals(other.originDate))
			return false;
		if (prefix == null) {
			if (other.prefix != null)
				return false;
		} else if (!prefix.equals(other.prefix))
			return false;
		if (suffix == null) {
			if (other.suffix != null)
				return false;
		} else if (!suffix.equals(other.suffix))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PersonPreprocessed [personId=" + personId + ", firstName=" + firstName + ", middleName=" + middleName + ", lastName=" + lastName
				+ ", prefix=" + prefix + ", suffix=" + suffix + ", birthDate=" + birthDate + ", gender=" + gender + ", originDate=" + originDate
				+ ", timestamp=" + timestamp + ", birthPlace=" + birthPlace + ", race=" + race + ", religion=" + religion + ", mothersMaidenName="
				+ mothersMaidenName + ", degree=" + degree + ", motherTongue=" + motherTongue + ", nationality=" + nationality + ", civilStatus="
				+ civilStatus + ", value1=" + value1 + ", value2=" + value2 + ", value3=" + value3 + ", value4=" + value4 + ", value5=" + value5
				+ ", value6=" + value6 + ", value7=" + value7 + ", value8=" + value8 + ", value9=" + value9 + ", value10=" + value10 + "]";
	}

	public void updatePersonPreprocessed(PersonPreprocessed updatePersonPreprocessed) throws MPIException {

		if (!updatePersonPreprocessed.getPersonGroupID().equals(personGroupID)) {
			throw new MPIException(ErrorCode.INTERNAL_ERROR);
		}

		if (updatePersonPreprocessed.getPersonGroupID() != null && !("").equals(updatePersonPreprocessed.getPersonGroupID())) {
			this.setPersonGroupID(updatePersonPreprocessed.getPersonGroupID());
		}
		if (updatePersonPreprocessed.getFirstName() != null && !("").equals(updatePersonPreprocessed.getFirstName())) {
			this.setFirstName(updatePersonPreprocessed.getFirstName());
		}
		if (updatePersonPreprocessed.getMiddleName() != null && !("").equals(updatePersonPreprocessed.getMiddleName())) {
			this.setMiddleName(updatePersonPreprocessed.getMiddleName());
		}
		if (updatePersonPreprocessed.getLastName() != null && !("").equals(updatePersonPreprocessed.getLastName())) {
			this.setLastName(updatePersonPreprocessed.getLastName());
		}
		if (updatePersonPreprocessed.getPrefix() != null && !("").equals(updatePersonPreprocessed.getPrefix())) {
			this.setPrefix(updatePersonPreprocessed.getPrefix());
		}
		if (updatePersonPreprocessed.getSuffix() != null || !("").equals(updatePersonPreprocessed.getSuffix())) {
			this.setSuffix(updatePersonPreprocessed.getSuffix());
		}
		if (updatePersonPreprocessed.getGender() != null || !("").equals(updatePersonPreprocessed.getGender())) {
			this.setGender(updatePersonPreprocessed.getGender());
		}
		if (updatePersonPreprocessed.getBirthDate() != null) {
			this.setBirthDate(updatePersonPreprocessed.getBirthDate());
		}
		if (updatePersonPreprocessed.getBirthPlace() != null && !("").equals(updatePersonPreprocessed.getBirthPlace())) {
			this.setBirthPlace(updatePersonPreprocessed.getBirthPlace());
		}
		if (updatePersonPreprocessed.getRace() != null && !("").equals(updatePersonPreprocessed.getRace())) {
			this.setRace(updatePersonPreprocessed.getRace());
		}
		if (updatePersonPreprocessed.getReligion() != null && !("").equals(updatePersonPreprocessed.getReligion())) {
			this.setReligion(updatePersonPreprocessed.getReligion());
		}
		if (updatePersonPreprocessed.getMothersMaidenName() != null && !("").equals(updatePersonPreprocessed.getMothersMaidenName())) {
			this.setMothersMaidenName(updatePersonPreprocessed.getMothersMaidenName());
		}
		if (updatePersonPreprocessed.getDegree() != null && !("").equals(updatePersonPreprocessed.getDegree())) {
			this.setDegree(updatePersonPreprocessed.getDegree());
		}
		if (updatePersonPreprocessed.getMotherTongue() != null && !("").equals(updatePersonPreprocessed.getMotherTongue())) {
			this.setMotherTongue(updatePersonPreprocessed.getMotherTongue());
		}
		if (updatePersonPreprocessed.getNationality() != null && !("").equals(updatePersonPreprocessed.getNationality())) {
			this.setNationality(updatePersonPreprocessed.getNationality());
		}
		if (updatePersonPreprocessed.getCivilStatus() != null && !("").equals(updatePersonPreprocessed.getCivilStatus())) {
			this.setCivilStatus(updatePersonPreprocessed.getCivilStatus());
		}
		if (updatePersonPreprocessed.getValue1() != null && !"".equals(updatePersonPreprocessed.getValue1())) {
			this.setValue1(updatePersonPreprocessed.getValue1());
		}
		if (updatePersonPreprocessed.getValue2() != null && !"".equals(updatePersonPreprocessed.getValue2())) {
			this.setValue2(updatePersonPreprocessed.getValue2());
		}
		if (updatePersonPreprocessed.getValue3() != null && !"".equals(updatePersonPreprocessed.getValue3())) {
			this.setValue3(updatePersonPreprocessed.getValue3());
		}
		if (updatePersonPreprocessed.getValue4() != null && !"".equals(updatePersonPreprocessed.getValue4())) {
			this.setValue4(updatePersonPreprocessed.getValue4());
		}
		if (updatePersonPreprocessed.getValue5() != null && !"".equals(updatePersonPreprocessed.getValue5())) {
			this.setValue5(updatePersonPreprocessed.getValue5());
		}
		if (updatePersonPreprocessed.getValue6() != null && !"".equals(updatePersonPreprocessed.getValue6())) {
			this.setValue6(updatePersonPreprocessed.getValue6());
		}
		if (updatePersonPreprocessed.getValue7() != null && !"".equals(updatePersonPreprocessed.getValue7())) {
			this.setValue7(updatePersonPreprocessed.getValue7());
		}
		if (updatePersonPreprocessed.getValue8() != null && !"".equals(updatePersonPreprocessed.getValue8())) {
			this.setValue8(updatePersonPreprocessed.getValue8());
		}
		if (updatePersonPreprocessed.getValue9() != null && !"".equals(updatePersonPreprocessed.getValue9())) {
			this.setValue9(updatePersonPreprocessed.getValue9());
		}
		if (updatePersonPreprocessed.getValue10() != null && !"".equals(updatePersonPreprocessed.getValue10())) {
			this.setValue10(updatePersonPreprocessed.getValue10());
		}
		if (updatePersonPreprocessed.getOriginDate() != null) {
			this.setOriginDate(updatePersonPreprocessed.getOriginDate());
		}
	}

}
