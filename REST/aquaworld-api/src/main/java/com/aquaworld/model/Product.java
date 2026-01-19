package com.aquaworld.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Product Data Model (DBO - Database Object) for AquaWorld REST API
 *
 * Represents a product in the AquaWorld pet store inventory.
 * AquaWorld specializes in guppies and aquatic accessories.
 *
 * Product Categories:
 * - GUPPIES: Different varieties of guppy fish
 * - FISH_FOOD: Specialized food for guppies
 * - EQUIPMENT: Aquarium tanks, filters, heaters, etc.
 * - DECORATIONS: Plants, ornaments, castles, etc.
 * - MEDICINES: Fish health and treatment products
 *
 * Fields:
 * - id: Unique product identifier
 * - name: Product name (e.g., "Premium Red Guppy Male")
 * - category: Product category
 * - description: Detailed product description
 * - price: Product price in USD
 * - stock: Current quantity in stock
 * - imageUrl: URL to product image
 * - createdAt: When product was added
 * - updatedAt: Last update timestamp
 *
 * Usage:
 * - Product listing and search
 * - Shopping cart and checkout
 * - Inventory management
 * - Price display
 *
 * @author AquaWorld Development Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Product {

    /** Unique product identifier */
    private Long id;

    /** Product name (e.g., "Red Guppy Male - Premium Quality") */
    private String name;

    /** Product category (guppies, fish_food, equipment, decorations, medicines) */
    private String category;

    /** Detailed product description with specifications */
    private String description;

    /** Product price in USD */
    private BigDecimal price;

    /** Current stock quantity available */
    private Integer stock;

    /** URL to product image/photo */
    private String imageUrl;

    /** When the product was added to inventory */
    private LocalDateTime createdAt;

    /** Last update timestamp */
    private LocalDateTime updatedAt;
}
