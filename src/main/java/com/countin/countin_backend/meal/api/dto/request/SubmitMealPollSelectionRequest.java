package com.countin.countin_backend.meal.api.dto.request;

import com.countin.countin_backend.meal.domain.model.MealType;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmitMealPollSelectionRequest {

    @NotNull
    private MealType mealType;

    @NotNull
    private UUID selectedOptionId;
}
