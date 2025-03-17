package com.board;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MovieSiteApplication {

	public static void main(String[] args) {
		SpringApplication.run(MovieSiteApplication.class, args);
	}
}