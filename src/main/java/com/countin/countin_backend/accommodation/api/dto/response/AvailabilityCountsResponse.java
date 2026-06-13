package com.countin.countin_backend.accommodation.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Availability breakdown by accommodation level")
public class AvailabilityCountsResponse {

    @Schema(description = "Available beds", example = "90")
    private long availableBeds;

    @Schema(description = "Occupied beds", example = "0")
    private long occupiedBeds;

    @Schema(description = "Available rooms", example = "30")
    private long availableRooms;

    @Schema(description = "Occupied rooms", example = "0")
    private long occupiedRooms;

    @Schema(description = "Available units", example = "0")
    private long availableUnits;

    @Schema(description = "Occupied units", example = "0")
    private long occupiedUnits;
}
