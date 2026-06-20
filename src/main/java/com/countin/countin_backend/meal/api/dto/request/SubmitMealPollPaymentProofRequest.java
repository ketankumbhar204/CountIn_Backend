package com.countin.countin_backend.meal.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmitMealPollPaymentProofRequest {

    @NotBlank
    private String proofImageBase64;
}
