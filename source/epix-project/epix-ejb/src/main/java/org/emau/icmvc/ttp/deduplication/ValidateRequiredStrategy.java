package org.emau.icmvc.ttp.deduplication;

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
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.emau.icmvc.ttp.deduplication.config.model.RequiredFields;
import org.emau.icmvc.ttp.epix.common.exception.InvalidParameterException;
import org.emau.icmvc.ttp.epix.common.exception.MPIErrorCode;
import org.emau.icmvc.ttp.epix.common.exception.MPIException;
import org.emau.icmvc.ttp.epix.common.model.enums.FieldName;
import org.emau.icmvc.ttp.epix.persistence.model.IdentityPreprocessed;
import org.emau.icmvc.ttp.utils.ReflectionUtil;

public class ValidateRequiredStrategy
{
	private final static Logger logger = LogManager.getLogger(ValidateRequiredStrategy.class);
	private final List<FieldName> requiredFields = new ArrayList<>();

	public ValidateRequiredStrategy(RequiredFields requiredFieldsConfig)
	{
		if (requiredFieldsConfig != null)
		{
			requiredFields.addAll(requiredFieldsConfig.getNames());
			logger.info("required fields configured");
		}
		else
		{
			logger.info("no required config available");
		}
	}

	public void checkValidation(IdentityPreprocessed ip) throws InvalidParameterException, MPIException
	{
		for (FieldName requiredField : requiredFields)
		{
			Object object;
			try
			{
				object = ReflectionUtil.getProperty(ip, requiredField.name());
			}
			catch (Exception e)
			{
				String message = "exception while preprocessing: " + e.getMessage();
				logger.error(message, e);
				throw new MPIException(MPIErrorCode.INTERNAL_ERROR, message, e);
			}
			if (object == null || object instanceof String && ((String) object).isEmpty()
					|| object instanceof Character && (((Character) object).charValue() == ' ' || ((Character) object).charValue() == Character.MIN_VALUE))
			{
				// aus abwaertskompatibilitaetsgruenden repraesentiert ' ' auf db-ebene den null-wert bei gender
				String message = "no value for required match field " + requiredField;
				logger.error(message);
				throw new InvalidParameterException(requiredField.name(), message);
			}
		}
	}
}
