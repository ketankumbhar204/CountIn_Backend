package com.countin.countin_backend.member.api.dto.response;

import com.countin.countin_backend.member.domain.model.MemberStatus;
import com.countin.countin_backend.member.domain.model.MembershipRole;
import com.countin.countin_backend.member.infrastructure.persistence.entity.MemberEntity;
import com.countin.countin_backend.occupancy.domain.model.MemberOccupancyStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "Member master list item")
public class MemberResponse {

    private UUID memberId;
    private String fullName;
    private String mobileNumber;

    @Schema(description = "Member role in the space")
    private MembershipRole role;

    @Schema(description = "Whether this member is linked to a registered User account")
    private boolean linkedUser;

    @Schema(description = "Operational member status (distinct from soft-delete isActive)")
    private MemberStatus status;

    @Schema(description = "Current occupancy assignment state in this space")
    private MemberOccupancyStatus occupancyStatus;

    private LocalDateTime createdAt;

    public static MemberResponse from(MemberEntity member) {
        return MemberResponse.builder()
                .memberId(member.getId())
                .fullName(member.getFullName())
                .mobileNumber(member.getMobileNumber())
                .role(member.getRole())
                .linkedUser(member.getUser() != null)
                .status(member.getStatus())
                .occupancyStatus(member.getOccupancyStatus())
                .createdAt(member.getCreatedAt())
                .build();
    }
}
