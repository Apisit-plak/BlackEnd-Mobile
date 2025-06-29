package com.example.IOT_HELL.Exception;

import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    //ดักเมื่อ login username password ไม่ถูกต้อง
    @ExceptionHandler(AlreadyExistsException.BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentialsException(AlreadyExistsException.BadCredentialsException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Login");  // เปลี่ยน key เป็น "error"
        response.put("message", e.getMessage()); // ข้อความจาก exception
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }



    //ดัก ID ที่ไม่มี ในData Base
    @ExceptionHandler
    public ResponseEntity<String> ExceptionServiceImpl(AlreadyExistsException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.CONFLICT);
    }


    //ดักค่าซ้ำใน Data Base
    @ExceptionHandler(AlreadyExistsException.UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleUserAlreadyExistsException(AlreadyExistsException.UserAlreadyExistsException exception) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.CONFLICT.value());
        response.put("message", "User creation failed due to existing values");
        response.put("errors", exception.getErrors());

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AlreadyExistsException.DataNotFoundException.class)
    public ResponseEntity<Map<String, Object>> DataNotFoundException(AlreadyExistsException.DataNotFoundException exception) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("message", exception.getMessage());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<Map<String, Object>> handleSignatureException(SignatureException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", HttpStatus.UNAUTHORIZED.value());
        errorResponse.put("error", "Token หมดอายุ หรือ ไม่ถูกต้อง");
        errorResponse.put("message", ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }



}

