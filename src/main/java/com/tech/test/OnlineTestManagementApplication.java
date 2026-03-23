package com.tech.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class OnlineTestManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnlineTestManagementApplication.class, args);
	}

}
