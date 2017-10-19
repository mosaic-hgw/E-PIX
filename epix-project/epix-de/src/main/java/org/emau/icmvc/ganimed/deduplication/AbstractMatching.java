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

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.emau.icmvc.ganimed.deduplication.config.model.SimpleType;
import org.emau.icmvc.ganimed.epix.common.utils.ReflectionUtil;
import org.emau.icmvc.ganimed.exception.DeduplicationException;

/**
 * 
 * @author Christian Schack, Dirk Langner
 * @since 2011
 * 
 */
public abstract class AbstractMatching {

	protected Logger logger = Logger.getLogger(AbstractMatching.class);
	private static final Map<String, SimpleDateFormat> dateFormats = new HashMap<String, SimpleDateFormat>();

	protected <V> String getFieldValue(V object, SimpleType fieldType, String fieldName) throws DeduplicationException {
		try {
			String propertyType = fieldType.getPropertyType();

			Object fieldValue = ReflectionUtil.getProperty(object, fieldName);

			if (fieldValue != null) {
				if (propertyType != null && propertyType.equals(String.class.getName())) {
					return (String) fieldValue;
				} else if (propertyType != null && propertyType.equals(Date.class.getName())) {
					String regexPattern = fieldType.getRegexPattern();
					return formatDate((Date) fieldValue, regexPattern);
				} else {
					logger.warn("No matchable property type for field " + fieldName);
					return "";
				}
			} else {
				if(logger.isDebugEnabled()) {
					logger.debug("Value for field type null: " + fieldName);
				}
				return "";
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			throw new DeduplicationException(e.getLocalizedMessage(), e);
		}
	}

	private String formatDate(Date date, String regexPattern) {
		SimpleDateFormat df = dateFormats.get(regexPattern);
		if (df == null) {
			df = new SimpleDateFormat(regexPattern != null ? regexPattern : "yyyy-MM-dd");
			dateFormats.put(regexPattern, df);
		}
		String result;
		synchronized (df) {
			result = df.format(date);
		}
		return result;
	}
}
