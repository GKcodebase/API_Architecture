package com.aquaworld.graphql.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OrderItemInput DTO - for order item in CreateOrderInput
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemInput {
    private Long productId;
    private Integer quantity;
}
