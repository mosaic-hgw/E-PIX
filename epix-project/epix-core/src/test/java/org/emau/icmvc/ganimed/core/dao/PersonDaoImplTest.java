package org.emau.icmvc.ganimed.core.dao;

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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.emau.icmvc.ganimed.deduplication.config.model.MatchingConfiguration;
import org.emau.icmvc.ganimed.deduplication.impl.CommonPreprocessor;
import org.emau.icmvc.ganimed.epix.common.utils.XMLBindingUtil;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Patient;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Person;
import org.junit.After;
import org.junit.Before;

public class PersonDaoImplTest {
	
	private EntityManager em;
	
	private MatchingConfiguration config = null;
	
	@SuppressWarnings("rawtypes")
	private CommonPreprocessor cp = new CommonPreprocessor();
	
	@Before
	public void setUp() throws Exception {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("epix_test");
		em = emf.createEntityManager();
		XMLBindingUtil binder = new XMLBindingUtil();
		config =  (MatchingConfiguration)binder.load(MatchingConfiguration.class, "matching-config-1.5.0.xml");
		cp.setMatchingConfiguration(config);
	}
	
	private Person createPerson() {
		Patient p = new Patient();
		p.setFirstName("Frank");
		p.setLastName("Tester");
		p.setGender("M");
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			java.sql.Date date = new java.sql.Date(format.parse("1999-11-21").getTime());
			p.setBirthDate(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
//		Identifier ident1 = new Identifier();
//		ident1.setValue("CCC123456789");
//		ident1.setSendingApplication("MInCa");
//		p.getLocalIdentifiers().add(ident1);

		
		return p;
	}
	
//	@Test
//	public void testUpdatePerson() {
//		PersonDAOImpl dao = new PersonDAOImpl();
//		dao.setEntityManager(em);
//		
//		em.getTransaction().begin();
//		
//		Person u = createPerson();
//		u.setMiddleName("MiddleName");
//		
//		try {
//			dao.updatePerson2(u);
//			em.getTransaction().commit();
//		} catch (MPIException e) {
//			e.printStackTrace();
//		}
//	}
	
	
	//2 types of named queries, but first one is better because of check while building.
//	@Test
//	public void testQuery() {
//	//Type 1
//		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//		java.sql.Date date = null;
//		try {
//			date = new java.sql.Date(format.parse("1999-11-21").getTime());
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		em.getTransaction().begin();
//		Query q = em.createNamedQuery("Person.findByFirstNameAndLastNameAndDateOfBirth");
//		q.setParameter("lastName", "Tester");
//		q.setParameter("firstName", "Frank");
//		q.setParameter("birthDate", date);
//		List li = q.getResultList();
//		Person update = (Person)li.get(0);
//		System.out.println(update.toString());
//		update.setEntityManager(em);
//		Person np = createPerson();
//		np.setId(update.getId());
//		np.setEntityManager(em);
//		np.setGender("W");
//		em.merge(np);
//		
//		
//	//Type 2
//		String lastName = "Tester";
//		em.getTransaction().begin();
//		TypedQuery<Person> q1 = em.createQuery("SELECT p FROM Person p WHERE p.lastName = '" + lastName + "'", Person.class);
//		List li = q1.getResultList();
//		Iterator it = li.iterator();
//		while(it.hasNext()) {
//			Person u = (Person)it.next();
//			System.out.println(u.toString());
//		}
//		Person c = (Person)li.get(0);
//		c.setFirstName("Neuervorname");
//		c.setEntityManager(em);
//		em.persist(c);
//		em.getTransaction().commit();
//	}
	
	@After
	public void close() {
		if (em != null) {
			em.close();
		}
	}

}
