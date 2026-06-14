package com.countin.countin_backend.meal.infrastructure.persistence.repository;

import com.countin.countin_backend.meal.infrastructure.persistence.entity.MealComboItemEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MealComboItemRepository extends JpaRepository<MealComboItemEntity, UUID> {

    @Query("""
            SELECT ci FROM MealComboItemEntity ci
            JOIN FETCH ci.item
            WHERE ci.combo.id = :comboId
            ORDER BY ci.sortOrder ASC
            """)
    List<MealComboItemEntity> findByComboIdWithItems(@Param("comboId") UUID comboId);

    void deleteByComboId(UUID comboId);
}
