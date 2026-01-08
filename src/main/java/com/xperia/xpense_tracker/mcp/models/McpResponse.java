package com.xperia.xpense_tracker.mcp.models;

import lombok.Getter;

@Getter
public class McpResponse<T> {

    private boolean success;

    private T data;

    private String error;

    public static <T> McpResponse<T> success(T data){
        McpResponse<T> response = new McpResponse<>();
        response.success = true;
        response.data = data;
        return response;
    }

    public static <T> McpResponse<T> error(String error){
        McpResponse<T> response = new McpResponse<>();
        response.success = false;
        response.error = error;
        return response;
    }
}
