package com.countin.countin_backend.meal.api.dto.response;

import com.countin.countin_backend.meal.domain.model.MealParticipationStatus;
import com.countin.countin_backend.meal.domain.model.MealPlanCode;
import com.countin.countin_backend.meal.infrastructure.persistence.entity.MealParticipationEntity;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberMealParticipationSummaryResponse {

    private UUID participationId;
    private MealPlanCode mealPlanCode;
    private String mealPlanName;
    private MealParticipationStatus status;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;

    public static MemberMealParticipationSummaryResponse from(MealParticipationEntity entity) {
        return MemberMealParticipationSummaryResponse.builder()
                .participationId(entity.getId())
                .mealPlanCode(entity.getMealPlan().getCode())
                .mealPlanName(entity.getMealPlan().getName())
                .status(entity.getStatus())
                .effectiveFrom(entity.getEffectiveFrom())
                .effectiveTo(entity.getEffectiveTo())
                .build();
    }
}
