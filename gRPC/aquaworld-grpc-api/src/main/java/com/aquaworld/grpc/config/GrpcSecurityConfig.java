package com.aquaworld.grpc.config;

import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * gRPC Security Configuration
 * Registers JWT authentication interceptor globally
 */
@Configuration
public class GrpcSecurityConfig {

    @Bean
    @GrpcGlobalServerInterceptor
    public JwtAuthenticationInterceptor jwtAuthenticationInterceptor(com.aquaworld.grpc.util.JwtTokenProvider tokenProvider) {
        return new JwtAuthenticationInterceptor(tokenProvider);
    }
}
