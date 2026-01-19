package com.aquaworld.repository;

import com.aquaworld.model.Payment;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.ArrayList;
import java.util.List;

/**
 * Payment Repository - In-Memory Data Access Object (DAO)
 *
 * Implements the Data Access Layer for Payment entities.
 * Manages payment transactions for orders.
 *
 * Payment Status:
 * - PENDING: Payment initiated, awaiting processing
 * - SUCCESS: Payment successfully processed
 * - FAILED: Payment failed
 * - REFUNDED: Payment refunded back to customer
 *
 * Thread-Safe Storage:
 * - ConcurrentHashMap for thread-safe payment storage
 * - AtomicLong for auto-incrementing payment IDs
 *
 * Operations:
 * - Create, Read, Update payments
 * - Find payments by order ID
 * - Find payments by status
 * - List all payments
 *
 * @author AquaWorld Development Team
 */
@Repository
public class PaymentRepository {

    // In-memory storage for payments: Map<paymentId, Payment>
    private final ConcurrentHashMap<Long, Payment> payments = new ConcurrentHashMap<>();

    // Auto-increment ID generator (starting from 5000)
    private final AtomicLong idGenerator = new AtomicLong(5000);

    /**
     * Save a new payment to the repository
     *
     * Generates a new ID if not set, and stores the payment.
     *
     * @param payment the payment to save
     * @return the saved payment with generated ID
     */
    public Payment save(Payment payment) {
        if (payment.getId() == null) {
            payment.setId(idGenerator.incrementAndGet());
        }
        payments.put(payment.getId(), payment);
        return payment;
    }

    /**
     * Find payment by ID
     *
     * @param id the payment ID
     * @return Optional containing the payment if found
     */
    public Optional<Payment> findById(Long id) {
        return Optional.ofNullable(payments.get(id));
    }

    /**
     * Find payment by order ID
     *
     * Each order typically has one payment record
     *
     * @param orderId the order ID
     * @return Optional containing the payment if found
     */
    public Optional<Payment> findByOrderId(Long orderId) {
        return payments.values().stream()
                .filter(payment -> payment.getOrderId().equals(orderId))
                .findFirst();
    }

    /**
     * Find all payments with specific status
     *
     * Statuses: PENDING, SUCCESS, FAILED, REFUNDED
     *
     * @param status the payment status
     * @return list of payments with the given status
     */
    public List<Payment> findByStatus(String status) {
        return payments.values().stream()
                .filter(payment -> payment.getStatus().equals(status))
                .toList();
    }

    /**
     * Find payment by transaction ID (from payment gateway)
     *
     * @param transactionId the transaction ID
     * @return Optional containing the payment if found
     */
    public Optional<Payment> findByTransactionId(String transactionId) {
        return payments.values().stream()
                .filter(payment -> payment.getTransactionId() != null &&
                        payment.getTransactionId().equals(transactionId))
                .findFirst();
    }

    /**
     * Get all payments
     *
     * @return list of all payments
     */
    public List<Payment> findAll() {
        return new ArrayList<>(payments.values());
    }

    /**
     * Update an existing payment
     *
     * @param payment the payment with updated information
     * @return the updated payment
     */
    public Payment update(Payment payment) {
        payments.put(payment.getId(), payment);
        return payment;
    }

    /**
     * Delete a payment by ID
     *
     * @param id the payment ID to delete
     * @return true if deleted, false if not found
     */
    public boolean deleteById(Long id) {
        return payments.remove(id) != null;
    }

    /**
     * Check if payment exists
     *
     * @param id the payment ID
     * @return true if payment exists
     */
    public boolean existsById(Long id) {
        return payments.containsKey(id);
    }

    /**
     * Get total number of payments
     *
     * @return payment count
     */
    public long count() {
        return payments.size();
    }

    /**
     * Clear all payments (useful for testing)
     */
    public void deleteAll() {
        payments.clear();
    }
}
