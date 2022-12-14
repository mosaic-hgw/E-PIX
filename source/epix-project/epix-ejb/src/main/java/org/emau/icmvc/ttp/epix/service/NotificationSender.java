package org.emau.icmvc.ttp.epix.service;

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


import java.net.ConnectException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.emau.icmvc.ttp.epix.common.model.IdentifierDTO;
import org.emau.icmvc.ttp.epix.common.model.PersonDTO;
import org.emau.icmvc.ttp.epix.persistence.model.Identity;
import org.emau.icmvc.ttp.notification.interfaces.INotificationClient;

@Stateless
public class NotificationSender
{
	private static final Logger logger = LogManager.getLogger(NotificationSender.class);
	private static volatile INotificationClient notificationClient = null;
	static
	{
		try
		{
			notificationClient = InitialContext.doLookup("java:global/notification-client/notification-client-ejb/notification-client-default");
		}
		catch (NamingException e)
		{
			logger.warn("can't send notification - notification client is not present", e);
		}
	}

	private static boolean checkSend()
	{
		if (notificationClient == null)
		{
			try
			{
				notificationClient = InitialContext
						.doLookup("java:global/notification-client/notification-client-ejb/notification-client-default");
			}
			catch (NamingException e)
			{
				logger.warn("can't send notification - notification client is not present", e);
			}
		}
		return notificationClient != null;
	}

	public void sendHandleRequestNotification(String notificationClientID, PersonDTO personDTO, String domainName, String comment)
	{
		logger.debug("sendHandleRequestNotification");
		if (checkSend())
		{
			try
			{
				JSONObject root = new JSONObject();
				JSONObject person = new JSONObject();
				person.put("mpiId", personDTO.getMpiId().getValue());
				root.put("Person", person);
				root.put("EPIX domain", domainName);
				root.put("comment", comment);
				logger.debug("send handle request notification with clientId " + notificationClientID);
				notificationClient.sendNotification("EPIX.RequestMPI", root, notificationClientID);
			}
			catch (ConnectException e)
			{
				logger.warn("can't send handle request notification", e);
			}
			catch (JSONException shouldNotHappen)
			{
				logger.error("unexpected exception while sending handle request notification", shouldNotHappen);
			}
			logger.debug("handle request notification sent");
		}
	}

	public void sendAddIdentifierToPersonNotification(String notificationClientID, String domainName, String mpi, List<IdentifierDTO> localIds)
	{
		logger.debug("sendAddIdentifierToPersonNotification");
		if (checkSend())
		{
			try
			{
				List<String> ids = localIds.stream().map(IdentifierDTO::getValue).collect(Collectors.toList());

				JSONObject root = new JSONObject();
				JSONObject person = new JSONObject();
				person.put("mpiId", mpi);
				person.put("localIds", ids);
				root.put("Person", person);
				root.put("EPIX domain", domainName);
				logger.debug("send add identifier notification with clientId " + notificationClientID);
				notificationClient.sendNotification("EPIX.AddIdentifierToPerson", root, notificationClientID);
			}
			catch (ConnectException e)
			{
				logger.warn("can't send add identifier notification", e);
			}
			catch (JSONException shouldNotHappen)
			{
				logger.error("unexpected exception while sending add identifier notification", shouldNotHappen);
			}
			logger.debug("add identifier notification sent");
		}
	}

	public void sendAddLocalIdentifierToIdentifierNotification(String notificationClientID, String domainName, IdentifierDTO identifierDTO, List<IdentifierDTO> localIds)
	{
		logger.debug("sendAddLocalIdentifierToIdentifierNotification");
		if (checkSend())
		{
			try
			{
				List<String> ids = localIds.stream().map(IdentifierDTO::getValue).collect(Collectors.toList());

				JSONObject root = new JSONObject();
				JSONObject identifier = new JSONObject();
				identifier.put("Identifier", identifierDTO.getValue());
				identifier.put("Identifiers", ids);
				root.put("Local Identifier", identifier);
				root.put("EPIX domain", domainName);
				logger.debug("send add local identifier notification with clientId " + notificationClientID);
				notificationClient.sendNotification("EPIX.AddLocalIdentifierToIdentifier", root, notificationClientID);
			}
			catch (ConnectException e)
			{
				logger.warn("can't send add local identifier notification", e);
			}
			catch (JSONException shouldNotHappen)
			{
				logger.error("unexpected exception while sending add local identifier notification", shouldNotHappen);
			}
			logger.debug("add local identifier notification sent");
		}
	}

	public void sendUpdatePersonNotification(String notificationClientID, PersonDTO personDTO, String domainName, String comment)
	{
		logger.debug("sendUpdatePersonNotification");
		if (checkSend())
		{
			try
			{
				JSONObject root = new JSONObject();
				JSONObject person = new JSONObject();
				person.put("mpiId", personDTO.getMpiId().getValue());
				root.put("Person", person);
				root.put("EPIX domain", domainName);
				root.put("comment", comment);
				logger.debug("send update person notification with clientId " + notificationClientID);
				notificationClient.sendNotification("EPIX.UpdatePerson", root, notificationClientID);
			}
			catch (ConnectException e)
			{
				logger.warn("can't send update person notification", e);
			}
			catch (JSONException shouldNotHappen)
			{
				logger.error("unexpected exception while sending update person notification", shouldNotHappen);
			}
			logger.debug("update person notification sent");
		}
	}

	public void sendAddPersonNotification(String notificationClientID, PersonDTO personDTO, String domainName, String comment)
	{
		logger.debug("sendAddPersonNotification");
		if (checkSend())
		{
			try
			{
				JSONObject root = new JSONObject();
				JSONObject person = new JSONObject();
				person.put("mpiId", personDTO.getMpiId().getValue());
				root.put("Person", person);
				root.put("EPIX domain", domainName);
				root.put("comment", comment);
				logger.debug("send add person notification with clientId " + notificationClientID);
				notificationClient.sendNotification("EPIX.AddPerson", root, notificationClientID);
			}
			catch (ConnectException e)
			{
				logger.warn("can't send add person notification", e);
			}
			catch (JSONException shouldNotHappen)
			{
				logger.error("unexpected exception while sending add person notification", shouldNotHappen);
			}
			logger.debug("add person notification sent");
		}
	}

	public void sendDeactivatePersonNotification(String notificationClientID, String mpi, String domainName)
	{
		logger.debug("sendDeactivatePersonNotification");
		if (checkSend())
		{
			try
			{
				JSONObject root = new JSONObject();
				JSONObject person = new JSONObject();
				person.put("mpiId", mpi);
				root.put("Person", person);
				root.put("EPIX domain", domainName);
				logger.debug("send deactivate person notification with clientId " + notificationClientID);
				notificationClient.sendNotification("EPIX.DeactivatePerson", root, notificationClientID);
			}
			catch (ConnectException e)
			{
				logger.warn("can't send deactivate person notification", e);
			}
			catch (JSONException shouldNotHappen)
			{
				logger.error("unexpected exception while sending deactivate person notification", shouldNotHappen);
			}
			logger.debug("deactivate person notification sent");
		}
	}

	public void sendDeletePersonNotification(String notificationClientID, String mpi, String domainName)
	{
		logger.debug("sendDeletePersonNotification");
		if (checkSend())
		{
			try
			{
				JSONObject root = new JSONObject();
				JSONObject person = new JSONObject();
				person.put("mpiId", mpi);
				root.put("Person", person);
				root.put("EPIX domain", domainName);
				logger.debug("send delete person notification with clientId " + notificationClientID);
				notificationClient.sendNotification("EPIX.DeletePerson", root, notificationClientID);
			}
			catch (ConnectException e)
			{
				logger.warn("can't send delete person notification", e);
			}
			catch (JSONException shouldNotHappen)
			{
				logger.error("unexpected exception while sending delete person notification", shouldNotHappen);
			}
			logger.debug("delete person notification sent");
		}
	}

	public void sendSetReferenceIdentityNotification(String notificationClientID, String mpi, long identityId, String domainName, String comment)
	{
		logger.debug("sendSetReferenceIdentityNotification");
		if (checkSend())
		{
			try
			{
				JSONObject root = new JSONObject();
				JSONObject person = new JSONObject();
				person.put("mpiId", mpi);
				person.put("identityId", identityId);
				root.put("Person", person);
				root.put("EPIX domain", domainName);
				root.put("comment", comment);
				logger.debug("send set reference identity notification with clientId " + notificationClientID);
				notificationClient.sendNotification("EPIX.SetReferenceIdentity", root, notificationClientID);
			}
			catch (ConnectException e)
			{
				logger.warn("can't send set reference identity notification", e);
			}
			catch (JSONException shouldNotHappen)
			{
				logger.error("unexpected exception while sending set reference identity notification", shouldNotHappen);
			}
			logger.debug("set reference identity notification sent");
		}
	}

	public void sendDeactivateIdentityNotification(String notificationClientID, long identityId, String domainName)
	{
		logger.debug("sendDeactivateIdentityNotification");
		if (checkSend())
		{
			try
			{
				JSONObject root = new JSONObject();
				JSONObject person = new JSONObject();
				person.put("identityId", identityId);
				root.put("Person", person);
				root.put("EPIX domain", domainName);
				logger.debug("send deactivate identity notification with clientId " + notificationClientID);
				notificationClient.sendNotification("EPIX.DeactivateIdentity", root, notificationClientID);
			}
			catch (ConnectException e)
			{
				logger.warn("can't send deactivate identity notification", e);
			}
			catch (JSONException shouldNotHappen)
			{
				logger.error("unexpected exception while sending set reference identity notification", shouldNotHappen);
			}
			logger.debug("deactivate identity notification sent");
		}
	}

	public void sendDeleteIdentityNotification(String notificationClientID, long identityId, String domainName)
	{
		logger.debug("sendDeleteIdentityNotification");
		if (checkSend())
		{
			try
			{
				JSONObject root = new JSONObject();
				JSONObject person = new JSONObject();
				person.put("identityId", identityId);
				root.put("Person", person);
				root.put("EPIX domain", domainName);
				logger.debug("send delete identity notification with clientId " + notificationClientID);
				notificationClient.sendNotification("EPIX.DeleteIdentity", root, notificationClientID);
			}
			catch (ConnectException e)
			{
				logger.warn("can't send delete identity notification", e);
			}
			catch (JSONException shouldNotHappen)
			{
				logger.error("unexpected exception while sending delete identity notification", shouldNotHappen);
			}
			logger.debug("delete identity notification sent");
		}
	}

	public void sendAddContactNotification(String notificationClientID, long identityId, String domainName)
	{
		logger.debug("sendAddContactNotification");
		if (checkSend())
		{
			try
			{
				JSONObject root = new JSONObject();
				JSONObject person = new JSONObject();
				person.put("identityId", identityId);
				root.put("Person", person);
				root.put("EPIX domain", domainName);
				logger.debug("send add contact notification with clientId " + notificationClientID);
				notificationClient.sendNotification("EPIX.AddContact", root, notificationClientID);
			}
			catch (ConnectException e)
			{
				logger.warn("can't send add contact notification", e);
			}
			catch (JSONException shouldNotHappen)
			{
				logger.error("unexpected exception while sending add contact notification", shouldNotHappen);
			}
			logger.debug("add contact notification sent");
		}
	}

	public void sendMoveIdentitiesForIdentifierToPersonNotification(String notificationClientID, String domainName, IdentifierDTO identifier, String mpiId, String comment)
	{
		logger.debug("sendMoveIdentitiesForIdentifierToPersonNotification");
		if (checkSend())
		{
			try
			{
				JSONObject root = new JSONObject();
				root.put("identifier", identifier.getValue());
				JSONObject targetPerson = new JSONObject();
				targetPerson.put("mpiId", mpiId);
				root.put("Target Person", targetPerson);
				root.put("EPIX domain", domainName);
				root.put("comment", comment);
				logger.debug("send move identities for identifier to person notification with clientId " + notificationClientID);
				notificationClient.sendNotification("EPIX.MoveIdentitiesForIdentifierToPerson", root, notificationClientID);
			}
			catch (ConnectException e)
			{
				logger.warn("can't send move identities for identifier to person notification", e);
			}
			catch (JSONException shouldNotHappen)
			{
				logger.error("unexpected exception while sending move identities for identifier to person notification", shouldNotHappen);
			}
			logger.debug("move identities for identifier to person notification sent");
		}
	}

	public void sendAssignNotification(String notificationClientID, Identity winningIdentity, Identity identityToMerge, String mpiToMerge,
			String comment)
	{
		logger.debug("sendAssignNotification");
		if (checkSend())
		{
			try
			{
				JSONObject root = new JSONObject();
				JSONObject masterPerson = new JSONObject();
				masterPerson.put("mpiId", winningIdentity.getPerson().getFirstMPI().getValue());
				masterPerson.put("identityId", winningIdentity.getId());
				root.put("Master Person", masterPerson);
				JSONObject slavePerson = new JSONObject();
				slavePerson.put("mpiId", mpiToMerge);
				slavePerson.put("identityId", identityToMerge.getId());
				root.put("Slave Person", slavePerson);
				root.put("EPIX domain", winningIdentity.getPerson().getDomain().getName());
				root.put("comment", comment);
				logger.debug("send assign notification with clientId " + notificationClientID);
				notificationClient.sendNotification("EPIX.AssignIdentity", root, notificationClientID);
			}
			catch (ConnectException e)
			{
				logger.warn("can't send assign notification", e);
			}
			catch (JSONException shouldNotHappen)
			{
				logger.error("unexpected exception while sending assign notification", shouldNotHappen);
			}
			logger.debug("assign notification sent");
		}
	}
}
