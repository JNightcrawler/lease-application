package com.leaseapp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI Configuration for Swagger UI.
 * Access Swagger UI at: http://localhost:8080/swagger-ui.html
 * OpenAPI JSON at: http://localhost:8080/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI leaseApplicationApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Lease Application API")
                        .description("A Spring Boot backend for managing rental/lease business: material inventory, customer orders, and per-material lending records.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Lease Application Team")
                                .url("https://github.com")
                                .email("support@leaseapp.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
