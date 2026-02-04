package com.aquaworld.grpc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot Application Class for AquaWorld gRPC API
 *
 * AquaWorld is an online pet store specialized in selling guppies and aquatic accessories.
 * This gRPC API provides comprehensive services for:
 * - User authentication and management
 * - Product catalog management
 * - Order processing and tracking
 * - Payment processing
 *
 * The application uses:
 * - Spring Boot 3.3.x with Java 21
 * - gRPC with Protocol Buffers
 * - JWT for authentication
 * - Spring Data JPA for data access
 * - H2 database for storage
 *
 * @author AquaWorld Development Team
 * @version 1.0.0
 */
@SpringBootApplication
public class AquaWorldGrpcApplication {

    public static void main(String[] args) {
        SpringApplication.run(AquaWorldGrpcApplication.class, args);
        System.out.println("\n🐠 ==========================================");
        System.out.println("🐠 AquaWorld Pet Store gRPC API");
        System.out.println("🐠 ==========================================");
        System.out.println("🐠 gRPC Server started on port 9090");
        System.out.println("🐠 Test with grpcurl: grpcurl -plaintext localhost:9090 list");
        System.out.println("🐠 ==========================================\n");
    }
}
