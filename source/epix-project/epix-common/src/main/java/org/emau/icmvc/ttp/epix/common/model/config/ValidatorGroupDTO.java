package org.emau.icmvc.ttp.epix.common.model.config;

/*
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.emau.icmvc.ttp.epix.common.model.enums.ValidatorOperator;

/**
 * @author Christopher Hampf
 */
public class ValidatorGroupDTO implements Serializable
{
	private static final long serialVersionUID = -8977693000054278027L;

	private List<ValidatorGroupDTO> validatorGroups = new ArrayList<>();
	private List<ValidatorDTO> validators = new ArrayList<>();

	private ValidatorOperator operator;

	public ValidatorGroupDTO()
	{
	}

	public ValidatorGroupDTO(List<ValidatorGroupDTO> groups, List<ValidatorDTO> validators, ValidatorOperator operator)
	{
		this();

		setValidatorGroups(groups);
		setValidators(validators);
		setOperator(operator);
	}

	public ValidatorGroupDTO(ValidatorGroupDTO dto)
	{
		this(dto.getValidatorGroups(), dto.getValidators(), dto.getOperator());
	}

	public List<ValidatorGroupDTO> getValidatorGroups()
	{
		return validatorGroups;
	}

	public void setValidatorGroups(List<ValidatorGroupDTO> validatorGroups)
	{
		this.validatorGroups = validatorGroups;
	}

	public List<ValidatorDTO> getValidators()
	{
		return validators;
	}

	public void setValidators(List<ValidatorDTO> validators)
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
		ValidatorGroupDTO that = (ValidatorGroupDTO) o;
		return Objects.equals(validatorGroups, that.validatorGroups) && Objects.equals(validators, that.validators) && operator == that.operator;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(validatorGroups, validators, operator);
	}

	@Override
	public String toString()
	{
		return "ValidatorGroupDTO{" +
				"validatorGroups=" + validatorGroups +
				", validators=" + validators +
				", operator=" + operator +
				'}';
	}
}
