package com.aquaworld.graphql.repository;

import com.aquaworld.graphql.model.Order;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Order Repository - In-memory storage for orders
 */
@Repository
public class OrderRepository {
    private final Map<Long, Order> orders = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public Order save(Order order) {
        if (order.getId() == null) {
            order.setId(idCounter.getAndIncrement());
        }
        orders.put(order.getId(), order);
        return order;
    }

    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(orders.get(id));
    }

    public Optional<Order> findByOrderNumber(String orderNumber) {
        return orders.values().stream()
                .filter(o -> o.getOrderNumber().equals(orderNumber))
                .findFirst();
    }

    public List<Order> findByUserId(Long userId) {
        return orders.values().stream()
                .filter(o -> o.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }

    public void delete(Long id) {
        orders.remove(id);
    }

    public void deleteAll() {
        orders.clear();
    }

    public long count() {
        return orders.size();
    }
}
