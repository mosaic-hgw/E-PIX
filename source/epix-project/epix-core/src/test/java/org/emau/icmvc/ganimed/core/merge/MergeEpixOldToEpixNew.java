package org.emau.icmvc.ganimed.core.merge;

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

import java.sql.Timestamp;
import java.util.ArrayList;
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
import org.emau.icmvc.ganimed.epix.common.model.IdentifierDomain;
import org.emau.icmvc.ganimed.epix.common.model.MPIRequest;
import org.emau.icmvc.ganimed.epix.common.model.MPIResponse;
import org.emau.icmvc.ganimed.epix.common.model.RequestEntry;
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
import org.emau.icmvc.ganimed.epix.core.persistence.model.Identifier;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Person;
import org.emau.icmvc.ganimed.epix.core.persistence.model.PersonPreprocessed;
import org.emau.icmvc.ganimed.epix.core.report.impl.HtmlFileReportBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MergeEpixOldToEpixNew {

	private EntityManager em_old;
	private EntityManager em_new;
	private final Logger logger = Logger.getLogger(MergeEpixOldToEpixNew.class);
	private static final PersonPreprocessedCache<PersonPreprocessed> personPreprocessedCache = new PersonPreprocessedCache<PersonPreprocessed>();
	private static final Object emSynchronizerDummy = new Object();
	private static final Logger logger2 = Logger.getLogger(EPIXFactoryImpl.class);
	private Properties epixProperties = null;
	/**
	 * Cache initialization
	 * @param matchingConfiguration 
	 */
	private void initCache(MatchingConfiguration matchingConfiguration) {
		synchronized (emSynchronizerDummy) {
			synchronized (personPreprocessedCache) {
				if (!personPreprocessedCache.isInitialised()) {
					logger2.info("initialise pp cache");
					personPreprocessedCache.init(em_new,matchingConfiguration);
					logger2.info("pp cache initialised");
				}
			}
		}
	}

	@Before
	public void setUp() throws Exception {
		
		EntityManagerFactory emf_new = Persistence
				.createEntityManagerFactory("epix_new");
		em_new = emf_new.createEntityManager();
		
		EntityManagerFactory emf_old = Persistence
				.createEntityManagerFactory("epix_old");
		em_old = emf_old.createEntityManager();
	}

	@Test
	public void testImportEpixProdTest() {
		EPIXServiceImpl epixOldService = null;
		EPIXServiceImpl epixDevService = null;
		try {
			
			epixProperties = new Properties();
			XMLBindingUtil binder = new XMLBindingUtil();
			MatchingConfiguration matchingConfiguration =null;
			matchingConfiguration = binder.load(MatchingConfiguration.class, epixProperties.getProperty("deduplicationEngine.matchingConfigFile"));
			initCache(matchingConfiguration);	
			
			EPIXContext context_old = new EPIXContextImpl(new EPIXNotifierFactory());
			context_old.setEntityManager(em_old);
			PersonDAOImpl personDAO = new PersonDAOImpl();
			personDAO.setEntityManager(em_old);
			context_old.setPersonDAO(personDAO);
			
			epixOldService = new EPIXServiceImpl(context_old, personPreprocessedCache, matchingConfiguration);
			epixOldService.setModelMapper(new EPIXModelMapperImpl());
			HtmlFileReportBuilder html = new HtmlFileReportBuilder();
			html.setReportActivation("false");
			html.setReportFile("/tmp/epix_dev.html");
			epixOldService.setReportBuilder(html);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
			EPIXContext context = new EPIXContextImpl(new EPIXNotifierFactory());
			context.setEntityManager(em_new);
			
			AccountDAO accountDAO = new AccountDAOImpl();
			accountDAO.setEntityManager(em_new);
			ContactDAOImpl contactDAO = new ContactDAOImpl();
			contactDAO.setEntityManager(em_new);
			ContactHistoryDAOImpl contactHistoryDAO = new ContactHistoryDAOImpl();
			contactHistoryDAO.setEntityManager(em_new);
			
			IdentifierDAOImpl identifierDAO = new IdentifierDAOImpl();
			identifierDAO.setEntityManager(em_new);
			IdentifierDomainDAO domainDAO = new IdentifierDomainDAOImpl();
			domainDAO.setEntityManager(em_new);
			IdentifierHistoryDAOImpl identifierHistoryDAO = new IdentifierHistoryDAOImpl();
			identifierHistoryDAO.setEntityManager(em_new);
			
			IDTypeDAO idType =new IDTypeDAOImpl();
			idType.setEntityManager(em_new);
			PersonDAOImpl personDAO = new PersonDAOImpl();
			personDAO.setEntityManager(em_new);
			PersonHistoryDAOImpl personHistoryDAO = new PersonHistoryDAOImpl();
			personHistoryDAO.setEntityManager(em_new);
			PersonLinkDAOImpl personLinkDAO = new PersonLinkDAOImpl();
			personLinkDAO.setEntityManager(em_new);
			ProjectDAO projectDAO = new ProjectDAOImpl();
			projectDAO.setEntityManager(em_new);
			RoleDAO roleDAO = new RoleDAOImpl();
			roleDAO.setEntityManager(em_new);
					
			PersonPreprocessedDAO personPreprocessedDAO = new PersonPreprocessedDAOImpl();
			personPreprocessedDAO.setEntityManager(em_new);
//			ContactPreprocessedDAO contactPreprocessedDAO = new ContactPreprocessedDAOImpl();
//			contactPreprocessedDAO.setEntityManager(em_new);
			
			PersonGroupDAO personGroupDAO = new PersonGroupDAOImpl();
			personGroupDAO.setEntityManager(em_new);
			PersonGroupHistoryDAO personGroupHistoryDAO = new PersonGroupHistoryDAOImpl();
			personGroupHistoryDAO.setEntityManager(em_new);
			
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
			
			Account account = null;
			try {
				account = accountDAO.getAccount("user", SHA.getSHA1("minca2011", null));
			} catch (MPIException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			context.setAccount(account);
			
			epixProperties = new Properties();
			XMLBindingUtil binder = new XMLBindingUtil();
			MatchingConfiguration matchingConfiguration =null;
			try{
				matchingConfiguration = binder.load(MatchingConfiguration.class, epixProperties.getProperty("deduplicationEngine.matchingConfigFile"));
				initCache(matchingConfiguration);
				} catch (Exception e) {
					e.printStackTrace();
				}
			
			epixDevService = new EPIXServiceImpl(context, personPreprocessedCache, matchingConfiguration);
			
			epixDevService.setModelMapper(new EPIXModelMapperImpl());
			
			HtmlFileReportBuilder html =new HtmlFileReportBuilder();
			html.setReportActivation("false");
			html.setReportFile("/tmp/epix_dev.html");
			epixDevService.setReportBuilder(html);
			
			DeduplicationEngine<PersonPreprocessed> ded = new DeduplicationEngine<PersonPreprocessed>();
			//ded.setMatchingConfigFile("matching-config-2.0.4.xml");
			//ded.setPreprocessingStrategy(new CommonPreprocessor<PersonPreprocessed>());
			ded.setValidateRequiredStrategy(new CommonValidateRequiredStrategy<PersonPreprocessed>());
			ded.setMatchCandidateLoader(new CommonMatchCandidateLoader<PersonPreprocessed>(personPreprocessedCache));
			//NEW for perfectMatch
			ded.setPerfectMatchStrategy(new CommonPerfectMatchStrategy<PersonPreprocessed>());
			ded.setPerfectMatchProzessor(new CommonPerfectMatchProcessor<PersonPreprocessed>(personPreprocessedCache));
			try {
				ded.init(matchingConfiguration);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			epixDevService.setDeduplicationEngine(ded);
			
			Map<String, MPIGenerator> map =new HashMap<String, MPIGenerator>();
			map.put("EAN13", new MPIGenerator(new EAN13Generator(context)));
			
			epixDevService.setGenerators(map);		
		
		List<Person> personList = new ArrayList<Person>();
		try {
			HashMap<String, String> mpiMap = new HashMap<String, String>();
			List<Identifier> identifierList = epixDevService.getContext().getIdentifierDAO().getAllIdentifierByDomain(
					context.getIdentifierDomainDAO().findMPIDomain(context.getProject()));
			for(Identifier ident : identifierList) {
				mpiMap.put(ident.getValue(), null);
			}
			List<Person> personL = epixOldService.getContext().getPersonDAO()
					.getAllPersons();
			//TODO Neuer Algorithmus zum Prüfen auf Vorhandensein für schrittweise Migration
//			for (int i = 0; i < personL.size(); i++) {
//				if (!mpiMap.containsKey(personL.get(i).getMpiid().getValue())) {
//					personList.add(personL.get(i));
//				}
//			}
			
//			for (int i = 0; i < 100; i++) {
//				if(!mpiMap.containsKey(personL.get(i).getMpiid().getValue())) {
//					personList.add(personL.get(i));
//				}
//			}
			
			logger.debug("Anzahl erhaltener neuer Personen: " + personList.size());
		} catch (MPIException e) {
			logger.debug("Error until getting all persons from database"
					+ e.getLocalizedMessage());
		}
		logger.debug("Getting process finished.");
		
		// MPIRequest mpiRequest = new MPIRequest();
		// org.emau.icmvc.ganimed.epix.common.model.IdentifierDomain idomain = new
		// org.emau.icmvc.ganimed.epix.common.model.IdentifierDomain();
		//

		// idomain.setName("ExctractionLayer");

		// idomain.setOid("1.2.276.0.76.3.1.132.1.1.1.3.2.2");

		// mpiRequest.setIdentifierDomain(idomain);

		//One request holds one person
//		try {
////			for (Person person : personList) {
//			
//				 for(int i=4015; i<4515; i++) {
//				 Person person = personList.get(i);				
//				
//				MPIRequest mpiRequest = new MPIRequest();
//				org.emau.icmvc.ganimed.epix.common.model.IdentifierDomain idomain = new org.emau.icmvc.ganimed.epix.common.model.IdentifierDomain();
//				idomain.setName("ExctractionLayer");
//				idomain.setOid("1.2.276.0.76.3.1.132.1.1.1.3.2.2");
//				mpiRequest.setIdentifierDomain(idomain);
//
//				org.emau.icmvc.ganimed.epix.common.model.Person modelPerson = epixDevService
//						.getModelMapper().mapOutputPerson(person);
//				for (Identifier ident : person.getLocalIdentifiers()) {
//					org.emau.icmvc.ganimed.epix.common.model.Identifier modelIdentifier = epixDevService
//							.getModelMapper().mapOutputIdentifier(ident);
//					modelPerson.getIdentifiers().add(modelIdentifier);
//				}
//				MPIID mpiid = person.getMpiid();
//				Identifier ident = buildIdentifier(mpiid.getValue(), person,
//						context.getIdentifierDomainDAO().findMPIDomain());
//				org.emau.icmvc.ganimed.epix.common.model.Identifier modelMPIIdentifier = epixDevService
//						.getModelMapper().mapOutputIdentifier(ident);
//				modelPerson.getIdentifiers().add(modelMPIIdentifier);
//				RequestEntry entry = new RequestEntry();
//				entry.setPerson(modelPerson);
//				mpiRequest.getRequestEntries().add(entry);
//
//				try {
//					em_new.getTransaction().begin();
//					epixDevService.requestMPI(mpiRequest);
//					em_new.getTransaction().commit();
//				} catch (Exception e) {
//					em_new.getTransaction().rollback();
//					e.printStackTrace();
//					// assertTrue(false);
//				}
//			}
//		} catch (MPIException e) {
//			logger.debug("Error while creating modelPerson and adding to mpiRequest "
//					+ e.getLocalizedMessage());
//			e.printStackTrace();
//		}
		
		//One request holds 100 persons (can be adjust free) (is faster than the first version)
		IdentifierDomain idomain = new IdentifierDomain();
		idomain.setName("ExctractionLayer");
		idomain.setOid("1.2.276.0.76.3.1.132.1.1.1.3.2.2");
//		
		for (int i = 0; i<personList.size(); i+=100) {
			MPIRequest mpiRequest = new MPIRequest();
			mpiRequest.setSource("GANI_MED");
			for(int p = i; p<i+100 && p<personList.size(); p++) {
				try {
					if(personList.get(p) != null) {
						org.emau.icmvc.ganimed.epix.common.model.Person modelPerson = epixDevService
								.getModelMapper().mapOutputPerson(personList.get(p));
						for (Identifier ident : personList.get(p).getLocalIdentifiers()) {
							org.emau.icmvc.ganimed.epix.common.model.Identifier modelIdentifier = epixDevService
									.getModelMapper().mapOutputIdentifier(ident);
							modelPerson.getIdentifiers().add(modelIdentifier);
						}
						//TODO Prüfen, ob erforderlich
//						Identifier ident = buildIdentifier(mpiid.getValue(), personList.get(p),
//								context.getIdentifierDomainDAO().findMPIDomain(context.getProject()));
//						org.emau.icmvc.ganimed.epix.common.model.Identifier modelMPIIdentifier = epixDevService
//								.getModelMapper().mapOutputIdentifier(ident);
//						modelPerson.getIdentifiers().add(modelMPIIdentifier);
//						modelPerson.setMpiid(epixDevService.getModelMapper().mapOutputMPIID(mpiid));
						RequestEntry entry = new RequestEntry();
						entry.setPerson(modelPerson);
						mpiRequest.getRequestEntries().add(entry);
					}
				} catch (Exception e) {
					logger.debug("Error while creating modelPerson and adding to mpiRequest "
							+ e.getLocalizedMessage());
					e.printStackTrace();
				}
			}
			try {
				em_new.getTransaction().begin();
				MPIResponse resp = epixDevService.requestMPI(mpiRequest);
				em_new.getTransaction().commit();
			} catch (Exception e) {
				em_new.getTransaction().rollback();
				e.printStackTrace();
				// assertTrue(false);
			}
		}
		

//		 try {
//		 em_new.getTransaction().begin();
//		 MPIResponse res = epixDevService.requestMPI(mpiRequest);
//		 em_new.getTransaction().commit();
//		 } catch (Exception e) {
//		 em_new.getTransaction().rollback();
//		 e.printStackTrace();
//		 // assertTrue(false);
//		 }
	}

	private Identifier buildIdentifier(String value, Person person,
			org.emau.icmvc.ganimed.epix.core.persistence.model.IdentifierDomain domain) {
		Identifier identifier = new Identifier();
		identifier.setValue(value);
		identifier.setDomain(domain);
		identifier.setEntryDate(new Timestamp(System.currentTimeMillis()));
		List<Person> list = new ArrayList<Person>();
		list.add(person);
		identifier.setPerson(list);
		return identifier;
	}
}
