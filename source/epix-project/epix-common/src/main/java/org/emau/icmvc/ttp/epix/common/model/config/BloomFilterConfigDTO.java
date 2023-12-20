package org.emau.icmvc.ttp.epix.common.model.config;

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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.emau.icmvc.ttp.epix.common.model.enums.FieldName;

public class BloomFilterConfigDTO implements Serializable
{
	private static final long serialVersionUID = 505962401870367366L;
	private static final String DEFAULT_ALGORITHM = "org.emau.icmvc.ttp.deduplication.impl.bloomfilter.RandomHashingStrategy";
	private static final String DEFAULT_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ .-0123456789mfoux";
	private String algorithm = DEFAULT_ALGORITHM;
	private FieldName field;
	private int length = 1000;
	private int ngrams = 2;
	private int bitsPerNgram = 15;
	private int fold = 0;
	private String alphabet = DEFAULT_ALPHABET;
	private BalancedDTO balanced;
	private final List<SourceFieldDTO> sourceFields = new ArrayList<>();

	public BloomFilterConfigDTO()
	{}

	public BloomFilterConfigDTO(String algorithm, FieldName field, int length, int ngrams, int bitsPerNgram, int fold, String alphabet, BalancedDTO balanced, List<SourceFieldDTO> sourceFields)
	{
		super();
		setAlgorithm(algorithm);
		this.field = field;
		this.length = length;
		this.ngrams = ngrams;
		this.bitsPerNgram = bitsPerNgram;
		this.fold = fold;
		setAlphabet(alphabet);
		setBalanced(balanced);
		setSourceFields(sourceFields);
	}

	public BloomFilterConfigDTO(BloomFilterConfigDTO dto)
	{
		this(dto.getAlgorithm(), dto.getField(), dto.getLength(), dto.getNgrams(), dto.getBitsPerNgram(), dto.getFold(), dto.getAlphabet(), dto.getBalanced(), dto.getSourceFields());
	}

	public String getAlgorithm()
	{
		return algorithm;
	}

	public void setAlgorithm(String algorithm)
	{
		this.algorithm = algorithm != null ? algorithm : DEFAULT_ALGORITHM;
	}

	public FieldName getField()
	{
		return field;
	}

	public void setField(FieldName field)
	{
		this.field = field;
	}

	public int getLength()
	{
		return length;
	}

	public void setLength(int length)
	{
		this.length = length;
	}

	public int getNgrams()
	{
		return ngrams;
	}

	public void setNgrams(int ngrams)
	{
		this.ngrams = ngrams;
	}

	public int getBitsPerNgram()
	{
		return bitsPerNgram;
	}

	public void setBitsPerNgram(int bitsPerNgram)
	{
		this.bitsPerNgram = bitsPerNgram;
	}

	public int getFold()
	{
		return fold;
	}

	public void setFold(int fold)
	{
		this.fold = fold;
	}

	public String getAlphabet()
	{
		return alphabet;
	}

	public void setAlphabet(String alphabet)
	{
		this.alphabet = alphabet != null ? alphabet : DEFAULT_ALPHABET;
	}

	public BalancedDTO getBalanced()
	{
		return balanced;
	}

	public void setBalanced(BalancedDTO balanced)
	{
		this.balanced = balanced != null ? new BalancedDTO(balanced) : null;
	}

	public List<SourceFieldDTO> getSourceFields()
	{
		return sourceFields;
	}

	public void setSourceFields(List<SourceFieldDTO> sourceFields)
	{
		this.sourceFields.clear();
		if (sourceFields != null)
		{
			for (SourceFieldDTO sfDTO : sourceFields)
			{
				this.sourceFields.add(new SourceFieldDTO(sfDTO));
			}
		}
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (algorithm == null ? 0 : algorithm.hashCode());
		result = prime * result + (field == null ? 0 : field.hashCode());
		result = prime * result + length;
		result = prime * result + ngrams;
		result = prime * result + bitsPerNgram;
		result = prime * result + fold;
		result = prime * result + (alphabet == null ? 0 : alphabet.hashCode());
		result = prime * result + (balanced == null ? 0 : balanced.hashCode());
		result = prime * result + sourceFields.hashCode();
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
		BloomFilterConfigDTO other = (BloomFilterConfigDTO) obj;
		if (algorithm == null)
		{
			if (other.algorithm != null)
			{
				return false;
			}
		}
		else if (!algorithm.equals(other.algorithm))
		{
			return false;
		}
		if (field == null)
		{
			if (other.field != null)
			{
				return false;
			}
		}
		else if (!field.equals(other.field))
		{
			return false;
		}
		if (length != other.length)
		{
			return false;
		}
		if (ngrams != other.ngrams)
		{
			return false;
		}
		if (bitsPerNgram != other.bitsPerNgram)
		{
			return false;
		}
		if (fold != other.fold)
		{
			return false;
		}
		if (alphabet == null)
		{
			if (other.alphabet != null)
			{
				return false;
			}
		}
		else if (!alphabet.equals(other.alphabet))
		{
			return false;
		}
		if (balanced == null)
		{
			if (other.balanced != null)
			{
				return false;
			}
		}
		else if (!balanced.equals(other.balanced))
		{
			return false;
		}
		return sourceFields.equals(other.sourceFields);
	}

	@Override
	public String toString()
	{
		return "BloomFilterConfigDTO [algorithm=" + algorithm + ", field=" + field + ", length=" + length + ", ngrams=" + ngrams
				+ ", bitsPerNgram=" + bitsPerNgram + ", fold=" + fold + ", alphabet=" + alphabet + ", balanced=" + balanced + ", sourceFields=" + sourceFields + "]";
	}
}
