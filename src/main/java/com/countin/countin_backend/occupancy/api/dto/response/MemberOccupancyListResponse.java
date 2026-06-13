package com.countin.countin_backend.occupancy.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "Member occupancy summary with history")
public class MemberOccupancyListResponse {

    private OccupancyResponse currentOccupancy;
    private List<OccupancyResponse> occupancies;
    private List<OccupancyHistoryEntryResponse> history;
}
