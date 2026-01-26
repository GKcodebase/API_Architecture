package com.aquaworld.graphql.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Order entity - represents a customer order
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    private Long id;
    private String orderNumber;
    private Long userId;
    private List<OrderItem> orderItems;
    private Double totalPrice;
    private String status; // PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED
    private String createdAt;
    private String updatedAt;
}
