package org.emau.icmvc.ttp.epix.common.exception;

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


/**
 * should be thrown when the given object is not known / found
 * 
 * @author geidell
 *
 */
public class UnknownObjectException extends Exception
{
	private static final long serialVersionUID = -5984869172534598375L;
	private UnknownObjectType objectType;
	private String objectId;

	public UnknownObjectException()
	{
		super();
	}

	public UnknownObjectException(String message, Throwable cause)
	{
		super(message, cause);
		objectId = null;
		objectType = null;
	}

	public UnknownObjectException(String message)
	{
		super(message);
		objectId = null;
		objectType = null;
	}

	public UnknownObjectException(Throwable cause)
	{
		super(cause);
		objectId = null;
		objectType = null;
	}

	public UnknownObjectException(String message, Throwable cause, UnknownObjectType objectType, String objectId)
	{
		super(message, cause);
		this.objectType = objectType;
		this.objectId = objectId;
	}

	public UnknownObjectException(String message, UnknownObjectType objectType, String objectId)
	{
		super(message);
		this.objectType = objectType;
		this.objectId = objectId;
	}

	public UnknownObjectException(Throwable cause, UnknownObjectType objectType, String objectId)
	{
		super(cause);
		this.objectType = objectType;
		this.objectId = objectId;
	}

	public UnknownObjectType getObjectType()
	{
		return objectType;
	}

	public void setObjectType(UnknownObjectType objectType)
	{
		this.objectType = objectType;
	}

	public String getObjectId()
	{
		return objectId;
	}

	public void setObjectId(String objectId)
	{
		this.objectId = objectId;
	}

	@Override
	public String toString()
	{
		return "UnknownObjectException [objectType=" + objectType + ", objectId=" + objectId + "]";
	}
}
