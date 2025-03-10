package com.example.website_login_1.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;

import static com.example.website_login_1.constant.WebsiteLoginConstants.KafkaConstants.NOTIFICATION_EMAIL_TOPIC;

//@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic topic() {
        return TopicBuilder.name(NOTIFICATION_EMAIL_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
