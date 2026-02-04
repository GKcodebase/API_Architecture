package com.aquaworld.grpc.service;

import com.aquaworld.grpc.model.Order;
import com.aquaworld.grpc.model.OrderItem;
import com.aquaworld.grpc.exception.ResourceNotFoundException;
import com.aquaworld.grpc.exception.InvalidInputException;
import com.aquaworld.grpc.repository.OrderRepository;
import com.aquaworld.grpc.repository.OrderItemRepository;
import com.aquaworld.grpc.util.Constants;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Order Service for AquaWorld gRPC API
 * Handles order management operations
 */
@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    /**
     * Create a new order
     *
     * @param userId the user ID
     * @param orderItems the order items
     * @param totalAmount the total amount
     * @param shippingAddress the shipping address
     * @return newly created Order
     */
    public Order createOrder(String userId, List<OrderItem> orderItems, Double totalAmount, String shippingAddress) {
        if (orderItems == null || orderItems.isEmpty()) {
            throw new InvalidInputException("Order must contain at least one item");
        }
        if (totalAmount == null || totalAmount <= 0) {
            throw new InvalidInputException("Order total must be greater than 0");
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setShippingAddress(shippingAddress);
        order.setStatus(Constants.ORDER_STATUS_PENDING);

        Order savedOrder = orderRepository.save(order);

        // Save order items
        for (OrderItem item : orderItems) {
            item.setOrderId(savedOrder.getOrderId());
            orderItemRepository.save(item);
        }

        log.info("Order {} created successfully", savedOrder.getOrderId());
        return savedOrder;
    }

    /**
     * Get order by ID
     *
     * @param orderId the order ID
     * @return Order
     * @throws ResourceNotFoundException if order not found
     */
    public Order getOrder(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
    }

    /**
     * Get user's orders
     *
     * @param userId the user ID
     * @return List of user's orders
     */
    public List<Order> getUserOrders(String userId) {
        return orderRepository.findByUserId(userId);
    }

    /**
     * Get user's orders by status
     *
     * @param userId the user ID
     * @param status the order status
     * @return List of user's orders with specified status
     */
    public List<Order> getUserOrdersByStatus(String userId, String status) {
        return orderRepository.findByUserIdAndStatus(userId, status);
    }

    /**
     * Get order items for an order
     *
     * @param orderId the order ID
     * @return List of order items
     */
    public List<OrderItem> getOrderItems(String orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    /**
     * Update order status
     *
     * @param orderId the order ID
     * @param status the new status
     * @return updated Order
     * @throws ResourceNotFoundException if order not found
     */
    public Order updateOrderStatus(String orderId, String status) {
        Order order = getOrder(orderId);
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        log.info("Order {} status updated to {}", orderId, status);
        return updatedOrder;
    }

    /**
     * Cancel an order
     *
     * @param orderId the order ID
     * @return cancelled Order
     * @throws ResourceNotFoundException if order not found
     */
    public Order cancelOrder(String orderId) {
        Order order = getOrder(orderId);
        order.setStatus(Constants.ORDER_STATUS_CANCELLED);
        Order cancelledOrder = orderRepository.save(order);
        log.info("Order {} cancelled successfully", orderId);
        return cancelledOrder;
    }
}
