package com.countin.countin_backend.dashboard.api.dto.response;

import com.countin.countin_backend.dashboard.domain.model.DashboardFinancialSource;
import com.countin.countin_backend.space.domain.model.MealBillingType;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DashboardFinancialSummaryResponse {

    private BigDecimal expectedCharges;
    private BigDecimal collected;
    private BigDecimal pending;
    private String currencyCode;
    private DashboardFinancialSource source;
    private MealBillingType mealBillingType;
    private PrepaidBalanceSummaryResponse prepaidBalance;
    private Boolean mixedMealBilling;
}
