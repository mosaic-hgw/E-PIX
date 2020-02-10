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

import org.emau.icmvc.ganimed.epix.common.MPIException;
import org.emau.icmvc.ganimed.epix.core.persistence.model.HealthcareProvider;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Identifier;
import org.emau.icmvc.ganimed.epix.core.persistence.model.IdentifierDomain;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Patient;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Person;
import org.emau.icmvc.ganimed.epix.core.persistence.model.PersonGroup;
import org.emau.icmvc.ganimed.epix.core.persistence.model.PersonPreprocessed;

/**
 * 
 * @author Christian Schack
 * @since 08.11.2010
 */
public abstract class PersonDAO extends GenericDAO {

	public PersonDAO() {
	}

	/**
	 * 
	 * @param person
	 * @param personType
	 * @return
	 * @throws MPIException
	 */
	public abstract Person addPerson(Person person, Class<? extends Person> personType) throws MPIException;
	
	public abstract Person getPersonById(Long id) throws MPIException;
	
	public abstract Person getPersonByPersonPreprocessed(PersonPreprocessed personPreprocessed) throws MPIException;

	public abstract Person getPersonByIdentifierAndDomain(Identifier identifier, IdentifierDomain domain) throws MPIException;
	
	public abstract List<Person> getPersonsByIdentifierAndDomain(Identifier identifier, IdentifierDomain domain) throws MPIException;
	
	public abstract List<Person> getPersonsByIdentifierAndDomain(String identifier, IdentifierDomain domain) throws MPIException;

	public abstract List<Person> getPersonByIdentifierAndDomain(String identifier, IdentifierDomain domain) throws MPIException;

	public abstract List<Person> getAllPersons() throws MPIException;

	public abstract List<Person> getAllPersons(int pageSize, long pageNr) throws MPIException;

	public abstract List<Person> getAllPersonMinusLinkedPersons(int pageSize, long pageNr) throws MPIException;

	public abstract void addLinkedPerson(Person src, Person dest, double matchValue, String matchStrategy) throws MPIException;

	public abstract List<Person> getAllLinkedPersonsByPersonId(Long id) throws MPIException;

	public abstract List<Person> getPersonByFirstNameAndLastNameAndBirthDate(Person p) throws MPIException;

	public abstract int countLinkedPersonsByPersonId(Long personId) throws MPIException;

	public abstract void deleteAll() throws MPIException;

	public abstract Person findPersonByIdentifierAndDomain(Identifier identifier, IdentifierDomain domain) throws MPIException;

	public abstract void updatePerson(Person findPerson, Person p) throws MPIException;

	public abstract void updatePatient(Patient findPatient, Patient updatePatient) throws MPIException;

	public abstract void updateHealthcareProvider(HealthcareProvider findHealthcareProvider, HealthcareProvider updateHealthcareProvider) throws MPIException;

	public abstract List<Person> findPersonsByPDQ(String firstName, String lastName, String year, String month, String day, String mpiId, String identifier) throws MPIException;

	public abstract List<Person> findPersonsByPDQ(String firstName, String lastName, String year, String month, String day, String mpiId, String identifier, 
			String identifierDomain) throws MPIException;
	
	public abstract List<Person> findPersonByPersonGroup(PersonGroup personGroup) throws MPIException;
	
	public abstract List<Person> getPersonsByIds (List<Long> ids) throws MPIException;
}
