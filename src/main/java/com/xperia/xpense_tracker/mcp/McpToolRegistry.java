package com.xperia.xpense_tracker.mcp;

import com.xperia.xpense_tracker.mcp.tool.McpTool;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class McpToolRegistry {

    private final Map<String, McpTool> tools = new HashMap<>();

    public McpToolRegistry(List<McpTool> toolList){
        for (McpTool tool: toolList){
            tools.put(tool.name(), tool);
        }
    }

    public McpTool getTool(String name){
        return tools.get(name);
    }
}
