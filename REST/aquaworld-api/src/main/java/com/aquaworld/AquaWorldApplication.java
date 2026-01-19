package com.aquaworld;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot Application Class for AquaWorld REST API
 *
 * AquaWorld is an online pet store specialized in selling guppies and aquatic accessories.
 * This REST API provides comprehensive endpoints for:
 * - User authentication and management
 * - Product catalog management
 * - Order processing and tracking
 * - Payment processing
 *
 * The application uses:
 * - Spring Security with JWT for authentication
 * - In-memory data storage (ConcurrentHashMap) for demonstration
 * - Spring Boot 3.3.x with Java 21
 * - OpenAPI/Swagger documentation
 *
 * @author AquaWorld Development Team
 * @version 1.0.0
 */
@SpringBootApplication
public class AquaWorldApplication {

    public static void main(String[] args) {
        SpringApplication.run(AquaWorldApplication.class, args);
        System.out.println("🐠 AquaWorld Pet Store REST API started successfully!");
        System.out.println("📚 API Documentation: http://localhost:8080/aquaworld/swagger-ui.html");
    }
}
