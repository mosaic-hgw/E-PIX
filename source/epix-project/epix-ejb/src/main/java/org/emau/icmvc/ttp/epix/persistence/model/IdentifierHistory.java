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


import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.logging.log4j.util.Strings;
import org.emau.icmvc.ttp.epix.common.model.IdentifierHistoryDTO;
import org.emau.icmvc.ttp.epix.common.model.enums.IdentifierHistoryEvent;

/**
 * 
 * @author moser
 *
 */
@Entity
@Table(name = "identifier_history")
@TableGenerator(name = "identifier_history_index", table = "sequence", initialValue = 0, allocationSize = 50)
@Cacheable(false)
@NamedQueries({ @NamedQuery(name = "IdentifierHistory.findByIdentifier",
		query = "SELECT ih FROM IdentifierHistory ih WHERE ih.identifier = :identifier"), })
public class IdentifierHistory implements Serializable
{
	@Serial
	private static final long serialVersionUID = -5465498743788208622L;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "identifier_history_index")
	private long id;
	@ManyToOne(optional = false)
	@JoinColumns({ @JoinColumn(name = "identifier_domain_name", referencedColumnName = "identifier_domain_name"),
			@JoinColumn(name = "value", referencedColumnName = "value") })
	private Identifier identifier;
	@Column(name = "history_timestamp", nullable = false)
	private Timestamp historyTimestamp;
	@Column(columnDefinition = "BIT", length = 1)
	private boolean active = true;
	private String description;
	private String comment;
	private String user;
	@Column(columnDefinition = "char(20)")
	@Enumerated(EnumType.STRING)
	private IdentifierHistoryEvent event;

	public IdentifierHistory()
	{
		historyTimestamp = new Timestamp(System.currentTimeMillis());
	}

	public IdentifierHistory(Identifier identifier, IdentifierHistoryEvent event, String comment, Timestamp timestamp, String user)
	{
		this.identifier = identifier;
		historyTimestamp = timestamp;
		this.comment = comment;
		this.user = user;
		this.event = event;
	}

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public Identifier getIdentifier()
	{
		return identifier;
	}

	public void setIdentifier(Identifier identifier)
	{
		this.identifier = identifier;
	}

	public Timestamp getHistoryTimestamp()
	{
		return historyTimestamp;
	}

	public void setHistoryTimestamp(Timestamp historyTimestamp)
	{
		this.historyTimestamp = historyTimestamp;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public boolean isActive()
	{
		return active;
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}

	public String getComment()
	{
		return comment;
	}

	public void setComment(String comment)
	{
		this.comment = comment;
	}

	public String getUser()
	{
		return user;
	}

	public void setUser(String user)
	{
		this.user = user;
	}

	public IdentifierHistoryEvent getEvent()
	{
		return event;
	}

	public void setEvent(IdentifierHistoryEvent event)
	{
		this.event = event;
	}

	public IdentifierHistoryDTO toDTO()
	{
		return new IdentifierHistoryDTO(identifier.toDTO(), id, new Date(historyTimestamp.getTime()), event, comment, user);
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
		IdentifierHistory other = (IdentifierHistory) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "IdentifierHistory [id=" + id + ", historyTimestamp=" + historyTimestamp
				+ (event != null ? ", event=" + event : "")
				+ ", identifier=" + identifier
				+ ", description=" + description
				+ ", active=" + active
				+ (Strings.isNotBlank(comment) ? ", comment=" + comment : "")
				+ (Strings.isNotBlank(user) ? ", user=" + user : "")
				+ "]";
	}
}
