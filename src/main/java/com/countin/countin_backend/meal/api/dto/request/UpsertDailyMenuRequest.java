package com.countin.countin_backend.meal.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpsertDailyMenuRequest {

    @NotEmpty
    @Valid
    private List<DailyMenuOptionRequest> options;

    private String notes;
}
