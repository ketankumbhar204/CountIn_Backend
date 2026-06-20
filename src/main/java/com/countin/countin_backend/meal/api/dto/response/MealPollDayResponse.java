package com.countin.countin_backend.meal.api.dto.response;

import com.countin.countin_backend.meal.domain.model.MealPollPaymentChoice;
import com.countin.countin_backend.meal.domain.model.MealPollPaymentStatus;
import com.countin.countin_backend.meal.domain.model.MealType;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MealPollDayResponse {

    private LocalDate pollDate;
    private List<MealPollResponse> polls;
    private MealPollPaymentStatus myPaymentStatus;
    private MealPollPaymentChoice myPaymentChoice;
    private String myProofImageUrl;
    private String myRejectionReason;
    private java.util.List<MealDeliveryLocationResponse> deliveryLocations;
    private Map<MealType, UUID> myLastDeliveryLocationIds;
}
