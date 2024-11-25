package com.volley.microservices.notification;

import org.springframework.boot.SpringApplication;

import com.volley.microservices.NotificationServiceApplication;

public class TestNotificationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(NotificationServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
