package com.xperia.xpense_tracker.models.metrics;

import com.xperia.xpense_tracker.models.entities.Expenses;
import com.xperia.xpense_tracker.models.entities.Tag;
import com.xperia.xpense_tracker.models.entities.TransactionType;
import lombok.Getter;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Enum for providing implementation for processing metrics
 * The processor is a Functional Interface that provides the stream operation for computing the metrics
 */
@Getter
public enum MetricDefinitions {

    FIRST_EXPENSE_RECORDED_DATE(
      "first_expense_recorded_date",
      "",
      stream -> stream
              .filter(Expenses.class::isInstance)
              .min(Comparator.comparing(expense -> ((Expenses) expense).getTransactionDate(),
                      Comparator.nullsLast(Comparator.naturalOrder())))
              .map(expense -> {
                  Expenses expenses = ((Expenses) expense);
                  if (expenses.getTransactionDate() != null){
                      return expenses.getTransactionDate().toString();
                  }
                  return "Not found";
              })
    ),
    LAST_EXPENSE_RECORDED_DATE(
            "last_expense_recorded_date",
            "",
            stream -> stream
                    .filter(Expenses.class::isInstance)
                    .min(Comparator.comparing(expense -> ((Expenses) expense).getTransactionDate(),
                            Comparator.nullsLast(Comparator.reverseOrder())))
                    .map(expense -> {
                        Expenses expenses = ((Expenses) expense);
                        if (expenses.getTransactionDate() != null){
                            return expenses.getTransactionDate().toString();
                        }
                        return "Not found";
                    })
    ),
    TOTAL_EXPENSES_ENTRY(
         "total_expenses_entry",
            "SUM",
            Stream::count
    ),
    TOTAL_UNTAGGED_EXPENSES_ENTRY(
      "total_untagged_expenses_entry",
      "SUM",
      stream -> stream
              .filter(Expenses.class::isInstance)
              .filter(expense -> ((Expenses) expense).getTags() == null || ((Expenses) expense).getTags().isEmpty())
              .count()
    ),
    TOTAL_TAGGED_EXPENSES_ENTRY(
      "total_tagged_expenses_entry",
      "SUM",
      stream -> stream
              .filter(Expenses.class::isInstance)
              .filter(expense -> ((Expenses) expense).getTags() != null && !((Expenses) expense).getTags().isEmpty())
              .count()
    ),
    HIGHEST_EXPENSE_RECORDED(
      "highest_expense_recorded",
      "SUM",
      stream -> stream
              .filter(Expenses.class::isInstance)
              .max(Comparator.comparingDouble(e -> ((Expenses) e).getDebit()))
              .map(e -> ((Expenses)e).getDebit())
    ),
    HIGHEST_EXPENSE_TAG(
            "highest_expense_tag",
            "SUM",
            stream -> stream
                    .filter(Expenses.class::isInstance)
                    .max(Comparator.comparingDouble(e -> ((Expenses) e).getDebit()))
                    .map(e -> {
                        Set<Tag> tags = ((Expenses) e).getTags();
                        if (tags != null && !tags.isEmpty()){
                            return tags.stream().map(Tag::getName).reduce((t1, t2) -> t1 + "," + t2);
                        }
                        return "";
                    })

    ),
    HIGHEST_CREDIT_RECORDED(
      "highest_credit_recorded",
      "SUM",
      stream -> stream
              .filter(Expenses.class::isInstance)
              .max(Comparator.comparingDouble(e -> ((Expenses) e).getCredit()))
              .map(e -> ((Expenses)e).getCredit())
    ),
    HIGHEST_CREDIT_RECORDED_TAG(
            "highest_credit_recorded_tag",
            "SUM",
            stream -> stream
                    .filter(Expenses.class::isInstance)
                    .max(Comparator.comparingDouble(e -> ((Expenses) e).getCredit()))
                    .map(e -> {
                        Set<Tag> tags = ((Expenses) e).getTags();
                        if (tags != null && !tags.isEmpty()){
                            return tags.stream().map(Tag::getName).reduce((t1, t2) -> t1 + "," + t2);
                        }
                        return "";
                    })
    ),
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
                    .filter(Expenses.class::isInstance)
                    .map(Expenses.class::cast)
                    .flatMap(expense -> {
                        Set<Tag> tags = expense.getTags();
                        if (tags == null || tags.isEmpty()) {
                            if (expense.getType() == TransactionType.CREDIT){
                                return Stream.of(Map.entry("UnTagged Credit", expense));
                            }else{
                                return Stream.of(Map.entry("UnTagged Debit", expense));
                            }
                        } else {
                            return tags.stream()
                                    .map(tag -> Map.entry(tag.getName(), expense));
                        }
                    })
                    .collect(Collectors.groupingBy(
                            Map.Entry::getKey,
                            Collectors.summingDouble(entry ->
                                    entry.getValue().getCredit() + entry.getValue().getDebit()
                            )
                    ))
    ),
    AGG_EXPENSE("expense_aggregate",
            "SUM",
            stream -> stream
                    .filter(Expenses.class::isInstance)
                    .map(Expenses.class::cast)
                    .filter(expense -> expense.getTags().stream()
                            .anyMatch(tag -> tag.getCategory() != null && tag.getCategory().isExpense()))
                    .mapToDouble(Expenses::getDebit)
                    .sum()
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
