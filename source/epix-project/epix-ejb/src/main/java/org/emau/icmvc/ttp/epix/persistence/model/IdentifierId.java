package org.emau.icmvc.ttp.epix.persistence.model;

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

import org.emau.icmvc.ttp.epix.common.model.IdentifierDTO;

/**
 * 
 * @author geidell
 *
 */
public class IdentifierId implements Serializable
{
	private static final long serialVersionUID = -1308419401770844298L;
	private String identifierDomain;
	private String value;

	public IdentifierId()
	{}

	public IdentifierId(String identifierDomain, String value)
	{
		this.identifierDomain = identifierDomain;
		this.value = value;
	}

	public IdentifierId(IdentifierDTO identifierDTO)
	{
		this.identifierDomain = identifierDTO.getIdentifierDomain().getName();
		this.value = identifierDTO.getValue();
	}

	public String getIdentifierDomain()
	{
		return identifierDomain;
	}

	public String getValue()
	{
		return value;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((identifierDomain == null) ? 0 : identifierDomain.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		IdentifierId other = (IdentifierId) obj;
		if (identifierDomain == null)
		{
			if (other.identifierDomain != null)
				return false;
		}
		else if (!identifierDomain.equals(other.identifierDomain))
			return false;
		if (value == null)
		{
			if (other.value != null)
				return false;
		}
		else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "IdentifierId [identifierDomain=" + identifierDomain + ", value=" + value + "]";
	}
}
