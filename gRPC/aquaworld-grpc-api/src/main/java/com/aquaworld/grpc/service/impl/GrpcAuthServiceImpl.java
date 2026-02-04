package com.aquaworld.grpc.service.impl;

import com.aquaworld.grpc.auth.AuthServiceGrpc;
import com.aquaworld.grpc.auth.LoginRequest;
import com.aquaworld.grpc.auth.LoginResponse;
import com.aquaworld.grpc.auth.RegisterRequest;
import com.aquaworld.grpc.auth.RegisterResponse;
import com.aquaworld.grpc.auth.TokenRequest;
import com.aquaworld.grpc.auth.TokenResponse;
import com.aquaworld.grpc.auth.RefreshTokenRequest;
import com.aquaworld.grpc.auth.User;
import com.aquaworld.grpc.exception.GrpcExceptionHandler;
import com.aquaworld.grpc.service.AuthenticationService;
import com.aquaworld.grpc.util.JwtTokenProvider;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.ZoneId;

/**
 * gRPC Authentication Service Implementation
 * Implements AuthService defined in auth.proto
 */
@GrpcService
@RequiredArgsConstructor
public class GrpcAuthServiceImpl extends AuthServiceGrpc.AuthServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(GrpcAuthServiceImpl.class);

    private final AuthenticationService authenticationService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void login(LoginRequest request, StreamObserver<LoginResponse> responseObserver) {
        try {
            log.info("Login request for user: {}", request.getUsername());

            var user = authenticationService.authenticate(request.getUsername(), request.getPassword());
            var token = jwtTokenProvider.generateToken(user.getUsername());

            var userProto = User.newBuilder()
                    .setUserId(user.getUserId())
                    .setUsername(user.getUsername())
                    .setEmail(user.getEmail())
                    .setFullName(user.getFullName())
                    .setCreatedAt(user.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant().getEpochSecond())
                    .build();

            var response = LoginResponse.newBuilder()
                    .setToken(token)
                    .setRefreshToken(token)
                    .setExpiresIn(86400000)
                    .setUser(userProto)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Login failed: {}", e.getMessage());
            responseObserver.onError(GrpcExceptionHandler.handle(e));
        }
    }

    @Override
    public void register(RegisterRequest request, StreamObserver<RegisterResponse> responseObserver) {
        try {
            log.info("Register request for user: {}", request.getUsername());

            var user = authenticationService.register(request.getUsername(), request.getEmail(),
                    request.getPassword(), request.getFullName());

            var userProto = User.newBuilder()
                    .setUserId(user.getUserId())
                    .setUsername(user.getUsername())
                    .setEmail(user.getEmail())
                    .setFullName(user.getFullName())
                    .setCreatedAt(user.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant().getEpochSecond())
                    .build();

            var response = RegisterResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("User registered successfully")
                    .setUser(userProto)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Registration failed: {}", e.getMessage());
            responseObserver.onError(GrpcExceptionHandler.handle(e));
        }
    }

    @Override
    public void validateToken(TokenRequest request, StreamObserver<TokenResponse> responseObserver) {
        try {
            log.info("Token validation request");

            var user = authenticationService.validateToken(request.getToken());

            var userProto = User.newBuilder()
                    .setUserId(user.getUserId())
                    .setUsername(user.getUsername())
                    .setEmail(user.getEmail())
                    .setFullName(user.getFullName())
                    .setCreatedAt(user.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant().getEpochSecond())
                    .build();

            var response = TokenResponse.newBuilder()
                    .setValid(true)
                    .setUser(userProto)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            responseObserver.onError(GrpcExceptionHandler.handle(e));
        }
    }

    @Override
    public void refreshToken(RefreshTokenRequest request, StreamObserver<LoginResponse> responseObserver) {
        try {
            log.info("Refresh token request");

            var user = authenticationService.validateToken(request.getRefreshToken());
            var token = jwtTokenProvider.generateToken(user.getUsername());

            var userProto = User.newBuilder()
                    .setUserId(user.getUserId())
                    .setUsername(user.getUsername())
                    .setEmail(user.getEmail())
                    .setFullName(user.getFullName())
                    .setCreatedAt(user.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().getEpochSecond())
                    .build();

            var response = LoginResponse.newBuilder()
                    .setToken(token)
                    .setRefreshToken(token)
                    .setExpiresIn(86400000)
                    .setUser(userProto)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Token refresh failed: {}", e.getMessage());
            responseObserver.onError(GrpcExceptionHandler.handle(e));
        }
    }
}
