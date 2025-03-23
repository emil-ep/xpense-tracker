package com.xperia.xpense_tracker.models.metrics;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum MetricTimeFrame {

    DAILY("daily", date -> date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))),
    WEEKLY("weekly", date -> {
        LocalDate startOfWeek = date.minusDays(date.getDayOfWeek().getValue() - 1);
        return startOfWeek.format(DateTimeFormatter.ofPattern("yyyy-'W'ww"));
    }),
    MONTHLY("monthly", date -> date.format(DateTimeFormatter.ofPattern("yyyy-MM"))),
    YEARLY("yearly", date -> date.format(DateTimeFormatter.ofPattern("yyyy"))),
    CUSTOM("custom", date -> "custom");


    private final String timeframe;
    private final Function<LocalDate, String> groupingFunction;

    MetricTimeFrame(String timeframe, Function<LocalDate, String> groupingFunction){
        this.timeframe = timeframe;
        this.groupingFunction = groupingFunction;
    }


    public static MetricTimeFrame findByTimeframe(String timeframe){
        for (MetricTimeFrame metricTimeFrame : MetricTimeFrame.values()){
            if (metricTimeFrame.timeframe.equalsIgnoreCase(timeframe)){
                return metricTimeFrame;
            }
        }
        return null;
    }

    public String getTimeframe(){
        return this.timeframe;
    }

    /**
     * Groups a stream of items based on the specified time frame.
     *
     * @param stream        the stream of items to group
     * @param dateExtractor function to extract LocalDate from the item
     * @param <T>           the type of items in the stream
     * @return a map where the key is the grouped time frame and the value is the list of items
     */
    public <T> Map<String, List<T>> groupBy(Stream<T> stream, Function<T, LocalDate> dateExtractor) {
        return stream.collect(Collectors.groupingBy(
                item -> groupingFunction.apply(dateExtractor.apply(item)) // Extract date and group based on the timeframe
        ));
    }

    @Override
    public String toString() {
        return "MetricTimeFrame{" +
                "timeframe='" + timeframe +
                '}';
    }
}
