package com.xperia.xpense_tracker.services.impl;

import com.xperia.xpense_tracker.models.entities.MetricTimeFrame;
import com.xperia.xpense_tracker.models.entities.TrackerUser;
import com.xperia.xpense_tracker.models.response.AggregatedExpenseResponse;
import com.xperia.xpense_tracker.repository.ExpensesRepository;
import com.xperia.xpense_tracker.services.MetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MetricsServiceImpl implements MetricsService {

    @Autowired
    private ExpensesRepository expensesRepository;

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
}
