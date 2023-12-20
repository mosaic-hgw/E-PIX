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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author geidell
 *
 */
public class MPIRequestDTO implements Serializable
{
	private static final long serialVersionUID = 4711190705597851067L;
	private String domainName;
	private String sourceName;
	private String comment;
	private final List<IdentityInDTO> requestEntries = new ArrayList<>();
	private RequestConfig requestConfig = new RequestConfig();

	public MPIRequestDTO()
	{}

	public MPIRequestDTO(String domainName, String sourceName, String comment, List<IdentityInDTO> requestEntries, RequestConfig requestConfig)
	{
		super();
		this.domainName = domainName;
		this.sourceName = sourceName;
		setRequestEntries(requestEntries);
		setRequestConfig(requestConfig);
	}

	public String getDomainName()
	{
		return domainName;
	}

	public void setDomainName(String domainName)
	{
		this.domainName = domainName;
	}

	public String getSourceName()
	{
		return sourceName;
	}

	public void setSourceName(String sourceName)
	{
		this.sourceName = sourceName;
	}

	public String getComment()
	{
		return comment;
	}

	public void setComment(String comment)
	{
		this.comment = comment;
	}

	public List<IdentityInDTO> getRequestEntries()
	{
		return requestEntries;
	}

	public void setRequestEntries(List<IdentityInDTO> requestEntries)
	{
		this.requestEntries.clear();
		if (requestEntries != null)
		{
			for (IdentityInDTO iDTO : requestEntries)
			{
				this.requestEntries.add(new IdentityInDTO(iDTO));
			}
		}
	}

	public RequestConfig getRequestConfig()
	{
		return requestConfig;
	}

	public void setRequestConfig(RequestConfig requestConfig)
	{
		this.requestConfig = requestConfig != null ? new RequestConfig(requestConfig) : new RequestConfig();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (comment == null ? 0 : comment.hashCode());
		result = prime * result + (domainName == null ? 0 : domainName.hashCode());
		result = prime * result + (requestConfig == null ? 0 : requestConfig.hashCode());
		result = prime * result + (requestEntries == null ? 0 : requestEntries.hashCode());
		result = prime * result + (sourceName == null ? 0 : sourceName.hashCode());
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
		MPIRequestDTO other = (MPIRequestDTO) obj;
		if (comment == null)
		{
			if (other.comment != null)
			{
				return false;
			}
		}
		else if (!comment.equals(other.comment))
		{
			return false;
		}
		if (domainName == null)
		{
			if (other.domainName != null)
			{
				return false;
			}
		}
		else if (!domainName.equals(other.domainName))
		{
			return false;
		}
		if (requestConfig == null)
		{
			if (other.requestConfig != null)
			{
				return false;
			}
		}
		else if (!requestConfig.equals(other.requestConfig))
		{
			return false;
		}
		if (requestEntries == null)
		{
			if (other.requestEntries != null)
			{
				return false;
			}
		}
		else if (!requestEntries.equals(other.requestEntries))
		{
			return false;
		}
		if (sourceName == null)
		{
			if (other.sourceName != null)
			{
				return false;
			}
		}
		else if (!sourceName.equals(other.sourceName))
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		return "MPIRequestDTO [domainName=" + domainName + ", sourceName=" + sourceName + ", comment=" + comment + ", requestEntries="
				+ requestEntries.stream().map(Object::toString).collect(Collectors.joining(", ")) + ", requestConfig=" + requestConfig + "]";
	}
}
