package com.example.website_login_1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WebsiteLoginConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
