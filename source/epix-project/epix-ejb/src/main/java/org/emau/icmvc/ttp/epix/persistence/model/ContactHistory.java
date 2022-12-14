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
import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.emau.icmvc.ttp.epix.common.model.ContactHistoryDTO;
import org.emau.icmvc.ttp.epix.common.model.ContactInDTO;
import org.emau.icmvc.ttp.epix.common.model.ContactOutDTO;

/**
 * 
 * @author geidell
 *
 */
@Entity
@Table(name = "contact_history")
@TableGenerator(name = "contact_history_index", table = "sequence", initialValue = 0, allocationSize = 50)
@Cacheable(false)
@NamedQueries({ @NamedQuery(name = "ContactHistory.findByContact", query = "SELECT ch FROM ContactHistory ch WHERE ch.contact = :contact"), })
public class ContactHistory implements Serializable
{
	private static final long serialVersionUID = 5764152617724537883L;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "contact_history_index")
	private long id;
	@Column(name = "contact_version")
	private int contactVersion;
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
	@Column(name = "external_timestamp")
	private Timestamp externalTimestamp;
	@Column(name = "history_timestamp", nullable = false)
	private Timestamp historyTimestamp;
	@Column(name = "identity_id")
	private long identityId;
	private boolean deactivated;
	@ManyToOne(optional = false)
	@JoinColumn(name = "contact_id", nullable = false)
	private Contact contact;

	public ContactHistory()
	{
		historyTimestamp = new Timestamp(System.currentTimeMillis());
	}

	public ContactHistory(Contact contact, Timestamp timestamp)
	{
		this.contact = contact;
		contactVersion = contact.getVersion();
		street = getValidString(contact.getStreet());
		zipCode = getValidString(contact.getZipCode());
		city = getValidString(contact.getCity());
		state = getValidString(contact.getState());
		email = getValidString(contact.getEmail());
		phone = getValidString(contact.getPhone());
		country = getValidString(contact.getCountry());
		countryCode = getValidString(contact.getCountryCode());
		district = getValidString(contact.getDistrict());
		municipalityKey = getValidString(contact.getMunicipalityKey());
		externalTimestamp = contact.getExternalTimestamp() == null ? null : new Timestamp(contact.getExternalTimestamp().getTime());
		historyTimestamp = timestamp;
		identityId = contact.getIdentity().getId();
		deactivated = contact.isDeactivated();
	}

	private String getValidString(String obj)
	{
		return (obj != null ? obj : "");
	}

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public int getContactVersion()
	{
		return contactVersion;
	}

	public void setContactVersion(int contactVersion)
	{
		this.contactVersion = contactVersion;
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

	public long getIdentityId()
	{
		return identityId;
	}

	public void setIdentityId(long identityId)
	{
		this.identityId = identityId;
	}

	public boolean isDeactivated()
	{
		return deactivated;
	}

	public void setDeactivated(boolean deactivated)
	{
		this.deactivated = deactivated;
	}

	public Contact getContact()
	{
		return contact;
	}

	public void setContact(Contact contact)
	{
		this.contact = contact;
	}

	public ContactHistoryDTO toDTO()
	{
		ContactInDTO inDTO = new ContactInDTO(street, zipCode, city, state, country, email, phone, countryCode, district, municipalityKey,
				externalTimestamp == null ? null : new Date(externalTimestamp.getTime()));
		ContactOutDTO outDTO = new ContactOutDTO(inDTO, contact.getId(), contactVersion, identityId, deactivated,
				new Date(contact.getCreateTimestamp().getTime()), new Date(contact.getTimestamp().getTime()));
		return new ContactHistoryDTO(outDTO, id, new Date(historyTimestamp.getTime()));
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
		ContactHistory other = (ContactHistory) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "ContactHistory [id=" + id + ", contactVersion=" + contactVersion + ", externalTimestamp=" + externalTimestamp + ", historyTimestamp="
				+ historyTimestamp + ", identityId=" + identityId + ", deactivated=" + deactivated + ", contact=" + contact + "]";
	}

	public String toLongString()
	{
		return "ContactHistory [id=" + id + ", contactVersion=" + contactVersion + ", street=" + street + ", zipCode=" + zipCode + ", city=" + city
				+ ", state=" + state + ", email=" + email + ", phone=" + phone + ", country=" + country + ", countryCode=" + countryCode
				+ ", district=" + district + ", municipalityKey=" + municipalityKey + ", externalTimestamp=" + externalTimestamp
				+ ", historyTimestamp=" + historyTimestamp + ", identityId=" + identityId + ", deactivated=" + deactivated + ", contact=" + contact
				+ "]";
	}
}
