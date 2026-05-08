package com.webpage.T3D.outer.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // This links the "/uploads/" URL to your physical "uploaded-models" folder
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploaded-models/");
    }

    // 🚨 THIS IS THE MISSING KEY: Allow Angular to download physical files!
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/uploads/**")
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("GET");
    }
}