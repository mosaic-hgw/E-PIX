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

import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;
import org.emau.icmvc.ganimed.deduplication.config.model.MatchingConfiguration;
import org.emau.icmvc.ganimed.epix.common.MPIException;
import org.emau.icmvc.ganimed.epix.common.model.MPIResponse;
import org.emau.icmvc.ganimed.epix.common.model.ResponseEntry;
import org.emau.icmvc.ganimed.epix.common.utils.XMLBindingUtil;
import org.emau.icmvc.ganimed.epix.core.EPIXContext;
import org.emau.icmvc.ganimed.epix.core.impl.EPIXContextImpl;
import org.emau.icmvc.ganimed.epix.core.impl.EPIXFactoryImpl;
import org.emau.icmvc.ganimed.epix.core.impl.EPIXModelMapperImpl;
import org.emau.icmvc.ganimed.epix.core.impl.EPIXServiceImpl;
import org.emau.icmvc.ganimed.epix.core.internal.PersonPreprocessedCache;
import org.emau.icmvc.ganimed.epix.core.notifier.EPIXNotifierFactory;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.PersonLinkDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.impl.PersonDAOImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.impl.PersonLinkDAOImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.model.PersonPreprocessed;
import org.emau.icmvc.ganimed.epix.core.report.impl.HtmlFileReportBuilder;
import org.emau.icmvc.ganimed.epix.pdqquery.model.SearchMask;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FindPersonByPDQTest {

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
	public void testFindPersonByPDQTest() throws MPIException  {

		EPIXServiceImpl epixService=null;
		try
		{
			epixProperties = new Properties();
			XMLBindingUtil binder = new XMLBindingUtil();
			MatchingConfiguration matchingConfiguration =null;
			matchingConfiguration = binder.load(MatchingConfiguration.class, epixProperties.getProperty("deduplicationEngine.matchingConfigFile"));
			initCache(matchingConfiguration);

			EPIXContext context = new EPIXContextImpl(new EPIXNotifierFactory());
			context.setEntityManager(em);

			PersonDAOImpl personDAO = new PersonDAOImpl();
			personDAO.setEntityManager(em);
			PersonLinkDAO personLinkDAO = new PersonLinkDAOImpl();
			personLinkDAO.setEntityManager(em);

			context.setPersonDAO(personDAO);
			context.setPersonLinkDAO(personLinkDAO);

			epixService = new EPIXServiceImpl(context, personPreprocessedCache, matchingConfiguration);

			epixService.setModelMapper(new EPIXModelMapperImpl());

			HtmlFileReportBuilder html =new HtmlFileReportBuilder();
			html.setReportActivation("false");
			html.setReportFile("/tmp/epix_dev.html");
			epixService.setReportBuilder(html);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

		SearchMask searchMask = new SearchMask();

		searchMask.setFirstName("");
		searchMask.setLastName("Mayer");
		searchMask.setBirthDay("");
		searchMask.setBirthMonth("");
		searchMask.setBirthYear("");
		searchMask.setMpiid("");
		searchMask.setIdentifier("");
		searchMask.setIdentifierDomain("");

		try
		{
			MPIResponse response = epixService.queryPersonByDemographicData(searchMask);
			System.out.println("Erhaltene Personenliste:");
			for(ResponseEntry entry : response.getResponseEntries()) {
				System.out.println(entry.getPerson().getFirstName() + " -- " + entry.getPerson().getLastName() + " -- " +
						entry.getPerson().getBirthDate() + " -- " + entry.getPerson().getMpiid().getValue()
						+ " -- " + entry.getPerson().getIdentifiers());
			}
		}

		catch (Exception e)
		{
			System.out.println(e);
		}

//		String birthDay = "1";
//		String birthMonth = "";
//		String birthYear = "";
//
//		try {
//			XMLGregorianCalendar date = DatatypeFactory.newInstance().newXMLGregorianCalendar();
//			if(birthDay == null || birthDay.equals("")) {
//			} else {
//				int day = Integer.parseInt(birthDay);
//				date.setDay(day);
//			}
//			if(birthMonth == null || birthMonth.equals("")) {
//			} else {
//				int month = Integer.parseInt(birthMonth);
//				date.setMonth(month);
//			}
//			if(birthYear == null || birthYear.equals("")) {
//			} else {
//				int year = Integer.parseInt(birthYear);
//				date.setYear(year);
//			}
//			if(
//					((birthDay == null || birthDay.equals("")) &&
//					(birthMonth == null || birthMonth.equals("")) &&
//					(birthYear == null || birthYear.equals(""))) ||
//
//					((birthDay != null && !birthDay.equals("")) &&
//					(birthMonth == null || birthMonth.equals("")) &&
//					(birthYear != null && !birthYear.equals("")))) {
//				System.out.println("%");
//			} else {
//				System.out.println(date.toString().replace("--", ""));
//			}
//		} catch (DatatypeConfigurationException e1) {
//			e1.printStackTrace();
//		}
	}

}
