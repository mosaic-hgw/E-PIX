package org.emau.icmvc.ttp.deduplication.impl.validation;

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

import org.emau.icmvc.ttp.deduplication.config.model.Validator;
import org.emau.icmvc.ttp.epix.common.deduplication.IValidator;

public class ValidatorFactory
{
	private ValidatorFactory()
	{

	}

	public static IValidator build(Validator validator)
	{
		final String ROOT = "org.emau.icmvc.ttp.deduplication.impl.validation.";

		if (validator == null)
			throw new IllegalArgumentException("validator is null");

		return switch (validator.getClassName())
		{
			case ROOT + "RegExValidator" -> new RegExValidator(validator.getValidationCriterion());
			case ROOT + "EGKValidator" -> new EGKValidator();
			case ROOT + "GermanZipCodeValidator" -> new GermanZipCodeValidator();
			case ROOT + "EMailValidator" -> new EMailValidator();
			case ROOT + "Base64Validator" -> new Base64Validator();
			case ROOT + "EmptyFieldValidator" -> new EmptyFieldValidator(Boolean.parseBoolean(validator.getValidationCriterion()));
			case ROOT + "LengthValidator" -> new LengthValidator(Integer.parseInt(validator.getValidationCriterion()));
			case ROOT + "AlphabetValidator" -> new AlphabetValidator(validator.getValidationCriterion());
			case ROOT + "PhoneNumberValidator" -> new PhoneNumberValidator();
			case ROOT + "BalancedBloomFilterValidator" -> new BalancedBloomFilterValidator(Integer.parseInt(validator.getValidationCriterion()));
			default -> null;
		};
	}

	@Override
	public String toString()
	{
		return "ValidatorFactory{}";
	}
}
