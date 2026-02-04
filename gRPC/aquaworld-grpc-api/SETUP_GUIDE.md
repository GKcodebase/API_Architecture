# AquaWorld gRPC API - Setup & Installation Guide

## Prerequisites

Before you begin, ensure you have the following installed:

### Required
- **Java 21** or higher
  ```bash
  java -version
  ```
- **Maven 3.8+**
  ```bash
  mvn -version
  ```
- **Git**
  ```bash
  git --version
  ```

### Optional (for testing)
- **grpcurl** - Command-line tool for testing gRPC APIs
  ```bash
  # macOS
  brew install grpcurl
  
  # Linux (requires Go)
  go install github.com/fullstorydev/grpcurl/cmd/grpcurl@latest
  ```

---

## Installation Steps

### 1. Clone/Setup Project

Navigate to the gRPC folder:
```bash
cd /Users/gokulg.k/Documents/GitHub/API_Architecture/gRPC/aquaworld-grpc-api
```

### 2. Install Dependencies

Build the project and download all dependencies:
```bash
mvn clean install
```

This will:
- Download all Maven dependencies
- Compile Protocol Buffer files
- Generate gRPC stubs
- Build the JAR file

**Expected output:**
```
[INFO] BUILD SUCCESS
[INFO] Total time: XX.XXX s
```

### 3. Verify Installation

Verify the project structure:
```bash
ls -la src/main/java/com/aquaworld/grpc/
```

Expected directories:
- `config/` - Configuration classes
- `service/` - Business logic services
- `service/impl/` - gRPC service implementations
- `model/` - JPA entities
- `repository/` - Data access layer
- `exception/` - Exception classes
- `util/` - Utility classes

### 4. Run the Application

Start the gRPC server:
```bash
mvn spring-boot:run
```

**Expected startup output:**
```
🐠 ==========================================
🐠 AquaWorld Pet Store gRPC API
🐠 ==========================================
🐠 gRPC Server started on port 9090
🐠 Test with grpcurl: grpcurl -plaintext localhost:9090 list
🐠 ==========================================
```

---

## Configuration

### Default Settings

The application comes with sensible defaults in `src/main/resources/application.properties`:

| Setting | Default | Description |
|---------|---------|-------------|
| `grpc.server.port` | 9090 | gRPC server port |
| `jwt.expiration` | 86400000 | JWT token lifetime (24 hours) |
| `spring.h2.console.enabled` | true | H2 database console access |

### Change gRPC Port

Edit `src/main/resources/application.properties`:
```properties
grpc.server.port=9091  # Change from 9090 to any available port
```

### Change JWT Secret

**Important:** Change the default JWT secret for production:
```properties
jwt.secret=your-very-secure-secret-key-change-this-in-production
```

### Use PostgreSQL Instead of H2

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
spring.datasource.driverClassName=org.postgresql.Driver
spring.datasource.username=postgres
spring.datasource.password=yourpassword
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

3. Create database:
```sql
CREATE DATABASE aquaworld;
```

4. Rebuild:
```bash
mvn clean install
```

---

## Testing the Installation

### 1. Check Server Status

```bash
# List all gRPC services
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

### 2. Test User Registration

```bash
grpcurl -plaintext \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "testpass123",
    "full_name": "Test User"
  }' \
  localhost:9090 com.aquaworld.grpc.auth.AuthService/Register
```

Expected response:
```json
{
  "success": true,
  "message": "User registered successfully",
  "user": {
    "userId": "...",
    "username": "testuser",
    "email": "test@example.com",
    "fullName": "Test User",
    "createdAt": "..."
  }
}
```

### 3. Test User Login

```bash
grpcurl -plaintext \
  -d '{
    "username": "testuser",
    "password": "testpass123"
  }' \
  localhost:9090 com.aquaworld.grpc.auth.AuthService/Login
```

Save the returned `token` for authenticated requests.

### 4. Test Authenticated Request

```bash
grpcurl -plaintext \
  -H 'authorization: Bearer <YOUR_TOKEN>' \
  -d '{"page": 1, "page_size": 10}' \
  localhost:9090 com.aquaworld.grpc.product.ProductService/ListProducts
```

---

## Development Setup

### Using IDE

#### IntelliJ IDEA
1. Open IntelliJ
2. File → Open → Select `aquaworld-grpc-api` folder
3. Select JDK 21 if prompted
4. Maven will automatically download dependencies
5. Run `AquaWorldGrpcApplication.java`

#### VS Code
1. Install extensions:
   - Extension Pack for Java
   - Spring Boot Extension Pack
   - gRPC
2. Open folder: `aquaworld-grpc-api`
3. Press F5 to start debugging
4. Choose "Create launch.json file" → Select Java (Spring Boot)

### Hot Reload

Enable spring-boot-devtools for hot reload:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <optional>true</optional>
</dependency>
```

Then run with:
```bash
mvn spring-boot:run
```

---

## Build & Package

### Create Executable JAR

```bash
mvn clean package
```

Output: `target/aquaworld-grpc-api-1.0.0.jar`

### Run JAR

```bash
java -jar target/aquaworld-grpc-api-1.0.0.jar
```

### Docker Build

Create `Dockerfile`:
```dockerfile
FROM openjdk:21-slim
COPY target/aquaworld-grpc-api-1.0.0.jar app.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

Build image:
```bash
mvn clean package
docker build -t aquaworld-grpc:latest .
```

Run container:
```bash
docker run -p 9090:9090 aquaworld-grpc:latest
```

---

## Troubleshooting

### Issue: Maven Build Fails

**Solution:**
```bash
# Clear Maven cache
mvn clean install -U

# Verify Java version
java -version
```

### Issue: Port 9090 Already in Use

**Solution:**
```bash
# Find process using port
lsof -i :9090

# Kill process (macOS/Linux)
kill -9 <PID>

# Or change port in application.properties
grpc.server.port=9091
```

### Issue: Proto Files Not Compiling

**Solution:**
```bash
# Force proto recompilation
mvn clean protobuf:compile protobuf:compile-custom compile
```

### Issue: grpcurl Command Not Found

**Solution:**
```bash
# Install grpcurl
# macOS
brew install grpcurl

# Linux (requires Go 1.18+)
go install github.com/fullstorydev/grpcurl/cmd/grpcurl@latest

# Verify installation
grpcurl --version
```

### Issue: H2 Console Not Accessible

The H2 database console is available at:
```
http://localhost:8081/h2-console

JDBC URL: jdbc:h2:mem:aquaworld
Username: sa
Password: (leave blank)
```

---

## Next Steps

1. **Read API Documentation**: See `GRPC_API_REFERENCE.md`
2. **Test APIs**: Use `GRPC_USAGE_GUIDE.md` for examples
3. **Deploy**: Follow deployment guide in `README.md`
4. **Customize**: Modify services for your business logic

---

## Getting Help

- Check logs: `mvn spring-boot:run -X` (verbose mode)
- Read error messages carefully
- Check `TROUBLESHOOTING.md` in main documentation
- Review `application.properties` for configuration issues

---

**Installation Complete! Ready to start developing.** 🚀
