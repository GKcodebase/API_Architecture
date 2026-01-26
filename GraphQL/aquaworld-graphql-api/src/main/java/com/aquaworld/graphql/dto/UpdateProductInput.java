package com.aquaworld.graphql.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UpdateProductInput DTO - for updating an existing product
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProductInput {
    private String name;
    private String category;
    private String description;
    private Double price;
    private Integer stock;
    private String imageUrl;
}
