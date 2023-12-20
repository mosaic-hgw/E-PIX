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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.emau.icmvc.ttp.epix.common.model.IdentityOutDTO;
import org.emau.icmvc.ttp.epix.common.model.PersonBaseDTO;
import org.emau.icmvc.ttp.epix.common.model.PersonDTO;

/**
 *
 * @author geidell
 *
 */
@Entity
@Table(name = "person")
@TableGenerator(name = "person_index", table = "sequence", initialValue = 0, allocationSize = 50)
@Cacheable(false)
@NamedQueries({
		@NamedQuery(name = "Person.findByFirstMPIForDomain", query = "SELECT p FROM Person p WHERE p.domain = :domain AND p.firstMPI = :mpi"),
		@NamedQuery(name = "Person.findByLocalIdentifierForDomain", query = "SELECT DISTINCT p FROM Person p JOIN p.identities i JOIN i.identifiers idf WHERE p.domain = :domain AND p.deactivated = false AND idf = :identifier"),
		@NamedQuery(name = "Person.findDeactivatedByDomain", query = "SELECT p FROM Person p WHERE p.domain = :domain AND p.deactivated = true") })
public class Person implements Serializable
{
	private static final long serialVersionUID = 3792217232378119545L;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "person_index")
	private long id;
	@Column(columnDefinition = "BIT", length = 1)
	private boolean deactivated;
	@Column(name = "create_timestamp", nullable = false)
	private Timestamp createTimestamp;
	@Column(nullable = false)
	private Timestamp timestamp;
	// CascadeType.ALL (or more specifically CascadeType.REMOVE) is not allowed here, because after reassigning an identity
	// this person's firstMPI will persist in the 'identifier_identity' table associated with the reassigned identity
	// to allow finding the new identity (and the associated person) by querying the old mpi.
	// So after deleting a person, deleting its first mpi must be handled manually!
	@OneToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH }, optional = false)
	@JoinColumns({ @JoinColumn(name = "first_mpi_identifier_domain_name", referencedColumnName = "identifier_domain_name"),
			@JoinColumn(name = "first_mpi_value", referencedColumnName = "value") })
	private Identifier firstMPI;
	@ManyToOne(cascade = CascadeType.MERGE, optional = false)
	@JoinColumn(name = "domain_name")
	private Domain domain;
	@OneToMany(mappedBy = "person", fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	private List<Identity> identities = new ArrayList<>();

	public Person()
	{
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		this.timestamp = timestamp;
		this.createTimestamp = timestamp;
	}

	public Person(boolean deactivated, Identifier firstMPI, Domain domain, List<Identity> identities, Timestamp timestamp)
	{
		this.deactivated = deactivated;
		this.firstMPI = firstMPI;
		this.domain = domain;
		this.identities = identities;
		this.createTimestamp = timestamp;
		this.timestamp = timestamp;
	}

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public boolean isDeactivated()
	{
		return deactivated;
	}

	public void setDeactivated(boolean deactivated)
	{
		this.deactivated = deactivated;
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

	public Identifier getFirstMPI()
	{
		return firstMPI;
	}

	public void setFirstMPI(Identifier firstMPI)
	{
		this.firstMPI = firstMPI;
	}

	public Domain getDomain()
	{
		return domain;
	}

	public void setDomain(Domain domain)
	{
		this.domain = domain;
	}

	public List<Identity> getIdentities()
	{
		return identities;
	}

	public void setIdentities(List<Identity> identities)
	{
		this.identities = identities;
	}

	public Identity getReferenceIdentity()
	{
		Identity referenceIdentity = null;
		for (Identity identity : identities)
		{
			if (identity.isDeactivated())
			{
				continue;
			}
			if (referenceIdentity == null)
			{
				referenceIdentity = identity;
			}
			else if (!referenceIdentity.isPossibleReference()
					&& (identity.isPossibleReference() || identity.getTimestamp().after(referenceIdentity.getTimestamp()))
					|| referenceIdentity.isPossibleReference() && identity.isPossibleReference()
							&& identity.getTimestamp().after(referenceIdentity.getTimestamp()))
			{
				referenceIdentity = identity;
			}
		}
		return referenceIdentity;
	}

	public PersonDTO toDTO()
	{
		List<IdentityOutDTO> identDTOs = new ArrayList<>();
		Identity referenceIdentity = null;
		for (Identity identity : identities)
		{
			if (identity.isDeactivated())
			{
				continue;
			}
			if (referenceIdentity == null)
			{
				referenceIdentity = identity;
			}
			else if (!referenceIdentity.isPossibleReference()
					&& (identity.isPossibleReference() || identity.getTimestamp().after(referenceIdentity.getTimestamp()))
					|| referenceIdentity.isPossibleReference() && identity.isPossibleReference()
							&& identity.getTimestamp().after(referenceIdentity.getTimestamp()))
			{
				identDTOs.add(referenceIdentity.toDTO());
				referenceIdentity = identity;
			}
			else
			{
				identDTOs.add(identity.toDTO());
			}
		}
		PersonBaseDTO personBase = new PersonBaseDTO(id, deactivated, new Date(createTimestamp.getTime()), new Date(timestamp.getTime()),
				firstMPI.toDTO(), domain.getName());
		return new PersonDTO(personBase, referenceIdentity != null ? referenceIdentity.toDTO() : null, identDTOs);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (domain == null ? 0 : domain.hashCode());
		result = prime * result + (firstMPI == null ? 0 : firstMPI.hashCode());
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
		Person other = (Person) obj;
		if (domain == null)
		{
			if (other.domain != null)
			{
				return false;
			}
		}
		else if (!domain.equals(other.domain))
		{
			return false;
		}
		if (firstMPI == null)
		{
			if (other.firstMPI != null)
			{
				return false;
			}
		}
		else if (!firstMPI.equals(other.firstMPI))
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		return "Person [id=" + id + ", deactivated=" + deactivated + ", createTimestamp=" + createTimestamp + ", timestamp=" + timestamp
				+ ", firstMPI=" + firstMPI + ", domain=" + (domain == null ? "null" : domain.getName()) + ", identities="
				+ (identities == null ? "null" : identities.stream().map(Object::toString).collect(Collectors.joining(", "))) + "]";
	}

	public String toLongString()
	{
		return "Person [id=" + id + ", deactivated=" + deactivated + ", createTimestamp=" + createTimestamp + ", timestamp=" + timestamp
				+ ", firstMPI=" + firstMPI + ", domain=" + (domain == null ? "null" : domain.getName()) + ", identities="
				+ (identities == null ? "null" : identities.stream().map(Identity::toLongString).collect(Collectors.joining(", "))) + "]";
	}
}
