package com.aquaworld.graphql.resolver;

import com.aquaworld.graphql.model.Order;
import com.aquaworld.graphql.model.User;
import com.aquaworld.graphql.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

/**
 * Field Resolver for Order Type
 * Handles nested field resolution for Order
 */
@Controller
public class OrderResolver {

    @Autowired
    private UserService userService;

    /**
     * Resolve the User field in Order
     */
    @SchemaMapping(typeName = "Order", field = "user")
    public User resolveUser(Order order) {
        return userService.getUserById(order.getUserId());
    }
}
