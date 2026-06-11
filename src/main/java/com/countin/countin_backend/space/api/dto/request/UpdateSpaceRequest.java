package com.countin.countin_backend.space.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "Request body for updating an existing space")
public class UpdateSpaceRequest {

    @NotBlank(message = "Space name is required")
    @Schema(description = "Display name of the space", example = "Sunrise PG")
    private String name;

    @Schema(description = "Physical address of the space", example = "Pune")
    private String address;

    @Schema(description = "Public contact number for the space", example = "9876543210")
    private String contactNumber;
}
