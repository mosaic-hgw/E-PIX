package org.emau.icmvc.ttp.epix.frontend.controller;

/*-
 * ###license-information-start###
 * E-PIX - Enterprise Patient Identifier Cross-referencing
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


import java.util.ArrayList;
import java.util.Arrays;

import org.emau.icmvc.ttp.epix.common.model.config.ConfigurationContainer;
import org.emau.icmvc.ttp.epix.frontend.controller.component.DomainSelector;
import org.emau.icmvc.ttp.epix.frontend.controller.testtools.EpixWebTest;
import org.emau.icmvc.ttp.epix.frontend.util.EpixHelper;
import org.icmvc.ttp.web.controller.LanguageBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ExportControllerTest extends EpixWebTest
{
	@InjectMocks
	ExportController exportController;

	LanguageBean languageBean;
	ConfigurationContainer configurationContainer;
	DomainSelector domainSelector;
	EpixHelper epixHelper;

	@BeforeEach
	void setUpExportControllerTest()
	{
		initMocks(this);

		languageBean = mock(LanguageBean.class);
		when(languageBean.getSupportedLanguages()).thenReturn(Arrays.asList("de", "en"));
		exportController.setLanguageBean(languageBean);

		configurationContainer = mock(ConfigurationContainer.class);

		domainSelector = mock(DomainSelector.class, RETURNS_DEEP_STUBS);
		when(domainSelector.getSelectedDomainConfiguration()).thenReturn(configurationContainer);
		when(domainSelector.getSelectedDomain().getMpiDomain().getName()).thenReturn("MPI");
		epixHelper = mock(EpixHelper.class, RETURNS_DEEP_STUBS);
		when(epixHelper.getDomainSelector()).thenReturn(domainSelector);
		when(epixHelper.getManager()).thenReturn(managementService);
		exportController.setEpixHelper(epixHelper);

		when(managementService.getIdentifierDomains()).thenReturn(new ArrayList<>());
	}

	@Test
	void splitStreetAndNumber()
	{
		// Arrange
		String[][] streetsAndNumbers = {
				{"Weg", "1"},
				{"Weg", "1a"},
				{"Weg", "1 a"},
				{"Weg 1", "2"},
				{"Weg 1", "2a"},
				{"Weg 1", "2 a"},
				{"Weg", "11"},
				{"Weg", "11a"},
				{"Weg", "11 a"},
				{"Weg 11", "2"},
				{"Weg 11", "2a"},
				{"Weg 11", "2 a"},
				{"Weg 11", "22"},
				{"Weg 11", "22a"},
				{"Weg 11", "22 a"}
		};

		// Assert
		for (String[] streetAndNumber : streetsAndNumbers)
		{
			String combination = streetAndNumber[0] + " " + streetAndNumber[1];
			assertArrayEquals(streetAndNumber, exportController.splitStreetAndNumber(combination), Arrays.toString(streetAndNumber));
		}

		assertArrayEquals(new String[]{"Weg", null}, exportController.splitStreetAndNumber("Weg"));
	}
}
