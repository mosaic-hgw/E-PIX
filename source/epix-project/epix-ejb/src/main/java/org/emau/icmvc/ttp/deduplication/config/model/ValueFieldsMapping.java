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
@XmlType(name = "ValueFieldsMapping", propOrder = { "value1", "value2", "value3", "value4", "value5", "value6", "value7", "value8", "value9",
		"value10" })
public class ValueFieldsMapping
{
	// unschoen, aber wird bei umstellung auf eav eh anders
	@XmlElement(name = "value1", required = false)
	private String value1;
	@XmlElement(name = "value2", required = false)
	private String value2;
	@XmlElement(name = "value3", required = false)
	private String value3;
	@XmlElement(name = "value4", required = false)
	private String value4;
	@XmlElement(name = "value5", required = false)
	private String value5;
	@XmlElement(name = "value6", required = false)
	private String value6;
	@XmlElement(name = "value7", required = false)
	private String value7;
	@XmlElement(name = "value8", required = false)
	private String value8;
	@XmlElement(name = "value9", required = false)
	private String value9;
	@XmlElement(name = "value10", required = false)
	private String value10;

	public String getValue1()
	{
		return value1;
	}

	public void setValue1(String value1)
	{
		this.value1 = value1;
	}

	public String getValue2()
	{
		return value2;
	}

	public void setValue2(String value2)
	{
		this.value2 = value2;
	}

	public String getValue3()
	{
		return value3;
	}

	public void setValue3(String value3)
	{
		this.value3 = value3;
	}

	public String getValue4()
	{
		return value4;
	}

	public void setValue4(String value4)
	{
		this.value4 = value4;
	}

	public String getValue5()
	{
		return value5;
	}

	public void setValue5(String value5)
	{
		this.value5 = value5;
	}

	public String getValue6()
	{
		return value6;
	}

	public void setValue6(String value6)
	{
		this.value6 = value6;
	}

	public String getValue7()
	{
		return value7;
	}

	public void setValue7(String value7)
	{
		this.value7 = value7;
	}

	public String getValue8()
	{
		return value8;
	}

	public void setValue8(String value8)
	{
		this.value8 = value8;
	}

	public String getValue9()
	{
		return value9;
	}

	public void setValue9(String value9)
	{
		this.value9 = value9;
	}

	public String getValue10()
	{
		return value10;
	}

	public void setValue10(String value10)
	{
		this.value10 = value10;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value1 == null) ? 0 : value1.hashCode());
		result = prime * result + ((value10 == null) ? 0 : value10.hashCode());
		result = prime * result + ((value2 == null) ? 0 : value2.hashCode());
		result = prime * result + ((value3 == null) ? 0 : value3.hashCode());
		result = prime * result + ((value4 == null) ? 0 : value4.hashCode());
		result = prime * result + ((value5 == null) ? 0 : value5.hashCode());
		result = prime * result + ((value6 == null) ? 0 : value6.hashCode());
		result = prime * result + ((value7 == null) ? 0 : value7.hashCode());
		result = prime * result + ((value8 == null) ? 0 : value8.hashCode());
		result = prime * result + ((value9 == null) ? 0 : value9.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ValueFieldsMapping other = (ValueFieldsMapping) obj;
		if (value1 == null)
		{
			if (other.value1 != null)
				return false;
		}
		else if (!value1.equals(other.value1))
			return false;
		if (value10 == null)
		{
			if (other.value10 != null)
				return false;
		}
		else if (!value10.equals(other.value10))
			return false;
		if (value2 == null)
		{
			if (other.value2 != null)
				return false;
		}
		else if (!value2.equals(other.value2))
			return false;
		if (value3 == null)
		{
			if (other.value3 != null)
				return false;
		}
		else if (!value3.equals(other.value3))
			return false;
		if (value4 == null)
		{
			if (other.value4 != null)
				return false;
		}
		else if (!value4.equals(other.value4))
			return false;
		if (value5 == null)
		{
			if (other.value5 != null)
				return false;
		}
		else if (!value5.equals(other.value5))
			return false;
		if (value6 == null)
		{
			if (other.value6 != null)
				return false;
		}
		else if (!value6.equals(other.value6))
			return false;
		if (value7 == null)
		{
			if (other.value7 != null)
				return false;
		}
		else if (!value7.equals(other.value7))
			return false;
		if (value8 == null)
		{
			if (other.value8 != null)
				return false;
		}
		else if (!value8.equals(other.value8))
			return false;
		if (value9 == null)
		{
			if (other.value9 != null)
				return false;
		}
		else if (!value9.equals(other.value9))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "ValueFieldsMapping [value1=" + value1 + ", value2=" + value2 + ", value3=" + value3 + ", value4=" + value4 + ", value5=" + value5
				+ ", value6=" + value6 + ", value7=" + value7 + ", value8=" + value8 + ", value9=" + value9 + ", value10=" + value10 + "]";
	}
}
