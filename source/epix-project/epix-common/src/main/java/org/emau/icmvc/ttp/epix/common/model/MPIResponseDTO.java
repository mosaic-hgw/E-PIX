package org.emau.icmvc.ttp.epix.common.model;

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


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class MPIResponseDTO implements Serializable
{
	private static final long serialVersionUID = -4955852741732521492L;
	private final Map<IdentityInBaseDTO, ResponseEntryDTO> responseEntries = new HashMap<>();

	public MPIResponseDTO()
	{}

	public MPIResponseDTO(Map<IdentityInBaseDTO, ResponseEntryDTO> responseEntries)
	{
		super();
		this.responseEntries.putAll(responseEntries);
	}

	public Map<IdentityInBaseDTO, ResponseEntryDTO> getResponseEntries()
	{
		return responseEntries;
	}

	public void setResponseEntries(Map<IdentityInBaseDTO, ResponseEntryDTO> responseEntries)
	{
		if (!this.responseEntries.equals(responseEntries))
		{
			this.responseEntries.clear();
			this.responseEntries.putAll(responseEntries);
		}
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((responseEntries == null) ? 0 : responseEntries.hashCode());
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
		MPIResponseDTO other = (MPIResponseDTO) obj;
		if (responseEntries == null)
		{
			if (other.responseEntries != null)
				return false;
		}
		else if (!responseEntries.equals(other.responseEntries))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "MPIResponseDTO [responseEntries=" + responseEntries.values().stream().map(Object::toString).collect(Collectors.joining(", ")) + "]";
	}
}
