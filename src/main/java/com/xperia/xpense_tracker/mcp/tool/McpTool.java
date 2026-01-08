package com.xperia.xpense_tracker.mcp.tool;

import java.util.Map;

public interface McpTool {

    String name();

    Object execute(Map<String, Object> arguments);
}
