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
import org.emau.icmvc.ttp.epix.common.model.enums.IdentityHistoryEvent;

/**
 *
 * @author geidell
 *
 */
public class IdentityHistoryDTO extends IdentityOutBaseDTO
{
	@Serial
	private static final long serialVersionUID = 7914394675246745984L;
	private long historyId;
	private Date historyTimestamp;
	private IdentityHistoryEvent event;
	private double matchingScore;
	private String comment;
	private String user;

	public IdentityHistoryDTO()
	{
		super();
	}

	public IdentityHistoryDTO(IdentityOutBaseDTO superDTO, long historyId, Date historyTimestamp, IdentityHistoryEvent event, double matchingScore,
			String comment, String user)
	{
		super(superDTO);
		this.historyId = historyId;
		setHistoryTimestamp(historyTimestamp);
		this.event = event;
		this.matchingScore = matchingScore;
		this.comment = comment;
		this.user = user;
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
		this.historyTimestamp = historyTimestamp != null ? new Date(historyTimestamp.getTime()) : null;
	}

	public IdentityHistoryEvent getEvent()
	{
		return event;
	}

	public void setEvent(IdentityHistoryEvent event)
	{
		this.event = event;
	}

	public double getMatchingScore()
	{
		return matchingScore;
	}

	public void setMatchingScore(double matchingScore)
	{
		this.matchingScore = matchingScore;
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

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (comment == null ? 0 : comment.hashCode());
		result = prime * result + (event == null ? 0 : event.hashCode());
		result = prime * result + (int) (historyId ^ historyId >>> 32);
		result = prime * result + (historyTimestamp == null ? 0 : historyTimestamp.hashCode());
		long temp;
		temp = Double.doubleToLongBits(matchingScore);
		result = prime * result + (int) (temp ^ temp >>> 32);
		result = prime * result + (user == null ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (!super.equals(obj))
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		IdentityHistoryDTO other = (IdentityHistoryDTO) obj;
		if (comment == null)
		{
			if (other.comment != null)
			{
				return false;
			}
		}
		else if (!comment.equals(other.comment))
		{
			return false;
		}
		if (event != other.event)
		{
			return false;
		}
		if (historyId != other.historyId)
		{
			return false;
		}
		if (historyTimestamp == null)
		{
			if (other.historyTimestamp != null)
			{
				return false;
			}
		}
		else if (!historyTimestamp.equals(other.historyTimestamp))
		{
			return false;
		}
		if (Double.doubleToLongBits(matchingScore) != Double.doubleToLongBits(other.matchingScore))
		{
			return false;
		}
		if (user == null)
		{
			if (other.user != null)
			{
				return false;
			}
		}
		else if (!user.equals(other.user))
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		return "IdentityHistoryDTO [historyId=" + historyId + ", historyTimestamp=" + historyTimestamp
				+ (event != null ? ", event=" + event : "")
				+ ", matchingScore=" + matchingScore
				+ (Strings.isNotBlank(comment) ? ", comment=" + comment : "")
				+ (Strings.isNotBlank(user) ? ", user=" + user : "")
				+ "]";
	}
}
