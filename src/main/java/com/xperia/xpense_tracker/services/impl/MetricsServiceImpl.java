package com.xperia.xpense_tracker.services.impl;

import com.xperia.xpense_tracker.models.entities.Expenses;
import com.xperia.xpense_tracker.models.metrics.MetricDefinitions;
import com.xperia.xpense_tracker.models.metrics.MetricTimeFrame;
import com.xperia.xpense_tracker.models.entities.TrackerUser;
import com.xperia.xpense_tracker.models.request.TimeframeServiceRequest;
import com.xperia.xpense_tracker.models.response.AggregatedExpenseResponse;
import com.xperia.xpense_tracker.repository.ExpensesRepository;
import com.xperia.xpense_tracker.services.MetricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.xperia.xpense_tracker.cache.CacheNames.METRICS_CACHE_NAME;

@Service
public class MetricsServiceImpl implements MetricsService {

    @Autowired
    private ExpensesRepository expensesRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(MetricsServiceImpl.class);

    @Override
    public List<AggregatedExpenseResponse> fetchMetrics(MetricTimeFrame timeframe, int limit, UserDetails userDetails) {
        TrackerUser user = (TrackerUser) userDetails;
        List<Object[]> rawResponse = expensesRepository.aggregateExpensesByMetricTimeFrame(timeframe.getTimeframe(), user);
        return rawResponse.stream()
                        .map(aggExpense -> new AggregatedExpenseResponse(
                                (String) aggExpense[0],
                                ((Number) aggExpense[1]).doubleValue(),
                                ((Number) aggExpense[2]).doubleValue()
                        )).toList();
    }

    /**
     * Very important function for processing metrics. NEED TO TEST COMPLETELY
     * This is a highly expensive function, there is a lot of calculation which wouldn't change for sometime
     * The metrics changes only if the user uploaded a new statement for processing.
     * Hence a cache is implemented in place to reduce the load on the server and database
     * Caffeine cache implementation takes care of this caching
     * The second time user calls the function, it is returned from cache
     *
     * @param aggregationTimeframe the timeframe in which metrics needs to be fetched
     * @param metricToBeFetched    the metrics that needs to be fetched. The metric should correspond to MetricTimeFrame enum
     * @param userDetails          the user for which the details should be fetched
     * @param timeInterval         the time interval in which metrics should be calculated
     * @return returns the list of metrics aggregated by timeframe
     */
    @Override
    @Cacheable(value = METRICS_CACHE_NAME,
            key = "T(org.springframework.util.StringUtils).arrayToCommaDelimitedString(#metricToBeFetched) + ':' + #aggregationTimeframe.toString() + ':' + #timeInterval.toString() + ':' + #userDetails.toString()")
    public List<Object> fetchMetricsV2(MetricTimeFrame aggregationTimeframe, String[] metricToBeFetched,
                                       UserDetails userDetails, TimeframeServiceRequest timeInterval) {
        TrackerUser user = (TrackerUser) userDetails;
        //converting the metrics received in request to the corresponding MetricDefinitions
        List<MetricDefinitions> metricDefinitions = Arrays.stream(metricToBeFetched)
                .map(MetricDefinitions::findByMetricName)
                .toList();
        List<Object> results = new ArrayList<>();
        List<Expenses> expenses = expensesRepository
                .findExpensesByUserAndTransactionDateBetween(
                        user,
                        timeInterval.getFromDate(),
                        timeInterval.getToDate()
                );
        Map<String, List<Expenses>> groupedByTimeframe = new TreeMap<>(aggregationTimeframe
                .groupBy(expenses.stream(), Expenses::getTransactionDate));
        for (Map.Entry<String, List<Expenses>> entry : groupedByTimeframe.entrySet()) {
            String currentTimeframe = entry.getKey();
            List<Expenses> group = entry.getValue();

            // Aggregate the group's values
            Map<String, Object> aggregatedMetrics = metricDefinitions
                    .parallelStream()
                    .collect(Collectors.toMap(
                            MetricDefinitions::getMetricName,
                            definition -> definition.process(group.stream())
                    ));

            Map<String, Object> result = new HashMap<>();
            result.put("timeframe", currentTimeframe);
            result.putAll(aggregatedMetrics);
            results.add(result);
        }
        return results;
    }
}
