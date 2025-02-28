package com.xperia.xpense_tracker.models.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class SyncExpenseResponse {

    private String uiText;

    private String requestId;
}
