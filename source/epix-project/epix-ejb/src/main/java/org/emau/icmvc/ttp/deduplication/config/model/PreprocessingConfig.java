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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;
import org.emau.icmvc.ttp.epix.common.model.config.PreprocessingFieldDTO;

/**
 *
 * @author geidell
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PreprocessingConfig", propOrder = { "preprocessingFields" })
public class PreprocessingConfig
{
	@XmlElement(name = "preprocessing-field", required = true)
	private final List<PreprocessingField> preprocessingFields = new ArrayList<>();

	public PreprocessingConfig()
	{}

	public PreprocessingConfig(List<PreprocessingFieldDTO> preprocessingFieldDTOs)
	{
		for (PreprocessingFieldDTO fieldDTO : preprocessingFieldDTOs)
		{
			preprocessingFields.add(new PreprocessingField(fieldDTO));
		}
	}

	public List<PreprocessingField> getPreprocessingFields()
	{
		return preprocessingFields;
	}

	public void setPreprocessingFields(List<PreprocessingField> preprocessingFields)
	{
		this.preprocessingFields.clear();
		this.preprocessingFields.addAll(preprocessingFields);
	}

	public List<PreprocessingFieldDTO> toDTO()
	{
		List<PreprocessingFieldDTO> result = new ArrayList<>();
		for (PreprocessingField field : preprocessingFields)
		{
			Map<String, String> simpleTransformationTypes = new HashMap<>();
			List<String> complexTransformationClasses = new ArrayList<>();
			Map<String, Character> simpleFilterTypes = new HashMap<>();
			if (field.getSimpleTransformationTypes() != null && !field.getSimpleTransformationTypes().isEmpty())
			{
				for (SimpleTransformation sTrans : field.getSimpleTransformationTypes())
				{
					simpleTransformationTypes.put(sTrans.getInputPattern(), sTrans.getOutputPattern());
				}
			}
			if (field.getComplexTransformationTypes() != null && !field.getComplexTransformationTypes().isEmpty())
			{
				for (ComplexTransformation cTrans : field.getComplexTransformationTypes())
				{
					complexTransformationClasses.add(cTrans.getQualifiedClassName());
				}
			}
			if (field.getSimpleFilterTypes() != null && !field.getSimpleFilterTypes().isEmpty())
			{
				for (SimpleFilter sFilter : field.getSimpleFilterTypes())
				{
					simpleFilterTypes.put(sFilter.getPassAlphabet(), StringUtils.isEmpty(sFilter.getReplaceCharacter()) ? null : sFilter.getReplaceCharacter().charAt(0));
				}
			}
			result.add(new PreprocessingFieldDTO(field.getFieldName(), simpleTransformationTypes, complexTransformationClasses, simpleFilterTypes));
		}
		return result;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (preprocessingFields == null ? 0 : preprocessingFields.hashCode());
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
		PreprocessingConfig other = (PreprocessingConfig) obj;
		if (preprocessingFields == null)
		{
			if (other.preprocessingFields != null)
			{
				return false;
			}
		}
		else if (!preprocessingFields.equals(other.preprocessingFields))
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		return "PreprocessingConfig [preprocessingFields=" + preprocessingFields + "]";
	}
}
