package com.aquaworld.graphql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot Application for AquaWorld GraphQL API
 * 
 * This application provides a GraphQL API for the AquaWorld pet store
 * specializing in premium guppies and aquatic accessories.
 * 
 * Features:
 * - GraphQL queries for product browsing
 * - GraphQL mutations for orders and payments
 * - JWT-based authentication
 * - Interactive GraphiQL playground
 * - In-memory data storage
 * 
 * @author AquaWorld Development Team
 * @version 1.0.0
 */
@SpringBootApplication
public class GraphQLApplication {

    public static void main(String[] args) {
        SpringApplication.run(GraphQLApplication.class, args);
        
        System.out.println("\n" +
            "╔════════════════════════════════════════════════════════════╗\n" +
            "║     🐠 AquaWorld GraphQL API - Started Successfully 🐠     ║\n" +
            "╚════════════════════════════════════════════════════════════╝\n");
        
        System.out.println("📊 GraphQL Endpoint:");
        System.out.println("   🔗 http://localhost:8080/aquaworld/graphql\n");
        
        System.out.println("🎮 Interactive Playground:");
        System.out.println("   🔗 http://localhost:8080/aquaworld/graphiql\n");
        
        System.out.println("📚 Sample Products:");
        System.out.println("   ✓ Red Guppy Male - Premium ($5.99)");
        System.out.println("   ✓ Blue Guppy Male ($6.49)");
        System.out.println("   ✓ Premium Guppy Food ($8.99)");
        System.out.println("   ✓ 10 Gallon Tank ($49.99)\n");
        
        System.out.println("👤 Sample Users:");
        System.out.println("   ✓ john / john@123 (Customer)");
        System.out.println("   ✓ admin / admin@123 (Admin)");
        System.out.println("   ✓ jane / jane@123 (Customer)\n");
        
        System.out.println("🌐 Try your first query:");
        System.out.println("   query { products { id name price } }\n");
    }
}
