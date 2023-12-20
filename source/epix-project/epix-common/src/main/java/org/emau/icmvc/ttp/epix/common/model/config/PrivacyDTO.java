package org.emau.icmvc.ttp.epix.common.model.config;

/*-
 * ###license-information-start###
 * E-PIX - Enterprise Patient Identifier
 * 							Cross-referencing
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
import java.util.ArrayList;
import java.util.List;

public class PrivacyDTO implements Serializable
{
	private static final long serialVersionUID = -5472484681723853680L;
	private final List<BloomFilterConfigDTO> bloomFilterConfigs = new ArrayList<>();

	public PrivacyDTO()
	{}

	public PrivacyDTO(List<BloomFilterConfigDTO> bloomFilterConfigs)
	{
		super();
		setBloomFilterConfigs(bloomFilterConfigs);
	}

	public PrivacyDTO(PrivacyDTO dto)
	{
		this(dto.getBloomFilterConfigs());
	}

	public List<BloomFilterConfigDTO> getBloomFilterConfigs()
	{
		return bloomFilterConfigs;
	}

	public void setBloomFilterConfigs(List<BloomFilterConfigDTO> bloomFilterConfigs)
	{
		this.bloomFilterConfigs.clear();
		if (bloomFilterConfigs != null)
		{
			for (BloomFilterConfigDTO configDTO : bloomFilterConfigs)
			{
				this.bloomFilterConfigs.add(new BloomFilterConfigDTO(configDTO));
			}
		}
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (bloomFilterConfigs == null ? 0 : bloomFilterConfigs.hashCode());
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
		PrivacyDTO other = (PrivacyDTO) obj;
		if (bloomFilterConfigs == null)
		{
			return other.bloomFilterConfigs == null;
		}
		else
		{
			return bloomFilterConfigs.equals(other.bloomFilterConfigs);
		}
	}

	@Override
	public String toString()
	{
		return "PrivacyDTO [bloomFilterConfigs=" + bloomFilterConfigs + "]";
	}
}
