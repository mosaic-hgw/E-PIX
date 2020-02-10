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

import java.util.List;

import org.emau.icmvc.ganimed.epix.common.MPIException;
import org.emau.icmvc.ganimed.epix.common.model.CriticalMatch;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Contact;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Identifier;
import org.emau.icmvc.ganimed.epix.core.persistence.model.IdentifierDomain;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Person;

/**
 * 
 * @author schackc
 * @since 05.11.2010
 */
public interface EPIXModelMapper {

	/**
	 * Maps the given JAXB Person object list to an equivalent person entity
	 * list of the PIXMan model.
	 * 
	 * @param persons
	 * @return list with person entities
	 * @throws MPIException
	 */
	public List<Person> mapPersons(List<org.emau.icmvc.ganimed.epix.common.model.Person> persons) throws MPIException;

	/**
	 * Maps the given JAXB person object to the equivalent person entity object
	 * of the PIXMan model
	 * 
	 * @param person
	 * @return an person object
	 * @throws MPIException
	 */
	public Person mapPerson(org.emau.icmvc.ganimed.epix.common.model.Person person) throws MPIException;

	/**
	 * Maps the given JAXB contact object list to an equivalent contact entity
	 * list of the PIXMan model.
	 * 
	 * @param contacts
	 * @param person
	 * @return a list of contacts
	 * @throws MPIException
	 */
	public List<Contact> mapContacts(List<org.emau.icmvc.ganimed.epix.common.model.Contact> contacts, Person person)
			throws MPIException;

	/**
	 * Maps the given JAXB contact object to the equivalent contact entity
	 * object of the PIXMan model
	 * 
	 * @param person
	 * @return an person object
	 * @throws MPIException
	 */
	public Contact mapContact(org.emau.icmvc.ganimed.epix.common.model.Contact contact, Person person)
			throws MPIException;

	/**
	 * 
	 * @param identifier
	 * @return
	 * @throws MPIException
	 */
	public List<Identifier> mapIdentifiers(List<org.emau.icmvc.ganimed.epix.common.model.Identifier> identifiers)
			throws MPIException;

	/**
	 * 
	 * @param identifier
	 * @return
	 * @throws MPIException
	 */
	public Identifier mapIdentifier(org.emau.icmvc.ganimed.epix.common.model.Identifier identifier) throws MPIException;

	/**
	 * 
	 * @param p
	 * @return
	 */
	public org.emau.icmvc.ganimed.epix.common.model.Person mapOutputPerson(Person p) throws MPIException;

	/**
	 * 
	 * @param localIdentifiers
	 * @return
	 */
	public List<org.emau.icmvc.ganimed.epix.common.model.Identifier> mapOutputIdentifiers(
			List<Identifier> localIdentifiers);

	/**
	 * 
	 * @param localIdentifiers
	 * @return
	 */
	public org.emau.icmvc.ganimed.epix.common.model.Identifier mapOutputIdentifier(Identifier localIdentifier);

	/**
	 * 
	 * @param domain
	 * @return
	 */
	public org.emau.icmvc.ganimed.epix.common.model.IdentifierDomain mapOutputDomain(
			org.emau.icmvc.ganimed.epix.core.persistence.model.IdentifierDomain domain);

	/**
	 * 
	 * @param identifierDomain
	 * @return
	 */
	public IdentifierDomain mapIdentifierDomain(
			org.emau.icmvc.ganimed.epix.common.model.IdentifierDomain identifierDomain);

	/**
	 * 
	 * @param person
	 * @return
	 * @throws MPIException
	 */
	public CriticalMatch mapCriticalMatch(Person person) throws MPIException;
}
