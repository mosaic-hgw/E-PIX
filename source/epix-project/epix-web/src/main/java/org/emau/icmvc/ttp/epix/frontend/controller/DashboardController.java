package org.emau.icmvc.ttp.epix.frontend.controller;

/*-
 * ###license-information-start###
 * gICS - a Generic Informed Consent Service
 * __
 * Copyright (C) 2009 - 2025 Trusted Third Party of the University Medicine Greifswald
 * 							kontakt-ths@uni-greifswald.de
 * 
 * 							concept and implementation
 * 							l.geidel,c.schack, d.langner, g.koetzschke
 * 
 * 							web client
 * 							a.blumentritt, f.m. moser
 * 
 * 							docker
 * 							r.schuldt, f.m. moser
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

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.annotation.ManagedProperty;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.emau.icmvc.ttp.epix.common.model.StatisticDTO;
import org.emau.icmvc.ttp.epix.common.utils.StatisticKeys;
import org.emau.icmvc.ttp.epix.frontend.controller.common.AbstractEpixWebBean;
import org.emau.icmvc.ttp.epix.frontend.controller.component.DomainSelector;
import org.icmvc.ttp.web.controller.ThemeBean;
import org.icmvc.ttp.web.util.Chart;
import org.icmvc.ttp.web.util.File;
import org.primefaces.model.StreamedContent;
import software.xdev.chartjs.model.charts.BarChart;
import software.xdev.chartjs.model.charts.LineChart;
import software.xdev.chartjs.model.charts.PieChart;

@SessionScoped
@Named("dashboardController")
public class DashboardController extends AbstractEpixWebBean implements Serializable
{
	@Serial
	private static final long serialVersionUID = 3170474590648204760L;
	@Inject
	@ManagedProperty(value = "#{themeBean}")
	private ThemeBean themeBean;

	private List<StatisticDTO> rangeStats;
	private StatisticDTO stats;
	private boolean hasStats;

	private Date rangeStartDate;
	private Date rangeEndDate;
	private Date statsDate;
	private Date statsMinDate;
	private Date statsMaxDate;

	private Chart.BarScale personsIdentitiesBarScale = Chart.BarScale.MONTHS_12;

	private DashboardDomain domain = DashboardDomain.CURRENT;

	private boolean rangeStatsLoaded = false;
	private String domainName;
	@Named("domainSelector") @Inject private DomainSelector domainSelector;

	@PostConstruct
	public void init()
	{
		statsDate = null;
		statsMinDate = null;
		statsMaxDate = null;
		rangeStartDate = null;
		rangeEndDate = null;
		rangeStatsLoaded = false;
		domainName = domainSelector.getSelectedDomainName();
		loadStats();
	}

	public void checkDomainChange()
	{
		if (FacesContext.getCurrentInstance().isPostback())
		{
			return;
		}
		if (!domainSelector.getSelectedDomainName().equals(domainName))
		{
			init();
		}
	}

	public void updateStats()
	{
		getStatisticService().updateStats();
		init();
		logMessage(getCommonBundle().getString("page.dashboard.statistic.updated"), Severity.INFO);
	}

	public void loadRangeStats()
	{
		rangeStats = getStatisticService().getStatsFromTo(rangeStartDate, rangeEndDate);
		rangeStatsLoaded = true;
	}

	public void onDateChange()
	{
		Calendar statsCal = Calendar.getInstance();
		statsCal.setTime(statsDate);
		statsCal.set(Calendar.HOUR_OF_DAY, 23);
		statsCal.set(Calendar.MINUTE, 59);
		statsCal.set(Calendar.SECOND, 59);
		statsDate = statsCal.getTime();

		Calendar rangeEndCal = Calendar.getInstance();
		rangeEndCal.setTime(rangeEndDate);
		rangeEndCal.set(Calendar.HOUR_OF_DAY, 23);
		rangeEndCal.set(Calendar.MINUTE, 59);
		rangeEndCal.set(Calendar.SECOND, 59);
		rangeEndDate = rangeEndCal.getTime();
		rangeStatsLoaded = false;
		loadStats();
	}

	/* Stats Overview */
	public Map<String, String> getLatestStatsLabels()
	{
		if (DashboardDomain.ALL.equals(domain))
		{
			return getLatestStatsAllDomainsLabels();
		}
		else
		{
			return getLatestStatsActiveDomainLabels();
		}
	}

	public Map<String, String> getLatestStatsAllDomainsLabels()
	{
		Map<String, String> result = new LinkedHashMap<>();
		result.put(StatisticKeys.PERSONS, getBundle().getString("model.person.persons"));
		result.put(StatisticKeys.IDENTITIES, getBundle().getString("model.identity.identities"));
		result.put(StatisticKeys.POSSIBLE_MATCHES_OPEN, getBundle().getString("page.dashboard.possibleMatches.open"));
		result.put(StatisticKeys.POSSIBLE_MATCHES_POSTPONED, getBundle().getString("page.dashboard.possibleMatches.postponed"));
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
		result.put(new StatisticKeys(StatisticKeys.POSSIBLE_MATCHES_POSTPONED).perDomain(getSelectedDomain().getName()).build(), getBundle().getString("page.dashboard.possibleMatches.postponed"));
		result.put(new StatisticKeys(StatisticKeys.POSSIBLE_MATCHES_MERGED).perDomain(getSelectedDomain().getName()).build(), getBundle().getString("page.dashboard.possibleMatches.merged"));
		result.put(new StatisticKeys(StatisticKeys.POSSIBLE_MATCHES_SPLIT).perDomain(getSelectedDomain().getName()).build(), getBundle().getString("page.dashboard.possibleMatches.split"));
		return result;
	}

	/* Persons + Identities Charts */
	public LineChart getPersonsIdentitiesHistoryChart()
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

		for (StatisticDTO statisticDTO : Chart.reduceStatistic(rangeStats, 50))
		{
			if (DashboardDomain.ALL.equals(domain))
			{
				personsValues.add(statisticDTO.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.PERSONS).build(), 0L));
				identitiesValues.add(statisticDTO.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITIES).build(), 0L));
			}
			else
			{
				personsValues.add(statisticDTO.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.PERSONS).perDomain(getSelectedDomain().getName()).build(), 0L));
				identitiesValues.add(statisticDTO.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITIES).perDomain(getSelectedDomain().getName()).build(), 0L));
			}
			dataLabels.add(dateToString(statisticDTO.getEntrydate(), "date"));
		}

		return Chart.initLineChart(valuesLists, dataSetLabels, dataSetColors, dataLabels, themeBean.getDarkMode());
	}

	public BarChart getMonthChart(List<String> dataSetLabels, List<String> dataSetColors, List<String> dataSetTypes)
	{
		List<String> dataLabels = new ArrayList<>();

		Map<String, List<Number>> allValues = new LinkedHashMap<>();
		Map<String, Long> previousValues = new LinkedHashMap<>();
		Map<String, Long> currentValues = new LinkedHashMap<>();

		for (String type : dataSetTypes)
		{
			allValues.put(type, new ArrayList<>());
			previousValues.put(type, 0L);
			currentValues.put(type, 0L);
		}

		// get start date
		LocalDate start = rangeStartDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		// year*12 + month = startMonths
		int startMonths = start.getYear() * 12 + start.getMonthValue();

		// get end date
		LocalDate end = rangeEndDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		// year*12 + month = endMonths
		int endMonths = end.getYear() * 12 + end.getMonthValue();

		// for all months from start to end
		for (int months = startMonths; months < endMonths; months++)
		{
			// month and year
			int year = months / 12;
			int month = months % 12 + 1;

			// get stats of the month
			List<StatisticDTO> monthStats = rangeStats.stream().filter(s -> s.getEntrydate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear() == year
					&& s.getEntrydate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getMonthValue() == month).toList();

			// get last stat of the month if any stats for the month exist
			StatisticDTO stat = null;
			if (!monthStats.isEmpty())
			{
				stat = monthStats.getLast();
			}

			if (stat != null)
			{
				for (String type : dataSetTypes)
				{
					if (DashboardDomain.ALL.equals(domain))
					{
						currentValues.put(type, stat.getMappedStatValue().getOrDefault(new StatisticKeys(type).build(), 0L));
					}
					else
					{
						currentValues.put(type, stat.getMappedStatValue().getOrDefault(new StatisticKeys(type).perDomain(getSelectedDomain().getName()).build(), 0L));
					}
					allValues.get(type).add(currentValues.get(type) - previousValues.get(type));
					previousValues.put(type, currentValues.get(type));
				}
			}
			else
			{
				for (String type : dataSetTypes)
				{
					allValues.get(type).add(0L);
				}
			}
			dataLabels.add(year + "-" + month);
		}
		return Chart.initBarChart(allValues.values().stream().toList(), dataSetLabels, dataSetColors, dataLabels, false, themeBean.getDarkMode(), true);
	}

	public BarChart getYearChart(List<String> dataSetLabels, List<String> dataSetColors, List<String> dataSetTypes)
	{
		List<String> dataLabels = new ArrayList<>();

		Map<String, List<Number>> allValues = new LinkedHashMap<>();
		Map<String, Long> previousValues = new LinkedHashMap<>();
		Map<String, Long> currentValues = new LinkedHashMap<>();

		for (String type : dataSetTypes)
		{
			allValues.put(type, new ArrayList<>());
			previousValues.put(type, 0L);
			currentValues.put(type, 0L);
		}

		// get start date
		LocalDate start = rangeStartDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		int startYear = start.getYear();

		// get end date
		LocalDate end = rangeEndDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		// year*12 + month = endMonths
		int endYear = end.getYear();

		// for all 12 previous months + year
		for (int year = startYear; year <= endYear; year++)
		{
			// get stats of the year
			int streamYear = year;

			// get stats of the month
			List<StatisticDTO> yearStats = rangeStats.stream().filter(s -> s.getEntrydate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear() == streamYear).toList();

			// get last stat of the year if any stats for the year exist
			StatisticDTO stat = null;
			if (!yearStats.isEmpty())
			{
				stat = yearStats.getLast();
			}

			if (stat != null)
			{
				for (String type : dataSetTypes)
				{
					if (DashboardDomain.ALL.equals(domain))
					{
						currentValues.put(type, stat.getMappedStatValue().getOrDefault(new StatisticKeys(type).build(), 0L));
					}
					else
					{
						currentValues.put(type, stat.getMappedStatValue().getOrDefault(new StatisticKeys(type).perDomain(getSelectedDomain().getName()).build(), 0L));
					}
					allValues.get(type).add(currentValues.get(type) - previousValues.get(type));
					previousValues.put(type, currentValues.get(type));
				}
			}
			else
			{
				for (String type : dataSetTypes)
				{
					allValues.get(type).add(0L);
				}
			}
			dataLabels.add(String.valueOf(year));
		}
		return Chart.initBarChart(allValues.values().stream().toList(), dataSetLabels, dataSetColors, dataLabels, false, themeBean.getDarkMode(), true);
	}

	/* Persons monthly increase bar chart */
	public BarChart getPersonsIdentityMonthChart()
	{
		List<String> dataSetLabels = new ArrayList<>(Arrays.asList(
				getBundle().getString("model.person.persons"),
				getBundle().getString("model.identity.identities")));
		List<String> dataSetColors = new ArrayList<>(Arrays.asList(
				"#26547C",
				"#FFD166"));
		List<String> dataSetTypes = new ArrayList<>(Arrays.asList(
				StatisticKeys.PERSONS,
				StatisticKeys.IDENTITIES));

		return getMonthChart(dataSetLabels, dataSetColors, dataSetTypes);
	}

	/* Persons yearly increase bar chart */
	public BarChart getPersonsIdentityYearChart()
	{
		List<String> dataSetLabels = new ArrayList<>(Arrays.asList(
				getBundle().getString("model.person.persons"),
				getBundle().getString("model.identity.identities")));
		List<String> dataSetColors = new ArrayList<>(Arrays.asList(
				"#26547C",
				"#FFD166"));
		List<String> dataSetTypes = new ArrayList<>(Arrays.asList(
				StatisticKeys.PERSONS,
				StatisticKeys.IDENTITIES));

		return getYearChart(dataSetLabels, dataSetColors, dataSetTypes);
	}

	/* Matching Charts */
	public LineChart getMatchingHistoryChart()
	{
		List<Number> noMatchValues = new ArrayList<>();
		List<Number> possibleMatchValues = new ArrayList<>();
		List<Number> matchValues = new ArrayList<>();
		List<Number> perfectMatchValues = new ArrayList<>();
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

		List<List<Number>> valuesLists = new ArrayList<>();
		valuesLists.add(noMatchValues);
		valuesLists.add(possibleMatchValues);
		valuesLists.add(matchValues);
		valuesLists.add(perfectMatchValues);

		for (StatisticDTO statisticDTO : Chart.reduceStatistic(rangeStats, 50))
		{
			if (DashboardDomain.ALL.equals(domain))
			{
				noMatchValues.add(statisticDTO.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITY_NO_MATCH).build(), 0L));
				possibleMatchValues.add(statisticDTO.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITY_POSSIBLE_MATCH).build(), 0L));
				matchValues.add(statisticDTO.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITY_MATCH).build(), 0L));
				perfectMatchValues.add(statisticDTO.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITY_PERFECT_MATCH).build(), 0L));
			}
			else
			{
				noMatchValues.add(statisticDTO.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITY_NO_MATCH).perDomain(getSelectedDomain().getName()).build(), 0L));
				possibleMatchValues.add(statisticDTO.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITY_POSSIBLE_MATCH).perDomain(getSelectedDomain().getName()).build(), 0L));
				matchValues.add(statisticDTO.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITY_MATCH).perDomain(getSelectedDomain().getName()).build(), 0L));
				perfectMatchValues.add(statisticDTO.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITY_PERFECT_MATCH).perDomain(getSelectedDomain().getName()).build(), 0L));
			}
			dataLabels.add(dateToString(statisticDTO.getEntrydate(), "date"));
		}

		return Chart.initLineChart(valuesLists, dataSetLabels, dataSetColors, dataLabels, themeBean.getDarkMode());
	}

	public PieChart getMatchingChart(boolean mobile)
	{
		List<Number> values = new ArrayList<>();
		List<String> labels = new ArrayList<>();
		List<String> colors = new ArrayList<>();

		if (DashboardDomain.ALL.equals(domain))
		{
			values.add(stats.getMappedStatValue().get(new StatisticKeys(StatisticKeys.IDENTITY_NO_MATCH).build()));
			values.add(stats.getMappedStatValue().get(new StatisticKeys(StatisticKeys.IDENTITY_POSSIBLE_MATCH).build()));
			values.add(stats.getMappedStatValue().get(new StatisticKeys(StatisticKeys.IDENTITY_MATCH).build()));
			values.add(stats.getMappedStatValue().get(new StatisticKeys(StatisticKeys.IDENTITY_PERFECT_MATCH).build()));
		}
		else
		{
			values.add(stats.getMappedStatValue().get(new StatisticKeys(StatisticKeys.IDENTITY_NO_MATCH).perDomain(getSelectedDomain().getName()).build()));
			values.add(stats.getMappedStatValue().get(new StatisticKeys(StatisticKeys.IDENTITY_POSSIBLE_MATCH).perDomain(getSelectedDomain().getName()).build()));
			values.add(stats.getMappedStatValue().get(new StatisticKeys(StatisticKeys.IDENTITY_MATCH).perDomain(getSelectedDomain().getName()).build()));
			values.add(stats.getMappedStatValue().get(new StatisticKeys(StatisticKeys.IDENTITY_PERFECT_MATCH).perDomain(getSelectedDomain().getName()).build()));
		}
		labels.add(getBundle().getString("page.dashboard.matching.noMatch.short"));
		labels.add(getBundle().getString("page.dashboard.matching.possibleMatch.short"));
		labels.add(getBundle().getString("page.dashboard.matching.match.short"));
		labels.add(getBundle().getString("page.dashboard.matching.perfectMatch.short"));
		colors.add("#06D6A0");
		colors.add("#FFD166");
		colors.add("#EF7548");
		colors.add("#EF476F");

		return Chart.initPieChart(values, labels, colors, mobile ? Chart.LegendPosition.TOP : Chart.LegendPosition.LEFT, themeBean.getDarkMode());
	}

	/* Possible Matches Charts */
	public LineChart getPossibleMatchesHistoryChart()
	{
		List<Number> openValues = new ArrayList<>();
		List<Number> mergedValues = new ArrayList<>();
		List<Number> splitValues = new ArrayList<>();
		List<Number> postponedValues = new ArrayList<>();
		List<String> dataLabels = new ArrayList<>();
		List<String> dataSetLabels = new ArrayList<>(Arrays.asList(
				getBundle().getString("page.dashboard.possibleMatches.open.short"),
				getBundle().getString("page.dashboard.possibleMatches.merged.short"),
				getBundle().getString("page.dashboard.possibleMatches.split.short"),
				getBundle().getString("page.dashboard.possibleMatches.postponed.short")));
		List<String> dataSetColors = new ArrayList<>(Arrays.asList(
				"#FFD166",
				"#06D6A0",
				"#26547C",
				"#7d7d7d"));

		List<List<Number>> valuesLists = new ArrayList<>();
		valuesLists.add(openValues);
		valuesLists.add(mergedValues);
		valuesLists.add(splitValues);
		valuesLists.add(postponedValues);

		for (StatisticDTO statisticDTO : Chart.reduceStatistic(rangeStats, 50))
		{
			if (DashboardDomain.ALL.equals(domain))
			{
				openValues.add(statisticDTO.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.POSSIBLE_MATCHES_OPEN).build(), 0L));
				mergedValues.add(statisticDTO.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.POSSIBLE_MATCHES_MERGED).build(), 0L));
				splitValues.add(statisticDTO.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.POSSIBLE_MATCHES_SPLIT).build(), 0L));
				postponedValues.add(statisticDTO.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.POSSIBLE_MATCHES_POSTPONED).build(), 0L));
			}
			else
			{
				openValues.add(statisticDTO.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.POSSIBLE_MATCHES_OPEN).perDomain(getSelectedDomain().getName()).build(), 0L));
				mergedValues.add(statisticDTO.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.POSSIBLE_MATCHES_MERGED).perDomain(getSelectedDomain().getName()).build(), 0L));
				splitValues.add(statisticDTO.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.POSSIBLE_MATCHES_SPLIT).perDomain(getSelectedDomain().getName()).build(), 0L));
				postponedValues.add(statisticDTO.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.POSSIBLE_MATCHES_POSTPONED).perDomain(getSelectedDomain().getName()).build(), 0L));
			}
			dataLabels.add(dateToString(statisticDTO.getEntrydate(), "date"));
		}

		return Chart.initLineChart(valuesLists, dataSetLabels, dataSetColors, dataLabels, themeBean.getDarkMode());
	}

	public PieChart getPossibleMatchesChart(boolean mobile)
	{
		List<Number> values = new ArrayList<>();
		List<String> labels = new ArrayList<>();
		List<String> colors = new ArrayList<>();

		if (DashboardDomain.ALL.equals(domain))
		{
			values.add(stats.getMappedStatValue().get(new StatisticKeys(StatisticKeys.POSSIBLE_MATCHES_OPEN).build()));
			values.add(stats.getMappedStatValue().get(new StatisticKeys(StatisticKeys.POSSIBLE_MATCHES_MERGED).build()));
			values.add(stats.getMappedStatValue().get(new StatisticKeys(StatisticKeys.POSSIBLE_MATCHES_SPLIT).build()));
			values.add(stats.getMappedStatValue().get(new StatisticKeys(StatisticKeys.POSSIBLE_MATCHES_POSTPONED).build()));
		}
		else
		{
			values.add(stats.getMappedStatValue().get(new StatisticKeys(StatisticKeys.POSSIBLE_MATCHES_OPEN).perDomain(getSelectedDomain().getName()).build()));
			values.add(stats.getMappedStatValue().get(new StatisticKeys(StatisticKeys.POSSIBLE_MATCHES_MERGED).perDomain(getSelectedDomain().getName()).build()));
			values.add(stats.getMappedStatValue().get(new StatisticKeys(StatisticKeys.POSSIBLE_MATCHES_SPLIT).perDomain(getSelectedDomain().getName()).build()));
			values.add(stats.getMappedStatValue().get(new StatisticKeys(StatisticKeys.POSSIBLE_MATCHES_POSTPONED).perDomain(getSelectedDomain().getName()).build()));
		}
		labels.add(getBundle().getString("page.dashboard.possibleMatches.open.short"));
		labels.add(getBundle().getString("page.dashboard.possibleMatches.merged.short"));
		labels.add(getBundle().getString("page.dashboard.possibleMatches.split.short"));
		labels.add(getBundle().getString("page.dashboard.possibleMatches.postponed.short"));
		colors.add("#FFD166");
		colors.add("#06D6A0");
		colors.add("#26547C");
		colors.add("#7d7d7d");

		return Chart.initPieChart(values, labels, colors, mobile ? Chart.LegendPosition.TOP : Chart.LegendPosition.LEFT, themeBean.getDarkMode());
	}

	/* Ratios */
	public double getPersonsIdentitiesRatio()
	{
		if (DashboardDomain.ALL.equals(domain))
		{
			double identities = stats.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITIES).build(), 1L);
			double persons = stats.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.PERSONS).build(), 1L);
			return persons > 0 ? identities / persons : 0L;
		}
		else
		{
			double identities = stats.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITIES).perDomain(getSelectedDomain().getName()).build(), 1L);
			double persons = stats.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.PERSONS).perDomain(getSelectedDomain().getName()).build(), 1L);
			return persons > 0 ? identities / persons : 0L;
		}
	}

	public double getIdentityPossibleMatchRatio()
	{
		if (DashboardDomain.ALL.equals(domain))
		{
			double identitiesPossibleMatch = stats.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITY_POSSIBLE_MATCH).build(), 1L);
			double identities = stats.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITIES).build(), 1L);
			return identities > 0 ? identitiesPossibleMatch / identities : 0L;
		}
		else
		{
			double identitiesPossibleMatch = stats.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITY_POSSIBLE_MATCH).perDomain(getSelectedDomain().getName()).build(), 1L);
			double identities = stats.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITIES).perDomain(getSelectedDomain().getName()).build(), 1L);
			return identities > 0 ? identitiesPossibleMatch / identities : 0L;
		}
	}

	public double getIdentityMatchRatio()
	{
		if (DashboardDomain.ALL.equals(domain))
		{
			double identitiesMatch = (stats.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITY_MATCH).build(), 1L)
					+ stats.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITY_PERFECT_MATCH).build(), 1L));
			double identities = stats.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITIES).build(), 1L);
			return identities > 0 ? identitiesMatch / identities : 0L;
		}
		else
		{
			double identitiesMatch = (stats.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITY_MATCH).perDomain(getSelectedDomain().getName()).build(), 1L)
					+ stats.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITY_PERFECT_MATCH).perDomain(getSelectedDomain().getName()).build(), 1L));
			double identities = stats.getMappedStatValue().getOrDefault(new StatisticKeys(StatisticKeys.IDENTITIES).perDomain(getSelectedDomain().getName()).build(), 1L);
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
			valueMap.put(key, stats.getMappedStatValue().getOrDefault(key, 0L));
		}
		return getMapAsCsv(valueMap, stats.getEntrydate(), "all_domains stats latest");
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
			valueMap.put(key, stats.getMappedStatValue().getOrDefault(key, 0L));
		}
		return getMapAsCsv(valueMap, stats.getEntrydate(), getSelectedDomain().getName() + " stats latest");
	}

	public StreamedContent getHistoryStatsActiveDomainDownload()
	{
		return getHistoryStats(new ArrayList<>(getLatestStatsActiveDomainLabels().keySet()), getSelectedDomain().getName() + " stats history");
	}

	/* Private methods */
	private void loadStats()
	{
		// Look if any stats exist
		stats = getStatisticService().getLatestStats();
		hasStats = stats != null && stats.getMappedStatValue().containsKey(StatisticKeys.CALCULATION_TIME);

		if (hasStats)
		{
			// Set min and max date for stats
			statsMinDate = getStatisticService().getFirstStats().getEntrydate();
			statsMaxDate = stats.getEntrydate();

			// Get stats for custom date
			if (statsDate != null && !stats.getEntrydate().equals(statsDate))
			{
				List<StatisticDTO> historyForCustomStatsDate = getStatisticService().getStatsFromTo(new Date(0), statsDate);
				stats = historyForCustomStatsDate.get(historyForCustomStatsDate.size() - 1);
			}
			else
			{
				statsDate = stats.getEntrydate();
			}

			// Set range start date if not set
			rangeStartDate = rangeStartDate != null ? rangeStartDate : statsMinDate;

			// set range end date if not set or if range ends after custom statsDate
			rangeEndDate = rangeEndDate != null && !rangeEndDate.after(statsDate) ? rangeEndDate : statsDate;
		}
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
		for (StatisticDTO statisticDTO : rangeStats)
		{
			dates.add(dateToString(statisticDTO.getEntrydate(), "date"));
			for (Map.Entry<String, List<Object>> entry : valueMap.entrySet())
			{
				entry.getValue().add(statisticDTO.getMappedStatValue().getOrDefault(entry.getKey(), 0L));
			}
		}

		return File.get3DDataAsCSV(valueMap, dates, details, TOOL);
	}

	public StatisticDTO getStats()
	{
		return stats;
	}

	public String getLatestStatsDateTimeString()
	{
		if (stats.getEntrydate().toInstant().truncatedTo(ChronoUnit.DAYS).equals(new Date().toInstant().truncatedTo(ChronoUnit.DAYS)))
		{
			return getCommonBundle().getString("ui.date.today") + " " + getLatestStatsTimeString();
		}
		else
		{
			return dateToString(stats.getEntrydate(), "date") + " " + getLatestStatsTimeString();
		}
	}

	public String getLatestStatsTimeString()
	{
		return dateToString(stats.getEntrydate(), "time");
	}

	public long getLatestStatsCalculationTime()
	{
		return stats.getMappedStatValue().getOrDefault(StatisticKeys.CALCULATION_TIME, -1L);
	}

	public boolean isHasStats()
	{
		return hasStats;
	}

	public boolean isHasSummaryStats()
	{
		return hasStats && stats != null && stats.containsSummary();
	}

	public boolean isHasStatsInTimespan()
	{
		return hasStats && stats != null;
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

	public List<Chart.BarScale> getAvailableBarScales()
	{
		return Arrays.asList(Chart.getAvailableBarScales());
	}

	public DashboardDomain getDomain()
	{
		return domain;
	}

	public void setDomain(DashboardDomain domain)
	{
		this.domain = domain;
	}

	public Date getStatsDate()
	{
		return statsDate;
	}

	public void setStatsDate(Date statsDate)
	{
		this.statsDate = statsDate;
	}

	public Date getRangeStartDate()
	{
		return rangeStartDate;
	}

	public void setRangeStartDate(Date rangeStartDate)
	{
		this.rangeStartDate = rangeStartDate;
	}

	public Date getRangeEndDate()
	{
		return rangeEndDate;
	}

	public void setRangeEndDate(Date rangeEndDate)
	{
		this.rangeEndDate = rangeEndDate;
	}

	public Date getStatsMinDate()
	{
		return statsMinDate;
	}

	public Date getStatsMaxDate()
	{
		return statsMaxDate;
	}

	public boolean isRangeStatsLoaded()
	{
		return rangeStatsLoaded;
	}
	
	public enum DashboardDomain
	{
		ALL, CURRENT
	}
}
