package com.aquaworld.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Product Response DTO for API Responses
 *
 * Returned to clients when retrieving products.
 * Contains all product information needed for display/ordering.
 *
 * Used in:
 * - GET /api/v1/products (list all products)
 * - GET /api/v1/products/{id} (get single product)
 * - GET /api/v1/products/search (search products)
 *
 * HTTP Response Example:
 * {
 *   "id": 2001,
 *   "name": "Red Guppy Male - Premium",
 *   "category": "guppies",
 *   "description": "Beautiful red male guppy with full tail",
 *   "price": 5.99,
 *   "stock": 25,
 *   "imageUrl": "https://aquaworld.com/images/red-guppy.jpg",
 *   "createdAt": "2025-01-15T08:00:00"
 * }
 *
 * @author AquaWorld Development Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Product information for display")
public class ProductResponse {

    @Schema(description = "Product ID", example = "2001")
    private Long id;

    @Schema(description = "Product name", example = "Red Guppy Male - Premium")
    private String name;

    @Schema(description = "Product category", example = "guppies")
    private String category;

    @Schema(description = "Product description", example = "Beautiful red male guppy with full tail fin")
    private String description;

    @Schema(description = "Product price", example = "5.99")
    private BigDecimal price;

    @Schema(description = "Stock quantity available", example = "25")
    private Integer stock;

    @Schema(description = "Product image URL")
    private String imageUrl;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;
}
