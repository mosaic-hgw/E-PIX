package org.emau.icmvc.ttp.epix.frontend.controller;

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

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.emau.icmvc.ttp.epix.common.exception.InvalidParameterException;
import org.emau.icmvc.ttp.epix.common.exception.MPIException;
import org.emau.icmvc.ttp.epix.common.exception.UnknownObjectException;
import org.emau.icmvc.ttp.epix.common.model.ContactInDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentifierDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentifierDomainDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityInBaseDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityInDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityOutDTO;
import org.emau.icmvc.ttp.epix.common.model.RequestConfig;
import org.emau.icmvc.ttp.epix.common.model.ResponseEntryDTO;
import org.emau.icmvc.ttp.epix.common.model.SourceDTO;
import org.emau.icmvc.ttp.epix.common.model.enums.Gender;
import org.emau.icmvc.ttp.epix.common.model.enums.MatchStatus;
import org.emau.icmvc.ttp.epix.common.model.enums.RequestSaveAction;
import org.emau.icmvc.ttp.epix.frontend.controller.common.AbstractEpixWebBean;
import org.emau.icmvc.ttp.epix.frontend.model.EpixWebFile;
import org.emau.icmvc.ttp.epix.frontend.model.WebPerson;
import org.emau.icmvc.ttp.epix.frontend.model.WebPersonField;

@ViewScoped
@ManagedBean(name = "importController")
public class ImportController extends AbstractEpixWebBean
{
	// File
	private EpixWebFile webFile;
	private final List<WebPerson> successfulImports = new ArrayList<>();
	private final List<WebPerson> failedImports = new ArrayList<>();
	// Options
	private SourceDTO selectedSource;
	private boolean detectUpdatesInPerfectMatch = false;
	private boolean dontSaveOnPerfectMatch = false;
	private boolean forceUpdate = false;
	private boolean preview = false;
	// Progress bar
	private int sum;
	private int progress;

	@PostConstruct
	public void init()
	{
		webFile = new EpixWebFile(languageBean, domainSelector.getSelectedDomainConfiguration());
		onNewUpload();
	}

	public void onNewUpload()
	{
		webFile.onNewUpload();

		successfulImports.clear();
		failedImports.clear();

		selectedSource = null;
		detectUpdatesInPerfectMatch = false;
		dontSaveOnPerfectMatch = false;
		forceUpdate = false;

		sum = 0;
		progress = 0;
	}

	public void onImport()
	{
		// Persons
		List<WebPerson> personsToImport = new ArrayList<>();

		webFile.generateColumnTypeIndex();

		if (!webFile.checkActiveColumnsNotTypeUnkown())
		{
			this.logMessage(getBundle().getString("import.error.columnTypeMissing"), Severity.ERROR, Severity.WARN);
			return;
		}

		if (!webFile.checkColumnTypesUnique())
		{
			this.logMessage(getBundle().getString("import.error.columnsNotUnique"), Severity.ERROR, Severity.WARN);
			return;
		}

		if (!webFile.checkRequiredTypesPresent(getRequiredTypes()))
		{
			this.logMessage(getBundle().getString("import.error.requiredColumnMissing"), Severity.ERROR, Severity.WARN);
			return;
		}

		for (List<String> personData : webFile.getElements())
		{
			personsToImport.add(parsePerson(personData));
		}
		sum = personsToImport.size();

		RequestConfig saveConfig = new RequestConfig(dontSaveOnPerfectMatch ? RequestSaveAction.DONT_SAVE_ON_PERFECT_MATCH_EXCEPT_CONTACTS : RequestSaveAction.SAVE_ALL, false);

		if (preview)
		{
			saveConfig.setSaveAction(RequestSaveAction.DONT_SAVE);
		}

		for (WebPerson person : personsToImport)
		{
			try
			{
				// Result for imported person
				ResponseEntryDTO importedPerson;

				// Update existing persons (not possible in Batch yet)
				if (webFile.getColumnTypeIndex().containsKey(WebPersonField.MPI.name()))
				{

					if (domainSelector.getSelectedDomainConfiguration().isUseNotifications())
					{
						importedPerson = serviceWithNotification.updatePersonWithConfig(NOTIFICATION_CLIENT_ID, domainSelector.getSelectedDomainName(), person.getMpiId(),
								new IdentityInDTO(person.getIdentity(), person.getContacts()), selectedSource.getName(), forceUpdate, null,
								saveConfig);
					}
					else
					{
						importedPerson = service.updatePersonWithConfig(domainSelector.getSelectedDomainName(), person.getMpiId(),
								new IdentityInDTO(person.getIdentity(), person.getContacts()), selectedSource.getName(), forceUpdate, null,
								saveConfig);
					}
				}
				// New persons (Request MPI)
				else
				{
					if (domainSelector.getSelectedDomainConfiguration().isUseNotifications())
					{
						importedPerson = serviceWithNotification.requestMPIWithConfig(NOTIFICATION_CLIENT_ID, domainSelector.getSelectedDomainName(),
								new IdentityInDTO(person.getIdentity(), person.getContacts()), selectedSource.getName(), null, saveConfig);
					}
					else
					{
						importedPerson = service.requestMPIWithConfig(domainSelector.getSelectedDomainName(),
								new IdentityInDTO(person.getIdentity(), person.getContacts()), selectedSource.getName(), null, saveConfig);
					}
				}

				if (importedPerson != null)
				{
					// Compare updates in non matching fields on perfect match
					if (detectUpdatesInPerfectMatch)
					{
						if (MatchStatus.PERFECT_MATCH.equals(importedPerson.getMatchStatus()))
						{
							if (!compareIdentitiesEqual(person.getIdentity(), importedPerson.getPerson().getReferenceIdentity()))
							{
								importedPerson.setMatchStatus(MatchStatus.PERFECT_MATCH_WITH_UPDATE);
							}
						}
					}

					successfulImports.add(new WebPerson(importedPerson.getPerson().getReferenceIdentity(), importedPerson.getPerson().getReferenceIdentity().getContacts(),
							importedPerson.getPerson().getMpiId().getValue(), importedPerson.getMatchStatus()));
				}
				else
				{
					failedImports.add(person);
				}
			}
			catch (UnknownObjectException | InvalidParameterException e)
			{
				person.setErrorMsg(person.getErrorMsg() == null ? e.getLocalizedMessage() : person.getErrorMsg());
				failedImports.add(person);
				logger.error(e.getLocalizedMessage());
			}
			catch (MPIException e)
			{
				logMPIException(e);
			}
			progress++;
		}

		if (!failedImports.isEmpty())
		{
			Object[] args = { failedImports.size() };
			logMessage(new MessageFormat(getBundle().getString("import.error.invalidParameter")).format(args), Severity.WARN, true, true);
		}

		if (!successfulImports.isEmpty())
		{
			logMessage(successfulImports.size() + " " + getBundle().getString("import.info.imported"), Severity.INFO);
		}
		personsToImport.clear();
	}

	public void onDownload()
	{
		webFile.onDownload("Download import result");
	}

	private List<IdentityInDTO> getIdentitiesFromImportPersons(List<WebPerson> list)
	{
		return list.stream()
				.map(e -> new IdentityInDTO(e.getIdentity(), e.getContacts()))
				.collect(Collectors.toList());
	}

	public Integer getProgress()
	{
		if (sum == 0)
		{
			return 1;
		}
		else
		{
			int result = progress * 100 / sum;
			return result == 0 ? 1 : result;
		}
	}

	public List<WebPersonField> getRequiredTypes()
	{
		List<WebPersonField> result = new ArrayList<>();
		for (WebPersonField type : WebPersonField.values())
		{
			if (required(type.name()))
			{
				result.add(type);
			}
		}
		return result;
	}

	/**
	 * Parses a record (List of String entries) to PatientModel. The record
	 * should be im the Form:
	 * {LocalID,Degree,LastName,MotherMaidensname,MiddleName,FirstName,BirthDate
	 * ,BirthPlace,Gender,Nationality,
	 * value1,value2,value3,value4,valu5,value6,value7,value8,value9,value10,
	 * (Street,PostCode,City,State,Country,Phone,E-Mail)*}
	 *
	 * @param personData
	 *            List of String entries of a patient record
	 * @return PatientModel from data
	 */
	WebPerson parsePerson(List<String> personData)
	{
		WebPerson person = new WebPerson();
		person.setMpiId(getValueForType(personData, WebPersonField.MPI.name()));

		List<IdentifierDTO> identifiers = new ArrayList<>();
		person.setIdentifiers(identifiers);

		for (IdentifierDomainDTO identifierDomain : getIdentifierDomainsFiltered())
		{
			String idValue = getValueForType(personData, "localId." + identifierDomain.getName());
			if (idValue != null)
			{
				identifiers.add(new IdentifierDTO(idValue, null, null, identifierDomain));
			}
		}

		person.setDegree(getValueForType(personData, WebPersonField.degree.name()));
		person.setLastName(getValueForType(personData, WebPersonField.lastName.name()));
		person.setMothersMaidenName(getValueForType(personData, WebPersonField.mothersMaidenName.name()));
		person.setMiddleName(getValueForType(personData, WebPersonField.middleName.name()));
		person.setFirstName(getValueForType(personData, WebPersonField.firstName.name()));
		person.setBirthDate(parseDateString(getValueForType(personData, WebPersonField.birthDate.name()), WebPersonField.birthDate));
		person.setBirthPlace(getValueForType(personData, WebPersonField.birthPlace.name()));
		String gender = getValueForType(personData, WebPersonField.gender.name());
		if (gender == null || gender.isEmpty())
		{
			person.setGender(null);
		}
		else
		{
			switch (gender.toUpperCase())
			{
				case "M":
				case "MALE":
				case "MÄNNLICH":
					person.setGender(Gender.M);
					break;
				case "F":
				case "FEMALE":
				case "W":
				case "WEIBLICH":
					person.setGender(Gender.F);
					break;
				case "O":
				case "OTHER":
				case "S":
				case "SONSTIGES":
					person.setGender(Gender.O);
					break;
				case "U":
				case "UNKOWN":
				case "UNBEKANNT":
					person.setGender(Gender.U);
					break;
				case "X":
				case "DIVERS":
				case "DIVERSE":
					person.setGender(Gender.X);
					break;
				default:
					person.setErrorMsg(getBundle().getString("import.warn.parseGender") + gender);
					break;
			}
		}
		person.setNationality(getValueForType(personData, WebPersonField.nationality.name()));
		person.setMotherTongue(getValueForType(personData, WebPersonField.motherTongue.name()));
		person.setCivilStatus(getValueForType(personData, WebPersonField.civilStatus.name()));
		person.setRace(getValueForType(personData, WebPersonField.race.name()));
		person.setReligion(getValueForType(personData, WebPersonField.religion.name()));
		person.setPrefix(getValueForType(personData, WebPersonField.prefix.name()));
		person.setSuffix(getValueForType(personData, WebPersonField.suffix.name()));
		person.setExternalDate(parseDateString(getValueForType(personData, WebPersonField.externalDate.name()), WebPersonField.externalDate));
		person.setValue1(getValueForType(personData, WebPersonField.value1.name()));
		person.setValue2(getValueForType(personData, WebPersonField.value2.name()));
		person.setValue3(getValueForType(personData, WebPersonField.value3.name()));
		person.setValue4(getValueForType(personData, WebPersonField.value4.name()));
		person.setValue5(getValueForType(personData, WebPersonField.value5.name()));
		person.setValue6(getValueForType(personData, WebPersonField.value6.name()));
		person.setValue7(getValueForType(personData, WebPersonField.value7.name()));
		person.setValue8(getValueForType(personData, WebPersonField.value8.name()));
		person.setValue9(getValueForType(personData, WebPersonField.value9.name()));
		person.setValue10(getValueForType(personData, WebPersonField.value10.name()));

		List<ContactInDTO> contacts = new ArrayList<>();
		ContactInDTO contact = new ContactInDTO();
		// Build street and number from individual columns if not set in
		// combined column
		String streetAndNumber = getValueForType(personData, WebPersonField.street.name());
		String street = getValueForType(personData, WebPersonField.streetOnly.name());
		String number = getValueForType(personData, WebPersonField.number.name());
		if (streetAndNumber == null)
		{
			if (street != null)
			{
				streetAndNumber = street;
			}
			if (streetAndNumber != null && number != null)
			{
				streetAndNumber += " " + number;
			}
		}
		contact.setStreet(streetAndNumber);
		contact.setZipCode(getValueForType(personData, WebPersonField.zipCode.name()));
		contact.setCity(getValueForType(personData, WebPersonField.city.name()));
		contact.setState(getValueForType(personData, WebPersonField.state.name()));
		contact.setCountry(getValueForType(personData, WebPersonField.country.name()));
		contact.setCountryCode(getValueForType(personData, WebPersonField.countryCode.name()));
		contact.setDistrict(getValueForType(personData, WebPersonField.district.name()));
		contact.setMunicipalityKey(getValueForType(personData, WebPersonField.municipalityKey.name()));
		contact.setPhone(getValueForType(personData, WebPersonField.phone.name()));
		contact.setEmail(getValueForType(personData, WebPersonField.email.name()));
		contact.setExternalDate(parseDateString(getValueForType(personData, WebPersonField.contactExternalDate.name()), WebPersonField.contactExternalDate));
		if (!(contact.getStreet() == null && contact.getZipCode() == null && contact.getCity() == null && contact.getState() == null && contact.getCountry() == null && contact.getCountryCode() == null
				&& contact.getDistrict() == null && contact.getMunicipalityKey() == null && contact.getPhone() == null
				&& contact.getEmail() == null && contact.getExternalDate() == null))
		{
			contacts.add(contact);
		}
		if (!contacts.isEmpty())
		{
			person.setContacts(contacts);
		}

		return person;
	}

	private Date parseDateString(String string, WebPersonField type)
	{
		List<SimpleDateFormat> simpleDateFormats = Arrays.asList(
				new SimpleDateFormat("dd.MM.yyyy"),
				new SimpleDateFormat("yyyy-MM-dd"),
				new SimpleDateFormat("yyyyMMdd"),
				new SimpleDateFormat("MM/dd/yyyy"));

		if (string != null && !string.isEmpty())
		{
			for (SimpleDateFormat simpleDateFormat : simpleDateFormats)
			{
				try
				{
					return simpleDateFormat.parse(string);
				}
				catch (Exception e)
				{
					// Expected, try next simpleDateFormat
					// Create log message if none works
				}
			}

			Object[] args = { string };
			logMessage(new MessageFormat(getBundle().getString("import.warn.parse." + type.toString())).format(args), Severity.WARN);
		}
		return null;
	}

	/**
	 * Get the value of a record at the given column
	 *
	 * @param record
	 *            with data
	 * @param type
	 *            to get the value from
	 * @return value for type
	 */
	private String getValueForType(List<String> record, String type)
	{
		if (webFile.getColumnTypeIndex().containsKey(type))
		{
			// Get a value from the uploaded row (at the previous remembered position of this type)
			int index = webFile.getColumnTypeIndex().get(type);

			if (record.size() > index)
			{
				return record.get(index).equals("null") ? null : record.get(index);
			}
		}

		return null;
	}

	private boolean compareIdentitiesEqual(IdentityInBaseDTO id1, IdentityOutDTO id2)
	{
		return Objects.equals(id1.getDegree(), id2.getDegree())
				&& Objects.equals(id1.getLastName(), id2.getLastName())
				&& Objects.equals(id1.getMothersMaidenName(), id2.getMothersMaidenName())
				&& Objects.equals(id1.getMiddleName(), id2.getMiddleName())
				&& Objects.equals(id1.getFirstName(), id2.getFirstName())
				&& Objects.equals(id1.getBirthDate(), id2.getBirthDate())
				&& Objects.equals(id1.getBirthPlace(), id2.getBirthPlace())
				&& Objects.equals(id1.getGender(), id2.getGender())
				&& Objects.equals(id1.getNationality(), id2.getNationality())
				&& Objects.equals(id1.getMotherTongue(), id2.getMotherTongue())
				&& Objects.equals(id1.getCivilStatus(), id2.getCivilStatus())
				&& Objects.equals(id1.getRace(), id2.getRace())
				&& Objects.equals(id1.getReligion(), id2.getReligion())
				&& Objects.equals(id1.getPrefix(), id2.getPrefix())
				&& Objects.equals(id1.getSuffix(), id2.getSuffix())
				&& Objects.equals(id1.getExternalDate(), id2.getExternalDate())
				&& Objects.equals(id1.getValue1(), id2.getValue1())
				&& Objects.equals(id1.getValue2(), id2.getValue2())
				&& Objects.equals(id1.getValue3(), id2.getValue3())
				&& Objects.equals(id1.getValue4(), id2.getValue4())
				&& Objects.equals(id1.getValue5(), id2.getValue5())
				&& Objects.equals(id1.getValue6(), id2.getValue6())
				&& Objects.equals(id1.getValue7(), id2.getValue7())
				&& Objects.equals(id1.getValue8(), id2.getValue8())
				&& Objects.equals(id1.getValue9(), id2.getValue9())
				&& Objects.equals(id1.getValue10(), id2.getValue10());
	}

	public EpixWebFile getWebFile()
	{
		return webFile;
	}

	public void setWebFile(EpixWebFile webFile)
	{
		this.webFile = webFile;
	}

	public SourceDTO getSelectedSource()
	{
		return selectedSource;
	}

	public void setSelectedSource(SourceDTO selectedSource)
	{
		this.selectedSource = selectedSource;
	}

	public boolean getDetectUpdatesInPerfectMatch()
	{
		return detectUpdatesInPerfectMatch;
	}

	public void setDetectUpdatesInPerfectMatch(boolean detectUpdatesInPerfectMatch)
	{
		this.detectUpdatesInPerfectMatch = detectUpdatesInPerfectMatch;
	}

	public boolean getDontSaveOnPerfectMatch()
	{
		return dontSaveOnPerfectMatch;
	}

	public void setDontSaveOnPerfectMatch(boolean dontSaveOnPerfectMatch)
	{
		this.dontSaveOnPerfectMatch = dontSaveOnPerfectMatch;
	}

	public boolean getForceUpdate()
	{
		return !forceUpdate;
	}

	public void setForceUpdate(boolean forceUpdate)
	{
		this.forceUpdate = !forceUpdate;
	}

	public boolean isPreview()
	{
		return preview;
	}

	public void setPreview(boolean preview)
	{
		this.preview = preview;
	}

	public List<WebPerson> getSuccessfulImports()
	{
		return successfulImports;
	}

	public List<WebPerson> getFailedImports()
	{
		return failedImports;
	}
}
