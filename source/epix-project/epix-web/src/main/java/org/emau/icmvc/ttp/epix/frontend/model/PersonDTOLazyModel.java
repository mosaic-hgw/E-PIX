package org.emau.icmvc.ttp.epix.frontend.model;

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

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.emau.icmvc.ttp.epix.common.exception.InvalidParameterException;
import org.emau.icmvc.ttp.epix.common.exception.UnknownObjectException;
import org.emau.icmvc.ttp.epix.common.model.PersonDTO;
import org.emau.icmvc.ttp.epix.service.EPIXService;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// public class PersonDTOLazyModel extends LazyDataModel<PersonDTO>
// {
// private static final long serialVersionUID = 1104825405795685955L;
// public Logger logger = LoggerFactory.getLogger(getClass());
//
// private EPIXService service;
// private String domainName;
// private List<String> mpis;
// private Map<String, PersonDTO> data;
//
// public PersonDTOLazyModel(EPIXService service, String domainName)
// {
// this.service = service;
// this.domainName = domainName;
// data = new LinkedHashMap<>();
// }
//
// @Override
// public PersonDTO getRowData(String rowKey)
// {
// return data.get(rowKey);
// }
//
// @Override
// public Object getRowKey(PersonDTO object)
// {
// return object.getMpiId().getValue();
// }
//
// @Override
// public List<PersonDTO> load(int first, int pageSize, String sortField, SortOrder sortOrder,
// Map<String, Object> filters)
// {
// List<PersonDTO> result = new ArrayList<>();
// String filter = null;
// SimpleDateFormat sdf = null;
//
// // Prepare operations
// int start;
// int end;
//
// // Filter
// if (!filters.isEmpty() && filters.containsKey("globalFilter") && !StringUtils.isEmpty((String)
// filters.get("globalFilter")))
// {
// filter = ((String) filters.get("globalFilter")).toLowerCase();
// start = 0;
// end = mpis.size();
// FacesContext facesContext = FacesContext.getCurrentInstance();
// ResourceBundle bundle = facesContext.getApplication().getResourceBundle(facesContext, "msg");
// sdf = new SimpleDateFormat(bundle.getString("common.date.time.pattern"));
// }
// // Sorting
// else if (!StringUtils.isEmpty(sortField))
// {
// start = 0;
// end = mpis.size();
// }
// // Normal
// else
// {
// start = first;
// end = (first + pageSize) <= mpis.size() ? first + pageSize : mpis.size();
// }
//
// int found = 0;
// for (int i = start; i < end; i++)
// {
// try
// {
// String mpi = mpis.get(i);
// PersonDTO person;
//
// // person already in data cache
// if (data.containsKey(mpi))
// {
// person = data.get(mpi);
// }
// // person is new and must be gathered from db service
// else
// {
// person = service.getPersonByMPI(domainName, mpi);
// data.put(mpi, person);
// }
//
// // no filter
// if (filter == null)
// {
// result.add(person);
// }
// // filter
// else
// {
// String firstName = person.getReferenceIdentity().getFirstName().toLowerCase();
// String lastName = person.getReferenceIdentity().getLastName().toLowerCase();
// String gender = person.getReferenceIdentity().getGender().dateToString().toLowerCase();
// String birthDate = sdf.format(person.getReferenceIdentity().getBirthDate()).toLowerCase();
// String changeDate =
// sdf.format(person.getReferenceIdentity().getIdentityLastEdited()).toLowerCase();
//
// String[] filterItems = filter.split(" ");
//
// int hits = 0;
// for (String item : filterItems)
// {
// if (firstName.contains(item) || lastName.contains(item) || gender.contains(item) ||
// birthDate.contains(item) || changeDate.contains(item))
// {
// hits++;
// }
// }
//
// // if all filterItems are found
// if (hits == filterItems.length)
// {
// // TODO setRowCount aufrufen wenn entsprechende dbquery vorhanden
// if (found >= first)
// {
// result.add(person);
// }
// found++;
//
// if (found >= end)
// {
// break;
// }
// }
// }
//
// // sorting
// if (!StringUtils.isEmpty(sortField))
// {
// boolean asc = sortOrder.equals(SortOrder.ASCENDING);
//
// if (sortField.equals("referenceIdentity.firstName"))
// {
// Collections.sort(result, new Comparator<PersonDTO>() {
// @Override
// public int compare(PersonDTO p1, PersonDTO p2)
// {
// return asc ?
// p1.getReferenceIdentity().getFirstName().compareTo(p2.getReferenceIdentity().getFirstName())
// : p2.getReferenceIdentity().getFirstName().compareTo(p1.getReferenceIdentity().getFirstName());
// }
// });
// }
// else if (sortField.equals("referenceIdentity.lastName"))
// {
// Collections.sort(result, new Comparator<PersonDTO>() {
// @Override
// public int compare(PersonDTO p1, PersonDTO p2)
// {
// return asc ?
// p1.getReferenceIdentity().getLastName().compareTo(p2.getReferenceIdentity().getLastName())
// : p2.getReferenceIdentity().getLastName().compareTo(p1.getReferenceIdentity().getLastName());
// }
// });
// }
// else if (sortField.equals("referenceIdentity.gender"))
// {
// Collections.sort(result, new Comparator<PersonDTO>() {
// @Override
// public int compare(PersonDTO p1, PersonDTO p2)
// {
// return asc ?
// p1.getReferenceIdentity().getGender().compareTo(p2.getReferenceIdentity().getGender())
// : p2.getReferenceIdentity().getGender().compareTo(p1.getReferenceIdentity().getGender());
// }
// });
// }
// else if (sortField.equals("referenceIdentity.birthDate"))
// {
// Collections.sort(result, new Comparator<PersonDTO>() {
// @Override
// public int compare(PersonDTO p1, PersonDTO p2)
// {
// return asc ?
// p1.getReferenceIdentity().getBirthDate().compareTo(p2.getReferenceIdentity().getBirthDate())
// : p2.getReferenceIdentity().getBirthDate().compareTo(p1.getReferenceIdentity().getBirthDate());
// }
// });
// }
// else if (sortField.equals("referenceIdentity.identityLastEdited"))
// {
// Collections.sort(result, new Comparator<PersonDTO>() {
// @Override
// public int compare(PersonDTO p1, PersonDTO p2)
// {
// return asc ?
// p1.getReferenceIdentity().getIdentityLastEdited().compareTo(p2.getReferenceIdentity().getIdentityLastEdited())
// :
// p2.getReferenceIdentity().getIdentityLastEdited().compareTo(p1.getReferenceIdentity().getIdentityLastEdited());
// }
// });
// }
// }
// }
// catch (InvalidParameterException | UnknownObjectException e)
// {
// e.printStackTrace();
// }
// }
//
// if (!StringUtils.isEmpty(sortField))
// {
// // setRowCount(result.size());
// // result = result.subList(first, (first + pageSize) <= mpis.size() ? first + pageSize :
// // mpis.size());
// }
//
// logger.trace("load: first=" + String.valueOf(first) + "; pageSize=" + String.valueOf(pageSize) +
// "; sortField=" + sortField
// + "; sortOrder=" + sortOrder + "; filters=" + filters.dateToString());
// logger.debug("Loaded persons for possible matches from " + start + " to " + end);
// logger.trace("RowCount is " + getRowCount());
// logger.trace("Current size of data is " + data.size());
// logger.trace("Current size of result is " + result.size());
//
// return result;
//
//
// // for (PersonDTO person : result)
// // {
// // logger.debug("# " + person.getReferenceIdentity().getFirstName());
// // }
//
// // if (filters.isEmpty())
// // {
// // return result;
// // }
// // else
// // {
// // if (result.isEmpty())
// // {
// // return result;
// // }
// //
// // try
// // {
// // return result.subList(first, first + pageSize - 1);
// // }
// // catch (IndexOutOfBoundsException e)
// // {
// // return result.subList(first, result.size() - 1);
// // }
// // }
// // if (filter == null)
// // {
// // setRowCount(mpis.size());
// // RequestContext.getCurrentInstance().update("rowCount");
// // logger.debug(filter);
// // return result;
// // }
// // else
// // {
// // setRowCount(result.size());
// // RequestContext.getCurrentInstance().update("rowCount");
// // int toIndex = (first + pageSize < result.size()) ? first + pageSize : result.size();
// // return result.subList(first, toIndex);
// // }
// }
//
// public Map<String, PersonDTO> getData()
// {
// return data;
// }
//
// public void setMpis(List<String> mpis)
// {
// this.mpis = mpis;
// setRowCount(mpis.size());
// }
//
// public void addMpi(String mpi)
// {
// mpis.add(mpi);
// setRowCount(mpis.size());
// }
// }

public class PersonDTOLazyModel extends LazyDataModel<PersonDTO>
{
	@Serial
	private static final long serialVersionUID = 1104825405795685955L;

	protected transient final Logger logger = LoggerFactory.getLogger(getClass());

	private final transient EPIXService service;
	private final String domainName;
	private List<String> mpis;
	private final List<PersonDTO> data;

	public PersonDTOLazyModel(EPIXService service, String domainName)
	{
		this.service = service;
		this.domainName = domainName;
		data = new ArrayList<>();
	}

	@Override
	public PersonDTO getRowData(String rowKey)
	{
		for (PersonDTO person : data)
		{
			if (person.getMpiId().getValue().equals(rowKey))
			{
				return person;
			}
		}
		return null;
	}

	@Override
	public String getRowKey(PersonDTO object)
	{
		return object.getMpiId().getValue();
	}

	@Override
	public List<PersonDTO> load(int first, int pageSize, Map<String, SortMeta> sortFields, Map<String, FilterMeta> filters)
	{
		List<PersonDTO> result = new ArrayList<>();

		for (int i = first; i < first + pageSize && i < mpis.size(); i++)
		{
			try
			{
				result.add(service.getPersonByFirstMPI(domainName, mpis.get(i)));
			}
			catch (InvalidParameterException | UnknownObjectException e)
			{
				logger.error(e.getLocalizedMessage());
			}
		}
		data.addAll(result);
		setRowCount(mpis.size());
		return result;
	}

	public void addMpi(String mpi)
	{
		mpis.add(mpi);
		setRowCount(mpis.size());
	}

	public void setMpis(List<String> mpis)
	{
		this.mpis = mpis;
		setRowCount(mpis.size());
	}

	public List<PersonDTO> getData()
	{
		return data;
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
	 * @param filterBy
	 *            the filter map
	 * @return the number of items in the database wrt. the filter configuration or any arbitrary value, when {@link #setRowCount(int)} is used correctly
	 */
	@Override
	public int count(Map<String, FilterMeta> filterBy)
	{
		return 0;
	}
}
