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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.emau.icmvc.ttp.epix.common.model.IdentifierDomainDTO;

/**
 * 
 * @author geidell
 *
 */
@Entity
@Table(name = "identifier_domain")
@NamedQueries({ @NamedQuery(name = "IdentifierDomain.findByOID", query = "SELECT id FROM IdentifierDomain id WHERE id.oid = :oid") })
public class IdentifierDomain implements Serializable
{
	private static final long serialVersionUID = -3879325787474538754L;
	@Id
	@Column(name = "name", nullable = false)
	private String name;
	@Column(name = "label", nullable = false)
	private String label;
	@Column(name = "oid", unique = true, nullable = true)
	private String oid;
	@Column(name = "create_timestamp", nullable = false)
	private Timestamp createTimestamp;
	@Column(nullable = false)
	private Timestamp timestamp;
	private String description;

	public IdentifierDomain()
	{
		createTimestamp = new Timestamp(System.currentTimeMillis());
	}

	public IdentifierDomain(IdentifierDomainDTO dto, Timestamp timestamp)
	{
		this.name = dto.getName();
		this.oid = dto.getOid() != null ? dto.getOid() : "";
		this.label = dto.getLabel() != null ? dto.getLabel() : "";
		this.createTimestamp = timestamp;
		this.timestamp = timestamp;
		this.description = dto.getDescription();
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getLabel()
	{
		return label;
	}

	public void setLabel(String label)
	{
		this.label = label;
	}

	public String getOid()
	{
		return oid;
	}

	public void setOid(String oid)
	{
		this.oid = oid;
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

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public void update(IdentifierDomainDTO dto) throws RuntimeException
	{
		this.label = dto.getLabel();
		this.description = dto.getDescription();
		this.oid = dto.getOid();
		this.timestamp = new Timestamp(System.currentTimeMillis());
	}

	public IdentifierDomainDTO toDTO()
	{
		return new IdentifierDomainDTO(name, label, oid, createTimestamp, timestamp, description);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		IdentifierDomain other = (IdentifierDomain) obj;
		if (name == null)
		{
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "IdentifierDomain [name=" + name + ", label=" + label + ", oid=" + oid + ", createTimestamp=" + createTimestamp + ", timestamp="
				+ timestamp + ", description=" + description + "]";
	}
}
