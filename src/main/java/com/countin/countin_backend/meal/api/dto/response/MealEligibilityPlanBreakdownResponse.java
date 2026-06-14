package com.countin.countin_backend.meal.api.dto.response;

import com.countin.countin_backend.meal.domain.model.MealPlanCode;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MealEligibilityPlanBreakdownResponse {

    private MealPlanCode mealPlanCode;
    private String mealPlanName;
    private int count;
}
