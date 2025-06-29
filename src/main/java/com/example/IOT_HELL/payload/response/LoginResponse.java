package com.example.IOT_HELL.payload.response;


import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private long expiresIn;

}
