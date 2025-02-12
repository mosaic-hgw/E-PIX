package org.emau.icmvc.ttp.epix.common.exception;

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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.emau.icmvc.ttp.epix.common.model.enums.FieldName;

public class ValidatorException extends Exception
{
	private static final long serialVersionUID = 8119131712904091658L;

	private final ArrayList<String> invalidFields = new ArrayList<>();

	public ValidatorException()
	{
		super();
	}

	public ValidatorException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public ValidatorException(String message)
	{
		super(message);
	}

	public ValidatorException(Throwable cause)
	{
		super(cause);
	}

	public ValidatorException(String message, Set<FieldName> invalidFields)
	{
		super(message);

		Set<String> tmp = new HashSet<>();
		for (FieldName fn : invalidFields)
		{
			tmp.add(fn.name());
		}

		this.invalidFields.addAll(tmp);
	}

	public List<String> getInvalidFields()
	{
		return invalidFields;
	}

	@Override public String getLocalizedMessage()
	{
		return super.getLocalizedMessage() + invalidFields;
	}
}
