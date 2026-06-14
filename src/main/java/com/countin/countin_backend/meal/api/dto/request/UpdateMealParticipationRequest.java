package com.countin.countin_backend.meal.api.dto.request;

import com.countin.countin_backend.meal.domain.model.MealParticipationStatus;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMealParticipationRequest {

    private UUID mealPlanId;
    private MealParticipationStatus status;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
}
