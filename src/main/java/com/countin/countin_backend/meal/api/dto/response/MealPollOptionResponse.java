package com.countin.countin_backend.meal.api.dto.response;

import com.countin.countin_backend.meal.domain.model.DailyMenuEntryType;
import com.countin.countin_backend.meal.domain.model.MealPollOptionType;
import com.countin.countin_backend.meal.infrastructure.persistence.entity.DailyMenuEntryEntity;
import com.countin.countin_backend.meal.infrastructure.persistence.entity.MealPollOptionEntity;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MealPollOptionResponse {

    private UUID id;
    private MealPollOptionType optionType;
    private int sortOrder;
    private String label;
    private String detail;
    private UUID dailyMenuEntryId;
    private BigDecimal price;
    private String currencyCode;

    public static MealPollOptionResponse from(MealPollOptionEntity option) {
        DailyMenuEntryEntity entry = option.getDailyMenuEntry();
        return MealPollOptionResponse.builder()
                .id(option.getId())
                .optionType(option.getOptionType())
                .sortOrder(option.getSortOrder())
                .label(option.getLabel())
                .detail(option.getDetail())
                .dailyMenuEntryId(entry != null ? entry.getId() : null)
                .price(resolvePrice(entry))
                .currencyCode(resolveCurrencyCode(entry))
                .build();
    }

    private static BigDecimal resolvePrice(DailyMenuEntryEntity entity) {
        if (entity == null) {
            return null;
        }
        if (entity.getEntryType() == DailyMenuEntryType.COMBO
                && entity.getCombo() != null
                && entity.getCombo().getPrice() != null) {
            return entity.getCombo().getPrice();
        }
        return entity.getPrice();
    }

    private static String resolveCurrencyCode(DailyMenuEntryEntity entity) {
        if (entity == null) {
            return null;
        }
        if (entity.getEntryType() == DailyMenuEntryType.COMBO
                && entity.getCombo() != null
                && entity.getCombo().getPrice() != null) {
            return entity.getCombo().getCurrencyCode();
        }
        return entity.getCurrencyCode();
    }
}
