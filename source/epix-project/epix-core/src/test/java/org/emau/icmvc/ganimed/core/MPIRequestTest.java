package org.emau.icmvc.ganimed.core;

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


import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.emau.icmvc.ganimed.deduplication.DeduplicationEngine;
import org.emau.icmvc.ganimed.deduplication.config.model.MatchingConfiguration;
import org.emau.icmvc.ganimed.deduplication.impl.CommonPerfectMatchStrategy;
import org.emau.icmvc.ganimed.deduplication.impl.CommonValidateRequiredStrategy;
import org.emau.icmvc.ganimed.epix.common.model.Contact;
import org.emau.icmvc.ganimed.epix.common.model.Gender;
import org.emau.icmvc.ganimed.epix.common.model.Identifier;
import org.emau.icmvc.ganimed.epix.common.model.IdentifierDomain;
import org.emau.icmvc.ganimed.epix.common.model.MPIID;
import org.emau.icmvc.ganimed.epix.common.model.MPIRequest;
import org.emau.icmvc.ganimed.epix.common.model.MPIResponse;
import org.emau.icmvc.ganimed.epix.common.model.Patient;
import org.emau.icmvc.ganimed.epix.common.model.Person;
import org.emau.icmvc.ganimed.epix.common.model.RequestEntry;
import org.emau.icmvc.ganimed.epix.common.model.ResponseEntry;
import org.emau.icmvc.ganimed.epix.common.utils.SHA;
import org.emau.icmvc.ganimed.epix.common.utils.XMLBindingUtil;
import org.emau.icmvc.ganimed.epix.core.EPIXContext;
import org.emau.icmvc.ganimed.epix.core.deduplication.impl.CommonMatchCandidateLoader;
import org.emau.icmvc.ganimed.epix.core.deduplication.impl.CommonPerfectMatchProcessor;
import org.emau.icmvc.ganimed.epix.core.gen.MPIGenerator;
import org.emau.icmvc.ganimed.epix.core.gen.impl.EAN13Generator;
import org.emau.icmvc.ganimed.epix.core.impl.EPIXContextImpl;
import org.emau.icmvc.ganimed.epix.core.impl.EPIXModelMapperImpl;
import org.emau.icmvc.ganimed.epix.core.impl.EPIXServiceImpl;
import org.emau.icmvc.ganimed.epix.core.internal.PersonPreprocessedCache;
import org.emau.icmvc.ganimed.epix.core.notifier.EPIXNotifierFactory;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.AccountDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.impl.AccountDAOImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.impl.ContactDAOImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.impl.ContactHistoryDAOImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.impl.IDTypeDAOImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.impl.IdentifierDAOImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.impl.IdentifierDomainDAOImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.impl.IdentifierHistoryDAOImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.impl.PersonDAOImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.impl.PersonGroupDAOImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.impl.PersonGroupHistoryDAOImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.impl.PersonHistoryDAOImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.impl.PersonLinkDAOImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.impl.PersonLinkHistoryDAOImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.impl.PersonPreprocessedDAOImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.impl.ProjectDAOImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.impl.SourceDAOImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Account;
import org.emau.icmvc.ganimed.epix.core.persistence.model.PersonPreprocessed;
import org.emau.icmvc.ganimed.epix.core.report.impl.HtmlFileReportBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MPIRequestTest  {

	private EntityManager em;
	
	public MPIRequestTest() {}
	
	@Before
	public void setUp() throws Exception {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("epix_new");
		em = emf.createEntityManager();
	}
	
	@Test
	public void testMPIRequest()  {
		EPIXServiceImpl epixService=null;
		try{
		
			EPIXContext context = new EPIXContextImpl(new EPIXNotifierFactory());
			
			XMLBindingUtil binder = new XMLBindingUtil();
			MatchingConfiguration matchingConfiguration = binder.load(MatchingConfiguration.class, "matching-config-2.1_0.xml");
			
			PersonPreprocessedCache<PersonPreprocessed> personPreprocessedCache = new PersonPreprocessedCache<PersonPreprocessed>();
			personPreprocessedCache.init(em, matchingConfiguration);
			
			AccountDAO accountDAO = new AccountDAOImpl();

			context.setAccountDAO(accountDAO);
			context.setContactDAO(new ContactDAOImpl());
			context.setContactHistoryDAO(new ContactHistoryDAOImpl());
			context.setIdentifierDAO(new IdentifierDAOImpl());
			context.setIdentifierDomainDAO(new IdentifierDomainDAOImpl());
			context.setIdentifierHistoryDAO(new IdentifierHistoryDAOImpl());
			context.setIdTypeDAO(new IDTypeDAOImpl());
			context.setPersonDAO(new PersonDAOImpl());
			context.setPersonHistoryDAO(new PersonHistoryDAOImpl());
			context.setPersonLinkDAO(new PersonLinkDAOImpl());
			context.setProjectDAO(new ProjectDAOImpl());
			context.setPersonPreprocessedDAO(new PersonPreprocessedDAOImpl());
			context.setPersonGroupDAO(new PersonGroupDAOImpl());
			context.setPersonGroupHistoryDAO(new PersonGroupHistoryDAOImpl());
			context.setPersonLinkHistoryDAO(new PersonLinkHistoryDAOImpl());
			context.setSourceDAO(new SourceDAOImpl());	
			context.setEntityManager(em);
			
			Account account = accountDAO.getAccount("user", SHA.getSHA1("minca2011", null));
			
			context.setAccount(account);
			
			epixService = new EPIXServiceImpl(context,personPreprocessedCache,matchingConfiguration);
			
			epixService.setModelMapper(new EPIXModelMapperImpl());
			
			HtmlFileReportBuilder html =new HtmlFileReportBuilder();
			html.setReportActivation("false");
			html.setReportFile("/tmp/epix_dev.html");
			epixService.setReportBuilder(html);
	
			DeduplicationEngine<PersonPreprocessed> ded = new DeduplicationEngine<PersonPreprocessed>();
			ded.setValidateRequiredStrategy(new CommonValidateRequiredStrategy<PersonPreprocessed>());
			ded.setMatchCandidateLoader(new CommonMatchCandidateLoader<PersonPreprocessed>(personPreprocessedCache));
			//NEW for perfectMatch
			ded.setPerfectMatchStrategy(new CommonPerfectMatchStrategy<PersonPreprocessed>());
			ded.setPerfectMatchProzessor(new CommonPerfectMatchProcessor<PersonPreprocessed>(personPreprocessedCache));		
			
			ded.init(matchingConfiguration);
			
			epixService.setDeduplicationEngine(ded);
			
			Map<String, MPIGenerator> map =new HashMap<String, MPIGenerator>();
			map.put("EAN13", new MPIGenerator(new EAN13Generator(context)));
			
			epixService.setGenerators(map);
	
		}catch (Exception e) {
			e.printStackTrace();
			assertTrue(e.getMessage(), false);
		}
		
		Patient p = new Patient();
		
		p.setFirstName("Peter");
		p.setLastName("Meier");						
		p.setGender(Gender.M);
		p.setBirthDate(new Date(67, 8, 16));
		

		Contact contact = new Contact();
		contact.setCity("Musterort");
		contact.setStreet("Musterstr. 11");
		contact.setZipCode("12345");		
		
		p.getContacts().add(contact);
		
		Contact contact2 = new Contact();
		contact2.setCity("Teststadt");
		contact2.setStreet("Teststr. 9");
		contact2.setZipCode("54321");		
		
		p.getContacts().add(contact2);
		
		IdentifierDomain idomain = new IdentifierDomain();		
		idomain.setName("TEST");
		//Safe system
		idomain.setOid("1.2.276.0.76.3.1.132.100.1.1.1.3.0.1.1");	
		// Unsafe system
//		idomain.setOid("1.2.276.0.76.3.1.132.100");	
//		 Another unsafe system
//		idomain.setOid("1.2.276.0.76.3.1.132.100.1.1.1");
		Identifier ident1 = new Identifier();
		ident1.setValue("HGW-a4567890");
		ident1.setSendingApplication("MInCa");
		ident1.setIdentifierDomain(idomain);
//		Identifier ident2 = new Identifier();
//		ident2.setValue("123");
//		ident2.setSendingApplication("MInCa");
//		ident2.setIdentifierDomain(idomain);
		
//		org.emau.icmvc.ganimed.common.model.IdentifierDomain idomain2 = new org.emau.icmvc.ganimed.common.model.IdentifierDomain();		
//		idomain2.setName("TEST A");
//		idomain2.setOid("1.2.276.0.76.3.1.132.100");	
		
//		Identifier mpiIdent = new Identifier();
//		mpiIdent.setValue("1001000016144");
//		mpiIdent.setReceiveApplication("MInCa");
//		org.emau.icmvc.ganimed.common.model.IdentifierDomain mpidomain = null;
//		try {
//			mpidomain = epixService.getModelMapper().mapOutputDomain(
//					epixService.getContext().getIdentifierDomainDAO().findMPIDomain());
//		} catch (MPIException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		mpiIdent.setIdentifierDomain(mpidomain);
//		
//		p.getIdentifiers().add(mpiIdent);
		
//		MPIID mpiid = new MPIID("1001000016144", "EAN13");
//		p.setMpiid(mpiid);
		
		
		MPIRequest request = new MPIRequest();
								
		RequestEntry r1 = new RequestEntry();
		r1.setPerson(p);
		p.getIdentifiers().add(ident1);
//		p.getIdentifiers().add(ident2);
		
		request.setSource("GANI_MED");
		request.getRequestEntries().add(r1);
		
		
		try {
			em.getTransaction().begin();
			GregorianCalendar start = new GregorianCalendar();
			MPIResponse res = epixService.requestMPI(request);
			GregorianCalendar stop = new GregorianCalendar();
			
			String difference = difference(start, stop);
			
			List<ResponseEntry> rentries = res.getResponseEntries();
	//		assertEquals(rentries.size() , 2);
			for (ResponseEntry entry : rentries) {
				Person person = entry.getPerson();
				MPIID mpiId = person.getMpiid();
//				List<Identifier> idents = person.getIdentifiers();
//				for (Identifier ident : idents) {
//					org.emau.icmvc.ganimed.common.model.IdentifierDomain id = ident.getIdentifierDomain();
//					assertEquals(id.getName(), idomain.getName());
//					assertEquals(id.getOid(), idomain.getOid());
//				}
				System.out.println(entry.getErrorCode().getMessage());				
				System.out.println("Requested MPI     : "+mpiId.getValue());
				System.out.println("Requested MPI Type: "+mpiId.getIdType());
				System.out.println("Associated Patient: "+entry.getPerson().getFirstName()+" "+entry.getPerson().getLastName());		
				System.out.println("InfoCode          : "+ entry.getErrorCode().getMessage());
				int c_cnt = 0;
				for (Contact c : entry.getPerson().getContacts()) {
					System.out.println("Contact[" + c_cnt + "]        : " + c.toString());
					c_cnt++;
				}
				System.out.println("Duration: " + difference);
				assertTrue(entry.getErrorCode().getCode() == (0) 
						|| entry.getErrorCode().getCode() == (16)
						|| entry.getErrorCode().getCode() == (17)
						|| entry.getErrorCode().getCode() == (41));
			}
			
	//		throw new MPIException(ErrorCode.EPIXCLIENT_INSTANTIATION_ERROR);
			
//			JAXBContext context;
//			try {
//				context = JAXBContext.newInstance( MPIResponse.class.getPackage().getName() );
//	
//			    Marshaller m = context.createMarshaller();
//			    m.marshal(new JAXBElement<MPIRequest>(new QName(XMLConstants.DEFAULT_NS_PREFIX,"rootTag"), MPIRequest.class, request),  new File("e:\\MPIRequest.xml"));
//	
//			} catch (JAXBException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			em.getTransaction().commit();
		} catch (Exception e) {
			em.getTransaction().rollback();
			e.printStackTrace();
			assertTrue(false);
		} 
	//	assertTrue(true);
	//	em.close();
	}
	

	@After
	public void tearDown() throws Exception {				
		//em.close();
	}
	
	private static String difference(GregorianCalendar start, GregorianCalendar stop) {
		long difference = stop.getTimeInMillis() - start.getTimeInMillis();
	    int days = (int)(difference / (1000 * 60 * 60 * 24));
	    int hours = (int)(difference / (1000 * 60 * 60) % 24);
	    int minutes = (int)(difference / (1000 * 60) % 60);
	    int seconds = (int)(difference / 1000 % 60);
	    int millis = (int)(difference % 1000);
		
		String out = days + " days, " + hours + ":" + minutes + ":" + seconds + "." + millis;
		
		return out;
	}

}
