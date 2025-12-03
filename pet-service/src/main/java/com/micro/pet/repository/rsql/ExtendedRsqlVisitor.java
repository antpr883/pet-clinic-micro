package com.micro.pet.repository.rsql;

import com.micro.pet.config.rsql.RsqlOperators;
import cz.jirutka.rsql.parser.ast.*;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Extended RSQL Visitor - Converter of RSQL queries to JPA Specification
 * 
 * Converts RSQL queries (REST Query Language) to JPA Specification,
 * which are then used to generate SQL queries to the database.
 */
@Component
public class ExtendedRsqlVisitor<T> implements RSQLVisitor<Specification<T>, Void> {
    
    @Override
    public Specification<T> visit(AndNode node, Void param) {
        return node.getChildren().stream()
                .map(n -> n.accept(this))
                .reduce(Specification::and)
                .orElse(null);
    }
    
    @Override
    public Specification<T> visit(OrNode node, Void param) {
        return node.getChildren().stream()
                .map(n -> n.accept(this))
                .reduce(Specification::or)
                .orElse(null);
    }
    
    @Override
    public Specification<T> visit(ComparisonNode node, Void param) {
        return (root, query, cb) -> {
            String property = node.getSelector();
            String operator = node.getOperator().getSymbol();
            List<String> arguments = node.getArguments();
            
            return buildPredicate(root, cb, property, operator, arguments);
        };
    }
    
    private Predicate buildPredicate(
            Root<T> root,
            CriteriaBuilder cb,
            String property,
            String operator,
            List<String> arguments) {
        
        if (RsqlOperators.ILIKE.equals(operator)) {
            return buildCaseInsensitiveLike(root, cb, property, arguments);
        }
        
        if (RsqlOperators.MATCH.equals(operator)) {
            return buildFullTextSearch(root, cb, property, arguments);
        }
        
        Path<?> path = getPath(root, property);
        String value = arguments.get(0);
        
        if (isDateTimeField(path)) {
            return buildDateTimePredicate(path, cb, operator, value);
        }
        
        return switch (operator) {
            case RsqlOperators.EQUAL -> cb.equal(path, parseValue(path, value));
            case RsqlOperators.NOT_EQUAL -> cb.notEqual(path, parseValue(path, value));
            case RsqlOperators.GREATER_THAN -> cb.greaterThan(path.as(Comparable.class), (Comparable) parseValue(path, value));
            case RsqlOperators.GREATER_THAN_OR_EQUAL -> cb.greaterThanOrEqualTo(path.as(Comparable.class), (Comparable) parseValue(path, value));
            case RsqlOperators.LESS_THAN -> cb.lessThan(path.as(Comparable.class), (Comparable) parseValue(path, value));
            case RsqlOperators.LESS_THAN_OR_EQUAL -> cb.lessThanOrEqualTo(path.as(Comparable.class), (Comparable) parseValue(path, value));
            case RsqlOperators.IN -> path.in(arguments.stream().map(v -> parseValue(path, v)).toList());
            case RsqlOperators.NOT_IN -> path.in(arguments.stream().map(v -> parseValue(path, v)).toList()).not();
            default -> throw new IllegalArgumentException("Unknown operator: " + operator);
        };
    }
    
    private Predicate buildCaseInsensitiveLike(
            Root<T> root,
            CriteriaBuilder cb,
            String property,
            List<String> arguments) {
        
        Path<String> path = getPath(root, property);
        return cb.like(cb.lower(path), arguments.get(0).toLowerCase());
    }
    
    private Predicate buildFullTextSearch(
            Root<T> root,
            CriteriaBuilder cb,
            String property,
            List<String> arguments) {
        
        String searchTerm = "%" + arguments.get(0).toLowerCase() + "%";
        Path<String> path = getPath(root, property);
        return cb.like(cb.lower(path), searchTerm);
    }
    
    private Predicate buildDateTimePredicate(
            Path<?> path,
            CriteriaBuilder cb,
            String operator,
            String value) {
        
        Class<?> type = path.getJavaType();
        
        if (LocalDate.class.isAssignableFrom(type)) {
            LocalDate date = parseDate(value);
            Path<LocalDate> datePath = (Path<LocalDate>) path;
            
            return switch (operator) {
                case RsqlOperators.GREATER_THAN -> cb.greaterThan(datePath, date);
                case RsqlOperators.GREATER_THAN_OR_EQUAL -> cb.greaterThanOrEqualTo(datePath, date);
                case RsqlOperators.LESS_THAN -> cb.lessThan(datePath, date);
                case RsqlOperators.LESS_THAN_OR_EQUAL -> cb.lessThanOrEqualTo(datePath, date);
                default -> cb.equal(datePath, date);
            };
        }
        
        LocalDateTime dateTime = parseDateTime(value);
        Path<LocalDateTime> dateTimePath = (Path<LocalDateTime>) path;
        
        return switch (operator) {
            case RsqlOperators.GREATER_THAN -> cb.greaterThan(dateTimePath, dateTime);
            case RsqlOperators.GREATER_THAN_OR_EQUAL -> cb.greaterThanOrEqualTo(dateTimePath, dateTime);
            case RsqlOperators.LESS_THAN -> cb.lessThan(dateTimePath, dateTime);
            case RsqlOperators.LESS_THAN_OR_EQUAL -> cb.lessThanOrEqualTo(dateTimePath, dateTime);
            default -> cb.equal(dateTimePath, dateTime);
        };
    }
    
    private <Y> Path<Y> getPath(Root<T> root, String property) {
        if (!property.contains(".")) {
            return root.get(property);
        }
        
        String[] parts = property.split("\\.");
        Path<Y> path = root.get(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            path = path.get(parts[i]);
        }
        return path;
    }
    
    private Object parseValue(Path<?> path, String value) {
        Class<?> type = path.getJavaType();
        
        if (type == String.class) {
            return value;
        }
        if (type == Long.class || type == long.class) {
            return Long.parseLong(value);
        }
        if (type == Integer.class || type == int.class) {
            return Integer.parseInt(value);
        }
        if (type == Boolean.class || type == boolean.class) {
            return Boolean.parseBoolean(value);
        }
        if (type.isEnum()) {
            return Enum.valueOf((Class<Enum>) type, value);
        }
        
        return value;
    }
    
    private boolean isDateTimeField(Path<?> path) {
        Class<?> type = path.getJavaType();
        return LocalDateTime.class.isAssignableFrom(type) ||
               LocalDate.class.isAssignableFrom(type);
    }
    
    private LocalDate parseDate(String value) {
        return LocalDate.parse(value);
    }
    
    private LocalDateTime parseDateTime(String value) {
        if (value.contains("T")) {
            return LocalDateTime.parse(value);
        }
        return LocalDate.parse(value).atStartOfDay();
    }
}

