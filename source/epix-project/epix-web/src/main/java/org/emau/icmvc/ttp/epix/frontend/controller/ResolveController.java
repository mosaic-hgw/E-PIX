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
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
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
import org.emau.icmvc.ttp.epix.common.model.enums.PossibleMatchPriority;
import org.emau.icmvc.ttp.epix.common.utils.PaginationConfig;
import org.emau.icmvc.ttp.epix.frontend.controller.common.AbstractEpixWebBean;
import org.emau.icmvc.ttp.epix.frontend.model.PossibleMatchDTOLazyModel;
import org.icmvc.ttp.web.util.File;
import org.primefaces.PrimeFaces;
import org.primefaces.model.StreamedContent;

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

	private boolean showPostponed = false;

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
			for (ReasonDTO reasonDTO : managementService.getDefinedDeduplicationReasons(getDomainSelector().getSelectedDomainName()))
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

	public void load(String domain, String mpi)
	{
		if (FacesContext.getCurrentInstance().isPostback())
		{
			return;
		}

		if (StringUtils.isNotEmpty(domain))
		{
			getDomainSelector().setSelectedDomain(domain);
		}
		if (StringUtils.isNotEmpty(mpi))
		{
			PrimeFaces.current().executeScript("PF('blockMatches').show()");
			PrimeFaces.current().executeScript("$('#main\\\\:matches\\\\:globalFilter').val('" + mpi + "')");
			PrimeFaces.current().executeScript("PF('matches').filter();");
		}
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
		catch (InvalidParameterException | UnknownObjectException e)
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

	public void onPrioritize(String priority)
	{
		try
		{
			service.prioritizePossibleMatch(selectedPossibleMatch.getLinkId(), PossibleMatchPriority.valueOf(priority));
			logMessage(getBundle().getString("resolve.info.prioritized." + PossibleMatchPriority.valueOf(priority)), Severity.INFO);
			loadMatches();
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

	public void onShowPostponed(boolean showPostponed)
	{
		this.showPostponed = showPostponed;
		loadMatches();
	}

	public StreamedContent onDownloadPossibleMatches() throws InvalidParameterException, UnknownObjectException
	{
		Map<String, List<Object>> valueMap = new LinkedHashMap<>();
		List<String> dates = new ArrayList<>();

		valueMap.put(getBundle().getString("common.person.firstName") + " 1", new ArrayList<>());
		valueMap.put(getBundle().getString("common.person.lastName") + " 1", new ArrayList<>());
		valueMap.put(getBundle().getString("common.person.gender") + " 1", new ArrayList<>());
		valueMap.put(getBundle().getString("common.person.birthDate") + " 1", new ArrayList<>());
		valueMap.put(getBundle().getString("common.person.birthPlace") + " 1", new ArrayList<>());
		valueMap.put(getBundle().getString("common.person.MPI") + " 1", new ArrayList<>());
		valueMap.put(getBundle().getString("common.person.firstName") + " 2", new ArrayList<>());
		valueMap.put(getBundle().getString("common.person.lastName") + " 2", new ArrayList<>());
		valueMap.put(getBundle().getString("common.person.gender") + " 2", new ArrayList<>());
		valueMap.put(getBundle().getString("common.person.birthDate") + " 2", new ArrayList<>());
		valueMap.put(getBundle().getString("common.person.birthPlace") + " 2", new ArrayList<>());
		valueMap.put(getBundle().getString("common.person.MPI") + " 2", new ArrayList<>());

		PaginationConfig pc = PaginationConfig.builder()
				.withPriorityFilter(getPriority())
				.build();

		for (PossibleMatchDTO possibleMatch : service.getPossibleMatchesForDomainFiltered(getDomainSelector().getSelectedDomainName(), pc))
		{
			dates.add(dateToString(possibleMatch.getPossibleMatchCreated(), "datetime"));
			MPIIdentityDTO i1 = (MPIIdentityDTO) possibleMatch.getMatchingMPIIdentities().toArray()[0];
			MPIIdentityDTO i2 = (MPIIdentityDTO) possibleMatch.getMatchingMPIIdentities().toArray()[1];

			valueMap.get(getBundle().getString("common.person.firstName") + " 1").add(i1.getIdentity().getFirstName());
			valueMap.get(getBundle().getString("common.person.lastName") + " 1").add(i1.getIdentity().getLastName());
			valueMap.get(getBundle().getString("common.person.gender") + " 1").add(i1.getIdentity().getGender());
			valueMap.get(getBundle().getString("common.person.birthDate") + " 1").add(dateToString(i1.getIdentity().getBirthDate(), "date"));
			valueMap.get(getBundle().getString("common.person.birthPlace") + " 1").add(i1.getIdentity().getBirthPlace());
			valueMap.get(getBundle().getString("common.person.MPI") + " 1").add(String.valueOf(i1.getMpiId().getValue()));
			valueMap.get(getBundle().getString("common.person.firstName") + " 2").add(i2.getIdentity().getFirstName());
			valueMap.get(getBundle().getString("common.person.lastName") + " 2").add(i2.getIdentity().getFirstName());
			valueMap.get(getBundle().getString("common.person.gender") + " 2").add(i2.getIdentity().getGender());
			valueMap.get(getBundle().getString("common.person.birthDate") + " 2").add(dateToString(i2.getIdentity().getBirthDate(), "date"));
			valueMap.get(getBundle().getString("common.person.birthPlace") + " 2").add(i2.getIdentity().getBirthPlace());
			valueMap.get(getBundle().getString("common.person.MPI") + " 2").add(String.valueOf(i2.getMpiId().getValue()));
		}

		return File.get3DDataAsCSV(valueMap, dates, "Possible Matches " + getPriority().name(), TOOL);
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
				result = service.externalPossibleMatchForPerson(getDomainSelector().getSelectedDomainName(), externalPossibleMatchId1, externalPossibleMatchId2);
			}
			else
			{
				result = service.externalPossibleMatchForIdentity(getDomainSelector().getSelectedDomainName(), Long.parseLong(externalPossibleMatchId1), Long.parseLong(externalPossibleMatchId2));
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
	 *            id to gather contacts for
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
			person = service.getPersonByFirstMPI(getDomainSelector().getSelectedDomainName(), mpi);

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
		// initializes the model if needed
		getPossibleMatchDTOLazyModel().setPriority(getPriority());
		selectedPossibleMatch = null;
		otherReason = null;
	}

	private PossibleMatchPriority getPriority()
	{
		return showPostponed ? PossibleMatchPriority.POSTPONED : PossibleMatchPriority.OPEN;
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
			possibleMatchDTOLazyModel = new PossibleMatchDTOLazyModel(service, getDomainSelector(),
					getSimpleDateFormat("date").toPattern(), getSimpleDateFormat("datetime").toPattern());
		}

		return possibleMatchDTOLazyModel;
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

	public boolean isShowPostponed()
	{
		return showPostponed;
	}
}
