package org.emau.icmvc.ttp.epix.frontend.controller;

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


import static org.emau.icmvc.ttp.epix.frontend.util.SessionMapKeys.EDIT_PERSON;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.emau.icmvc.ttp.epix.common.exception.InvalidParameterException;
import org.emau.icmvc.ttp.epix.common.exception.MPIException;
import org.emau.icmvc.ttp.epix.common.exception.UnknownObjectException;
import org.emau.icmvc.ttp.epix.common.exception.UnknownObjectType;
import org.emau.icmvc.ttp.epix.common.model.IdentifierDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityInDTO;
import org.emau.icmvc.ttp.epix.common.model.PersonDTO;
import org.emau.icmvc.ttp.epix.common.model.enums.MatchStatus;
import org.emau.icmvc.ttp.epix.frontend.controller.common.AbstractEpixWebBean;
import org.emau.icmvc.ttp.epix.frontend.model.WebPerson;
import org.emau.icmvc.ttp.epix.pdqquery.model.SearchMask;

/**
 *
 * @author Arne Blumentritt
 *
 */
@ViewScoped
@ManagedBean(name = "searchController")
public class SearchController extends AbstractEpixWebBean
{
	private IdentityInDTO identity;
	private IdentifierDTO identifier;
	private String mpi;

	private Boolean andMode = true;

	private List<WebPerson> persons = new ArrayList<>();
	private static final int DEFAULT_RESULT_SIZE = 100;

	/**
	 * Method called when loading the page.
	 */
	@PostConstruct
	public void init()
	{
		identity = new IdentityInDTO();
		identifier = new IdentifierDTO();
	}

	public void checkUpdate()
	{
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, Object> sessionMap = externalContext.getSessionMap();
		if (sessionMap.containsKey("updateIdentityResponse"))
		{
			switch ((MatchStatus) sessionMap.get("updateIdentityResponse"))
			{
				case NO_MATCH:
					logMessage(getBundle().getString("add.info.edited.noMatch"), Severity.INFO);
					break;
				case MATCH:
					logMessage(getBundle().getString("add.info.edited.match"), Severity.INFO);
					break;
				case PERFECT_MATCH:
					logMessage(getBundle().getString("add.info.edited.perfectMatch"), Severity.INFO);
					break;
				case POSSIBLE_MATCH:
					logMessage(getBundle().getString("add.info.edited.possibleMatch"), Severity.INFO);
					break;
				default:
					break;
			}

			sessionMap.remove("updateIdentityResponse");
		}
		else if (sessionMap.containsKey("cancelUpdateIdentity"))
		{
			logMessage(getBundle().getString("add.warn.editCanceled"), Severity.WARN);
			sessionMap.remove("cancelUpdateIdentity");
		}
	}

	/**
	 * Searches DEFAULT_RESULT_SIZE persons according to the searchmask by PDQ.
	 */
	public void onSearchPersonByIDAT()
	{
		searchPersonByIDAT(DEFAULT_RESULT_SIZE);
	}

	/**
	 * Searches all persons accoring to the searchmask by PDQ.
	 */
	public void onSearchAllPersonByIDAT()
	{
		searchPersonByIDAT(Integer.MAX_VALUE);
	}

	public void onSearchPersonByMPI()
	{
		identity = new IdentityInDTO();
		identifier = new IdentifierDTO();
		persons.clear();
		try
		{
			PersonDTO person = service.getPersonByMPI(domainSelector.getSelectedDomainName(), mpi);
			persons.add(new WebPerson(person.getReferenceIdentity(), person.getReferenceIdentity().getContacts(), person.getMpiId().getValue()));
			logMessage(getBundle().getString("search.message.mpi.success"), Severity.INFO);
		}
		catch (InvalidParameterException e)
		{
			logMessage(getBundle().getString("search.message.invalidParameterException"), Severity.WARN);
		}
		catch (UnknownObjectException e)
		{
			logMessage(getBundle().getString("search.message.mpi.successPlural"), Severity.INFO);
		}
	}

	/**
	 * Puts the selected Person from the dataTable in the ExternalContext and redirects to the next
	 * View.
	 *
	 * @return String
	 */
	public String onEditPerson(String mpi)
	{
		// puts selected Person into the ExternalContext
		// so it can be read in the next view (update-view)
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, Object> sessionMap = externalContext.getSessionMap();
		sessionMap.put(EDIT_PERSON, mpi);

		// redirects to the update-View
		return "add.xhml?faces-redirect=true";
	}

	private void searchPersonByIDAT(int maxResults)
	{
		mpi = null;

		try
		{
			logger.info("Searching Person by PDQ");

			int dayOfBirth = 0;
			int monthOfBirth = 0;
			int yearOfBirth = 0;

			if (identity.getBirthDate() != null)
			{
				Calendar cal = Calendar.getInstance();
				cal.setTime(identity.getBirthDate());
				dayOfBirth = cal.get(Calendar.DAY_OF_MONTH);
				monthOfBirth = cal.get(Calendar.MONTH) + 1;
				yearOfBirth = cal.get(Calendar.YEAR);
			}

			if (identifier != null && identifier.getIdentifierDomain() != null && identifier.getValue() != null)
			{
				identity.getIdentifiers().add(identifier);
			}

			SearchMask searchMask = new SearchMask(domainSelector.getSelectedDomainName(), identity, andMode, yearOfBirth, monthOfBirth, dayOfBirth, maxResults);

			persons = service.searchPersonsByPDQ(searchMask).stream()
					.map(p -> new WebPerson(p.getReferenceIdentity(), p.getReferenceIdentity().getContacts(), p.getMpiId().getValue()))
					.collect(Collectors.toList());

			Object[] args = { persons.size() };

			if (persons.size() == 1)
			{
				logMessage(new MessageFormat(getBundle().getString("search.message.idat.success")).format(args), Severity.INFO);
			}
			else if (persons.size() == DEFAULT_RESULT_SIZE)
			{
				logMessage(new MessageFormat(getBundle().getString("search.message.idat.successMore")).format(args), Severity.WARN);
			}
			else
			{
				logMessage(new MessageFormat(getBundle().getString("search.message.idat.successPlural")).format(args), Severity.INFO);
			}
		}
		catch (InvalidParameterException e)
		{
			logMessage(getBundle().getString("search.message.invalidParameterException"), Severity.WARN);
		}
		catch (MPIException e)
		{
			logMPIException(e);
		}
		catch (UnknownObjectException e)
		{
			if (e.getObjectType().equals(UnknownObjectType.DOMAIN))
			{
				logMessage(getBundle().getString("search.message.unknownDomainException"), Severity.WARN);
			}
			else if (e.getObjectType().equals(UnknownObjectType.IDENTITFIER_DOMAIN))
			{
				logMessage(getBundle().getString("search.message.UnknownIdentifierDomainException"), Severity.WARN);
			}
		}
	}

	public List<WebPerson> getPersons()
	{
		return persons;
	}

	public IdentityInDTO getIdentity()
	{
		return identity;
	}

	public IdentifierDTO getIdentifier()
	{
		return identifier;
	}

	public String getMpi()
	{
		return mpi;
	}

	public void setMpi(String mpi)
	{
		this.mpi = mpi;
	}

	public Boolean getAndMode()
	{
		return andMode;
	}

	public void setAndMode(Boolean andMode)
	{
		this.andMode = andMode;
	}
}
