package com.countin.countin_backend.meal.infrastructure.persistence.entity;

import com.countin.countin_backend.common.model.BaseEntity;
import com.countin.countin_backend.meal.domain.model.MealPollPaymentChoice;
import com.countin.countin_backend.meal.domain.model.MealPollPaymentStatus;
import com.countin.countin_backend.member.infrastructure.persistence.entity.MemberEntity;
import com.countin.countin_backend.space.infrastructure.persistence.entity.SpaceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "meal_poll_day_payments",
        uniqueConstraints = @UniqueConstraint(columnNames = {"space_id", "member_id", "poll_date"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealPollDayPaymentEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "space_id", nullable = false)
    private SpaceEntity space;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member;

    @Column(name = "poll_date", nullable = false)
    private LocalDate pollDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_choice", nullable = false, length = 20)
    private MealPollPaymentChoice paymentChoice;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    private MealPollPaymentStatus paymentStatus;

    @Column(name = "proof_image_url", columnDefinition = "TEXT")
    private String proofImageUrl;

    @Column(name = "proof_submitted_at")
    private java.time.LocalDateTime proofSubmittedAt;

    @Column(name = "proof_reviewed_at")
    private java.time.LocalDateTime proofReviewedAt;

    @Column(name = "proof_reviewed_by")
    private java.util.UUID proofReviewedBy;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;
}
