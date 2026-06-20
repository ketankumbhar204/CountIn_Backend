package com.countin.countin_backend.meal.infrastructure.persistence.repository;

import com.countin.countin_backend.meal.infrastructure.persistence.entity.MealDeliveryLocationEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MealDeliveryLocationRepository extends JpaRepository<MealDeliveryLocationEntity, UUID> {

    List<MealDeliveryLocationEntity> findBySpaceIdAndActiveTrueOrderBySortOrderAscNameAsc(UUID spaceId);

    List<MealDeliveryLocationEntity> findBySpaceIdOrderBySortOrderAscNameAsc(UUID spaceId);

    Optional<MealDeliveryLocationEntity> findByIdAndSpaceId(UUID id, UUID spaceId);
}
