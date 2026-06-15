package com.countin.countin_backend.meal.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMealComboRequest {

    @NotBlank
    private String name;

    private String description;

    private Boolean active;

    private List<UUID> itemIds = new ArrayList<>();

    @Valid
    private List<CreateComboInlineItemRequest> newItems = new ArrayList<>();
}
