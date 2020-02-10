package org.emau.icmvc.ganimed.epix.common.model;

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


import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author weiherg
 *
 */
public class ConfigurationContainer implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5341484440705370464L;
	private List<String> requiredFields;
	private Map<String,String> valueFieldMapping;

	public List<String> getRequiredFields() {
		return requiredFields;
	}

	public void setRequiredFields(List<String> requiredFields) {
		this.requiredFields = requiredFields;
	}

	public Map<String,String> getValueFieldMapping() {
		return valueFieldMapping;
	}

	public void setValueFieldMapping(Map<String,String> valueFieldMapping) {
		this.valueFieldMapping = valueFieldMapping;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((requiredFields == null) ? 0 : requiredFields.hashCode());
		result = prime
				* result
				+ ((valueFieldMapping == null) ? 0 : valueFieldMapping
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConfigurationContainer other = (ConfigurationContainer) obj;
		if (requiredFields == null) {
			if (other.requiredFields != null)
				return false;
		} else if (!requiredFields.equals(other.requiredFields))
			return false;
		if (valueFieldMapping == null) {
			if (other.valueFieldMapping != null)
				return false;
		} else if (!valueFieldMapping.equals(other.valueFieldMapping))
			return false;
		return true;
	}
	
}
