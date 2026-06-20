package com.countin.countin_backend.meal.api.dto.response;

import com.countin.countin_backend.meal.domain.model.FoodType;
import com.countin.countin_backend.meal.infrastructure.persistence.entity.MealComboItemEntity;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MealComboItemLineResponse {

    private UUID itemId;
    private String name;
    private FoodType foodType;

    public static MealComboItemLineResponse from(MealComboItemEntity entity) {
        return MealComboItemLineResponse.builder()
                .itemId(entity.getItem().getId())
                .name(entity.getItem().getName())
                .foodType(entity.getItem().getFoodType())
                .build();
    }
}
