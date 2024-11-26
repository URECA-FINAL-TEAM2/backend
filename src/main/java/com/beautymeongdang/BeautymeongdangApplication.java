package com.beautymeongdang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BeautymeongdangApplication {

	public static void main(String[] args) {
		SpringApplication.run(BeautymeongdangApplication.class, args);
	}

}
