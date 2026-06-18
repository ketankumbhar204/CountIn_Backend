package com.countin.countin_backend.meal.api.dto.response;

import com.countin.countin_backend.meal.domain.model.DailyMenuEntryType;
import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;

@Getter
@Builder
public class MealSharePreviewLineResponse {

    private DailyMenuEntryType entryType;
    private String label;
    private String detail;
    private BigDecimal price;
    private String currencyCode;
}
