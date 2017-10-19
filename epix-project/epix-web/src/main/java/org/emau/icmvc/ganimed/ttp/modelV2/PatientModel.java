package org.emau.icmvc.ganimed.ttp.modelV2;

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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.datatype.DatatypeConfigurationException;

import org.emau.icmvc.ganimed.epix.common.model.Contact;
import org.emau.icmvc.ganimed.epix.common.model.Gender;
import org.emau.icmvc.ganimed.epix.common.model.Identifier;
import org.emau.icmvc.ganimed.epix.common.model.IdentifierDomain;
import org.emau.icmvc.ganimed.epix.common.model.MPIID;
import org.emau.icmvc.ganimed.epix.common.model.MPIRequest;
import org.emau.icmvc.ganimed.epix.common.model.Patient;
import org.emau.icmvc.ganimed.epix.common.model.Person;
import org.emau.icmvc.ganimed.epix.common.model.RequestEntry;
import org.emau.icmvc.ganimed.epix.pdqquery.model.SearchMask;
import org.emau.icmvc.ganimed.ttp.exception.EmptyMaskException;
import org.emau.icmvc.ganimed.ttp.exception.InvalidIdentifierException;
import org.emau.icmvc.ganimed.ttp.model.TableContact;

/**
 * Model, containing all the data of a person/patient. Can be created directly
 * from a person and parsed to MPIREquest and SearchMask. Some of the fields
 * have a different type than the original persondata for better handling in the
 * view
 * 
 * 
 * @author weiherg
 * 
 */
public class PatientModel {

	private MPIID mpiid = new MPIID();
	// id
	private String localIdentifier;

	private String personType;

	private String identifierDomain;

	// person data
	private String firstName;
	private String secondName;
	private String lastName;
	private String degree;
	private String nationality;
	private String mothersmaidenname;
	private String birthplace;
	private Gender sex;
	private Date birthDate;
	// List of the persons contacts + ids
	private List<TableContact> tableContacts = new ArrayList<TableContact>();
	private List<String> values = new ArrayList<String>(Arrays.asList(new String[10]));
	/**
	 * Index for contacts, used to create unique ids for tableContact objects
	 */
	private AtomicInteger contactIndex = new AtomicInteger(1);

	/**
	 * empty constructor
	 */
	public PatientModel() {
	}

	/**
	 * Constructor
	 * 
	 * @param person
	 *            existing person to create a model from
	 */
	public PatientModel(Person person) {
		// get Identifiers for diplay
		this.mpiid = person.getMpiid();
		if (person.getIdentifiers() != null && !person.getIdentifiers().isEmpty()) {
			this.identifierDomain = person.getIdentifiers().get(0).getIdentifierDomain().getName();
			this.localIdentifier = person.getIdentifiers().get(0).getValue();
		}

		// get person fields
		this.firstName = person.getFirstName();
		this.secondName = person.getMiddleName();
		this.lastName = person.getLastName();
		this.degree = person.getDegree();
		this.nationality = person.getNationality();
		this.mothersmaidenname = person.getMothersMaidenName();
		this.birthplace = person.getBirthPlace();
		this.sex = person.getGender();
		this.setBirthDate(person.getBirthDate());

		// getVariableValues
		values.set(0, person.getValue1());
		values.set(1, person.getValue2());
		values.set(2, person.getValue3());
		values.set(3, person.getValue4());
		values.set(4, person.getValue5());
		values.set(5, person.getValue6());
		values.set(6, person.getValue7());
		values.set(7, person.getValue8());
		values.set(8, person.getValue9());
		values.set(9, person.getValue10());

		// getContacts
		tableContacts = new ArrayList<TableContact>();
		for (Contact contact : person.getContacts()) {
			tableContacts.add(new TableContact(contactIndex.getAndIncrement(), contact));
		}
	}

	/**
	 * adds a new Contact to the List or overrides an old one, depending on if
	 * the id is set or not
	 * 
	 * @param contact
	 */
	public void addContact(TableContact contact) {
		if (contact.getId() == 0) {
			contact.setId(contactIndex.getAndIncrement());
			tableContacts.add(contact);
		} else {
			for (TableContact tableContact : tableContacts) {
				if (tableContact.getId() == contact.getId()) {
					tableContact.setContact(contact.getContact());
				}
				break;
			}
			// if(!contains){
			// contact.setId(contactIndex.getAndIncrement());
			// tableContacts.add(contact);
			// }
		}

	}

	public void deleteContact(TableContact contact) {
		tableContacts.remove(contact);
	}

	/**
	 * Creates an MPI Request from the Persons data
	 * 
	 * @param domain
	 *            Identifier Domain of the Request
	 * @return MPI Request generated from the models Data
	 * @throws DatatypeConfigurationException
	 *             if the given birthdate can't be parsed with
	 *             XMLGregorianConverter
	 */
	public MPIRequest toRequest(IdentifierDomain domain) throws DatatypeConfigurationException {
		final MPIRequest request = new MPIRequest();

		IdentifierDomain gmDomain = new IdentifierDomain();
		gmDomain.setId(domain.getId());
		gmDomain.setName(domain.getName());
		gmDomain.setOid(domain.getOid());
		request.setSource(gmDomain.getName());

		// new Person
		Person person = new Patient();
		// TODO determines the type of person

		if (person != null) {
			// sets data of person from the inputs
			person.setFirstName(firstName);
			person.setMiddleName(secondName);
			person.setLastName(lastName);
			person.setMothersMaidenName(mothersmaidenname);
			person.setBirthPlace(birthplace);
			person.setDegree(degree);
			person.setNationality(nationality);
			person.setBirthDate(birthDate);
			person.setGender(sex);
			// person.setContacts(contacts);
			ArrayList<Contact> personContacts = new ArrayList<Contact>();
			for (TableContact tableContact : tableContacts) {
				personContacts.add(tableContact.getContact());
			}
			person.setContacts(personContacts);

			person.setMpiid(mpiid);

			person.setValue1(values.get(0));
			person.setValue2(values.get(1));
			person.setValue3(values.get(2));
			person.setValue4(values.get(3));
			person.setValue5(values.get(4));
			person.setValue6(values.get(5));
			person.setValue7(values.get(6));
			person.setValue8(values.get(7));
			person.setValue9(values.get(8));
			person.setValue10(values.get(9));

			/**
			 * Edit (MB+PP): the identifierdomain has additionally to be set per
			 * person in order to be processed properly
			 * 
			 * Without this an error would occur: IdentifierDomain must not be
			 * null.
			 */

			if (localIdentifier != null && !localIdentifier.isEmpty()) {
				final List<Identifier> identifiers = new ArrayList<Identifier>();
				final Identifier i = new Identifier();
				i.setValue(localIdentifier);

				IdentifierDomain ganimedDomain = new IdentifierDomain();

				ganimedDomain.setId(domain.getId());
				ganimedDomain.setName(domain.getName());
				ganimedDomain.setOid(domain.getOid());
				i.setIdentifierDomain(ganimedDomain);
				identifiers.add(i);
				person.setIdentifiers(identifiers);
			}

		}
		// creates RequestEntry out of the person
		final RequestEntry entry = new RequestEntry(person);
		request.getRequestEntries().add(entry);
		//TODO
		request.setSource("EPIX-WEB");

		return request;

	}

	/**
	 * Validates the input and creates a Search MAsk from it
	 * 
	 * @return Search Mask with the values from this model
	 * @throws DatatypeConfigurationException
	 * @throws InvalidIdentifierException
	 * @throws EmptyMaskException
	 */
	public SearchMask toSearchMask(String domainOid) throws DatatypeConfigurationException, InvalidIdentifierException,
			EmptyMaskException {

		// validation states
		boolean isMaskEmpty = true;
		boolean identifierIsEmpty = true;
		boolean domainIsEmpty = true;

		SearchMask searchMask = new SearchMask();
		searchMask.setIdentifierDomain(domainOid);

		if (firstName != null) {
			searchMask.setFirstName(firstName);
			if (!firstName.isEmpty()) {
				isMaskEmpty = false;
			}
		}
		if (lastName != null) {
			searchMask.setLastName(lastName);
			if (!lastName.isEmpty()) {
				isMaskEmpty = false;
			}
		}
		if (mpiid.getValue() != null) {
			searchMask.setMpiid(mpiid.getValue());
			if (!mpiid.getValue().isEmpty()) {
				isMaskEmpty = false;
			}
		}
		if (birthDate != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(birthDate);
			searchMask.setBirthDay(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
			searchMask.setBirthMonth(String.valueOf(cal.get(Calendar.MONTH) + 1));
			searchMask.setBirthYear(String.valueOf(cal.get(Calendar.YEAR)));
			isMaskEmpty = false;
		}
		if (identifierDomain != null && !identifierDomain.isEmpty()) {
			// searchMask.setIdentifierDomain(identifierDomain);
			domainIsEmpty = false;
			isMaskEmpty = false;

			if (localIdentifier != null && !localIdentifier.isEmpty()) {
				searchMask.setIdentifier(localIdentifier);
				identifierIsEmpty = false;
			}
		}

		// check if MAsk is valid
		if (isMaskEmpty) {
			throw new EmptyMaskException("Mask is empty");
		}
		if (identifierIsEmpty != domainIsEmpty) {
			throw new InvalidIdentifierException("invalid identifier");
		}

		return searchMask;
	}

	public static int getContactKey(Contact contact) {
		return contact.hashCode();
		// return contact.getCity() + contact.getCountry() +
		// contact.getCountryCode() + contact.getDistrict()
		// +contact.getEmail()+contact.getPhone()+contact.getState()+contact.getStreet()+contact.getZipCode();
	}

	// ----------------getters/setters-------------------------------
	public String getLocalIdentifier() {
		return localIdentifier;
	}

	public void setLocalIdentifier(String localIdentifier) {
		this.localIdentifier = localIdentifier;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getSecondName() {
		return secondName;
	}

	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getDegree() {
		return degree;
	}

	public void setDegree(String degree) {
		this.degree = degree;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getMothersmaidenname() {
		return mothersmaidenname;
	}

	public void setMothersmaidenname(String mothersmaidenname) {
		this.mothersmaidenname = mothersmaidenname;
	}

	public String getBirthplace() {
		return birthplace;
	}

	public void setBirthplace(String birthplace) {
		this.birthplace = birthplace;
	}

	public Gender getSex() {
		return sex;
	}

	public void setSex(Gender sex) {
		this.sex = sex;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	public String getPersonType() {
		return personType;
	}

	public void setPersonType(String personType) {
		this.personType = personType;
	}

	public String getIdentifierDomain() {
		return identifierDomain;
	}

	public void setIdentifierDomain(String identifierDomain) {
		this.identifierDomain = identifierDomain;
	}

	public MPIID getMpiid() {
		return mpiid;
	}

	public void setMpiid(MPIID mpiid) {
		this.mpiid = mpiid;
	}

	public List<TableContact> getTableContacts() {
		return tableContacts;
	}

	public void setTableContacts(List<TableContact> tableContacts) {
		this.tableContacts = tableContacts;
	}

}
