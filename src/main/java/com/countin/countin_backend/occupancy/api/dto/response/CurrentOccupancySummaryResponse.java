package com.countin.countin_backend.occupancy.api.dto.response;

import com.countin.countin_backend.occupancy.domain.model.AllocationTargetType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "Current member accommodation assignment summary")
public class CurrentOccupancySummaryResponse {

    private AllocationTargetType targetType;
    private UUID buildingId;
    private String buildingName;
    private UUID floorId;
    private String floorName;
    private UUID unitId;
    private String unitName;
    private UUID roomId;
    private String roomName;
    private UUID bedId;
    private String bedName;
}
