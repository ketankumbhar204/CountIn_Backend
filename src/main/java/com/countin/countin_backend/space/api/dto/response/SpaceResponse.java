package com.countin.countin_backend.space.api.dto.response;

import com.countin.countin_backend.space.domain.model.SpaceType;
import com.countin.countin_backend.space.infrastructure.persistence.entity.SpaceEntity;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SpaceResponse {

    private UUID id;
    private String name;
    private SpaceType type;
    private String address;
    private String contactNumber;
    private boolean isActive;
    private UUID ownerId;
    private String ownerName;
    private LocalDateTime createdAt;

    public static SpaceResponse from(SpaceEntity space) {
        return SpaceResponse.builder()
                .id(space.getId())
                .name(space.getName())
                .type(space.getType())
                .address(space.getAddress())
                .contactNumber(space.getContactNumber())
                .isActive(space.isActive())
                .ownerId(space.getOwner().getId())
                .ownerName(space.getOwner().getFullName())
                .createdAt(space.getCreatedAt())
                .build();
    }
}
