package com.tech.test.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Online Test Management API")
                                .description(
                                        "Comprehensive API documentation for Online Test Management System including student management, exam operations, and image upload functionality")
                                .version("1.0.0")
                                .contact(
                                        new Contact()
                                                .name("API Support")
                                                .email("support@techtest.com")
                                                .url("https://techtest.com"))
                                .license(
                                        new License()
                                                .name("Apache 2.0")
                                                .url(
                                                        "https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(
                        List.of(
                                new Server()
                                        .url("http://localhost:8089")
                                        .description("Development Server")))
                .components(
                        new Components()
                                .addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"));
    }

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer")
                .description("Enter JWT token (without 'Bearer ' prefix)");
    }
}
