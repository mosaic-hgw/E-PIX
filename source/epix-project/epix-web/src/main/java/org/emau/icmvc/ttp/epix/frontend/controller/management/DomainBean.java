package org.emau.icmvc.ttp.epix.frontend.controller.management;

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

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.emau.icmvc.ttp.epix.common.exception.DuplicateEntryException;
import org.emau.icmvc.ttp.epix.common.exception.InvalidParameterException;
import org.emau.icmvc.ttp.epix.common.exception.MPIException;
import org.emau.icmvc.ttp.epix.common.exception.ObjectInUseException;
import org.emau.icmvc.ttp.epix.common.exception.UnknownObjectException;
import org.emau.icmvc.ttp.epix.common.exception.UnknownObjectType;
import org.emau.icmvc.ttp.epix.common.model.DomainDTO;
import org.emau.icmvc.ttp.epix.common.model.config.BalancedDTO;
import org.emau.icmvc.ttp.epix.common.model.config.BloomFilterConfigDTO;
import org.emau.icmvc.ttp.epix.common.model.config.ConfigurationContainer;
import org.emau.icmvc.ttp.epix.common.model.config.FieldDTO;
import org.emau.icmvc.ttp.epix.common.model.config.PreprocessingFieldDTO;
import org.emau.icmvc.ttp.epix.common.model.config.ReasonDTO;
import org.emau.icmvc.ttp.epix.common.model.config.SourceFieldDTO;
import org.emau.icmvc.ttp.epix.common.model.enums.BlockingMode;
import org.emau.icmvc.ttp.epix.common.model.enums.FieldName;
import org.emau.icmvc.ttp.epix.common.model.enums.MatchingMode;
import org.emau.icmvc.ttp.epix.common.model.enums.PersistMode;
import org.emau.icmvc.ttp.epix.frontend.controller.common.AbstractEpixBean;
import org.emau.icmvc.ttp.epix.frontend.controller.common.ICRUDObject;
import org.emau.icmvc.ttp.epix.frontend.controller.component.DomainSelector;
import org.emau.icmvc.ttp.epix.frontend.util.EpixHelper;
import org.emau.icmvc.ttp.epix.service.EPIXManagementService;
import org.icmvc.ttp.web.controller.Text;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

public class DomainBean extends AbstractEpixBean implements ICRUDObject<DomainDTO>
{
	private static final Charset MATCHING_CONFIG_XML_CHARSET = StandardCharsets.UTF_8;
	private DomainDTO selected;
	private DomainSelector domainSelector;

	private String selectedValueFieldField;
	private String selectedValueFieldLabel;

	private ReasonDTO selectedReason;

	private PreprocessingFieldDTO selectedPreprocessingField;
	private boolean selectedPreprocessingFieldIsNew;
	private String selectedSimpleTransformationInput;
	private String selectedSimpleTransformationOutput;
	private String selectedComplexTransformationClass;
	private String selectedSimpleFilterPassAlphabet;
	private String selectedSimpleFilterReplaceCharacter;

	private FieldDTO selectedMatchingField;
	private boolean disableAutomaticMatch;
	private boolean selectedMatchingFieldIsNew;

	private BloomFilterConfigDTO selectedBloomFilter;
	private boolean selectedBloomFilterIsNew;

	private SourceFieldDTO selectedSourceField;
	private boolean selectedSourceFieldIsNew;

	private DoubleHashingSaltType doubleHashingSaltType;

	@Override
	public void init(EPIXManagementService managementService, EpixHelper epixHelper, Text text)
	{
		this.managementService = managementService;
		this.text = text;
		reload();
	}

	@Override
	public void reload()
	{
		domainSelector.loadDomains();
		pageMode = PageMode.READ;
	}

	@Override
	public void onShowDetails(DomainDTO domain)
	{
		selected = domain;
		disableAutomaticMatch = getConfig().getMatchingConfig().getThresholdAutomaticMatch() >= 1000;
		pageMode = PageMode.READ;
	}

	@Override
	public void onNew()
	{
		selected = new DomainDTO();

		// Set some defaults
		getConfig().getMatchingConfig().setParallelMatchingAfter(1000);
		getConfig().setMpiPrefix("1001");
		getConfig().getMatchingConfig().getFields()
				.add(new FieldDTO(FieldName.firstName, 0.4, BlockingMode.TEXT, 0.8, 8, "org.emau.icmvc.ttp.deduplication.impl.LevenshteinAlgorithm", ' ', 0.1, 0.1, 0.2));
		getConfig().getMatchingConfig().getFields()
				.add(new FieldDTO(FieldName.lastName, 0.0, BlockingMode.TEXT, 0.8, 6, "org.emau.icmvc.ttp.deduplication.impl.LevenshteinAlgorithm", Character.MIN_VALUE, 0.0, 0.0, 0.0));
		getConfig().getMatchingConfig().getFields()
				.add(new FieldDTO(FieldName.gender, 0.0, BlockingMode.TEXT, 0.75, 3, "org.emau.icmvc.ttp.deduplication.impl.LevenshteinAlgorithm", Character.MIN_VALUE, 0.0, 0.0, 0.0));
		getConfig().getMatchingConfig().getFields()
				.add(new FieldDTO(FieldName.birthDate, 0.6, BlockingMode.NUMBERS, 1, 9, "org.emau.icmvc.ttp.deduplication.impl.LevenshteinAlgorithm", Character.MIN_VALUE, 0.0, 0.0, 0.0));
		disableAutomaticMatch = false;
		getConfig().getRequiredFields().add(FieldName.firstName);
		getConfig().getRequiredFields().add(FieldName.lastName);
		getConfig().getRequiredFields().add(FieldName.gender);
		getConfig().getRequiredFields().add(FieldName.birthDate);
		PreprocessingFieldDTO firstNamePreprocessing = new PreprocessingFieldDTO();
		firstNamePreprocessing.setFieldName(FieldName.firstName);
		fillPreprocessingField(firstNamePreprocessing);
		getConfig().getPreprocessingFields().add(firstNamePreprocessing);
		PreprocessingFieldDTO lastNamePreprocessing = new PreprocessingFieldDTO();
		lastNamePreprocessing.setFieldName(FieldName.lastName);
		fillPreprocessingField(lastNamePreprocessing);
		getConfig().getPreprocessingFields().add(lastNamePreprocessing);
		getConfig().getDeduplication().getReasons().add(new ReasonDTO("TYPING_ERROR", "TYPING_ERROR.description"));
		getConfig().getDeduplication().getReasons().add(new ReasonDTO("NAME_CHANGE_MARRIAGE", "NAME_CHANGE_MARRIAGE.description"));

		pageMode = PageMode.NEW;
	}

	private void fillPreprocessingField(PreprocessingFieldDTO field)
	{
		field.getSimpleTransformationTypes().put("é", "e");
		field.getSimpleTransformationTypes().put("è", "e");
		field.getSimpleTransformationTypes().put("ê", "e");
		field.getSimpleTransformationTypes().put("ë", "e");
		field.getSimpleTransformationTypes().put("à", "a");
		field.getSimpleTransformationTypes().put("â", "a");
		field.getSimpleTransformationTypes().put("æ", "a");
		field.getSimpleTransformationTypes().put("ô", "o");
		field.getSimpleTransformationTypes().put("ò", "o");
		field.getSimpleTransformationTypes().put("œ", "o");
		field.getSimpleTransformationTypes().put("û", "u");
		field.getSimpleTransformationTypes().put("ù", "u");
		field.getSimpleTransformationTypes().put("ç", "c");
		field.getSimpleTransformationTypes().put("?", "");
		field.getSimpleTransformationTypes().put("Dr.", "");
		field.getSimpleTransformationTypes().put("Prof.", "");
		field.getSimpleTransformationTypes().put("med.", "");
		field.getSimpleTransformationTypes().put("rer.", "");
		field.getSimpleTransformationTypes().put("nat.", "");
		field.getSimpleTransformationTypes().put("Ing.", "");
		field.getSimpleTransformationTypes().put("Dipl.", "");
		field.getSimpleTransformationTypes().put(",", "");
		field.getSimpleTransformationTypes().put("-", "");
		field.getComplexTransformationClasses().add("org.emau.icmvc.ttp.deduplication.preprocessing.impl.ToUpperCaseTransformation");
		field.getComplexTransformationClasses().add("org.emau.icmvc.ttp.deduplication.preprocessing.impl.CharsMutationTransformation");
	}
	
	@Override
	public void onEdit(DomainDTO object)
	{
		onShowDetails(object);
		pageMode = PageMode.EDIT;
	}

	@Override
	public void onSaveCurrent()
	{
		Object[] args = { selected.getLabel() };
		selected.setConfig(null); // Set null so ConfigurationObjects will be used in update

		// Update
		if (pageMode == PageMode.EDIT)
		{
			try
			{
				// Small edit
				if (selected.isInUse())
				{
					managementService.updateDomainInUse(selected.getName(), selected.getLabel(), selected.getDescription());
				}
				// Large edit
				else
				{
					managementService.updateDomain(selected);
				}
				logMessage(new MessageFormat(getBundle().getString("domain.message.edit.success")).format(args), Severity.INFO);
				reload();
				updateDomainSelector();
			}
			catch (UnknownObjectException e)
			{
				logMessage(getBundle().getString("domain.message.edit.unknownDomain"), Severity.WARN);
			}
			catch (InvalidParameterException e)
			{
				logMessage(getBundle().getString("domain.message.edit.invalidParameter"), Severity.WARN);
				logMessage(e.getLocalizedMessage(), Severity.ERROR);
			}
			catch (ObjectInUseException e)
			{
				logMessage(getBundle().getString("domain.message.edit.objectInUse"), Severity.WARN);
			}
			catch (MPIException e)
			{
				logMPIException(e);
			}
		}
		// Save new
		else
		{
			// operate on a copy for the case that we stay on the dialog after a warning
			DomainDTO tmp = new DomainDTO(selected);
			tmp.setName(getValidatedName(tmp));

			try
			{
				managementService.addDomain(tmp);
				logMessage(new MessageFormat(getBundle().getString("domain.message.add.success")).format(args), Severity.INFO);
				// on success write back changes on selected
				selected = new DomainDTO(tmp);
				reload();
				updateDomainSelector();
			}
			catch (DuplicateEntryException e)
			{
				logMessage(getBundle().getString("domain.message.add.duplicateEntry"), Severity.WARN);
			}
			catch (InvalidParameterException e)
			{
				if ("domain.getConfig()".equals(e.getParameterName()))
				{
					logMessage(getBundle().getString("domain.message.add.invalidConfig"), Severity.WARN);
				}
				else if ("domain.getName()".equals(e.getParameterName()))
				{
					Object[] msg = { tmp.getName() };
					logMessage(new MessageFormat(getBundle().getString("domain.message.add.invalidParameterDomainName")).format(msg), Severity.WARN);
				}
				else
				{
					logMessage(getBundle().getString("domain.message.add.invalidParameter"), Severity.WARN);
					logMessage(e.getLocalizedMessage(), Severity.ERROR);
				}
			}
			catch (UnknownObjectException e)
			{
				if (e.getObjectType().equals(UnknownObjectType.SOURCE))
				{
					logMessage(getBundle().getString("domain.message.add.unknownSourceException"), Severity.WARN);
				}
				else if (e.getObjectType().equals(UnknownObjectType.IDENTITFIER_DOMAIN))
				{
					logMessage(getBundle().getString("domain.message.add.unknownIdentifierDomain"), Severity.WARN);
				}
			}
			catch (MPIException e)
			{
				logMPIException(e);
			}
		}
	}

	private static String getValidatedName(DomainDTO tmp)
	{
		return StringUtils.isEmpty(tmp.getName()) ? tmp.getLabel().replace(" ", "_") : tmp.getName();
	}

	private void updateDomainSelector()
	{
		if (domainSelector.getSelectedDomainName() == null || domainSelector.getSelectedDomainName().equals(selected.getName()))
		{
			domainSelector.setSelectedDomain(selected.getName());
		}
	}

	@Override
	public void onCancel()
	{
		selected = null;
		pageMode = PageMode.READ;
	}

	@Override
	public boolean isEditable(DomainDTO object)
	{
		return object != null && !object.isInUse();
	}

	@Override
	public void onDeleteCurrent()
	{
		Object[] args = { selected.getLabel() };
		try
		{
			managementService.deleteDomain(selected.getName(), false);
			logMessage(new MessageFormat(getBundle().getString("domain.message.delete.success")).format(args), Severity.INFO);
			reload();
			updateDomainSelector();
			selected = null;
		}
		catch (UnknownObjectException e)
		{
			logMessage(getBundle().getString("domain.message.delete.unknownObject"), Severity.WARN);
		}
		catch (InvalidParameterException e)
		{
			logMessage(getBundle().getString("domain.message.delete.invalidParameter"), Severity.WARN);
		}
		catch (ObjectInUseException e)
		{
			logMessage(getBundle().getString("domain.message.delete.objectInUse"), Severity.WARN);
		}
		catch (MPIException e)
		{
			logMPIException(e);
		}
	}

	@Override
	public List<DomainDTO> getAll()
	{
		return domainSelector.getDomains();
	}

	@Override
	public boolean isNew()
	{
		if (selected == null || selected.getName() == null)
		{
			return true;
		}

		if (pageMode == PageMode.NEW)
		{
			return true;
		}

		for (DomainDTO domainDTO : getAll())
		{
			if (domainDTO.getName().equals(selected.getName()))
			{
				return false;
			}
		}
		return true;
	}

	public void onNewValueField()
	{
		selectedValueFieldField = null;
		selectedValueFieldLabel = null;
	}

	public void onAddValueField()
	{
		getConfig().getValueFieldMapping().put(selectedValueFieldField, selectedValueFieldLabel);
	}

	public void onRemoveValueField(String field)
	{
		getConfig().getValueFieldMapping().remove(field);
	}

	public void onNewMatchingField()
	{
		selectedMatchingField = new FieldDTO();
		selectedMatchingField.setAlgorithm("org.emau.icmvc.ttp.deduplication.impl.LevenshteinAlgorithm");
		selectedMatchingField.setWeight(1);
		selectedMatchingFieldIsNew = true;
	}

	public void onEditMatchingField(FieldDTO matchingField)
	{
		selectedMatchingField = matchingField;
		selectedMatchingFieldIsNew = false;
	}

	public void onAddMatchingField()
	{
		if (selectedMatchingFieldIsNew)
		{
			getConfig().getMatchingConfig().getFields().add(selectedMatchingField);
		}
	}

	public void onRemoveMatchingField(FieldDTO matchingField)
	{
		if (getConfig().getMatchingConfig().getFields().size() > 1)
		{
			getConfig().getMatchingConfig().getFields().remove(matchingField);
		}
		else {
			logMessage(getBundle().getString("page.management.domain.matching.message.atLeastOneField"), Severity.WARN);
		}
	}

	public void onNewReason()
	{
		selectedReason = new ReasonDTO();
	}

	public void onAddReason()
	{
		getConfig().getDeduplication().getReasons().add(selectedReason);
	}

	public void onRemoveReason(ReasonDTO reason)
	{
		getConfig().getDeduplication().getReasons().remove(reason);
	}

	public void onNewPreprocessingField()
	{
		selectedPreprocessingField = new PreprocessingFieldDTO();
		selectedPreprocessingFieldIsNew = true;
	}

	public void onEditPreprocessingField(PreprocessingFieldDTO preprocessingField)
	{
		selectedPreprocessingField = preprocessingField;
		selectedPreprocessingFieldIsNew = false;
	}

	public void onAddPreprocessingField()
	{
		if (selectedPreprocessingFieldIsNew)
		{
			getConfig().getPreprocessingFields().add(selectedPreprocessingField);
		}
	}

	public void onRemovePreprocessingField(PreprocessingFieldDTO preprocessingField)
	{
		getConfig().getPreprocessingFields().remove(preprocessingField);
	}

	public void onNewSimpleTransformation()
	{
		selectedSimpleTransformationInput = null;
		selectedSimpleTransformationOutput = null;
	}

	public void onAddSimpleTransformation()
	{
		selectedPreprocessingField.getSimpleTransformationTypes().put(selectedSimpleTransformationInput, selectedSimpleTransformationOutput);
	}

	public void onRemoveSimpleTransformation(String input)
	{
		selectedPreprocessingField.getSimpleTransformationTypes().remove(input);
	}

	public void onNewComplexTransformation()
	{
		selectedComplexTransformationClass = null;
	}

	public void onAddComplexTransformation()
	{
		selectedPreprocessingField.getComplexTransformationClasses().add(selectedComplexTransformationClass);
	}

	public void onRemoveComplexTransformation(String className)
	{
		selectedPreprocessingField.getComplexTransformationClasses().remove(className);
	}

	public void onNewSimpleFilter()
	{
		selectedSimpleFilterPassAlphabet = null;
		selectedSimpleFilterReplaceCharacter = null;
	}

	public void onAddSimpleFilter()
	{
		selectedPreprocessingField.getSimpleFilterTypes()
				.put(selectedSimpleFilterPassAlphabet, StringUtils.isEmpty(selectedSimpleFilterReplaceCharacter) ? null : selectedSimpleFilterReplaceCharacter.charAt(0));
	}

	public void onRemoveSimpleFilter(String passAlphabet)
	{
		selectedPreprocessingField.getSimpleFilterTypes().remove(passAlphabet);
	}

	public void onNewBloomFilter()
	{
		selectedBloomFilter = new BloomFilterConfigDTO();
		selectedBloomFilterIsNew = true;
	}

	public void onEditBloomFilter(BloomFilterConfigDTO bloomFilter)
	{
		selectedBloomFilter = bloomFilter;
		selectedBloomFilterIsNew = false;
	}

	public void onAddBloomFilter()
	{
		if (selectedBloomFilterIsNew)
		{
			getConfig().getPrivacy().getBloomFilterConfigs().add(selectedBloomFilter);
		}
	}

	public void onRemoveBloomFilter(BloomFilterConfigDTO bloomFilter)
	{
		getConfig().getPrivacy().getBloomFilterConfigs().remove(bloomFilter);
	}

	public void onNewSourceField()
	{
		selectedSourceField = new SourceFieldDTO();
		doubleHashingSaltType = DoubleHashingSaltType.FIXED;
		selectedSourceFieldIsNew = true;
	}

	public void onEditSourceField(SourceFieldDTO sourceField)
	{
		selectedSourceField = sourceField;
		doubleHashingSaltType = selectedSourceField.getSaltField() != null ? DoubleHashingSaltType.FIELD : DoubleHashingSaltType.FIXED;
		selectedSourceFieldIsNew = false;
	}

	public void onAddSourceField()
	{
		if (selectedSourceFieldIsNew)
		{
			selectedBloomFilter.getSourceFields().add(selectedSourceField);
		}
	}

	public void onUploadMatchingConfigXml(FileUploadEvent event)
	{
		onNew();
		String xml = new String(event.getFile().getContent(), MATCHING_CONFIG_XML_CHARSET);
		try
		{
			selected.setConfigObjects(managementService.parseMatchingConfiguration(xml));
		}
		catch (MPIException | InvalidParameterException e)
		{
			onCancel();
			logger.error(e.getLocalizedMessage());
			logMessage(getBundle().getString("domain.message.import.invalidConfig"), Severity.ERROR);
		}
	}

	public StreamedContent onDownloadMatchingConfigXml() throws MPIException, InvalidParameterException
	{
		String xml = selected.getConfig();

		if (StringUtils.isBlank(xml) || hasEditableMatchingConfig())
		{
			// configuration objects to xml config
			xml = managementService.encodeMatchingConfiguration(selected.getConfigObjects());
		}

		ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes(MATCHING_CONFIG_XML_CHARSET));
		return DefaultStreamedContent.builder().stream(() -> bais)
				.contentType("text/xml").name("matching-config-for-domain-" + getValidatedName(selected) + ".xml")
				.contentEncoding(MATCHING_CONFIG_XML_CHARSET.name()).build();
	}

	public void onRemoveSourceField(SourceFieldDTO sourceField)
	{
		selectedBloomFilter.getSourceFields().remove(sourceField);
	}

	public boolean hasEditableMatchingConfig()
	{
		return getPageMode() == PageMode.NEW || (getPageMode() == PageMode.EDIT && selected.getPersonCount() == 0);
	}

	public void setDomainSelector(DomainSelector domainSelector)
	{
		this.domainSelector = domainSelector;
	}

	@Override
	public DomainDTO getSelected()
	{
		return selected;
	}

	@Override
	public void setSelected(DomainDTO selected)
	{
		this.selected = selected != null ? selected : this.selected;
	}

	public ConfigurationContainer getConfig()
	{
		return selected == null ? null : selected.getConfigObjects();
	}

	public MatchingMode[] getMatchingModeOptions()
	{
		return new MatchingMode[] { MatchingMode.MATCHING_IDENTITIES, MatchingMode.NO_DECISION };
	}

	public PersistMode[] getPersistModeOptions()
	{
		return PersistMode.values();
	}

	public String[] getMpiGeneratorOptions()
	{
		return new String[] { "org.emau.icmvc.ttp.epix.gen.impl.EAN13Generator" };
	}

	public FieldName[] getFieldNameOptions()
	{
		return FieldName.values();
	}

	public List<FieldName> getAvailablePreprocessingFieldNameOptions()
	{
		return Arrays.stream(FieldName.values())
				.filter(f -> selectedPreprocessingField != null && f.equals(selectedPreprocessingField.getFieldName())
						|| !getConfig().getPreprocessingFields()
						.stream()
						.map(PreprocessingFieldDTO::getFieldName)
						.toList()
						.contains(f))
				.collect(Collectors.toList());
	}

	public List<FieldName> getAvailableMatchingFieldNameOptions()
	{
		return Arrays.stream(FieldName.values())
				.filter(f -> selectedMatchingField != null && f.equals(selectedMatchingField.getName())
						|| !getConfig().getMatchingConfig().getFields()
						.stream()
						.map(FieldDTO::getName)
						.toList()
						.contains(f))
				.collect(Collectors.toList());
	}

	public List<FieldName> getAvailableBloomFilterFieldNameOptions()
	{
		return Arrays.stream(FieldName.values())
				.filter(f -> selectedBloomFilter != null && f.equals(selectedBloomFilter.getField())
						|| !getConfig().getPrivacy().getBloomFilterConfigs()
						.stream()
						.map(BloomFilterConfigDTO::getField)
						.toList()
						.contains(f))
				.collect(Collectors.toList());
	}

	public List<String> getAvailableValueFieldOptions()
	{
		return Arrays.stream(getValueFieldOptions()).filter(v -> !getConfig().getValueFieldMapping().containsKey(v)).collect(Collectors.toList());
	}

	public String[] getValueFieldOptions()
	{
		return new String[] { "value1", "value2", "value3", "value4", "value5", "value6", "value7", "value8", "value9", "value10" };
	}

	public List<String> getValueFieldKeysSorted()
	{
		return getConfig().getValueFieldMapping().keySet().stream().sorted().collect(Collectors.toList());
	}

	public String getClassNameFromFullPath(String fullPath)
	{
		return fullPath.substring(fullPath.lastIndexOf(".") + 1);
	}

	public String getSelectedValueFieldField()
	{
		return selectedValueFieldField;
	}

	public void setSelectedValueFieldField(String selectedValueFieldField)
	{
		this.selectedValueFieldField = selectedValueFieldField;
	}

	public String getSelectedValueFieldLabel()
	{
		return selectedValueFieldLabel;
	}

	public void setSelectedValueFieldLabel(String selectedValueFieldLabel)
	{
		this.selectedValueFieldLabel = selectedValueFieldLabel;
	}

	public FieldDTO getSelectedMatchingField()
	{
		return selectedMatchingField;
	}

	public void setSelectedMatchingField(FieldDTO selectedMatchingField)
	{
		this.selectedMatchingField = selectedMatchingField;
	}

	public String[] getMatchingAlgorithmOptions()
	{
		return new String[] { "org.emau.icmvc.ttp.deduplication.impl.ColognePhoneticAlgorithm", "org.emau.icmvc.ttp.deduplication.impl.DeterministicAlgorithm",
				"org.emau.icmvc.ttp.deduplication.impl.LevenshteinAlgorithm", "org.emau.icmvc.ttp.deduplication.impl.SorensenDiceCoefficientCoded",
				"org.emau.icmvc.ttp.deduplication.impl.JaccardSimilarityAlgorithmCoded", "org.emau.icmvc.ttp.deduplication.impl.SorensenDiceCoefficient",
				"org.emau.icmvc.ttp.deduplication.impl.JaccardSimilarityAlgorithm" };
	}

	public String[] getBloomFilterAlgorithmOptions()
	{
		return new String[] { "org.emau.icmvc.ttp.deduplication.impl.bloomfilter.RandomHashingStrategy",
				"org.emau.icmvc.ttp.deduplication.impl.bloomfilter.DoubleHashingStrategy",
				"org.emau.icmvc.ttp.deduplication.impl.bloomfilter.DoubleHashingStrategyFaster" };
	}

	public PreprocessingFieldDTO getSelectedPreprocessingField()
	{
		return selectedPreprocessingField;
	}

	public void setSelectedPreprocessingField(PreprocessingFieldDTO selectedPreprocessingField)
	{
		this.selectedPreprocessingField = selectedPreprocessingField;
	}

	public ReasonDTO getSelectedReason()
	{
		return selectedReason;
	}

	public void setSelectedReason(ReasonDTO selectedReason)
	{
		this.selectedReason = selectedReason;
	}

	public BlockingMode[] getBlockingModeOptions()
	{
		return BlockingMode.values();
	}

	public int getSelectedMatchingFieldThreshold()
	{
		return (int) (selectedMatchingField.getMatchingThreshold() * 100);
	}

	public void setSelectedMatchingFieldThreshold(int percent)
	{
		selectedMatchingField.setMatchingThreshold((double) percent / 100);
	}

	public int getSelectedBlockingFieldThreshold()
	{
		return (int) (selectedMatchingField.getBlockingThreshold() * 100);
	}

	public void setSelectedBlockingFieldThreshold(int percent)
	{
		selectedMatchingField.setBlockingThreshold((double) percent / 100);
	}

	public int getPenaltyNotAPerfectMatch()
	{
		return (int) (selectedMatchingField.getPenaltyNotAPerfectMatch() * -100);
	}

	public void setPenaltyNotAPerfectMatch(int percent)
	{
		selectedMatchingField.setPenaltyNotAPerfectMatch((double) percent / -100);
	}

	public int getPenaltyOneShort()
	{
		return (int) (selectedMatchingField.getPenaltyOneShort() * -100);
	}

	public void setPenaltyOneShort(int percent)
	{
		selectedMatchingField.setPenaltyOneShort((double) percent / -100);
	}

	public int getPenaltyBothShort()
	{
		return (int) (selectedMatchingField.getPenaltyBothShort() * -100);
	}

	public void setPenaltyBothShort(int percent)
	{
		selectedMatchingField.setPenaltyBothShort((double) percent / -100);
	}

	public BloomFilterConfigDTO getSelectedBloomFilter()
	{
		return selectedBloomFilter;
	}

	public void setSelectedBloomFilter(BloomFilterConfigDTO selectedBloomFilter)
	{
		this.selectedBloomFilter = selectedBloomFilter;
	}

	public boolean isBalanced()
	{
		return selectedBloomFilter.getBalanced() != null;
	}

	public void setBalanced(boolean balanced)
	{
		selectedBloomFilter.setBalanced(balanced ? new BalancedDTO() : null);
	}

	public DoubleHashingSaltType getDoubleHashingSaltType()
	{
		return doubleHashingSaltType;
	}

	public void setDoubleHashingSaltType(DoubleHashingSaltType doubleHashingSaltType)
	{
		this.doubleHashingSaltType = doubleHashingSaltType;
		if (DoubleHashingSaltType.FIXED.equals(this.doubleHashingSaltType))
		{
			selectedSourceField.setSaltField(null);
		}
		else if (DoubleHashingSaltType.FIELD.equals(this.doubleHashingSaltType))
		{
			selectedSourceField.setSaltValue(null);
		}
	}

	public SourceFieldDTO getSelectedSourceField()
	{
		return selectedSourceField;
	}

	public void setSelectedSourceField(SourceFieldDTO selectedSourceField)
	{
		this.selectedSourceField = selectedSourceField;
	}

	public enum DoubleHashingSaltType
	{
		FIXED, FIELD
	}

	public boolean isDisableAutomaticMatch()
	{
		return disableAutomaticMatch;
	}

	public void setDisableAutomaticMatch(boolean disableAutomaticMatch)
	{
		this.disableAutomaticMatch = disableAutomaticMatch;
		if (disableAutomaticMatch)
		{
			getConfig().getMatchingConfig().setThresholdAutomaticMatch(1000);
		}
		else if (getConfig().getMatchingConfig().getThresholdAutomaticMatch() == 1000)
		{
			getConfig().getMatchingConfig().setThresholdAutomaticMatch(14.5);
		}
	}

	public boolean isSelectedPreprocessingFieldIsNew()
	{
		return selectedPreprocessingFieldIsNew;
	}

	public String getSelectedSimpleTransformationInput()
	{
		return selectedSimpleTransformationInput;
	}

	public void setSelectedSimpleTransformationInput(String selectedSimpleTransformationInput)
	{
		this.selectedSimpleTransformationInput = selectedSimpleTransformationInput;
	}

	public String getSelectedSimpleTransformationOutput()
	{
		return selectedSimpleTransformationOutput;
	}

	public void setSelectedSimpleTransformationOutput(String selectedSimpleTransformationOutput)
	{
		this.selectedSimpleTransformationOutput = selectedSimpleTransformationOutput;
	}

	public String getSelectedComplexTransformationClass()
	{
		return selectedComplexTransformationClass;
	}

	public void setSelectedComplexTransformationClass(String selectedComplexTransformationClass)
	{
		this.selectedComplexTransformationClass = selectedComplexTransformationClass;
	}

	public String getSelectedSimpleFilterPassAlphabet()
	{
		return selectedSimpleFilterPassAlphabet;
	}

	public void setSelectedSimpleFilterPassAlphabet(String selectedSimpleFilterPassAlphabet)
	{
		this.selectedSimpleFilterPassAlphabet = selectedSimpleFilterPassAlphabet;
	}

	public String getSelectedSimpleFilterReplaceCharacter()
	{
		return selectedSimpleFilterReplaceCharacter;
	}

	public void setSelectedSimpleFilterReplaceCharacter(String selectedSimpleFilterReplaceCharacter)
	{
		this.selectedSimpleFilterReplaceCharacter = selectedSimpleFilterReplaceCharacter;
	}

	public String[] getComplexTransformationOptions()
	{
		return new String[] { "org.emau.icmvc.ttp.deduplication.preprocessing.impl.ToUpperCaseTransformation", "org.emau.icmvc.ttp.deduplication.preprocessing.impl.CharNormalizationTransformation",
				"org.emau.icmvc.ttp.deduplication.preprocessing.impl.CharsMutationTransformation", "org.emau.icmvc.ttp.deduplication.preprocessing.impl.TrimTransformation" };
	}
}
