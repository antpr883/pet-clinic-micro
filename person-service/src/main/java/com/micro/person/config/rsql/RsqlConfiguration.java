package com.micro.person.config.rsql;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

/**
 * RSQL Configuration - Конфігурація RSQL операторів
 * 
 * <p>Цей клас конфігурує custom RSQL оператори для використання в пошукових запитах.
 * Spring автоматично завантажує цю конфігурацію при старті застосунку завдяки анотації {@code @Configuration}.
 * 
 * <h3>Що робить ця конфігурація?</h3>
 * <p>Реєструє додаткові RSQL оператори які не входять в стандартний набір:
 * <ul>
 *   <li>{@code =ilike=} - case-insensitive LIKE пошук</li>
 *   <li>{@code =match=} - full-text пошук</li>
 * </ul>
 * 
 * <h3>Як це працює?</h3>
 * <p>RSQL parser використовує ці оператори для розбору запитів:
 * <pre>
 * // Стандартний оператор (з коробки):
 * firstName==Іван
 * 
 * // Custom оператор (наш):
 * firstName=ilike='%іван%'
 * </pre>
 * 
 * <h3>Приклади використання:</h3>
 * <pre>
 * // В PersonSearchService:
 * Set&lt;ComparisonOperator&gt; operators = new HashSet&lt;&gt;(RSQLOperators.defaultOperators());
 * operators.addAll(RsqlConfiguration.CUSTOM_OPERATORS);  // Додаємо наші custom оператори
 * 
 * RSQLParser parser = new RSQLParser(operators);
 * Node rootNode = parser.parse("firstName=ilike='%іван%'");  // Тепер parser розуміє =ilike=
 * </pre>
 * 
 * @see RsqlOperators Константи операторів
 * @see cz.jirutka.rsql.parser.ast.ComparisonOperator
 * @author Person Service Team
 * @since 1.0
 */
@Configuration
public class RsqlConfiguration {
    
    /**
     * Custom оператор для case-insensitive LIKE пошуку
     * <p>Символ оператора: {@code =ilike=}
     * <p>Параметр {@code false} означає що оператор не є "multiple values" 
     * (тобто приймає одне значення, а не список)
     */
    public static final ComparisonOperator ILIKE = new ComparisonOperator(RsqlOperators.ILIKE, false);
    
    /**
     * Custom оператор для full-text пошуку
     * <p>Символ оператора: {@code =match=}
     * <p>Параметр {@code false} означає що оператор не є "multiple values"
     */
    public static final ComparisonOperator MATCH = new ComparisonOperator(RsqlOperators.MATCH, false);
    
    /**
     * Набір всіх custom операторів
     * <p>Використовується в {@code PersonSearchService} та {@code ContactSearchService}
     * для розширення стандартного набору RSQL операторів
     */
    public static final Set<ComparisonOperator> CUSTOM_OPERATORS = Set.of(ILIKE, MATCH);
}

