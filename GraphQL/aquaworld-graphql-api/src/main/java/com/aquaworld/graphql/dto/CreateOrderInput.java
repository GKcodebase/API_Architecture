package com.aquaworld.graphql.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * CreateOrderInput DTO - for creating a new order
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderInput {
    private List<OrderItemInput> orderItems;
}
