package com.xperia.xpense_tracker.mcp.tool;

import com.xperia.xpense_tracker.models.entities.tracker.TrackerUser;
import com.xperia.xpense_tracker.models.metrics.MetricDefinitions;
import com.xperia.xpense_tracker.models.metrics.MetricTimeFrame;
import com.xperia.xpense_tracker.services.MetricsService;
import com.xperia.xpense_tracker.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class TotalExpensesEntryTool implements McpTool{

    @Autowired
    private UserService userService;

    @Autowired
    private MetricsService metricsService;


    @Override
    public String name() {
        return "getMetrics";
    }

    @Override
    public Object execute(Map<String, Object> arguments) {
        MetricTimeFrame timeFrame = MetricTimeFrame.findByTimeframe((String) arguments.get("timeframe"));
        String[] metricNames = MetricDefinitions.allMetricNames();
        Optional<TrackerUser> user = userService.findUserByUserId((String)arguments.get("userId"));
        return metricsService.fetchMetricsV2(timeFrame, metricNames, user.get(), null);
    }
}
