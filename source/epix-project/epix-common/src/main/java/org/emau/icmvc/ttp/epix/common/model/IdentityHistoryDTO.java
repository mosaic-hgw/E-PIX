package org.emau.icmvc.ttp.epix.common.model;

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


import java.util.Date;

import org.emau.icmvc.ttp.epix.common.model.enums.IdentityHistoryEvent;

/**
 * 
 * @author geidell
 *
 */
public class IdentityHistoryDTO extends IdentityOutBaseDTO
{
	private static final long serialVersionUID = -6148149007417877446L;
	private long historyId;
	private Date historyTimestamp;
	private IdentityHistoryEvent event;
	private Double matchingScore;
	private String comment;

	public IdentityHistoryDTO()
	{
		super();
	}

	public IdentityHistoryDTO(IdentityOutBaseDTO superDTO, long historyId, Date historyTimestamp, IdentityHistoryEvent event, Double matchingScore,
			String comment)
	{
		super(superDTO);
		this.historyId = historyId;
		this.historyTimestamp = historyTimestamp;
		this.event = event;
		this.matchingScore = matchingScore;
		this.comment = comment;
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

	public IdentityHistoryEvent getEvent()
	{
		return event;
	}

	public void setEvent(IdentityHistoryEvent event)
	{
		this.event = event;
	}

	public Double getMatchingScore()
	{
		return matchingScore;
	}

	public void setMatchingScore(Double matchingScore)
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

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((comment == null) ? 0 : comment.hashCode());
		result = prime * result + ((event == null) ? 0 : event.hashCode());
		result = prime * result + (int) (historyId ^ (historyId >>> 32));
		result = prime * result + ((historyTimestamp == null) ? 0 : historyTimestamp.hashCode());
		result = prime * result + ((matchingScore == null) ? 0 : matchingScore.hashCode());
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
		IdentityHistoryDTO other = (IdentityHistoryDTO) obj;
		if (comment == null)
		{
			if (other.comment != null)
				return false;
		}
		else if (!comment.equals(other.comment))
			return false;
		if (event != other.event)
			return false;
		if (historyId != other.historyId)
			return false;
		if (historyTimestamp == null)
		{
			if (other.historyTimestamp != null)
				return false;
		}
		else if (!historyTimestamp.equals(other.historyTimestamp))
			return false;
		if (matchingScore == null)
		{
			if (other.matchingScore != null)
				return false;
		}
		else if (!matchingScore.equals(other.matchingScore))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "IdentityHistoryDTO [historyId=" + historyId + ", historyTimestamp=" + historyTimestamp + ", event=" + event + ", matchingScore="
				+ matchingScore + ", comment=" + comment + "]";
	}
}
