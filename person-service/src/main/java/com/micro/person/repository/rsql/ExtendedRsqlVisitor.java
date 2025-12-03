package com.micro.person.repository.rsql;

import com.micro.person.config.rsql.RsqlOperators;
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
 * <p>This class converts RSQL queries (REST Query Language) to JPA Specification,
 * which are then used to generate SQL queries to the database.
 * 
 * <h3>What does this class do?</h3>
 * <p>Implements Visitor pattern for processing RSQL Abstract Syntax Tree (AST):
 * <pre>
 * RSQL query: "firstName==Ivan;active==true"
 *      ↓ Parser parses
 * AST tree:
 *         AND (;)
 *        /       \
 *  firstName==Ivan  active==true
 *      ↓ ExtendedRsqlVisitor processes
 * JPA Specification:
 *  WHERE first_name = 'Ivan' AND active = true
 * </pre>
 * 
 * <h3>Spring integration:</h3>
 * <p>{@code @Component} - Spring automatically creates bean of this class on startup.
 * This bean can then be injected into other classes via {@code @Autowired} or constructor.
 * 
 * <h3>Generic parameter {@code <T>}:</h3>
 * <p>Allows using this Visitor for any Entity:
 * <ul>
 *   <li>{@code ExtendedRsqlVisitor<PersonEntity>} - for searching persons</li>
 *   <li>{@code ExtendedRsqlVisitor<ContactEntity>} - for searching contacts</li>
 * </ul>
 * 
 * <h3>Supported operators:</h3>
 * <ul>
 *   <li>Standard: ==, !=, =gt=, =ge=, =lt=, =le=, =in=, =out=</li>
 *   <li>Custom: =ilike=, =match=</li>
 *   <li>Logical: ; (AND), , (OR)</li>
 * </ul>
 * 
 * <h3>Usage examples:</h3>
 * <pre>
 * // In PersonSearchService:
 * ExtendedRsqlVisitor&lt;PersonEntity&gt; visitor = new ExtendedRsqlVisitor&lt;&gt;();
 * Node rootNode = parser.parse("firstName==Ivan;active==true");
 * Specification&lt;PersonEntity&gt; spec = rootNode.accept(visitor);
 * List&lt;PersonEntity&gt; results = personRepository.findAll(spec);
 * </pre>
 * 
 * @param <T> Entity type for which Specification is generated
 * @see org.springframework.data.jpa.domain.Specification
 * @see cz.jirutka.rsql.parser.ast.RSQLVisitor
 * @author Person Service Team
 * @since 1.0
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
    
    /**
     * Builds predicate based on operator
     */
    private Predicate buildPredicate(
            Root<T> root,
            CriteriaBuilder cb,
            String property,
            String operator,
            List<String> arguments) {
        
        // Custom operators
        if (RsqlOperators.ILIKE.equals(operator)) {
            return buildCaseInsensitiveLike(root, cb, property, arguments);
        }
        
        if (RsqlOperators.MATCH.equals(operator)) {
            return buildFullTextSearch(root, cb, property, arguments);
        }
        
        // Nested property handling (e.g., contacts.email)
        Path<?> path = getPath(root, property);
        String value = arguments.get(0);
        
        // DateTime handling
        if (isDateTimeField(path)) {
            return buildDateTimePredicate(path, cb, operator, value);
        }
        
        // Standard operators
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
    
    /**
     * Case-insensitive LIKE
     */
    private Predicate buildCaseInsensitiveLike(
            Root<T> root,
            CriteriaBuilder cb,
            String property,
            List<String> arguments) {
        
        Path<String> path = getPath(root, property);
        return cb.like(cb.lower(path), arguments.get(0).toLowerCase());
    }
    
    /**
     * Full-text search - simple case-insensitive LIKE
     * Client can use OR to search across multiple fields:
     * firstName=match='Ivan',lastName=match='Ivan'
     */
    private Predicate buildFullTextSearch(
            Root<T> root,
            CriteriaBuilder cb,
            String property,
            List<String> arguments) {
        
        String searchTerm = "%" + arguments.get(0).toLowerCase() + "%";
        Path<String> path = getPath(root, property);
        return cb.like(cb.lower(path), searchTerm);
    }
    
    /**
     * DateTime predicate (supports LocalDate and LocalDateTime)
     */
    private Predicate buildDateTimePredicate(
            Path<?> path,
            CriteriaBuilder cb,
            String operator,
            String value) {
        
        Class<?> type = path.getJavaType();
        
        // Handle LocalDate
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
        
        // Handle LocalDateTime
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
    
    /**
     * Gets Path for property (supports nested properties)
     */
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
    
    /**
     * Parse value according to field type
     */
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
    
    /**
     * Check if field is DateTime
     */
    private boolean isDateTimeField(Path<?> path) {
        Class<?> type = path.getJavaType();
        return LocalDateTime.class.isAssignableFrom(type) ||
               LocalDate.class.isAssignableFrom(type);
    }
    
    /**
     * Parse Date (LocalDate)
     */
    private LocalDate parseDate(String value) {
        return LocalDate.parse(value);
    }
    
    /**
     * Parse DateTime
     */
    private LocalDateTime parseDateTime(String value) {
        if (value.contains("T")) {
            return LocalDateTime.parse(value);
        }
        return LocalDate.parse(value).atStartOfDay();
    }
}

