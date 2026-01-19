package com.aquaworld.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * Create Order Request DTO
 *
 * Used when a customer creates a new order.
 * Contains list of items to order.
 *
 * HTTP Request Example:
 * POST /api/v1/orders
 * {
 *   "orderItems": [
 *     {
 *       "productId": 2001,
 *       "quantity": 2
 *     },
 *     {
 *       "productId": 2002,
 *       "quantity": 1
 *     }
 *   ]
 * }
 *
 * Success Response (201 Created):
 * {
 *   "success": true,
 *   "message": "Order created successfully",
 *   "statusCode": 201,
 *   "data": {
 *     "id": 3001,
 *     "orderNumber": "ORD-20250118-001",
 *     "totalPrice": 16.97,
 *     "status": "PENDING",
 *     "createdAt": "2025-01-18T10:30:00"
 *   }
 * }
 *
 * @author AquaWorld Development Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a new order")
@Getter
public class CreateOrderRequest {

    /**
     * List of items to include in the order
     *
     * Validation:
     * - Required (at least one item)
     * - Each item must be valid (see OrderItemRequest)
     */
    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    @Schema(description = "Items to order")
    private List<OrderItemRequest> orderItems;
}
