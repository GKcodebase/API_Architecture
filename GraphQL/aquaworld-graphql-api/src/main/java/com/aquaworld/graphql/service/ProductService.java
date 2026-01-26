package com.aquaworld.graphql.service;

import com.aquaworld.graphql.dto.CreateProductInput;
import com.aquaworld.graphql.dto.UpdateProductInput;
import com.aquaworld.graphql.exception.ResourceNotFoundException;
import com.aquaworld.graphql.model.Product;
import com.aquaworld.graphql.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Product Service
 * Handles product operations (CRUD, search, filter)
 */
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Get all products
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Get product by ID
     */
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }

    /**
     * Search products by name
     */
    public List<Product> searchProducts(String name) {
        return productRepository.findByNameContaining(name);
    }

    /**
     * Get products by category
     */
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    /**
     * Get available products (in stock)
     */
    public List<Product> getAvailableProducts() {
        return productRepository.findAvailable();
    }

    /**
     * Create a new product
     */
    public Product createProduct(CreateProductInput input) {
        Product product = Product.builder()
                .name(input.getName())
                .category(input.getCategory())
                .description(input.getDescription())
                .price(input.getPrice())
                .stock(input.getStock())
                .imageUrl(input.getImageUrl())
                .createdAt(LocalDateTime.now().format(formatter))
                .updatedAt(LocalDateTime.now().format(formatter))
                .build();

        return productRepository.save(product);
    }

    /**
     * Update a product
     */
    public Product updateProduct(Long id, UpdateProductInput input) {
        Product product = getProductById(id);

        if (input.getName() != null) {
            product.setName(input.getName());
        }
        if (input.getCategory() != null) {
            product.setCategory(input.getCategory());
        }
        if (input.getDescription() != null) {
            product.setDescription(input.getDescription());
        }
        if (input.getPrice() != null) {
            product.setPrice(input.getPrice());
        }
        if (input.getStock() != null) {
            product.setStock(input.getStock());
        }
        if (input.getImageUrl() != null) {
            product.setImageUrl(input.getImageUrl());
        }

        product.setUpdatedAt(LocalDateTime.now().format(formatter));
        return productRepository.save(product);
    }

    /**
     * Delete a product
     */
    public boolean deleteProduct(Long id) {
        getProductById(id); // Check if exists
        productRepository.delete(id);
        return true;
    }
}
