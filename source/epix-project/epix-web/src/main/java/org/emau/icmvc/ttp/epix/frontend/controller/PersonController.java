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

import java.io.Serial;
import java.io.Serializable;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.emau.icmvc.ttp.epix.common.exception.DuplicateEntryException;
import org.emau.icmvc.ttp.epix.common.exception.IllegalOperationException;
import org.emau.icmvc.ttp.epix.common.exception.InvalidParameterException;
import org.emau.icmvc.ttp.epix.common.exception.MPIException;
import org.emau.icmvc.ttp.epix.common.exception.UnknownObjectException;
import org.emau.icmvc.ttp.epix.common.model.ContactHistoryDTO;
import org.emau.icmvc.ttp.epix.common.model.ContactInDTO;
import org.emau.icmvc.ttp.epix.common.model.ContactOutDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentifierDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentifierHistoryDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityHistoryDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityInDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityOutBaseDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityOutDTO;
import org.emau.icmvc.ttp.epix.common.model.PersonDTO;
import org.emau.icmvc.ttp.epix.common.model.PersonHistoryDTO;
import org.emau.icmvc.ttp.epix.common.model.PossibleMatchForMPIDTO;
import org.emau.icmvc.ttp.epix.common.model.PossibleMatchHistoryDTO;
import org.emau.icmvc.ttp.epix.frontend.controller.common.AbstractEpixWebBean;
import org.emau.icmvc.ttp.epix.frontend.model.IdentityHistoryPair;
import org.emau.icmvc.ttp.epix.frontend.model.WebHistory;
import org.emau.icmvc.ttp.epix.frontend.model.WebPerson;
import org.emau.icmvc.ttp.epix.frontend.util.HistoryHelper;
import org.icmvc.ttp.web.model.WebTag;

@ViewScoped
@ManagedBean(name = "personController")
public class PersonController extends AbstractEpixWebBean implements Serializable
{
	@Serial
	private static final long serialVersionUID = -1087566417706811854L;

	@ManagedProperty(value = "#{historyHelper}")
	protected HistoryHelper historyHelper;

	private PersonDTO person;
	private String referenceMpi;
	private IdentityOutDTO referenceIdentity;

	private List<String> mpis;
	private List<IdentityOutDTO> identities;
	private List<WebPerson> webIdentities;
	private List<ContactOutDTO> contacts;
	private List<WebPerson> webContacts;
	private Set<IdentifierDTO> identifiers;

	private IdentityInDTO editIdentity;
	private String editSource;
	private String editComment;
	private boolean forceEdit = false;
	private boolean edit = false;

	private ContactInDTO editContact;
	private Long editContactId;

	private IdentifierDTO editIdentifier;

	private List<WebHistory> historyList;

	private List<PossibleMatchForMPIDTO> possibleMatches;

	public void init(String domain, String mpi) throws UnknownObjectException, InvalidParameterException
	{
		if (FacesContext.getCurrentInstance().isPostback())
		{
			return;
		}

		if (StringUtils.isNotEmpty(domain))
		{
			getDomainSelector().setSelectedDomain(domain);
		}
		load(mpi);
	}

	private void load(String mpi) throws InvalidParameterException, UnknownObjectException
	{
		if (StringUtils.isEmpty(mpi))
		{
			logMessage(getBundle().getString("page.person.message.error.noMpi"), Severity.ERROR);
		}
		else
		{
			try
			{
				person = service.getPersonByFirstMPI(getDomainSelector().getSelectedDomainName(), mpi);
			}
			catch (UnknownObjectException e)
			{
				Object[] args = { mpi };
				logMessage(new MessageFormat(getBundle().getString("page.person.message.warn.notFound")).format(args), Severity.WARN);
				return;
			}
			referenceMpi = person.getMpiId().getValue();
			referenceIdentity = person.getReferenceIdentity();
			if (person.isDeactivated())
			{
				logMessage(getBundle().getString("page.person.message.warn.deactivated"), Severity.WARN);
			}
			else
			{
				possibleMatches = service.getPossibleMatchesForPerson(getDomainSelector().getSelectedDomainName(), referenceMpi);
			}
			loadIdentities();
			loadContacts();
			loadIdentifiers();
			loadHistory();
		}
	}

	private void loadIdentities()
	{
		webIdentities = new ArrayList<>();
		identities = new ArrayList<>();

		if (referenceIdentity != null)
		{
			identities.add(referenceIdentity);
			webIdentities.add(
					new WebPerson(referenceIdentity, referenceIdentity.getContacts(), new WebTag(getBundle().getString("page.person.identity.reference"), WebTag.Color.lightblack, "star-circle"),
							true));
		}
		if (person.getOtherIdentities() != null)
		{
			identities.addAll(person.getOtherIdentities());
			webIdentities.addAll(person.getOtherIdentities().stream().map(i -> new WebPerson(i, i.getContacts(), "")).toList());
		}

		webIdentities = webIdentities.stream().sorted(Comparator.comparing(WebPerson::getIdentityLastEdited).reversed()).collect(Collectors.toList());
	}

	private void loadContacts()
	{
		contacts = new ArrayList<>();
		for (IdentityOutDTO identity : identities)
		{
			contacts.addAll(identity.getContacts());
		}
		Collections.reverse(contacts);

		webContacts = new ArrayList<>();
		boolean first = true;
		for (ContactOutDTO contact : contacts)
		{
			if (first)
			{
				webContacts.add(new WebPerson(new IdentityOutDTO(), Collections.singletonList(contact),
						new WebTag(getBundle().getString("page.person.contact.current"), WebTag.Color.lightblack, "home-circle")));
				first = false;
			}
			else
			{
				webContacts.add(new WebPerson(new IdentityOutDTO(), Collections.singletonList(contact), ""));
			}
		}
	}

	private void loadIdentifiers()
	{
		identifiers = new HashSet<>();
		mpis = new ArrayList<>();
		for (IdentityOutDTO identity : identities)
		{
			for (IdentifierDTO identifier : identity.getIdentifiers())
			{
				if (identifier.getIdentifierDomain().getName().equals(getDomainSelector().getSelectedDomain().getMpiDomain().getName()))
				{
					mpis.add(identifier.getValue());
				}
				else
				{
					identifiers.add(identifier);
				}
			}
		}
	}

	private void loadHistory() throws UnknownObjectException, InvalidParameterException
	{
		historyList = new ArrayList<>();

		// Link History (Merges and Splits)
		if (!person.isDeactivated())
		{
			for (PossibleMatchHistoryDTO possibleMatchHistoryDTO : managementService.getPossibleMatchHistoryForPerson(getDomainSelector().getSelectedDomainName(), referenceMpi))
			{
				switch (possibleMatchHistoryDTO.getSolution())
				{
					case MERGE ->
					{
						IdentityHistoryPair pair = historyHelper.toMergePair(possibleMatchHistoryDTO);
						historyList.add(new WebHistory(possibleMatchHistoryDTO.getHistoryTimestamp(), WebHistory.Event.PERSON_MERGED, possibleMatchHistoryDTO.getUser(), pair.getNewIdentity(),
								pair.getOldIdentity()));
					}
					case SPLIT ->
					{
						IdentityHistoryPair pair = historyHelper.toSplitPair(possibleMatchHistoryDTO);
						historyList.add(new WebHistory(possibleMatchHistoryDTO.getHistoryTimestamp(), WebHistory.Event.PERSON_SPLIT, possibleMatchHistoryDTO.getUser(),
								pair.getNewIdentity().getPersonId() == referenceIdentity.getPersonId() ? pair.getNewIdentity() : pair.getOldIdentity(),
								pair.getOldIdentity().getPersonId() != referenceIdentity.getPersonId() ? pair.getOldIdentity() : pair.getNewIdentity()));
					}
				}
			}
		}
		else
		{
			logMessage(getBundle().getString("page.person.message.warn.historyIncomplete"), Severity.WARN);
		}

		// Identifier History
		for (IdentifierDTO identifierDTO : identifiers)
		{
			// post v2023.1
			for (IdentifierHistoryDTO identifierHistoryDTO : managementService.getHistoryForIdentifier(identifierDTO.getIdentifierDomain().getName(), identifierDTO.getValue()))
			{
				WebHistory.Event webEvent = switch (identifierHistoryDTO.getEvent())
						{
							case NEW -> WebHistory.Event.IDENTIFIER_ADDED;
							case DEACTIVATE -> WebHistory.Event.IDENTIFIER_DEACTIVATED;
							default -> null;
						};
				if (webEvent != null)
				{
					historyList.add(new WebHistory(identifierHistoryDTO.getHistoryTimestamp(), webEvent, identifierHistoryDTO.getUser(), identifierHistoryDTO));
				}
			}

			// pre v2023.1
			if (historyList.stream().noneMatch(h -> h.getIdentifier() != null && h.getIdentifier().getIdentifierDomain().equals(identifierDTO.getIdentifierDomain()) && h.getIdentifier().getValue()
					.equals(identifierDTO.getValue())))
			{
				historyList.add(new WebHistory(identifierDTO.getEntryDate(), WebHistory.Event.IDENTIFIER_ADDED, null, identifierDTO));
			}
		}

		// Contact History
		for (ContactOutDTO contact : contacts)
		{
			// post v2023.1
			for (ContactHistoryDTO contactHistoryDTO : managementService.getHistoryForContact(contact.getContactId()))
			{
				WebHistory.Event webEvent = switch (contactHistoryDTO.getEvent())
						{
							case NEW -> WebHistory.Event.CONTACT_ADDED;
							case UPDATE -> WebHistory.Event.CONTACT_UPDATED;
							case DEACTIVATED -> WebHistory.Event.CONTACT_DEACTIVATED;
							case SET_REFERENCE -> WebHistory.Event.CONTACT_REFERENCE;
							default -> null;
						};
				if (webEvent != null)
				{
					historyList.add(new WebHistory(contactHistoryDTO.getHistoryTimestamp(), webEvent, contactHistoryDTO.getUser(), contactHistoryDTO));
				}
			}

			// pre v2023.1
			if (historyList.stream().noneMatch(h -> h.getContact() != null && h.getContact().getContactId() == contact.getContactId()))
			{
				historyList.add(new WebHistory(contact.getContactCreated(), WebHistory.Event.CONTACT_ADDED, null, contact));
			}

			// Person history
			for (PersonHistoryDTO personHistoryDTO : managementService.getHistoryForPerson(getDomainSelector().getSelectedDomainName(), referenceMpi))
			{
				switch (personHistoryDTO.getEvent())
				{
					case DELETE_IDENTITY -> historyList.add(new WebHistory(personHistoryDTO.getHistoryTimestamp(), WebHistory.Event.IDENTITY_DELETED, personHistoryDTO.getUser()));
				}
			}
		}

		// Identity History
		for (IdentityHistoryDTO identityHistoryDTO : managementService.getIdentityHistoryByPersonId(person.getPersonId()))
		{
			switch (identityHistoryDTO.getEvent())
			{
				case NEW -> historyList.add(new WebHistory(identityHistoryDTO.getHistoryTimestamp(), WebHistory.Event.PERSON_CREATED, identityHistoryDTO.getUser(), identityHistoryDTO));
				case UPDATE -> historyList.add(new WebHistory(identityHistoryDTO.getHistoryTimestamp(), WebHistory.Event.IDENTITY_UPDATED, identityHistoryDTO.getUser(), identityHistoryDTO));
				case FORCED_UPDATE -> historyList.add(new WebHistory(identityHistoryDTO.getHistoryTimestamp(), WebHistory.Event.IDENTITY_ADDED, identityHistoryDTO.getUser(), identityHistoryDTO));
				case SET_REFERENCE -> historyList.add(new WebHistory(identityHistoryDTO.getHistoryTimestamp(), WebHistory.Event.PERSON_REFERENCE, identityHistoryDTO.getUser(), identityHistoryDTO));
				case DEACTIVATED -> historyList.add(new WebHistory(identityHistoryDTO.getHistoryTimestamp(), WebHistory.Event.IDENTITY_DEACTIVATED, identityHistoryDTO.getUser(), identityHistoryDTO));
				case MATCH ->
				{
					IdentityHistoryPair pair = historyHelper.toIdentityHistoryPair(identityHistoryDTO);
					historyList.add(
							new WebHistory(identityHistoryDTO.getHistoryTimestamp(), WebHistory.Event.IDENTITY_MATCHED, identityHistoryDTO.getUser(), pair.getNewIdentity(), pair.getOldIdentity()));
				}
				case PERFECT_MATCH ->
				{
					IdentityHistoryPair pair = historyHelper.toIdentityHistoryPair(identityHistoryDTO);
					historyList.add(new WebHistory(identityHistoryDTO.getHistoryTimestamp(), WebHistory.Event.IDENTITY_PERFECT_MATCHED, identityHistoryDTO.getUser(), pair.getNewIdentity(),
							pair.getOldIdentity()));
				}
				case DEL_CONTACT -> historyList.add(new WebHistory(identityHistoryDTO.getHistoryTimestamp(), WebHistory.Event.CONTACT_DELETED, identityHistoryDTO.getUser()));
				case ADD_CONTACT ->
				{
					// use event identity.add_contact only if contact.new is no longer present (e.g. after contact was deleted) because identity.add_contact provides only meta information and no contact data
					if (historyList.stream().noneMatch(
							h -> h.getContact() != null && h.getContact().getIdentityId() == identityHistoryDTO.getIdentityId() && h.getDate().equals(identityHistoryDTO.getHistoryTimestamp())))
					{
						historyList.add(new WebHistory(identityHistoryDTO.getHistoryTimestamp(), WebHistory.Event.CONTACT_ADDED, identityHistoryDTO.getUser()));
					}
				}
				case DEL_IDENTIF -> historyList.add(new WebHistory(identityHistoryDTO.getHistoryTimestamp(), WebHistory.Event.IDENTIFIER_DELETED, identityHistoryDTO.getUser()));
				case ADD_IDENTIF ->
				{
					// use event identity.add_identifier only if identifier.new is no longer present (e.g. after identifier was deleted) because identity.add_identifier provides only meta information and no identifier data
					if (historyList.stream().noneMatch(
							h -> h.getIdentifier() != null && h.getDate().equals(identityHistoryDTO.getHistoryTimestamp())))
					{
						historyList.add(new WebHistory(identityHistoryDTO.getHistoryTimestamp(), WebHistory.Event.IDENTIFIER_ADDED, identityHistoryDTO.getUser()));
					}
				}
			}
		}

		historyList = historyList.stream()
				.sorted(Comparator.comparing(WebHistory::getDate).thenComparing(WebHistory::getEvent).reversed())
				.collect(Collectors.toList());
	}

	public String onOpenPossibleMatches()
	{
		return "resolve.xhtml?domain=" + getSelectedDomain().getName() + "&mpi=" + referenceMpi + "&faces-redirect=true";
	}

	public void onDeactivatePerson()
	{
		try
		{
			service.deactivatePerson(getDomainSelector().getSelectedDomainName(), referenceMpi);
			logMessage(getBundle().getString("page.person.message.info.personDeactivated"), Severity.INFO);
		}
		catch (InvalidParameterException | MPIException | UnknownObjectException e)
		{
			logMessage(e.getLocalizedMessage(), Severity.ERROR);
		}
	}

	public String onDeletePerson()
	{
		try
		{
			service.deactivatePerson(getDomainSelector().getSelectedDomainName(), referenceMpi);
			service.deletePerson(getDomainSelector().getSelectedDomainName(), referenceMpi);
			return "dashboard.xhtml?message=message.epix.person.deleted&severity=INFO&faces-redirect=true";
		}
		catch (InvalidParameterException | MPIException | UnknownObjectException | IllegalOperationException e)
		{
			logMessage(e.getLocalizedMessage(), Severity.ERROR);
			return null;
		}
	}

	public void onNewIdentity()
	{
		editIdentity = new IdentityInDTO();
		edit = false;
	}

	public void onAddIdentity()
	{
		try
		{
			service.updatePerson(getDomainSelector().getSelectedDomainName(), referenceMpi, editIdentity, editSource, forceEdit, editComment);
			load(referenceMpi);
			logMessage(getBundle().getString("page.person.message.info.identityAdded"), Severity.INFO);
		}
		catch (InvalidParameterException | MPIException | UnknownObjectException e)
		{
			if (e.getLocalizedMessage().contains("fit to the person"))
			{
				logMessage(getBundle().getString("page.person.identity.add.error.idatDoesNotFit"), Severity.ERROR);
			}
			else
			{
				logMessage(e.getLocalizedMessage(), Severity.ERROR);
			}

			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.renderResponse();
			facesContext.validationFailed();
		}
	}

	public void onEditIdentity(long identityId)
	{
		editIdentity = new IdentityInDTO(identities.stream().filter(i -> i.getIdentityId() == identityId).findFirst().orElse(new IdentityOutDTO()), null);
		edit = true;
	}

	public void onDeleteIdentity(long identityId)
	{
		try
		{
			service.deactivateIdentity(identityId);
			service.deleteIdentity(identityId);
			load(referenceMpi);
			logMessage(getBundle().getString("page.person.message.info.identityDeleted"), Severity.INFO);
		}
		catch (IllegalOperationException | MPIException | UnknownObjectException | InvalidParameterException e)
		{
			logMessage(e.getLocalizedMessage(), Severity.ERROR);
		}
	}

	public void onSetReferenceIdentity(long identityId) throws MPIException, InvalidParameterException, UnknownObjectException
	{
		service.setReferenceIdentity(getDomainSelector().getSelectedDomainName(), referenceMpi, identityId, null);
		load(referenceMpi);
		logMessage(getBundle().getString("page.person.message.info.referenceIdentitySet"), Severity.INFO);
	}

	public void onNewContact()
	{
		editContact = new ContactInDTO();
		editContactId = null;
	}

	public void onAddContact()
	{
		try
		{
			if (editContactId != null)
			{
				service.deactivateContact(editContactId);
				service.deleteContact(editContactId);
			}
			service.addContact(referenceIdentity.getIdentityId(), editContact);

			load(referenceMpi);
			logMessage(getBundle().getString("page.person.message.info.contactAdded"), Severity.INFO);
		}
		catch (InvalidParameterException | MPIException | UnknownObjectException | DuplicateEntryException e)
		{
			logMessage(e.getLocalizedMessage(), Severity.ERROR);

			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.renderResponse();
			facesContext.validationFailed();
		}
		catch (IllegalOperationException e)
		{
			logMessage(e.getLocalizedMessage(), Severity.ERROR);
		}
	}

	public void onEditContact(long contactId)
	{
		editContact = new ContactInDTO(contacts.stream().filter(c -> c.getContactId() == contactId).findFirst().orElse(new ContactOutDTO()));
		editContactId = contactId;
	}

	public void onDeleteContact(long contactId)
	{
		try
		{
			service.deactivateContact(contactId);
			service.deleteContact(contactId);
			load(referenceMpi);
			logMessage(getBundle().getString("page.person.message.info.contactDeleted"), Severity.INFO);
		}
		catch (UnknownObjectException | InvalidParameterException | IllegalOperationException e)
		{
			logMessage(e.getLocalizedMessage(), Severity.ERROR);
		}
	}

	public void onNewIdentifier()
	{
		editIdentifier = new IdentifierDTO();
	}

	public void onAddIdentifier()
	{
		try
		{
			service.addLocalIdentifierToActivePersonWithMPI(getDomainSelector().getSelectedDomainName(), referenceMpi, Collections.singletonList(editIdentifier));
			logMessage(getBundle().getString("page.person.message.info.identifierAdded"), Severity.INFO);
			load(referenceMpi);
		}
		catch (InvalidParameterException | MPIException | UnknownObjectException e)
		{
			logMessage(e.getLocalizedMessage(), Severity.ERROR);
			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.renderResponse();
			facesContext.validationFailed();
		}
	}

	public void onDeleteIdentifier(IdentifierDTO identifierDTO)
	{
		try
		{
			service.removeLocalIdentifier(getDomainSelector().getSelectedDomainName(), Collections.singletonList(identifierDTO));
			load(referenceMpi);
			logMessage(getBundle().getString("page.person.message.info.identifierDeleted"), Severity.INFO);
		}
		catch (MPIException | UnknownObjectException | InvalidParameterException e)
		{
			logMessage(e.getLocalizedMessage(), Severity.ERROR);
		}
	}

	public int getAge(IdentityOutBaseDTO identity)
	{
		LocalDate startDate = identity.getBirthDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate endDate = (identity.getDateOfDeath() != null ? identity.getDateOfDeath() : new Date()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		return (int) ChronoUnit.YEARS.between(startDate, endDate);
	}

	public void setHistoryHelper(HistoryHelper historyHelper)
	{
		this.historyHelper = historyHelper;
	}

	public PersonDTO getPerson()
	{
		return person;
	}

	public String getReferenceMpi()
	{
		return referenceMpi;
	}

	public List<String> getMpis()
	{
		return mpis;
	}

	public IdentityOutDTO getReferenceIdentity()
	{
		return referenceIdentity;
	}

	public List<IdentityOutDTO> getIdentities()
	{
		return identities;
	}

	public List<WebPerson> getWebIdentities()
	{
		return webIdentities;
	}

	public List<ContactOutDTO> getContacts()
	{
		return contacts;
	}

	public List<WebPerson> getWebContacts()
	{
		return webContacts;
	}

	public Set<IdentifierDTO> getIdentifiers()
	{
		return identifiers;
	}

	public IdentityInDTO getEditIdentity()
	{
		return editIdentity;
	}

	public void setEditIdentity(IdentityInDTO editIdentity)
	{
		this.editIdentity = editIdentity;
	}

	public String getEditSource()
	{
		return editSource;
	}

	public void setEditSource(String editSource)
	{
		this.editSource = editSource;
	}

	public String getEditComment()
	{
		return editComment;
	}

	public void setEditComment(String editComment)
	{
		this.editComment = editComment;
	}

	public boolean isForceEdit()
	{
		return forceEdit;
	}

	public void setForceEdit(boolean forceEdit)
	{
		this.forceEdit = forceEdit;
	}

	public ContactInDTO getEditContact()
	{
		return editContact;
	}

	public void setEditContact(ContactInDTO editContact)
	{
		this.editContact = editContact;
	}

	public IdentifierDTO getEditIdentifier()
	{
		return editIdentifier;
	}

	public void setEditIdentifier(IdentifierDTO editIdentifier)
	{
		this.editIdentifier = editIdentifier;
	}

	public List<WebHistory> getHistoryList()
	{
		return historyList;
	}

	public void setHistoryList(List<WebHistory> historyList)
	{
		this.historyList = historyList;
	}

	public List<PossibleMatchForMPIDTO> getPossibleMatches()
	{
		return possibleMatches;
	}

	public boolean isEdit()
	{
		return edit;
	}
}
