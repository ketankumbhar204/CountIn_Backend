package com.countin.countin_backend.meal.api.dto.request;

import com.countin.countin_backend.meal.domain.model.DailyMenuEntryType;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DailyMenuOptionRequest {

    /** Existing entry id from GET response; omit for new options. */
    private UUID optionId;

    private DailyMenuEntryType entryType;

    private UUID comboId;

    private UUID itemId;

    /** Required for PACKAGE entries — items included in this meal-only package. */
    private List<UUID> itemIds;

    @NotBlank
    private String label;

    private int sortOrder;
    private boolean isAvailable = true;
}
