package com.aquaworld.service;

import com.aquaworld.dto.ProductResponse;
import com.aquaworld.exception.ResourceNotFoundException;
import com.aquaworld.model.Product;
import com.aquaworld.repository.ProductRepository;
import com.aquaworld.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Product Service for AquaWorld REST API
 *
 * Handles product-related business logic:
 * - Get all products
 * - Get product by ID
 * - Search products by name
 * - Filter products by category
 * - Manage product inventory
 *
 * AquaWorld specializes in:
 * - Guppies (different colors, patterns, sizes)
 * - Fish food and supplements
 * - Aquarium equipment
 * - Decorations
 * - Medicines and treatments
 *
 * Responsibilities:
 * - Product retrieval and filtering
 * - Search functionality
 * - Validation
 *
 * Does NOT:
 * - Handle HTTP requests
 * - Modify products (CRUD)
 *
 * @author AquaWorld Development Team
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Get all products
     *
     * @return list of all products
     */
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get product by ID
     *
     * @param productId the product ID
     * @return ProductResponse
     * @throws ResourceNotFoundException if product not found (404)
     */
    public ProductResponse getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.MSG_PRODUCT_NOT_FOUND));
        return convertToResponse(product);
    }

    /**
     * Search products by name (case-insensitive)
     *
     * @param name the product name or partial name
     * @return list of matching products
     */
    public List<ProductResponse> searchProductsByName(String name) {
        return productRepository.searchByName(name).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get products by category
     *
     * Categories: guppies, fish_food, equipment, decorations, medicines
     *
     * @param category the product category
     * @return list of products in category
     */
    public List<ProductResponse> getProductsByCategory(String category) {
        return productRepository.findByCategory(category).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get products in stock
     *
     * @return list of products with stock > 0
     */
    public List<ProductResponse> getProductsInStock() {
        return productRepository.findInStock().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Check if product is in stock
     *
     * @param productId the product ID
     * @param quantity the quantity needed
     * @return true if enough stock available
     * @throws ResourceNotFoundException if product not found (404)
     */
    public boolean isInStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.MSG_PRODUCT_NOT_FOUND));
        return product.getStock() != null && product.getStock() >= quantity;
    }

    /**
     * Reduce product stock (used when order is placed)
     *
     * @param productId the product ID
     * @param quantity the quantity to reduce
     * @throws ResourceNotFoundException if product not found (404)
     */
    public void reduceStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.MSG_PRODUCT_NOT_FOUND));

        if (product.getStock() < quantity) {
            throw new RuntimeException(Constants.MSG_INSUFFICIENT_STOCK);
        }

        product.setStock(product.getStock() - quantity);
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.update(product);
    }

    /**
     * Increase product stock (used when order is cancelled)
     *
     * @param productId the product ID
     * @param quantity the quantity to increase
     * @throws ResourceNotFoundException if product not found (404)
     */
    public void increaseStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.MSG_PRODUCT_NOT_FOUND));

        product.setStock(product.getStock() + quantity);
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.update(product);
    }

    /**
     * Convert Product entity to ProductResponse DTO
     *
     * @param product the product entity
     * @return ProductResponse DTO
     */
    private ProductResponse convertToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .category(product.getCategory())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .imageUrl(product.getImageUrl())
                .createdAt(product.getCreatedAt())
                .build();
    }
}
