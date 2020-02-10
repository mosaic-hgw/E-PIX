package org.emau.icmvc.ganimed.epix.core.impl;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.emau.icmvc.ganimed.epix.common.MPIException;
import org.emau.icmvc.ganimed.epix.common.model.ErrorCode;
import org.emau.icmvc.ganimed.epix.core.EPIXContext;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.IdentifierDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.ProjectDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Identifier;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Project;

public class EPIXMPIIdCounterImpl {

	private static EPIXMPIIdCounterImpl INSTANCE;

	// private static AtomicReference<EPIXMPIIdCounterImpl> INSTANCE = new
	// AtomicReference<EPIXMPIIdCounterImpl>();

	// public EPIXMPIIdCounterImpl() {
	// final EPIXMPIIdCounterImpl previous = INSTANCE.getAndSet(this);
	// if(previous != null)
	// throw new IllegalStateException("Second singleton " + this +
	// " created after " + previous);
	// }

	private EPIXMPIIdCounterImpl() {

	}

	public static synchronized EPIXMPIIdCounterImpl getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new EPIXMPIIdCounterImpl();
		}
		return INSTANCE;
	}

	private EPIXContext context;

	private Map<Long, Long> counterMap;

	public EPIXMPIIdCounterImpl(EPIXContext context) throws MPIException {
		this.context = context;
		createCounterMap(context);
	}

	public synchronized long getCounter(long projectId, EPIXContext context) throws MPIException {

		if (counterMap == null) {
			createCounterMap(context);
		}
		Long lo;
		if (counterMap.get(projectId) == null) {
			throw new MPIException(ErrorCode.PROJECT_ID_NOT_KNOWN);
		} else {
			lo = counterMap.get(projectId);

			lo++;
			counterMap.put(projectId, lo);

			return lo;
		}
	}

	private void createCounterMap(EPIXContext context) throws MPIException {
		this.context = context;
		ProjectDAO projectDao = context.getProjectDAO();
		if (counterMap == null) {
			List<Project> projects;
			try {
				counterMap = new HashMap<Long, Long>();
				projects = projectDao.getAllProjects();
				for (Project project : projects) {
					// long counter =
					// mpiDao.getLastIndexByProjectId(project.getId());
					long counter = getLastIndexByProject(project);
					counterMap.put(project.getId(), counter);
				}
			} catch (MPIException e) {
				throw new MPIException(ErrorCode.INTERNAL_ERROR, e);
			}
		}
	}

	private long getLastIndexByProject(Project project) throws MPIException {
		if (project == null) {
			throw new MPIException(ErrorCode.PROJECT_ID_NOT_KNOWN);
		}
		IdentifierDAO identifierDAO = context.getIdentifierDAO();
		Identifier identifier = identifierDAO.getlastMpiIdentifierByProject(project);
		long counter = 0l;
		if (identifier != null && !identifier.getValue().isEmpty() && identifier.getValue().length() >= 12) {
			counter = Long.parseLong(identifier.getValue().substring(4, 12));
		}
		return counter;
	}

}
