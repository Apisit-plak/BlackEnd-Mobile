package com.example.IOT_HELL.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static com.example.IOT_HELL.constants.Constants.RequestHeader.REQUEST_DATE;
import static com.example.IOT_HELL.constants.Constants.RequestHeader.RESPONSE_DATE;



public class ResponseHelper {
    public static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    private static final String MESSAGE = "message";
    private static final String STATUS = "status";
    private static final String TOKEN = "tokens";

    private ResponseHelper() {
        throw new IllegalStateException("Utility class");
    }

    public static ResponseEntity<Object> response(HttpStatus httpStatus, Object object) {
        HttpHeaders httpHeaders = new HttpHeaders();
        if (RequestContextHolder.getRequestAttributes() != null) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            httpHeaders.add(REQUEST_DATE, request.getHeader(REQUEST_DATE));
        }

        httpHeaders.add(RESPONSE_DATE, LocalDateTime.now().format(timeFormatter));
        return ResponseEntity.status(httpStatus).headers(httpHeaders).body(object);
    }

    public static ResponseEntity<Object> success(String message) {
        Map<String, Object> data = new HashMap<>();
        data.put(MESSAGE, message);
        data.put(STATUS, true);
        return response(HttpStatus.OK, data);
    }

    public static ResponseEntity<Object> successData(String message, Object responseData) {
        Map<String, Object> data = new HashMap<>();
        data.put("message", message);
        data.put("status", true);
        data.put("data", responseData);
        return response(HttpStatus.OK, data);
    }


    public static ResponseEntity<Object> successWithList(String message, Object obj) {
        Map<String, Object> data = new HashMap<>();
        data.put(MESSAGE, message);
        data.put(STATUS, true);
        data.put(TOKEN, obj);
        return response(HttpStatus.OK, data);
    }
    public static ResponseEntity<Object> successPage(String message, long totalPage, long total) {
        Map<String, Object> data = new HashMap<>();
        data.put(MESSAGE, message);
        data.put(STATUS, true);
        data.put("totalPage", totalPage);
        data.put("total", total);

        return response(HttpStatus.OK, data);
    }
}
