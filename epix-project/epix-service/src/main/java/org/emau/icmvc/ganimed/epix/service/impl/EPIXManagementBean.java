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

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.emau.icmvc.ganimed.epix.common.MPIException;
import org.emau.icmvc.ganimed.epix.common.model.ErrorCode;
import org.emau.icmvc.ganimed.epix.common.model.IdentifierDomain;
import org.emau.icmvc.ganimed.epix.service.EPIXManagementService;
import org.jboss.ejb3.annotation.SecurityDomain;

@Stateless
@Remote(value = EPIXManagementService.class)
@SecurityDomain(value = "epix-security-domain")
public class EPIXManagementBean extends EPIXBean implements EPIXManagementService {

	private final Logger logger = Logger.getLogger(EPIXManagementService.class);

	@Override
	@RolesAllowed({ "admin" })
	public IdentifierDomain addIdentifierDomain(IdentifierDomain domain) throws MPIException {

		try {

			EPIXManagementService epixManagement = createEPIXManagement();

			logger.debug("Invoking addIdentifierDomain method...");
			domain = epixManagement.addIdentifierDomain(domain);
			logger.debug("addIdentifierDomain method executed...");
			return domain;
		} catch (MPIException e) {
			String message = ErrorCode.getMessage(e.getErrorCode());
			logger.error(message, e);
			throw e;
		}
	}

	@Override
	public IdentifierDomain getIdentifierDomainByInstitution(String name) throws MPIException {
		throw new UnsupportedOperationException();
	}

	@Override
	public IdentifierDomain getIdentifierDomainByOID(String oid) throws MPIException {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<IdentifierDomain> getAllIdentifierDomains() throws MPIException {
		throw new UnsupportedOperationException();
	}

}
