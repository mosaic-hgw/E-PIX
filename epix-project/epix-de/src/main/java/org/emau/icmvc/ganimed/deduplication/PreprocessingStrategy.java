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

import org.apache.log4j.Logger;
import org.emau.icmvc.ganimed.deduplication.config.model.MatchingConfiguration;
import org.emau.icmvc.ganimed.deduplication.config.model.PreprocessingConfig;
import org.emau.icmvc.ganimed.epix.common.utils.ReflectionUtil;
import org.emau.icmvc.ganimed.exception.DeduplicationException;

/**
 * 
 * @author Christian Schack
 * @since 23.09.2011
 * 
 * @param <T>
 */
public abstract class PreprocessingStrategy<T> {

	protected MatchingConfiguration matchingConfiguration = null;

	protected PreprocessingConfig preprocessingConfig = null;

	protected Map<String, Object> transformationCache = new HashMap<String, Object>();

	protected Logger logger = Logger.getLogger(PreprocessingStrategy.class);

	public PreprocessingStrategy() {
	}

	public abstract T preprocess(T toProcess) throws DeduplicationException;

	public MatchingConfiguration getMatchingConfiguration() {
		return matchingConfiguration;
	}

	/**
	 * 
	 * @param matchingConfiguration
	 */
	public void setMatchingConfiguration(MatchingConfiguration matchingConfiguration) {
		this.matchingConfiguration = matchingConfiguration;
		preprocessingConfig = matchingConfiguration.getPreprocessingConfig();
		if (preprocessingConfig == null) {
			logger.info("No preprocessing config available.");
		}
	}

	protected String getFieldKey(String qualifiedClassName, String fieldName) {
		return qualifiedClassName.trim().toLowerCase() + "." + fieldName.trim().toLowerCase();
	}

	protected Object getTransformationType(String qualifiedClassName) throws Exception {
		Object transformationType = transformationCache.get(qualifiedClassName);
		if (transformationType == null) {
			transformationType = ReflectionUtil.newInstance(qualifiedClassName);
			transformationCache.put(qualifiedClassName, transformationType);
		}
		return transformationType;
	}

}
