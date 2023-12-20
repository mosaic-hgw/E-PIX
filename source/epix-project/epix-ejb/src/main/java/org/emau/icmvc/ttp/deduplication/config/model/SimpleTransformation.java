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

/**
 *
 * @author geidell
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SimpleTransformation", propOrder = { "inputPattern", "outputPattern" })
public class SimpleTransformation
{
	@XmlElement(name = "input-pattern", required = false, defaultValue = "")
	private String inputPattern;
	@XmlElement(name = "output-pattern", required = false, defaultValue = "")
	private String outputPattern;

	public SimpleTransformation()
	{}

	public SimpleTransformation(String inputPattern, String outputPattern)
	{
		this.inputPattern = inputPattern;
		this.outputPattern = outputPattern;
	}

	public String getInputPattern()
	{
		return inputPattern;
	}

	public void setInputPattern(String inputPattern)
	{
		this.inputPattern = inputPattern;
	}

	public String getOutputPattern()
	{
		return outputPattern;
	}

	public void setOutputPattern(String outputPattern)
	{
		this.outputPattern = outputPattern;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (inputPattern == null ? 0 : inputPattern.hashCode());
		result = prime * result + (outputPattern == null ? 0 : outputPattern.hashCode());
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
		SimpleTransformation other = (SimpleTransformation) obj;
		if (inputPattern == null)
		{
			if (other.inputPattern != null)
			{
				return false;
			}
		}
		else if (!inputPattern.equals(other.inputPattern))
		{
			return false;
		}
		if (outputPattern == null)
		{
			if (other.outputPattern != null)
			{
				return false;
			}
		}
		else if (!outputPattern.equals(other.outputPattern))
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		return "SimpleTransformation [inputPattern=" + inputPattern + ", outputPattern=" + outputPattern + "]";
	}
}
