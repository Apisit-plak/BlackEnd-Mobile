package com.example.IOT_HELL.controller;

import com.example.IOT_HELL.service.Impl.NotificationService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api")
public class ForestController {

    private final NotificationService notificationService;

    public FirebaseToken verifyToken(String token) throws FirebaseAuthException {
        return FirebaseAuth.getInstance().verifyIdToken(token);
    }


    public ForestController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/forest-status")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> getForestStatus() {
        return notificationService.getForestStatus()
                .thenApply(forestStatus -> {
                    // ✅ ตรวจสอบและแปลง Object เป็น Map ก่อนใช้ `.get()`
                    forestStatus.forEach((boardId, data) -> {
                        if (data instanceof Map) {
                            Map<String, Object> boardData = (Map<String, Object>) data;
                            String status = (String) boardData.get("status");

                            // ✅ ตรวจสอบสถานะที่ต้องแจ้งเตือน
                            if ("ต้นไม้นี้กำลังโดนตัด!".equals(status) || "มีเสียงดังผิดปกติ".equals(status)) {
                                notificationService.sendAlertNotification(boardId, status, boardData);
                            }
                        }
                    });

                    return ResponseEntity.ok(forestStatus);
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                });
    }

    @GetMapping("/test-auth")
    public ResponseEntity<String> testAuth(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            FirebaseToken decodedToken = verifyToken(token);
            return ResponseEntity.ok("User ID: " + decodedToken.getUid());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }
    }


}
