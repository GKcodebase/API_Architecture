package com.aquaworld.model;

import lombok.*;

import java.math.BigDecimal;

/**
 * OrderItem Data Model (DBO - Database Object) for AquaWorld REST API
 *
 * Represents a single line item within an order.
 * An order can contain multiple items (e.g., 2 guppies + 1 fish food).
 *
 * Fields:
 * - id: Unique item identifier
 * - orderId: Which order this item belongs to
 * - productId: The product being ordered
 * - quantity: How many units of this product
 * - price: Unit price at time of order (prices may change over time)
 *
 * Example Order with Multiple Items:
 * Order #ORD-20250118-001
 *   ├─ Item 1: Red Guppy Male (quantity: 2, price: $5.99 each)
 *   ├─ Item 2: Fish Food Premium (quantity: 1, price: $9.99 each)
 *   └─ Item 3: Aquarium Filter (quantity: 1, price: $29.99 each)
 *
 * Usage:
 * - Store order items
 * - Calculate order totals
 * - Track what products were ordered
 * - Generate invoices/receipts
 *
 * @author AquaWorld Development Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class OrderItem {

    /** Unique order item identifier */
    private Long id;

    /** Order ID this item belongs to */
    private Long orderId;

    /** Product ID being ordered */
    private Long productId;

    /** Quantity of this product in the order */
    private Integer quantity;

    /** Unit price at the time of order (price snapshot) */
    private BigDecimal price;
}
