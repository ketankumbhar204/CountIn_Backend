package com.countin.countin_backend.meal.application.service;

import com.countin.countin_backend.meal.api.dto.response.EligibleParticipantResponse;
import com.countin.countin_backend.meal.api.dto.response.MealEligibilityPlanBreakdownResponse;
import com.countin.countin_backend.meal.api.dto.response.MealEligibilitySlotResponse;
import com.countin.countin_backend.meal.api.dto.response.MealEligibilitySummaryResponse;
import com.countin.countin_backend.meal.domain.model.MealPlanCode;
import com.countin.countin_backend.meal.domain.model.MealType;
import com.countin.countin_backend.meal.domain.policy.MealEligibilityEngine;
import com.countin.countin_backend.meal.infrastructure.persistence.entity.MealParticipationEntity;
import com.countin.countin_backend.meal.infrastructure.persistence.repository.MealParticipationRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MealEligibilityService {

    private final MealParticipationRepository participationRepository;
    private final DailyMenuService dailyMenuService;
    private final MealAccessService mealAccessService;

    @Transactional(readOnly = true)
    public MealEligibilitySummaryResponse getSummary(UUID spaceId, UUID callerId, LocalDate date) {
        mealAccessService.requireViewMeals(spaceId, callerId);
        LocalDate targetDate = date != null ? date : LocalDate.now();
        List<MealParticipationEntity> participations = participationRepository.findAllNonStoppedBySpaceId(spaceId);
        List<MealEligibilitySlotResponse> slots = new ArrayList<>();

        for (MealType mealType : MealType.values()) {
            int eligibleCount = 0;
            int pausedCount = 0;
            Map<MealPlanCode, PlanAccumulator> byPlanCounts = new EnumMap<>(MealPlanCode.class);

            for (MealParticipationEntity participation : participations) {
                if (MealEligibilityEngine.isEligibleForPollAudience(
                        participation.getMember(), participation, targetDate, mealType)) {
                    eligibleCount++;
                    MealPlanCode planCode = participation.getMealPlan().getCode();
                    byPlanCounts
                            .computeIfAbsent(planCode, code -> new PlanAccumulator(participation.getMealPlan().getName()))
                            .increment();
                } else if (MealEligibilityEngine.isPausedPollAudienceMember(
                        participation.getMember(), participation, targetDate, mealType)) {
                    pausedCount++;
                }
            }

            List<MealEligibilityPlanBreakdownResponse> byPlan = byPlanCounts.entrySet().stream()
                    .map(entry -> MealEligibilityPlanBreakdownResponse.builder()
                            .mealPlanCode(entry.getKey())
                            .mealPlanName(entry.getValue().planName())
                            .count(entry.getValue().count())
                            .build())
                    .sorted(Comparator.comparing(response -> response.getMealPlanCode().name()))
                    .toList();

            slots.add(MealEligibilitySlotResponse.builder()
                    .mealType(mealType)
                    .eligibleCount(eligibleCount)
                    .pausedCount(pausedCount)
                    .published(dailyMenuService.isPublished(spaceId, targetDate, mealType))
                    .byPlan(byPlan)
                    .build());
        }

        return MealEligibilitySummaryResponse.builder().date(targetDate).slots(slots).build();
    }

    @Transactional(readOnly = true)
    public List<EligibleParticipantResponse> listEligibleParticipants(
            UUID spaceId, UUID callerId, LocalDate date, MealType mealType) {
        mealAccessService.requireManageMeals(spaceId, callerId);
        LocalDate targetDate = date != null ? date : LocalDate.now();
        return participationRepository.findAllActiveBySpaceId(spaceId).stream()
                .filter(participation -> MealEligibilityEngine.isEligibleForPollAudience(
                        participation.getMember(), participation, targetDate, mealType))
                .map(participation -> EligibleParticipantResponse.builder()
                        .memberId(participation.getMember().getId())
                        .memberName(participation.getMember().getFullName())
                        .mobileNumber(participation.getMember().getMobileNumber())
                        .mealPlanCode(participation.getMealPlan().getCode())
                        .mealPlanName(participation.getMealPlan().getName())
                        .build())
                .toList();
    }

    private static final class PlanAccumulator {
        private final String planName;
        private int count;

        private PlanAccumulator(String planName) {
            this.planName = planName;
        }

        private void increment() {
            count++;
        }

        private String planName() {
            return planName;
        }

        private int count() {
            return count;
        }
    }
}
