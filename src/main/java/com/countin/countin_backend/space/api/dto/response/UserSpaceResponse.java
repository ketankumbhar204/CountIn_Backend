package com.countin.countin_backend.space.api.dto.response;

import com.countin.countin_backend.member.domain.model.MembershipRole;
import com.countin.countin_backend.member.domain.model.MembershipStatus;
import com.countin.countin_backend.member.infrastructure.persistence.entity.SpaceMembershipEntity;
import com.countin.countin_backend.space.domain.model.SpaceType;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserSpaceResponse {

    private UUID spaceId;
    private String spaceName;
    private SpaceType spaceType;
    private MembershipRole role;
    private MembershipStatus membershipStatus;

    public static UserSpaceResponse from(SpaceMembershipEntity membership) {
        return UserSpaceResponse.builder()
                .spaceId(membership.getSpace().getId())
                .spaceName(membership.getSpace().getName())
                .spaceType(membership.getSpace().getType())
                .role(membership.getRole())
                .membershipStatus(membership.getStatus())
                .build();
    }
}
