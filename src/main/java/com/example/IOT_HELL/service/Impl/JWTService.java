package com.example.IOT_HELL.service.Impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;

@Service
public class JWTService {

    private static final String SECRET_KEY = Base64.getEncoder().encodeToString(Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded());

    // ✅ สร้าง Token พร้อม `id` และ `username`
    public static String generateToken(Long id, String username) {
        Date now = new Date();
        Date expirationDate = Date.from(Instant.now().plus(20, ChronoUnit.MINUTES));

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id); // ✅ ใส่ ID ลงใน JWT
        claims.put("username", username);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username) // ✅ `sub` = username
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(getKey()) // ✅ ใช้ SecretKey ที่ถูกต้อง
                .compact();
    }

    // ✅ ดึง SecretKey จาก `SECRET_KEY`
    private static SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    // ✅ ดึง `username` จาก Token
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // ✅ ดึง `id` จาก Token
    public String extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("id", String.class));
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    // ✅ ตรวจสอบว่า Token ถูกต้องและยังไม่หมดอายุ
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUserName(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
