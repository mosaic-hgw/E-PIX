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

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.Table;

import org.emau.icmvc.ttp.deduplication.config.model.MatchingConfiguration;
import org.emau.icmvc.ttp.epix.common.exception.InvalidParameterException;
import org.emau.icmvc.ttp.epix.common.model.DomainDTO;
import org.emau.icmvc.ttp.epix.common.model.config.ConfigurationContainer;

/**
 *
 * @author geidell
 *
 */
@Entity
@Table(name = "domain")
public class Domain implements Serializable
{
	@Serial
	private static final long serialVersionUID = 9050935990346574799L;

	@Id
	@Column
	private String name;
	@Column
	private String label;
	@Column(name = "description")
	private String description;
	@Column(name = "create_timestamp", nullable = false)
	private Timestamp createTimestamp;
	@Column(nullable = false)
	private Timestamp timestamp;
	@ManyToOne(cascade = CascadeType.MERGE, optional = false)
	@JoinColumn(name = "safe_source_name")
	private Source safeSource;
	@ManyToOne(cascade = CascadeType.MERGE, optional = false)
	@JoinColumn(name = "mpi_domain_name")
	private IdentifierDomain mpiDomain;
	@Column(columnDefinition = "TEXT")
	private String config;
	private transient MatchingConfiguration matchingConfiguration;
	private transient ConfigurationContainer configurationContainer;
	private transient long personCount;

	public Domain()
	{
		createTimestamp = new Timestamp(System.currentTimeMillis());
	}

	public Domain(DomainDTO dto, IdentifierDomain mpiDomain, Source safeSource, Timestamp timestamp) throws RuntimeException
	{
		this.name = dto.getName();
		this.label = dto.getLabel();
		setConfig(dto.getConfig(), dto.getConfigObjects());
		this.description = dto.getDescription();
		this.mpiDomain = mpiDomain;
		this.safeSource = safeSource;
		this.createTimestamp = timestamp;
		this.timestamp = timestamp;
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

	public Timestamp getCreateTimestamp()
	{
		return createTimestamp;
	}

	public void setCreateTimestamp(Timestamp createTimestamp)
	{
		this.createTimestamp = createTimestamp;
	}

	public Timestamp getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp)
	{
		this.timestamp = timestamp;
	}

	public IdentifierDomain getMpiDomain()
	{
		return mpiDomain;
	}

	public void setMpiDomain(IdentifierDomain mpiDomain)
	{
		this.mpiDomain = mpiDomain;
	}

	public Source getSafeSource()
	{
		return safeSource;
	}

	public void setSafeSource(Source safeSource)
	{
		this.safeSource = safeSource;
	}

	public String getConfig()
	{
		return config;
	}

	public void setConfig(String config, ConfigurationContainer configObjects) throws RuntimeException
	{
		if (config != null && !config.isEmpty())
		{
			// prefer XML config
			this.config = config;
		}
		else
		{
			// create XML config from config objects
			this.config = new MatchingConfiguration(configObjects, name).toXml(name);
		}

		parseConfig();
	}

	/**
	 * this method is called by jpa
	 *
	 * @throws RuntimeException
	 */
	@PostLoad
	public void parseConfig() throws RuntimeException
	{
		if (config == null)
		{
			throw new RuntimeException(new InvalidParameterException("domain.config", "matching configuration missing for domain: " + name));
		}

		matchingConfiguration = MatchingConfiguration.fromXml(config, name);
		configurationContainer = matchingConfiguration.toConfigurationContainer();
	}

	public MatchingConfiguration getMatchingConfiguration()
	{
		return matchingConfiguration;
	}

	public ConfigurationContainer getConfigurationContainer()
	{
		return configurationContainer;
	}

	public boolean isInUse()
	{
		return personCount > 0;
	}

	public void setPersonCount(long personCount)
	{
		this.personCount = personCount;
	}

	public long getPersonCount()
	{
		return personCount;
	}

	public void update(DomainDTO domainDTO, IdentifierDomain mpiDomain, Source safeSource) throws RuntimeException
	{
		this.label = domainDTO.getLabel();
		setConfig(domainDTO.getConfig(), domainDTO.getConfigObjects());
		this.description = domainDTO.getDescription();
		this.mpiDomain = mpiDomain;
		this.safeSource = safeSource;
		this.timestamp = new Timestamp(System.currentTimeMillis());
	}

	public void updateInUse(String label, String description)
	{
		this.label = label;
		this.description = description;
		this.timestamp = new Timestamp(System.currentTimeMillis());
	}

	public DomainDTO toDTO()
	{
		DomainDTO result = new DomainDTO();
		result.setConfig(config);
		result.setDescription(description);
		result.setEntryDate(new Date(createTimestamp.getTime()));
		result.setUpdateDate(new Date(timestamp.getTime()));
		result.setLabel(label);
		result.setMpiDomain(mpiDomain.toDTO());
		result.setName(name);
		result.setSafeSource(safeSource.toDTO());
		result.setMatchingMode(matchingConfiguration.getMatchingMode());
		result.setInUse(personCount > 0);
		result.setPersonCount(personCount);
		result.setConfigObjects(new ConfigurationContainer(configurationContainer));
		return result;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (name == null ? 0 : name.hashCode());
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
		Domain other = (Domain) obj;
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
		sb.append(", createTimestamp:");
		sb.append(createTimestamp);
		sb.append(", timestamp:");
		sb.append(timestamp);
		sb.append(", mpiDomain:");
		sb.append(mpiDomain);
		sb.append(", desc:");
		sb.append(description);
		sb.append(", safe source:");
		sb.append(safeSource);
		sb.append(", number of persons:");
		sb.append(personCount);
		sb.append(", config:");
		sb.append(config);
		return sb.toString();
	}
}
