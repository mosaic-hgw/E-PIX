package org.emau.icmvc.ttp.test;

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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.emau.icmvc.ttp.epix.common.exception.DuplicateEntryException;
import org.emau.icmvc.ttp.epix.common.exception.IllegalOperationException;
import org.emau.icmvc.ttp.epix.common.exception.InvalidParameterException;
import org.emau.icmvc.ttp.epix.common.exception.MPIException;
import org.emau.icmvc.ttp.epix.common.exception.ObjectInUseException;
import org.emau.icmvc.ttp.epix.common.exception.UnknownObjectException;
import org.emau.icmvc.ttp.epix.common.model.ContactInDTO;
import org.emau.icmvc.ttp.epix.common.model.DomainDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentifierDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentifierDomainDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityInDTO;
import org.emau.icmvc.ttp.epix.common.model.PersonDTO;
import org.emau.icmvc.ttp.epix.common.model.PossibleMatchForMPIDTO;
import org.emau.icmvc.ttp.epix.common.model.ResponseEntryDTO;
import org.emau.icmvc.ttp.epix.common.model.enums.Gender;
import org.emau.icmvc.ttp.epix.service.EPIXManagementService;
import org.emau.icmvc.ttp.epix.service.EPIXService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*-
 * loeschskript
delete from identity_history_identifier where identity_history_id in
(select id from identity_history where identity_id in
(select id from identity where person_id in
(select id from person where domain_name = 'test-remove-patients')));

delete from identity_history where identity_id in
(select id from identity where person_id in
(select id from person where domain_name = 'test-remove-patients'));

delete from identitylink_history where src_person in
(select id from person where domain_name = 'test-remove-patients')
or dest_person in
(select id from person where domain_name = 'test-remove-patients');

delete from identity_identifier where identity_id in
(select id from identity where person_id in
(select id from person where domain_name = 'test-remove-patients'));

delete from identity_preprocessed where identity_id in
(select id from identity where person_id in
(select id from person where domain_name = 'test-remove-patients'));

delete from contact where identity_id in
(select id from identity where person_id in
(select id from person where domain_name = 'test-remove-patients'));

delete from contact_history where identity_id in
(select id from identity where person_id in
(select id from person where domain_name = 'test-remove-patients'));

delete from identity where person_id in
(select id from person where domain_name = 'test-remove-patients');

delete from person_history where person_id in
(select id from person where domain_name = 'test-remove-patients');

delete from person where domain_name = 'test-remove-patients';

delete from domain where name = 'test-remove-patients';

delete from identifier where value like '9876%';
 */
@EnabledIf("isEpixManagementServiceAvailable")
public class DeletePersonTest
{
	private static final String EPIX_SERVICE_URL = "http://localhost:8080/epix/epixService?wsdl";
	private static final String EPIX_MANAGEMENT_URL = "http://localhost:8080/epix/epixManagementService?wsdl";
	private static final String DOMAIN = "test-remove-patients";
	private static final String SOURCE = "dummy_safe_source";
	private static final String IDENTIFIER_DOMAIN = "MPI";

	private static EPIXService epixService;
	private static EPIXManagementService epixManagement;
	private static final Logger logger = LogManager.getLogger(DeletePersonTest.class);

	/* DOMAIN_CONFIG is defined below */

	@BeforeAll
	public static void initServices() throws Exception
	{
		logger.info("setup");
		QName managementServiceName = new QName("http://service.epix.ttp.icmvc.emau.org/", "EPIXManagementServiceImplService");
		URL managementWsdlURL = new URL(EPIX_MANAGEMENT_URL);

		Service tmpManagementService = Service.create(managementWsdlURL, managementServiceName);
		assertNotNull(tmpManagementService, "webservice object for EPIXManagementService is null");

		epixManagement = tmpManagementService.getPort(EPIXManagementService.class);
		assertNotNull(epixManagement, "epix management service object is null");

		QName serviceName = new QName("http://service.epix.ttp.icmvc.emau.org/", "EPIXServiceImplService");
		URL wsdlURL = new URL(EPIX_SERVICE_URL);

		Service tmpService = Service.create(wsdlURL, serviceName);
		assertNotNull(tmpService, "webservice object for EPIXService is null");

		epixService = tmpService.getPort(EPIXService.class);
		assertNotNull(epixService, "epix service object is null");

		try
		{
			epixManagement.getDomain(DOMAIN);
			logger.info("clean-up - forcefully delete domain");
			epixManagement.deleteDomain(DOMAIN, true);
		}
		catch (UnknownObjectException maybe)
		{}
	}

	static boolean isEpixManagementServiceAvailable()
	{
		boolean success = false;
		try
		{
			URL url = new URL(EPIX_MANAGEMENT_URL);
			HttpURLConnection huc = (HttpURLConnection) url.openConnection();

			success = huc.getResponseCode() == HttpURLConnection.HTTP_OK;
		}
		catch (IOException e)
		{}
		if (!success)
		{
			logger.info("EPIX management service not available at " + EPIX_MANAGEMENT_URL + " - skipping manual test cases");
		}
		return success;
	}

	private static String registerPerson(String firstName, String lastName, Date birthDate, Gender gender) throws Exception
	{
		String mpi = "";
		try
		{
			IdentityInDTO identity = new IdentityInDTO();
			identity.setFirstName(firstName);
			identity.setLastName(lastName);
			identity.setBirthDate(birthDate);
			identity.setGender(gender);

			ResponseEntryDTO response = epixService.requestMPI(DOMAIN, identity, SOURCE, "");

			mpi = response.getPerson().getMpiId().getValue();
		}
		catch (InvalidParameterException | MPIException | UnknownObjectException e)
		{
			logger.error("exception while registering person", e);
			throw e;
		}

		return mpi;
	}

	public ResponseEntryDTO registerPerson2(String firstName, String lastName, Date birthDate, Gender gender) throws Exception
	{
		IdentityInDTO identity = new IdentityInDTO();
		identity.setFirstName(firstName);
		identity.setLastName(lastName);
		identity.setBirthDate(birthDate);
		identity.setGender(gender);

		return epixService.requestMPI(DOMAIN, identity, SOURCE, "");
	}

	private static Date getDate(int day, int month, int year)
	{
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DAY_OF_MONTH, day);
		return cal.getTime();
	}

	protected boolean skipAnyDeactivationAndDeletionOfPersonsOrIdentities()
	{
		return false;
	}

	@BeforeEach
	public void createDomain() throws Exception
	{
		try
		{
			DomainDTO domainDTO = new DomainDTO();
			domainDTO.setName(DOMAIN);
			domainDTO.setLabel(DOMAIN);
			domainDTO.setMpiDomain(epixManagement.getIdentifierDomain(IDENTIFIER_DOMAIN));
			domainDTO.setSafeSource(epixManagement.getSource("dummy_safe_source"));
			domainDTO.setConfig(DOMAIN_CONFIG);

			logger.info("Add domain");
			epixManagement.addDomain(domainDTO);
		}
		catch (DuplicateEntryException | UnknownObjectException | InvalidParameterException | MPIException e)
		{
			e.printStackTrace();
			throw e;
		}
	}

	@AfterEach
	public void deleteDomain() throws Exception
	{
		try
		{
			logger.info("Delete domain");
			// if not skipDeactivationAndDeletion then force=false, because every single test case must leave behind an empty domain
			epixManagement.deleteDomain(DOMAIN, skipAnyDeactivationAndDeletionOfPersonsOrIdentities());
		}
		catch (InvalidParameterException | MPIException | ObjectInUseException | UnknownObjectException e)
		{
			e.printStackTrace();
			throw e;
		}
	}

	@Test
	public void testRemoveOnePerson() throws Exception
	{
		logger.info("--- testRemoveOnePatient start ---");

		logger.info("register person");
		String mpi = registerPerson("Horst", "Schlemmer", getDate(15, 11, 1980), Gender.M);

		if (!skipAnyDeactivationAndDeletionOfPersonsOrIdentities())
		{
			logger.info("deactivate person");
			epixService.deactivatePerson(DOMAIN, mpi);

			logger.info("remove person");
			epixService.deletePerson(DOMAIN, mpi);

			assertThrows(UnknownObjectException.class, () ->
			{
				epixService.getPersonByFirstMPI(DOMAIN, mpi);
			});
		}

		logger.info("--- testRemoveOnePatient end ---");
	}

	@Test
	public void testRemoveOnePersonWithMultipleIdentitesAndContacts() throws Exception
	{
		logger.info("--- testRemoveOnePersonWithMultipleIdentitesAndContacts start ---");

		logger.info("register person");
		String mpi = registerPerson("Horst", "Schlemmer", getDate(15, 11, 1980), Gender.M);
		PersonDTO person = epixService.getPersonByFirstMPI(DOMAIN, mpi);
		List<ContactInDTO> contacts = new ArrayList<>();
		ContactInDTO contact = new ContactInDTO("street", "zip", "city", "state", "country", "email", "phone", "countrCode", "district", "whatever", new Date(), new Date(), new Date());
		contacts.add(contact);
		contact = new ContactInDTO(contact);
		contact.setStreet("another street");
		contacts.add(contact);
		IdentityInDTO in = new IdentityInDTO(person.getReferenceIdentity(), contacts);
		in.setFirstName("Horsti");
		IdentifierDomainDTO idDomain = new IdentifierDomainDTO("MPI", "", "", null, null, "");
		in.getIdentifiers().add(new IdentifierDTO("9876000000000", "dummy description", new Date(), idDomain));
		epixService.updatePerson(DOMAIN, mpi, in, SOURCE, false, "test update");
		epixService.updatePerson(DOMAIN, mpi, in, SOURCE, false, "test update");
		person = epixService.getPersonByFirstMPI(DOMAIN, mpi);

		if (!skipAnyDeactivationAndDeletionOfPersonsOrIdentities())
		{
			logger.info("delete one contact");
			epixService.deactivateContact(person.getReferenceIdentity().getContacts().get(0).getContactId());
			epixService.deleteContact(person.getReferenceIdentity().getContacts().get(0).getContactId());

			logger.info("deactivate person");
			epixService.deactivatePerson(DOMAIN, mpi);

			logger.info("remove person");
			epixService.deletePerson(DOMAIN, mpi);

			assertThrows(UnknownObjectException.class, () ->
			{
				epixService.getPersonByFirstMPI(DOMAIN, mpi);
			});
		}

		logger.info("--- testRemoveOnePersonWithMultipleIdentitesAndContacts end ---");
	}

	@Test
	public void testRemoveOfNonDeactivatedPerson() throws Exception
	{
		logger.info("--- testRemoveOfNonDeactivatedPerson start ---");

		logger.info("register person");
		String mpi = registerPerson("Claire", "Grube", getDate(30, 5, 1943), Gender.F);

		logger.info("try to remove a not deactivated person");
		assertThrows(IllegalOperationException.class, () -> epixService.deletePerson(DOMAIN, mpi));

		if (!skipAnyDeactivationAndDeletionOfPersonsOrIdentities())
		{
			logger.info("deactivate person");
			epixService.deactivatePerson(DOMAIN, mpi);

			logger.info("remove person");
			epixService.deletePerson(DOMAIN, mpi);

			assertThrows(UnknownObjectException.class, () ->
			{
				epixService.getPersonByFirstMPI(DOMAIN, mpi);
			});
		}

		logger.info("--- testRemoveOfNonDeactivatedPerson end ---");
	}

	@Test
	public void testRemoveMultiplePersons() throws Exception
	{
		logger.info("--- testRemoveMultiplePersons start ---");

		String mpi1 = registerPerson("Sarah", "Schulz", getDate(1, 1, 2000), Gender.F);
		String mpi2 = registerPerson("Markus", "Meier", getDate(5, 5, 2000), Gender.M);
		String mpi3 = registerPerson("Sarah", "Müller", getDate(8, 8, 2000), Gender.F);
		String mpi4 = registerPerson("Timo", "Schmidt", getDate(10, 10, 2000), Gender.M);

		if (!skipAnyDeactivationAndDeletionOfPersonsOrIdentities())
		{
			epixService.deactivatePerson(DOMAIN, mpi1);
			epixService.deletePerson(DOMAIN, mpi1);

			epixService.deactivatePerson(DOMAIN, mpi2);
			epixService.deactivatePerson(DOMAIN, mpi3);

			epixService.deletePerson(DOMAIN, mpi3);

			epixService.deactivatePerson(DOMAIN, mpi4);
			epixService.deletePerson(DOMAIN, mpi4);

			epixService.deletePerson(DOMAIN, mpi2);

			assertThrows(UnknownObjectException.class, () ->
			{
				epixService.getPersonByFirstMPI(DOMAIN, mpi1);
			});
			assertThrows(UnknownObjectException.class, () ->
			{
				epixService.getPersonByFirstMPI(DOMAIN, mpi2);
			});
			assertThrows(UnknownObjectException.class, () ->
			{
				epixService.getPersonByFirstMPI(DOMAIN, mpi3);
			});
			assertThrows(UnknownObjectException.class, () ->
			{
				epixService.getPersonByFirstMPI(DOMAIN, mpi4);
			});
		}

		logger.info("--- testRemoveMultiplePersons end ---");
	}

	@Test
	public void testRemoveAutomaticMatches() throws Exception
	{
		logger.info("--- testRemoveAutomaticMatches start ---");

		String mpi1 = registerPerson("XXXXXXXXXXXXXXXXXXXXXXXAnna", "Schulz", getDate(1, 1, 2000), Gender.F);
		String mpi2 = registerPerson("XXXXXXXXXXXXXXXXXXXXXXXAnne", "Schulz", getDate(1, 1, 2000), Gender.F);

		assertEquals(mpi1, mpi2);

		if (!skipAnyDeactivationAndDeletionOfPersonsOrIdentities())
		{
			epixService.deactivatePerson(DOMAIN, mpi1);
			epixService.deletePerson(DOMAIN, mpi1);

			assertThrows(UnknownObjectException.class, () ->
			{
				epixService.getPersonByFirstMPI(DOMAIN, mpi1);
			});
		}

		logger.info("--- testRemoveAutomaticMatches end ---");
	}

	@Test
	public void testRemoveAssignedPossibleMatches() throws Exception
	{
		logger.info("--- testRemoveAssignedPossibleMatches start ---");

		String mpi1 = registerPerson("Anna", "Schulz", getDate(1, 1, 2000), Gender.F);
		String mpi2 = registerPerson("Anne", "Schulz", getDate(1, 1, 2000), Gender.F);

		PersonDTO p1 = epixService.getPersonByFirstMPI(DOMAIN, mpi1);
		epixService.getPersonByFirstMPI(DOMAIN, mpi2);

		List<PossibleMatchForMPIDTO> pms = epixService.getPossibleMatchesForPerson(DOMAIN, mpi1);

		epixService.assignIdentity(pms.get(0).getLinkId(), p1.getReferenceIdentity().getIdentityId(), "");

		if (!skipAnyDeactivationAndDeletionOfPersonsOrIdentities())
		{
			epixService.deactivatePerson(DOMAIN, mpi1);

			epixService.deletePerson(DOMAIN, mpi1);
			epixService.deletePerson(DOMAIN, mpi2);

			assertThrows(UnknownObjectException.class, () ->
			{
				epixService.getPersonByFirstMPI(DOMAIN, mpi1);
			});
			assertThrows(UnknownObjectException.class, () ->
			{
				epixService.getPersonByFirstMPI(DOMAIN, mpi2);
			});
		}

		logger.info("--- testRemoveAssignedPossibleMatches end ---");
	}

	@Test
	public void testRemoveAssignedPossibleMatchesFirstLoser() throws Exception
	{
		logger.info("--- testRemoveAssignedPossibleMatchesFirstLoser start ---");

		String mpi1 = registerPerson("Anna", "Schulz", getDate(1, 1, 2000), Gender.F);
		String mpi2 = registerPerson("Anne", "Schulz", getDate(1, 1, 2000), Gender.F);

		PersonDTO p1 = epixService.getPersonByFirstMPI(DOMAIN, mpi1);
		List<PossibleMatchForMPIDTO> pms = epixService.getPossibleMatchesForPerson(DOMAIN, mpi1);

		epixService.assignIdentity(pms.get(0).getLinkId(), p1.getReferenceIdentity().getIdentityId(), "");

		// relation p1 - mpi1 - id1 is untouched
		PersonDTO p1New = epixService.getActivePersonByMPI(DOMAIN, mpi1);
		assertEquals(p1.getPersonId(), p1New.getPersonId());
		assertEquals(p1.getReferenceIdentity().getIdentityId(), p1New.getReferenceIdentity().getIdentityId());

		// from outside the epixService now there is no longer any evidence of p2 ...
		PersonDTO p2New = epixService.getActivePersonByMPI(DOMAIN, mpi2);
		assertEquals(p1.getPersonId(), p2New.getPersonId());
		assertEquals(p1.getReferenceIdentity().getIdentityId(), p2New.getReferenceIdentity().getIdentityId());

		if (!skipAnyDeactivationAndDeletionOfPersonsOrIdentities())
		{
			// ... so there is no way to assert that there IS p2 in DB before deletion
			epixService.deletePerson(DOMAIN, mpi2);
			// ... as well as no way to assert that there IS NO p2 in DB after deletion (or tell me please)

			// as said, from outside the epixService there is no longer any evidence of p2 but however mpi2 is yet connected to p1
			p2New = epixService.getActivePersonByMPI(DOMAIN, mpi2);
			assertEquals(p1.getPersonId(), p2New.getPersonId());
			assertEquals(p1.getReferenceIdentity().getIdentityId(), p2New.getReferenceIdentity().getIdentityId());

			// deleting p1
			epixService.deactivatePerson(DOMAIN, mpi1);
			epixService.deletePerson(DOMAIN, mpi1);

			// now neither mpi1 nor mpi2 corresponds to any person
			assertThrows(UnknownObjectException.class, () ->
			{
				epixService.getPersonByFirstMPI(DOMAIN, mpi1);
			});
			assertThrows(UnknownObjectException.class, () ->
			{
				epixService.getPersonByFirstMPI(DOMAIN, mpi2);
			});

			// How to assert that the identifier table really does not contain any orphan MPIs?
		}

		logger.info("--- testRemoveAssignedPossibleMatchesFirstLoser end ---");
	}

	@Test
	public void testRemoveAssignedPossibleMatchesViaIdentity() throws Exception
	{
		logger.info("--- testRemoveAssignedPossibleMatchesViaIdentity start ---");

		String mpi1 = registerPerson("Anna", "Schulz", getDate(1, 1, 2000), Gender.F);
		String mpi2 = registerPerson("Anne", "Schulz", getDate(1, 1, 2000), Gender.F);

		PersonDTO p1 = epixService.getPersonByFirstMPI(DOMAIN, mpi1);
		PersonDTO p2 = epixService.getPersonByFirstMPI(DOMAIN, mpi2);
		List<PossibleMatchForMPIDTO> pms = epixService.getPossibleMatchesForPerson(DOMAIN, mpi1);

		epixService.assignIdentity(pms.get(0).getLinkId(), p1.getReferenceIdentity().getIdentityId(), "");

		// relation p1 - mpi1 - id1 is untouched
		PersonDTO p1New = epixService.getPersonByFirstMPI(DOMAIN, mpi1);
		assertEquals(p1.getPersonId(), p1New.getPersonId());
		assertEquals(p1.getReferenceIdentity().getIdentityId(), p1New.getReferenceIdentity().getIdentityId());

		// from outside the epixService now there is no longer any evidence of p2 ...
		PersonDTO p2New = epixService.getActivePersonByMPI(DOMAIN, mpi2);
		assertEquals(p1.getPersonId(), p2New.getPersonId());
		assertEquals(p1.getReferenceIdentity().getIdentityId(), p2New.getReferenceIdentity().getIdentityId());

		if (!skipAnyDeactivationAndDeletionOfPersonsOrIdentities())
		{
			epixService.deactivateIdentity(p1.getReferenceIdentity().getIdentityId());
			p1New = epixService.getPersonByFirstMPI(DOMAIN, mpi1);
			assertEquals(p1.getPersonId(), p1New.getPersonId());
			// now the identity origin from p2 should be the reference identity of p1
			assertEquals(p1New.getReferenceIdentity().getIdentityId(), p2.getReferenceIdentity().getIdentityId());
			epixService.deleteIdentity(p1.getReferenceIdentity().getIdentityId());

			// person should still be accessible via both mpis
			epixService.getActivePersonByMPI(DOMAIN, mpi1);
			epixService.getActivePersonByMPI(DOMAIN, mpi2);

			epixService.deactivateIdentity(p2.getReferenceIdentity().getIdentityId());
			epixService.deleteIdentity(p2.getReferenceIdentity().getIdentityId());

			// now neither mpi1 nor mpi2 corresponds to any active person
			assertThrows(UnknownObjectException.class, () ->
			{
				epixService.getActivePersonByMPI(DOMAIN, mpi1);
			});
			assertThrows(UnknownObjectException.class, () ->
			{
				epixService.getActivePersonByMPI(DOMAIN, mpi2);
			});

			// ... but still to deactivated persons
			assertTrue(epixService.getPersonByFirstMPI(DOMAIN, mpi1).isDeactivated());
			assertTrue(epixService.getPersonByFirstMPI(DOMAIN, mpi2).isDeactivated());

			assertThrows(ObjectInUseException.class, () ->
			{
				// throws exception because the persons still exist
				epixManagement.deleteDomain(DOMAIN, false);
			});
			epixService.deletePerson(DOMAIN, mpi1);
			epixService.deletePerson(DOMAIN, mpi2);
			// How to assert that the identifier table really does not contain any orphan MPIs?
		}

		logger.info("--- testRemoveAssignedPossibleMatchesViaIdentity end ---");
	}

	@Test
	public void testRemoveAssignedPossibleMatchesViaIdentityFirstLoser() throws Exception
	{
		logger.info("--- testRemoveAssignedPossibleMatchesViaIdentityFirstLoser start ---");

		String mpi1 = registerPerson("Anna", "Schulz", getDate(1, 1, 2000), Gender.F);
		String mpi2 = registerPerson("Anne", "Schulz", getDate(1, 1, 2000), Gender.F);

		PersonDTO p1 = epixService.getPersonByFirstMPI(DOMAIN, mpi1);
		PersonDTO p2 = epixService.getPersonByFirstMPI(DOMAIN, mpi2);
		List<PossibleMatchForMPIDTO> pms = epixService.getPossibleMatchesForPerson(DOMAIN, mpi1);

		epixService.assignIdentity(pms.get(0).getLinkId(), p1.getReferenceIdentity().getIdentityId(), "");

		// relation p1 - mpi1 - id1 is untouched
		PersonDTO p1New = epixService.getActivePersonByMPI(DOMAIN, mpi1);
		assertEquals(p1.getPersonId(), p1New.getPersonId());
		assertEquals(p1.getReferenceIdentity().getIdentityId(), p1New.getReferenceIdentity().getIdentityId());

		// from outside the epixService now there is no longer any evidence of p2 ...
		PersonDTO p2New = epixService.getActivePersonByMPI(DOMAIN, mpi2);
		assertEquals(p1.getPersonId(), p2New.getPersonId());
		assertEquals(p1.getReferenceIdentity().getIdentityId(), p2New.getReferenceIdentity().getIdentityId());

		if (!skipAnyDeactivationAndDeletionOfPersonsOrIdentities())
		{
			epixService.deactivateIdentity(p2.getReferenceIdentity().getIdentityId());
			epixService.deleteIdentity(p2.getReferenceIdentity().getIdentityId());

			// person should now only be accessible via mpi1
			epixService.getActivePersonByMPI(DOMAIN, mpi1);
			assertThrows(UnknownObjectException.class, () ->
			{
				epixService.getActivePersonByMPI(DOMAIN, mpi2);
			});

			epixService.deactivateIdentity(p1.getReferenceIdentity().getIdentityId());
			epixService.deleteIdentity(p1.getReferenceIdentity().getIdentityId());

			// now neither mpi1 nor mpi2 corresponds to any active person
			assertThrows(UnknownObjectException.class, () ->
			{
				epixService.getActivePersonByMPI(DOMAIN, mpi1);
			});
			assertThrows(UnknownObjectException.class, () ->
			{
				epixService.getActivePersonByMPI(DOMAIN, mpi2);
			});

			// ... but still to deactivated persons
			assertTrue(epixService.getPersonByFirstMPI(DOMAIN, mpi1).isDeactivated());
			assertTrue(epixService.getPersonByFirstMPI(DOMAIN, mpi2).isDeactivated());

			assertThrows(ObjectInUseException.class, () ->
			{
				// throws exception because the persons still exist
				epixManagement.deleteDomain(DOMAIN, false);
			});
			epixService.deletePerson(DOMAIN, mpi1);
			epixService.deletePerson(DOMAIN, mpi2);
			// How to assert that the identifier table really does not contain any orphan MPIs?
		}

		logger.info("--- testRemoveAssignedPossibleMatchesViaIdentityFirstLoser end ---");
	}

	@Test
	public void testRemoveAssignedPossibleMatchesViaIdentityFirstLoserPerson() throws Exception
	{
		logger.info("--- testRemoveAssignedPossibleMatchesViaIdentityFirstLoserPerson start ---");

		String mpi1 = registerPerson("Anna", "Schulz", getDate(1, 1, 2000), Gender.F);
		String mpi2 = registerPerson("Anne", "Schulz", getDate(1, 1, 2000), Gender.F);

		PersonDTO p1 = epixService.getPersonByFirstMPI(DOMAIN, mpi1);
		PersonDTO p2 = epixService.getPersonByFirstMPI(DOMAIN, mpi2);
		List<PossibleMatchForMPIDTO> pms = epixService.getPossibleMatchesForPerson(DOMAIN, mpi1);

		epixService.assignIdentity(pms.get(0).getLinkId(), p1.getReferenceIdentity().getIdentityId(), "");

		// relation p1 - mpi1 - id1 is untouched
		PersonDTO p1New = epixService.getActivePersonByMPI(DOMAIN, mpi1);
		assertEquals(p1.getPersonId(), p1New.getPersonId());
		assertEquals(p1.getReferenceIdentity().getIdentityId(), p1New.getReferenceIdentity().getIdentityId());

		// from outside the epixService now there is no longer any evidence of p2 ...
		PersonDTO p2New = epixService.getActivePersonByMPI(DOMAIN, mpi2);
		assertEquals(p1.getPersonId(), p2New.getPersonId());
		assertEquals(p1.getReferenceIdentity().getIdentityId(), p2New.getReferenceIdentity().getIdentityId());

		if (!skipAnyDeactivationAndDeletionOfPersonsOrIdentities())
		{
			epixService.deletePerson(DOMAIN, mpi2);

			// person should still be accessible via both mpis
			epixService.getActivePersonByMPI(DOMAIN, mpi1);
			epixService.getActivePersonByMPI(DOMAIN, mpi2);

			epixService.deactivateIdentity(p2.getReferenceIdentity().getIdentityId());
			epixService.deleteIdentity(p2.getReferenceIdentity().getIdentityId());

			// person should now only be accessible via mpi1
			epixService.getActivePersonByMPI(DOMAIN, mpi1);
			assertThrows(UnknownObjectException.class, () ->
			{
				epixService.getActivePersonByMPI(DOMAIN, mpi2);
			});
			assertThrows(UnknownObjectException.class, () ->
			{
				epixService.getPersonByFirstMPI(DOMAIN, mpi2);
			});

			epixService.deactivateIdentity(p1.getReferenceIdentity().getIdentityId());
			epixService.deleteIdentity(p1.getReferenceIdentity().getIdentityId());

			// now neither mpi1 nor mpi2 corresponds to any active person
			assertThrows(UnknownObjectException.class, () ->
			{
				epixService.getActivePersonByMPI(DOMAIN, mpi1);
			});
			assertThrows(UnknownObjectException.class, () ->
			{
				epixService.getActivePersonByMPI(DOMAIN, mpi2);
			});
			// mpi2 not even to a deactivated person
			assertThrows(UnknownObjectException.class, () ->
			{
				epixService.getPersonByFirstMPI(DOMAIN, mpi2);
			});
			// ... but mpi1 still to one deactivated person
			assertTrue(epixService.getPersonByFirstMPI(DOMAIN, mpi1).isDeactivated());

			assertThrows(ObjectInUseException.class, () ->
			{
				// throws exception because the persons still exist
				epixManagement.deleteDomain(DOMAIN, false);
			});
			epixService.deletePerson(DOMAIN, mpi1);
			// How to assert that the identifier table really does not contain any orphan MPIs?
		}

		logger.info("--- testRemoveAssignedPossibleMatchesViaIdentityFirstLoserPerson end ---");
	}

	private static final String DOMAIN_CONFIG = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<ma:MatchingConfiguration xmlns:ma=\"http://www.ttp.icmvc.emau.org/deduplication/config/model\"\n"
			+ "\txmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
			+ "\txsi:schemaLocation=\"http://www.ttp.icmvc.emau.org/deduplication/config/model matching-config-2.9.0.xsd \">\n"
			+ "\t<matching-mode>MATCHING_IDENTITIES</matching-mode>\n"
			+ "\t<mpi-generator>org.emau.icmvc.ttp.epix.gen.impl.EAN13Generator</mpi-generator>\n"
			+ "\t<mpi-prefix>9876</mpi-prefix>\n"
			+ "\t<use-notifications>false</use-notifications>\n"
			+ "\t<limit-search-to-reduce-memory-consumption>false</limit-search-to-reduce-memory-consumption>\n"
			+ "\n"
			+ "\t<required-fields>\n"
			+ "\t\t<name>firstName</name>\n"
			+ "\t\t<name>lastName</name>\n"
			+ "\t</required-fields>\n"
			+ "\t<preprocessing-config>\n"
			+ "\t\t<preprocessing-field>\n"
			+ "\t\t\t<field-name>firstName</field-name>\n"
			+ "\t\t\t<complex-transformation-type xsi:type=\"ma:ComplexTransformation\">\n"
			+ "\t\t\t\t<qualified-class-name>org.emau.icmvc.ttp.deduplication.preprocessing.impl.ToUpperCaseTransformation</qualified-class-name>\n"
			+ "\t\t\t</complex-transformation-type>\n"
			+ "\t\t\t<complex-transformation-type xsi:type=\"ma:ComplexTransformation\">\n"
			+ "\t\t\t\t<qualified-class-name>org.emau.icmvc.ttp.deduplication.preprocessing.impl.CharsMutationTransformation</qualified-class-name>\n"
			+ "\t\t\t</complex-transformation-type>\n"
			+ "\t\t</preprocessing-field>\n"
			+ "\t\t<preprocessing-field>\n"
			+ "\t\t\t<field-name>lastName</field-name>\n"
			+ "\t\t\t<complex-transformation-type xsi:type=\"ma:ComplexTransformation\">\n"
			+ "\t\t\t\t<qualified-class-name>org.emau.icmvc.ttp.deduplication.preprocessing.impl.ToUpperCaseTransformation</qualified-class-name>\n"
			+ "\t\t\t</complex-transformation-type>\n"
			+ "\t\t\t<complex-transformation-type xsi:type=\"ma:ComplexTransformation\">\n"
			+ "\t\t\t\t<qualified-class-name>org.emau.icmvc.ttp.deduplication.preprocessing.impl.CharsMutationTransformation</qualified-class-name>\n"
			+ "\t\t\t</complex-transformation-type>\n"
			+ "\t\t</preprocessing-field>\n"
			+ "\t</preprocessing-config>\n"
			+ "\t<matching>\n"
			+ "\t\t<threshold-possible-match>2.99</threshold-possible-match>\n"
			+ "\t\t<threshold-automatic-match>14.5</threshold-automatic-match>\n"
			+ "\t\t<use-cemfim>false</use-cemfim>\n"
			+ "\t\t<parallel-matching-after>1000</parallel-matching-after>\n"
			+ "\t\t<number-of-threads-for-matching>4</number-of-threads-for-matching>\n"
			+ "\t\t<field>\n"
			+ "\t\t\t<name>firstName</name>\n"
			+ "\t\t\t<blocking-threshold>0.4</blocking-threshold>\n"
			+ "\t\t\t<matching-threshold>0.8</matching-threshold>\n"
			+ "\t\t\t<weight>8</weight>\n"
			+ "\t\t\t<algorithm>org.emau.icmvc.ttp.deduplication.impl.LevenshteinAlgorithm</algorithm>\n"
			+ "\t\t\t<multiple-values>\n"
			+ "\t\t\t\t<separator> </separator>\n"
			+ "\t\t\t\t<penalty-not-a-perfect-match>0.1</penalty-not-a-perfect-match>\n"
			+ "\t\t\t\t<penalty-one-short>0.1</penalty-one-short>\n"
			+ "\t\t\t\t<penalty-both-short>0.2</penalty-both-short>\n"
			+ "\t\t\t</multiple-values>\n"
			+ "\t\t</field>\n"
			+ "\t\t<field>\n"
			+ "\t\t\t<name>lastName</name>\n"
			+ "\t\t\t<matching-threshold>0.8</matching-threshold>\n"
			+ "\t\t\t<weight>6</weight>\n"
			+ "\t\t\t<algorithm>org.emau.icmvc.ttp.deduplication.impl.LevenshteinAlgorithm</algorithm>\n"
			+ "\t\t</field>\n"
			+ "\t</matching>\n"
			+ "</ma:MatchingConfiguration>\n";
}
