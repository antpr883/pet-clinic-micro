package com.micro.pet.config.rsql;

/**
 * RSQL Operators - Константи для RSQL операторів
 * 
 * <p>Цей клас містить всі RSQL оператори які використовуються в проекті.
 * Це дозволяє уникнути magic strings в коді та забезпечує єдине джерело правди.
 * 
 * <h3>Стандартні оператори (з коробки RSQL):</h3>
 * <ul>
 *   <li>{@link #EQUAL} - Дорівнює (==)</li>
 *   <li>{@link #NOT_EQUAL} - Не дорівнює (!=)</li>
 *   <li>{@link #GREATER_THAN} - Більше (=gt=)</li>
 *   <li>{@link #GREATER_THAN_OR_EQUAL} - Більше або дорівнює (=ge=)</li>
 *   <li>{@link #LESS_THAN} - Менше (=lt=)</li>
 *   <li>{@link #LESS_THAN_OR_EQUAL} - Менше або дорівнює (=le=)</li>
 *   <li>{@link #IN} - В списку (=in=)</li>
 *   <li>{@link #NOT_IN} - Не в списку (=out=)</li>
 * </ul>
 * 
 * <h3>Custom оператори (наші власні):</h3>
 * <ul>
 *   <li>{@link #ILIKE} - Case-insensitive LIKE (=ilike=)</li>
 *   <li>{@link #MATCH} - Full-text search (=match=)</li>
 * </ul>
 */
public final class RsqlOperators {
    
    // Стандартні RSQL оператори (з коробки)
    public static final String EQUAL = "==";
    public static final String NOT_EQUAL = "!=";
    public static final String GREATER_THAN = "=gt=";
    public static final String GREATER_THAN_OR_EQUAL = "=ge=";
    public static final String LESS_THAN = "=lt=";
    public static final String LESS_THAN_OR_EQUAL = "=le=";
    public static final String IN = "=in=";
    public static final String NOT_IN = "=out=";
    
    // Custom оператори (наші власні розширення)
    public static final String ILIKE = "=ilike=";
    public static final String MATCH = "=match=";
    
    private RsqlOperators() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

