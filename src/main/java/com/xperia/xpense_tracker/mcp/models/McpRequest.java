package com.xperia.xpense_tracker.mcp.models;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class McpRequest {

    private String tool;

    private Map<String, Object> arguments;

}
