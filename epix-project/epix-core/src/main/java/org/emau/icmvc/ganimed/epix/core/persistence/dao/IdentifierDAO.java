package org.emau.icmvc.ganimed.epix.core.persistence.dao;

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

import org.emau.icmvc.ganimed.epix.common.MPIException;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Identifier;
import org.emau.icmvc.ganimed.epix.core.persistence.model.IdentifierDomain;
import org.emau.icmvc.ganimed.epix.core.persistence.model.PersonGroup;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Project;

/**
 * @author schackc
 * @since 04.11.2010
 */
public abstract class IdentifierDAO extends GenericDAO {

	/**
	 * @param sf
	 */
	public IdentifierDAO() {
	}

	public abstract Identifier addIdentifier(Identifier identifier) throws MPIException;

	public abstract Identifier getIdentifierByValue(String value, IdentifierDomain domain) throws MPIException;

	public abstract List<Identifier> getAllIdentifierByDomain(IdentifierDomain id) throws MPIException;

	public abstract List<Identifier> getAllIdentifierByPersonId(Long id) throws MPIException;

	public abstract Identifier getIdentifierByIdentifierAndDomain(Identifier identifier, IdentifierDomain domain)
			throws MPIException;

	public abstract List<Identifier> getIdentifierByPersonGroup(PersonGroup personGroup) throws MPIException;

	public abstract List<Identifier> getIdentifierByPersonGroupAndDomain(PersonGroup personGroup,
			IdentifierDomain domain) throws MPIException;

	public abstract Identifier getlastMpiIdentifierByProject(Project project) throws MPIException;

}
