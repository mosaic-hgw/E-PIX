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

import java.util.HashMap;
import java.util.Map;

import org.emau.icmvc.ganimed.deduplication.config.model.MatchingConfiguration;
import org.emau.icmvc.ganimed.deduplication.config.model.ObjectField;
import org.emau.icmvc.ganimed.deduplication.config.model.ObjectType;
import org.emau.icmvc.ganimed.deduplication.config.model.SimpleType;
import org.emau.icmvc.ganimed.epix.common.utils.ReflectionUtil;
import org.emau.icmvc.ganimed.exception.DeduplicationException;

/**
 * 
 * @author Christian Schack, geidell
 * @since 2011
 * 
 */
public final class BlockingStrategy<T> extends AbstractMatching {

	private final MatchingConfiguration config;
	private final BlockingStrategy<T> successor;
	private final T entity;
	private final Map<String, String> entityValues = new HashMap<String, String>();
	
	private final static Map<String, BlockingMatchingAlgorithm<?> > blockingAlgorithms = new HashMap<String, BlockingMatchingAlgorithm<?> >();

	public BlockingStrategy(T entity, MatchingConfiguration config) {
		this.entity = entity;
		this.config = config;
		this.successor = null;
	}

	public BlockingStrategy(T entity, MatchingConfiguration config, BlockingStrategy<T> successor) {
		this.entity = entity;
		this.config = config;
		this.successor = successor;
	}

	//protected abstract float block(String toBlock, String candidate) throws DeduplicationException;	

	public boolean block(T candidate) throws DeduplicationException {

		boolean blocked = block(candidate, config.getBlocking().getBlockingType());

		/**
		 * Call next element within the chain if not blocked
		 */
		if (!blocked && successor != null) {
			blocked = successor.block(candidate);
		}

		return blocked;
	}

	private <V> boolean block(V candidate, ObjectType objectType) throws DeduplicationException {
		boolean blocked = true;
		for (ObjectField objectField : objectType.getObjectField()) {

			if (objectField.getSimpleType() != null) {
				//Only consider comparable values
				blocked = blockSimpleType(candidate, objectField);
			}

			if (objectField.getComplexType() != null) {
				blocked = block(candidate, objectField.getComplexType());
			}

			//if (objectField.getListType() != null) {	
			//	blocked = blockListType(toBlock, candidate, objectField.getListType(), objectField.getName());
			//}

			if (blocked) {
				return true;
			}

		}

		return blocked;
	}

	//	private <V> boolean blockListType(V toBlock, V candidate, ObjectType listType, String name) {
	//		try {
	//			//FIXME currently all list entries are taken into account for the final decision
	//			List<V> list = (List<V>)ReflectionUtil.getProperty(toBlock, fieldName);						 
	//			for (V item : list) {										
	//				
	//				block(item, )
	//				preprocessComplexType(processableItem, listType);
	//			}
	//		} catch (Exception e) {
	//			throw new DeduplicationException(e.getLocalizedMessage(), e);
	//		}
	//	}

	private <V> boolean blockSimpleType(V candidate, ObjectField field) throws DeduplicationException {
		String toBlockValue = getEntityFieldValue(field.getSimpleType(), field.getName());
		String candidateValue = getFieldValue(candidate, field.getSimpleType(), field.getName());
		double threshold = field.getSimpleType().getThresholdConfig().getRejectThreshold();

		try {
			String clazz = field.getSimpleType().getAlgorithm();
			@SuppressWarnings("unchecked")
			BlockingMatchingAlgorithm<V> blocking = (BlockingMatchingAlgorithm<V>) getBlockingAlgorithm(clazz);
			return blocking.block(toBlockValue, candidateValue) < threshold;
		} catch (Exception e) {
			logger.error(e);
			throw new DeduplicationException(e);
		}
	}

	private BlockingMatchingAlgorithm<?> getBlockingAlgorithm(String clazz) throws Exception {
		BlockingMatchingAlgorithm<?> result = blockingAlgorithms.get(clazz);
		if (result == null) {
			result = (BlockingMatchingAlgorithm<?>) ReflectionUtil.newInstance(clazz);
			blockingAlgorithms.put(clazz, result);
		}
		return result;
	}

	private String getEntityFieldValue(SimpleType simpleType, String name) throws DeduplicationException {
		String result = entityValues.get(name);
		if (result == null) {
			result = getFieldValue(entity, simpleType, name);
			entityValues.put(name, result);
		}
		return result;
	}
}
