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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.emau.icmvc.ttp.deduplication.PrivacyStrategy;
import org.emau.icmvc.ttp.deduplication.config.model.BloomFilterConfig;
import org.emau.icmvc.ttp.deduplication.config.model.Privacy;
import org.emau.icmvc.ttp.deduplication.config.model.SourceField;
import org.emau.icmvc.ttp.epix.common.model.enums.FieldName;
import org.emau.icmvc.ttp.epix.persistence.model.IdentityPreprocessed;
import org.junit.jupiter.api.Test;

public class PrivacyConfigTest
{
	@Test
	public void testSaltFields()
	{
		BloomFilterConfig bfc = new BloomFilterConfig();
		bfc.setAlgorithm("org.emau.icmvc.ttp.deduplication.impl.bloomfilter.DoubleHashingStrategy");
		bfc.setField(FieldName.value6);
		bfc.setFold(0);
		bfc.setBitsPerNgram(15);
		bfc.setLength(100);
		bfc.setNGram(2);
		SourceField sf = new SourceField();
		sf.setName(FieldName.firstName);
		sf.setSaltField(FieldName.birthDate);
		List<SourceField> tmp = new ArrayList<>();
		tmp.add(sf);

		bfc.setSourceFields(tmp);

		Privacy p = new Privacy();
		p.getBloomFilterConfigs().add(bfc);

		PrivacyStrategy ps = new PrivacyStrategy(p);

		Calendar date1 = Calendar.getInstance();
		date1.set(1995, Calendar.MAY, 21);
		Calendar date2 = Calendar.getInstance();
		date2.set(1994, Calendar.FEBRUARY, 12);

		IdentityPreprocessed ident1 = new IdentityPreprocessed();
		ident1.setFirstName("Hans");
		ident1.setBirthDate(date1.getTime().toString());
		IdentityPreprocessed iibd1 = ps.setPrivacyAttributes(ident1);

		IdentityPreprocessed ident2 = new IdentityPreprocessed();
		ident2.setFirstName("Olaf");
		ident2.setBirthDate(date1.getTime().toString());
		IdentityPreprocessed iibd2 = ps.setPrivacyAttributes(ident2);

		IdentityPreprocessed ident3 = new IdentityPreprocessed();
		ident3.setFirstName("Hans");
		ident3.setBirthDate(date2.getTime().toString());
		IdentityPreprocessed iibd3 = ps.setPrivacyAttributes(ident3);

		IdentityPreprocessed ident4 = new IdentityPreprocessed();
		ident4.setFirstName("Hans");
		ident4.setBirthDate(date1.getTime().toString());
		IdentityPreprocessed iibd4 = ps.setPrivacyAttributes(ident4);

		assertEquals(iibd1.getValue6(), iibd4.getValue6());
		assertNotEquals(iibd1.getValue6(), iibd2.getValue6());
		assertNotEquals(iibd1.getValue6(), iibd3.getValue6());
	}

	@Test
	public void testSaltValues()
	{
		BloomFilterConfig bfc = new BloomFilterConfig();
		bfc.setAlgorithm("org.emau.icmvc.ttp.deduplication.impl.bloomfilter.DoubleHashingStrategy");
		bfc.setField(FieldName.value6);
		bfc.setFold(0);
		bfc.setBitsPerNgram(15);
		bfc.setLength(100);
		bfc.setNGram(2);

		SourceField sf = new SourceField();
		sf.setName(FieldName.firstName);
		sf.setSaltValue("ABC123");
		List<SourceField> tmp = new ArrayList<>();
		tmp.add(sf);

		bfc.setSourceFields(tmp);

		Privacy p = new Privacy();
		p.getBloomFilterConfigs().add(bfc);

		PrivacyStrategy ps = new PrivacyStrategy(p);

		Calendar date1 = Calendar.getInstance();
		date1.set(1995, Calendar.MAY, 21);
		Calendar date2 = Calendar.getInstance();
		date2.set(1994, Calendar.FEBRUARY, 12);

		IdentityPreprocessed ident1 = new IdentityPreprocessed();
		ident1.setFirstName("Hans");
		ident1.setBirthDate(date1.getTime().toString());
		IdentityPreprocessed iibd1 = ps.setPrivacyAttributes(ident1);

		IdentityPreprocessed ident2 = new IdentityPreprocessed();
		ident2.setFirstName("Olaf");
		ident2.setBirthDate(date1.getTime().toString());
		IdentityPreprocessed iibd2 = ps.setPrivacyAttributes(ident2);

		IdentityPreprocessed ident3 = new IdentityPreprocessed();
		ident3.setFirstName("Hans");
		ident3.setBirthDate(date2.getTime().toString());
		IdentityPreprocessed iibd3 = ps.setPrivacyAttributes(ident3);

		IdentityPreprocessed ident4 = new IdentityPreprocessed();
		ident4.setFirstName("Hans");
		ident4.setBirthDate(date1.getTime().toString());
		IdentityPreprocessed iibd4 = ps.setPrivacyAttributes(ident4);

		assertEquals(iibd1.getValue6(), iibd4.getValue6());
		assertEquals(iibd1.getValue6(), iibd3.getValue6());
		assertNotEquals(iibd1.getValue6(), iibd2.getValue6());
	}

	@Test
	public void testSetOfMultipleBloomFiltersOk()
	{
		final int NUMBER_OF_CONFIGS = 3;
		List<BloomFilterConfig> configs = new ArrayList<>();

		List<FieldName> fieldnames = Arrays.asList(FieldName.value6, FieldName.value7, FieldName.value8);

		for (int i = 0; i < NUMBER_OF_CONFIGS; ++i)
		{
			BloomFilterConfig bfc = new BloomFilterConfig();
			bfc.setAlgorithm("org.emau.icmvc.ttp.deduplication.impl.bloomfilter.DoubleHashingStrategy");
			bfc.setField(fieldnames.get(i));
			bfc.setFold(0);
			bfc.setBitsPerNgram(15);
			bfc.setLength(100);
			bfc.setNGram(2);

			SourceField sf = new SourceField();
			sf.setName(FieldName.firstName);
			sf.setSaltValue("ABC123");
			List<SourceField> tmp = new ArrayList<>();
			tmp.add(sf);

			bfc.setSourceFields(tmp);

			configs.add(bfc);
		}

		Privacy p = new Privacy();
		p.setBloomFilterConfigs(configs);

		assertEquals(NUMBER_OF_CONFIGS, p.getBloomFilterConfigs().size());
		assertEquals(configs, p.getBloomFilterConfigs());
	}

	@Test
	public void testOverwrite()
	{
		BloomFilterConfig bfc = new BloomFilterConfig();
		bfc.setAlgorithm("org.emau.icmvc.ttp.deduplication.impl.bloomfilter.DoubleHashingStrategy");
		bfc.setField(FieldName.value6);
		bfc.setFold(0);
		bfc.setBitsPerNgram(15);
		bfc.setLength(100);
		bfc.setNGram(2);

		SourceField sf = new SourceField();
		sf.setName(FieldName.firstName);
		List<SourceField> tmp = new ArrayList<>();
		tmp.add(sf);

		bfc.setSourceFields(tmp);

		Privacy p = new Privacy();
		p.getBloomFilterConfigs().add(bfc);

		PrivacyStrategy ps = new PrivacyStrategy(p);

		IdentityPreprocessed ident1 = new IdentityPreprocessed();
		ident1.setFirstName("Hans");
		IdentityPreprocessed iibd1 = ps.setPrivacyAttributes(ident1, false);

		IdentityPreprocessed ident2 = new IdentityPreprocessed();
		ident2.setValue6(iibd1.getValue6());
		IdentityPreprocessed iibd2 = ps.setPrivacyAttributes(ident2, false);

		assertEquals(iibd1.getValue6(), iibd2.getValue6());
	}
}
