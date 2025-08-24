package com.xperia.xpense_tracker.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
@AllArgsConstructor
public class ParsedRowData {

    private int parsedRowIndex;

    private DateTimeFormatter dateTimeFormatter;
}
