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

import javax.persistence.Query;

import org.emau.icmvc.ganimed.epix.common.MPIException;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.IDTypeDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.model.IDType;

public class IDTypeDAOImpl extends IDTypeDAO {

	public IDTypeDAOImpl() {
	}


	@Override
	public IDType addIDType(IDType type) throws MPIException {
		if (type == null ) {
			throw new IllegalArgumentException("IDType must not be null.");
		}
		
		if (type.getValue() == null || type.getValue().equals("")) {
			throw new IllegalArgumentException("IDType value must not be null or empty");
		}
		
		IDType check = getIDTypeByValue(type.getValue());
			
		if (check==null) {
			type.setEntryDate(new Timestamp(System.currentTimeMillis()));			
			em.persist(type);		
		} else {
			logger.warn("IdType "+type+" already exists. ");			
		}
		return type;
			
	}

	@Override
	public List<IDType> getAllIDTypes() throws MPIException {
		throw new UnsupportedOperationException();
	}

	@Override
	public IDType getIDTypeByValue(String value) throws MPIException {
		Query q = em.createQuery("FROM IDType AS it WHERE it.value = :value");
		q.setParameter("value", value);
		
		if (q.getResultList().size() > 0) {
			return (IDType)q.getResultList().get(0);
		} else {			
			return null;
		}
		
	}

}
