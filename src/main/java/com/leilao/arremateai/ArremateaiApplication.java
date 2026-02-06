package com.leilao.arremateai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ArremateaiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArremateaiApplication.class, args);
	}

}
