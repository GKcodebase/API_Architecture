# 🐠 AquaWorld GraphQL API

A comprehensive GraphQL API for the AquaWorld online pet store specializing in premium guppies and aquatic accessories.

**Same business logic as REST API, but with GraphQL's powerful query capabilities, strongly typed schema, and efficient data fetching.**

---

## 📖 Table of Contents

1. [What is GraphQL?](#what-is-graphql)
2. [REST vs GraphQL](#rest-vs-graphql)
3. [Project Setup](#project-setup)
4. [Running the Application](#running-the-application)
5. [GraphQL Schema](#graphql-schema)
6. [Query Examples](#query-examples)
7. [Mutation Examples](#mutation-examples)
8. [Authentication](#authentication)
9. [Error Handling](#error-handling)
10. [GraphiQL Playground](#graphiql-playground)
11. [Architecture](#architecture)
12. [Tech Stack](#tech-stack)

---

## 🌐 What is GraphQL?

**GraphQL** (Graph Query Language) is a query language and runtime for APIs that allows clients to request exactly the data they need.

### Key Characteristics

| Feature | Description |
|---------|-------------|
| **Strongly Typed** | Complete type system defined in schema |
| **Declarative** | Client specifies what data is needed |
| **Single Endpoint** | All requests go to `/graphql` |
| **Efficient** | No over-fetching or under-fetching |
| **Self-Documenting** | Schema is the API documentation |
| **Developer-Friendly** | Built-in playground (GraphiQL) |

---

## 🔄 REST vs GraphQL

### AquaWorld Endpoint Comparison

| Operation | REST | GraphQL |
|-----------|------|---------|
| **Get all products** | `GET /api/v1/products` | `query { products { ... } }` |
| **Get single product** | `GET /api/v1/products/2001` | `query { product(id: 2001) { ... } }` |
| **Create order** | `POST /api/v1/orders` | `mutation { createOrder(input: {...}) { ... } }` |
| **Get order with items** | 2 requests | 1 request with nested data |
| **Filter by name** | `GET /api/v1/products/search?name=...` | `query { searchProducts(name: "...") { ... } }` |

### Data Fetching Comparison

**REST**: Fixed response structure (over-fetching)
```json
GET /api/v1/products/2001
{
  "id": 2001,
  "name": "Red Guppy Male",
  "category": "guppies",
  "description": "...",
  "price": 5.99,
  "stock": 25,
  "imageUrl": "...",
  "createdAt": "2025-01-15"
}
```

**GraphQL**: Client specifies needed fields
```graphql
query {
  product(id: 2001) {
    name
    price
    stock
  }
}
# Returns ONLY: name, price, stock
```

---

## 🔧 Project Setup

### Prerequisites

- Java 21 or latest LTS
- Maven 3.8.1+
- Git

### Dependencies

```xml
<dependencies>
    <!-- Spring Boot -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- Spring GraphQL -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-graphql</artifactId>
    </dependency>
    
    <!-- Spring Security -->
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
    
    <!-- Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    
    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

### Configuration

**application.properties**:
```properties
spring.application.name=aquaworld-graphql-api
server.port=8080
server.servlet.context-path=/aquaworld

# GraphQL Configuration
spring.graphql.graphiql.enabled=true
spring.graphql.graphiql.path=/graphiql

# JWT Configuration
jwt.secret=your-secret-key-min-32-characters-long-for-HS256
jwt.expiration=3600000

# Logging
logging.level.root=INFO
logging.level.com.aquaworld=DEBUG
```

---

## ▶️ Running the Application

### Build Project
```bash
mvn clean install
```

### Run Application
```bash
mvn spring-boot:run
```

### Server Information

- **Base URL**: `http://localhost:8080/aquaworld`
- **GraphQL Endpoint**: `/aquaworld/graphql`
- **GraphiQL Playground**: `http://localhost:8080/aquaworld/graphiql`

---

## 📊 GraphQL Schema

### Type Definitions

#### Product Type
```graphql
type Product {
  id: ID!
  name: String!
  category: String!
  description: String!
  price: Float!
  stock: Int!
  imageUrl: String
  createdAt: String!
  updatedAt: String
}
```

#### User Type
```graphql
type User {
  id: ID!
  username: String!
  email: String!
  firstName: String!
  lastName: String!
  phone: String
  address: String
  role: String!
  createdAt: String!
  lastLogin: String
  active: Boolean!
}

type AuthPayload {
  id: ID!
  username: String!
  email: String!
  token: String!
  tokenType: String!
  expiresIn: Int!
  loginAt: String!
}
```

#### Order Type
```graphql
type Order {
  id: ID!
  orderNumber: String!
  user: User!
  orderItems: [OrderItem!]!
  totalPrice: Float!
  status: String!
  createdAt: String!
  updatedAt: String
}

type OrderItem {
  id: ID!
  product: Product!
  quantity: Int!
  price: Float!
}
```

#### Payment Type
```graphql
type Payment {
  id: ID!
  order: Order!
  amount: Float!
  status: String!
  paymentMethod: String!
  transactionId: String!
  createdAt: String!
  updatedAt: String
}
```

### Input Types

```graphql
input RegisterInput {
  username: String!
  email: String!
  password: String!
  firstName: String!
  lastName: String!
  phone: String
}

input LoginInput {
  username: String!
  password: String!
}

input CreateProductInput {
  name: String!
  category: String!
  description: String!
  price: Float!
  stock: Int!
  imageUrl: String
}

input CreateOrderInput {
  orderItems: [OrderItemInput!]!
}

input OrderItemInput {
  productId: ID!
  quantity: Int!
}

input PaymentInput {
  orderId: ID!
  amount: Float!
  paymentMethod: String!
}
```

---

## 🔍 Query Examples

### 1. Get All Products

**Request**:
```graphql
query {
  products {
    id
    name
    category
    price
    stock
  }
}
```

**Response**:
```json
{
  "data": {
    "products": [
      {
        "id": "2001",
        "name": "Red Guppy Male - Premium",
        "category": "guppies",
        "price": 5.99,
        "stock": 25
      },
      {
        "id": "2002",
        "name": "Blue Guppy Male",
        "category": "guppies",
        "price": 6.49,
        "stock": 18
      }
    ]
  }
}
```

### 2. Get Single Product with Full Details

**Request**:
```graphql
query {
  product(id: "2001") {
    id
    name
    category
    description
    price
    stock
    imageUrl
    createdAt
  }
}
```

**Response**:
```json
{
  "data": {
    "product": {
      "id": "2001",
      "name": "Red Guppy Male - Premium",
      "category": "guppies",
      "description": "Beautiful red male guppy with full tail fin",
      "price": 5.99,
      "stock": 25,
      "imageUrl": "https://aquaworld.com/red-guppy.jpg",
      "createdAt": "2025-01-15T08:00:00"
    }
  }
}
```

### 3. Search Products by Name

**Request**:
```graphql
query {
  searchProducts(name: "Guppy") {
    id
    name
    price
  }
}
```

### 4. Get Products by Category

**Request**:
```graphql
query {
  productsByCategory(category: "guppies") {
    id
    name
    price
    stock
  }
}
```

### 5. Get Available Products (In Stock)

**Request**:
```graphql
query {
  availableProducts {
    id
    name
    price
    stock
  }
}
```

### 6. Get User Orders with Details

**Request** (Requires Authentication):
```graphql
query {
  orders {
    id
    orderNumber
    status
    totalPrice
    createdAt
    orderItems {
      quantity
      price
      product {
        name
        category
      }
    }
  }
}
```

**Response**:
```json
{
  "data": {
    "orders": [
      {
        "id": "3001",
        "orderNumber": "ORD-20250119-001",
        "status": "PENDING",
        "totalPrice": 27.96,
        "createdAt": "2025-01-19T10:30:00",
        "orderItems": [
          {
            "quantity": 2,
            "price": 5.99,
            "product": {
              "name": "Red Guppy Male - Premium",
              "category": "guppies"
            }
          }
        ]
      }
    ]
  }
}
```

### 7. Get Single Order

**Request**:
```graphql
query {
  order(id: "3001") {
    id
    orderNumber
    status
    totalPrice
    user {
      username
      email
    }
    orderItems {
      quantity
      product {
        name
        price
      }
    }
  }
}
```

### 8. Get Payment Information

**Request**:
```graphql
query {
  payment(id: "5001") {
    id
    amount
    status
    paymentMethod
    transactionId
    createdAt
    order {
      orderNumber
      totalPrice
    }
  }
}
```

### 9. Get Current User Profile

**Request** (Requires Authentication):
```graphql
query {
  me {
    id
    username
    email
    firstName
    lastName
    phone
    address
    role
  }
}
```

---

## ✏️ Mutation Examples

### 1. User Registration

**Request**:
```graphql
mutation {
  register(input: {
    username: "johndoe"
    email: "john@example.com"
    password: "SecurePass123"
    firstName: "John"
    lastName: "Doe"
    phone: "555-1234"
  }) {
    id
    username
    email
    firstName
    lastName
  }
}
```

**Response**:
```json
{
  "data": {
    "register": {
      "id": "1003",
      "username": "johndoe",
      "email": "john@example.com",
      "firstName": "John",
      "lastName": "Doe"
    }
  }
}
```

### 2. User Login

**Request**:
```graphql
mutation {
  login(input: {
    username: "john"
    password: "john@123"
  }) {
    id
    username
    email
    token
    tokenType
    expiresIn
    loginAt
  }
}
```

**Response**:
```json
{
  "data": {
    "login": {
      "id": "1000",
      "username": "john",
      "email": "john@aquaworld.com",
      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "tokenType": "Bearer",
      "expiresIn": 3600,
      "loginAt": "2025-01-19T10:35:00"
    }
  }
}
```

### 3. Create Order

**Request** (Requires Authentication):
```graphql
mutation {
  createOrder(input: {
    orderItems: [
      { productId: "2001", quantity: 2 }
      { productId: "2003", quantity: 1 }
    ]
  }) {
    id
    orderNumber
    status
    totalPrice
    createdAt
    orderItems {
      product {
        name
        price
      }
      quantity
    }
  }
}
```

**Response**:
```json
{
  "data": {
    "createOrder": {
      "id": "3003",
      "orderNumber": "ORD-20250119-002",
      "status": "PENDING",
      "totalPrice": 27.96,
      "createdAt": "2025-01-19T10:40:00",
      "orderItems": [
        {
          "product": {
            "name": "Red Guppy Male - Premium",
            "price": 5.99
          },
          "quantity": 2
        },
        {
          "product": {
            "name": "Premium Guppy Food",
            "price": 8.99
          },
          "quantity": 1
        }
      ]
    }
  }
}
```

### 4. Update Order Status

**Request**:
```graphql
mutation {
  updateOrderStatus(id: "3001", status: "CONFIRMED") {
    id
    orderNumber
    status
    updatedAt
  }
}
```

### 5. Cancel Order

**Request**:
```graphql
mutation {
  cancelOrder(id: "3001") {
    id
    status
    updatedAt
  }
}
```

### 6. Process Payment

**Request**:
```graphql
mutation {
  processPayment(input: {
    orderId: "3001"
    amount: 27.96
    paymentMethod: "CREDIT_CARD"
  }) {
    id
    amount
    status
    paymentMethod
    transactionId
    createdAt
  }
}
```

**Response**:
```json
{
  "data": {
    "processPayment": {
      "id": "5001",
      "amount": 27.96,
      "status": "SUCCESS",
      "paymentMethod": "CREDIT_CARD",
      "transactionId": "TXN-550e8400-e29b-41d4-a716",
      "createdAt": "2025-01-19T10:45:00"
    }
  }
}
```

### 7. Refund Payment

**Request**:
```graphql
mutation {
  refundPayment(id: "5001") {
    id
    amount
    status
    updatedAt
  }
}
```

### 8. Update User Profile

**Request**:
```graphql
mutation {
  updateProfile(input: {
    firstName: "John"
    lastName: "Doe Updated"
    phone: "555-9999"
    address: "123 Fish St, Water City"
  }) {
    id
    firstName
    lastName
    phone
    address
  }
}
```

### 9. Create Product (Admin Only)

**Request**:
```graphql
mutation {
  createProduct(input: {
    name: "Yellow Guppy Pair"
    category: "guppies"
    description: "Beautiful yellow guppies - mated pair"
    price: 9.99
    stock: 12
    imageUrl: "https://aquaworld.com/yellow-guppies.jpg"
  }) {
    id
    name
    category
    price
    stock
    createdAt
  }
}
```

---

## 🔐 Authentication

### How to Authenticate

1. **Obtain Token**: Use `login` mutation
   ```graphql
   mutation {
     login(input: { username: "john", password: "john@123" }) {
       token
     }
   }
   ```

2. **Send Token**: Include in Authorization header
   ```
   Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
   ```

### Using GraphiQL with Authentication

1. Open `http://localhost:8080/aquaworld/graphiql`
2. Click **HTTP Headers** (bottom left)
3. Add header:
   ```json
   {
     "Authorization": "Bearer <your-token>"
   }
   ```

### Protected Queries & Mutations

| Resource | Public | Requires Auth |
|----------|--------|---------------|
| Products (queries) | ✅ | ❌ |
| Products (mutations) | ❌ | ✅ (Admin) |
| User auth (register, login) | ✅ | ❌ |
| Orders | ❌ | ✅ |
| Payments | ❌ | ✅ |
| User profile | ❌ | ✅ |

---

## ⚠️ Error Handling

### GraphQL Error Response Format

```json
{
  "errors": [
    {
      "message": "Product not found",
      "extensions": {
        "code": "NOT_FOUND",
        "statusCode": 404,
        "path": ["product"]
      }
    }
  ]
}
```

### Common Error Codes

| Code | Status | Description |
|------|--------|-------------|
| **NOT_FOUND** | 404 | Resource doesn't exist |
| **UNAUTHORIZED** | 401 | Missing/invalid authentication |
| **FORBIDDEN** | 403 | Insufficient permissions |
| **BAD_REQUEST** | 400 | Invalid input |
| **CONFLICT** | 409 | Resource already exists |
| **INTERNAL_ERROR** | 500 | Server error |

### Example Error Response

**Request**:
```graphql
query {
  product(id: "9999") {
    name
    price
  }
}
```

**Response** (404 Not Found):
```json
{
  "errors": [
    {
      "message": "Product with ID 9999 not found",
      "extensions": {
        "code": "NOT_FOUND",
        "statusCode": 404,
        "path": ["product"]
      }
    }
  ]
}
```

---

## 🎮 GraphiQL Playground

### Access

```
http://localhost:8080/aquaworld/graphiql
```

### Features

- ✅ Interactive query editor
- ✅ Real-time syntax highlighting
- ✅ Auto-completion and suggestions
- ✅ Built-in documentation
- ✅ Query history
- ✅ Variable support

### Example Usage

1. Open GraphiQL
2. Write query in left panel:
   ```graphql
   query {
     products {
       id
       name
       price
     }
   }
   ```
3. Press **Ctrl+Enter** or click play button
4. View results in right panel

---

## 🏗️ Architecture

### Layered Design

```
GraphQL Resolvers (Query, Mutation)
            ↓
Service Layer (Business Logic)
            ↓
Repository Layer (Data Access)
            ↓
In-Memory Storage (HashMap/ConcurrentHashMap)
```

### Component Interaction

```
GraphQL Request
    ↓
QueryResolver / MutationResolver
    ↓
Service Layer (ProductService, OrderService, etc.)
    ↓
Repository Layer (ProductRepository, OrderRepository, etc.)
    ↓
In-Memory Storage
    ↓
Domain Models (Product, Order, User, Payment)
    ↓
Response back through resolvers
    ↓
GraphQL Response
```

### Code Reuse

| Component | Source | Status |
|-----------|--------|--------|
| Service Layer | REST API | ✅ Reused |
| Repositories | REST API | ✅ Reused |
| Models | REST API | ✅ Reused |
| JWT Utility | REST API | ✅ Reused |
| Exceptions | REST API | ✅ Reused |
| Security Config | REST API | ✅ Adapted |

---

## 🛠️ Tech Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| **Language** | Java | 21 |
| **Framework** | Spring Boot | 3.3.4 |
| **GraphQL** | Spring GraphQL | 1.2.x |
| **Security** | Spring Security | 6.x |
| **Authentication** | JWT (JJWT) | 0.12.3 |
| **Validation** | Jakarta Validation | 3.x |
| **Build Tool** | Maven | 3.8.1+ |
| **Data Storage** | In-Memory (HashMap) | - |
| **Playground** | GraphiQL | Built-in |

---

## 📚 Sample Data

The application auto-initializes with AquaWorld products on startup.

### Pre-loaded Products

| ID | Name | Category | Price | Stock |
|----|------|----------|-------|-------|
| 2001 | Red Guppy Male - Premium | guppies | $5.99 | 25 |
| 2002 | Blue Guppy Male | guppies | $6.49 | 18 |
| 2003 | Premium Guppy Food | fish_food | $8.99 | 50 |
| 2004 | 10 Gallon Tank | equipment | $49.99 | 15 |

### Pre-loaded Users

| Username | Password | Role | Email |
|----------|----------|------|-------|
| john | john@123 | CUSTOMER | john@aquaworld.com |
| admin | admin@123 | ADMIN | admin@aquaworld.com |
| jane | jane@123 | CUSTOMER | jane@aquaworld.com |

---

## 🚀 Performance Benefits

### GraphQL Advantages

1. **No Over-fetching**: Get only needed fields
2. **No Under-fetching**: Get related data in single request
3. **Strongly Typed**: Compile-time error detection
4. **Self-Documenting**: Schema is API documentation
5. **Developer Experience**: Interactive playground
6. **Efficient**: Reduced bandwidth usage
7. **Caching**: Better HTTP caching potential

### Example: Network Efficiency

**REST API** - 3 requests:
```
GET /api/v1/orders/3001         (600 bytes)
GET /api/v1/products/2001       (500 bytes)
GET /api/v1/products/2003       (500 bytes)
Total: 1600 bytes
```

**GraphQL** - 1 request:
```
POST /graphql
query { order(id: "3001") { ... } }
Total: 400 bytes + response
```

---

## 📖 Documentation

### Schema Introspection

View complete schema in GraphiQL:
1. Open GraphiQL
2. Click **DOCS** (right panel)
3. Browse all types and operations

### Full Implementation Plan

See [GRAPHQL_API_PLAN.md](./GRAPHQL_API_PLAN.md) for:
- Complete architecture design
- Phase-by-phase implementation plan
- Detailed schema specifications
- Conversion guide from REST

---

## 🔗 Related Projects

- [REST API Documentation](../REST/README.md)
- [REST API Simple Guide](../REST/SIMPLE_REST_API_GUIDE.md)

---

**Welcome to AquaWorld GraphQL API! 🐠**

**Version**: 1.0.0  
**Last Updated**: January 2026  
**Author**: AquaWorld Development Team

