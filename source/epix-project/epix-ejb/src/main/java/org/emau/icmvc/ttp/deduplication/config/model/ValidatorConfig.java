package org.emau.icmvc.ttp.deduplication.config.model;

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

import java.util.Objects;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import org.emau.icmvc.ttp.epix.common.model.config.ValidatorConfigDTO;
import org.emau.icmvc.ttp.epix.common.model.config.ValidatorDTO;
import org.emau.icmvc.ttp.epix.common.model.config.ValidatorGroupDTO;
import org.emau.icmvc.ttp.epix.common.model.enums.FieldName;

/**
 * @author Christopher Hampf
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ValidationConfig", propOrder = { "field", "validator", "validatorGroup" })
public class ValidatorConfig
{
	@XmlElement(name = "field")
	private FieldName field;

	@XmlElement(name = "validator")
	private Validator validator;

	@XmlElement(name = "validator-group")
	private ValidatorGroup validatorGroup;

	public ValidatorConfig()
	{
	}

	public ValidatorConfig(FieldName field, ValidatorDTO validator, ValidatorGroupDTO group)
	{
		this();

		this.field = field;
		this.validator = validator == null ? null : new Validator(validator);
		this.validatorGroup = group == null ? null : new ValidatorGroup(group);
	}

	public ValidatorConfig(ValidatorConfigDTO dto)
	{
		this(dto.getField(), dto.getValidator(), dto.getValidatorGroup());
	}

	public FieldName getField()
	{
		return field;
	}

	public void setField(FieldName field)
	{
		this.field = field;
	}

	public Validator getValidator()
	{
		return validator;
	}

	public ValidatorConfigDTO toDTO()
	{
		return new ValidatorConfigDTO(field,
				(validator != null) ? validator.toDTO() : null,
				(validatorGroup != null) ? validatorGroup.toDTO() : null);
	}

	public void setValidator(Validator validator)
	{
		this.validator = validator;
	}

	public ValidatorGroup getValidatorGroup()
	{
		return validatorGroup;
	}

	public void setValidatorGroup(ValidatorGroup validatorGroup)
	{
		this.validatorGroup = validatorGroup;
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
		ValidatorConfig that = (ValidatorConfig) o;
		return field == that.field && Objects.equals(validator, that.validator) && Objects.equals(validatorGroup, that.validatorGroup);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(field, validator, validatorGroup);
	}

	@Override
	public String toString()
	{
		return "ValidatorConfig{" +
				"field=" + field +
				", validator=" + validator +
				", validatorGroup=" + validatorGroup +
				'}';
	}
}
