package org.emau.icmvc.ttp.deduplication;

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

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.emau.icmvc.ttp.deduplication.config.model.ComplexTransformation;
import org.emau.icmvc.ttp.deduplication.config.model.PreprocessingConfig;
import org.emau.icmvc.ttp.deduplication.config.model.PreprocessingField;
import org.emau.icmvc.ttp.deduplication.config.model.SimpleFilter;
import org.emau.icmvc.ttp.deduplication.config.model.SimpleTransformation;
import org.emau.icmvc.ttp.epix.common.deduplication.IComplexTransformation;
import org.emau.icmvc.ttp.epix.common.exception.MPIErrorCode;
import org.emau.icmvc.ttp.epix.common.exception.MPIException;
import org.emau.icmvc.ttp.epix.persistence.model.IdentityPreprocessed;
import org.emau.icmvc.ttp.utils.ReflectionUtil;

/**
 *
 * @author Christian Schack, geidell
 * @since 23.09.2011
 *
 */
public class PreprocessingStrategy
{
	private final static Logger logger = LogManager.getLogger(PreprocessingStrategy.class);
	private final PreprocessingConfig preprocessingConfiguration;
	private final Map<String, IComplexTransformation> transformationCache = new HashMap<>();

	public PreprocessingStrategy(PreprocessingConfig preprocessingConfiguration)
	{
		if (preprocessingConfiguration != null)
		{
			logger.info("preprocessing configured");
		}
		else
		{
			logger.info("no preprocessing config available");
		}
		this.preprocessingConfiguration = preprocessingConfiguration;
	}

	public IdentityPreprocessed preprocess(IdentityPreprocessed preprocessable) throws MPIException
	{
		if (preprocessingConfiguration != null)
		{
			for (PreprocessingField preprocessingField : preprocessingConfiguration.getPreprocessingFields())
			{
				for (SimpleTransformation st : preprocessingField.getSimpleTransformationTypes())
				{
					String newValue = performSimpleTransformation(preprocessable, st, preprocessingField.getFieldName().name());
					try
					{
						ReflectionUtil.setProperty(preprocessable, preprocessingField.getFieldName().name(), newValue, String.class);
					}
					catch (Exception e)
					{
						String message = "exception while preprocessing (simple transformation)";
						logger.error(message, e);
						throw new MPIException(MPIErrorCode.INTERNAL_ERROR, message, e);
					}
				}
				for (ComplexTransformation ct : preprocessingField.getComplexTransformationTypes())
				{
					String newValue = performComplexTransformation(preprocessable, ct, preprocessingField.getFieldName().name());
					try
					{
						ReflectionUtil.setProperty(preprocessable, preprocessingField.getFieldName().name(), newValue, String.class);
					}
					catch (Exception e)
					{
						String message = "exception while preprocessing (complex transformation)";
						logger.error(message, e);
						throw new MPIException(MPIErrorCode.INTERNAL_ERROR, message, e);
					}
				}
				for (SimpleFilter sf : preprocessingField.getSimpleFilterTypes())
				{
					String newValue = performSimpleFilter(preprocessable, sf, preprocessingField.getFieldName().name());
					try
					{
						ReflectionUtil.setProperty(preprocessable, preprocessingField.getFieldName().name(), newValue, String.class);
					}
					catch (Exception e)
					{
						String message = "exception while preprocessing (simple filter)";
						logger.error(message, e);
						throw new MPIException(MPIErrorCode.INTERNAL_ERROR, message, e);
					}
				}
			}
		}
		else
		{
			logger.debug("no preprocessing-config available");
		}
		return preprocessable;
	}

	private <V> String performSimpleTransformation(V processable, SimpleTransformation transformationConfig, String propertyName)
			throws MPIException
	{
		try
		{
			String inputPattern = transformationConfig.getInputPattern();
			String outputPattern = transformationConfig.getOutputPattern();

			String propertyValue = (String) ReflectionUtil.getProperty(processable, propertyName);

			if (propertyValue != null && outputPattern != null && inputPattern != null)
			{
				propertyValue = propertyValue.replace(inputPattern, outputPattern);
			}

			return propertyValue;
		}
		catch (Exception e)
		{
			String message = "exception while preprocessing (simple transformation): " + e.getMessage();
			logger.error(message, e);
			throw new MPIException(MPIErrorCode.INTERNAL_ERROR, message, e);
		}
	}

	private <V> String performComplexTransformation(V processable, ComplexTransformation transformationConfig, String propertyName)
			throws MPIException
	{
		try
		{
			String propertyValue = (String) ReflectionUtil.getProperty(processable, propertyName);
			if (propertyValue != null)
			{
				IComplexTransformation transformation = getComplexTransformationInstance(transformationConfig.getQualifiedClassName());
				propertyValue = transformation.transform(propertyValue);
			}
			else
			{
				logger.warn(String.format("property %s is null", propertyName));
			}
			return propertyValue;
		}
		catch (Exception e)
		{
			String message = "exception while preprocessing (complex transformation): " + e.getMessage();
			logger.error(message, e);
			throw new MPIException(MPIErrorCode.INTERNAL_ERROR, message, e);
		}
	}

	private <V> String performSimpleFilter(V processable, SimpleFilter transformationConfig, String propertyName) throws MPIException
	{
		try
		{
			String passAlphabet = transformationConfig.getPassAlphabet();
			String replaceChar = transformationConfig.getReplaceCharacter();
			String replace = replaceChar.length() > 0 ? Character.toString(replaceChar.charAt(0)) : replaceChar;

			String propertyValue = (String) ReflectionUtil.getProperty(processable, propertyName);

			if (propertyValue != null && passAlphabet != null)
			{
				StringBuilder sb = new StringBuilder(propertyValue);

				int idx = 0;
				while (idx < sb.length())
				{
					if (passAlphabet.indexOf(sb.charAt(idx)) == -1)
					{
						if (replace.isEmpty())
						{
							sb.replace(idx, idx + 1, "");
							--idx;
						}
						else
						{
							sb.setCharAt(idx, replace.charAt(0));
						}
					}
					++idx;
				}

				propertyValue = sb.toString();
			}

			return propertyValue;
		}
		catch (Exception e)
		{
			String message = "exception while preprocessing (simple filter): " + e.getMessage();
			logger.error(message, e);
			throw new MPIException(MPIErrorCode.INTERNAL_ERROR, message, e);
		}
	}

	private IComplexTransformation getComplexTransformationInstance(String qualifiedClassName) throws Exception
	{
		IComplexTransformation transformationType = transformationCache.get(qualifiedClassName);
		if (transformationType == null)
		{
			transformationType = (IComplexTransformation) ReflectionUtil.newInstance(qualifiedClassName);
			transformationCache.put(qualifiedClassName, transformationType);
		}
		return transformationType;
	}
}
