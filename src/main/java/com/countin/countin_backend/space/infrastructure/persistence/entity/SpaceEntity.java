package com.countin.countin_backend.space.infrastructure.persistence.entity;

import com.countin.countin_backend.common.model.BaseEntity;
import com.countin.countin_backend.space.domain.model.GenderPolicy;
import com.countin.countin_backend.space.domain.model.MealBillingType;
import com.countin.countin_backend.space.domain.model.PrepaidBalanceUnit;
import com.countin.countin_backend.space.domain.model.SpaceType;
import com.countin.countin_backend.user.infrastructure.persistence.entity.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "spaces",
        indexes = @Index(name = "idx_spaces_owner_id", columnList = "owner_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpaceEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SpaceType type;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "contact_number", length = 15)
    private String contactNumber;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender_policy", length = 20)
    private GenderPolicy genderPolicy;

    @Column(name = "default_food_charge", precision = 12, scale = 2)
    private BigDecimal defaultFoodCharge;

    @Builder.Default
    @Column(name = "food_included_in_rent", nullable = false)
    private boolean foodIncludedInRent = false;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "meal_billing_type", nullable = false, length = 20)
    private MealBillingType mealBillingType = MealBillingType.PAY_PER_MEAL;

    @Enumerated(EnumType.STRING)
    @Column(name = "prepaid_balance_unit", length = 10)
    private PrepaidBalanceUnit prepaidBalanceUnit;

    @Builder.Default
    @Column(name = "prepaid_fallback_to_pay_per_meal", nullable = false)
    private boolean prepaidFallbackToPayPerMeal = true;
}
