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

import org.emau.icmvc.ttp.epix.common.model.enums.MatchStatus;

/**
 *
 * @author geidell
 *
 */
public class ResponseEntryDTO implements Serializable
{
	private static final long serialVersionUID = 3821711796527472820L;
	private PersonDTO person;
	private MatchStatus matchStatus;

	public ResponseEntryDTO()
	{
		super();
	}

	public ResponseEntryDTO(PersonDTO person, MatchStatus matchStatus)
	{
		super();
		setPerson(person);
		this.matchStatus = matchStatus;
	}

	public ResponseEntryDTO(ResponseEntryDTO dto)
	{
		this(dto.getPerson(), dto.getMatchStatus());
	}

	public PersonDTO getPerson()
	{
		return person;
	}

	public void setPerson(PersonDTO person)
	{
		this.person = person != null ? new PersonDTO(person) : null;
	}

	public MatchStatus getMatchStatus()
	{
		return matchStatus;
	}

	public void setMatchStatus(MatchStatus matchStatus)
	{
		this.matchStatus = matchStatus;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (matchStatus == null ? 0 : matchStatus.hashCode());
		result = prime * result + (person == null ? 0 : person.hashCode());
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
		ResponseEntryDTO other = (ResponseEntryDTO) obj;
		if (matchStatus != other.matchStatus)
		{
			return false;
		}
		if (person == null)
		{
			if (other.person != null)
			{
				return false;
			}
		}
		else if (!person.equals(other.person))
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		return "ResponseEntryDTO [person=" + person + ", matchStatus=" + matchStatus + "]";
	}
}
