package com.aquaworld.controller;

import com.aquaworld.dto.CreateOrderRequest;
import com.aquaworld.dto.OrderResponse;
import com.aquaworld.response.ApiResponse;
import com.aquaworld.service.OrderService;
import com.aquaworld.util.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Order REST Controller for AquaWorld
 *
 * Provides endpoints for order management.
 *
 * Base Path: /api/v1/orders
 * Authentication: REQUIRED (JWT token in Authorization header)
 *
 * Endpoints:
 * - GET /orders - Get user's orders
 * - POST /orders - Create new order
 * - GET /orders/{id} - Get order details
 * - PUT /orders/{id}/status - Update order status
 * - DELETE /orders/{id} - Cancel order
 *
 * HTTP Response Status Codes:
 * - 200 OK: Request successful
 * - 201 Created: Order created successfully
 * - 204 No Content: Order deleted
 * - 400 Bad Request: Invalid input/insufficient stock
 * - 401 Unauthorized: Missing/invalid JWT token
 * - 404 Not Found: Order or product not found
 * - 500 Internal Server Error: Server error
 *
 * JWT Token Format:
 * Authorization: Bearer <JWT_TOKEN>
 *
 * @author AquaWorld Development Team
 */
@RestController
@RequestMapping(Constants.ORDERS_ENDPOINT)
@Tag(name = "Orders", description = "Order management endpoints (requires authentication)")
@SecurityRequirement(name = "Bearer Authentication")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Get authenticated user's orders
     *
     * HTTP Request:
     * GET /api/v1/orders
     * Authorization: Bearer <JWT_TOKEN>
     *
     * Success Response (200 OK):
     * {
     *   "success": true,
     *   "message": "OK",
     *   "statusCode": 200,
     *   "data": [
     *     {
     *       "id": 3001,
     *       "orderNumber": "ORD-20250118-001",
     *       "totalPrice": 16.97,
     *       "status": "PENDING",
     *       "createdAt": "2025-01-18T10:30:00"
     *     }
     *   ]
     * }
     *
     * Error Response (401 Unauthorized):
     * {
     *   "success": false,
     *   "message": "Unauthorized - Invalid or missing token",
     *   "statusCode": 401
     * }
     *
     * @param authentication the authenticated user info from JWT token
     * @return ResponseEntity with user's orders
     */
    @GetMapping
    @Operation(summary = "Get user's orders", description = "Retrieve all orders placed by authenticated user")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Orders retrieved successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - invalid or missing JWT token"
            )
    })
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getUserOrders(Authentication authentication) {
        // Get user ID from authentication (username is stored in principal)
        // In production, use a proper user ID from JWT claims
        Long userId = Long.parseLong(authentication.getPrincipal().toString().split("_")[0]);

        List<OrderResponse> orders = orderService.getUserOrders(userId);

        ApiResponse<List<OrderResponse>> response = ApiResponse.success(
                "Orders retrieved successfully",
                orders,
                HttpStatus.OK.value()
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Create a new order
     *
     * HTTP Request:
     * POST /api/v1/orders
     * Authorization: Bearer <JWT_TOKEN>
     * Content-Type: application/json
     * {
     *   "orderItems": [
     *     {
     *       "productId": 2001,
     *       "quantity": 2
     *     },
     *     {
     *       "productId": 2002,
     *       "quantity": 1
     *     }
     *   ]
     * }
     *
     * Success Response (201 Created):
     * {
     *   "success": true,
     *   "message": "Order created successfully",
     *   "statusCode": 201,
     *   "data": {
     *     "id": 3001,
     *     "orderNumber": "ORD-20250118-001",
     *     "totalPrice": 16.97,
     *     "status": "PENDING",
     *     "orderItems": [
     *       {
     *         "id": 4001,
     *         "productId": 2001,
     *         "quantity": 2,
     *         "price": 5.99
     *       }
     *     ],
     *     "createdAt": "2025-01-18T10:30:00"
     *   }
     * }
     *
     * Error Responses:
     * - 400 Bad Request: Invalid input, no items, insufficient stock
     * - 401 Unauthorized: Invalid JWT token
     * - 404 Not Found: Product not found
     *
     * @param authentication the authenticated user
     * @param createOrderRequest the order items to create
     * @return ResponseEntity with created order (201 Created)
     */
    @PostMapping
    @Operation(summary = "Create new order", description = "Create a new order with items")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Order created successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid input or insufficient stock"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            )
    })
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            Authentication authentication,
            @Valid @RequestBody CreateOrderRequest createOrderRequest) {
        // Extract user ID from authentication
        Long userId = Long.parseLong(authentication.getPrincipal().toString().split("_")[0]);

        OrderResponse order = orderService.createOrder(userId, createOrderRequest);

        ApiResponse<OrderResponse> response = ApiResponse.success(
                Constants.MSG_ORDER_CREATED,
                order,
                HttpStatus.CREATED.value()
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get order details by ID
     *
     * HTTP Request:
     * GET /api/v1/orders/3001
     * Authorization: Bearer <JWT_TOKEN>
     *
     * Success Response (200 OK):
     * {
     *   "success": true,
     *   "message": "OK",
     *   "statusCode": 200,
     *   "data": {
     *     "id": 3001,
     *     "orderNumber": "ORD-20250118-001",
     *     "totalPrice": 16.97,
     *     "status": "PENDING",
     *     "orderItems": [ ... ]
     *   }
     * }
     *
     * Error Response (404 Not Found):
     * {
     *   "success": false,
     *   "message": "Order not found",
     *   "statusCode": 404
     * }
     *
     * @param orderId the order ID
     * @return ResponseEntity with order details
     */
    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID", description = "Retrieve order details by order ID")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Order found and returned"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Order not found"
            )
    })
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @Parameter(description = "Order ID", example = "3001")
            @PathVariable Long orderId) {
        OrderResponse order = orderService.getOrderById(orderId);

        ApiResponse<OrderResponse> response = ApiResponse.success(
                "Order retrieved successfully",
                order,
                HttpStatus.OK.value()
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Update order status
     *
     * HTTP Request:
     * PUT /api/v1/orders/3001/status
     * Authorization: Bearer <JWT_TOKEN>
     * Content-Type: application/json
     * {
     *   "status": "CONFIRMED"
     * }
     *
     * Success Response (200 OK):
     * {
     *   "success": true,
     *   "message": "Order updated successfully",
     *   "statusCode": 200,
     *   "data": {
     *     "id": 3001,
     *     "status": "CONFIRMED"
     *   }
     * }
     *
     * Error Response (400 Bad Request):
     * {
     *   "success": false,
     *   "message": "Cannot update order from PENDING to DELIVERED",
     *   "statusCode": 400
     * }
     *
     * @param orderId the order ID
     * @param statusRequest contains new status
     * @return ResponseEntity with updated order
     */
    @PutMapping("/{orderId}/status")
    @Operation(summary = "Update order status", description = "Change order status (PENDING -> CONFIRMED -> PROCESSING -> SHIPPED -> DELIVERED)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Order status updated successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid status transition"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Order not found"
            )
    })
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @Parameter(description = "Order ID", example = "3001")
            @PathVariable Long orderId,
            @RequestBody StatusUpdateRequest statusRequest) {
        OrderResponse order = orderService.updateOrderStatus(orderId, statusRequest.getStatus());

        ApiResponse<OrderResponse> response = ApiResponse.success(
                Constants.MSG_ORDER_UPDATED,
                order,
                HttpStatus.OK.value()
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Cancel an order
     *
     * HTTP Request:
     * DELETE /api/v1/orders/3001
     * Authorization: Bearer <JWT_TOKEN>
     *
     * Success Response (200 OK):
     * {
     *   "success": true,
     *   "message": "Order cancelled successfully",
     *   "statusCode": 200,
     *   "data": {
     *     "id": 3001,
     *     "status": "CANCELLED"
     *   }
     * }
     *
     * Error Response (400 Bad Request):
     * {
     *   "success": false,
     *   "message": "Cannot cancel order in current status",
     *   "statusCode": 400
     * }
     *
     * @param orderId the order ID to cancel
     * @return ResponseEntity with cancelled order
     */
    @DeleteMapping("/{orderId}")
    @Operation(summary = "Cancel order", description = "Cancel an order and refund stock")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Order cancelled successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Cannot cancel order in current status"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Order not found"
            )
    })
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @Parameter(description = "Order ID", example = "3001")
            @PathVariable Long orderId) {
        OrderResponse order = orderService.cancelOrder(orderId);

        ApiResponse<OrderResponse> response = ApiResponse.success(
                Constants.MSG_ORDER_CANCELLED,
                order,
                HttpStatus.OK.value()
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Helper class for status update request
     */
    public static class StatusUpdateRequest {
        private String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
