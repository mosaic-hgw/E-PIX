package org.emau.icmvc.ganimed.ttp.model;

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
import java.util.LinkedHashMap;
import java.util.List;

import org.emau.icmvc.ganimed.epix.common.model.CriticalMatch;

/**
 * holds data to create a Detail view from. Maps label keys to values
 * 
 * @author weiherg
 * 
 */
public class PersonDetails {
	private LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

	public PersonDetails(CriticalMatch crit) {

		map.put("mpi", crit.getMpiid());
		map.put("firstName", crit.getFirstName());
		map.put("secondName", crit.getMiddleName());
		map.put("maidenname", crit.getMothersMaidenName());
		map.put("lastName", crit.getName());
		map.put("gender", crit.getSex());
		map.put("birthDate", crit.getBirthdate());
		map.put("birthplace", crit.getBirthPlace());
		map.put("nationality", crit.getNationality());
		map.put("degree", crit.getDegree());

		map.put("street", crit.getStreet());
		map.put("zip", crit.getPostalCode());
		map.put("city", crit.getCity());
		map.put("state", crit.getState());
		map.put("country", crit.getCountry());

		map.put("value1", crit.getValue1());
		map.put("value2", crit.getValue2());
		map.put("value3", crit.getValue3());
		map.put("value4", crit.getValue4());
		map.put("value5", crit.getValue5());
		map.put("value6", crit.getValue6());
		map.put("value7", crit.getValue7());
		map.put("value8", crit.getValue8());
		map.put("value9", crit.getValue9());
		map.put("value10", crit.getValue10());
		
		// Berechne Errorcode
//		map.put("adresse", crit.getFirstName());

		map.put("lastChange", crit.getLastChange());

	}

	/**
	 * 
	 * @return List of key strings to iterate the person Details map from
	 */
	public List<String> getDetailKeys() {
		List<String> keys = new ArrayList<String>();
		for (String key : map.keySet()) {
			keys.add(key);
		}
		return keys;
	}

	/**
	 * 
	 * @param key
	 * @return the value mapped to the key
	 */
	public String get(String key) {
		return map.get(key);
	}
}
