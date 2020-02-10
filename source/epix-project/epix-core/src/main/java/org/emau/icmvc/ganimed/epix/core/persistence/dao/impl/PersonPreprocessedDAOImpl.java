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

import javax.persistence.PersistenceException;

import org.emau.icmvc.ganimed.epix.common.MPIException;
import org.emau.icmvc.ganimed.epix.common.model.ErrorCode;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.PersonPreprocessedDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.model.PersonPreprocessed;

public class PersonPreprocessedDAOImpl extends PersonPreprocessedDAO {

	public PersonPreprocessedDAOImpl() {
	}

	@Override
	public PersonPreprocessed addPersonPreprocessed(PersonPreprocessed personPreprocessed) throws MPIException {

		if (personPreprocessed == null) {
			throw new IllegalArgumentException("PersonPreprocessed must not be null.");
		}
		try {
			em.persist(personPreprocessed);
			logger.debug("New personpreprocessed entry added: " + personPreprocessed);
			return personPreprocessed;
		} catch (PersistenceException e) {
			logger.error(e);
			throw new MPIException(ErrorCode.PERSISTENCE_ERROR, e.getLocalizedMessage(), e);
		}
	}


	@Override
	public void updatePersonPreprocessed(PersonPreprocessed updatePersonPreprocessed) throws MPIException {
		
		try
		{
			PersonPreprocessed pp = em.find(PersonPreprocessed.class, updatePersonPreprocessed.getPersonId());
			if(pp!=null)
			{
				pp.updatePersonPreprocessed(updatePersonPreprocessed);
			}
			else
			{
				throw new MPIException(ErrorCode.NO_PERSON_FOUND);
			}
		}
		catch(IllegalArgumentException e)
		{
			logger.error("Error while updating personPreprocessed",e);
		}
		
	}
}
