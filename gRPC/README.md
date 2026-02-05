# AquaWorld gRPC API - README

Welcome to the **AquaWorld gRPC API** - a high-performance gRPC-based microservice for managing an online guppy fish pet store.

## 🐠 Overview

AquaWorld gRPC API is a modern, efficient microservice built with:
- **gRPC** for high-performance inter-service communication
- **Protocol Buffers** for efficient data serialization
- **Spring Boot 3.3** with Java 21
- **JWT** for secure authentication
- **H2** in-memory database (easily swap with PostgreSQL/MySQL)

### Key Features

✅ **User Authentication** - Secure login and registration with JWT tokens  
✅ **Product Management** - Full CRUD operations with search and filtering  
✅ **Order Processing** - Complete order lifecycle management  
✅ **Payment Handling** - Payment processing and refund support  
✅ **Server-side Streaming** - Efficient bulk data streaming  
✅ **High Performance** - HTTP/2 multiplexing and binary serialization  

---

## 🚀 Quick Start

### Prerequisites

- **Java 21** or higher
- **Maven 3.8+**
- **Git**

### Installation

1. **Clone the repository**
```bash
cd /Users/gokulg.k/Documents/GitHub/API_Architecture/gRPC/aquaworld-grpc-api
```

2. **Build the project**
```bash
mvn clean install
```

3. **Run the application**
```bash
mvn spring-boot:run
```

The gRPC server will start on **port 9090**.

```
🐠 ==========================================
🐠 AquaWorld Pet Store gRPC API
🐠 ==========================================
🐠 gRPC Server started on port 9090
🐠 Test with grpcurl: grpcurl -plaintext localhost:9090 list
🐠 ==========================================
```

---

## 📡 Testing the API

### Using grpcurl

Install grpcurl:
```bash
# macOS
brew install grpcurl

# Linux
go get -u github.com/fullstorydev/grpcurl/cmd/grpcurl
```

### List Available Services

```bash
grpcurl -plaintext localhost:9090 list
```

Expected output:
```
com.aquaworld.grpc.auth.AuthService
com.aquaworld.grpc.order.OrderService
com.aquaworld.grpc.payment.PaymentService
com.aquaworld.grpc.product.ProductService
grpc.reflection.v1.ServerReflection
grpc.reflection.v1alpha.ServerReflection
```

### Example API Calls

#### 1. Register User

```bash
grpcurl -plaintext \
  -d '{"username":"john_doe","email":"john@example.com","password":"securepass123","full_name":"John Doe"}' \
  localhost:9090 com.aquaworld.grpc.auth.AuthService/Register
```

#### 2. Login User

```bash
grpcurl -plaintext \
  -d '{"username":"john_doe","password":"securepass123"}' \
  localhost:9090 com.aquaworld.grpc.auth.AuthService/Login
```

You'll receive a JWT token. Use it for authenticated requests:

```bash
grpcurl -plaintext \
  -H 'authorization: Bearer <YOUR_TOKEN>' \
  -d '{}' \
  localhost:9090 com.aquaworld.grpc.product.ProductService/ListProducts
```

#### 3. Create Product

```bash
grpcurl -plaintext \
  -H 'authorization: Bearer <YOUR_TOKEN>' \
  -d '{
    "name": "Red Guppy",
    "description": "Beautiful red male guppy",
    "price": 25.99,
    "stock_quantity": 50,
    "category": "FISH",
    "tags": ["colorful", "male", "freshwater"],
    "image_url": "http://example.com/red-guppy.jpg"
  }' \
  localhost:9090 com.aquaworld.grpc.product.ProductService/CreateProduct
```

#### 4. Stream All Products

```bash
grpcurl -plaintext \
  -H 'authorization: Bearer <YOUR_TOKEN>' \
  -d '{}' \
  localhost:9090 com.aquaworld.grpc.product.ProductService/StreamProducts
```

---

## 📁 Project Structure

```
aquaworld-grpc-api/
├── pom.xml                          # Maven configuration
├── src/
│   ├── main/
│   │   ├── java/com/aquaworld/grpc/
│   │   │   ├── AquaWorldGrpcApplication.java
│   │   │   ├── config/              # Configuration classes
│   │   │   │   ├── AppConfig.java
│   │   │   │   ├── GrpcSecurityConfig.java
│   │   │   │   └── JwtAuthenticationInterceptor.java
│   │   │   ├── service/             # Business logic services
│   │   │   │   ├── AuthenticationService.java
│   │   │   │   ├── ProductService.java
│   │   │   │   ├── OrderService.java
│   │   │   │   ├── PaymentService.java
│   │   │   │   └── impl/            # gRPC service implementations
│   │   │   │       ├── GrpcAuthServiceImpl.java
│   │   │   │       ├── GrpcProductServiceImpl.java
│   │   │   │       ├── GrpcOrderServiceImpl.java
│   │   │   │       └── GrpcPaymentServiceImpl.java
│   │   │   ├── model/               # JPA entity models
│   │   │   │   ├── User.java
│   │   │   │   ├── Product.java
│   │   │   │   ├── Order.java
│   │   │   │   ├── OrderItem.java
│   │   │   │   └── Payment.java
│   │   │   ├── repository/          # Data access layer
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── ProductRepository.java
│   │   │   │   ├── OrderRepository.java
│   │   │   │   ├── OrderItemRepository.java
│   │   │   │   └── PaymentRepository.java
│   │   │   ├── exception/           # Custom exceptions
│   │   │   │   ├── ApiException.java
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   ├── UnauthorizedException.java
│   │   │   │   ├── ForbiddenException.java
│   │   │   │   ├── InvalidInputException.java
│   │   │   │   ├── ResourceAlreadyExistsException.java
│   │   │   │   └── GrpcExceptionHandler.java
│   │   │   └── util/                # Utility classes
│   │   │       ├── Constants.java
│   │   │       └── JwtTokenProvider.java
│   │   ├── proto/                   # Protocol Buffer definitions
│   │   │   ├── common.proto
│   │   │   ├── auth.proto
│   │   │   ├── product.proto
│   │   │   ├── order.proto
│   │   │   └── payment.proto
│   │   └── resources/
│   │       └── application.properties
│   └── test/java/...                # Test files
└── target/                          # Build output
```

---

## 🔐 Authentication

### JWT Token Flow

1. **Register** - Create a new user account
2. **Login** - Authenticate with username/password, receive JWT token
3. **Use Token** - Include token in `authorization` header for authenticated requests
4. **Validate** - Server intercepts and validates token on each request
5. **Refresh** - Get a new token when current one expires

### Headers Format

```
authorization: Bearer <JWT_TOKEN>
```

### Token Claims

- **sub** (subject) - Username
- **iat** (issued at) - Token creation time
- **exp** (expiration) - Token expiry time

### Protected Endpoints

All endpoints except `AuthService/Login` and `AuthService/Register` require valid JWT token.

---

## 📚 API Documentation

### Services Overview

#### 1. AuthService
- `Login()` - Authenticate user
- `Register()` - Create new user
- `ValidateToken()` - Verify token validity
- `RefreshToken()` - Get new token

#### 2. ProductService
- `GetProduct()` - Get single product by ID
- `ListProducts()` - List products with pagination
- `CreateProduct()` - Create new product
- `UpdateProduct()` - Update product details
- `UpdateStock()` - Adjust inventory
- `DeleteProduct()` - Remove product
- `StreamProducts()` - Stream all products (server-side streaming)

#### 3. OrderService
- `CreateOrder()` - Place new order
- `GetOrder()` - Get order details
- `ListOrders()` - List user's orders with pagination
- `UpdateOrderStatus()` - Change order status
- `CancelOrder()` - Cancel order

#### 4. PaymentService
- `ProcessPayment()` - Process payment
- `GetPayment()` - Get payment details
- `GetPaymentStatus()` - Check payment status
- `RefundPayment()` - Issue refund

---

## 🛠️ Configuration

### application.properties

Key settings in `src/main/resources/application.properties`:

```properties
# gRPC Server
grpc.server.port=9090                    # gRPC server port

# JWT
jwt.secret=your-secret-key               # Change in production!
jwt.expiration=86400000                  # 24 hours

# Database
spring.datasource.url=jdbc:h2:mem:aquaworld
spring.jpa.hibernate.ddl-auto=create-drop

# Logging
logging.level.com.aquaworld=DEBUG
```

### Database Setup

The application uses H2 in-memory database by default. To use PostgreSQL:

1. Add PostgreSQL driver to `pom.xml`:
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

2. Update `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/aquaworld
spring.datasource.username=postgres
spring.datasource.password=yourpassword
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

---

## 🧪 Testing

Run all tests:
```bash
mvn test
```

Run specific test:
```bash
mvn test -Dtest=GrpcAuthServiceImplTest
```

---

## 📊 Error Handling

### gRPC Status Codes

| HTTP Code | gRPC Status | Situation |
|-----------|-------------|-----------|
| 400 | INVALID_ARGUMENT | Bad request data |
| 401 | UNAUTHENTICATED | Missing/invalid token |
| 403 | PERMISSION_DENIED | Access denied |
| 404 | NOT_FOUND | Resource not found |
| 409 | ALREADY_EXISTS | Duplicate resource |
| 500 | INTERNAL | Server error |

### Example Error Response

```bash
$ grpcurl -plaintext \
  localhost:9090 com.aquaworld.grpc.product.ProductService/GetProduct
  
Code: NOT_FOUND
Message: Product with ID 'invalid-id' not found
```

---

## 🚢 Deployment

### Docker

Create `Dockerfile`:
```dockerfile
FROM openjdk:21-slim
COPY target/aquaworld-grpc-api-1.0.0.jar app.jar
EXPOSE 9090
ENTRYPOINT ["java","-jar","/app.jar"]
```

Build and run:
```bash
docker build -t aquaworld-grpc:latest .
docker run -p 9090:9090 aquaworld-grpc:latest
```

### Kubernetes

Example Kubernetes deployment coming soon...

---

## 📖 Additional Resources

- [gRPC Documentation](https://grpc.io/docs/)
- [Protocol Buffers Guide](https://developers.google.com/protocol-buffers)
- [Spring Boot gRPC Starter](https://github.com/yidongnan/grpc-spring-boot-starter)
- [JWT Best Practices](https://tools.ietf.org/html/rfc8949)

---

## 📝 Troubleshooting

### Port Already in Use

If port 9090 is in use:
```bash
# Find process using port 9090
lsof -i :9090

# Kill process
kill -9 <PID>
```

### Build Issues

Clear Maven cache:
```bash
mvn clean install -U
```

### Proto Compilation

Recompile proto files:
```bash
mvn clean protobuf:compile protobuf:compile-custom
```

---

## 🤝 Contributing

Contributions welcome! Please follow:
1. Fork the repository
2. Create feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open Pull Request

---

## 📄 License

This project is licensed under the MIT License - see LICENSE file for details.

---

## 👥 Author

**AquaWorld Development Team**  
Version 1.0.0  
Last Updated: February 4, 2026

---

## 🐠 Support

For issues, questions, or suggestions, please open an issue on GitHub.

**Happy coding with gRPC! 🚀**
