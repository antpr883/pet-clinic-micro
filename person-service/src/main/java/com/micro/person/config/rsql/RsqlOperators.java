package com.micro.person.config.rsql;

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
 * 
 * <h3>Приклади використання:</h3>
 * <pre>
 * // Замість:
 * if ("=ilike=".equals(operator)) { ... }
 * 
 * // Пишемо:
 * if (RsqlOperators.ILIKE.equals(operator)) { ... }
 * </pre>
 * 
 * @author Person Service Team
 * @since 1.0
 */
public final class RsqlOperators {
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Стандартні RSQL оператори (з коробки)
    // ═══════════════════════════════════════════════════════════════════════════
    
    /**
     * Оператор "дорівнює" (==)
     * <p>Приклад: {@code firstName==Іван}
     * <p>SQL: {@code first_name = 'Іван'}
     */
    public static final String EQUAL = "==";
    
    /**
     * Оператор "не дорівнює" (!=)
     * <p>Приклад: {@code firstName!=Петро}
     * <p>SQL: {@code first_name != 'Петро'}
     */
    public static final String NOT_EQUAL = "!=";
    
    /**
     * Оператор "більше" (=gt=)
     * <p>Приклад: {@code id=gt=100}
     * <p>SQL: {@code id > 100}
     */
    public static final String GREATER_THAN = "=gt=";
    
    /**
     * Оператор "більше або дорівнює" (=ge=)
     * <p>Приклад: {@code id=ge=100}
     * <p>SQL: {@code id >= 100}
     */
    public static final String GREATER_THAN_OR_EQUAL = "=ge=";
    
    /**
     * Оператор "менше" (=lt=)
     * <p>Приклад: {@code id=lt=100}
     * <p>SQL: {@code id < 100}
     */
    public static final String LESS_THAN = "=lt=";
    
    /**
     * Оператор "менше або дорівнює" (=le=)
     * <p>Приклад: {@code id=le=100}
     * <p>SQL: {@code id <= 100}
     */
    public static final String LESS_THAN_OR_EQUAL = "=le=";
    
    /**
     * Оператор "в списку" (=in=)
     * <p>Приклад: {@code id=in=(1,2,3)}
     * <p>SQL: {@code id IN (1, 2, 3)}
     */
    public static final String IN = "=in=";
    
    /**
     * Оператор "не в списку" (=out=)
     * <p>Приклад: {@code id=out=(1,2,3)}
     * <p>SQL: {@code id NOT IN (1, 2, 3)}
     */
    public static final String NOT_IN = "=out=";
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Custom оператори (наші власні розширення)
    // ═══════════════════════════════════════════════════════════════════════════
    
    /**
     * Оператор "case-insensitive LIKE" (=ilike=)
     * <p>Виконує пошук без урахування регістру
     * <p>Приклад: {@code firstName=ilike='%іван%'}
     * <p>SQL: {@code LOWER(first_name) LIKE LOWER('%іван%')}
     * 
     * <h4>Використання:</h4>
     * <ul>
     *   <li>Знайти всіх персон з іменем що містить "іван" (ігноруючи регістр)</li>
     *   <li>Пошук по email без урахування регістру</li>
     * </ul>
     */
    public static final String ILIKE = "=ilike=";
    
    /**
     * Оператор "full-text search" (=match=)
     * <p>Виконує пошук тексту (case-insensitive LIKE з автоматичним додаванням %)
     * <p>Приклад: {@code firstName=match='Іван'}
     * <p>SQL: {@code LOWER(first_name) LIKE '%іван%'}
     * 
     * <h4>Використання:</h4>
     * <ul>
     *   <li>Швидкий текстовий пошук</li>
     *   <li>Пошук по частині тексту без ручного додавання %</li>
     * </ul>
     * 
     * <h4>Примітка:</h4>
     * <p>Для пошуку по кількох полях використовуйте OR:
     * <pre>firstName=match='Іван',lastName=match='Іван'</pre>
     */
    public static final String MATCH = "=match=";
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Private constructor - заборона створення екземплярів
    // ═══════════════════════════════════════════════════════════════════════════
    
    /**
     * Private constructor щоб заборонити створення екземплярів цього utility класу.
     * <p>Цей клас містить тільки статичні константи та не повинен бути інстанційованим.
     */
    private RsqlOperators() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

