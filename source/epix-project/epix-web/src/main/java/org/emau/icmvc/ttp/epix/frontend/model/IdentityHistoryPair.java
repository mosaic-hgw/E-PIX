package org.emau.icmvc.ttp.epix.frontend.model;

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


import java.io.Serializable;

import org.emau.icmvc.ttp.epix.common.model.IdentityHistoryDTO;

public class IdentityHistoryPair implements Serializable
{
	private static final long serialVersionUID = -7279627369233296602L;

	private IdentityHistoryDTO newIdentity = new IdentityHistoryDTO();
	private IdentityHistoryDTO oldIdentity = new IdentityHistoryDTO();
	private Double treshold = null;
	private String explanation = null;
	private String newMpi = null;

	public IdentityHistoryPair()
	{
		newIdentity = new IdentityHistoryDTO();
	}

	public IdentityHistoryPair(IdentityHistoryDTO newIdentity)
	{
		this.newIdentity = newIdentity;
	}

	public IdentityHistoryPair(IdentityHistoryDTO newIdentity, IdentityHistoryDTO oldIdentity, Double treshold, String explanation)
	{
		this.newIdentity = newIdentity;
		this.oldIdentity = oldIdentity;
		this.treshold = treshold;
		this.explanation = explanation;
	}

	public IdentityHistoryDTO getNewIdentity()
	{
		return newIdentity;
	}

	public void setNewIdentity(IdentityHistoryDTO newIdentity)
	{
		this.newIdentity = newIdentity;
	}

	public IdentityHistoryDTO getOldIdentity()
	{
		return oldIdentity;
	}

	public void setOldIdentity(IdentityHistoryDTO oldIdentity)
	{
		this.oldIdentity = oldIdentity;
	}

	public Double getTreshold()
	{
		return treshold;
	}

	public void setTreshold(Double treshold)
	{
		this.treshold = treshold;
	}

	public String getExplanation()
	{
		return explanation;
	}

	public void setExplanation(String explanation)
	{
		this.explanation = explanation;
	}

	public String getNewMpi()
	{
		return newMpi;
	}

	public void setNewMpi(String newMpi)
	{
		this.newMpi = newMpi;
	}
}
