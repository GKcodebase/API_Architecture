# � Creating a Simple REST API with Spring Boot 3
## Real-World Example: AquaWorld Pet Store

A practical guide to building REST APIs in Spring Boot 3 using the AquaWorld project as a real-world example.

---


## 📖 Table of Contents

1. [What is REST?](#what-is-rest)
2. [Project Setup](#project-setup)
3. [Creating the Entity](#creating-the-entity)
4. [Building the Controller](#building-the-controller)
5. [API Endpoints Explained](#api-endpoints-explained)
6. [In-Memory Storage](#in-memory-storage)
7. [Testing the API](#testing-the-api)
8. [Complete Code Example](#complete-code-example)

---

## 🌐 What is REST?

**REST** (Representational State Transfer) is an architectural style for building web APIs using HTTP protocol.

### REST Principles

| Principle | Description |
|-----------|-------------|
| **Client-Server** | Client and server are separate entities |
| **Stateless** | Each request contains all information needed |
| **Cacheable** | Responses can be cached for efficiency |
| **Uniform Interface** | Consistent API design |
| **Resource-Based** | APIs expose resources (nouns), not actions (verbs) |

### HTTP Methods in REST

| Method | Purpose | Status Codes |
|--------|---------|--------------|
| **GET** | Retrieve data (safe, idempotent) | 200, 404 |
| **POST** | Create new resource | 201, 400 |
| **PUT** | Replace entire resource | 200, 400, 404 |
| **PATCH** | Update part of resource | 200, 400, 404 |
| **DELETE** | Remove resource | 200, 404 |

---

## 🔧 Project Setup

### 1. Create Spring Boot Project

**Using Spring Initializr** (https://start.spring.io/):

```
Project: Maven
Language: Java
Spring Boot: 3.3.x (Latest)
Java: 21

Dependencies:
  - Spring Web (spring-boot-starter-web)
  - Spring Security (spring-boot-starter-security)
  - Lombok (optional, for reducing boilerplate)
  - JJWT (for JWT authentication)
  - Springdoc OpenAPI (for Swagger documentation)
```

### 2. Maven Dependencies (pom.xml)

```xml
<dependencies>
    <!-- Spring Web for REST APIs -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- Spring Security for authentication -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    
    <!-- JWT for token-based auth -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.12.3</version>
    </dependency>
    
    <!-- Lombok for @Data annotation -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

### 3. Application Properties

Create `src/main/resources/application.properties`:

```properties
spring.application.name=aquaworld-api
server.port=8080
server.servlet.context-path=/aquaworld

# JWT Configuration
jwt.secret=your-secret-key-min-32-characters-long-for-HS256
jwt.expiration=3600000

# Logging
logging.level.root=INFO
logging.level.com.aquaworld=DEBUG
```

---

## � Creating the Entity

The **Product** entity represents aquatic products in the AquaWorld store.

### Product Entity Class

**File**: `src/main/java/com/aquaworld/model/Product.java`

```java
package com.aquaworld.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Product entity representing an aquatic product in AquaWorld store.
 * 
 * Fields:
 * - id: Unique identifier
 * - name: Product name (e.g., "Red Guppy Male")
 * - category: Product category (guppies, fish_food, equipment, etc.)
 * - description: Product description
 * - price: Product price (BigDecimal for accurate currency)
 * - stock: Available quantity
 * - imageUrl: Product image URL
 * - createdAt: Creation timestamp
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    /**
     * Unique identifier for the product
     */
    private Long id;
    
    /**
     * Product name
     */
    private String name;
    
    /**
     * Product category
     */
    private String category;
    
    /**
     * Product description
     */
    private String description;
    
    /**
     * Product price
     */
    private BigDecimal price;
    
    /**
     * Stock quantity
     */
    private Integer stock;
    
    /**
     * Product image URL
     */
    private String imageUrl;
    
    /**
     * Creation timestamp
     */
    private LocalDateTime createdAt;
    
    /**
     * Constructor without ID (for creation)
     */
    public Product(String name, String category, String description, 
                   BigDecimal price, Integer stock, String imageUrl) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.imageUrl = imageUrl;
        this.createdAt = LocalDateTime.now();
    }
}
```

**What is `@Data`?**
- Lombok annotation that automatically generates:
  - Getters and setters for all fields
  - `toString()` method
  - `equals()` and `hashCode()` methods
  - Constructor with all fields

**What is `@NoArgsConstructor`?**
- Generates a constructor with no arguments (default constructor)

**What is `@AllArgsConstructor`?**
- Generates a constructor with all fields as arguments

---

## 🎮 Building the Controller

The **Controller** handles HTTP requests for product operations.

### ProductController Class

**File**: `src/main/java/com/aquaworld/controller/ProductController.java`

```java
package com.aquaworld.controller;

import com.aquaworld.model.Product;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * REST Controller for Product API (Public endpoints - no authentication required)
 * 
 * Handles HTTP requests for product operations:
 * GET    /api/v1/products           - List all products
 * GET    /api/v1/products/{id}      - Get product by ID
 * POST   /api/v1/products           - Create new product
 * PUT    /api/v1/products/{id}      - Update product
 * DELETE /api/v1/products/{id}      - Delete product
 */
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    
    /**
     * In-memory storage for products using HashMap
     * Key: Product ID (Long)
     * Value: Product object
     */
    private static final Map<Long, Product> productStore = new HashMap<>();
    
    /**
     * Atomic counter for generating unique product IDs
     * Thread-safe auto-increment
     */
    private static final AtomicLong idCounter = new AtomicLong(2000);
    
    /**
     * Initialize with AquaWorld sample products
     */
    static {
        // Guppies
        productStore.put(2001L, new Product(2001L, "Red Guppy Male - Premium", "guppies",
            "Beautiful red male guppy with full tail fin", new BigDecimal("5.99"), 
            25, "https://aquaworld.com/red-guppy.jpg", LocalDateTime.now().minusDays(5)));
        
        productStore.put(2002L, new Product(2002L, "Blue Guppy Male", "guppies",
            "Vibrant blue colored male guppy", new BigDecimal("6.49"), 
            18, "https://aquaworld.com/blue-guppy.jpg", LocalDateTime.now().minusDays(5)));
        
        // Fish Food
        productStore.put(2003L, new Product(2003L, "Premium Guppy Food", "fish_food",
            "High-quality flakes for optimal nutrition", new BigDecimal("8.99"), 
            50, "https://aquaworld.com/guppy-food.jpg", LocalDateTime.now().minusDays(10)));
        
        // Equipment
        productStore.put(2004L, new Product(2004L, "10 Gallon Tank", "equipment",
            "Perfect starter aquarium tank", new BigDecimal("49.99"), 
            15, "https://aquaworld.com/tank.jpg", LocalDateTime.now().minusDays(15)));
        
        idCounter.set(2005);
    }
    
    // ==================== GET ENDPOINTS ====================
    
    /**
     * GET /api/v1/products
     * Retrieve all products
     * 
     * @return List of all products with 200 OK status
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllProducts() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Products retrieved successfully");
        response.put("statusCode", 200);
        response.put("count", productStore.size());
        response.put("data", productStore.values());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/v1/products/{id}
     * Retrieve a specific product by ID
     * 
     * @param id The product ID
     * @return Product details with 200 OK, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        // Check if product exists
        if (!productStore.containsKey(id)) {
            response.put("success", false);
            response.put("message", "Product not found");
            response.put("statusCode", 404);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        
        response.put("success", true);
        response.put("message", "Product retrieved successfully");
        response.put("statusCode", 200);
        response.put("data", productStore.get(id));
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/v1/products/search?name=...
     * Search products by name
     * 
     * @param name Product name to search
     * @return Matching products with 200 OK
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchProducts(@RequestParam String name) {
        Map<String, Object> response = new HashMap<>();
        
        // Case-insensitive search
        List<Product> results = productStore.values().stream()
            .filter(p -> p.getName().toLowerCase().contains(name.toLowerCase()))
            .toList();
        
        response.put("success", true);
        response.put("message", "Search completed successfully");
        response.put("statusCode", 200);
        response.put("count", results.size());
        response.put("data", results);
        
        return ResponseEntity.ok(response);
    }
    
    // ==================== POST ENDPOINT ====================
    
    /**
     * POST /api/v1/products
     * Create a new product
     * 
     * @param product The product object from request body
     * @return Created product with 201 Created status
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createProduct(@RequestBody Product product) {
        Map<String, Object> response = new HashMap<>();
        
        // Validate input
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Product name is required");
            response.put("statusCode", 400);
            return ResponseEntity.badRequest().body(response);
        }
        
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            response.put("success", false);
            response.put("message", "Product price must be greater than 0");
            response.put("statusCode", 400);
            return ResponseEntity.badRequest().body(response);
        }
        
        if (product.getStock() == null || product.getStock() < 0) {
            response.put("success", false);
            response.put("message", "Product stock cannot be negative");
            response.put("statusCode", 400);
            return ResponseEntity.badRequest().body(response);
        }
        
        // Generate ID and save product
        Long newId = idCounter.getAndIncrement();
        product.setId(newId);
        product.setCreatedAt(LocalDateTime.now());
        productStore.put(newId, product);
        
        response.put("success", true);
        response.put("message", "Product created successfully");
        response.put("statusCode", 201);
        response.put("data", product);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    // ==================== PUT ENDPOINT ====================
    
    /**
     * PUT /api/v1/products/{id}
     * Update an entire product resource
     * 
     * @param id The product ID
     * @param product Updated product data
     * @return Updated product with 200 OK, or 404 if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateProduct(
            @PathVariable Long id,
            @RequestBody Product product) {
        Map<String, Object> response = new HashMap<>();
        
        // Check if product exists
        if (!productStore.containsKey(id)) {
            response.put("success", false);
            response.put("message", "Product not found");
            response.put("statusCode", 404);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        
        // Validate input
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Product name is required");
            response.put("statusCode", 400);
            return ResponseEntity.badRequest().body(response);
        }
        
        // Update product (preserve original creation time)
        Product existing = productStore.get(id);
        product.setId(id);
        product.setCreatedAt(existing.getCreatedAt());
        productStore.put(id, product);
        
        response.put("success", true);
        response.put("message", "Product updated successfully");
        response.put("statusCode", 200);
        response.put("data", product);
        
        return ResponseEntity.ok(response);
    }
    
    // ==================== DELETE ENDPOINT ====================
    
    /**
     * DELETE /api/v1/products/{id}
     * Delete a product by ID
     * 
     * @param id The product ID
     * @return Success message with 200 OK, or 404 if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        // Check if product exists
        if (!productStore.containsKey(id)) {
            response.put("success", false);
            response.put("message", "Product not found");
            response.put("statusCode", 404);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        
        // Delete product
        Product deleted = productStore.remove(id);
        
        response.put("success", true);
        response.put("message", "Product deleted successfully");
        response.put("statusCode", 200);
        Map<String, Object> data = new HashMap<>();
        data.put("id", deleted.getId());
        data.put("name", deleted.getName());
        response.put("data", data);
        
        return ResponseEntity.ok(response);
    }
}
```

### Understanding the Annotations

| Annotation | Purpose |
|------------|---------|
| `@RestController` | Marks class as REST API controller, returns JSON |
| `@RequestMapping("/books")` | Base path for all endpoints in this controller |
| `@GetMapping` | Maps GET HTTP requests |
| `@PostMapping` | Maps POST HTTP requests |
| `@PutMapping` | Maps PUT HTTP requests |
| `@DeleteMapping` | Maps DELETE HTTP requests |
| `@PathVariable` | Extracts value from URL path (e.g., `{id}`) |
| `@RequestBody` | Binds JSON request body to Java object |

---

## 📡 API Endpoints Explained

### 1. GET /api/v1/products (List All Products)

**Request:**
```http
GET /aquaworld/api/v1/products HTTP/1.1
Host: localhost:8080
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Products retrieved successfully",
  "statusCode": 200,
  "count": 4,
  "data": [
    {
      "id": 2001,
      "name": "Red Guppy Male - Premium",
      "category": "guppies",
      "description": "Beautiful red male guppy with full tail fin",
      "price": 5.99,
      "stock": 25,
      "imageUrl": "https://aquaworld.com/red-guppy.jpg",
      "createdAt": "2025-01-15T08:00:00"
    },
    {
      "id": 2002,
      "name": "Blue Guppy Male",
      "category": "guppies",
      "description": "Vibrant blue colored male guppy",
      "price": 6.49,
      "stock": 18,
      "imageUrl": "https://aquaworld.com/blue-guppy.jpg",
      "createdAt": "2025-01-15T08:00:00"
    }
  ]
}
```

---

### 2. GET /api/v1/products/{id} (Get Specific Product)

**Request:**
```http
GET /aquaworld/api/v1/products/2001 HTTP/1.1
Host: localhost:8080
```

**Response (200 OK):**
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
    "imageUrl": "https://aquaworld.com/red-guppy.jpg",
    "createdAt": "2025-01-15T08:00:00"
  }
}
```

**Error Response (404 Not Found):**
```json
{
  "success": false,
  "message": "Product not found",
  "statusCode": 404
}
```

---

### 3. GET /api/v1/products/search?name=... (Search Products)

**Request:**
```http
GET /aquaworld/api/v1/products/search?name=Guppy HTTP/1.1
Host: localhost:8080
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Search completed successfully",
  "statusCode": 200,
  "count": 2,
  "data": [
    {
      "id": 2001,
      "name": "Red Guppy Male - Premium",
      "category": "guppies",
      "price": 5.99,
      "stock": 25
    },
    {
      "id": 2002,
      "name": "Blue Guppy Male",
      "category": "guppies",
      "price": 6.49,
      "stock": 18
    }
  ]
}
```

---

### 4. POST /api/v1/products (Create New Product)

**Request:**
```http
POST /aquaworld/api/v1/products HTTP/1.1
Host: localhost:8080
Content-Type: application/json

{
  "name": "Black Lace Guppy Female",
  "category": "guppies",
  "description": "Elegant black laced patterned female guppy",
  "price": 4.99,
  "stock": 30,
  "imageUrl": "https://aquaworld.com/black-guppy.jpg"
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Product created successfully",
  "statusCode": 201,
  "data": {
    "id": 2005,
    "name": "Black Lace Guppy Female",
    "category": "guppies",
    "description": "Elegant black laced patterned female guppy",
    "price": 4.99,
    "stock": 30,
    "imageUrl": "https://aquaworld.com/black-guppy.jpg",
    "createdAt": "2025-01-19T10:30:00"
  }
}
```

**Error Response (400 Bad Request):**
```json
{
  "success": false,
  "message": "Product price must be greater than 0",
  "statusCode": 400
}
```

---

### 5. PUT /api/v1/products/{id} (Update Product)

**Request:**
```http
PUT /aquaworld/api/v1/products/2001 HTTP/1.1
Host: localhost:8080
Content-Type: application/json

{
  "name": "Red Guppy Male - Premium Edition",
  "category": "guppies",
  "description": "Premium red male guppy - show quality",
  "price": 7.99,
  "stock": 20,
  "imageUrl": "https://aquaworld.com/red-guppy-premium.jpg"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Product updated successfully",
  "statusCode": 200,
  "data": {
    "id": 2001,
    "name": "Red Guppy Male - Premium Edition",
    "category": "guppies",
    "description": "Premium red male guppy - show quality",
    "price": 7.99,
    "stock": 20,
    "imageUrl": "https://aquaworld.com/red-guppy-premium.jpg",
    "createdAt": "2025-01-15T08:00:00"
  }
}
```

---

### 6. DELETE /api/v1/products/{id} (Delete Product)

**Request:**
```http
DELETE /aquaworld/api/v1/products/2001 HTTP/1.1
Host: localhost:8080
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Product deleted successfully",
  "statusCode": 200,
  "data": {
    "id": 2001,
    "name": "Red Guppy Male - Premium Edition"
  }
}
```

---

## 💾 In-Memory Storage

We use a simple `Map<Long, Product>` for storage:

```java
private static final Map<Long, Product> productStore = new HashMap<>();
private static final AtomicLong idCounter = new AtomicLong(2000);
```

### Why HashMap?

| Feature | Benefit |
|---------|---------|
| **Key-Value Storage** | Fast O(1) lookup by product ID |
| **Simple** | No database setup needed |
| **Perfect for Learning** | Focus on REST, not persistence |
| **Real-Time** | Changes visible immediately |

### Why AtomicLong?

```java
private static final AtomicLong idCounter = new AtomicLong(2000);
```

- **Thread-Safe**: Multiple requests can increment safely
- **Atomic**: No race conditions
- **Auto-Increment**: `getAndIncrement()` returns current and increments
- **Concurrent**: Handles multiple simultaneous requests

### Static Initialization Block

```java
static {
    productStore.put(2001L, new Product(2001L, "Red Guppy Male - Premium", ...));
    productStore.put(2002L, new Product(2002L, "Blue Guppy Male", ...));
    idCounter.set(2005);
}
```

Runs once when class is loaded - prepopulates AquaWorld products.

---

## 🧪 Testing the API

### Using cURL

#### 1. Get All Products
```bash
curl -X GET http://localhost:8080/aquaworld/api/v1/products
```

#### 2. Get Product by ID
```bash
curl -X GET http://localhost:8080/aquaworld/api/v1/products/2001
```

#### 3. Search Products
```bash
curl -X GET "http://localhost:8080/aquaworld/api/v1/products/search?name=Guppy"
```

#### 4. Create New Product
```bash
curl -X POST http://localhost:8080/aquaworld/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Yellow Guppy Pair",
    "category": "guppies",
    "description": "Beautiful yellow guppies - mated pair",
    "price": 9.99,
    "stock": 12,
    "imageUrl": "https://aquaworld.com/yellow-guppies.jpg"
  }'
```

#### 5. Update Product
```bash
curl -X PUT http://localhost:8080/aquaworld/api/v1/products/2001 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Red Guppy Male - Deluxe",
    "category": "guppies",
    "description": "Premium show-quality red guppy",
    "price": 8.99,
    "stock": 15,
    "imageUrl": "https://aquaworld.com/red-guppy-deluxe.jpg"
  }'
```

#### 6. Delete Product
```bash
curl -X DELETE http://localhost:8080/aquaworld/api/v1/products/2001
```

### Using JavaScript Fetch

```javascript
// GET all products
fetch('http://localhost:8080/aquaworld/api/v1/products')
  .then(res => res.json())
  .then(data => console.log(data));

// GET product by ID
fetch('http://localhost:8080/aquaworld/api/v1/products/2001')
  .then(res => res.json())
  .then(data => console.log(data.data));

// CREATE product
fetch('http://localhost:8080/aquaworld/api/v1/products', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    name: 'Yellow Guppy Pair',
    category: 'guppies',
    description: 'Beautiful yellow guppies',
    price: 9.99,
    stock: 12,
    imageUrl: 'https://aquaworld.com/yellow-guppies.jpg'
  })
})
.then(res => res.json())
.then(data => console.log('Created:', data.data));

// UPDATE product
fetch('http://localhost:8080/aquaworld/api/v1/products/2001', {
  method: 'PUT',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    name: 'Red Guppy Male - Updated',
    category: 'guppies',
    description: 'Updated description',
    price: 6.99,
    stock: 20,
    imageUrl: 'https://aquaworld.com/red-guppy.jpg'
  })
})
.then(res => res.json())
.then(data => console.log('Updated:', data.data));

// DELETE product
fetch('http://localhost:8080/aquaworld/api/v1/products/2001', {
  method: 'DELETE'
})
.then(res => res.json())
.then(data => console.log('Deleted:', data.message));
```

---

## 📦 Complete Code Example

### Project Structure
```
aquaworld-api/
├── pom.xml
├── src/main/
│   ├── java/com/aquaworld/
│   │   ├── AquaWorldApplication.java    (Main class)
│   │   ├── controller/
│   │   │   └── ProductController.java
│   │   └── model/
│   │       └── Product.java
│   └── resources/
│       └── application.properties
└── README.md
```

### Main Application Class

**File**: `src/main/java/com/aquaworld/AquaWorldApplication.java`

```java
package com.aquaworld;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class for AquaWorld Pet Store REST API
 */
@SpringBootApplication
public class AquaWorldApplication {
    public static void main(String[] args) {
        SpringApplication.run(AquaWorldApplication.class, args);
        System.out.println("\n🐠 AquaWorld API started at http://localhost:8080/aquaworld/api/v1/products\n");
        System.out.println("✓ Red Guppy Male - $5.99");
        System.out.println("✓ Blue Guppy Male - $6.49");
        System.out.println("✓ Premium Guppy Food - $8.99");
        System.out.println("✓ 10 Gallon Tank - $49.99\n");
    }
}
```

### Run the Application

```bash
# Build
mvn clean install

# Run
mvn spring-boot:run

# Or run directly
java -jar target/aquaworld-api-1.0.0.jar
```

---

## 🎯 Key Concepts

### 1. Stateless Architecture
Each request is independent and contains all needed information.

```
Client Request → Server → Response
(No session maintained on server)
```

### 2. Resource-Based Endpoints
Use nouns (resources) instead of verbs (actions):

```
✅ Correct:
  GET    /api/v1/products        (get all)
  GET    /api/v1/products/2001   (get one)
  POST   /api/v1/products        (create)
  PUT    /api/v1/products/2001   (update)
  DELETE /api/v1/products/2001   (delete)

❌ Incorrect:
  GET  /getProducts
  GET  /fetchProductById
  POST /createProduct
  GET  /updateProduct
  GET  /deleteProduct
```

### 3. HTTP Status Codes

```
2xx Success:
  200 - OK (successful GET, PUT, DELETE)
  201 - Created (successful POST)

4xx Client Error:
  400 - Bad Request (validation failed)
  404 - Not Found (resource doesn't exist)

5xx Server Error:
  500 - Internal Server Error
```

### 4. Request/Response Format

Every response includes:
```json
{
  "success": true/false,
  "message": "Descriptive message",
  "statusCode": 200,
  "data": { ... or [] }
}
```

### 5. AquaWorld Product Categories

```
- guppies       (Various guppy species and colors)
- fish_food     (Food products for guppies)
- equipment     (Tanks, filters, heaters)
- decorations   (Plants, driftwood, ornaments)
- medicines     (Health treatments)
```

---

## 🚀 Next Steps

1. **Add Validation** - Use `@Valid` and `@NotBlank`
2. **Authentication** - Add JWT token-based auth for protected endpoints
3. **Database** - Replace HashMap with JPA/Hibernate + PostgreSQL
4. **Order Management** - Add Order and OrderItem endpoints
5. **Payment Processing** - Add Payment endpoints
6. **Exception Handling** - Create custom exceptions and GlobalExceptionHandler
7. **API Documentation** - Add Swagger/SpringDoc OpenAPI
8. **Unit Testing** - Write tests with JUnit 5 and Mockito

---

## 📚 Resources

- [Spring Boot Official Docs](https://spring.io/projects/spring-boot)
- [Spring Web Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html)
- [REST API Best Practices](https://restfulapi.net/)
- [HTTP Status Codes](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status)
- [AquaWorld Full Project](./aquaworld-api/README.md) - See complete implementation with authentication, orders, and payments

---

**Happy REST API Building with AquaWorld! 🐠**
