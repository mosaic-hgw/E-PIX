package org.emau.icmvc.ttp.deduplication.config.model;

/*-
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

/**
 * Simple filter that replaces all characters with the specified replace character that are not included in the pass alphabet.
 * @author hampfc
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SimpleFilter", propOrder = { "passAlphabet", "replaceCharacter" })
public class SimpleFilter
{
	@XmlElement(name = "pass-alphabet", required = false, defaultValue = "")
	private String passAlphabet;
	@XmlElement(name = "replace-character", required = false, defaultValue = "")
	private String replaceCharacter;

	public String getPassAlphabet()
	{
		return passAlphabet;
	}

	public void setPassAlphabet(String passAlphabet)
	{
		this.passAlphabet = passAlphabet;
	}

	public String getReplaceCharacter()
	{
		return replaceCharacter;
	}

	public void setReplaceCharacter(String replaceCharacter)
	{
		this.replaceCharacter = replaceCharacter;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((passAlphabet == null) ? 0 : passAlphabet.hashCode());
		result = prime * result + ((replaceCharacter == null) ? 0 : replaceCharacter.hashCode());
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
		SimpleFilter other = (SimpleFilter) obj;
		if (passAlphabet == null)
		{
			if (other.passAlphabet != null)
				return false;
		}
		else if (!passAlphabet.equals(other.passAlphabet))
			return false;
		if (replaceCharacter == null)
		{
			if (other.replaceCharacter != null)
				return false;
		}
		else if (!replaceCharacter.equals(other.replaceCharacter))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "SimpleFilter [passAlphabet=" + passAlphabet + ", replaceCharacter=" + replaceCharacter + "]";
	}
}
