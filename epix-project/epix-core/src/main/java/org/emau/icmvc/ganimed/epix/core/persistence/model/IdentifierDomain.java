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
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.emau.icmvc.ganimed.epix.core.persistence.model.enums.IdentifierDomainTypeEnum;

@Entity
@Table(name = "identifier_domain")
@NamedQueries({
		@NamedQuery(name = "IdentifierDomain.getAllIdentifierDomains", query = "SELECT id FROM IdentifierDomain id"),
		@NamedQuery(name = "IdentifierDomain.getMpiDomainByProject", query = "SELECT id FROM Project p JOIN p.mpiDomain id WHERE p = :project AND id.domainType = :type") })
public class IdentifierDomain implements Serializable {

	private static final long serialVersionUID = 3322829059231218241L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "oid", nullable = false)
	private String oid;

	@Column(name = "entry_date")
	private Timestamp entryDate;

	@Column(name = "domain_type")
	@Enumerated(EnumType.STRING)
	private IdentifierDomainTypeEnum domainType;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public Timestamp getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(Timestamp entryDate) {
		this.entryDate = entryDate;
	}

	/**
	 * @return the domainType
	 */
	public IdentifierDomainTypeEnum getDomainType() {
		return domainType;
	}

	/**
	 * @param domainType
	 *            the domainType to set
	 */
	public void setDomainType(IdentifierDomainTypeEnum domainType) {
		this.domainType = domainType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((oid == null) ? 0 : oid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof IdentifierDomain))
			return false;
		IdentifierDomain other = (IdentifierDomain) obj;
		if (oid == null) {
			if (other.oid != null)
				return false;
		} else if (!oid.equals(other.oid))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "IdentifierDomain [id=" + id + ", name=" + name + ", oid=" + oid + ", entryDate=" + entryDate
				+ ", domainType=" + domainType + "]";
	}
}
