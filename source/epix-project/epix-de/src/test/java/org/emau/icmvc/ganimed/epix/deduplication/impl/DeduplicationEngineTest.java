package org.emau.icmvc.ganimed.epix.deduplication.impl;

/*
 * ###license-information-start###
 * E-PIX - Enterprise Patient Identifier Cross-referencing
 * __
 * Copyright (C) 2009 - 2017 The MOSAIC Project - Institut fuer Community Medicine der
 * 							Universitaetsmedizin Greifswald - mosaic-projekt@uni-greifswald.de
 * 							concept and implementation
 * 							c. schack, d.langner, l. geidel
 * 							web client
 * 							a. blumentritt
 * 							g. weiher
 * 							please cite our publications
 * 							http://dx.doi.org/10.3414/ME14-01-0133
 * 							http://dx.doi.org/10.1186/s12967-015-0545-6
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

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.language.ColognePhonetic;
import org.apache.commons.codec.language.Soundex;
import org.apache.log4j.Logger;
import org.emau.icmvc.ganimed.deduplication.config.model.MatchingConfiguration;
import org.emau.icmvc.ganimed.deduplication.impl.LevenstheinMatchingAlgorithm;
import org.emau.icmvc.ganimed.epix.common.utils.XMLBindingUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DeduplicationEngineTest {

	@SuppressWarnings("unused")
	private MatchingConfiguration matchingConfiguration;

	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(DeduplicationEngineTest.class);

	@Before
	public void init() {
		XMLBindingUtil binder = new XMLBindingUtil();
		try {
			matchingConfiguration = binder.load(MatchingConfiguration.class, "matching-config-1.5.0.xml");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}
	}

	@Test
	public void testLevenstheinDistance() throws EncoderException {

		//		ColognePhonetic cp = new ColognePhonetic();
		//		String value = cp.colognePhonetic("test");
		//		boolean bool = cp.isEncodeEqual("test", "zzzzz");
		//		boolean b1 = cp.isEncodeEqual("lutz", "butz");
		//		boolean b2 = cp.isEncodeEqual("lars", "dirk");
		//		boolean b3 = cp.isEncodeEqual("tim", "tom");
		//		boolean b4 = cp.isEncodeEqual("lars", "lahrs");
		//		boolean b5 = cp.isEncodeEqual("2011-01-01", "2011-01-01");
		//		boolean b6 = cp.isEncodeEqual("2011-01-01", "2011-02-01");
		//		boolean b7 = cp.isEncodeEqual("2012-01-01", "2011-02-01");
		//		boolean b8 = cp.isEncodeEqual("2012-10-09", "2008-02-01");

		LevenstheinMatchingAlgorithm lva = new LevenstheinMatchingAlgorithm();
		Double apache = lva.match("Karl-Heinz", "Walter");
		System.out.println(apache);
		//		Assert.assertEquals(Math.floor(simmetrics), Math.floor(apache));
	}

	@Test
	public void testColognePhonetic() throws EncoderException {
		Soundex sx = new Soundex();
		ColognePhonetic cp = new ColognePhonetic();
		//FellegiSunterAlgorithm fsa = new FellegiSunterAlgorithm<String>();

		String toBlock = "Karl-Heinz";
		String candidate = "Katl-Heinz";

		System.out.println("Cologne Phonetik PCODE1: " + cp.colognePhonetic(toBlock));
		System.out.println("Cologne Phonetik PCODE2: " + cp.colognePhonetic(candidate));

		System.out.println("Soundex PCODE1: " + sx.encode(toBlock));
		System.out.println("Soundex PCODE2: " + sx.encode(candidate));

		System.out.println("Soundex PCODE1: " + sx.encode(toBlock));
		System.out.println("Soundex PCODE2: " + sx.encode(candidate));

		//System.out.println("FellegiSunter PCODE1: " + fsa);

		boolean bool = cp.isEncodeEqual(toBlock, candidate);
		System.out.println("Blocking: " + toBlock + "  " + candidate + " " + bool);
		System.out.println("Decition " + bool + " " + (bool ? 1 : 0));

		/*
		PreprocessingStrategy<Patient> ps = new CommonPreprocessor<Patient>();
		ps.preprocess(cp);
		
		DeduplicationStrategy<Patient> ds = new FellegiSunterAlgorithm<Patient>();
		ds.setMatchingConfiguration(matchingConfiguration);
		ds.match(toMatch, candidate)
		*/
	}

}
