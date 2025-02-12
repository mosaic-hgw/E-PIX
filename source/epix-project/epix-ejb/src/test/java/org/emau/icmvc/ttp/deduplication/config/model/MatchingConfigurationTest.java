package org.emau.icmvc.ttp.deduplication.config.model;

/*-
 * ###license-information-start###
 * E-PIX - Enterprise Identifier Cross-Referencing
 * __
 * Copyright (C) 2009 - 2025 Trusted Third Party of the University Medicine Greifswald
 * 							kontakt-ths@uni-greifswald.de
 * 
 * 							concept and implementation
 * 							l.geidel,c.schack, d.langner, g.koetzschke
 * 
 * 							web client
 * 							a.blumentritt, f.m. moser
 * 
 * 							docker
 * 							r.schuldt, f.m. moser
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

import java.util.List;

import jakarta.xml.bind.JAXBException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.emau.icmvc.ttp.epix.common.model.config.ConfigurationContainer;
import org.emau.icmvc.ttp.epix.common.model.config.FieldDTO;
import org.emau.icmvc.ttp.epix.common.model.config.MatchingDTO;
import org.emau.icmvc.ttp.epix.common.model.enums.BlockingMode;
import org.emau.icmvc.ttp.epix.common.model.enums.FieldName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MatchingConfigurationTest
{
	private static final Logger logger = LogManager.getLogger(MatchingConfigurationTest.class);
	public static final String DOMAIN_NAME = "domain-name";
	public static final String MPI_PREFIX_NAME = "mpi-prefix-name";
	public static final String MPI_GENERATOR_NAME = "mpi-generator-name";

	private MatchingConfiguration config;

	@BeforeEach
	void setUp()
	{
		ConfigurationContainer cc = new ConfigurationContainer();
		cc.setMpiGenerator(MPI_GENERATOR_NAME);
		cc.setMpiPrefix(MPI_PREFIX_NAME);
		MatchingDTO matching = new MatchingDTO();
		FieldDTO field = new FieldDTO(FieldName.firstName, 0.5, BlockingMode.TEXT, 0.5, 0.5, "", ' ', 0.0, 0.0, 0.0);
		matching.setFields(List.of(field));
		cc.setMatchingConfig(matching);
		config = new MatchingConfiguration(cc, DOMAIN_NAME);
	}

	@AfterEach
	void tearDown()
	{
	}

	@Test
	void testToFromXmlRoundtrip() throws JAXBException
	{
		String xml = config.toXml();
		logger.info(xml);
		assertTrue(xml.contains(MatchingConfiguration.class.getSimpleName()));
		assertTrue(xml.contains(MPI_PREFIX_NAME));
		assertTrue(xml.contains(MPI_GENERATOR_NAME));
		MatchingConfiguration config2 = MatchingConfiguration.fromXml(xml);
		assertEquals(config, config2);
	}
}
