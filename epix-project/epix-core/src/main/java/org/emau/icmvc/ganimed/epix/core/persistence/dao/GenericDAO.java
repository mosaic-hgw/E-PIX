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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.apache.log4j.Logger;
import org.emau.icmvc.ganimed.epix.common.MPIException;
import org.emau.icmvc.ganimed.epix.common.model.ErrorCode;
import org.hibernate.HibernateException;

public abstract class GenericDAO {
	
	protected EntityManager em;
	
	protected Logger logger = Logger.getLogger(GenericDAO.class);
	
	public GenericDAO() {}
	

	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	
	public void deleteAll(String className) throws MPIException {
		try {
			@SuppressWarnings("rawtypes")
			List entries = getAllEntries(className);
			for (Object entry : entries) {
				em.remove(entry);
			}
		} catch (HibernateException e) {
			logger.error(e);
			throw new MPIException(ErrorCode.PERSISTENCE_ERROR, e.getLocalizedMessage(), e);
		}			
	}

	@SuppressWarnings("rawtypes")
	public List getAllEntries(String className) throws MPIException {
		try {
			List entries = (List)em.createQuery("FROM " +className+ " c").getResultList();					
			return entries;
		} catch (PersistenceException e) {
			logger.error(e);
			throw new MPIException(ErrorCode.PERSISTENCE_ERROR, e.getLocalizedMessage(), e);
		}		
	}
	
	public int countAll(String className) throws MPIException {
		try {
			int count = (Integer)em.createQuery("SELECT count(c) FROM " +className+ " c").getSingleResult();																									  
			return count;
		} catch (PersistenceException e) {
			logger.error(e);
			throw new MPIException(ErrorCode.PERSISTENCE_ERROR, e.getLocalizedMessage(), e);
		}	
	}
}
