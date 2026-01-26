package com.aquaworld.graphql.resolver;

import com.aquaworld.graphql.model.Order;
import com.aquaworld.graphql.model.Payment;
import com.aquaworld.graphql.model.Product;
import com.aquaworld.graphql.model.User;
import com.aquaworld.graphql.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * GraphQL Query Resolver
 * Handles all read operations (queries)
 */
@Controller
public class QueryResolver {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserService userService;

    // ==================== Product Queries ====================

    @QueryMapping
    public List<Product> products(
            @Argument(name = "limit") Integer limit,
            @Argument(name = "offset") Integer offset) {
        List<Product> products = productService.getAllProducts();
        
        // Apply pagination if provided
        if (limit != null && offset != null) {
            int start = offset != null ? offset : 0;
            int end = Math.min(start + (limit != null ? limit : 10), products.size());
            return products.subList(start, end);
        }
        
        return products;
    }

    @QueryMapping
    public Product product(@Argument Long id) {
        return productService.getProductById(id);
    }

    @QueryMapping
    public List<Product> searchProducts(@Argument String name) {
        return productService.searchProducts(name);
    }

    @QueryMapping
    public List<Product> productsByCategory(@Argument String category) {
        return productService.getProductsByCategory(category);
    }

    @QueryMapping
    public List<Product> availableProducts() {
        return productService.getAvailableProducts();
    }

    // ==================== User Queries ====================

    @QueryMapping
    public User me() {
        try {
            return userService.getCurrentUser();
        } catch (Exception e) {
            // Return null if not authenticated (allows introspection to work)
            return null;
        }
    }

    @QueryMapping
    public User user(@Argument String username) {
        return userService.getUserByUsername(username);
    }

    // ==================== Order Queries ====================

    @QueryMapping
    public List<Order> orders(
            @Argument(name = "limit") Integer limit,
            @Argument(name = "offset") Integer offset) {
        try {
            User currentUser = userService.getCurrentUser();
            List<Order> orders = orderService.getOrdersByUserId(currentUser.getId());
            
            // Apply pagination if provided
            if (limit != null && offset != null) {
                int start = offset != null ? offset : 0;
                int end = Math.min(start + (limit != null ? limit : 10), orders.size());
                return orders.subList(start, end);
            }
            
            return orders;
        } catch (Exception e) {
            // Return empty list if not authenticated
            return List.of();
        }
    }

    @QueryMapping
    public Order order(@Argument Long id) {
        try {
            Order order = orderService.getOrderById(id);
            // Verify user owns this order
            User currentUser = userService.getCurrentUser();
            if (!order.getUserId().equals(currentUser.getId())) {
                throw new SecurityException("You don't have permission to view this order");
            }
            return order;
        } catch (Exception e) {
            // Return null if not authenticated or permission denied
            return null;
        }
    }

    @QueryMapping
    public Order orderByNumber(@Argument String orderNumber) {
        try {
            Order order = orderService.getOrderByOrderNumber(orderNumber);
            // Verify user owns this order
            User currentUser = userService.getCurrentUser();
            if (!order.getUserId().equals(currentUser.getId())) {
                throw new SecurityException("You don't have permission to view this order");
            }
            return order;
        } catch (Exception e) {
            // Return null if not authenticated or permission denied
            return null;
        }
    }

    // ==================== Payment Queries ====================

    @QueryMapping
    public Payment payment(@Argument Long id) {
        try {
            Payment payment = paymentService.getPaymentById(id);
            // Verify user owns the associated order
            Order order = orderService.getOrderById(payment.getOrderId());
            User currentUser = userService.getCurrentUser();
            if (!order.getUserId().equals(currentUser.getId())) {
                throw new SecurityException("You don't have permission to view this payment");
            }
            return payment;
        } catch (Exception e) {
            // Return null if not authenticated or permission denied
            return null;
        }
    }

    @QueryMapping
    public Payment paymentByOrder(@Argument Long orderId) {
        try {
            // Verify user owns this order
            Order order = orderService.getOrderById(orderId);
            User currentUser = userService.getCurrentUser();
            if (!order.getUserId().equals(currentUser.getId())) {
                throw new SecurityException("You don't have permission to view payments for this order");
            }
            return paymentService.getPaymentByOrderId(orderId);
        } catch (Exception e) {
            // Return null if not authenticated or permission denied
            return null;
        }
    }
}
