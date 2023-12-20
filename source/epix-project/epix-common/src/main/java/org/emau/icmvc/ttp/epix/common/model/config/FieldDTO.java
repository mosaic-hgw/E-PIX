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

import org.emau.icmvc.ttp.epix.common.model.enums.BlockingMode;
import org.emau.icmvc.ttp.epix.common.model.enums.FieldName;

public class FieldDTO implements Serializable
{
	private static final long serialVersionUID = 8135910160259436944L;
	private FieldName name;
	private double blockingThreshold;
	private BlockingMode blockingMode;
	private double matchingThreshold;
	private double weight;
	private String algorithm;
	private char multipleValuesSeparator;
	private double penaltyNotAPerfectMatch;
	private double penaltyOneShort;
	private double penaltyBothShort;

	public FieldDTO()
	{}

	public FieldDTO(FieldName name, double blockingThreshold, BlockingMode blockingMode, double matchingThreshold, double weight, String algorithm,
			char multipleValuesSeparator, double penaltyNotAPerfectMatch, double penaltyOneShort, double penaltyBothShort)
	{
		super();
		this.name = name;
		this.blockingThreshold = blockingThreshold;
		this.blockingMode = blockingMode;
		this.matchingThreshold = matchingThreshold;
		this.weight = weight;
		this.algorithm = algorithm;
		this.multipleValuesSeparator = multipleValuesSeparator;
		this.penaltyNotAPerfectMatch = penaltyNotAPerfectMatch;
		this.penaltyOneShort = penaltyOneShort;
		this.penaltyBothShort = penaltyBothShort;
	}

	public FieldDTO(FieldDTO dto)
	{
		this(dto.getName(), dto.getBlockingThreshold(), dto.getBlockingMode(), dto.getMatchingThreshold(), dto.getWeight(), dto.getAlgorithm(), dto.getMultipleValuesSeparator(),
				dto.getPenaltyNotAPerfectMatch(), dto.getPenaltyOneShort(), dto.getPenaltyBothShort());
	}

	public FieldName getName()
	{
		return name;
	}

	public void setName(FieldName name)
	{
		this.name = name;
	}

	public double getBlockingThreshold()
	{
		return blockingThreshold;
	}

	public void setBlockingThreshold(double blockingThreshold)
	{
		this.blockingThreshold = blockingThreshold;
	}

	public BlockingMode getBlockingMode()
	{
		return blockingMode;
	}

	public void setBlockingMode(BlockingMode blockingMode)
	{
		this.blockingMode = blockingMode;
	}

	public double getMatchingThreshold()
	{
		return matchingThreshold;
	}

	public void setMatchingThreshold(double matchingThreshold)
	{
		this.matchingThreshold = matchingThreshold;
	}

	public double getWeight()
	{
		return weight;
	}

	public void setWeight(double weight)
	{
		this.weight = weight;
	}

	public String getAlgorithm()
	{
		return algorithm;
	}

	public void setAlgorithm(String algorithm)
	{
		this.algorithm = algorithm;
	}

	public char getMultipleValuesSeparator()
	{
		return multipleValuesSeparator;
	}

	public void setMultipleValuesSeparator(char multipleValuesSeparator)
	{
		this.multipleValuesSeparator = multipleValuesSeparator;
	}

	public double getPenaltyNotAPerfectMatch()
	{
		return penaltyNotAPerfectMatch;
	}

	public void setPenaltyNotAPerfectMatch(double penaltyNotAPerfectMatch)
	{
		this.penaltyNotAPerfectMatch = penaltyNotAPerfectMatch;
	}

	public double getPenaltyOneShort()
	{
		return penaltyOneShort;
	}

	public void setPenaltyOneShort(double penaltyOneShort)
	{
		this.penaltyOneShort = penaltyOneShort;
	}

	public double getPenaltyBothShort()
	{
		return penaltyBothShort;
	}

	public void setPenaltyBothShort(double penaltyBothShort)
	{
		this.penaltyBothShort = penaltyBothShort;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (algorithm == null ? 0 : algorithm.hashCode());
		result = prime * result + (blockingMode == null ? 0 : blockingMode.hashCode());
		long temp;
		temp = Double.doubleToLongBits(blockingThreshold);
		result = prime * result + (int) (temp ^ temp >>> 32);
		temp = Double.doubleToLongBits(matchingThreshold);
		result = prime * result + (int) (temp ^ temp >>> 32);
		result = prime * result + multipleValuesSeparator;
		result = prime * result + (name == null ? 0 : name.hashCode());
		temp = Double.doubleToLongBits(penaltyBothShort);
		result = prime * result + (int) (temp ^ temp >>> 32);
		temp = Double.doubleToLongBits(penaltyNotAPerfectMatch);
		result = prime * result + (int) (temp ^ temp >>> 32);
		temp = Double.doubleToLongBits(penaltyOneShort);
		result = prime * result + (int) (temp ^ temp >>> 32);
		temp = Double.doubleToLongBits(weight);
		result = prime * result + (int) (temp ^ temp >>> 32);
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
		FieldDTO other = (FieldDTO) obj;
		if (algorithm == null)
		{
			if (other.algorithm != null)
			{
				return false;
			}
		}
		else if (!algorithm.equals(other.algorithm))
		{
			return false;
		}
		if (blockingMode != other.blockingMode)
		{
			return false;
		}
		if (Double.doubleToLongBits(blockingThreshold) != Double.doubleToLongBits(other.blockingThreshold))
		{
			return false;
		}
		if (Double.doubleToLongBits(matchingThreshold) != Double.doubleToLongBits(other.matchingThreshold))
		{
			return false;
		}
		if (multipleValuesSeparator != other.multipleValuesSeparator)
		{
			return false;
		}
		if (name != other.name)
		{
			return false;
		}
		if (Double.doubleToLongBits(penaltyBothShort) != Double.doubleToLongBits(other.penaltyBothShort))
		{
			return false;
		}
		if (Double.doubleToLongBits(penaltyNotAPerfectMatch) != Double.doubleToLongBits(other.penaltyNotAPerfectMatch))
		{
			return false;
		}
		if (Double.doubleToLongBits(penaltyOneShort) != Double.doubleToLongBits(other.penaltyOneShort))
		{
			return false;
		}
		if (Double.doubleToLongBits(weight) != Double.doubleToLongBits(other.weight))
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		return "FieldDTO [name=" + name + ", blockingThreshold=" + blockingThreshold + ", blockingMode=" + blockingMode + ", matchingThreshold="
				+ matchingThreshold + ", weight=" + weight + ", algorithm=" + algorithm + ", multipleValuesSeparator=" + multipleValuesSeparator
				+ ", penaltyNotAPerfectMatch=" + penaltyNotAPerfectMatch + ", penaltyOneShort=" + penaltyOneShort + ", penaltyBothShort="
				+ penaltyBothShort + "]";
	}
}
