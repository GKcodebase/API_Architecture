package com.aquaworld.graphql.resolver;

import com.aquaworld.graphql.model.Payment;
import com.aquaworld.graphql.model.Order;
import com.aquaworld.graphql.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

/**
 * Field Resolver for Payment Type
 * Handles nested field resolution for Payment
 */
@Controller
public class PaymentResolver {

    @Autowired
    private OrderService orderService;

    /**
     * Resolve the Order field in Payment
     */
    @SchemaMapping(typeName = "Payment", field = "order")
    public Order resolveOrder(Payment payment) {
        return orderService.getOrderById(payment.getOrderId());
    }
}
