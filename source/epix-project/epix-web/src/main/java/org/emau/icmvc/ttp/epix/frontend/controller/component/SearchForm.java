package org.emau.icmvc.ttp.epix.frontend.controller.component;

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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.emau.icmvc.ttp.epix.common.exception.InvalidParameterException;
import org.emau.icmvc.ttp.epix.common.exception.MPIException;
import org.emau.icmvc.ttp.epix.common.exception.UnknownObjectException;
import org.emau.icmvc.ttp.epix.common.exception.UnknownObjectType;
import org.emau.icmvc.ttp.epix.common.model.IdentifierDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityInDTO;
import org.emau.icmvc.ttp.epix.common.model.PersonDTO;
import org.emau.icmvc.ttp.epix.frontend.controller.common.AbstractEpixWebBean;
import org.emau.icmvc.ttp.epix.frontend.model.WebPerson;
import org.emau.icmvc.ttp.epix.pdqquery.model.SearchMask;
import org.icmvc.ttp.web.controller.AbstractBean.Severity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Arne Blumentritt
 */
public class SearchForm
{
	protected static final int DEFAULT_RESULT_SIZE = 100;

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	protected IdentityInDTO identity;
	protected String mpi;

	protected Boolean andMode = true;

	protected final List<WebPerson> persons = new ArrayList<>();
	protected final List<PersonDTO> personDTOs = new ArrayList<>();

	protected final AbstractEpixWebBean controller;

	/**
	 * Method called when loading the page.
	 */
	public SearchForm(AbstractEpixWebBean controller)
	{
		this.controller = controller;
		identity = new IdentityInDTO();
		addEmptyIdentifier();
	}

	private void addEmptyIdentifier()
	{
		identity.getIdentifiers().add(new IdentifierDTO());
	}

	private boolean removeEmptyIdentifier()
	{
		if (identity.getIdentifiers().size() > 0 && (identity.getIdentifiers().get(0).getIdentifierDomain() == null || StringUtils.isEmpty(identity.getIdentifiers().get(0).getValue())))
		{
			identity.getIdentifiers().clear();
			return true;
		}
		return false;
	}

	private void clearResult()
	{
		persons.clear();
		personDTOs.clear();
	}

	/**
	 * Searches DEFAULT_RESULT_SIZE persons according to the searchmask by PDQ.
	 */
	public void onSearchPersons()
	{
		clearResult();
		if (StringUtils.isEmpty(mpi))
		{
			searchPersonsPDQ(DEFAULT_RESULT_SIZE, true);
		}
		else
		{
			onSearchPersonByMPI();
		}
	}

	/**
	 * Searches all persons accoring to the searchmask by PDQ.
	 */
	public void onSearchAllPersons(boolean scrollToTop)
	{
		clearResult();
		if (StringUtils.isEmpty(mpi))
		{
			searchPersonsPDQ(Integer.MAX_VALUE, scrollToTop);
		}
		else
		{
			onSearchPersonByMPI();
		}
	}

	public void onSearchPersonByMPI()
	{
		clearResult();

		try
		{
			PersonDTO person = controller.getService().getPersonByFirstMPI(controller.getDomainSelector().getSelectedDomainName(), mpi);
			personDTOs.add(person);
			if (person.getReferenceIdentity() != null)
			{
				persons.add(new WebPerson(person.getReferenceIdentity(), person.getReferenceIdentity().getContacts(), person.getMpiId().getValue()));
			}
			else {
				WebPerson webPerson = new WebPerson(person.getMpiId().getValue());
				webPerson.setLastEdited(person.getPersonLastEdited());
				webPerson.setCreated(person.getPersonCreated());
				persons.add(webPerson);
			}
			controller.logMessage(controller.getBundle().getString("search.message.mpi.success"), Severity.INFO);
		}
		catch (InvalidParameterException e)
		{
			controller.logMessage(controller.getBundle().getString("search.message.invalidParameterException"), Severity.WARN);
		}
		catch (UnknownObjectException e)
		{
			controller.logMessage(e.getLocalizedMessage(), Severity.ERROR);
		}
	}

	private void searchPersonsPDQ(int maxResults, boolean scrollToTop)
	{
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

			boolean removedIdentifier = removeEmptyIdentifier();
			SearchMask searchMask = new SearchMask(controller.getDomainSelector().getSelectedDomainName(),
					identity, andMode, yearOfBirth, monthOfBirth, dayOfBirth, maxResults);
			List<PersonDTO> result = controller.getService().searchPersonsByPDQ(searchMask);
			personDTOs.addAll(result);
			persons.addAll(result.stream()
					.map(p -> new WebPerson(p.getReferenceIdentity(), p.getReferenceIdentity().getContacts(), p.getMpiId().getValue())).toList());

			Object[] args = { result.size() };

			if (result.size() == 1)
			{
				controller.logMessage(new MessageFormat(controller.getBundle().getString("search.message.idat.success")).format(args), Severity.INFO, scrollToTop);
			}
			else if (result.size() == DEFAULT_RESULT_SIZE)
			{
				controller.logMessage(new MessageFormat(controller.getBundle().getString("search.message.idat.successMore")).format(args), Severity.WARN);
			}
			else
			{
				controller.logMessage(new MessageFormat(controller.getBundle().getString("search.message.idat.successPlural")).format(args), Severity.INFO, scrollToTop);
			}
			if (removedIdentifier)
			{
				addEmptyIdentifier();
			}
		}
		catch (InvalidParameterException e)
		{
			controller.logMessage(controller.getBundle().getString("search.message.invalidParameterException"), Severity.WARN);
		}
		catch (MPIException e)
		{
			controller.logMPIException(e);
		}
		catch (UnknownObjectException e)
		{
			if (e.getObjectType().equals(UnknownObjectType.DOMAIN))
			{
				controller.logMessage(controller.getBundle().getString("search.message.unknownDomainException"), Severity.WARN);
			}
			else if (e.getObjectType().equals(UnknownObjectType.IDENTITFIER_DOMAIN))
			{
				controller.logMessage(controller.getBundle().getString("search.message.UnknownIdentifierDomainException"), Severity.WARN);
			}
		}
	}

	public List<WebPerson> getPersons()
	{
		return persons;
	}

	public List<PersonDTO> getPersonDTOs()
	{
		return personDTOs;
	}

	public IdentityInDTO getIdentity()
	{
		return identity;
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

	public AbstractEpixWebBean getController()
	{
		return controller;
	}
}
