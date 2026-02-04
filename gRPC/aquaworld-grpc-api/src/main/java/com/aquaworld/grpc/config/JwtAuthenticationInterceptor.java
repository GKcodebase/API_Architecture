package com.aquaworld.grpc.config;

import com.aquaworld.grpc.util.JwtTokenProvider;
import io.grpc.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JWT Authentication Interceptor for gRPC
 * Validates JWT tokens from gRPC metadata
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationInterceptor implements ServerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationInterceptor.class);
    private final JwtTokenProvider tokenProvider;

    private static final Context.Key<String> USERNAME_CONTEXT_KEY = Context.key("username");

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        try {
            String methodName = call.getMethodDescriptor().getFullMethodName();
            log.debug("Processing gRPC call: {}", methodName);

            // Skip authentication for public endpoints and reflection services
            if (methodName.contains("AuthService/Login") || 
                methodName.contains("AuthService/Register") ||
                methodName.endsWith("/Login") ||
                methodName.endsWith("/Register") ||
                methodName.contains("ServerReflection") ||
                methodName.contains("grpc.health")) {
                log.debug("Skipping authentication for public endpoint: {}", methodName);
                return next.startCall(call, headers);
            }

            // Extract token from metadata
            String authHeader = headers.get(Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER));

            if (authHeader == null || authHeader.isEmpty()) {
                throw new StatusRuntimeException(Status.UNAUTHENTICATED.withDescription("Missing authentication token"));
            }

            String token = authHeader;
            if (authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }

            // Validate token
            if (!tokenProvider.validateToken(token)) {
                throw new StatusRuntimeException(Status.UNAUTHENTICATED.withDescription("Invalid or expired token"));
            }

            // Extract username from token
            String username = tokenProvider.getUsernameFromToken(token);
            if (username == null || username.isEmpty()) {
                throw new StatusRuntimeException(Status.UNAUTHENTICATED.withDescription("Invalid token"));
            }

            // Create context with username
            Context context = Context.current().withValue(USERNAME_CONTEXT_KEY, username);

            // Continue with authenticated context
            return Contexts.interceptCall(context, call, headers, next);

        } catch (StatusRuntimeException e) {
            call.close(e.getStatus(), new Metadata());
            return new ServerCall.Listener<ReqT>() {};
        } catch (Exception e) {
            log.error("Authentication error: {}", e.getMessage());
            call.close(Status.UNAUTHENTICATED.withDescription(e.getMessage()), new Metadata());
            return new ServerCall.Listener<ReqT>() {};
        }
    }

    /**
     * Get authenticated username from context
     */
    public static String getAuthenticatedUsername() {
        return USERNAME_CONTEXT_KEY.get();
    }
}
