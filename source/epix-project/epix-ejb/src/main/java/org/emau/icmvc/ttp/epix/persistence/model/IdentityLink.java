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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.emau.icmvc.ttp.epix.common.model.IdentifierDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityOutDTO;
import org.emau.icmvc.ttp.epix.common.model.MPIIdentityDTO;
import org.emau.icmvc.ttp.epix.common.model.PossibleMatchDTO;
import org.emau.icmvc.ttp.epix.common.model.PossibleMatchForMPIDTO;

/**
 * 
 * @author geidell
 *
 */
@Entity
@Table(name = "identitylink")
@TableGenerator(name = "identitylink_index", table = "sequence", initialValue = 0, allocationSize = 50)
@NamedQueries({
		@NamedQuery(name = "IdentityLink.findByIdentities", query = "SELECT il FROM IdentityLink il WHERE (il.destIdentity = :identity1 and il.srcIdentity = :identity2) or (il.destIdentity = :identity2 and il.srcIdentity = :identity1)"),
		@NamedQuery(name = "IdentityLink.findByIdentity", query = "SELECT il FROM IdentityLink il WHERE (il.destIdentity = :identity or il.srcIdentity = :identity)"),
		@NamedQuery(name = "IdentityLink.findByPerson", query = "SELECT il FROM IdentityLink il WHERE il.destIdentity.person = :person or il.srcIdentity.person = :person") })
public class IdentityLink implements Serializable
{
	private static final long serialVersionUID = -6198083103032389316L;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "identitylink_index")
	private long id;
	private double threshold = 0.0;
	private String algorithm;
	@Column(name = "create_timestamp", nullable = false)
	private Timestamp createTimestamp;

	@OneToOne(cascade = CascadeType.MERGE, optional = false)
	@JoinColumn(name = "src_identity")
	private Identity srcIdentity;
	@OneToOne(cascade = CascadeType.MERGE, optional = false)
	@JoinColumn(name = "dest_identity")
	private Identity destIdentity;

	public IdentityLink()
	{
		this.createTimestamp = new Timestamp(System.currentTimeMillis());
	}

	public IdentityLink(Identity srcIdentity, Identity destIdentity, String algorithm, double threshold, Timestamp timestamp)
	{
		super();
		this.srcIdentity = srcIdentity;
		this.destIdentity = destIdentity;
		this.algorithm = algorithm;
		this.threshold = threshold;
		this.createTimestamp = timestamp;
	}

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public Identity getSrcIdentity()
	{
		return srcIdentity;
	}

	public void setSrcIdentity(Identity srcIdentity)
	{
		this.srcIdentity = srcIdentity;
	}

	public Identity getDestIdentity()
	{
		return destIdentity;
	}

	public void setDestIdentity(Identity destIdentity)
	{
		this.destIdentity = destIdentity;
	}

	public double getThreshold()
	{
		return threshold;
	}

	public void setThreshold(double threshold)
	{
		this.threshold = threshold;
	}

	public String getAlgorithm()
	{
		return algorithm;
	}

	public void setAlgorithm(String algorithm)
	{
		this.algorithm = algorithm;
	}

	public Timestamp getCreateTimestamp()
	{
		return createTimestamp;
	}

	public void setCreateTimestamp(Timestamp createTimestamp)
	{
		this.createTimestamp = createTimestamp;
	}

	public PossibleMatchDTO toDTO()
	{
		return new PossibleMatchDTO(new MPIIdentityDTO(srcIdentity.getPerson().getFirstMPI().toDTO(), srcIdentity.toDTO()),
				new MPIIdentityDTO(destIdentity.getPerson().getFirstMPI().toDTO(), destIdentity.toDTO()), id, threshold,
				createTimestamp == null ? null : new Date(createTimestamp.getTime()));
	}

	public PossibleMatchForMPIDTO toDTOForMPI(String mpiId)
	{
		IdentifierDTO mpi;
		IdentityOutDTO assignedIdentity;
		MPIIdentityDTO matchingMPIIdentity;
		if (srcIdentity.getPerson().getFirstMPI().getValue().equals(mpiId))
		{
			mpi = srcIdentity.getPerson().getFirstMPI().toDTO();
			assignedIdentity = srcIdentity.toDTO();
			matchingMPIIdentity = new MPIIdentityDTO(destIdentity.getPerson().getFirstMPI().toDTO(), destIdentity.toDTO());
		}
		else
		{
			mpi = destIdentity.getPerson().getFirstMPI().toDTO();
			assignedIdentity = destIdentity.toDTO();
			matchingMPIIdentity = new MPIIdentityDTO(srcIdentity.getPerson().getFirstMPI().toDTO(), srcIdentity.toDTO());
		}
		return new PossibleMatchForMPIDTO(mpi, assignedIdentity, matchingMPIIdentity, id, threshold,
				createTimestamp == null ? null : new Date(createTimestamp.getTime()));
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
		IdentityLink other = (IdentityLink) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "IdentityLink [id=" + id + ", threshold=" + threshold + ", algorithm=" + algorithm + ", createTimestamp=" + createTimestamp
				+ ", srcIdentityId=" + (srcIdentity == null ? "null" : srcIdentity.getId()) + ", destIdentityId="
				+ (destIdentity == null ? "null" : destIdentity.getId()) + "]";
	}
}
