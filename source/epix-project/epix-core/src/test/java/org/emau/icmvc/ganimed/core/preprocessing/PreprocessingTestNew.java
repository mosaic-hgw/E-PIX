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


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.emau.icmvc.ganimed.deduplication.config.model.MatchingConfiguration;
import org.emau.icmvc.ganimed.deduplication.impl.CommonPreprocessor;
import org.emau.icmvc.ganimed.epix.common.utils.XMLBindingUtil;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Contact;
import org.emau.icmvc.ganimed.epix.core.persistence.model.HealthcareProvider;
import org.emau.icmvc.ganimed.epix.core.persistence.model.HealthcareProviderPreprocessed;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Patient;
import org.emau.icmvc.ganimed.epix.core.persistence.model.PatientPreprocessed;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Person;
import org.emau.icmvc.ganimed.epix.core.persistence.model.PersonPreprocessed;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PreprocessingTestNew {

	private MatchingConfiguration config = null;
	
	@SuppressWarnings("rawtypes")
	private CommonPreprocessor cp = new CommonPreprocessor();
	
	@Before
	public void setUp() throws Exception {
		XMLBindingUtil binder = new XMLBindingUtil();
		config =  (MatchingConfiguration)binder.load(MatchingConfiguration.class, "matching-config-2.1.0.xml");
		cp.setMatchingConfiguration(config);
	}

	@After
	public void tearDown() throws Exception {
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void preprocessTest() {
		List<Person> list = createPersons();
		for(Person p : list){
			try {
				PersonPreprocessed new_person = null;
				String type = null;
				if(p instanceof Patient) {
					PersonPreprocessed  pp = new PatientPreprocessed((Patient) p);
					new_person = (PersonPreprocessed) cp.preprocess(pp); 
					type = "Patient";
				} else if (p instanceof HealthcareProvider) {
					PersonPreprocessed  pp = new HealthcareProviderPreprocessed((HealthcareProvider) p);
					new_person = (PersonPreprocessed) cp.preprocess(pp); 
					type = "HealthcareProvider";
				} else {
					System.out.println("Person not type of \"Patient\" or \"HealthcareProvider\"");
					System.out.println("****************************************");
					continue;
				}
							
				System.out.println("Preprocessing test:");
				System.out.println("Persontype: " + type);
				System.out.println("Before:	" + p.getFirstName());
				System.out.println("After:	" + new_person.getFirstName());
				System.out.println("Before:	" + p.getLastName());
				System.out.println("After:	" + new_person.getLastName());
				System.out.println("Before:	" + p.getMiddleName());
				System.out.println("After:	" + new_person.getMiddleName());
				System.out.println("Before:	" + p.getGender());
				System.out.println("After:	" + new_person.getGender());
				System.out.println("Before:	" + p.getContacts().get(0).getZipCode());
//				System.out.println("After:	" + new_person.getContactPreprocessed().get(0).getZipCode());
				System.out.println("Before:	" + p.getContacts().get(0).getStreet());
//				System.out.println("After:	" + new_person.getContactPreprocessed().get(0).getStreet());
				System.out.println("Before:	" + p.getContacts().get(0).getCity());
//				System.out.println("After:	" + new_person.getContactPreprocessed().get(0).getCity());
				System.out.println("****************************************");
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}	
		}
		
	}
	
	private List<Person> createPersons() {
		List<Person> personList = new ArrayList <Person>();
		Patient p1 = new Patient();
		p1.setFirstName("Frank");
		p1.setLastName("Tester");
		p1.setGender("M");
		
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
		try {
			java.sql.Date date = new java.sql.Date(format1.parse("1999-11-21").getTime());
			p1.setBirthDate(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Contact c1 = new Contact();
		c1.setStreet("Muster-str .     12");
		c1.setZipCode("17489 ");
		c1.setCity("Greifswald-Wiek");
		p1.getContacts().add(c1);
		
		personList.add(p1);
		
		HealthcareProvider p2 = new HealthcareProvider();
		p2.setFirstName("Karl - Heinz");
		p2.setLastName("Pühvögelß ");
		p2.setGender("M");
		
		try {
			java.sql.Date date = new java.sql.Date(format1.parse("1963-03-12").getTime());
			p1.setBirthDate(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Contact c2 = new Contact();
		c2.setStreet("Prüfstraße Nr.     12");
		c2.setZipCode(" 17489 ");
		c2.setCity("darß");
		p2.getContacts().add(c2);
		
		personList.add(p2);
		
		return personList;
	}
}
