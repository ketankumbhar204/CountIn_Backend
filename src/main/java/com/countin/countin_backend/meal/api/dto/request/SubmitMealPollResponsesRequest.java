package com.countin.countin_backend.meal.api.dto.request;

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
}
