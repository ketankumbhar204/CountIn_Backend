package com.countin.countin_backend.meal.api.dto.response;

import com.countin.countin_backend.meal.domain.model.MealPollPaymentChoice;
import com.countin.countin_backend.meal.domain.model.MealPollPaymentStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberMealActivityDayPaymentResponse {

    private MealPollPaymentChoice paymentChoice;
    private MealPollPaymentStatus paymentStatus;
    private String proofImageUrl;
    private String rejectionReason;
    private LocalDateTime proofSubmittedAt;
    private LocalDateTime proofReviewedAt;
    private java.math.BigDecimal prepaidOverflowAmount;
    private java.math.BigDecimal prepaidDebitedAmount;
    private boolean prepaidOverflowPayment;
}
