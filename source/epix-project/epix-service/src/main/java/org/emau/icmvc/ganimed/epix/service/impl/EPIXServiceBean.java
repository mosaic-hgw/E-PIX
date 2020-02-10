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
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.WebServiceException;

import org.apache.log4j.Logger;
import org.emau.icmvc.ganimed.epix.common.MPIException;
import org.emau.icmvc.ganimed.epix.common.model.ConfigurationContainer;
import org.emau.icmvc.ganimed.epix.common.model.CriticalMatch;
import org.emau.icmvc.ganimed.epix.common.model.ErrorCode;
import org.emau.icmvc.ganimed.epix.common.model.Identifier;
import org.emau.icmvc.ganimed.epix.common.model.IdentifierDomain;
import org.emau.icmvc.ganimed.epix.common.model.MPIRequest;
import org.emau.icmvc.ganimed.epix.common.model.MPIResponse;
import org.emau.icmvc.ganimed.epix.common.model.Person;
import org.emau.icmvc.ganimed.epix.pdqquery.model.SearchMask;
import org.emau.icmvc.ganimed.epix.service.EPIXService;
import org.jboss.ejb3.annotation.SecurityDomain;
// jboss eap 6.4 / wildfly 9
import org.jboss.ws.api.annotation.WebContext;

/**
 * Session Bean implementation class EPIXService
 */
@Stateless
@Remote(value = EPIXService.class)
@TransactionManagement(TransactionManagementType.CONTAINER)
@WebService(endpointInterface = "org.emau.icmvc.ganimed.epix.service.EPIXService")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT)
// jboss eap 6.4 / wildfly 9
@WebContext(contextRoot = "/epix", urlPattern = "/*", authMethod = "BASIC", transportGuarantee = "NONE", secureWSDLAccess = false)
// jboss eap 6.1 / wildfly 7
//@org.jboss.wsf.spi.annotation.WebContext(contextRoot = "/epix", urlPattern = "/*", authMethod = "BASIC", transportGuarantee = "NONE", secureWSDLAccess = false)
@SecurityDomain(value = "epix-security-domain")
public class EPIXServiceBean extends EPIXBean implements EPIXService {

	private final Logger logger = Logger.getLogger(EPIXServiceBean.class);

	private static final Object lock = new Object();

	@EJB
	private EPIXEmbeddedBean embeddedBean;

	/**
	 * Default constructor.
	 */
	public EPIXServiceBean() {
	}

	@Override
	@RolesAllowed({ "user" })
	public MPIResponse requestMPI(MPIRequest mpiRequest) throws WebServiceException {
		synchronized (lock) {
			try {
				EPIXService epixService = createEPIXService();
				MPIResponse response = new MPIResponse();
				logger.info("Invoking requestMPI Operation...");
				response = embeddedBean.requestMPI(mpiRequest, epixService);
				logger.info("RequestMPI Operation executed...");
				return response;
			} catch (MPIException e) {
				String message = ErrorCode.getMessage(e.getErrorCode());
				logger.error(message, e);
				throw new WebServiceException(message, e);
			}
		}
	}

	@Override
	@RolesAllowed({ "user" })
	public MPIResponse requestMPINoPersist(MPIRequest mpiRequest) throws WebServiceException {
		try {
			EPIXService epixService = createEPIXService();
			MPIResponse response = new MPIResponse();
			logger.info("Invoking requestMPINoPersist Operation...");
			response = epixService.requestMPINoPersist(mpiRequest);
			logger.info("RequestMPINoPersist Operation executed...");
			return response;
		} catch (MPIException e) {
			String message = ErrorCode.getMessage(e.getErrorCode());
			logger.error(message, e);
			throw new WebServiceException(message, e);
		}
	}

	@Override
	@RolesAllowed({ "user" })
	public MPIResponse updatePerson(MPIRequest mpiRequest) throws WebServiceException {
		synchronized (lock) {
			try {
				EPIXService epixService = createEPIXService();
				logger.info("Invoking updatePerson Operation...");
				MPIResponse response = embeddedBean.updatePerson(mpiRequest, epixService);
				logger.info("UpdatePerson Operation executed...");
				return response != null ? response : new MPIResponse();
			} catch (MPIException e) {
				String message = ErrorCode.getMessage(e.getErrorCode());
				logger.error(message, e);
				throw new WebServiceException(message, e);
			}
		}
	}

	@Override
	@RolesAllowed({ "user" })
	public MPIResponse queryPersonByLocalIdentfier(String identifier, IdentifierDomain domain) throws WebServiceException {
		try {
			EPIXService epixService = createEPIXService();
			logger.info("Invoking queryPersonByLocalIdentfier Operation...");
			MPIResponse response = epixService.queryPersonByLocalIdentfier(identifier, domain);
			logger.info("queryPersonByLocalIdentfier Operation executed...");
			return response != null ? response : new MPIResponse();
		} catch (MPIException e) {
			String message = ErrorCode.getMessage(e.getErrorCode());
			logger.error(message, e);
			throw new WebServiceException(message, e);
		}
	}

	@Override
	public MPIResponse getLinkedPersons(Person person, IdentifierDomain domain) throws WebServiceException {
		// TODO Auto-generated method stub
		throw new WebServiceException(new UnsupportedOperationException());
	}

	@Override
	@RolesAllowed({ "user" })
	public List<org.emau.icmvc.ganimed.epix.common.model.IdentifierDomain> getAllIdentifierDomains() throws WebServiceException {
		try {
			EPIXService epixService = createEPIXService();
			logger.info("Invoking getAllIdentifierDomains operation...");
			List<org.emau.icmvc.ganimed.epix.common.model.IdentifierDomain> identifierDomains = epixService.getAllIdentifierDomains();
			logger.info("GetAllIdentifierDomains operation executed...");
			return identifierDomains;
		} catch (MPIException e) {
			String message = ErrorCode.getMessage(e.getErrorCode());
			logger.error(message, e);
			throw new WebServiceException(message, e);
		}
	}

	@Override
	@RolesAllowed({ "user" })
	public MPIResponse queryPersonByDemographicData(SearchMask searchMask) throws MPIException {
		try {
			EPIXService epixService = createEPIXService();
			MPIResponse response = new MPIResponse();
			logger.info("Invoking findPersonByPDQ Operation...");
			response = epixService.queryPersonByDemographicData(searchMask);
			logger.info("FindPersonByPDQ Operation executed...");
			logger.info("Found " + response.getResponseEntries().size() + " persons");
			return response;
		} catch (MPIException e) {
			String message = ErrorCode.getMessage(e.getErrorCode());
			logger.error(message, e);
			throw new WebServiceException(message, e);
		}
	}

	@Override
	@RolesAllowed({ "user" })
	public List<CriticalMatch> requestCriticalMatches() throws MPIException {
		try {
			EPIXService epixService = createEPIXService();
			logger.info("Invoking requestCriticalMatches Operation...");
			List<CriticalMatch> criticalMatches = epixService.requestCriticalMatches();
			logger.info("RequestCriticalMatches Operation executed...");
			return criticalMatches;
		} catch (MPIException e) {
			String message = ErrorCode.getMessage(e.getErrorCode());
			logger.error(message, e);
			throw new WebServiceException(message, e);
		}
	}

	@Override
	@RolesAllowed({ "user" })
	public List<CriticalMatch> requestLinkedPersons(String personId) throws MPIException {
		try {
			EPIXService epixService = createEPIXService();
			logger.info("Invoking requestLinkedPersons Operation...");
			List<CriticalMatch> criticalMatches = epixService.requestLinkedPersons(personId);
			logger.info("RequestLinkedPersons Operation executed...");
			return criticalMatches;
		} catch (MPIException e) {
			String message = ErrorCode.getMessage(e.getErrorCode());
			logger.error(message, e);
			throw new WebServiceException(message, e);
		}
	}

	@Override
	@RolesAllowed({ "user" })
	public void removeLinks(String personId, List<String> linkedPersonIds, String explanation) throws MPIException {
		synchronized (lock) {
			try {
				EPIXService epixService = createEPIXService();
				logger.info("Invoking removeLinks Operation...");
				embeddedBean.removeLinks(personId, linkedPersonIds, epixService, explanation);
				logger.info("RemoveLinks Operation executed...");
			} catch (MPIException e) {
				String message = ErrorCode.getMessage(e.getErrorCode());
				logger.error(message, e);
				throw new WebServiceException(message, e);
			}
		}
	}

	@Override
	@RolesAllowed({ "user" })
	public void removeLink(String personId, String linkedPersonId, String explanation) throws MPIException {
		synchronized (lock) {
			try {
				EPIXService epixService = createEPIXService();
				logger.info("Invoking removeLink Operation...");
				embeddedBean.removeLink(personId, linkedPersonId, epixService, explanation);
				logger.info("RemoveLink Operation executed...");
			} catch (MPIException e) {
				String message = ErrorCode.getMessage(e.getErrorCode());
				logger.error(message, e);
				throw new WebServiceException(message, e);
			}
		}
	}

	@Override
	@RolesAllowed({ "user" })
	public void assignIdentity(String personId, String aliasId, String explanation) throws MPIException {
		synchronized (lock) {
			try {
				EPIXService epixService = createEPIXService();
				logger.info("Invoking assignIdentity Operation...");
				embeddedBean.assignIdentity(personId, aliasId, epixService, explanation);
				logger.info("AssignIdentity Operation executed...");
			} catch (MPIException e) {
				String message = ErrorCode.getMessage(e.getErrorCode());
				logger.error(message, e);
				throw new WebServiceException(message, e);
			}
		}
	}

	@Override
	@RolesAllowed({ "user" })
	public List<String> getAllMpiFromPersonByMpi(String mpiId) throws MPIException {
		try {
			EPIXService epixService = createEPIXService();
			if (logger.isInfoEnabled()) {
				logger.info("Invoking getAllMpiFromPersonByMpi Operation...");
			}
			List<String> mpiList = epixService.getAllMpiFromPersonByMpi(mpiId);
			if (logger.isInfoEnabled()) {
				logger.info("GetAllMpiFromPersonByMpi Operation executed...");
			}
			return mpiList;
		} catch (MPIException e) {
			String message = ErrorCode.getMessage(e.getErrorCode());
			logger.error(message, e);
			throw new WebServiceException(message, e);
		}
	}
	/**
	 * @author weiherg
	 */
	@Override
	@RolesAllowed({ "user" })
	public ConfigurationContainer getConfig() {
		try {
			EPIXService epixService = createEPIXService();
			if (logger.isInfoEnabled()) {
				logger.info("Invoking getConfig Operation...");
			}
			ConfigurationContainer config = epixService.getConfig();
			if (logger.isInfoEnabled()) {
				logger.info("GetConfig Operation executed...");
			}
			return config;
		} catch (MPIException e) {
			String message = ErrorCode.getMessage(e.getErrorCode());
			logger.error(message, e);
			throw new WebServiceException(message, e);
		}
	}

	@Override
	@RolesAllowed({ "user" })
	public List<Person> searchPersonByPDQCache(SearchMask searchmask) throws MPIException
	{
		EPIXService service = createEPIXService();

		return service.searchPersonByPDQCache(searchmask);
	}

	@Override
	@RolesAllowed({ "user" })
	public void addLocalIdsToMpi(String mpiId, List<Identifier> localIds) throws MPIException {
		try {
			EPIXService epixService = createEPIXService();
			logger.info("Invoking addLocalIdsToMpi Operation...");
			epixService.addLocalIdsToMpi(mpiId, localIds);
			logger.info("addLocalIdsToMpi Operation executed...");
		} catch (MPIException e) {
			String message = "exception while adding local ids to mpiId: " + ErrorCode.getMessage(e.getErrorCode());
			logger.error(message, e);
			throw new WebServiceException(message, e);
		}
	}
	
	@Override
	@RolesAllowed({ "user" })
	public List<Person> getAllPersons() throws MPIException
	{
		EPIXService service = createEPIXService();
		return service.getAllPersons();
	}
	
	@Override
	@RolesAllowed({ "user" })
	public List<Person> getAllReferencePersons() throws MPIException
	{
		EPIXService service = createEPIXService();
		return service.getAllReferencePersons();
	}
}
