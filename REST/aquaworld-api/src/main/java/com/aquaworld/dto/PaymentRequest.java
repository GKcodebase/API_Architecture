package com.aquaworld.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Payment Request DTO
 *
 * Used when processing a payment for an order.
 *
 * HTTP Request Example:
 * POST /api/v1/payments
 * {
 *   "orderId": 3001,
 *   "amount": 16.97,
 *   "paymentMethod": "CREDIT_CARD"
 * }
 *
 * @author AquaWorld Development Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Payment processing request")
public class PaymentRequest {

    /**
     * Order ID to pay for
     *
     * Validation:
     * - Required (not null)
     * - Must exist in orders
     */
    @NotNull(message = "Order ID is required")
    @Schema(description = "Order ID", example = "3001")
    private Long orderId;

    /**
     * Payment amount
     *
     * Validation:
     * - Required (not null)
     * - Must be greater than 0
     */
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Schema(description = "Payment amount", example = "16.97")
    private BigDecimal amount;

    /**
     * Payment method
     *
     * Values: CREDIT_CARD, DEBIT_CARD, DIGITAL_WALLET, BANK_TRANSFER
     */
    @NotNull(message = "Payment method is required")
    @Schema(description = "Payment method", example = "CREDIT_CARD")
    private String paymentMethod;
}
