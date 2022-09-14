package com.example.week7project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableJpaAuditing
public class Week7ProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(Week7ProjectApplication.class, args);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedOrigins("http://localhost:3000", "http://1336.s3-website.ap-northeast-2.amazonaws.com")
						.exposedHeaders("Authorization", "RefreshToken")
						.allowedMethods("*")
						.allowedHeaders("*")
						.allowCredentials(true)
						.maxAge(3600);
			}
		};
	}
}
