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
import java.text.SimpleDateFormat;
import java.util.Date;

import org.emau.icmvc.ttp.epix.common.model.config.ConfigurationContainer;
import org.emau.icmvc.ttp.epix.common.model.enums.MatchingMode;

/**
 * the values of "configObjects" are only used for addDomain and updateDomain if the String "config" is null or empty
 *
 * @author geidell
 *
 */
public class DomainDTO implements Serializable
{
	private static final long serialVersionUID = 7197441148975025640L;
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
	private String name;
	private String label;
	private String description;
	private IdentifierDomainDTO mpiDomain;
	private String config;
	private SourceDTO safeSource;
	private Date entryDate;
	private Date updateDate;
	private MatchingMode matchingMode;
	private boolean inUse;
	private long personCount;
	private ConfigurationContainer configObjects = new ConfigurationContainer();

	public DomainDTO()
	{
		super();
	}

	public DomainDTO(DomainDTO other)
	{
		name = other.name;
		label = other.label;
		description = other.description;
		setMpiDomain(other.getMpiDomain());
		config = other.config;
		setSafeSource(other.getSafeSource());
		setEntryDate(other.getEntryDate());
		setUpdateDate(other.getUpdateDate());
		matchingMode = other.matchingMode;
		inUse = other.inUse;
		personCount = other.personCount;
		setConfigObjects(other.configObjects);
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getLabel()
	{
		return label;
	}

	public void setLabel(String label)
	{
		this.label = label;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public IdentifierDomainDTO getMpiDomain()
	{
		return mpiDomain;
	}

	public void setMpiDomain(IdentifierDomainDTO mpiDomain)
	{
		this.mpiDomain = mpiDomain != null ? new IdentifierDomainDTO(mpiDomain) : null;
	}

	public String getConfig()
	{
		return config;
	}

	public void setConfig(String config)
	{
		this.config = config;
	}

	public SourceDTO getSafeSource()
	{
		return safeSource;
	}

	public void setSafeSource(SourceDTO safeSource)
	{
		this.safeSource = safeSource != null ? new SourceDTO(safeSource) : null;
	}

	public Date getEntryDate()
	{
		return entryDate;
	}

	public void setEntryDate(Date entryDate)
	{
		this.entryDate = entryDate != null ? new Date(entryDate.getTime()) : null;
	}

	public Date getUpdateDate()
	{
		return updateDate;
	}

	public void setUpdateDate(Date updateDate)
	{
		this.updateDate = updateDate != null ? new Date(updateDate.getTime()) : null;
	}

	public MatchingMode getMatchingMode()
	{
		return matchingMode;
	}

	public void setMatchingMode(MatchingMode matchingMode)
	{
		this.matchingMode = matchingMode;
	}

	public boolean isInUse()
	{
		return inUse;
	}

	public void setInUse(boolean inUse)
	{
		this.inUse = inUse;
	}

	public long getPersonCount()
	{
		return personCount;
	}

	public void setPersonCount(long personCount)
	{
		this.personCount = personCount;
	}

	public ConfigurationContainer getConfigObjects()
	{
		return configObjects;
	}

	public void setConfigObjects(ConfigurationContainer configObjects)
	{
		this.configObjects = configObjects != null ? new ConfigurationContainer(configObjects) : new ConfigurationContainer();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (config == null ? 0 : config.hashCode());
		result = prime * result + (configObjects == null ? 0 : configObjects.hashCode());
		result = prime * result + (description == null ? 0 : description.hashCode());
		result = prime * result + (entryDate == null ? 0 : entryDate.hashCode());
		result = prime * result + (inUse ? 1231 : 1237);
		result = prime * result + (label == null ? 0 : label.hashCode());
		result = prime * result + (matchingMode == null ? 0 : matchingMode.hashCode());
		result = prime * result + (mpiDomain == null ? 0 : mpiDomain.hashCode());
		result = prime * result + (name == null ? 0 : name.hashCode());
		result = prime * result + (int) (personCount ^ personCount >>> 32);
		result = prime * result + (safeSource == null ? 0 : safeSource.hashCode());
		result = prime * result + (updateDate == null ? 0 : updateDate.hashCode());
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
		DomainDTO other = (DomainDTO) obj;
		if (config == null)
		{
			if (other.config != null)
			{
				return false;
			}
		}
		else if (!config.equals(other.config))
		{
			return false;
		}
		if (configObjects == null)
		{
			if (other.configObjects != null)
			{
				return false;
			}
		}
		else if (!configObjects.equals(other.configObjects))
		{
			return false;
		}
		if (description == null)
		{
			if (other.description != null)
			{
				return false;
			}
		}
		else if (!description.equals(other.description))
		{
			return false;
		}
		if (entryDate == null)
		{
			if (other.entryDate != null)
			{
				return false;
			}
		}
		else if (!entryDate.equals(other.entryDate))
		{
			return false;
		}
		if (inUse != other.inUse)
		{
			return false;
		}
		if (label == null)
		{
			if (other.label != null)
			{
				return false;
			}
		}
		else if (!label.equals(other.label))
		{
			return false;
		}
		if (matchingMode != other.matchingMode)
		{
			return false;
		}
		if (mpiDomain == null)
		{
			if (other.mpiDomain != null)
			{
				return false;
			}
		}
		else if (!mpiDomain.equals(other.mpiDomain))
		{
			return false;
		}
		if (name == null)
		{
			if (other.name != null)
			{
				return false;
			}
		}
		else if (!name.equals(other.name))
		{
			return false;
		}
		if (personCount != other.personCount)
		{
			return false;
		}
		if (safeSource == null)
		{
			if (other.safeSource != null)
			{
				return false;
			}
		}
		else if (!safeSource.equals(other.safeSource))
		{
			return false;
		}
		if (updateDate == null)
		{
			if (other.updateDate != null)
			{
				return false;
			}
		}
		else if (!updateDate.equals(other.updateDate))
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("name:");
		sb.append(name);
		sb.append(", label:");
		sb.append(label);
		sb.append(", mpiDomain:");
		sb.append(mpiDomain);
		sb.append(", desc:");
		sb.append(description);
		sb.append(", generator:");
		sb.append(safeSource);
		sb.append(", entry date:");
		synchronized (sdf)
		{
			if (entryDate != null)
			{
				sb.append(sdf.format(entryDate));
			}
		}
		sb.append(", update date:");
		synchronized (sdf)
		{
			if (entryDate != null)
			{
				sb.append(sdf.format(updateDate));
			}
		}
		sb.append(", matchingMode:");
		if (matchingMode != null)
		{
			sb.append(matchingMode);
		}
		else
		{
			sb.append("null");
		}
		sb.append(", in use:");
		sb.append(inUse);
		sb.append(", number of persons:");
		sb.append(personCount);
		return sb.toString();
	}
}
