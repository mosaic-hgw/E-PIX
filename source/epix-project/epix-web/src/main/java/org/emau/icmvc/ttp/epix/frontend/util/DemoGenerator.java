package org.emau.icmvc.ttp.epix.frontend.util;

/*-
 * ###license-information-start###
 * E-PIX - Enterprise Patient Identifier Cross-referencing
 * __
 * Copyright (C) 2009 - 2022 Trusted Third Party of the University Medicine Greifswald
 * 							kontakt-ths@uni-greifswald.de
 * 
 * 							concept and implementation
 * 							l.geidel,c.schack, d.langner, g.koetzschke
 * 
 * 							web client
 * 							a.blumentritt, f.m. moser
 * 
 * 							docker
 * 							r.schuldt
 * 
 * 							privacy preserving record linkage (PPRL)
 * 							c.hampf
 * 
 * 							please cite our publications
 * 							http://dx.doi.org/10.3414/ME14-01-0133
 * 							http://dx.doi.org/10.1186/s12967-015-0545-6
 * 							https://translational-medicine.biomedcentral.com/articles/10.1186/s12967-020-02257-4
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.emau.icmvc.ttp.epix.common.model.ContactOutDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentifierDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentifierDomainDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityOutDTO;
import org.emau.icmvc.ttp.epix.common.model.enums.Gender;

public abstract class DemoGenerator
{
	public static IdentityOutDTO getDemoIdentity(List<IdentifierDomainDTO> identifierDomains)
	{
		IdentityOutDTO identity = new IdentityOutDTO();

		// Generate identifiers
		List<IdentifierDTO> identifiers = new ArrayList<>();
		for (IdentifierDomainDTO identifierDomain : identifierDomains)
		{
			identifiers.add(new IdentifierDTO(UUID.randomUUID().toString().substring(0, 8), "", new Date(), identifierDomain));
		}
		identity.setIdentifiers(identifiers);

		// Generate IDAT
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2890);
		cal.set(Calendar.MONTH, Calendar.SEPTEMBER);
		cal.set(Calendar.DAY_OF_MONTH, 22);
		identity.setBirthDate(cal.getTime());
		identity.setBirthPlace("-37.857644, 175.680038");
		identity.setCivilStatus("ledig");
		identity.setDegree("");
		identity.setExternalDate(new Date());
		identity.setFirstName("Bilbo");
		identity.setGender(Gender.M);
		identity.setLastName("Beutlin");
		identity.setMiddleName("");
		identity.setMothersMaidenName("Tuk");
		identity.setMotherTongue("Hobbit-Westron");
		identity.setNationality("Mensch");
		identity.setPrefix("");
		identity.setRace("Halbling");
		identity.setReligion("");
		identity.setSuffix("");
		identity.setValue1("1");
		identity.setValue2("2");
		identity.setValue3("3");
		identity.setValue4("4");
		identity.setValue5("5");
		identity.setValue6("6");
		identity.setValue7("7");
		identity.setValue8("8");
		identity.setValue9("9");
		identity.setValue10("10");

		// Generate contact data
		List<ContactOutDTO> contacts = new ArrayList<>();
		ContactOutDTO contact = new ContactOutDTO();
		contact.setCity("Beutelsend");
		contact.setCountry("Auenland");
		contact.setCountryCode("");
		contact.setDistrict("Hobbingen");
		contact.setEmail("");
		contact.setExternalDate(new Date());
		contact.setMunicipalityKey("");
		contact.setPhone("");
		contact.setState("Mittelerde");
		contact.setStreet("Weg 1");
		contact.setZipCode("123");
		contacts.add(contact);
		identity.setContacts(contacts);

		return identity;
	}
}
