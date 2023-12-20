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
import org.emau.icmvc.ttp.epix.common.model.PersonBaseDTO;
import org.emau.icmvc.ttp.epix.common.model.PersonHistoryDTO;
import org.emau.icmvc.ttp.epix.common.model.enums.PersonHistoryEvent;

/**
 * 
 * @author geidell
 *
 */
@Entity
@Table(name = "person_history")
@TableGenerator(name = "person_history_index", table = "sequence", initialValue = 0, allocationSize = 50)
@Cacheable(false)
@NamedQueries({ @NamedQuery(name = "PersonHistory.findByPerson", query = "SELECT ph FROM PersonHistory ph WHERE ph.person = :person"), })
public class PersonHistory implements Serializable
{
	@Serial
	private static final long serialVersionUID = -2732749371894104311L;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "person_history_index")
	private long id;
	@Column(columnDefinition = "BIT", length = 1)
	private boolean deactivated;
	@Column(name = "history_timestamp", nullable = false)
	private Timestamp historyTimestamp;
	@Column(name = "domain_name")
	private String domainName;
	private String comment;
	@ManyToOne(optional = false)
	private Person person;
	@ManyToOne(optional = false)
	@JoinColumns({ @JoinColumn(name = "first_mpi_identifier_domain_name", referencedColumnName = "identifier_domain_name"),
			@JoinColumn(name = "first_mpi_value", referencedColumnName = "value") })
	private Identifier firstMpi;
	private String user;
	@Column(columnDefinition = "char(20)")
	@Enumerated(EnumType.STRING)
	private PersonHistoryEvent event;


	public PersonHistory()
	{
		historyTimestamp = new Timestamp(System.currentTimeMillis());
	}

	public PersonHistory(Person person, PersonHistoryEvent event, String comment, Timestamp timestamp, String user)
	{
		this.person = person;
		deactivated = person.isDeactivated();
		firstMpi = person.getFirstMPI();
		historyTimestamp = timestamp;
		domainName = person.getDomain().getName();
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

	public boolean isDeactivated()
	{
		return deactivated;
	}

	public void setDeactivated(boolean deactivated)
	{
		this.deactivated = deactivated;
	}

	public Timestamp getHistoryTimestamp()
	{
		return historyTimestamp;
	}

	public void setHistoryTimestamp(Timestamp historyTimestamp)
	{
		this.historyTimestamp = historyTimestamp;
	}

	public Person getPerson()
	{
		return person;
	}

	public void setPerson(Person person)
	{
		this.person = person;
	}

	public Identifier getFirstMpi()
	{
		return firstMpi;
	}

	public void setFirstMpi(Identifier firstMpi)
	{
		this.firstMpi = firstMpi;
	}

	public String getDomainName()
	{
		return domainName;
	}

	public void setDomainName(String domainName)
	{
		this.domainName = domainName;
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

	public PersonHistoryEvent getEvent()
	{
		return event;
	}

	public void setEvent(PersonHistoryEvent event)
	{
		this.event = event;
	}

	public PersonHistoryDTO toDTO()
	{
		PersonBaseDTO personBase = new PersonBaseDTO(person.getId(), deactivated, new Date(person.getCreateTimestamp().getTime()),
				new Date(person.getTimestamp().getTime()), firstMpi.toDTO(), domainName);
		return new PersonHistoryDTO(personBase, id, new Date(historyTimestamp.getTime()), event, comment, user);
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
		PersonHistory other = (PersonHistory) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "PersonHistory [id=" + id + ", historyTimestamp=" + historyTimestamp
				+ (event != null ? ", event=" + event : "")
				+ ", domainName=" + domainName
				+ ", deactivated=" + deactivated
				+ ", firstMpi=" + firstMpi
				+ ", person=" + person
				+ (Strings.isNotBlank(comment) ? ", comment=" + comment : "")
				+ (Strings.isNotBlank(user) ? ", user=" + user : "")
				+ "]";
	}
}
