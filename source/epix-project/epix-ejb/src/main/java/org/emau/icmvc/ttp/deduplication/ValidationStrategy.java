package org.emau.icmvc.ttp.deduplication;


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


import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.emau.icmvc.ttp.deduplication.config.model.Validation;
import org.emau.icmvc.ttp.deduplication.config.model.Validator;
import org.emau.icmvc.ttp.deduplication.config.model.ValidatorConfig;
import org.emau.icmvc.ttp.deduplication.config.model.ValidatorGroup;
import org.emau.icmvc.ttp.deduplication.impl.validation.GroupValidator;
import org.emau.icmvc.ttp.epix.common.deduplication.IValidator;
import org.emau.icmvc.ttp.epix.common.exception.MPIErrorCode;
import org.emau.icmvc.ttp.epix.common.exception.MPIException;
import org.emau.icmvc.ttp.epix.common.model.enums.FieldName;
import org.emau.icmvc.ttp.epix.common.model.enums.ValidatorOperator;
import org.emau.icmvc.ttp.epix.persistence.model.Identity;
import org.emau.icmvc.ttp.utils.ReflectionUtil;
import org.emau.icmvc.ttp.utils.ValidatorResult;

/**
 * @author Christopher Hampf
 */
public class ValidationStrategy
{
	private static final Logger logger = LogManager.getLogger(ValidationStrategy.class);

	private final Map<FieldName, List<IValidator>> validators = new EnumMap<>(FieldName.class);

	public ValidationStrategy(Validation validation)
	{
		if (validation != null)
		{
			logger.info("validation configured");

			for (ValidatorConfig conf : validation.getValidatorConfigs())
			{
				ValidatorGroup tmpGroup = new ValidatorGroup();

				List<Validator> tmpValList = new ArrayList<>();
				if (conf.getValidator() != null)
				{
					tmpValList.add(conf.getValidator());
				}
				tmpGroup.setValidators(tmpValList);

				List<ValidatorGroup> tmpGroupList = new ArrayList<>();
				if (conf.getValidatorGroup() != null)
				{
					tmpGroupList.add(conf.getValidatorGroup());
				}
				tmpGroup.setValidatorGroups(tmpGroupList);

				tmpGroup.setOperator(ValidatorOperator.ALL);

				IValidator root = new GroupValidator(tmpGroup);

				if (!validators.containsKey(conf.getField()))
				{
					validators.put(conf.getField(), new ArrayList<>());
				}

				validators.get(conf.getField()).add(root);
			}
		}
		else
		{
			logger.info("no validation available");
		}
	}

	public ValidatorResult validate(Identity identity) throws MPIException
	{
		boolean result = true;
		Set<FieldName> invalidFields = new HashSet<>();
		for (Map.Entry<FieldName, List<IValidator>> valElement : validators.entrySet())
		{
			Object object;
			try
			{
				object = ReflectionUtil.getProperty(identity, valElement.getKey().name());
			}
			catch (Exception e)
			{
				String message = "exception while validation: " + e.getMessage();
				logger.error(message, e);
				throw new MPIException(MPIErrorCode.INTERNAL_ERROR, message, e);
			}
			String value = (String) object;
			boolean tmpResult = true;
			for (IValidator val : valElement.getValue())
			{
				tmpResult = tmpResult && val.validate(value);
			}

			result = result && tmpResult;

			if (!tmpResult)
			{
				invalidFields.add(valElement.getKey());
			}
		}

		return new ValidatorResult(result, invalidFields);
	}
}
