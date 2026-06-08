package com.countin.countin_backend.space.api.dto.request;

import com.countin.countin_backend.space.domain.model.SpaceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateSpaceRequest {

    @NotBlank(message = "Space name is required")
    private String name;

    @NotNull(message = "Space type is required")
    private SpaceType type;

    private String address;

    private String contactNumber;

    @NotNull(message = "Owner ID is required")
    private UUID ownerId;
}
