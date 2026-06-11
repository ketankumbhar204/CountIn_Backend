package com.countin.countin_backend.member.api.dto.response;

import com.countin.countin_backend.member.domain.model.MemberStatus;
import com.countin.countin_backend.member.domain.model.MembershipRole;
import com.countin.countin_backend.member.infrastructure.persistence.entity.MemberEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "Complete member master record details")
public class MemberDetailsResponse {

    private UUID memberId;
    private UUID spaceId;
    private String fullName;
    private String mobileNumber;

    @Schema(description = "Member role in the space")
    private MembershipRole role;

    @Schema(description = "Whether this member is linked to a registered User account")
    private boolean linkedUser;

    private UUID linkedUserId;

    @Schema(description = "Internal space membership link when the member has app access")
    private UUID membershipId;

    private boolean active;

    @Schema(description = "Operational member status (distinct from soft-delete isActive)")
    private MemberStatus status;

    private LocalDateTime statusUpdatedAt;
    private String emergencyContactName;
    private String emergencyContactRelation;
    private String emergencyContactMobile;
    private BigDecimal depositAmount;
    private BigDecimal depositPaid;
    private BigDecimal depositRefunded;

    @Schema(description = "Computed deposit balance: depositPaid minus depositRefunded")
    private BigDecimal depositBalance;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MemberDetailsResponse from(MemberEntity member) {
        BigDecimal depositBalance = member.getDepositPaid().subtract(member.getDepositRefunded());
        return MemberDetailsResponse.builder()
                .memberId(member.getId())
                .spaceId(member.getSpace().getId())
                .fullName(member.getFullName())
                .mobileNumber(member.getMobileNumber())
                .role(member.getRole())
                .linkedUser(member.getUser() != null)
                .linkedUserId(member.getUser() != null ? member.getUser().getId() : null)
                .membershipId(member.getMembership() != null ? member.getMembership().getId() : null)
                .active(member.isActive())
                .status(member.getStatus())
                .statusUpdatedAt(member.getStatusUpdatedAt())
                .emergencyContactName(member.getEmergencyContactName())
                .emergencyContactRelation(member.getEmergencyContactRelation())
                .emergencyContactMobile(member.getEmergencyContactMobile())
                .depositAmount(member.getDepositAmount())
                .depositPaid(member.getDepositPaid())
                .depositRefunded(member.getDepositRefunded())
                .depositBalance(depositBalance)
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }
}
