package com.aquaworld.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Order Item Request DTO
 *
 * Represents a single item in an order request.
 *
 * Example:
 * {
 *   "productId": 2001,
 *   "quantity": 2
 * }
 *
 * @author AquaWorld Development Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Single item in an order")
@Getter
public class OrderItemRequest {

    /**
     * Product ID to order
     *
     * Validation:
     * - Required (not null)
     * - Must exist in product database
     */
    @NotNull(message = "Product ID is required")
    @Schema(description = "Product ID", example = "2001")
    private Long productId;

    /**
     * Quantity to order
     *
     * Validation:
     * - Required (not null)
     * - Must be at least 1
     * - Cannot exceed stock
     */
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Schema(description = "Quantity to order", example = "2")
    private Integer quantity;
}
