package com.countin.countin_backend.occupancy.api.dto.request;

import com.countin.countin_backend.occupancy.domain.model.AllocationTargetType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Allocate a member to a bed, room, or unit")
public class AllocateOccupancyRequest {

    @NotNull
    private UUID memberId;

    @NotNull
    private AllocationTargetType targetType;

    private UUID bedId;
    private UUID roomId;
    private UUID unitId;

    private LocalDate expectedCheckoutDate;
    private String remarks;
}
