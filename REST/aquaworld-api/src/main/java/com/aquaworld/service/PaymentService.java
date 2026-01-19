package com.aquaworld.service;

import com.aquaworld.dto.PaymentRequest;
import com.aquaworld.dto.PaymentResponse;
import com.aquaworld.exception.InvalidInputException;
import com.aquaworld.exception.ResourceNotFoundException;
import com.aquaworld.model.Order;
import com.aquaworld.model.Payment;
import com.aquaworld.repository.OrderRepository;
import com.aquaworld.repository.PaymentRepository;
import com.aquaworld.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Payment Service for AquaWorld REST API
 *
 * Handles payment processing business logic:
 * - Process payments for orders
 * - Validate payment amounts
 * - Track payment status
 * - Generate transaction IDs
 * - Handle payment updates
 *
 * Payment Status:
 * - PENDING: Payment initiated, processing
 * - SUCCESS: Payment successfully processed
 * - FAILED: Payment processing failed
 * - REFUNDED: Payment refunded back to customer
 *
 * Responsibilities:
 * - Payment creation and validation
 * - Amount verification (matches order total)
 * - Payment status tracking
 * - Transaction ID generation
 *
 * Does NOT:
 * - Actually process with payment gateway (integration point)
 * - Handle refunds directly
 * - Manage shipping
 *
 * @author AquaWorld Development Team
 */
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository,
                         OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * Process payment for an order
     *
     * Process:
     * 1. Find order by ID (404 if not found)
     * 2. Validate payment amount matches order total
     * 3. Create payment with PENDING status
     * 4. Generate transaction ID
     * 5. In production: Call payment gateway API here
     * 6. Update payment status based on gateway response
     * 7. Return payment response
     *
     * @param paymentRequest contains orderId, amount, paymentMethod
     * @return PaymentResponse
     * @throws ResourceNotFoundException if order not found (404)
     * @throws InvalidInputException if amount doesn't match order or order not found (400)
     */
    public PaymentResponse processPayment(PaymentRequest paymentRequest) {
        // Find order
        Order order = orderRepository.findById(paymentRequest.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException(Constants.MSG_ORDER_NOT_FOUND));

        // Validate payment amount matches order total
        if (paymentRequest.getAmount().compareTo(order.getTotalPrice()) != 0) {
            throw new InvalidInputException(
                    "Payment amount does not match order total. Expected: " + order.getTotalPrice() +
                    ", Received: " + paymentRequest.getAmount()
            );
        }

        // Check if payment already exists for this order
        if (paymentRepository.findByOrderId(paymentRequest.getOrderId()).isPresent()) {
            throw new InvalidInputException("Payment already exists for this order");
        }

        // Create payment
        Payment payment = Payment.builder()
                .orderId(paymentRequest.getOrderId())
                .amount(paymentRequest.getAmount())
                .paymentMethod(paymentRequest.getPaymentMethod())
                .status(Constants.PAYMENT_STATUS_PENDING)
                .transactionId(generateTransactionId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Save payment
        Payment savedPayment = paymentRepository.save(payment);

        // ========== INTEGRATION POINT ==========
        // In production, call payment gateway API here
        // Example: PaymentGateway.processPayment(payment.getTransactionId(), ...)
        //
        // For this demo, simulate successful payment for testing
        savedPayment.setStatus(Constants.PAYMENT_STATUS_SUCCESS);
        savedPayment.setUpdatedAt(LocalDateTime.now());
        paymentRepository.update(savedPayment);

        return convertToResponse(savedPayment);
    }

    /**
     * Get payment by ID
     *
     * @param paymentId the payment ID
     * @return PaymentResponse
     * @throws ResourceNotFoundException if payment not found (404)
     */
    public PaymentResponse getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.MSG_PAYMENT_NOT_FOUND));
        return convertToResponse(payment);
    }

    /**
     * Get payment by order ID
     *
     * @param orderId the order ID
     * @return PaymentResponse
     * @throws ResourceNotFoundException if payment not found (404)
     */
    public PaymentResponse getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.MSG_PAYMENT_NOT_FOUND));
        return convertToResponse(payment);
    }

    /**
     * Update payment status
     *
     * Used to mark payment as failed, refunded, etc.
     *
     * @param paymentId the payment ID
     * @param newStatus the new payment status
     * @return updated PaymentResponse
     * @throws ResourceNotFoundException if payment not found (404)
     */
    public PaymentResponse updatePaymentStatus(Long paymentId, String newStatus) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.MSG_PAYMENT_NOT_FOUND));

        payment.setStatus(newStatus);
        payment.setUpdatedAt(LocalDateTime.now());
        Payment updatedPayment = paymentRepository.update(payment);

        return convertToResponse(updatedPayment);
    }

    /**
     * Refund a payment
     *
     * Used when customer returns order
     * Creates a new refund transaction
     *
     * @param paymentId the payment ID to refund
     * @return updated PaymentResponse with REFUNDED status
     * @throws ResourceNotFoundException if payment not found (404)
     */
    public PaymentResponse refundPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.MSG_PAYMENT_NOT_FOUND));

        // Validate payment can be refunded
        if (!payment.getStatus().equals(Constants.PAYMENT_STATUS_SUCCESS)) {
            throw new InvalidInputException("Can only refund successfully processed payments");
        }

        // ========== INTEGRATION POINT ==========
        // In production, call payment gateway refund API here
        // Example: PaymentGateway.refund(payment.getTransactionId(), ...)

        // Update payment status
        payment.setStatus(Constants.PAYMENT_STATUS_REFUNDED);
        payment.setUpdatedAt(LocalDateTime.now());
        Payment refundedPayment = paymentRepository.update(payment);

        return convertToResponse(refundedPayment);
    }

    /**
     * Generates a unique transaction ID for payment tracking
     *
     * Format: TXN-UUID (e.g., TXN-550e8400-e29b-41d4-a716-446655440000)
     *
     * @return unique transaction ID
     */
    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().toUpperCase();
    }

    /**
     * Converts Payment entity to PaymentResponse DTO
     *
     * @param payment the payment entity
     * @return PaymentResponse DTO
     */
    private PaymentResponse convertToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .paymentMethod(payment.getPaymentMethod())
                .transactionId(payment.getTransactionId())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
