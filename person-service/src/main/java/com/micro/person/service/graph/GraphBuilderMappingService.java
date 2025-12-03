package com.micro.person.service.graph;

import com.cosium.spring.data.jpa.entity.graph.domain2.DynamicEntityGraph;
import com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph;
import com.micro.person.data.constants.PersonEntityGraphConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service для динамічної побудови Entity Graph для Cosium.
 * 
 * Cosium використовує власний EntityGraph тип з пакету
 * com.cosium.spring.data.jpa.entity.graph.domain2
 * 
 * Спрощена версія БЕЗ @MappingAttribute анотацій - просто додає атрибути до графу.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GraphBuilderMappingService {
    
    /**
     * Створює Entity Graph з атрибутами для Cosium.
     * 
     * @param entityClass клас Entity
     * @param attributes атрибути для завантаження (наприклад, {@link PersonEntityGraphConstants#CONTACTS}, {@link PersonEntityGraphConstants#ADDRESSES})
     * @return EntityGraph або null якщо атрибутів немає
     */
    public <T> EntityGraph getGraphWithAttributes(Class<T> entityClass, String... attributes) {
        if (attributes == null || attributes.length == 0) {
            return null; // Cosium використає default fetch
        }
        
        DynamicEntityGraph.Builder builder = DynamicEntityGraph.fetching();
        
        for (String attribute : attributes) {
            // Підтримка nested attributes (наприклад, "contacts.person")
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
     * Додає nested attribute до Entity Graph.
     * Наприклад: "contacts.person" -> contacts -> person
     */
    private void addNestedAttribute(DynamicEntityGraph.Builder builder, String attributePath) {
        String[] parts = attributePath.split("\\.");
        
        if (parts.length == 0) {
            return;
        }
        
        // Додаємо весь шлях як один path
        // Cosium підтримує nested paths через крапку
        builder.addPath(attributePath);
    }
}

