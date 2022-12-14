package org.emau.icmvc.ttp.deduplication.config.model;

/*
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.List;

import org.emau.icmvc.ttp.epix.common.model.config.DeduplicationDTO;
import org.emau.icmvc.ttp.epix.common.model.config.ReasonDTO;

/**
 * @author Christopher Hampf
 */
@XmlAccessorType(XmlAccessType.FIELD) @XmlType(name = "Deduplication", propOrder = { "reasons" }) public class Deduplication
{
	@XmlElement(name = "reason") private final List<Reason> reasons = new ArrayList<>();

	public Deduplication()
	{
		super();
	}

	public Deduplication(DeduplicationDTO dto)
	{
		super();

		List<ReasonDTO> tmp = dto.getReasons();
		if (tmp != null)
		{
			for (ReasonDTO rtmp : tmp)
			{
				Reason r = new Reason();
				r.setName(rtmp.getName());
				r.setDescription(rtmp.getDescription());

				reasons.add(r);
			}
		}
	}

	public DeduplicationDTO toDTO()
	{
		List<ReasonDTO> tmpReason = new ArrayList<>();
		for (Reason r : reasons)
		{
			tmpReason.add(r.toDTO());
		}

		return new DeduplicationDTO(tmpReason);
	}

	@Override public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null || getClass() != obj.getClass())
		{
			return false;
		}
		Deduplication other = (Deduplication) obj;
		return reasons.equals(other.reasons);
	}

	@Override public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + reasons.hashCode();
		return result;
	}

	@Override public String toString()
	{
		return "Deduplication [" +
				"reasons=" + reasons +
				']';
	}
}
