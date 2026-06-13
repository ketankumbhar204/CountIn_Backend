package com.countin.countin_backend.space.api.dto.response;

import com.countin.countin_backend.space.domain.model.Space;
import com.countin.countin_backend.space.domain.model.SpaceType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "Complete details of a space")
public class SpaceDetailsResponse {

    private UUID id;

    @Schema(example = "Sunrise PG")
    private String name;

    @Schema(description = "Space category", example = "PG", implementation = SpaceType.class)
    private SpaceType type;

    private String address;
    private String contactNumber;
    private UUID ownerId;

    @Schema(description = "When true, food is mandatory and included in rent (no separate food charge line)")
    private boolean foodIncludedInRent;

    @Schema(description = "Default monthly food charge prefill when food is billed separately")
    private BigDecimal defaultFoodCharge;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static SpaceDetailsResponse from(Space space) {
        return SpaceDetailsResponse.builder()
                .id(space.getId())
                .name(space.getName())
                .type(space.getType())
                .address(space.getAddress())
                .contactNumber(space.getContactNumber())
                .ownerId(space.getOwnerId())
                .foodIncludedInRent(space.isFoodIncludedInRent())
                .defaultFoodCharge(space.getDefaultFoodCharge())
                .createdAt(space.getCreatedAt())
                .updatedAt(space.getUpdatedAt())
                .build();
    }
}
