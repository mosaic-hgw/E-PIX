package org.emau.icmvc.ganimed.core.matching;

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

import junit.framework.TestCase;

import org.emau.icmvc.ganimed.deduplication.config.model.MatchingConfiguration;
import org.emau.icmvc.ganimed.deduplication.impl.FellegiSunterAlgorithm;
import org.emau.icmvc.ganimed.deduplication.model.MatchResult;
import org.emau.icmvc.ganimed.epix.common.utils.XMLBindingUtil;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Contact;
import org.emau.icmvc.ganimed.epix.core.persistence.model.HealthcareProvider;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Patient;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Person;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MatchingTest extends TestCase {

	private MatchingConfiguration config = null;
	
	private FellegiSunterAlgorithm<Person> pm = new FellegiSunterAlgorithm<Person>();
	
	@Before
	protected void setUp() throws Exception {
		XMLBindingUtil binder = new XMLBindingUtil();
		config =  (MatchingConfiguration)binder.load(MatchingConfiguration.class, "matching-config-2.0.4.xml"); 
		pm.setMatchingConfiguration(config);
	}

	@After
	protected void tearDown() throws Exception {
	}
	
	@Test
	public void testMatching() {
		
		Patient p1 = new Patient();
		p1.setFirstName("OELOEF");
		p1.setLastName("SCHUBERT");
		p1.setGender("M");
		String pattern = "yyyy-MM-dd";
		try {
			p1.setBirthDate(new Date(new SimpleDateFormat(pattern).parse("1980-09-15").getTime()));
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Contact c1 = new Contact();
		c1.setStreet("Franz-Mehring-Str. 24");
		c1.setZipCode("17489");
		c1.setCity("Greifswald");
		p1.getContacts().add(c1);
		
		
		HealthcareProvider p2 = new HealthcareProvider();
		p2.setFirstName("OLOEF");
		p2.setLastName("SCHUBERT");
		p2.setGender("M");
		try {
			p2.setBirthDate(new Date(new SimpleDateFormat(pattern).parse("1980-09-15").getTime()));
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Contact c2 = new Contact();
		c2.setStreet("Franz-Mehring-Str. 24");
		c2.setZipCode("17489");
		c2.setCity("Greifswald ");
		p2.getContacts().add(c2);
			
		try {
			MatchResult<Person> mr = pm.match(p1, p2);			
			System.out.println("Matchresult "+mr);
			System.out.println("Decision: " + mr.getDecision());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
