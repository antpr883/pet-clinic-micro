package com.micro.auth.repository;

import com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface CustomJpaRepository<T, ID extends Serializable>
        extends JpaRepository<T, ID>, 
                EntityGraphJpaRepository<T, ID>,
                EntityGraphJpaSpecificationExecutor<T> {
    
    /**
     * М'яке видалення сутності (встановлює active = false).
     * Реалізація в CustomJpaRepositoryImpl.
     */
    Optional<T> softDelete(@Param("id") ID id, EntityGraph entityGraph);
    
    /**
     * Знаходить всі активні сутності.
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.active = true")
    List<T> findAllActive();
    
    /**
     * Перевіряє чи активна сутність за ID.
     */
    @Query("SELECT CASE WHEN e.active = true THEN true ELSE false END FROM #{#entityName} e WHERE e.id = :id")
    boolean isActive(@Param("id") ID id);
    
    /**
     * М'яке видалення сутності - перевантажений метод без EntityGraph.
     */
    default Optional<T> softDelete(ID id) {
        return softDelete(id, null);
    }
    
    /**
     * М'яке видалення кількох сутностей.
     */
    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e.id IN :ids")
    int softDeleteAll(@Param("ids") List<ID> ids, EntityGraph entityGraph);
    
    /**
     * М'яке видалення кількох сутностей - перевантажений метод без EntityGraph.
     */
    default int softDeleteAll(List<ID> ids) {
        return softDeleteAll(ids, null);
    }
}

