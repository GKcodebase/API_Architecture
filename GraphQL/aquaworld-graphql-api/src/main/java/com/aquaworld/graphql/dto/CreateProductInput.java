package com.aquaworld.graphql.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CreateProductInput DTO - for creating a new product
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductInput {
    private String name;
    private String category;
    private String description;
    private Double price;
    private Integer stock;
    private String imageUrl;
}
