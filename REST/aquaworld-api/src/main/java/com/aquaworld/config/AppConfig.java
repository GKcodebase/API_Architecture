package com.aquaworld.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Application Configuration for AquaWorld REST API
 *
 * This configuration class provides common beans used throughout the application:
 * - Password encoder for secure password storage
 * - CORS configuration for cross-origin requests
 *
 * @author AquaWorld Development Team
 */
@Configuration
public class AppConfig {

    /**
     * Password Encoder Bean
     *
     * Uses BCrypt algorithm to securely hash user passwords.
     * BCrypt automatically generates a salt and applies multiple rounds of hashing
     * making it resistant to brute-force attacks.
     *
     * @return PasswordEncoder bean configured with BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10); // Strength 10: 2^10 = 1024 iterations
    }

    /**
     * CORS Configuration Bean
     *
     * Enables Cross-Origin Resource Sharing (CORS) to allow requests from different origins.
     * This is essential for frontend applications running on different hosts/ports.
     *
     * Configuration:
     * - Allowed origins: http://localhost:3000 (typical React dev server)
     * - Allowed HTTP methods: GET, POST, PUT, DELETE, OPTIONS
     * - Allowed headers: All headers
     * - Credentials: Allowed (cookies, authorization headers)
     * - Max age: 3600 seconds (1 hour)
     *
     * @return CORS configuration source
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow requests from these origins (add production URL later)
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:4200",
                "http://localhost:8080"
        ));

        // Allow these HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Allow all headers
        configuration.setAllowedHeaders(List.of("*"));

        // Allow credentials (cookies, authorization)
        configuration.setAllowCredentials(true);

        // Cache preflight response for 1 hour
        configuration.setMaxAge(3600L);

        // Register this configuration for all paths
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
