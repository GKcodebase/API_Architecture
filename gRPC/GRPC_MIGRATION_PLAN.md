# AquaWorld REST API to gRPC Conversion Plan

## Executive Summary

This document outlines a comprehensive strategy for creating a new AquaWorld gRPC-based microservice based on the existing REST API. The new gRPC service will maintain feature parity with the REST API while leveraging gRPC's performance benefits, efficient serialization, and streaming capabilities.

---

## Current REST API Architecture

### REST API Stack
- **Framework**: Spring Boot 3.3.4
- **Language**: Java 21
- **Database**: In-memory (ConcurrentHashMap)
- **Authentication**: JWT Token-based
- **Security**: Spring Security
- **Dependencies**: Lombok, OpenAPI/Swagger, Spring Web

### REST API Services & Endpoints

| Controller | HTTP Endpoints | DTOs |
|------------|----------------|------|
| **AuthController** | POST /auth/login, POST /auth/register | LoginRequest, LoginResponse, RegisterRequest |
| **ProductController** | GET /products, GET /products/{id}, POST /products | ProductResponse |
| **OrderController** | POST /orders, GET /orders/{id}, GET /orders, DELETE /orders/{id} | CreateOrderRequest, OrderResponse, OrderItemRequest, OrderItemResponse |
| **PaymentController** | POST /payments, GET /payments/{id}, GET /payments/status/{orderId} | PaymentRequest, PaymentResponse |

### Core Models
- User (username, email, password, fullName)
- Product (productId, name, description, price, stockQuantity, category, tags, imageUrl)
- Order (orderId, userId, items, totalAmount, status, shippingAddress)
- OrderItem (itemId, productId, quantity, unitPrice)
- Payment (paymentId, orderId, amount, paymentMethod, status, transactionId)

---

## gRPC Project Creation Strategy

### Phase 1: Foundation Setup

#### 1.1 Create gRPC Project Structure

**Changes from REST:**
- Replace REST package `com.aquaworld` with `com.aquaworld.grpc`
- Add `proto/` directory for Protocol Buffer definitions (NEW)
- Reorganize service layer to support gRPC implementations (NEW)

```
aquaworld-grpc-api/                          (NEW PROJECT)
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/aquaworld/grpc/
│   │   │   ├── AquaWorldGrpcApplication.java       (NEW - gRPC entry point)
│   │   │   ├── config/
│   │   │   │   ├── GrpcServerConfig.java           (NEW)
│   │   │   │   ├── JwtAuthenticationInterceptor.java (NEW - gRPC specific)
│   │   │   │   └── GrpcSecurityConfig.java         (NEW)
│   │   │   ├── service/
│   │   │   │   ├── AuthService.java                (SAME as REST)
│   │   │   │   ├── ProductService.java             (SAME as REST)
│   │   │   │   ├── OrderService.java               (SAME as REST)
│   │   │   │   ├── PaymentService.java             (SAME as REST)
│   │   │   │   ├── impl/
│   │   │   │   │   ├── GrpcAuthServiceImpl.java     (NEW)
│   │   │   │   │   ├── GrpcProductServiceImpl.java  (NEW)
│   │   │   │   │   ├── GrpcOrderServiceImpl.java    (NEW)
│   │   │   │   │   └── GrpcPaymentServiceImpl.java  (NEW)
│   │   │   ├── repository/                          (SAME as REST)
│   │   │   ├── model/                               (SAME as REST - reuse entities)
│   │   │   ├── exception/                           (EXTENDED for gRPC)
│   │   │   │   ├── GrpcExceptionHandler.java       (NEW)
│   │   │   │   └── (other exceptions - REUSE)
│   │   │   └── util/
│   │   │       ├── JwtUtil.java                     (SAME as REST)
│   │   │       └── Constants.java                   (SAME as REST)
│   │   ├── proto/                                    (NEW - Protocol Buffers)
│   │   │   ├── common.proto
│   │   │   ├── auth.proto
│   │   │   ├── product.proto
│   │   │   ├── order.proto
│   │   │   └── payment.proto
│   │   └── resources/
│   │       ├── application.properties               (MODIFIED - gRPC config)
│   │       └── logback-spring.xml                   (OPTIONAL - NEW)
│   └── test/
│       └── java/com/aquaworld/grpc/
│           ├── service/
│           │   └── (integration tests - NEW)
└── target/
```

#### 1.2 Maven POM.xml Changes

**Dependencies to ADD (New for gRPC):**
```xml
<!-- gRPC Dependencies -->
<dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-netty-shaded</artifactId>
    <version>1.60.0</version>
</dependency>
<dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-protobuf</artifactId>
    <version>1.60.0</version>
</dependency>
<dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-stub</artifactId>
    <version>1.60.0</version>
</dependency>
<dependency>
    <groupId>com.google.protobuf</groupId>
    <artifactId>protobuf-java</artifactId>
    <version>3.24.4</version>
</dependency>
<dependency>
    <groupId>net.devh</groupId>
    <artifactId>grpc-server-spring-boot-starter</artifactId>
    <version>2.15.0.RELEASE</version>
</dependency>

<!-- gRPC Testing -->
<dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-testing</artifactId>
    <version>1.60.0</version>
    <scope>test</scope>
</dependency>
```

**Dependencies to KEEP (Reuse from REST):**
```xml
<!-- Spring Data JPA -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- Spring Security (for JWT interceptor) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>

<!-- Lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.42</version>
</dependency>

<!-- Database -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

**Dependencies to REMOVE (REST-specific):**
- `spring-boot-starter-web` - No longer needed (gRPC instead)
- `springdoc-openapi-starter-webmvc-ui` - Swagger/OpenAPI (use gRPC documentation tools)

**Maven Plugins to ADD:**
```xml
<!-- Protocol Buffer Compiler Plugin -->
<plugin>
    <groupId>org.xolstice.maven.plugins</groupId>
    <artifactId>protobuf-maven-plugin</artifactId>
    <version>0.6.1</version>
    <configuration>
        <protocArtifact>com.google.protobuf:protoc:3.24.4:exe:${os.detected.classifier}</protocArtifact>
        <pluginId>grpc-java</pluginId>
        <pluginArtifact>io.grpc:protoc-gen-grpc-java:1.60.0:exe:${os.detected.classifier}</pluginArtifact>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>compile</goal>
                <goal>compile-custom</goal>
            </goals>
        </execution>
    </executions>
</plugin>

<!-- OS Maven Plugin (for protobuf) -->
<plugin>
    <groupId>kr.motd.maven</groupId>
    <artifactId>os-maven-plugin</artifactId>
    <version>1.7.1</version>
    <executions>
        <execution>
            <phase>initialize</phase>
            <goals>
                <goal>detect</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

#### 1.3 Application Properties Changes

**REST API (application.properties):**
```properties
server.port=8080
spring.application.name=aquaworld-api
jwt.secret=your-secret-key
jwt.expiration=86400000
```

**gRPC API (application.properties) - MODIFIED:**
```properties
# gRPC Server Configuration (NEW)
grpc.server.port=9090
grpc.server.enable-keep-alive=true
grpc.server.keep-alive-time=30s
grpc.server.keep-alive-timeout=5s
grpc.server.permit-keep-alive-without-calls=true
grpc.server.max-concurrent-streams=100

# Application Configuration (SAME)
spring.application.name=aquaworld-grpc-api
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true

# JWT Configuration (REUSED from REST)
jwt.secret=your-secret-key
jwt.expiration=86400000

# Logging
logging.level.com.aquaworld=DEBUG
logging.level.io.grpc=INFO
```

---

### Phase 2: Protocol Buffer Definitions

#### 2.1 Common Messages (common.proto)

**CHANGES**: Protocol Buffers are a gRPC concept - NO equivalent in REST API

```protobuf
syntax = "proto3";

package com.aquaworld.grpc;

option java_multiple_files = true;
option java_package = "com.aquaworld.grpc";
option java_outer_classname = "CommonProto";

message Empty {}

message ApiResponse {
  bool success = 1;
  string message = 2;
  string error_code = 3;
}

message PageInfo {
  int32 page = 1;
  int32 page_size = 2;
  int32 total_items = 3;
  int32 total_pages = 4;
}
```

#### 2.2 Authentication Service (auth.proto)

**CHANGES from REST:**
- REST: `POST /auth/login` with JSON body → gRPC: `Login(LoginRequest)` RPC call
- REST: `POST /auth/register` with JSON body → gRPC: `Register(RegisterRequest)` RPC call
- NEW: `ValidateToken(TokenRequest)` - useful for service-to-service communication
- NEW: `RefreshToken(RefreshTokenRequest)` - explicit refresh endpoint

```protobuf
syntax = "proto3";

package com.aquaworld.grpc;

option java_multiple_files = true;
option java_package = "com.aquaworld.grpc.auth";

import "common.proto";

message LoginRequest {
  string username = 1;
  string password = 2;
}

message LoginResponse {
  string token = 1;
  string refresh_token = 2;
  int64 expires_in = 3;
  User user = 4;
}

message RegisterRequest {
  string username = 1;
  string email = 2;
  string password = 3;
  string full_name = 4;
}

message RegisterResponse {
  bool success = 1;
  string message = 2;
  User user = 3;
}

message User {
  string user_id = 1;
  string username = 2;
  string email = 3;
  string full_name = 4;
  int64 created_at = 5;
}

message TokenRequest {
  string token = 1;
}

message TokenResponse {
  bool valid = 1;
  User user = 2;
}

message RefreshTokenRequest {
  string refresh_token = 1;
}

service AuthService {
  rpc Login(LoginRequest) returns (LoginResponse);
  rpc Register(RegisterRequest) returns (RegisterResponse);
  rpc ValidateToken(TokenRequest) returns (TokenResponse);
  rpc RefreshToken(RefreshTokenRequest) returns (LoginResponse);
}
```

#### 2.3 Product Service (product.proto)

**CHANGES from REST:**
- REST: `GET /products` (query params) → gRPC: `ListProducts(ProductListRequest)` with structured pagination
- REST: `GET /products/{id}` → gRPC: `GetProduct(ProductRequest)` (same concept)
- REST: `POST /products` → gRPC: `CreateProduct(CreateProductRequest)` (same concept)
- NEW: `StreamProducts()` - Server-side streaming for bulk operations (gRPC advantage)
- NEW: `UpdateStock(UpdateStockRequest)` - Separate endpoint for inventory updates

```protobuf
syntax = "proto3";

package com.aquaworld.grpc;

option java_multiple_files = true;
option java_package = "com.aquaworld.grpc.product";

import "common.proto";

message Product {
  string product_id = 1;
  string name = 2;
  string description = 3;
  double price = 4;
  int32 stock_quantity = 5;
  string category = 6;
  repeated string tags = 7;
  string image_url = 8;
  int64 created_at = 9;
}

message ProductRequest {
  string product_id = 1;
}

message ProductListRequest {
  int32 page = 1;
  int32 page_size = 2;
  string category = 3;
  string search_query = 4;
}

message ProductListResponse {
  repeated Product products = 1;
  PageInfo page_info = 2;
}

message CreateProductRequest {
  string name = 1;
  string description = 2;
  double price = 3;
  int32 stock_quantity = 4;
  string category = 5;
  repeated string tags = 6;
  string image_url = 7;
}

message UpdateProductRequest {
  string product_id = 1;
  string name = 2;
  string description = 3;
  double price = 4;
  int32 stock_quantity = 5;
  string category = 6;
  repeated string tags = 7;
  string image_url = 8;
}

message UpdateStockRequest {
  string product_id = 1;
  int32 quantity_change = 2;
}

service ProductService {
  rpc GetProduct(ProductRequest) returns (Product);
  rpc ListProducts(ProductListRequest) returns (ProductListResponse);
  rpc CreateProduct(CreateProductRequest) returns (Product);
  rpc UpdateProduct(UpdateProductRequest) returns (Product);
  rpc UpdateStock(UpdateStockRequest) returns (Product);
  rpc DeleteProduct(ProductRequest) returns (ApiResponse);
  rpc StreamProducts(Empty) returns (stream Product);  // NEW - Server-side streaming
}
```

#### 2.4 Order Service (order.proto)

**CHANGES from REST:**
- REST: `POST /orders` → gRPC: `CreateOrder(CreateOrderRequest)` (same)
- REST: `GET /orders/{id}` → gRPC: `GetOrder(GetOrderRequest)` (same)
- REST: `GET /orders` → gRPC: `ListOrders(ListOrdersRequest)` with pagination (same)
- REST: `DELETE /orders/{id}` → gRPC: `CancelOrder(CancelOrderRequest)` (same)
- NEW: `UpdateOrderStatus(UpdateOrderStatusRequest)` - Explicit status update (better semantics)

```protobuf
syntax = "proto3";

package com.aquaworld.grpc;

option java_multiple_files = true;
option java_package = "com.aquaworld.grpc.order";

import "common.proto";

message OrderItem {
  string item_id = 1;
  string product_id = 2;
  string product_name = 3;
  int32 quantity = 4;
  double unit_price = 5;
  double subtotal = 6;
}

message Order {
  string order_id = 1;
  string user_id = 2;
  repeated OrderItem items = 3;
  double total_amount = 4;
  string status = 5;
  string shipping_address = 6;
  int64 created_at = 7;
  int64 updated_at = 8;
}

message CreateOrderRequest {
  repeated OrderItem items = 1;
  string shipping_address = 2;
}

message CreateOrderResponse {
  bool success = 1;
  Order order = 2;
  string message = 3;
}

message GetOrderRequest {
  string order_id = 1;
}

message ListOrdersRequest {
  string user_id = 1;
  int32 page = 2;
  int32 page_size = 3;
  string status = 4;
}

message ListOrdersResponse {
  repeated Order orders = 1;
  PageInfo page_info = 2;
}

message CancelOrderRequest {
  string order_id = 1;
}

message UpdateOrderStatusRequest {
  string order_id = 1;
  string status = 2;
}

service OrderService {
  rpc CreateOrder(CreateOrderRequest) returns (CreateOrderResponse);
  rpc GetOrder(GetOrderRequest) returns (Order);
  rpc ListOrders(ListOrdersRequest) returns (ListOrdersResponse);
  rpc CancelOrder(CancelOrderRequest) returns (ApiResponse);
  rpc UpdateOrderStatus(UpdateOrderStatusRequest) returns (Order);
}
```

#### 2.5 Payment Service (payment.proto)

**CHANGES from REST:**
- REST: `POST /payments` → gRPC: `ProcessPayment(ProcessPaymentRequest)` (same)
- REST: `GET /payments/{id}` → gRPC: `GetPayment(GetPaymentRequest)` (same)
- REST: `GET /payments/status/{orderId}` → gRPC: `GetPaymentStatus(PaymentStatusRequest)` (same)
- NEW: `RefundPayment(RefundPaymentRequest)` - Separate endpoint for refunds

```protobuf
syntax = "proto3";

package com.aquaworld.grpc;

option java_multiple_files = true;
option java_package = "com.aquaworld.grpc.payment";

import "common.proto";

message Payment {
  string payment_id = 1;
  string order_id = 2;
  double amount = 3;
  string payment_method = 4;
  string status = 5;
  string transaction_id = 6;
  int64 created_at = 7;
}

message ProcessPaymentRequest {
  string order_id = 1;
  double amount = 2;
  string payment_method = 3;
  PaymentDetails details = 4;
}

message PaymentDetails {
  string card_number = 1;
  string card_holder = 2;
  string expiry_date = 3;
  string cvv = 4;
  string bank_account = 5;
  string upi_id = 6;
}

message ProcessPaymentResponse {
  bool success = 1;
  Payment payment = 2;
  string message = 3;
}

message GetPaymentRequest {
  string payment_id = 1;
}

message PaymentStatusRequest {
  string order_id = 1;
}

message RefundPaymentRequest {
  string payment_id = 1;
  double amount = 2;
  string reason = 3;
}

service PaymentService {
  rpc ProcessPayment(ProcessPaymentRequest) returns (ProcessPaymentResponse);
  rpc GetPayment(GetPaymentRequest) returns (Payment);
  rpc GetPaymentStatus(PaymentStatusRequest) returns (Payment);
  rpc RefundPayment(RefundPaymentRequest) returns (ApiResponse);
}
```

---

### Phase 3: Reuse & Adapt Service Layer

#### 3.1 Reuse Existing Services

**From REST API - KEEP AS IS:**
- `AuthenticationService.java` - Login/register logic
- `ProductService.java` - Product CRUD logic
- `OrderService.java` - Order processing logic
- `PaymentService.java` - Payment processing logic
- `UserService.java` - User management logic

These services contain your business logic and can be reused without modification.

#### 3.2 Reuse Existing Models

**From REST API - KEEP AS IS:**
- `User.java` - JPA entity
- `Product.java` - JPA entity
- `Order.java` - JPA entity
- `OrderItem.java` - JPA entity
- `Payment.java` - JPA entity

These are database models and don't change between REST and gRPC.

#### 3.3 Reuse Existing Repositories

**From REST API - KEEP AS IS:**
- `UserRepository.java`
- `ProductRepository.java`
- `OrderRepository.java`
- `OrderItemRepository.java`
- `PaymentRepository.java`

Spring Data JPA repositories work the same way in gRPC projects.

#### 3.4 Adapt & Reuse Utilities

**From REST API - MINIMAL CHANGES:**
- `JwtUtil.java` - REUSE for token generation/validation
- `Constants.java` - REUSE for constants

#### 3.5 Exception Handling - EXTEND

**From REST API - REUSE:**
- `ApiException.java`
- `ResourceNotFoundException.java`
- `UnauthorizedException.java`
- `ForbiddenException.java`
- `InvalidInputException.java`
- `ResourceAlreadyExistsException.java`

**NEW - Create for gRPC:**
- `GrpcExceptionHandler.java` - Maps exceptions to gRPC Status codes

```java
public class GrpcExceptionHandler {
    public static StatusRuntimeException handle(Exception ex) {
        if (ex instanceof UnauthorizedException) {
            return Status.UNAUTHENTICATED.withDescription(ex.getMessage()).asException();
        } else if (ex instanceof ForbiddenException) {
            return Status.PERMISSION_DENIED.withDescription(ex.getMessage()).asException();
        } else if (ex instanceof ResourceNotFoundException) {
            return Status.NOT_FOUND.withDescription(ex.getMessage()).asException();
        } else if (ex instanceof InvalidInputException) {
            return Status.INVALID_ARGUMENT.withDescription(ex.getMessage()).asException();
        } else if (ex instanceof ResourceAlreadyExistsException) {
            return Status.ALREADY_EXISTS.withDescription(ex.getMessage()).asException();
        }
        return Status.INTERNAL.withDescription("Internal server error").asException();
    }
}
```

#### Error Code Mapping

| REST HTTP Code | gRPC Status | Exception |
|---|---|---|
| 401 | UNAUTHENTICATED (16) | UnauthorizedException |
| 403 | PERMISSION_DENIED (7) | ForbiddenException |
| 404 | NOT_FOUND (5) | ResourceNotFoundException |
| 400 | INVALID_ARGUMENT (3) | InvalidInputException |
| 409 | ALREADY_EXISTS (6) | ResourceAlreadyExistsException |
| 500 | INTERNAL (13) | ApiException/Generic |

---

### Phase 4: Create gRPC Service Implementations

#### 4.1 Authentication Service Implementation

**NEW - GrpcAuthServiceImpl.java**

Key differences from REST:
- REST: `@RestController` on AuthController → gRPC: `@Service` extending `AuthServiceGrpc.AuthServiceImplBase`
- REST: `@RequestBody` parameters → gRPC: Direct message parameters
- REST: Returns `ResponseEntity<LoginResponse>` → gRPC: StreamObserver with onNext/onCompleted
- REST: HTTP exceptions with status codes → gRPC: StatusRuntimeException

```java
@Service
public class GrpcAuthServiceImpl extends AuthServiceGrpc.AuthServiceImplBase {
    
    private final AuthenticationService authService;
    private final JwtTokenProvider tokenProvider;
    
    @Override
    public void login(LoginRequest request, StreamObserver<LoginResponse> responseObserver) {
        try {
            // Reuse REST service logic
            var user = authService.authenticate(request.getUsername(), request.getPassword());
            var token = tokenProvider.generateToken(user);
            
            var response = LoginResponse.newBuilder()
                .setToken(token)
                .setExpiresIn(86400000)
                .setUser(mapUserToProto(user))
                .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(GrpcExceptionHandler.handle(e));
        }
    }
    
    // Similar implementations for register, validateToken, refreshToken
}
```

#### 4.2 Product Service Implementation

**NEW - GrpcProductServiceImpl.java**

Special features:
- Server-side streaming for `StreamProducts()` method
- Pagination support in `ListProducts()`
- Reuse of existing `ProductService` business logic

```java
@Service
public class GrpcProductServiceImpl extends ProductServiceGrpc.ProductServiceImplBase {
    
    private final ProductService productService;
    
    @Override
    public void listProducts(ProductListRequest request, 
            StreamObserver<ProductListResponse> responseObserver) {
        try {
            // Reuse service logic with pagination
            var products = productService.getAllProducts();
            var pageInfo = createPageInfo(request.getPage(), request.getPageSize(), products.size());
            
            var response = ProductListResponse.newBuilder()
                .addAllProducts(mapProductsToProto(products))
                .setPageInfo(pageInfo)
                .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(GrpcExceptionHandler.handle(e));
        }
    }
    
    @Override
    public void streamProducts(Empty request, StreamObserver<Product> responseObserver) {
        try {
            // NEW capability - stream all products
            var products = productService.getAllProducts();
            for (var product : products) {
                responseObserver.onNext(mapProductToProto(product));
            }
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(GrpcExceptionHandler.handle(e));
        }
    }
}
```

#### 4.3 Order Service Implementation

**NEW - GrpcOrderServiceImpl.java**

```java
@Service
public class GrpcOrderServiceImpl extends OrderServiceGrpc.OrderServiceImplBase {
    
    private final OrderService orderService;
    
    @Override
    public void createOrder(CreateOrderRequest request, 
            StreamObserver<CreateOrderResponse> responseObserver) {
        try {
            // Reuse service logic
            var order = orderService.createOrder(mapCreateOrderRequest(request));
            
            var response = CreateOrderResponse.newBuilder()
                .setSuccess(true)
                .setOrder(mapOrderToProto(order))
                .setMessage("Order created successfully")
                .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(GrpcExceptionHandler.handle(e));
        }
    }
    
    // Similar implementations for other methods
}
```

#### 4.4 Payment Service Implementation

**NEW - GrpcPaymentServiceImpl.java**

```java
@Service
public class GrpcPaymentServiceImpl extends PaymentServiceGrpc.PaymentServiceImplBase {
    
    private final PaymentService paymentService;
    
    @Override
    public void processPayment(ProcessPaymentRequest request, 
            StreamObserver<ProcessPaymentResponse> responseObserver) {
        try {
            // Reuse service logic
            var payment = paymentService.processPayment(mapProcessPaymentRequest(request));
            
            var response = ProcessPaymentResponse.newBuilder()
                .setSuccess(true)
                .setPayment(mapPaymentToProto(payment))
                .setMessage("Payment processed successfully")
                .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(GrpcExceptionHandler.handle(e));
        }
    }
}
```

---

### Phase 5: Create gRPC Authentication & Security

#### 5.1 JWT Authentication Interceptor

**NEW - JwtAuthenticationInterceptor.java**

**Difference from REST:**
- REST: `JwtAuthenticationFilter.java` - Filter-based authentication for HTTP
- gRPC: `JwtAuthenticationInterceptor.java` - Interceptor-based authentication for gRPC

```java
@Component
public class JwtAuthenticationInterceptor implements ServerInterceptor {
    
    private final JwtTokenProvider tokenProvider;
    
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {
        
        try {
            String token = headers.get(Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER));
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                
                if (tokenProvider.validateToken(token)) {
                    String username = tokenProvider.getUsernameFromToken(token);
                    // Set in context for use in service
                    Context context = Context.current()
                        .withValue(USERNAME_CONTEXT_KEY, username);
                    return Contexts.interceptCall(context, call, headers, next);
                }
            }
            throw new UnauthorizedException("Invalid or missing token");
        } catch (Exception e) {
            call.close(Status.UNAUTHENTICATED.withDescription(e.getMessage()), new Metadata());
            return new ServerCall.Listener<ReqT>() {};
        }
    }
}
```

#### 5.2 gRPC Security Configuration

**NEW - GrpcSecurityConfig.java**

```java
@Configuration
public class GrpcSecurityConfig {
    
    @Bean
    public JwtAuthenticationInterceptor jwtAuthenticationInterceptor(
            JwtTokenProvider tokenProvider) {
        return new JwtAuthenticationInterceptor(tokenProvider);
    }
    
    @Bean
    public GrpcServerConfigurer grpcServerConfigurer(
            JwtAuthenticationInterceptor interceptor) {
        return serverBuilder -> serverBuilder.intercept(interceptor);
    }
}
```

---

### Phase 6: Create gRPC Configuration

#### 6.1 Main Application Class

**NEW - AquaWorldGrpcApplication.java**

```java
@SpringBootApplication
public class AquaWorldGrpcApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(AquaWorldGrpcApplication.class, args);
        System.out.println("🐠 AquaWorld Pet Store gRPC API started on port 9090!");
        System.out.println("📡 Use grpcurl for testing: grpcurl -plaintext localhost:9090 list");
    }
}
```

#### 6.2 gRPC Server Configuration

**NEW - GrpcServerConfig.java**

```java
@Configuration
public class GrpcServerConfig {
    
    @Bean
    public GrpcServerConfigurer grpcServerConfigurer() {
        return serverBuilder -> {
            serverBuilder.maxInboundMessageSize(4 * 1024 * 1024); // 4MB
            serverBuilder.keepAliveTime(30, TimeUnit.SECONDS);
            serverBuilder.keepAliveTimeout(5, TimeUnit.SECONDS);
            serverBuilder.permitKeepAliveWithoutCalls(true);
        };
    }
}
```

---

### Phase 7: Testing Strategy

#### 7.1 Unit Tests

**NEW - GrpcAuthServiceImplTest.java**

```java
@SpringBootTest
public class GrpcAuthServiceImplTest {
    
    @InjectMocks
    private GrpcAuthServiceImpl authService;
    
    private static final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();
    
    @Test
    public void testLoginSuccess() {
        // Test successful login with valid credentials
    }
    
    @Test
    public void testLoginInvalidCredentials() {
        // Test login failure with invalid credentials
    }
}
```

#### 7.2 Integration Tests

**NEW - GrpcAuthServiceIntegrationTest.java**

```java
@SpringBootTest
public class GrpcAuthServiceIntegrationTest {
    
    private Channel channel;
    private AuthServiceGrpc.AuthServiceBlockingStub authStub;
    
    @Before
    public void setUp() throws IOException {
        // Setup in-process gRPC server for testing
        Server server = InProcessServerBuilder.forName("test")
            .addService(new GrpcAuthServiceImpl(authService, tokenProvider))
            .build()
            .start();
        
        channel = InProcessChannelBuilder.forName("test").build();
        authStub = AuthServiceGrpc.newBlockingStub(channel);
    }
    
    @Test
    public void testLoginFlow() {
        LoginResponse response = authStub.login(LoginRequest.newBuilder()
            .setUsername("user")
            .setPassword("pass")
            .build());
        
        assertNotNull(response.getToken());
    }
}
```

#### 7.3 Test Scenarios

| Test Case | REST Endpoint | gRPC RPC | Description |
|-----------|---------------|----------|------------|
| Valid Login | POST /auth/login | AuthService.Login | Test successful authentication |
| Invalid Credentials | POST /auth/login | AuthService.Login | Test failure with wrong password |
| Register User | POST /auth/register | AuthService.Register | Test user registration |
| List Products | GET /products | ProductService.ListProducts | Test product pagination |
| Stream Products | N/A | ProductService.StreamProducts | NEW: Test server-side streaming |
| Create Order | POST /orders | OrderService.CreateOrder | Test order creation |
| Process Payment | POST /payments | PaymentService.ProcessPayment | Test payment processing |
| Missing Token | Any with auth | Any service | Test interceptor rejects missing token |
| Expired Token | Any with auth | Any service | Test interceptor rejects expired token |

---

### Phase 8: Implementation Checklist

**Foundation Setup:**
- [ ] Create aquaworld-grpc-api directory
- [ ] Create pom.xml with gRPC and protobuf dependencies
- [ ] Add protobuf-maven-plugin and os-maven-plugin
- [ ] Create directory structure (proto/, service/impl/, config/, etc.)
- [ ] Copy JwtUtil.java, Constants.java from REST
- [ ] Copy exception classes from REST

**Protocol Buffers:**
- [ ] Create common.proto with base messages
- [ ] Create auth.proto with LoginRequest, LoginResponse, etc.
- [ ] Create product.proto with Product and service definition
- [ ] Create order.proto with Order and service definition
- [ ] Create payment.proto with Payment and service definition
- [ ] Compile proto files: `mvn clean compile`
- [ ] Verify generated Java classes in target/

**Service Layer:**
- [ ] Copy service interfaces from REST (AuthenticationService, etc.)
- [ ] Copy JPA entities from REST (User, Product, Order, etc.)
- [ ] Copy repositories from REST
- [ ] Create GrpcExceptionHandler.java

**Authentication & Security:**
- [ ] Create JwtAuthenticationInterceptor.java
- [ ] Create GrpcSecurityConfig.java
- [ ] Copy JwtTokenProvider logic from REST

**gRPC Implementations:**
- [ ] Create GrpcAuthServiceImpl.java
- [ ] Create GrpcProductServiceImpl.java
- [ ] Create GrpcOrderServiceImpl.java
- [ ] Create GrpcPaymentServiceImpl.java
- [ ] Create proto-to-Java mapping methods (mapUserToProto, etc.)

**Configuration:**
- [ ] Create AquaWorldGrpcApplication.java
- [ ] Create GrpcServerConfig.java
- [ ] Create application.properties with gRPC port (9090)
- [ ] Copy database and JWT properties from REST

**Testing:**
- [ ] Create unit tests for each gRPC service
- [ ] Create integration tests using gRPC test framework
- [ ] Test error handling and exception mapping
- [ ] Test JWT interceptor with valid/invalid tokens

**Documentation:**
- [ ] Create GRPC_SETUP.md with installation steps
- [ ] Create GRPC_USAGE.md with client examples
- [ ] Create GRPC_API_REFERENCE.md with all RPC signatures
- [ ] Create DIFFERENCES_FROM_REST.md highlighting REST vs gRPC changes

---

### Phase 9: Key Differences Between REST and gRPC

#### Communication Model

| Aspect | REST API | gRPC |
|--------|----------|------|
| Protocol | HTTP/1.1 | HTTP/2 |
| Data Format | JSON | Protocol Buffers (Binary) |
| Port | 8080 | 9090 |
| Service Definition | @RestController with @RequestMapping | .proto files with service definitions |

#### API Calls

| Operation | REST | gRPC |
|-----------|------|------|
| Get User | `GET /users/{id}` | `UserService.GetUser(UserId)` |
| List Users | `GET /users?page=1&size=10` | `UserService.ListUsers(PageRequest)` |
| Create User | `POST /users` with JSON body | `UserService.CreateUser(CreateUserRequest)` |
| Bulk Data | Multiple requests | `StreamUsers()` (server-side streaming) |

#### Authentication

| Aspect | REST API | gRPC |
|--------|----------|------|
| Token Location | HTTP Header: `Authorization: Bearer <token>` | gRPC Metadata: `authorization: Bearer <token>` |
| Validation | JwtAuthenticationFilter (HTTP Filter) | JwtAuthenticationInterceptor (gRPC Interceptor) |
| Context | SecurityContext (Spring Security) | Context (gRPC Context) |

#### Error Handling

| Aspect | REST API | gRPC |
|--------|----------|------|
| Error Response | HTTP Status Code + JSON body | gRPC Status Code + message |
| 404 Not Found | HTTP 404 with error JSON | gRPC NOT_FOUND status |
| 401 Unauthorized | HTTP 401 with error JSON | gRPC UNAUTHENTICATED status |
| 400 Bad Request | HTTP 400 with validation errors | gRPC INVALID_ARGUMENT status |

#### Service Classes

| Layer | REST | gRPC | Notes |
|-------|------|------|-------|
| Controllers | AuthController, ProductController, etc. | GrpcAuthServiceImpl, GrpcProductServiceImpl, etc. | CHANGED: Different base classes & structure |
| Services | AuthenticationService, ProductService, etc. | REUSE: Same business logic | SAME: Can reuse directly |
| Models | User, Product, Order (JPA entities) | REUSE: Same entities | SAME: Database layer unchanged |
| Repositories | UserRepository, ProductRepository, etc. | REUSE: Same repositories | SAME: Data access unchanged |
| DTOs | LoginRequest, ProductResponse, etc. (JSON classes) | NEW: Proto messages instead | CHANGED: Generated from .proto files |

---

## Summary Table: What Changes vs. What Stays Same

| Component | REST API | gRPC API | Status |
|-----------|----------|----------|--------|
| **Controllers** | AuthController, ProductController, etc. | GrpcAuthServiceImpl, GrpcProductServiceImpl, etc. | ❌ REPLACE |
| **Service Classes** | AuthenticationService, ProductService, etc. | REUSE same services | ✅ REUSE |
| **JPA Models** | User, Product, Order, Payment | REUSE same entities | ✅ REUSE |
| **Repositories** | Spring Data JPA Repositories | REUSE same repositories | ✅ REUSE |
| **Authentication** | JwtAuthenticationFilter (HTTP Filter) | JwtAuthenticationInterceptor (gRPC Interceptor) | ⚠️ ADAPT |
| **JWT Utility** | JwtUtil.java | REUSE same utility | ✅ REUSE |
| **DTOs** | REST DTO classes (LoginRequest, ProductResponse, etc.) | Proto-generated messages (LoginRequest, Product, etc.) | ⚠️ CONVERT |
| **Exceptions** | ApiException, NotFoundException, etc. | REUSE same exceptions + add GrpcExceptionHandler | ✅ REUSE + EXTEND |
| **Database** | H2/PostgreSQL | REUSE same database | ✅ REUSE |
| **Config Classes** | SecurityConfig, JwtAuthenticationFilter | GrpcSecurityConfig, GrpcServerConfig, JwtAuthenticationInterceptor | ⚠️ ADD NEW |
| **Port** | 8080 | 9090 | ❌ NEW PORT |
| **Framework** | Spring Boot Web (spring-boot-starter-web) | Spring Boot gRPC (grpc-spring-boot-starter) | ⚠️ REPLACE DEPENDENCY |

---

**Document Status**: Ready for gRPC Project Implementation  
**Last Updated**: February 4, 2026
