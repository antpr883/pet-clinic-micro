package com.micro.pet.repository.rsql;

import com.micro.pet.config.rsql.RsqlConfiguration;
import com.micro.pet.data.entities.PetEntity;
import com.micro.pet.repository.PetRepository;
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
public class PetSearchService {
    
    private final PetRepository petRepository;
    private final ExtendedRsqlVisitor<PetEntity> rsqlVisitor;
    
    /**
     * Universal search with RSQL
     */
    public List<PetEntity> search(String rsqlQuery) {
        Specification<PetEntity> spec = buildSpecification(rsqlQuery);
        return petRepository.findAll(spec);
    }
    
    /**
     * Search with RSQL and Entity Graph
     */
    public List<PetEntity> search(String rsqlQuery, String... graphAttributes) {
        Specification<PetEntity> spec = buildSpecification(rsqlQuery);
        if (graphAttributes != null && graphAttributes.length > 0) {
            com.cosium.spring.data.jpa.entity.graph.domain2.DynamicEntityGraph.Builder builder = 
                com.cosium.spring.data.jpa.entity.graph.domain2.DynamicEntityGraph.fetching();
            for (String attribute : graphAttributes) {
                builder.addPath(attribute);
            }
            com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph graph = builder.build();
            return petRepository.findAll(spec, graph);
        }
        return petRepository.findAll(spec);
    }
    
    /**
     * Search with pagination
     */
    public Page<PetEntity> searchWithPagination(String rsqlQuery, Pageable pageable) {
        Specification<PetEntity> spec = buildSpecification(rsqlQuery);
        return petRepository.findAll(spec, pageable);
    }
    
    /**
     * Search with pagination and Entity Graph
     */
    public Page<PetEntity> searchWithPagination(String rsqlQuery, Pageable pageable, String... graphAttributes) {
        Specification<PetEntity> spec = buildSpecification(rsqlQuery);
        if (graphAttributes != null && graphAttributes.length > 0) {
            com.cosium.spring.data.jpa.entity.graph.domain2.DynamicEntityGraph.Builder builder = 
                com.cosium.spring.data.jpa.entity.graph.domain2.DynamicEntityGraph.fetching();
            for (String attribute : graphAttributes) {
                builder.addPath(attribute);
            }
            com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph graph = builder.build();
            return petRepository.findAll(spec, pageable, graph);
        }
        return petRepository.findAll(spec, pageable);
    }
    
    /**
     * Count search results
     */
    public long count(String rsqlQuery) {
        Specification<PetEntity> spec = buildSpecification(rsqlQuery);
        return petRepository.count(spec);
    }
    
    /**
     * Builds Specification from RSQL query
     */
    private Specification<PetEntity> buildSpecification(String rsqlQuery) {
        if (rsqlQuery == null || rsqlQuery.trim().isEmpty()) {
            return null;
        }
        
        Set<cz.jirutka.rsql.parser.ast.ComparisonOperator> operators = new HashSet<>(RSQLOperators.defaultOperators());
        operators.addAll(RsqlConfiguration.CUSTOM_OPERATORS);
        
        RSQLParser parser = new RSQLParser(operators);
        Node rootNode = parser.parse(rsqlQuery);
        
        return rootNode.accept(rsqlVisitor);
    }
}

