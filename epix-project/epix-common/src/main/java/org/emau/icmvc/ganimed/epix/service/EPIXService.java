package org.emau.icmvc.ganimed.epix.service;

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

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.emau.icmvc.ganimed.epix.common.MPIException;
import org.emau.icmvc.ganimed.epix.common.model.ConfigurationContainer;
import org.emau.icmvc.ganimed.epix.common.model.CriticalMatch;
import org.emau.icmvc.ganimed.epix.common.model.Identifier;
import org.emau.icmvc.ganimed.epix.common.model.IdentifierDomain;
import org.emau.icmvc.ganimed.epix.common.model.MPIRequest;
import org.emau.icmvc.ganimed.epix.common.model.MPIResponse;
import org.emau.icmvc.ganimed.epix.common.model.Person;
import org.emau.icmvc.ganimed.epix.pdqquery.model.SearchMask;

/**
 *
 * @author Christian Schack
 *
 */

@WebService(targetNamespace = "http://service.epix.ganimed.icmvc.emau.org/", name = "EPIXService")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.WRAPPED, style = SOAPBinding.Style.DOCUMENT)
public interface EPIXService {

	@WebResult(name = "pixQueryResponse", partName = "mpiResponsePart", targetNamespace = "http://model.common.epix.ganimed.icmvc.emau.org/")
	@WebMethod(action = "queryPersonByLocalIdentifier")
	public MPIResponse queryPersonByLocalIdentfier(
			@WebParam(partName = "pixQueryParamPart", name = "pixIdentifier", targetNamespace = "http://model.common.epix.ganimed.icmvc.emau.org/") String identifier,
			@WebParam(partName = "pixQueryParamPart", name = "pixDomain", targetNamespace = "http://model.common.ganimed.icmvc.emau.org/") IdentifierDomain domain)
			throws MPIException;;

	@WebResult(name = "updateResponse", partName = "mpiResponsePart", targetNamespace = "http://model.common.epix.ganimed.icmvc.emau.org/")
	@WebMethod(action = "updatePerson")
	public MPIResponse updatePerson(
			@WebParam(partName = "updateParamPart", name = "toUpdate", targetNamespace = "http://model.common.epix.ganimed.icmvc.emau.org/") MPIRequest toUpdate)
			throws MPIException;

	@WebResult(name = "pdqQueryResponse", partName = "mpiResponsePart", targetNamespace = "http://model.common.epix.ganimed.icmvc.emau.org/")
	@WebMethod(action = "queryPersonByDemographicData")
	public MPIResponse queryPersonByDemographicData(
			@WebParam(partName = "pdqQueryParamPart", name = "searchMask", targetNamespace = "http://model.pdqquery.ganimed.icmvc.emau.org/") SearchMask searchMask)
			throws MPIException;

	@WebResult(name = "requestResponse", partName = "mpiResponsePart", targetNamespace = "http://model.common.epix.ganimed.icmvc.emau.org/")
	@WebMethod(action = "requestMPI")
	public MPIResponse requestMPI(
			@WebParam(partName = "requestMPIParamPart", name = "mpiRequest", targetNamespace = "http://model.common.epix.ganimed.icmvc.emau.org/") MPIRequest mpiRequest)
			throws MPIException;

	@WebResult(name = "requestResponse", partName = "mpiResponsePart", targetNamespace = "http://model.common.epix.ganimed.icmvc.emau.org/")
	@WebMethod(action = "requestMPINoPersist")
	public MPIResponse requestMPINoPersist(
			@WebParam(partName = "requestMPIParamPart", name = "mpiRequest", targetNamespace = "http://model.common.epix.ganimed.icmvc.emau.org/") MPIRequest mpiRequest)
			throws MPIException;

	@WebResult(name = "identifierDomainQueryResponse", partName = "mpiResponsePart", targetNamespace = "http://model.common.epix.ganimed.icmvc.emau.org/")
	@WebMethod(action = "getAllIdentifierDomains")
	public List<org.emau.icmvc.ganimed.epix.common.model.IdentifierDomain> getAllIdentifierDomains() throws MPIException;

	@WebResult(name = "linkedPersonQueryResponse", partName = "mpiResponsePart", targetNamespace = "http://model.common.epix.ganimed.icmvc.emau.org/")
	@WebMethod(action = "getLinkedPersons")
	public MPIResponse getLinkedPersons(
			@WebParam(partName = "linkedPersonQueryParamPart", name = "person", targetNamespace = "http://model.common.ganimed.icmvc.emau.org/") Person person,
			@WebParam(partName = "linkedPersonQueryParamPart", name = "linkedDomain", targetNamespace = "http://model.common.ganimed.icmvc.emau.org/") IdentifierDomain domain)
			throws MPIException;

	@WebResult(name = "criticalMatchesResponse", partName = "criticalMatchPart", targetNamespace = "http://model.common.epix.ganimed.icmvc.emau.org/")
	@WebMethod(action = "requestCriticalMatches")
	public List<CriticalMatch> requestCriticalMatches()
			throws MPIException;

	@WebResult(name = "linkedPersonsResponse", partName = "linkedPersonPart", targetNamespace = "http://model.common.epix.ganimed.icmvc.emau.org/")
	@WebMethod(action = "requestLinkedPersons")
	public List<CriticalMatch> requestLinkedPersons(
			@WebParam(partName = "requestLinkedPersonsPart", name = "personId", targetNamespace = "http://model.common.epix.ganimed.icmvc.emau.org/") String personId)
			throws MPIException;

	@WebMethod(action = "removeLinks")
	public void removeLinks(
			@WebParam(partName = "removeLinksPart", name = "personId", targetNamespace = "http://model.common.epix.ganimed.icmvc.emau.org/") String personId,
			@WebParam(partName = "removeLinksPart", name = "linkedPersonIds", targetNamespace = "http://model.common.epix.ganimed.icmvc.emau.org/") List<String> linkedPersonIds,
			@WebParam(partName = "removeLinksPart", name = "explanation", targetNamespace = "http://model.common.epix.ganimed.icmvc.emau.org/") String explanation)
			throws MPIException;

	@WebMethod(action = "removeLink")
	public void removeLink(
			@WebParam(partName = "removeLinkPart", name = "personId", targetNamespace = "http://model.common.epix.ganimed.icmvc.emau.org/") String personId,
			@WebParam(partName = "removeLinkPart", name = "linkedPersonId", targetNamespace = "http://model.common.epix.ganimed.icmvc.emau.org/") String linkedPersonId,
			@WebParam(partName = "removeLinkPart", name = "explanation", targetNamespace = "http://model.common.epix.ganimed.icmvc.emau.org/") String explanation)
			throws MPIException;

	@WebMethod(action = "assignIdentity")
	public void assignIdentity(
			@WebParam(partName = "assignIdentityPart", name = "personId", targetNamespace = "http://model.common.epix.ganimed.icmvc.emau.org/") String personId,
			@WebParam(partName = "assignIdentityPart", name = "aliasId", targetNamespace = "http://model.common.epix.ganimed.icmvc.emau.org/") String aliasId,
			@WebParam(partName = "assignIdentityPart", name = "explanation", targetNamespace = "http://model.common.epix.ganimed.icmvc.emau.org/") String explanation)
			throws MPIException;

	@WebResult(name = "getAllMpiFromPersonByMpiResponse", partName = "getAllMpiFromPersonByMpiPart", targetNamespace = "http://model.common.epix.ganimed.icmvc.emau.org/")
	@WebMethod(action = "getAllMpiFromPersonByMpi")
	public List<String> getAllMpiFromPersonByMpi(
			@WebParam(partName = "getAllMpiFromPersonByMpiPart", name = "mpiId", targetNamespace = "http://model.common.epix.ganimed.icmvc.emau.org/") String mpiId)
			throws MPIException;

	/**
	 * @author weiherg
	 */
	@WebMethod(action = "getConfig")
	public ConfigurationContainer getConfig() throws MPIException;

	/**
	 * @author Marko Witt
	 */
	@WebResult(name = "searchPersonByPDQCache", partName = "searchPersonByPDQCache", targetNamespace = "http://model.common.epix.ganimed.icmvc.emau.org/")
	@WebMethod(action = "searchPersonByPDQCache")
	public List<Person> searchPersonByPDQCache(
			@WebParam(partName = "pdqQueryParamPart", name = "searchMask", targetNamespace = "http://model.pdqquery.ganimed.icmvc.emau.org/") SearchMask searchMask) throws MPIException;


	public void addLocalIdsToMpi(
			@WebParam(partName = "addLokalIdsToMpi", name = "mpiId", targetNamespace = "http://model.common.epix.ganimed.icmvc.emau.org/") String mpiId,
			@WebParam(partName = "addLokalIdsToMpi", name = "localIds", targetNamespace = "http://model.common.epix.ganimed.icmvc.emau.org/") List<Identifier> localIds)
			throws MPIException;
	
	/**
	 * @author blumentritta
	 */
	@WebResult(name = "getAllPersons", partName = "getAllPersons", targetNamespace = "http://model.common.epix.ganimed.icmvc.emau.org/")
	@WebMethod(action = "getAllPersons")
	public List<Person> getAllPersons(
			) throws MPIException;
	
	/**
	 * @author blumentritta
	 */
	@WebResult(name = "getAllReferencePersons", partName = "getAllReferencePersons", targetNamespace = "http://model.common.epix.ganimed.icmvc.emau.org/")
	@WebMethod(action = "getAllReferencePersons")
	public List<Person> getAllReferencePersons(
			) throws MPIException;

}
