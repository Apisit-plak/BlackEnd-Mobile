package com.example.IOT_HELL.service.Impl;

import com.example.IOT_HELL.payload.request.NotificationRequest2;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.database.*;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final Firestore firestore;
    private final FirebaseDatabase firebaseDatabase;
    private final SimpMessageSendingOperations messagingTemplate;

    public CompletableFuture<Map<String, Object>> getForestStatus() {
        CompletableFuture<Map<String, Object>> future = new CompletableFuture<>();
        DatabaseReference ref = firebaseDatabase.getReference("/");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Map<String, Object> response = new HashMap<>();

                for (DataSnapshot child : snapshot.getChildren()) {
                    String rawValue = child.child("value").getValue(String.class);
                    String timestamp = child.child("timestamp").getValue(String.class);

                    if (rawValue != null && rawValue.length() == 3) {
                        String boardId = rawValue.substring(0, 1);
                        String statusCode = rawValue.substring(1, 3);

                        try {
                            Map<String, Object> boardData = getBoardData(boardId);
                            if (boardData != null) {
                                boardData.put("status", getStatusMessage(statusCode));
                                boardData.put("timestamp", Objects.requireNonNullElse(timestamp, "ไม่มีข้อมูลเวลา"));
                                response.put(boardId, boardData);

                                // 🚨 ตรวจสอบสถานะ และส่งแจ้งเตือนถ้าพบเหตุการณ์สำคัญ
                                if ("3".equals(statusCode) || "5".equals(statusCode)) {
                                    sendAlertNotification(boardId, boardData.get("status").toString(), boardData);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                future.complete(response);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });

        return future;
    }

    private Map<String, Object> getBoardData(String boardId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection("forest").document(boardId);
        DocumentSnapshot docSnapshot = docRef.get().get();
        return docSnapshot.exists() ? docSnapshot.getData() : new HashMap<>(); // ✅ ป้องกัน null
    }

    private String getStatusMessage(String statusCode) {
        switch (statusCode) {
            case "0": return "บอร์ดปกติ";
            case "1": return "มีการเอียงจากจุดที่เปิดเครื่อง";
            case "2": return "มีแรงสั่น";
            case "3": return "มีแรงสั่นผิดปกติ";
            case "4": return "มีเสียงดังผิดปกติ";
            case "5": return "ต้นไม้นี้กำลังโดนตัด!";
            default: return "ไม่ทราบสถานะ";
        }
    }

    // ✅ ฟังก์ชันส่งแจ้งเตือนผ่าน WebSocket
    private void sendWebSocketNotification(String message) {
        messagingTemplate.convertAndSend("/topic/alerts", message);
    }

    public void sendAlertNotification(String boardId, String status, Map<String, Object> data) {
        String message = String.format(
                "🚨 แจ้งเตือน!\nบอร์ด ID: %s\nสถานะ: %s\nพิกัด: %s, %s\nอัปเดตล่าสุด: %s",
                boardId,
                status,
                Objects.requireNonNullElse(data.get("latitude"), "ไม่มีข้อมูล"),
                Objects.requireNonNullElse(data.get("longitude"), "ไม่มีข้อมูล"),
                Objects.requireNonNullElse(data.get("timestamp"), "ไม่มีข้อมูล")
        );

        // ✅ ส่งแจ้งเตือนผ่าน WebSocket
        sendWebSocketNotification(message);

        // ✅ ส่งแจ้งเตือนไปยัง Expo Push Notification (ถ้าใช้)
        NotificationRequest2 request = new NotificationRequest2(
                getExpoPushToken(boardId),  // รับ Token ตาม boardId
                "🌲 แจ้งเตือนจากระบบ!",
                message
        );
        sendPushNotification(request);
    }

    public void sendPushNotification(NotificationRequest2 request) {
        // ✅ โค้ดส่ง Push Notification ไปยัง Expo (Implement API Call ที่นี่)
    }

    private String getExpoPushToken(String boardId) {
        // ✅ คืนค่า Expo Push Token ของผู้ใช้ตาม Board ID (ใช้ฐานข้อมูลหรือ Config)
        return "<ExpoPushToken>";  // แทนที่ด้วยการดึง Token จริง
    }
}
