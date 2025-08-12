package com.example.demo.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserKafkaConsumer {

    @KafkaListener(topics = "user-joined", groupId = "demo-group")
    public void handleUserJoined(ConsumerRecord<String, String> record) {
        String message = record.value();
        System.out.println("kafaka 메지지 수신됨: " + message);
    }

    @KafkaListener(topics = "user-logged-in", groupId = "demo-group")
    public void handleUserLoggedIn(ConsumerRecord<String, String> record) {
        String userId = record.value();
        System.out.println("로그인 이벤트 수신: " + userId);
    }
}