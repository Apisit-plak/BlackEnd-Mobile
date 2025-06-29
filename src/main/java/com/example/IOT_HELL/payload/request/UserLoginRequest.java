package com.example.IOT_HELL.payload.request;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
public class UserLoginRequest {

    private String userName;
    private String passWord;
}
