package com.xperia.xpense_tracker.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.xperia.xpense_tracker.cache.CacheService;
import com.xperia.xpense_tracker.models.entities.tracker.Expenses;
import com.xperia.xpense_tracker.models.entities.tracker.TagCategoryEnum;
import com.xperia.xpense_tracker.models.entities.tracker.UserSettings;
import com.xperia.xpense_tracker.models.metrics.MetricContext;
import com.xperia.xpense_tracker.models.metrics.MetricDefinitions;
import com.xperia.xpense_tracker.models.metrics.MetricTimeFrame;
import com.xperia.xpense_tracker.models.entities.tracker.TrackerUser;
import com.xperia.xpense_tracker.models.request.TimeframeServiceRequest;
import com.xperia.xpense_tracker.models.response.AggregatedExpenseResponse;
import com.xperia.xpense_tracker.models.settings.SettingsType;
import com.xperia.xpense_tracker.repository.tracker.ExpensesRepository;
import com.xperia.xpense_tracker.repository.tracker.TagRepository;
import com.xperia.xpense_tracker.repository.tracker.UserSettingRepository;
import com.xperia.xpense_tracker.services.MetricsService;
import com.xperia.xpense_tracker.services.UserSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.xperia.xpense_tracker.cache.CacheNames.METRICS_CACHE_NAME;

@Service
public class MetricsServiceImpl implements MetricsService {

    @Autowired
    private ExpensesRepository expensesRepository;

    @Autowired
    private CacheService cacheService;

    private static final Logger LOGGER = LoggerFactory.getLogger(MetricsServiceImpl.class);
    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserSettingRepository userSettingRepository;

    @Autowired
    private UserSettingsService userSettingsService;

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
        String cacheKey = StringUtils.arrayToCommaDelimitedString(metricToBeFetched)
                + ":"
                + aggregationTimeframe.toString()
                + ":"
                + timeInterval.toString()
                + ":"
                + userDetails.toString();
        cacheService.storeByUser(user.getId(), cacheKey, METRICS_CACHE_NAME);
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

        List<TagCategoryEnum> savingsCategories = findUserSavingsTagCategories(userDetails);

        for (Map.Entry<String, List<Expenses>> entry : groupedByTimeframe.entrySet()) {
            String currentTimeframe = entry.getKey();
            List<Expenses> group = entry.getValue();

            // Aggregate the group's values
            Map<String, Object> aggregatedMetrics = metricDefinitions
                    .stream()
                    .collect(Collectors.toMap(
                            MetricDefinitions::getMetricName,
                            definition -> definition.process(group.stream(), new MetricContext(savingsCategories))
                    ));

            Map<String, Object> result = new HashMap<>();
            result.put("timeframe", currentTimeframe);
            result.putAll(aggregatedMetrics);
            results.add(result);
        }
        return results;
    }

    /**
     * This function returns the tag categories that can be considered as a savings tag for the particular user
     * The savings tags will be saved under user settings
     * @param userDetails the user details
     * @return returns the TagCategoryEnum's that is specified in the user settings
     */
    private List<TagCategoryEnum> findUserSavingsTagCategories(UserDetails userDetails){

        UserSettings savingsCategoriesOfUser = userSettingsService.findUserSettingsByType(SettingsType.SAVINGS_TAGS, userDetails);
        JsonNode payload =  savingsCategoriesOfUser.getPayload();
        Set<String> categories = StreamSupport
                .stream(payload.get("tags").spliterator(), false)
                .map(JsonNode::asText)
                .collect(Collectors.toSet());
        List<TagCategoryEnum> savingsCategories = Arrays.stream(TagCategoryEnum.values())
                .filter(category -> categories.stream().anyMatch(tag -> category.getName().equalsIgnoreCase(tag)))
                .toList();
        return savingsCategories;
    }
}
