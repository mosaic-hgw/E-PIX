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

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.emau.icmvc.ganimed.epix.common.MPIException;
import org.emau.icmvc.ganimed.epix.common.model.MPIRequest;
import org.emau.icmvc.ganimed.epix.common.model.MPIResponse;
import org.emau.icmvc.ganimed.epix.service.EPIXService;



@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class EPIXEmbeddedBean {

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public MPIResponse requestMPI(MPIRequest mpiRequest, EPIXService epixService) throws MPIException {
		return epixService.requestMPI(mpiRequest);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public MPIResponse updatePerson(MPIRequest mpiRequest, EPIXService epixService) throws MPIException {
		return epixService.updatePerson(mpiRequest);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void removeLinks(String personId, List<String> linkedPersonIds, EPIXService epixService, String explanation) throws MPIException {
		epixService.removeLinks(personId, linkedPersonIds, explanation);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void removeLink(String personId, String linkedPersonId, EPIXService epixService, String explanation) throws MPIException {
		epixService.removeLink(personId, linkedPersonId, explanation);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void assignIdentity(String personId, String aliasId, EPIXService epixService, String explanation) throws MPIException {
		epixService.assignIdentity(personId, aliasId, explanation);
	}
	
}
