package com.example.IOT_HELL.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserProfile {
    private String email;
    private String first_name;
    private String last_name;
    private String phone_number;
}