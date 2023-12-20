package org.emau.icmvc.ttp.deduplication.config.model;

/*
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
import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.emau.icmvc.ttp.epix.common.model.config.BloomFilterConfigDTO;
import org.emau.icmvc.ttp.epix.common.model.config.SourceFieldDTO;
import org.emau.icmvc.ttp.epix.common.model.enums.FieldName;

/**
 * @author Christopher Hampf
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BloomFilterConfig", propOrder = { "algorithm", "field", "length", "ngrams", "bitsPerNgram", "fold", "alphabet", "balanced", "sourceFields" })
public class BloomFilterConfig
{
	@XmlElement(name = "algorithm", required = true, defaultValue = "org.emau.icmvc.ttp.deduplication.impl.bloomfilter.DoubleHashingStrategy")
	private String algorithm = "";

	@Enumerated(EnumType.STRING)
	@XmlElement(name = "field", required = true, defaultValue = "")
	private FieldName field;

	@XmlElement(name = "length", required = true, defaultValue = "500")
	private int length = 500;

	@XmlElement(name = "ngrams", required = true, defaultValue = "2")
	private int ngrams = 2;

	@XmlElement(name = "bits-per-ngram", required = true, defaultValue = "15")
	private int bitsPerNgram = 15;

	@XmlElement(name = "fold", defaultValue = "0")
	private int fold = 0;

	@XmlElement(name = "alphabet", defaultValue = " -.ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789")
	private String alphabet = "";

	@XmlElement(name = "balanced")
	private Balanced balanced;

	@XmlElement(name = "source-field", required = true)
	private final List<SourceField> sourceFields = new ArrayList<>();

	public BloomFilterConfig()
	{
		super();
	}

	public BloomFilterConfig(BloomFilterConfigDTO dto)
	{
		super();

		algorithm = dto.getAlgorithm();
		field = dto.getField();
		length = dto.getLength();
		ngrams = dto.getNgrams();
		bitsPerNgram = dto.getBitsPerNgram();
		fold = dto.getFold();
		alphabet = dto.getAlphabet();

		if (dto.getBalanced() != null)
		{
			balanced = new Balanced();
			balanced.setSeed(dto.getBalanced().getSeed());
		}

		List<SourceFieldDTO> tmp = dto.getSourceFields();
		if (tmp != null)
		{
			for (SourceFieldDTO sfDTO : tmp)
			{
				SourceField sf = new SourceField();
				sf.setName(sfDTO.getName());
				sf.setSaltField(sfDTO.getSaltField());
				sf.setSaltValue(sfDTO.getSaltValue());
				sf.setSeed(sfDTO.getSeed());

				sourceFields.add(sf);
			}
		}
	}

	public String getAlgorithm()
	{
		return algorithm;
	}

	public void setAlgorithm(String algorithm)
	{
		this.algorithm = algorithm;
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

	public int getNGram()
	{
		return ngrams;
	}

	public void setNGram(int ngrams)
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
		this.alphabet = alphabet;
	}

	public Balanced getBalanced()
	{
		return balanced;
	}

	public void setBalanced(Balanced balanced)
	{
		this.balanced = balanced;
	}

	public List<SourceField> getSourceFields()
	{
		return sourceFields;
	}

	public void setSourceFields(List<SourceField> sourceFields)
	{
		this.sourceFields.addAll(sourceFields);
	}

	public BloomFilterConfigDTO toDTO()
	{
		List<SourceFieldDTO> tmpSourceFields = new ArrayList<>();
		for (SourceField sf : sourceFields)
		{
			tmpSourceFields.add(sf.toDTO());
		}

		return new BloomFilterConfigDTO(algorithm, field, length, ngrams, bitsPerNgram, fold, alphabet, balanced != null ? balanced.toDTO() : null, tmpSourceFields);
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
		BloomFilterConfig other = (BloomFilterConfig) obj;
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
		return "BloomFilterConfig [algorithm=" + algorithm + ", field=" + field + ", length=" + length + ", ngrams=" + ngrams
				+ ", bitsPerNgram=" + bitsPerNgram + ", fold=" + fold + ", alphabet=" + alphabet + ", balanced=" + balanced + ", sourceFields=" + sourceFields + "]";
	}
}
