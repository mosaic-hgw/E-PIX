package org.emau.icmvc.ttp.epix.frontend.controller.component;

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
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.emau.icmvc.ttp.epix.common.model.IdentifierDTO;
import org.emau.icmvc.ttp.epix.frontend.model.WebPerson;
import org.emau.icmvc.ttp.epix.frontend.model.WebPersonField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersonsTable
{
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final List<WebPersonField> fields;
	private final List<String> identifierDomains;

	public PersonsTable (List<WebPerson> persons)
	{
		logger.debug("Loading fields and identifier domains for {} persons", persons.size());

		fields = new ArrayList<>();
		identifierDomains = new ArrayList<>();

		for (WebPerson person : persons)
		{
			for (IdentifierDTO identifierDTO : person.getIdentifiers())
			{
				if (!identifierDomains.contains(identifierDTO.getIdentifierDomain().getName()))
				{
					identifierDomains.add(identifierDTO.getIdentifierDomain().getName());
				}
			}

			if (!fields.contains(WebPersonField.firstName)
					&& StringUtils.isNotEmpty(person.getFirstName()))
			{
				fields.add(WebPersonField.firstName);
			}
			if (!fields.contains(WebPersonField.middleName)
					&& StringUtils.isNotEmpty(person.getMiddleName()))
			{
				fields.add(WebPersonField.middleName);
			}
			if (!fields.contains(WebPersonField.lastName)
					&& StringUtils.isNotEmpty(person.getLastName()))
			{
				fields.add(WebPersonField.lastName);
			}
			if (!fields.contains(WebPersonField.prefix)
					&& StringUtils.isNotEmpty(person.getPrefix()))
			{
				fields.add(WebPersonField.prefix);
			}
			if (!fields.contains(WebPersonField.suffix)
					&& StringUtils.isNotEmpty(person.getSuffix()))
			{
				fields.add(WebPersonField.suffix);
			}
			if (!fields.contains(WebPersonField.birthDate)
					&& person.getBirthDate() != null)
			{
				fields.add(WebPersonField.birthDate);
			}
			if (!fields.contains(WebPersonField.gender)
					&& person.getGender() != null)
			{
				fields.add(WebPersonField.gender);
			}
			if (!fields.contains(WebPersonField.birthPlace)
					&& StringUtils.isNotEmpty(person.getBirthPlace()))
			{
				fields.add(WebPersonField.birthPlace);
			}
			if (!fields.contains(WebPersonField.race)
					&& StringUtils.isNotEmpty(person.getRace()))
			{
				fields.add(WebPersonField.race);
			}
			if (!fields.contains(WebPersonField.religion)
					&& StringUtils.isNotEmpty(person.getReligion()))
			{
				fields.add(WebPersonField.religion);
			}
			if (!fields.contains(WebPersonField.mothersMaidenName)
					&& StringUtils.isNotEmpty(person.getMothersMaidenName()))
			{
				fields.add(WebPersonField.mothersMaidenName);
			}
			if (!fields.contains(WebPersonField.degree)
					&& StringUtils.isNotEmpty(person.getDegree()))
			{
				fields.add(WebPersonField.degree);
			}
			if (!fields.contains(WebPersonField.motherTongue)
					&& StringUtils.isNotEmpty(person.getMotherTongue()))
			{
				fields.add(WebPersonField.motherTongue);
			}
			if (!fields.contains(WebPersonField.nationality)
					&& StringUtils.isNotEmpty(person.getNationality()))
			{
				fields.add(WebPersonField.nationality);
			}
			if (!fields.contains(WebPersonField.civilStatus)
					&& StringUtils.isNotEmpty(person.getCivilStatus()))
			{
				fields.add(WebPersonField.civilStatus);
			}
			if (!fields.contains(WebPersonField.externalDate)
					&& person.getExternalDate() != null)
			{
				fields.add(WebPersonField.externalDate);
			}
			if (!fields.contains(WebPersonField.value1)
					&& StringUtils.isNotEmpty(person.getValue1()))
			{
				fields.add(WebPersonField.value1);
			}
			if (!fields.contains(WebPersonField.value2)
					&& StringUtils.isNotEmpty(person.getValue2()))
			{
				fields.add(WebPersonField.value2);
			}
			if (!fields.contains(WebPersonField.value3)
					&& StringUtils.isNotEmpty(person.getValue3()))
			{
				fields.add(WebPersonField.value3);
			}
			if (!fields.contains(WebPersonField.value4)
					&& StringUtils.isNotEmpty(person.getValue4()))
			{
				fields.add(WebPersonField.value4);
			}
			if (!fields.contains(WebPersonField.value5)
					&& StringUtils.isNotEmpty(person.getValue5()))
			{
				fields.add(WebPersonField.value5);
			}
			if (!fields.contains(WebPersonField.value6)
					&& StringUtils.isNotEmpty(person.getValue6()))
			{
				fields.add(WebPersonField.value6);
			}
			if (!fields.contains(WebPersonField.value7)
					&& StringUtils.isNotEmpty(person.getValue7()))
			{
				fields.add(WebPersonField.value7);
			}
			if (!fields.contains(WebPersonField.value8)
					&& StringUtils.isNotEmpty(person.getValue8()))
			{
				fields.add(WebPersonField.value8);
			}
			if (!fields.contains(WebPersonField.value9)
					&& StringUtils.isNotEmpty(person.getValue9()))
			{
				fields.add(WebPersonField.value9);
			}
			if (!fields.contains(WebPersonField.value10)
					&& StringUtils.isNotEmpty(person.getValue10()))
			{
				fields.add(WebPersonField.value10);
			}
			if (!fields.contains(WebPersonField.street)
					&& StringUtils.isNotEmpty(person.getContact().getStreet()))
			{
				fields.add(WebPersonField.street);
			}
			if (!fields.contains(WebPersonField.zipCode)
					&& StringUtils.isNotEmpty(person.getContact().getZipCode()))
			{
				fields.add(WebPersonField.zipCode);
			}
			if (!fields.contains(WebPersonField.city)
					&& StringUtils.isNotEmpty(person.getContact().getCity()))
			{
				fields.add(WebPersonField.city);
			}
			if (!fields.contains(WebPersonField.state)
					&& StringUtils.isNotEmpty(person.getContact().getState()))
			{
				fields.add(WebPersonField.state);
			}
			if (!fields.contains(WebPersonField.country)
					&& StringUtils.isNotEmpty(person.getContact().getCountry()))
			{
				fields.add(WebPersonField.country);
			}
			if (!fields.contains(WebPersonField.countryCode)
					&& StringUtils.isNotEmpty(person.getContact().getCountryCode()))
			{
				fields.add(WebPersonField.countryCode);
			}
			if (!fields.contains(WebPersonField.email)
					&& StringUtils.isNotEmpty(person.getContact().getEmail()))
			{
				fields.add(WebPersonField.email);
			}
			if (!fields.contains(WebPersonField.phone)
					&& StringUtils.isNotEmpty(person.getContact().getPhone()))
			{
				fields.add(WebPersonField.phone);
			}
			if (!fields.contains(WebPersonField.district)
					&& StringUtils.isNotEmpty(person.getContact().getDistrict()))
			{
				fields.add(WebPersonField.district);
			}
			if (!fields.contains(WebPersonField.municipalityKey)
					&& StringUtils.isNotEmpty(person.getContact().getMunicipalityKey()))
			{
				fields.add(WebPersonField.municipalityKey);
			}
			if (!fields.contains(WebPersonField.contactExternalDate)
					&& person.getContact().getExternalDate() != null)
			{
				fields.add(WebPersonField.contactExternalDate);
			}
			if (!fields.contains(WebPersonField.MPI)
					&& StringUtils.isNotEmpty(person.getMpiId()))
			{
				fields.add(WebPersonField.MPI);
			}
			if (!fields.contains(WebPersonField.matchStatus)
					&& person.getMatchStatus() != null)
			{
				fields.add(WebPersonField.matchStatus);
			}
			if (!fields.contains(WebPersonField.errorMsg)
					&& StringUtils.isNotEmpty(person.getErrorMsg()))
			{
				fields.add(WebPersonField.errorMsg);
			}
		}

		logger.debug("Found {} fields and {} identifier domains", fields.size(), identifierDomains.size());
	}

	public boolean hasField(WebPersonField field)
	{
		if (fields == null)
		{
			logger.info("personTable field list not yet initialized");
			return false;
		}
		else
		{
			return fields.contains(field);
		}
	}

	public boolean hasIdentifierDomain(String identifierDomainName)
	{
		if (identifierDomains == null)
		{
			logger.info("Identifier domain list not yet initialized");
			return false;
		}
		else
		{
			return identifierDomains.contains(identifierDomainName);
		}
	}
}
