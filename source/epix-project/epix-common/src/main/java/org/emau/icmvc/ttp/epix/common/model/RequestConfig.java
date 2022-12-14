package org.emau.icmvc.ttp.epix.common.model;

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

import org.emau.icmvc.ttp.epix.common.model.enums.RequestSaveAction;

/**
 * 
 * @author geidell
 *
 */
public class RequestConfig implements Serializable
{
	private static final long serialVersionUID = -6322114780576094176L;
	private RequestSaveAction saveAction = RequestSaveAction.SAVE_ALL;
	private boolean forceReferenceUpdate = false;

	public RequestConfig()
	{
		super();
	}

	public RequestConfig(RequestSaveAction saveAction, boolean forceReferenceUpdate)
	{
		super();
		this.saveAction = saveAction;
		this.forceReferenceUpdate = forceReferenceUpdate;
	}

	public RequestSaveAction getSaveAction()
	{
		return saveAction;
	}

	public void setSaveAction(RequestSaveAction saveAction)
	{
		this.saveAction = saveAction;
	}

	public boolean isForceReferenceUpdate()
	{
		return forceReferenceUpdate;
	}

	public void setForceReferenceUpdate(boolean forceReferenceUpdate)
	{
		this.forceReferenceUpdate = forceReferenceUpdate;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (forceReferenceUpdate ? 1231 : 1237);
		result = prime * result + ((saveAction == null) ? 0 : saveAction.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RequestConfig other = (RequestConfig) obj;
		if (forceReferenceUpdate != other.forceReferenceUpdate)
			return false;
		if (saveAction != other.saveAction)
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "RequestConfig [saveAction=" + saveAction + ", forceReferenceUpdate=" + forceReferenceUpdate + "]";
	}
}
