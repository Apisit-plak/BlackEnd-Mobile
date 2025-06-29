package com.example.IOT_HELL.controller;

import com.example.IOT_HELL.payload.response.NotificationRequest;
import com.example.IOT_HELL.service.Impl.FirebaseService;
import com.example.IOT_HELL.service.Impl.NotificationService;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api")
public class NotificationController {

    private final NotificationService notificationService;
    private final FirebaseService firebaseService;

    public NotificationController(NotificationService notificationService, FirebaseService firebaseService) {
        this.notificationService = notificationService;
        this.firebaseService = firebaseService;
    }

    //    @PostMapping("/send")
//    public ResponseEntity<String> sendNotification(@RequestBody NotificationRequest request) {
//        String response = notificationService.sendPushNotification(request);
//        return ResponseEntity.ok(response);
//    }
    @GetMapping("/boards")
    public List<Map<String, Object>> getBoardData() throws ExecutionException, InterruptedException {
        return firebaseService.getBoardData();
    }

    @GetMapping("fetch")
    public ResponseEntity<List<Map<String, Object>>> fetchTemperatureData() {
        List<Map<String, Object>> data = firebaseService.fetchTemperatureData().join();
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(data);
    }

}