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


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.emau.icmvc.ganimed.epix.common.model.Contact;
import org.emau.icmvc.ganimed.epix.common.model.Gender;
import org.emau.icmvc.ganimed.ttp.exception.PatientParseException;
import org.emau.icmvc.ganimed.ttp.model.TableContact;

/**
 * Wrapper Class for patient, wrapping Information for Display in Batch Processing
 * 
 * @author weiherg
 * 
 */
public class PatientModelWrapper {

	private PatientModel patient;
	private boolean importEnabled;

	public PatientModelWrapper(ArrayList<String> record) throws PatientParseException {
		PatientModel person = new PatientModel();
		SimpleDateFormat sdf = new SimpleDateFormat();
		int index = 0;
		if (record.size() - 20 % 7 != 0) {
			throw new PatientParseException("Wrong number of entries in record");
		}
		// LocalID,Degree,LastName,MotherMaidensname,MiddleName,FirstName,BirthDate,BirthPlace,Gender,Nationality,value1-10,[Street,PostCode,City,State,Country,Phone,E-Mail]
		person.setLocalIdentifier(record.get(index++));
		person.setDegree(record.get(index++));
		person.setLastName(record.get(index++));
		person.setMothersmaidenname(record.get(index++));
		person.setSecondName(record.get(index++));
		person.setFirstName(record.get(index++));
		try {
			person.setBirthDate(sdf.parse(record.get(index)));
		} catch (ParseException e) {
			throw new PatientParseException("Unable to parse birthdate", e);
		}
		person.setBirthplace(record.get(1));
		if (record.get(index).equals("M")) {
			person.setSex(Gender.M);
		} else if (record.get(index).equals("F")) {
			person.setSex(Gender.F);
		} else if (record.get(index).equals("O")) {
			person.setSex(Gender.O);
		} else if (record.get(index).equals("U")) {
			person.setSex(Gender.U);
		} else {
			throw new PatientParseException("Unkown gender '" + record.get(index) + "'");
		}
		index++;
		person.setNationality(record.get(index++));

		while (index < record.size()) {
			Contact contact = new Contact();
			contact.setStreet((record.get(index++)));
			contact.setZipCode((record.get(index++)));
			contact.setCity((record.get(index++)));
			contact.setState((record.get(index++)));
			contact.setCountry((record.get(index++)));
			contact.setPhone((record.get(index++)));
			contact.setPhone((record.get(index++)));
			contact.setEmail((record.get(index++)));
			TableContact tableContact = new TableContact();
			tableContact.setContact(contact);
			person.addContact(tableContact);
		}
	}

	public PatientModelWrapper() {

	}

	public PatientModel getPatient() {
		return patient;
	}

	public void setPatient(PatientModel patient) {
		this.patient = patient;
	}

	public boolean isImportEnabled() {
		return importEnabled;
	}

	public void setImportEnabled(boolean importEnabled) {
		this.importEnabled = importEnabled;
	}
}
