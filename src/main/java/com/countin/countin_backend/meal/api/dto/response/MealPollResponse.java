package com.countin.countin_backend.meal.api.dto.response;

import com.countin.countin_backend.meal.domain.model.MealPollStatus;
import com.countin.countin_backend.meal.domain.model.MealType;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MealPollResponse {

    private UUID id;
    private LocalDate pollDate;
    private MealType mealType;
    private MealPollStatus status;
    private UUID dailyMenuId;
    private List<MealPollOptionResponse> options;
    private UUID mySelectedOptionId;
    private int responseCount;
}
