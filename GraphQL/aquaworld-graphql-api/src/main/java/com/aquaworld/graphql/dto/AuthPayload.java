package com.aquaworld.graphql.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AuthPayload DTO - returned after successful login
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthPayload {
    private Long id;
    private String username;
    private String email;
    private String token;
    private String tokenType;
    private Integer expiresIn;
    private String loginAt;
}
