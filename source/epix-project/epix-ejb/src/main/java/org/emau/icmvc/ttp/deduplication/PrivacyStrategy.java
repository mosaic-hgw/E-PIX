package org.emau.icmvc.ttp.deduplication;

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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.emau.icmvc.ttp.deduplication.config.model.BloomFilterConfig;
import org.emau.icmvc.ttp.deduplication.config.model.Privacy;
import org.emau.icmvc.ttp.deduplication.config.model.SourceField;
import org.emau.icmvc.ttp.deduplication.impl.bloomfilter.BloomFilter;
import org.emau.icmvc.ttp.deduplication.impl.bloomfilter.DoubleHashingStrategy;
import org.emau.icmvc.ttp.deduplication.impl.bloomfilter.DoubleHashingStrategyFaster;
import org.emau.icmvc.ttp.deduplication.impl.bloomfilter.RandomHashingStrategy;
import org.emau.icmvc.ttp.epix.common.deduplication.IBloomFilterStrategy;
import org.emau.icmvc.ttp.epix.common.exception.NotImplementedException;
import org.emau.icmvc.ttp.epix.common.model.enums.FieldName;
import org.emau.icmvc.ttp.epix.persistence.model.Identity;
import org.emau.icmvc.ttp.epix.persistence.model.IdentityPreprocessed;
import org.emau.icmvc.ttp.utils.ReflectionUtil;

/**
 * @author Christopher Hampf
 */
public class PrivacyStrategy
{
	private static final Logger logger = LogManager.getLogger(PrivacyStrategy.class);

	private final Privacy privacy;

	public PrivacyStrategy(Privacy privacyConfig)
	{
		if (privacyConfig != null)
		{
			logger.info("privacy attributes configured");
		}
		else
		{
			logger.info("no privacy config available");
		}

		this.privacy = privacyConfig;
	}

	public IdentityPreprocessed setPrivacyAttributes(IdentityPreprocessed identity)
	{
		return setPrivacyAttributes(identity, true);
	}

	public IdentityPreprocessed setPrivacyAttributes(IdentityPreprocessed identity, boolean overwrite)
	{
		if (privacy != null)
		{
			for (BloomFilterConfig bfc : privacy.getBloomFilterConfigs())
			{
				String algorithm = bfc.getAlgorithm();
				int length = bfc.getLength();
				int ngram = bfc.getNGram();
				int bitsPerNgram = bfc.getBitsPerNgram();
				int folds = bfc.getFold();

				boolean isRandomHashing = false;

				IBloomFilterStrategy strategy;
				if (algorithm.equals("org.emau.icmvc.ttp.deduplication.impl.bloomfilter.DoubleHashingStrategy"))
				{
					strategy = new DoubleHashingStrategy(length, bitsPerNgram, ngram);
				}
				else if (algorithm.equals("org.emau.icmvc.ttp.deduplication.impl.bloomfilter.DoubleHashingStrategyFaster"))
				{
					strategy = new DoubleHashingStrategyFaster(length, bitsPerNgram, ngram);
				}
				else if (algorithm.equals("org.emau.icmvc.ttp.deduplication.impl.bloomfilter.RandomHashingStrategy"))
				{
					Map<String, Long> tmp = new HashMap<>();

					for (SourceField field : bfc.getSourceFields())
					{
						tmp.put(field.getName().name(), field.getSeed());
					}

					String alphabet = bfc.getAlphabet() != null && bfc.getAlphabet().length() > 0 ? bfc.getAlphabet() : "ABCDEFGHIJKLMNOPQRSTUVWXYZ .-0123456789";
					strategy = new RandomHashingStrategy(length, bitsPerNgram, ngram, alphabet, tmp);

					isRandomHashing = true;
				}
				else
				{
					throw new NotImplementedException();
				}

				BloomFilter bf = new BloomFilter(length, strategy);
				for (SourceField field : bfc.getSourceFields())
				{
					try
					{
						Object tmpVal = ReflectionUtil.getProperty(identity, field.getName().name());
						String val = tmpVal != null ? tmpVal.toString() : "";

						String param = "";
						if (!isRandomHashing)
						{
							String salt = "";
							if (field.getSaltField() != null)
							{
								salt = ReflectionUtil.getProperty(identity, field.getSaltField().name()).toString();
							}
							else
							{
								salt = field.getSaltValue() == null ? "" : field.getSaltValue();
							}

							param = salt;
						}
						else
						{
							param = field.getName().value();
						}

						bf.add(val, param);
					}
					catch (Exception e)
					{
						String message = "exception while getting privacy attributes: " + e.getMessage();
						logger.error(message, e);
					}
				}

				if (folds > 0)
				{
					try
					{
						bf = bf.fold(folds);
					}
					catch (Exception iae)
					{
						logger.error("exception while fold bloom filter: " + iae.getMessage(), iae);
					}
				}

				try
				{
					if (bfc.getBalanced() != null)
					{
						bf = bf.getBalancedBloomFilter(bfc.getBalanced().getSeed());
					}

					Object tmpField = ReflectionUtil.getProperty(identity, bfc.getField().name());
					String bfField = tmpField != null ? (String) tmpField : "";

					if (overwrite || bfField.isEmpty())
					{
						ReflectionUtil.setProperty(identity, bfc.getField().name(), bf.getAsEncodedString(), String.class);
					}
				}
				catch (Exception e)
				{
					String message = "exception while setting privacy attributes: " + e.getMessage();
					logger.error(message, e);
				}
			}
		}

		return identity;
	}

	public IdentityPreprocessed removePII(IdentityPreprocessed identity)
	{
		Set<FieldName> bloomFilterFields = new HashSet<>();

		if (privacy != null)
		{
			for (BloomFilterConfig bfc : privacy.getBloomFilterConfigs())
			{
				bloomFilterFields.add(bfc.getField());
			}
		}

		if (!bloomFilterFields.contains(FieldName.firstName))
		{
			identity.setFirstName(null);
		}
		if (!bloomFilterFields.contains(FieldName.middleName))
		{
			identity.setMiddleName(null);
		}
		if (!bloomFilterFields.contains(FieldName.lastName))
		{
			identity.setLastName(null);
		}
		if (!bloomFilterFields.contains(FieldName.prefix))
		{
			identity.setPrefix(null);
		}
		if (!bloomFilterFields.contains(FieldName.suffix))
		{
			identity.setSuffix(null);
		}
		if (!bloomFilterFields.contains(FieldName.birthDate))
		{
			identity.setBirthDate(null);
		}
		if (!bloomFilterFields.contains(FieldName.birthPlace))
		{
			identity.setBirthPlace(null);
		}
		if (!bloomFilterFields.contains(FieldName.gender))
		{
			identity.setGender(' ');
		}
		if (!bloomFilterFields.contains(FieldName.birthPlace))
		{
			identity.setBirthDate(null);
		}
		if (!bloomFilterFields.contains(FieldName.race))
		{
			identity.setRace(null);
		}
		if (!bloomFilterFields.contains(FieldName.religion))
		{
			identity.setReligion(null);
		}
		if (!bloomFilterFields.contains(FieldName.mothersMaidenName))
		{
			identity.setMothersMaidenName(null);
		}
		if (!bloomFilterFields.contains(FieldName.degree))
		{
			identity.setDegree(null);
		}
		if (!bloomFilterFields.contains(FieldName.motherTongue))
		{
			identity.setMotherTongue(null);
		}
		if (!bloomFilterFields.contains(FieldName.nationality))
		{
			identity.setNationality(null);
		}
		if (!bloomFilterFields.contains(FieldName.civilStatus))
		{
			identity.setCivilStatus(null);
		}
		if (!bloomFilterFields.contains(FieldName.externalDate))
		{
			identity.setExternalTimestamp(null);
		}
		if (!bloomFilterFields.contains(FieldName.value1))
		{
			identity.setValue1(null);
		}
		if (!bloomFilterFields.contains(FieldName.value2))
		{
			identity.setValue2(null);
		}
		if (!bloomFilterFields.contains(FieldName.value3))
		{
			identity.setValue3(null);
		}
		if (!bloomFilterFields.contains(FieldName.value4))
		{
			identity.setValue4(null);
		}
		if (!bloomFilterFields.contains(FieldName.value5))
		{
			identity.setValue5(null);
		}
		if (!bloomFilterFields.contains(FieldName.value6))
		{
			identity.setValue6(null);
		}
		if (!bloomFilterFields.contains(FieldName.value7))
		{
			identity.setValue7(null);
		}
		if (!bloomFilterFields.contains(FieldName.value8))
		{
			identity.setValue8(null);
		}
		if (!bloomFilterFields.contains(FieldName.value9))
		{
			identity.setValue9(null);
		}
		if (!bloomFilterFields.contains(FieldName.value10))
		{
			identity.setValue10(null);
		}

		return identity;
	}

	public Identity removePII(Identity identity)
	{
		Set<FieldName> bloomFilterFields = new HashSet<>();

		if (privacy != null)
		{
			for (BloomFilterConfig bfc : privacy.getBloomFilterConfigs())
			{
				bloomFilterFields.add(bfc.getField());
			}
		}

		if (!bloomFilterFields.contains(FieldName.firstName))
		{
			identity.setFirstName(null);
		}
		if (!bloomFilterFields.contains(FieldName.middleName))
		{
			identity.setMiddleName(null);
		}
		if (!bloomFilterFields.contains(FieldName.lastName))
		{
			identity.setLastName(null);
		}
		if (!bloomFilterFields.contains(FieldName.prefix))
		{
			identity.setPrefix(null);
		}
		if (!bloomFilterFields.contains(FieldName.suffix))
		{
			identity.setSuffix(null);
		}
		if (!bloomFilterFields.contains(FieldName.birthDate))
		{
			identity.setBirthDate(null);
		}
		if (!bloomFilterFields.contains(FieldName.birthPlace))
		{
			identity.setBirthPlace(null);
		}
		if (!bloomFilterFields.contains(FieldName.gender))
		{
			identity.setGender(' ');
		}
		if (!bloomFilterFields.contains(FieldName.birthPlace))
		{
			identity.setBirthDate(null);
		}
		if (!bloomFilterFields.contains(FieldName.race))
		{
			identity.setRace(null);
		}
		if (!bloomFilterFields.contains(FieldName.religion))
		{
			identity.setReligion(null);
		}
		if (!bloomFilterFields.contains(FieldName.mothersMaidenName))
		{
			identity.setMothersMaidenName(null);
		}
		if (!bloomFilterFields.contains(FieldName.degree))
		{
			identity.setDegree(null);
		}
		if (!bloomFilterFields.contains(FieldName.motherTongue))
		{
			identity.setMotherTongue(null);
		}
		if (!bloomFilterFields.contains(FieldName.nationality))
		{
			identity.setNationality(null);
		}
		if (!bloomFilterFields.contains(FieldName.civilStatus))
		{
			identity.setCivilStatus(null);
		}
		if (!bloomFilterFields.contains(FieldName.externalDate))
		{
			identity.setExternalTimestamp(null);
		}
		if (!bloomFilterFields.contains(FieldName.value1))
		{
			identity.setValue1(null);
		}
		if (!bloomFilterFields.contains(FieldName.value2))
		{
			identity.setValue2(null);
		}
		if (!bloomFilterFields.contains(FieldName.value3))
		{
			identity.setValue3(null);
		}
		if (!bloomFilterFields.contains(FieldName.value4))
		{
			identity.setValue4(null);
		}
		if (!bloomFilterFields.contains(FieldName.value5))
		{
			identity.setValue5(null);
		}
		if (!bloomFilterFields.contains(FieldName.value6))
		{
			identity.setValue6(null);
		}
		if (!bloomFilterFields.contains(FieldName.value7))
		{
			identity.setValue7(null);
		}
		if (!bloomFilterFields.contains(FieldName.value8))
		{
			identity.setValue8(null);
		}
		if (!bloomFilterFields.contains(FieldName.value9))
		{
			identity.setValue9(null);
		}
		if (!bloomFilterFields.contains(FieldName.value10))
		{
			identity.setValue10(null);
		}
		identity.getContacts().clear();

		return identity;
	}

	public void copyPrivacyAttributes(IdentityPreprocessed from, Identity to)
	{
		if (privacy != null)
		{
			for (BloomFilterConfig bfc : privacy.getBloomFilterConfigs())
			{
				try
				{
					String val = ReflectionUtil.getProperty(from, bfc.getField().name()).toString();
					ReflectionUtil.setProperty(to, bfc.getField().name(), val, String.class);
				}
				catch (Exception e)
				{
					String message = "exception while copying privacy attributes: " + e.getMessage();
					logger.error(message, e);
				}
			}

		}
	}
}
