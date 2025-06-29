package com.example.IOT_HELL.service.Impl;

import com.example.IOT_HELL.database.entity.IoTData;
import com.example.IOT_HELL.database.jpaRepo.IoTDataRepository;
import com.example.IOT_HELL.firebase.FirebaseMessagingService;
import com.example.IOT_HELL.payload.response.IoTDataGet;
import com.google.api.core.ApiFuture;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.database.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.logging.Logger;

@Service
public class FirebaseService {

    private static final Logger logger = Logger.getLogger(FirebaseService.class.getName());
    private static final String COLLECTION_NAME = "forest";
    private final DatabaseReference databaseReference;
    //    private final Firestore firestore;
    private final IoTDataRepository ioTDataRepository;
    private SimpMessagingTemplate messagingTemplate;
    private FirebaseMessagingService firebaseMessagingService;
    private Firestore firestore = FirestoreClient.getFirestore();
    private final DatabaseReference realtimeDatabase = FirebaseDatabase.getInstance().getReference("sensorData");


    @Autowired
    public FirebaseService(FirebaseDatabase firebaseDatabase, Firestore firestore, IoTDataRepository ioTDataRepository) {
        if (firebaseDatabase == null || firestore == null) {
            throw new IllegalStateException("Firebase services are not properly initialized");
        }
        this.databaseReference = firebaseDatabase.getReference("/microbit/temperature");
        this.firestore = firestore;
        this.ioTDataRepository = ioTDataRepository;
        logger.info("✅ FirebaseService initialized successfully.");
    }

    @Transactional
    public String saveIoTData(IoTData data) throws ExecutionException, InterruptedException {
        // ✅ ตรวจสอบค่าพิกัดก่อนบันทึกข้อมูล
        if (data.getLatitude() < -90 || data.getLatitude() > 90 ||
                data.getLongitude() < -180 || data.getLongitude() > 180) {
            return "❌ Invalid latitude/longitude values!";
        }

        // ✅ ตัดทศนิยมให้เหลือ 4 ตำแหน่ง
        data.setLatitude(roundToFourDecimalPlaces(data.getLatitude()));
        data.setLongitude(roundToFourDecimalPlaces(data.getLongitude()));

        // ✅ ตรวจสอบว่าข้อมูลซ้ำหรือไม่
        if (ioTDataRepository.existsByLongitudeAndLatitude(data.getLongitude(), data.getLatitude())) {
            return "❌ Data already exists at latitude: " + data.getLatitude() + ", longitude: " + data.getLongitude();
        }

        // ✅ ใช้ SQL Timestamp แทน Reflection
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        data.setCreateDate(currentTimestamp);
        data.setUpdateDate(currentTimestamp);

        // ✅ ตรวจสอบ ID และป้องกัน null
        Long lastId = ioTDataRepository.findLastId();
        long nextId = (lastId != null) ? lastId + 1 : 1;
        data.setId(nextId);

        // ✅ บันทึกข้อมูลลงฐานข้อมูล
        IoTData savedData = ioTDataRepository.save(data);
        logger.info("✅ Data saved to MySQL: ID " + savedData.getId());

        // ✅ บันทึกข้อมูลลง Firestore
        ApiFuture<WriteResult> firestoreFuture = firestore.collection(COLLECTION_NAME)
                .document(String.valueOf(savedData.getId()))
                .set(savedData);

        // ✅ บันทึกข้อมูลลง Firebase Realtime Database (เพิ่ม Exception Handling)
        try {
            databaseReference.child(String.valueOf(savedData.getId())).setValueAsync(savedData);
        } catch (Exception e) {
            logger.severe("❌ Error saving to Firebase Realtime Database: " + e.getMessage());
        }

        // ✅ ดึงเวลาอัปเดตจาก Firestore (ป้องกัน Null Pointer Exception)
        WriteResult firestoreWriteResult = firestoreFuture.get();
        String updateTime = (firestoreWriteResult != null) ? firestoreWriteResult.getUpdateTime().toString() : "Unknown";

        return "✅ Saved with ID: " + savedData.getId() + ", Firestore Update Time: " + updateTime;
    }

    /**
     * เมธอดสำหรับตัดค่าทศนิยมให้เหลือ 4 ตำแหน่ง
     */
    private double roundToFourDecimalPlaces(double value) {
        return new BigDecimal(value).setScale(4, RoundingMode.HALF_UP).doubleValue();
    }


    public IoTDataGet getFromFirebase(Long Id) throws ExecutionException, InterruptedException {
        DocumentSnapshot document = firestore.collection(COLLECTION_NAME)
                .document(String.valueOf(Id))
                .get()
                .get();

        if (!document.exists()) {
            return null;
        }

        IoTDataGet data = document.toObject(IoTDataGet.class);
        if (data != null) {
            data.setCreateDate(document.getTimestamp("createDate"));
        }
        return data;
    }

    public List<IoTData> getAllIoTData() {
        return ioTDataRepository.findAll();
    }

    public String updateIoTData(Long id, IoTData newData) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future = firestore.collection(COLLECTION_NAME)
                .document(String.valueOf(id))
                .set(newData);

        databaseReference.child(String.valueOf(id)).setValueAsync(newData);

        return "✅ Updated ID: " + id + ", Firebase Update Time: " + future.get().getUpdateTime().toString();
    }

    public String deleteIoTData(Long id) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future = firestore.collection(COLLECTION_NAME)
                .document(String.valueOf(id)).delete();

        databaseReference.child(String.valueOf(id)).removeValueAsync();

        return "✅ Deleted ID: " + id + ", Firebase Deletion Time: " + future.get().getUpdateTime().toString();
    }

    public void fetchDataAsync(Consumer<Map<String, String>> callback) {
        Map<String, String> result = new HashMap<>();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    Integer value = child.child("value").getValue(Integer.class);
                    Long timestamp = child.child("timestamp").getValue(Long.class);

                    if (value != null && timestamp != null) {
                        result.put(String.valueOf(timestamp), getAlertMessage(value));
                    }
                }
                callback.accept(result);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.accept(result);
            }
        });
    }

    private String getAlertMessage(int value) {
        int baseCode = (value / 10) * 10;
        int subCode = value % 10;

        Map<Integer, String> alertTypes = Map.of(
                1000, "ต้นนี้มีปัญหา: ",
                2000, "อุณหภูมิผิดปกติ: ",
                3000, "ความชื้นผิดปกติ: "
        );

        Map<Integer, String> reasons = Map.of(
                1, "ต้นนี้เอียงเกินไป",
                2, "เกิดแรงสั่นมากเกินไป",
                3, "มีเสียงดังบริเวณนี้"
        );

        return alertTypes.getOrDefault(baseCode, "ไม่ทราบประเภท: ") +
                reasons.getOrDefault(subCode, "ไม่ทราบสาเหตุ");
    }

    public List<Map<String, Object>> getBoardData() throws ExecutionException, InterruptedException {
        Map<String, Map<String, Object>> boardMap = new HashMap<>();

        // Log เช็คว่า Firestore ดึงข้อมูลได้ไหม
        System.out.println("🔍 Fetching Boards from Firestore...");

        for (QueryDocumentSnapshot document : firestore.collection("boards").get().get().getDocuments()) {
            System.out.println("📌 Found Board: " + document.getString("id") + " - " + document.getString("name"));

            Map<String, Object> boardInfo = new HashMap<>();
            boardInfo.put("name", document.getString("name"));
            boardInfo.put("latitude", document.getDouble("latitude"));
            boardInfo.put("longitude", document.getDouble("longitude"));
            boardMap.put(document.getString("id"), boardInfo);
        }

        if (boardMap.isEmpty()) {
            System.out.println("⚠️ No Boards Found in Firestore!");
        }

        return new ArrayList<>();
    }

    private String getStatusMessage(Integer value) {
        if (value == null) return "สถานะไม่ทราบ";

        String statusMessage = switch (value % 10) {
            case 1 -> "ต้นไม้เอียงผิดปกติ";
            case 2 -> "เกิดแรงสั่นที่ผิดปกติ";
            case 3 -> "มีแรงสั่นที่ผิดปกติ ปานกลาง(ตรวจสอบด่วน)";
            case 5 -> "ต้นไม้นี้ล้มโดนตัดแล้ว!(ตรวจสอบด่วน)";
            case 4 -> "มีเสียงดังผิดปกติ";
            case 6 -> "มีการสั่นผิดปกติ มาก(ตรวจสอบด่วน)";
            default -> "สถานะไม่ทราบ";
        };

        return new String(statusMessage.getBytes(), Charset.forName("UTF-8"));
    }



    public CompletableFuture<List<Map<String, Object>>> fetchTemperatureData() {
        CompletableFuture<List<Map<String, Object>>> future = new CompletableFuture<>();
        List<Map<String, Object>> resultList = new ArrayList<>();

        System.out.println("🔍 Fetching Data from Realtime Database...");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println("📊 Snapshot exists? " + snapshot.exists());

                if (!snapshot.exists()) {
                    System.out.println("⚠️ No data found in Realtime Database!");
                    future.complete(resultList);
                    return;
                }

                for (DataSnapshot child : snapshot.getChildren()) {
                    Map<String, Object> data = new HashMap<>();
                    String timestamp = child.child("timestamp").getValue(String.class);
                    Integer value = child.child("value").getValue(Integer.class);

                    if (timestamp != null && value != null) {
                        // แยกค่า 2 หลักแรกของ Value เป็น Board ID
                        int boardId = value / 100; // เช่น 102 -> 10 -> Board ID 1

                        data.put("boardId", boardId); // เพิ่ม Board ID ลงใน JSON
                        data.put("timestamp", timestamp);
                        data.put("value", value);
                        data.put("status", getStatusMessage(value));

                        System.out.println("📌 Found Data: " + data);
                        resultList.add(data);
                    }
                }
                future.complete(resultList);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("🔥 Firebase Error: " + error.getMessage());
                future.complete(resultList);
            }
        });

        return future;
    }





}

