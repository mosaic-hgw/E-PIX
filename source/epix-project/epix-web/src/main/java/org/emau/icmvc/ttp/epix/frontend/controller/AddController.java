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


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

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

	private boolean emptyButton;
	private Integer editContact;
	private boolean editIdentifier;

	private String mpi;

	@PostConstruct
	public void init() throws UnknownObjectException, InvalidParameterException
	{
		identity = new IdentityInDTO();
		contact = new ContactInDTO();
		identifier = new IdentifierDTO();
		selectedSource = null;
		emptyButton = false;
		editContact = null;
		editIdentifier = false;
		mpi = null;

		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, Object> sessionMap = externalContext.getSessionMap();
		if (sessionMap.containsKey(EDIT_PERSON))
		{
			PersonDTO person = service.getPersonByMPI(domainSelector.getSelectedDomainName(), (String) sessionMap.get(EDIT_PERSON));
			List<ContactInDTO> contacts = new ArrayList<>();
			for (ContactOutDTO c : person.getReferenceIdentity().getContacts())
			{
				contacts.add(new ContactInDTO(c));
			}
			identity = new IdentityInDTO(person.getReferenceIdentity(), contacts);
			mpi = person.getMpiId().getValue();
			sessionMap.remove(EDIT_PERSON);
			loadIdentifiers();
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
				for (IdentityOutDTO otherIdentitiy : service.getPersonByMPI(domainSelector.getSelectedDomainName(), mpi).getOtherIdentities())
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
			ResponseEntryDTO response = service.requestMPI(domainSelector.getSelectedDomainName(), identity, selectedSource.getName(), null);
			switch (response.getMatchStatus())
			{
				case NO_MATCH:
					logMessage(getBundle().getString("add.info.added.noMatch"), Severity.INFO);
					break;
				case MATCH:
					logMessage(getBundle().getString("add.info.added.match"), Severity.INFO);
					break;
				case PERFECT_MATCH:
					logMessage(getBundle().getString("add.info.added.perfectMatch"), Severity.WARN);
					break;
				case POSSIBLE_MATCH:
					resolveController.init();
					logMessage(getBundle().getString("add.info.added.possibleMatch"), Severity.INFO);
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
					emptyButton = true;
					break;
			}
		}
		catch (InvalidParameterException e)
		{
			logMessage(getBundle().getString("exception." + e.getMessage().replace(" ", "_")), Severity.ERROR);
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

	public String onUpdateIdentity()
	{
		try
		{
			// TODO force ggf. über Rückfrage im Frontend lösen
			ResponseEntryDTO response = service.updatePerson(domainSelector.getSelectedDomainName(), mpi, identity, selectedSource.getName(), true, comment);

			switch (response.getMatchStatus())
			{
				case NO_MATCH:
				case MATCH:
				case PERFECT_MATCH:
				case POSSIBLE_MATCH:
					ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
					Map<String, Object> sessionMap = externalContext.getSessionMap();
					sessionMap.put("updateIdentityResponse", response.getMatchStatus());
					return "search.xhml?faces-redirect=true";

				case MATCH_ERROR:
					logMessage(getBundle().getString("add.info.edited.matchError"), Severity.ERROR);
					break;
				case MULTIPLE_MATCH:
					logMessage(getBundle().getString("add.info.edited.multipleMatch"), Severity.ERROR);
					break;
				default:
					logMessage(getBundle().getString("add.info.edited.matchUnknown"), Severity.ERROR);
					break;
			}
		}
		catch (InvalidParameterException | UnknownObjectException e)
		{
			logMessage(e);
		}
		catch (MPIException e)
		{
			logMPIException(e);
		}
		return null;
	}

	public String onCancelUpdateIdentity()
	{
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, Object> sessionMap = externalContext.getSessionMap();
		sessionMap.put("cancelUpdateIdentity", "cancel");

		return "search.xhml?faces-redirect=true";
	}

	public void onNewContact()
	{
		contact = new ContactInDTO();
	}

	public void onAddContact()
	{
		if (validateContact())
		{
			identity.getContacts().add(contact);
		}
	}

	public void onDeleteContact(int index)
	{
		identity.getContacts().remove(index);
	}

	public void onEditContact(int index)
	{
		contact = new ContactInDTO(identity.getContacts().get(index));
		editContact = index;
	}

	public void onUpdateContact()
	{
		if (validateContact())
		{
			identity.getContacts().set(editContact, contact);
			editContact = null;
		}
	}

	public void onNewIdentifier()
	{
		identifier = new IdentifierDTO();
	}

	public void onAddIdentifier()
	{
		identity.getIdentifiers().add(identifier);
		loadIdentifiers();
	}

	public void onDeleteIdentifier(int index)
	{
		identity.getIdentifiers().remove(index);
		loadIdentifiers();
	}

	public void onEditIdentifier(int index)
	{
		identifier = identity.getIdentifiers().get(index);
		editIdentifier = true;
	}

	public void onUpdateIdentifier()
	{
		editIdentifier = false;
	}

	private boolean validateContact()
	{
		if (StringUtils.isEmpty(contact.getCity())
				&& StringUtils.isEmpty(contact.getCountry())
				&& StringUtils.isEmpty(contact.getCountryCode())
				&& StringUtils.isEmpty(contact.getDistrict())
				&& StringUtils.isEmpty(contact.getEmail())
				&& StringUtils.isEmpty(contact.getMunicipalityKey())
				&& StringUtils.isEmpty(contact.getPhone())
				&& StringUtils.isEmpty(contact.getState())
				&& StringUtils.isEmpty(contact.getStreet())
				&& StringUtils.isEmpty(contact.getZipCode()))
		{
			logMessage(getBundle().getString("add.error.contactEmpty"), Severity.WARN, false);
			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.renderResponse();
			facesContext.validationFailed();
			return false;
		}
		else if (identity.getContacts().contains(contact))
		{
			logMessage(getBundle().getString("add.error.contactDuplicate"), Severity.WARN, false);
			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.renderResponse();
			facesContext.validationFailed();
			return false;
		}

		return true;
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

	public boolean getEmptyButton()
	{
		return emptyButton;
	}

	public boolean isEditContact()
	{
		return editContact != null;
	}

	public boolean isEditIdentifier()
	{
		return editIdentifier;
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
}
