package com.aquaworld.graphql.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PaymentInput DTO - for processing a payment
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentInput {
    private Long orderId;
    private Double amount;
    private String paymentMethod;
}
