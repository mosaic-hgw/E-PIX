package org.emau.icmvc.ttp.deduplication.config.model;

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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import org.emau.icmvc.ttp.epix.common.model.config.ValidatorDTO;
import org.emau.icmvc.ttp.epix.common.model.config.ValidatorGroupDTO;
import org.emau.icmvc.ttp.epix.common.model.enums.ValidatorOperator;

/**
 * @author Christopher Hampf
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ValidationGroup", propOrder = { "validatorGroups", "validators", "operator" })
public class ValidatorGroup
{
	@XmlElement(name = "validator-group")
	private List<ValidatorGroup> validatorGroups = new ArrayList<>();

	@XmlElement(name = "validator")
	private List<Validator> validators = new ArrayList<>();

	@XmlElement(name = "link")
	private ValidatorOperator operator;

	public ValidatorGroup()
	{
	}

	public ValidatorGroup(ValidatorGroup validatorGroup)
	{
		this();

		setValidatorGroups(validatorGroup.getValidatorGroup());
		setValidators(validatorGroup.getValidators());
		setOperator(validatorGroup.getOperator());
	}

	public ValidatorGroup(List<ValidatorGroupDTO> validatorGroup, List<ValidatorDTO> validators, ValidatorOperator operator)
	{
		this();

		List<ValidatorGroup> validatorGroupList = new ArrayList<>();
		List<Validator> validatorList = new ArrayList<>();

		if (validatorGroup != null)
		{
			for (ValidatorGroupDTO vgdto : validatorGroup)
			{
				validatorGroupList.add(new ValidatorGroup(vgdto));
			}
		}

		if (validators != null)
		{
			for (ValidatorDTO vdto : validators)
			{
				validatorList.add(new Validator(vdto));
			}
		}

		setValidatorGroups(validatorGroupList);
		setValidators(validatorList);
		setOperator(operator);
	}

	public ValidatorGroup(ValidatorGroupDTO dto)
	{
		this(dto.getValidatorGroups(), dto.getValidators(), dto.getOperator());
	}

	public List<ValidatorGroup> getValidatorGroup()
	{
		return validatorGroups;
	}

	public void setValidatorGroups(List<ValidatorGroup> validatorGroup)
	{
		this.validatorGroups = validatorGroup;
	}

	public List<Validator> getValidators()
	{
		return validators;
	}

	public void setValidators(List<Validator> validators)
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

	public ValidatorGroupDTO toDTO()
	{
		List<ValidatorDTO> tmpValidators = new ArrayList<>();
		List<ValidatorGroupDTO> tmpGroups = new ArrayList<>();

		if (validators != null)
		{
			for (Validator v : validators)
			{
				tmpValidators.add(v.toDTO());
			}
		}

		if (validatorGroups != null)
		{
			for (ValidatorGroup vg : validatorGroups)
			{
				tmpGroups.add(vg.toDTO());
			}
		}

		return new ValidatorGroupDTO(tmpGroups, tmpValidators, operator);
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
		ValidatorGroup that = (ValidatorGroup) o;
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
		return "ValidatorGroup{" +
				"validatorGroups=" + validatorGroups +
				", validators=" + validators +
				", operator=" + operator +
				'}';
	}
}
