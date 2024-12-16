package com.xperia.xpense_tracker.services;

import com.xperia.xpense_tracker.models.metrics.MetricTimeFrame;
import com.xperia.xpense_tracker.models.request.TimeframeServiceRequest;
import com.xperia.xpense_tracker.models.response.AggregatedExpenseResponse;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface MetricsService {

    List<AggregatedExpenseResponse> fetchMetrics(MetricTimeFrame timeframe, int limit, UserDetails userDetails);

    List<Object> fetchMetricsV2(MetricTimeFrame timeFrame, String[] metricToBeFetched, UserDetails userDetails, TimeframeServiceRequest timeInterval);
}
