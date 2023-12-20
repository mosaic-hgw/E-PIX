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
import java.io.Serializable;
import java.util.Date;

import org.emau.icmvc.ttp.epix.common.model.ContactOutDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentifierDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityOutBaseDTO;

public class WebHistory implements Serializable
{
	@Serial private static final long serialVersionUID = -3408171599915900408L;

	private final Date date;
	private final Event event;
	private IdentityOutBaseDTO identity;
	private IdentityOutBaseDTO oldIdentity;
	private ContactOutDTO contact;
	private IdentifierDTO identifier;
	private final String user;

	public WebHistory(Date date, Event event, String user)
	{
		this.date = date;
		this.event = event;
		this.user = user;
	}

	public WebHistory(Date date, Event event, String user, IdentityOutBaseDTO identity)
	{
		this(date, event, user);
		this.identity = identity;
	}

	public WebHistory(Date date, Event event, String user, IdentityOutBaseDTO identity, IdentityOutBaseDTO oldIdentity)
	{
		this(date, event, user, identity);
		this.oldIdentity = oldIdentity;
	}

	public WebHistory(Date date, Event event, String user, ContactOutDTO contact)
	{
		this(date, event, user);
		this.contact = contact;
	}

	public WebHistory(Date date, Event event, String user, IdentifierDTO identifier)
	{
		this(date, event, user);
		this.identifier = identifier;
	}

	public Date getDate()
	{
		return date;
	}

	public Event getEvent()
	{
		return event;
	}

	public IdentityOutBaseDTO getIdentity()
	{
		return identity;
	}

	public IdentityOutBaseDTO getOldIdentity()
	{
		return oldIdentity;
	}

	public ContactOutDTO getContact()
	{
		return contact;
	}

	public IdentifierDTO getIdentifier()
	{
		return identifier;
	}

	public void setContact(ContactOutDTO contact)
	{
		this.contact = contact;
	}

	public String getUser()
	{
		return user;
	}

	public enum Event
	{
		PERSON_CREATED, PERSON_MERGED, PERSON_SPLIT, PERSON_REFERENCE, PERSON_UNKNOWN,
		IDENTITY_DELETED, IDENTITY_DEACTIVATED, IDENTITY_ADDED, IDENTITY_UPDATED, IDENTITY_MATCHED, IDENTITY_PERFECT_MATCHED,
		IDENTIFIER_DELETED, IDENTIFIER_DEACTIVATED, IDENTIFIER_ADDED,
		CONTACT_DELETED, CONTACT_DEACTIVATED, CONTACT_ADDED, CONTACT_UPDATED, CONTACT_REFERENCE, CONTACT_UNKNOWN
	}

	@Override public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (!(o instanceof WebHistory that))
			return false;

		if (getDate() != null ? !getDate().equals(that.getDate()) : that.getDate() != null)
			return false;
		if (getEvent() != that.getEvent())
			return false;
		if (getIdentity() != null ? !getIdentity().equals(that.getIdentity()) : that.getIdentity() != null)
			return false;
		if (getOldIdentity() != null ? !getOldIdentity().equals(that.getOldIdentity()) : that.getOldIdentity() != null)
			return false;
		if (getContact() != null ? !getContact().equals(that.getContact()) : that.getContact() != null)
			return false;
		return getIdentifier() != null ? getIdentifier().equals(that.getIdentifier()) : that.getIdentifier() == null;
	}

	@Override public int hashCode()
	{
		int result = getDate() != null ? getDate().hashCode() : 0;
		result = 31 * result + (getEvent() != null ? getEvent().hashCode() : 0);
		result = 31 * result + (getIdentity() != null ? getIdentity().hashCode() : 0);
		result = 31 * result + (getOldIdentity() != null ? getOldIdentity().hashCode() : 0);
		result = 31 * result + (getContact() != null ? getContact().hashCode() : 0);
		result = 31 * result + (getIdentifier() != null ? getIdentifier().hashCode() : 0);
		return result;
	}
}
