package com.example.IOT_HELL.payload.response;

import lombok.Data;

@Data
public class NotificationRequest {
    private String to; // Expo Push Token
    private String title;
    private String body;
}