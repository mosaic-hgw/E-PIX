package org.emau.icmvc.ganimed.core.listener;

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


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;
import org.emau.icmvc.ganimed.deduplication.DeduplicationEngine;
import org.emau.icmvc.ganimed.deduplication.config.model.MatchingConfiguration;
import org.emau.icmvc.ganimed.deduplication.impl.CommonValidateRequiredStrategy;
import org.emau.icmvc.ganimed.epix.common.model.Contact;
import org.emau.icmvc.ganimed.epix.common.model.Gender;
import org.emau.icmvc.ganimed.epix.common.model.Identifier;
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
import org.emau.icmvc.ganimed.epix.core.gen.MPIGenerator;
import org.emau.icmvc.ganimed.epix.core.gen.impl.EAN13Generator;
import org.emau.icmvc.ganimed.epix.core.impl.EPIXContextImpl;
import org.emau.icmvc.ganimed.epix.core.impl.EPIXFactoryImpl;
import org.emau.icmvc.ganimed.epix.core.impl.EPIXModelMapperImpl;
import org.emau.icmvc.ganimed.epix.core.impl.EPIXServiceImpl;
import org.emau.icmvc.ganimed.epix.core.internal.PersonPreprocessedCache;
import org.emau.icmvc.ganimed.epix.core.notifier.EPIXNotifierFactory;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.AccountDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.IDTypeDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.IdentifierDomainDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.PersonPreprocessedDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.ProjectDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.RoleDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.impl.AccountDAOImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.impl.ContactDAOImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.impl.ContactHistoryDAOImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.impl.IDTypeDAOImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.impl.IdentifierDAOImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.impl.IdentifierDomainDAOImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.impl.IdentifierHistoryDAOImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.impl.PersonDAOImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.impl.PersonHistoryDAOImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.impl.PersonLinkDAOImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.impl.PersonPreprocessedDAOImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.impl.ProjectDAOImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.impl.RoleDAOImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Account;
import org.emau.icmvc.ganimed.epix.core.persistence.model.PersonPreprocessed;
import org.emau.icmvc.ganimed.epix.core.report.impl.HtmlFileReportBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PersonHistoryListenerTest {

private EntityManager em;
private static final PersonPreprocessedCache<PersonPreprocessed> personPreprocessedCache = new PersonPreprocessedCache<PersonPreprocessed>();
private static final Object emSynchronizerDummy = new Object();
private static final Logger logger = Logger.getLogger(EPIXFactoryImpl.class);
private Properties epixProperties = null;
/**
 * Cache initialization
 * @param matchingConfiguration 
 */
private void initCache(MatchingConfiguration matchingConfiguration) {
	synchronized (emSynchronizerDummy) {
		synchronized (personPreprocessedCache) {
			if (!personPreprocessedCache.isInitialised()) {
				logger.info("initialise pp cache");
				personPreprocessedCache.init(em,matchingConfiguration);
				logger.info("pp cache initialised");
			}
		}
	}
}
	
	public PersonHistoryListenerTest() {}
	
	@Before
	public void setUp() throws Exception {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("epix_test");
		em = emf.createEntityManager();
	}
	
	@Test
	public void testPersonListenerHistoryTest()  {
		EPIXServiceImpl epixService=null;
		try{
		
		epixProperties = new Properties();
		XMLBindingUtil binder = new XMLBindingUtil();
		MatchingConfiguration matchingConfiguration =null;
		matchingConfiguration = binder.load(MatchingConfiguration.class, epixProperties.getProperty("deduplicationEngine.matchingConfigFile"));
		initCache(matchingConfiguration);
		
		EPIXContext context = new EPIXContextImpl(new EPIXNotifierFactory());
		context.setEntityManager(em);
		
		AccountDAO accountDAO = new AccountDAOImpl();
		accountDAO.setEntityManager(em);
		ContactDAOImpl contactDAO = new ContactDAOImpl();
		contactDAO.setEntityManager(em);
		ContactHistoryDAOImpl contactHistoryDAO = new ContactHistoryDAOImpl();
		contactHistoryDAO.setEntityManager(em);
		
		IdentifierDAOImpl identifierDAO = new IdentifierDAOImpl();
		identifierDAO.setEntityManager(em);
		IdentifierDomainDAO domainDAO = new IdentifierDomainDAOImpl();
		domainDAO.setEntityManager(em);
		IdentifierHistoryDAOImpl identifierHistoryDAO = new IdentifierHistoryDAOImpl();
		identifierHistoryDAO.setEntityManager(em);
		
		IDTypeDAO idType =new IDTypeDAOImpl();
		idType.setEntityManager(em);
		PersonDAOImpl personDAO = new PersonDAOImpl();
		personDAO.setEntityManager(em);
		PersonHistoryDAOImpl personHistoryDAO = new PersonHistoryDAOImpl();
		personHistoryDAO.setEntityManager(em);
		PersonLinkDAOImpl personLinkDAO = new PersonLinkDAOImpl();
		personLinkDAO.setEntityManager(em);
		ProjectDAO projectDAO = new ProjectDAOImpl();
		projectDAO.setEntityManager(em);
		RoleDAO roleDAO = new RoleDAOImpl();
		roleDAO.setEntityManager(em);
		
		PersonPreprocessedDAO personPreprocessedDAO = new PersonPreprocessedDAOImpl();
		personPreprocessedDAO.setEntityManager(em);
//		ContactPreprocessedDAO contactPreprocessedDAO = new ContactPreprocessedDAOImpl();
//		contactPreprocessedDAO.setEntityManager(em);
		
		context.setAccountDAO(accountDAO);
		context.setContactDAO(contactDAO);
		context.setContactHistoryDAO(contactHistoryDAO);
		context.setIdentifierDAO(identifierDAO);
		context.setIdentifierDomainDAO(domainDAO);
		context.setIdentifierHistoryDAO(identifierHistoryDAO);
		context.setIdTypeDAO(idType);
		context.setPersonDAO(personDAO);
		context.setPersonHistoryDAO(personHistoryDAO);
		context.setPersonLinkDAO(personLinkDAO);
		context.setProjectDAO(projectDAO);
		context.setPersonPreprocessedDAO(personPreprocessedDAO);
//		context.setContactPreprocessedDAO(contactPreprocessedDAO);

			
		Account account = accountDAO.getAccount("minca", SHA.getSHA1("minca2011", null));
		
		context.setAccount(account);
		
		epixService = new EPIXServiceImpl(context, personPreprocessedCache, matchingConfiguration);
		
		epixService.setModelMapper(new EPIXModelMapperImpl());
		
		HtmlFileReportBuilder html =new HtmlFileReportBuilder();
		html.setReportActivation("false");
		html.setReportFile("/tmp/epix_dev.html");
		epixService.setReportBuilder(html);
		
		DeduplicationEngine<PersonPreprocessed> ded = new DeduplicationEngine<PersonPreprocessed>();
		//ded.setMatchingConfigFile("matching-config-1.5.0.xml");
		//ded.setPreprocessingStrategy(new CommonPreprocessor<PersonPreprocessed>());
		ded.setValidateRequiredStrategy(new CommonValidateRequiredStrategy<PersonPreprocessed>());
		ded.setMatchCandidateLoader(new CommonMatchCandidateLoader<PersonPreprocessed>(personPreprocessedCache));
		ded.init(matchingConfiguration);
		
		epixService.setDeduplicationEngine(ded);
		
		Map<String, MPIGenerator> map =new HashMap<String, MPIGenerator>();
		map.put("EAN13", new MPIGenerator(new EAN13Generator(context)));
		
		epixService.setGenerators(map);

	}catch (Exception e) {
		e.printStackTrace();
		Assert.fail(e.getMessage());
	}
		
		Patient p = new Patient();
		
		p.setFirstName("aBc");
		p.setLastName("Def");						
		p.setGender(Gender.M);
		p.setBirthDate(new Date(76, 7, 11));
	
		Contact contact = new Contact();
		contact.setCity("test");
		contact.setStreet("Musterstr. 12");
		contact.setZipCode("17491");		
		
		p.getContacts().add(contact);
		
		Identifier ident1 = new Identifier();
		ident1.setValue("CCC123456789");
		ident1.setSendingApplication("MInCa");
		
		MPIRequest request = new MPIRequest();
								
		RequestEntry r1 = new RequestEntry();
		r1.setPerson(p);
		p.getIdentifiers().add(ident1);

		
		org.emau.icmvc.ganimed.epix.common.model.IdentifierDomain idomain = new org.emau.icmvc.ganimed.epix.common.model.IdentifierDomain();
				
		idomain.setName("IT-Kohortenmanagement");
		idomain.setOid("1.2.276.0.76.3.1.132.100.1.1.1.3.0.1.1");			
		
		request.setSource("GANI_MED");
		request.getRequestEntries().add(r1);
	
		
		try {
			em.getTransaction().begin();
			
			MPIResponse res = epixService.requestMPI(request);
			
			
			List<ResponseEntry> rentries = res.getResponseEntries();
	//		assertEquals(rentries.size() , 2);
			for (ResponseEntry entry : rentries) {
				Person person = entry.getPerson();
				MPIID mpiId = person.getMpiid();
				List<Identifier> idents = person.getIdentifiers();
				for (Identifier ident : idents) {
					org.emau.icmvc.ganimed.epix.common.model.IdentifierDomain id = ident.getIdentifierDomain();
//					assertEquals(id.getName(), idomain.getName());
//					assertEquals(id.getOid(), idomain.getOid());
				}
				System.out.println(entry.getErrorCode().getMessage());				
				System.out.println("Requested MPI     : "+mpiId.getValue());
				System.out.println("Requested MPI Type: "+mpiId.getIdType());
				System.out.println("Associated Patient:  "+entry.getPerson().getFirstName()+" "+entry.getPerson().getLastName());		
				System.out.println("InfoCode          : "+ entry.getErrorCode().getMessage());
				int c_cnt = 0;
				for (Contact c : entry.getPerson().getContacts()) {
					System.out.println("Contact[" + c_cnt + "]        : " + c.toString());
					c_cnt++;
				}
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
	//		assertTrue(false);
		} 
		//new
		p.setFirstName("NewFirstName");
		
		MPIRequest request2 = new MPIRequest();
		RequestEntry r2 = new RequestEntry();
		r2.setPerson(p);
		p.getIdentifiers().add(ident1);

		request2.setSource("GANI_MED");
		request2.getRequestEntries().add(r2);
		try {
			em.getTransaction().begin();
			
			MPIResponse res = epixService.updatePerson(request2);
			
			
			List<ResponseEntry> rentries = res.getResponseEntries();
	//		assertEquals(rentries.size() , 2);
			for (ResponseEntry entry : rentries) {
				Person person = entry.getPerson();
				MPIID mpiId = person.getMpiid();
				List<Identifier> idents = person.getIdentifiers();
				for (Identifier ident : idents) {
					org.emau.icmvc.ganimed.epix.common.model.IdentifierDomain id = ident.getIdentifierDomain();
//					assertEquals(id.getName(), idomain.getName());
//					assertEquals(id.getOid(), idomain.getOid());
				}
				System.out.println("---Output for updated Person---");
				System.out.println(entry.getErrorCode().getMessage());				
				System.out.println("Requested MPI     : "+mpiId.getValue());
				System.out.println("Requested MPI Type: "+mpiId.getIdType());
				System.out.println("Associated Patient:  "+entry.getPerson().getFirstName()+" "+entry.getPerson().getLastName());		
				System.out.println("InfoCode          : "+ entry.getErrorCode().getMessage());
				int c_cnt = 0;
				for (Contact c : entry.getPerson().getContacts()) {
					System.out.println("Contact[" + c_cnt + "]        : " + c.toString());
					c_cnt++;
				}
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
	//		assertTrue(false);
		} 
	//	assertTrue(true);
	//	em.close();
	}
	

	@After
	public void tearDown() throws Exception {				
		//em.close();
	}
}
