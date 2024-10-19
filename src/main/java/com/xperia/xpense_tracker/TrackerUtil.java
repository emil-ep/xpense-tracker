package com.xperia.xpense_tracker;

import com.xperia.xpense_tracker.models.entities.ExpenseFields;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class TrackerUtil {

    public static LocalDate convertToLocalDate(Date dateToConvert) {
        return Instant.ofEpochMilli(dateToConvert.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public static String getFieldName(ExpenseFields field) {
        return field.getFieldName();
    }
}
