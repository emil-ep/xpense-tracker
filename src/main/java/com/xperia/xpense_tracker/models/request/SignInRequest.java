package com.xperia.xpense_tracker.models.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignInRequest {

    @NotBlank(message = "username field cannot be empty")
    @Email(message = "Value provided is not valid email")
    private String username;

    @NotBlank(message = "Password cannot be empty")
    private String password;

}
