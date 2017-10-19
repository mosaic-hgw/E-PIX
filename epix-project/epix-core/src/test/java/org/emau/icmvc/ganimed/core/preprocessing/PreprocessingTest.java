package org.emau.icmvc.ganimed.core.preprocessing;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.emau.icmvc.ganimed.deduplication.config.model.MatchingConfiguration;
import org.emau.icmvc.ganimed.deduplication.impl.CommonPreprocessor;
import org.emau.icmvc.ganimed.epix.common.utils.XMLBindingUtil;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Contact;
//import org.emau.icmvc.ganimed.epix.core.persistence.model.ContactPreprocessed;
import org.emau.icmvc.ganimed.epix.core.persistence.model.PatientPreprocessed;
import org.emau.icmvc.ganimed.epix.core.persistence.model.PersonPreprocessed;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PreprocessingTest {
	
	private MatchingConfiguration config = null;
	
	@SuppressWarnings("rawtypes")
	private CommonPreprocessor cp = new CommonPreprocessor();
	
	@Before
	public void setUp() throws Exception {
		XMLBindingUtil binder = new XMLBindingUtil();
		config =  (MatchingConfiguration)binder.load(MatchingConfiguration.class, "matching-config-2.0.3.xml");
		cp.setMatchingConfiguration(config);
	}

	@After
	public void tearDown() throws Exception {
		
	}
	
	@Test
	public void testPreprocessing() {
		
		PatientPreprocessed p1 = new PatientPreprocessed();
		p1.setFirstName("Karl - Heinz");
		p1.setLastName("Pühvögelß ");
		
		String pattern = "yyyy-MM-dd";
		try {
			p1.setBirthDate(new Date(new SimpleDateFormat(pattern).parse("1963-03-12").getTime()));
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Contact c1 = new Contact();
		c1.setStreet("Muster-str .     12");
		c1.setZipCode("17489 ");
		c1.setCity("Greifswald-Wiek");
//		p1.getContactPreprocessed().add(c1);
			
		try {
			@SuppressWarnings("unchecked")
			PersonPreprocessed new_person = (PersonPreprocessed) cp.preprocess(p1);
						
			System.out.println("Preprocessing "+new_person.getFirstName());
			System.out.println("Preprocessing "+new_person.getLastName());
			System.out.println("Preprocessing "+new_person.getMiddleName());
//			System.out.println("Preprocessing "+new_person.getContactPreprocessed().get(0).getZipCode());
//			System.out.println("Preprocessing "+new_person.getContactPreprocessed().get(0).getStreet());
//			System.out.println("Preprocessing "+new_person.getContactPreprocessed().get(0).getCity());
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
