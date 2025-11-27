package com.example.AsteroidAlertNotificationService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AsteroidAlertNotificationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AsteroidAlertNotificationServiceApplication.class, args);
	}

}
