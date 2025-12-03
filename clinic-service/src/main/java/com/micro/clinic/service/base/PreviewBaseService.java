package com.micro.clinic.service.base;

import com.micro.clinic.data.dto.base.BasePreviewDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Interface for Preview operations (lightweight DTOs for lists).
 */
public interface PreviewBaseService<P extends BasePreviewDto> {
    
    P findPreviewById(Long id, String... graphAttributes);
    List<P> findAllPreview(String... graphAttributes);
    Page<P> findAllPreview(Pageable pageable, String... graphAttributes);
    List<P> findPreviewsByIds(List<Long> ids, String... graphAttributes);
    List<P> findAllActivePreview(String... graphAttributes);
    Page<P> findAllActivePreview(Pageable pageable, String... graphAttributes);
}

