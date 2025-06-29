package com.example.IOT_HELL.payload.request;


import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;

@Data
public class UsersCreateRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
    private String userName;
    @NotBlank(message = "password is required")
    @Size(min = 4, max = 20, message = "password must be between 4 and 20 characters")
    private String passWord;
    @NotBlank(message = "firstName is required")
    @Size(min = 4, max = 20, message = "firstName must be between 4 and 20 characters")
    private String firstName;
    @NotBlank(message = "lastName is required")
    @Size(min = 4, max = 20, message = "lastName must be between 4 and 20 characters")
    private String lastName;
    @NotBlank(message = "email is required")
    @Size(min = 4, max = 20, message = "email must be between 4 and 20 characters")
    private String email;
    @NotBlank(message = "phoneNumber is required")
    @Size(min = 4, max = 20, message = "phoneNumber must be between 4 and 20 characters")
    private String phoneNumber;


}
