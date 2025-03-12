package com.example.website_login_1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@EnableJms
@SpringBootApplication
public class WebsiteLogin1Application {

	public static void main(String[] args) {
		SpringApplication.run(WebsiteLogin1Application.class, args);
	}

}
