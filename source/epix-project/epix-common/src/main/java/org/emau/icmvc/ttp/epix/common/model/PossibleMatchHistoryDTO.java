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


import java.io.Serializable;
import java.util.Date;

import org.emau.icmvc.ttp.epix.common.model.enums.PossibleMatchSolution;

/**
 * 
 * @author geidell
 *
 */
public class PossibleMatchHistoryDTO implements Serializable
{
	private static final long serialVersionUID = -1717508050127614356L;
	private long id;
	private long identity1Id;
	private long identity2Id;
	private long identityLinkId;
	private Double threshold;
	private String algorithm;
	private Date historyTimestamp;
	/**
	 * could be -1 which means that there isn't any linked updated identity
	 */
	private long updatedIdentityId;
	private long person1Id;
	private long person2Id;
	private PossibleMatchSolution solution;
	private String explanation;

	public PossibleMatchHistoryDTO()
	{}

	public PossibleMatchHistoryDTO(long id, long identity1Id, long identity2Id, long identityLinkId, Double threshold, String algorithm,
			Date historyTimestamp, long updatedIdentityId, long person1Id, long person2Id, PossibleMatchSolution solution, String explanation)
	{
		super();
		this.id = id;
		this.identity1Id = identity1Id;
		this.identity2Id = identity2Id;
		this.threshold = threshold;
		this.algorithm = algorithm;
		this.historyTimestamp = new Date(historyTimestamp.getTime());
		this.updatedIdentityId = updatedIdentityId;
		this.person1Id = person1Id;
		this.person2Id = person2Id;
		this.solution = solution;
		this.explanation = explanation;
	}

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public long getIdentity1Id()
	{
		return identity1Id;
	}

	public void setIdentity1Id(long identity1Id)
	{
		this.identity1Id = identity1Id;
	}

	public long getIdentity2Id()
	{
		return identity2Id;
	}

	public void setIdentity2Id(long identity2Id)
	{
		this.identity2Id = identity2Id;
	}

	public long getIdentityLinkId()
	{
		return identityLinkId;
	}

	public void setIdentityLinkId(long identityLinkId)
	{
		this.identityLinkId = identityLinkId;
	}

	public Double getThreshold()
	{
		return threshold;
	}

	public void setThreshold(Double threshold)
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

	public Date getHistoryTimestamp()
	{
		return historyTimestamp;
	}

	public void setHistoryTimestamp(Date historyTimestamp)
	{
		this.historyTimestamp = historyTimestamp;
	}

	/**
	 * could be -1 which means that there isn't any linked updated identity
	 */
	public long getUpdatedIdentityId()
	{
		return updatedIdentityId;
	}

	public void setUpdatedIdentityId(long updatedIdentityId)
	{
		this.updatedIdentityId = updatedIdentityId;
	}

	public long getPerson1Id()
	{
		return person1Id;
	}

	public void setPerson1Id(long person1Id)
	{
		this.person1Id = person1Id;
	}

	public long getPerson2Id()
	{
		return person2Id;
	}

	public void setPerson2Id(long person2Id)
	{
		this.person2Id = person2Id;
	}

	public PossibleMatchSolution getSolution()
	{
		return solution;
	}

	public void setSolution(PossibleMatchSolution solution)
	{
		this.solution = solution;
	}

	public String getExplanation()
	{
		return explanation;
	}

	public void setExplanation(String explanation)
	{
		this.explanation = explanation;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((algorithm == null) ? 0 : algorithm.hashCode());
		result = prime * result + ((explanation == null) ? 0 : explanation.hashCode());
		result = prime * result + ((historyTimestamp == null) ? 0 : historyTimestamp.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + (int) (identity1Id ^ (identity1Id >>> 32));
		result = prime * result + (int) (identity2Id ^ (identity2Id >>> 32));
		result = prime * result + (int) (identityLinkId ^ (identityLinkId >>> 32));
		result = prime * result + (int) (person1Id ^ (person1Id >>> 32));
		result = prime * result + (int) (person2Id ^ (person2Id >>> 32));
		result = prime * result + ((solution == null) ? 0 : solution.hashCode());
		result = prime * result + ((threshold == null) ? 0 : threshold.hashCode());
		result = prime * result + (int) (updatedIdentityId ^ (updatedIdentityId >>> 32));
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
		PossibleMatchHistoryDTO other = (PossibleMatchHistoryDTO) obj;
		if (algorithm == null)
		{
			if (other.algorithm != null)
				return false;
		}
		else if (!algorithm.equals(other.algorithm))
			return false;
		if (explanation == null)
		{
			if (other.explanation != null)
				return false;
		}
		else if (!explanation.equals(other.explanation))
			return false;
		if (historyTimestamp == null)
		{
			if (other.historyTimestamp != null)
				return false;
		}
		else if (!historyTimestamp.equals(other.historyTimestamp))
			return false;
		if (id != other.id)
			return false;
		if (identity1Id != other.identity1Id)
			return false;
		if (identity2Id != other.identity2Id)
			return false;
		if (identityLinkId != other.identityLinkId)
			return false;
		if (person1Id != other.person1Id)
			return false;
		if (person2Id != other.person2Id)
			return false;
		if (solution != other.solution)
			return false;
		if (threshold == null)
		{
			if (other.threshold != null)
				return false;
		}
		else if (!threshold.equals(other.threshold))
			return false;
		if (updatedIdentityId != other.updatedIdentityId)
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "PossibleMatchHistoryDTO [id=" + id + ", identity1Id=" + identity1Id + ", identity2Id=" + identity2Id + ", identityLinkId="
				+ identityLinkId + ", threshold=" + threshold + ", algorithm=" + algorithm + ", historyTimestamp=" + historyTimestamp
				+ ", updatedIdentityId=" + updatedIdentityId + ", person1Id=" + person1Id + ", person2Id=" + person2Id + ", solution=" + solution
				+ ", explanation=" + explanation + "]";
	}
}
