package com.example.IOT_HELL.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); // ✅ เปิด Message Broker
        config.setApplicationDestinationPrefixes("/app"); // ✅ Prefix สำหรับ Client ส่งข้อมูล
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // ✅ WebSocket Endpoint
                .setAllowedOrigins("*") // ✅ อนุญาตทุก Origin
                .withSockJS(); // ✅ รองรับ SockJS (แก้ปัญหาเบราว์เซอร์บางตัวบล็อก WebSocket)
    }
}


