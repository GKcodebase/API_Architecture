package com.aquaworld.graphql.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payment entity - represents a payment transaction
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    private Long id;
    private Long orderId;
    private Double amount;
    private String status; // PENDING, SUCCESS, FAILED, REFUNDED
    private String paymentMethod; // CREDIT_CARD, DEBIT_CARD, DIGITAL_WALLET, BANK_TRANSFER
    private String transactionId;
    private String createdAt;
    private String updatedAt;
}
