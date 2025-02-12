package org.emau.icmvc.ttp.epix.frontend.controller.common;

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

import java.text.MessageFormat;
import java.util.List;

import jakarta.faces.annotation.ManagedProperty;
import jakarta.inject.Inject;
import org.emau.icmvc.ttp.epix.common.exception.ValidatorException;
import org.emau.icmvc.ttp.epix.common.model.DomainDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentifierDomainDTO;
import org.emau.icmvc.ttp.epix.common.model.SourceDTO;
import org.emau.icmvc.ttp.epix.common.model.enums.FieldName;
import org.emau.icmvc.ttp.epix.frontend.controller.component.DomainSelector;
import org.emau.icmvc.ttp.epix.frontend.util.EpixHelper;
import org.emau.icmvc.ttp.epix.service.EPIXService;

public abstract class AbstractEpixWebBean extends AbstractEpixBean
{
	@Inject
	@ManagedProperty(value = "#{epixHelper}")
	protected EpixHelper epixHelper;

	@Deprecated
	public boolean showValueField(String valueField, boolean showBloomfilterFields)
	{
		return epixHelper.showValueField(valueField, showBloomfilterFields);
	}

	@Deprecated
	public boolean showValueField(String valueField)
	{
		return epixHelper.showValueField(valueField, true);
	}

	@Deprecated
	public String getValueFieldLabel(String valueField)
	{
		return epixHelper.getValueFieldLabel(valueField);
	}

	public DomainSelector getDomainSelector()
	{
		return epixHelper.getDomainSelector();
	}

	public List<IdentifierDomainDTO> getIdentifierDomainsFiltered()
	{
		return epixHelper.getIdentifierDomainsFiltered();
	}

	public List<SourceDTO> getSources()
	{
		return getManager().getSources();
	}

	public DomainDTO getSelectedDomain()
	{
		return getDomainSelector().getSelectedDomain();
	}

	public boolean isUseNotifications()
	{
		return getDomainSelector().getSelectedDomainConfiguration().isUseNotifications();
	}

	public void setEpixHelper(EpixHelper epixHelper)
	{
		this.epixHelper = epixHelper;
	}

	public String getDeduplicationReasonLabel(String reason)
	{
		return getBundle().containsKey("deduplication." + reason) ? getBundle().getString("deduplication." + reason) : reason;
	}

	public String getDeduplicationReasonDescription(String description)
	{
		return getBundle().containsKey("deduplication." + description) ? getBundle().getString("deduplication." + description) : description;
	}
	
	public void handleValidatorException(ValidatorException e)
	{
		logger.warn(e.getLocalizedMessage());
		for (String field : e.getInvalidFields())
		{
			String fieldLabel = epixHelper.getFieldLabel(field);
			getSelectedDomain().getConfigObjects().getValidation().getValidationConfigs().stream().filter(vc -> vc.getField().equals(FieldName.valueOf(field))).forEach(vc ->
					{
						if (vc.getValidator() != null)
						{
							String className = vc.getValidator().getQualifiedClassName().substring(vc.getValidator().getQualifiedClassName().lastIndexOf(".") + 1);
							Object[] args = {fieldLabel, getBundle().getString("model.domain.validation.validator." + className) + (vc.getValidator().getCriterion() != null ? ": " + vc.getValidator().getCriterion() : "")};
							logMessage(new MessageFormat(getBundle().getString("page.person.message.warn.validatorFailed")).format(args), Severity.WARN);
						}
						if (vc.getValidatorGroup() != null)
						{
							int rules = vc.getValidatorGroup().getValidators() != null ? vc.getValidatorGroup().getValidators().size() : 0;
							rules += vc.getValidatorGroup().getValidatorGroups() != null ? vc.getValidatorGroup().getValidatorGroups().size() : 0;
							Object[] args = {fieldLabel, rules, getCommonBundle().getString("ui.operator." + vc.getValidatorGroup().getOperator())};
							logMessage(new MessageFormat(getBundle().getString("page.person.message.warn.validatorGroupFailed")).format(args), Severity.WARN);
						}
					}
			);
		}
	}

	public EPIXService getServiceWithAutomaticNotification()
	{
		return getServiceWithAutomaticNotification(isUseNotifications());
	}
}
