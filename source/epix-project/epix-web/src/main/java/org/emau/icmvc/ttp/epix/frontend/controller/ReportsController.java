package org.emau.icmvc.ttp.epix.frontend.controller;

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

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.emau.icmvc.ttp.epix.frontend.controller.common.AbstractEpixWebBean;
import org.emau.icmvc.ttp.epix.frontend.model.IdentityHistoryPairLazyModel;
import org.emau.icmvc.ttp.epix.frontend.util.HistoryHelper;

/**
 * @author Arne Blumentritt
 */
@ViewScoped
@ManagedBean(name = "reportsController")
public class ReportsController extends AbstractEpixWebBean
{
	private IdentityHistoryPairLazyModel identityHistoryPairsLazyModel;

	@ManagedProperty(value = "#{historyHelper}")
	protected HistoryHelper historyHelper;
	
	/**
	 * Method called when loading the page.
	 */
	@PostConstruct
	public void init()
	{
	}

	public String getIdentityReportFilename()
	{
		String fileName = "E-PIX";
		fileName += " " + dateToString(new Date(), "date");
		fileName += " " + getBundle().getString("reports.label.identityHistory");
		fileName += " " + getDomainSelector().getSelectedDomainName();
		return fileName;
	}

	public IdentityHistoryPairLazyModel getIdentityHistoryPairsLazyModel()
	{
		if (identityHistoryPairsLazyModel == null)
		{
			identityHistoryPairsLazyModel = new IdentityHistoryPairLazyModel(managementService, historyHelper, getDomainSelector(),
					getSimpleDateFormat("date").toPattern(), getSimpleDateFormat("datetime").toPattern());
		}

		return identityHistoryPairsLazyModel;
	}

	public void setHistoryHelper(HistoryHelper historyHelper)
	{
		this.historyHelper = historyHelper;
	}
}
