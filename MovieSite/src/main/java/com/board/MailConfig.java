package com.board;

import java.util.Properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "spring.mail")
@Getter
@Setter
public class MailConfig {
	
	private String host;
	private String username;
	private String password;
	private Properties properties;

    @Bean
    JavaMailSender javaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(host);
		mailSender.setPort(587);
		mailSender.setUsername(username);
		mailSender.setPassword(password);
		mailSender.setJavaMailProperties(properties);
		
		return mailSender;
	}
}