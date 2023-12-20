package org.emau.icmvc.ttp.epix.frontend.controller;

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
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.emau.icmvc.ttp.epix.common.exception.InvalidParameterException;
import org.emau.icmvc.ttp.epix.common.exception.MPIException;
import org.emau.icmvc.ttp.epix.common.exception.UnknownObjectException;
import org.emau.icmvc.ttp.epix.common.model.ContactInDTO;
import org.emau.icmvc.ttp.epix.common.model.ContactOutDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentifierDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityInDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityOutDTO;
import org.emau.icmvc.ttp.epix.common.model.PersonDTO;
import org.emau.icmvc.ttp.epix.common.model.ResponseEntryDTO;
import org.emau.icmvc.ttp.epix.common.model.SourceDTO;
import org.emau.icmvc.ttp.epix.common.model.enums.VitalStatus;
import org.emau.icmvc.ttp.epix.frontend.controller.common.AbstractEpixWebBean;

import static org.emau.icmvc.ttp.epix.frontend.util.SessionMapKeys.EDIT_PERSON;

@ViewScoped
@ManagedBean(name = "addController")
public class AddController extends AbstractEpixWebBean
{
	@ManagedProperty(value = "#{resolveController}")
	private ResolveController resolveController;
	private IdentityInDTO identity;
	private ContactInDTO contact;
	private IdentifierDTO identifier;
	private SourceDTO selectedSource;
	private String comment;
	private List<IdentifierDTO> identifiers;
	private String mpi;
	PersonDTO person;

	@PostConstruct
	public void init() throws UnknownObjectException, InvalidParameterException
	{
		identity = new IdentityInDTO();
		contact = new ContactInDTO();
		identifier = new IdentifierDTO();
		selectedSource = null;
		mpi = null;

		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, Object> sessionMap = externalContext.getSessionMap();
		if (sessionMap.containsKey(EDIT_PERSON))
		{
			person = service.getPersonByFirstMPI(getDomainSelector().getSelectedDomainName(), (String) sessionMap.get(EDIT_PERSON));
			List<ContactInDTO> contacts = new ArrayList<>();
			for (ContactOutDTO c : person.getReferenceIdentity().getContacts())
			{
				contacts.add(new ContactInDTO(c));
			}
			IdentityOutDTO referenceIdentity = person.getReferenceIdentity();
			identity = new IdentityInDTO(referenceIdentity, contacts);
			mpi = person.getMpiId().getValue();

			sessionMap.remove(EDIT_PERSON);
			loadIdentifiers();
		}
		else
		{
			identity.setVitalStatus(VitalStatus.ALIVE);
		}
	}

	private void loadIdentifiers()
	{
		identifiers = new ArrayList<>();
		identifiers.addAll(identity.getIdentifiers());
		if (StringUtils.isNotEmpty(mpi))
		{
			try
			{
				for (IdentityOutDTO otherIdentitiy : service.getPersonByFirstMPI(getDomainSelector().getSelectedDomainName(), mpi).getOtherIdentities())
				{
					identifiers.addAll(otherIdentitiy.getIdentifiers());
				}
			}
			catch (InvalidParameterException | UnknownObjectException e)
			{
				logger.error(e.getLocalizedMessage());
			}
		}
	}

	public void onAddIdentity()
	{
		try
		{
			validateDateOfDeath();
			String domainName = getDomainSelector().getSelectedDomainName();
			ResponseEntryDTO response;

			if (isUseNotifications())
			{
				response = serviceWithNotification.requestMPI(NOTIFICATION_CLIENT_ID, domainName, identity, selectedSource.getName(), null);
			}
			else
			{
				response = service.requestMPI(domainName, identity, selectedSource.getName(), null);
			}

			Object[] args = {};
			Object[] args2 = {};
			if (response.getPerson() != null && response.getPerson().getMpiId() != null)
			{
				args = new Object[] { getRequestPath(
						(HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest())
						+ "/html/internal/person.xhtml?domain="
						+ getDomainSelector().getSelectedDomainName()
						+ "&mpi="
						+ response.getPerson().getMpiId().getValue()};

				args2 = new Object[] { getRequestPath(
						(HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest())
						+ "/html/internal/resolve.xhtml?domain="
						+ getDomainSelector().getSelectedDomainName()
						+ "&mpi="
						+ response.getPerson().getMpiId().getValue()};
			}
			
			switch (response.getMatchStatus())
			{
				case NO_MATCH:
					logMessage(getBundle().getString("add.info.added.noMatch"), Severity.INFO);
					logMessage(new MessageFormat(getBundle().getString("add.info.open")).format(args), Severity.INFO);
					break;
				case MATCH:
					logMessage(getBundle().getString("add.info.added.match"), Severity.INFO);
					logMessage(new MessageFormat(getBundle().getString("add.info.open")).format(args), Severity.INFO);
					break;
				case PERFECT_MATCH:
					logMessage(getBundle().getString("add.info.added.perfectMatch"), Severity.WARN);
					logMessage(new MessageFormat(getBundle().getString("add.info.open")).format(args), Severity.INFO);
					break;
				case POSSIBLE_MATCH:
					resolveController.init();
					logMessage(new MessageFormat(getBundle().getString("add.info.added.possibleMatch")).format(args2), Severity.INFO);
					logMessage(new MessageFormat(getBundle().getString("add.info.open")).format(args), Severity.INFO);
					break;
				case MATCH_ERROR:
					logMessage(getBundle().getString("add.info.added.matchError"), Severity.ERROR);
					break;
				case MULTIPLE_MATCH:
					logMessage(getBundle().getString("add.info.added.multipleMatch"), Severity.ERROR);
					break;
				default:
					logMessage(getBundle().getString("add.info.added.matchUnknown"), Severity.ERROR);
					break;
			}

			switch (response.getMatchStatus())
			{
				case NO_MATCH:
				case MATCH:
				case POSSIBLE_MATCH:
					init();
					break;
				default:
					break;
			}
		}
		catch (InvalidParameterException e)
		{
			logInvalidParameterException(e);
		}
		catch (MPIException e)
		{
			logMPIException(e);
		}
		catch (UnknownObjectException e)
		{
			logMessage(e);
		}
	}

	public IdentityInDTO getIdentity()
	{
		return identity;
	}

	public ContactInDTO getContact()
	{
		return contact;
	}

	public IdentifierDTO getIdentifier()
	{
		return identifier;
	}

	@Override
	public List<SourceDTO> getSources()
	{
		return managementService.getSources();
	}

	public SourceDTO getSelectedSource()
	{
		return selectedSource;
	}

	public void setSelectedSource(SourceDTO selectedSource)
	{
		this.selectedSource = selectedSource;
	}

	public String getComment()
	{
		return comment;
	}

	public void setComment(String comment)
	{
		this.comment = comment;
	}

	public String getMpi()
	{
		return mpi;
	}

	public List<IdentifierDTO> getIdentifiers()
	{
		return identifiers;
	}

	public void setResolveController(ResolveController resolveController)
	{
		this.resolveController = resolveController;
	}

	private void validateDateOfDeath()
	{
		if (!identity.getVitalStatus().isDead())
		{
			identity.setDateOfDeath(null);
		}
	}

	public PersonDTO getPerson()
	{
		return person;
	}
}
