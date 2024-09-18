package com.xperia.xpense_tracker.models.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class SignUpRequest {

    private String name;

    private String email;

    private String password;


}
