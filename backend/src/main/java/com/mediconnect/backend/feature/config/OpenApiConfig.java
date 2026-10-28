package com.mediconnect.backend.feature.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI mediConnectOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MediConnect Healthcare API")
                        .description("REST API documentation for MediConnect healthcare platform, including user synchronization and registration.")
                        .version("v1.0")
                        .contact(new Contact().name("MediConnect Team")));
    }
}

