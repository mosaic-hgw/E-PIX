package org.emau.icmvc.ttp.epix.frontend.controller.common;

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

import java.util.List;

import org.emau.icmvc.ttp.epix.common.model.enums.Gender;
import org.icmvc.ttp.web.testtools.JsfTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AbstractEpixWebBeanTest extends JsfTest
{
	@Test
	void getGenders()
	{
		// Arrange
		NotSoAbstractEpixWebBean bean = new NotSoAbstractEpixWebBean();

		// Act
		List<Gender> genders = bean.epixHelper.getGenders();

		// Assert
		assertEquals(Gender.values().length, genders.size());
		assertEquals(Gender.M, genders.get(0));
		assertEquals(Gender.F, genders.get(1));
		assertEquals(Gender.X, genders.get(2));
		assertEquals(Gender.O, genders.get(3));
		assertEquals(Gender.U, genders.get(4));
	}
}
