package com.example.reporting_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ReportingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReportingSystemApplication.class, args);
	}

}
