package com.micro.clinic.service.graph;

import com.cosium.spring.data.jpa.entity.graph.domain2.DynamicEntityGraph;
import com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph;
import com.micro.clinic.data.constants.ClinicEntityGraphConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for dynamic Entity Graph building for Cosium.
 * 
 * Cosium uses its own EntityGraph type from package
 * com.cosium.spring.data.jpa.entity.graph.domain2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GraphBuilderMappingService {
    
    /**
     * Creates Entity Graph with attributes for Cosium.
     * 
     * @param entityClass Entity class
     * @param attributes attributes to load (e.g., {@link ClinicEntityGraphConstants#VISITS}, {@link ClinicEntityGraphConstants#DIAGNOSES})
     * @return EntityGraph or null if no attributes
     */
    public <T> EntityGraph getGraphWithAttributes(Class<T> entityClass, String... attributes) {
        if (attributes == null || attributes.length == 0) {
            return null; // Cosium will use default fetch
        }
        
        DynamicEntityGraph.Builder builder = DynamicEntityGraph.fetching();
        
        for (String attribute : attributes) {
            // Support nested attributes (e.g., "hospitalization.dailyChecks")
            if (attribute.contains(".")) {
                addNestedAttribute(builder, attribute);
            } else {
                builder.addPath(attribute);
            }
        }
        
        log.debug("Created EntityGraph for {} with attributes: {}", 
                  entityClass.getSimpleName(), String.join(", ", attributes));
        
        return builder.build();
    }
    
    /**
     * Adds nested attribute to Entity Graph.
     * Example: "hospitalization.dailyChecks" -> hospitalization -> dailyChecks
     */
    private void addNestedAttribute(DynamicEntityGraph.Builder builder, String attributePath) {
        String[] parts = attributePath.split("\\.");
        
        if (parts.length == 0) {
            return;
        }
        
        // Add entire path as one path
        // Cosium supports nested paths via dot
        builder.addPath(attributePath);
    }
}

