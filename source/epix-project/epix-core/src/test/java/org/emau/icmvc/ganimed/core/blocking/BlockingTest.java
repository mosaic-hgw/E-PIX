package org.emau.icmvc.ganimed.core.blocking;

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

import org.emau.icmvc.ganimed.deduplication.BlockingStrategy;
import org.emau.icmvc.ganimed.deduplication.config.model.MatchingConfiguration;
import org.emau.icmvc.ganimed.epix.common.utils.XMLBindingUtil;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Contact;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Patient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BlockingTest {
	
private MatchingConfiguration config = null;
	
	@Before
	public void setUp() throws Exception {
		XMLBindingUtil binder = new XMLBindingUtil();
		config =  (MatchingConfiguration)binder.load(MatchingConfiguration.class, "matching-config-2.0.4.xml");
	}

	@After
	public void tearDown() throws Exception {
		
	}
	
	@Test
	public void testPreprocessing() {
		
		Patient p1 = new Patient();
		p1.setFirstName("Tom");
		p1.setLastName("Pühvögelß ");
		p1.setGender("m");
		
		String pattern = "yyyy-MM-dd";
		try {
			p1.setBirthDate(new Date(new SimpleDateFormat(pattern).parse("1994-10-19").getTime()));
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Contact c1 = new Contact();
		c1.setStreet("Muster-str .     12");
		c1.setZipCode("17489 ");
		c1.setCity("Greifswald-Wiek");
		p1.getContacts().add(c1);
		
		Patient p2 = new Patient();
		p2.setFirstName("Tom");
		p2.setLastName("Pühvögelß ");
		p2.setGender("m");
		
		try {
			p2.setBirthDate(new Date(new SimpleDateFormat(pattern).parse("1990-03-29").getTime()));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		Contact c2 = new Contact();
		c2.setStreet("Muster-str .     12");
		c2.setZipCode("17489 ");
		c2.setCity("Greifswald-Wiek");
		p2.getContacts().add(c2);
			
		try {
			
			BlockingStrategy<Patient> blockingStrategy = new BlockingStrategy<Patient>(p1, config);
			boolean bool = blockingStrategy.block(p2);
			
			System.out.println("Test: "+ bool);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
