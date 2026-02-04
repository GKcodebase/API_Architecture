package com.aquaworld.grpc.service.impl;

import com.aquaworld.grpc.payment.PaymentServiceGrpc;
import com.aquaworld.grpc.payment.Payment;
import com.aquaworld.grpc.payment.ProcessPaymentRequest;
import com.aquaworld.grpc.payment.ProcessPaymentResponse;
import com.aquaworld.grpc.payment.GetPaymentRequest;
import com.aquaworld.grpc.payment.GetPaymentStatusRequest;
import com.aquaworld.grpc.payment.PaymentStatusRequest;
import com.aquaworld.grpc.payment.RefundPaymentRequest;
import com.aquaworld.grpc.exception.GrpcExceptionHandler;
import com.aquaworld.grpc.service.PaymentService;
import com.aquaworld.grpc.ApiResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.devh.boot.grpc.server.service.GrpcService;

/**
 * gRPC Payment Service Implementation
 * Implements PaymentService defined in payment.proto
 */
@GrpcService
@RequiredArgsConstructor
public class GrpcPaymentServiceImpl extends PaymentServiceGrpc.PaymentServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(GrpcPaymentServiceImpl.class);

    private final PaymentService paymentService;

    @Override
    public void processPayment(ProcessPaymentRequest request, StreamObserver<ProcessPaymentResponse> responseObserver) {
        try {
            log.info("Process payment request - orderId: {}, amount: {}", request.getOrderId(), request.getAmount());

            var payment = paymentService.processPayment(
                    request.getOrderId(),
                    request.getAmount(),
                    request.getPaymentMethod()
            );

            var response = ProcessPaymentResponse.newBuilder()
                    .setSuccess(true)
                    .setPayment(mapPaymentToProto(payment))
                    .setMessage("Payment processed successfully")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Process payment failed: {}", e.getMessage());
            responseObserver.onError(GrpcExceptionHandler.handle(e));
        }
    }

    @Override
    public void getPayment(GetPaymentRequest request, StreamObserver<Payment> responseObserver) {
        try {
            log.info("Get payment request: {}", request.getPaymentId());

            var payment = paymentService.getPayment(request.getPaymentId());
            var response = mapPaymentToProto(payment);

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Get payment failed: {}", e.getMessage());
            responseObserver.onError(GrpcExceptionHandler.handle(e));
        }
    }

    @Override
    public void getPaymentStatus(PaymentStatusRequest request, StreamObserver<Payment> responseObserver) {
        try {
            log.info("Get payment status request - orderId: {}", request.getOrderId());

            var payment = paymentService.getPaymentByOrderId(request.getOrderId());
            var response = mapPaymentToProto(payment);

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Get payment status failed: {}", e.getMessage());
            responseObserver.onError(GrpcExceptionHandler.handle(e));
        }
    }

    @Override
    public void refundPayment(RefundPaymentRequest request, StreamObserver<ApiResponse> responseObserver) {
        try {
            log.info("Refund payment request: {} - reason: {}", request.getPaymentId(), request.getReason());

            paymentService.refundPayment(request.getPaymentId(), request.getReason());

            var response = ApiResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Payment refunded successfully")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Refund payment failed: {}", e.getMessage());
            responseObserver.onError(GrpcExceptionHandler.handle(e));
        }
    }

    private Payment mapPaymentToProto(com.aquaworld.grpc.model.Payment payment) {
        return Payment.newBuilder()
                .setPaymentId(payment.getPaymentId())
                .setOrderId(payment.getOrderId())
                .setAmount(payment.getAmount())
                .setPaymentMethod(payment.getPaymentMethod())
                .setStatus(payment.getStatus())
                .setTransactionId(payment.getTransactionId())
                .setCreatedAt(payment.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant().getEpochSecond())
                .build();
    }
}
