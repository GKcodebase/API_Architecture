package com.aquaworld.repository;

import com.aquaworld.model.Order;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.ArrayList;
import java.util.List;

/**
 * Order Repository - In-Memory Data Access Object (DAO)
 *
 * Implements the Data Access Layer for Order entities.
 * Manages customer orders and their lifecycle.
 *
 * Order Status Lifecycle:
 * PENDING -> CONFIRMED -> PROCESSING -> SHIPPED -> DELIVERED
 *        -> CANCELLED (at any point)
 *        -> REFUND_PENDING (if return requested)
 *
 * Thread-Safe Storage:
 * - ConcurrentHashMap for thread-safe order storage
 * - AtomicLong for auto-incrementing order IDs
 *
 * Operations:
 * - Create, Read, Update, Delete orders
 * - Find orders by user ID
 * - Find orders by status
 * - List all orders
 *
 * @author AquaWorld Development Team
 */
@Repository
public class OrderRepository {

    // In-memory storage for orders: Map<orderId, Order>
    private final ConcurrentHashMap<Long, Order> orders = new ConcurrentHashMap<>();

    // Auto-increment ID generator (starting from 3000)
    private final AtomicLong idGenerator = new AtomicLong(3000);

    /**
     * Save a new order to the repository
     *
     * Generates a new ID if not set, and stores the order.
     *
     * @param order the order to save
     * @return the saved order with generated ID
     */
    public Order save(Order order) {
        if (order.getId() == null) {
            order.setId(idGenerator.incrementAndGet());
        }
        orders.put(order.getId(), order);
        return order;
    }

    /**
     * Find order by ID
     *
     * @param id the order ID
     * @return Optional containing the order if found
     */
    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(orders.get(id));
    }

    /**
     * Find all orders by user ID
     *
     * @param userId the user ID
     * @return list of orders placed by the user
     */
    public List<Order> findByUserId(Long userId) {
        return orders.values().stream()
                .filter(order -> order.getUserId().equals(userId))
                .toList();
    }

    /**
     * Find all orders with specific status
     *
     * Statuses: PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED, REFUND_PENDING
     *
     * @param status the order status
     * @return list of orders with the given status
     */
    public List<Order> findByStatus(String status) {
        return orders.values().stream()
                .filter(order -> order.getStatus().equals(status))
                .toList();
    }

    /**
     * Find orders by order number
     *
     * Order numbers are unique (e.g., "ORD-20250118-001")
     *
     * @param orderNumber the order number
     * @return Optional containing the order if found
     */
    public Optional<Order> findByOrderNumber(String orderNumber) {
        return orders.values().stream()
                .filter(order -> order.getOrderNumber().equals(orderNumber))
                .findFirst();
    }

    /**
     * Get all orders
     *
     * @return list of all orders
     */
    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }

    /**
     * Update an existing order
     *
     * @param order the order with updated information
     * @return the updated order
     */
    public Order update(Order order) {
        orders.put(order.getId(), order);
        return order;
    }

    /**
     * Delete an order by ID
     *
     * @param id the order ID to delete
     * @return true if deleted, false if not found
     */
    public boolean deleteById(Long id) {
        return orders.remove(id) != null;
    }

    /**
     * Check if order exists
     *
     * @param id the order ID
     * @return true if order exists
     */
    public boolean existsById(Long id) {
        return orders.containsKey(id);
    }

    /**
     * Get total number of orders
     *
     * @return order count
     */
    public long count() {
        return orders.size();
    }

    /**
     * Clear all orders (useful for testing)
     */
    public void deleteAll() {
        orders.clear();
    }
}
