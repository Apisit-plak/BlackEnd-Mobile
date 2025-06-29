package com.example.IOT_HELL;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class IotHellApplication {

	public static void main(String[] args) {
		SpringApplication.run(IotHellApplication.class, args);
	}

}
