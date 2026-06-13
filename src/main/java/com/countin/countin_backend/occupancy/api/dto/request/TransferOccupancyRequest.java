package com.countin.countin_backend.occupancy.api.dto.request;

import com.countin.countin_backend.occupancy.domain.model.AllocationTargetType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Transfer an active occupancy to a new target")
public class TransferOccupancyRequest {

    @NotNull
    private AllocationTargetType targetType;

    private UUID bedId;
    private UUID roomId;
    private UUID unitId;

    private String remarks;
}
