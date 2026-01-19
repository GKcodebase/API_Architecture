package com.aquaworld.controller;

import com.aquaworld.dto.PaymentRequest;
import com.aquaworld.dto.PaymentResponse;
import com.aquaworld.response.ApiResponse;
import com.aquaworld.service.PaymentService;
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

/**
 * Payment REST Controller for AquaWorld
 *
 * Provides endpoints for payment processing.
 *
 * Base Path: /api/v1/payments
 * Authentication: REQUIRED (JWT token in Authorization header)
 *
 * Endpoints:
 * - POST /payments - Process payment
 * - GET /payments/{id} - Get payment details
 * - GET /payments/order/{orderId} - Get payment by order ID
 * - POST /payments/{id}/refund - Refund payment
 *
 * HTTP Response Status Codes:
 * - 200 OK: Request successful
 * - 201 Created: Payment processed successfully
 * - 400 Bad Request: Invalid amount/input
 * - 401 Unauthorized: Missing/invalid JWT token
 * - 404 Not Found: Order or payment not found
 * - 503 Service Unavailable: Payment gateway unavailable
 *
 * Payment Methods:
 * - CREDIT_CARD
 * - DEBIT_CARD
 * - DIGITAL_WALLET
 * - BANK_TRANSFER
 *
 * @author AquaWorld Development Team
 */
@RestController
@RequestMapping(Constants.PAYMENTS_ENDPOINT)
@Tag(name = "Payments", description = "Payment processing endpoints (requires authentication)")
@SecurityRequirement(name = "Bearer Authentication")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Process payment for an order
     *
     * HTTP Request:
     * POST /api/v1/payments
     * Authorization: Bearer <JWT_TOKEN>
     * Content-Type: application/json
     * {
     *   "orderId": 3001,
     *   "amount": 16.97,
     *   "paymentMethod": "CREDIT_CARD"
     * }
     *
     * Success Response (201 Created):
     * {
     *   "success": true,
     *   "message": "Payment processed successfully",
     *   "statusCode": 201,
     *   "data": {
     *     "id": 5001,
     *     "orderId": 3001,
     *     "amount": 16.97,
     *     "status": "SUCCESS",
     *     "paymentMethod": "CREDIT_CARD",
     *     "transactionId": "TXN-550e8400-e29b-41d4-a716",
     *     "createdAt": "2025-01-18T10:35:00"
     *   }
     * }
     *
     * Error Responses:
     * - 400 Bad Request: Amount doesn't match order, invalid input
     * - 404 Not Found: Order not found
     * - 503 Service Unavailable: Payment gateway unavailable
     *
     * Integration Point:
     * In production, call actual payment gateway API (Stripe, PayPal, etc.)
     * This demo simulates successful payment processing
     *
     * @param authentication the authenticated user
     * @param paymentRequest contains orderId, amount, paymentMethod
     * @return ResponseEntity with payment result (201 Created)
     */
    @PostMapping
    @Operation(summary = "Process payment", description = "Process payment for an order")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Payment processed successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid payment amount or input"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - invalid or missing JWT token"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Order not found"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "503",
                    description = "Payment gateway unavailable"
            )
    })
    public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(
            Authentication authentication,
            @Valid @RequestBody PaymentRequest paymentRequest) {
        PaymentResponse payment = paymentService.processPayment(paymentRequest);

        ApiResponse<PaymentResponse> response = ApiResponse.success(
                Constants.MSG_PAYMENT_PROCESSED,
                payment,
                HttpStatus.CREATED.value()
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get payment details by ID
     *
     * HTTP Request:
     * GET /api/v1/payments/5001
     * Authorization: Bearer <JWT_TOKEN>
     *
     * Success Response (200 OK):
     * {
     *   "success": true,
     *   "message": "OK",
     *   "statusCode": 200,
     *   "data": {
     *     "id": 5001,
     *     "orderId": 3001,
     *     "amount": 16.97,
     *     "status": "SUCCESS",
     *     "paymentMethod": "CREDIT_CARD",
     *     "transactionId": "TXN-550e8400-e29b-41d4-a716"
     *   }
     * }
     *
     * Error Response (404 Not Found):
     * {
     *   "success": false,
     *   "message": "Payment not found",
     *   "statusCode": 404
     * }
     *
     * @param paymentId the payment ID
     * @return ResponseEntity with payment details
     */
    @GetMapping("/{paymentId}")
    @Operation(summary = "Get payment by ID", description = "Retrieve payment details by payment ID")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Payment found and returned"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Payment not found"
            )
    })
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(
            @Parameter(description = "Payment ID", example = "5001")
            @PathVariable Long paymentId) {
        PaymentResponse payment = paymentService.getPaymentById(paymentId);

        ApiResponse<PaymentResponse> response = ApiResponse.success(
                "Payment retrieved successfully",
                payment,
                HttpStatus.OK.value()
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get payment by order ID
     *
     * HTTP Request:
     * GET /api/v1/payments/order/3001
     * Authorization: Bearer <JWT_TOKEN>
     *
     * Success Response (200 OK):
     * {
     *   "success": true,
     *   "message": "OK",
     *   "statusCode": 200,
     *   "data": {
     *     "id": 5001,
     *     "orderId": 3001,
     *     "amount": 16.97,
     *     "status": "SUCCESS"
     *   }
     * }
     *
     * Error Response (404 Not Found):
     * {
     *   "success": false,
     *   "message": "Payment not found",
     *   "statusCode": 404
     * }
     *
     * @param orderId the order ID
     * @return ResponseEntity with payment details
     */
    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get payment by order", description = "Retrieve payment details for a specific order")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Payment found and returned"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Payment not found for order"
            )
    })
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByOrderId(
            @Parameter(description = "Order ID", example = "3001")
            @PathVariable Long orderId) {
        PaymentResponse payment = paymentService.getPaymentByOrderId(orderId);

        ApiResponse<PaymentResponse> response = ApiResponse.success(
                "Payment retrieved successfully",
                payment,
                HttpStatus.OK.value()
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Refund a payment
     *
     * HTTP Request:
     * POST /api/v1/payments/5001/refund
     * Authorization: Bearer <JWT_TOKEN>
     *
     * Success Response (200 OK):
     * {
     *   "success": true,
     *   "message": "OK",
     *   "statusCode": 200,
     *   "data": {
     *     "id": 5001,
     *     "status": "REFUNDED"
     *   }
     * }
     *
     * Error Response (400 Bad Request):
     * {
     *   "success": false,
     *   "message": "Can only refund successfully processed payments",
     *   "statusCode": 400
     * }
     *
     * @param paymentId the payment ID to refund
     * @return ResponseEntity with refunded payment
     */
    @PostMapping("/{paymentId}/refund")
    @Operation(summary = "Refund payment", description = "Refund a successfully processed payment")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Payment refunded successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Cannot refund payment in current status"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Payment not found"
            )
    })
    public ResponseEntity<ApiResponse<PaymentResponse>> refundPayment(
            @Parameter(description = "Payment ID", example = "5001")
            @PathVariable Long paymentId) {
        PaymentResponse payment = paymentService.refundPayment(paymentId);

        ApiResponse<PaymentResponse> response = ApiResponse.success(
                "Payment refunded successfully",
                payment,
                HttpStatus.OK.value()
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
