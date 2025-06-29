package com.example.IOT_HELL.service.Impl;

import com.example.IOT_HELL.database.entity.IoTData;
import com.example.IOT_HELL.database.jpaRepo.IoTDataRepository;
import com.example.IOT_HELL.payload.request.BoardRequest;
import com.example.IOT_HELL.payload.response.TreeResponseAll;
import com.example.IOT_HELL.service.IotService;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.SetOptions;
import com.google.cloud.firestore.WriteResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class IotServiceImpl implements IotService {

    private final IoTDataRepository ioTDataRepository;

    private final Firestore firestore;

    @Override
    public List<TreeResponseAll> getAllTree() {
        System.out.println("From DataBase");
        List<IoTData> ioTData = ioTDataRepository.findAll();
        List<TreeResponseAll> response = new ArrayList<>();
        for (IoTData iotdata : ioTData) {
            TreeResponseAll res = new TreeResponseAll();
            res.setId(iotdata.getId())
                    .setLatitude(iotdata.getLatitude())
                    .setLongitude(iotdata.getLongitude());
            response.add(res);
        }
        return response;
    }
    @Override
    public String updateIoTData(Long id, BoardRequest request) throws ExecutionException, InterruptedException {
        // 🔹 1️⃣ อัปเดต MySQL Database
        Optional<IoTData> optionalIoTData = ioTDataRepository.findById(id);
        if (optionalIoTData.isEmpty()) {
            return "Board not found in MySQL";
        }

        IoTData ioTData = optionalIoTData.get();
        ioTData.setLatitude(request.getLatitude());
        ioTData.setLongitude(request.getLongitude());
        ioTDataRepository.save(ioTData);

        // 🔹 2️⃣ อัปเดต Firestore Database
        DocumentReference docRef = firestore.collection("iot_data").document(String.valueOf(id));

        Map<String, Object> updates = new HashMap<>();
        updates.put("latitude", request.getLatitude());
        updates.put("longitude", request.getLongitude());
        updates.put("updateDate", System.currentTimeMillis());

        ApiFuture<WriteResult> result = docRef.set(updates, SetOptions.merge());

        return "IoTData updated at: " + result.get().getUpdateTime();
    }
}

