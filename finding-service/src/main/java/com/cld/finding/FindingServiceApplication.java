package com.cld.finding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class FindingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FindingServiceApplication.class, args);
	}

}
