package org.emau.icmvc.ttp.epix.common.model;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.emau.icmvc.ttp.epix.common.model.enums.Gender;

/**
 * 
 * @author geidell
 *
 */
public class IdentityInBaseDTO implements Serializable
{
	private static final long serialVersionUID = 1361751816485689578L;
	private String firstName;
	private String middleName;
	private String lastName;
	private String prefix;
	private String suffix;
	private Gender gender;
	private Date birthDate;
	private List<IdentifierDTO> identifiers = new ArrayList<>();
	private String birthPlace;
	private String race;
	private String religion;
	private String mothersMaidenName;
	private String degree;
	private String motherTongue;
	private String nationality;
	private String civilStatus;
	private Date externalDate;
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

	public IdentityInBaseDTO()
	{
		super();
	}

	public IdentityInBaseDTO(String firstName, String middleName, String lastName, String prefix, String suffix, Gender gender, Date birthDate,
			List<IdentifierDTO> identifiers, String birthPlace, String race, String religion, String mothersMaidenName, String degree,
			String motherTongue, String nationality, String civilStatus, Date externalDate, String value1, String value2, String value3,
			String value4, String value5, String value6, String value7, String value8, String value9, String value10)
	{
		super();
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.prefix = prefix;
		this.suffix = suffix;
		this.gender = gender;
		this.birthDate = birthDate;
		if (identifiers != null)
		{
			this.identifiers = identifiers;
		}
		this.birthPlace = birthPlace;
		this.race = race;
		this.religion = religion;
		this.mothersMaidenName = mothersMaidenName;
		this.degree = degree;
		this.motherTongue = motherTongue;
		this.nationality = nationality;
		this.civilStatus = civilStatus;
		this.externalDate = externalDate;
		this.value1 = value1;
		this.value2 = value2;
		this.value3 = value3;
		this.value4 = value4;
		this.value5 = value5;
		this.value6 = value6;
		this.value7 = value7;
		this.value8 = value8;
		this.value9 = value9;
		this.value10 = value10;
	}

	public IdentityInBaseDTO(IdentityInBaseDTO dto)
	{
		this(dto.getFirstName(), dto.getMiddleName(), dto.getLastName(), dto.getPrefix(), dto.getSuffix(), dto.getGender(), dto.getBirthDate(),
				dto.getIdentifiers(), dto.getBirthPlace(), dto.getRace(), dto.getReligion(), dto.getMothersMaidenName(), dto.getDegree(),
				dto.getMotherTongue(), dto.getNationality(), dto.getCivilStatus(), dto.getExternalDate(), dto.getValue1(), dto.getValue2(),
				dto.getValue3(), dto.getValue4(), dto.getValue5(), dto.getValue6(), dto.getValue7(), dto.getValue8(), dto.getValue9(),
				dto.getValue10());
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

	public Gender getGender()
	{
		return gender;
	}

	public void setGender(Gender gender)
	{
		this.gender = gender;
	}

	public Date getBirthDate()
	{
		return birthDate;
	}

	public void setBirthDate(Date birthDate)
	{
		this.birthDate = birthDate;
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

	public Date getExternalDate()
	{
		return externalDate;
	}

	public void setExternalDate(Date externalDate)
	{
		this.externalDate = externalDate;
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

	public List<IdentifierDTO> getIdentifiers()
	{
		return identifiers;
	}

	public void setIdentifiers(List<IdentifierDTO> identifiers)
	{
		if (identifiers != null)
		{
			this.identifiers = identifiers;
		}
		else
		{
			this.identifiers.clear();
		}
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((birthDate == null) ? 0 : birthDate.hashCode());
		result = prime * result + ((birthPlace == null) ? 0 : birthPlace.hashCode());
		result = prime * result + ((civilStatus == null) ? 0 : civilStatus.hashCode());
		result = prime * result + ((degree == null) ? 0 : degree.hashCode());
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + ((gender == null) ? 0 : gender.hashCode());
		result = prime * result + ((identifiers == null) ? 0 : identifiers.hashCode());
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + ((middleName == null) ? 0 : middleName.hashCode());
		result = prime * result + ((motherTongue == null) ? 0 : motherTongue.hashCode());
		result = prime * result + ((mothersMaidenName == null) ? 0 : mothersMaidenName.hashCode());
		result = prime * result + ((nationality == null) ? 0 : nationality.hashCode());
		result = prime * result + ((externalDate == null) ? 0 : externalDate.hashCode());
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		result = prime * result + ((race == null) ? 0 : race.hashCode());
		result = prime * result + ((religion == null) ? 0 : religion.hashCode());
		result = prime * result + ((suffix == null) ? 0 : suffix.hashCode());
		result = prime * result + ((value1 == null) ? 0 : value1.hashCode());
		result = prime * result + ((value10 == null) ? 0 : value10.hashCode());
		result = prime * result + ((value2 == null) ? 0 : value2.hashCode());
		result = prime * result + ((value3 == null) ? 0 : value3.hashCode());
		result = prime * result + ((value4 == null) ? 0 : value4.hashCode());
		result = prime * result + ((value5 == null) ? 0 : value5.hashCode());
		result = prime * result + ((value6 == null) ? 0 : value6.hashCode());
		result = prime * result + ((value7 == null) ? 0 : value7.hashCode());
		result = prime * result + ((value8 == null) ? 0 : value8.hashCode());
		result = prime * result + ((value9 == null) ? 0 : value9.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IdentityInBaseDTO other = (IdentityInBaseDTO) obj;
		if (birthDate == null)
		{
			if (other.birthDate != null)
				return false;
		}
		else if (!birthDate.equals(other.birthDate))
			return false;
		if (birthPlace == null)
		{
			if (other.birthPlace != null)
				return false;
		}
		else if (!birthPlace.equals(other.birthPlace))
			return false;
		if (civilStatus == null)
		{
			if (other.civilStatus != null)
				return false;
		}
		else if (!civilStatus.equals(other.civilStatus))
			return false;
		if (degree == null)
		{
			if (other.degree != null)
				return false;
		}
		else if (!degree.equals(other.degree))
			return false;
		if (firstName == null)
		{
			if (other.firstName != null)
				return false;
		}
		else if (!firstName.equals(other.firstName))
			return false;
		if (gender != other.gender)
			return false;
		if (identifiers == null)
		{
			if (other.identifiers != null)
				return false;
		}
		else if (!identifiers.equals(other.identifiers))
			return false;
		if (lastName == null)
		{
			if (other.lastName != null)
				return false;
		}
		else if (!lastName.equals(other.lastName))
			return false;
		if (middleName == null)
		{
			if (other.middleName != null)
				return false;
		}
		else if (!middleName.equals(other.middleName))
			return false;
		if (motherTongue == null)
		{
			if (other.motherTongue != null)
				return false;
		}
		else if (!motherTongue.equals(other.motherTongue))
			return false;
		if (mothersMaidenName == null)
		{
			if (other.mothersMaidenName != null)
				return false;
		}
		else if (!mothersMaidenName.equals(other.mothersMaidenName))
			return false;
		if (nationality == null)
		{
			if (other.nationality != null)
				return false;
		}
		else if (!nationality.equals(other.nationality))
			return false;
		if (externalDate == null)
		{
			if (other.externalDate != null)
				return false;
		}
		else if (!externalDate.equals(other.externalDate))
			return false;
		if (prefix == null)
		{
			if (other.prefix != null)
				return false;
		}
		else if (!prefix.equals(other.prefix))
			return false;
		if (race == null)
		{
			if (other.race != null)
				return false;
		}
		else if (!race.equals(other.race))
			return false;
		if (religion == null)
		{
			if (other.religion != null)
				return false;
		}
		else if (!religion.equals(other.religion))
			return false;
		if (suffix == null)
		{
			if (other.suffix != null)
				return false;
		}
		else if (!suffix.equals(other.suffix))
			return false;
		if (value1 == null)
		{
			if (other.value1 != null)
				return false;
		}
		else if (!value1.equals(other.value1))
			return false;
		if (value10 == null)
		{
			if (other.value10 != null)
				return false;
		}
		else if (!value10.equals(other.value10))
			return false;
		if (value2 == null)
		{
			if (other.value2 != null)
				return false;
		}
		else if (!value2.equals(other.value2))
			return false;
		if (value3 == null)
		{
			if (other.value3 != null)
				return false;
		}
		else if (!value3.equals(other.value3))
			return false;
		if (value4 == null)
		{
			if (other.value4 != null)
				return false;
		}
		else if (!value4.equals(other.value4))
			return false;
		if (value5 == null)
		{
			if (other.value5 != null)
				return false;
		}
		else if (!value5.equals(other.value5))
			return false;
		if (value6 == null)
		{
			if (other.value6 != null)
				return false;
		}
		else if (!value6.equals(other.value6))
			return false;
		if (value7 == null)
		{
			if (other.value7 != null)
				return false;
		}
		else if (!value7.equals(other.value7))
			return false;
		if (value8 == null)
		{
			if (other.value8 != null)
				return false;
		}
		else if (!value8.equals(other.value8))
			return false;
		if (value9 == null)
		{
			if (other.value9 != null)
				return false;
		}
		else if (!value9.equals(other.value9))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "IdentityInBaseDTO [identifiers=" + identifiers.stream().map(Object::toString).collect(Collectors.joining(", ")) + ", externalDate="
				+ externalDate + "]";
	}

	public String toLongString()
	{
		return "IdentityInBaseDTO [firstName=" + firstName + ", middleName=" + middleName + ", lastName=" + lastName + ", prefix=" + prefix
				+ ", suffix=" + suffix + ", gender=" + gender + ", birthDate=" + birthDate + ", identifiers="
				+ identifiers.stream().map(Object::toString).collect(Collectors.joining(", ")) + ", birthPlace=" + birthPlace + ", race=" + race
				+ ", religion=" + religion + ", mothersMaidenName=" + mothersMaidenName + ", degree=" + degree + ", motherTongue=" + motherTongue
				+ ", nationality=" + nationality + ", civilStatus=" + civilStatus + ", externalDate=" + externalDate + ", value1=" + value1
				+ ", value2=" + value2 + ", value3=" + value3 + ", value4=" + value4 + ", value5=" + value5 + ", value6=" + value6 + ", value7="
				+ value7 + ", value8=" + value8 + ", value9=" + value9 + ", value10=" + value10 + "]";
	}
}
