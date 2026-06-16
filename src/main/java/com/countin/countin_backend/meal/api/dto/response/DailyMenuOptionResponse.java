package com.countin.countin_backend.meal.api.dto.response;

import com.countin.countin_backend.meal.domain.model.DailyMenuEntryType;
import com.countin.countin_backend.meal.infrastructure.persistence.entity.DailyMenuEntryEntity;
import com.countin.countin_backend.meal.infrastructure.persistence.entity.DailyMenuPackageItemEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DailyMenuOptionResponse {

    private UUID optionId;
    private DailyMenuEntryType entryType;
    private UUID comboId;
    private UUID itemId;
    private String label;
    private int sortOrder;
    private List<DailyMenuPackageItemResponse> packageItems;

    @JsonProperty("isAvailable")
    private boolean available;

    public static DailyMenuOptionResponse from(DailyMenuEntryEntity entity) {
        return from(entity, Collections.emptyList());
    }

    public static DailyMenuOptionResponse from(
            DailyMenuEntryEntity entity, List<DailyMenuPackageItemEntity> packageItems) {
        List<DailyMenuPackageItemResponse> items = packageItems.stream()
                .map(pi -> DailyMenuPackageItemResponse.builder()
                        .itemId(pi.getItem().getId())
                        .name(pi.getItem().getName())
                        .build())
                .toList();
        return DailyMenuOptionResponse.builder()
                .optionId(entity.getId())
                .entryType(entity.getEntryType())
                .comboId(entity.getCombo() != null ? entity.getCombo().getId() : null)
                .itemId(entity.getItem() != null ? entity.getItem().getId() : null)
                .label(entity.getLabel())
                .sortOrder(entity.getSortOrder())
                .available(entity.isAvailable())
                .packageItems(items.isEmpty() ? null : items)
                .build();
    }
}
