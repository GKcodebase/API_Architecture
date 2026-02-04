# AquaWorld gRPC API - Usage Guide

This guide provides detailed examples for using the AquaWorld gRPC API with grpcurl and code samples.

---

## Table of Contents
1. [Authentication Flow](#authentication-flow)
2. [Authentication Service](#authentication-service)
3. [Product Service](#product-service)
4. [Order Service](#order-service)
5. [Payment Service](#payment-service)
6. [Advanced Features](#advanced-features)

---

## Authentication Flow

### Step 1: Register New User

```bash
grpcurl -plaintext \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "SecurePassword123!",
    "full_name": "John Doe"
  }' \
  localhost:9090 com.aquaworld.grpc.auth.AuthService/Register
```

**Response:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "user": {
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "username": "john_doe",
    "email": "john@example.com",
    "fullName": "John Doe",
    "createdAt": "1707040000"
  }
}
```

### Step 2: Login to Get Token

```bash
grpcurl -plaintext \
  -d '{
    "username": "john_doe",
    "password": "SecurePassword123!"
  }' \
  localhost:9090 com.aquaworld.grpc.auth.AuthService/Login
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "refresh_token": "eyJhbGciOiJIUzUxMiJ9...",
  "expires_in": "86400000",
  "user": {
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "username": "john_doe",
    "email": "john@example.com",
    "fullName": "John Doe",
    "createdAt": "1707040000"
  }
}
```

**Save the token for authenticated requests:**
```bash
TOKEN="eyJhbGciOiJIUzUxMiJ9..."
```

### Step 3: Use Token for Authenticated Requests

All subsequent requests must include the token in the authorization header:

```bash
grpcurl -plaintext \
  -H "authorization: Bearer $TOKEN" \
  -d '{}' \
  localhost:9090 com.aquaworld.grpc.product.ProductService/ListProducts
```

---

## Authentication Service

### 1. Register User

Register a new user account:

```bash
grpcurl -plaintext \
  -d '{
    "username": "alice_smith",
    "email": "alice@aquaworld.com",
    "password": "AlicePassword456!",
    "full_name": "Alice Smith"
  }' \
  localhost:9090 com.aquaworld.grpc.auth.AuthService/Register
```

**Error Handling:**

User already exists:
```bash
grpcurl -plaintext \
  -d '{
    "username": "john_doe",
    "email": "newemail@example.com",
    "password": "newpass123",
    "full_name": "Another User"
  }' \
  localhost:9090 com.aquaworld.grpc.auth.AuthService/Register
```

Response: `Code: ALREADY_EXISTS, Message: User with identifier 'john_doe' already exists`

### 2. Login

Authenticate user and receive JWT token:

```bash
grpcurl -plaintext \
  -d '{
    "username": "john_doe",
    "password": "SecurePassword123!"
  }' \
  localhost:9090 com.aquaworld.grpc.auth.AuthService/Login
```

**Invalid Credentials:**
```bash
grpcurl -plaintext \
  -d '{
    "username": "john_doe",
    "password": "WrongPassword"
  }' \
  localhost:9090 com.aquaworld.grpc.auth.AuthService/Login
```

Response: `Code: UNAUTHENTICATED, Message: Invalid username or password`

### 3. Validate Token

Check if a token is still valid:

```bash
grpcurl -plaintext \
  -d "{\"token\": \"$TOKEN\"}" \
  localhost:9090 com.aquaworld.grpc.auth.AuthService/ValidateToken
```

**Response:**
```json
{
  "valid": true,
  "user": {
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "username": "john_doe",
    "email": "john@example.com",
    "fullName": "John Doe"
  }
}
```

### 4. Refresh Token

Get a new token:

```bash
grpcurl -plaintext \
  -d "{\"refresh_token\": \"$TOKEN\"}" \
  localhost:9090 com.aquaworld.grpc.auth.AuthService/RefreshToken
```

---

## Product Service

### 1. Create Product

Create a new product in catalog:

```bash
grpcurl -plaintext \
  -H "authorization: Bearer $TOKEN" \
  -d '{
    "name": "Red Dragon Guppy",
    "description": "Beautiful red male guppy with long flowing tail",
    "price": 45.99,
    "stock_quantity": 25,
    "category": "FISH",
    "tags": ["red", "male", "dragon-tail", "premium"],
    "image_url": "https://images.example.com/red-dragon-guppy.jpg"
  }' \
  localhost:9090 com.aquaworld.grpc.product.ProductService/CreateProduct
```

**Response:**
```json
{
  "productId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "name": "Red Dragon Guppy",
  "description": "Beautiful red male guppy with long flowing tail",
  "price": 45.99,
  "stockQuantity": 25,
  "category": "FISH",
  "tags": ["red", "male", "dragon-tail", "premium"],
  "imageUrl": "https://images.example.com/red-dragon-guppy.jpg",
  "createdAt": "1707040000"
}
```

### 2. List All Products

Get all products with pagination:

```bash
grpcurl -plaintext \
  -H "authorization: Bearer $TOKEN" \
  -d '{
    "page": 1,
    "page_size": 10
  }' \
  localhost:9090 com.aquaworld.grpc.product.ProductService/ListProducts
```

**With Category Filter:**
```bash
grpcurl -plaintext \
  -H "authorization: Bearer $TOKEN" \
  -d '{
    "page": 1,
    "page_size": 10,
    "category": "FISH"
  }' \
  localhost:9090 com.aquaworld.grpc.product.ProductService/ListProducts
```

**With Search Query:**
```bash
grpcurl -plaintext \
  -H "authorization: Bearer $TOKEN" \
  -d '{
    "page": 1,
    "page_size": 10,
    "search_query": "red"
  }' \
  localhost:9090 com.aquaworld.grpc.product.ProductService/ListProducts
```

**Response:**
```json
{
  "products": [
    {
      "productId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
      "name": "Red Dragon Guppy",
      "price": 45.99,
      "stockQuantity": 25,
      "category": "FISH"
    }
  ],
  "pageInfo": {
    "page": 1,
    "pageSize": 10,
    "totalItems": 1,
    "totalPages": 1
  }
}
```

### 3. Get Single Product

Retrieve details of a specific product:

```bash
grpcurl -plaintext \
  -H "authorization: Bearer $TOKEN" \
  -d '{
    "product_id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
  }' \
  localhost:9090 com.aquaworld.grpc.product.ProductService/GetProduct
```

### 4. Update Product

Modify existing product:

```bash
grpcurl -plaintext \
  -H "authorization: Bearer $TOKEN" \
  -d '{
    "product_id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "name": "Premium Red Dragon Guppy",
    "price": 49.99,
    "description": "Premium quality red dragon tail guppy"
  }' \
  localhost:9090 com.aquaworld.grpc.product.ProductService/UpdateProduct
```

### 5. Update Stock

Adjust product inventory:

```bash
# Increase stock by 10
grpcurl -plaintext \
  -H "authorization: Bearer $TOKEN" \
  -d '{
    "product_id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "quantity_change": 10
  }' \
  localhost:9090 com.aquaworld.grpc.product.ProductService/UpdateStock

# Decrease stock by 5
grpcurl -plaintext \
  -H "authorization: Bearer $TOKEN" \
  -d '{
    "product_id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "quantity_change": -5
  }' \
  localhost:9090 com.aquaworld.grpc.product.ProductService/UpdateStock
```

### 6. Stream All Products

Get all products as a stream (useful for bulk operations):

```bash
grpcurl -plaintext \
  -H "authorization: Bearer $TOKEN" \
  -d '{}' \
  localhost:9090 com.aquaworld.grpc.product.ProductService/StreamProducts
```

Output: Products will stream one by one as they're retrieved.

### 7. Delete Product

Remove a product from catalog:

```bash
grpcurl -plaintext \
  -H "authorization: Bearer $TOKEN" \
  -d '{
    "product_id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
  }' \
  localhost:9090 com.aquaworld.grpc.product.ProductService/DeleteProduct
```

---

## Order Service

### 1. Create Order

Place a new order:

```bash
grpcurl -plaintext \
  -H "authorization: Bearer $TOKEN" \
  -d '{
    "items": [
      {
        "item_id": "item-1",
        "product_id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
        "product_name": "Red Dragon Guppy",
        "quantity": 2,
        "unit_price": 45.99,
        "subtotal": 91.98
      },
      {
        "item_id": "item-2",
        "product_id": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
        "product_name": "Aquarium Filter",
        "quantity": 1,
        "unit_price": 29.99,
        "subtotal": 29.99
      }
    ],
    "shipping_address": "123 Aqua Lane, Fish City, FC 12345"
  }' \
  localhost:9090 com.aquaworld.grpc.order.OrderService/CreateOrder
```

**Response:**
```json
{
  "success": true,
  "order": {
    "orderId": "order-123abc",
    "userId": "user-456def",
    "items": [...],
    "totalAmount": 121.97,
    "status": "PENDING",
    "shippingAddress": "123 Aqua Lane, Fish City, FC 12345",
    "createdAt": "1707040000",
    "updatedAt": "1707040000"
  },
  "message": "Order created successfully"
}
```

### 2. Get Order

Retrieve order details:

```bash
grpcurl -plaintext \
  -H "authorization: Bearer $TOKEN" \
  -d '{
    "order_id": "order-123abc"
  }' \
  localhost:9090 com.aquaworld.grpc.order.OrderService/GetOrder
```

### 3. List User Orders

Get all orders for a user:

```bash
grpcurl -plaintext \
  -H "authorization: Bearer $TOKEN" \
  -d '{
    "user_id": "user-456def",
    "page": 1,
    "page_size": 10
  }' \
  localhost:9090 com.aquaworld.grpc.order.OrderService/ListOrders
```

**Filter by Status:**
```bash
grpcurl -plaintext \
  -H "authorization: Bearer $TOKEN" \
  -d '{
    "user_id": "user-456def",
    "page": 1,
    "page_size": 10,
    "status": "SHIPPED"
  }' \
  localhost:9090 com.aquaworld.grpc.order.OrderService/ListOrders
```

### 4. Update Order Status

Change order status:

```bash
grpcurl -plaintext \
  -H "authorization: Bearer $TOKEN" \
  -d '{
    "order_id": "order-123abc",
    "status": "PROCESSING"
  }' \
  localhost:9090 com.aquaworld.grpc.order.OrderService/UpdateOrderStatus
```

**Valid Status Values:**
- `PENDING` - Order awaiting processing
- `PROCESSING` - Order being prepared
- `SHIPPED` - Order shipped to customer
- `DELIVERED` - Order delivered
- `CANCELLED` - Order cancelled

### 5. Cancel Order

Cancel an order:

```bash
grpcurl -plaintext \
  -H "authorization: Bearer $TOKEN" \
  -d '{
    "order_id": "order-123abc"
  }' \
  localhost:9090 com.aquaworld.grpc.order.OrderService/CancelOrder
```

---

## Payment Service

### 1. Process Payment

Process a payment for an order:

```bash
grpcurl -plaintext \
  -H "authorization: Bearer $TOKEN" \
  -d '{
    "order_id": "order-123abc",
    "amount": 121.97,
    "payment_method": "CREDIT_CARD",
    "details": {
      "card_number": "4532015112830366",
      "card_holder": "John Doe",
      "expiry_date": "12/25",
      "cvv": "123"
    }
  }' \
  localhost:9090 com.aquaworld.grpc.payment.PaymentService/ProcessPayment
```

**Response:**
```json
{
  "success": true,
  "payment": {
    "paymentId": "pay-789xyz",
    "orderId": "order-123abc",
    "amount": 121.97,
    "paymentMethod": "CREDIT_CARD",
    "status": "SUCCESS",
    "transactionId": "txn-20240204-001",
    "createdAt": "1707040000"
  },
  "message": "Payment processed successfully"
}
```

### 2. Get Payment

Retrieve payment details:

```bash
grpcurl -plaintext \
  -H "authorization: Bearer $TOKEN" \
  -d '{
    "payment_id": "pay-789xyz"
  }' \
  localhost:9090 com.aquaworld.grpc.payment.PaymentService/GetPayment
```

### 3. Get Payment Status

Check payment status for an order:

```bash
grpcurl -plaintext \
  -H "authorization: Bearer $TOKEN" \
  -d '{
    "order_id": "order-123abc"
  }' \
  localhost:9090 com.aquaworld.grpc.payment.PaymentService/GetPaymentStatus
```

### 4. Refund Payment

Issue a refund:

```bash
grpcurl -plaintext \
  -H "authorization: Bearer $TOKEN" \
  -d '{
    "payment_id": "pay-789xyz",
    "amount": 121.97,
    "reason": "Customer requested return"
  }' \
  localhost:9090 com.aquaworld.grpc.payment.PaymentService/RefundPayment
```

---

## Advanced Features

### Pagination

All list endpoints support pagination:

```bash
-d '{
  "page": 2,
  "page_size": 20
}'
```

### Search and Filter

Product service supports search and filtering:

```bash
# Category filter
-d '{"category": "FISH"}'

# Search query
-d '{"search_query": "dragon"}'

# Both
-d '{
  "category": "FISH",
  "search_query": "red"
}'
```

### Server-Side Streaming

Get products as a continuous stream:

```bash
grpcurl -plaintext \
  -H "authorization: Bearer $TOKEN" \
  -d '{}' \
  localhost:9090 com.aquaworld.grpc.product.ProductService/StreamProducts
```

### Error Handling

All services return appropriate gRPC status codes:

```bash
# Missing authentication
Code: UNAUTHENTICATED

# Resource not found
Code: NOT_FOUND

# Invalid input
Code: INVALID_ARGUMENT

# Already exists
Code: ALREADY_EXISTS

# Server error
Code: INTERNAL
```

---

## Quick Reference

| Operation | Service | RPC Method |
|-----------|---------|-----------|
| Register | AuthService | Register |
| Login | AuthService | Login |
| Validate Token | AuthService | ValidateToken |
| Refresh Token | AuthService | RefreshToken |
| List Products | ProductService | ListProducts |
| Get Product | ProductService | GetProduct |
| Create Product | ProductService | CreateProduct |
| Update Product | ProductService | UpdateProduct |
| Update Stock | ProductService | UpdateStock |
| Delete Product | ProductService | DeleteProduct |
| Stream Products | ProductService | StreamProducts |
| Create Order | OrderService | CreateOrder |
| Get Order | OrderService | GetOrder |
| List Orders | OrderService | ListOrders |
| Update Order Status | OrderService | UpdateOrderStatus |
| Cancel Order | OrderService | CancelOrder |
| Process Payment | PaymentService | ProcessPayment |
| Get Payment | PaymentService | GetPayment |
| Get Payment Status | PaymentService | GetPaymentStatus |
| Refund Payment | PaymentService | RefundPayment |

---

Happy testing! 🐠
