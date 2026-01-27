# AquaWorld GraphQL API - Implementation Complete ✅

A full-featured GraphQL API for an aquatic pet store (AquaWorld) built with Spring Boot 3, featuring JWT authentication, product management, order processing, and payment handling.

## 🚀 Quick Start

### Prerequisites
- Java 21 or higher
- Maven 3.8.1 or higher
- Git

### Installation & Running

```bash
# Navigate to the project directory
cd /Users/gokulg.k/Documents/GitHub/API_Architecture/GraphQL/aquaworld-graphql-api

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080/aquaworld`

## 📊 Available Endpoints

### GraphQL Endpoint
- **Query & Mutation**: `http://localhost:8080/aquaworld/graphql`
- **Interactive Playground**: `http://localhost:8080/aquaworld/graphiql`

## 🔐 Authentication

The API uses JWT (JSON Web Token) for authentication.

### Getting a Token

1. **Register a new user:**
```graphql
mutation {
  register(input: {
    username: "newuser"
    email: "user@example.com"
    password: "password123"
    firstName: "John"
    lastName: "Doe"
    phone: "+1-555-0100"
  }) {
    token
    tokenType
    expiresIn
    loginAt
  }
}
```

2. **Login with existing user:**
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

3. **Using the Token:**
Add the token to the Authorization header in GraphiQL:
```
Authorization: Bearer <your_token_here>
```

## 📚 Sample Data

The application automatically initializes with sample data on startup:

### Users
- **Admin**: username: `admin`, password: `admin@123`
- **Customer 1**: username: `john`, password: `john@123`
- **Customer 2**: username: `jane`, password: `jane@123`

### Products
- Red Guppy - $5.99
- Blue Guppy - $6.49
- Fish Food Premium - $3.99
- 10L Aquarium Filter - $24.99
- Artificial Aqua Plant - $7.99
- Fish Medicine (Ich Cure) - $12.99

## 🎯 API Operations

### Query Examples

#### 1. Get All Products
```graphql
query {
  products(limit: 10, offset: 0) {
    id
    name
    category
    price
    stock
  }
}
```

#### 2. Get Single Product
```graphql
query {
  product(id: 1) {
    id
    name
    description
    price
    stock
    category
  }
}
```

#### 3. Search Products by Name
```graphql
query {
  searchProducts(name: "guppy") {
    id
    name
    price
    description
  }
}
```

#### 4. Get Products by Category
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

#### 5. Get Available Products (In Stock)
```graphql
query {
  availableProducts {
    id
    name
    stock
    price
  }
}
```

#### 6. Get Current User Profile (Requires Auth)
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

#### 7. Get User by Username
```graphql
query {
  user(username: "john") {
    id
    username
    email
    firstName
    lastName
    role
  }
}
```

#### 8. Get Current User's Orders (Requires Auth)
```graphql
query {
  orders(limit: 10, offset: 0) {
    id
    orderNumber
    status
    totalPrice
    createdAt
    user {
      username
      email
    }
    orderItems {
      quantity
      price
      product {
        name
        price
      }
    }
  }
}
```

#### 9. Get Specific Order (Requires Auth)
```graphql
query {
  order(id: 1) {
    id
    orderNumber
    status
    totalPrice
    createdAt
    user {
      username
    }
    orderItems {
      product {
        name
      }
      quantity
      price
    }
  }
}
```

### Mutation Examples

#### 1. Create Order (Requires Auth)
```graphql
mutation {
  createOrder(input: {
    orderItems: [
      { productId: 1, quantity: 2 }
      { productId: 3, quantity: 1 }
    ]
  }) {
    id
    orderNumber
    totalPrice
    status
    createdAt
    orderItems {
      product {
        name
      }
      quantity
      price
    }
  }
}
```

#### 2. Update Order Status (Requires Auth/Admin)
```graphql
mutation {
  updateOrderStatus(id: 1, status: "CONFIRMED") {
    id
    status
    updatedAt
  }
}
```

#### 3. Cancel Order (Requires Auth)
```graphql
mutation {
  cancelOrder(id: 1) {
    id
    status
    updatedAt
  }
}
```

#### 4. Process Payment (Requires Auth)
```graphql
mutation {
  processPayment(input: {
    orderId: 1
    amount: 29.98
    paymentMethod: "CREDIT_CARD"
  }) {
    id
    status
    transactionId
    amount
    paymentMethod
    createdAt
  }
}
```

#### 5. Refund Payment (Requires Auth)
```graphql
mutation {
  refundPayment(id: 1) {
    id
    status
    amount
    updatedAt
  }
}
```

#### 6. Create Product (Admin Only)
```graphql
mutation {
  createProduct(input: {
    name: "Yellow Guppy"
    category: "guppies"
    description: "Vibrant yellow male guppy"
    price: 7.99
    stock: 25
    imageUrl: "https://example.com/yellow-guppy.jpg"
  }) {
    id
    name
    price
    stock
    createdAt
  }
}
```

#### 7. Update Product (Admin Only)
```graphql
mutation {
  updateProduct(id: 1, input: {
    price: 6.99
    stock: 40
  }) {
    id
    name
    price
    stock
    updatedAt
  }
}
```

#### 8. Delete Product (Admin Only)
```graphql
mutation {
  deleteProduct(id: 1)
}
```

#### 9. Update User Profile (Requires Auth)
```graphql
mutation {
  updateProfile(input: {
    firstName: "John"
    lastName: "Smith"
    phone: "+1-555-0105"
    address: "789 Fin Street, Fish City, FC 12347"
  }) {
    id
    firstName
    lastName
    phone
    address
  }
}
```

## 🏗️ Project Structure

```
aquaworld-graphql-api/
├── src/main/java/com/aquaworld/graphql/
│   ├── config/
│   │   ├── DataInitializer.java         (Sample data initialization)
│   │   ├── GraphQLConfig.java           (GraphQL configuration)
│   │   └── SecurityConfig.java          (Spring Security setup)
│   ├── dto/
│   │   ├── AuthPayload.java
│   │   ├── CreateOrderInput.java
│   │   ├── CreateProductInput.java
│   │   ├── LoginInput.java
│   │   ├── OrderItemInput.java
│   │   ├── PaymentInput.java
│   │   ├── RegisterInput.java
│   │   └── UpdateProductInput.java
│   ├── exception/
│   │   ├── DuplicateResourceException.java
│   │   ├── GraphQLExceptionHandler.java
│   │   ├── InvalidOperationException.java
│   │   └── ResourceNotFoundException.java
│   ├── model/
│   │   ├── Order.java
│   │   ├── OrderItem.java
│   │   ├── Payment.java
│   │   ├── Product.java
│   │   └── User.java
│   ├── repository/
│   │   ├── OrderRepository.java
│   │   ├── PaymentRepository.java
│   │   ├── ProductRepository.java
│   │   └── UserRepository.java
│   ├── resolver/
│   │   ├── MutationResolver.java        (GraphQL mutations)
│   │   ├── OrderItemResolver.java       (Field resolver)
│   │   ├── OrderResolver.java           (Field resolver)
│   │   ├── PaymentResolver.java         (Field resolver)
│   │   └── QueryResolver.java           (GraphQL queries)
│   ├── security/
│   │   ├── CustomUserDetailsService.java
│   │   ├── JwtAuthenticationFilter.java
│   │   └── JwtTokenProvider.java
│   ├── service/
│   │   ├── AuthenticationService.java
│   │   ├── OrderService.java
│   │   ├── PaymentService.java
│   │   ├── ProductService.java
│   │   └── UserService.java
│   └── GraphQLApplication.java          (Main entry point)
├── src/main/resources/
│   ├── graphql/
│   │   └── schema.graphqls              (GraphQL schema definition)
│   └── application.properties           (Application configuration)
└── pom.xml                              (Maven dependencies)
```

## 📋 GraphQL Schema Overview

### Root Types
- **Query**: 14 read operations (product search, orders, payments, user profile)
- **Mutation**: 12 write operations (authentication, CRUD, payments)
- **Types**: User, Product, Order, OrderItem, Payment, AuthPayload
- **Input Types**: RegisterInput, LoginInput, CreateProductInput, CreateOrderInput, PaymentInput, etc.

### Available Queries
1. `products` - Get all products with pagination
2. `product` - Get single product by ID
3. `searchProducts` - Search products by name
4. `productsByCategory` - Filter products by category
5. `availableProducts` - Get products in stock
6. `me` - Get current user profile (requires auth)
7. `user` - Get user by username
8. `orders` - Get current user's orders (requires auth)
9. `order` - Get specific order (requires auth)
10. `orderByNumber` - Get order by order number (requires auth)
11. `payment` - Get payment by ID (requires auth)
12. `paymentByOrder` - Get payment for order (requires auth)

### Available Mutations
1. `register` - Register new user
2. `login` - Login and get JWT token
3. `createProduct` - Create new product (admin)
4. `updateProduct` - Update product (admin)
5. `deleteProduct` - Delete product (admin)
6. `createOrder` - Create new order (requires auth)
7. `updateOrderStatus` - Update order status (requires auth)
8. `cancelOrder` - Cancel order (requires auth)
9. `deleteOrder` - Delete order (requires auth)
10. `processPayment` - Process payment (requires auth)
11. `refundPayment` - Refund payment (requires auth)
12. `updateProfile` - Update user profile (requires auth)

## 🔒 Security

- **Authentication**: JWT-based stateless authentication
- **Authorization**: Role-based access control (CUSTOMER, ADMIN)
- **Password**: BCrypt encoding
- **Token Expiration**: 1 hour (configurable)
- **CORS**: Enabled for localhost and configurable origins

## 🛠️ Technologies Used

| Technology | Version | Purpose |
|-----------|---------|---------|
| Spring Boot | 3.3.4 | Framework |
| Spring GraphQL | 1.2.x | GraphQL support |
| Spring Security | 6.x | Authentication/Authorization |
| Java | 21 | Language |
| Maven | 3.8.1+ | Build tool |
| JJWT | 0.12.3 | JWT handling |
| Lombok | Latest | Code generation |
| Jakarta Validation | Latest | Input validation |

## 🧪 Testing the API

### Via GraphiQL Playground
1. Open `http://localhost:8080/aquaworld/graphiql`
2. Login using the sample credentials to get JWT token
3. Add Authorization header: `Authorization: Bearer <token>`
4. Execute queries and mutations

### Via cURL

```bash
# Example: Query all products
curl -X POST http://localhost:8080/aquaworld/graphql \
  -H "Content-Type: application/json" \
  -d '{
    "query": "{ products { id name price } }"
  }'

# Example: Login mutation
curl -X POST http://localhost:8080/aquaworld/graphql \
  -H "Content-Type: application/json" \
  -d '{
    "query": "mutation { login(input: {username: \"john\", password: \"john@123\"}) { token } }"
  }'
```

## 📈 Performance Features

- **Schema-First Design**: Eliminates over/under-fetching
- **Field Resolvers**: Handles nested data efficiently
- **In-Memory Storage**: Fast data access for demo/testing
- **JWT Caching**: Reduces authentication overhead
- **Selective Fetching**: Clients request only needed fields

## 🐛 Error Handling

The API returns standardized GraphQL errors with:
- **Code**: Error code (e.g., NOT_FOUND, UNAUTHORIZED)
- **StatusCode**: HTTP status code (404, 401, 400, etc.)
- **Message**: Human-readable error message
- **Path**: GraphQL query path where error occurred

### Error Codes
- `NOT_FOUND` (404): Resource not found
- `DUPLICATE_RESOURCE` (409): Resource already exists
- `INVALID_OPERATION` (400): Invalid operation
- `UNAUTHORIZED` (401): Authentication/authorization failed
- `INTERNAL_SERVER_ERROR` (500): Unexpected error

## 📝 Configuration

Edit `application.properties` to customize:

```properties
# Server
server.port=8080
server.servlet.context-path=/aquaworld

# JWT
app.jwt.secret=<your-secret-key>
app.jwt.expiration=3600000

# Logging
logging.level.com.aquaworld=DEBUG
```

## 🔄 Version History

- **v1.0.0** (Current): Initial release with full GraphQL API

## 👥 Sample User Accounts

| Username | Password | Role |
|----------|----------|------|
| admin | admin@123 | ADMIN |
| john | john@123 | CUSTOMER |
| jane | jane@123 | CUSTOMER |

## 🤝 Contributing

This is a demonstration project. Feel free to fork and customize as needed.

## 📄 License

This project is part of the API Architecture demonstration series.

## 🚀 Next Steps

1. Connect to a real database (PostgreSQL, MongoDB)
2. Add file upload for product images
3. Implement real payment gateway integration
4. Add subscription support for GraphQL
5. Implement batch loading to optimize N+1 queries
6. Add comprehensive unit and integration tests
7. Deploy to cloud platform (AWS, GCP, Azure)

---

**Happy GraphQL API Building! 🐠**
