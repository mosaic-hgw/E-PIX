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

import org.emau.icmvc.ganimed.deduplication.ValidateRequiredStrategy;
import org.emau.icmvc.ganimed.deduplication.config.model.ObjectField;
import org.emau.icmvc.ganimed.deduplication.config.model.ObjectOfSingleTruth;
import org.emau.icmvc.ganimed.deduplication.config.model.ObjectType;
import org.emau.icmvc.ganimed.deduplication.config.model.RequiredField;
import org.emau.icmvc.ganimed.epix.common.utils.ReflectionUtil;
import org.emau.icmvc.ganimed.exception.DeduplicationException;

public class CommonValidateRequiredStrategy<T> extends ValidateRequiredStrategy<T> {
	
	@Override
	public boolean isValidate(T obj) throws DeduplicationException {
		if (requiredConfig != null) {														
			ObjectOfSingleTruth oost = matchingConfiguration.getObjectOfSingleTruth();
			return processType(obj, oost.getObjectType(), true);										
		}
		return false;
	}
	
	private <V> boolean processType(V processable, ObjectType type, boolean bool) throws DeduplicationException {	
		List<ObjectField> fields = type.getObjectField();
		
		for (ObjectField objectField : fields) {
			if (objectField.getSimpleType() != null ) {					
				bool = processSimpleType(processable, objectField);				
			}			
			if (objectField.getComplexType() != null ) { 
				processType(processable, objectField.getComplexType(), bool);
			}
			if (objectField.getListType() != null) {	
				if (!isEmptyListValidate(processable, objectField.getName(), objectField.getListType().getQualifiedClassName())){
					bool= false;
				}else{
					bool = processListType(processable, objectField.getListType(), objectField.getName(), bool);
				}
			}	
			if (!bool) return false;
		}			
		return bool;
	}
	
	private <V> boolean processSimpleType(V processable, ObjectField property) throws DeduplicationException {	
		try {
			String key = getFieldKey(processable.getClass().getName(), property.getName());		
					RequiredField field = fieldCache.get(key);
			if (field != null) {
				String propertyName = field.getFieldName();		
				return isValueAvailable(processable, propertyName);
			}
		} catch (Exception e) {
			throw new DeduplicationException(e.getLocalizedMessage(), e);
		}	
		return true;
	}
	
	protected <V> boolean isValueAvailable(V processable, String propertyName) throws DeduplicationException {		
		try {
			Object object = ReflectionUtil.getProperty(processable, propertyName);
			if(object==null || (object!=null && object instanceof String && ((String)object).isEmpty())){
				return false;
			}
		} catch (Exception e) {
			throw new DeduplicationException(e.getLocalizedMessage(), e);
		}
		return true;
	}

	@SuppressWarnings("unchecked")	
	private <V> boolean processListType(V processable, ObjectType listType, String fieldName, boolean bool) throws DeduplicationException  {
		try {
			//FIXME currently all list entries are taken into account for the final decision
			List<V> processableList = (List<V>)ReflectionUtil.getProperty(processable, fieldName);			
			
			for (V processableItem : processableList) {										
				bool = processType(processableItem, listType, bool);
			}
		} catch (Exception e) {
			throw new DeduplicationException(e.getLocalizedMessage(), e);
		}
		return bool;
	}
	
	private <V> boolean isEmptyListValidate(V processable, String fieldName, String qualifedclassname) throws DeduplicationException{
		try{
			@SuppressWarnings("unchecked")
			List<V> processableList = (List<V>)ReflectionUtil.getProperty(processable, fieldName);					
			if (processableList.isEmpty()){
				String canonicalName = ReflectionUtil.getCanonicalNameOfListElement(processable, fieldName);
				if (canonicalName!=null && canonicalName.equals(qualifedclassname)){
					return false;
				}
			}
			return true;
		}catch (Exception e) {
			throw new DeduplicationException(e.getLocalizedMessage(), e);
		}
	}

	
	

}
