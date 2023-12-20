package org.emau.icmvc.ttp.epix.common.model;

/*-
 * ###license-information-start###
 * gICS - a Generic Informed Consent Service
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
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

public class StatisticDTO implements Serializable
{
	private static final long serialVersionUID = 3248339807246508058L;
	private final long id;
	private final Date entrydate;
	private final Date entrydateWithoutTime;
	private final Map<String, Long> mappedStatValue = new LinkedHashMap<>();

	public StatisticDTO()
	{
		this.id = 0;
		this.entrydate = new Date();
		this.entrydateWithoutTime = stripTime(entrydate);
	}

	public StatisticDTO(long id, Date entrydate, Map<String, Long> mappedStatValue)
	{
		this.id = id;
		this.entrydate = entrydate != null ? new Date(entrydate.getTime()) : null;
		this.entrydateWithoutTime = stripTime(entrydate);
		if (mappedStatValue != null)
		{
			this.mappedStatValue.putAll(mappedStatValue);
		}
	}

	public StatisticDTO(StatisticDTO dto)
	{
		this(dto.getId(), dto.getEntrydate(), dto.getMappedStatValue());
	}

	private Date stripTime(Date date)
	{
		if (date == null)
		{
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	/**
	 * @return id
	 */
	@XmlElement(required = true)
	public long getId()
	{
		return id;
	}

	/**
	 * @return entrydate
	 */
	@XmlElement(required = true)
	public Date getEntrydate()
	{
		return entrydate;
	}

	/**
	 * @return entrydate without time component
	 */
	@XmlElement(required = true)
	public Date getEntrydateWithoutTime()
	{
		return entrydateWithoutTime;
	}

	/**
	 * @return mappedStatValue
	 */
	@XmlElement(required = true)
	public Map<String, Long> getMappedStatValue()
	{
		return mappedStatValue;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ id >>> 32);
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
		StatisticDTO other = (StatisticDTO) obj;
		if (id != other.id)
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("id: '");
		sb.append(id);
		sb.append("',  entrydate: ");
		sb.append(entrydate);
		sb.append(", values:");

		for (Map.Entry<String, Long> item : mappedStatValue.entrySet())
		{
			sb.append(" " + item.getKey() + "=" + item.getValue());
		}

		return sb.toString();
	}
}
