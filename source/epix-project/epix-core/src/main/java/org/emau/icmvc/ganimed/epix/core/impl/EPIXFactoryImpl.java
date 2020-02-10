package org.emau.icmvc.ganimed.epix.core.impl;

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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;
import org.emau.icmvc.ganimed.deduplication.DeduplicationEngine;
import org.emau.icmvc.ganimed.deduplication.config.model.MatchingConfiguration;
import org.emau.icmvc.ganimed.deduplication.impl.CommonPerfectMatchStrategy;
import org.emau.icmvc.ganimed.deduplication.impl.CommonValidateRequiredStrategy;
import org.emau.icmvc.ganimed.epix.common.MPIException;
import org.emau.icmvc.ganimed.epix.common.model.ErrorCode;
import org.emau.icmvc.ganimed.epix.common.notifier.NotifierFactory;
import org.emau.icmvc.ganimed.epix.common.utils.XMLBindingUtil;
import org.emau.icmvc.ganimed.epix.core.EPIX;
import org.emau.icmvc.ganimed.epix.core.EPIXContext;
import org.emau.icmvc.ganimed.epix.core.EPIXFactory;
import org.emau.icmvc.ganimed.epix.core.EPIXModelMapper;
import org.emau.icmvc.ganimed.epix.core.deduplication.impl.CommonMatchCandidateLoader;
import org.emau.icmvc.ganimed.epix.core.deduplication.impl.CommonPerfectMatchProcessor;
import org.emau.icmvc.ganimed.epix.core.gen.MPIGenerator;
import org.emau.icmvc.ganimed.epix.core.gen.MPIGeneratorStrategy;
import org.emau.icmvc.ganimed.epix.core.internal.PersonPreprocessedCache;
import org.emau.icmvc.ganimed.epix.core.notifier.EPIXNotifierFactory;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.AccountDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.SourceDAO;
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
import org.emau.icmvc.ganimed.epix.core.persistence.model.Source;
import org.emau.icmvc.ganimed.epix.core.report.impl.HtmlFileReportBuilder;

public class EPIXFactoryImpl extends EPIXFactory {

	private static final Logger logger = Logger.getLogger(EPIXFactoryImpl.class);

	private EPIX epixService = null;

	private EPIX epixManagementService = null;

	private Properties epixProperties = null;

	private static final Object emSynchronizerDummy = new Object();

	private static final PersonPreprocessedCache<PersonPreprocessed> personPreprocessedCache = new PersonPreprocessedCache<PersonPreprocessed>();

	public EPIXFactoryImpl(EntityManager em) throws MPIException {
		super(em);
		epixProperties = new Properties();
		try {
			InputStream stream2 = getClass().getResourceAsStream("/epix.properties");
			epixProperties.load(stream2);
			stream2.close();
		} catch (FileNotFoundException e) {
			logger.error("Configuration file epix.properties could not be found: " + e.getMessage());
			throw new MPIException(ErrorCode.NO_CONFIGURATION_FOR_EPIX);
		} catch (IOException e) {
			logger.error("Could not load Configuration file epix.properties: " + e.getMessage());
			throw new MPIException(ErrorCode.NO_CONFIGURATION_FOR_EPIX);
		}
		XMLBindingUtil binder = new XMLBindingUtil();
		MatchingConfiguration matchingConfiguration =null;
		try {
			matchingConfiguration = binder.load(MatchingConfiguration.class, epixProperties.getProperty("deduplicationEngine.matchingConfigFile"));
			initCache(matchingConfiguration);
			epixService = instantiateEpixService(matchingConfiguration);
			epixManagementService = instantiateEpixManagementService(matchingConfiguration);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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

	@Override
	public EPIX createEPIXService() throws MPIException {
		return epixService;
	}

	@Override
	public EPIX createEPIXManagementService() throws MPIException {
		return epixManagementService;
	}

	@Override
	protected void authorize(EPIXContext context, Principal principal) throws MPIException {
		try {
			AccountDAO accountDAO = context.getAccountDAO();
			Account account = accountDAO.getAccount(principal.getName());
			if (account == null) {
				logger.error("Username does not exist " + principal.getName());
				throw new MPIException(ErrorCode.USERNAME_NOT_EXIST);
			}
			context.setAccount(account);
		} catch (MPIException e) {
			logger.error(e.getLocalizedMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			throw new MPIException(ErrorCode.INTERNAL_ERROR, e);
		}
	}

	private EPIX instantiateEpixService(MatchingConfiguration matchingConfiguration) throws MPIException {
		// Create nessesary elements for service
		NotifierFactory notifierFactory = new EPIXNotifierFactory();
		EPIXContext context = instantiateEPIXContext(notifierFactory);
		HtmlFileReportBuilder htmlFileReportBuilder = instantiateHtmlFileReportBuilder();
		EPIXModelMapper modelMapper = new EPIXModelMapperImpl();
		MPIGenerator mpiGenerator = instantiateMPIGenerator(context);
		Map<String, MPIGenerator> generators = new HashMap<String, MPIGenerator>();
		String key = epixProperties.getProperty("MPIGenerator.Key");
		generators.put(key, mpiGenerator);
		DeduplicationEngine<PersonPreprocessed> deduplicationEngine = instantiateDeduplicationEngine(context,matchingConfiguration);
		// Create service and add elements
		EPIX epixService = new EPIXServiceImpl(context, personPreprocessedCache,matchingConfiguration);
		epixService.getContext().setEntityManager(em);
		epixService.setDeduplicationEngine(deduplicationEngine);
		epixService.setGenerators(generators);
		epixService.setModelMapper(modelMapper);
		epixService.setReportBuilder(htmlFileReportBuilder);
		return epixService;
	}

	private EPIX instantiateEpixManagementService(MatchingConfiguration matchingConfiguration) throws MPIException {
		// Create nessesary elements for service
		NotifierFactory notifierFactory = new EPIXNotifierFactory();
		EPIXContext context = instantiateEPIXContext(notifierFactory);
		EPIXModelMapper modelMapper = new EPIXModelMapperImpl();
		MPIGenerator mpiGenerator = instantiateMPIGenerator(context);
		Map<String, MPIGenerator> generators = new HashMap<String, MPIGenerator>();
		String key = epixProperties.getProperty("MPIGenerator.Key");
		generators.put(key, mpiGenerator);
		DeduplicationEngine<PersonPreprocessed> deduplicationEngine = instantiateDeduplicationEngine(context, matchingConfiguration);
		// Create service and add elements
		EPIX epixService = new EPIXManagementServiceImpl(context);
		epixService.getContext().setEntityManager(em);
		epixService.setDeduplicationEngine(deduplicationEngine);
		epixService.setGenerators(generators);
		epixService.setModelMapper(modelMapper);

		return epixManagementService;
	}

	private EPIXContext instantiateEPIXContext(NotifierFactory notifierFactory) {
		EPIXContext context = new EPIXContextImpl(notifierFactory);
		context.setAccountDAO(new AccountDAOImpl());
		context.setContactDAO(new ContactDAOImpl());
		context.setContactHistoryDAO(new ContactHistoryDAOImpl());
		context.setIdentifierDAO(new IdentifierDAOImpl());
		context.setIdentifierDomainDAO(new IdentifierDomainDAOImpl());
		context.setIdentifierHistoryDAO(new IdentifierHistoryDAOImpl());
		context.setIdTypeDAO(new IDTypeDAOImpl());
		context.setPersonDAO(new PersonDAOImpl());
		context.setPersonPreprocessedDAO(new PersonPreprocessedDAOImpl());
		context.setPersonGroupDAO(new PersonGroupDAOImpl());
		context.setPersonGroupHistoryDAO(new PersonGroupHistoryDAOImpl());
		context.setPersonHistoryDAO(new PersonHistoryDAOImpl());
		context.setPersonLinkDAO(new PersonLinkDAOImpl());
		context.setPersonLinkHistoryDAO(new PersonLinkHistoryDAOImpl());
		context.setProjectDAO(new ProjectDAOImpl());
		context.setSourceDAO(new SourceDAOImpl());
		return context;
	}

	private HtmlFileReportBuilder instantiateHtmlFileReportBuilder() {
		HtmlFileReportBuilder htmlFileReportBuilder = new HtmlFileReportBuilder();
		htmlFileReportBuilder.setReportFile(epixProperties.getProperty("reportBuilder.reportFile"));
		htmlFileReportBuilder.setReportActivation(epixProperties.getProperty("reportBuilder.reportActivation"));
		return htmlFileReportBuilder;
	}

	private MPIGenerator instantiateMPIGenerator(EPIXContext context) throws MPIException {
		MPIGeneratorStrategy mpiGeneratorStrategy = null;
		try {
			Constructor<?> generatorConstructor = Class.forName(epixProperties.getProperty("MPIGenerator.Strategy"))
					.getConstructor(EPIXContext.class);
			mpiGeneratorStrategy = (MPIGeneratorStrategy) generatorConstructor.newInstance(context);
		} catch (Exception e) {
			logger.error("Error while creating mpiGenerator: " + e.getMessage());
			throw new MPIException(ErrorCode.INTERNAL_ERROR, e);
		}
		MPIGenerator mpiGenerator = new MPIGenerator(mpiGeneratorStrategy);
		return mpiGenerator;
	}

	private DeduplicationEngine<PersonPreprocessed> instantiateDeduplicationEngine(EPIXContext context, MatchingConfiguration matchingConfiguration) throws MPIException {
		DeduplicationEngine<PersonPreprocessed> deduplicationEngine = new DeduplicationEngine<PersonPreprocessed>();
		deduplicationEngine.setMatchCandidateLoader(new CommonMatchCandidateLoader<PersonPreprocessed>(personPreprocessedCache));	
		deduplicationEngine.setValidateRequiredStrategy(new CommonValidateRequiredStrategy<PersonPreprocessed>());
		deduplicationEngine.setPerfectMatchProzessor(new CommonPerfectMatchProcessor<PersonPreprocessed>(personPreprocessedCache));
		deduplicationEngine.setPerfectMatchStrategy(new CommonPerfectMatchStrategy<PersonPreprocessed>());
		try {
			deduplicationEngine.init(matchingConfiguration);
		} catch (Exception e) {
			logger.error("Error while creating deduplicationEngine: " + e.getMessage());
			throw new MPIException(ErrorCode.INTERNAL_ERROR, e);
		}
		return deduplicationEngine;
	}

	public void setSafeSource() throws MPIException {
		if (epixProperties.getProperty("safe_source").equals("")) {
			epixService.getContext().setSafeSource(null);
		} else {
			SourceDAO sourceDAO = epixService.getContext().getSourceDAO();
			Source safeSource = sourceDAO.getSourceByValue(epixProperties.getProperty("safe_source"));
			epixService.getContext().setSafeSource(safeSource);
		}
	}
}
