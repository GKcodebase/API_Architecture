package com.aquaworld.graphql.exception;

import org.springframework.stereotype.Component;

/**
 * Custom GraphQL Exception Handler
 * Maps domain exceptions to GraphQL error format
 * Spring GraphQL 1.2.x automatically handles exception conversion
 * Custom exception handling is done through @GraphQLExceptionHandler annotations in resolvers
 */
@Component
public class GraphQLExceptionHandler {
    // Exception handling is done implicitly by Spring GraphQL
    // Custom exceptions thrown in services/resolvers are automatically converted to GraphQL errors
}
