package com.countin.countin_backend.meal.api.dto.response;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberMealActivitySummaryResponse {

    private int acceptedMeals;
    private int pendingResponses;
    private int skippedMeals;
    private BigDecimal amountGenerated;
    private BigDecimal paidAmount;
    private BigDecimal pendingAmount;
    private String currencyCode;
}
