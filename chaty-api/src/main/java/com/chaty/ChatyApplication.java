package com.chaty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableAsync
@EnableRetry
@SpringBootApplication
public class ChatyApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatyApplication.class, args);
	}

}
