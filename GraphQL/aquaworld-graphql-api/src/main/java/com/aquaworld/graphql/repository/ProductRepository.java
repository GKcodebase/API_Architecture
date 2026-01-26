package com.aquaworld.graphql.repository;

import com.aquaworld.graphql.model.Product;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Product Repository - In-memory storage for products
 */
@Repository
public class ProductRepository {
    private final Map<Long, Product> products = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public Product save(Product product) {
        if (product.getId() == null) {
            product.setId(idCounter.getAndIncrement());
        }
        products.put(product.getId(), product);
        return product;
    }

    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(products.get(id));
    }

    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }

    public List<Product> findByCategory(String category) {
        return products.values().stream()
                .filter(p -> p.getCategory().equals(category))
                .collect(Collectors.toList());
    }

    public List<Product> findByNameContaining(String name) {
        String lowerName = name.toLowerCase();
        return products.values().stream()
                .filter(p -> p.getName().toLowerCase().contains(lowerName))
                .collect(Collectors.toList());
    }

    public List<Product> findAvailable() {
        return products.values().stream()
                .filter(p -> p.getStock() > 0)
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        products.remove(id);
    }

    public void deleteAll() {
        products.clear();
    }

    public long count() {
        return products.size();
    }
}
