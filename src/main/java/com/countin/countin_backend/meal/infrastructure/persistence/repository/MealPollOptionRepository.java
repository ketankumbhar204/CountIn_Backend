package com.countin.countin_backend.meal.infrastructure.persistence.repository;

import com.countin.countin_backend.meal.infrastructure.persistence.entity.MealPollOptionEntity;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MealPollOptionRepository extends JpaRepository<MealPollOptionEntity, UUID> {

    List<MealPollOptionEntity> findByPollIdOrderBySortOrderAsc(UUID pollId);

    @Query("""
            SELECT DISTINCT o.dailyMenuEntry.id FROM MealPollOptionEntity o
            WHERE o.dailyMenuEntry.dailyMenu.id = :dailyMenuId
            AND o.dailyMenuEntry.id IS NOT NULL
            """)
    Set<UUID> findReferencedEntryIdsByDailyMenuId(@Param("dailyMenuId") UUID dailyMenuId);
}
