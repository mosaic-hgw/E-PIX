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

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.emau.icmvc.ttp.epix.common.model.IdentifierDTO;

/**
 * 
 * @author Christian Schack, geidell
 * @since 08.11.2010
 */
@Entity
@IdClass(IdentifierId.class)
@Table(name = "identifier")
@Cacheable(false)
@NamedQueries({
		@NamedQuery(name = "Identifier.findByPerson", query = "SELECT li FROM Identity i JOIN i.identifiers li WHERE i.person = :person GROUP BY li.value, li.identifierDomain"),
		@NamedQuery(name = "Identifier.findByPersonAndDomain", query = "SELECT li FROM Identity i JOIN i.identifiers li WHERE i.person = :person AND li.identifierDomain = :identifierDomain"),
		@NamedQuery(name = "Identifier.getOrderedIdentifierByIdentifierDomain", query = "SELECT i FROM Identifier i WHERE i.identifierDomain = :identifierDomain AND i.value LIKE CONCAT(:prefix, '%') ORDER BY i.value desc") })
public class Identifier implements Serializable
{
	private static final long serialVersionUID = 7695713504626546050L;
	@Id
	@ManyToOne(cascade = CascadeType.MERGE, optional = false)
	@JoinColumn(name = "identifier_domain_name")
	private IdentifierDomain identifierDomain;
	@Id
	@Column(nullable = false)
	private String value;
	private String description;
	@Column(columnDefinition = "BIT", length = 1)
	private boolean active = true;
	@Column(name = "create_timestamp", nullable = false)
	private Timestamp createTimestamp;

	public Identifier()
	{
		createTimestamp = new Timestamp(System.currentTimeMillis());
	}

	public Identifier(IdentifierDTO dto, IdentifierDomain identifierDomain, Timestamp timestamp)
	{
		this.value = dto.getValue();
		this.description = dto.getDescription();
		this.identifierDomain = identifierDomain;
		this.createTimestamp = timestamp;
	}

	public Identifier(IdentifierDomain identifierDomain, String value, String description, Timestamp timestamp)
	{
		super();
		this.identifierDomain = identifierDomain;
		this.value = value;
		this.description = description;
		this.createTimestamp = timestamp;
	}

	public IdentifierDomain getIdentifierDomain()
	{
		return identifierDomain;
	}

	public void setIdentifierDomain(IdentifierDomain identifierDomain)
	{
		this.identifierDomain = identifierDomain;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public Timestamp getCreateTimestamp()
	{
		return createTimestamp;
	}

	public void setCreateTimestamp(Timestamp createTimestamp)
	{
		this.createTimestamp = createTimestamp;
	}

	public boolean isActive()
	{
		return active;
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}

	public IdentifierId getId()
	{
		return new IdentifierId(identifierDomain.getName(), value);
	}

	public IdentifierDTO toDTO()
	{
		return new IdentifierDTO(value, description, createTimestamp, identifierDomain.toDTO());
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((identifierDomain == null) ? 0 : identifierDomain.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Identifier))
			return false;
		Identifier other = (Identifier) obj;
		if (identifierDomain == null)
		{
			if (other.identifierDomain != null)
				return false;
		}
		else if (!identifierDomain.equals(other.identifierDomain))
			return false;
		if (value == null)
		{
			if (other.value != null)
				return false;
		}
		else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "Identifier [identifierDomain=" + identifierDomain + ", value=" + value + ", description=" + description + ", active=" + active
				+ ", createTimestamp=" + createTimestamp + "]";
	}

	public String toShortString()
	{
		return "Identifier [identifierDomain=" + identifierDomain + ", value=" + value + "]";
	}
}
