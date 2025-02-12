package org.emau.icmvc.ttp.deduplication.impl.validation;

/*-
 * ###license-information-start###
 * E-PIX - Enterprise Patient Identifier
 * 							Cross-referencing
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

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.emau.icmvc.ttp.deduplication.config.model.Validator;
import org.emau.icmvc.ttp.epix.common.deduplication.IValidator;

/**
 * RegEx validator that validate a given value by a defined pattern.
 *
 * @author Christopher Hampf
 */
public class RegExValidator implements IValidator
{
	private final String regexStr;

	public RegExValidator(Validator validator)
	{
		this.regexStr = validator.getValidationCriterion();
	}

	public RegExValidator(String regex)
	{
		this.regexStr = regex;
	}

	@Override
	public boolean validate(String value)
	{
		String val = (value == null) ? "" : value;

		Pattern p = Pattern.compile(regexStr);
		Matcher m = p.matcher(val);
		return m.matches();
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}
		RegExValidator that = (RegExValidator) o;
		return Objects.equals(regexStr, that.regexStr);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(regexStr);
	}

	@Override
	public String toString()
	{
		return "RegExValidator{" +
				"regexStr='" + regexStr + '\'' +
				'}';
	}
}
