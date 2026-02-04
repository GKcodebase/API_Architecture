package com.aquaworld.grpc.service;

import com.aquaworld.grpc.model.Product;
import com.aquaworld.grpc.exception.ResourceNotFoundException;
import com.aquaworld.grpc.exception.InvalidInputException;
import com.aquaworld.grpc.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Product Service for AquaWorld gRPC API
 * Handles product management operations
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    /**
     * Get product by ID
     *
     * @param productId the product ID
     * @return Product
     * @throws ResourceNotFoundException if product not found
     */
    public Product getProduct(String productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));
    }

    /**
     * Get all products
     *
     * @return List of all products
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Get products by category
     *
     * @param category the product category
     * @return List of products in category
     */
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    /**
     * Search products by name or description
     *
     * @param searchQuery the search query
     * @return List of matching products
     */
    public List<Product> searchProducts(String searchQuery) {
        return productRepository.searchByNameOrDescription(searchQuery);
    }

    /**
     * Search products by category and query
     *
     * @param category the category
     * @param searchQuery the search query
     * @return List of matching products
     */
    public List<Product> searchProductsByCategoryAndQuery(String category, String searchQuery) {
        return productRepository.searchByCategoryAndQuery(category, searchQuery);
    }

    /**
     * Create a new product
     *
     * @param name the product name
     * @param description the product description
     * @param price the product price
     * @param stockQuantity the stock quantity
     * @param category the product category
     * @param tags the product tags
     * @param imageUrl the image URL
     * @return newly created Product
     * @throws InvalidInputException if input is invalid
     */
    public Product createProduct(String name, String description, Double price, Integer stockQuantity,
                                 String category, String tags, String imageUrl) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidInputException("Product name is required");
        }
        if (price == null || price <= 0) {
            throw new InvalidInputException("Product price must be greater than 0");
        }
        if (stockQuantity == null || stockQuantity < 0) {
            throw new InvalidInputException("Stock quantity must be greater than or equal to 0");
        }

        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStockQuantity(stockQuantity);
        product.setCategory(category);
        product.setTags(tags);
        product.setImageUrl(imageUrl);

        Product savedProduct = productRepository.save(product);
        log.info("Product {} created successfully", product.getProductId());
        return savedProduct;
    }

    /**
     * Update an existing product
     *
     * @param productId the product ID
     * @param name the product name
     * @param description the product description
     * @param price the product price
     * @param stockQuantity the stock quantity
     * @param category the product category
     * @param tags the product tags
     * @param imageUrl the image URL
     * @return updated Product
     * @throws ResourceNotFoundException if product not found
     */
    public Product updateProduct(String productId, String name, String description, Double price,
                                Integer stockQuantity, String category, String tags, String imageUrl) {
        Product product = getProduct(productId);

        if (name != null && !name.trim().isEmpty()) {
            product.setName(name);
        }
        if (description != null) {
            product.setDescription(description);
        }
        if (price != null && price > 0) {
            product.setPrice(price);
        }
        if (stockQuantity != null && stockQuantity >= 0) {
            product.setStockQuantity(stockQuantity);
        }
        if (category != null && !category.trim().isEmpty()) {
            product.setCategory(category);
        }
        if (tags != null) {
            product.setTags(tags);
        }
        if (imageUrl != null) {
            product.setImageUrl(imageUrl);
        }

        Product updatedProduct = productRepository.save(product);
        log.info("Product {} updated successfully", productId);
        return updatedProduct;
    }

    /**
     * Update product stock
     *
     * @param productId the product ID
     * @param quantityChange the quantity change
     * @return updated Product
     * @throws ResourceNotFoundException if product not found
     */
    public Product updateStock(String productId, Integer quantityChange) {
        Product product = getProduct(productId);
        int newQuantity = product.getStockQuantity() + quantityChange;

        if (newQuantity < 0) {
            throw new InvalidInputException("Insufficient stock");
        }

        product.setStockQuantity(newQuantity);
        Product updatedProduct = productRepository.save(product);
        log.info("Product {} stock updated to {}", productId, newQuantity);
        return updatedProduct;
    }

    /**
     * Delete a product
     *
     * @param productId the product ID
     * @throws ResourceNotFoundException if product not found
     */
    public void deleteProduct(String productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product", productId);
        }
        productRepository.deleteById(productId);
        log.info("Product {} deleted successfully", productId);
    }
}
