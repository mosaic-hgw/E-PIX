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

/**
 * 
 * @author geidell
 *
 */
public class PersonHistoryDTO extends PersonBaseDTO
{
	private static final long serialVersionUID = 5386731307005630372L;
	private long historyId;
	private Date historyTimestamp;

	public PersonHistoryDTO()
	{}

	public PersonHistoryDTO(PersonBaseDTO superDTO, long historyId, Date historyTimestamp)
	{
		super(superDTO);
		this.historyId = historyId;
		this.historyTimestamp = historyTimestamp;
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

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (int) (historyId ^ (historyId >>> 32));
		result = prime * result + ((historyTimestamp == null) ? 0 : historyTimestamp.hashCode());
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
		PersonHistoryDTO other = (PersonHistoryDTO) obj;
		if (historyId != other.historyId)
			return false;
		if (historyTimestamp == null)
		{
			if (other.historyTimestamp != null)
				return false;
		}
		else if (!historyTimestamp.equals(other.historyTimestamp))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "PersonHistoryDTO [historyId=" + historyId + "historyTimestamp=" + historyTimestamp + ", including " + super.toString() + "]";
	}
}
