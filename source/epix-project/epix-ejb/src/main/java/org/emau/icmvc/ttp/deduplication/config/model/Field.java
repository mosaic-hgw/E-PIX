package org.emau.icmvc.ttp.deduplication.config.model;

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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.emau.icmvc.ttp.epix.common.model.config.FieldDTO;
import org.emau.icmvc.ttp.epix.common.model.enums.BlockingMode;
import org.emau.icmvc.ttp.epix.common.model.enums.FieldName;

/**
 *
 * @author geidell
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Field", propOrder = { "name", "blockingThreshold", "blockingMode", "matchingThreshold", "weight", "algorithm", "multipleValues" })
public class Field
{
	@XmlElement(required = true)
	private FieldName name;
	@XmlElement(name = "blocking-threshold", required = false, defaultValue = "0.0")
	private double blockingThreshold;
	@XmlElement(name = "blocking-mode", required = false, defaultValue = "TEXT")
	private BlockingMode blockingMode = BlockingMode.TEXT;
	@XmlElement(name = "matching-threshold", required = true)
	private double matchingThreshold;
	@XmlElement(required = false, defaultValue = "1.0")
	private double weight = 1l;
	@XmlElement(required = true)
	private String algorithm;
	@XmlElement(name = "multiple-values", required = false)
	private MultipleValues multipleValues;

	public Field()
	{}

	public Field(FieldDTO fieldDTO)
	{
		name = fieldDTO.getName();
		blockingThreshold = fieldDTO.getBlockingThreshold();
		blockingMode = fieldDTO.getBlockingMode();
		matchingThreshold = fieldDTO.getMatchingThreshold();
		weight = fieldDTO.getWeight();
		algorithm = fieldDTO.getAlgorithm();
		if (fieldDTO.getMultipleValuesSeparator() != Character.MIN_VALUE)
		{
			multipleValues = new MultipleValues(fieldDTO.getMultipleValuesSeparator(), fieldDTO.getPenaltyNotAPerfectMatch(), fieldDTO.getPenaltyOneShort(), fieldDTO.getPenaltyBothShort());
		}
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
		if (blockingMode == null)
		{
			this.blockingMode = BlockingMode.TEXT;
		}
		else
		{
			this.blockingMode = blockingMode;
		}
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

	public MultipleValues getMultipleValues()
	{
		return multipleValues;
	}

	public void setMultipleValues(MultipleValues multipleValues)
	{
		this.multipleValues = multipleValues;
	}

	public boolean isBlockingField()
	{
		return blockingThreshold > 0.;
	}

	public FieldDTO toDTO()
	{
		if (multipleValues == null)
		{
			return new FieldDTO(name, blockingThreshold, blockingMode, matchingThreshold, weight, algorithm, Character.MIN_VALUE, 0., 0., 0.);
		}
		else
		{
			return new FieldDTO(name, blockingThreshold, blockingMode, matchingThreshold, weight, algorithm, multipleValues.getSeparatorChar(),
					multipleValues.getPenaltyNotAPerfectMatch(), multipleValues.getPenaltyOneShort(), multipleValues.getPenaltyBothShort());
		}
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
		result = prime * result + (multipleValues == null ? 0 : multipleValues.hashCode());
		result = prime * result + (name == null ? 0 : name.hashCode());
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
		Field other = (Field) obj;
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
		if (multipleValues == null)
		{
			if (other.multipleValues != null)
			{
				return false;
			}
		}
		else if (!multipleValues.equals(other.multipleValues))
		{
			return false;
		}
		if (name != other.name)
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
		return "Field [name=" + name + ", blockingThreshold=" + blockingThreshold + ", blockingMode=" + blockingMode + ", matchingThreshold="
				+ matchingThreshold + ", weight=" + weight + ", algorithm=" + algorithm + ", multipleValues=" + multipleValues + "]";
	}
}
