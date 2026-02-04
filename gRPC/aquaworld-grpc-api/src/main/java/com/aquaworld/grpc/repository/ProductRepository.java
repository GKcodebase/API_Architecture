package com.aquaworld.grpc.repository;

import com.aquaworld.grpc.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Product Repository for AquaWorld gRPC API
 * Provides database access methods for Product entities
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    List<Product> findByCategory(String category);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchQuery, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :searchQuery, '%'))")
    List<Product> searchByNameOrDescription(@Param("searchQuery") String searchQuery);

    @Query("SELECT p FROM Product p WHERE p.category = :category AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :searchQuery, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :searchQuery, '%')))")
    List<Product> searchByCategoryAndQuery(@Param("category") String category, @Param("searchQuery") String searchQuery);
}
