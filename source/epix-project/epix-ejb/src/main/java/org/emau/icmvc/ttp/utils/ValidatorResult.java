package org.emau.icmvc.ttp.utils;

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

import java.util.Objects;
import java.util.Set;

import org.emau.icmvc.ttp.epix.common.model.enums.FieldName;

/**
 * @author Christopher Hampf
 */
public class ValidatorResult
{
	private final boolean valid;
	private final Set<FieldName> invalidFields;

	public ValidatorResult(boolean valid, Set<FieldName> invalidFields)
	{
		this.valid = valid;
		this.invalidFields = invalidFields;
	}

	public boolean isValid()
	{
		return valid;
	}

	public Set<FieldName> getInvalidFields()
	{
		return invalidFields;
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
		ValidatorResult that = (ValidatorResult) o;
		return valid == that.valid && Objects.equals(invalidFields, that.invalidFields);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(valid, invalidFields);
	}

	@Override
	public String toString()
	{
		return "ValidatorResult{" +
				"valid=" + valid +
				", invalidFields=" + invalidFields +
				'}';
	}
}
