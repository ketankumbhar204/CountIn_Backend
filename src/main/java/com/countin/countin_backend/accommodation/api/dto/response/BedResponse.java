package com.countin.countin_backend.accommodation.api.dto.response;

import com.countin.countin_backend.accommodation.domain.model.AccommodationStatus;
import com.countin.countin_backend.accommodation.infrastructure.persistence.entity.BedEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "Bed within a room")
public class BedResponse {

    private UUID bedId;
    private UUID roomId;
    private String name;
    private String bedNumber;
    private AccommodationStatus status;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private AccommodationActionMetadata actions;

    public static BedResponse from(BedEntity bed) {
        return from(bed, null);
    }

    public static BedResponse from(BedEntity bed, AccommodationActionMetadata actions) {
        return BedResponse.builder()
                .bedId(bed.getId())
                .roomId(bed.getRoom().getId())
                .name(bed.getName())
                .bedNumber(bed.getBedNumber())
                .status(bed.getStatus())
                .active(bed.isActive())
                .createdAt(bed.getCreatedAt())
                .updatedAt(bed.getUpdatedAt())
                .actions(actions)
                .build();
    }
}
