package org.emau.icmvc.ttp.deduplication.config.model;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.emau.icmvc.ttp.epix.common.model.config.PreprocessingFieldDTO;
import org.emau.icmvc.ttp.epix.common.model.enums.FieldName;

/**
 *
 * @author geidell
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PreprocessingField", propOrder = { "fieldName", "simpleTransformationTypes", "complexTransformationTypes", "simpleFilterTypes" })
public class PreprocessingField
{
	@XmlElement(name = "field-name", required = true)
	private FieldName fieldName;
	@XmlElement(name = "simple-transformation-type", required = false)
	private final List<SimpleTransformation> simpleTransformationTypes = new ArrayList<>();
	@XmlElement(name = "complex-transformation-type", required = false)
	private final List<ComplexTransformation> complexTransformationTypes = new ArrayList<>();
	@XmlElement(name = "simple-filter-type", required = false)
	private final List<SimpleFilter> simpleFilterTypes = new ArrayList<>();

	public PreprocessingField()
	{}

	public PreprocessingField(PreprocessingFieldDTO fieldDTO)
	{
		fieldName = fieldDTO.getFieldName();
		setSimpleTransformationTypesFromDTO(fieldDTO.getSimpleTransformationTypes());
		setComplexTransformationTypeFromDTO(fieldDTO.getComplexTransformationClasses());
		setSimpleFilterTypesFromDTO(fieldDTO.getSimpleFilterTypes());
	}

	public FieldName getFieldName()
	{
		return fieldName;
	}

	public void setFieldName(FieldName fieldName)
	{
		this.fieldName = fieldName;
	}

	public List<SimpleTransformation> getSimpleTransformationTypes()
	{
		return simpleTransformationTypes;
	}

	public void setSimpleTransformationTypes(List<SimpleTransformation> simpleTransformationTypes)
	{
		this.simpleTransformationTypes.clear();
		this.simpleTransformationTypes.addAll(simpleTransformationTypes);
	}

	private void setSimpleTransformationTypesFromDTO(Map<String, String> simpleTransformationTypes)
	{
		this.simpleTransformationTypes.clear();
		for (Entry<String, String> entry : simpleTransformationTypes.entrySet())
		{
			this.simpleTransformationTypes.add(new SimpleTransformation(entry.getKey(), entry.getValue()));
		}
	}

	public List<ComplexTransformation> getComplexTransformationTypes()
	{
		return complexTransformationTypes;
	}

	public void setComplexTransformationType(List<ComplexTransformation> complexTransformationTypes)
	{
		this.complexTransformationTypes.clear();
		this.complexTransformationTypes.addAll(complexTransformationTypes);
	}

	private void setComplexTransformationTypeFromDTO(List<String> complexTransformationClasses)
	{
		this.complexTransformationTypes.clear();
		for (String entry : complexTransformationClasses)
		{
			this.complexTransformationTypes.add(new ComplexTransformation(entry));
		}
	}

	public List<SimpleFilter> getSimpleFilterTypes()
	{
		return simpleFilterTypes;
	}

	public void setSimpleFilterTypes(List<SimpleFilter> simpleFilterTypes)
	{
		this.simpleFilterTypes.clear();
		this.simpleFilterTypes.addAll(simpleFilterTypes);
	}

	private void setSimpleFilterTypesFromDTO(Map<String, Character> simpleFilterTypes)
	{
		this.simpleFilterTypes.clear();
		for (Entry<String, Character> entry : simpleFilterTypes.entrySet())
		{
			this.simpleFilterTypes.add(new SimpleFilter(entry.getKey(), entry.getValue()));
		}
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (complexTransformationTypes == null ? 0 : complexTransformationTypes.hashCode());
		result = prime * result + (fieldName == null ? 0 : fieldName.hashCode());
		result = prime * result + (simpleTransformationTypes == null ? 0 : simpleTransformationTypes.hashCode());
		result = prime * result + simpleFilterTypes.hashCode();
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
		PreprocessingField other = (PreprocessingField) obj;
		if (complexTransformationTypes == null)
		{
			if (other.complexTransformationTypes != null)
			{
				return false;
			}
		}
		else if (!complexTransformationTypes.equals(other.complexTransformationTypes))
		{
			return false;
		}
		if (fieldName == null)
		{
			if (other.fieldName != null)
			{
				return false;
			}
		}
		else if (!fieldName.equals(other.fieldName))
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
		return simpleFilterTypes.equals(other.simpleFilterTypes);
	}

	@Override
	public String toString()
	{
		return "PreprocessingField [fieldName=" + fieldName + ", simpleTransformationTypes=" + simpleTransformationTypes
				+ ", complexTransformationTypes=" + complexTransformationTypes + ", simpleFilterTypes=" + simpleFilterTypes + "]";
	}
}
