package com.xperia.xpense_tracker.mcp;

import com.xperia.xpense_tracker.mcp.models.McpRequest;
import com.xperia.xpense_tracker.mcp.models.McpResponse;
import com.xperia.xpense_tracker.mcp.tool.McpTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/mcp")
public class McpController {

    @Autowired
    private McpToolRegistry toolRegistry;

    @GetMapping
    public String healthCheck(){
        return "MCP server is up";
    }

    @PostMapping
    public McpResponse<?> execute(@RequestBody McpRequest request){

        McpTool tool = toolRegistry.getTool(request.getTool());

        if (tool == null){
            return McpResponse.error("Unknown tool : " + request.getTool());
        }

        try{
            Object result = tool.execute(request.getArguments());
            return McpResponse.success(result);
        }catch (Exception ex){
            return McpResponse.error(ex.getMessage());
        }
    }
}
