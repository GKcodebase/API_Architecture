package com.aquaworld.graphql.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UpdateUserInput DTO - for updating user profile
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserInput {
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
}
