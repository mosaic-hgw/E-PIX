package org.emau.icmvc.ganimed.epix.client;

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

import javax.xml.ws.WebServiceException;

import org.apache.log4j.Logger;
import org.emau.icmvc.ganimed.epix.common.MPIException;
import org.emau.icmvc.ganimed.epix.common.model.CriticalMatch;
import org.emau.icmvc.ganimed.epix.common.model.ErrorCode;
import org.emau.icmvc.ganimed.epix.common.model.IdentifierDomain;
import org.emau.icmvc.ganimed.epix.common.model.MPIRequest;
import org.emau.icmvc.ganimed.epix.common.model.MPIResponse;
import org.emau.icmvc.ganimed.epix.pdqquery.model.SearchMask;
import org.emau.icmvc.ganimed.epix.service.EPIXService;
import org.emau.icmvc.ganimed.epix.ws.security.Account;

/**
 * 
 * @author Christian Schack
 * 
 */
public class EPIXClient {

	protected EPIXService epix = null;

	protected Logger logger = Logger.getLogger(EPIXClient.class);

	protected Account account = null;

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	private IdentifierDomain identifierDomain = null;

	/**
	 * FIXME Testcase: Invalid Domain FIXME Testcase: no connection to server
	 * 
	 * @param epixService
	 * @param identifierDomain
	 */
	public EPIXClient(EPIXService epixService, IdentifierDomain identifierDomain, Account account) {

		if (epixService == null) {
			throw new IllegalArgumentException("EPIXService must not be null.");
		}

		if (identifierDomain == null) {
			throw new IllegalArgumentException("IdentifierDomain must not be null.");
		}

		if (account == null) {
			throw new IllegalArgumentException("Account must not be null.");
		}

		this.identifierDomain = identifierDomain;
		this.epix = epixService;
		this.account = account;
	}

	/**
	 * 
	 * @param request
	 * @return
	 */
	public MPIResponse requestMPI(MPIRequest mpiRequest) throws MPIException {
		try {
			return epix.requestMPI(mpiRequest);
		} catch (WebServiceException e) {
			logger.error(e.getMessage(), e);
			throw new MPIException(ErrorCode.INTERNAL_ERROR, e);
		}
	}

	public MPIResponse requestMPINoPersist(MPIRequest mpiRequest) throws MPIException {
		try {
			return epix.requestMPINoPersist(mpiRequest);
		} catch (WebServiceException e) {
			logger.error(e.getMessage(), e);
			throw new MPIException(ErrorCode.INTERNAL_ERROR, e);
		}
	}

	/**
	 * 
	 * @param config
	 */
	public void setIdentifierDomain(IdentifierDomain identifierDomain) {
		this.identifierDomain = identifierDomain;
	}

	/**
	 * 
	 * @return
	 */
	public IdentifierDomain getIdentifierDomain() {
		return identifierDomain;
	}

	/**
	 * 
	 * @param searchMask
	 * @return
	 * @throws MPIException
	 */
	public MPIResponse findPersonByPDQ(SearchMask searchMask) throws MPIException {
		try {
			return epix.queryPersonByDemographicData(searchMask);
		} catch (WebServiceException e) {
			logger.error(e.getMessage(), e);
			throw new MPIException(ErrorCode.INTERNAL_ERROR, e);
		}
	}

	public MPIResponse updatePerson(MPIRequest mpiRequest) throws MPIException {
		try {
			return epix.updatePerson(mpiRequest);
		} catch (WebServiceException e) {
			logger.error(e.getMessage(), e);
			throw new MPIException(ErrorCode.INTERNAL_ERROR, e);
		}
	}

	public MPIResponse queryPersonByLocalIdentfier(String identifier, IdentifierDomain domain) throws MPIException {
		try {
			return epix.queryPersonByLocalIdentfier(identifier, domain);
		} catch (MPIException e) {
			logger.error(e.getMessage(), e);
			throw new MPIException(ErrorCode.INTERNAL_ERROR, e);
		}
	}

	public List<CriticalMatch> requestCriticalMatches() throws MPIException {
		try {
			return epix.requestCriticalMatches();
		} catch (MPIException e) {
			logger.error(e.getMessage(), e);
			throw new MPIException(ErrorCode.INTERNAL_ERROR, e);
		}
	}

	public List<CriticalMatch> requestLinkedPersons(String personId) throws MPIException {
		try {
			return epix.requestLinkedPersons(personId);
		} catch (MPIException e) {
			logger.error(e.getMessage(), e);
			throw new MPIException(ErrorCode.INTERNAL_ERROR, e);
		}
	}

	public void removeLinks(String personId, List<String> linkedPersonIds, String explanation) throws MPIException {
		try {
			epix.removeLinks(personId, linkedPersonIds, explanation);
		} catch (MPIException e) {
			logger.error(e.getMessage(), e);
			throw new MPIException(ErrorCode.INTERNAL_ERROR, e);
		}
	}

	public void removeLink(String personId, String linkedPersonId, String explanation) throws MPIException {
		try {
			epix.removeLink(personId, linkedPersonId, explanation);
		} catch (MPIException e) {
			logger.error(e.getMessage(), e);
			throw new MPIException(ErrorCode.INTERNAL_ERROR, e);
		}
	}

	public void assignIdentitiy(String personId, String aliasId, String explanation) throws MPIException {
		try {
			epix.assignIdentity(personId, aliasId, explanation);
		} catch (MPIException e) {
			logger.error(e.getMessage(), e);
			throw new MPIException(ErrorCode.INTERNAL_ERROR, e);
		}
	}

	public List<org.emau.icmvc.ganimed.epix.common.model.IdentifierDomain> getAllIdentifierDomains()
			throws MPIException {
		try {
			return epix.getAllIdentifierDomains();
		} catch (MPIException e) {
			logger.error(e.getMessage(), e);
			throw new MPIException(ErrorCode.INTERNAL_ERROR, e);
		}
	}

	public List<String> getAllMpiFromPersonByMpi(String mpiId) throws MPIException {
		try {
			return epix.getAllMpiFromPersonByMpi(mpiId);
		} catch (MPIException e) {
			logger.error(e.getMessage());
			throw new MPIException(ErrorCode.INTERNAL_ERROR, e);
		}
	}

}
