package com.xperia.xpense_tracker.mcp.tool;

import com.xperia.xpense_tracker.mcp.models.McpResponse;
import com.xperia.xpense_tracker.services.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MonthlySpendSummaryTool implements McpTool{


    @Autowired
    private ExpenseService expenseService;

    @Override
    public String name() {
        return "getMonthlySpendSummary";
    }

    @Override
    public Object execute(Map<String, Object> arguments) {
        String totalUsage = "100 Rs";
        return McpResponse.success(totalUsage);
    }
}
