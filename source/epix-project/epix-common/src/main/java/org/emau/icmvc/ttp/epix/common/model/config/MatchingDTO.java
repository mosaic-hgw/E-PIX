package org.emau.icmvc.ttp.epix.common.model.config;

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
import java.util.ArrayList;
import java.util.List;

public class MatchingDTO implements Serializable
{
	private static final long serialVersionUID = -7695731282640723270L;
	private double thresholdPossibleMatch = 2.99;
	private double thresholdAutomaticMatch = 14.5;
	private boolean useCEMFIM = false;
	private int parallelMatchingAfter = 1000;
	private final List<FieldDTO> fields = new ArrayList<>();

	public MatchingDTO()
	{}

	public MatchingDTO(double thresholdPossibleMatch, double thresholdAutomaticMatch, boolean useCEMFIM, int parallelMatchingAfter,
			List<FieldDTO> fields)
	{
		super();
		this.thresholdPossibleMatch = thresholdPossibleMatch;
		this.thresholdAutomaticMatch = thresholdAutomaticMatch;
		this.useCEMFIM = useCEMFIM;
		this.parallelMatchingAfter = parallelMatchingAfter;
		setFields(fields);
	}

	public MatchingDTO(MatchingDTO dto)
	{
		this(dto.getThresholdPossibleMatch(), dto.getThresholdAutomaticMatch(), dto.isUseCEMFIM(), dto.getParallelMatchingAfter(), dto.getFields());
	}

	public double getThresholdPossibleMatch()
	{
		return thresholdPossibleMatch;
	}

	public void setThresholdPossibleMatch(double thresholdPossibleMatch)
	{
		this.thresholdPossibleMatch = thresholdPossibleMatch;
	}

	public double getThresholdAutomaticMatch()
	{
		return thresholdAutomaticMatch;
	}

	public void setThresholdAutomaticMatch(double thresholdAutomaticMatch)
	{
		this.thresholdAutomaticMatch = thresholdAutomaticMatch;
	}

	public boolean isUseCEMFIM()
	{
		return useCEMFIM;
	}

	public void setUseCEMFIM(boolean useCEMFIM)
	{
		this.useCEMFIM = useCEMFIM;
	}

	public int getParallelMatchingAfter()
	{
		return parallelMatchingAfter;
	}

	public void setParallelMatchingAfter(int parallelMatchingAfter)
	{
		this.parallelMatchingAfter = parallelMatchingAfter;
	}

	public List<FieldDTO> getFields()
	{
		return fields;
	}

	public void setFields(List<FieldDTO> fields)
	{
		this.fields.clear();
		if (fields != null)
		{
			for (FieldDTO fieldDTO : fields)
			{
				this.fields.add(new FieldDTO(fieldDTO));
			}
		}
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (fields == null ? 0 : fields.hashCode());
		result = prime * result + parallelMatchingAfter;
		long temp;
		temp = Double.doubleToLongBits(thresholdAutomaticMatch);
		result = prime * result + (int) (temp ^ temp >>> 32);
		temp = Double.doubleToLongBits(thresholdPossibleMatch);
		result = prime * result + (int) (temp ^ temp >>> 32);
		result = prime * result + (useCEMFIM ? 1231 : 1237);
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
		MatchingDTO other = (MatchingDTO) obj;
		if (fields == null)
		{
			if (other.fields != null)
			{
				return false;
			}
		}
		else if (!fields.equals(other.fields))
		{
			return false;
		}
		if (parallelMatchingAfter != other.parallelMatchingAfter)
		{
			return false;
		}
		if (Double.doubleToLongBits(thresholdAutomaticMatch) != Double.doubleToLongBits(other.thresholdAutomaticMatch))
		{
			return false;
		}
		if (Double.doubleToLongBits(thresholdPossibleMatch) != Double.doubleToLongBits(other.thresholdPossibleMatch))
		{
			return false;
		}
		if (useCEMFIM != other.useCEMFIM)
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		return "MatchingDTO [thresholdPossibleMatch=" + thresholdPossibleMatch + ", thresholdAutomaticMatch=" + thresholdAutomaticMatch
				+ ", useCEMFIM=" + useCEMFIM + ", parallelMatchingAfter=" + parallelMatchingAfter + ", fields=" + fields + "]";
	}
}
