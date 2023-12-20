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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.emau.icmvc.ttp.epix.common.model.config.FieldDTO;
import org.emau.icmvc.ttp.epix.common.model.config.MatchingDTO;

/**
 *
 * @author geidell
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Matching", propOrder = { "thresholdPossibleMatch", "thresholdAutomaticMatch", "useCEMFIM", "parallelMatchingAfter",
		"numberOfThreads", "fields" })
public class Matching
{
	@XmlElement(name = "threshold-possible-match", required = false, defaultValue = "4")
	private double thresholdPossibleMatch = 4;
	@XmlElement(name = "threshold-automatic-match", required = false, defaultValue = "20")
	private double thresholdAutomaticMatch = 20;
	@XmlElement(name = "use-cemfim", required = false, defaultValue = "false")
	private boolean useCEMFIM = false;
	@XmlElement(name = "parallel-matching-after", required = false, defaultValue = "1000")
	private int parallelMatchingAfter = 1000;
	/**
	 * defaults to Runtime.getRuntime().availableProcessors()
	 */
	@XmlElement(name = "number-of-threads-for-matching", required = false, defaultValue = "-1")
	private int numberOfThreads = Runtime.getRuntime().availableProcessors();
	@XmlElement(name = "field", required = true)
	private final List<Field> fields = new ArrayList<>();

	public Matching()
	{}

	public Matching(MatchingDTO matchingConfig)
	{
		thresholdPossibleMatch = matchingConfig.getThresholdPossibleMatch();
		thresholdAutomaticMatch = matchingConfig.getThresholdAutomaticMatch();
		useCEMFIM = matchingConfig.isUseCEMFIM();
		parallelMatchingAfter = matchingConfig.getParallelMatchingAfter();
		if (matchingConfig.getFields() != null && !matchingConfig.getFields().isEmpty())
		{
			for (FieldDTO fieldDTO : matchingConfig.getFields())
			{
				fields.add(new Field(fieldDTO));
			}
		}
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

	public int getNumberOfThreads()
	{
		return numberOfThreads;
	}

	public void setNumberOfThreads(int numberOfThreads)
	{
		if (numberOfThreads < 1)
		{
			this.numberOfThreads = Runtime.getRuntime().availableProcessors();
		}
		else
		{
			this.numberOfThreads = numberOfThreads;
		}
	}

	public List<Field> getFields()
	{
		return fields;
	}

	public MatchingDTO toDTO()
	{
		List<FieldDTO> fieldDTOs = new ArrayList<>();
		for (Field field : fields)
		{
			fieldDTOs.add(field.toDTO());
		}

		return new MatchingDTO(thresholdPossibleMatch, thresholdAutomaticMatch, useCEMFIM, parallelMatchingAfter, fieldDTOs);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (fields == null ? 0 : fields.hashCode());
		result = prime * result + numberOfThreads;
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
		Matching other = (Matching) obj;
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
		if (numberOfThreads != other.numberOfThreads)
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
		return "Matching [thresholdPossibleMatch=" + thresholdPossibleMatch + ", thresholdAutomaticMatch=" + thresholdAutomaticMatch + ", useCEMFIM="
				+ useCEMFIM + ", parallelMatchingAfter=" + parallelMatchingAfter + ", numberOfThreads=" + numberOfThreads + ", fields=" + fields
				+ "]";
	}
}
