package com.example.IOT_HELL.service;

import com.example.IOT_HELL.database.entity.IoTData;
import com.example.IOT_HELL.payload.request.BoardRequest;
import com.example.IOT_HELL.payload.response.TreeResponseAll;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public interface IotService {

    List<TreeResponseAll> getAllTree();

    String updateIoTData(Long id, BoardRequest request) throws ExecutionException, InterruptedException;
}
