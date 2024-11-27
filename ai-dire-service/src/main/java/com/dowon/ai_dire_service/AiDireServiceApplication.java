package com.dowon.ai_dire_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.dowon.ai_dire_service", "com.dowon.common"})
public class AiDireServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiDireServiceApplication.class, args);
	}

}
