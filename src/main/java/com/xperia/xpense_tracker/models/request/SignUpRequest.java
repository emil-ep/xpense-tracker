package com.xperia.xpense_tracker.models.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "Email field cannot be empty")
    @Email(message = "Value provided is not valid email")
    private String email;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 8, message = "Password entered should be at least 8 characters long")
    private String password;


}
