package com.xperia.xpense_tracker.models.metrics;

import java.util.stream.Stream;

@FunctionalInterface
public interface MetricProcessor<T, R> {

    R process(Stream<T> values);
}
