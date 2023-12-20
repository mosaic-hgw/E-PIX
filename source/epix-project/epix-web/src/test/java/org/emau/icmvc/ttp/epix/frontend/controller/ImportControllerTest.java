package org.emau.icmvc.ttp.epix.frontend.controller;

/*-
 * ###license-information-start###
 * E-PIX - Enterprise Patient Identifier Cross-referencing
 * __
 * Copyright (C) 2009 - 2023 Trusted Third Party of the University Medicine Greifswald
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


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;

import org.emau.icmvc.ttp.epix.common.exception.InvalidParameterException;
import org.emau.icmvc.ttp.epix.common.exception.MPIException;
import org.emau.icmvc.ttp.epix.common.exception.UnknownObjectException;
import org.emau.icmvc.ttp.epix.common.model.IdentifierDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentifierDomainDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityInBaseDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityOutDTO;
import org.emau.icmvc.ttp.epix.common.model.PersonDTO;
import org.emau.icmvc.ttp.epix.common.model.ResponseEntryDTO;
import org.emau.icmvc.ttp.epix.common.model.SourceDTO;
import org.emau.icmvc.ttp.epix.common.model.config.ConfigurationContainer;
import org.emau.icmvc.ttp.epix.common.model.enums.FieldName;
import org.emau.icmvc.ttp.epix.common.model.enums.MatchStatus;
import org.emau.icmvc.ttp.epix.frontend.controller.component.DomainSelector;
import org.emau.icmvc.ttp.epix.frontend.controller.testtools.EpixWebTest;
import org.emau.icmvc.ttp.epix.frontend.model.EpixWebFile;
import org.emau.icmvc.ttp.epix.frontend.model.WebPerson;
import org.emau.icmvc.ttp.epix.frontend.model.WebPersonField;
import org.emau.icmvc.ttp.epix.frontend.util.EpixHelper;
import org.icmvc.ttp.web.controller.LanguageBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ImportControllerTest extends EpixWebTest
{
	@InjectMocks
	ImportController importController;

	EpixWebFile epixWebFile;
	UploadedFile uploadFile;
	FileUploadEvent event;
	LanguageBean languageBean;
	ConfigurationContainer configurationContainer;
	DomainSelector domainSelector;
	SourceDTO selectedSource;
	EpixHelper epixHelper;

	@BeforeEach
	void setUpImportControllerTest()
	{
		initMocks(this);
		
		uploadFile = mock(UploadedFile.class);

		event = mock(FileUploadEvent.class);
		when(event.getFile()).thenReturn(uploadFile);

		languageBean = mock(LanguageBean.class);
		when(languageBean.getSupportedLanguages()).thenReturn(Arrays.asList("de", "en"));
		importController.setLanguageBean(languageBean);

		configurationContainer = mock(ConfigurationContainer.class);
		epixWebFile = new EpixWebFile(languageBean, configurationContainer, new ArrayList<>());
		epixWebFile.setContainsHeader(true);
		importController.setWebFile(epixWebFile);

		domainSelector = mock(DomainSelector.class, RETURNS_DEEP_STUBS);
		when(domainSelector.getSelectedDomainConfiguration()).thenReturn(configurationContainer);
		when(domainSelector.getSelectedDomain().getMpiDomain().getName()).thenReturn("MPI");
		epixHelper = mock(EpixHelper.class, RETURNS_DEEP_STUBS);
		when(epixHelper.getDomainSelector()).thenReturn(domainSelector);
		when(epixHelper.getManagementService()).thenReturn(managementService);
		importController.setEpixHelper(epixHelper);

		selectedSource = mock(SourceDTO.class);
		when(selectedSource.getName()).thenReturn("Source");
		importController.setSelectedSource(selectedSource);

		when(managementService.getIdentifierDomains()).thenReturn(new ArrayList<>());
	}

	@Test
	void onUpload()
	{
		// Arrange
		epixWebFile = new EpixWebFile(languageBean, configurationContainer, new ArrayList<>());
		String content = "Max";
		when(uploadFile.getContent()).thenReturn(content.getBytes());

		// Act
		importController.init();
		epixWebFile.onUpload(event);

		// Assert
		assertEquals(1, epixWebFile.getColumns().size());
		assertEquals(1, epixWebFile.getElements().size());
		assertEquals(1, epixWebFile.getElements().get(0).size());
	}

	@Test
	void onNewUpload()
	{
		// Arrange
		importController.setDetectUpdatesInPerfectMatch(true);
		importController.setWebFile(null);
		importController.init();

		// Act
		importController.onNewUpload();

		// Assert
		assertFalse(importController.getDetectUpdatesInPerfectMatch());
		assertFalse(importController.getWebFile().isContainsHeader());
		assertEquals(0, importController.getSuccessfulImports().size());
		assertEquals(0, importController.getFailedImports().size());
	}

	@Test
	void onReUpload()
	{
		// Arrange
		epixWebFile.setContainsHeader(true);

		// Act
		importController.onNewUpload();

		// Assert
		assertTrue(importController.getWebFile().isContainsHeader());
		assertEquals(0, importController.getSuccessfulImports().size());
		assertEquals(0, importController.getFailedImports().size());

		// Arrange
		epixWebFile.setContainsHeader(false);
		importController.setWebFile(epixWebFile);

		// Act
		importController.onNewUpload();

		// Assert
		assertFalse(importController.getWebFile().isContainsHeader());
	}

	@Test
	void detectColumnTypes()
	{
		// Arrange
		String content = "firstname;last_name;bIrTh PlAcE;*birthDate;geschlecht\n"
				+ "Max;Mustermann;Greifswald;01.01.1990";
		when(uploadFile.getContent()).thenReturn(content.getBytes());

		// Act
		epixWebFile.onUpload(event);

		// Assert
		// Every column of the test file should be recognized and mapped to the correct enum value
		assertEquals(WebPersonField.firstName.name(), epixWebFile.getColumnTypeMapping().get(epixWebFile.getColumns().get(0)));
		assertEquals(WebPersonField.lastName.name(), epixWebFile.getColumnTypeMapping().get(epixWebFile.getColumns().get(1)));
		assertEquals(WebPersonField.birthPlace.name(), epixWebFile.getColumnTypeMapping().get(epixWebFile.getColumns().get(2)));
		assertEquals(WebPersonField.birthDate.name(), epixWebFile.getColumnTypeMapping().get(epixWebFile.getColumns().get(3)));
		assertEquals(WebPersonField.gender.name(), epixWebFile.getColumnTypeMapping().get(epixWebFile.getColumns().get(4)));
	}

	@Test
	void allowOnlyEnabledValueFields()
	{
		// Arrange
		Map<String, String> valueFieldMapping = new HashMap<>();
		valueFieldMapping.put("value1", "A");
		valueFieldMapping.put("value3", "C");
		when(configurationContainer.getValueFieldMapping()).thenReturn(valueFieldMapping);

		// Act
		List<String> types = epixWebFile.getTypes();

		// Assert
		assertTrue(types.contains(WebPersonField.value1.name()));
		assertFalse(types.contains(WebPersonField.value2.name()));
		assertTrue(types.contains(WebPersonField.value3.name()));
	}

	@Test
	void detectCustomColumnTypes()
	{
		// Arrange
		String content = "field1;Field 2;CcCc;Feld 4\n"
				+ "Max;Mustermann;Greifswald;01.01.1990";
		when(uploadFile.getContent()).thenReturn(content.getBytes());

		Map<String, String> valueFieldMapping = new HashMap<>();
		valueFieldMapping.put("value1", "A");
		valueFieldMapping.put("value2", "B");
		valueFieldMapping.put("value3", "CC-CC");
		valueFieldMapping.put("value4", "D");
		when(configurationContainer.getValueFieldMapping()).thenReturn(valueFieldMapping);

		// Act
		epixWebFile.onUpload(event);

		// Assert
		assertEquals(WebPersonField.value1.name(), epixWebFile.getColumnTypeMapping().get(epixWebFile.getColumns().get(0)));
		assertEquals(WebPersonField.value2.name(), epixWebFile.getColumnTypeMapping().get(epixWebFile.getColumns().get(1)));
		assertEquals(WebPersonField.value3.name(), epixWebFile.getColumnTypeMapping().get(epixWebFile.getColumns().get(2)));
		assertEquals(WebPersonField.value4.name(), epixWebFile.getColumnTypeMapping().get(epixWebFile.getColumns().get(3)));
	}
	
	@Test
	void detectIdentifierDomain()
	{
		// Arrange
		String content = "MyIdentifierDomain;Vorname\n"
				+ "id-001;Max";
		when(uploadFile.getContent()).thenReturn(content.getBytes());

		IdentifierDomainDTO myIdentifierDomain = mock(IdentifierDomainDTO.class);
		when(myIdentifierDomain.getName()).thenReturn("MyIdentifierDomain");
		List<IdentifierDomainDTO> identifierDomains = List.of(myIdentifierDomain);

		epixWebFile = new EpixWebFile(languageBean, configurationContainer, identifierDomains);
		epixWebFile.setContainsHeader(true);

		// Act
		epixWebFile.onUpload(event);

		// Assert
		assertEquals("localId.MyIdentifierDomain", epixWebFile.getColumnTypeMapping().get(epixWebFile.getColumns().get(0)));
		assertEquals(WebPersonField.firstName.name(), epixWebFile.getColumnTypeMapping().get(epixWebFile.getColumns().get(1)));
	}

	@Test
	void onUploadWithMPI()
	{
		// Arrange
		String content = "MPI";
		when(uploadFile.getContent()).thenReturn(content.getBytes());

		// Act
		epixWebFile.onUpload(event);

		// Assert
		assertTrue(epixWebFile.hasMpi());
	}

	@Test
	void onUploadWithMPIButUnselected()
	{
		// Arrange
		String content = "MPI";
		when(uploadFile.getContent()).thenReturn(content.getBytes());

		// Act
		epixWebFile.onUpload(event);
		epixWebFile.setSelectedColumns(new ArrayList<>());

		// Assert
		assertFalse(epixWebFile.hasMpi());
	}

	@Test
	void onUploadWithoutMPI()
	{
		// Arrange
		String content = "Firstname";
		when(uploadFile.getContent()).thenReturn(content.getBytes());

		// Act
		epixWebFile.onUpload(event);

		// Assert
		assertFalse(epixWebFile.hasMpi());
	}

	@Test
	void onImport() throws MPIException, InvalidParameterException, UnknownObjectException
	{
		// Arrange
		String content = "Firstname\n"
				+ "Max";
		when(uploadFile.getContent()).thenReturn(content.getBytes());

		// Mock response of EJB service
		PersonDTO personDTO = deepMockPersonDTO("Max");
		ResponseEntryDTO responseEntryDTO = mock(ResponseEntryDTO.class);
		when(responseEntryDTO.getMatchStatus()).thenReturn(MatchStatus.NO_MATCH);
		when(responseEntryDTO.getPerson()).thenReturn(personDTO);
		when(service.requestMPIWithConfig(any(), any(), any(), any(), any())).thenReturn(responseEntryDTO);

		// Act
		epixWebFile.onUpload(event);
		importController.onImport();

		// Assert
		assertEquals(1, importController.getSuccessfulImports().size());
		assertEquals("Max", importController.getSuccessfulImports().get(0).getFirstName());
		assertEquals(0, importController.getFailedImports().size());
	}

	@Test
	void onImportRequiredFields() throws MPIException, InvalidParameterException, UnknownObjectException
	{
		// Arrange
		String content = "Firstname\n"
				+ "Max";
		when(uploadFile.getContent()).thenReturn(content.getBytes());

		// Set required fields
		List<FieldName> requiredFields = Collections.singletonList(FieldName.firstName);
		when(importController.getDomainSelector().getSelectedDomainConfiguration().getRequiredFields()).thenReturn(requiredFields);

		// Mock response of EJB service
		PersonDTO personDTO = deepMockPersonDTO("Max");
		ResponseEntryDTO responseEntryDTO = mock(ResponseEntryDTO.class);
		when(responseEntryDTO.getMatchStatus()).thenReturn(MatchStatus.NO_MATCH);
		when(responseEntryDTO.getPerson()).thenReturn(personDTO);
		when(service.requestMPIWithConfig(any(), any(), any(), any(), any())).thenReturn(responseEntryDTO);

		// Act
		epixWebFile.onUpload(event);
		importController.onImport();

		// Assert
		assertEquals(1, importController.getSuccessfulImports().size());
		assertEquals("Max", importController.getSuccessfulImports().get(0).getFirstName());
		assertEquals(0, importController.getFailedImports().size());
	}

	@Test
	void onImportError() throws MPIException, InvalidParameterException, UnknownObjectException
	{
		// TODO include Contact data

		// Arrange
		String content = "Firstname\n"
				+ "Max";
		when(uploadFile.getContent()).thenReturn(content.getBytes());

		// Mock response of EJB service
		when(service.requestMPIWithConfig(any(), any(), any(), any(), any())).thenReturn(null);

		// Act
		epixWebFile.onUpload(event);
		importController.onImport();

		// Assert
		assertEquals(0, importController.getSuccessfulImports().size());
		assertEquals(1, importController.getFailedImports().size());
		assertEquals("Max", importController.getFailedImports().get(0).getFirstName());
	}

	@Test
	void onImportSuccessAndError() throws MPIException, InvalidParameterException, UnknownObjectException
	{
		// Arrange
		String content = "Firstname\n"
				+ "Max\n"
				+ "Maria";
		when(uploadFile.getContent()).thenReturn(content.getBytes());

		// Mock response of EJB service
		PersonDTO personDTO1 = deepMockPersonDTO("Max");
		ResponseEntryDTO responseEntryDTO1 = mock(ResponseEntryDTO.class);
		when(responseEntryDTO1.getMatchStatus()).thenReturn(MatchStatus.NO_MATCH);
		when(responseEntryDTO1.getPerson()).thenReturn(personDTO1);
		when(service.requestMPIWithConfig(any(), any(), any(), any(), any())).thenReturn(responseEntryDTO1).thenReturn(null);

		// Act
		epixWebFile.onUpload(event);
		importController.onImport();

		// Assert
		assertEquals(1, importController.getSuccessfulImports().size());
		assertEquals("Max", importController.getSuccessfulImports().get(0).getFirstName());
		assertEquals(1, importController.getFailedImports().size());
		assertEquals("Maria", importController.getFailedImports().get(0).getFirstName());
	}

	@Test
	void OnImportErrorUnkownColumn()
	{
		// Arrange
		// Upload a column that cannot be auto deteted and will therefore be of type UNKOWN
		String content = "Test\n"
				+ "Max";
		when(uploadFile.getContent()).thenReturn(content.getBytes());

		// Act
		epixWebFile.onUpload(event);
		// Explicit select unkown column for import (not automatically selected)
		epixWebFile.setSelectedColumns(Collections.singletonList("Test"));
		importController.onImport();

		// Assert
		FacesMessage message = facesContext.getMessageList().get(0);
		assertEquals(FacesMessage.SEVERITY_ERROR, message.getSeverity());
		assertTrue(message.getSummary().contains(bundleDe.getString("import.error.columnTypeMissing")));
	}

	@Test
	void OnImportErrorColumnsNotUnique()
	{
		// Arrange
		// Upload two columns of the same time
		String content = "Firstname;First_Name\n"
				+ "Max;Muster";
		when(uploadFile.getContent()).thenReturn(content.getBytes());

		// Act
		epixWebFile.onUpload(event);
		importController.onImport();

		// Assert
		FacesMessage message = facesContext.getMessageList().get(0);
		assertEquals(FacesMessage.SEVERITY_ERROR, message.getSeverity());
		assertTrue(message.getSummary().contains(bundleDe.getString("import.error.columnsNotUnique")));
	}

	@Test
	void OnImportErrorRequiredColumnMissing()
	{
		// Arrange
		// Upload firstname column but lastname is also required
		String content = "Firstname\n"
				+ "Max";
		when(uploadFile.getContent()).thenReturn(content.getBytes());
		when(epixHelper.required(FieldName.firstName.name())).thenReturn(true);
		when(epixHelper.required(FieldName.lastName.name())).thenReturn(true);

		// Act
		epixWebFile.onUpload(event);
		importController.onImport();

		// Assert
		FacesMessage message = facesContext.getMessageList().get(0);
		assertEquals(FacesMessage.SEVERITY_ERROR, message.getSeverity());
		assertTrue(message.getSummary().contains(bundleDe.getString("import.error.requiredColumnMissing")));
	}

	@Test
	void onImportOnlySelectedFields()
	{
		// Arrange
		// Upload 3 columns
		String content = "Firstname;Middlename;Lastname";
		when(uploadFile.getContent()).thenReturn(content.getBytes());

		// Act
		epixWebFile.onUpload(event);
		// Only select the 1st and 3rd column
		epixWebFile.setSelectedColumns(Arrays.asList("Firstname", "Lastname"));
		epixWebFile.generateColumnTypeIndex();
		// Parse person data
		WebPerson importPerson = importController.parsePerson(Arrays.asList("Max", "Middle", "Muster"));

		// Assert
		// Parsed person should only have a firstname and lastname but no middlename
		assertEquals("Max", importPerson.getFirstName());
		assertEquals("Muster", importPerson.getLastName());
		assertNull(importPerson.getMiddleName());
	}

	@Test
	void onImportLocalIdentifiers()
	{
		// Arrange
		// Upload a file with firstname, lastname and two local identifiers
		String content = "Firstname;lid1;lid2;Lastname\n"
				+ "Max;P-100;C-100;Muster";
		when(uploadFile.getContent()).thenReturn(content.getBytes());

		// Prepare list of identifier domains
		IdentifierDomainDTO identifierDomain1 = mock(IdentifierDomainDTO.class);
		when(identifierDomain1.getName()).thenReturn("PATIENT");
		IdentifierDomainDTO identifierDomain2 = mock(IdentifierDomainDTO.class);
		when(identifierDomain2.getName()).thenReturn("CASE");
		List<IdentifierDomainDTO> identifierDomains = Arrays.asList(identifierDomain1, identifierDomain2);
		when(epixHelper.getIdentifierDomainsFiltered()).thenReturn(identifierDomains);

		// Act
		epixWebFile.onUpload(event);
		// Select all columns for import
		epixWebFile.setSelectedColumns(Arrays.asList("Firstname", "lid1", "lid2", "Lastname"));
		// Map uploaded identifier columns to identifier domains (usually done with selectbox in frontend)
		epixWebFile.getColumnTypeMapping().put("lid1", "localId.PATIENT");
		epixWebFile.getColumnTypeMapping().put("lid2", "localId.CASE");
		epixWebFile.generateColumnTypeIndex();
		// Parse person
		WebPerson importPerson = importController.parsePerson(Arrays.asList("Max", "P-100", "C-100", "Muster"));

		// Assert
		// Parsed person should have firstname, lastname and both local identifiers
		assertEquals("Max", importPerson.getFirstName());
		assertEquals("Muster", importPerson.getLastName());
		assertEquals("PATIENT", importPerson.getIdentifiers().get(0).getIdentifierDomain().getName());
		assertEquals("P-100", importPerson.getIdentifiers().get(0).getValue());
		assertEquals("CASE", importPerson.getIdentifiers().get(1).getIdentifierDomain().getName());
		assertEquals("C-100", importPerson.getIdentifiers().get(1).getValue());
	}

	@Test
	void detectDateFormat() throws ParseException
	{
		// Arrange
		String content = "birthDate";
		when(uploadFile.getContent()).thenReturn(content.getBytes());

		String date1 = "01.02.1990";
		String date2 = "1990-02-01";
		String date3 = "19900201";
		String date4 = "02/01/1990";
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd.MM.yyyy");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf4 = new SimpleDateFormat("MM/dd/yyyy");

		// Act
		epixWebFile.onUpload(event);
		epixWebFile.generateColumnTypeIndex();
		WebPerson importPerson1 = importController.parsePerson(Collections.singletonList(date1));
		WebPerson importPerson2 = importController.parsePerson(Collections.singletonList(date2));
		WebPerson importPerson3 = importController.parsePerson(Collections.singletonList(date3));
		WebPerson importPerson4 = importController.parsePerson(Collections.singletonList(date4));

		// Assert
		assertEquals(sdf1.parse(date1), importPerson1.getBirthDate());
		assertEquals(sdf2.parse(date2), importPerson2.getBirthDate());
		assertEquals(sdf3.parse(date3), importPerson3.getBirthDate());
		assertEquals(sdf4.parse(date4), importPerson4.getBirthDate());
	}

	private PersonDTO deepMockPersonDTO(String firstName)
	{
		IdentityInBaseDTO identityInBaseDTO = mock(IdentityInBaseDTO.class);
		when(identityInBaseDTO.getFirstName()).thenReturn(firstName);
		IdentityOutDTO identityOutDTO = mock(IdentityOutDTO.class);
		when(identityOutDTO.getFirstName()).thenReturn(firstName);
		IdentifierDTO identifierDTO = mock(IdentifierDTO.class);
		when(identifierDTO.getValue()).thenReturn("1");
		PersonDTO personDTO = mock(PersonDTO.class);
		when(personDTO.getReferenceIdentity()).thenReturn(identityOutDTO);
		when(personDTO.getMpiId()).thenReturn(identifierDTO);

		return personDTO;
	}
}
