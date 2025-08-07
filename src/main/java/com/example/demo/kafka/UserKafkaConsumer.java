package com.example.demo.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserKafkaConsumer {

    @KafkaListener(topics = "user-topic", groupId = "my-group")
    public void listen(String message) {
        System.out.println("📩 Received Kafka message: " + message);
    }
}