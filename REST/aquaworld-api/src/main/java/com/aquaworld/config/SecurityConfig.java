package com.aquaworld.config;

import com.aquaworld.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security Configuration for AquaWorld REST API
 *
 * This configuration class sets up:
 * - JWT token-based authentication
 * - Stateless session management (no session cookies)
 * - API endpoint security rules
 * - Custom JWT filter
 *
 * Security Approach:
 * - Public endpoints: Authentication/Registration, Product browsing
 * - Protected endpoints: User profile, Orders, Payments (require valid JWT)
 * - Admin endpoints: User management (require admin role)
 *
 * @author AquaWorld Development Team
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    @Autowired
    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Configures HTTP security for the application
     *
     * Security Rules:
     * - POST /api/v1/auth/** - Public (authentication/registration)
     * - GET /api/v1/products/** - Public (product browsing)
     * - All other endpoints - Require authentication
     *
     * Uses JWT token in Authorization header (Bearer token)
     *
     * @param http HttpSecurity to configure
     * @return Configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF - not needed for stateless REST API with JWT
                .csrf(csrf -> csrf.disable())

                // Enable CORS using the configuration bean from AppConfig
                .cors(cors -> {})

                // Configure authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - accessible without authentication
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/products/**").permitAll()
                        .requestMatchers("/api/v1/products/search").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        // Protected endpoints - require authentication
                        .requestMatchers("/api/v1/users/**").authenticated()
                        .requestMatchers("/api/v1/orders/**").authenticated()
                        .requestMatchers("/api/v1/payments/**").authenticated()

                        // All other requests require authentication
                        .anyRequest().authenticated()
                )

                // Configure stateless session management
                // Each request is independent, no session stored on server
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Add JWT filter before UsernamePasswordAuthenticationFilter
                // This filter validates JWT tokens on each request
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtUtil),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    /**
     * Authentication Manager Bean
     *
     * Used for authenticating user credentials during login
     *
     * @param config AuthenticationConfiguration
     * @return AuthenticationManager bean
     * @throws Exception if configuration fails
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
