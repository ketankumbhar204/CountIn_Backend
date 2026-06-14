package com.countin.countin_backend.meal.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMealComboRequest {

    @NotBlank
    private String name;

    private String description;

    @NotEmpty
    private List<UUID> itemIds;
}
