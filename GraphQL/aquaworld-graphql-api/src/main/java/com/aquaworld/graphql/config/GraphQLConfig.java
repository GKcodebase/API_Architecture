package com.aquaworld.graphql.config;

import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * GraphQL Configuration
 * Registers custom scalar types and other GraphQL configurations
 */
@Configuration
public class GraphQLConfig {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    /**
     * Create custom DateTime scalar type
     */
    private GraphQLScalarType dateTimeScalar() {
        return GraphQLScalarType.newScalar()
                .name("DateTime")
                .description("ISO 8601 DateTime scalar type")
                .coercing(new Coercing<Object, String>() {
                    @Override
                    public String serialize(Object dataFetcherResult, GraphQLContext context, Locale locale)
                            throws CoercingSerializeException {
                        try {
                            if (dataFetcherResult instanceof String) {
                                return (String) dataFetcherResult;
                            }
                            if (dataFetcherResult instanceof OffsetDateTime) {
                                return FORMATTER.format((OffsetDateTime) dataFetcherResult);
                            }
                            if (dataFetcherResult instanceof LocalDateTime) {
                                return FORMATTER.format(((LocalDateTime) dataFetcherResult)
                                        .atZone(ZoneId.of("UTC")).toOffsetDateTime());
                            }
                            return dataFetcherResult.toString();
                        } catch (Exception e) {
                            throw new CoercingSerializeException("Cannot serialize DateTime: " + e.getMessage());
                        }
                    }

                    @Override
                    public Object parseValue(Object input, GraphQLContext context, Locale locale)
                            throws CoercingParseValueException {
                        try {
                            if (input instanceof String) {
                                return OffsetDateTime.parse((String) input, FORMATTER);
                            }
                            throw new CoercingParseValueException("Cannot parse DateTime from: " + input);
                        } catch (DateTimeParseException e) {
                            throw new CoercingParseValueException("Invalid DateTime format: " + e.getMessage());
                        }
                    }

                    @Override
                    public Object parseLiteral(Value<?> value, CoercedVariables variables, GraphQLContext context, Locale locale)
                            throws CoercingParseValueException {
                        try {
                            if (value instanceof StringValue) {
                                String strValue = ((StringValue) value).getValue();
                                return OffsetDateTime.parse(strValue, FORMATTER);
                            }
                            throw new CoercingParseValueException("Cannot parse DateTime literal from: " + value);
                        } catch (DateTimeParseException e) {
                            throw new CoercingParseValueException("Invalid DateTime format: " + e.getMessage());
                        }
                    }
                })
                .build();
    }

    /**
     * Configure runtime wiring to include custom scalars
     */
    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder
                .scalar(dateTimeScalar())
                .build();
    }
}



