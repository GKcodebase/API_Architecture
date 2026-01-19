package com.aquaworld.config;

import com.aquaworld.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * JWT Authentication Filter for AquaWorld REST API
 *
 * This filter is executed on every HTTP request to:
 * 1. Extract JWT token from Authorization header
 * 2. Validate the token using JwtUtil
 * 3. Extract username from the token
 * 4. Set the authenticated user in Spring Security context
 *
 * Flow:
 * Client Request
 *    ↓
 * Extract token from "Authorization: Bearer <token>" header
 *    ↓
 * Validate token (signature, expiration)
 *    ↓
 * Extract username from token claims
 *    ↓
 * Set authentication in SecurityContext
 *    ↓
 * Pass request to next filter/controller
 *
 * Note: This filter runs ONCE per request (extends OncePerRequestFilter)
 *
 * @author AquaWorld Development Team
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    /**
     * Constructor for JwtAuthenticationFilter
     *
     * @param jwtUtil the JWT utility for token validation
     */
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Filters each HTTP request to validate JWT token
     *
     * Process:
     * 1. Get Authorization header from request
     * 2. Check if header contains "Bearer" token
     * 3. Extract token from "Bearer <token>" format
     * 4. Validate token signature and expiration
     * 5. Extract username from token
     * 6. Create authentication token and set in SecurityContext
     * 7. Pass request to next filter
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @param filterChain the filter chain
     * @throws ServletException if servlet error occurs
     * @throws IOException if I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // Get Authorization header from request
            // Expected format: "Authorization: Bearer <jwt_token>"
            String authHeader = request.getHeader("Authorization");

            // Check if Authorization header exists and starts with "Bearer "
            if (authHeader != null && authHeader.startsWith("Bearer ")) {

                // Extract token (remove "Bearer " prefix)
                // Example: "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                //           -> "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                String token = authHeader.substring(7);

                // Validate token (check signature and expiration)
                if (jwtUtil.validateToken(token)) {

                    // Extract username from token claims
                    String username = jwtUtil.extractUsername(token);

                    // Create authentication token
                    // UsernamePasswordAuthenticationToken with:
                    // - Principal: username
                    // - Credentials: null (not needed, token already validated)
                    // - Authorities: empty list (no roles in this implementation)
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    username,           // principal
                                    null,              // credentials
                                    new ArrayList()    // authorities/roles
                            );

                    // Set authentication in Spring Security context
                    // This makes the username available to controllers via @AuthenticationPrincipal
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Log error (optional) and continue without setting authentication
            // Secured endpoints will fail due to missing authentication
            System.err.println("JWT Filter Error: " + e.getMessage());
        }

        // Continue the filter chain regardless of JWT validation result
        // Secured endpoints will be rejected by Spring Security if not authenticated
        filterChain.doFilter(request, response);
    }
}
