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
import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

import org.emau.icmvc.ttp.epix.common.model.ContactInDTO;
import org.emau.icmvc.ttp.epix.common.model.ContactOutDTO;

/**
 * 
 * @author geidell
 *
 */
@Entity
@Table(name = "contact")
@TableGenerator(name = "contact_index", table = "sequence", initialValue = 0, allocationSize = 50)
@Cacheable(false)
public class Contact implements Serializable
{
	private static final long serialVersionUID = 5807030586991103334L;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "contact_index")
	private long id;
	@Version
	private int version;
	private String street;
	@Column(name = "zip_code")
	private String zipCode;
	private String city;
	private String state;
	private String email;
	private String phone;
	private String country;
	@Column(name = "country_code")
	private String countryCode;
	private String district;
	@Column(name = "municipality_key")
	private String municipalityKey;
	@Column(columnDefinition = "BIT", length = 1)
	private boolean deactivated;
	@Column(name = "external_timestamp")
	private Timestamp externalTimestamp;
	@Column(name = "create_timestamp", nullable = false)
	private Timestamp createTimestamp;
	@Column(nullable = false)
	private Timestamp timestamp;
	@Column(name = "date_of_move_in")
	private Date dateOfMoveIn;
	@Column(name = "date_of_move_out")
	private Date dateOfMoveOut;

	@ManyToOne(cascade = CascadeType.MERGE, optional = false)
	private Identity identity;

	public Contact()
	{
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		version = 0;
		createTimestamp = timestamp;
		this.timestamp = timestamp;
	}

	public Contact(ContactInDTO dto, Identity identity, Timestamp timestamp)
	{
		street = dto.getStreet();
		zipCode = dto.getZipCode();
		city = dto.getCity();
		state = dto.getState();
		country = dto.getCountry();
		email = dto.getEmail();
		phone = dto.getPhone();
		countryCode = dto.getCountryCode();
		district = dto.getDistrict();
		municipalityKey = dto.getMunicipalityKey();
		externalTimestamp = dto.getExternalDate() != null ? new Timestamp(dto.getExternalDate().getTime()) : null;
		dateOfMoveIn = dto.getDateOfMoveIn();
		dateOfMoveOut = dto.getDateOfMoveOut();
		this.identity = identity;
		createTimestamp = timestamp;
		this.timestamp = timestamp;
		version = 0;
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

	public boolean isDeactivated()
	{
		return deactivated;
	}

	public void setDeactivated(boolean deactivated)
	{
		this.deactivated = deactivated;
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

	public Date getDateOfMoveIn()
	{
		return dateOfMoveIn;
	}

	public void setDateOfMoveIn(Date dateOfMoveIn)
	{
		this.dateOfMoveIn = dateOfMoveIn;
	}

	public Date getDateOfMoveOut()
	{
		return dateOfMoveOut;
	}

	public void setDateOfMoveOut(Date dateOfMoveOut)
	{
		this.dateOfMoveOut = dateOfMoveOut;
	}

	public Identity getIdentity()
	{
		return identity;
	}

	public void setIdentity(Identity identity)
	{
		this.identity = identity;
	}

	public void update(Contact newContact, Timestamp timestamp)
	{
		this.street = newContact.getStreet();
		this.zipCode = newContact.getZipCode();
		this.city = newContact.getCity();
		this.state = newContact.getState();
		this.country = newContact.getCountry();
		this.email = newContact.getEmail();
		this.phone = newContact.getPhone();
		this.countryCode = newContact.getCountryCode();
		this.district = newContact.getDistrict();
		this.municipalityKey = newContact.getMunicipalityKey();
		this.externalTimestamp = newContact.getExternalTimestamp() != null ? new Timestamp(newContact.getExternalTimestamp().getTime()) : null;
		this.dateOfMoveIn = newContact.dateOfMoveIn;
		this.dateOfMoveOut = newContact.dateOfMoveOut;
		this.timestamp = timestamp;
	}

	public ContactOutDTO toDTO()
	{
		ContactInDTO inDTO = new ContactInDTO(street, zipCode, city, state, country, email, phone, countryCode, district, municipalityKey,
				externalTimestamp == null ? null : new Date(externalTimestamp.getTime()), dateOfMoveIn, dateOfMoveOut);
		return new ContactOutDTO(inDTO, id, version, identity.getId(), deactivated,
				createTimestamp == null ? null : new Date(createTimestamp.getTime()), timestamp == null ? null : new Date(timestamp.getTime()));
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
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
		Contact other = (Contact) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public boolean equalContent(Contact other)
	{
		if (this == other)
			return true;
		if (other == null)
			return false;
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
		if (deactivated != other.deactivated)
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
		if (externalTimestamp == null)
		{
			if (other.externalTimestamp != null)
				return false;
		}
		else if (!externalTimestamp.equals(other.externalTimestamp))
			return false;
		if (municipalityKey == null)
		{
			if (other.municipalityKey != null)
				return false;
		}
		else if (!municipalityKey.equals(other.municipalityKey))
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
		if (dateOfMoveIn == null)
		{
			if (other.dateOfMoveIn != null)
				return false;
		}
		else if (!dateOfMoveIn.equals(other.dateOfMoveIn))
			return false;
		if (dateOfMoveOut == null)
		{
			if (other.dateOfMoveOut != null)
				return false;
		}
		else if (!dateOfMoveOut.equals(other.dateOfMoveOut))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "Contact [id=" + id + ", version=" + version + ", deactivated=" + deactivated + ", externalTimestamp=" + externalTimestamp
				+ ", createTimestamp=" + createTimestamp + ", timestamp=" + timestamp + ", identityId="
				+ (identity == null ? "null" : identity.getId()) + "]";
	}

	public String toLongString()
	{
		return "Contact [id=" + id + ", version=" + version + ", street=" + street + ", zipCode=" + zipCode + ", city=" + city + ", state=" + state
				+ ", email=" + email + ", phone=" + phone + ", country=" + country + ", countryCode=" + countryCode + ", district=" + district
				+ ", municipalityKey=" + municipalityKey + ", deactivated=" + deactivated + ", externalTimestamp=" + externalTimestamp
				+ ", createTimestamp=" + createTimestamp + ", timestamp=" + timestamp + ", identityId="
				+ (identity == null ? "null" : identity.getId())
				+ (dateOfMoveIn != null ? ", dateOfMoveIn=" : dateOfMoveIn)
				+ (dateOfMoveOut != null ? ", dateOfMoveOut=" : dateOfMoveOut)
				+ (identity == null ? "null" : identity.getId())
				+ "]";
	}
}
