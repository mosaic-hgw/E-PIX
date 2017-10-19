package org.emau.icmvc.ganimed.core.ean13genarator;

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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;
import org.emau.icmvc.ganimed.deduplication.config.model.MatchingConfiguration;
import org.emau.icmvc.ganimed.epix.common.utils.XMLBindingUtil;
import org.emau.icmvc.ganimed.epix.core.EPIXContext;
import org.emau.icmvc.ganimed.epix.core.gen.MPIGenerator;
import org.emau.icmvc.ganimed.epix.core.gen.impl.EAN13Generator;
import org.emau.icmvc.ganimed.epix.core.impl.EPIXContextImpl;
import org.emau.icmvc.ganimed.epix.core.impl.EPIXFactoryImpl;
import org.emau.icmvc.ganimed.epix.core.impl.EPIXServiceImpl;
import org.emau.icmvc.ganimed.epix.core.internal.PersonPreprocessedCache;
import org.emau.icmvc.ganimed.epix.core.notifier.EPIXNotifierFactory;
import org.emau.icmvc.ganimed.epix.core.persistence.model.PersonPreprocessed;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CheckConsistenceTest {
	private EntityManager em;
	String mpiIdValue = "1001000001A28";
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
	public void testMPIRequest()  {
		
		EPIXServiceImpl epixService=null;
		
		try{
			
		epixProperties = new Properties();
		XMLBindingUtil binder = new XMLBindingUtil();
		MatchingConfiguration matchingConfiguration =null;
		matchingConfiguration = binder.load(MatchingConfiguration.class, epixProperties.getProperty("deduplicationEngine.matchingConfigFile"));
		initCache(matchingConfiguration);
		
		EPIXContext context = new EPIXContextImpl(new EPIXNotifierFactory());
		context.setEntityManager(em);
		
		epixService = new EPIXServiceImpl(context, personPreprocessedCache, matchingConfiguration);
		
		Map<String, MPIGenerator> map =new HashMap<String, MPIGenerator>();
		map.put("EAN13", new MPIGenerator(new EAN13Generator(context)));
		
		epixService.setGenerators(map);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
		MPIGenerator gen = epixService.getGenerators().get("EAN13");
		
		try {
			if(gen.checkConsistence(mpiIdValue)) {
				System.out.println("MPI-ID: " + mpiIdValue + " is conform");
			} else {
				System.out.println("MPI-ID: " + mpiIdValue + " is not conform");
			}
			
			String test = mpiIdValue.substring(4, mpiIdValue.length()-1);
			int neu = Integer.parseInt(test);
			System.out.println("Counter: " + neu);
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
