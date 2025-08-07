package com.example.demo.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserKafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendUserCreateMessage(String message) {
        kafkaTemplate.send("user-topic", message); // "user-topic"은 Kafka 토픽 이용
    }
}
