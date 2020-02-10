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
import javax.persistence.PersistenceException;

import org.emau.icmvc.ganimed.epix.common.MPIException;
import org.emau.icmvc.ganimed.epix.common.model.ErrorCode;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.ProjectDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Project;

public class ProjectDAOImpl extends ProjectDAO {
	
	public ProjectDAOImpl() {}

	@Override
	public Project addProject(Project project) throws MPIException {
		
		if (project == null){
			throw new IllegalArgumentException("Project must be null.");
		}
		
		if (project.getName()==null || project.getName().equals("")) {
			throw new IllegalArgumentException("Project name must not be null or empty");
		}			
		Project toCheck = getProject(project.getName());
		if (toCheck!=null) {
			throw new MPIException(ErrorCode.PROJECT_ALREADY_EXISTS);
		}		
		
		try {
			project.setEntryDate(new Timestamp(System.currentTimeMillis()));
			em.persist(project);
		} catch (PersistenceException e) {
			throw new MPIException(ErrorCode.PERSISTENCE_ERROR, e.getLocalizedMessage(), e);
		}
		return project;
	}

	@Override
	public Project getProject(Long id) throws MPIException {
		try {
			Project p = (Project)em.createQuery("SELECT p FROM Project p WHERE p.id = :id")
			          			.setParameter("id", id)
			          			.getSingleResult();						
			return p;
		}catch (NoResultException noe) {
			return null;
		} catch (PersistenceException e) {
			throw new MPIException(ErrorCode.PERSISTENCE_ERROR, e.getLocalizedMessage(), e);
		}
	}

	@Override
	public Project getProject(String name) throws MPIException {
		try {
			Project p = (Project)em.createQuery("FROM Project p WHERE p.name = :name")
			          			.setParameter("name", name)
			          			.getSingleResult();						
			return p;
		}catch (NoResultException noe) {
			return null;
		} catch (PersistenceException e) {
			throw new MPIException(ErrorCode.PERSISTENCE_ERROR, e.getLocalizedMessage(), e);
		}
	}

	@Override
	public List<Project> getAllProjects() throws MPIException {
		try {
			@SuppressWarnings("unchecked")	
			List<Project> p = (List<Project>)em.createQuery("FROM Project p")
			          			.getResultList();						
			return p;
		}catch (NoResultException noe) {
			return null;
		} catch (PersistenceException e) {
			throw new MPIException(ErrorCode.PERSISTENCE_ERROR, e.getLocalizedMessage(), e);
		}
	}

}
