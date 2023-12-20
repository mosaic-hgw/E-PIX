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

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.StringJoiner;

import org.emau.icmvc.ttp.epix.common.model.enums.PossibleMatchPriority;

public abstract class PossibleMatchBaseDTO implements Serializable
{
	private static final long serialVersionUID = -8293993278414725888L;
	public static final PossibleMatchPriority DEFAULT_PRIORITY = PossibleMatchPriority.OPEN;
	protected long linkId;
	protected double probability;
	protected Date possibleMatchCreated;
	protected PossibleMatchPriority priority;

	public PossibleMatchBaseDTO()
	{}

	public PossibleMatchBaseDTO(long linkId, double probability, Date possibleMatchCreated, PossibleMatchPriority priority)
	{
		this.linkId = linkId;
		this.probability = probability;
		setPossibleMatchCreated(possibleMatchCreated);
		setPriority(priority);
	}

	public PossibleMatchBaseDTO(PossibleMatchBaseDTO dto)
	{
		this(dto.getLinkId(), dto.getProbability(), dto.getPossibleMatchCreated(), dto.getPriority());
	}

	public long getLinkId()
	{
		return linkId;
	}

	public void setLinkId(long linkId)
	{
		this.linkId = linkId;
	}

	public double getProbability()
	{
		return probability;
	}

	public void setProbability(double probability)
	{
		this.probability = probability;
	}

	public Date getPossibleMatchCreated()
	{
		return possibleMatchCreated;
	}

	public void setPossibleMatchCreated(Date possibleMatchCreated)
	{
		this.possibleMatchCreated = possibleMatchCreated != null ? new Date(possibleMatchCreated.getTime()) : null;
	}

	public PossibleMatchPriority getPriority()
	{
		return priority != null ? priority : DEFAULT_PRIORITY;
	}

	public void setPriority(PossibleMatchPriority priority)
	{
		this.priority = priority != null ? priority : DEFAULT_PRIORITY;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof PossibleMatchBaseDTO that))
		{
			return false;
		}
		return getLinkId() == that.getLinkId() && Objects.equals(getProbability(), that.getProbability()) && Objects.equals(getPossibleMatchCreated(), that.getPossibleMatchCreated())
				&& getPriority() == that.getPriority();
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(getLinkId(), getProbability(), getPossibleMatchCreated(), getPriority());
	}

	@Override
	public String toString()
	{
		return new StringJoiner(", ", PossibleMatchBaseDTO.class.getSimpleName() + "[", "]")
				.add(toStringBase())
				.toString();
	}

	protected String toStringBase()
	{
		return new StringJoiner(", ", "", "")
				.add("linkId=" + linkId)
				.add("probability=" + probability)
				.add("possibleMatchCreated=" + possibleMatchCreated)
				.add("priority=" + getPriority())
				.toString();
	}
}
