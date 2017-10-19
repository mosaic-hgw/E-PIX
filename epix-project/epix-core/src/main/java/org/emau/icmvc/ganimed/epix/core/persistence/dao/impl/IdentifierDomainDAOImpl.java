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

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.emau.icmvc.ganimed.epix.common.MPIException;
import org.emau.icmvc.ganimed.epix.common.model.ErrorCode;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.IdentifierDomainDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.model.IdentifierDomain;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Project;
import org.emau.icmvc.ganimed.epix.core.persistence.model.enums.IdentifierDomainTypeEnum;

/**
 *
 * @author Christian Schack
 * @since 08.11.2010
 */
public class IdentifierDomainDAOImpl extends IdentifierDomainDAO {

	public IdentifierDomainDAOImpl() {
	}

	@Override
	public IdentifierDomain addIdentifierDomain(IdentifierDomain domain) throws MPIException {

		if (domain == null) {
			throw new IllegalArgumentException("IdentifierDomain must not be null.");
		}

		if (domain.getName() == null || domain.getName().equals("")) {
			throw new IllegalArgumentException("Institution must not be null or empty.");
		}

		if (domain.getOid() == null || domain.getOid().equals("")) {
			throw new IllegalArgumentException("OID must not be null or empty.");
		}

		IdentifierDomain check = getIdentifierDomainByOID(domain.getOid());

		if (check != null) {
			throw new MPIException(ErrorCode.IDENTIFIERDOMAIN_ALREADY_EXISTS);
		}

		try {
			domain.setEntryDate(new Timestamp(System.currentTimeMillis()));
			em.persist(domain);
			return domain;
		} catch (PersistenceException e) {
			throw new MPIException(ErrorCode.PERSISTENCE_ERROR, e.getLocalizedMessage(), e);
		}
	}

	@Override
	public IdentifierDomain getIdentifierDomainByInstitution(String name) throws MPIException {
		try {
			IdentifierDomain id = (IdentifierDomain) em
					.createQuery("FROM IdentifierDomain domain " + "WHERE domain.name").setParameter(0, name)
					.getSingleResult();
			return id;
		} catch (NoResultException noe) {
			return null;
		} catch (PersistenceException e) {
			throw new MPIException(ErrorCode.PERSISTENCE_ERROR, e.getLocalizedMessage(), e);
		}
	}

	@Override
	public IdentifierDomain getIdentifierDomainByOID(String oid) throws MPIException {
		try {
			IdentifierDomain id = (IdentifierDomain) em
					.createQuery("FROM IdentifierDomain domain " + "WHERE domain.oid = :oid").setParameter("oid", oid)
					.getSingleResult();
			return id;
		} catch (NoResultException noe) {
			return null;
		} catch (PersistenceException e) {
			throw new MPIException(ErrorCode.PERSISTENCE_ERROR, e.getLocalizedMessage(), e);
		}
	}

	@Override
	public List<IdentifierDomain> getAllIdentifierDomains() throws MPIException {
		try {
			Query q = em.createNamedQuery("IdentifierDomain.getAllIdentifierDomains");
			@SuppressWarnings("unchecked")
			List<IdentifierDomain> id = q.getResultList();
			return id;
		} catch (Exception e) {
			throw new MPIException(ErrorCode.PERSISTENCE_ERROR, e.getLocalizedMessage(), e);
		}
	}

	@Override
	public IdentifierDomain findMPIDomain(Project project) throws MPIException {
		try {
			// TODO Abfrage auf Projekt ID anpassen
			IdentifierDomain id = (IdentifierDomain) em.createNamedQuery("IdentifierDomain.getMpiDomainByProject")
					.setParameter("project", project).setParameter("type", IdentifierDomainTypeEnum.MPI_DOMAIN)
					.getSingleResult();
			return id;
		} catch (NoResultException noe) {
			throw new MPIException(ErrorCode.NO_MPI_DOMAIN_AVAILABLE, noe.getLocalizedMessage(), noe);
		} catch (NonUniqueResultException nue) {
			throw new MPIException(ErrorCode.MULTI_MPI_DOMAIN_AVAILABLE, nue.getLocalizedMessage(), nue);
		} catch (Exception e) {
			throw new MPIException(ErrorCode.PERSISTENCE_ERROR, e.getLocalizedMessage(), e);
		}
	}

}
