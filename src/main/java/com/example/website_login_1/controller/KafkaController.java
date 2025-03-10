package com.example.website_login_1.controller;

import com.example.website_login_1.messaging.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.example.website_login_1.constant.WebsiteLoginConstants.KafkaConstants.NOTIFICATION_EMAIL_TOPIC;

@RequiredArgsConstructor
//@RestController
@RequestMapping("/kafka")
public class KafkaController {

    private final KafkaProducer kafkaProducer;

    private Integer kafkaKey = 0;

    @PostMapping("/send")
    public String sendMessage(@RequestParam String message) {
        kafkaKey++;
        kafkaProducer.sendMessage(NOTIFICATION_EMAIL_TOPIC, message, kafkaKey.toString());
        return "Message sent to Kafka topic: " + NOTIFICATION_EMAIL_TOPIC;
    }
}
