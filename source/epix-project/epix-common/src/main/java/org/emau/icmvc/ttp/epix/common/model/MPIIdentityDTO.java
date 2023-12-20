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

/**
 *
 * @author geidell
 *
 */
public class MPIIdentityDTO implements Serializable
{
	private static final long serialVersionUID = 7777641498701900273L;
	private IdentifierDTO mpiId;
	private IdentityOutDTO identity;

	public MPIIdentityDTO()
	{}

	public MPIIdentityDTO(IdentifierDTO mpiId, IdentityOutDTO identity)
	{
		super();
		setMpiId(mpiId);
		setIdentity(identity);
	}

	public MPIIdentityDTO(MPIIdentityDTO dto)
	{
		this(dto.getMpiId(), dto.getIdentity());
	}

	public IdentifierDTO getMpiId()
	{
		return mpiId;
	}

	public void setMpiId(IdentifierDTO mpiId)
	{
		this.mpiId = mpiId != null ? new IdentifierDTO(mpiId) : null;
	}

	public IdentityOutDTO getIdentity()
	{
		return identity;
	}

	public void setIdentity(IdentityOutDTO identity)
	{
		this.identity = identity != null ? new IdentityOutDTO(identity) : null;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (identity == null ? 0 : identity.hashCode());
		result = prime * result + (mpiId == null ? 0 : mpiId.hashCode());
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
		MPIIdentityDTO other = (MPIIdentityDTO) obj;
		if (identity == null)
		{
			if (other.identity != null)
			{
				return false;
			}
		}
		else if (!identity.equals(other.identity))
		{
			return false;
		}
		if (mpiId == null)
		{
			if (other.mpiId != null)
			{
				return false;
			}
		}
		else if (!mpiId.equals(other.mpiId))
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		return "MPIIdentityDTO [mpiId=" + mpiId + ", identity=" + identity + "]";
	}
}
