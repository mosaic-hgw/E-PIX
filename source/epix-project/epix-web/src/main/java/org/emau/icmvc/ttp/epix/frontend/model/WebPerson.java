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


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.emau.icmvc.ttp.epix.common.model.ContactOutDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentifierDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityInBaseDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityOutBaseDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityOutDTO;
import org.emau.icmvc.ttp.epix.common.model.enums.MatchStatus;
import org.icmvc.ttp.web.model.WebTag;

public class WebPerson extends IdentityOutBaseDTO
{
	private static final long serialVersionUID = -7889422396542984987L;

	private List<ContactOutDTO> contacts = new ArrayList<>();
	private String mpiId;
	private MatchStatus matchStatus;
	private Date lastEdited;
	private Date created;
	private String errorMsg;
	private WebTag tag;
	private boolean reference;

	public WebPerson()
	{}
	
	public WebPerson(IdentityOutDTO dto, List<ContactOutDTO> contacts)
	{
		super(dto);
		this.created = dto.getIdentityCreated();
		this.lastEdited = dto.getIdentityLastEdited();
		this.contacts = contacts;
	}
	
	public WebPerson (String mpiId)
	{
		this.mpiId = mpiId;
	}
	
	public WebPerson(IdentityOutDTO dto, List<ContactOutDTO> contacts, String mpiId)
	{
		this(dto, contacts);
		this.mpiId = mpiId;
	}

	public WebPerson(IdentityOutDTO dto, List<ContactOutDTO> contacts, WebTag tag)
	{
		this(dto, contacts);
		this.tag = tag;
	}

	public WebPerson(IdentityOutDTO dto, List<ContactOutDTO> contacts, WebTag tag, boolean reference)
	{
		this(dto, contacts, tag);
		this.reference = reference;
	}

	public WebPerson(IdentityOutDTO dto, List<ContactOutDTO> contacts, String mpiId, MatchStatus matchStatus)
	{
		this(dto, contacts, mpiId);
		this.matchStatus = matchStatus;
	}

	public String getValueForIdentifierDomain(String identifierDomainName)
	{
		for (IdentifierDTO identifierDTO : getIdentifiers())
		{
			if (identifierDTO.getIdentifierDomain().getName().equals(identifierDomainName))
			{
				return identifierDTO.getValue();
			}
		}
		return null;
	}

	public List<ContactOutDTO> getContacts()
	{
		return contacts;
	}

	public void setContacts(List<ContactOutDTO> contacts)
	{
		this.contacts = contacts;
	}

	public String getMpiId()
	{
		return mpiId;
	}

	public void setMpiId(String mpiId)
	{
		this.mpiId = mpiId;
	}

	public IdentityInBaseDTO getIdentity()
	{
		return new IdentityInBaseDTO(this);
	}

	public MatchStatus getMatchStatus()
	{
		return matchStatus;
	}

	public void setMatchStatus(MatchStatus matchStatus)
	{
		this.matchStatus = matchStatus;
	}

	public Date getLastEdited()
	{
		return lastEdited;
	}

	public void setLastEdited(Date lastEdited)
	{
		this.lastEdited = lastEdited;
	}

	public Date getCreated()
	{
		return created;
	}

	public void setCreated(Date created)
	{
		this.created = created;
	}

	public String getErrorMsg()
	{
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg)
	{
		this.errorMsg = errorMsg;
	}

	public WebTag getTag()
	{
		return tag;
	}

	public void setTag(WebTag tag)
	{
		this.tag = tag;
	}

	public ContactOutDTO getContact()
	{
		if (getContacts().isEmpty())
		{
			return new ContactOutDTO();
		}
		else
		{
			return getContacts().get(getContacts().size() - 1);
		}
	}

	public boolean isReference()
	{
		return reference;
	}

	public void setReference(boolean reference)
	{
		this.reference = reference;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;
		WebPerson webPerson = (WebPerson) o;
		return Objects.equals(contacts, webPerson.contacts) &&
				Objects.equals(mpiId, webPerson.mpiId) &&
				matchStatus == webPerson.matchStatus &&
				Objects.equals(lastEdited, webPerson.lastEdited) &&
				Objects.equals(errorMsg, webPerson.errorMsg);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(super.hashCode(), contacts, mpiId, matchStatus, lastEdited, errorMsg);
	}
}
