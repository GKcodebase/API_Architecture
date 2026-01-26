package com.aquaworld.graphql.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RegisterInput DTO - for user registration
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterInput {
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phone;
}
