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
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.emau.icmvc.ganimed.deduplication.config.model.MatchingConfiguration;
import org.emau.icmvc.ganimed.deduplication.config.model.RequiredConfig;
import org.emau.icmvc.ganimed.deduplication.config.model.RequiredField;
import org.emau.icmvc.ganimed.deduplication.config.model.RequiredType;
import org.emau.icmvc.ganimed.exception.DeduplicationException;

public abstract class ValidateRequiredStrategy<T> {

	protected MatchingConfiguration matchingConfiguration = null;

	protected RequiredConfig requiredConfig = null;

	protected Map<String, RequiredField> fieldCache = new HashMap<String, RequiredField>();

	protected Logger logger = Logger.getLogger(ValidateRequiredStrategy.class);

	public abstract boolean isValidate(T preprocessable) throws DeduplicationException;

	public MatchingConfiguration getMatchingConfiguration() {
		return matchingConfiguration;
	}

	public void setMatchingConfiguration(MatchingConfiguration matchingConfiguration) {
		this.matchingConfiguration = matchingConfiguration;
		requiredConfig = matchingConfiguration.getRequiredConfig();
		if (requiredConfig != null) {
			for (RequiredType type : requiredConfig.getRequiredTypes()) {
				List<RequiredField> fields = type.getRequiredFields();
				for (RequiredField field : fields) {
					String key = getFieldKey(type.getQualifiedClassName(), field.getFieldName());
					RequiredField current = fieldCache.get(key);
					if (current == null) {
						fieldCache.put(key, field);
					}
				}
			}
		} else {
			logger.info("No required-config available.");
		}
	}

	protected String getFieldKey(String qualifiedClassName, String fieldName) {
		return qualifiedClassName.trim().toLowerCase() + "." + fieldName.trim().toLowerCase();
	}

}
