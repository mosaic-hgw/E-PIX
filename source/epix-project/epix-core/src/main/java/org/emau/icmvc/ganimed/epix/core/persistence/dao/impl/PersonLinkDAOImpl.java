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

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.emau.icmvc.ganimed.epix.common.MPIException;
import org.emau.icmvc.ganimed.epix.common.model.ErrorCode;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.PersonLinkDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Person;
import org.emau.icmvc.ganimed.epix.core.persistence.model.PersonLink;

/**
 * 
 * @author Christian Schack
 *
 */
public class PersonLinkDAOImpl extends PersonLinkDAO {
	
	@Override
	public List<PersonLink> getAllLinkedPersons() throws MPIException{
		try {
		Query q = em.createNamedQuery("PersonLink.getAll");
		@SuppressWarnings("unchecked")
		List <PersonLink> personLinks = (List<PersonLink>)q.getResultList();
		return personLinks;
		} catch (PersistenceException e) {
			logger.error(e);
			throw new MPIException(ErrorCode.PERSISTENCE_ERROR, e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void deletePersonLink(PersonLink personLink) throws MPIException {
		try {
			Query q = em.createNamedQuery("PersonLink.getByPersonID");
			q.setParameter("id1", personLink.getSrcPerson());
			q.setParameter("id2", personLink.getDestPerson());
			@SuppressWarnings("unchecked")
			List <PersonLink> links = (List<PersonLink>)q.getResultList();
			for(PersonLink link : links) {
				em.remove(link);
			}
		} catch (Exception e) {
			logger.error(e);
			throw new MPIException(ErrorCode.PERSISTENCE_ERROR, e.getLocalizedMessage(), e);
		}
	}
	
	@Override
	public void deletePersonLink(Person p1, Person p2) throws MPIException {
		try {
			Query q = em.createNamedQuery("PersonLink.getByPersonID");
			q.setParameter("id1", p1);
			q.setParameter("id2", p2);
			@SuppressWarnings("unchecked")
			List <PersonLink> links = (List<PersonLink>)q.getResultList();
			for(PersonLink link : links) {
				em.remove(link);
			}
		} catch (Exception e) {
			logger.error(e);
			throw new MPIException(ErrorCode.PERSISTENCE_ERROR, e.getLocalizedMessage(), e);
		}
	}
	
	@Override
	public List<PersonLink> getPersonLinkForPersonGroup(Person person) throws MPIException {
		try {
			Query q = em.createNamedQuery("PersonLink.getForPersonGroup");
			q.setParameter("pg", person.getPersonGroup());
			@SuppressWarnings("unchecked")
			List<PersonLink> links = (List<PersonLink>)q.getResultList();
			return links;
		} catch (Exception e) {
			logger.error(e);
			throw new MPIException(ErrorCode.PERSISTENCE_ERROR, e.getLocalizedMessage(), e);
		}
	}
	
	@Override
	public void deletePersonLinkByPersonGroups(Person person1, Person person2) throws MPIException {
		try {
			Query q = em.createNamedQuery("PersonLink.getByPersonGroups");
			q.setParameter("pg1", person1.getPersonGroup());
			q.setParameter("pg2", person2.getPersonGroup());
			@SuppressWarnings("unchecked")
			List <PersonLink> links = q.getResultList();
			for(PersonLink link : links) {
				em.remove(link);
			}
		} catch (Exception e) {
			logger.error(e);
			throw new MPIException(ErrorCode.PERSISTENCE_ERROR, e.getLocalizedMessage(), e);
		}
	}
	
	@Override
	public List<PersonLink> getPersonLinkByPersonGroups(Person person1, Person person2) throws MPIException {
		try {
			Query q = em.createNamedQuery("PersonLink.getByPersonGroups");
			q.setParameter("pg1", person1.getPersonGroup());
			q.setParameter("pg2", person2.getPersonGroup());
			@SuppressWarnings("unchecked")
			List <PersonLink> links = q.getResultList();
			return links;
		} catch (Exception e) {
			logger.error(e);
			throw new MPIException(ErrorCode.PERSISTENCE_ERROR, e.getLocalizedMessage(), e);
		}
	}

}
