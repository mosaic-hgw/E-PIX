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
import org.emau.icmvc.ganimed.epix.core.persistence.dao.IdentifierDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Identifier;
import org.emau.icmvc.ganimed.epix.core.persistence.model.IdentifierDomain;
import org.emau.icmvc.ganimed.epix.core.persistence.model.PersonGroup;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Project;

/**
 * 
 * @author Christian Schack
 * @since 08.11.2010
 */
public class IdentifierDAOImpl extends IdentifierDAO {

	public IdentifierDAOImpl() {
	}

	@Override
	public Identifier addIdentifier(Identifier identifier) throws MPIException {
		if (identifier == null) {
			throw new IllegalArgumentException("Argument must not be null.");
		}

		try {
			em.persist(identifier);
			logger.debug("New local identifier added: " + identifier);
			return identifier;
		} catch (PersistenceException e) {
			throw new MPIException(ErrorCode.PERSISTENCE_ERROR, e.getLocalizedMessage(), e);
		}
	}

	public Identifier getIdentifierByIdentifierAndDomain(Identifier identifier, IdentifierDomain domain)
			throws MPIException {
		try {
			Identifier i = (Identifier) em
					.createQuery("FROM Identifier i " + "WHERE i.value = :value AND i.domain.oid = :oid")
					.setParameter("value", identifier.getValue()).setParameter("oid", domain.getOid())
					.getSingleResult();
			return i;
		} catch (NoResultException noe) {
			return null;
		} catch (PersistenceException e) {
			throw new MPIException(ErrorCode.PERSISTENCE_ERROR, e.getLocalizedMessage(), e);
		}
	}

	@Override
	public Identifier getIdentifierByValue(String value, IdentifierDomain domain) throws MPIException {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Identifier> getAllIdentifierByDomain(IdentifierDomain id) throws MPIException {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Get all Identifiers for domain " + id + " from database");
			}
			@SuppressWarnings("unchecked")
			List<Identifier> idents = em.createNamedQuery("Identifier.findByDomainId").setParameter("oid", id.getOid())
					.getResultList();
			return idents;
		} catch (PersistenceException e) {
			logger.error(e);
			throw new MPIException(ErrorCode.PERSISTENCE_ERROR, e.getLocalizedMessage(), e);
		}
	}

	@Override
	public List<Identifier> getAllIdentifierByPersonId(Long id) throws MPIException {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Identifier> getIdentifierByPersonGroup(PersonGroup personGroup) throws MPIException {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Get all Identifiers for PersonGroup " + personGroup.getId() + " from database");
			}
			@SuppressWarnings("unchecked")
			List<Identifier> idents = em.createNamedQuery("Identifier.findByPersonGroup")
					.setParameter("pg", personGroup).getResultList();
			return idents;
		} catch (PersistenceException e) {
			logger.error(e);
			throw new MPIException(ErrorCode.PERSISTENCE_ERROR, e.getLocalizedMessage(), e);
		}
	}

	@Override
	public List<Identifier> getIdentifierByPersonGroupAndDomain(PersonGroup personGroup, IdentifierDomain domain)
			throws MPIException {
		try {
			if(logger.isDebugEnabled()) {
				logger.debug("Get all Identifiers for personGroup " + personGroup.getId() + " and identifierDomain " + domain.getName() + " from Database.");
			}
			@SuppressWarnings("unchecked")
			List<Identifier> idents = em.createNamedQuery("Identifier.findByPersonGroupAndDomain")
					.setParameter("pg", personGroup)
					.setParameter("domain", domain)
					.getResultList();
			return idents;
		} catch (PersistenceException e) {
			logger.error(e);
			throw new MPIException(ErrorCode.PERSISTENCE_ERROR, e.getLocalizedMessage(), e);
		}
	}
	@Override
	public Identifier getlastMpiIdentifierByProject(Project project) throws MPIException {
		if (project == null) {
			throw new MPIException(ErrorCode.NO_ATTRIBUTES_DEFINED);
		}
		try {
			Identifier identifier = (Identifier) em.createNamedQuery("Identifier.getLastMpiIdentifierByProject")
					.setParameter("project", project)
					.setMaxResults(1)
					.getSingleResult();
			return identifier;
		}  catch (NoResultException noe) {
			return null;
		} catch (PersistenceException e) {
			throw new MPIException(ErrorCode.PERSISTENCE_ERROR, e.getLocalizedMessage(), e);
		}
	}

}
