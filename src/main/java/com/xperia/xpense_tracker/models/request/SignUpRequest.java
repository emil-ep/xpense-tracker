package com.xperia.xpense_tracker.models.request;


import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class SignUpRequest {

    private String name;

    private String email;

    private String password;


}
