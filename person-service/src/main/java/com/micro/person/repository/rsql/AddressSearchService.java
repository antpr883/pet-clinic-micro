package com.micro.person.repository.rsql;

import com.micro.person.config.rsql.RsqlConfiguration;
import com.micro.person.data.constants.EntityConstants;
import com.micro.person.data.entities.AddressEntity;
import com.micro.person.repository.AddressRepository;
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
public class AddressSearchService {
    
    private final AddressRepository addressRepository;
    private final ExtendedRsqlVisitor<AddressEntity> rsqlVisitor;
    
    /**
     * Універсальний пошук адрес з RSQL
     * 
     * Приклади:
     * - "addressType==HOME" - по типу
     * - "city=ilike='%київ%'" - case-insensitive
     * - "person.id==1" - по персоні
     * - "country==Україна;active==true" - AND
     * 
     * @param rsqlQuery RSQL query string
     * @return список адрес
     */
    public List<AddressEntity> search(String rsqlQuery) {
        Specification<AddressEntity> spec = buildSpecification(rsqlQuery);
        return addressRepository.findAll(spec);
    }
    
    /**
     * Пошук з пагінацією
     */
    public Page<AddressEntity> searchWithPagination(String rsqlQuery, Pageable pageable) {
        Specification<AddressEntity> spec = buildSpecification(rsqlQuery);
        return addressRepository.findAll(spec, pageable);
    }
    
    /**
     * Підрахунок результатів
     */
    public long count(String rsqlQuery) {
        Specification<AddressEntity> spec = buildSpecification(rsqlQuery);
        return addressRepository.count(spec);
    }
    
    /**
     * Пошук тільки активних адрес
     */
    public List<AddressEntity> searchActive(String rsqlQuery) {
        String queryWithActive = addActiveFilter(rsqlQuery);
        return search(queryWithActive);
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Private helpers
    // ═══════════════════════════════════════════════════════════════════════════
    
    private Specification<AddressEntity> buildSpecification(String rsqlQuery) {
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

