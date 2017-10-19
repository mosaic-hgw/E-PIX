package org.emau.icmvc.ganimed.epix.core;

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

import org.emau.icmvc.ganimed.epix.common.notifier.AbstractNotifier;
import org.emau.icmvc.ganimed.epix.common.notifier.NotifierFactory;
import org.emau.icmvc.ganimed.epix.core.impl.EPIXMPIIdCounterImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.AccountDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.ContactDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.ContactHistoryDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.IDTypeDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.IdentifierDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.IdentifierDomainDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.IdentifierHistoryDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.PersonDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.PersonGroupDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.PersonGroupHistoryDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.PersonHistoryDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.PersonLinkDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.PersonLinkHistoryDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.PersonPreprocessedDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.ProjectDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.SourceDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Account;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Project;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Source;

/**
 * 
 * @author Christian Schack
 * @since 2011
 * 
 */
public abstract class EPIXContext {

	protected PersonDAO personDAO = null;
	
	protected PersonPreprocessedDAO personPreprocessedDAO=null;

	protected PersonLinkDAO personLinkDAO = null;

	protected IDTypeDAO idTypeDAO = null;

	protected IdentifierDAO identifierDAO = null;

	protected AccountDAO accountDAO = null;

	protected ProjectDAO projectDAO = null;

	protected ContactDAO contactDAO = null;

	protected IdentifierDomainDAO identifierDomainDAO = null;

	protected PersonHistoryDAO personHistoryDAO = null;

	protected IdentifierHistoryDAO identifierHistoryDAO = null;

	protected ContactHistoryDAO contactHistoryDAO = null;

	protected PersonGroupDAO personGroupDAO = null;

	protected PersonGroupHistoryDAO personGroupHistoryDAO = null;

	protected PersonLinkHistoryDAO personLinkHistoryDAO = null;

	protected SourceDAO sourceDAO = null;

	protected Account account = null;

	protected AbstractNotifier notifier = null;

	private EntityManager em = null;

	private EPIXMPIIdCounterImpl iEpixMpiIdCounter;

	private Source safeSource = null;

	public EPIXContext(NotifierFactory notifierFactory) {
		notifier = notifierFactory.createNotifier();
	}

	private void initEntityManager() {
		accountDAO.setEntityManager(em);
		personDAO.setEntityManager(em);
		personPreprocessedDAO.setEntityManager(em);
		accountDAO.setEntityManager(em);
		identifierDAO.setEntityManager(em);
		identifierDomainDAO.setEntityManager(em);
		idTypeDAO.setEntityManager(em);
		personLinkDAO.setEntityManager(em);
		contactDAO.setEntityManager(em);
		personHistoryDAO.setEntityManager(em);
		contactHistoryDAO.setEntityManager(em);
		identifierHistoryDAO.setEntityManager(em);
		projectDAO.setEntityManager(em);
		personGroupDAO.setEntityManager(em);
		personGroupHistoryDAO.setEntityManager(em);
		personLinkHistoryDAO.setEntityManager(em);
		sourceDAO.setEntityManager(em);
	}

	public PersonDAO getPersonDAO() {
		return personDAO;
	}

	public void setPersonDAO(PersonDAO personDAO) {
		this.personDAO = personDAO;
	}

	public PersonPreprocessedDAO getPersonPreprocessedDAO() {
		return personPreprocessedDAO;
	}

	public void setPersonPreprocessedDAO(PersonPreprocessedDAO personPreprocessedDAO) {
		this.personPreprocessedDAO = personPreprocessedDAO;
	}

	public IDTypeDAO getIdTypeDAO() {
		return idTypeDAO;
	}

	public void setIdTypeDAO(IDTypeDAO idTypeDAO) {
		this.idTypeDAO = idTypeDAO;
	}

	public IdentifierDAO getIdentifierDAO() {
		return identifierDAO;
	}

	public void setIdentifierDAO(IdentifierDAO identifierDAO) {
		this.identifierDAO = identifierDAO;
	}

	public IdentifierDomainDAO getIdentifierDomainDAO() {
		return identifierDomainDAO;
	}

	public void setIdentifierDomainDAO(IdentifierDomainDAO identifierDomainDAO) {
		this.identifierDomainDAO = identifierDomainDAO;
	}

	public AccountDAO getAccountDAO() {
		return accountDAO;
	}

	public void setAccountDAO(AccountDAO accountDAO) {
		this.accountDAO = accountDAO;
	}

	public ProjectDAO getProjectDAO() {
		return projectDAO;
	}

	public void setProjectDAO(ProjectDAO projectDAO) {
		this.projectDAO = projectDAO;
	}

	public PersonLinkDAO getPersonLinkDAO() {
		return personLinkDAO;
	}

	public void setPersonLinkDAO(PersonLinkDAO personLinkDAO) {
		this.personLinkDAO = personLinkDAO;
	}

	public PersonHistoryDAO getPersonHistoryDAO() {
		return personHistoryDAO;
	}

	public void setPersonHistoryDAO(PersonHistoryDAO personHistoryDAO) {
		this.personHistoryDAO = personHistoryDAO;
	}

	public IdentifierHistoryDAO getIdentifierHistoryDAO() {
		return identifierHistoryDAO;
	}

	public void setIdentifierHistoryDAO(IdentifierHistoryDAO identifierHistoryDAO) {
		this.identifierHistoryDAO = identifierHistoryDAO;
	}

	public ContactHistoryDAO getContactHistoryDAO() {
		return contactHistoryDAO;
	}

	public void setContactHistoryDAO(ContactHistoryDAO contactHistoryDAO) {
		this.contactHistoryDAO = contactHistoryDAO;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Project getProject() {
		return account.getProject();
	}

	public ContactDAO getContactDAO() {
		return contactDAO;
	}

	public void setContactDAO(ContactDAO contactDAO) {
		this.contactDAO = contactDAO;
	}

	public AbstractNotifier getNotifier() {
		return notifier;
	}

	public void setEntityManager(EntityManager em) {
		this.em = em;
		initEntityManager();
	}

	public EPIXMPIIdCounterImpl getIEpixMpiIdCounter() {
		return iEpixMpiIdCounter;
	}

	public void setIEpixMpiIdCounter(EPIXMPIIdCounterImpl iEpixMpiIdCounter) {
		this.iEpixMpiIdCounter = iEpixMpiIdCounter;
	}

	public PersonGroupDAO getPersonGroupDAO() {
		return personGroupDAO;
	}

	public void setPersonGroupDAO(PersonGroupDAO personGroupDAO) {
		this.personGroupDAO = personGroupDAO;
	}

	public PersonGroupHistoryDAO getPersonGroupHistoryDAO() {
		return personGroupHistoryDAO;
	}

	public void setPersonGroupHistoryDAO(PersonGroupHistoryDAO personGroupHistoryDAO) {
		this.personGroupHistoryDAO = personGroupHistoryDAO;
	}

	public PersonLinkHistoryDAO getPersonLinkHistoryDAO() {
		return personLinkHistoryDAO;
	}

	public void setPersonLinkHistoryDAO(PersonLinkHistoryDAO personLinkHistoryDAO) {
		this.personLinkHistoryDAO = personLinkHistoryDAO;
	}

	/**
	 * Method to flush the entity manager.
	 */
	public void flushEM() {
		em.flush();
	}

	public SourceDAO getSourceDAO() {
		return sourceDAO;
	}

	public void setSourceDAO(SourceDAO sourceDAO) {
		this.sourceDAO = sourceDAO;
	}

	public Source getSafeSource() {
		return safeSource;
	}

	public void setSafeSource(Source safeSource) {
		this.safeSource = safeSource;
	}

}
