package com.countin.countin_backend.dashboard.application.support;

import com.countin.countin_backend.meal.api.dto.response.MemberMealActivitySummaryResponse;
import com.countin.countin_backend.meal.application.service.MemberMealActivityService;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PayPerMealBillingCalculator {

    private static final String DEFAULT_CURRENCY = "INR";

    private final MemberMealActivityService memberMealActivityService;

    public MealLedgerContribution computeMemberContribution(
            UUID spaceId, UUID memberId, UUID callerId, YearMonth month) {
        MemberMealActivitySummaryResponse mealSummary =
                memberMealActivityService.getMonthlyActivity(spaceId, memberId, callerId, month.toString())
                        .getSummary();

        BigDecimal expected = mealSummary.getAmountGenerated();
        BigDecimal collected = mealSummary.getPaidAmount();
        String currencyCode = mealSummary.getCurrencyCode() != null
                ? mealSummary.getCurrencyCode()
                : DEFAULT_CURRENCY;

        return MealLedgerContribution.builder()
                .expected(expected)
                .collected(collected)
                .hasExpected(expected != null)
                .hasCollected(collected != null)
                .currencyCode(currencyCode)
                .build();
    }
}
