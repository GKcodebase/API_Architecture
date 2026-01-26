package com.aquaworld.graphql.service;

import com.aquaworld.graphql.dto.PaymentInput;
import com.aquaworld.graphql.dto.UpdateUserInput;
import com.aquaworld.graphql.exception.InvalidOperationException;
import com.aquaworld.graphql.exception.ResourceNotFoundException;
import com.aquaworld.graphql.model.Payment;
import com.aquaworld.graphql.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Payment Service
 * Handles payment operations (process, refund)
 */
@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderService orderService;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Get payment by ID
     */
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
    }

    /**
     * Get payment by order ID
     */
    public Payment getPaymentByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "orderId", orderId));
    }

    /**
     * Process payment for an order
     */
    public Payment processPayment(PaymentInput input) {
        // Verify order exists
        orderService.getOrderById(input.getOrderId());

        // Check if payment already exists for this order
        paymentRepository.findByOrderId(input.getOrderId()).ifPresent(p -> {
            throw new InvalidOperationException("Payment already exists for this order");
        });

        // Create payment with unique transaction ID
        Payment payment = Payment.builder()
                .orderId(input.getOrderId())
                .amount(input.getAmount())
                .paymentMethod(input.getPaymentMethod())
                .transactionId(generateTransactionId())
                .status("SUCCESS") // In real system, this would depend on payment gateway
                .createdAt(LocalDateTime.now().format(formatter))
                .updatedAt(LocalDateTime.now().format(formatter))
                .build();

        // Update order status to CONFIRMED
        orderService.updateOrderStatus(input.getOrderId(), "CONFIRMED");

        return paymentRepository.save(payment);
    }

    /**
     * Refund a payment
     */
    public Payment refundPayment(Long id) {
        Payment payment = getPaymentById(id);

        if (payment.getStatus().equals("REFUNDED")) {
            throw new InvalidOperationException("Payment is already refunded");
        }

        if (!payment.getStatus().equals("SUCCESS")) {
            throw new InvalidOperationException("Only successful payments can be refunded");
        }

        payment.setStatus("REFUNDED");
        payment.setUpdatedAt(LocalDateTime.now().format(formatter));

        // Update order status to CANCELLED
        orderService.updateOrderStatus(payment.getOrderId(), "CANCELLED");

        return paymentRepository.save(payment);
    }

    /**
     * Generate unique transaction ID
     */
    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
