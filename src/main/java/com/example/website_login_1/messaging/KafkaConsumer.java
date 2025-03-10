package com.example.website_login_1.messaging;

//@Service
public class KafkaConsumer {

    //@KafkaListener(topics = NOTIFICATION_EMAIL_TOPIC, groupId = NOTIFICATION_EMAIL_TOPIC_CONSUMER_GROUP)
    public void listen(String message) {
        System.out.println("Received message: " + message);
    }
}
