package org.emau.icmvc.ttp.utils;

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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Christian Schack, geidell
 * @since 2011
 *
 */
public final class ReflectionUtil
{
	private static final Map<ReflectionUtil.ReflectionCacheKey, Method> getterCache = new HashMap<>();
	private static final Map<ReflectionUtil.ReflectionCacheKey, Method> setterCache = new HashMap<>();
	private static final Map<String, Class<?>> classCache = new HashMap<>();
	private static final ReflectionUtil instance = new ReflectionUtil();

	// suppress default constructor for noninstantiability
	private ReflectionUtil()
	{}

	private static String createGetterMethod(String fieldName)
	{
		String firstLetter = fieldName.substring(0, 1).toUpperCase();
		String rest = fieldName.substring(1, fieldName.length());
		return "get" + firstLetter + rest;
	}

	private static String createSetterMethod(String fieldName)
	{
		String firstLetter = fieldName.substring(0, 1).toUpperCase();
		String rest = fieldName.substring(1, fieldName.length());
		return "set" + firstLetter + rest;
	}

	public static Object getProperty(Object object, String fieldName) throws Exception
	{
		ReflectionCacheKey reflectionCacheKey = instance.new ReflectionCacheKey(object.getClass(), fieldName);
		synchronized (getterCache)
		{
			Method method = getterCache.get(reflectionCacheKey);
			if (method == null)
			{
				String getter = createGetterMethod(fieldName);
				method = object.getClass().getMethod(getter);
				getterCache.put(reflectionCacheKey, method);
			}
			return method.invoke(object);
		}
	}

	public static Object invoke(Object object, String methodName, Object... args) throws Exception
	{
		Method method = object.getClass().getMethod(methodName);
		return method.invoke(object, args);
	}

	public static Object newInstance(String type) throws Exception
	{
		Class<?> clazz = getClass(type);
		Object object = clazz.newInstance();
		return object;
	}

	private static Class<?> getClass(String type) throws ClassNotFoundException
	{
		synchronized (classCache)
		{
			Class<?> clazz = classCache.get(type);
			if (clazz == null)
			{
				clazz = Class.forName(type);
				classCache.put(type, clazz);
			}
			return clazz;
		}
	}

	public static Object newInstance(String type, Object... args) throws Exception
	{
		Class<?> clazz = getClass(type);
		Class<?>[] argClasses = new Class<?>[args.length];
		for (int i = 0; i < args.length; i++)
		{
			argClasses[i] = args[i].getClass();
		}
		Constructor<?> constructor = clazz.getConstructor(argClasses);
		Object object = constructor.newInstance(args);
		return object;
	}

	public static Object setProperty(Object object, String fieldName, Object value, Class<?> valueType) throws Exception
	{
		ReflectionCacheKey reflectionCacheKey = instance.new ReflectionCacheKey(object.getClass(), fieldName);
		synchronized (setterCache)
		{
			Method method = setterCache.get(reflectionCacheKey);
			if (method == null)
			{
				String setter = createSetterMethod(fieldName);
				method = object.getClass().getDeclaredMethod(setter, valueType);
				setterCache.put(reflectionCacheKey, method);
			}
			Object result = method.invoke(object, value);

			return result;
		}
	}

	private class ReflectionCacheKey
	{
		private final Class<? extends Object> clazz;
		private final String fieldName;

		public ReflectionCacheKey(Class<? extends Object> clazz, String fieldName)
		{
			super();
			this.clazz = clazz;
			this.fieldName = fieldName;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + (clazz == null ? 0 : clazz.hashCode());
			result = prime * result + (fieldName == null ? 0 : fieldName.hashCode());
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
			ReflectionCacheKey other = (ReflectionCacheKey) obj;
			if (!getOuterType().equals(other.getOuterType()))
			{
				return false;
			}
			if (clazz == null)
			{
				if (other.clazz != null)
				{
					return false;
				}
			}
			else if (!clazz.equals(other.clazz))
			{
				return false;
			}
			if (fieldName == null)
			{
				if (other.fieldName != null)
				{
					return false;
				}
			}
			else if (!fieldName.equals(other.fieldName))
			{
				return false;
			}
			return true;
		}

		private ReflectionUtil getOuterType()
		{
			return ReflectionUtil.this;
		}
	}
}
