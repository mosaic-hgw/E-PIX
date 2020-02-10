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
@Table(name = "person_group")
@NamedQueries({
	@NamedQuery(name = "PersonGroup.findByIdentifierAndDomain", query = "SELECT pg FROM PersonGroup pg join pg.firstMpi i WHERE i.value = :value AND i.domain.oid = :oid AND pg.locked = false"),
	@NamedQuery(name = "PersonGroup.findById", query = "SELECT pg FROM PersonGroup pg WHERE pg.id = :id")})
public class PersonGroup implements Serializable {

	private static final long serialVersionUID = 6506091449327531111L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@OneToOne
	private Person referencePerson;

	@Column(columnDefinition = "BIT", length = 1)
	private boolean locked;

	@OneToOne
	private Identifier firstMpi;

	public PersonGroup() {
	}

	public PersonGroup(Person referencePerson) {
		this.referencePerson = referencePerson;
		locked = false;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public Person getReferencePerson() {
		return referencePerson;
	}

	public void setReferencePerson(Person referencePerson) {
		this.referencePerson = referencePerson;
	}

	public Identifier getFirstMpi() {
		return firstMpi;
	}

	public void setFirstMpi(Identifier firstMpi) {
		this.firstMpi = firstMpi;
	}

}
