package org.emau.icmvc.ganimed.ttp.proxy;

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

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.emau.icmvc.ganimed.epix.common.MPIException;
import org.emau.icmvc.ganimed.epix.common.model.ConfigurationContainer;
import org.emau.icmvc.ganimed.epix.common.model.CriticalMatch;
import org.emau.icmvc.ganimed.epix.common.model.Identifier;
import org.emau.icmvc.ganimed.epix.common.model.IdentifierDomain;
import org.emau.icmvc.ganimed.epix.common.model.MPIRequest;
import org.emau.icmvc.ganimed.epix.common.model.MPIResponse;
import org.emau.icmvc.ganimed.epix.common.model.Person;
import org.emau.icmvc.ganimed.epix.pdqquery.model.SearchMask;
import org.emau.icmvc.ganimed.epix.service.EPIXService;
import org.jboss.ejb3.annotation.SecurityDomain;

/**
 *
 * @author Peter Penndorf edited by bialkem 19.02.2014
 */
@Singleton(name = "ProxyBean")
@Startup
@SecurityDomain("epix-security-domain")
@PermitAll
public class MpiEjbProxy implements EPIXService {

	@EJB(lookup = "java:global/epix/epix-ejb/EPIXServiceBean!org.emau.icmvc.ganimed.epix.service.EPIXService")
	private EPIXService service;

	private ConfigurationContainer configuration;

	// @EJB(lookup = "java:global/epix/epix-ejb/EPIXConfigurationServiceBean!org.emau.icmvc.ganimed.epix.service.EPIXConfigurationService")
	// private EpixConfigurationService configService;

	@Override
	public MPIResponse queryPersonByLocalIdentfier(String identifier, IdentifierDomain domain) throws MPIException {
		return service.queryPersonByLocalIdentfier(identifier, domain);
	}

	@Override
	public MPIResponse updatePerson(MPIRequest toUpdate) throws MPIException {
		return service.updatePerson(toUpdate);
	}

	@Override
	public MPIResponse queryPersonByDemographicData(SearchMask searchMask) throws MPIException {
		return service.queryPersonByDemographicData(searchMask);
	}

	@Override
	public MPIResponse requestMPI(MPIRequest mpiRequest) throws MPIException {
		return service.requestMPI(mpiRequest);
	}

	@Override
	public MPIResponse requestMPINoPersist(MPIRequest mpiRequest) throws MPIException {
		return service.requestMPINoPersist(mpiRequest);
	}

	@Override
	public List<IdentifierDomain> getAllIdentifierDomains() throws MPIException {
		return service.getAllIdentifierDomains();
	}

	@Override
	public MPIResponse getLinkedPersons(Person person, IdentifierDomain domain) throws MPIException {
		return service.getLinkedPersons(person, domain);
	}

	/*
	 * @Override public MPIResponse mergePerson(MPIRequest firstPerson, MPIRequest secondPerson) throws MPIException { return service.mergePerson(firstPerson, secondPerson); }
	 */

	@Override
	public List<CriticalMatch> requestCriticalMatches() throws MPIException {
		return service.requestCriticalMatches();
	}

	@Override
	public List<CriticalMatch> requestLinkedPersons(String personId) throws MPIException {
		return service.requestLinkedPersons(personId);
	}

	@Override
	public void removeLinks(String personId, List<String> linkedPersonIds, String explanation) throws MPIException {
		service.removeLinks(personId, linkedPersonIds, explanation);
	}

	@Override
	public void removeLink(String personId, String linkedPersonId, String explanation) throws MPIException {
		service.removeLink(personId, linkedPersonId, explanation);
	}

	@Override
	public void assignIdentity(String personId, String aliasId, String explanation) throws MPIException {
		service.assignIdentity(personId, aliasId, explanation);
	}

	@Override
	public List<String> getAllMpiFromPersonByMpi(String mpiId) throws MPIException {
		return service.getAllMpiFromPersonByMpi(mpiId);
	}

	@Override
	public ConfigurationContainer getConfig() throws MPIException {
		if (this.configuration == null) {
			this.configuration = service.getConfig();
		}
		return configuration;
	}

	@Override
	public List<Person> searchPersonByPDQCache(SearchMask searchMask) throws MPIException
	{
		return service.searchPersonByPDQCache(searchMask);
	}

	@Override
	public void addLocalIdsToMpi(String mpiId, List<Identifier> localIds) throws MPIException {
		service.addLocalIdsToMpi(mpiId, localIds);
	}

	@Override
	public List<Person> getAllPersons() throws MPIException {
		return service.getAllPersons();
	}
	
	@Override
	public List<Person> getAllReferencePersons() throws MPIException {
		return service.getAllReferencePersons();
	}
}
