package com.countin.countin_backend.meal.api.dto.request;

import com.countin.countin_backend.space.domain.model.MealBillingType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMealBillingChangeRequest {

    @NotNull
    private MealBillingType requestedBillingType;

    private String customerNotes;
}
