package com.countin.countin_backend.meal.api.dto.response;

import com.countin.countin_backend.meal.domain.model.MealPollStatus;
import com.countin.countin_backend.meal.domain.model.MealType;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MealHeadcountSlotResponse {

    private MealType mealType;
    private UUID pollId;
    private MealPollStatus pollStatus;
    private int mealsToPrepare;
}
