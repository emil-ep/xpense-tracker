package com.xperia.xpense_tracker.models.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BankAccountRequest {

    private String id;

    private String name;

    private String accountNumber;

    private String type;

}
