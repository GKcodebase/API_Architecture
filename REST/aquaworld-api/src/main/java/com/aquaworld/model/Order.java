package com.aquaworld.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Order Data Model (DBO - Database Object) for AquaWorld REST API
 *
 * Represents a customer order in the system.
 * Tracks the order status from creation to delivery.
 *
 * Order Lifecycle:
 * 1. PENDING: Customer creates order, waiting for confirmation
 * 2. CONFIRMED: Customer confirms the order
 * 3. PROCESSING: Order is being packed
 * 4. SHIPPED: Order has been shipped (tracking info available)
 * 5. DELIVERED: Order reached the customer
 * 6. CANCELLED: Customer cancelled the order
 * 7. REFUND_PENDING: Return/refund in progress
 *
 * Fields:
 * - id: Unique order identifier
 * - userId: The user who placed the order
 * - orderNumber: Unique order number (e.g., ORD-20250118-001)
 * - orderItems: List of items in the order
 * - totalPrice: Total order amount
 * - status: Current order status
 * - createdAt: When order was created
 * - updatedAt: Last status update
 *
 * Usage:
 * - Order creation
 * - Order tracking
 * - Order cancellation
 * - Order history
 *
 * @author AquaWorld Development Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Order {

    /** Unique order identifier */
    private Long id;

    /** User ID who placed this order */
    private Long userId;

    /** Unique order number for tracking (e.g., "ORD-20250118-001234") */
    private String orderNumber;

    /** List of items in this order */
    private List<OrderItem> orderItems;

    /** Total order amount (sum of all items) */
    private BigDecimal totalPrice;

    /**
     * Order status: PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED, REFUND_PENDING
     */
    private String status;

    /** When the order was created */
    private LocalDateTime createdAt;

    /** When the order was last updated */
    private LocalDateTime updatedAt;
}
