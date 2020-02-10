package org.emau.icmvc.ganimed.ttp.util;

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


/**
 * @author penndorfp
 */

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.faces.validator.ValidatorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesConverter("org.icmvc.jsf.BirthdateConverter")
public class BirthdateValidateConverter implements Converter, Serializable {

	/**
	     * 
	     */
	private static final long serialVersionUID = -8872122724828626492L;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final Boolean allowLenientDates = false;

	private final String datePattern = "yyyy-MM-dd";

	SimpleDateFormat minDate = new SimpleDateFormat(datePattern);

	private void validate(Date value) throws ValidatorException, ParseException {
		Date min = minDate.parse("1900-01-01");
		if (value != null) {
			if (value instanceof Date) {
				Date d = value;

				if (d.after(new Date())) {
					FacesMessage msg = new FacesMessage("Invalid birthdate: value must be smaller than todays date!");
					msg.setSeverity(FacesMessage.SEVERITY_ERROR);
					throw new ValidatorException(msg);
				} else if (d.before(min)) {
					FacesMessage msg = new FacesMessage("Invalid birthdate: value must be greater than 01-01-1900!");
					msg.setSeverity(FacesMessage.SEVERITY_ERROR);
					throw new ValidatorException(msg);
				}
			} else {
				logger.error("given value is not of type Date");
			}
		}
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		SimpleDateFormat sf = new SimpleDateFormat(component.getAttributes().get("pattern").toString());
		try {
			sf.setLenient(allowLenientDates);
			Date d = sf.parse(value);
			validate(d);

			return d;
		} catch (ValidatorException e) {
			throw new ConverterException(e.getFacesMessage());
		} catch (ParseException e) {
			FacesMessage msg = new FacesMessage("invalid birthdate: " + e.getMessage());
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ConverterException(msg);
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		SimpleDateFormat sf = new SimpleDateFormat(component.getAttributes().get("pattern").toString());
		if (value instanceof Date) {
			return sf.format(value);
		} else {
			FacesMessage msg = new FacesMessage("Invalid Date Pattern");
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ConverterException(msg);
		}
	}

}
