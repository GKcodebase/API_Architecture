package com.aquaworld.graphql.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User entity - represents a customer or admin user
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Long id;
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private String role; // CUSTOMER, ADMIN, VENDOR
    private String createdAt;
    private String lastLogin;
    private Boolean active;
}
