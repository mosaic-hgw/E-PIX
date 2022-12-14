package org.emau.icmvc.ttp.test;

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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.emau.icmvc.ttp.epix.common.exception.DuplicateEntryException;
import org.emau.icmvc.ttp.epix.common.exception.InvalidParameterException;
import org.emau.icmvc.ttp.epix.common.exception.MPIException;
import org.emau.icmvc.ttp.epix.common.exception.ObjectInUseException;
import org.emau.icmvc.ttp.epix.common.exception.UnknownObjectException;
import org.emau.icmvc.ttp.epix.common.model.DomainDTO;
import org.emau.icmvc.ttp.epix.common.model.config.ReasonDTO;
import org.emau.icmvc.ttp.epix.service.EPIXManagementService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

@EnabledIf("isEpixManagementServiceAvailable")
public class DeduplicationReasonTest
{
	private static final String EPIX_SERVICE_URL = "http://localhost:8080/epix/epixService?wsdl";
	private static final String EPIX_MANAGEMENT_URL = "http://localhost:8080/epix/epixManagementService?wsdl";
	private static final String DOMAIN = "test-dedup-reasons";
	private static final String SOURCE = "dummy_safe_source";
	private static final String IDENTIFIER_DOMAIN = "MPI";

	private static EPIXManagementService epixManagement;
	private static final Logger logger = LogManager.getLogger(DeduplicationReasonTest.class);

	/* DOMAIN_CONFIG is defined below */

	@BeforeAll
	public static void initServices() throws MalformedURLException
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
		if (!success) {
			logger.info("EPIX management service not available at " + EPIX_MANAGEMENT_URL + " - skipping manual test cases");
		}
		return success;
	}

	protected boolean skipAnyDeactivationAndDeletionOfPersonsOrIdentities()
	{
		return false;
	}

	@BeforeEach
	public void createDomain()
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
			fail();
		}
	}

	@AfterEach
	public void deleteDomain()
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
			fail();
		}
	}

	@Test
	public void testReasonResult() throws InvalidParameterException, UnknownObjectException
	{
		logger.info("--- testReasonResult start ---");
		List<ReasonDTO> rs = epixManagement.getDefinedDeduplicationReasons(DOMAIN);

		Assertions.assertEquals(3, rs.size());

		logger.info("--- testReasonResult end ---");
	}

	private static final String DOMAIN_CONFIG = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<ma:MatchingConfiguration xmlns:ma=\"http://www.ttp.icmvc.emau.org/deduplication/config/model\"\n"
			+ "\txmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
			+ "\txsi:schemaLocation=\"http://www.ttp.icmvc.emau.org/deduplication/config/model matching-config-2.9.0.xsd \">\n"
			+ "\t<matching-mode>MATCHING_IDENTITIES</matching-mode>\n"
			+ "\t<mpi-generator>org.emau.icmvc.ttp.epix.gen.impl.EAN13Generator</mpi-generator>\n"
			+ "\t<mpi-prefix>1001</mpi-prefix>\n"
			+ "\t<use-notifications>false</use-notifications>\n"
			+ "\t<limit-search-to-reduce-memory-consumption>false</limit-search-to-reduce-memory-consumption>\n"
			+ "\n"
			+ "\t<required-fields>\n"
			+ "\t\t<name>firstName</name>\n"
			+ "\t\t<name>lastName</name>\n"
			+ "\t</required-fields>\n"
			+ "\t<deduplication>\n"
			+ "\t\t<reason>\n"
			+ "\t\t\t<name>Tippfehler</name>\n"
			+ "\t\t\t<description>Vertauschte oder fehlende Zeichen usw.</description>\n"
			+ "\t\t</reason>\n"
			+ "\t\t<reason>\n"
			+ "\t\t\t<name>Namensänderung durch Heirat</name>\n"
			+ "\t\t\t<description>Person hat geheiratet</description>\n"
			+ "\t\t</reason>\n"
			+ "\t\t<reason>\n"
			+ "\t\t\t<name>Fehlerhafte Daten</name>\n"
			+ "\t\t</reason>\n"
			+ "\t</deduplication>"
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
