package com.aquaworld.grpc.repository;

import com.aquaworld.grpc.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * OrderItem Repository for AquaWorld gRPC API
 * Provides database access methods for OrderItem entities
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, String> {
    List<OrderItem> findByOrderId(String orderId);
}
