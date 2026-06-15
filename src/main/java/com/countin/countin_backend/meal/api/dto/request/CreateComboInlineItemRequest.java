package com.countin.countin_backend.meal.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateComboInlineItemRequest {

    @NotNull
    private UUID categoryId;

    @NotBlank
    private String name;
}
