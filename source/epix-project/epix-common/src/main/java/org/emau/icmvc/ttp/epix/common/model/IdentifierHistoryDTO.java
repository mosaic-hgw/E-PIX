package org.emau.icmvc.ttp.epix.common.model;

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
import java.util.Date;

import org.apache.logging.log4j.util.Strings;
import org.emau.icmvc.ttp.epix.common.model.enums.IdentifierHistoryEvent;

/**
 * 
 * @author moser
 *
 */
public class IdentifierHistoryDTO extends IdentifierDTO
{
	@Serial
	private static final long serialVersionUID = 5480550935867775430L;
	private long historyId;
	private Date historyTimestamp;
	private String comment;
	private String user;
	private IdentifierHistoryEvent event;

	public IdentifierHistoryDTO()
	{}

	public IdentifierHistoryDTO(IdentifierDTO identifierDTO, long historyId, Date historyTimestamp, IdentifierHistoryEvent event, String comment, String user)
	{
		super(identifierDTO);
		this.historyId = historyId;
		this.historyTimestamp = historyTimestamp;
		this.comment = comment;
		this.user = user;
		this.event = event;
	}

	public long getHistoryId()
	{
		return historyId;
	}

	public void setHistoryId(long historyId)
	{
		this.historyId = historyId;
	}

	public Date getHistoryTimestamp()
	{
		return historyTimestamp;
	}

	public void setHistoryTimestamp(Date historyTimestamp)
	{
		this.historyTimestamp = historyTimestamp;
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

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (int) (historyId ^ (historyId >>> 32));
		result = prime * result + ((event == null) ? 0 : event.hashCode());
		result = prime * result + ((historyTimestamp == null) ? 0 : historyTimestamp.hashCode());
		result = prime * result + ((comment == null) ? 0 : comment.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		IdentifierHistoryDTO other = (IdentifierHistoryDTO) obj;
		if (historyId != other.historyId)
			return false;
		if (historyTimestamp == null)
		{
			if (other.historyTimestamp != null)
				return false;
		}
		else if (!historyTimestamp.equals(other.historyTimestamp))
			return false;
		if (event != other.event)
			return false;
		if (comment == null)
		{
			if (other.comment != null)
				return false;
		}
		else if (!comment.equals(other.comment))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "IdentifierHistoryDTO [historyId=" + historyId + "historyTimestamp=" + historyTimestamp
				+ (event != null ? ", event=" + event : "")
				+ ", including " + super.toString()
				+ (Strings.isNotBlank(comment) ? ", comment=" + comment : "")
				+ (Strings.isNotBlank(user) ? ", user=" + user : "")
				+ "]";
	}
}
