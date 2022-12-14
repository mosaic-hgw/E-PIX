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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.emau.icmvc.ttp.epix.common.model.PossibleMatchHistoryDTO;
import org.emau.icmvc.ttp.epix.common.model.enums.PossibleMatchSolution;

/**
 * 
 * @author geidell
 *
 */
@Entity
@Table(name = "identitylink_history")
@TableGenerator(name = "identitylink_history_index", table = "sequence", initialValue = 0, allocationSize = 50)
@Cacheable(false)
@NamedQueries({
		@NamedQuery(name = "IdentityLinkHistory.findByPerson", query = "SELECT il FROM IdentityLinkHistory il WHERE il.srcPerson = :person or il.destPerson = :person"),
		@NamedQuery(name = "IdentityLinkHistory.findByUpdatedIdentity", query = "SELECT il FROM IdentityLinkHistory il WHERE il.updatedIdentity = :updatedIdentity"),
		@NamedQuery(name = "IdentityLinkHistory.findByIdentity", query = "SELECT il FROM IdentityLinkHistory il WHERE (il.updatedIdentity = :identity OR il.srcIdentity = :identity OR il.destIdentity = :identity)"),})
public class IdentityLinkHistory implements Serializable
{
	private static final long serialVersionUID = -6823220640081892330L;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "identitylink_history_index")
	private long id;
	@OneToOne(cascade = CascadeType.MERGE, optional = false)
	@JoinColumn(name = "src_identity", nullable = false)
	private Identity srcIdentity;
	@OneToOne(cascade = CascadeType.MERGE, optional = false)
	@JoinColumn(name = "dest_identity", nullable = false)
	private Identity destIdentity;
	private double threshold;
	private String algorithm;
	@Column(name = "history_timestamp", nullable = false)
	private Timestamp historyTimestamp;
	@Column(columnDefinition = "char(14)")
	@Enumerated(EnumType.STRING)
	private PossibleMatchSolution event;
	private String comment;
	@Column(name = "identity_link_id")
	private long identityLinkId;
	@OneToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "updated_identity")
	private Identity updatedIdentity;
	@OneToOne(cascade = CascadeType.MERGE, optional = false)
	@JoinColumn(name = "src_person")
	private Person srcPerson;
	@OneToOne(cascade = CascadeType.MERGE, optional = false)
	@JoinColumn(name = "dest_person")
	private Person destPerson;

	public IdentityLinkHistory()
	{
		historyTimestamp = new Timestamp(System.currentTimeMillis());
	}

	public IdentityLinkHistory(IdentityLink identitylink, PossibleMatchSolution solution, String comment, Timestamp timestamp)
	{
		this.srcIdentity = identitylink.getSrcIdentity();
		this.destIdentity = identitylink.getDestIdentity();
		this.threshold = identitylink.getThreshold();
		this.algorithm = identitylink.getAlgorithm();
		this.srcPerson = srcIdentity.getPerson();
		this.destPerson = destIdentity.getPerson();
		this.event = solution;
		this.comment = comment;
		this.historyTimestamp = timestamp;
		this.identityLinkId = identitylink.getId();
	}

	public IdentityLinkHistory(IdentityLink identitylink, PossibleMatchSolution solution, String comment, Timestamp timestamp,
			Identity updatedIdentity)
	{
		this.srcIdentity = identitylink.getSrcIdentity();
		this.destIdentity = identitylink.getDestIdentity();
		this.updatedIdentity = updatedIdentity;
		this.threshold = identitylink.getThreshold();
		this.algorithm = identitylink.getAlgorithm();
		this.srcPerson = srcIdentity.getPerson();
		this.destPerson = destIdentity.getPerson();
		this.event = solution;
		this.comment = comment;
		this.historyTimestamp = timestamp;
		this.identityLinkId = identitylink.getId();
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

	public Timestamp getHistoryTimestamp()
	{
		return historyTimestamp;
	}

	public void setHistoryTimestamp(Timestamp historyTimestamp)
	{
		this.historyTimestamp = historyTimestamp;
	}

	public PossibleMatchSolution getEvent()
	{
		return event;
	}

	public void setEvent(PossibleMatchSolution event)
	{
		this.event = event;
	}

	public String getComment()
	{
		return comment;
	}

	public void setComment(String comment)
	{
		this.comment = comment;
	}

	public long getIdentityLinkId()
	{
		return identityLinkId;
	}

	public void setIdentityLinkId(long identityLinkId)
	{
		this.identityLinkId = identityLinkId;
	}

	public Identity getUpdatedIdentity()
	{
		return updatedIdentity;
	}

	public void setUpdatedIdentity(Identity updatedIdentity)
	{
		this.updatedIdentity = updatedIdentity;
	}

	public Person getSrcPerson()
	{
		return srcPerson;
	}

	public void setSrcPerson(Person srcPerson)
	{
		this.srcPerson = srcPerson;
	}

	public Person getDestPerson()
	{
		return destPerson;
	}

	public void setDestPerson(Person destPerson)
	{
		this.destPerson = destPerson;
	}

	public PossibleMatchHistoryDTO toDTO()
	{
		return new PossibleMatchHistoryDTO(id, srcIdentity.getId(), destIdentity.getId(), identityLinkId, threshold, algorithm, historyTimestamp,
				updatedIdentity != null ? updatedIdentity.getId() : -1, srcPerson.getId(), destPerson.getId(), event, comment);
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
		IdentityLinkHistory other = (IdentityLinkHistory) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "IdentityLinkHistory [id=" + id + ", srcIdentityId=" + (srcIdentity == null ? "null" : srcIdentity.getId()) + ", destIdentityId="
				+ (destIdentity == null ? "null" : destIdentity.getId()) + ", identityLinkId=" + identityLinkId + ", threshold=" + threshold
				+ ", algorithm=" + algorithm + ", historyTimestamp=" + historyTimestamp + ", updatedIdentityId="
				+ (updatedIdentity != null ? updatedIdentity.getId() : "null") + ", srcPersonId=" + (srcPerson == null ? "null" : srcPerson.getId())
				+ ", destPersonID=" + (destPerson == null ? "null" : destPerson.getId()) + ", event=" + event + ", comment=" + comment + "]";
	}
}
