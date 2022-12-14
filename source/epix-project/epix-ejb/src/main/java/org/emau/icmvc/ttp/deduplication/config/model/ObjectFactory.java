//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2012.03.26 at 05:49:30 PM CEST
//

package org.emau.icmvc.ttp.deduplication.config.model;

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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each Java content interface and Java element interface generated in the org.emau.icmvc.ttp.deduplication.config.model package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the Java representation for XML content. The Java representation of XML content can consist of schema derived interfaces
 * and classes representing the binding of schema type definitions, element declarations and model groups. Factory methods for each of these are provided in this class.
 *
 */
@XmlRegistry
public class ObjectFactory
{
	private final static QName _MatchingConfiguration_QNAME = new QName("http://www.ttp.icmvc.emau.org/deduplication/config/model",
			"MatchingConfiguration");

	/**
	 * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.emau.icmvc.ttp.deduplication.config.model
	 *
	 */
	public ObjectFactory()
	{}

	/**
	 * Create an instance of {@link PreprocessingConfig }
	 *
	 */
	public PreprocessingConfig createPreprocessingConfig()
	{
		return new PreprocessingConfig();
	}

	/**
	 * Create an instance of {@link Matching }
	 *
	 */
	public Matching createMatching()
	{
		return new Matching();
	}

	/**
	 * Create an instance of {@link PreprocessingField }
	 *
	 */
	public PreprocessingField createPreprocessingField()
	{
		return new PreprocessingField();
	}

	/**
	 * Create an instance of {@link SimpleTransformation }
	 *
	 */
	public SimpleTransformation createSimpleTransformation()
	{
		return new SimpleTransformation();
	}

	/**
	 * Create an instance of {@link ComplexTransformation }
	 *
	 */
	public ComplexTransformation createComplexTransformation()
	{
		return new ComplexTransformation();
	}

	/**
	 * Create an instance of {@link MatchingConfiguration }
	 *
	 */
	public MatchingConfiguration createMatchingConfiguration()
	{
		return new MatchingConfiguration();
	}

	/**
	 * Create an instance of {@link RequiredFields }
	 *
	 */
	public RequiredFields createRequiredFields()
	{
		return new RequiredFields();
	}

	/**
	 * Create an instance of {@link Field }
	 *
	 */
	public Field createField()
	{
		return new Field();
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link MatchingConfiguration }{@code >}}
	 *
	 */
	@XmlElementDecl(namespace = "http://www.ttp.icmvc.emau.org/deduplication/config/model", name = "MatchingConfiguration")
	public JAXBElement<MatchingConfiguration> createMatchingConfiguration(MatchingConfiguration value)
	{
		return new JAXBElement<>(_MatchingConfiguration_QNAME, MatchingConfiguration.class, null, value);
	}
}