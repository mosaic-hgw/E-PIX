package org.emau.icmvc.ganimed.epix.service.impl;

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

import java.security.Principal;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.emau.icmvc.ganimed.epix.common.MPIException;
import org.emau.icmvc.ganimed.epix.common.model.ErrorCode;
import org.emau.icmvc.ganimed.epix.core.impl.EPIXFactoryImpl;
import org.emau.icmvc.ganimed.epix.service.EPIXManagementService;
import org.emau.icmvc.ganimed.epix.service.EPIXService;

/**
 * 
 * @author Christian Schack
 * 
 */
public abstract class EPIXBean {

	protected static final Logger logger = Logger.getLogger(EPIXBean.class);

	@PersistenceContext(unitName = "epix")
	private EntityManager em;

	@Resource
	protected SessionContext ctx;

	private static volatile EPIXFactoryImpl epixFactoryImpl = null;

	private static final Object synchronizerDummy = new Object();

	private static volatile Boolean initAccessed = false;

	/**
	 * Default constructor.
	 */
	public EPIXBean() {
	}

	protected EPIXService createEPIXService() throws MPIException {
		Principal principal = ctx.getCallerPrincipal();
		logger.info("User: " + principal.getName() + " invoked EPIX.");

		EPIXService epix = getEpixFactoryImpl().createEPIXService(principal);
		logger.info("EPIXService initialized...");

		return epix;
	}

	protected EPIXManagementService createEPIXManagement() throws MPIException {
		Principal principal = ctx.getCallerPrincipal();
		logger.info("User: " + principal.getName() + " invoked EPIXManagement.");

		EPIXManagementService epixManagement = getEpixFactoryImpl().createEPIXManagementService(principal);
		logger.info("EPIXManagementService initialized...");

		return epixManagement;
	}

	protected void init() throws MPIException {
		getEpixFactoryImpl();
	}

	private EPIXFactoryImpl getEpixFactoryImpl() throws MPIException {
		synchronized (initAccessed) {
			if (epixFactoryImpl == null && initAccessed) {
				throw new MPIException(ErrorCode.MPI_INITIALISING);
			}
			initAccessed = true;
		}

		synchronized (synchronizerDummy) {
			if (epixFactoryImpl == null) {
				epixFactoryImpl = new EPIXFactoryImpl(em);
				epixFactoryImpl.setSafeSource();
			}
		}
		return epixFactoryImpl;
	}
}
