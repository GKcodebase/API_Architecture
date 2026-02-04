package com.aquaworld.grpc.service;

import com.aquaworld.grpc.model.Payment;
import com.aquaworld.grpc.exception.ResourceNotFoundException;
import com.aquaworld.grpc.exception.InvalidInputException;
import com.aquaworld.grpc.repository.PaymentRepository;
import com.aquaworld.grpc.util.Constants;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Payment Service for AquaWorld gRPC API
 * Handles payment processing operations
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;

    /**
     * Process a payment
     *
     * @param orderId the order ID
     * @param amount the payment amount
     * @param paymentMethod the payment method
     * @return Payment
     */
    public Payment processPayment(String orderId, Double amount, String paymentMethod) {
        if (amount == null || amount <= 0) {
            throw new InvalidInputException("Payment amount must be greater than 0");
        }

        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setAmount(amount);
        payment.setPaymentMethod(paymentMethod);
        payment.setStatus(Constants.PAYMENT_STATUS_SUCCESS);
        payment.setTransactionId(UUID.randomUUID().toString());

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment {} processed successfully for order {}", payment.getPaymentId(), orderId);
        return savedPayment;
    }

    /**
     * Get payment by ID
     *
     * @param paymentId the payment ID
     * @return Payment
     * @throws ResourceNotFoundException if payment not found
     */
    public Payment getPayment(String paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));
    }

    /**
     * Get payment by order ID
     *
     * @param orderId the order ID
     * @return Payment
     * @throws ResourceNotFoundException if payment not found
     */
    public Payment getPaymentByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment for order", orderId));
    }

    /**
     * Refund a payment
     *
     * @param paymentId the payment ID
     * @param reason the refund reason
     * @return refunded Payment
     * @throws ResourceNotFoundException if payment not found
     */
    public Payment refundPayment(String paymentId, String reason) {
        Payment payment = getPayment(paymentId);

        if (Constants.PAYMENT_STATUS_REFUNDED.equals(payment.getStatus())) {
            throw new InvalidInputException("Payment already refunded");
        }

        payment.setStatus(Constants.PAYMENT_STATUS_REFUNDED);
        Payment refundedPayment = paymentRepository.save(payment);
        log.info("Payment {} refunded successfully. Reason: {}", paymentId, reason);
        return refundedPayment;
    }
}
