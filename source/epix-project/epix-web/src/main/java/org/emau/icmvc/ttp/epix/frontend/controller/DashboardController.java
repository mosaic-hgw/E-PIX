package org.emau.icmvc.ttp.epix.frontend.controller;

/*-
 * ###license-information-start###
 * gICS - a Generic Informed Consent Service
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

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.emau.icmvc.ttp.auth.TTPNames.Tool;
import org.emau.icmvc.ttp.epix.common.model.StatisticDTO;
import org.emau.icmvc.ttp.epix.common.utils.StatisticKeys;
import org.emau.icmvc.ttp.epix.frontend.controller.common.AbstractEpixWebBean;
import org.emau.icmvc.ttp.epix.service.StatisticManager;
import org.icmvc.ttp.web.controller.ThemeBean;
import org.icmvc.ttp.web.util.Chart;
import org.icmvc.ttp.web.util.File;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.line.LineChartModel;
import org.primefaces.model.charts.pie.PieChartModel;

@ViewScoped
@ManagedBean(name = "dashboardController")
public class DashboardController extends AbstractEpixWebBean
{
	@EJB(lookup = "java:global/epix/epix-ejb/StatisticManagerBean!org.emau.icmvc.ttp.epix.service.StatisticManager")
	private StatisticManager statisticServiceTarget;
	private StatisticManager statisticService;

	@ManagedProperty(value = "#{themeBean}")
	private ThemeBean themeBean;

	private List<StatisticDTO> historyStats;
	private StatisticDTO latestStats;

	private Chart.BarScale personsIdentitiesBarScale = Chart.BarScale.MONTHS_12;
	
	private DashboardDomain domain = DashboardDomain.CURRENT; 

	@PostConstruct
	public void init()
	{
		if (getWebAuthContext().isUsingDomainBasedRolesDisabled(Tool.epix))
		{
			statisticService = statisticServiceTarget;
		}
		else
		{
			statisticService = getWebAuthContext().createUpdateAuthContextProxy(statisticServiceTarget, StatisticManager.class);
		}

		loadStats();
	}

	public void updateStats()
	{
		statisticService.updateStats();
		init();
		logMessage(getCommonBundle().getString("page.dashboard.statistic.updated"), Severity.INFO);
	}

	/* Stats Overview */
	public Map<String, String> getLatestStatsLabels()
	{
		if (DashboardDomain.ALL.equals(domain))
		{
			return getLatestStatsAllDomainsLabels();
		}
		else {
			return getLatestStatsActiveDomainLabels();
		}
	}
	
	public Map<String, String> getLatestStatsAllDomainsLabels()
	{
		Map<String, String> result = new LinkedHashMap<>();
		result.put(StatisticKeys.PERSONS, getBundle().getString("model.person.persons"));
		result.put(StatisticKeys.IDENTITIES, getBundle().getString("model.identity.identities"));
		result.put(StatisticKeys.POSSIBLE_MATCHES_OPEN, getBundle().getString("page.dashboard.possibleMatches.open"));
		result.put(StatisticKeys.POSSIBLE_MATCHES_MERGED, getBundle().getString("page.dashboard.possibleMatches.merged"));
		result.put(StatisticKeys.POSSIBLE_MATCHES_SPLIT, getBundle().getString("page.dashboard.possibleMatches.split"));
		return result;
	}

	public Map<String, String> getLatestStatsActiveDomainLabels()
	{
		Map<String, String> result = new LinkedHashMap<>();
		result.put(new StatisticKeys(StatisticKeys.PERSONS).perDomain(getSelectedDomain().getName()).build(), getBundle().getString("model.person.persons"));
		result.put(new StatisticKeys(StatisticKeys.IDENTITIES).perDomain(getSelectedDomain().getName()).build(), getBundle().getString("model.identity.identities"));
		result.put(new StatisticKeys(StatisticKeys.POSSIBLE_MATCHES_OPEN).perDomain(getSelectedDomain().getName()).build(), getBundle().getString("page.dashboard.possibleMatches.open"));
		result.put(new StatisticKeys(StatisticKeys.POSSIBLE_MATCHES_MERGED).perDomain(getSelectedDomain().getName()).build(), getBundle().getString("page.dashboard.possibleMatches.merged"));
		result.put(new StatisticKeys(StatisticKeys.POSSIBLE_MATCHES_SPLIT).perDomain(getSelectedDomain().getName()).build(), getBundle().getString("page.dashboard.possibleMatches.split"));
		return result;
	}

	/* Persons + Identities Charts */
	public LineChartModel getPersonsIdentitiesHistoryChart()
	{
		List<Object> personsValues = new ArrayList<>();
		List<Object> identitiesValues = new ArrayList<>();
		List<String> dataLabels = new ArrayList<>();
		List<String> dataSetLabels = new ArrayList<>(Arrays.asList(
				getBundle().getString("model.person.persons"),
				getBundle().getString("model.identity.identities")));
		List<String> dataSetColors = new ArrayList<>(Arrays.asList(
				"#26547C",
				"#FFD166"));

		List<List<Object>> valuesLists = new ArrayList<>();
		valuesLists.add(personsValues);
		valuesLists.add(identitiesValues);

		LineChartModel historyChart = Chart.initLineChartModel(valuesLists, dataSetLabels, dataSetColors, dataLabels, themeBean.getDarkMode());

		for (StatisticDTO statisticDTO : Chart.reduceStatistic(historyStats, 50))
		{
			if (DashboardDomain.ALL.equals(domain))
			{
				personsValues.add(statisticDTO.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.PERSONS).build(), 0L));
				identitiesValues.add(statisticDTO.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITIES).build(), 0L));
			}
			else {
				personsValues.add(statisticDTO.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.PERSONS).perDomain(getSelectedDomain().getName()).build(), 0L));
				identitiesValues.add(statisticDTO.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITIES).perDomain(getSelectedDomain().getName()).build(), 0L));
			}
			dataLabels.add(dateToString(statisticDTO.getEntrydate(), "date"));
		}

		return historyChart;
	}

	/* Persons monthly increase bar chart */
	public BarChartModel getPersonsIdentityMonthChart()
	{
		List<Number> personsValues = new ArrayList<>();
		List<Number> identitiesValues = new ArrayList<>();
		List<String> dataLabels = new ArrayList<>();
		List<String> dataSetLabels = new ArrayList<>(Arrays.asList(
				getBundle().getString("model.person.persons"),
				getBundle().getString("model.identity.identities")));
		List<String> dataSetColors = new ArrayList<>(Arrays.asList(
				"#26547C",
				"#FFD166"));

		List<List<Number>> valuesLists = new ArrayList<>();
		valuesLists.add(personsValues);
		valuesLists.add(identitiesValues);

		BarChartModel barChart = Chart.initVerticalBarChart(valuesLists, dataSetLabels, dataSetColors, dataLabels, false, themeBean.getDarkMode());

		// get current month + year
		LocalDate today = LocalDate.now();

		// year*12 + month = currentMonth
		int todayYearMonth = today.getYear() * 12 + today.getMonthValue();

		long previousPersons = 0L;
		long previousIdentities = 0L;
		long currentPersons;
		long currentIdentities;

		// for all 12 previous months + year
		for (int yearMonth = todayYearMonth - 12; yearMonth < todayYearMonth; yearMonth++)
		{
			// month and year
			int year = yearMonth / 12;
			int month = (yearMonth % 12) + 1;

			// get stats of the month
			List<StatisticDTO> monthStats = historyStats.stream().filter(s -> s.getEntrydate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear() == year
					&& s.getEntrydate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getMonthValue() == month).toList();

			// get last stat of the month if any stats for the month exist
			StatisticDTO stat = null;
			if (!monthStats.isEmpty())
			{
				stat = monthStats.get(monthStats.size() - 1);
			}

			if (stat != null)
			{
				if (DashboardDomain.ALL.equals(domain))
				{
					currentPersons = stat.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.PERSONS).build(), 0L);
					currentIdentities = stat.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITIES).build(), 0L);
				}
				else
				{
					currentPersons = stat.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.PERSONS).perDomain(getSelectedDomain().getName()).build(), 0L);
					currentIdentities = stat.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITIES).perDomain(getSelectedDomain().getName()).build(), 0L);
				}

				personsValues.add(currentPersons - previousPersons);
				identitiesValues.add(currentIdentities - previousIdentities);

				previousPersons = currentPersons;
				previousIdentities = currentIdentities;
			}
			else
			{
				personsValues.add(0);
				identitiesValues.add(0);
			}
			dataLabels.add(year + "-" + month);
		}

		return barChart;
	}

	/* Persons yearly increase bar chart */
	public BarChartModel getPersonsIdentityYearChart()
	{
		List<Number> personsValues = new ArrayList<>();
		List<Number> identitiesValues = new ArrayList<>();
		List<String> dataLabels = new ArrayList<>();
		List<String> dataSetLabels = new ArrayList<>(Arrays.asList(
				getBundle().getString("model.person.persons"),
				getBundle().getString("model.identity.identities")));
		List<String> dataSetColors = new ArrayList<>(Arrays.asList(
				"#26547C",
				"#FFD166"));

		List<List<Number>> valuesLists = new ArrayList<>();
		valuesLists.add(personsValues);
		valuesLists.add(identitiesValues);

		BarChartModel barChart = Chart.initVerticalBarChart(valuesLists, dataSetLabels, dataSetColors, dataLabels, false, themeBean.getDarkMode());

		// get current year
		LocalDate today = LocalDate.now();
		int todayYear = today.getYear();

		long previousPersons = 0L;
		long previousIdentities = 0L;
		long currentPersons;
		long currentIdentities;

		// first year
		int firstYear = historyStats.get(0).getEntrydate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear();

		// for all years till today
		for (int year = firstYear; year <= todayYear; year++)
		{
			// get stats of the year
			int streamYear = year;
			List<StatisticDTO> yearStats = historyStats.stream().filter(s -> s.getEntrydate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear() == streamYear).toList();

			// get last stat of the year if any stats for the year exist
			StatisticDTO stat = null;
			if (!yearStats.isEmpty())
			{
				stat = yearStats.get(yearStats.size() - 1);
			}

			if (stat != null)
			{
				if (DashboardDomain.ALL.equals(domain))
				{
					currentPersons = stat.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.PERSONS).build(), 0L);
					currentIdentities = stat.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITIES).build(), 0L);
				}
				else {
					currentPersons = stat.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.PERSONS).perDomain(getSelectedDomain().getName()).build(), 0L);
					currentIdentities = stat.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITIES).perDomain(getSelectedDomain().getName()).build(), 0L);
				}

				personsValues.add(currentPersons - previousPersons);
				identitiesValues.add(currentIdentities - previousIdentities);

				previousPersons = currentPersons;
				previousIdentities = currentIdentities;
			}
			else
			{
				personsValues.add(0);
				identitiesValues.add(0);
			}
			dataLabels.add(String.valueOf(year));
		}

		return barChart;
	}

	/* Matching Charts */
	public LineChartModel getMatchingHistoryChart()
	{
		List<Object> noMatchValues = new ArrayList<>();
		List<Object> possibleMatchValues = new ArrayList<>();
		List<Object> matchValues = new ArrayList<>();
		List<Object> perfectMatchValues = new ArrayList<>();
		List<String> dataLabels = new ArrayList<>();
		List<String> dataSetLabels = new ArrayList<>(Arrays.asList(
				getBundle().getString("page.dashboard.matching.noMatch.short"),
				getBundle().getString("page.dashboard.matching.possibleMatch.short"),
				getBundle().getString("page.dashboard.matching.match.short"),
				getBundle().getString("page.dashboard.matching.perfectMatch.short")));
		List<String> dataSetColors = new ArrayList<>(Arrays.asList(
				"#06D6A0",
				"#FFD166",
				"#EF7548",
				"#EF476F"));

		List<List<Object>> valuesLists = new ArrayList<>();
		valuesLists.add(noMatchValues);
		valuesLists.add(possibleMatchValues);
		valuesLists.add(matchValues);
		valuesLists.add(perfectMatchValues);

		LineChartModel historyChart = Chart.initLineChartModel(valuesLists, dataSetLabels, dataSetColors, dataLabels, themeBean.getDarkMode());

		for (StatisticDTO statisticDTO : Chart.reduceStatistic(historyStats, 50))
		{
			if (DashboardDomain.ALL.equals(domain))
			{
				noMatchValues.add(statisticDTO.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITY_NO_MATCH).build(), 0L));
				possibleMatchValues.add(statisticDTO.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITY_POSSIBLE_MATCH).build(), 0L));
				matchValues.add(statisticDTO.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITY_MATCH).build(), 0L));
				perfectMatchValues.add(statisticDTO.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITY_PERFECT_MATCH).build(), 0L));
			}
			else {
				noMatchValues.add(statisticDTO.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITY_NO_MATCH).perDomain(getSelectedDomain().getName()).build(), 0L));
				possibleMatchValues.add(statisticDTO.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITY_POSSIBLE_MATCH).perDomain(getSelectedDomain().getName()).build(), 0L));
				matchValues.add(statisticDTO.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITY_MATCH).perDomain(getSelectedDomain().getName()).build(), 0L));
				perfectMatchValues.add(statisticDTO.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITY_PERFECT_MATCH).perDomain(getSelectedDomain().getName()).build(), 0L));
			}
			dataLabels.add(dateToString(statisticDTO.getEntrydate(), "date"));
		}

		return historyChart;
	}

	public PieChartModel getMatchingChart(boolean mobile)
	{
		List<Number> values = new ArrayList<>();
		List<String> labels = new ArrayList<>();
		List<String> colors = new ArrayList<>();

		if (DashboardDomain.ALL.equals(domain))
		{
			values.add(latestStats.getMappedStatValue().get(new StatisticKeys(StatisticKeys.IDENTITY_NO_MATCH).build()));
			values.add(latestStats.getMappedStatValue().get(new StatisticKeys(StatisticKeys.IDENTITY_POSSIBLE_MATCH).build()));
			values.add(latestStats.getMappedStatValue().get(new StatisticKeys(StatisticKeys.IDENTITY_MATCH).build()));
			values.add(latestStats.getMappedStatValue().get(new StatisticKeys(StatisticKeys.IDENTITY_PERFECT_MATCH).build()));
		}
		else {
			values.add(latestStats.getMappedStatValue().get(new StatisticKeys(StatisticKeys.IDENTITY_NO_MATCH).perDomain(getSelectedDomain().getName()).build()));
			values.add(latestStats.getMappedStatValue().get(new StatisticKeys(StatisticKeys.IDENTITY_POSSIBLE_MATCH).perDomain(getSelectedDomain().getName()).build()));
			values.add(latestStats.getMappedStatValue().get(new StatisticKeys(StatisticKeys.IDENTITY_MATCH).perDomain(getSelectedDomain().getName()).build()));
			values.add(latestStats.getMappedStatValue().get(new StatisticKeys(StatisticKeys.IDENTITY_PERFECT_MATCH).perDomain(getSelectedDomain().getName()).build()));
		}
		labels.add(getBundle().getString("page.dashboard.matching.noMatch.short"));
		labels.add(getBundle().getString("page.dashboard.matching.possibleMatch.short"));
		labels.add(getBundle().getString("page.dashboard.matching.match.short"));
		labels.add(getBundle().getString("page.dashboard.matching.perfectMatch.short"));
		colors.add("#06D6A0");
		colors.add("#FFD166");
		colors.add("#EF7548");
		colors.add("#EF476F");

		return Chart.initPieChart(values, labels, colors, mobile ? "top" : "left", themeBean.getDarkMode());
	}

	/* Possible Matches Charts */
	public LineChartModel getPossibleMatchesHistoryChart()
	{
		List<Object> openValues = new ArrayList<>();
		List<Object> mergedValues = new ArrayList<>();
		List<Object> splitValues = new ArrayList<>();
		List<String> dataLabels = new ArrayList<>();
		List<String> dataSetLabels = new ArrayList<>(Arrays.asList(
				getBundle().getString("page.dashboard.possibleMatches.open.short"),
				getBundle().getString("page.dashboard.possibleMatches.merged.short"),
				getBundle().getString("page.dashboard.possibleMatches.split.short")));
		List<String> dataSetColors = new ArrayList<>(Arrays.asList(
				"#FFD166",
				"#06D6A0",
				"#26547C"));

		List<List<Object>> valuesLists = new ArrayList<>();
		valuesLists.add(openValues);
		valuesLists.add(mergedValues);
		valuesLists.add(splitValues);

		LineChartModel historyChart = Chart.initLineChartModel(valuesLists, dataSetLabels, dataSetColors, dataLabels, themeBean.getDarkMode());

		for (StatisticDTO statisticDTO : Chart.reduceStatistic(historyStats, 50))
		{
			if (DashboardDomain.ALL.equals(domain))
			{
				openValues.add(statisticDTO.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.POSSIBLE_MATCHES_OPEN).build(), 0L));
				mergedValues.add(statisticDTO.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.POSSIBLE_MATCHES_MERGED).build(), 0L));
				splitValues.add(statisticDTO.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.POSSIBLE_MATCHES_SPLIT).build(), 0L));
			}
			else {
				openValues.add(statisticDTO.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.POSSIBLE_MATCHES_OPEN).perDomain(getSelectedDomain().getName()).build(), 0L));
				mergedValues.add(statisticDTO.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.POSSIBLE_MATCHES_MERGED).perDomain(getSelectedDomain().getName()).build(), 0L));
				splitValues.add(statisticDTO.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.POSSIBLE_MATCHES_SPLIT).perDomain(getSelectedDomain().getName()).build(), 0L));
			}
			dataLabels.add(dateToString(statisticDTO.getEntrydate(), "date"));
		}

		return historyChart;
	}

	public PieChartModel getPossibleMatchesChart(boolean mobile)
	{
		List<Number> values = new ArrayList<>();
		List<String> labels = new ArrayList<>();
		List<String> colors = new ArrayList<>();

		if (DashboardDomain.ALL.equals(domain))
		{
			values.add(latestStats.getMappedStatValue().get(new StatisticKeys(StatisticKeys.POSSIBLE_MATCHES_OPEN).build()));
			values.add(latestStats.getMappedStatValue().get(new StatisticKeys(StatisticKeys.POSSIBLE_MATCHES_MERGED).build()));
			values.add(latestStats.getMappedStatValue().get(new StatisticKeys(StatisticKeys.POSSIBLE_MATCHES_SPLIT).build()));
		}
		else {
			values.add(latestStats.getMappedStatValue().get(new StatisticKeys(StatisticKeys.POSSIBLE_MATCHES_OPEN).perDomain(getSelectedDomain().getName()).build()));
			values.add(latestStats.getMappedStatValue().get(new StatisticKeys(StatisticKeys.POSSIBLE_MATCHES_MERGED).perDomain(getSelectedDomain().getName()).build()));
			values.add(latestStats.getMappedStatValue().get(new StatisticKeys(StatisticKeys.POSSIBLE_MATCHES_SPLIT).perDomain(getSelectedDomain().getName()).build()));
		}
		labels.add(getBundle().getString("page.dashboard.possibleMatches.open.short"));
		labels.add(getBundle().getString("page.dashboard.possibleMatches.merged.short"));
		labels.add(getBundle().getString("page.dashboard.possibleMatches.split.short"));
		colors.add("#FFD166");
		colors.add("#06D6A0");
		colors.add("#26547C");

		return Chart.initPieChart(values, labels, colors, mobile ? "top" : "left", themeBean.getDarkMode());
	}

	/* Ratios */
	public double getPersonsIdentitiesRatio()
	{
		if (DashboardDomain.ALL.equals(domain))
		{
			double identities = latestStats.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITIES).build(), 1L);
			double persons = latestStats.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.PERSONS).build(), 1L);
			return persons > 0 ? identities / persons : 0L;
		}
		else {
			double identities = latestStats.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITIES).perDomain(getSelectedDomain().getName()).build(), 1L);
			double persons = latestStats.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.PERSONS).perDomain(getSelectedDomain().getName()).build(), 1L);
			return persons > 0 ? identities / persons : 0L;
		}
	}

	public double getIdentityPossibleMatchRatio()
	{
		if (DashboardDomain.ALL.equals(domain))
		{
			double identitiesPossibleMatch = latestStats.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITY_POSSIBLE_MATCH).build(), 1L);
			double identities = latestStats.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITIES).build(), 1L);
			return identities > 0 ? identitiesPossibleMatch / identities : 0L;
		}
		else {
			double identitiesPossibleMatch = latestStats.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITY_POSSIBLE_MATCH).perDomain(getSelectedDomain().getName()).build(), 1L);
			double identities = latestStats.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITIES).perDomain(getSelectedDomain().getName()).build(), 1L);
			return identities > 0 ? identitiesPossibleMatch / identities : 0L;
		}
	}

	public double getIdentityMatchRatio()
	{
		if (DashboardDomain.ALL.equals(domain))
		{
			double identitiesMatch = (latestStats.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITY_MATCH).build(), 1L)
					+ latestStats.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITY_PERFECT_MATCH).build(), 1L));
			double identities = latestStats.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITIES).build(), 1L);
			return identities > 0 ? identitiesMatch / identities : 0L;
		}
		else {
			double identitiesMatch = (latestStats.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITY_MATCH).perDomain(getSelectedDomain().getName()).build(), 1L)
					+ latestStats.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITY_PERFECT_MATCH).perDomain(getSelectedDomain().getName()).build(), 1L));
			double identities = latestStats.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITIES).perDomain(getSelectedDomain().getName()).build(), 1L);
			return identities > 0 ? identitiesMatch / identities : 0L;
		}
	}

	/* Downloads */
	public StreamedContent getLatestStatsDownload()
	{
		if (DashboardDomain.ALL.equals(domain))
		{
			return getLatestStatsAllDomainsDownload();
		}
		else
		{
			return getLatestStatsActiveDomainDownload();
		}
	}

	public StreamedContent getLatestStatsAllDomainsDownload()
	{
		Map<String, Number> valueMap = new LinkedHashMap<>();
		for (String key : getLatestStatsAllDomainsLabels().keySet())
		{
			valueMap.put(key, latestStats.getMappedStatValue().getOrDefault(key, 0L));
		}
		return getMapAsCsv(valueMap, latestStats.getEntrydate(), "all_domains stats latest");
	}

	public StreamedContent getHistoryStatsDownload()
	{
		if (DashboardDomain.ALL.equals(domain))
		{
			return getHistoryStatsAllDomainsDownload();
		}
		else
		{
			return getHistoryStatsActiveDomainDownload();
		}
	}

	public StreamedContent getHistoryStatsAllDomainsDownload()
	{
		return getHistoryStats(new ArrayList<>(getLatestStatsAllDomainsLabels().keySet()), "all_domains stats history");
	}

	public StreamedContent getLatestStatsActiveDomainDownload()
	{
		Map<String, Number> valueMap = new LinkedHashMap<>();
		for (String key : getLatestStatsActiveDomainLabels().keySet())
		{
			valueMap.put(key, latestStats.getMappedStatValue().getOrDefault(key, 0L));
		}
		return getMapAsCsv(valueMap, latestStats.getEntrydate(), getSelectedDomain().getName() + " stats latest");
	}

	public StreamedContent getHistoryStatsActiveDomainDownload()
	{
		return getHistoryStats(new ArrayList<>(getLatestStatsActiveDomainLabels().keySet()), getSelectedDomain().getName() + " stats history");
	}

	/* Private methods */
	private void loadStats()
	{
		historyStats = statisticService.getAllStats();
		latestStats = statisticService.getLatestStats();
	}

	private StreamedContent getMapAsCsv(Map<String, Number> map, Date date, String details)
	{
		return File.get2DDataAsCsv(new ArrayList<>(map.values()), new ArrayList<>(map.keySet()), date, details, TOOL);
	}

	private StreamedContent getHistoryStats(List<String> keys, String details)
	{
		// Prepare lists
		List<String> dates = new ArrayList<>();
		Map<String, List<Object>> valueMap = new LinkedHashMap<>();
		for (String key : keys)
		{
			valueMap.put(key, new ArrayList<>());
		}

		// Fill lists
		for (StatisticDTO statisticDTO : historyStats)
		{
			dates.add(dateToString(statisticDTO.getEntrydate(), "date"));
			for (Map.Entry<String, List<Object>> entry : valueMap.entrySet())
			{
				entry.getValue().add(statisticDTO.getMappedStatValue().getOrDefault(entry.getKey(), 0L));
			}
		}

		return File.get3DDataAsCSV(valueMap, dates, details, TOOL);
	}

	public StatisticDTO getLatestStats()
	{
		return latestStats;
	}

	public String getLatestStatsDate()
	{
		if (latestStats.getEntrydate().toInstant().truncatedTo(ChronoUnit.DAYS).equals(new Date().toInstant().truncatedTo(ChronoUnit.DAYS)))
		{
			return getCommonBundle().getString("ui.date.today");
		}
		else
		{
			return dateToString(latestStats.getEntrydate(), "date");
		}
	}

	public String getLatestStatsTime()
	{
		return dateToString(latestStats.getEntrydate(), "time");
	}

	public long getLatestStatsCalculationTime()
	{
		return latestStats.getMappedStatValue().getOrDefault(StatisticKeys.CALCULATION_TIME, -1L);
	}

	/**
	 * LatestStats should have calculation time and stats for current domain
	 *
	 * @return true if init was successful
	 */
	public boolean getInit()
	{
		return latestStats != null && latestStats.getMappedStatValue().containsKey(StatisticKeys.CALCULATION_TIME);
	}

	/**
	 * Sets the managed property to color graphs according to selected theme
	 *
	 * @param themeBean
	 * 		web-common theme bean with information about darkmode/lightmode
	 */
	public void setThemeBean(ThemeBean themeBean)
	{
		this.themeBean = themeBean;
	}

	public Chart.BarScale getPersonsIdentitiesBarScale()
	{
		return personsIdentitiesBarScale;
	}

	public void setPersonsIdentitiesBarScale(Chart.BarScale personsIdentitiesBarScale)
	{
		this.personsIdentitiesBarScale = personsIdentitiesBarScale != null ? personsIdentitiesBarScale : this.personsIdentitiesBarScale;
	}

	public Chart.BarScale[] getAvailableBarScales()
	{
		return Chart.BarScale.values();
	}

	public DashboardDomain getDomain()
	{
		return domain;
	}

	public void setDomain(DashboardDomain domain)
	{
		this.domain = domain;
	}
	
	public DashboardDomain[] getAvailableDomains()
	{
		return DashboardDomain.values();
	}

	public enum DashboardDomain
	{
		ALL, CURRENT
	}
}
