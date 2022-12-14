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
import java.util.Date;

/**
 * 
 * @author geidell
 *
 */
public class ContactInDTO implements Serializable
{
	private static final long serialVersionUID = -4202121417957009220L;
	private String street;
	private String zipCode;
	private String city;
	private String state;
	private String country;
	private String countryCode;
	private String email;
	private String phone;
	private String district;
	private String municipalityKey;
	private Date externalDate;

	public ContactInDTO()
	{
		super();
	}

	public ContactInDTO(String street, String zipCode, String city, String state, String country, String email, String phone, String countryCode,
			String district, String municipalityKey, Date externalDate)
	{
		super();
		this.street = street;
		this.zipCode = zipCode;
		this.city = city;
		this.state = state;
		this.country = country;
		this.email = email;
		this.phone = phone;
		this.countryCode = countryCode;
		this.district = district;
		this.municipalityKey = municipalityKey;
		this.externalDate = externalDate;
	}

	public ContactInDTO(ContactInDTO dto)
	{
		this(dto.getStreet(), dto.getZipCode(), dto.getCity(), dto.getState(), dto.getCountry(), dto.getEmail(), dto.getPhone(), dto.getCountryCode(),
				dto.getDistrict(), dto.getMunicipalityKey(), dto.getExternalDate());
	}

	public String getStreet()
	{
		return street;
	}

	public void setStreet(String street)
	{
		this.street = street;
	}

	public String getZipCode()
	{
		return zipCode;
	}

	public void setZipCode(String zipCode)
	{
		this.zipCode = zipCode;
	}

	public String getCity()
	{
		return city;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	public String getState()
	{
		return state;
	}

	public void setState(String state)
	{
		this.state = state;
	}

	public String getCountry()
	{
		return country;
	}

	public void setCountry(String country)
	{
		this.country = country;
	}

	public String getCountryCode()
	{
		return countryCode;
	}

	public void setCountryCode(String countryCode)
	{
		this.countryCode = countryCode;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getPhone()
	{
		return phone;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	public String getDistrict()
	{
		return district;
	}

	public void setDistrict(String district)
	{
		this.district = district;
	}

	public String getMunicipalityKey()
	{
		return municipalityKey;
	}

	public void setMunicipalityKey(String municipalityKey)
	{
		this.municipalityKey = municipalityKey;
	}

	public Date getExternalDate()
	{
		return externalDate;
	}

	public void setExternalDate(Date externalDate)
	{
		this.externalDate = externalDate;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result + ((countryCode == null) ? 0 : countryCode.hashCode());
		result = prime * result + ((district == null) ? 0 : district.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((municipalityKey == null) ? 0 : municipalityKey.hashCode());
		result = prime * result + ((externalDate == null) ? 0 : externalDate.hashCode());
		result = prime * result + ((phone == null) ? 0 : phone.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + ((street == null) ? 0 : street.hashCode());
		result = prime * result + ((zipCode == null) ? 0 : zipCode.hashCode());
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
		ContactInDTO other = (ContactInDTO) obj;
		if (city == null)
		{
			if (other.city != null)
				return false;
		}
		else if (!city.equals(other.city))
			return false;
		if (country == null)
		{
			if (other.country != null)
				return false;
		}
		else if (!country.equals(other.country))
			return false;
		if (countryCode == null)
		{
			if (other.countryCode != null)
				return false;
		}
		else if (!countryCode.equals(other.countryCode))
			return false;
		if (district == null)
		{
			if (other.district != null)
				return false;
		}
		else if (!district.equals(other.district))
			return false;
		if (email == null)
		{
			if (other.email != null)
				return false;
		}
		else if (!email.equals(other.email))
			return false;
		if (municipalityKey == null)
		{
			if (other.municipalityKey != null)
				return false;
		}
		else if (!municipalityKey.equals(other.municipalityKey))
			return false;
		if (externalDate == null)
		{
			if (other.externalDate != null)
				return false;
		}
		else if (!externalDate.equals(other.externalDate))
			return false;
		if (phone == null)
		{
			if (other.phone != null)
				return false;
		}
		else if (!phone.equals(other.phone))
			return false;
		if (state == null)
		{
			if (other.state != null)
				return false;
		}
		else if (!state.equals(other.state))
			return false;
		if (street == null)
		{
			if (other.street != null)
				return false;
		}
		else if (!street.equals(other.street))
			return false;
		if (zipCode == null)
		{
			if (other.zipCode != null)
				return false;
		}
		else if (!zipCode.equals(other.zipCode))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "ContactDTO [externalDate=" + externalDate + "]";
	}

	public String toLongString()
	{
		return "ContactDTO [street=" + street + ", zipCode=" + zipCode + ", city=" + city + ", state=" + state + ", country=" + country
				+ ", countryCode=" + countryCode + ", email=" + email + ", phone=" + phone + ", district=" + district + ", municipalityKey="
				+ municipalityKey + ", externalDate=" + externalDate + "]";
	}
}
