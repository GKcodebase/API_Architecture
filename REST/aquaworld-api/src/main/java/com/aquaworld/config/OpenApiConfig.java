package com.aquaworld.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger Configuration for AquaWorld REST API
 *
 * This configuration class sets up API documentation using OpenAPI 3.0 standard.
 * It provides interactive API documentation accessible at /swagger-ui.html
 *
 * Features:
 * - API metadata (title, description, version)
 * - Contact information
 * - Security scheme configuration (Bearer JWT token)
 *
 * @author AquaWorld Development Team
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configures OpenAPI documentation for AquaWorld API
     *
     * @return OpenAPI configuration bean
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // API Information
                .info(new Info()
                        .title("AquaWorld Pet Store REST API")
                        .version("1.0.0")
                        .description("Comprehensive REST API for AquaWorld online guppy fish pet store. " +
                                "This API provides endpoints for user authentication, product management, " +
                                "order processing, and payment handling.")
                        .contact(new Contact()
                                .name("AquaWorld Support Team")
                                .email("support@aquaworld.com")
                                .url("https://www.aquaworld.com")))
                // Security Scheme - Bearer JWT Token
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter JWT token")))
                // Apply security globally
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"));
    }
}
