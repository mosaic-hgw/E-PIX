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
import org.emau.icmvc.ganimed.deduplication.impl.CommonPerfectMatchStrategy;
import org.emau.icmvc.ganimed.deduplication.impl.CommonValidateRequiredStrategy;
import org.emau.icmvc.ganimed.epix.common.MPIException;
import org.emau.icmvc.ganimed.epix.common.model.CriticalMatch;
import org.emau.icmvc.ganimed.epix.common.model.Gender;
import org.emau.icmvc.ganimed.epix.common.model.Identifier;
import org.emau.icmvc.ganimed.epix.common.model.IdentifierDomain;
import org.emau.icmvc.ganimed.epix.common.model.MPIRequest;
import org.emau.icmvc.ganimed.epix.common.model.MPIResponse;
import org.emau.icmvc.ganimed.epix.common.model.Patient;
import org.emau.icmvc.ganimed.epix.common.model.QueryResponseEntry;
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
import org.emau.icmvc.ganimed.epix.core.impl.EPIXFactoryImpl;
import org.emau.icmvc.ganimed.epix.core.impl.EPIXModelMapperImpl;
import org.emau.icmvc.ganimed.epix.core.impl.EPIXServiceImpl;
import org.emau.icmvc.ganimed.epix.core.internal.PersonPreprocessedCache;
import org.emau.icmvc.ganimed.epix.core.notifier.EPIXNotifierFactory;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.AccountDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.IDTypeDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.IdentifierDomainDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.PersonDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.PersonGroupDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.PersonGroupHistoryDAO;
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
import org.emau.icmvc.ganimed.epix.core.persistence.dao.impl.PersonGroupDAOImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.impl.PersonGroupHistoryDAOImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.impl.PersonHistoryDAOImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.impl.PersonLinkDAOImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.impl.PersonPreprocessedDAOImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.impl.ProjectDAOImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.impl.RoleDAOImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Account;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Person;
import org.emau.icmvc.ganimed.epix.core.persistence.model.PersonPreprocessed;
import org.emau.icmvc.ganimed.epix.core.report.impl.HtmlFileReportBuilder;
import org.junit.Before;
import org.junit.Test;

public class SolveCriticalMatchTest {
	
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
	
	@Before
	public void setUp() throws Exception {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("epix_new");
		em = emf.createEntityManager();
	}
	
	@Test
	public void testSolveCriticalMatchTest() throws MPIException  {
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
//			ContactPreprocessedDAO contactPreprocessedDAO = new ContactPreprocessedDAOImpl();
//			contactPreprocessedDAO.setEntityManager(em);
			
			PersonGroupDAO personGroupDAO = new PersonGroupDAOImpl();
			personGroupDAO.setEntityManager(em);
			PersonGroupHistoryDAO personGroupHistoryDAO = new PersonGroupHistoryDAOImpl();
			personGroupHistoryDAO.setEntityManager(em);
			
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
//			context.setContactPreprocessedDAO(contactPreprocessedDAO);
			context.setPersonGroupDAO(personGroupDAO);
			context.setPersonGroupHistoryDAO(personGroupHistoryDAO);
			
			Account account = accountDAO.getAccount("user", SHA.getSHA1("minca2011", null));
			
			context.setAccount(account);
			
			epixService = new EPIXServiceImpl(context, personPreprocessedCache, matchingConfiguration);
			
			epixService.setModelMapper(new EPIXModelMapperImpl());
			
			HtmlFileReportBuilder html =new HtmlFileReportBuilder();
			html.setReportActivation("false");
			html.setReportFile("/tmp/epix_dev.html");
			epixService.setReportBuilder(html);
	
			DeduplicationEngine<PersonPreprocessed> ded = new DeduplicationEngine<PersonPreprocessed>();
			//ded.setMatchingConfigFile("matching-config-2.0.4.xml");
			//ded.setPreprocessingStrategy(new CommonPreprocessor<PersonPreprocessed>());
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
		
//Firstly create two persons in database which are matching critical	
		IdentifierDomain idomain = new IdentifierDomain();		
		idomain.setName("TEST");
		//Safe system
		idomain.setOid("1.2.276.0.76.3.1.132.100.1.1.1.3.0.1.1");
		
		Patient patient1 = new Patient();
		patient1.setFirstName("Peter");
		patient1.setLastName("Meier");						
		patient1.setGender(Gender.M);
		patient1.setBirthDate(new Date(76, 8, 15));
		Identifier ident1 = new Identifier();
		ident1.setValue("HGW-5678901");
		ident1.setSendingApplication("MInCa");
		ident1.setIdentifierDomain(idomain);
		patient1.getIdentifiers().add(ident1);
		
		Patient patient2 = new Patient();
		patient2.setFirstName("Peter");
		patient2.setLastName("Mayer");						
		patient2.setGender(Gender.M);
		patient2.setBirthDate(new Date(67, 8, 15));
		Identifier ident2 = new Identifier();
		ident2.setValue("HGW-4567890");
		ident2.setSendingApplication("MInCa");
		ident2.setIdentifierDomain(idomain);
		patient1.getIdentifiers().add(ident2);
		
		MPIRequest request = new MPIRequest();
		request.setSource("GANI_MED");
		RequestEntry r1 = new RequestEntry();
		r1.setPerson(patient1);
		request.getRequestEntries().add(r1);
		RequestEntry r2 = new RequestEntry();
		r2.setPerson(patient2);
		request.getRequestEntries().add(r2);
		
		MPIResponse res = null;
		try {
			em.getTransaction().begin();
			res = epixService.requestMPI(request);
			em.getTransaction().commit();
		} catch (Exception e) {
			em.getTransaction().rollback();
			e.printStackTrace();
			assertTrue(false);
			return;
		}
		
		Identifier mpiId1 = null;
		Identifier mpiId2 = null;
		
		List<ResponseEntry> rentries = res.getResponseEntries();
		if(rentries.get(0) instanceof QueryResponseEntry) {
			QueryResponseEntry qentry1 = (QueryResponseEntry) rentries.get(0);
			QueryResponseEntry qentry2 = (QueryResponseEntry) rentries.get(1);
			try {
			
			mpiId1 = qentry1.getFirstMpi();
			mpiId2 = qentry2.getFirstMpi();
			
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
			return;
		}
		}
		
		org.emau.icmvc.ganimed.epix.core.persistence.model.Identifier mpiIdent1 = epixService.getModelMapper().mapIdentifier(mpiId1);
		org.emau.icmvc.ganimed.epix.core.persistence.model.Identifier mpiIdent2 = epixService.getModelMapper().mapIdentifier(mpiId2);
		
		
		
		
// Second part will get both persons from persistence context and merges them together or in the other case declares them as different persons		
			
		PersonDAO personDAO = epixService.getContext().getPersonDAO();
		Person mergePerson1 = personDAO.getPersonByIdentifierAndDomain(mpiIdent1, mpiIdent1.getDomain());
		Person mergePerson2 = personDAO.getPersonByIdentifierAndDomain(mpiIdent1, mpiIdent2.getDomain());

		System.out.println("Person1: "+ mergePerson1.toString());
		System.out.println("Person2: "+ mergePerson2.toString());

		List<CriticalMatch> cricitcalMatches;
		
		cricitcalMatches = epixService.requestCriticalMatches();
		List<CriticalMatch> cricitcalMatches2 = epixService.requestLinkedPersons(cricitcalMatches.get(0).getDatabaseId());
		
				System.out.println("Personenlink #1 contains the following persons:");
				System.out.println("SrcPerson:  " + cricitcalMatches.get(0).getFirstName() + " -- " 
						+ cricitcalMatches.get(0).getName() + " -- " + cricitcalMatches.get(0).getBirthdate()
						+ " -- " + cricitcalMatches.get(0).getMpiid() + " -- ID:" 
						+ cricitcalMatches.get(0).getDatabaseId());
				System.out.println("DestPerson: " + cricitcalMatches2.get(0).getFirstName() + " -- " 
						+ cricitcalMatches2.get(0).getName() + " -- " + cricitcalMatches2.get(0).getBirthdate()
						+ " -- " + cricitcalMatches2.get(0).getMpiid() + " -- ID:" 
								+ cricitcalMatches2.get(0).getDatabaseId());
				System.out.println("Therethold is: " + cricitcalMatches2.get(0).getProbability());
				System.out.println("");
				
				
		epixService.assignIdentity(mergePerson1.getId().toString(), mergePerson2.getId().toString(), "Erfolgreich kontrolliert");				
	}

}
