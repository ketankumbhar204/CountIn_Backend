package com.countin.countin_backend.meal.api.dto.request;

import com.countin.countin_backend.meal.domain.model.MealPollPaymentChoice;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmitMealPollResponsesRequest {

    @NotEmpty
    @Valid
    private List<SubmitMealPollSelectionRequest> selections;

    /** Required for MESS spaces when saving priced meal selections. */
    private MealPollPaymentChoice paymentChoice;

    /** Required when paymentChoice is MARK_AS_PAID — base64 data URI of payment screenshot. */
    private String proofImageBase64;
}
