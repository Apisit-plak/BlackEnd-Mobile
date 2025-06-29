package com.example.IOT_HELL.payload.response;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UsersResponseAll {
    private String userName;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
}
