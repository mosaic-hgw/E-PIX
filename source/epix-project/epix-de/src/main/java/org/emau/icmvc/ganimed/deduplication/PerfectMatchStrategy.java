package org.emau.icmvc.ganimed.deduplication;

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

import java.util.ArrayList;
import java.util.List;

import org.emau.icmvc.ganimed.deduplication.config.model.MatchingConfiguration;
import org.emau.icmvc.ganimed.deduplication.config.model.ObjectField;
import org.emau.icmvc.ganimed.deduplication.config.model.ObjectType;
import org.emau.icmvc.ganimed.deduplication.model.PerfectMatchTransferObject;
import org.emau.icmvc.ganimed.exception.DeduplicationException;

public abstract class PerfectMatchStrategy<T> extends AbstractMatching {
	
	protected MatchingConfiguration matchingConfiguration = null;
	
	protected PerfectMatchProcessor<T> perfectMatchProzessor = null;
	
	protected PerfectMatchTransferObject transferObject = null;
	
	//Set to false if perfectMatch should not be tested -> in future this can be configured in matchingConfiguration
	protected boolean active = true;
	
	public abstract T perfectMatch (T candidate) throws DeduplicationException;
	
	public abstract T perfectMatch (T candidate, List<T> candidateList) throws DeduplicationException;
	
	public void setMatchingConfiguration (MatchingConfiguration  matchingConfiguration) {
		this.matchingConfiguration = matchingConfiguration;
		if(transferObject == null) { 
			transferObject = new PerfectMatchTransferObject();
		}
		if(this.matchingConfiguration != null) {
			ObjectType objectType = this.matchingConfiguration.getObjectOfSingleTruth().getObjectType();
			fillTransferObject(objectType, transferObject);
		} else {
			logger.info("No perfectMatch-config available.");
		}
	}
	
	public void setPerfectMatchProzessor (PerfectMatchProcessor<T> perfectMatchProzessor) {
		this.perfectMatchProzessor = perfectMatchProzessor;
	}
	
	private void fillTransferObject(ObjectType objectType, PerfectMatchTransferObject transferObject) {
		transferObject.setName(objectType.getQualifiedClassName());
		List<ObjectField> objectFields = objectType.getObjectField();
		
		for(ObjectField objectField : objectFields) {
			if(objectField.getSimpleType() != null) {
				fillSimpleTransferObject(objectField, transferObject);
			}
			
			if(objectField.getComplexType() != null) {
				fillTransferObject(objectField.getComplexType(), transferObject);
			}
			 
			if(objectField.getListType() != null) {
				fillTransferObjectListType(objectField.getName(), objectField.getListType(), transferObject);
			}
		}
	}
	
	private void fillTransferObjectListType(String name, ObjectType listType, PerfectMatchTransferObject transferObject) {
		List<PerfectMatchTransferObject> listTransferObject = new ArrayList<PerfectMatchTransferObject>();
		PerfectMatchTransferObject itemTransferObject = new PerfectMatchTransferObject();
		itemTransferObject.setName(listType.getQualifiedClassName());
		fillTransferObject(listType, itemTransferObject);
		listTransferObject.add(itemTransferObject);
		
		transferObject.getValueMap().put(name, listTransferObject);

		
	}

	private void fillSimpleTransferObject(ObjectField objectField, PerfectMatchTransferObject transferObject) {
		transferObject.getValueMap().put(objectField.getName(), null);
	}

}
