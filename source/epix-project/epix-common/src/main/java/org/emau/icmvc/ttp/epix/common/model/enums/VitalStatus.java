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

import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author moser
 */
@XmlJavaTypeAdapter(VitalStatusAdapter.class)
public enum VitalStatus
{
	// ATTENTION!
	// DO NOT change the order and add new values at the end,
	// the values will be saved as ordinals in the DB
	UNKNOWN(VitalStatusType.UNKNOWN), // ordinal 0 is used as default in DB scripts
	ALIVE(VitalStatusType.ALIVE),
	DECEASED(VitalStatusType.DEAD);
	// you can add other statuses like
	// DECEASED_BY_CANCER(DEAD), MORTALLY_ILL(ALIVE), NOT_DETERMINABLE(UNKNOWN), ...

	private final VitalStatusType type;

	VitalStatus(VitalStatusType type)
	{
		this.type = type;
	}

	public VitalStatusType getType()
	{
		return type;
	}

	public boolean isDead()
	{
		return getType().isDead();
	}

	public boolean isAlive()
	{
		return getType().isAlive();
	}

	public boolean isUnknown()
	{
		return getType().isUnknown();
	}

	public static VitalStatus nonNull(VitalStatus vs)
	{
		return vs != null ? vs : UNKNOWN;
	}

	public static class Descriptor implements Comparable<Descriptor>
	{
		private final VitalStatus vitalStatus;
		private final Date dateOfDeath;

		public Descriptor(VitalStatus vitalStatus, Date dateOfDeath)
		{
			this.vitalStatus = nonNull(vitalStatus);
			this.dateOfDeath = this.vitalStatus.isDead() ? dateOfDeath : null;
		}

		public VitalStatus getVitalStatus()
		{
			return vitalStatus;
		}

		public Date getDateOfDeath()
		{
			return dateOfDeath;
		}

		@Override
		public boolean equals(Object o)
		{
			if (this == o)
			{
				return true;
			}

			if (o == null || getClass() != o.getClass())
			{
				return false;
			}

			return compareTo((Descriptor) o) == 0;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + vitalStatus.hashCode();
			result = prime * result + (dateOfDeath != null ? dateOfDeath.hashCode() : 0);
			return result;
		}

		@Override
		public int compareTo(Descriptor o)
		{
			if (o == null)
			{
				return 1;
			}

			VitalStatus vs1 = getVitalStatus();
			VitalStatus vs2 = o.getVitalStatus();

			if (vs1.equals(vs2))
			{
				if (vs1.isDead())
				{
					Date d1 = getDateOfDeath();
					Date d2 = o.getDateOfDeath();

					if (d1 == null && d2 == null)
					{
						return 0;
					}
					else if (d1 == null)
					{
						return -1;
					}
					else if (d2 == null)
					{
						return 1;
					}
					return d1.compareTo(d2);
				}
			}
			else if (!vs1.getType().equals(vs2.getType()))
			{
				return -Integer.compare(vs1.getType().ordinal(), vs2.getType().ordinal());
			}
			else
			{
				return Integer.compare(vs1.ordinal(), vs2.ordinal());
			}
			return 0;
		}

		public boolean conflictsWith(Descriptor d)
		{
			return !equals(d);
		}
	}
}
