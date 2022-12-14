package org.emau.icmvc.ttp.epix.frontend.controller.common;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.emau.icmvc.ttp.epix.frontend.controller.component.DomainSelector;
import org.icmvc.ttp.web.testtools.JsfTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class AbstractEpixWebBeanTest extends JsfTest
{
	@Test
	void getValueFieldLabelFromConfig()
	{
		// Arrange
		NotSoAbstractEpixWebBean bean = new NotSoAbstractEpixWebBean();

		Map<String, String> valueFieldMapping = new HashMap<>();
		String field = "value1";
		String value = "Testfeld";
		valueFieldMapping.put(field, value);

		DomainSelector domainSelector = mock(DomainSelector.class, Mockito.RETURNS_DEEP_STUBS);
		when(domainSelector.getSelectedDomainConfiguration().getValueFieldMapping()).thenReturn(valueFieldMapping);
		bean.setDomainSelector(domainSelector);

		// Act
		String result = bean.getValueFieldLabel(field);

		// Assert
		assertEquals(value, result);
	}

	@Test
	void getCustomValueFieldLabelFromResources()
	{
		// Arrange
		NotSoAbstractEpixWebBean bean = new NotSoAbstractEpixWebBean();

		Map<String, String> valueFieldMapping = new HashMap<>();
		String field = "value1";
		String value = "deathDate";
		valueFieldMapping.put(field, value);

		DomainSelector domainSelector = mock(DomainSelector.class, Mockito.RETURNS_DEEP_STUBS);
		when(domainSelector.getSelectedDomainConfiguration().getValueFieldMapping()).thenReturn(valueFieldMapping);
		bean.setDomainSelector(domainSelector);

		// Act
		String result = bean.getValueFieldLabel(field);

		// Assert
		assertNotEquals(value, result);
		assertEquals(bundleDe.getString("value.label." + value), result);
	}

	@Test
	void getDefaultValueFieldLabelFromResources()
	{
		// Arrange
		NotSoAbstractEpixWebBean bean = new NotSoAbstractEpixWebBean();

		Map<String, String> valueFieldMapping = new HashMap<>();
		String field = "value1";

		DomainSelector domainSelector = mock(DomainSelector.class, Mockito.RETURNS_DEEP_STUBS);
		when(domainSelector.getSelectedDomainConfiguration().getValueFieldMapping()).thenReturn(valueFieldMapping);
		bean.setDomainSelector(domainSelector);

		// Act
		String result = bean.getValueFieldLabel(field);

		// Assert
		assertEquals(bundleDe.getString("common.person." + field), result);
	}
}
