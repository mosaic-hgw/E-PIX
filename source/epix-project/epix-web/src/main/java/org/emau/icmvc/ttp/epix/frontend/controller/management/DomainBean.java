package org.emau.icmvc.ttp.epix.frontend.controller.management;

/*-
 * ###license-information-start###
 * E-PIX - Enterprise Patient Identifier Cross-referencing
 * __
 * Copyright (C) 2009 - 2025 Trusted Third Party of the University Medicine Greifswald
 * 							kontakt-ths@uni-greifswald.de
 * 
 * 							concept and implementation
 * 							l.geidel,c.schack, d.langner, g.koetzschke
 * 
 * 							web client
 * 							a.blumentritt, f.m. moser
 * 
 * 							docker
 * 							r.schuldt, f.m. moser
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
import org.emau.icmvc.ttp.epix.common.model.config.ValidatorConfigDTO;
import org.emau.icmvc.ttp.epix.common.model.config.ValidatorDTO;
import org.emau.icmvc.ttp.epix.common.model.config.ValidatorGroupDTO;
import org.emau.icmvc.ttp.epix.common.model.enums.BlockingMode;
import org.emau.icmvc.ttp.epix.common.model.enums.FieldName;
import org.emau.icmvc.ttp.epix.common.model.enums.MatchingMode;
import org.emau.icmvc.ttp.epix.common.model.enums.PersistMode;
import org.emau.icmvc.ttp.epix.common.model.enums.ValidatorOperator;
import org.emau.icmvc.ttp.epix.frontend.controller.common.AbstractEpixBean;
import org.emau.icmvc.ttp.epix.frontend.controller.common.ICRUDObject;
import org.emau.icmvc.ttp.epix.frontend.controller.component.DomainSelector;
import org.emau.icmvc.ttp.epix.frontend.model.ValidatorNode;
import org.emau.icmvc.ttp.epix.frontend.util.EpixHelper;
import org.icmvc.ttp.web.controller.Text;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;

public class DomainBean extends AbstractEpixBean implements ICRUDObject<DomainDTO>
{
	private static final Charset MATCHING_CONFIG_XML_CHARSET = StandardCharsets.UTF_8;
	private DomainDTO selected;
	private DomainSelector domainSelector;

	private String selectedValueFieldField;
	private String selectedValueFieldLabel;

	private ReasonDTO selectedReason;

	// Validation
	private TreeNode<ValidatorNode> validatorTree;
	private ValidatorConfigDTO selectedValidatorConfig;
	private ValidatorDTO selectedValidator;
	private ValidatorGroupDTO selectedValidatorGroup;
	private ValidatorGroupDTO selectedValidatorParent;
	private boolean selectedIsNew;

	// Preprocessing
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
	public void init(EpixHelper epixHelper, Text text)
	{
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
		loadWebEnhancements();
		pageMode = PageMode.READ;
	}

	private void loadWebEnhancements()
	{
		disableAutomaticMatch = getConfig().getMatchingConfig().getThresholdAutomaticMatch() >= 1000;
		loadValidatorTree();
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
		loadWebEnhancements();

		pageMode = PageMode.NEW;
	}

	private void fillPreprocessingField(PreprocessingFieldDTO field)
	{
		field.getSimpleTransformationTypes().put("æ", "a");
		field.getSimpleTransformationTypes().put("œ", "o");
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
		field.getComplexTransformationClasses().add("org.emau.icmvc.ttp.deduplication.preprocessing.impl.CharNormalizationTransformation");
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
					getManager().updateDomainInUse(selected.getName(), selected.getLabel(), selected.getDescription());
				}
				// Large edit
				else
				{
					getManager().updateDomain(selected);
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
				getManager().addDomain(tmp);
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
			getManager().deleteDomain(selected.getName(), false);
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
		selectedMatchingField.setBlockingThreshold(0.4);
		selectedMatchingField.setMatchingThreshold(0.8);
		selectedMatchingField.setWeight(5);
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
		else
		{
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

	private void loadValidatorTree()
	{
		validatorTree = new DefaultTreeNode<>(new ValidatorNode());
		if (getConfig().getValidation() != null)
		{
			getConfig().getValidation().getValidationConfigs().forEach(v -> {
				if (v.getValidator() != null)
				{
					new DefaultTreeNode<>(new ValidatorNode(v, v.getValidator()), validatorTree);
				}
				else if (v.getValidatorGroup() != null)
				{
					addValidatorGroup(v, v.getValidatorGroup(), validatorTree);
				}
			});
		}
	}

	private void addValidatorGroup(ValidatorConfigDTO validatorConfig, ValidatorGroupDTO validatorGroup, TreeNode<ValidatorNode> parent)
	{
		TreeNode<ValidatorNode> validatorGroupNode = new DefaultTreeNode<>(new ValidatorNode(validatorConfig, validatorGroup), parent);
		validatorGroupNode.setExpanded(true);

		validatorGroup.getValidators().forEach(validator -> new DefaultTreeNode<>(new ValidatorNode(validatorConfig, validator), validatorGroupNode));
		validatorGroup.getValidatorGroups().forEach(group -> addValidatorGroup(validatorConfig, group, validatorGroupNode));
	}

	public void onNewValidator(ValidatorGroupDTO parent)
	{
		selectedValidatorConfig = new ValidatorConfigDTO();
		selectedValidator = new ValidatorDTO();
		selectedValidatorParent = parent;
		selectedIsNew = true;
	}

	public void onEditValidator(ValidatorConfigDTO validatorConfig, ValidatorDTO validator, ValidatorGroupDTO parent)
	{
		selectedValidatorConfig = validatorConfig;
		selectedValidator = validator;
		selectedValidatorParent = parent;
		selectedIsNew = false;
	}

	public void onSaveValidator()
	{
		if (selectedIsNew)
		{
			if (selectedValidatorParent == null)
			{
				getConfig().getValidation().getValidationConfigs().add(new ValidatorConfigDTO(selectedValidatorConfig.getField(), selectedValidator, null));
			}
			else
			{
				selectedValidatorParent.getValidators().add(selectedValidator);
			}
		}
		loadValidatorTree();
	}

	public void onRemoveValidator(ValidatorDTO validator, ValidatorGroupDTO parent)
	{
		if (parent == null)
		{
			getConfig().getValidation().getValidationConfigs().removeIf(vc -> validator.equals(vc.getValidator()));
		}
		else
		{
			parent.getValidators().remove(validator);
		}
		loadValidatorTree();
	}

	public void onNewValidatorGroup(ValidatorGroupDTO parent)
	{
		selectedValidatorConfig = new ValidatorConfigDTO();
		selectedValidatorGroup = new ValidatorGroupDTO();
		selectedValidatorParent = parent;
		selectedIsNew = true;
	}

	public void onEditValidatorGroup(ValidatorConfigDTO validatorConfig, ValidatorGroupDTO validatorGroup, ValidatorGroupDTO parent)
	{
		selectedValidatorConfig = validatorConfig;
		selectedValidatorGroup = validatorGroup;
		selectedValidatorParent = parent;
		selectedIsNew = false;
	}

	public void onSaveValidatorGroup()
	{
		if (selectedIsNew)
		{
			if (selectedValidatorParent == null)
			{
				getConfig().getValidation().getValidationConfigs().add(new ValidatorConfigDTO(selectedValidatorConfig.getField(), null, selectedValidatorGroup));
			}
			else
			{
				selectedValidatorParent.getValidatorGroups().add(selectedValidatorGroup);
			}
		}
		loadValidatorTree();
	}

	public void onRemoveValidatorGroup(ValidatorGroupDTO validatorGroup, ValidatorGroupDTO parent)
	{
		if (parent == null)
		{
			getConfig().getValidation().getValidationConfigs().removeIf(vc -> validatorGroup.equals(vc.getValidatorGroup()));
		}
		else
		{
			parent.getValidatorGroups().remove(validatorGroup);
		}
		loadValidatorTree();
	}
	
	public void onChangeValidatorClass()
	{
		selectedValidator.setCriterion(null);
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
			selected.setConfigObjects(getManager().parseMatchingConfiguration(xml));
			loadWebEnhancements();
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
			xml = getManager().encodeMatchingConfiguration(selected.getConfigObjects());
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

	public TreeNode<ValidatorNode> getValidatorTree()
	{
		return validatorTree;
	}

	public boolean isSelectedIsNew()
	{
		return selectedIsNew;
	}

	public void setSelectedIsNew(boolean selectedIsNew)
	{
		this.selectedIsNew = selectedIsNew;
	}

	public ValidatorDTO getSelectedValidator()
	{
		return selectedValidator;
	}

	public void setSelectedValidator(ValidatorDTO selectedValidator)
	{
		this.selectedValidator = selectedValidator;
	}

	public ValidatorConfigDTO getSelectedValidatorConfig()
	{
		return selectedValidatorConfig;
	}

	public void setSelectedValidatorConfig(ValidatorConfigDTO selectedValidatorConfig)
	{
		this.selectedValidatorConfig = selectedValidatorConfig;
	}

	public ValidatorGroupDTO getSelectedValidatorGroup()
	{
		return selectedValidatorGroup;
	}

	public void setSelectedValidatorGroup(ValidatorGroupDTO selectedValidatorGroup)
	{
		this.selectedValidatorGroup = selectedValidatorGroup;
	}

	public ValidatorGroupDTO getSelectedValidatorParent()
	{
		return selectedValidatorParent;
	}

	public String[] getAvailableQualifiedClassNames()
	{
		return new String[] { "org.emau.icmvc.ttp.deduplication.impl.validation.AlphabetValidator",
				"org.emau.icmvc.ttp.deduplication.impl.validation.LengthValidator",
				"org.emau.icmvc.ttp.deduplication.impl.validation.EmptyFieldValidator",
				"org.emau.icmvc.ttp.deduplication.impl.validation.RegExValidator",
				"org.emau.icmvc.ttp.deduplication.impl.validation.Base64Validator",
				"org.emau.icmvc.ttp.deduplication.impl.validation.BalancedBloomFilterValidator",
				"org.emau.icmvc.ttp.deduplication.impl.validation.GermanZipCodeValidator",
				"org.emau.icmvc.ttp.deduplication.impl.validation.PhoneNumberValidator",
				"org.emau.icmvc.ttp.deduplication.impl.validation.EMailValidator",
				"org.emau.icmvc.ttp.deduplication.impl.validation.EGKValidator" };
	}

	public boolean containsCriteria(String validatorClassName)
	{
		return containsStringCriteria(validatorClassName) || containsIntegerCriteria(validatorClassName) || containsBooleanCriteria(validatorClassName);
	}

	public boolean containsStringCriteria(String validatorClassName)
	{
		List<String> criteriaValidators = Arrays.asList("org.emau.icmvc.ttp.deduplication.impl.validation.RegExValidator",
				"org.emau.icmvc.ttp.deduplication.impl.validation.AlphabetValidator");
		return criteriaValidators.contains(validatorClassName);
	}

	public boolean containsIntegerCriteria(String validatorClassName)
	{
		List<String> criteriaValidators = Arrays.asList("org.emau.icmvc.ttp.deduplication.impl.validation.LengthValidator",
				"org.emau.icmvc.ttp.deduplication.impl.validation.BalancedBloomFilterValidator");
		return criteriaValidators.contains(validatorClassName);
	}

	public boolean containsBooleanCriteria(String validatorClassName)
	{
		List<String> criteriaValidators = Arrays.asList("org.emau.icmvc.ttp.deduplication.impl.validation.EmptyFieldValidator");
		return criteriaValidators.contains(validatorClassName);
	}

	public ValidatorOperator[] getAvailableOperators()
	{
		return ValidatorOperator.values();
	}
}
