package org.emau.icmvc.ttp.epix.common.model.enums;

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

import java.util.Arrays;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author moser
 */
@XmlJavaTypeAdapter(VitalStatusTypeAdapter.class)
public enum VitalStatusType
{
	UNKNOWN(VitalStatus.UNKNOWN), ALIVE(VitalStatus.ALIVE), DEAD(VitalStatus.DECEASED);

	private final VitalStatus defaultStatus;

	VitalStatusType(VitalStatus status)
	{
		defaultStatus = status;
	}

	public VitalStatus getDefaultStatus()
	{
		return defaultStatus;
	}

	public char getSymbol()
	{
		return toString().charAt(0);
	}

	public boolean isDead()
	{
		return VitalStatusType.DEAD.equals(this);
	}

	public boolean isAlive()
	{
		return VitalStatusType.ALIVE.equals(this);
	}

	public boolean isUnknown()
	{
		return VitalStatusType.UNKNOWN.equals(this);
	}

	public static boolean isDead(VitalStatus status)
	{
		return status != null && status.getType().isDead();
	}

	public static boolean isAlive(VitalStatus status)
	{
		return status != null && status.getType().isAlive();
	}

	public static boolean isUnknown(VitalStatus status)
	{
		return status == null || status.getType().isUnknown();
	}

	public static VitalStatusType fromSymbol(String symbol)
	{
		if (symbol == null || symbol.length() != 1)
		{
			throw new IllegalArgumentException("invalid value for vital status type: " + symbol);
		}

		return fromSymbol(symbol.charAt(0));
	}

	public static VitalStatusType fromSymbol(Character symbol)
	{
		if (symbol == null)
		{
			throw new IllegalArgumentException("invalid value for vital status type: " + symbol);
		}

		return fromSymbol(symbol.charValue());
	}

	public static VitalStatusType fromSymbol(char symbol)
	{
		if (symbol == ' ' || symbol == Character.MIN_VALUE)
		{
			return getDefault();
		}

		char compare = Character.toUpperCase(symbol);
		VitalStatusType status = Arrays.stream(values()).filter(s -> s.getSymbol() == compare).findAny().orElse(null);

		if (status != null)
		{
			return status;
		}

		throw new IllegalArgumentException("invalid char for vital status type: " + symbol);
	}

	public static boolean isValidSymbol(char symbol)
	{
		try
		{
			return fromSymbol(symbol) != null;
		}
		catch (IllegalArgumentException e)
		{
			// intentionally empty
		}
		return false;
	}

	public static VitalStatusType getDefault()
	{
		return UNKNOWN;
	}

	public static char getDefaultSymbol()
	{
		return getDefault().getSymbol();
	}

	public static VitalStatusType getType(VitalStatus vitalStatus)
	{
		return vitalStatus != null ? vitalStatus.getType() : UNKNOWN;
	}
}
