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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManager;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.emau.icmvc.ganimed.deduplication.DeduplicationEngine;
import org.emau.icmvc.ganimed.deduplication.config.model.MatchingConfiguration;
import org.emau.icmvc.ganimed.deduplication.impl.CommonPerfectMatchStrategy;
import org.emau.icmvc.ganimed.deduplication.impl.CommonPreprocessor;
import org.emau.icmvc.ganimed.deduplication.impl.CommonValidateRequiredStrategy;
import org.emau.icmvc.ganimed.deduplication.model.DeduplicationResult;
import org.emau.icmvc.ganimed.deduplication.model.MatchCriteriaResult;
import org.emau.icmvc.ganimed.deduplication.model.MatchResult;
import org.emau.icmvc.ganimed.epix.common.utils.XMLBindingUtil;
import org.emau.icmvc.ganimed.epix.core.EPIXContext;
import org.emau.icmvc.ganimed.epix.core.deduplication.impl.CommonMatchCandidateLoader;
import org.emau.icmvc.ganimed.epix.core.deduplication.impl.CommonPerfectMatchProcessor;
import org.emau.icmvc.ganimed.epix.core.impl.EPIXContextImpl;
import org.emau.icmvc.ganimed.epix.core.impl.EPIXFactoryImpl;
import org.emau.icmvc.ganimed.epix.core.internal.PersonPreprocessedCache;
import org.emau.icmvc.ganimed.epix.core.notifier.EPIXNotifierFactory;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Patient;
import org.emau.icmvc.ganimed.epix.core.persistence.model.PatientPreprocessed;
import org.emau.icmvc.ganimed.epix.core.persistence.model.PersonPreprocessed;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MatchingTestWithPreprocessing extends TestCase {

	private EntityManager em;
	private DeduplicationEngine<PersonPreprocessed> ded = null;
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
	protected void setUp() throws Exception {
		
		epixProperties = new Properties();
		XMLBindingUtil binder = new XMLBindingUtil();
		MatchingConfiguration matchingConfiguration =null;
		matchingConfiguration = binder.load(MatchingConfiguration.class, epixProperties.getProperty("deduplicationEngine.matchingConfigFile"));
		initCache(matchingConfiguration);
		
		EPIXContext context = new EPIXContextImpl(new EPIXNotifierFactory());
		ded = new DeduplicationEngine<PersonPreprocessed>();
		//ded.setMatchingConfigFile("matching-config-2.2.1.xml");
		//ded.setPreprocessingStrategy(new CommonPreprocessor<PersonPreprocessed>());
		ded.setValidateRequiredStrategy(new CommonValidateRequiredStrategy<PersonPreprocessed>());
		ded.setMatchCandidateLoader(new CommonMatchCandidateLoader<PersonPreprocessed>(personPreprocessedCache));
		//NEW for perfectMatch
		ded.setPerfectMatchStrategy(new CommonPerfectMatchStrategy<PersonPreprocessed>());
		ded.setPerfectMatchProzessor(new CommonPerfectMatchProcessor<PersonPreprocessed>(personPreprocessedCache));		
		
		ded.init(matchingConfiguration);
	}

	@After
	protected void tearDown() throws Exception {
	}
	
	@Test
	public void testMatching() {
		
		Patient p1 = new Patient();
		p1.setFirstName("Peter");
		p1.setLastName("Meier");
		
//		p1.setGender("M");
//		p1.setNationality("10");	
//		p1.setValue1("1996");
//		p1.setValue3("A");
//		p1.setValue4("17489");
		
		p1.setBirthPlace("Greifswald");
		String pattern = "yyyy-MM-dd";
		try {
			p1.setBirthDate(new Date(new SimpleDateFormat(pattern).parse("1982-04-28").getTime()));
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			
		
		
		Patient p2 = new Patient();
		p2.setFirstName("Peter");
		p2.setLastName("Mayer");
		
//		p2.setGender("M");
//		p2.setNationality("10");	
//		p2.setValue1("1996");
//		p2.setValue3("A");
//		p2.setValue4("17480");
//		
//		p2.setBirthPlace("Greifswald");
		try {
			p2.setBirthDate(new Date(new SimpleDateFormat(pattern).parse("1982-04-28").getTime()));
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			
		try {
			PatientPreprocessed pp1 = new PatientPreprocessed(p1);
			PatientPreprocessed pp2 = new PatientPreprocessed(p2);
			
			DeduplicationResult<PersonPreprocessed> dr = ded.matchSameLocalIdentifierCandidate(pp1, pp2);
					
			List<MatchResult<PersonPreprocessed>> matchList = new ArrayList<MatchResult<PersonPreprocessed>>();
			matchList.addAll(dr.getMatches());
			matchList.addAll(dr.getCriticalMatches());
			
			System.out.println("======= Match Result =======");
			for (MatchResult<PersonPreprocessed> mr : matchList) {
				System.out.println("== Match attributes ==");
				for(MatchCriteriaResult mcr : mr.getMatches()) {
					System.out.println(mcr.getCriteria() + " " + mcr.getCandidateValue() + " vs. " + mcr.getToMatchValue() + " result: " + mcr.getProbability());
				}
				System.out.println("== NoMatch attributes ==");
				for(MatchCriteriaResult mcr : mr.getNoMatches()) {
					System.out.println(mcr.getCriteria() + " " + mcr.getCandidateValue() + " vs. " + mcr.getToMatchValue() + " result: " + mcr.getProbability());
				}
				System.out.println("--------------------------");
				System.out.println("Decision: " + mr.getDecision() + " with ratio: " + mr.getRatio());
			} 
			
			System.out.println("======= Contact Check =======");
//			System.out.println(matchList.get(0).getComparativeValue().getContactPreprocessed().toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
