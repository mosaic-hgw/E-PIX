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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class AbstractBatchBeanTest
{
	@Test
	void splitStreetAndNumber()
	{
	    // Arrange
		NotSoAbstractBatchBean bean = new NotSoAbstractBatchBean();

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
			assertArrayEquals(streetAndNumber, bean.splitStreetAndNumber(combination), Arrays.toString(streetAndNumber));
		}

		assertArrayEquals(new String[]{"Weg", null}, bean.splitStreetAndNumber("Weg"));
	}
}
