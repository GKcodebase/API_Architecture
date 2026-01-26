package com.aquaworld.graphql.resolver;

import com.aquaworld.graphql.dto.*;
import com.aquaworld.graphql.model.Order;
import com.aquaworld.graphql.model.Payment;
import com.aquaworld.graphql.model.Product;
import com.aquaworld.graphql.model.User;
import com.aquaworld.graphql.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

/**
 * GraphQL Mutation Resolver
 * Handles all write operations (mutations)
 */
@Controller
public class MutationResolver {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserService userService;

    // ==================== Authentication Mutations ====================

    @MutationMapping
    public AuthPayload register(@Argument RegisterInput input) {
        return authenticationService.register(input);
    }

    @MutationMapping
    public AuthPayload login(@Argument LoginInput input) {
        return authenticationService.login(input);
    }

    // ==================== Product Mutations ====================

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Product createProduct(@Argument CreateProductInput input) {
        return productService.createProduct(input);
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Product updateProduct(@Argument Long id, @Argument UpdateProductInput input) {
        return productService.updateProduct(id, input);
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Boolean deleteProduct(@Argument Long id) {
        return productService.deleteProduct(id);
    }

    // ==================== Order Mutations ====================

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Order createOrder(@Argument CreateOrderInput input) {
        User currentUser = userService.getCurrentUser();
        return orderService.createOrder(currentUser.getId(), input);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Order updateOrderStatus(@Argument Long id, @Argument String status) {
        Order order = orderService.getOrderById(id);
        // Verify user owns this order or is admin
        User currentUser = userService.getCurrentUser();
        if (!order.getUserId().equals(currentUser.getId()) && !currentUser.getRole().equals("ADMIN")) {
            throw new SecurityException("You don't have permission to update this order");
        }
        return orderService.updateOrderStatus(id, status);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Order cancelOrder(@Argument Long id) {
        Order order = orderService.getOrderById(id);
        // Verify user owns this order
        User currentUser = userService.getCurrentUser();
        if (!order.getUserId().equals(currentUser.getId())) {
            throw new SecurityException("You don't have permission to cancel this order");
        }
        return orderService.cancelOrder(id);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Boolean deleteOrder(@Argument Long id) {
        Order order = orderService.getOrderById(id);
        // Verify user owns this order or is admin
        User currentUser = userService.getCurrentUser();
        if (!order.getUserId().equals(currentUser.getId()) && !currentUser.getRole().equals("ADMIN")) {
            throw new SecurityException("You don't have permission to delete this order");
        }
        return orderService.deleteOrder(id);
    }

    // ==================== Payment Mutations ====================

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Payment processPayment(@Argument PaymentInput input) {
        // Verify user owns the associated order
        Order order = orderService.getOrderById(input.getOrderId());
        User currentUser = userService.getCurrentUser();
        if (!order.getUserId().equals(currentUser.getId())) {
            throw new SecurityException("You don't have permission to pay for this order");
        }
        return paymentService.processPayment(input);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Payment refundPayment(@Argument Long id) {
        Payment payment = paymentService.getPaymentById(id);
        // Verify user owns the associated order
        Order order = orderService.getOrderById(payment.getOrderId());
        User currentUser = userService.getCurrentUser();
        if (!order.getUserId().equals(currentUser.getId()) && !currentUser.getRole().equals("ADMIN")) {
            throw new SecurityException("You don't have permission to refund this payment");
        }
        return paymentService.refundPayment(id);
    }

    // ==================== User Mutations ====================

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public User updateProfile(@Argument UpdateUserInput input) {
        User currentUser = userService.getCurrentUser();
        return userService.updateProfile(currentUser.getId(), input);
    }
}
