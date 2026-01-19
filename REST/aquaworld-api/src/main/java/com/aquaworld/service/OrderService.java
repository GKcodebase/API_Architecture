package com.aquaworld.service;

import com.aquaworld.dto.CreateOrderRequest;
import com.aquaworld.dto.OrderItemRequest;
import com.aquaworld.dto.OrderResponse;
import com.aquaworld.dto.OrderItemDetailResponse;
import com.aquaworld.exception.InvalidInputException;
import com.aquaworld.exception.ResourceNotFoundException;
import com.aquaworld.model.Order;
import com.aquaworld.model.OrderItem;
import com.aquaworld.model.Product;
import com.aquaworld.repository.OrderRepository;
import com.aquaworld.repository.OrderItemRepository;
import com.aquaworld.repository.ProductRepository;
import com.aquaworld.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Order Service for AquaWorld REST API
 *
 * Handles order-related business logic:
 * - Create new orders
 * - Retrieve orders
 * - Update order status
 * - Cancel orders
 * - Calculate order totals
 *
 * Order Lifecycle:
 * PENDING -> CONFIRMED -> PROCESSING -> SHIPPED -> DELIVERED
 *        -> CANCELLED (at any point)
 *        -> REFUND_PENDING (if return requested)
 *
 * Responsibilities:
 * - Order creation and validation
 * - Order item management
 * - Stock management
 * - Order status transitions
 * - Total calculation
 *
 * Does NOT:
 * - Handle HTTP requests
 * - Process payments
 * - Handle shipping logistics
 *
 * @author AquaWorld Development Team
 */
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                       OrderItemRepository orderItemRepository,
                       ProductRepository productRepository,
                       ProductService productService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.productService = productService;
    }

    /**
     * Create a new order
     *
     * Process:
     * 1. Validate order has at least one item
     * 2. For each item:
     *    - Check product exists (404 if not)
     *    - Check stock available (400 if insufficient)
     *    - Add item to order
     *    - Reduce product stock
     * 3. Calculate total price
     * 4. Save order with status PENDING
     * 5. Return order response
     *
     * @param userId the user placing the order
     * @param createOrderRequest contains items to order
     * @return OrderResponse
     * @throws InvalidInputException if order has no items (400)
     * @throws ResourceNotFoundException if product not found (404)
     * @throws RuntimeException if insufficient stock (400)
     */
    public OrderResponse createOrder(Long userId, CreateOrderRequest createOrderRequest) {
        // Validate order has items
        if (createOrderRequest.getOrderItems() == null || createOrderRequest.getOrderItems().isEmpty()) {
            throw new InvalidInputException("Order must contain at least one item");
        }

        // Validate all items before creating order (fail-fast)
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderItemRequest itemRequest : createOrderRequest.getOrderItems()) {
            // Check product exists
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(Constants.MSG_PRODUCT_NOT_FOUND));

            // Check stock
            if (!productService.isInStock(itemRequest.getProductId(), itemRequest.getQuantity())) {
                throw new InvalidInputException(Constants.MSG_INSUFFICIENT_STOCK);
            }

            // Add to total
            totalPrice = totalPrice.add(
                    product.getPrice().multiply(new BigDecimal(itemRequest.getQuantity()))
            );
        }

        // Create order
        Order order = Order.builder()
                .userId(userId)
                .orderNumber(generateOrderNumber())
                .status(Constants.ORDER_STATUS_PENDING)
                .totalPrice(totalPrice)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Save order
        Order savedOrder = orderRepository.save(order);

        // Add items to order and reduce stock
        for (OrderItemRequest itemRequest : createOrderRequest.getOrderItems()) {
            Product product = productRepository.findById(itemRequest.getProductId()).get();

            // Create order item
            OrderItem orderItem = OrderItem.builder()
                    .orderId(savedOrder.getId())
                    .productId(itemRequest.getProductId())
                    .quantity(itemRequest.getQuantity())
                    .price(product.getPrice())
                    .build();

            orderItemRepository.save(orderItem);

            // Reduce product stock
            productService.reduceStock(itemRequest.getProductId(), itemRequest.getQuantity());
        }

        // Reload order items
        List<OrderItem> items = orderItemRepository.findByOrderId(savedOrder.getId());
        savedOrder.setOrderItems(items);

        return convertToResponse(savedOrder);
    }

    /**
     * Get user's orders
     *
     * @param userId the user ID
     * @return list of user's orders
     */
    public List<OrderResponse> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get order by ID
     *
     * @param orderId the order ID
     * @return OrderResponse
     * @throws ResourceNotFoundException if order not found (404)
     */
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.MSG_ORDER_NOT_FOUND));

        // Load order items
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        order.setOrderItems(items);

        return convertToResponse(order);
    }

    /**
     * Update order status
     *
     * Allowed transitions:
     * - PENDING -> CONFIRMED, CANCELLED
     * - CONFIRMED -> PROCESSING, CANCELLED
     * - PROCESSING -> SHIPPED
     * - SHIPPED -> DELIVERED
     * - ANY -> REFUND_PENDING
     *
     * @param orderId the order ID
     * @param newStatus the new status
     * @return updated OrderResponse
     * @throws ResourceNotFoundException if order not found (404)
     * @throws InvalidInputException if status transition invalid (400)
     */
    public OrderResponse updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.MSG_ORDER_NOT_FOUND));

        // Validate status transition
        if (!isValidStatusTransition(order.getStatus(), newStatus)) {
            throw new InvalidInputException("Cannot update order from " + order.getStatus() + " to " + newStatus);
        }

        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        Order updatedOrder = orderRepository.update(order);

        return convertToResponse(updatedOrder);
    }

    /**
     * Cancel an order
     *
     * Can only cancel if status is PENDING or CONFIRMED
     * Refunds product stock
     *
     * @param orderId the order ID
     * @return cancelled OrderResponse
     * @throws ResourceNotFoundException if order not found (404)
     * @throws InvalidInputException if order cannot be cancelled (400)
     */
    public OrderResponse cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.MSG_ORDER_NOT_FOUND));

        // Check if order can be cancelled
        if (!order.getStatus().equals(Constants.ORDER_STATUS_PENDING) &&
            !order.getStatus().equals(Constants.ORDER_STATUS_CONFIRMED)) {
            throw new InvalidInputException(Constants.MSG_ORDER_CANNOT_CANCEL);
        }

        // Load order items
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);

        // Refund stock for all items
        for (OrderItem item : items) {
            productService.increaseStock(item.getProductId(), item.getQuantity());
        }

        // Update order status
        order.setStatus(Constants.ORDER_STATUS_CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        Order updatedOrder = orderRepository.update(order);

        return convertToResponse(updatedOrder);
    }

    /**
     * Delete an order (soft delete concept)
     *
     * @param orderId the order ID
     * @throws ResourceNotFoundException if order not found (404)
     */
    public void deleteOrder(Long orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException(Constants.MSG_ORDER_NOT_FOUND);
        }
        orderRepository.deleteById(orderId);
    }

    /**
     * Validates if a status transition is allowed
     *
     * @param currentStatus the current order status
     * @param newStatus the new order status
     * @return true if transition is valid
     */
    private boolean isValidStatusTransition(String currentStatus, String newStatus) {
        return switch (currentStatus) {
            case Constants.ORDER_STATUS_PENDING -> newStatus.equals(Constants.ORDER_STATUS_CONFIRMED) ||
                                                   newStatus.equals(Constants.ORDER_STATUS_CANCELLED);
            case Constants.ORDER_STATUS_CONFIRMED -> newStatus.equals(Constants.ORDER_STATUS_PROCESSING) ||
                                                     newStatus.equals(Constants.ORDER_STATUS_CANCELLED);
            case Constants.ORDER_STATUS_PROCESSING -> newStatus.equals(Constants.ORDER_STATUS_SHIPPED);
            case Constants.ORDER_STATUS_SHIPPED -> newStatus.equals(Constants.ORDER_STATUS_DELIVERED);
            case Constants.ORDER_STATUS_CANCELLED, Constants.ORDER_STATUS_DELIVERED -> false; // Terminal states
            default -> true; // Allow REFUND_PENDING from any state
        };
    }

    /**
     * Generates a unique order number
     *
     * Format: ORD-YYYYMMDD-XXXXX (e.g., ORD-20250118-00001)
     *
     * @return unique order number
     */
    private String generateOrderNumber() {
        String datePrefix = java.time.LocalDate.now().toString().replace("-", "");
        long orderCount = orderRepository.count() + 1;
        return String.format("ORD-%s-%05d", datePrefix, orderCount);
    }

    /**
     * Converts Order entity to OrderResponse DTO
     *
     * @param order the order entity
     * @return OrderResponse DTO
     */
    private OrderResponse convertToResponse(Order order) {
        List<OrderItemDetailResponse> itemResponses = order.getOrderItems() != null ?
                order.getOrderItems().stream()
                        .map(item -> OrderItemDetailResponse.builder()
                                .id(item.getId())
                                .productId(item.getProductId())
                                .quantity(item.getQuantity())
                                .price(item.getPrice())
                                .build())
                        .collect(Collectors.toList()) : List.of();

        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
//                .orderItems(itemResponses)
                .orderItems(null)

                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
