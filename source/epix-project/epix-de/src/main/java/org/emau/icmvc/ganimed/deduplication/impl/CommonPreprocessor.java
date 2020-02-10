package org.emau.icmvc.ganimed.deduplication.impl;

/*
 * ###license-information-start###
 * E-PIX - Enterprise Patient Identifier Cross-referencing
 * __
 * Copyright (C) 2009 - 2017 The MOSAIC Project - Institut fuer Community Medicine der
 * 							Universitaetsmedizin Greifswald - mosaic-projekt@uni-greifswald.de
 * 							concept and implementation
 * 							c. schack, d.langner, l. geidel
 * 							web client
 * 							a. blumentritt
 * 							g. weiher
 * 							please cite our publications
 * 							http://dx.doi.org/10.3414/ME14-01-0133
 * 							http://dx.doi.org/10.1186/s12967-015-0545-6
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


import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.emau.icmvc.ganimed.deduplication.PreprocessingStrategy;
import org.emau.icmvc.ganimed.deduplication.config.model.ComplexTransformation;
import org.emau.icmvc.ganimed.deduplication.config.model.PreprocessingField;
import org.emau.icmvc.ganimed.deduplication.config.model.PreprocessingType;
import org.emau.icmvc.ganimed.deduplication.config.model.SimpleTransformation;
import org.emau.icmvc.ganimed.deduplication.config.model.Transformation;
import org.emau.icmvc.ganimed.deduplication.preprocessing.impl.IComplexTransformation;
import org.emau.icmvc.ganimed.epix.common.utils.ReflectionUtil;
import org.emau.icmvc.ganimed.exception.DeduplicationException;

/**
 * 
 * @author Christian Schack
 * @since 23.09.2011
 *
 */

public class CommonPreprocessor<T> extends PreprocessingStrategy<T>{
	
	@Override
	public T preprocess(T preprocessable) throws DeduplicationException {
		if (preprocessingConfig != null) {
			List<PreprocessingType> preprocessingTypes = preprocessingConfig.getPreprocessingTypes();
			for (PreprocessingType preprocessingType : preprocessingTypes) {
				preprocessComplexType(preprocessable, preprocessingType);
			}									
		}		
		return preprocessable;
	}
	
	private <V> void preprocessComplexType(V preprocessable, PreprocessingType preprocessingTypes) throws DeduplicationException {
		List<PreprocessingField> preprocessingFields = preprocessingTypes.getPreprocessingField();
		
		for (PreprocessingField preprocessingField : preprocessingFields) {
			
			if (preprocessingField.getSimpleTransformationType() != null) {
				preprocessSimpleType(preprocessable, preprocessingField);
			}
			
			if (preprocessingField.getComplexTransformationType() != null) {
				preprocessComplexType(preprocessable, preprocessingField.getComplexTransformationType());
			}
			
			if (preprocessingField.getListTransformationType() != null) {
				preprocessListType(preprocessable, preprocessingField.getListTransformationType(), preprocessingField.getFieldName());
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private <V> void preprocessListType(V preprocessable, PreprocessingType preprocessingType, String fieldname) throws DeduplicationException {
		try {
			List<V> processableList = (List<V>)ReflectionUtil.getProperty(preprocessable, fieldname);
			for (V preprocessableItem : processableList) {
				preprocessComplexType(preprocessableItem, preprocessingType);
			}
		} catch (Exception e) {
			throw new DeduplicationException(e.getLocalizedMessage(), e);
		}
	}
	
	private <V> void preprocessSimpleType(V processable, PreprocessingField preprocessingField) throws DeduplicationException {
		if(preprocessingField != null) {
			String propertyName = preprocessingField.getFieldName();
			for (Transformation transformation : preprocessingField.getSimpleTransformationType()) {
				transform(processable, transformation, propertyName);
			}
		}
	}

	protected <V> void transform(V processable, Transformation transformation, String propertyName) throws DeduplicationException {		
		String newValue = null;
		if (transformation!=null && transformation instanceof SimpleTransformation){
			newValue = performSimpleTransformation(processable, (SimpleTransformation)transformation, propertyName);
		}else if (transformation!=null && transformation instanceof ComplexTransformation){
			newValue = performComplexTransformation(processable, (ComplexTransformation)transformation, propertyName);
		}		
		try {
			ReflectionUtil.setProperty(processable, propertyName, newValue, String.class);
		} catch (Exception e) {
			throw new DeduplicationException(e.getLocalizedMessage(), e);
		}
	}
	
	/**
	 * 
	 * @param processable
	 * @param transformationConfig
	 * @param propertyName
	 * @return
	 * @throws DeduplicationException
	 */
	protected <V> String performSimpleTransformation(V processable, SimpleTransformation transformationConfig, String propertyName) throws DeduplicationException {
		try { 
			String inputPattern = transformationConfig.getInputPattern();
			String outputPattern = transformationConfig.getOutputPattern();
			
			String propertyValue = (String)ReflectionUtil.getProperty(processable, propertyName);
			String newValue = propertyValue;
	
			if (propertyValue != null) {
				if (outputPattern != null) {
					// standard string replacement 
					newValue = StringUtils.replace(propertyValue, inputPattern, outputPattern);													
				} 		
			}
			
			return newValue;
 		} catch (Exception e) {
			throw new DeduplicationException(e.getLocalizedMessage(), e);
		}
	}
	
	protected <V> String performComplexTransformation(V processable, ComplexTransformation transformationConfig, String propertyName) throws DeduplicationException {
		try { 
			String propertyValue = (String)ReflectionUtil.getProperty(processable, propertyName);
			String newValue = null;
			if (propertyValue != null) {		
				if (transformationConfig == null) {
					throw new IllegalStateException("Invalid matching configuration !");
				}
				//TODO cache
				IComplexTransformation transformation = (IComplexTransformation) ReflectionUtil.newInstance(transformationConfig.getQualifiedClassName());
				newValue = transformation.transform(propertyName, propertyValue);
								
			}else{
				logger.warn(String.format("Property %s is empty!", propertyName));
			}
			return newValue;
 		} catch (Exception e) {
			throw new DeduplicationException(e.getLocalizedMessage(), e);
		}
	}
	
}
