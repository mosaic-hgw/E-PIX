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

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.emau.icmvc.ttp.epix.common.model.StatisticDTO;

@Entity
@Table(name = "stat_entry")
@TableGenerator(name = "statistic_index", table = "sequence", initialValue = 0, allocationSize = 50)
public class Statistic implements Serializable
{
	private static final long serialVersionUID = 3287994464965041832L;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "statistic_index")
	private long stat_entry_id;
	@Temporal(TemporalType.TIMESTAMP)
	@Column
	private Date entrydate;
	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "stat_value", joinColumns = { @JoinColumn(name = "stat_value_id") })
	@MapKeyColumn(name = "stat_attr", length = 50)
	@Column(name = "stat_value")
	private Map<String, Long> mappedStatValue = new HashMap<>();

	public Statistic()
	{}

	public Statistic(Date entrydate, Map<String, Long> mappedStatValue)
	{
		this.entrydate = entrydate;
		this.mappedStatValue = mappedStatValue;
	}

	public Statistic(StatisticDTO dto)
	{
		this.entrydate = dto.getEntrydate();
		if (dto.getMappedStatValue() != null)
		{
			this.mappedStatValue.putAll(dto.getMappedStatValue());
		}
	}

	public long getId()
	{
		return stat_entry_id;
	}

	public void setId(int id)
	{
		this.stat_entry_id = id;
	}

	public Date getEntrydate()
	{
		return entrydate;
	}

	public void setEntrydate(Date entrydate)
	{
		this.entrydate = entrydate;
	}

	public Map<String, Long> getMappedStatValue()
	{
		return new HashMap<>(mappedStatValue);
	}

	public void setMappedStatValue(Map<String, Long> mappedStatValue)
	{
		if (mappedStatValue != null)
		{
			this.mappedStatValue.clear();
			this.mappedStatValue.putAll(mappedStatValue);
		}
	}

	public StatisticDTO toDTO()
	{
		return new StatisticDTO(stat_entry_id, entrydate, mappedStatValue);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (stat_entry_id ^ stat_entry_id >>> 32);
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
		Statistic other = (Statistic) obj;
		if (stat_entry_id != other.stat_entry_id)
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
		sb.append(stat_entry_id);
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
