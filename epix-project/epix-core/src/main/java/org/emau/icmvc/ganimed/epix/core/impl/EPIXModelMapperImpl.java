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

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.emau.icmvc.ganimed.epix.common.MPIException;
import org.emau.icmvc.ganimed.epix.common.model.CriticalMatch;
import org.emau.icmvc.ganimed.epix.common.model.ErrorCode;
import org.emau.icmvc.ganimed.epix.common.model.Gender;
import org.emau.icmvc.ganimed.epix.core.EPIXModelMapper;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Contact;
import org.emau.icmvc.ganimed.epix.core.persistence.model.HealthcareProvider;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Identifier;
import org.emau.icmvc.ganimed.epix.core.persistence.model.IdentifierDomain;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Patient;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Person;

/**
 * 
 * @author schackc
 * @since 05.11.2010
 */
public class EPIXModelMapperImpl implements EPIXModelMapper {

	private Logger logger = Logger.getLogger(EPIXModelMapperImpl.class);

	public List<Person> mapPersons(List<org.emau.icmvc.ganimed.epix.common.model.Person> persons) throws MPIException {
		if (persons == null || persons.size() == 0) {
			throw new IllegalArgumentException("Personlist must not be null");
		}

		List<Person> mappedPersons = new ArrayList<Person>();
		for (org.emau.icmvc.ganimed.epix.common.model.Person p : persons) {
			mappedPersons.add(mapPerson(p));
		}

		return mappedPersons;
	}

	public Person mapPerson(org.emau.icmvc.ganimed.epix.common.model.Person person) throws MPIException {

		if (person instanceof org.emau.icmvc.ganimed.epix.common.model.Patient) {
			Patient p = new Patient();
			return mapPerson(p, person);
		} else if (person instanceof org.emau.icmvc.ganimed.epix.common.model.HealthcareProvider) {
			HealthcareProvider hp = new HealthcareProvider();
			hp.setDepartment(((org.emau.icmvc.ganimed.epix.common.model.HealthcareProvider) person).getDepartment());
			hp.setInstitute(((org.emau.icmvc.ganimed.epix.common.model.HealthcareProvider) person).getInstitute());
			return mapPerson(hp, person);
		} else {
			throw new MPIException(ErrorCode.ILLEGAL_PERSON_TYPE);
		}

		// Person mappedPerson = initPerson(person);
		// mappedPerson.setPrefix(person.getPrefix());
		// mappedPerson.setFirstName(person.getFirstName());
		// mappedPerson.setMiddleName(person.getMiddleName());
		// mappedPerson.setLastName(person.getLastName());
		// mappedPerson.setSuffix(person.getSuffix());
		//
		// if (person.getBirthDate() != null) {
		// mappedPerson.setBirthDate(new
		// Date(person.getBirthDate().toGregorianCalendar().getTimeInMillis()));
		// }
		//
		// mappedPerson.setGender(person.getGender()==null?Gender.U.name():person.getGender().name());
		//
		// if (mappedPerson instanceof Patient) {
		// Patient p = (Patient)mappedPerson;
		// p.setMothersMaidenName(((org.emau.icmvc.ganimed.epix.common.model.Patient)person).getMothersMaidenName());
		// p.setRace(((org.emau.icmvc.ganimed.epix.common.model.Patient)person).getRace());
		// p.setReligion(((org.emau.icmvc.ganimed.epix.common.model.Patient)person).getReligion());
		// p.setBirthPlace(((org.emau.icmvc.ganimed.epix.common.model.Patient)person).getBirthPlace());
		// }
		//
		// if (mappedPerson instanceof HealthcareProvider) {
		// HealthcareProvider hp = (HealthcareProvider)mappedPerson;
		// hp.setDepartment(((org.emau.icmvc.ganimed.epix.common.model.HealthcareProvider)person).getDepartment());
		// hp.setInstitute(((org.emau.icmvc.ganimed.epix.common.model.HealthcareProvider)person).getInstitute());
		// }
		//
		// return mapPerson(p,person);
	}

	private Person mapPerson(Person p, org.emau.icmvc.ganimed.epix.common.model.Person person) {
		Person mappedPerson = p;
		mappedPerson.setPrefix(person.getPrefix());
		mappedPerson.setFirstName(person.getFirstName());
		mappedPerson.setMiddleName(person.getMiddleName());
		mappedPerson.setLastName(person.getLastName());
		mappedPerson.setSuffix(person.getSuffix());
		mappedPerson.setMothersMaidenName(person.getMothersMaidenName());
		mappedPerson.setRace(person.getRace());
		mappedPerson.setReligion(person.getReligion());
		mappedPerson.setBirthPlace(person.getBirthPlace());
		mappedPerson.setDegree(person.getDegree());
		mappedPerson.setMotherTongue(person.getMotherTongue());
		mappedPerson.setNationality(person.getNationality());
		mappedPerson.setCivilStatus(person.getCivilStatus());
		mappedPerson.setValue1(person.getValue1());
		mappedPerson.setValue2(person.getValue2());
		mappedPerson.setValue3(person.getValue3());
		mappedPerson.setValue4(person.getValue4());
		mappedPerson.setValue5(person.getValue5());
		mappedPerson.setValue6(person.getValue6());
		mappedPerson.setValue7(person.getValue7());
		mappedPerson.setValue8(person.getValue8());
		mappedPerson.setValue9(person.getValue9());
		mappedPerson.setValue10(person.getValue10());
		if (person.getBirthDate() != null) {
			mappedPerson.setBirthDate(new Date(person.getBirthDate().getTime()));
		}
		if (person.getOriginDate() != null) {
			if (person.getOriginDate().after(new Timestamp(System.currentTimeMillis()))) {
				mappedPerson.setOriginDate(new Timestamp(System.currentTimeMillis()));
			} else {
				mappedPerson.setOriginDate(new Timestamp(person.getOriginDate().getTime()));
			}
		}
		mappedPerson.setGender(person.getGender() == null ? Gender.U.name() : person.getGender().name());

		return mappedPerson;
	}

	public List<Contact> mapContacts(List<org.emau.icmvc.ganimed.epix.common.model.Contact> contacts, Person person)
			throws MPIException {
		List<Contact> mappedContacts = new ArrayList<Contact>();
		if (contacts != null && contacts.size() > 0) {
			for (org.emau.icmvc.ganimed.epix.common.model.Contact contact : contacts) {
				mappedContacts.add(mapContact(contact, person));
			}
		} else {
			logger.warn("Contact list of given person is empty");
		}
		return mappedContacts;
	}

	public Contact mapContact(org.emau.icmvc.ganimed.epix.common.model.Contact contact, Person person)
			throws MPIException {
		Contact mappedContact = new Contact();
		mappedContact.setStreet(contact.getStreet());
		mappedContact.setZipCode(contact.getZipCode());
		mappedContact.setCity(contact.getCity());
		mappedContact.setState(contact.getState());
		mappedContact.setPhone(contact.getPhone());
		mappedContact.setEmail(contact.getEmail());
		mappedContact.setCountry(contact.getCountry());
		mappedContact.setCountryCode(contact.getCountryCode());
		mappedContact.setDistrict(contact.getDistrict());
		// mappedContact.setPerson(person);
		return mappedContact;
	}

	public List<Identifier> mapIdentifiers(List<org.emau.icmvc.ganimed.epix.common.model.Identifier> identifiers)
			throws MPIException {
		if (identifiers == null) {
			return new ArrayList<Identifier>();
		}

		List<Identifier> mappedIdentifiers = new ArrayList<Identifier>();
		for (org.emau.icmvc.ganimed.epix.common.model.Identifier identifier : identifiers) {
			Identifier ident = mapIdentifier(identifier);
			if (ident != null) {
				mappedIdentifiers.add(ident);
			}

		}
		return mappedIdentifiers;
	}

	public Identifier mapIdentifier(org.emau.icmvc.ganimed.epix.common.model.Identifier identifier) throws MPIException {
		Identifier mappedIdentifier = new Identifier();
		if (identifier.getValue() == null || identifier.getValue().trim().length() == 0) {
			if (identifier.getIdentifierDomain() == null || identifier.getIdentifierDomain().getOid() == null
					|| identifier.getIdentifierDomain().getOid().trim().length() == 0) {
				return null;
			} else {
				throw new MPIException(ErrorCode.IDENTIFIER_VALUE_NULL_ERROR);
			}
		} else if (identifier.getIdentifierDomain() == null || identifier.getIdentifierDomain().getOid() == null
				|| identifier.getIdentifierDomain().getOid().trim().length() == 0) {
			throw new MPIException(ErrorCode.IDENTIFIERDOMAIN_NULL_ERROR);
		}
		mappedIdentifier.setValue(identifier.getValue());
		mappedIdentifier.setDescription(identifier.getDescription());
		mappedIdentifier.setSendingApplication(identifier.getSendingApplication());
		mappedIdentifier.setSendingFacility(identifier.getSendingFacility());
		mappedIdentifier.setDomain(mapIdentifierDomain(identifier.getIdentifierDomain()));
		return mappedIdentifier;
	}

	public IdentifierDomain mapIdentifierDomain(
			org.emau.icmvc.ganimed.epix.common.model.IdentifierDomain identifierDomain) {

		IdentifierDomain domain = new IdentifierDomain();
		domain.setName(identifierDomain.getName());
		domain.setOid(identifierDomain.getOid());
		return domain;
	}

	public org.emau.icmvc.ganimed.epix.common.model.Person mapOutputPerson(Person p) throws MPIException {
		org.emau.icmvc.ganimed.epix.common.model.Person outputPerson = initPerson(p);
		outputPerson.setId(p.getId());
		outputPerson.setPrefix(p.getPrefix());
		outputPerson.setSuffix(p.getSuffix());
		outputPerson.setFirstName(p.getFirstName());
		outputPerson.setLastName(p.getLastName());
		outputPerson.setMiddleName(p.getMiddleName());
		outputPerson.setMothersMaidenName(p.getMothersMaidenName());
		outputPerson.setRace(p.getRace());
		outputPerson.setReligion(p.getReligion());
		outputPerson.setBirthPlace(p.getBirthPlace());
		outputPerson.setDegree(p.getDegree());
		outputPerson.setMotherTongue(p.getMotherTongue());
		outputPerson.setNationality(p.getNationality());
		outputPerson.setCivilStatus(p.getCivilStatus());
		outputPerson.setValue1(p.getValue1());
		outputPerson.setValue2(p.getValue2());
		outputPerson.setValue3(p.getValue3());
		outputPerson.setValue4(p.getValue4());
		outputPerson.setValue5(p.getValue5());
		outputPerson.setValue6(p.getValue6());
		outputPerson.setValue7(p.getValue7());
		outputPerson.setValue8(p.getValue8());
		outputPerson.setValue9(p.getValue9());
		outputPerson.setValue10(p.getValue10());
		String gender = p.getGender();
		outputPerson.setGender((gender != null && !gender.equals("")) ? Gender.fromValue(p.getGender().toLowerCase())
				: Gender.U);
		List<Contact> contacts = p.getContacts();
		outputPerson.getContacts().addAll(mapOutputContacts(contacts));
		outputPerson.setBirthDate(p.getBirthDate());
		outputPerson.setOriginDate(p.getOriginDate());

		return outputPerson;
	}

	private List<? extends org.emau.icmvc.ganimed.epix.common.model.Contact> mapOutputContacts(List<Contact> contacts) {
		List<org.emau.icmvc.ganimed.epix.common.model.Contact> outputContacts = new ArrayList<org.emau.icmvc.ganimed.epix.common.model.Contact>();
		for (Contact contact : contacts) {
			outputContacts.add(mapOutputContact(contact));
		}
		return outputContacts;
	}

	private org.emau.icmvc.ganimed.epix.common.model.Contact mapOutputContact(Contact contact) {
		org.emau.icmvc.ganimed.epix.common.model.Contact outputContact = new org.emau.icmvc.ganimed.epix.common.model.Contact();
		outputContact.setId(contact.getId());
		outputContact.setCity(contact.getCity());
		outputContact.setCountry(contact.getCountry());
		outputContact.setCountryCode(contact.getCountryCode());
		outputContact.setEmail(contact.getEmail());
		outputContact.setPhone(contact.getPhone());
		outputContact.setState(contact.getState());
		outputContact.setStreet(contact.getStreet());
		outputContact.setZipCode(contact.getZipCode());
		outputContact.setDistrict(contact.getDistrict());
		return outputContact;
	}

	private org.emau.icmvc.ganimed.epix.common.model.Person initPerson(Person person) {
		if (person instanceof Patient) {
			return new org.emau.icmvc.ganimed.epix.common.model.Patient();
		} else if (person instanceof HealthcareProvider) {
			return new org.emau.icmvc.ganimed.epix.common.model.HealthcareProvider();
		} else {
			// TODO Check if that makes sense
			return new org.emau.icmvc.ganimed.epix.common.model.Person();
		}
	}

	public List<org.emau.icmvc.ganimed.epix.common.model.Identifier> mapOutputIdentifiers(List<Identifier> idents) {
		if (idents == null) {
			return new ArrayList<org.emau.icmvc.ganimed.epix.common.model.Identifier>();
		}
		List<org.emau.icmvc.ganimed.epix.common.model.Identifier> mappedIdentifiers = new ArrayList<org.emau.icmvc.ganimed.epix.common.model.Identifier>();
		for (Identifier ident : idents) {
			org.emau.icmvc.ganimed.epix.common.model.Identifier mappedIdent = mapOutputIdentifier(ident);
			if (mappedIdent != null) {
				mappedIdentifiers.add(mappedIdent);
			}
		}
		return mappedIdentifiers;
	}

	public org.emau.icmvc.ganimed.epix.common.model.Identifier mapOutputIdentifier(Identifier ident) {

		org.emau.icmvc.ganimed.epix.common.model.Identifier output = new org.emau.icmvc.ganimed.epix.common.model.Identifier();
		// output.setId(ident.getId());
		output.setValue(ident.getValue());
		output.setDescription(ident.getDescription());
		output.setSendingApplication(ident.getSendingApplication());
		output.setSendingFacility(ident.getSendingFacility());
		output.setIdentifierDomain(mapOutputDomain(ident.getDomain()));
		return output;
	}

	public org.emau.icmvc.ganimed.epix.common.model.IdentifierDomain mapOutputDomain(IdentifierDomain domain) {
		if (domain == null) {
			throw new IllegalArgumentException("Domain must not be null.");
		}

		org.emau.icmvc.ganimed.epix.common.model.IdentifierDomain d = new org.emau.icmvc.ganimed.epix.common.model.IdentifierDomain();
		d.setId(domain.getId());
		d.setName(domain.getName());
		d.setOid(domain.getOid());
		return d;
	}

	public CriticalMatch mapCriticalMatch(Person person) throws MPIException {
		if (person == null) {
			throw new MPIException(ErrorCode.NO_ATTRIBUTES_DEFINED);
		}
		CriticalMatch criticalMatch = new CriticalMatch();
		criticalMatch.setMpiid(person.getPersonGroup().getFirstMpi().getValue());
		criticalMatch.setName(person.getLastName());
		criticalMatch.setFirstName(person.getFirstName());
		criticalMatch.setSex(person.getGender());
		if (person.getContacts() != null && !person.getContacts().isEmpty()) {
			criticalMatch.setPostalCode(person.getContacts().get(0).getZipCode());
			criticalMatch.setStreet(person.getContacts().get(0).getStreet());
			criticalMatch.setCity(person.getContacts().get(0).getCity());
			criticalMatch.setCountry(person.getContacts().get(0).getCountry());
			criticalMatch.setState(person.getContacts().get(0).getState());
		}
		criticalMatch.setBirthdate(mapBirthString(person.getBirthDate()));
		criticalMatch.setLastChange(mapTimestampString(person.getTimestamp()));
		criticalMatch.setDatabaseId(person.getId().toString());
		criticalMatch.setMothersMaidenName(person.getMothersMaidenName());
		criticalMatch.setNationality(person.getNationality());
		criticalMatch.setBirthPlace(person.getBirthPlace());
		criticalMatch.setDegree(person.getDegree());
		criticalMatch.setMiddleName(person.getMiddleName());
		criticalMatch.setValue1(person.getValue1());
		criticalMatch.setValue2(person.getValue2());
		criticalMatch.setValue3(person.getValue3());
		criticalMatch.setValue4(person.getValue4());
		criticalMatch.setValue5(person.getValue5());
		criticalMatch.setValue6(person.getValue6());
		criticalMatch.setValue7(person.getValue7());
		criticalMatch.setValue8(person.getValue8());
		criticalMatch.setValue9(person.getValue9());
		criticalMatch.setValue10(person.getValue10());
		// aliase

		return criticalMatch;
	}

	private String mapBirthString(Date birthdate) {
		if (birthdate == null) {
			return null;
		}
		SimpleDateFormat birthdateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String birthString = birthdateFormat.format(birthdate);
		return birthString;
	}

	private String mapTimestampString(Timestamp timestamp) {
		if (timestamp == null) {
			return null;
		}
		SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
		String timestampString = timestampFormat.format(timestamp);
		return timestampString;
	}

}
