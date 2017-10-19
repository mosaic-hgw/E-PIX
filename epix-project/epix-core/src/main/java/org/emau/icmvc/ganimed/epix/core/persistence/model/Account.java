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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 
 * @author schackc
 * @since 08.11.2010
 */
@Entity
@Table(name="account")
public class Account implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8776881615017749285L;
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@Column(name="username")
	private String userName; 
	
	@Column(name="password")
	private String password;
	
	@Column(name="entry_date")
	private Timestamp entryDate;
	
	@ManyToMany(targetEntity=Role.class)
	@JoinTable(name = "account_role", 			
			   joinColumns = {@JoinColumn(name = "account_id") }, 
			   inverseJoinColumns = { @JoinColumn(name = "role_id") })
	private List<Role> roles;
	
	@ManyToOne(optional=false, targetEntity=Project.class)
	private Project project;
	
	public Account() {
		
	}

	public Account(String userName, String password) {
		this.userName = userName;
		this.password = password;
	}	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public Timestamp getEntryDate() {
		return entryDate;
	}
	
	public void setEntryDate(Timestamp entryDate) {
		this.entryDate = entryDate;
	}
	
	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	
	public List<Role> getRoles() {
		if(roles == null) {
			roles = new ArrayList<Role>();
		}
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((userName == null) ? 0 : userName.hashCode());
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
		Account other = (Account) obj;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Account [id=" + id + ", userName=" + userName + ", password="
				+ password + ", entryDate=" + entryDate + ", roles=" + roles
				+ ", project=" + project + "]";
	}
}
