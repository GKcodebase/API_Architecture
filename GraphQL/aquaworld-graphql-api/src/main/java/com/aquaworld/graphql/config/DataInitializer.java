package com.aquaworld.graphql.config;

import com.aquaworld.graphql.model.Product;
import com.aquaworld.graphql.repository.ProductRepository;
import com.aquaworld.graphql.repository.UserRepository;
import com.aquaworld.graphql.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Data Initializer
 * Populates sample data on application startup
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void run(String... args) throws Exception {
        initializeUsers();
        initializeProducts();
    }

    private void initializeUsers() {
        // Admin user
        User admin = User.builder()
                .username("admin")
                .email("admin@aquaworld.com")
                .password(passwordEncoder.encode("admin@123"))
                .firstName("Admin")
                .lastName("User")
                .phone("+1-800-AQUA-001")
                .role("ADMIN")
                .createdAt(LocalDateTime.now().format(formatter))
                .active(true)
                .build();
        userRepository.save(admin);

        // Regular customer 1
        User john = User.builder()
                .username("john")
                .email("john@example.com")
                .password(passwordEncoder.encode("john@123"))
                .firstName("John")
                .lastName("Doe")
                .phone("+1-555-0101")
                .address("123 Aquatic Street, Fish City, FC 12345")
                .role("CUSTOMER")
                .createdAt(LocalDateTime.now().format(formatter))
                .active(true)
                .build();
        userRepository.save(john);

        // Regular customer 2
        User jane = User.builder()
                .username("jane")
                .email("jane@example.com")
                .password(passwordEncoder.encode("jane@123"))
                .firstName("Jane")
                .lastName("Smith")
                .phone("+1-555-0102")
                .address("456 Pet Avenue, Fish City, FC 12346")
                .role("CUSTOMER")
                .createdAt(LocalDateTime.now().format(formatter))
                .active(true)
                .build();
        userRepository.save(jane);
    }

    private void initializeProducts() {
        String now = LocalDateTime.now().format(formatter);

        // Red Guppy
        Product redGuppy = Product.builder()
                .name("Red Guppy")
                .category("guppies")
                .description("Beautiful red male guppy fish with vibrant coloring")
                .price(5.99)
                .stock(50)
                .imageUrl("https://example.com/red-guppy.jpg")
                .createdAt(now)
                .updatedAt(now)
                .build();
        productRepository.save(redGuppy);

        // Blue Guppy
        Product blueGuppy = Product.builder()
                .name("Blue Guppy")
                .category("guppies")
                .description("Stunning blue and green male guppy with flowing fins")
                .price(6.49)
                .stock(45)
                .imageUrl("https://example.com/blue-guppy.jpg")
                .createdAt(now)
                .updatedAt(now)
                .build();
        productRepository.save(blueGuppy);

        // Fish Food Premium
        Product fishFood = Product.builder()
                .name("Fish Food Premium")
                .category("fish_food")
                .description("High-quality pellet food for tropical fish - 100g")
                .price(3.99)
                .stock(100)
                .imageUrl("https://example.com/fish-food.jpg")
                .createdAt(now)
                .updatedAt(now)
                .build();
        productRepository.save(fishFood);

        // Aquarium Filter
        Product filter = Product.builder()
                .name("10L Aquarium Filter")
                .category("equipment")
                .description("Powerful filter system suitable for 10-liter aquariums")
                .price(24.99)
                .stock(15)
                .imageUrl("https://example.com/filter.jpg")
                .createdAt(now)
                .updatedAt(now)
                .build();
        productRepository.save(filter);

        // Plant Decoration
        Product plant = Product.builder()
                .name("Artificial Aqua Plant")
                .category("decorations")
                .description("Realistic silk aquatic plant for tank decoration - 20cm height")
                .price(7.99)
                .stock(30)
                .imageUrl("https://example.com/plant.jpg")
                .createdAt(now)
                .updatedAt(now)
                .build();
        productRepository.save(plant);

        // Medicine
        Product medicine = Product.builder()
                .name("Fish Medicine - Ich Cure")
                .category("medicines")
                .description("Effective treatment for ick disease in tropical fish")
                .price(12.99)
                .stock(20)
                .imageUrl("https://example.com/medicine.jpg")
                .createdAt(now)
                .updatedAt(now)
                .build();
        productRepository.save(medicine);
    }
}
