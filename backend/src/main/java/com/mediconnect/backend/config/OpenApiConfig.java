package com.mediconnect.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI mediconnectOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("MediConnect Backend API")
                .description("User feature CRUD endpoints")
                .version("1.0.0"));
    }
}