package com.example.IOT_HELL.payload.request;

import lombok.Data;

@Data
public class UsersUpdateRequest {
    private String userName;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;

}
