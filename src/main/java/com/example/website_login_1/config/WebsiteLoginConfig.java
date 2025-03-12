package com.example.website_login_1.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WebsiteLoginConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        // Register modules for better Java 8+ support
        objectMapper.registerModule(new JavaTimeModule());    // Java 8 date/time types
        objectMapper.registerModule(new Jdk8Module());        // Java 8 features like Optional
        objectMapper.registerModule(new ParameterNamesModule()); // Better constructor handling

        // Date/time handling
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Null handling
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // Don't include null values

        // Error handling
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // Ignore unknown properties
        objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true); // Handle unknown enum values

        // Precision features
        objectMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS); // Better precision for decimals

        // Output formatting
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // Pretty printing

        return objectMapper;
    }

}
