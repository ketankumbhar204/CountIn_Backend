package com.countin.countin_backend.meal.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DailyMenuOptionRequest {

    private UUID comboId;

    private UUID itemId;

    @NotBlank
    private String label;

    private int sortOrder;
    private boolean isAvailable = true;
}
