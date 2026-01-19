package com.aquaworld.config;

import com.aquaworld.model.Order;
import com.aquaworld.model.OrderItem;
import com.aquaworld.model.Product;
import com.aquaworld.model.User;
import com.aquaworld.repository.OrderItemRepository;
import com.aquaworld.repository.OrderRepository;
import com.aquaworld.repository.ProductRepository;
import com.aquaworld.repository.UserRepository;
import com.aquaworld.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * Data Initializer for AquaWorld REST API
 *
 * Initializes the application with sample data on startup.
 * This runs automatically when the Spring application starts.
 *
 * Sample Data Includes:
 * - 3 test users (customer, admin)
 * - 12 products (guppies, food, equipment, decorations, medicines)
 * - 2 sample orders
 * - Order items
 *
 * Benefits:
 * - Provides test data for API testing
 * - Demonstrates full workflow
 * - No manual data entry needed
 * - Useful for development and demos
 *
 * To disable sample data initialization:
 * - Comment out the @Component annotation
 * - Or set a Spring profile to prevent execution
 *
 * @author AquaWorld Development Team
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserRepository userRepository,
                          ProductRepository productRepository,
                          OrderRepository orderRepository,
                          OrderItemRepository orderItemRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Initialize sample data on application startup
     *
     * @param args command line arguments (unused)
     * @throws Exception if initialization fails
     */
    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n========== INITIALIZING AQUAWORLD SAMPLE DATA ==========\n");

        // Initialize users
        initializeUsers();

        // Initialize products
        initializeProducts();

        // Initialize orders
        initializeOrders();

        System.out.println("========== SAMPLE DATA INITIALIZED SUCCESSFULLY ==========\n");
        System.out.println("🐠 Sample Users:");
        System.out.println("   - john (password: john@123) - Customer");
        System.out.println("   - admin (password: admin@123) - Administrator");
        System.out.println("\n🐠 Sample Products: 12 guppies, food, equipment");
        System.out.println("🐠 Sample Orders: 2 orders with multiple items");
        System.out.println("\n📚 API Documentation: http://localhost:8080/swagger-ui.html\n");
    }

    /**
     * Initialize sample users
     */
    private void initializeUsers() {
        // User 1: John Doe (Customer)
        User john = User.builder()
                .username("john")
                .email("john@aquaworld.com")
                .password(passwordEncoder.encode("john@123"))
                .firstName("John")
                .lastName("Doe")
                .phone("555-1001")
                .address("123 Fish Lane, Aqua City, AC 12345")
                .role(Constants.ROLE_CUSTOMER)
                .createdAt(LocalDateTime.now().minusDays(5))
                .lastLogin(LocalDateTime.now().minusHours(2))
                .active(true)
                .build();

        // User 2: Admin User
        User admin = User.builder()
                .username("admin")
                .email("admin@aquaworld.com")
                .password(passwordEncoder.encode("admin@123"))
                .firstName("Admin")
                .lastName("Manager")
                .phone("555-2001")
                .address("456 Business Ave, Aqua City, AC 12345")
                .role(Constants.ROLE_ADMIN)
                .createdAt(LocalDateTime.now().minusDays(30))
                .lastLogin(LocalDateTime.now().minusHours(1))
                .active(true)
                .build();

        // User 3: Jane Smith (Customer)
        User jane = User.builder()
                .username("jane")
                .email("jane@aquaworld.com")
                .password(passwordEncoder.encode("jane@123"))
                .firstName("Jane")
                .lastName("Smith")
                .phone("555-3001")
                .address("789 Pet Street, Aqua City, AC 12345")
                .role(Constants.ROLE_CUSTOMER)
                .createdAt(LocalDateTime.now().minusDays(10))
                .lastLogin(LocalDateTime.now().minusDays(1))
                .active(true)
                .build();

        userRepository.save(john);
        userRepository.save(admin);
        userRepository.save(jane);

        System.out.println("✓ Created 3 sample users");
    }

    /**
     * Initialize sample products (AquaWorld guppy focus)
     */
    private void initializeProducts() {
        // ========== GUPPIES (Main Product Category) ==========
        Product[] guppies = {
                Product.builder()
                        .name("Red Guppy Male - Premium")
                        .category(Constants.CATEGORY_GUPPIES)
                        .description("Beautiful red male guppy with full tail fin and vibrant colors")
                        .price(new BigDecimal("5.99"))
                        .stock(25)
                        .imageUrl("https://aquaworld.com/images/red-guppy.jpg")
                        .createdAt(LocalDateTime.now().minusDays(20))
                        .build(),
                Product.builder()
                        .name("Blue Guppy Male - Platinum")
                        .category(Constants.CATEGORY_GUPPIES)
                        .description("Stunning blue male guppy with platinum highlights")
                        .price(new BigDecimal("6.49"))
                        .stock(18)
                        .imageUrl("https://aquaworld.com/images/blue-guppy.jpg")
                        .createdAt(LocalDateTime.now().minusDays(18))
                        .build(),
                Product.builder()
                        .name("Black Lace Guppy Female")
                        .category(Constants.CATEGORY_GUPPIES)
                        .description("Elegant black female guppy perfect for breeding")
                        .price(new BigDecimal("4.99"))
                        .stock(30)
                        .imageUrl("https://aquaworld.com/images/black-guppy.jpg")
                        .createdAt(LocalDateTime.now().minusDays(15))
                        .build(),
                Product.builder()
                        .name("Yellow Guppy Pair")
                        .category(Constants.CATEGORY_GUPPIES)
                        .description("Bright yellow male and female pair for breeding")
                        .price(new BigDecimal("9.99"))
                        .stock(12)
                        .imageUrl("https://aquaworld.com/images/yellow-guppy.jpg")
                        .createdAt(LocalDateTime.now().minusDays(12))
                        .build()
        };

        Arrays.stream(guppies).forEach(productRepository::save);

        // ========== FISH FOOD ==========
        Product[] food = {
                Product.builder()
                        .name("Premium Guppy Food - Flakes")
                        .category(Constants.CATEGORY_FISH_FOOD)
                        .description("High-quality nutritious flakes for guppies and tropical fish")
                        .price(new BigDecimal("8.99"))
                        .stock(50)
                        .imageUrl("https://aquaworld.com/images/food-flakes.jpg")
                        .createdAt(LocalDateTime.now().minusDays(25))
                        .build(),
                Product.builder()
                        .name("Guppy Fry Food - Micro Pellets")
                        .category(Constants.CATEGORY_FISH_FOOD)
                        .description("Special micro pellets for guppy fry and small fish")
                        .price(new BigDecimal("12.99"))
                        .stock(35)
                        .imageUrl("https://aquaworld.com/images/fry-food.jpg")
                        .createdAt(LocalDateTime.now().minusDays(20))
                        .build(),
                Product.builder()
                        .name("Color Enhancement Pellets")
                        .category(Constants.CATEGORY_FISH_FOOD)
                        .description("Pellets with color enhancers to brighten guppy colors")
                        .price(new BigDecimal("14.99"))
                        .stock(28)
                        .imageUrl("https://aquaworld.com/images/color-pellets.jpg")
                        .createdAt(LocalDateTime.now().minusDays(18))
                        .build()
        };

        Arrays.stream(food).forEach(productRepository::save);

        // ========== EQUIPMENT ==========
        Product[] equipment = {
                Product.builder()
                        .name("10 Gallon Aquarium Starter Kit")
                        .category(Constants.CATEGORY_EQUIPMENT)
                        .description("Complete 10-gallon tank with filter, heater, and light")
                        .price(new BigDecimal("49.99"))
                        .stock(15)
                        .imageUrl("https://aquaworld.com/images/tank-10g.jpg")
                        .createdAt(LocalDateTime.now().minusDays(30))
                        .build(),
                Product.builder()
                        .name("Submersible Tank Filter")
                        .category(Constants.CATEGORY_EQUIPMENT)
                        .description("Efficient internal filter for 20-40 gallon tanks")
                        .price(new BigDecimal("24.99"))
                        .stock(22)
                        .imageUrl("https://aquaworld.com/images/filter.jpg")
                        .createdAt(LocalDateTime.now().minusDays(22))
                        .build(),
                Product.builder()
                        .name("Aquarium Heater - 50W")
                        .category(Constants.CATEGORY_EQUIPMENT)
                        .description("Adjustable 50W heater for maintaining optimal temperature")
                        .price(new BigDecimal("19.99"))
                        .stock(40)
                        .imageUrl("https://aquaworld.com/images/heater.jpg")
                        .createdAt(LocalDateTime.now().minusDays(28))
                        .build()
        };

        Arrays.stream(equipment).forEach(productRepository::save);

        // ========== DECORATIONS ==========
        Product[] decorations = {
                Product.builder()
                        .name("Aquatic Plant - Cabomba")
                        .category(Constants.CATEGORY_DECORATIONS)
                        .description("Live cabomba plant for tank decoration and oxygen production")
                        .price(new BigDecimal("5.99"))
                        .stock(60)
                        .imageUrl("https://aquaworld.com/images/cabomba.jpg")
                        .createdAt(LocalDateTime.now().minusDays(15))
                        .build(),
                Product.builder()
                        .name("Driftwood - Large")
                        .category(Constants.CATEGORY_DECORATIONS)
                        .description("Natural driftwood for tank decoration and hiding spots")
                        .price(new BigDecimal("17.99"))
                        .stock(8)
                        .imageUrl("https://aquaworld.com/images/driftwood.jpg")
                        .createdAt(LocalDateTime.now().minusDays(10))
                        .build()
        };

        Arrays.stream(decorations).forEach(productRepository::save);

        // ========== MEDICINES ==========
        Product medicine = Product.builder()
                .name("Fish Antibiotic Treatment")
                .category(Constants.CATEGORY_MEDICINES)
                .description("Effective antibiotic treatment for fish diseases")
                .price(new BigDecimal("16.99"))
                .stock(20)
                .imageUrl("https://aquaworld.com/images/antibiotic.jpg")
                .createdAt(LocalDateTime.now().minusDays(20))
                .build();

        productRepository.save(medicine);

        System.out.println("✓ Created 12 sample products (guppies, food, equipment, decorations, medicines)");
    }

    /**
     * Initialize sample orders
     */
    private void initializeOrders() {
        // ===== ORDER 1 =====
        Order order1 = Order.builder()
                .userId(1001L) // John's order
                .orderNumber("ORD-20250115-001")
                .status(Constants.ORDER_STATUS_PENDING)
                .totalPrice(new BigDecimal("27.96")) // 2 guppies (5.99 each) + 1 food (8.99)
                .createdAt(LocalDateTime.now().minusDays(3))
                .updatedAt(LocalDateTime.now().minusDays(3))
                .build();

        Order savedOrder1 = orderRepository.save(order1);

        // Order 1 Items
        OrderItem item1_1 = OrderItem.builder()
                .orderId(savedOrder1.getId())
                .productId(2001L) // Red Guppy Male
                .quantity(2)
                .price(new BigDecimal("5.99"))
                .build();

        OrderItem item1_2 = OrderItem.builder()
                .orderId(savedOrder1.getId())
                .productId(2005L) // Premium Guppy Food
                .quantity(1)
                .price(new BigDecimal("8.99"))
                .build();

        orderItemRepository.save(item1_1);
        orderItemRepository.save(item1_2);

        // ===== ORDER 2 =====
        Order order2 = Order.builder()
                .userId(1003L) // Jane's order
                .orderNumber("ORD-20250116-002")
                .status(Constants.ORDER_STATUS_CONFIRMED)
                .totalPrice(new BigDecimal("99.96")) // 10-gallon kit + heater + guppy pair
                .createdAt(LocalDateTime.now().minusDays(2))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        Order savedOrder2 = orderRepository.save(order2);

        // Order 2 Items
        OrderItem item2_1 = OrderItem.builder()
                .orderId(savedOrder2.getId())
                .productId(2009L) // 10 Gallon Tank
                .quantity(1)
                .price(new BigDecimal("49.99"))
                .build();

        OrderItem item2_2 = OrderItem.builder()
                .orderId(savedOrder2.getId())
                .productId(2011L) // Heater
                .quantity(1)
                .price(new BigDecimal("19.99"))
                .build();

        OrderItem item2_3 = OrderItem.builder()
                .orderId(savedOrder2.getId())
                .productId(2004L) // Yellow Guppy Pair
                .quantity(1)
                .price(new BigDecimal("9.99"))
                .build();

        orderItemRepository.save(item2_1);
        orderItemRepository.save(item2_2);
        orderItemRepository.save(item2_3);

        System.out.println("✓ Created 2 sample orders with multiple items");
    }
}
