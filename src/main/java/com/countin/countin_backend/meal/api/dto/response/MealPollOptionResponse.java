package com.countin.countin_backend.meal.api.dto.response;

import com.countin.countin_backend.meal.domain.model.MealPollOptionType;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MealPollOptionResponse {

    private UUID id;
    private MealPollOptionType optionType;
    private int sortOrder;
    private String label;
    private String detail;
    private UUID dailyMenuEntryId;
}
