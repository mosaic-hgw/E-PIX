package org.emau.icmvc.ttp.test;

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
import java.util.List;

import org.emau.icmvc.ttp.deduplication.impl.validation.AlphabetValidator;
import org.emau.icmvc.ttp.deduplication.impl.validation.GermanZipCodeValidator;
import org.emau.icmvc.ttp.deduplication.impl.validation.GroupValidator;
import org.emau.icmvc.ttp.deduplication.impl.validation.LengthValidator;
import org.emau.icmvc.ttp.deduplication.impl.validation.RegExValidator;
import org.emau.icmvc.ttp.epix.common.deduplication.IValidator;
import org.emau.icmvc.ttp.epix.common.model.enums.ValidatorOperator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidatorTest
{
	@Test
	void testORGroupTwoValidators()
	{
		// OR: one of the values must be valid
		RegExValidator rev1 = new RegExValidator("[0-9]*");
		RegExValidator rev2 = new RegExValidator("[a-z]*");

		ArrayList<IValidator> valList = new ArrayList<>();
		valList.add(rev1);
		valList.add(rev2);

		GroupValidator group = new GroupValidator();
		group.setValidators(valList);
		group.setOperator(ValidatorOperator.AT_LEAST_ONE);

		assertTrue(group.validate("aaaab"));
		assertTrue(group.validate("ab"));
		assertTrue(group.validate("012345"));
		assertTrue(group.validate("los"));
		assertTrue(group.validate(""));

		assertFalse(group.validate("+"));
		assertFalse(group.validate("_"));
		assertFalse(group.validate(" "));
		assertFalse(group.validate("fju65"));
		assertFalse(group.validate("ABC"));
	}

	@Test
	void testORGroupTwoValidatorsZipLength()
	{
		GermanZipCodeValidator val1 = new GermanZipCodeValidator();
		LengthValidator val2 = new LengthValidator(3);

		List<IValidator> l = new ArrayList<>();
		l.add(val1);
		l.add(val2);

		GroupValidator vg = new GroupValidator();
		vg.setValidators(l);
		vg.setOperator(ValidatorOperator.AT_LEAST_ONE);

		assertTrue(vg.validate("12345"));
		assertTrue(vg.validate("123"));

		assertFalse(vg.validate("12"));
		assertFalse(vg.validate("123454"));
	}

	@Test
	void testORGroupThreeValidators()
	{
		// OR: one of the values must be valid
		RegExValidator rev1 = new RegExValidator("[0-9]*");
		RegExValidator rev2 = new RegExValidator("[a-z]*");
		RegExValidator rev3 = new RegExValidator("[A-Z]{3}");

		ArrayList<IValidator> valList = new ArrayList<>();
		valList.add(rev1);
		valList.add(rev2);
		valList.add(rev3);

		GroupValidator group = new GroupValidator();
		group.setValidators(valList);
		group.setOperator(ValidatorOperator.AT_LEAST_ONE);

		assertTrue(group.validate("aaaab"));
		assertTrue(group.validate("ab"));
		assertTrue(group.validate("012345"));
		assertTrue(group.validate("los"));
		assertTrue(group.validate("ABC"));

		assertFalse(group.validate("+"));
		assertFalse(group.validate("_"));
		assertFalse(group.validate(" "));
		assertFalse(group.validate("fju65"));
		assertFalse(group.validate("ABCD"));
		assertFalse(group.validate("acD"));
	}

	@Test
	void testANDGroupTwoValidators()
	{
		// AND: all values must be valid
		RegExValidator rev1 = new RegExValidator("[\\dA-Z]*");
		RegExValidator rev2 = new RegExValidator("[0-9A-Za-z]*");

		ArrayList<IValidator> valList = new ArrayList<>();
		valList.add(rev1);
		valList.add(rev2);

		GroupValidator group = new GroupValidator();
		group.setValidators(valList);
		group.setOperator(ValidatorOperator.ALL);

		assertTrue(group.validate("ABC12321"));
		assertTrue(group.validate("12122ABC"));
		assertTrue(group.validate(""));
		assertTrue(group.validate("ABC0123ABC"));
		assertTrue(group.validate("0S1Y23X"));

		assertFalse(group.validate("+"));
		assertFalse(group.validate("_"));
		assertFalse(group.validate(" "));
		assertFalse(group.validate("0123ABCabc"));
		assertFalse(group.validate("ABCabc"));
	}

	@Test
	void testANDGroupTwoValidatorsZipLength()
	{
		GermanZipCodeValidator val1 = new GermanZipCodeValidator();
		LengthValidator val2 = new LengthValidator(5);

		List<IValidator> l = new ArrayList<>();
		l.add(val1);
		l.add(val2);

		GroupValidator vg = new GroupValidator();
		vg.setValidators(l);
		vg.setOperator(ValidatorOperator.ALL);

		assertTrue(vg.validate("12345"));
		assertTrue(vg.validate("54321"));

		assertFalse(vg.validate("123"));
		assertFalse(vg.validate("__"));
	}

	@Test
	void testANDGroupThreeValidators()
	{
		// AND: all values must be valid
		RegExValidator rev1 = new RegExValidator("[\\dA-Z]*");
		RegExValidator rev2 = new RegExValidator("[0-9A-Za-z]*");
		RegExValidator rev3 = new RegExValidator("[0-9A-Za-z]{4}");

		ArrayList<IValidator> valList = new ArrayList<>();
		valList.add(rev1);
		valList.add(rev2);
		valList.add(rev3);

		GroupValidator group = new GroupValidator();
		group.setValidators(valList);
		group.setOperator(ValidatorOperator.ALL);

		assertTrue(group.validate("A121"));
		assertTrue(group.validate("21AB"));
		assertTrue(group.validate("ABCC"));
		assertTrue(group.validate("Y23X"));

		assertFalse(group.validate("++++"));
		assertFalse(group.validate("____"));
		assertFalse(group.validate("    "));
		assertFalse(group.validate(""));
		assertFalse(group.validate("0123ABCabc"));
		assertFalse(group.validate("ABCabc"));
		assertFalse(group.validate("A121A"));
	}

	@Test
	void testANDGroupThreeValidatorsZipLengthAlphabet()
	{
		GermanZipCodeValidator val1 = new GermanZipCodeValidator();
		LengthValidator val2 = new LengthValidator(5);
		AlphabetValidator val3 = new AlphabetValidator("1234567890");

		List<IValidator> l = new ArrayList<>();
		l.add(val1);
		l.add(val2);
		l.add(val3);

		GroupValidator vg = new GroupValidator();
		vg.setValidators(l);
		vg.setOperator(ValidatorOperator.ALL);

		assertTrue(vg.validate("12345"));
		assertTrue(vg.validate("54321"));

		assertFalse(vg.validate("00000"));
		assertFalse(vg.validate("__"));
	}

	@Test
	void testXORGroup()
	{
		// XOR: exactly one of the values must be valid
		RegExValidator rev1 = new RegExValidator("[0-9]*");
		RegExValidator rev2 = new RegExValidator("[0-9a-zA-Z]*");

		ArrayList<IValidator> valList = new ArrayList<>();
		valList.add(rev1);
		valList.add(rev2);

		GroupValidator group = new GroupValidator();
		group.setValidators(valList);
		group.setOperator(ValidatorOperator.EXACT_ONE);

		assertTrue(group.validate("123abc"));
		assertTrue(group.validate("ABC"));
		assertTrue(group.validate("abc"));
		assertTrue(group.validate("aBcD"));
		assertTrue(group.validate("ABC0234"));

		assertFalse(group.validate(""));
		assertFalse(group.validate("+"));
		assertFalse(group.validate("_"));
		assertFalse(group.validate(" "));
		assertFalse(group.validate("1234"));
	}

	@Test
	void testXORGroupFourValidators()
	{
		AlphabetValidator val1 = new AlphabetValidator("ABC");
		AlphabetValidator val2 = new AlphabetValidator("DEF");
		AlphabetValidator val3 = new AlphabetValidator("GHI");
		AlphabetValidator val4 = new AlphabetValidator("JKL");

		List<IValidator> l = new ArrayList<>();
		l.add(val1);
		l.add(val2);
		l.add(val3);
		l.add(val4);

		GroupValidator vg = new GroupValidator();
		vg.setValidators(l);
		vg.setOperator(ValidatorOperator.EXACT_ONE);

		assertTrue(vg.validate("AAABBB"));
		assertTrue(vg.validate("DEF"));
		assertTrue(vg.validate("GHIIII"));

		assertFalse(vg.validate("ABCD"));
		assertFalse(vg.validate("ABCDEF"));
		assertFalse(vg.validate("XYZ"));
	}

	@Test
	void testXNORGroup()
	{
		// XNOR: all or none values must be valid
		RegExValidator rev1 = new RegExValidator("[0-9a-d]*");
		RegExValidator rev2 = new RegExValidator("[0-9a-zA-Z]*");

		ArrayList<IValidator> valList = new ArrayList<>();
		valList.add(rev1);
		valList.add(rev2);

		GroupValidator group = new GroupValidator();
		group.setValidators(valList);
		group.setOperator(ValidatorOperator.ALL_OR_NONE);

		assertTrue(group.validate(""));
		assertTrue(group.validate("+"));
		assertTrue(group.validate("#"));
		assertTrue(group.validate("123456"));
		assertTrue(group.validate(""));
		assertTrue(group.validate("abc"));
		assertTrue(group.validate(" "));

		assertFalse(group.validate("02aefg"));
		assertFalse(group.validate("1234A"));
		assertFalse(group.validate("abcde"));
		assertFalse(group.validate("ABC"));
	}

	@Test
	void testXNORGroupFourValidators()
	{
		AlphabetValidator val1 = new AlphabetValidator("0123456789");
		LengthValidator val2 = new LengthValidator(5);
		GermanZipCodeValidator val3 = new GermanZipCodeValidator();
		RegExValidator val4 = new RegExValidator("[\\d]*");

		List<IValidator> l = new ArrayList<>();
		l.add(val1);
		l.add(val2);
		l.add(val3);
		l.add(val4);

		GroupValidator vg = new GroupValidator();
		vg.setValidators(l);
		vg.setOperator(ValidatorOperator.ALL_OR_NONE);

		assertTrue(vg.validate("12345"));
		assertTrue(vg.validate("XYZ"));
		assertTrue(vg.validate("01234"));
		assertTrue(vg.validate("33333"));

		assertFalse(vg.validate("1234"));
		assertFalse(vg.validate("123AA"));

	}
}
