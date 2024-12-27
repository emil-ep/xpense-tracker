package com.xperia.xpense_tracker.controllers;

import com.xperia.xpense_tracker.exception.customexception.TrackerBadRequestException;
import com.xperia.xpense_tracker.models.metrics.MetricTimeFrame;
import com.xperia.xpense_tracker.models.request.TimeframeRequest;
import com.xperia.xpense_tracker.models.request.TimeframeServiceRequest;
import com.xperia.xpense_tracker.models.response.AbstractResponse;
import com.xperia.xpense_tracker.models.response.ErrorResponse;
import com.xperia.xpense_tracker.models.response.SuccessResponse;
import com.xperia.xpense_tracker.services.MetricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/v1/metrics")
public class MetricController {

    @Autowired
    private MetricsService metricsService;

    private static final Logger LOGGER = LoggerFactory.getLogger(MetricController.class);

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

    @PostMapping("/v2")
    public ResponseEntity<AbstractResponse> fetchMetricsV2(@RequestParam("aggregationMode") String timeframe,
                                                           @RequestParam("metrics") String[] metrics,
                                                           @RequestBody TimeframeRequest request,
                                                           @AuthenticationPrincipal UserDetails userDetails){
        try{
            if (MetricTimeFrame.findByTimeframe(timeframe) == null){
                throw new TrackerBadRequestException("Invalid parameter for timeframe");
            }
            TimeframeServiceRequest timeInterval = validateTimeframeRequest(request);
            MetricTimeFrame metricTimeFrame = MetricTimeFrame.findByTimeframe(timeframe);
            List<Object> response = metricsService.fetchMetricsV2(metricTimeFrame, metrics, userDetails, timeInterval);
            return ResponseEntity.ok(new SuccessResponse(response));
        } catch (TrackerBadRequestException ex){
            LOGGER.error("Error processing metrics : {}", ex.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
        } catch (Exception ex){
            LOGGER.debug("Exception while processing metrics - timeframe : {} , metrics : {} - ex: {}", timeframe, metrics, ex.getMessage());
            return ResponseEntity.internalServerError().body(new ErrorResponse("Error while processing metrics"));
        }
    }


    private TimeframeServiceRequest validateTimeframeRequest(TimeframeRequest request) throws TrackerBadRequestException{

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
        LocalDate fromDate;
        LocalDate toDate;
        try{
            fromDate = LocalDate.parse(request.getFromDate(), formatter);
            toDate = LocalDate.parse(request.getToDate(), formatter);
        }catch (Exception ex){
            LOGGER.error("Error while parsing fromDate or toDate : {}", ex.getMessage());
            throw new TrackerBadRequestException("Error parsing fromDate or toDate - should be in the format dd/MM/yy");
        }

        if (fromDate.isAfter(toDate)){
            throw new TrackerBadRequestException("fromDate is after toDate");
        }

        return new TimeframeServiceRequest(fromDate, toDate);
    }
}
