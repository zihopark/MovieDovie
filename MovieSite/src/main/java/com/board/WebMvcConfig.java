package com.board;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer{
	
	@Override
	public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/profile/**")
				.addResourceLocations("file:///C:/Repository/moviedovie/profile/");
	}
}