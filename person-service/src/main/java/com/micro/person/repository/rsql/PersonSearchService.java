package com.micro.person.repository.rsql;

import com.micro.person.config.rsql.RsqlConfiguration;
import com.micro.person.data.constants.EntityConstants;
import com.micro.person.data.constants.PersonEntityGraphConstants;
import com.micro.person.data.entities.PersonEntity;
import com.micro.person.repository.PersonRepository;
import com.micro.person.repository.rsql.ExtendedRsqlVisitor;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.RSQLOperators;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PersonSearchService {
    
    private final PersonRepository personRepository;
    private final ExtendedRsqlVisitor<PersonEntity> rsqlVisitor;
    
    /**
     * Універсальний пошук з RSQL
     * 
     * Приклади:
     * - "firstName==Іван" - exact match
     * - "firstName=ilike='%іван%'" - case-insensitive LIKE
     * - "lastName==Петренко;active==true" - AND
     * - "firstName==Іван,lastName==Петро" - OR
     * - "contacts.email==test@test.com" - nested property
     * - "createdAt>2025-01-01" - date comparison
     * - "firstName==Іван;lastName==Петренко" - комбінований пошук
     * 
     * @param rsqlQuery RSQL query string
     * @return список персон
     */
    public List<PersonEntity> search(String rsqlQuery) {
        Specification<PersonEntity> spec = buildSpecification(rsqlQuery);
        return personRepository.findAll(spec);
    }
    
    /**
     * Пошук з RSQL та Entity Graph (для завантаження зв'язаних сутностей).
     * 
     * @param rsqlQuery RSQL query string
     * @param graphAttributes атрибути для Entity Graph (наприклад, {@link PersonEntityGraphConstants#CONTACTS}, {@link PersonEntityGraphConstants#ADDRESSES})
     * @return список персон з завантаженими зв'язками
     */
    public List<PersonEntity> search(String rsqlQuery, String... graphAttributes) {
        Specification<PersonEntity> spec = buildSpecification(rsqlQuery);
        if (graphAttributes != null && graphAttributes.length > 0) {
            com.cosium.spring.data.jpa.entity.graph.domain2.DynamicEntityGraph.Builder builder = 
                com.cosium.spring.data.jpa.entity.graph.domain2.DynamicEntityGraph.fetching();
            for (String attribute : graphAttributes) {
                builder.addPath(attribute);
            }
            com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph graph = builder.build();
            return personRepository.findAll(spec, graph);
        }
        return personRepository.findAll(spec);
    }
    
    /**
     * Пошук з пагінацією
     */
    public Page<PersonEntity> searchWithPagination(String rsqlQuery, Pageable pageable) {
        Specification<PersonEntity> spec = buildSpecification(rsqlQuery);
        return personRepository.findAll(spec, pageable);
    }
    
    /**
     * Пошук з пагінацією та Entity Graph (для завантаження зв'язаних сутностей).
     * 
     * @param rsqlQuery RSQL query string
     * @param pageable пагінація
     * @param graphAttributes атрибути для Entity Graph (наприклад, {@link PersonEntityGraphConstants#CONTACTS}, {@link PersonEntityGraphConstants#ADDRESSES})
     * @return сторінка персон з завантаженими зв'язками
     */
    public Page<PersonEntity> searchWithPagination(String rsqlQuery, Pageable pageable, String... graphAttributes) {
        Specification<PersonEntity> spec = buildSpecification(rsqlQuery);
        if (graphAttributes != null && graphAttributes.length > 0) {
            com.cosium.spring.data.jpa.entity.graph.domain2.DynamicEntityGraph.Builder builder = 
                com.cosium.spring.data.jpa.entity.graph.domain2.DynamicEntityGraph.fetching();
            for (String attribute : graphAttributes) {
                builder.addPath(attribute);
            }
            com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph graph = builder.build();
            return personRepository.findAll(spec, pageable, graph);
        }
        return personRepository.findAll(spec, pageable);
    }
    
    /**
     * Підрахунок результатів пошуку
     */
    public long count(String rsqlQuery) {
        Specification<PersonEntity> spec = buildSpecification(rsqlQuery);
        return personRepository.count(spec);
    }
    
    /**
     * Пошук тільки активних персон
     */
    public List<PersonEntity> searchActive(String rsqlQuery) {
        String queryWithActive = addActiveFilter(rsqlQuery);
        return search(queryWithActive);
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Private helpers
    // ═══════════════════════════════════════════════════════════════════════════
    
    /**
     * Конвертує RSQL query в Specification
     */
    private Specification<PersonEntity> buildSpecification(String rsqlQuery) {
        if (rsqlQuery == null || rsqlQuery.isBlank()) {
            return null;
        }
        
        Set<cz.jirutka.rsql.parser.ast.ComparisonOperator> operators = new HashSet<>(RSQLOperators.defaultOperators());
        operators.addAll(RsqlConfiguration.CUSTOM_OPERATORS);
        
        RSQLParser parser = new RSQLParser(operators);
        Node rootNode = parser.parse(rsqlQuery);
        
        return rootNode.accept(rsqlVisitor);
    }
    
    /**
     * Додає фільтр active=true до query
     */
    private String addActiveFilter(String rsqlQuery) {
        if (rsqlQuery == null || rsqlQuery.isBlank()) {
            return EntityConstants.ACTIVE_RSQL_QUERY;
        }
        return rsqlQuery + EntityConstants.RSQL_SEPARATOR + EntityConstants.ACTIVE_RSQL_QUERY;
    }
}

