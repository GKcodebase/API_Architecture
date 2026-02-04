# 🐠 API Architecture - AquaWorld Pet Store

A comprehensive repository showcasing **three modern API architectures** built with **Java 21 and Spring Boot 3.3** for the AquaWorld online pet store specializing in premium guppies and aquatic accessories.

This project demonstrates how the **same business logic** can be implemented using three distinct API paradigms: **REST**, **GraphQL**, and **gRPC**, allowing you to understand the strengths, trade-offs, and use cases of each approach.

---

## 📚 Table of Contents

- [Project Overview](#project-overview)
- [Folder Structure](#folder-structure)
- [API Architectures](#api-architectures)
- [Quick Start](#quick-start)
- [Tech Stack](#tech-stack)
- [Features](#features)
- [Sample Data](#sample-data)
- [Documentation](#documentation)
- [Comparison Guide](#comparison-guide)
- [Requirements](#requirements)

---

## 🎯 Project Overview

**AquaWorld** is a modern online pet store platform with the following capabilities:

- **User Management**: Registration, authentication, and profile management
- **Product Catalog**: Browse premium guppies and aquatic accessories
- **Order Management**: Create, view, and manage orders
- **Payment Processing**: Process and track payments
- **Authentication**: JWT-based stateless authentication
- **Authorization**: Role-based access control (Customer, Admin)

Each API implementation (REST, GraphQL, gRPC) provides complete coverage of these business domains while demonstrating best practices specific to that architecture.

---

## 📁 Folder Structure

```
API_Architecture/
├── REST/                           # Traditional REST API Implementation
│   ├── README.md                   # REST API documentation
│   ├── SIMPLE_REST_API_GUIDE.md     # Quick start guide
│   └── aquaworld-api/
│       ├── pom.xml
│       └── src/main/java/com/aquaworld/
│           ├── config/             # Security, CORS, JWT, Data initialization
│           ├── controller/         # REST endpoints
│           ├── service/            # Business logic
│           ├── repository/         # Data access layer
│           ├── model/              # Domain entities
│           ├── dto/                # Request/Response objects
│           ├── exception/          # Error handling
│           ├── response/           # Unified response wrapper
│           └── util/               # Constants, JWT utilities
│
├── GraphQL/                        # GraphQL API Implementation
│   ├── README.md                   # GraphQL API documentation
│   ├── QUICKSTART.md               # Quick start guide
│   ├── IMPLEMENTATION_COMPLETE.md  # Full API reference
│   ├── PROJECT_COMPLETION_SUMMARY.md
│   ├── VISUAL_GUIDE.md             # Architecture diagrams
│   ├── RUN_GRAPHQL_API.sh          # Startup script
│   └── aquaworld-graphql-api/
│       ├── pom.xml
│       └── src/main/java/com/aquaworld/graphql/
│           ├── config/             # Spring GraphQL, Security, CORS config
│           ├── security/           # JWT validation
│           ├── controller/         # GraphiQL controller
│           ├── resolver/           # Query & Mutation resolvers
│           ├── service/            # Business logic
│           ├── repository/         # Data access layer
│           ├── model/              # Domain entities
│           ├── dto/                # Input types for mutations
│           └── exception/          # Error handling
│       └── resources/
│           └── graphql/
│               └── schema.graphqls # GraphQL schema definition
│
├── gRPC/                           # gRPC API Implementation
│   ├── README.md                   # gRPC overview
│   ├── GRPC_MIGRATION_PLAN.md      # Migration strategy
│   └── aquaworld-grpc-api/
│       ├── pom.xml
│       ├── SETUP_GUIDE.md          # Installation guide
│       ├── USAGE_GUIDE.md          # Usage examples
│       └── src/
│           ├── main/
│           │   ├── java/com/aquaworld/grpc/
│           │   │   ├── config/     # gRPC configuration
│           │   │   ├── service/    # Service implementations
│           │   │   ├── repository/ # Data access layer
│           │   │   ├── model/      # Domain entities
│           │   │   ├── exception/  # Error handling
│           │   │   ├── util/       # Utilities
│           │   │   ├── auth/       # Auth service
│           │   │   ├── product/    # Product service
│           │   │   ├── order/      # Order service
│           │   │   └── payment/    # Payment service
│           │   └── proto/          # Protocol Buffer definitions
│           │       ├── auth.proto
│           │       ├── product.proto
│           │       ├── order.proto
│           │       ├── payment.proto
│           │       └── common.proto
│           └── test/java/
│
└── LICENSE
```

---

## 🏗️ API Architectures

### 1. **REST API** - Traditional HTTP-based API

**Location**: `REST/aquaworld-api/`

REST (Representational State Transfer) is the most common API architecture pattern, using HTTP verbs and standard status codes.

#### Key Characteristics:
- ✅ **Resource-oriented**: Resources identified by URIs
- ✅ **HTTP-native**: Uses GET, POST, PUT, DELETE, PATCH
- ✅ **Stateless**: Each request contains all needed information
- ✅ **Standard Status Codes**: 200, 201, 404, 500, etc.
- ✅ **JSON payloads**: Standardized request/response format
- ✅ **Swagger/OpenAPI**: Interactive API documentation

#### Sample Endpoints:
```
GET    /api/v1/products              # List all products
GET    /api/v1/products/{id}         # Get product details
POST   /api/v1/products              # Create product (Admin)
PUT    /api/v1/products/{id}         # Update product (Admin)
DELETE /api/v1/products/{id}         # Delete product (Admin)

POST   /api/v1/auth/register         # User registration
POST   /api/v1/auth/login            # User login
POST   /api/v1/orders                # Create order
GET    /api/v1/orders/{id}           # Get order details
```

#### Best For:
- 🎯 Public APIs
- 🎯 Simple, straightforward operations
- 🎯 Standard CRUD operations
- 🎯 Browser-based clients
- 🎯 Widely understood patterns

**📖 [REST API Documentation](REST/README.md)** | **🚀 [Quick Start](REST/SIMPLE_REST_API_GUIDE.md)**

---

### 2. **GraphQL API** - Query Language for APIs

**Location**: `GraphQL/aquaworld-graphql-api/`

GraphQL is a modern query language that lets clients request exactly the data they need, enabling efficient and flexible data fetching.

#### Key Characteristics:
- ✅ **Strongly typed schema**: Self-documenting API
- ✅ **Single endpoint**: All queries to `/graphql`
- ✅ **Client-driven**: Request only needed fields
- ✅ **Nested queries**: Fetch related data in one request
- ✅ **Powerful playground**: Built-in GraphiQL IDE
- ✅ **No over-fetching**: Get exactly what you request

#### Sample Queries:
```graphql
# Get products with selective fields
query {
  products {
    id
    name
    price
    stock
  }
}

# Nested query - Get order with items and payments
query {
  order(id: 1001) {
    id
    status
    totalAmount
    items {
      productName
      quantity
      price
    }
    payment {
      method
      status
    }
  }
}

# Search and filter
query {
  searchProducts(name: "Guppy") {
    id
    name
    description
    price
  }
}
```

#### Sample Mutations:
```graphql
# Create an order
mutation {
  createOrder(input: {
    userId: 1
    items: [
      { productId: 2001, quantity: 2 }
      { productId: 2002, quantity: 1 }
    ]
  }) {
    id
    status
    totalAmount
  }
}

# User login
mutation {
  login(input: { email: "user@example.com", password: "password" }) {
    user {
      id
      name
      email
    }
    token
  }
}
```

#### Best For:
- 🎯 Complex data requirements
- 🎯 Mobile applications (bandwidth conscious)
- 🎯 Multiple client types with different needs
- 🎯 Rapid frontend development
- 🎯 Real-time applications

**📖 [GraphQL API Documentation](GraphQL/README.md)** | **🚀 [Quick Start](GraphQL/QUICKSTART.md)** | **📋 [Full Reference](GraphQL/IMPLEMENTATION_COMPLETE.md)**

---

### 3. **gRPC API** - High-Performance Binary Protocol

**Location**: `gRPC/aquaworld-grpc-api/`

gRPC (gRPC Remote Procedure Call) uses Protocol Buffers and HTTP/2 for ultra-fast, efficient communication - ideal for microservices and high-performance scenarios.

#### Key Characteristics:
- ✅ **Binary protocol**: Smaller payloads than JSON
- ✅ **HTTP/2**: Multiplexing, server push, header compression
- ✅ **Code generation**: Automatic client/server stubs
- ✅ **Streaming**: Bi-directional streaming support
- ✅ **Type-safe**: Protocol Buffer definitions enforce types
- ✅ **Language-agnostic**: Works with Java, Go, Python, etc.

#### Service Definitions (Proto):
```protobuf
service AuthService {
  rpc Register(RegisterRequest) returns (AuthResponse);
  rpc Login(LoginRequest) returns (AuthResponse);
}

service ProductService {
  rpc GetProduct(ProductRequest) returns (Product);
  rpc ListProducts(Empty) returns (stream Product);
  rpc SearchProducts(SearchRequest) returns (stream Product);
}

service OrderService {
  rpc CreateOrder(OrderRequest) returns (Order);
  rpc GetOrder(OrderRequest) returns (Order);
  rpc ListOrders(UserRequest) returns (stream Order);
}

service PaymentService {
  rpc ProcessPayment(PaymentRequest) returns (PaymentResponse);
}
```

#### Best For:
- 🎯 Microservices architecture
- 🎯 High-performance applications
- 🎯 Real-time bidirectional communication
- 🎯 Backend-to-backend communication
- 🎯 IoT and embedded systems
- 🎯 Bandwidth-constrained environments

**📖 [gRPC Documentation](gRPC/README.md)** | **🚀 [Setup Guide](gRPC/aquaworld-grpc-api/SETUP_GUIDE.md)** | **📘 [Usage Guide](gRPC/aquaworld-grpc-api/USAGE_GUIDE.md)**

---

## 🚀 Quick Start

### Run REST API

```bash
cd REST/aquaworld-api
mvn clean install
mvn spring-boot:run
```

API available at: `http://localhost:8080/api/v1/`
Swagger UI: `http://localhost:8080/swagger-ui.html`

### Run GraphQL API

```bash
cd GraphQL/aquaworld-graphql-api
mvn clean install
mvn spring-boot:run
```

API available at: `http://localhost:8081/graphql`
GraphiQL Playground: `http://localhost:8081/graphiql`

Or use the startup script:
```bash
cd GraphQL
bash RUN_GRAPHQL_API.sh
```

### Run gRPC API

```bash
cd gRPC/aquaworld-grpc-api
mvn clean install
mvn spring-boot:run
```

gRPC server available at: `localhost:9090`

**Detailed setup guides:**
- [REST Setup](REST/SIMPLE_REST_API_GUIDE.md)
- [GraphQL Setup](GraphQL/QUICKSTART.md)
- [gRPC Setup](gRPC/aquaworld-grpc-api/SETUP_GUIDE.md)

---

## 🛠️ Tech Stack

All three implementations use the same modern Java stack:

| Component | Version | Purpose |
|-----------|---------|---------|
| **Java** | 21 | Latest LTS version |
| **Spring Boot** | 3.3.4 | Framework foundation |
| **Maven** | 3.8+ | Build tool |
| **Spring Security** | Latest | Authentication & authorization |
| **JWT** | 0.12.3 | Stateless auth tokens |
| **Lombok** | 1.18.42 | Reduce boilerplate code |
| **Spring GraphQL** | 1.2.x | GraphQL integration (GraphQL only) |
| **Protocol Buffers** | 3.24.4 | Serialization (gRPC only) |
| **gRPC** | 1.60.0 | RPC framework (gRPC only) |

---

## ✨ Features

### Common Across All APIs

- ✅ **User Authentication**: Registration and login with JWT tokens
- ✅ **JWT Authorization**: Stateless token-based access control
- ✅ **Role-Based Access**: Customer and Admin roles
- ✅ **Password Encryption**: BCrypt for secure storage
- ✅ **CORS Support**: Cross-origin request handling
- ✅ **Exception Handling**: Centralized error handling
- ✅ **In-Memory Database**: Thread-safe data storage (ConcurrentHashMap)
- ✅ **Sample Data**: Auto-loaded test data on startup
- ✅ **Security Best Practices**: Input validation, secure headers

### REST API Specific
- ✅ HTTP Methods: GET, POST, PUT, DELETE
- ✅ Proper Status Codes: 200, 201, 204, 400, 401, 403, 404, 409, 500
- ✅ Swagger/OpenAPI Documentation
- ✅ RESTful conventions

### GraphQL API Specific
- ✅ Strongly Typed Schema
- ✅ 14+ Queries for data retrieval
- ✅ 12+ Mutations for data modification
- ✅ GraphiQL Interactive Playground
- ✅ Nested/Related data in single request
- ✅ Query validation and optimization

### gRPC API Specific
- ✅ Protocol Buffer definitions
- ✅ Code-generated client/server stubs
- ✅ HTTP/2 binary protocol
- ✅ Streaming support
- ✅ Low-latency communication

---

## 📊 Sample Data

All APIs automatically initialize with sample data:

### Users
- **Admin User**: `admin@aquaworld.com` / `admin123`
- **Regular User**: `user@example.com` / `password123`

### Products
- Premium Guppies (various colors)
- Aquatic Accessories
- Fish Food and Supplements

### Sample Orders & Payments
Pre-loaded with demo data for testing

---

## 📚 Documentation

### Main Documentation
| File | Purpose |
|------|---------|
| [REST API Guide](REST/README.md) | Complete REST API documentation |
| [GraphQL API Guide](GraphQL/README.md) | Complete GraphQL API documentation |
| [gRPC API Guide](gRPC/README.md) | Complete gRPC API documentation |

### Implementation Guides
| File | Purpose |
|------|---------|
| [REST Quick Start](REST/SIMPLE_REST_API_GUIDE.md) | REST setup and examples |
| [GraphQL Quick Start](GraphQL/QUICKSTART.md) | GraphQL setup and examples |
| [gRPC Setup Guide](gRPC/aquaworld-grpc-api/SETUP_GUIDE.md) | gRPC installation guide |
| [gRPC Usage Guide](gRPC/aquaworld-grpc-api/USAGE_GUIDE.md) | gRPC usage examples |

### Additional Resources
| File | Purpose |
|------|---------|
| [GraphQL Implementation Complete](GraphQL/IMPLEMENTATION_COMPLETE.md) | 40+ GraphQL query/mutation examples |
| [GraphQL Visual Guide](GraphQL/VISUAL_GUIDE.md) | Architecture diagrams |
| [gRPC Migration Plan](gRPC/GRPC_MIGRATION_PLAN.md) | Migration strategy from REST |

---

## 🔄 Comparison Guide

### Quick Comparison

| Feature | REST | GraphQL | gRPC |
|---------|------|---------|------|
| **Endpoint Type** | Multiple | Single | Service-based |
| **Protocol** | HTTP/1.1 | HTTP/1.1 | HTTP/2 |
| **Data Format** | JSON | JSON | Binary (Protobuf) |
| **Over-fetching** | ⚠️ Common | ✅ Eliminated | ✅ Eliminated |
| **Under-fetching** | ⚠️ Requires multiple calls | ✅ Single call | ✅ Single call |
| **Learning Curve** | ✅ Low | ⚠️ Medium | ⚠️ Medium |
| **Performance** | ⚠️ Good | ✅ Good | ✅ Excellent |
| **Bandwidth** | ⚠️ High | ✅ Optimized | ✅ Very optimized |
| **Real-time** | ⚠️ Polling/WebSocket | ✅ Subscriptions | ✅ Streaming |
| **Caching** | ✅ Easy (HTTP) | ⚠️ Complex | ✅ Via proxies |
| **Debugging** | ✅ Easy | ✅ Easy | ⚠️ Harder |
| **Best For** | Public APIs, CRUD | Complex queries, mobile | Microservices, performance |

### When to Use Each

**Choose REST if:**
- Building a public-facing API
- Endpoints are simple and well-defined
- Standard CRUD operations
- Team is unfamiliar with modern patterns
- Browser client is primary consumer

**Choose GraphQL if:**
- Multiple clients with different data needs
- Mobile app is a primary client (bandwidth matters)
- Complex data relationships and filtering needed
- Rapid frontend iteration important
- Team wants self-documenting API

**Choose gRPC if:**
- Building microservices architecture
- Performance is critical
- Backend-to-backend communication
- Real-time bidirectional streaming needed
- Need cross-language support

---

## 📋 Requirements

### System Requirements
- **OS**: macOS, Linux, or Windows
- **Java**: JDK 21 or higher
- **Maven**: 3.8.1 or higher
- **Memory**: 4GB RAM minimum
- **Disk**: 2GB for project and dependencies

### Optional Tools
- **cURL**: For testing REST/GraphQL endpoints
- **Postman**: For API testing
- **grpcurl**: For testing gRPC endpoints
- **Git**: For version control

### Development Setup
```bash
# Verify Java installation
java -version  # Should be 21+

# Verify Maven installation
mvn -version  # Should be 3.8.1+

# Clone or navigate to the project
cd /path/to/API_Architecture
```

---

## 🎓 Learning Path

If you're new to API development:

1. **Start with REST** - Most familiar pattern
   - Learn HTTP verbs and status codes
   - Understand CRUD operations
   - Read [REST documentation](REST/README.md)

2. **Explore GraphQL** - Modern query language
   - Understand strongly-typed schemas
   - Learn query composition
   - Read [GraphQL documentation](GraphQL/README.md)

3. **Discover gRPC** - High-performance RPC
   - Learn Protocol Buffers
   - Understand HTTP/2 benefits
   - Read [gRPC documentation](gRPC/README.md)

---

## 📞 Support & Resources

- **Project Structure**: See the `Folder Structure` section above
- **API Documentation**: Inline swagger/GraphiQL/proto definitions
- **Setup Issues**: Check relevant SETUP_GUIDE.md or QUICKSTART.md
- **Code Examples**: See IMPLEMENTATION_COMPLETE.md for GraphQL, or README files for others

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).

---

## 🎯 Summary

This repository demonstrates **professional API design** using three modern paradigms, all implementing the same AquaWorld business domain. Whether you're learning API development, comparing architectures, or building production systems, you'll find practical, well-structured examples here.

**Start exploring today by choosing your API style:**
- 🌐 [REST API](REST/aquaworld-api) - Traditional and reliable
- 📊 [GraphQL API](GraphQL/aquaworld-graphql-api) - Modern and flexible  
- ⚡ [gRPC API](gRPC/aquaworld-grpc-api) - Fast and efficient
