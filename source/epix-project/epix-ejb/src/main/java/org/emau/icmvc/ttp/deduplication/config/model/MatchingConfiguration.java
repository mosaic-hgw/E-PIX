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

import org.emau.icmvc.ttp.epix.common.model.enums.MatchingMode;
import org.emau.icmvc.ttp.epix.common.model.enums.PersistMode;

/**
 * 
 * @author geidell
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MatchingConfiguration", propOrder = { "matchingMode", "mpiGenerator", "mpiPrefix", "useNotifications", "lowMemory", "persistMode", "requiredFields",
		"valueFieldsMapping", "deduplication", "privacy", "preprocessingConfig", "matching" })
public class MatchingConfiguration
{
	@XmlElement(name = "matching-mode", required = true)
	private MatchingMode matchingMode;
	@XmlElement(name = "mpi-generator", required = true)
	private String mpiGenerator;
	@XmlElement(name = "mpi-prefix", required = true)
	private String mpiPrefix;
	@XmlElement(name = "use-notifications", required = false, defaultValue = "false")
	private boolean useNotifications = false;
	@XmlElement(name = "limit-search-to-reduce-memory-consumption", required = false, defaultValue = "false")
	private boolean lowMemory = false;
	@XmlElement(name = "persist-mode", required = false, defaultValue = "IDENTIFYING")
	private PersistMode persistMode;
	@XmlElement(name = "required-fields", required = false)
	private RequiredFields requiredFields;
	@XmlElement(name = "value-fields-mapping", required = false)
	private ValueFieldsMapping valueFieldsMapping;
	@XmlElement(name = "deduplication", required = false)
	private Deduplication deduplication;
	@XmlElement(name = "privacy", required = false)
	private Privacy privacy;
	@XmlElement(name = "preprocessing-config", required = false)
	private PreprocessingConfig preprocessingConfig;
	@XmlElement(required = true)
	private Matching matching;

	public MatchingMode getMatchingMode()
	{
		return matchingMode;
	}

	public void setMatchingMode(MatchingMode matchingMode)
	{
		this.matchingMode = matchingMode;
	}

	public String getMpiGenerator()
	{
		return mpiGenerator;
	}

	public void setMpiGenerator(String mpiGenerator)
	{
		this.mpiGenerator = mpiGenerator;
	}

	public String getMpiPrefix()
	{
		return mpiPrefix;
	}

	public void setMpiPrefix(String mpiPrefix)
	{
		this.mpiPrefix = mpiPrefix;
	}

	public boolean isUseNotifications()
	{
		return useNotifications;
	}

	public void setUseNotifications(boolean useNotifications)
	{
		this.useNotifications = useNotifications;
	}

	public boolean isLowMemory()
	{
		return lowMemory;
	}

	public void setLowMemory(boolean lowMemory)
	{
		this.lowMemory = lowMemory;
	}

	public PersistMode getPersistMode()
	{
		return persistMode;
	}

	public void setPersistMode(PersistMode persistMode)
	{
		this.persistMode = persistMode;
	}

	public RequiredFields getRequiredFields()
	{
		return requiredFields;
	}

	public void setRequiredFields(RequiredFields requiredFields)
	{
		this.requiredFields = requiredFields;
	}

	public ValueFieldsMapping getValueFieldsMapping()
	{
		return valueFieldsMapping;
	}

	public void setValueFieldsMapping(ValueFieldsMapping valueFieldsMapping)
	{
		this.valueFieldsMapping = valueFieldsMapping;
	}

	public Deduplication getDeduplication()
	{
		return deduplication;
	}

	public void setDeduplication(Deduplication deduplication)
	{
		this.deduplication = deduplication;
	}

	public Privacy getPrivacy()
	{
		return privacy;
	}

	public void setPrivacy(Privacy privacy)
	{
		this.privacy = privacy;
	}

	public PreprocessingConfig getPreprocessingConfig()
	{
		return preprocessingConfig;
	}

	public void setPreprocessingConfig(PreprocessingConfig preprocessingConfig)
	{
		this.preprocessingConfig = preprocessingConfig;
	}

	public Matching getMatching()
	{
		return matching;
	}

	public void setMatching(Matching matching)
	{
		this.matching = matching;
	}

	@Override public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (!(o instanceof MatchingConfiguration))
			return false;

		MatchingConfiguration that = (MatchingConfiguration) o;

		if (isUseNotifications() != that.isUseNotifications())
			return false;
		if (isLowMemory() != that.isLowMemory())
			return false;
		if (getMatchingMode() != that.getMatchingMode())
			return false;
		if (getMpiGenerator() != null ? !getMpiGenerator().equals(that.getMpiGenerator()) : that.getMpiGenerator() != null)
			return false;
		if (getMpiPrefix() != null ? !getMpiPrefix().equals(that.getMpiPrefix()) : that.getMpiPrefix() != null)
			return false;
		if (getPersistMode() != that.getPersistMode())
			return false;
		if (getRequiredFields() != null ? !getRequiredFields().equals(that.getRequiredFields()) : that.getRequiredFields() != null)
			return false;
		if (getValueFieldsMapping() != null ? !getValueFieldsMapping().equals(that.getValueFieldsMapping()) : that.getValueFieldsMapping() != null)
			return false;
		if (getDeduplication() != null ? !getDeduplication().equals(that.getDeduplication()) : that.getDeduplication() != null)
			return false;
		if (getPrivacy() != null ? !getPrivacy().equals(that.getPrivacy()) : that.getPrivacy() != null)
			return false;
		if (getPreprocessingConfig() != null ? !getPreprocessingConfig().equals(that.getPreprocessingConfig()) : that.getPreprocessingConfig() != null)
			return false;
		return getMatching() != null ? getMatching().equals(that.getMatching()) : that.getMatching() == null;
	}

	@Override public int hashCode()
	{
		int result = getMatchingMode() != null ? getMatchingMode().hashCode() : 0;
		result = 31 * result + (getMpiGenerator() != null ? getMpiGenerator().hashCode() : 0);
		result = 31 * result + (getMpiPrefix() != null ? getMpiPrefix().hashCode() : 0);
		result = 31 * result + (isUseNotifications() ? 1 : 0);
		result = 31 * result + (isLowMemory() ? 1 : 0);
		result = 31 * result + (getPersistMode() != null ? getPersistMode().hashCode() : 0);
		result = 31 * result + (getRequiredFields() != null ? getRequiredFields().hashCode() : 0);
		result = 31 * result + (getValueFieldsMapping() != null ? getValueFieldsMapping().hashCode() : 0);
		result = 31 * result + (getDeduplication() != null ? getDeduplication().hashCode() : 0);
		result = 31 * result + (getPrivacy() != null ? getPrivacy().hashCode() : 0);
		result = 31 * result + (getPreprocessingConfig() != null ? getPreprocessingConfig().hashCode() : 0);
		result = 31 * result + (getMatching() != null ? getMatching().hashCode() : 0);
		return result;
	}

	@Override
	public String toString()
	{
		return "MatchingConfiguration [matchingMode=" + matchingMode + ", mpiGenerator=" + mpiGenerator + ", mpiPrefix=" + mpiPrefix
				+ ", useNotifications=" + useNotifications + ", lowMemory=" + lowMemory + ", persistMode=" + persistMode
				+ ", requiredFields=" + requiredFields + ", valueFieldsMapping=" + valueFieldsMapping + ", preprocessingConfig=" + preprocessingConfig + ", matching=" + matching + "]";
	}
}
