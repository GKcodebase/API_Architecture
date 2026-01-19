package com.aquaworld.controller;

import com.aquaworld.dto.ProductResponse;
import com.aquaworld.response.ApiResponse;
import com.aquaworld.service.ProductService;
import com.aquaworld.util.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Product REST Controller for AquaWorld
 *
 * Provides endpoints for browsing AquaWorld product catalog.
 *
 * Base Path: /api/v1/products
 * Authentication: NOT REQUIRED (public endpoints)
 *
 * Endpoints:
 * - GET /products - Get all products
 * - GET /products/{id} - Get product by ID
 * - GET /products/search - Search products by name
 * - GET /products/category/{category} - Get products by category
 *
 * HTTP Response Status Codes:
 * - 200 OK: Request successful
 * - 400 Bad Request: Invalid search/filter parameters
 * - 404 Not Found: Product not found
 * - 503 Service Unavailable: Service temporarily unavailable
 *
 * Product Categories (AquaWorld specialization):
 * - guppies: Various guppy fish varieties (main focus)
 * - fish_food: Specialized food for guppies
 * - equipment: Aquarium tanks, filters, heaters
 * - decorations: Plants, ornaments, castles
 * - medicines: Fish health treatments
 *
 * @author AquaWorld Development Team
 */
@RestController
@RequestMapping(Constants.PRODUCTS_ENDPOINT)
@Tag(name = "Products", description = "Product catalog endpoints (public)")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Get all products
     *
     * HTTP Request:
     * GET /api/v1/products
     *
     * Success Response (200 OK):
     * {
     *   "success": true,
     *   "message": "OK",
     *   "statusCode": 200,
     *   "data": [
     *     {
     *       "id": 2001,
     *       "name": "Red Guppy Male",
     *       "category": "guppies",
     *       "price": 5.99,
     *       "stock": 25
     *     },
     *     ...
     *   ]
     * }
     *
     * @return ResponseEntity with list of all products
     */
    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieve all products from AquaWorld catalog")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Products retrieved successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "503",
                    description = "Service unavailable"
            )
    })
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();

        ApiResponse<List<ProductResponse>> response = ApiResponse.success(
                "Products retrieved successfully",
                products,
                HttpStatus.OK.value()
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get product by ID
     *
     * HTTP Request:
     * GET /api/v1/products/2001
     *
     * Success Response (200 OK):
     * {
     *   "success": true,
     *   "message": "OK",
     *   "statusCode": 200,
     *   "data": {
     *     "id": 2001,
     *     "name": "Red Guppy Male",
     *     "category": "guppies",
     *     "description": "Beautiful red male guppy",
     *     "price": 5.99,
     *     "stock": 25,
     *     "imageUrl": "https://..."
     *   }
     * }
     *
     * Error Response (404 Not Found):
     * {
     *   "success": false,
     *   "message": "Product not found",
     *   "statusCode": 404
     * }
     *
     * @param productId the product ID
     * @return ResponseEntity with product data
     */
    @GetMapping("/{productId}")
    @Operation(summary = "Get product by ID", description = "Retrieve product details by product ID")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Product found and returned"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            )
    })
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(
            @Parameter(description = "Product ID", example = "2001")
            @PathVariable Long productId) {
        ProductResponse product = productService.getProductById(productId);

        ApiResponse<ProductResponse> response = ApiResponse.success(
                "Product retrieved successfully",
                product,
                HttpStatus.OK.value()
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Search products by name
     *
     * HTTP Request:
     * GET /api/v1/products/search?name=Red
     *
     * Success Response (200 OK):
     * {
     *   "success": true,
     *   "message": "OK",
     *   "statusCode": 200,
     *   "data": [
     *     {
     *       "id": 2001,
     *       "name": "Red Guppy Male",
     *       ...
     *     }
     *   ]
     * }
     *
     * @param name the product name or partial name to search
     * @return ResponseEntity with matching products
     */
    @GetMapping("/search")
    @Operation(summary = "Search products", description = "Search products by name (case-insensitive)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Search results returned"
            )
    })
    public ResponseEntity<ApiResponse<List<ProductResponse>>> searchProducts(
            @Parameter(description = "Product name to search", example = "Red")
            @RequestParam String name) {
        List<ProductResponse> products = productService.searchProductsByName(name);

        ApiResponse<List<ProductResponse>> response = ApiResponse.success(
                "Search results returned",
                products,
                HttpStatus.OK.value()
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get products by category
     *
     * HTTP Request:
     * GET /api/v1/products/category/guppies
     *
     * Success Response (200 OK):
     * {
     *   "success": true,
     *   "message": "OK",
     *   "statusCode": 200,
     *   "data": [
     *     {
     *       "id": 2001,
     *       "name": "Red Guppy Male",
     *       "category": "guppies",
     *       ...
     *     },
     *     ...
     *   ]
     * }
     *
     * Available categories:
     * - guppies
     * - fish_food
     * - equipment
     * - decorations
     * - medicines
     *
     * @param category the product category
     * @return ResponseEntity with products in category
     */
    @GetMapping("/category/{category}")
    @Operation(summary = "Get products by category", description = "Retrieve all products in a specific category")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Products in category returned"
            )
    })
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProductsByCategory(
            @Parameter(description = "Product category", example = "guppies")
            @PathVariable String category) {
        List<ProductResponse> products = productService.getProductsByCategory(category);

        ApiResponse<List<ProductResponse>> response = ApiResponse.success(
                "Products in category retrieved successfully",
                products,
                HttpStatus.OK.value()
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get products in stock
     *
     * HTTP Request:
     * GET /api/v1/products/stock/available
     *
     * Success Response (200 OK):
     * {
     *   "success": true,
     *   "message": "OK",
     *   "statusCode": 200,
     *   "data": [
     *     {
     *       "id": 2001,
     *       "stock": 25
     *     },
     *     ...
     *   ]
     * }
     *
     * @return ResponseEntity with in-stock products
     */
    @GetMapping("/stock/available")
    @Operation(summary = "Get in-stock products", description = "Retrieve all products currently in stock")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "In-stock products retrieved successfully"
            )
    })
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getInStockProducts() {
        List<ProductResponse> products = productService.getProductsInStock();

        ApiResponse<List<ProductResponse>> response = ApiResponse.success(
                "In-stock products retrieved successfully",
                products,
                HttpStatus.OK.value()
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
