package com.a407.back.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("https://i10a407.p.ssafy.io", "http://localhost:3000").allowedMethods("*")
            .allowedHeaders("*").maxAge(3000).allowCredentials(true).exposedHeaders("Set-Cookie");
    }
    
}