package com.aquaworld.grpc.repository;

import com.aquaworld.grpc.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Order Repository for AquaWorld gRPC API
 * Provides database access methods for Order entities
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByUserId(String userId);
    List<Order> findByUserIdAndStatus(String userId, String status);
    List<Order> findByStatus(String status);
}
