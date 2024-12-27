package com.xperia.xpense_tracker.models.response;

import com.xperia.xpense_tracker.models.entities.Expenses;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ExpensePaginatedResponse {

    private int totalPages;

    private long totalCount;

    private int currentCount;

    private int page;

    private List<Expenses> expenses;


}
