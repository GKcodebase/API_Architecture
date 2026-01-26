package com.aquaworld.graphql.service;

import com.aquaworld.graphql.dto.CreateOrderInput;
import com.aquaworld.graphql.dto.OrderItemInput;
import com.aquaworld.graphql.dto.UpdateProductInput;
import com.aquaworld.graphql.exception.InvalidOperationException;
import com.aquaworld.graphql.exception.ResourceNotFoundException;
import com.aquaworld.graphql.model.Order;
import com.aquaworld.graphql.model.OrderItem;
import com.aquaworld.graphql.model.Product;
import com.aquaworld.graphql.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Order Service
 * Handles order operations (CRUD, status management)
 */
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final AtomicInteger orderNumberCounter = new AtomicInteger(1);

    /**
     * Get all orders for a user
     */
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    /**
     * Get order by ID
     */
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
    }

    /**
     * Get order by order number
     */
    public Order getOrderByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderNumber", orderNumber));
    }

    /**
     * Create a new order
     */
    public Order createOrder(Long userId, CreateOrderInput input) {
        if (input.getOrderItems() == null || input.getOrderItems().isEmpty()) {
            throw new InvalidOperationException("Order must have at least one item");
        }

        // Create order items and calculate total price
        List<OrderItem> orderItems = new ArrayList<>();
        Double totalPrice = 0.0;

        for (OrderItemInput itemInput : input.getOrderItems()) {
            Product product = productService.getProductById(itemInput.getProductId());

            if (product.getStock() < itemInput.getQuantity()) {
                throw new InvalidOperationException("Insufficient stock for product: " + product.getName());
            }

            // Reduce stock
            product.setStock(product.getStock() - itemInput.getQuantity());
            productService.updateProduct(product.getId(), UpdateProductInput.builder()
                    .stock(product.getStock())
                    .build());

            OrderItem orderItem = OrderItem.builder()
                    .productId(product.getId())
                    .quantity(itemInput.getQuantity())
                    .price(product.getPrice())
                    .build();

            orderItems.add(orderItem);
            totalPrice += product.getPrice() * itemInput.getQuantity();
        }

        String orderNumber = "ORD-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) 
                             + "-" + String.format("%03d", orderNumberCounter.getAndIncrement());

        Order order = Order.builder()
                .userId(userId)
                .orderNumber(orderNumber)
                .orderItems(orderItems)
                .totalPrice(totalPrice)
                .status("PENDING")
                .createdAt(LocalDateTime.now().format(formatter))
                .updatedAt(LocalDateTime.now().format(formatter))
                .build();

        return orderRepository.save(order);
    }

    /**
     * Update order status
     */
    public Order updateOrderStatus(Long id, String status) {
        Order order = getOrderById(id);

        // Validate status
        if (!isValidStatus(status)) {
            throw new InvalidOperationException("Invalid order status: " + status);
        }

        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now().format(formatter));
        return orderRepository.save(order);
    }

    /**
     * Cancel order
     */
    public Order cancelOrder(Long id) {
        Order order = getOrderById(id);

        if (order.getStatus().equals("CANCELLED")) {
            throw new InvalidOperationException("Order is already cancelled");
        }

        if (order.getStatus().equals("DELIVERED")) {
            throw new InvalidOperationException("Cannot cancel delivered order");
        }

        // Restore stock for all items
        for (OrderItem item : order.getOrderItems()) {
            Product product = productService.getProductById(item.getProductId());
            product.setStock(product.getStock() + item.getQuantity());
        }

        order.setStatus("CANCELLED");
        order.setUpdatedAt(LocalDateTime.now().format(formatter));
        return orderRepository.save(order);
    }

    /**
     * Delete order
     */
    public boolean deleteOrder(Long id) {
        getOrderById(id); // Check if exists
        orderRepository.delete(id);
        return true;
    }

    /**
     * Validate order status
     */
    private boolean isValidStatus(String status) {
        return status.equals("PENDING") || status.equals("CONFIRMED") || 
               status.equals("PROCESSING") || status.equals("SHIPPED") || 
               status.equals("DELIVERED") || status.equals("CANCELLED");
    }
}
