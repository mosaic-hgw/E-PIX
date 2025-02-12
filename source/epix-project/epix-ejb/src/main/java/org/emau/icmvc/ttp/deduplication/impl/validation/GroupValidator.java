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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.emau.icmvc.ttp.deduplication.config.model.Validator;
import org.emau.icmvc.ttp.deduplication.config.model.ValidatorGroup;
import org.emau.icmvc.ttp.epix.common.deduplication.IValidator;
import org.emau.icmvc.ttp.epix.common.model.enums.ValidatorOperator;

/**
 * @author Christopher Hampf
 */
public class GroupValidator implements IValidator
{
	private List<IValidator> validators = new ArrayList<>();
	private ValidatorOperator operator;

	public GroupValidator()
	{
	}

	public GroupValidator(ValidatorGroup group)
	{
		if (group != null)
		{
			this.operator = group.getOperator();

			for (Validator v : group.getValidators())
			{
				validators.add(ValidatorFactory.build(v));
			}

			for (ValidatorGroup vg : group.getValidatorGroup())
			{
				validators.add(new GroupValidator(vg));
			}
		}
		else
		{
			this.operator = ValidatorOperator.ALL;
		}
	}

	@Override
	public boolean validate(String value)
	{
		int trueResults = 0;
		boolean finalResult = false;

		for (int i = 0; i < validators.size() && !finalResult; ++i)
		{
			if (validators.get(i).validate(value))
			{
				++trueResults;
			}

			finalResult = (operator == ValidatorOperator.AT_LEAST_ONE && trueResults > 0) ||
					(operator == ValidatorOperator.ALL && trueResults <= i) ||
					(operator == ValidatorOperator.EXACT_ONE && trueResults > 1);
		}

		return switch (operator)
		{
			case AT_LEAST_ONE -> trueResults > 0;
			case ALL -> trueResults == validators.size();
			case EXACT_ONE -> trueResults == 1;
			case ALL_OR_NONE -> trueResults == 0 || trueResults == validators.size();
		};
	}

	public List<IValidator> getValidators()
	{
		return validators;
	}

	public void setValidators(List<IValidator> validators)
	{
		this.validators = validators;
	}

	public ValidatorOperator getOperator()
	{
		return operator;
	}

	public void setOperator(ValidatorOperator operator)
	{
		this.operator = operator;
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
		GroupValidator that = (GroupValidator) o;
		return Objects.equals(validators, that.validators) && operator == that.operator;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(validators, operator);
	}

	@Override
	public String toString()
	{
		return "GroupValidator{" +
				"validators=" + validators +
				", operator=" + operator +
				'}';
	}
}
