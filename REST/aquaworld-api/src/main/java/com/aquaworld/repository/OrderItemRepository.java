package com.aquaworld.repository;

import com.aquaworld.model.OrderItem;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.ArrayList;
import java.util.List;

/**
 * OrderItem Repository - In-Memory Data Access Object (DAO)
 *
 * Implements the Data Access Layer for OrderItem entities.
 * Manages individual line items within orders.
 *
 * Each order can have multiple items (e.g., 2 guppies + 1 fish food + 1 filter)
 *
 * Thread-Safe Storage:
 * - ConcurrentHashMap for thread-safe order item storage
 * - AtomicLong for auto-incrementing order item IDs
 *
 * Operations:
 * - Create, Read order items
 * - Find items by order ID
 * - Find items by product ID
 * - List all items
 *
 * @author AquaWorld Development Team
 */
@Repository
public class OrderItemRepository {

    // In-memory storage for order items: Map<itemId, OrderItem>
    private final ConcurrentHashMap<Long, OrderItem> orderItems = new ConcurrentHashMap<>();

    // Auto-increment ID generator (starting from 4000)
    private final AtomicLong idGenerator = new AtomicLong(4000);

    /**
     * Save a new order item to the repository
     *
     * Generates a new ID if not set, and stores the item.
     *
     * @param orderItem the order item to save
     * @return the saved order item with generated ID
     */
    public OrderItem save(OrderItem orderItem) {
        if (orderItem.getId() == null) {
            orderItem.setId(idGenerator.incrementAndGet());
        }
        orderItems.put(orderItem.getId(), orderItem);
        return orderItem;
    }

    /**
     * Find order item by ID
     *
     * @param id the order item ID
     * @return Optional containing the order item if found
     */
    public Optional<OrderItem> findById(Long id) {
        return Optional.ofNullable(orderItems.get(id));
    }

    /**
     * Find all items in a specific order
     *
     * @param orderId the order ID
     * @return list of items in the order
     */
    public List<OrderItem> findByOrderId(Long orderId) {
        return orderItems.values().stream()
                .filter(item -> item.getOrderId().equals(orderId))
                .toList();
    }

    /**
     * Find items by product ID
     *
     * Used to see all orders containing a specific product
     *
     * @param productId the product ID
     * @return list of order items with the given product
     */
    public List<OrderItem> findByProductId(Long productId) {
        return orderItems.values().stream()
                .filter(item -> item.getProductId().equals(productId))
                .toList();
    }

    /**
     * Get all order items
     *
     * @return list of all order items
     */
    public List<OrderItem> findAll() {
        return new ArrayList<>(orderItems.values());
    }

    /**
     * Update an existing order item
     *
     * @param orderItem the order item with updated information
     * @return the updated order item
     */
    public OrderItem update(OrderItem orderItem) {
        orderItems.put(orderItem.getId(), orderItem);
        return orderItem;
    }

    /**
     * Delete an order item by ID
     *
     * @param id the order item ID to delete
     * @return true if deleted, false if not found
     */
    public boolean deleteById(Long id) {
        return orderItems.remove(id) != null;
    }

    /**
     * Delete all items in an order
     *
     * @param orderId the order ID
     * @return number of items deleted
     */
    public int deleteByOrderId(Long orderId) {
        List<Long> idsToDelete = orderItems.values().stream()
                .filter(item -> item.getOrderId().equals(orderId))
                .map(OrderItem::getId)
                .toList();

        int count = 0;
        for (Long id : idsToDelete) {
            if (orderItems.remove(id) != null) {
                count++;
            }
        }
        return count;
    }

    /**
     * Check if order item exists
     *
     * @param id the order item ID
     * @return true if order item exists
     */
    public boolean existsById(Long id) {
        return orderItems.containsKey(id);
    }

    /**
     * Get total number of order items
     *
     * @return order item count
     */
    public long count() {
        return orderItems.size();
    }

    /**
     * Clear all order items (useful for testing)
     */
    public void deleteAll() {
        orderItems.clear();
    }
}
