package com.aquaworld.grpc.util;

/**
 * Constants used across AquaWorld gRPC API
 */
public class Constants {
    private Constants() {
    }

    // JWT Constants
    public static final String JWT_SECRET = "your-secret-key-change-in-production";
    public static final long JWT_EXPIRATION = 86400000; // 24 hours

    // Order Status
    public static final String ORDER_STATUS_PENDING = "PENDING";
    public static final String ORDER_STATUS_PROCESSING = "PROCESSING";
    public static final String ORDER_STATUS_SHIPPED = "SHIPPED";
    public static final String ORDER_STATUS_DELIVERED = "DELIVERED";
    public static final String ORDER_STATUS_CANCELLED = "CANCELLED";

    // Payment Status
    public static final String PAYMENT_STATUS_PENDING = "PENDING";
    public static final String PAYMENT_STATUS_SUCCESS = "SUCCESS";
    public static final String PAYMENT_STATUS_FAILED = "FAILED";
    public static final String PAYMENT_STATUS_REFUNDED = "REFUNDED";

    // Pagination
    public static final int DEFAULT_PAGE = 1;
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;

    // Categories
    public static final String CATEGORY_FISH = "FISH";
    public static final String CATEGORY_ACCESSORIES = "ACCESSORIES";
    public static final String CATEGORY_FOOD = "FOOD";
    public static final String CATEGORY_PLANTS = "PLANTS";
}
