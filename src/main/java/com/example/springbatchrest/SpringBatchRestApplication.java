package com.example.springbatchrest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SpringBatchRestApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatchRestApplication.class, args);
	}

}
