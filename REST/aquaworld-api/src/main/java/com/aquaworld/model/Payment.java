package com.aquaworld.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payment Data Model (DBO - Database Object) for AquaWorld REST API
 *
 * Represents a payment transaction for an order.
 * Tracks payment status and method.
 *
 * Payment Lifecycle:
 * 1. PENDING: Payment initiated, awaiting processing
 * 2. SUCCESS: Payment successfully processed
 * 3. FAILED: Payment processing failed
 * 4. REFUNDED: Payment has been refunded
 *
 * Payment Methods:
 * - CREDIT_CARD: Visa, Mastercard, American Express
 * - DEBIT_CARD: Bank debit cards
 * - DIGITAL_WALLET: PayPal, Apple Pay, Google Pay
 * - BANK_TRANSFER: Direct bank transfer
 * - CRYPTOCURRENCY: Bitcoin, Ethereum (if supported)
 *
 * Fields:
 * - id: Unique payment identifier
 * - orderId: Which order this payment is for
 * - amount: Payment amount in USD
 * - status: Payment status (PENDING, SUCCESS, FAILED, REFUNDED)
 * - paymentMethod: How the customer paid
 * - transactionId: External payment gateway transaction ID
 * - createdAt: When payment was initiated
 *
 * Usage:
 * - Process payments
 * - Track payment status
 * - Handle refunds
 * - Generate payment receipts
 *
 * @author AquaWorld Development Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Payment {

    /** Unique payment identifier */
    private Long id;

    /** Order ID this payment is for */
    private Long orderId;

    /** Payment amount in USD */
    private BigDecimal amount;

    /** Payment status: PENDING, SUCCESS, FAILED, REFUNDED */
    private String status;

    /** Payment method: CREDIT_CARD, DEBIT_CARD, DIGITAL_WALLET, BANK_TRANSFER */
    private String paymentMethod;

    /** External payment gateway transaction ID (for reference) */
    private String transactionId;

    /** When the payment was initiated */
    private LocalDateTime createdAt;

    /** When the payment status was last updated */
    private LocalDateTime updatedAt;

}
