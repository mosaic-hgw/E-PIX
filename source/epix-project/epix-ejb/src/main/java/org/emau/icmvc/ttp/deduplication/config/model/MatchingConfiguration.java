package org.emau.icmvc.ttp.deduplication.config.model;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.emau.icmvc.ttp.epix.common.exception.InvalidParameterException;
import org.emau.icmvc.ttp.epix.common.exception.MPIErrorCode;
import org.emau.icmvc.ttp.epix.common.exception.MPIException;
import org.emau.icmvc.ttp.epix.common.model.config.ConfigurationContainer;
import org.emau.icmvc.ttp.epix.common.model.enums.FieldName;
import org.emau.icmvc.ttp.epix.common.model.enums.MatchingMode;
import org.emau.icmvc.ttp.epix.common.model.enums.PersistMode;
import org.emau.icmvc.ttp.epix.common.utils.XMLBindingUtil;

/**
 *
 * @author geidell
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MatchingConfiguration", propOrder = { "matchingMode", "mpiGenerator", "mpiPrefix", "useNotifications", "lowMemory", "persistMode", "requiredFields",
		"valueFieldsMapping", "deduplication", "privacy", "preprocessingConfig", "matching" })
@XmlRootElement(name = "MatchingConfiguration")
public class MatchingConfiguration
{
	private static final String DOMAIN_CONFIG_XSD = "matching-config-2.9.0.xsd";
	private static final XMLBindingUtil BINDER = new XMLBindingUtil();

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

	public MatchingConfiguration()
	{}

	public MatchingConfiguration(ConfigurationContainer configObjects, String domainName)
	{
		// TODO validierung
		if (configObjects == null)
		{
			throw new RuntimeException(new MPIException(MPIErrorCode.INTERNAL_ERROR,
					"exception while marshaling matching configuration objects for domain '" + domainName + "'"));
		}
		matchingMode = configObjects.getMatchingMode();
		mpiGenerator = configObjects.getMpiGenerator();
		mpiPrefix = configObjects.getMpiPrefix();
		useNotifications = configObjects.isUseNotifications();
		lowMemory = configObjects.isLimitSearchForLowMemory();
		persistMode = configObjects.getPersistMode();
		if (configObjects.getRequiredFields() != null)
		{
			requiredFields = new RequiredFields(configObjects.getRequiredFields());
		}
		if (configObjects.getValueFieldMapping() != null)
		{
			valueFieldsMapping = new ValueFieldsMapping();
			valueFieldsMapping.setValue1(configObjects.getValueFieldMapping().get("value1"));
			valueFieldsMapping.setValue2(configObjects.getValueFieldMapping().get("value2"));
			valueFieldsMapping.setValue3(configObjects.getValueFieldMapping().get("value3"));
			valueFieldsMapping.setValue4(configObjects.getValueFieldMapping().get("value4"));
			valueFieldsMapping.setValue5(configObjects.getValueFieldMapping().get("value5"));
			valueFieldsMapping.setValue6(configObjects.getValueFieldMapping().get("value6"));
			valueFieldsMapping.setValue7(configObjects.getValueFieldMapping().get("value7"));
			valueFieldsMapping.setValue8(configObjects.getValueFieldMapping().get("value8"));
			valueFieldsMapping.setValue9(configObjects.getValueFieldMapping().get("value9"));
			valueFieldsMapping.setValue10(configObjects.getValueFieldMapping().get("value10"));
		}
		if (configObjects.getDeduplication() != null)
		{
			deduplication = new Deduplication(configObjects.getDeduplication());
		}
		if (configObjects.getPrivacy() != null)
		{
			privacy = new Privacy(configObjects.getPrivacy());
		}
		if (configObjects.getPreprocessingFields() != null && !configObjects.getPreprocessingFields().isEmpty())
		{
			preprocessingConfig = new PreprocessingConfig(configObjects.getPreprocessingFields());
		}
		if (configObjects.getMatchingConfig() != null)
		{
			matching = new Matching(configObjects.getMatchingConfig());
		}
	}

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

	/**
	 * Marshals this matching configuration into the XML representation.
	 * @return the XML representation for this matching configuration
	 * @throws JAXBException if this matching configuration can not be marshalled
	 */
	public String toXml() throws JAXBException
	{
		return BINDER.marshal(DOMAIN_CONFIG_XSD, this);
	}

	/**
	 * Marshals this matching configuration into the XML representation. If it cannot be marshalled,
	 * this method throws a runtime exception which wraps an {@link MPIException} describing the
	 * problem referring to the given domain name.
	 * @param domainName the name of the domain to refer to in case of problems
	 * @return the XML representation for this matching configuration
	 * @throws RuntimeException if this matching configuration can not be marshalled
	 */
	public String toXml(String domainName) throws RuntimeException
	{
		try
		{
			return toXml();
		}
		catch (JAXBException e)
		{
			throw new RuntimeException(new MPIException(MPIErrorCode.INTERNAL_ERROR,
					"exception while marshaling matching configuration objects for domain '" + domainName + "': " + e.getMessage(), e));
		}
	}

	/**
	 * {@return a configuration container for this matching configuration}
	 */
	public ConfigurationContainer toConfigurationContainer()
	{
		List<FieldName> requiredFields = new ArrayList<>();
		if (getRequiredFields() != null)
		{
			requiredFields.addAll(getRequiredFields().getNames());
		}
		Map<String, String> valueFieldMapping = new HashMap<>();
		if (getValueFieldsMapping() != null)
		{
			ValueFieldsMapping mapping = getValueFieldsMapping();
			// unschoen, aber wird bei umstellung auf eav eh anders
			if (mapping.getValue1() != null && !mapping.getValue1().isEmpty())
			{
				valueFieldMapping.put("value1", mapping.getValue1());
			}
			if (mapping.getValue2() != null && !mapping.getValue2().isEmpty())
			{
				valueFieldMapping.put("value2", mapping.getValue2());
			}
			if (mapping.getValue3() != null && !mapping.getValue3().isEmpty())
			{
				valueFieldMapping.put("value3", mapping.getValue3());
			}
			if (mapping.getValue4() != null && !mapping.getValue4().isEmpty())
			{
				valueFieldMapping.put("value4", mapping.getValue4());
			}
			if (mapping.getValue5() != null && !mapping.getValue5().isEmpty())
			{
				valueFieldMapping.put("value5", mapping.getValue5());
			}
			if (mapping.getValue6() != null && !mapping.getValue6().isEmpty())
			{
				valueFieldMapping.put("value6", mapping.getValue6());
			}
			if (mapping.getValue7() != null && !mapping.getValue7().isEmpty())
			{
				valueFieldMapping.put("value7", mapping.getValue7());
			}
			if (mapping.getValue8() != null && !mapping.getValue8().isEmpty())
			{
				valueFieldMapping.put("value8", mapping.getValue8());
			}
			if (mapping.getValue9() != null && !mapping.getValue9().isEmpty())
			{
				valueFieldMapping.put("value9", mapping.getValue9());
			}
			if (mapping.getValue10() != null && !mapping.getValue10().isEmpty())
			{
				valueFieldMapping.put("value10", mapping.getValue10());
			}
		}

		return new ConfigurationContainer(getMatchingMode(), getMpiGenerator(),
				getMpiPrefix(), isUseNotifications(), isLowMemory(), getPersistMode(),
				requiredFields, valueFieldMapping, getDeduplication() != null ? getDeduplication().toDTO() : null,
				getPrivacy() != null ? getPrivacy().toDTO() : null, getMatching().toDTO(),
				getPreprocessingConfig() != null ? getPreprocessingConfig().toDTO() : null);
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof MatchingConfiguration))
		{
			return false;
		}

		MatchingConfiguration that = (MatchingConfiguration) o;

		if (isUseNotifications() != that.isUseNotifications())
		{
			return false;
		}
		if (isLowMemory() != that.isLowMemory())
		{
			return false;
		}
		if (getMatchingMode() != that.getMatchingMode())
		{
			return false;
		}
		if (getMpiGenerator() != null ? !getMpiGenerator().equals(that.getMpiGenerator()) : that.getMpiGenerator() != null)
		{
			return false;
		}
		if (getMpiPrefix() != null ? !getMpiPrefix().equals(that.getMpiPrefix()) : that.getMpiPrefix() != null)
		{
			return false;
		}
		if (getPersistMode() != that.getPersistMode())
		{
			return false;
		}
		if (getRequiredFields() != null ? !getRequiredFields().equals(that.getRequiredFields()) : that.getRequiredFields() != null)
		{
			return false;
		}
		if (getValueFieldsMapping() != null ? !getValueFieldsMapping().equals(that.getValueFieldsMapping()) : that.getValueFieldsMapping() != null)
		{
			return false;
		}
		if (getDeduplication() != null ? !getDeduplication().equals(that.getDeduplication()) : that.getDeduplication() != null)
		{
			return false;
		}
		if (getPrivacy() != null ? !getPrivacy().equals(that.getPrivacy()) : that.getPrivacy() != null)
		{
			return false;
		}
		if (getPreprocessingConfig() != null ? !getPreprocessingConfig().equals(that.getPreprocessingConfig()) : that.getPreprocessingConfig() != null)
		{
			return false;
		}
		return getMatching() != null ? getMatching().equals(that.getMatching()) : that.getMatching() == null;
	}

	@Override
	public int hashCode()
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

	/**
	 * Creates a matching configuration from an XML description
	 * @param configXml the configuration as XML
	 * @return a matching configuration for an XML description
	 * @throws JAXBException if the XML cannot be parsed
	 */
	public static MatchingConfiguration fromXml(String configXml) throws JAXBException
	{
			return BINDER.parse(MatchingConfiguration.class, configXml, DOMAIN_CONFIG_XSD);
	}

	/**
	 * Creates a matching configuration from an XML description. If the XML cannot be parsed,
	 * this method throws a runtime exception which wraps an {@link MPIException} describing the
	 * problem referring to the given domain name.
	 * @param configXml the configuration as XML
	 * @param domainName the name of the domain to refer to in case of problems
	 * @return a matching configuration for an XML description
	 * @throws RuntimeException if the XML cannot be parsed
	 */
	public static MatchingConfiguration fromXml(String configXml, String domainName) throws RuntimeException
	{
		MatchingConfiguration matchingConfiguration;
		try
		{
			matchingConfiguration = fromXml(configXml);
		}
		catch (JAXBException e)
		{
			throw new RuntimeException(new MPIException(MPIErrorCode.INTERNAL_ERROR,
					"exception while parsing matching configuration for domain '" + domainName + "': " + e.getMessage(), e));
		}
		if (matchingConfiguration.getMatching() == null)
		{
			throw new RuntimeException(
					new InvalidParameterException("domain.config", "matching configuration <matching> not set for domain: " + domainName));
		}
		else if (matchingConfiguration.getMatchingMode().equals(MatchingMode.MATCHING_IDENTITIES)
				&& (matchingConfiguration.getMatching().getFields() == null || matchingConfiguration.getMatching().getFields().isEmpty()))
		{
			throw new RuntimeException(
					new InvalidParameterException("domain.config", "matching configuration <matching> contains no fields for domain: " + domainName));
		}
		for (Field field : matchingConfiguration.getMatching().getFields())
		{
			if (field.getBlockingThreshold() == 0. && field.getMatchingThreshold() == 0.)
			{
				throw new RuntimeException(new InvalidParameterException("domain.config",
						"matching configuration <matching> for domain: " + domainName + " contains no threshold for field " + field.getName()));
			}
		}
		return matchingConfiguration;
	}
}
