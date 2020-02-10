package org.emau.icmvc.ganimed.epix.core.persistence.dao.impl;

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

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;

import org.emau.icmvc.ganimed.epix.common.MPIException;
import org.emau.icmvc.ganimed.epix.common.model.ErrorCode;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.RoleDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Role;
/**
 * 
 * @author schackc
 *
 */
public class RoleDAOImpl extends RoleDAO {

	public RoleDAOImpl() {}

	@Override
	public Role addRole(Role role) throws MPIException {	
		
		if (role == null) {
			throw new IllegalArgumentException("Role must not be null!");
		}
		
		Role check = getRoleByName(role.getName());
		
		if (check != null) {
			throw new MPIException(ErrorCode.ROLE_ALREADY_EXISTS);
		}
		
		try {
			em.persist(role);
			return role;
		} catch (PersistenceException e) {
			throw new MPIException(ErrorCode.PERSISTENCE_ERROR, e.getLocalizedMessage(), e);
		}
	}

	@Override
	public List<Role> getAllRoles() throws MPIException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Role getRoleByName(String name) throws MPIException {
		try {
			Role r = (Role)em.createQuery("FROM Role r WHERE r.name = :name")
			          			.setParameter("name", name)
			          			.getSingleResult();						
			return r;
		}catch (NoResultException noe) {
			return null;
		} catch (PersistenceException e) {
			throw new MPIException(ErrorCode.PERSISTENCE_ERROR, e.getLocalizedMessage(), e);
		}	
	}

}
