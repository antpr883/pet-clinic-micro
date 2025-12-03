package com.micro.pet.config.rsql;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

/**
 * RSQL Configuration - Конфігурація RSQL операторів
 * 
 * <p>Цей клас конфігурує custom RSQL оператори для використання в пошукових запитах.
 * Spring автоматично завантажує цю конфігурацію при старті застосунку завдяки анотації {@code @Configuration}.
 */
@Configuration
public class RsqlConfiguration {
    
    /**
     * Custom оператор для case-insensitive LIKE пошуку
     * <p>Символ оператора: {@code =ilike=}
     */
    public static final ComparisonOperator ILIKE = new ComparisonOperator(RsqlOperators.ILIKE, false);
    
    /**
     * Custom оператор для full-text пошуку
     * <p>Символ оператора: {@code =match=}
     */
    public static final ComparisonOperator MATCH = new ComparisonOperator(RsqlOperators.MATCH, false);
    
    /**
     * Набір всіх custom операторів
     * <p>Використовується в SearchService для розширення стандартного набору RSQL операторів
     */
    public static final Set<ComparisonOperator> CUSTOM_OPERATORS = Set.of(ILIKE, MATCH);
}

