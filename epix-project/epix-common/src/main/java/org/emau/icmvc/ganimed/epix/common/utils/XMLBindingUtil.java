package org.emau.icmvc.ganimed.epix.common.utils;

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


import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

public class XMLBindingUtil {

	public <T> T load(Class<T> docClass, String fileName) throws Exception {
		final InputStream in = getClass().getResourceAsStream("/" + fileName);

		if (in == null) {
			throw new FileNotFoundException("Cannot find resource: " + fileName);
		}

		try {
			return unmarshal(docClass, in);
		} finally {
			in.close();
		}
	}

	public <T> T unmarshal(Class<T> docClass, InputStream inputStream) throws JAXBException {
		String packageName = docClass.getPackage().getName();
		JAXBContext jc = JAXBContext.newInstance(packageName);
		Unmarshaller u = jc.createUnmarshaller();
		JAXBElement<T> doc = u.unmarshal(new StreamSource(inputStream), docClass);
		return doc.getValue();
	}

	public <T> T unmarshal(String xsdSchema, InputStream xmlInput, Class<T> clss) throws JAXBException, SAXException {
		Schema schema = null;
		try {
			if (xsdSchema != null && xsdSchema.trim().length() > 0) {
				ClassLoader loader = Thread.currentThread().getContextClassLoader();
				InputStream xsd_stream = loader.getResourceAsStream(xsdSchema);
				SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
				StreamSource source = new StreamSource(xsd_stream);
				schema = schemaFactory.newSchema(source);
			} else {
				throw new IllegalArgumentException("Xml Schema Definition is not available");
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("JAXBContext or Schema Definition invalid", e);
		}
		JAXBContext jaxbContext = JAXBContext.newInstance(clss.getPackage().getName());
		return unmarshal(jaxbContext, schema, xmlInput, clss);
	}

	@SuppressWarnings("unchecked")
	public <T> T unmarshal(String xsdSchema, InputStream xmlInput, String clss) throws JAXBException {
		Schema schema = null;
		Class<T> clazz = null;
		try {
			clazz = (Class<T>) Class.forName(clss);
			if (xsdSchema != null && xsdSchema.trim().length() > 0) {
				ClassLoader loader = Thread.currentThread().getContextClassLoader();
				InputStream xsd_stream = loader.getResourceAsStream(xsdSchema);
				SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
				StreamSource source = new StreamSource(xsd_stream);
				schema = schemaFactory.newSchema(source);
				JAXBContext jaxbContext = JAXBContext.newInstance(clazz.getPackage().getName());
				return unmarshal(jaxbContext, schema, xmlInput, clazz);
			} else {
				throw new IllegalArgumentException("Xml Schema Definition is not available");
			}
		} catch (SAXException e) {
			throw new IllegalArgumentException("JAXBContext or Schema Definition invalid", e);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public <T> T unmarshal(JAXBContext jaxbContext, Schema schema, InputStream xmlInput, Class<T> clazz) throws JAXBException {
		if (schema == null) {
			throw new IllegalArgumentException("Xml Schema Definition is not available");

		}
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		unmarshaller.setSchema(schema);
		Object obj = unmarshaller.unmarshal(xmlInput);

		if (obj != null && obj instanceof JAXBElement<?>) {
			@SuppressWarnings("unchecked")
			JAXBElement<T> element = (JAXBElement<T>) obj;
			return clazz.cast(element.getValue());
		}
		return clazz.cast(obj);
	}

	public void marshal(String xsdSchema, String xmlDatei, Object jaxbElement) throws JAXBException, SAXException {
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = xsdSchema == null || xsdSchema.trim().length() == 0 ? null : schemaFactory.newSchema(new File(xsdSchema));
		JAXBContext jaxbContext = JAXBContext.newInstance(jaxbElement.getClass().getPackage().getName());
		marshal(jaxbContext, schema, xmlDatei, jaxbElement);
	}

	public void marshal(JAXBContext jaxbContext, Schema schema, String xmlDatei, Object jaxbElement) throws JAXBException {
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setSchema(schema);
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "ISO-8859-1");
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.marshal(jaxbElement, new File(xmlDatei));
	}
}
