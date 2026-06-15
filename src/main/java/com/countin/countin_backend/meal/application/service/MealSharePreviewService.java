package com.countin.countin_backend.meal.application.service;

import com.countin.countin_backend.common.exception.BusinessException;
import com.countin.countin_backend.meal.api.dto.response.MealSharePreviewLineResponse;
import com.countin.countin_backend.meal.api.dto.response.MealSharePreviewResponse;
import com.countin.countin_backend.meal.api.dto.response.MealSharePreviewSlotResponse;
import com.countin.countin_backend.meal.domain.model.DailyMenuEntryType;
import com.countin.countin_backend.meal.domain.model.DailyMenuStatus;
import com.countin.countin_backend.meal.domain.model.MealType;
import com.countin.countin_backend.meal.domain.policy.MealEligibilityEngine;
import com.countin.countin_backend.meal.infrastructure.persistence.entity.DailyMenuEntity;
import com.countin.countin_backend.meal.infrastructure.persistence.entity.DailyMenuEntryEntity;
import com.countin.countin_backend.meal.infrastructure.persistence.entity.MealComboItemEntity;
import com.countin.countin_backend.meal.infrastructure.persistence.entity.MealParticipationEntity;
import com.countin.countin_backend.meal.infrastructure.persistence.repository.DailyMenuEntryRepository;
import com.countin.countin_backend.meal.infrastructure.persistence.repository.DailyMenuRepository;
import com.countin.countin_backend.meal.infrastructure.persistence.repository.MealComboItemRepository;
import com.countin.countin_backend.meal.infrastructure.persistence.repository.MealParticipationRepository;
import com.countin.countin_backend.space.infrastructure.persistence.entity.SpaceEntity;
import com.countin.countin_backend.space.infrastructure.persistence.repository.SpaceRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MealSharePreviewService {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("EEEE, d MMM yyyy", Locale.ENGLISH);

    private final DailyMenuRepository dailyMenuRepository;
    private final DailyMenuEntryRepository dailyMenuEntryRepository;
    private final MealComboItemRepository mealComboItemRepository;
    private final MealParticipationRepository participationRepository;
    private final SpaceRepository spaceRepository;
    private final MealAccessService mealAccessService;

    @Transactional(readOnly = true)
    public MealSharePreviewResponse getSharePreview(
            UUID spaceId, UUID callerId, LocalDate date, MealType mealType) {
        mealAccessService.requireManageMeals(spaceId, callerId);
        LocalDate menuDate = date != null ? date : LocalDate.now();
        SpaceEntity space = spaceRepository
                .findById(spaceId)
                .orElseThrow(() -> new BusinessException("Space not found", HttpStatus.NOT_FOUND));

        List<DailyMenuEntity> publishedMenus = dailyMenuRepository
                .findBySpaceAndDate(spaceId, menuDate, true, DailyMenuStatus.PUBLISHED)
                .stream()
                .filter(menu -> mealType == null || menu.getMealType() == mealType)
                .toList();

        if (publishedMenus.isEmpty()) {
            throw new BusinessException("No published menus found for the selected date", HttpStatus.NOT_FOUND);
        }

        List<MealParticipationEntity> participations = participationRepository.findAllNonStoppedBySpaceId(spaceId);
        List<MealSharePreviewSlotResponse> slots = new ArrayList<>();
        for (DailyMenuEntity menu : publishedMenus) {
            int eligibleCount = countEligible(participations, menuDate, menu.getMealType());
            slots.add(MealSharePreviewSlotResponse.builder()
                    .mealType(menu.getMealType())
                    .dailyMenuId(menu.getId())
                    .lines(buildLines(menu))
                    .notes(menu.getNotes())
                    .eligibleCount(eligibleCount)
                    .build());
        }

        DailyMenuEntity primaryMenu = publishedMenus.get(0);
        int primaryEligibleCount = slots.get(0).getEligibleCount();
        String messageText = buildMessageText(space.getName(), menuDate, mealType, slots);

        return MealSharePreviewResponse.builder()
                .spaceName(space.getName())
                .menuDate(menuDate)
                .mealType(mealType != null ? mealType : (publishedMenus.size() == 1 ? primaryMenu.getMealType() : null))
                .dailyMenuId(mealType != null || publishedMenus.size() == 1 ? primaryMenu.getId() : null)
                .status(DailyMenuStatus.PUBLISHED)
                .eligibleCount(primaryEligibleCount)
                .messageText(messageText)
                .slots(slots)
                .build();
    }

    private List<MealSharePreviewLineResponse> buildLines(DailyMenuEntity menu) {
        List<DailyMenuEntryEntity> entries = dailyMenuEntryRepository.findByDailyMenuId(menu.getId());
        return entries.stream()
                .filter(DailyMenuEntryEntity::isAvailable)
                .map(this::toLine)
                .toList();
    }

    private MealSharePreviewLineResponse toLine(DailyMenuEntryEntity entry) {
        String detail = null;
        if (entry.getEntryType() == DailyMenuEntryType.COMBO && entry.getCombo() != null) {
            detail = mealComboItemRepository.findByComboIdWithItems(entry.getCombo().getId()).stream()
                    .map(MealComboItemEntity::getItem)
                    .map(item -> item.getName())
                    .collect(Collectors.joining(" · "));
        }
        return MealSharePreviewLineResponse.builder()
                .entryType(entry.getEntryType())
                .label(entry.getLabel())
                .detail(detail != null && !detail.isBlank() ? detail : null)
                .build();
    }

    private int countEligible(List<MealParticipationEntity> participations, LocalDate date, MealType mealType) {
        int count = 0;
        for (MealParticipationEntity participation : participations) {
            if (MealEligibilityEngine.isEligibleForPollAudience(
                    participation.getMember(), participation, date, mealType)) {
                count++;
            }
        }
        return count;
    }

    private String buildMessageText(
            String spaceName, LocalDate menuDate, MealType mealType, List<MealSharePreviewSlotResponse> slots) {
        StringBuilder message = new StringBuilder();
        message.append("🍽 ").append(spaceName).append('\n');
        message.append(DATE_FORMAT.format(menuDate));
        if (mealType != null) {
            message.append(" · ").append(formatMealType(mealType));
        }
        message.append("\n\n");

        for (MealSharePreviewSlotResponse slot : slots) {
            if (mealType == null && slots.size() > 1) {
                message.append(formatMealType(slot.getMealType())).append('\n');
            }
            for (MealSharePreviewLineResponse line : slot.getLines()) {
                message.append("• ").append(line.getLabel());
                if (line.getDetail() != null && !line.getDetail().isBlank()) {
                    message.append('\n').append("  ").append(line.getDetail());
                }
                message.append('\n');
            }
            if (slot.getNotes() != null && !slot.getNotes().isBlank()) {
                message.append("Note: ").append(slot.getNotes()).append('\n');
            }
            message.append('\n');
        }

        if (mealType != null && !slots.isEmpty()) {
            message.append("Eligible participants: ").append(slots.get(0).getEligibleCount());
        } else if (slots.size() > 1) {
            message.append("Eligible participants: ");
            message.append(slots.stream()
                    .map(slot -> formatMealType(slot.getMealType()) + " " + slot.getEligibleCount())
                    .collect(Collectors.joining(" · ")));
        } else if (!slots.isEmpty()) {
            message.append("Eligible participants: ").append(slots.get(0).getEligibleCount());
        }

        return message.toString().trim();
    }

    private String formatMealType(MealType mealType) {
        String name = mealType.name().toLowerCase(Locale.ENGLISH);
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}
