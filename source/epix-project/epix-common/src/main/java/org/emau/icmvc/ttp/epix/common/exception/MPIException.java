package org.emau.icmvc.ttp.epix.common.exception;

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


import java.util.HashSet;
import java.util.Set;

public class MPIException extends Exception
{
	private static final long serialVersionUID = 45910309123036654L;
	private final MPIErrorCode errorCode;
	private final Set<String> relatedMPIIds = new HashSet<>();

	public MPIException()
	{
		super();
		errorCode = null;
	}

	public MPIException(String message, Throwable cause)
	{
		super(message, cause);
		errorCode = null;
	}

	public MPIException(String message)
	{
		super(message);
		errorCode = null;
	}

	public MPIException(Throwable cause)
	{
		super(cause);
		errorCode = null;
	}

	public MPIException(MPIErrorCode errorCode, Throwable cause)
	{
		super(cause);
		this.errorCode = errorCode;
	}

	public MPIException(MPIErrorCode errorCode, String message)
	{
		super(message);
		this.errorCode = errorCode;
	}

	public MPIException(MPIErrorCode errorCode, String message, Set<String> relatedMPIIds)
	{
		super(message);
		this.errorCode = errorCode;
		this.relatedMPIIds.addAll(relatedMPIIds);
	}

	public MPIException(MPIErrorCode errorCode, String message, Throwable cause)
	{
		super(message, cause);
		this.errorCode = errorCode;
	}

	public MPIErrorCode getErrorCode()
	{
		return errorCode;
	}

	public Set<String> getRelatedMPIIds()
	{
		return relatedMPIIds;
	}
}
