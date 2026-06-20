package com.countin.countin_backend.meal.application.service;

import com.countin.countin_backend.meal.domain.model.MealType;
import com.countin.countin_backend.meal.infrastructure.persistence.entity.MealDeliveryLocationEntity;
import com.countin.countin_backend.meal.infrastructure.persistence.entity.MealParticipationEntity;
import com.countin.countin_backend.meal.infrastructure.persistence.entity.MealParticipationLastDeliveryEntity;
import com.countin.countin_backend.meal.infrastructure.persistence.repository.MealParticipationLastDeliveryRepository;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MealParticipationLastDeliveryService {

    private final MealParticipationLastDeliveryRepository lastDeliveryRepository;
    private final MealDeliveryLocationService deliveryLocationService;

    @Transactional(readOnly = true)
    public Map<MealType, UUID> loadLastDeliveryLocationIds(MealParticipationEntity participation) {
        List<MealParticipationLastDeliveryEntity> rows =
                lastDeliveryRepository.findByParticipationId(participation.getId());
        Map<MealType, UUID> result = new EnumMap<>(MealType.class);
        for (MealParticipationLastDeliveryEntity row : rows) {
            result.put(row.getMealType(), row.getDeliveryLocation().getId());
        }
        return result;
    }

    @Transactional(readOnly = true)
    public UUID resolveLastDeliveryLocationId(MealParticipationEntity participation, MealType mealType) {
        return lastDeliveryRepository
                .findByParticipationIdAndMealType(participation.getId(), mealType)
                .map(row -> row.getDeliveryLocation().getId())
                .orElse(null);
    }

    @Transactional
    public void saveLastDeliveryLocation(
            MealParticipationEntity participation, MealType mealType, UUID deliveryLocationId) {
        MealDeliveryLocationEntity location =
                deliveryLocationService.loadActiveLocation(participation.getSpace().getId(), deliveryLocationId);

        MealParticipationLastDeliveryEntity row = lastDeliveryRepository
                .findByParticipationIdAndMealType(participation.getId(), mealType)
                .orElse(MealParticipationLastDeliveryEntity.builder()
                        .participation(participation)
                        .mealType(mealType)
                        .build());
        row.setDeliveryLocation(location);
        lastDeliveryRepository.save(row);
    }
}
