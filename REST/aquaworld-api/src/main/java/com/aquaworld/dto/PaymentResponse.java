package com.aquaworld.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payment Response DTO
 *
 * Returned to client after payment processing.
 *
 * HTTP Response Example:
 * {
 *   "id": 5001,
 *   "orderId": 3001,
 *   "amount": 16.97,
 *   "status": "SUCCESS",
 *   "paymentMethod": "CREDIT_CARD",
 *   "transactionId": "TXN-20250118-001",
 *   "createdAt": "2025-01-18T10:35:00"
 * }
 *
 * @author AquaWorld Development Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Payment response")
public class PaymentResponse {

    @Schema(description = "Payment ID", example = "5001")
    private Long id;

    @Schema(description = "Order ID", example = "3001")
    private Long orderId;

    @Schema(description = "Payment amount", example = "16.97")
    private BigDecimal amount;

    @Schema(description = "Payment status", example = "SUCCESS")
    private String status;

    @Schema(description = "Payment method", example = "CREDIT_CARD")
    private String paymentMethod;

    @Schema(description = "Transaction ID from payment gateway", example = "TXN-20250118-001")
    private String transactionId;

    @Schema(description = "Payment creation timestamp")
    private LocalDateTime createdAt;
}
