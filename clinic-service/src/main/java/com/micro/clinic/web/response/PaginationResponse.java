package com.micro.clinic.web.response;

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
        
        private long total;
        private int limit;
        private int page;
        private int pages;
        private boolean first;
        private boolean last;
        private boolean hasNext;
        private boolean hasPrevious;
    }
}

