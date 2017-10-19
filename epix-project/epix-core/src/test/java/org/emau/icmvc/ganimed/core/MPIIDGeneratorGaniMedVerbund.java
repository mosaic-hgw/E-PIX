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


import javax.persistence.EntityManager;

import org.emau.icmvc.ganimed.epix.common.model.Contact;
import org.emau.icmvc.ganimed.epix.common.model.Gender;
import org.emau.icmvc.ganimed.epix.common.model.HealthcareProvider;
import org.emau.icmvc.ganimed.epix.common.model.Identifier;
import org.emau.icmvc.ganimed.epix.common.model.RequestEntry;
import org.emau.icmvc.ganimed.epix.core.impl.EPIXServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MPIIDGeneratorGaniMedVerbund {

	private EPIXServiceImpl epixService = null;
	private EntityManager em =null;
	
	public MPIIDGeneratorGaniMedVerbund() {}
	
	@Before
	protected void setUp() throws Exception {
//		EntityManagerFactory emf = Persistence.createEntityManagerFactory("epix_test");
//		em = emf.createEntityManager();
	}
	
	@Test
	public void testMPIRequest()  {
//		try{
//		
//		EPIXContext context = new EPIXContextImpl(new EPIXNotifierFactory());
//		context.setEntityManager(em);
//		
//		AccountDAO accountDAO = new AccountDAOImpl();
//		accountDAO.setEntityManager(em);
//		ContactDAOImpl contactDAO = new ContactDAOImpl();
//		contactDAO.setEntityManager(em);
//		ContactHistoryDAOImpl contactHistoryDAO = new ContactHistoryDAOImpl();
//		contactHistoryDAO.setEntityManager(em);
//		
//		IdentifierDAOImpl identifierDAO = new IdentifierDAOImpl();
//		identifierDAO.setEntityManager(em);
//		IdentifierDomainDAO domainDAO = new IdentifierDomainDAOImpl();
//		domainDAO.setEntityManager(em);
//		IdentifierHistoryDAOImpl identifierHistoryDAO = new IdentifierHistoryDAOImpl();
//		identifierHistoryDAO.setEntityManager(em);
//		
//		IDTypeDAO idType =new IDTypeDAOImpl();
//		idType.setEntityManager(em);
//		MPIIDDAOImpl mpiIdDAO = new MPIIDDAOImpl();
//		mpiIdDAO.setEntityManager(em);
//		PersonDAOImpl personDAO = new PersonDAOImpl();
//		personDAO.setEntityManager(em);
//		PersonHistoryDAOImpl personHistoryDAO = new PersonHistoryDAOImpl();
//		personHistoryDAO.setEntityManager(em);
//		PersonLinkDAOImpl personLinkDAO = new PersonLinkDAOImpl();
//		personLinkDAO.setEntityManager(em);
//		ProjectDAO projectDAO = new ProjectDAOImpl();
//		projectDAO.setEntityManager(em);
//		RoleDAO roleDAO = new RoleDAOImpl();
//		roleDAO.setEntityManager(em);
//				
//		
//		context.setAccountDAO(accountDAO);
//		context.setContactDAO(contactDAO);
//		context.setContactHistoryDAO(contactHistoryDAO);
//		context.setIdentifierDAO(identifierDAO);
//		context.setIdentifierDomainDAO(domainDAO);
//		context.setIdentifierHistoryDAO(identifierHistoryDAO);
//		context.setIdTypeDAO(idType);
//		context.setMpiIdDAO(mpiIdDAO);
//		context.setPersonDAO(personDAO);
//		context.setPersonHistoryDAO(personHistoryDAO);
//		context.setPersonLinkDAO(personLinkDAO);
//		context.setProjectDAO(projectDAO);
//			
//		ShaPasswordEncoder pe = new ShaPasswordEncoder();
//		Account account = accountDAO.getAccount("minca", pe.encodePassword("minca2011", null));
//		
//		context.setAccount(account);
//		
//		epixService = new EPIXServiceImpl(context);
//		
//		epixService.setModelMapper(new EPIXModelMapperImpl());
//		
//		HtmlFileReportBuilder html =new HtmlFileReportBuilder();
//		html.setReportActivation("true");
//		html.setReportFile("/tmp/epix_dev.html");
//		epixService.setReportBuilder(html);
//		
//		DeduplicationEngine ded = new DeduplicationEngine<Person>();
//		ded.setMatchingConfigFile("matching-config-1.5.0.xml");
//		ded.setBlockingAlgorithmFactory(new CommonBlockingAlgorithmFactory<Person>());
//		ded.setPreprocessingStrategy(new CommonPreprocessor<Person>());
//		ded.setMatchCandidateLoader(new CommonMatchCandidateLoader<Person>(context));
//		ded.init();
//		
//		epixService.setDeduplicationEngine(ded);
//		
//		Map<String, MPIGenerator> map =new HashMap<String, MPIGenerator>();
//		map.put("EAN13", new MPIGenerator(new EAN13Generator(context)));
//		
//		epixService.setGenerators(map);
//
//	}catch (Exception e) {
//		e.printStackTrace();
//	}
//		
//		em.getTransaction().begin();
		
//		Patient p = new Patient();
//		
//		p.setFirstName("Katja");
//		p.setLastName("Wienholz");						
//		p.setGender(Gender.F);
//	
//		Contact contact = new Contact();
//		contact.setCity("Greifswald");
//		contact.setStreet("Friedrich-Löfflerstr");
//		contact.setZipCode("17475");		
//		
//		p.getContacts().add(contact);
//		
//		Identifier ident1 = new Identifier();
//		ident1.setValue("28");
//		RequestEntry r1 = new RequestEntry();
//		r1.setPerson(p);
//		p.getIdentifiers().add(ident1);
		
//		MPIRequest request = new MPIRequest();
//								
//
//		
//		org.emau.icmvc.ganimed.common.model.IdentifierDomain idomain = new org.emau.icmvc.ganimed.common.model.IdentifierDomain();
//		
//		idomain.setName("GANI_MED Verbund");
//		idomain.setOid("1.2.276.0.76.3.1.132.100");		
//		
//		request.setIdentifierDomain(idomain);
//		request.getRequestEntries().add( getRequestEntry("Eckhard", "Weber", Gender.M,"Greifswald", "Friedrich-Loefflerstr", "17475", "28"));
	//	request.getRequestEntries().add( getRequestEntry("Silke", "Kreutzer", Gender.F,"Greifswald", "Ferdinand-Sauerbruch-Str", "17475", "29"));
//		request.getRequestEntries().add( getRequestEntry("Kathrin", "Radue-Thurow", Gender.F,"Greifswald", "Friedrich-Loefflerstr", "17475", "30"));
//		request.getRequestEntries().add( getRequestEntry("Simone", "Christensen", Gender.F,"Greifswald", "Friedrich-Loefflerstr", "17487", "31"));
//		request.getRequestEntries().add( getRequestEntry("Dita", "Gabe", Gender.F,"Greifswald", "Friedrich-Loefflerstr", "17475", "32"));
//		request.getRequestEntries().add( getRequestEntry("Katja", "Wienholz", Gender.F,"Greifswald", "Friedrich-Loefflerstr", "17475", "33"));
//		request.getRequestEntries().add( getRequestEntry("Sven", "Gläser", Gender.M,"Greifswald", "Friedrich-Loefflerstr", "17475", "34"));
//		request.getRequestEntries().add( getRequestEntry("Sandra", "Pasewald", Gender.M,"Greifswald", "Ellenholzstr", "17475", "35"));
//		request.getRequestEntries().add( getRequestEntry("Paul", "Marschall", Gender.M,"Greifswald", "Friedrich-Loefflerstr 70", "17489", "36"));
//		request.getRequestEntries().add( getRequestEntry("Anne", "Lexow", Gender.F,"Greifswald", "Friedrich-Loefflerstr 70", "17489", "37"));
//		request.getRequestEntries().add( getRequestEntry("Conrad", "Berbig", Gender.M,"Greifswald", "Friedrich-Loefflerstr 70", "17489", "38"));
//		request.getRequestEntries().add( getRequestEntry("Kristin", "Schreiber", Gender.F,"Greifswald", "An den Wurthen 14", "17489", "39"));
//	
//		
//		try {
//			MPIResponse res = epixService.requestMPI(request);
//			
//			
//			List<ResponseEntry> rentries = res.getResponseEntries();
//			
//			for (ResponseEntry entry : rentries) {
//				Person person = entry.getPerson();
//				MPIID mpiId = person.getMpiid();
//				List<Identifier> idents = person.getIdentifiers();
//				for (Identifier ident : idents) {
//					org.emau.icmvc.ganimed.common.model.IdentifierDomain id = ident.getIdentifierDomain();
//			
//				}
//				System.out.println(entry.getErrorCode().getMessage());				
//				System.out.println("Requested MPI "+mpiId.getValue());
//				System.out.println("Requested MPI Type "+mpiId.getIdType());
//				System.out.println("Associated Patient "+entry.getPerson().getFirstName()+" "+entry.getPerson().getLastName());			
//			}
			
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
//		em.getTransaction().commit();
//		} catch (Exception e) {
//			em.getTransaction().rollback();
//			e.printStackTrace();
//			assertTrue(false);
//		} 
//		assertTrue(true);
//		em.close();
	}

	@After
	protected void tearDown() throws Exception {				
//		em.close();
	}
	
	protected RequestEntry getRequestEntry(String vorname, String nachname, Gender gender, String city, String street, String zipCode, String ident){
		
		RequestEntry r = new RequestEntry();
		
		Identifier ident1 = new Identifier();
		ident1.setValue(ident);
		ident1.setSendingApplication("ganimed");
		
		HealthcareProvider patient = new HealthcareProvider();
		patient.setFirstName(vorname);
		patient.setLastName(nachname);
		patient.setGender(gender);
		
		Contact contact = new Contact();
		contact.setCity(city);
		contact.setStreet(street);
		contact.setZipCode(zipCode);
		
		patient.getContacts().add(contact);
		patient.getIdentifiers().add(ident1);
		
		r.setPerson(patient);
		return r;
	} 

}
