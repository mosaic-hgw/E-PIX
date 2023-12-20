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
@XmlType(name = "MultipleValues", propOrder = { "separator", "penaltyNotAPerfectMatch", "penaltyOneShort", "penaltyBothShort" })
public class MultipleValues
{
	@XmlElement(required = true)
	private String separator;
	@XmlElement(name = "penalty-not-a-perfect-match", required = false, defaultValue = "0.1")
	private double penaltyNotAPerfectMatch = 0.1;
	@XmlElement(name = "penalty-one-short", required = false, defaultValue = "0.1")
	private double penaltyOneShort = 0.1;
	@XmlElement(name = "penalty-both-short", required = false, defaultValue = "0.2")
	private double penaltyBothShort = 0.2;

	public MultipleValues()
	{}

	public MultipleValues(char separator, double penaltyNotAPerfectMatch, double penaltyOneShort, double penaltyBothShort)
	{
		this.separator = Character.toString(separator);
		this.penaltyNotAPerfectMatch = penaltyNotAPerfectMatch;
		this.penaltyOneShort = penaltyOneShort;
		this.penaltyBothShort = penaltyBothShort;
	}

	public char getSeparatorChar()
	{
		return separator.charAt(0);
	}

	public String getSeparator()
	{
		return separator;
	}

	public void setSeparator(String separator)
	{
		this.separator = separator;
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
		long temp;
		temp = Double.doubleToLongBits(penaltyBothShort);
		result = prime * result + (int) (temp ^ temp >>> 32);
		temp = Double.doubleToLongBits(penaltyNotAPerfectMatch);
		result = prime * result + (int) (temp ^ temp >>> 32);
		temp = Double.doubleToLongBits(penaltyOneShort);
		result = prime * result + (int) (temp ^ temp >>> 32);
		result = prime * result + (separator == null ? 0 : separator.hashCode());
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
		MultipleValues other = (MultipleValues) obj;
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
		if (separator == null)
		{
			if (other.separator != null)
			{
				return false;
			}
		}
		else if (!separator.equals(other.separator))
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		return "MultipleValues [separator=" + separator + ", penaltyNotAPerfectMatch=" + penaltyNotAPerfectMatch + ", penaltyOneShort="
				+ penaltyOneShort + ", penaltyBothShort=" + penaltyBothShort + "]";
	}
}
