package com.micro.person.repository;

import com.cosium.spring.data.jpa.entity.graph.repository.support.EntityGraphSimpleJpaRepository;
import com.micro.person.data.constants.EntityConstants;
import com.micro.person.data.entities.BaseActiveEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public class CustomJpaRepositoryImpl<T, ID extends Serializable> 
        extends EntityGraphSimpleJpaRepository<T, ID>
        implements CustomJpaRepository<T, ID> {

    private final EntityManager entityManager;
    private final JpaEntityInformation<T, ID> entityInformation;

    public CustomJpaRepositoryImpl(
            JpaEntityInformation<T, ID> entityInformation,
            EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
        this.entityInformation = entityInformation;
    }

    @Override
    @Transactional
    public Optional<T> softDelete(ID id, com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph entityGraph) {
        // @Query в інтерфейсі виконує SELECT, тут встановлюємо active = false
        // Використовуємо EntityManager для отримання сутності з урахуванням EntityGraph
        Optional<T> entity = entityGraph != null ? findById(id, entityGraph) : findById(id);
        if (entity.isPresent() && entity.get() instanceof BaseActiveEntity baseEntity) {
            baseEntity.setActive(false);
            T merged = entityManager.merge(entity.get());
            entityManager.flush(); // Зберігаємо зміни в БД
            return Optional.of(merged);
        }
        return entity;
    }

    @Override
    public List<T> findAllActive() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(getDomainClass());
        Root<T> root = query.from(getDomainClass());
        
        if (BaseActiveEntity.class.isAssignableFrom(getDomainClass())) {
            query.where(cb.equal(root.get(EntityConstants.ACTIVE_FIELD), true));
        }
        
        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public boolean isActive(ID id) {
        return findById(id)
                .map(e -> e instanceof BaseActiveEntity baseEntity && 
                         Boolean.TRUE.equals(baseEntity.getActive()))
                .orElse(false);
    }

    @Override
    @Transactional
    public int softDeleteAll(List<ID> ids, com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph entityGraph) {
        return (int) ids.stream()
                .map(id -> softDelete(id, entityGraph))
                .filter(Optional::isPresent)
                .count();
    }
}
