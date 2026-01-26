package com.aquaworld.graphql.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * LoginInput DTO - for user login
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginInput {
    private String username;
    private String password;
}
