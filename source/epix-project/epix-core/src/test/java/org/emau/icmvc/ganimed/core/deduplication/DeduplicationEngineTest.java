package org.emau.icmvc.ganimed.core.deduplication;

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
import java.util.HashMap;
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
import org.emau.icmvc.ganimed.deduplication.model.DeduplicationResult;
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
import org.emau.icmvc.ganimed.epix.core.persistence.dao.impl.ProjectDAOImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.impl.RoleDAOImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Account;
import org.emau.icmvc.ganimed.epix.core.persistence.model.PatientPreprocessed;
import org.emau.icmvc.ganimed.epix.core.persistence.model.PersonPreprocessed;
import org.emau.icmvc.ganimed.epix.core.report.impl.HtmlFileReportBuilder;
import org.junit.Before;
import org.junit.Test;

//Not finished

public class DeduplicationEngineTest {

	private EntityManager em;
	private static final PersonPreprocessedCache<PersonPreprocessed> personPreprocessedCache = new PersonPreprocessedCache<PersonPreprocessed>();
	
	private static final Object emSynchronizerDummy = new Object();
	private static final Logger logger = Logger.getLogger(EPIXFactoryImpl.class);
	private Properties epixProperties = null;

	/**
	 * Cache initialization
	 * 
	 * @param matchingConfiguration
	 */
	private void initCache(MatchingConfiguration matchingConfiguration) {
		synchronized (emSynchronizerDummy) {
			synchronized (personPreprocessedCache) {
				if (!personPreprocessedCache.isInitialised()) {
					logger.info("initialise pp cache");
					personPreprocessedCache.init(em, matchingConfiguration);
					logger.info("pp cache initialised");
				}
			}
		}
	}

	@Before
	public void setUp() throws Exception {
		EntityManagerFactory emf = Persistence
				.createEntityManagerFactory("epix_test");
		em = emf.createEntityManager();
	}

	@Test
	public void testMPIRequest() {
		EPIXServiceImpl epixService = null;
		try {

			epixProperties = new Properties();
			XMLBindingUtil binder = new XMLBindingUtil();
			MatchingConfiguration matchingConfiguration = null;
			matchingConfiguration = binder.load(MatchingConfiguration.class,epixProperties.getProperty("deduplicationEngine.matchingConfigFile"));
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

			IDTypeDAO idType = new IDTypeDAOImpl();
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

			Account account = accountDAO.getAccount("minca", SHA.getSHA1("minca2011", null));

			context.setAccount(account);

			epixService = new EPIXServiceImpl(context, personPreprocessedCache, matchingConfiguration);

			epixService.setModelMapper(new EPIXModelMapperImpl());

			HtmlFileReportBuilder html = new HtmlFileReportBuilder();
			html.setReportActivation("false");
			html.setReportFile("/tmp/epix_dev.html");
			epixService.setReportBuilder(html);

			DeduplicationEngine<PersonPreprocessed> ded = new DeduplicationEngine<PersonPreprocessed>();
			//ded.setMatchingConfigFile("matching-config-1.5.0.xml");
			//ded.setPreprocessingStrategy(new CommonPreprocessor<Person>());
			ded.setValidateRequiredStrategy(new CommonValidateRequiredStrategy<PersonPreprocessed>());
			ded.setMatchCandidateLoader(new CommonMatchCandidateLoader<PersonPreprocessed>(personPreprocessedCache));
			//NEW for perfectMatch
			ded.setPerfectMatchStrategy(new CommonPerfectMatchStrategy<PersonPreprocessed>());
			ded.setPerfectMatchProzessor(new CommonPerfectMatchProcessor<PersonPreprocessed>(personPreprocessedCache));		
			
			ded.init(matchingConfiguration);

			epixService.setDeduplicationEngine(ded);

			Map<String, MPIGenerator> map = new HashMap<String, MPIGenerator>();
			map.put("EAN13", new MPIGenerator(new EAN13Generator(context)));

			epixService.setGenerators(map);

			org.emau.icmvc.ganimed.epix.core.persistence.model.PersonPreprocessed testPerson = createPerson();
			DeduplicationResult<PersonPreprocessed> dedResult = ded.match(testPerson);
			System.out.println("!!! Test if there are matches !!!");
			if (dedResult.isNoMatchable()) {
				System.out
						.println("Person is not matchable with deduplication engine");
			} else if (dedResult.hasNonMatches()) {
				System.out
						.println("There are no matches to this person. Create a new entry");
			} else if (dedResult.hasMultibleMatches()) {
				System.out
						.println("The person has multiple matches. Create a new entry and linkedPerson entries");
			} else if (dedResult.hasUniqueMatch()) {
				System.out
						.println("Found unique match for the person. Add possible new local identifiers to matched person");
			}
			System.out.println("!!! End of test for matches !!!");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private org.emau.icmvc.ganimed.epix.core.persistence.model.PersonPreprocessed createPerson() {
		PatientPreprocessed p = new PatientPreprocessed();
		p.setFirstName("Frank");
		p.setLastName("Tester");
		p.setGender("M");

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			java.sql.Date date = new java.sql.Date(format.parse("1999-11-21")
					.getTime());
			p.setBirthDate(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return p;
	}
}
