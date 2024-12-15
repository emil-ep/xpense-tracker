package com.xperia.xpense_tracker.controllers;

import com.xperia.xpense_tracker.exception.customexception.TrackerBadRequestException;
import com.xperia.xpense_tracker.models.metrics.MetricTimeFrame;
import com.xperia.xpense_tracker.models.response.AbstractResponse;
import com.xperia.xpense_tracker.models.response.SuccessResponse;
import com.xperia.xpense_tracker.services.MetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/metrics")
public class MetricController {

    @Autowired
    private MetricsService metricsService;

    @PostMapping
    public ResponseEntity<AbstractResponse> fetchMetrics(@RequestParam("timeframe") String timeframe,
                                                         @RequestParam("limit") int limit,
                                                         @AuthenticationPrincipal UserDetails userDetails){

        try{
            if(MetricTimeFrame.findByTimeframe(timeframe) == null){
                throw new TrackerBadRequestException("Invalid parameter for timeframe");
            }
            MetricTimeFrame metricTimeFrame = MetricTimeFrame.findByTimeframe(timeframe);
            Object response = metricsService.fetchMetrics(metricTimeFrame, limit, userDetails);
            return ResponseEntity.ok(new SuccessResponse(response));
        }catch (Exception ex){
            return null;
        }
    }

    @GetMapping("/v2")
    public ResponseEntity<AbstractResponse> fetchMetricsV2(@RequestParam("timeframe") String timeframe,
                                                           @RequestParam("metrics") String[] metrics,
                                                           @AuthenticationPrincipal UserDetails userDetails){
        try{
            if (MetricTimeFrame.findByTimeframe(timeframe) == null){
                throw new TrackerBadRequestException("Invalid parameter for timeframe");
            }
            MetricTimeFrame metricTimeFrame = MetricTimeFrame.findByTimeframe(timeframe);
            List<Object> response = metricsService.fetchMetricsV2(metricTimeFrame, metrics, userDetails);
            return ResponseEntity.ok(new SuccessResponse(response));
        }catch (Exception ex){
            return null;
        }
    }
}
