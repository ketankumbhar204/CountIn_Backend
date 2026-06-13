package com.countin.countin_backend.occupancy.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Complete move-in for a reserved occupancy")
public class MoveInOccupancyRequest {

    private LocalDate moveInDate;
    private LocalDate expectedExitDate;

    @Schema(description = "Allow move-in before scheduled moveInDate")
    private boolean allowEarlyMoveIn;

    private Boolean agreementSigned;
    private String remarks;
}
