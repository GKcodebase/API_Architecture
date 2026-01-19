# 🐠 AquaWorld Pet Store REST API

A comprehensive REST API for an online guppy fish pet store, built with Spring Boot 3.3 and Java 21.

**AquaWorld specializes in selling premium guppies and aquatic accessories.**

## 📋 Table of Contents

- [Features](#features)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
- [Authentication](#authentication)
- [HTTP Status Codes](#http-status-codes)
- [Sample Data](#sample-data)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Tech Stack](#tech-stack)
- [Best Practices Implemented](#best-practices-implemented)

---

## ✨ Features

### REST API Features
- ✅ **CRUD Operations**: Create, Read, Update, Delete resources
- ✅ **HTTP Methods**: GET, POST, PUT, DELETE with proper status codes
- ✅ **Consistent Response Format**: All endpoints return standardized ApiResponse
- ✅ **Proper HTTP Status Codes**: 200, 201, 204, 400, 401, 403, 404, 409, 500, 503
- ✅ **Request Validation**: Input validation on all endpoints
- ✅ **Error Handling**: Centralized exception handling with meaningful error messages

### Authentication & Security
- ✅ **JWT Token Authentication**: Stateless token-based auth
- ✅ **Password Encryption**: BCrypt for secure password storage
- ✅ **Role-Based Access**: Customer, Admin roles
- ✅ **CORS Configuration**: Cross-origin request handling
- ✅ **Token Expiration**: 1-hour token validity

### Data & Storage
- ✅ **In-Memory Database**: Thread-safe ConcurrentHashMap for demo
- ✅ **Sample Data Initialization**: Auto-loads test data on startup
- ✅ **Entity Relationships**: Users, Products, Orders, Payments

### API Documentation
- ✅ **Swagger/OpenAPI**: Interactive API documentation
- ✅ **Comprehensive Comments**: Well-documented code
- ✅ **Usage Examples**: cURL examples for all endpoints

---

## 📁 Project Structure

```
aquaworld-api/
├── pom.xml                              # Maven configuration
├── src/main/java/com/aquaworld/
│   ├── AquaWorldApplication.java        # Main Spring Boot app
│   ├── config/
│   │   ├── AppConfig.java               # Bean configuration
│   │   ├── SecurityConfig.java          # Spring Security setup
│   │   ├── OpenApiConfig.java           # Swagger configuration
│   │   ├── JwtAuthenticationFilter.java # JWT filter
│   │   └── DataInitializer.java         # Sample data loader
│   ├── controller/                      # REST Controllers
│   │   ├── AuthController.java          # Login & registration
│   │   ├── ProductController.java       # Product browsing
│   │   ├── OrderController.java         # Order management
│   │   └── PaymentController.java       # Payment processing
│   ├── service/                         # Business Logic Layer
│   │   ├── AuthenticationService.java   # User auth logic
│   │   ├── ProductService.java          # Product operations
│   │   ├── OrderService.java            # Order management
│   │   ├── PaymentService.java          # Payment processing
│   │   └── UserService.java             # User profile
│   ├── repository/                      # Data Access Layer
│   │   ├── UserRepository.java          # User storage
│   │   ├── ProductRepository.java       # Product storage
│   │   ├── OrderRepository.java         # Order storage
│   │   ├── OrderItemRepository.java     # Order items
│   │   └── PaymentRepository.java       # Payment storage
│   ├── model/                           # Data Models (DBOs)
│   │   ├── User.java
│   │   ├── Product.java
│   │   ├── Order.java
│   │   ├── OrderItem.java
│   │   └── Payment.java
│   ├── dto/                             # Data Transfer Objects
│   │   ├── LoginRequest.java
│   │   ├── LoginResponse.java
│   │   ├── RegisterRequest.java
│   │   ├── ProductResponse.java
│   │   ├── CreateOrderRequest.java
│   │   ├── OrderResponse.java
│   │   ├── PaymentRequest.java
│   │   └── PaymentResponse.java
│   ├── exception/                       # Exception Classes
│   │   ├── ApiException.java
│   │   ├── ResourceNotFoundException.java
│   │   ├── UnauthorizedException.java
│   │   ├── ResourceAlreadyExistsException.java
│   │   ├── ForbiddenException.java
│   │   ├── InvalidInputException.java
│   │   └── GlobalExceptionHandler.java
│   ├── response/
│   │   └── ApiResponse.java             # Generic API response wrapper
│   └── util/
│       ├── Constants.java               # Application constants
│       └── JwtUtil.java                 # JWT token utilities
├── src/main/resources/
│   └── application.properties           # App configuration
└── README.md                            # This file
```

---

## 🔧 Prerequisites

- **Java**: Version 21 or latest LTS
- **Maven**: 3.8.1 or higher
- **Git**: For cloning repository
- **Postman/cURL**: For API testing (optional, use Swagger UI instead)

### Check Java Version
```bash
java -version
# Should output: openjdk version "21" or higher
```

### Check Maven Version
```bash
mvn -version
# Should output: Apache Maven 3.8.1 or higher
```

---

## 📦 Installation & Setup

### 1. Clone the Repository
```bash
cd /path/to/API_Architecture/REST
git clone <repository-url>
cd aquaworld-api
```

### 2. Build the Project
Maven will automatically download all dependencies specified in `pom.xml`.

```bash
mvn clean install
```

This command:
- Cleans previous builds
- Downloads dependencies
- Compiles source code
- Runs tests
- Packages the application

### 3. Build Output
If successful, you'll see:
```
[INFO] BUILD SUCCESS
[INFO] Total time: XX.XXXs
```

A JAR file is created: `target/aquaworld-api-1.0.0.jar`

---

## ▶️ Running the Application

### Option 1: Using Maven (Development)
```bash
mvn spring-boot:run
```

### Option 2: Running JAR file (Production)
```bash
java -jar target/aquaworld-api-1.0.0.jar
```

### Startup Output
You should see:
```
🐠 AquaWorld Pet Store REST API started successfully!
📚 API Documentation: http://localhost:8080/swagger-ui.html
✓ Created 3 sample users
✓ Created 12 sample products
✓ Created 2 sample orders with multiple items
```

### Server Information
- **Base URL**: `http://localhost:8080`
- **API Version**: `/api/v1`
- **Context Path**: `/aquaworld`
- **Full Base Path**: `http://localhost:8080/aquaworld/api/v1`

---

## 🔌 API Endpoints

### 1️⃣ Authentication Endpoints (Public)

**Base Path**: `/api/v1/auth`

#### Register New User
```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "SecurePass123",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "555-1234"
}
```

**Response (201 Created)**:
```json
{
  "success": true,
  "message": "Registration successful",
  "statusCode": 201,
  "data": {
    "id": 1001,
    "username": "johndoe",
    "email": "john@example.com",
    "firstName": "John",
    "lastName": "Doe"
  }
}
```

#### User Login
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "john",
  "password": "john@123"
}
```

**Response (200 OK)**:
```json
{
  "success": true,
  "message": "Login successful",
  "statusCode": 200,
  "data": {
    "id": 1001,
    "username": "john",
    "email": "john@aquaworld.com",
    "firstName": "John",
    "lastName": "Doe",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "loginAt": "2025-01-18T10:30:00"
  }
}
```

#### Health Check
```http
GET /api/v1/auth/health
```

**Response (200 OK)**:
```json
{
  "success": true,
  "message": "🐠 AquaWorld API is running",
  "statusCode": 200,
  "data": null
}
```

---

### 2️⃣ Product Endpoints (Public)

**Base Path**: `/api/v1/products`

#### Get All Products
```http
GET /api/v1/products
```

#### Get Product by ID
```http
GET /api/v1/products/2001
```

**Response (200 OK)**:
```json
{
  "success": true,
  "message": "Product retrieved successfully",
  "statusCode": 200,
  "data": {
    "id": 2001,
    "name": "Red Guppy Male - Premium",
    "category": "guppies",
    "description": "Beautiful red male guppy with full tail fin",
    "price": 5.99,
    "stock": 25,
    "imageUrl": "https://aquaworld.com/images/red-guppy.jpg",
    "createdAt": "2025-01-15T08:00:00"
  }
}
```

#### Search Products by Name
```http
GET /api/v1/products/search?name=Guppy
```

#### Get Products by Category
```http
GET /api/v1/products/category/guppies
```

**Categories**: `guppies`, `fish_food`, `equipment`, `decorations`, `medicines`

#### Get In-Stock Products
```http
GET /api/v1/products/stock/available
```

---

### 3️⃣ Order Endpoints (Protected - Requires JWT)

**Base Path**: `/api/v1/orders`
**Authentication**: Required (JWT token in Authorization header)

#### Get User's Orders
```http
GET /api/v1/orders
Authorization: Bearer <JWT_TOKEN>
```

#### Create New Order
```http
POST /api/v1/orders
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "orderItems": [
    {
      "productId": 2001,
      "quantity": 2
    },
    {
      "productId": 2005,
      "quantity": 1
    }
  ]
}
```

**Response (201 Created)**:
```json
{
  "success": true,
  "message": "Order created successfully",
  "statusCode": 201,
  "data": {
    "id": 3001,
    "orderNumber": "ORD-20250118-001",
    "totalPrice": 16.97,
    "status": "PENDING",
    "orderItems": [
      {
        "id": 4001,
        "productId": 2001,
        "quantity": 2,
        "price": 5.99
      }
    ],
    "createdAt": "2025-01-18T10:30:00"
  }
}
```

#### Get Order by ID
```http
GET /api/v1/orders/3001
Authorization: Bearer <JWT_TOKEN>
```

#### Update Order Status
```http
PUT /api/v1/orders/3001/status
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "status": "CONFIRMED"
}
```

**Valid Status Transitions**:
- PENDING → CONFIRMED, CANCELLED
- CONFIRMED → PROCESSING, CANCELLED
- PROCESSING → SHIPPED
- SHIPPED → DELIVERED

#### Cancel Order
```http
DELETE /api/v1/orders/3001
Authorization: Bearer <JWT_TOKEN>
```

**Response (200 OK)**:
```json
{
  "success": true,
  "message": "Order cancelled successfully",
  "statusCode": 200,
  "data": {
    "id": 3001,
    "status": "CANCELLED"
  }
}
```

---

### 4️⃣ Payment Endpoints (Protected - Requires JWT)

**Base Path**: `/api/v1/payments`
**Authentication**: Required (JWT token)

#### Process Payment
```http
POST /api/v1/payments
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "orderId": 3001,
  "amount": 16.97,
  "paymentMethod": "CREDIT_CARD"
}
```

**Payment Methods**: `CREDIT_CARD`, `DEBIT_CARD`, `DIGITAL_WALLET`, `BANK_TRANSFER`

**Response (201 Created)**:
```json
{
  "success": true,
  "message": "Payment processed successfully",
  "statusCode": 201,
  "data": {
    "id": 5001,
    "orderId": 3001,
    "amount": 16.97,
    "status": "SUCCESS",
    "paymentMethod": "CREDIT_CARD",
    "transactionId": "TXN-550e8400-e29b-41d4-a716",
    "createdAt": "2025-01-18T10:35:00"
  }
}
```

#### Get Payment by ID
```http
GET /api/v1/payments/5001
Authorization: Bearer <JWT_TOKEN>
```

#### Get Payment by Order ID
```http
GET /api/v1/payments/order/3001
Authorization: Bearer <JWT_TOKEN>
```

#### Refund Payment
```http
POST /api/v1/payments/5001/refund
Authorization: Bearer <JWT_TOKEN>
```

---

## 🔐 Authentication

### JWT Token Flow

```
1. Client sends credentials (username, password)
              ↓
2. Server validates credentials
              ↓
3. Server generates JWT token if valid
              ↓
4. Client receives token and stores it
              ↓
5. Client includes token in Authorization header for protected endpoints
   Format: Authorization: Bearer <JWT_TOKEN>
              ↓
6. Server validates token signature and expiration
              ↓
7. Request granted if token valid, denied if expired/invalid
```

### Token Format

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huIiwiaWF0IjoxNzM0NzY4MjAwLCJleHAiOjE3MzQ3NzE4MDB9.signature
│────────────────────────────────│ │──────────────────────────────────────────────────────────────────────│ │──────────────────────│
            Header                                              Payload                                          Signature
```

### Sample Credentials (Auto-Generated)
```
User 1: john / john@123
User 2: admin / admin@123
User 3: jane / jane@123
```

### Token Validity
- **Expiration**: 1 hour
- **Algorithm**: HMAC-SHA256
- **Secret Key**: Configured in application.properties

### Using Token in Requests

#### cURL Example:
```bash
curl -X GET http://localhost:8080/aquaworld/api/v1/orders \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

#### Postman:
1. Go to "Authorization" tab
2. Select "Bearer Token"
3. Paste your JWT token

#### JavaScript Fetch:
```javascript
const token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";

fetch('http://localhost:8080/aquaworld/api/v1/orders', {
  method: 'GET',
  headers: {
    'Authorization': `Bearer ${token}`
  }
})
.then(response => response.json())
.then(data => console.log(data));
```

---

## 📊 HTTP Status Codes

| Code | Name | Usage |
|------|------|-------|
| **200** | OK | GET request successful, resource retrieved |
| **201** | Created | POST request successful, new resource created |
| **204** | No Content | DELETE request successful |
| **400** | Bad Request | Invalid input, validation failed |
| **401** | Unauthorized | Missing/invalid JWT token, login failed |
| **403** | Forbidden | User lacks permission for action |
| **404** | Not Found | Resource (user, product, order) doesn't exist |
| **409** | Conflict | Resource already exists (duplicate username/email) |
| **500** | Internal Server Error | Unexpected server error |
| **503** | Service Unavailable | Service temporarily down |

### Status Code Examples

#### 200 OK - Successful GET
```json
{
  "success": true,
  "message": "Product retrieved successfully",
  "statusCode": 200,
  "data": {...}
}
```

#### 201 Created - Successful POST
```json
{
  "success": true,
  "message": "Order created successfully",
  "statusCode": 201,
  "data": {...}
}
```

#### 400 Bad Request - Validation Error
```json
{
  "success": false,
  "message": "Validation failed - Please check your input",
  "statusCode": 400,
  "data": {
    "username": "Username is required",
    "email": "Email should be valid"
  }
}
```

#### 401 Unauthorized - Invalid Credentials
```json
{
  "success": false,
  "message": "Invalid username or password",
  "statusCode": 401,
  "data": null
}
```

#### 404 Not Found - Resource Missing
```json
{
  "success": false,
  "message": "Order not found",
  "statusCode": 404,
  "data": null
}
```

#### 409 Conflict - Duplicate Resource
```json
{
  "success": false,
  "message": "Username already exists",
  "statusCode": 409,
  "data": null
}
```

---

## 📋 Sample Data

The application auto-initializes with sample data on startup:

### Users
| Username | Password | Role | Email |
|----------|----------|------|-------|
| john | john@123 | Customer | john@aquaworld.com |
| admin | admin@123 | Admin | admin@aquaworld.com |
| jane | jane@123 | Customer | jane@aquaworld.com |

### Products (Sample)
**Guppies**:
- Red Guppy Male ($5.99) - Stock: 25
- Blue Guppy Male ($6.49) - Stock: 18
- Black Lace Guppy ($4.99) - Stock: 30
- Yellow Guppy Pair ($9.99) - Stock: 12

**Fish Food**:
- Premium Guppy Food ($8.99) - Stock: 50
- Guppy Fry Food ($12.99) - Stock: 35
- Color Enhancement Pellets ($14.99) - Stock: 28

**Equipment**:
- 10 Gallon Tank ($49.99) - Stock: 15
- Tank Filter ($24.99) - Stock: 22
- Aquarium Heater ($19.99) - Stock: 40

**Decorations**:
- Aquatic Plant - Cabomba ($5.99) - Stock: 60
- Driftwood - Large ($17.99) - Stock: 8

**Medicines**:
- Fish Antibiotic Treatment ($16.99) - Stock: 20

### Sample Orders
- Order 1: 2 Red Guppies + 1 Food = $27.96 (PENDING)
- Order 2: 10-Gallon Tank + Heater + Yellow Guppy Pair = $99.96 (CONFIRMED)

---

## 📚 API Documentation

### Swagger/OpenAPI UI
Interactive API documentation with try-it-out feature:

```
http://localhost:8080/swagger-ui.html
```

Features:
- **Endpoint Documentation**: Full endpoint details and parameters
- **Try It Out**: Execute requests directly from browser
- **Authentication**: Set JWT token for protected endpoints
- **Response Examples**: View sample responses for each endpoint
- **Schema Definition**: View data model definitions

### Swagger JSON
Raw API specification:
```
http://localhost:8080/aquaworld/v3/api-docs
```

### API Health Check
```
http://localhost:8080/aquaworld/actuator/health
```

---

## 🧪 Testing

### Testing with cURL

#### 1. Register New User
```bash
curl -X POST http://localhost:8080/aquaworld/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "TestPass123",
    "firstName": "Test",
    "lastName": "User"
  }'
```

#### 2. Login
```bash
curl -X POST http://localhost:8080/aquaworld/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "password": "john@123"
  }'
```

**Save the returned token**: Copy the `data.token` value

#### 3. Get All Products
```bash
curl -X GET http://localhost:8080/aquaworld/api/v1/products
```

#### 4. Create Order (with token)
```bash
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

curl -X POST http://localhost:8080/aquaworld/api/v1/orders \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "orderItems": [
      {
        "productId": 2001,
        "quantity": 2
      }
    ]
  }'
```

#### 5. Get User Orders
```bash
curl -X GET http://localhost:8080/aquaworld/api/v1/orders \
  -H "Authorization: Bearer $TOKEN"
```

### Testing with Postman
1. Import API from: `http://localhost:8080/aquaworld/v3/api-docs`
2. Set environment variable: `token` = JWT token from login response
3. Use `{{token}}` in Authorization header for protected endpoints
4. Test all endpoints from Collections

---

## 🛠 Tech Stack

**Framework & Language**
- Spring Boot 3.3.4
- Java 21 (Latest LTS)
- Maven 3.8.1+

**Security & Authentication**
- Spring Security 6.x
- JWT (JJWT 0.12.3)
- BCrypt Password Encoding

**API & Documentation**
- Spring Web (REST Controller)
- Springdoc OpenAPI 2.3.0 (Swagger UI)
- Lombok (Reduce boilerplate)

**Validation**
- Spring Validation (Jakarta Bean Validation)

**Database** (For Demo)
- In-Memory Storage (ConcurrentHashMap)
- Thread-Safe Collections (AtomicLong)

---

## ✅ Best Practices Implemented

### 1. REST API Principles
- ✅ Resource-Based Endpoints (nouns: `/products`, `/orders`)
- ✅ HTTP Methods: GET (retrieve), POST (create), PUT (update), DELETE (delete)
- ✅ Stateless Communication (JWT tokens)
- ✅ Proper HTTP Status Codes (200, 201, 400, 401, 404, 409, 500)
- ✅ Standard Response Format (ApiResponse wrapper)

### 2. Code Organization (Layered Architecture)
```
Controller (HTTP) → Service (Business) → Repository (Data)
```
- ✅ Separation of Concerns
- ✅ Single Responsibility Principle
- ✅ Dependency Injection
- ✅ Loose Coupling

### 3. Security
- ✅ JWT Token-Based Authentication
- ✅ BCrypt Password Hashing (never plain text)
- ✅ Role-Based Authorization
- ✅ CORS Configuration
- ✅ Input Validation

### 4. Exception Handling
- ✅ Custom Exception Classes
- ✅ Global Exception Handler (GlobalExceptionHandler)
- ✅ Meaningful Error Messages
- ✅ Proper HTTP Error Codes

### 5. Data Transfer Objects (DTOs)
- ✅ Separation of API contract from internal models
- ✅ Request DTOs for input validation
- ✅ Response DTOs for output formatting
- ✅ Protection against over-exposure

### 6. Validation
- ✅ @Valid and @NotNull annotations
- ✅ Custom validators
- ✅ Bean Validation (Jakarta)
- ✅ Field-level validation errors

### 7. Documentation
- ✅ Javadoc Comments (extensive)
- ✅ OpenAPI/Swagger Integration
- ✅ README with examples
- ✅ Code explanations for complex logic

### 8. Testing Readiness
- ✅ Service layer easily testable
- ✅ Mock-friendly dependency injection
- ✅ Proper exception hierarchy
- ✅ In-memory repositories for quick testing

### 9. Configuration Management
- ✅ Externalized configuration (application.properties)
- ✅ Environment-specific settings
- ✅ Configurable JWT secret and expiration
- ✅ Logging configuration

### 10. Logging & Monitoring
- ✅ Structured logging
- ✅ Error stack traces in GlobalExceptionHandler
- ✅ Health check endpoint (/actuator/health)
- ✅ Application metrics ready

---

## 🚀 Future Enhancements

1. **Database Integration**
   - Replace in-memory storage with actual database (PostgreSQL, MySQL)
   - Add JPA/Hibernate ORM

2. **Payment Gateway Integration**
   - Stripe API integration
   - PayPal integration
   - Real payment processing

3. **Advanced Features**
   - Pagination and sorting
   - Advanced search/filtering
   - Wishlist functionality
   - Product reviews and ratings
   - Email notifications
   - SMS notifications

4. **Admin Features**
   - Admin dashboard
   - Inventory management
   - User management
   - Order reports and analytics

5. **Testing**
   - Unit tests (JUnit 5, Mockito)
   - Integration tests
   - API contract testing

6. **DevOps**
   - Docker containerization
   - Kubernetes deployment
   - CI/CD pipeline (GitHub Actions, Jenkins)

---

## 📞 Support

For issues or questions:
1. Check API Documentation: `/swagger-ui.html`
2. Review code comments and JavaDoc
3. Test with sample cURL commands
4. Check HTTP status codes for error details

---

**🐠 Happy Fish Shopping! Welcome to AquaWorld! 🐠**

**Version**: 1.0.0  
**Last Updated**: January 2025  
