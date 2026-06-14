package com.countin.countin_backend.meal.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateFoodItemRequest {

    @NotBlank
    private String name;

    private UUID categoryId;
}
