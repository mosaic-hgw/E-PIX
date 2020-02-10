package org.emau.icmvc.ganimed.epix.core.persistence.model;

/*
 * ###license-information-start###
 * E-PIX - Enterprise Patient Identifier Cross-referencing
 * __
 * Copyright (C) 2009 - 2017 The MOSAIC Project - Institut fuer Community Medicine der
 * 							Universitaetsmedizin Greifswald - mosaic-projekt@uni-greifswald.de
 * 							concept and implementation
 * 							c. schack, d.langner, l. geidel
 * 							web client
 * 							a. blumentritt
 * 							g. weiher
 * 							please cite our publications
 * 							http://dx.doi.org/10.3414/ME14-01-0133
 * 							http://dx.doi.org/10.1186/s12967-015-0545-6
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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "personlink")
@NamedQueries({
		@NamedQuery(name = "PersonLink.getAll", query = "SELECT pl FROM PersonLink pl"),
		@NamedQuery(name = "PersonLink.getByPersonID", query = "SELECT pl FROM PersonLink pl WHERE pl.destPerson = :id1 and pl.srcPerson = :id2 or pl.destPerson = :id2 and pl.srcPerson = :id1"),
		@NamedQuery(name = "PersonLink.getByPersonGroups", query = "SELECT pl FROM PersonLink pl WHERE (pl.destPerson.personGroup = :pg1 and pl.srcPerson.personGroup = :pg2) or"
				+ " (pl.destPerson.personGroup = :pg2 and pl.srcPerson.personGroup = :pg1)"),
		@NamedQuery(name = "PersonLink.getForPersonGroup", query = "SELECT pl FROM PersonLink pl WHERE pl.destPerson.personGroup = :pg or pl.srcPerson.personGroup = :pg") })
public class PersonLink implements Serializable {

	private static final long serialVersionUID = -8404003174989615687L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@OneToOne
	private Person srcPerson;

	@OneToOne
	private Person destPerson;

	@Column(name = "threshold")
	private Double threshold = 0.0;

	private String algorithm;

	public PersonLink() {

	}

	public PersonLink(Person srcPerson, Person destPerson) {
		super();
		this.srcPerson = srcPerson;
		this.destPerson = destPerson;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Person getSrcPerson() {
		return srcPerson;
	}

	public void setSrcPerson(Person srcPerson) {
		this.srcPerson = srcPerson;
	}

	public Person getDestPerson() {
		return destPerson;
	}

	public void setDestPerson(Person destPerson) {
		this.destPerson = destPerson;
	}

	public Double getThreshold() {
		return threshold;
	}

	public void setThreshold(Double threshold) {
		this.threshold = threshold;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PersonLink other = (PersonLink) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PersonLink [id=" + id + ", srcPerson=" + srcPerson + ", destPerson=" + destPerson + ", threshold=" + threshold + ", algorithm=" + algorithm + "]";
	}

}
