package com.aquaworld.grpc.service.impl;

import com.aquaworld.grpc.order.OrderServiceGrpc;
import com.aquaworld.grpc.order.Order;
import com.aquaworld.grpc.order.OrderItem;
import com.aquaworld.grpc.order.CreateOrderRequest;
import com.aquaworld.grpc.order.CreateOrderResponse;
import com.aquaworld.grpc.order.GetOrderRequest;
import com.aquaworld.grpc.order.ListOrdersRequest;
import com.aquaworld.grpc.order.ListOrdersResponse;
import com.aquaworld.grpc.order.UpdateOrderStatusRequest;
import com.aquaworld.grpc.order.CancelOrderRequest;
import com.aquaworld.grpc.exception.GrpcExceptionHandler;
import com.aquaworld.grpc.service.OrderService;
import com.aquaworld.grpc.util.Constants;
import com.aquaworld.grpc.ApiResponse;
import com.aquaworld.grpc.PageInfo;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;

/**
 * gRPC Order Service Implementation
 * Implements OrderService defined in order.proto
 */
@Slf4j
@GrpcService
@RequiredArgsConstructor
public class GrpcOrderServiceImpl extends OrderServiceGrpc.OrderServiceImplBase {

    private final OrderService orderService;

    @Override
    public void createOrder(CreateOrderRequest request, StreamObserver<CreateOrderResponse> responseObserver) {
        try {
            log.info("Create order request");

            var orderItems = request.getItemsList().stream()
                    .map(item -> {
                        var orderItem = new com.aquaworld.grpc.model.OrderItem();
                        orderItem.setItemId(item.getItemId());
                        orderItem.setOrderId(null);
                        orderItem.setProductId(item.getProductId());
                        orderItem.setProductName(item.getProductName());
                        orderItem.setQuantity(item.getQuantity());
                        orderItem.setUnitPrice(item.getUnitPrice());
                        orderItem.setSubtotal(item.getSubtotal());
                        return orderItem;
                    })
                    .toList();

            double totalAmount = orderItems.stream().mapToDouble(com.aquaworld.grpc.model.OrderItem::getSubtotal).sum();

            var order = orderService.createOrder(
                    "user-id-placeholder",
                    orderItems,
                    totalAmount,
                    request.getShippingAddress()
            );

            var response = CreateOrderResponse.newBuilder()
                    .setSuccess(true)
                    .setOrder(mapOrderToProto(order))
                    .setMessage("Order created successfully")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Create order failed: {}", e.getMessage());
            responseObserver.onError(GrpcExceptionHandler.handle(e));
        }
    }

    @Override
    public void getOrder(GetOrderRequest request, StreamObserver<Order> responseObserver) {
        try {
            log.info("Get order request: {}", request.getOrderId());

            var order = orderService.getOrder(request.getOrderId());
            var response = mapOrderToProto(order);

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Get order failed: {}", e.getMessage());
            responseObserver.onError(GrpcExceptionHandler.handle(e));
        }
    }

    @Override
    public void listOrders(ListOrdersRequest request, StreamObserver<ListOrdersResponse> responseObserver) {
        try {
            log.info("List orders request - userId: {}", request.getUserId());

            List<com.aquaworld.grpc.model.Order> orders;

            if (!request.getStatus().isEmpty()) {
                orders = orderService.getUserOrdersByStatus(request.getUserId(), request.getStatus());
            } else {
                orders = orderService.getUserOrders(request.getUserId());
            }

            int page = request.getPage() > 0 ? request.getPage() : Constants.DEFAULT_PAGE;
            int pageSize = request.getPageSize() > 0 ? request.getPageSize() : Constants.DEFAULT_PAGE_SIZE;
            pageSize = Math.min(pageSize, Constants.MAX_PAGE_SIZE);

            int startIndex = (page - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, orders.size());

            List<com.aquaworld.grpc.model.Order> paginatedOrders = orders.subList(startIndex, endIndex);

            var pageInfo = PageInfo.newBuilder()
                    .setPage(page)
                    .setPageSize(pageSize)
                    .setTotalItems(orders.size())
                    .setTotalPages((orders.size() + pageSize - 1) / pageSize)
                    .build();

            var response = ListOrdersResponse.newBuilder()
                    .addAllOrders(paginatedOrders.stream().map(this::mapOrderToProto).toList())
                    .setPageInfo(pageInfo)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("List orders failed: {}", e.getMessage());
            responseObserver.onError(GrpcExceptionHandler.handle(e));
        }
    }

    @Override
    public void cancelOrder(CancelOrderRequest request, StreamObserver<ApiResponse> responseObserver) {
        try {
            log.info("Cancel order request: {}", request.getOrderId());

            orderService.cancelOrder(request.getOrderId());

            var response = ApiResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Order cancelled successfully")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Cancel order failed: {}", e.getMessage());
            responseObserver.onError(GrpcExceptionHandler.handle(e));
        }
    }

    @Override
    public void updateOrderStatus(UpdateOrderStatusRequest request, StreamObserver<Order> responseObserver) {
        try {
            log.info("Update order status request: {} - status: {}", request.getOrderId(), request.getStatus());

            var order = orderService.updateOrderStatus(request.getOrderId(), request.getStatus());
            var response = mapOrderToProto(order);

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Update order status failed: {}", e.getMessage());
            responseObserver.onError(GrpcExceptionHandler.handle(e));
        }
    }

    private Order mapOrderToProto(com.aquaworld.grpc.model.Order order) {
        var items = orderService.getOrderItems(order.getOrderId());
        var protoItems = items.stream()
                .map(item -> OrderItem.newBuilder()
                        .setItemId(item.getItemId())
                        .setProductId(item.getProductId())
                        .setProductName(item.getProductName())
                        .setQuantity(item.getQuantity())
                        .setUnitPrice(item.getUnitPrice())
                        .setSubtotal(item.getSubtotal())
                        .build())
                .toList();

        return Order.newBuilder()
                .setOrderId(order.getOrderId())
                .setUserId(order.getUserId())
                .addAllItems(protoItems)
                .setTotalAmount(order.getTotalAmount())
                .setStatus(order.getStatus())
                .setShippingAddress(order.getShippingAddress())
                .setCreatedAt(order.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant().getEpochSecond())
                .setUpdatedAt(order.getUpdatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant().getEpochSecond())
                .build();
    }
}
