package com.aquaworld.graphql.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Product entity - represents a guppy or aquatic item
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    private Long id;
    private String name;
    private String category; // guppies, fish_food, equipment, decorations, medicines
    private String description;
    private Double price;
    private Integer stock;
    private String imageUrl;
    private String createdAt;
    private String updatedAt;
}
