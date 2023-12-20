package org.emau.icmvc.ttp.epix.common.model.config;

/*-
 * ###license-information-start###
 * E-PIX - Enterprise Patient Identifier Cross-referencing
 * __
 * Copyright (C) 2009 - 2023 Trusted Third Party of the University Medicine Greifswald
 * 							kontakt-ths@uni-greifswald.de
 * 
 * 							concept and implementation
 * 							l.geidel,c.schack, d.langner, g.koetzschke
 * 
 * 							web client
 * 							a.blumentritt, f.m. moser
 * 
 * 							docker
 * 							r.schuldt
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.emau.icmvc.ttp.epix.common.model.enums.FieldName;

public class PreprocessingFieldDTO implements Serializable
{
	private static final long serialVersionUID = 1042094477162647131L;
	private FieldName fieldName;
	private final Map<String, String> simpleTransformationTypes = new HashMap<>();
	private final List<String> complexTransformationClasses = new ArrayList<>();
	private final Map<String, Character> simpleFilterTypes = new HashMap<>();

	public PreprocessingFieldDTO()
	{}

	public PreprocessingFieldDTO(FieldName fieldName, Map<String, String> simpleTransformationTypes, List<String> complexTransformationClasses, Map<String, Character> simpleFilterTypes)
	{
		super();
		this.fieldName = fieldName;
		setSimpleTransformationTypes(simpleTransformationTypes);
		setComplexTransformationClasses(complexTransformationClasses);
		setSimpleFilterTypes(simpleFilterTypes);
	}

	public PreprocessingFieldDTO(PreprocessingFieldDTO dto)
	{
		this(dto.getFieldName(), dto.getSimpleTransformationTypes(), dto.getComplexTransformationClasses(), dto.getSimpleFilterTypes());
	}

	public FieldName getFieldName()
	{
		return fieldName;
	}

	public void setFieldName(FieldName fieldName)
	{
		this.fieldName = fieldName;
	}

	public Map<String, String> getSimpleTransformationTypes()
	{
		return simpleTransformationTypes;
	}

	public void setSimpleTransformationTypes(Map<String, String> simpleTransformationTypes)
	{
		this.simpleTransformationTypes.clear();
		if (simpleTransformationTypes != null)
		{
			this.simpleTransformationTypes.putAll(simpleTransformationTypes);
		}
	}

	public List<String> getComplexTransformationClasses()
	{
		return complexTransformationClasses;
	}

	public void setComplexTransformationClasses(List<String> complexTransformationClasses)
	{
		this.complexTransformationClasses.clear();
		if (complexTransformationClasses != null)
		{
			this.complexTransformationClasses.addAll(complexTransformationClasses);
		}
	}

	public Map<String, Character> getSimpleFilterTypes()
	{
		return simpleFilterTypes;
	}

	public void setSimpleFilterTypes(Map<String, Character> simpleFilterTypes)
	{
		this.simpleFilterTypes.clear();
		if (simpleFilterTypes != null)
		{
			this.simpleFilterTypes.putAll(simpleFilterTypes);
		}
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (complexTransformationClasses == null ? 0 : complexTransformationClasses.hashCode());
		result = prime * result + (fieldName == null ? 0 : fieldName.hashCode());
		result = prime * result + (simpleFilterTypes == null ? 0 : simpleFilterTypes.hashCode());
		result = prime * result + (simpleTransformationTypes == null ? 0 : simpleTransformationTypes.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		PreprocessingFieldDTO other = (PreprocessingFieldDTO) obj;
		if (complexTransformationClasses == null)
		{
			if (other.complexTransformationClasses != null)
			{
				return false;
			}
		}
		else if (!complexTransformationClasses.equals(other.complexTransformationClasses))
		{
			return false;
		}
		if (fieldName != other.fieldName)
		{
			return false;
		}
		if (simpleFilterTypes == null)
		{
			if (other.simpleFilterTypes != null)
			{
				return false;
			}
		}
		else if (!simpleFilterTypes.equals(other.simpleFilterTypes))
		{
			return false;
		}
		if (simpleTransformationTypes == null)
		{
			if (other.simpleTransformationTypes != null)
			{
				return false;
			}
		}
		else if (!simpleTransformationTypes.equals(other.simpleTransformationTypes))
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		return "PreprocessingFieldDTO [fieldName=" + fieldName + ", simpleTransformationTypes=" + simpleTransformationTypes + ", complexTransformationClasses=" + complexTransformationClasses
				+ ", simpleFilterTypes=" + simpleFilterTypes + "]";
	}
}
