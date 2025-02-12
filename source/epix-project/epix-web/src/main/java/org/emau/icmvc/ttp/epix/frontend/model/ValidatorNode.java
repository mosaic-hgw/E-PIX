package org.emau.icmvc.ttp.epix.frontend.model;

/*-
 * ###license-information-start###
 * E-PIX - Enterprise Identifier Cross-Referencing
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

import java.io.Serial;
import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.emau.icmvc.ttp.epix.common.model.config.ValidatorConfigDTO;
import org.emau.icmvc.ttp.epix.common.model.config.ValidatorDTO;
import org.emau.icmvc.ttp.epix.common.model.config.ValidatorGroupDTO;

public class ValidatorNode implements Serializable, Comparable<ValidatorNode>
{
	@Serial private static final long serialVersionUID = -4284673950988656263L;
	
	private ValidatorConfigDTO validatorConfig;
	private ValidatorDTO validator;
	private ValidatorGroupDTO validatorGroup;
	private final Type type;

	public ValidatorNode()
	{
		type = Type.VALIDATOR;
	}

	public ValidatorNode(ValidatorConfigDTO validatorConfig)
	{
		this.validatorConfig = validatorConfig;
		this.type = Type.VALIDATOR_GROUP;
	}

	public ValidatorNode(ValidatorConfigDTO validatorConfig, ValidatorDTO validatorDTO)
	{
		this.validatorConfig = validatorConfig;
		this.validator = validatorDTO;
		this.type = Type.VALIDATOR;
	}

	public ValidatorNode(ValidatorDTO validatorDTO)
	{
		this.validator = validatorDTO;
		this.type = Type.VALIDATOR;
	}

	public ValidatorNode(ValidatorConfigDTO validatorConfig, ValidatorGroupDTO validatorGroupDTO)
	{
		this.validatorConfig = validatorConfig;
		this.validatorGroup = validatorGroupDTO;
		this.type = Type.VALIDATOR_GROUP;
	}

	@Override public int compareTo(ValidatorNode o)
	{
		return this.getValidatorConfig().getField().compareTo(o.getValidatorConfig().getField());
	}

	@Override public boolean equals(Object o)
	{
		if (this == o)
			return true;

		if (!(o instanceof ValidatorNode that))
			return false;

		return new EqualsBuilder().append(getValidatorConfig(), that.getValidatorConfig()).append(getValidator(), that.getValidator())
				.append(getValidatorGroup(), that.getValidatorGroup()).append(getType(), that.getType()).isEquals();
	}

	@Override public int hashCode()
	{
		return new HashCodeBuilder(17, 37).append(getValidatorConfig()).append(getValidator()).append(getValidatorGroup()).append(getType()).toHashCode();
	}

	public ValidatorConfigDTO getValidatorConfig()
	{
		return validatorConfig;
	}

	public ValidatorDTO getValidator()
	{
		return validator;
	}

	public ValidatorGroupDTO getValidatorGroup()
	{
		return validatorGroup;
	}

	public Type getType()
	{
		return type;
	}

	public enum Type {
		VALIDATOR, VALIDATOR_GROUP
	}
}
