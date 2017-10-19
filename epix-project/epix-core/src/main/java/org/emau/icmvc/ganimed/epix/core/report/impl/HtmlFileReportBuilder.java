package org.emau.icmvc.ganimed.epix.core.report.impl;

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


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.emau.icmvc.ganimed.deduplication.model.MatchCriteriaResult;
import org.emau.icmvc.ganimed.deduplication.model.MatchResult;
import org.emau.icmvc.ganimed.deduplication.model.MatchResult.DECISION;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Person;
import org.emau.icmvc.ganimed.epix.core.persistence.model.PersonPreprocessed;
import org.emau.icmvc.ganimed.epix.core.report.AbstractReportBuilder;

/**
 * 
 * @author Christian Schack
 * @since 2011
 *
 */
public class HtmlFileReportBuilder extends AbstractReportBuilder {
	
	private String reportFile = "";
	
	private String reportActivation="";
	
	private boolean enabled = true;	
	
	private StringBuffer body = new StringBuffer();
	
	private StringBuilder report = new StringBuilder();
	
	public String getReportFile() {
		return reportFile;
	}

	public void setReportFile(String reportFile) {
		this.reportFile = reportFile;
	}

	public String getReportActivation() {
		return reportActivation;
	}

	public void setReportActivation(String reportActivation) {
		this.reportActivation = reportActivation;
	}

	@Override
	public void createReport() {
		throw new UnsupportedOperationException("Method not supperted by: "+this.getClass().getName());
	}

	@Override
	public void append(Person p, MatchResult<PersonPreprocessed> mr) {
		body.append(updateReport(p, mr));
		
	}
	
	@Override
	public boolean isReportActivated(){
		if (reportActivation!=null){
			return Boolean.parseBoolean(reportActivation);
		}
		return false;
	}

	@Override
	public void finalizeReport() {
		
		report.append(createHeader());
		report.append(body);
		report.append(createFooter());		
	}

	@Override
	public void storeReport() {
		try {
			if (reportFile != null) {
				File file = new File("/tmp","epix-report.html");
				if (!file.exists()) {
					file.createNewFile();
				}
				
				BufferedOutputStream oStream = new BufferedOutputStream(new FileOutputStream(file));
				oStream.write(report.toString().getBytes());
				oStream.close();
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}				
	}
	
	private StringBuffer createTableHeader() {
		StringBuffer tableHeader = new StringBuffer();		
		tableHeader.append("<tr>").append("\n");
		tableHeader.append("<th align=\"left\" width=\"100px\"></th>").append("\n");
		tableHeader.append("<th><font color=\"white\" size=\"2\">Eingabe</font></th>").append("\n");
		tableHeader.append("<th><font color=\"white\" size=\"2\">Kandidat</font></th>").append("\n");
		tableHeader.append("<th><font color=\"white\" size=\"2\">Wichtung</font></th>").append("\n");
		tableHeader.append("<th><font color=\"white\" size=\"2\">Kriterium</font></th>").append("\n");
		tableHeader.append("<th><font color=\"white\" size=\"2\">P</font></th>").append("\n");
		tableHeader.append("<th><font color=\"white\" size=\"2\">Entscheidung</font></th>").append("\n");
		return tableHeader;
	}
	
	protected StringBuffer createHeader() {
		StringBuffer header =  new StringBuffer();
	    header.append("<html>").append("\n");
	    header.append("	<head>").append("\n");
	    header.append("		<title>").append("\n");
	    header.append("			EPIX").append("\n");
	    header.append("		</title>").append("\n");
	    header.append("	</head>").append("\n");
	    header.append("	<body>").append("\n");
	    header.append("		<div>").append("\n");
	    header.append("			<img src=\"Banner_EPIX_VI.jpg\" width=\"1147px\" height=\"\" alt=\"\" border=\"0\" style=\"padding-left:5.1%\"/>").append("\n");
	    header.append("		</div>").append("\n");	    
	    return header;
	}
	
	private StringBuffer updateReport(Person p, MatchResult<PersonPreprocessed> mr) {
		StringBuffer newEntry = new StringBuffer();
						
		newEntry.append("<div style=\"position:relative; top:50px; left:5%; bottom:50px; width:1090px; background-color:#77B9D9; text-align:left; padding:20px; border:thin solid white; margin:25px;\">").append("\n");
		newEntry.append("<table border=\"0\" cellpadding=\"1\" cellspacing=\"1\" width=\"100%\">").append("\n");
		newEntry.append(createTableHeader());
		List<MatchCriteriaResult> matches = mr.getMatches();
		List<MatchCriteriaResult> noMatches = mr.getNoMatches();
						
		for (MatchCriteriaResult mrc : matches) {
			newEntry.append(createTableEntry(mrc, "white"));
			newEntry.append("<td><b>MATCH</b></td>").append("\n");
		}
		
		for (MatchCriteriaResult mrc : noMatches) {
			newEntry.append(createTableEntry(mrc, "red"));
			newEntry.append("<td><b>NOMATCH</b></td>").append("\n");
		}
		
		newEntry.append("</table>").append("\n");
		newEntry.append("</div>").append("\n");
		newEntry.append(createResultEntry(mr, p));
						
		return newEntry;
	}
	
	protected StringBuffer createFooter() {
		StringBuffer sb = new StringBuffer();
		sb.append("<div style=\"position:relative; top:50px; left:5%; bottom:50px; width:1096px; background-color:#365371; text-align:left; padding:17px; border:thin solid white; margin:25px;\">").append("\n");
		sb.append("<center><font size=\"2\" color=\"white\">Copyright (c) 2011</font>").append("\n");
		sb.append("</center></div>").append("\n");
		sb.append("</body>").append("\n");
		sb.append("</html>").append("\n");
		return sb;
	}

	private StringBuffer createTableEntry(MatchCriteriaResult mrc, String color) {
		StringBuffer newEntry = new StringBuffer();
		newEntry.append("<tr bgcolor=\""+color+"\">").append("\n");			
		newEntry.append("<td align=\"left\"><font color=\"#365371\" size=\"2\">&nbsp;"+mrc.getCriteria()+"</font></td>").append("\n");
		newEntry.append("<td>"+mrc.getToMatchValue()+"</td>").append("\n");
		newEntry.append("<td>"+mrc.getCandidateValue()+"</td>").append("\n");
		newEntry.append("<td>"+mrc.getWeight()+"</td>").append("\n");
		newEntry.append("<td>"+mrc.getProbability()+"</td>").append("\n");		
		return newEntry;
	}
	
	private StringBuffer createResultEntry(MatchResult<PersonPreprocessed> mr, Person person) {
		StringBuffer newEntry = new StringBuffer();
		
		String color = "white";
		String decision = "NOMATCH";
		if (mr.getDecision().equals(DECISION.MATCH)) {
			color="green";
			decision = "MATCH";
		} else if (mr.getDecision().equals(DECISION.CRITICAL)) {
			color="red";
			decision="CRITICAL";
		}
		
		newEntry.append("<div style=\"position:relative; top:50px; left:5%; bottom:50px; width:1090px; background-color:#649DBB; text-align:left; padding:20px; border:thin solid white; margin:25px; \">").append("\n");
		newEntry.append("<table border=\"0\" cellpadding=\"1\" cellspacing=\"1\" width=\"100%\">").append("\n");		
		newEntry.append("	<tr>").append("\n");
		newEntry.append("      <th><font color=\"white\" size=\"2\">Verh&auml;ltnis</font></th>").append("\n");
		newEntry.append("	   <th><font color=\"white\" size=\"2\">Entscheidung</font></th>").append("\n");
		newEntry.append("	</tr>").append("\n");
		newEntry.append("	<tr >").append("\n");
		newEntry.append("		<td bgcolor=\""+color+"\">"+mr.getRatio()+"</td>").append("\n");
		newEntry.append("		<td bgcolor=\""+color+"\">"+decision+"</td>").append("\n");
		newEntry.append("	</tr>").append("\n");
		newEntry.append("</table>").append("\n");
		newEntry.append("</div>").append("\n");

		return newEntry;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
}
