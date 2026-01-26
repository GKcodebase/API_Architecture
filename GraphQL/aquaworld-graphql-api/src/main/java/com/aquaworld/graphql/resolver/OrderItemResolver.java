package com.aquaworld.graphql.resolver;

import com.aquaworld.graphql.model.OrderItem;
import com.aquaworld.graphql.model.Product;
import com.aquaworld.graphql.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

/**
 * Field Resolver for OrderItem Type
 * Handles nested field resolution for OrderItem
 */
@Controller
public class OrderItemResolver {

    @Autowired
    private ProductService productService;

    /**
     * Resolve the Product field in OrderItem
     */
    @SchemaMapping(typeName = "OrderItem", field = "product")
    public Product resolveProduct(OrderItem orderItem) {
        return productService.getProductById(orderItem.getProductId());
    }
}
