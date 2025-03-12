package com.example.website_login_1.controller;

import com.example.website_login_1.messaging.ActiveMqProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.example.website_login_1.constant.WebsiteLoginConstants.ActiveMqConstants.NOTIFICATION_EMAIL_QUEUE;
import static com.example.website_login_1.constant.WebsiteLoginConstants.KafkaConstants.NOTIFICATION_EMAIL_TOPIC;

@RequiredArgsConstructor
@RestController
@RequestMapping("/messaging")
public class MessagingController {

    //private final KafkaProducer kafkaProducer;
    private final ActiveMqProducer activeMqProducer;

    private Integer kafkaKey = 0;

    /*@PostMapping("/kafka/send")
    public String sendKafkaMessage(@RequestParam String message) {
        kafkaKey++;
        kafkaProducer.sendMessage(NOTIFICATION_EMAIL_TOPIC, message, kafkaKey.toString());
        return "Message sent to Kafka topic: " + NOTIFICATION_EMAIL_TOPIC;
    }*/

    @PostMapping("/activemq/send")
    public String sendActiveMqMessage(@RequestParam String message) {
        activeMqProducer.sendMessage(NOTIFICATION_EMAIL_QUEUE, message);
        return "Message sent to ActiveMq queue: " + NOTIFICATION_EMAIL_TOPIC;
    }
}
