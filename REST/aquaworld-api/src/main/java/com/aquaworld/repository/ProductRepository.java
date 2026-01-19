package com.aquaworld.repository;

import com.aquaworld.model.Product;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.ArrayList;
import java.util.List;

/**
 * Product Repository - In-Memory Data Access Object (DAO)
 *
 * Implements the Data Access Layer for Product entities.
 * Manages the product catalog for AquaWorld pet store.
 *
 * AquaWorld specializes in:
 * - Guppies (various colors and patterns)
 * - Fish food and supplements
 * - Aquarium equipment
 * - Decorations
 * - Medicines
 *
 * Thread-Safe Storage:
 * - ConcurrentHashMap for thread-safe product storage
 * - AtomicLong for auto-incrementing product IDs
 *
 * Operations:
 * - Create, Read, Update, Delete products
 * - Search by category
 * - Search by name
 * - Filter by stock availability
 *
 * @author AquaWorld Development Team
 */
@Repository
public class ProductRepository {

    // In-memory storage for products: Map<productId, Product>
    private final ConcurrentHashMap<Long, Product> products = new ConcurrentHashMap<>();

    // Auto-increment ID generator (starting from 2000)
    private final AtomicLong idGenerator = new AtomicLong(2000);

    /**
     * Save a new product to the repository
     *
     * Generates a new ID if not set, and stores the product.
     *
     * @param product the product to save
     * @return the saved product with generated ID
     */
    public Product save(Product product) {
        if (product.getId() == null) {
            product.setId(idGenerator.incrementAndGet());
        }
        products.put(product.getId(), product);
        return product;
    }

    /**
     * Find product by ID
     *
     * @param id the product ID
     * @return Optional containing the product if found
     */
    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(products.get(id));
    }

    /**
     * Get all products
     *
     * @return list of all products
     */
    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }

    /**
     * Find products by category
     *
     * Categories: guppies, fish_food, equipment, decorations, medicines
     *
     * @param category the product category
     * @return list of products in the category
     */
    public List<Product> findByCategory(String category) {
        return products.values().stream()
                .filter(product -> product.getCategory().equalsIgnoreCase(category))
                .toList();
    }

    /**
     * Search products by name (case-insensitive)
     *
     * @param name the product name or partial name
     * @return list of matching products
     */
    public List<Product> searchByName(String name) {
        return products.values().stream()
                .filter(product -> product.getName().toLowerCase()
                        .contains(name.toLowerCase()))
                .toList();
    }

    /**
     * Find products in stock
     *
     * @return list of products with stock > 0
     */
    public List<Product> findInStock() {
        return products.values().stream()
                .filter(product -> product.getStock() != null && product.getStock() > 0)
                .toList();
    }

    /**
     * Update an existing product
     *
     * @param product the product with updated information
     * @return the updated product
     */
    public Product update(Product product) {
        products.put(product.getId(), product);
        return product;
    }

    /**
     * Delete a product by ID
     *
     * @param id the product ID to delete
     * @return true if deleted, false if not found
     */
    public boolean deleteById(Long id) {
        return products.remove(id) != null;
    }

    /**
     * Check if product exists
     *
     * @param id the product ID
     * @return true if product exists
     */
    public boolean existsById(Long id) {
        return products.containsKey(id);
    }

    /**
     * Get total number of products
     *
     * @return product count
     */
    public long count() {
        return products.size();
    }

    /**
     * Clear all products (useful for testing)
     */
    public void deleteAll() {
        products.clear();
    }
}
