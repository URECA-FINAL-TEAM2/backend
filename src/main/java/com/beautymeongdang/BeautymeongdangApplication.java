package com.beautymeongdang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableJpaAuditing
@SpringBootApplication
@EnableAsync
@EnableRetry
public class BeautymeongdangApplication {

	public static void main(String[] args) {
		SpringApplication.run(BeautymeongdangApplication.class, args);
	}

}
