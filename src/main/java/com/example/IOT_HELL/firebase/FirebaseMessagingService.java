package com.example.IOT_HELL.firebase;
import com.google.firebase.messaging.*;
import org.springframework.stereotype.Service;

@Service
public class FirebaseMessagingService {

    public void sendNotification(String title, String message, String token) {
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(message)
                .build();

        Message fcmMessage = Message.builder()
                .setToken(token)
                .setNotification(notification)
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(fcmMessage);
            System.out.println("✅ FCM Notification sent: " + response);
        } catch (FirebaseMessagingException e) {
            System.err.println("❌ Failed to send FCM notification: " + e.getMessage());
        }
    }
}


