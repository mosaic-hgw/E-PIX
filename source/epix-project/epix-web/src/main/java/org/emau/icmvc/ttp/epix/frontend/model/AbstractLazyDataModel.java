package org.emau.icmvc.ttp.epix.frontend.model;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.emau.icmvc.ttp.epix.common.model.enums.FieldName;
import org.emau.icmvc.ttp.epix.common.model.enums.Gender;
import org.emau.icmvc.ttp.epix.common.model.enums.IdentityField;
import org.emau.icmvc.ttp.epix.common.utils.PaginationConfig;
import org.emau.icmvc.ttp.epix.frontend.controller.component.DomainSelector;
import org.emau.icmvc.ttp.epix.service.EPIXManagementService;
import org.emau.icmvc.ttp.epix.service.EPIXService;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractLazyDataModel<T> extends LazyDataModel<T>
{
	protected final transient Logger logger = LoggerFactory.getLogger(getClass());
	protected final transient EPIXService service;
	protected final transient EPIXManagementService management;
	protected final transient DomainSelector domainSelector;
	protected final transient List<T> resultList;
	protected final transient Map<Long, T> resultMap;

	protected final String birthDateFormat;
	protected final String creationTimeFormat;
	private transient long lastAccess;
	private transient String lastDomain;
	private transient PaginationConfig lastConfig;

	public AbstractLazyDataModel(EPIXService service, DomainSelector domainSelector, String birthDateFormat, String creationTimeFormat)
	{
		this(service, null, domainSelector, birthDateFormat, creationTimeFormat);
	}

	public AbstractLazyDataModel(EPIXManagementService management, DomainSelector domainSelector, String birthDateFormat, String creationTimeFormat)
	{
		this(null, management, domainSelector, birthDateFormat, creationTimeFormat);
	}

	public AbstractLazyDataModel(EPIXService service, EPIXManagementService management, DomainSelector domainSelector, String birthDateFormat, String creationTimeFormat)
	{
		this.service = service;
		this.management = management;
		this.domainSelector = domainSelector;
		this.resultList = new ArrayList<>();
		this.resultMap = new LinkedHashMap<>();
		this.birthDateFormat = birthDateFormat;
		this.creationTimeFormat = creationTimeFormat;
	}

	/**
	 * Subclasses have to implement how to convert an entity to an identifying long value.
	 * @param entity the entity to return an ID for
	 * @return the ID for the given entity
	 */
	protected abstract Long toId(T entity);

	@Override
	public T getRowData(String rowKey)
	{
		return resultMap.get(Long.parseLong(rowKey));
	}

	@Override
	public String getRowKey(T entity)
	{
		return String.valueOf(toId(entity));
	}

	/**
	 * Registers the current result list.
	 * @param entities the current result list
	 */
	protected void updateResult(List<T> entities)
	{
		synchronized (resultList)
		{
			resultList.clear();
			resultList.addAll(entities);
			resultMap.clear();
			entities.forEach(e -> resultMap.put(toId(e), e));
		}

		logger.debug("load: got {} (of overall {}) filtered entities for the current page (IDs={})", resultMap.size(), getRowCount(), resultMap.keySet());
	}

	/**
	 * Returns the last result (as an unmodifiable list) which has been registered with {@link #updateResult(List)}.
	 * @return the last result (as an unmodifiable list)
	 */
	protected List<T> getLastResult()
	{
		return Collections.unmodifiableList(resultList);
	}

	/**
	 * Returns the currently selected domain name
	 * @return the currently selected domain name
	 */
	public String getDomainName()
	{
		return domainSelector.getSelectedDomainName();
	}

	/**
	 * Returns an SQL-compatible query string for searching substrings with LIKE surrounded by '%'
	 * unless the value already contains '%' (or null, if the value is empty).
	 * @param filterValue the filter value to convert
	 * @return the filter value as string surrounded by '%' unless the value already contains '%'
	 */
	protected String toSQLQueryPattern(Object filterValue)
	{
		if (filterValue == null)
		{
			return null;
		}

		String pattern = filterValue.toString();

		if (StringUtils.isEmpty(pattern))
		{
			return null;
		}

		// translate globbing wildcard to SQL wildcard
		pattern = pattern.replace('*', '%');

		if (!pattern.contains("%"))
		{
			// force substring matching when not using explicit wildcards
			pattern = "%" + pattern + "%";
		}

		return pattern;
	}

	/**
	 * Invalidates the last query and so resets caching.
	 */
	public synchronized void triggerDataChanged()
	{
		this.lastConfig = null;
	}

	/**
	 * Returns true if the given configuration equals the previously registered configuration
	 * when ignoring paging and sorting.
	 * @param config the configuration to check
	 * @return true if the given configuration equals the previously registered configuration
	 * 				ignoring paging and sorting
	 */
	protected boolean isSameQueryWhenPagingAndSortingIsIgnored(PaginationConfig config)
	{
		PaginationConfig lastConfig = this.lastConfig;
		if (lastConfig == null)
		{
			return config == null;
		}
		return lastConfig.equalsWhenPagingAndSortingIsIgnored(config);
	}

	/**
	 * Returns true if the given configuration equals the previously registered configuration.
	 * @param config the configuration to check
	 * @return true if the given configuration equals the previously registered configuration
	 */
	protected boolean isSameQuery(PaginationConfig config)
	{
		return Objects.equals(lastConfig, config) && Objects.equals(lastDomain, getDomainName());
	}

	/**
	 * Registers the given configuration as current query and returns true
	 * if it equals the previously registered configuration.
	 * @param config the configuration to register and check
	 * @return true if the given configuration equals the previously registered configuration
	 */
	protected synchronized boolean registerQuery(PaginationConfig config)
	{
		long time = System.currentTimeMillis();

		try
		{
			if (time - lastAccess < 3000 && isSameQuery(config))
			{
				logger.debug("return cached result");
				return true;
			}

			logger.debug("query fresh result");

		}
		finally
		{
			lastAccess = time;
			lastDomain = getDomainName();
			lastConfig = config;
		}
		return false;
	}

	/**
	 * Converts a property string to an {@link IdentityField}
	 * @param property the property string to convert
	 * @return the {@link IdentityField} for the property string
	 */
	protected IdentityField toIdentityField(String property)
	{
		IdentityField identityField = null;
		if (StringUtils.isNotEmpty(property))
		{
			try
			{
				// e.g. newIdentity.firstName
				String[] sortFieldParts = property.split("\\.");
				if (sortFieldParts.length > 0)
				{
					// e.g. newIdentity.firstName -> FIRST_NAME
					identityField = FieldName.valueOf(sortFieldParts[sortFieldParts.length - 1]).toIdentityField();
				}
			}
			catch (IllegalArgumentException | NullPointerException e)
			{
				identityField = IdentityField.NONE;
			}
		}
		return identityField;
	}

	/**
	 * Returns the resource bundle from the {@link FacesContext}.
	 * @return the resource bundle from the {@link FacesContext}
	 */
	protected ResourceBundle getBundle()
	{
		FacesContext facesContext = FacesContext.getCurrentInstance();
		return facesContext.getApplication().getResourceBundle(facesContext, "msg");
	}

	/**
	 * Returns the available gender strings from the resource bundle
	 * (prefixed with "common.person.gender.") as {@link Gender} fields.
	 * @return the available gender strings from the resource bundle
	 */
	protected Map<Gender, String> getGenderStrings()
	{
		ResourceBundle bundle = getBundle();
		return Arrays.stream(Gender.values()).collect(Collectors.toMap(
				g -> g, g -> bundle.getString("common.person.gender." + g.name())));
	}

	/**
	 * Returns the count of items in the database wrt. the filter configuration.
	 * It is legal to implement this method as a dummy e.g. always returning 0 (like this implementation does),
	 * as long as {@link #setRowCount(int)} is used correctly in {@link #load(int, int, Map, Map)}.
	 * In other words, when this method is implemented correctly, there is no need to call
	 * {@link #setRowCount(int)} in {@link #load(int, int, Map, Map)} anymore.
	 *
	 * @see <a href="https://primefaces.github.io/primefaces/11_0_0/#/../migrationguide/11_0_0?id=datatable-dataview-datagrid-datalist">DataTable section in PF Migration guide 10 -> 11</a>
	 * @see <a href="https://primefaces.github.io/primefaces/11_0_0/#/components/datatable?id=lazy-loading">Lazy Loading in DataTable part of PF Documentation</a>
	 *
	 * @param filterBy the filter map
	 * @return the number of items in the database wrt. the filter configuration or any arbitrary value, when {@link #setRowCount(int)} is used correctly
	 */
	//@Override // TODO(PF11): uncomment after updating to PF11
	public int count(Map<String, FilterMeta> filterBy) {
		return 0;
	}
}
