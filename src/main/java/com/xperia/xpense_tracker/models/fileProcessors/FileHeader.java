package com.xperia.xpense_tracker.models.fileProcessors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class FileHeader {

    private int rowIndex;

    private List<String> headers;

}
