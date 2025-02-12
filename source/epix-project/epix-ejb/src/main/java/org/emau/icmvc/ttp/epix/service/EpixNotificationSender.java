package org.emau.icmvc.ttp.epix.service;

/*-
 * ###license-information-start###
 * E-PIX - Enterprise Patient Identifier Cross-referencing
 * __
 * Copyright (C) 2009 - 2025 Trusted Third Party of the University Medicine Greifswald
 * 							kontakt-ths@uni-greifswald.de
 * 
 * 							concept and implementation
 * 							l.geidel,c.schack, d.langner, g.koetzschke
 * 
 * 							web client
 * 							a.blumentritt, f.m. moser
 * 
 * 							docker
 * 							r.schuldt, f.m. moser
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

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import jakarta.ejb.Stateless;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.emau.icmvc.ttp.epix.common.model.IdentifierDTO;
import org.emau.icmvc.ttp.epix.common.model.PersonDTO;
import org.emau.icmvc.ttp.epix.persistence.model.Identity;
import org.emau.icmvc.ttp.epix.persistence.model.Person;
import org.emau.icmvc.ttp.notification.NotificationSender;

@Stateless
public class EpixNotificationSender extends NotificationSender
{
	public static final String KEY_EPIX_DOMAIN = "EPIX domain";
	public static final String KEY_MPI_ID = "mpiId";
	public static final String KEY_PERSON = "Person";
	public static final String KEY_COMMENT = "comment";
	public static final String KEY_IDENTIFIER = "Identifier";
	public static final String KEY_IDENTIFIERS = "Identifiers";
	public static final String KEY_LOCAL_IDENTIFIER = "Local Identifier";
	public static final String KEY_LOCAL_IDS = "localIds";
	public static final String KEY_TARGET_PERSON = "Target Person";
	public static final String KEY_IDENTITY_ID = "identityId";
	public static final String KEY_CONTACT_ID = "contactId";
	public static final String KEY_MASTER_PERSON = "Master Person";
	public static final String KEY_SLAVE_PERSON = "Slave Person";

	public void sendRequestMPINotification(String notificationClientID, PersonDTO personDTO, String domainName, String comment)
	{
		sendNotification(notificationClientID, "RequestMPI", domainName, comment, root -> {
			JSONObject person = new JSONObject();
			person.put(KEY_MPI_ID, personDTO.getMpiId().getValue());
			root.put(KEY_PERSON, person);
		});
	}

	public void sendAddIdentifierToPersonNotification(String notificationClientID, String domainName, String mpi,
			List<IdentifierDTO> localIds)
	{
		sendNotification(notificationClientID, "AddIdentifierToPerson", domainName, null, root -> {
			JSONObject person = new JSONObject();
			person.put(KEY_MPI_ID, mpi);
			person.put(KEY_LOCAL_IDS, mapToIdentifierValues(localIds));
			root.put(KEY_PERSON, person);
		});
	}

	public void sendAddLocalIdentifierToIdentifierNotification(String notificationClientID, String domainName,
			IdentifierDTO identifierDTO, List<IdentifierDTO> localIds)
	{
		sendNotification(notificationClientID, "AddLocalIdentifierToIdentifier", domainName, null, root -> {
			JSONObject identifier = new JSONObject();
			identifier.put(KEY_IDENTIFIER, identifierDTO.getValue());
			identifier.put(KEY_IDENTIFIERS, mapToIdentifierValues(localIds));
			root.put(KEY_LOCAL_IDENTIFIER, identifier);
		});
	}

	public void sendUpdatePersonNotification(String notificationClientID, PersonDTO personDTO, String domainName, String comment)
	{
		sendNotification(notificationClientID, "UpdatePerson", domainName, comment, root -> {
			JSONObject person = new JSONObject();
			person.put(KEY_MPI_ID, personDTO.getMpiId().getValue());
			root.put(KEY_PERSON, person);
		});
	}

	public void sendAddPersonNotification(String notificationClientID, PersonDTO personDTO, String domainName, String comment)
	{
		sendNotification(notificationClientID, "AddPerson", domainName, comment, root -> {
			JSONObject person = new JSONObject();
			person.put(KEY_MPI_ID, personDTO.getMpiId().getValue());
			root.put(KEY_PERSON, person);
		});
	}

	public void sendDeactivatePersonNotification(String notificationClientID, String mpi, String domainName)
	{
		sendNotification(notificationClientID, "DeactivatePerson", domainName, null, root -> {
			JSONObject person = new JSONObject();
			person.put(KEY_MPI_ID, mpi);
			root.put(KEY_PERSON, person);
		});
	}

	public void sendDeletePersonNotification(String notificationClientID, String mpi, String domainName)
	{
		sendNotification(notificationClientID, "DeletePerson", domainName, null, root -> {
			JSONObject person = new JSONObject();
			person.put(KEY_MPI_ID, mpi);
			root.put(KEY_PERSON, person);
		});
	}

	public void sendSetReferenceIdentityNotification(String notificationClientID, String mpi, long identityId,
			String domainName, String comment)
	{
		sendNotification(notificationClientID, "SetReferenceIdentity", domainName, comment, root -> {
			JSONObject person = new JSONObject();
			person.put(KEY_MPI_ID, mpi);
			person.put(KEY_IDENTITY_ID, identityId);
			root.put(KEY_PERSON, person);
		});
	}

	public void sendDeactivateIdentityNotification(String notificationClientID, long identityId, String domainName)
	{
		sendNotification(notificationClientID, "DeactivateIdentity", domainName, null, root -> {
			JSONObject person = new JSONObject();
			person.put(KEY_IDENTITY_ID, identityId);
			root.put(KEY_PERSON, person);
		});
	}

	public void sendDeleteIdentityNotification(String notificationClientID, long identityId, String domainName)
	{
		sendNotification(notificationClientID, "DeleteIdentity", domainName, null, root -> {
			JSONObject person = new JSONObject();
			person.put(KEY_IDENTITY_ID, identityId);
			root.put(KEY_PERSON, person);
		});
	}

	public void sendAddContactNotification(String notificationClientID, long identityId, String domainName)
	{
		sendNotification(notificationClientID, "AddContact", domainName, null, root -> {
			JSONObject person = new JSONObject();
			person.put(KEY_IDENTITY_ID, identityId);
			root.put(KEY_PERSON, person);
		});
	}

	public void sendDeactivateContactNotification(String notificationClientID, long contactId, String domainName)
	{
		sendNotification(notificationClientID, "DeactivateContact", domainName, null, root -> {
			JSONObject person = new JSONObject();
			person.put(KEY_CONTACT_ID, contactId);
			root.put(KEY_PERSON, person);
		});
	}

	public void sendDeleteContactNotification(String notificationClientID, long contactId, String domainName)
	{
		sendNotification(notificationClientID, "DeleteContact", domainName, null, root -> {
			JSONObject person = new JSONObject();
			person.put(KEY_CONTACT_ID, contactId);
			root.put(KEY_PERSON, person);
		});
	}

	public void sendMoveIdentitiesForIdentifierToPersonNotification(String notificationClientID, String domainName,
			IdentifierDTO identifier, String mpiId, String comment)
	{
		sendNotification(notificationClientID, "MoveIdentitiesForIdentifierToPerson", domainName, comment, root -> {
			root.put(KEY_IDENTIFIER, identifier.getValue());
			JSONObject targetPerson = new JSONObject();
			targetPerson.put(KEY_MPI_ID, mpiId);
			root.put(KEY_TARGET_PERSON, targetPerson);
		});
	}

	public void sendAssignIdentityNotification(String notificationClientID, Identity winningIdentity, Identity identityToMerge,
			String mpiToMerge, String comment)
	{
		final Person person = winningIdentity.getPerson();
		sendNotification(notificationClientID, "AssignIdentity", person.getDomain().getName(), comment, root -> {
			JSONObject masterPerson = new JSONObject();
			masterPerson.put(KEY_MPI_ID, person.getFirstMPI().getValue());
			masterPerson.put(KEY_IDENTITY_ID, winningIdentity.getId());
			root.put(KEY_MASTER_PERSON, masterPerson);
			JSONObject slavePerson = new JSONObject();
			slavePerson.put(KEY_MPI_ID, mpiToMerge);
			slavePerson.put(KEY_IDENTITY_ID, identityToMerge.getId());
			root.put(KEY_SLAVE_PERSON, slavePerson);
		});
	}

	public void sendRemoveIdentifierNotification(String notificationClientID, String domainName, List<IdentifierDTO> localIds)
	{
		sendNotification(notificationClientID, "RemoveIdentifier", domainName, null, root ->
			root.put(KEY_LOCAL_IDS, mapToIdentifierValues(localIds))
		);
	}

	private void sendNotification(String clientID, String type, String domainName, String comment, JSONConsumer c)
	{
		try
		{
			JSONObject root = new JSONObject();
			if (StringUtils.isNotBlank(domainName))
			{
				root.put(KEY_EPIX_DOMAIN, domainName);
			}
			if (StringUtils.isNotBlank(comment))
			{
				root.put(KEY_COMMENT, comment);
			}
			c.accept(root); // let the given consumer customize the JSON
			sendNotification(root, clientID, "EPIX." + type);
		}
		catch (JSONException shouldNotHappen)
		{
			logger.error("unexpected exception while sending {} notification", type, shouldNotHappen);
		}
	}

	@FunctionalInterface
	public interface JSONConsumer
	{
		/**
		 * Performs some operation on the given JSON object.
		 */
		void accept(JSONObject t) throws JSONException;
	}

	private List<JSONObject> mapToIdentifierValues(List<IdentifierDTO> identifiers)
	{
		return identifiers.stream().map(item -> {
			try
			{
				return new JSONObject()
						.put("domain", item.getIdentifierDomain().getName())
						.put("value", item.getValue());
			}
			catch (JSONException e)
			{
				logger.error("unexpected exception while mapping identifier values ({})", item, e);
			}
			return null;
		}).filter(Predicate.not(Objects::nonNull)).collect(Collectors.toList());
	}
}
