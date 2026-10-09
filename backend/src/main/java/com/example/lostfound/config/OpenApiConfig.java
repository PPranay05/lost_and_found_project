package com.example.lostfound.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Lost & Found Portal with QR Code Verification API")
                        .version("1.0.0")
                        .description("RESTful Backend APIs for automated item reporting, matching, QR-code generation, QR camera scanning, ownership verification, claim lifecycle, and transaction tracking.")
                        .contact(new Contact()
                                .name("Academic Project Support")
                                .email("admin@campuslostfound.org")));
    }
}
