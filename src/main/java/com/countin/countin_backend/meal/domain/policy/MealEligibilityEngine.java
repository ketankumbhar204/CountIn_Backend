package com.countin.countin_backend.meal.domain.policy;

import com.countin.countin_backend.meal.domain.model.MealPlanCode;
import com.countin.countin_backend.meal.domain.model.MealParticipationStatus;
import com.countin.countin_backend.meal.domain.model.MealType;
import com.countin.countin_backend.meal.infrastructure.persistence.entity.MealParticipationEntity;
import com.countin.countin_backend.meal.infrastructure.persistence.entity.MealPlanEntity;
import com.countin.countin_backend.member.domain.model.MemberStatus;
import com.countin.countin_backend.member.infrastructure.persistence.entity.MemberEntity;
import java.time.LocalDate;

public final class MealEligibilityEngine {

    private MealEligibilityEngine() {}

    public static boolean mealPlanCovers(MealPlanEntity plan, MealType mealType) {
        if (plan.getCode() == MealPlanCode.NONE) {
            return false;
        }
        if (plan.getCode() == MealPlanCode.FULL) {
            return true;
        }
        return switch (mealType) {
            case BREAKFAST -> plan.isBreakfastIncluded();
            case LUNCH -> plan.isLunchIncluded();
            case DINNER -> plan.isDinnerIncluded();
        };
    }

    public static boolean isEligibleForPollAudience(
            MemberEntity member, MealParticipationEntity participation, LocalDate date, MealType mealType) {
        if (member.getStatus() != MemberStatus.ACTIVE || !member.isActive()) {
            return false;
        }
        if (participation.getStatus() != MealParticipationStatus.ACTIVE) {
            return false;
        }
        if (participation.getEffectiveFrom().isAfter(date)) {
            return false;
        }
        if (participation.getEffectiveTo() != null && participation.getEffectiveTo().isBefore(date)) {
            return false;
        }
        return mealPlanCovers(participation.getMealPlan(), mealType);
    }

    public static boolean isPausedPollAudienceMember(
            MemberEntity member, MealParticipationEntity participation, LocalDate date, MealType mealType) {
        if (member.getStatus() != MemberStatus.ACTIVE || !member.isActive()) {
            return false;
        }
        if (participation.getStatus() != MealParticipationStatus.PAUSED) {
            return false;
        }
        if (participation.getEffectiveFrom().isAfter(date)) {
            return false;
        }
        if (participation.getEffectiveTo() != null && participation.getEffectiveTo().isBefore(date)) {
            return false;
        }
        return mealPlanCovers(participation.getMealPlan(), mealType);
    }
}
