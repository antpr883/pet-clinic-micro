package com.micro.person.repository.rsql;

import com.micro.person.config.rsql.RsqlConfiguration;
import com.micro.person.data.constants.EntityConstants;
import com.micro.person.repository.rsql.ExtendedRsqlVisitor;
import com.micro.person.data.entities.ContactEntity;
import com.micro.person.repository.ContactRepository;
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
public class ContactSearchService {
    
    private final ContactRepository contactRepository;
    private final ExtendedRsqlVisitor<ContactEntity> rsqlVisitor;
    
    /**
     * Універсальний пошук контактів з RSQL
     * 
     * Приклади:
     * - "contactType==WORK" - по типу
     * - "email=ilike='%@gmail.com'" - case-insensitive
     * - "person.firstName==Іван" - по персоні
     * - "isPrimary==true;active==true" - AND
     * 
     * @param rsqlQuery RSQL query string
     * @return список контактів
     */
    public List<ContactEntity> search(String rsqlQuery) {
        Specification<ContactEntity> spec = buildSpecification(rsqlQuery);
        return contactRepository.findAll(spec);
    }
    
    /**
     * Пошук з пагінацією
     */
    public Page<ContactEntity> searchWithPagination(String rsqlQuery, Pageable pageable) {
        Specification<ContactEntity> spec = buildSpecification(rsqlQuery);
        return contactRepository.findAll(spec, pageable);
    }
    
    /**
     * Підрахунок результатів
     */
    public long count(String rsqlQuery) {
        Specification<ContactEntity> spec = buildSpecification(rsqlQuery);
        return contactRepository.count(spec);
    }
    
    /**
     * Пошук тільки активних контактів
     */
    public List<ContactEntity> searchActive(String rsqlQuery) {
        String queryWithActive = addActiveFilter(rsqlQuery);
        return search(queryWithActive);
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Private helpers
    // ═══════════════════════════════════════════════════════════════════════════
    
    private Specification<ContactEntity> buildSpecification(String rsqlQuery) {
        if (rsqlQuery == null || rsqlQuery.isBlank()) {
            return null;
        }
        
        Set<cz.jirutka.rsql.parser.ast.ComparisonOperator> operators = new HashSet<>(RSQLOperators.defaultOperators());
        operators.addAll(RsqlConfiguration.CUSTOM_OPERATORS);
        
        RSQLParser parser = new RSQLParser(operators);
        Node rootNode = parser.parse(rsqlQuery);
        
        return rootNode.accept(rsqlVisitor);
    }
    
    private String addActiveFilter(String rsqlQuery) {
        if (rsqlQuery == null || rsqlQuery.isBlank()) {
            return EntityConstants.ACTIVE_RSQL_QUERY;
        }
        return rsqlQuery + EntityConstants.RSQL_SEPARATOR + EntityConstants.ACTIVE_RSQL_QUERY;
    }
}

