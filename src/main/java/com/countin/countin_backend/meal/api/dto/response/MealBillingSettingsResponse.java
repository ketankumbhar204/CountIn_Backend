package com.countin.countin_backend.meal.api.dto.response;

import com.countin.countin_backend.space.domain.model.MealBillingType;
import com.countin.countin_backend.space.domain.model.PrepaidBalanceUnit;
import com.countin.countin_backend.space.infrastructure.persistence.entity.SpaceEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "Space-level meal billing configuration")
public class MealBillingSettingsResponse {

    private MealBillingType billingType;
    private PrepaidBalanceUnit prepaidBalanceUnit;
    private boolean fallbackToPayPerMeal;

    public static MealBillingSettingsResponse from(SpaceEntity space) {
        return MealBillingSettingsResponse.builder()
                .billingType(space.getMealBillingType())
                .prepaidBalanceUnit(space.getPrepaidBalanceUnit())
                .fallbackToPayPerMeal(space.isPrepaidFallbackToPayPerMeal())
                .build();
    }
}
