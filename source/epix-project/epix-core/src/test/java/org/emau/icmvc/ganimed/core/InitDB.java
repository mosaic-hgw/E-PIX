package org.emau.icmvc.ganimed.core;

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


import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.emau.icmvc.ganimed.epix.common.MPIException;
import org.emau.icmvc.ganimed.epix.common.utils.SHA;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.AccountDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.IDTypeDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.IdentifierDomainDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.ProjectDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.RoleDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.impl.AccountDAOImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.impl.IDTypeDAOImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.impl.IdentifierDomainDAOImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.impl.ProjectDAOImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.impl.RoleDAOImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Account;
import org.emau.icmvc.ganimed.epix.core.persistence.model.IDType;
import org.emau.icmvc.ganimed.epix.core.persistence.model.IdentifierDomain;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Project;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Role;
import org.junit.Before;
import org.junit.Test;

public class InitDB {
	
	protected EntityManager em;
	
	@Before
	public void before(){
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("epix_test");
		this.em = emf.createEntityManager();
	}
	
	@Test
	public void testtest() {
		
	    em.getTransaction().begin();
		IDTypeDAO idTypeDAO = new IDTypeDAOImpl();		
		
		
		RoleDAO roleDAO = new RoleDAOImpl();
		AccountDAO accountDAO = new AccountDAOImpl();
		ProjectDAO projectDAO = new ProjectDAOImpl();
		IdentifierDomainDAO domainDAO = new IdentifierDomainDAOImpl();
		
		idTypeDAO.setEntityManager(em);
		roleDAO.setEntityManager(em);
		accountDAO.setEntityManager(em);
		projectDAO.setEntityManager(em);
		domainDAO.setEntityManager(em);
		
		try {
			IDType ean13 = new IDType("EAN13");
			ean13 = idTypeDAO.addIDType(ean13);
			
			Project p = new Project("GANI_MED");			
			p.setIdType(ean13);
			
			Role role = new Role();
			role.setName("user");
			role.setDesc("Standard");			
			
			roleDAO.addRole(role);
			List<Role> roles = new ArrayList<Role>();
			roles.add(role);	
			
			Account account = new Account("minca", SHA.getSHA1("minca", null));
			account.setRoles(roles);
			account.setProject(p);
			
			account = accountDAO.getAccount("Schacky", "Pupacky");		
			
			projectDAO.addProject(p);
			accountDAO.addAccount(account);
			
			IdentifierDomain domain = new IdentifierDomain();
			domain.setName("IT-Kohortenmanagement");
			domain.setOid("org.emau.icmvc.ganimed.minca");
			domainDAO.addIdentifierDomain(domain);
			
			// 1 --> Create IDType
			// 2 --> Create Account
			// 3 --> Create Project			
			em.getTransaction().commit();

		} catch (MPIException e) {
			// TODO Auto-generated catch block
			em.getTransaction().rollback();
			e.printStackTrace();
		}finally{
			em.close();
		}
	}
}
