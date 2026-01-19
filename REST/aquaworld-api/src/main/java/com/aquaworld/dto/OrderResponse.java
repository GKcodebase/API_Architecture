package com.aquaworld.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Order Response DTO
 *
 * Returned to client when retrieving order information.
 *
 * HTTP Response Example:
 * {
 *   "id": 3001,
 *   "orderNumber": "ORD-20250118-001",
 *   "totalPrice": 16.97,
 *   "status": "PENDING",
 *   "orderItems": [
 *     {
 *       "id": 4001,
 *       "productId": 2001,
 *       "quantity": 2,
 *       "price": 5.99
 *     }
 *   ],
 *   "createdAt": "2025-01-18T10:30:00"
 * }
 *
 * @author AquaWorld Development Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Schema(description = "Order information for display")
public class OrderResponse {

    @Schema(description = "Order ID", example = "3001")
    private Long id;

    @Schema(description = "Order number", example = "ORD-20250118-001")
    private String orderNumber;

    @Schema(description = "Order items")
    private List<OrderItemResponse> orderItems;

    @Schema(description = "Total order price", example = "16.97")
    private BigDecimal totalPrice;

    @Schema(description = "Order status", example = "PENDING")
    private String status;

    @Schema(description = "Order creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Order last update timestamp")
    private LocalDateTime updatedAt;
}
