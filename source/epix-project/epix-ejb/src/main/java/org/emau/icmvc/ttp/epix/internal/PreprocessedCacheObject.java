package org.emau.icmvc.ttp.epix.internal;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.emau.icmvc.ttp.deduplication.config.model.Field;
import org.emau.icmvc.ttp.epix.common.exception.MPIErrorCode;
import org.emau.icmvc.ttp.epix.common.exception.MPIException;
import org.emau.icmvc.ttp.epix.common.model.enums.BlockingMode;
import org.emau.icmvc.ttp.epix.common.model.enums.FieldName;
import org.emau.icmvc.ttp.epix.persistence.model.IdentityPreprocessed;

public class PreprocessedCacheObject
{
	private static final Logger logger = LogManager.getLogger(PreprocessedCacheObject.class);
	private static final String EMPTY_STRING = "";
	private static final int MAX_MULTI_VALUES = 6;
	private static final Map<Integer, List<int[]>> PRECALCULATED_PERMUTATIONS = new HashMap<>();
	static
	{
		for (int i = 0; i < MAX_MULTI_VALUES; i++)
		{
			String s = "0";
			for (int j = 1; j <= i; j++)
			{
				s += "," + j;
			}
			PRECALCULATED_PERMUTATIONS.put(i + 1, createAllIntCombinations(s, ','));
		}
	}
	private static final int[] indicesForIncompleteMultiValue = { 0, 2, 9, 40, 205, 1236 };
	private long identityId;
	private long personId;
	private final int matchHashCode;
	private final String[] matchFieldValues;
	// blocking als arg vereinfachter levenshtein -> anzahl gleicher buchstaben / groesste laenge
	// max. betrachtete anzahl ist 4
	// alle zeichen ausser a-z werden als ein zeichen betrachtet
	// -> als aehnlichkeitsmass ist blocking(a, b) immer <= levenshtein(a, b)
	// anzahl wird wie folgt als bitvektor in 2 longs gespeichert: 1 = 0001; 2 = 0011; 3 = 0111; 4 = 1111
	// (damit bitoperationen zum vergleich genutzt werden koennen)
	// bsp: anne
	// 0001 0000 0000 0000 0001 0000 0000 0000 0000 0000 0000 0000 0000 0011 0000 0000
	// a--a b--b c--c d--d e--e f--f g--g h--h i--i j--j k--k l--l m--m n--n o--o p--p
	// 0000 0000 0000 0110 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000
	// ---- anzahl zeichen q--q r--r s--s t--t u--u v--v w--w x--x y--y z--z andere z.
	// bei blockingMode = NUMBERS ist der erste long 0 und im zweiten stehen die anzahlen fuer die ziffern 1-9 und alle anderen zeichen
	// das blocking erfolgt in IdentityPreprocessedCache.block(...)
	private final long[] blockFieldValues;
	// enthalten die max-indexe fuer das jeweilige field
	private final int[] countForMultiValueForBlocking;
	private final int[] indexAddForBlockingToMatching;
	private final int[] countForMultiValueForMatching;
	private final int[] indexForIncompleteMultiValueForMatching;
	private final boolean hasMultiValuesForMatching;
	private final boolean hasMultiValuesForBlocking;

	public PreprocessedCacheObject(IdentityPreprocessed ip, Field[] blockFields, Field[] matchFields, boolean multiValueFields) throws MPIException
	{
		super();
		this.identityId = ip.getIdentityId();
		this.personId = ip.getPersonId();
		StringBuilder matchHashSB = new StringBuilder();
		countForMultiValueForBlocking = new int[blockFields.length];
		indexAddForBlockingToMatching = new int[blockFields.length];
		countForMultiValueForMatching = new int[matchFields.length];
		indexForIncompleteMultiValueForMatching = new int[matchFields.length];
		int indexAddForB2M = 0;
		if (multiValueFields)
		{
			List<String> allCombinations = new ArrayList<>();
			List<String> allBlockingCombinations = new ArrayList<>();
			int blockingIndex = 0;
			int matchingIndex = 0;
			for (Field field : matchFields)
			{
				String s = getFieldValue(ip, field.getName());
				if (field.getMultipleValues() != null)
				{
					List<String> tempList = createAllCombinationsPrecalculated(s, field.getMultipleValues().getSeparatorChar(),
							field.getName().name());
					allCombinations.addAll(tempList);
					if (field.isBlockingField())
					{
						allBlockingCombinations.addAll(tempList);
					}
					else
					{
						indexAddForB2M += tempList.size();
					}
				}
				else
				{
					allCombinations.add(s);
					if (field.isBlockingField())
					{
						allBlockingCombinations.add(s);
					}
					else
					{
						indexAddForB2M++;
					}
				}
				matchHashSB.append(s);
				countForMultiValueForMatching[matchingIndex++] = allCombinations.size() - 1;
				if (field.isBlockingField())
				{
					indexAddForBlockingToMatching[blockingIndex] = indexAddForB2M;
					countForMultiValueForBlocking[blockingIndex++] = allBlockingCombinations.size() - 1;
				}
			}
			matchFieldValues = new String[allCombinations.size()];
			int i = 0;
			for (String singleCombination : allCombinations)
			{
				matchFieldValues[i++] = singleCombination;
			}
			blockFieldValues = new long[allBlockingCombinations.size() * 2];
			i = 0;
			int index = 0;
			for (String singleCombination : allBlockingCombinations)
			{
				if (i > countForMultiValueForBlocking[index])
				{
					index++;
				}
				long[] temp = calculateBlockValue(singleCombination, blockFields[index].getBlockingMode());
				blockFieldValues[i * 2] = temp[0];
				blockFieldValues[i * 2 + 1] = temp[1];
				i++;
			}
			hasMultiValuesForMatching = matchFieldValues.length > matchFields.length;
			hasMultiValuesForBlocking = blockFieldValues.length > blockFields.length * 2;
		}
		else
		{
			hasMultiValuesForBlocking = false;
			hasMultiValuesForMatching = false;
			blockFieldValues = new long[blockFields.length * 2];
			matchFieldValues = new String[matchFields.length];
			int blockingIndex = 0;
			int matchingIndex = 0;
			for (Field field : matchFields)
			{
				countForMultiValueForMatching[matchingIndex] = matchingIndex;
				String s = getFieldValue(ip, field.getName());
				matchFieldValues[matchingIndex] = s;
				matchHashSB.append(s);
				matchingIndex++;
				if (field.isBlockingField())
				{
					countForMultiValueForBlocking[blockingIndex] = blockingIndex;
					long[] temp = calculateBlockValue(s, field.getBlockingMode());
					blockFieldValues[blockingIndex * 2] = temp[0];
					blockFieldValues[blockingIndex * 2 + 1] = temp[1];
					indexAddForBlockingToMatching[blockingIndex] = indexAddForB2M;
					blockingIndex++;
				}
				else
				{
					indexAddForB2M++;
				}
			}
		}
		if (hasMultiValuesForMatching)
		{
			for (int i = 0; i < matchFields.length; i++)
			{
				int count = countForMultiValueForMatching[i] - (i < 1 ? 0 : countForMultiValueForMatching[i - 1]);
				for (int j = 1; j < indicesForIncompleteMultiValue.length; j++)
				{
					if (indicesForIncompleteMultiValue[j] > count)
					{
						indexForIncompleteMultiValueForMatching[i] = (i < 1 ? 0 : countForMultiValueForMatching[i - 1])
								+ indicesForIncompleteMultiValue[j - 1];
						break;
					}
				}
			}
		}
		matchHashCode = matchHashSB.toString().hashCode();
		logger.trace("initialised PreprocessedCacheObject with " + blockFieldValues.length / 2 + " block field values and " + matchFieldValues.length
				+ " match field values");
	}

	private static List<String> createAllCombinationsPrecalculated(String s, char separatorChar, String fieldName) throws MPIException
	{
		List<String> result = new ArrayList<>();
		String separator = String.valueOf(separatorChar);
		String[] temp = s.split(separator);
		int count = 0;
		for (int i = 0; i < temp.length; i++)
		{
			if (!temp[i].isEmpty())
			{
				count++;
				temp[i] = temp[i].intern();
			}
		}
		String[] singleValues = new String[count];
		int j = 0;
		for (String element : temp)
		{
			if (!element.isEmpty())
			{
				singleValues[j++] = element;
			}
		}
		if (singleValues.length > MAX_MULTI_VALUES)
		{
			String message = "there are " + singleValues.length + " values within the field " + fieldName + ", but only " + MAX_MULTI_VALUES
					+ " are allowed";
			logger.error(message);
			throw new MPIException(MPIErrorCode.TO_MANY_VALUES_FOR_MULTIVALUE_FIELD, message);
		}
		StringBuilder sb = new StringBuilder();
		for (int[] singleList : PRECALCULATED_PERMUTATIONS.get(singleValues.length))
		{
			sb.setLength(0);
			for (int i : singleList)
			{
				sb.append(singleValues[i]);
			}
			String singleResult = sb.toString().intern();
			if (!singleResult.isEmpty())
			{
				result.add(singleResult);
			}
		}
		return result;
	}

	private static List<int[]> createAllIntCombinations(String s, char separatorChar)
	{
		List<int[]> result = new ArrayList<>();
		String separator = String.valueOf(separatorChar);
		String[] temp = s.split(separator);
		int count = 0;
		for (String element : temp)
		{
			if (!element.isEmpty())
			{
				count++;
			}
		}
		int[] singleValues = new int[count];
		int j = 0;
		for (String element : temp)
		{
			if (!element.isEmpty())
			{
				singleValues[j++] = Integer.valueOf(element);
			}
		}
		combineRecursiveInt(singleValues, 0, new boolean[singleValues.length], result);
		return result;
	}

	private static void combineRecursiveInt(int[] singleValues, int pos, boolean[] filter, List<int[]> result)
	{
		if (pos >= singleValues.length)
		{
			permutateInt(singleValues, 0, filter, result);
		}
		else
		{
			filter[pos] = false;
			combineRecursiveInt(singleValues, pos + 1, filter, result);
			filter[pos] = true;
			combineRecursiveInt(singleValues, pos + 1, filter, result);
		}
	}

	private static void permutateInt(int[] singleValues, int pos, boolean[] filter, List<int[]> result)
	{
		if (pos == singleValues.length)
		{
			int count = 0;
			for (int i = 0; i < singleValues.length; i++)
			{
				if (filter[i])
				{
					count++;
				}
			}
			if (count > 0)
			{
				int[] array = new int[count];
				count = 0;
				for (int i = 0; i < singleValues.length; i++)
				{
					if (filter[i])
					{
						array[count++] = singleValues[i];
					}
				}
				result.add(array);
			}
		}
		else if (filter[pos])
		{
			// rueckwaerts, damit origstring ganz am ende der liste steht
			for (int i = singleValues.length - 1; i >= pos; i--)
			{
				if (filter[i])
				{
					swap(singleValues, pos, i);
					permutateInt(singleValues, pos + 1, filter, result);
					swap(singleValues, pos, i);
				}
			}
		}
		else
		{
			permutateInt(singleValues, pos + 1, filter, result);
		}
	}

	private static void swap(int[] singleValues, int i, int j)
	{
		Integer temp = singleValues[i];
		singleValues[i] = singleValues[j];
		singleValues[j] = temp;
	}

	private static long[] calculateBlockValue(String s, BlockingMode blockingMode)
	{
		long[] result = new long[2];
		switch (blockingMode)
		{
			case TEXT:
				short[] charsCounter = new short[27];
				for (char c : s.toCharArray())
				{
					int pos = c - 'A';
					if (pos > 26 || pos < 0)
					{
						pos = 26; // alle nicht-buchstaben
					}
					charsCounter[pos]++;
				}
				long firstLong = 0;
				for (int j = 0; j < 16; j++)
				{
					firstLong <<= 4;
					switch (charsCounter[j])
					{
						case 0:
							break;
						case 1:
							firstLong |= 1;
							break;
						case 2:
							firstLong |= 3;
							break;
						case 3:
							firstLong |= 7;
							break;
						default:
							firstLong |= 15;
							break;
					}
				}
				result[0] = firstLong;
				long secondLong = s.length();
				for (int j = 16; j < 26; j++)
				{
					secondLong <<= 4;
					switch (charsCounter[j])
					{
						case 0:
							break;
						case 1:
							secondLong |= 1;
							break;
						case 2:
							secondLong |= 3;
							break;
						case 3:
							secondLong |= 7;
							break;
						default:
							secondLong |= 15;
							break;
					}
				}
				secondLong <<= 8;
				switch (charsCounter[26])
				{
					case 0:
						break;
					case 1:
						secondLong |= 1;
						break;
					case 2:
						secondLong |= 3;
						break;
					case 3:
						secondLong |= 7;
						break;
					case 4:
						secondLong |= 15;
						break;
					case 5:
						secondLong |= 31;
						break;
					case 6:
						secondLong |= 63;
						break;
					case 7:
						secondLong |= 127;
						break;
					default:
						secondLong |= 255;
						break;
				}
				result[1] = secondLong;
				break;
			case NUMBERS:
				short[] numbersCounter = new short[11];
				for (char c : s.toCharArray())
				{
					int pos = c - '0';
					if (pos > 9 || pos < 0)
					{
						pos = 10; // alle nicht-ziffern
					}
					numbersCounter[pos]++;
				}
				result[0] = 0;
				long numbersLong = s.length();
				for (int j = 0; j < 10; j++)
				{
					numbersLong <<= 4;
					switch (numbersCounter[j])
					{
						case 0:
							break;
						case 1:
							numbersLong |= 1;
							break;
						case 2:
							numbersLong |= 3;
							break;
						case 3:
							numbersLong |= 7;
							break;
						default:
							numbersLong |= 15;
							break;
					}
				}
				numbersLong <<= 8;
				switch (numbersCounter[10])
				{
					case 0:
						break;
					case 1:
						numbersLong |= 1;
						break;
					case 2:
						numbersLong |= 3;
						break;
					case 3:
						numbersLong |= 7;
						break;
					case 4:
						numbersLong |= 15;
						break;
					case 5:
						numbersLong |= 31;
						break;
					case 6:
						numbersLong |= 63;
						break;
					case 7:
						numbersLong |= 127;
						break;
					default:
						numbersLong |= 255;
						break;
				}
				result[1] = numbersLong;
				break;
		}
		return result;
	}

	private static String getFieldValue(IdentityPreprocessed ip, FieldName name) throws MPIException
	{
		String result = null;
		switch (name)
		{
			case birthDate:
				result = ip.getBirthDate();
				break;
			case birthPlace:
				result = ip.getBirthPlace();
				break;
			case civilStatus:
				result = ip.getCivilStatus();
				break;
			case degree:
				result = ip.getDegree();
				break;
			case firstName:
				result = ip.getFirstName();
				break;
			case gender:
				result = String.valueOf(ip.getGender());
				break;
			case lastName:
				result = ip.getLastName();
				break;
			case middleName:
				result = ip.getMiddleName();
				break;
			case motherTongue:
				result = ip.getMotherTongue();
				break;
			case mothersMaidenName:
				result = ip.getMothersMaidenName();
				break;
			case nationality:
				result = ip.getNationality();
				break;
			case prefix:
				result = ip.getPrefix();
				break;
			case race:
				result = ip.getRace();
				break;
			case religion:
				result = ip.getReligion();
				break;
			case suffix:
				result = ip.getSuffix();
				break;
			case value1:
				result = ip.getValue1();
				break;
			case value10:
				result = ip.getValue10();
				break;
			case value2:
				result = ip.getValue2();
				break;
			case value3:
				result = ip.getValue3();
				break;
			case value4:
				result = ip.getValue4();
				break;
			case value5:
				result = ip.getValue5();
				break;
			case value6:
				result = ip.getValue6();
				break;
			case value7:
				result = ip.getValue7();
				break;
			case value8:
				result = ip.getValue8();
				break;
			case value9:
				result = ip.getValue9();
				break;
			default:
				String message = "unexpected field name in matching config: " + name;
				logger.error(message);
				throw new MPIException(MPIErrorCode.INTERNAL_ERROR, message);
		}
		if (result != null)
		{
			return result.intern();
		}
		else
		{
			// matchfelder duerfen null sein
			return EMPTY_STRING;
		}
	}

	public long getIdentityId()
	{
		return identityId;
	}

	public long getPersonId()
	{
		return personId;
	}

	public void setDBIds(long personId, long identityId)
	{
		this.personId = personId;
		this.identityId = identityId;
	}

	public int getMatchHashCode()
	{
		return matchHashCode;
	}

	public String[] getMatchFieldValues()
	{
		return matchFieldValues;
	}

	public String getMatchFieldValue(int i)
	{
		return matchFieldValues[i];
	}

	public long getBlockFieldValue(int i)
	{
		return blockFieldValues[i];
	}

	public int getCountForMultiValueForBlocking(int i)
	{
		return countForMultiValueForBlocking[i];
	}

	public int getIndexAddForBlockingToMatching(int i)
	{
		return indexAddForBlockingToMatching[i];
	}

	public int getCountForMultiValueForMatching(int i)
	{
		return countForMultiValueForMatching[i];
	}

	public int getIndexForIncompleteMultiValueForMatching(int i)
	{
		return indexForIncompleteMultiValueForMatching[i];
	}

	public boolean hasMultiValuesForMatching()
	{
		return hasMultiValuesForMatching;
	}

	public boolean hasMultiValuesForBlocking()
	{
		return hasMultiValuesForBlocking;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(blockFieldValues);
		result = prime * result + (int) (identityId ^ identityId >>> 32);
		// result = prime * result + Arrays.hashCode(matchFieldValues); muss nicht mit verhasht werden, ist durch matchHashCode abgedeckt
		result = prime * result + matchHashCode;
		result = prime * result + (int) (personId ^ personId >>> 32);
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		PreprocessedCacheObject other = (PreprocessedCacheObject) obj;
		if (matchHashCode != other.matchHashCode)
		{
			return false;
		}
		if (!Arrays.equals(blockFieldValues, other.blockFieldValues))
		{
			return false;
		}
		if (!Arrays.equals(matchFieldValues, other.matchFieldValues))
		{
			return false;
		}
		if (identityId != other.identityId)
		{
			return false;
		}
		if (personId != other.personId)
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		return "PreprocessedCacheObject [identityId=" + identityId + ", personId=" + personId + ", matchHashCode=" + matchHashCode + "]";
	}
}
