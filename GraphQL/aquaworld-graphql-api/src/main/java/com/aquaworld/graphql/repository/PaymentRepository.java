package com.aquaworld.graphql.repository;

import com.aquaworld.graphql.model.Payment;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Payment Repository - In-memory storage for payments
 */
@Repository
public class PaymentRepository {
    private final Map<Long, Payment> payments = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public Payment save(Payment payment) {
        if (payment.getId() == null) {
            payment.setId(idCounter.getAndIncrement());
        }
        payments.put(payment.getId(), payment);
        return payment;
    }

    public Optional<Payment> findById(Long id) {
        return Optional.ofNullable(payments.get(id));
    }

    public Optional<Payment> findByOrderId(Long orderId) {
        return payments.values().stream()
                .filter(p -> p.getOrderId().equals(orderId))
                .findFirst();
    }

    public Optional<Payment> findByTransactionId(String transactionId) {
        return payments.values().stream()
                .filter(p -> p.getTransactionId().equals(transactionId))
                .findFirst();
    }

    public List<Payment> findAll() {
        return new ArrayList<>(payments.values());
    }

    public void delete(Long id) {
        payments.remove(id);
    }

    public void deleteAll() {
        payments.clear();
    }

    public long count() {
        return payments.size();
    }
}
