package com.aquaworld.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Order Item Response DTO
 *
 * Represents a single item in an order response.
 *
 * @author AquaWorld Development Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Order item details")
public class OrderItemDetailResponse {

    @Schema(description = "Order item ID", example = "4001")
    private Long id;

    @Schema(description = "Product ID", example = "2001")
    private Long productId;

    @Schema(description = "Quantity ordered", example = "2")
    private Integer quantity;

    @Schema(description = "Unit price at order time", example = "5.99")
    private BigDecimal price;
}
