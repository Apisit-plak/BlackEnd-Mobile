package com.example.IOT_HELL.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor  // ✅ Constructor ว่าง (Default)
@AllArgsConstructor // ✅ Constructor ที่รับ 3 arguments
public class NotificationRequest2 {
    private String to;
    private String title;
    private String body;
}
