package com.board;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
public class RandomConfig {

	@Bean
    public Random random() {
        return new Random(); // Random 객체를 빈으로 등록
    }
}

