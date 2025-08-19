package com.xperia.xpense_tracker.models.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StatementHeaderMapResponse {

    private List<String> header;

    private List<String> entityMap;

    private Map<String, String> possibleMatches;

}
