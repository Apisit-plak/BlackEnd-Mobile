package com.example.IOT_HELL.controller;


import com.example.IOT_HELL.constants.ErrorMessage;
import com.example.IOT_HELL.database.entity.IoTData;
import com.example.IOT_HELL.database.jpaRepo.IoTDataRepository;
import com.example.IOT_HELL.payload.request.*;
import com.example.IOT_HELL.payload.response.*;
import com.example.IOT_HELL.service.Impl.FirebaseService;
import com.example.IOT_HELL.service.Impl.IotServiceImpl;
import com.example.IOT_HELL.service.Impl.NotificationService;
import com.example.IOT_HELL.service.IotService;
import com.example.IOT_HELL.service.UsersService;
import com.example.IOT_HELL.utils.ResponseHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.SetOptions;
import com.google.firebase.cloud.FirestoreClient;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import com.google.cloud.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@CrossOrigin
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UsersService usersService;
    private final FirebaseService firebaseService;
    private final IotService iotService;
    private final NotificationService notificationService;
    private final IotServiceImpl iotServiceimpl;
    private final IoTDataRepository ioTDataRepository;


    @PostMapping("/create")
    private ResponseEntity<Object> createUser(@RequestBody @Valid UsersCreateRequest usersCreateRequest) {
        usersService.createUser(usersCreateRequest);
        return ResponseHelper.success(ErrorMessage.MESSAGE.INF_US_0000.getMsg());
    }

    //    getAllUser
    @GetMapping(value = "/list")
    public ResponseEntity<Object> getAllUser() {
        List<UsersResponseAll> response = usersService.getAllUsers();
        return ResponseHelper.successWithList(ErrorMessage.MESSAGE.INF_US_0000.getMsg(), response);
    }

    @GetMapping(value = "/Tree888")
    public ResponseEntity<Object> getAllTree() {
        List<TreeResponseAll> response = iotService.getAllTree();
        System.out.println(response);
        return ResponseHelper.successData(ErrorMessage.MESSAGE.INF_US_0000.getMsg(), response);
    }



    //    updateUser
    @PostMapping("/update/{id}")
    private ResponseEntity<Object> updateUser(@PathVariable("id") long id, @RequestBody UsersUpdateRequest request) throws Exception {
        usersService.updateUser(id, request);
        return ResponseHelper.success(ErrorMessage.MESSAGE.INF_US_0000.getMsg());
    }

    //        deleteUser
    @PostMapping("/delete/{id}")
    private ResponseEntity<Object> deleteUser(@PathVariable("id") long id) throws Exception {
        usersService.deleteUser(id);
        return ResponseHelper.success(ErrorMessage.MESSAGE.INF_US_0000.getMsg());
    }

    //login
    @PostMapping("/login")
    private ResponseEntity<Object> login(@RequestBody UserLoginRequest user) {
        String response = usersService.verify(user);
        return ResponseHelper.successWithList("Login successful", response);
    }

    //    searchUser
    @PostMapping(value = "/search")
    public ResponseEntity<Object> filterUser(@RequestBody UsersSearchSortReq request) {
        UsersResponseSearch response = usersService.getAllUsersSearch(request);
        return ResponseHelper.successPage(ErrorMessage.MESSAGE.INF_US_0000.getMsg(), response.getTotalPage(), response.getTotal());
    }


    @PostMapping("/save")
    public String saveIoTData(@RequestBody IoTData data) throws ExecutionException, InterruptedException {
        return firebaseService.saveIoTData(data);
    }

    @PostMapping("/editIoTData/{id}")
    public ResponseEntity<?> editIoTData(@PathVariable Long id, @RequestBody BoardRequest request) {
        Optional<IoTData> optionalData = ioTDataRepository.findById(id);
        if (optionalData.isPresent()) {
            IoTData data = optionalData.get();
            data.setLatitude(request.getLatitude());
            data.setLongitude(request.getLongitude());

            // ✅ อัปเดต MySQL
            ioTDataRepository.save(data);

            // ✅ อัปเดต Firestore
            Firestore db = FirestoreClient.getFirestore();
            DocumentReference docRef = db.collection("forest").document(String.valueOf(id));
            Map<String, Object> updateData = new HashMap<>();
            updateData.put("latitude", request.getLatitude());
            updateData.put("longitude", request.getLongitude());
            updateData.put("updateDate", Timestamp.now());
            docRef.set(updateData, SetOptions.merge());

            return ResponseEntity.ok(Collections.singletonMap("message", "IoT Data updated successfully in MySQL and Firestore"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "IoT Data not found"));
    }


    @GetMapping("/firebase/{Id}")
    public ResponseEntity<?> getIotData(@PathVariable Long Id) throws ExecutionException, InterruptedException {
        Optional<IoTDataGet> data = Optional.ofNullable(firebaseService.getFromFirebase(Id));

        if (data.isPresent()) {
            return ResponseEntity.ok(data.get());
        } else {
            return ResponseEntity.ok(Collections.emptyMap()); // ✅ ส่ง JSON ว่างแทนข้อความเปล่า
        }
    }


    @GetMapping("/all")
    public List<IoTData> getAllIoTData() {
        return firebaseService.getAllIoTData();
    }

    @GetMapping("/alerts")
    public void getAlerts(HttpServletResponse response) throws IOException {
        firebaseService.fetchDataAsync(result -> {
            try {
                response.setContentType("application/json");
                response.getWriter().write(new ObjectMapper().writeValueAsString(result));
                response.setStatus(HttpServletResponse.SC_OK);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<?> getProfile(@PathVariable long id) {
        try {
            UsersResponseAll userProfile = usersService.getUserProfile(id);
            return ResponseEntity.ok(userProfile);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }









}

