package com.countin.countin_backend.meal.infrastructure.persistence.repository;

import com.countin.countin_backend.meal.domain.model.MealType;
import com.countin.countin_backend.meal.infrastructure.persistence.entity.MealPollEntity;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MealPollRepository extends JpaRepository<MealPollEntity, UUID> {

    Optional<MealPollEntity> findBySpaceIdAndPollDateAndMealType(UUID spaceId, LocalDate pollDate, MealType mealType);

    List<MealPollEntity> findBySpaceIdAndPollDateOrderByMealTypeAsc(UUID spaceId, LocalDate pollDate);

    List<MealPollEntity> findBySpaceIdAndPollDateBetweenOrderByPollDateAscMealTypeAsc(
            UUID spaceId, LocalDate from, LocalDate to);
}
