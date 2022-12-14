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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.validation.constraints.Size;

import org.emau.icmvc.ttp.epix.common.exception.DuplicateEntryException;
import org.emau.icmvc.ttp.epix.common.exception.InvalidParameterException;
import org.emau.icmvc.ttp.epix.common.exception.MPIException;
import org.emau.icmvc.ttp.epix.common.exception.UnknownObjectException;
import org.emau.icmvc.ttp.epix.common.model.ContactOutDTO;
import org.emau.icmvc.ttp.epix.common.model.IdentityOutDTO;
import org.emau.icmvc.ttp.epix.common.model.MPIIdentityDTO;
import org.emau.icmvc.ttp.epix.common.model.PersonDTO;
import org.emau.icmvc.ttp.epix.common.model.PossibleMatchDTO;
import org.emau.icmvc.ttp.epix.common.model.config.ReasonDTO;
import org.emau.icmvc.ttp.epix.frontend.controller.common.AbstractEpixWebBean;
import org.emau.icmvc.ttp.epix.frontend.model.PossibleMatchDTOLazyModel;

/**
 * @author Arne Blumentritt
 */
@ViewScoped
@ManagedBean(name = "resolveController")
public class ResolveController extends AbstractEpixWebBean
{
	private String externalPossibleMatchId1;
	private String externalPossibleMatchId2;
	private String externalPossibleMatchIdType;

	@Size(max = 255)
	private String selectedReason;
	public static final String NO_REASON_OPTION = "NO_REASON_OPTION";
	public static final String OTHER_REASON_OPTION = "OTHER_REASON_OPTION";
	private Map<String, ReasonDTO> reasons;
	private String otherReason;
	private Action action;
	private Long assignTargetId;
	private int assignPersonNumber;

	private PossibleMatchDTOLazyModel possibleMatchDTOLazyModel;
	private PossibleMatchDTO selectedPossibleMatch;

	private Boolean sendMergeNotification = true;

	/**
	 * Get possible matches when page loads
	 */
	@PostConstruct
	public void init()
	{
		reasons = new LinkedHashMap<>();
		reasons.put(NO_REASON_OPTION, new ReasonDTO(NO_REASON_OPTION, null));
		try
		{
			for (ReasonDTO reasonDTO : managementService.getDefinedDeduplicationReasons(domainSelector.getSelectedDomainName()))
			{
				reasons.put(reasonDTO.getName(), reasonDTO);
			}
		}
		catch (InvalidParameterException | UnknownObjectException e)
		{
			logMessage(e.getLocalizedMessage(), Severity.ERROR);
		}
		reasons.put(OTHER_REASON_OPTION, new ReasonDTO(OTHER_REASON_OPTION, null));

		loadMatches();
		onNewPossibleMatch();
	}

	/**
	 * Resolve possible match by assignin the looser to the winnning identity
	 */
	public void assignIdentity()
	{
		try
		{
			if (sendMergeNotification)
			{
				serviceWithNotification.assignIdentity(NOTIFICATION_CLIENT_ID, selectedPossibleMatch.getLinkId(), assignTargetId, getReason());
			}
			else
			{
				service.assignIdentity(selectedPossibleMatch.getLinkId(), assignTargetId, getReason());
			}

			Object[] args = { assignPersonNumber, assignPersonNumber == 1 ? 2 : 1 };
			logMessage(new MessageFormat(getBundle().getString("resolve.info.assigned")).format(args), Severity.INFO);

			loadMatches();
		}
		catch (InvalidParameterException e)
		{
			logMessage(getBundle().getString("resolve.error.reason"), Severity.ERROR);
		}
		catch (MPIException e)
		{
			logMPIException(e);
		}
	}

	/**
	 * Resolve possible match by keeping both identities in different persons
	 */
	public void split()
	{
		try
		{
			service.removePossibleMatch(selectedPossibleMatch.getLinkId(), getReason());
			logMessage(getBundle().getString("resolve.info.unlinked"), Severity.INFO);

			loadMatches();
		}
		catch (InvalidParameterException e)
		{
			logMessage(getBundle().getString("resolve.error.reason"), Severity.ERROR);
		}
		catch (MPIException e)
		{
			logMPIException(e);
		}
	}

	public void onNewPossibleMatch()
	{
		externalPossibleMatchId1 = null;
		externalPossibleMatchId2 = null;
		externalPossibleMatchIdType = "MPI";
	}

	public void onAddPossibleMatch()
	{
		try
		{
			PossibleMatchDTO result;
			if (externalPossibleMatchIdType.equals("MPI"))
			{
				result = service.externalPossibleMatchForPerson(domainSelector.getSelectedDomainName(), externalPossibleMatchId1, externalPossibleMatchId2);
			}
			else
			{
				result = service.externalPossibleMatchForIdentity(domainSelector.getSelectedDomainName(), Long.parseLong(externalPossibleMatchId1), Long.parseLong(externalPossibleMatchId2));
			}

			// Get both mpis and identites of the added possible match
			List<MPIIdentityDTO> identities = new ArrayList<>(result.getMatchingMPIIdentities());

			// Tell lazyLoad that data has changed to force reload/recount despite filter/pagination is unchanged
			getPossibleMatchDTOLazyModel().triggerDataChanged();

			// Print both identities in success message
			Object[] args = { identities.get(0).getIdentity().getFirstName() + " " + identities.get(0).getIdentity().getLastName(),
					identities.get(1).getIdentity().getFirstName() + " " + identities.get(1).getIdentity().getLastName() };
			logMessage(new MessageFormat(getBundle().getString("resolve.info.possibleMatchCreated")).format(args), Severity.INFO);

			loadMatches();
		}
		catch (DuplicateEntryException e)
		{
			logMessage(getBundle().getString("resolve.warn.duplicatePossibleMatch." + externalPossibleMatchIdType), Severity.ERROR);
		}
		catch (NumberFormatException e)
		{
			logMessage(getBundle().getString("resolve.warn.wrongFormat"), Severity.ERROR);
		}
		catch (UnknownObjectException e)
		{
			logMessage(getBundle().getString("resolve.warn.possibleMatchIdsNotFound." + externalPossibleMatchIdType), Severity.ERROR);
		}
		catch (InvalidParameterException e)
		{
			logMessage(e.getLocalizedMessage(), Severity.ERROR);
		}
		catch (MPIException e)
		{
			logMPIException(e);
		}
	}

	/**
	 * Get all unique contacts of the person with the given identityId
	 *
	 * @param identityId
	 * 		id to gather contacts for
	 * @return list of contacts of the given identity
	 */
	public List<ContactOutDTO> getContactsFromPersonForIdentity(Long identityId)
	{
		String mpi = null;
		List<ContactOutDTO> contacts = new ArrayList<>();

		if (selectedPossibleMatch == null)
		{
			return contacts;
		}

		for (MPIIdentityDTO identity : selectedPossibleMatch.getMatchingMPIIdentities())
		{
			if (identity.getIdentity().getIdentityId() == identityId)
			{
				mpi = identity.getMpiId().getValue();
				break;
			}
		}

		PersonDTO person;
		try
		{
			person = service.getPersonByMPI(domainSelector.getSelectedDomainName(), mpi);

			List<IdentityOutDTO> identities = new ArrayList<>();
			identities.add(person.getReferenceIdentity());
			identities.addAll(person.getOtherIdentities());

			for (IdentityOutDTO identity : identities)
			{
				contacts.addAll(identity.getContacts());
			}
		}
		catch (InvalidParameterException | UnknownObjectException e)
		{
			logMessage(e);
		}
		return contacts;
	}


	/**
	 * Lazyly load possible matches wrt. filter pattern and table page
	 */
	private void loadMatches()
	{
		getPossibleMatchDTOLazyModel(); // initializes the model if needed

		selectedPossibleMatch = null;
		otherReason = null;
	}

	public String getOtherReason()
	{
		return otherReason;
	}

	public void setOtherReason(String otherReason)
	{
		this.otherReason = otherReason;
	}

	public String getExternalPossibleMatchId1()
	{
		return externalPossibleMatchId1;
	}

	public void setExternalPossibleMatchId1(String externalPossibleMatchId1)
	{
		this.externalPossibleMatchId1 = externalPossibleMatchId1;
	}

	public String getExternalPossibleMatchId2()
	{
		return externalPossibleMatchId2;
	}

	public void setExternalPossibleMatchId2(String externalPossibleMatchId2)
	{
		this.externalPossibleMatchId2 = externalPossibleMatchId2;
	}

	public String getExternalPossibleMatchIdType()
	{
		return externalPossibleMatchIdType;
	}

	public void setExternalPossibleMatchIdType(String externalPossibleMatchIdType)
	{
		this.externalPossibleMatchIdType = externalPossibleMatchIdType;
	}

	public List<String> getExternalPossibleMatchIdTypes()
	{
		return new ArrayList<>(Arrays.asList("MPI", "IdentityId"));
	}

	public Action getAction()
	{
		return action;
	}

	public void setAction(Action action)
	{
		this.action = action;
		selectedReason = NO_REASON_OPTION;
		otherReason = "";
	}

	public void setAssignTargetId(Long assignTargetId, int assignPersonNumber)
	{
		this.assignTargetId = assignTargetId;
		this.assignPersonNumber = assignPersonNumber;
		setAction(Action.ASSIGN);
	}

	public PossibleMatchDTOLazyModel getPossibleMatchDTOLazyModel()
	{
		if (possibleMatchDTOLazyModel == null)
		{
			possibleMatchDTOLazyModel = new PossibleMatchDTOLazyModel(service, domainSelector,
					getSimpleDateFormat("date").toPattern(), getSimpleDateFormat("datetime").toPattern());
		}

		return possibleMatchDTOLazyModel;
	}

	public String getDeduplicationReasonLabel(String reason)
	{
		return getBundle().containsKey("deduplication." + reason) ? getBundle().getString("deduplication." + reason) : reason;
	}

	public String getDeduplicationReasonDescription(String description)
	{
		return getBundle().containsKey("deduplication." + description) ? getBundle().getString("deduplication." + description) : description;
	}

	public PossibleMatchDTO getSelectedPossibleMatch()
	{
		return selectedPossibleMatch;
	}

	public void setSelectedPossibleMatch(PossibleMatchDTO selectedPossibleMatch)
	{
		this.selectedPossibleMatch = selectedPossibleMatch;
	}

	public int getAssignPersonNumber()
	{
		return assignPersonNumber;
	}

	public Boolean getSendMergeNotification()
	{
		return sendMergeNotification;
	}

	public void setSendMergeNotification(Boolean sendMergeNotification)
	{
		this.sendMergeNotification = sendMergeNotification;
	}

	public String getReason()
	{
		return selectedReason.equals(OTHER_REASON_OPTION) ? otherReason : selectedReason;
	}

	public String getSelectedReason()
	{
		return selectedReason;
	}

	public void setSelectedReason(String selectedReason)
	{
		this.selectedReason = selectedReason;
	}

	public String getOtherReasonOption()
	{
		return OTHER_REASON_OPTION;
	}

	public Map<String, ReasonDTO> getReasons()
	{
		return reasons;
	}

	public Set<String> getAvailableDeduplicationReasons()
	{
		return reasons.keySet();
	}

	public enum Action
	{
		SPLIT, ASSIGN
	}
}
