package org.emau.icmvc.ttp.epix.common.model.config;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.emau.icmvc.ttp.epix.common.model.enums.FieldName;
import org.emau.icmvc.ttp.epix.common.model.enums.MatchingMode;
import org.emau.icmvc.ttp.epix.common.model.enums.PersistMode;

/**
 *
 * @author weiherg, geidell
 *
 */
public class ConfigurationContainer implements Serializable
{
	private static final long serialVersionUID = -8092674346366089256L;
	private final MatchingMode matchingMode;
	private final String mpiGenerator;
	private final String mpiPrefix;
	private boolean useNotifications = false;
	private boolean limitSearchForLowMemory = false;
	private final PersistMode persistMode;
	private final List<FieldName> requiredFields = new ArrayList<>();
	private final Map<String, String> valueFieldMapping = new HashMap<>();
	private final DeduplicationDTO deduplication;
	private final PrivacyDTO privacy;
	private final MatchingDTO matchingConfig;

	public ConfigurationContainer(MatchingMode matchingMode, String mpiGenerator, String mpiPrefix, boolean useNotifications, boolean limitSearchForLowMemory,
			PersistMode persistMode, List<FieldName> requiredFields, Map<String, String> valueFieldMapping, DeduplicationDTO deduplication, PrivacyDTO privacy, MatchingDTO matchingConfig)
	{
		super();
		this.matchingMode = matchingMode;
		this.mpiGenerator = mpiGenerator;
		this.mpiPrefix = mpiPrefix;
		this.useNotifications = useNotifications;
		this.limitSearchForLowMemory = limitSearchForLowMemory;
		this.persistMode = persistMode;
		this.requiredFields.addAll(requiredFields);
		this.valueFieldMapping.putAll(valueFieldMapping);
		this.deduplication = deduplication;
		this.privacy = privacy;
		this.matchingConfig = matchingConfig;
	}

	public boolean isUseNotifications()
	{
		return useNotifications;
	}

	public void setUseNotifications(boolean useNotifications)
	{
		this.useNotifications = useNotifications;
	}

	public boolean isLimitSearchForLowMemory()
	{
		return limitSearchForLowMemory;
	}

	public void setLimitSearchForLowMemory(boolean limitSearchForLowMemory)
	{
		this.limitSearchForLowMemory = limitSearchForLowMemory;
	}

	public MatchingMode getMatchingMode()
	{
		return matchingMode;
	}

	public String getMpiGenerator()
	{
		return mpiGenerator;
	}

	public String getMpiPrefix()
	{
		return mpiPrefix;
	}

	public PersistMode getPersistMode()
	{
		return persistMode == null ? PersistMode.IDENTIFYING : persistMode;
	}

	public List<FieldName> getRequiredFields()
	{
		return requiredFields;
	}

	public Map<String, String> getValueFieldMapping()
	{
		return valueFieldMapping;
	}

	public DeduplicationDTO getDeduplication()
	{
		return deduplication;
	}

	public PrivacyDTO getPrivacy()
	{
		return privacy;
	}

	public MatchingDTO getMatchingConfig()
	{
		return matchingConfig;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (limitSearchForLowMemory ? 1231 : 1237);
		result = prime * result + (matchingConfig == null ? 0 : matchingConfig.hashCode());
		result = prime * result + (matchingMode == null ? 0 : matchingMode.hashCode());
		result = prime * result + (mpiGenerator == null ? 0 : mpiGenerator.hashCode());
		result = prime * result + (mpiPrefix == null ? 0 : mpiPrefix.hashCode());
		result = prime * result + (persistMode == null ? 0 : persistMode.hashCode());
		result = prime * result + requiredFields.hashCode();
		result = prime * result + (useNotifications ? 1231 : 1237);
		result = prime * result + valueFieldMapping.hashCode();
		result = prime * result + (privacy == null ? 0 : privacy.hashCode());
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
		ConfigurationContainer other = (ConfigurationContainer) obj;
		if (limitSearchForLowMemory != other.limitSearchForLowMemory)
		{
			return false;
		}
		if (persistMode != other.persistMode)
		{
			return false;
		}
		if (matchingConfig == null)
		{
			if (other.matchingConfig != null)
			{
				return false;
			}
		}
		else if (!matchingConfig.equals(other.matchingConfig))
		{
			return false;
		}
		if (matchingMode != other.matchingMode)
		{
			return false;
		}
		if (mpiGenerator == null)
		{
			if (other.mpiGenerator != null)
			{
				return false;
			}
		}
		else if (!mpiGenerator.equals(other.mpiGenerator))
		{
			return false;
		}
		if (mpiPrefix == null)
		{
			if (other.mpiPrefix != null)
			{
				return false;
			}
		}
		else if (!mpiPrefix.equals(other.mpiPrefix))
		{
			return false;
		}
		if (requiredFields == null)
		{
			if (other.requiredFields != null)
			{
				return false;
			}
		}
		else if (!requiredFields.equals(other.requiredFields))
		{
			return false;
		}
		if (useNotifications != other.useNotifications)
		{
			return false;
		}
		if (valueFieldMapping == null)
		{
			if (other.valueFieldMapping != null)
			{
				return false;
			}
		}
		else if (!valueFieldMapping.equals(other.valueFieldMapping))
		{
			return false;
		}
		if (privacy == null)
		{
			return other.privacy == null;
		}
		else
		{
			return privacy.equals(other.privacy);
		}
	}

	@Override
	public String toString()
	{
		return "ConfigurationContainer [matchingMode=" + matchingMode + ", mpiGenerator=" + mpiGenerator + ", mpiPrefix=" + mpiPrefix
				+ ", useNotifications=" + useNotifications + ", limitSearchForLowMemory=" + limitSearchForLowMemory + ", persistMode="
				+ persistMode + ", requiredFields=" + requiredFields + ", valueFieldMapping=" + valueFieldMapping + ", matchingConfig=" + matchingConfig + ", privacy=" + privacy + "]";
	}
}
