package com.micro.person.web.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;

/**
 * Wrapper for paginated responses.
 * 
 * Used in Controller Layer for wrapping Spring Page.
 * Service Layer returns pure Page<DTO>, Controller wraps them here.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginationResponse<T> implements Serializable {
    
    private List<T> content;
    private PaginationMetadata pagination;

    /**
     * Creates PaginationResponse from Spring Page.
     * 
     * @param page Spring Page
     * @return PaginationResponse
     */
    public static <T> PaginationResponse<T> of(Page<T> page) {
        PaginationMetadata metadata = PaginationMetadata.builder()
                .total(page.getTotalElements())
                .limit(page.getSize())
                .page(page.getNumber())
                .pages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
        
        return PaginationResponse.<T>builder()
                .content(page.getContent())
                .pagination(metadata)
                .build();
    }

    /**
     * Pagination metadata.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationMetadata implements Serializable {
        
        /** Total number of elements */
        private long total;
        
        /** Number of elements per page */
        private int limit;
        
        /** Current page number (0-based) */
        private int page;
        
        /** Total number of pages */
        private int pages;
        
        /** Whether this is the first page */
        private boolean first;
        
        /** Whether this is the last page */
        private boolean last;
        
        /** Whether there is a next page */
        private boolean hasNext;
        
        /** Whether there is a previous page */
        private boolean hasPrevious;
    }
}
