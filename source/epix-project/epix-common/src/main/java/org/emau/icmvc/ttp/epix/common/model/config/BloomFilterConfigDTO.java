package org.emau.icmvc.ttp.epix.common.model.config;

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

import org.emau.icmvc.ttp.epix.common.model.enums.FieldName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BloomFilterConfigDTO implements Serializable
{
	private static final long serialVersionUID = -4812871977413201397L;
	private final String algorithm;
	private final FieldName field;
	private final int length;
	private final int ngrams;
	private final int bitsPerNgram;
	private final int fold;
	private final String alphabet;
	private final BalancedDTO balanced;
	private final List<SourceFieldDTO> sourceFields = new ArrayList<>();

	public BloomFilterConfigDTO(String algorithm, FieldName field, int length, int ngrams, int bitsPerNgram, int fold, String alphabet, BalancedDTO balanced, List<SourceFieldDTO> sourceFields)
	{
		super();
		this.algorithm = algorithm;
		this.field = field;
		this.length = length;
		this.ngrams = ngrams;
		this.bitsPerNgram = bitsPerNgram;
		this.fold = fold;
		this.alphabet = alphabet;
		this.balanced = balanced;
		this.sourceFields.addAll(sourceFields);
	}

	public String getAlgorithm()
	{
		return algorithm;
	}

	public FieldName getField()
	{
		return field;
	}

	public int getLength()
	{
		return length;
	}

	public int getNgrams()
	{
		return ngrams;
	}

	public int getBitsPerNgram()
	{
		return bitsPerNgram;
	}

	public int getFold()
	{
		return fold;
	}

	public String getAlphabet()
	{
		return alphabet;
	}

	public BalancedDTO getBalanced()
	{
		return balanced;
	}

	public List<SourceFieldDTO> getSourceFields()
	{
		return sourceFields;
	}


	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((algorithm == null) ? 0 : algorithm.hashCode());
		result = prime * result + ((field == null) ? 0 : field.hashCode());
		result = prime * result + length;
		result = prime * result + ngrams;
		result = prime * result + bitsPerNgram;
		result = prime * result + fold;
		result = prime * result + ((alphabet == null) ? 0 : alphabet.hashCode());
		result = prime * result + ((balanced == null) ? 0 : balanced.hashCode());
		result = prime * result + sourceFields.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BloomFilterConfigDTO other = (BloomFilterConfigDTO) obj;
		if (algorithm == null)
		{
			if (other.algorithm != null)
				return false;
		}
		else if (!algorithm.equals(other.algorithm))
			return false;
		if (field == null)
		{
			if (other.field != null)
				return false;
		}
		else if (!field.equals(other.field))
			return false;
		if (length != other.length)
			return false;
		if (ngrams != other.ngrams)
			return false;
		if (bitsPerNgram != other.bitsPerNgram)
			return false;
		if (fold != other.fold)
			return false;
		if (alphabet == null)
		{
			if (other.alphabet != null)
				return false;
		}
		else if (!alphabet.equals(other.alphabet))
			return false;
		if (balanced == null)
		{
			if (other.balanced != null)
				return false;
		}
		else if (!balanced.equals(other.balanced))
			return false;
		return sourceFields.equals(other.sourceFields);
	}

	@Override
	public String toString()
	{
		return "BloomFilterConfigDTO [algorithm=" + algorithm + ", field=" + field + ", length=" + length + ", ngrams=" + ngrams
				+ ", bitsPerNgram=" + bitsPerNgram + ", fold=" + fold + ", alphabet=" + alphabet + ", balanced=" + balanced + ", sourceFields=" + sourceFields + "]";
	}
}
