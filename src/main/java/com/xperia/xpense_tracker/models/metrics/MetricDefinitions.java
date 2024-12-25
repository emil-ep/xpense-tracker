package com.xperia.xpense_tracker.models.metrics;

import com.xperia.xpense_tracker.models.entities.Expenses;
import lombok.Getter;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Enum for providing implementation for processing metrics
 * The processor is a Functional Interface that provides the stream operation for computing the metrics
 */
@Getter
public enum MetricDefinitions {

    AGG_CREDIT(
            "credit_aggregate",
            "SUM",
            stream -> stream
                    .filter(Objects::nonNull)
                    .mapToDouble(value -> ((Expenses) value).getCredit())
                    .sum()),
    AGG_DEBIT(
            "debit_aggregate",
            "SUM",
            stream -> stream
                    .filter(Objects::nonNull)
                    .mapToDouble(value -> ((Expenses) value).getDebit())
                    .sum()),
    AGG_BY_TAG(
            "tags_aggregate",
            "GROUP_BY_TAG",
            stream -> stream
                    .filter(Expenses.class::isInstance) // Ensure the stream contains only Expenses
                    .map(Expenses.class::cast)          // Safely cast each element to Expenses
                    .flatMap(expense -> expense.getTags().stream()
                            .map(tag -> Map.entry(tag.getName(), expense))) // Use Map.entry
                    .collect(Collectors.groupingBy(
                            Map.Entry::getKey,
                            Collectors.summingDouble(entry ->
                                    entry.getValue().getCredit() + entry.getValue().getDebit())
                    ))
    );

    // ADD MORE METRIC DEFINITIONS HERE

    private final String metricName;
    private final String aggregationMode;
    private final MetricProcessor<?, ?> processor;

    <T, R> MetricDefinitions(String metricName, String aggregationMode, MetricProcessor<T, R> processor){
        this.metricName = metricName;
        this.aggregationMode = aggregationMode;
        this.processor = processor;
    }

    public static MetricDefinitions findByMetricName(String metricName){
        for(MetricDefinitions definitions : MetricDefinitions.values()){
            if(definitions.getMetricName().equalsIgnoreCase(metricName)){
                return definitions;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T, R> R  process(Stream<T> values) {
        return ((MetricProcessor<T, R>) processor).process(values);
    }
}
