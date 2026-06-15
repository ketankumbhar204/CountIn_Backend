package com.countin.countin_backend.meal.infrastructure.persistence.repository;

import com.countin.countin_backend.meal.infrastructure.persistence.entity.MealPollOptionEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MealPollOptionRepository extends JpaRepository<MealPollOptionEntity, UUID> {

    List<MealPollOptionEntity> findByPollIdOrderBySortOrderAsc(UUID pollId);
}
