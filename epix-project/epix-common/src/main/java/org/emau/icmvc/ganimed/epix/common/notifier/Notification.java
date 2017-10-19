package org.emau.icmvc.ganimed.epix.common.notifier;

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


import java.sql.Timestamp;

public class Notification {

	private String message;

	private PRIORITY priority;

	private Object data;

	private Timestamp notificationTime;

	public enum PRIORITY {
		DEBUG, INFO, WARN, ERROR, FATAL
	};

	public Notification() {
		super();
	}

	public Notification(String message, PRIORITY priority) {
		super();
		this.message = message;
		this.priority = priority;
	}

	public Notification(String message, PRIORITY priority, Object data, Timestamp notificationTime) {
		super();
		this.message = message;
		this.priority = priority;
		this.data = data;
		this.notificationTime = notificationTime;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public PRIORITY getPriority() {
		return priority;
	}

	public void setPriority(PRIORITY priority) {
		this.priority = priority;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Timestamp getNotificationTime() {
		return notificationTime;
	};

}
