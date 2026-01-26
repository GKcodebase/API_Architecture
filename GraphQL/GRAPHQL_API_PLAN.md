# 🐠 AquaWorld GraphQL API - Implementation Plan

A comprehensive plan for building a GraphQL API for the AquaWorld pet store using Spring Boot 3, Java 21, and Spring GraphQL.

---

## 📋 Executive Summary

This document outlines the complete plan for converting the existing AquaWorld REST API to a GraphQL API while maintaining the same business logic, data models, and repositories.

**Scope**: Full GraphQL implementation with queries, mutations, subscriptions, authentication, and error handling for the AquaWorld guppy pet store.

---

## 🎯 Project Goals

1. **API Paradigm Shift**: Convert REST endpoints to GraphQL schema
2. **Code Reuse**: Leverage existing service layer and repositories
3. **Feature Parity**: Maintain all REST API functionality in GraphQL
4. **Type Safety**: Define comprehensive GraphQL schema with strict types
5. **Developer Experience**: Interactive GraphQL playground (GraphiQL/Sandbox)
6. **Performance**: Efficient data fetching with field selection
7. **Real-Time**: Support subscriptions for order and payment updates

---

## 📁 Project Structure

```
aquaworld-graphql-api/
├── pom.xml
├── src/main/
│   ├── java/com/aquaworld/graphql/
│   │   ├── GraphQLApplication.java              # Main class
│   │   ├── config/
│   │   │   ├── GraphQLConfig.java               # GraphQL configuration
│   │   │   ├── SecurityConfig.java              # JWT security setup
│   │   │   └── JwtAuthenticationFilter.java     # JWT filter
│   │   ├── graphql/
│   │   │   ├── schema/                          # GraphQL schema files
│   │   │   │   ├── schema.graphqls              # Root schema definition
│   │   │   │   ├── types.graphqls               # Type definitions
│   │   │   │   ├── queries.graphqls             # Query definitions
│   │   │   │   └── mutations.graphqls           # Mutation definitions
│   │   │   └── resolver/                        # GraphQL resolvers
│   │   │       ├── QueryResolver.java           # Root query resolver
│   │   │       ├── MutationResolver.java        # Root mutation resolver
│   │   │       ├── ProductResolver.java         # Product type resolver
│   │   │       ├── UserResolver.java            # User type resolver
│   │   │       ├── OrderResolver.java           # Order type resolver
│   │   │       └── PaymentResolver.java         # Payment type resolver
│   │   ├── controller/                          # (Optional) REST for testing
│   │   │   └── AuthController.java              # Auth endpoint
│   │   ├── service/                             # (Reused from REST)
│   │   │   ├── ProductService.java
│   │   │   ├── OrderService.java
│   │   │   ├── PaymentService.java
│   │   │   ├── AuthenticationService.java
│   │   │   └── UserService.java
│   │   ├── repository/                          # (Reused from REST)
│   │   │   ├── ProductRepository.java
│   │   │   ├── OrderRepository.java
│   │   │   ├── PaymentRepository.java
│   │   │   └── UserRepository.java
│   │   ├── model/                               # (Reused from REST)
│   │   │   ├── User.java
│   │   │   ├── Product.java
│   │   │   ├── Order.java
│   │   │   ├── OrderItem.java
│   │   │   └── Payment.java
│   │   ├── dto/                                 # GraphQL DTOs
│   │   │   ├── ProductDTO.java
│   │   │   ├── OrderDTO.java
│   │   │   ├── PaymentDTO.java
│   │   │   └── UserDTO.java
│   │   ├── exception/                           # (Reused from REST)
│   │   │   ├── ApiException.java
│   │   │   ├── ResourceNotFoundException.java
│   │   │   └── Custom exceptions
│   │   ├── util/
│   │   │   ├── JwtUtil.java                     # (Reused from REST)
│   │   │   └── Constants.java                   # (Reused from REST)
│   │   └── response/
│   │       └── GraphQLErrorResponse.java        # GraphQL error wrapper
│   └── resources/
│       └── application.properties
└── README.md
```

---

## 🏗️ Architecture Overview

### Comparison: REST vs GraphQL

| Aspect | REST | GraphQL |
|--------|------|---------|
| **Endpoint Structure** | Multiple endpoints (`/products`, `/orders`) | Single endpoint (`/graphql`) |
| **Data Fetching** | Fixed response structure | Client specifies fields |
| **Over-fetching** | Yes (extra fields) | No (exact fields requested) |
| **Under-fetching** | Yes (multiple requests) | No (single request) |
| **Method Semantics** | GET, POST, PUT, DELETE | Query, Mutation, Subscription |
| **Playground** | Requires Postman/cURL | Built-in (GraphiQL/Sandbox) |
| **Type Safety** | Manual validation | Strong schema typing |

### Layered Architecture (Reused from REST)

```
GraphQL Resolver Layer
        ↓
Service Layer (Business Logic)
        ↓
Repository Layer (Data Access)
        ↓
In-Memory Storage (HashMap)
```

---

## 📊 GraphQL Schema Design

### 1. Type Definitions

#### **User Type**
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

#### **Product Type**
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

type ProductConnection {
  items: [Product!]!
  totalCount: Int!
}
```

#### **Order Type**
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

#### **Payment Type**
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

### 2. Query Definitions

#### **Root Query**
```graphql
type Query {
  # Product Queries (Public)
  products(limit: Int, offset: Int): ProductConnection!
  product(id: ID!): Product
  searchProducts(name: String!): [Product!]!
  productsByCategory(category: String!): [Product!]!
  availableProducts: [Product!]!
  
  # User Queries (Protected)
  me: User
  user(username: String!): User
  
  # Order Queries (Protected)
  orders(limit: Int, offset: Int): [Order!]!
  order(id: ID!): Order
  orderByNumber(orderNumber: String!): Order
  
  # Payment Queries (Protected)
  payment(id: ID!): Payment
  paymentByOrder(orderId: ID!): Payment
}
```

### 3. Mutation Definitions

#### **Root Mutation**
```graphql
type Mutation {
  # Authentication (Public)
  register(input: RegisterInput!): AuthPayload!
  login(input: LoginInput!): AuthPayload!
  
  # Product Mutations (Admin only)
  createProduct(input: CreateProductInput!): Product!
  updateProduct(id: ID!, input: UpdateProductInput!): Product!
  deleteProduct(id: ID!): Boolean!
  
  # Order Mutations (Protected)
  createOrder(input: CreateOrderInput!): Order!
  updateOrderStatus(id: ID!, status: String!): Order!
  cancelOrder(id: ID!): Order!
  deleteOrder(id: ID!): Boolean!
  
  # Payment Mutations (Protected)
  processPayment(input: PaymentInput!): Payment!
  refundPayment(id: ID!): Payment!
  
  # User Mutations (Protected)
  updateProfile(input: UpdateUserInput!): User!
}
```

### 4. Input Types

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

input UpdateProductInput {
  name: String
  category: String
  description: String
  price: Float
  stock: Int
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

input UpdateUserInput {
  firstName: String
  lastName: String
  phone: String
  address: String
}
```

---

## 🔐 Authentication & Authorization

### JWT Integration

- **Token-based**: Same JWT mechanism as REST API
- **Duration**: 1 hour expiration
- **Header**: `Authorization: Bearer <token>`
- **GraphQL Context**: Token extracted and injected into GraphQL context

### Protected Queries & Mutations

| Resource | Public | Requires Auth |
|----------|--------|---------------|
| Products (GET) | ✅ | ❌ |
| Products (CREATE/UPDATE/DELETE) | ❌ | ✅ (Admin) |
| Orders | ❌ | ✅ |
| Payments | ❌ | ✅ |
| User Profile | ❌ | ✅ |

---

## 🔧 Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| **Framework** | Spring Boot | 3.3.4 |
| **Language** | Java | 21 |
| **GraphQL** | Spring GraphQL | 1.2.x |
| **GraphQL Schema** | GraphQL Java | 21.x |
| **JWT** | JJWT | 0.12.3 |
| **Security** | Spring Security | 6.x |
| **Validation** | Jakarta Validation | 3.x |
| **Build** | Maven | 3.8.1+ |
| **API Playground** | GraphiQL/Apollo Sandbox | Built-in |

---

## 📋 Implementation Phases

### Phase 1: Project Setup (1-2 hours)
- [x] Create Maven project structure
- [x] Add Spring Boot and GraphQL dependencies
- [x] Configure application.properties
- [x] Set up SecurityConfig and JwtAuthenticationFilter

### Phase 2: Reuse Existing Components (30 mins)
- [x] Copy models, repositories, services from REST API
- [x] Copy JWT utility and exception classes
- [x] Copy security configuration

### Phase 3: GraphQL Schema Definition (2-3 hours)
- [ ] Define type definitions (User, Product, Order, Payment)
- [ ] Define query schema
- [ ] Define mutation schema
- [ ] Define input types
- [ ] Create schema.graphqls files

### Phase 4: GraphQL Resolvers Implementation (3-4 hours)
- [ ] Implement QueryResolver
- [ ] Implement MutationResolver
- [ ] Implement field resolvers (ProductResolver, UserResolver, etc.)
- [ ] Wire dependency injection

### Phase 5: Authentication & Authorization (1-2 hours)
- [ ] Implement GraphQL context with JWT
- [ ] Create authorization directives
- [ ] Add role-based access control

### Phase 6: Error Handling (1 hour)
- [ ] Create GraphQL error handler
- [ ] Map domain exceptions to GraphQL errors
- [ ] Format error responses

### Phase 7: Testing & Documentation (2-3 hours)
- [ ] Test queries and mutations via GraphiQL
- [ ] Create comprehensive README
- [ ] Add usage examples
- [ ] Document schema

---

## 🔄 Key Conversions: REST → GraphQL

### Product Queries

**REST**:
```
GET /api/v1/products
GET /api/v1/products/2001
GET /api/v1/products/search?name=Guppy
GET /api/v1/products/category/guppies
GET /api/v1/products/stock/available
```

**GraphQL**:
```graphql
query {
  products { items { id name price stock } }
  product(id: 2001) { name description price }
  searchProducts(name: "Guppy") { id name price }
  productsByCategory(category: "guppies") { id name }
  availableProducts { id name stock }
}
```

### Order Management

**REST**:
```
GET /api/v1/orders
POST /api/v1/orders
PUT /api/v1/orders/3001/status
DELETE /api/v1/orders/3001
```

**GraphQL**:
```graphql
query {
  orders { id orderNumber status totalPrice }
  order(id: 3001) { orderNumber orderItems { product { name } } }
}

mutation {
  createOrder(input: { orderItems: [...] }) { id orderNumber status }
  updateOrderStatus(id: 3001, status: "CONFIRMED") { status }
  cancelOrder(id: 3001) { status }
  deleteOrder(id: 3001)
}
```

---

## 📊 Data Fetching Efficiency

### Example: Eliminate Over-fetching

**REST API** (Always returns all fields):
```json
GET /api/v1/products/2001
{
  "id": 2001,
  "name": "Red Guppy",
  "category": "guppies",
  "description": "...",
  "price": 5.99,
  "stock": 25,
  "imageUrl": "...",
  "createdAt": "2025-01-15T08:00:00"
}
```

**GraphQL** (Client specifies needed fields):
```graphql
query {
  product(id: 2001) {
    name
    price
    stock
  }
}
# Returns only: name, price, stock
```

### Example: Eliminate Under-fetching

**REST** (Get order with products - requires 2 requests):
```
1. GET /api/v1/orders/3001
2. GET /api/v1/products/2001 (for each order item)
```

**GraphQL** (Single request with nested data):
```graphql
query {
  order(id: 3001) {
    orderNumber
    totalPrice
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

---

## 🧪 Testing Strategy

### GraphiQL/Apollo Sandbox

Built-in interactive playground for testing:
```
http://localhost:8080/graphiql
```

### Example Test Queries

1. **Get all products** (Public)
2. **Search products** (Public)
3. **Login** (Public)
4. **Create order** (Protected)
5. **Update order status** (Protected)
6. **Process payment** (Protected)

---

## 🚀 Deployment Considerations

### Endpoints

| Environment | URL |
|-------------|-----|
| **Local Dev** | `http://localhost:8080/graphql` |
| **Production** | `/api/v1/graphql` |

### Caching Strategy

- Use HTTP caching headers for persistent queries
- Implement resolver-level caching
- Cache product catalog (frequently accessed, rarely changed)

### Rate Limiting

- Implement query complexity analysis
- Set maximum query depth
- Limit request size

---

## 📈 Performance Optimization

### N+1 Problem Prevention

- **DataLoader**: Batch loading for related entities
- **Field Selection**: Only fetch requested fields
- **Pagination**: Use limit/offset for large result sets

### Query Complexity

```
Maximum Query Depth: 10
Maximum Alias Count: 10
Maximum Directive Count: 10
```

---

## 🔄 Subscription Support (Future)

**Subscriptions** for real-time updates:

```graphql
subscription {
  orderStatusChanged(userId: "1001") {
    order {
      id
      status
      updatedAt
    }
  }
}

subscription {
  paymentProcessed(orderId: "3001") {
    payment {
      id
      status
      transactionId
    }
  }
}
```

---

## 📚 Deliverables

1. ✅ Implementation Plan (this document)
2. ⬜ GraphQL schema definition
3. ⬜ QueryResolver implementation
4. ⬜ MutationResolver implementation
5. ⬜ Type resolvers (Product, Order, User, Payment)
6. ⬜ Exception handling & error mapping
7. ⬜ Authentication & authorization
8. ⬜ Comprehensive README with examples
9. ⬜ API documentation (schema)
10. ⬜ Testing guide

---

## 🎯 Success Criteria

- ✅ All product queries working (public)
- ✅ All user mutations working (register, login)
- ✅ All order operations working (create, read, update, delete)
- ✅ All payment operations working
- ✅ JWT authentication enforced on protected queries/mutations
- ✅ GraphiQL playground accessible
- ✅ Error handling with meaningful messages
- ✅ Comprehensive documentation
- ✅ Example queries and mutations documented

---

## 📝 Notes

- **Code Reuse**: Leverage existing service and repository layers
- **Consistency**: Maintain same business logic as REST API
- **Schema Design**: Use best practices from GraphQL community
- **Type Safety**: Leverage GraphQL strong typing benefits
- **Developer Experience**: Interactive playground > API docs

---

**Next Step**: Begin Phase 1 - Project Setup ➡️
