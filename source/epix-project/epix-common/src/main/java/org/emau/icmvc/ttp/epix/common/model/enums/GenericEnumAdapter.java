package org.emau.icmvc.ttp.epix.common.model.enums;

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


import java.lang.reflect.ParameterizedType;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * see https://stackoverflow.com/questions/12147306/handling-invalid-enum-values-while-doing-jaxb-unmarshalling<br>
 * and because of annotations can't be generic an extra class is needed for every enum
 * 
 * @author geidell
 *
 * @param <T>
 */
public abstract class GenericEnumAdapter<T extends Enum<T>> extends XmlAdapter<String, T>
{
	private final static Logger logger = LogManager.getLogger(GenericEnumAdapter.class);
	private Class<T> clazz;

	@SuppressWarnings("unchecked")
	public GenericEnumAdapter()
	{
		super();
		// http://blog.xebia.com/acessing-generic-types-at-runtime-in-java/
		this.clazz = ((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
	}

	@Override
	public T unmarshal(String s) throws Exception
	{
		if (s != null)
		{
			for (T c : clazz.getEnumConstants())
			{
				if (c.name().equalsIgnoreCase(s))
				{
					return c;
				}
			}
		}
		String message = "invalid value for enum " + clazz.getName() + " : " + s;
		logger.error(message);
		throw new JAXBException(message);
	}

	@Override
	public String marshal(T t) throws Exception
	{
		return t.name();
	}
}
