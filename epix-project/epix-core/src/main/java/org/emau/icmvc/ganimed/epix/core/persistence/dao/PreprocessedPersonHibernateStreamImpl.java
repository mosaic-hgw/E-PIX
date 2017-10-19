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


import javax.persistence.EntityManager;

import org.apache.log4j.Logger;
import org.emau.icmvc.ganimed.deduplication.DBStream;
import org.emau.icmvc.ganimed.epix.core.persistence.model.PersonPreprocessed;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;

/**
 * encapsulate a scrollable result set - call close() before dismiss this object!
 * 
 * @author geidell
 *
 */
public class PreprocessedPersonHibernateStreamImpl implements DBStream<PersonPreprocessed> {

	private static final Logger logger = Logger.getLogger(PreprocessedPersonHibernateStreamImpl.class);
	private final StatelessSession session;
	private final Transaction tx;
	private final ScrollableResults resultSet;

	public PreprocessedPersonHibernateStreamImpl(EntityManager em, String namedQuery) {
		if(logger.isDebugEnabled()) {
			logger.debug("opening db stream");
		}
		session = ((Session) em.getDelegate()).getSessionFactory().openStatelessSession();
		tx = session.beginTransaction();
		resultSet = session.getNamedQuery(namedQuery).setReadOnly(true).scroll(ScrollMode.FORWARD_ONLY);
		if(logger.isDebugEnabled()) {
			logger.debug("db stream instanciated");
		}
	}

	public boolean next() {
		return resultSet.next();
	}

	public PersonPreprocessed get() {
		return (PersonPreprocessed)resultSet.get(0);
	}

	public void close() {
		if(logger.isDebugEnabled()) {
			logger.debug("closing db stream");
		}
		if(tx != null) {
			tx.commit();
		}
		if (session != null) {
			session.close();
		}
	}
}
