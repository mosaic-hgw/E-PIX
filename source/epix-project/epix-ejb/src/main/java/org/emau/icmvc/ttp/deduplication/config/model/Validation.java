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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import org.emau.icmvc.ttp.epix.common.model.config.ValidationDTO;
import org.emau.icmvc.ttp.epix.common.model.config.ValidatorConfigDTO;

/**
 * @author Christopher Hampf
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Validation", propOrder = { "validatorConfigs" })
public class Validation
{
	@XmlElement(name = "validator-config")
	private List<ValidatorConfig> validatorConfigs = new ArrayList<>();

	public Validation()
	{
	}

	public Validation(Validation validation)
	{
		this();

		setValidatorConfigs(validation.getValidatorConfigs());
	}

	public Validation(ValidationDTO dto)
	{
		this();

		if (dto != null && dto.getValidationConfigs() != null)
		{
			for (ValidatorConfigDTO vcdto : dto.getValidationConfigs())
			{
				validatorConfigs.add(new ValidatorConfig(vcdto));
			}
		}
	}

	public ValidationDTO toDTO()
	{
		ValidationDTO dto = new ValidationDTO();

		List<ValidatorConfigDTO> configDTOs = new ArrayList<>();

		for (ValidatorConfig conf : validatorConfigs)
		{
			configDTOs.add(conf.toDTO());
		}

		dto.setValidationConfigs(configDTOs);

		return dto;
	}

	public void setValidatorConfigs(List<ValidatorConfig> validatorConfigs)
	{
		this.validatorConfigs = validatorConfigs;
	}

	public List<ValidatorConfig> getValidatorConfigs()
	{
		return validatorConfigs;
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
		Validation that = (Validation) o;
		return Objects.equals(validatorConfigs, that.validatorConfigs);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(validatorConfigs);
	}

	@Override
	public String toString()
	{
		return "Validation{" +
				"validatorConfigs=" + validatorConfigs +
				'}';
	}
}
